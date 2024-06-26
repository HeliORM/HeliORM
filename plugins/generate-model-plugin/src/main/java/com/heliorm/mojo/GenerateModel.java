package com.heliorm.mojo;

import com.heliorm.Database;
import com.heliorm.Table;
import com.heliorm.mojo.annotated.AnnotatedPojoGenerator;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static java.lang.String.format;

/**
 * @author gideon
 */
@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateModel extends AbstractMojo {

    @Parameter(property = "strategy", required = true)
    private Generator.PojoStrategy strategy;
    @Parameter(property = "packages", required = true)
    private Set<String> packages;
    @Parameter(property = "outputDir", required = true)
    private String outputDir;
    @Parameter(property = "resourceDir", required = true)
    private String resourceDir;
    @Parameter(property = "database", required = true)
    private String database;
    @Parameter(property = "databaseClass")
    private String databaseClass;
    @Component
    private MavenProject project;
    private ClassLoader localClassLoader;
    private ClassLoader globalClassLoader;

    private Modeller<?> modeller;

    public GenerateModel() {
    }

    @Override
    public void execute() throws MojoExecutionException {
        try {
            setupClassLoader();
            Generator<?> gen;
            if (Objects.requireNonNull(strategy) == Generator.PojoStrategy.annotated) {
                gen = new AnnotatedPojoGenerator(this, packages);
            } else {
                throw new MojoExecutionException(format("Unsupported POJO strategy '%s'. BUG?", strategy));
            }
            File dir = new File(resourceDir + "/META-INF/services/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            modeller = new Modeller<>(gen, packages);
            Map<String, PackageDatabase> packageDatabases = modeller.getPackageDatabases();
            PrintWriter svc = new PrintWriter(new FileWriter(resourceDir + "/META-INF/services/" + Database.class.getCanonicalName()));
            Set<Output> outputs = new HashSet<>();
            for (String pkg : packageDatabases.keySet()) {
                PackageDatabase database = packageDatabases.get(pkg);
                database.setSqlDatabase(this.database);
                Output output = new Output(this, database, pkg);
                for (Table<?> table : database.getTables()) {
                    output.addTable(table);
                }
                outputs.add(output);
                svc.println(getDatabaseClassFor(database));

            }
            svc.close();
            for (Output out : outputs) {
                out.output(outputDir);
            }
        } catch (GeneratorException | OrmMetaDataException | IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    public ClassLoader getLocalClassLoader() {
        return localClassLoader;
    }

    public ClassLoader getGlobalClassLoader() {
        return globalClassLoader;
    }

    String getTablesPackageFor(Table<?> table) {
        return modeller.getPackageDatabase(table.getObjectClass().getCanonicalName()).getPackageName();
    }

    String getDatabaseClassFor(Table<?> table) {
        if (databaseClass == null) {
            return getTablesPackageFor(table) + ".Tables";
        }
        return databaseClass;
    }


    String getDatabaseClassFor(Database db) {
        return getDatabaseClassFor(db.getTables().getFirst());
    }

    /**
     * Get a class loader that will load classes compiled during the build
     *
     */
    private void setupClassLoader() throws GeneratorException {
        List<String> classpathElements;
        try {
            classpathElements = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new GeneratorException(format("Error getting compiled class path elements (%s)", e.getMessage()), e);
        }
        globalClassLoader = makeClassLoader(classpathElements);
        localClassLoader = makeClassLoader(Collections.singletonList(project.getBuild().getOutputDirectory()));
    }

    private ClassLoader makeClassLoader(List<String> classpathElements) throws GeneratorException {
        List<URL> projectClasspathList = new ArrayList<>();
        for (String element : classpathElements) {
            try {
                projectClasspathList.add(new File(element).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new GeneratorException(e.getMessage(), e);
            }
        }
        return new URLClassLoader(projectClasspathList.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());
    }

}
