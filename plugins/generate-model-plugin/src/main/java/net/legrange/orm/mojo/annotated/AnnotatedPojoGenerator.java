package net.legrange.orm.mojo.annotated;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import static java.lang.String.format;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.legrange.orm.Database;
import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.mojo.GenerateModel;
import net.legrange.orm.mojo.Generator;
import net.legrange.orm.mojo.GeneratorException;
import org.apache.maven.artifact.DependencyResolutionRequiredException;

/**
 *
 * @author gideon
 */
public class AnnotatedPojoGenerator implements Generator<AnnotatedPojoTable> {

    private ScanResult scan;
    private Map<String, AnnotatedPojoTable> map;
    private final GenerateModel generator;

    public AnnotatedPojoGenerator(GenerateModel generator) throws GeneratorException {
        try {
            this.generator = generator;
            scan = new ClassGraph()
                    .enableAllInfo()
                    .addClassLoader(generator.getCompiledClassesLoader())
                    .whitelistPackages(generator.getPackages().toArray(new String[]{}))
                    .scan();
        } catch (DependencyResolutionRequiredException ex) {
            throw new GeneratorException(format("Error setting up class path scanner (%s)", ex.getMessage()), ex);
        }
    }

    @Override
    public Set<Class<?>> getAllPojoClasses() throws GeneratorException {
        try {
            HashSet<Class<?>> res = new HashSet();
            ClassInfoList list = scan.getClassesWithAnnotation(Pojo.class.getName());
            for (ClassInfo info : list) {
                res.add(generator.getCompiledClassesLoader().loadClass(info.getName()));
            }
            return res;
        } catch (ClassNotFoundException | DependencyResolutionRequiredException ex) {
            throw new GeneratorException(ex.getMessage(), ex);
        }

    }

    @Override
    public AnnotatedPojoTable getPojoModel(Class clazz, Database database, Set<AnnotatedPojoTable> subTables) {
        return new AnnotatedPojoTable(database, clazz, subTables);
    }

}
