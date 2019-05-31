package me.legrange.orm.mojo.pojo;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import static java.lang.String.format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.legrange.orm.Table;
import me.legrange.orm.annotation.Pojo;
import me.legrange.orm.mojo.GenerateModel;
import me.legrange.orm.mojo.Generator;
import me.legrange.orm.mojo.GeneratorException;
import org.apache.maven.artifact.DependencyResolutionRequiredException;

/**
 *
 * @author gideon
 */
public class AnnotatedPojoGenerator extends Generator {

    private ScanResult scan;
    private Map<String, PojoClassModel> map;

    public AnnotatedPojoGenerator(GenerateModel generator) throws GeneratorException {
        super(generator);
        try {
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
    public List<Table> getPojoModels() throws GeneratorException {
        if (map == null) {
            populate();
        }
        return new ArrayList(map.values());
    }

    @Override
    public Table getPojoModel(Class clazz) throws GeneratorException {
        if (map == null) {
            populate();
        }
        return map.get(clazz.getCanonicalName());
    }

    private void populate() throws GeneratorException {
        List<Table> res = new ArrayList();
        map = new HashMap();
        Set<Class<?>> all = getAllPojoClasses();
        for (Class<?> pojoClass : all) {
            map.put(pojoClass.getCanonicalName(), null);
        }
        for (Class<?> pojoClass : all) {
            populateForPojo(pojoClass);
        }
    }

    private void populateForPojo(Class<?> pojoClass) {
        Table table = map.get(pojoClass.getCanonicalName());
        if (table == null) {
            Class<?> sup = pojoClass.getSuperclass();
            if (map.containsKey(sup.getCanonicalName())) {
                populateForPojo(sup);
                PojoClassModel pm = new PojoClassModel(pojoClass);
                map.put(pojoClass.getCanonicalName(), pm);
                PojoClassModel supTable = map.get(sup.getCanonicalName());
                supTable.addSub(pm);
            } else {
                map.put(pojoClass.getCanonicalName(), new PojoClassModel(pojoClass));
            }
        }
    }

    private Set<Class<?>> getAllPojoClasses() throws GeneratorException {
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

}
