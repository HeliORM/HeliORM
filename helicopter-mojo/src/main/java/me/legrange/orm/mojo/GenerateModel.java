package me.legrange.orm.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.legrange.orm.OrmMetaDataException;
import me.legrange.orm.Table;
import me.legrange.orm.mojo.pojo.AnnotatedPojoGenerator;
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

/**
 *
 * @author gideon
 */
@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateModel extends AbstractMojo {

    @Parameter(property = "strategy", required = true)
    private Generator.PojoStrategy strategy;
    @Parameter(property = "packages", required = true)
    private Set<String> packages;
    @Parameter(property = "outputDir", required = false)
    private String outputDir;
    @Parameter(property = "resourceDir", required = false)
    private String resourceDir;
    @Component
    private MavenProject project;
    private Generator gen;
    private Map<String, Output> outputs;
    private PrintWriter svc;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            switch (strategy) {
                case annotated:
                    gen = new AnnotatedPojoGenerator(this);
                    break;
                default:
                    throw new MojoExecutionException(format("Unsupported POJO strategy '%s'. BUG?", strategy));
            }
            outputs = PackageOrganizer.organize(this, gen.getPojoModels().stream().map(pm -> pm.getObjectClass()).collect(Collectors.toList()));
            File dir = new File(resourceDir + "/META-INF/services/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            svc = new PrintWriter(new FileWriter(resourceDir + "/META-INF/services/" + Table.class.getCanonicalName()));
            for (Table pm : gen.getPojoModels()) {
                getOutputFor(pm).addTable(pm);
            }
            Set<Output> uniqueOuts = new HashSet(outputs.values());
            for (Output out : uniqueOuts) {
                out.output(outputDir);
            }
            svc.close();
        } catch (GeneratorException | OrmMetaDataException | IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    /**
     * Get a class loader that will load classes compiled during the build
     *
     * @return The class loader
     * @throws me.legrange.orm.mojo.GeneratorException
     * @throws org.apache.maven.artifact.DependencyResolutionRequiredException
     */
    public ClassLoader getCompiledClassesLoader() throws GeneratorException, DependencyResolutionRequiredException {
        List<String> classpathElements = project.getCompileClasspathElements();
        List<URL> projectClasspathList = new ArrayList<>();
        for (String element : classpathElements) {
            try {
                projectClasspathList.add(new File(element).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new GeneratorException(element + " is an invalid classpath element", e);
            }
        }
        return new URLClassLoader(projectClasspathList.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());

    }

    public Set<String> getPackages() {
        return packages;
    }

    Output getOutputFor(Table table) throws GeneratorException {
        Class<?> clazz = table.getObjectClass();
        Output out = getOutputFor(clazz);
        if (out == null) {
            throw new GeneratorException(format("Cannot find output table for class '%s'. BUG!", clazz.getCanonicalName()));
        }
        return out;
    }

    void addToService(String name) {
        svc.println(name);
    }

    private Output getOutputFor(Class<?> clazz) throws GeneratorException {
        return outputs.get(clazz.getCanonicalName());
    }

}
