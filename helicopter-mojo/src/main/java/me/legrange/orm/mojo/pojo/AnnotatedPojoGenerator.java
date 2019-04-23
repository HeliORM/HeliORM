package me.legrange.orm.mojo.pojo;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import static java.lang.String.format;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.legrange.orm.annotation.Pojo;
import me.legrange.orm.mojo.GenerateModel;
import me.legrange.orm.mojo.Generator;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import me.legrange.orm.mojo.ClassModel;

/**
 *
 * @author gideon
 */
public class AnnotatedPojoGenerator extends Generator {

    private ScanResult scan;

    public AnnotatedPojoGenerator(GenerateModel generator) throws MojoExecutionException {
        super(generator);
        try {
            scan = new ClassGraph()
                    .enableAllInfo()
                    .addClassLoader(generator.getCompiledClassesLoader())
                    .whitelistPackages(generator.getPackages().toArray(new String[]{}))
                    .scan();
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException(format("Error setting up class path scanner (%s)", ex.getMessage()), ex);
        }
    }

    @Override
    public List<ClassModel> getPojoModels() throws MojoExecutionException {
        List<ClassModel> res = new ArrayList();
        for (Class<?> pojoClass : getAllPojoClasses()) {
            res.add(makePojoModel(pojoClass));
        }
        return res;
    }

    private ClassModel makePojoModel(Class<?> pojoClass) {
        return new PojoClassModel(pojoClass);
    }

    private Set<Class<?>> getAllPojoClasses() throws MojoExecutionException {
        try {
            HashSet<Class<?>> res = new HashSet();
            ClassInfoList list = scan.getClassesWithAnnotation(Pojo.class.getName());
            for (ClassInfo info : list) {
                res.add(generator.getCompiledClassesLoader().loadClass(info.getName()));
            }
            return res;
        } catch (ClassNotFoundException | DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }

    }

}
