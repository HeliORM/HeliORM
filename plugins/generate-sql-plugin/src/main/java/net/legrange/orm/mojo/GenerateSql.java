package net.legrange.orm.mojo;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import net.legrange.orm.Database;
import net.legrange.orm.Orm;
import net.legrange.orm.Table;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author gideon
 */
@Mojo(name = "generate-sql", defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateSql extends AbstractMojo {

    @Parameter(property = "dialect", required = true)
    private Orm.Dialect dialect;
    @Parameter(property = "filePerTable", required = false, defaultValue = "false")
    private boolean filePerTable;
    @Parameter(property = "packages", required = true)
    private Set<String> packages;
    @Parameter(property = "outputDir", required = true)
    private String outputDir;
    @Component
    private MavenProject project;

    private URLClassLoader classLoader;
    private DialectGenerator gen;

    public GenerateSql() throws GeneratorException, DependencyResolutionRequiredException {
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        setupClassLoader();
        switch (dialect) {
            case MYSQL:
                gen = new MysqlDialectGenerator();
                break;
            default:
                throw new MojoExecutionException(format("Unsupported SQL Dialect '%s'. BUG?", dialect));
        }
        Set<Class<Database>> classes = getDatabaseClasses();
        if (classes.isEmpty()) {
            throw new MojoExecutionException(format("Could not find any database classes for packages %s", packages));
        }
        for (Class<Database> type : classes) {
            processDatabase(type);
        }
    }

    private void processDatabase(Class<Database> type) throws GeneratorException {
        try {
            StringBuilder sqlForDatabase = new StringBuilder();
            Database database = type.newInstance();
            info("Generating SQL for database %s", database.getSqlDatabase());
            for (Table table : database.getTables()) {
                if (!table.isAbstract()) {
                    String sqlForTable = processTable(table);
                    sqlForDatabase.append(sqlForTable);
                    sqlForDatabase.append("\n\n");
                    if (filePerTable) {
                        writeSqlFile(table.getSqlTable(), sqlForTable);
                    }
                }
            }
            writeSqlFile(database.getSqlDatabase(), sqlForDatabase.toString());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GeneratorException(format("Cannot instantiate table of type '%s' (%s)", type.getCanonicalName(), e.getMessage()), e);
        }
    }

    private String processTable(Table table) throws GeneratorException {
        return gen.generateSql(table);
    }

    private void writeSqlFile(String name, String sql) throws GeneratorException {
        String fileName = format("%s/%s.sql", outputDir, name);
        info("Writing SQL to %s", fileName);
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.print(sql);
        } catch (IOException e) {
            throw new GeneratorException(format("Error writing SQL to file '%s' (%s)", fileName, e.getMessage()), e);
        }
    }

    private Set<Class<Database>> getDatabaseClasses() throws GeneratorException {
        ScanResult scan = new ClassGraph()
                .enableAllInfo()
                .addClassLoader(classLoader)
                .whitelistPackages(packages.toArray(new String[]{}))
                .scan();
        ClassInfoList infos = scan.getClassesImplementing(Database.class.getCanonicalName());
        Set<Class<Database>> res = new HashSet<>();
        for (ClassInfo info : infos) {
            try {
                res.add((Class<Database>) classLoader.loadClass(info.getName()));
            } catch (ClassNotFoundException e) {
                throw new GeneratorException(format("Error loading classes '%s' from custom class loader (%s)", info.getName(), e.getMessage()), e);
            }
        }
        return res;
    }

    /**
     * Get a class loader that will load classes compiled during the build
     *
     * @return The class loader
     */
    private void setupClassLoader() throws GeneratorException {
        List<String> classpathElements = null;
        try {
            classpathElements = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new GeneratorException(format("Error getting compiled class path elements (%s)", e.getMessage()), e);
        }
        List<URL> projectClasspathList = new ArrayList<>();
        for (String element : classpathElements) {
            try {
                projectClasspathList.add(new File(element).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new GeneratorException(e.getMessage(), e);
            }
        }
        classLoader = new URLClassLoader(projectClasspathList.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());
        debug("Classloader created from %d elements", classpathElements.size());
    }

    private void debug(String fmt, Object... args) {
        getLog().debug(format(fmt, args));
    }

    private void info(String fmt, Object... args) {
        getLog().info(format(fmt, args));
    }
}
