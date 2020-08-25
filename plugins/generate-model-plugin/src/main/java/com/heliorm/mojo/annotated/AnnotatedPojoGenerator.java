package com.heliorm.mojo.annotated;

import com.heliorm.Database;
import com.heliorm.annotation.Pojo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.heliorm.mojo.GenerateModel;
import com.heliorm.mojo.Generator;
import com.heliorm.mojo.GeneratorException;

/**
 *
 * @author gideon
 */
public class AnnotatedPojoGenerator implements Generator<AnnotatedPojoTable> {

    private ScanResult scan;
    private final GenerateModel generator;


    public AnnotatedPojoGenerator(GenerateModel generator) throws GeneratorException {
        this.generator = generator;
        scan = new ClassGraph()
                .enableAllInfo()
                .addClassLoader(generator.getLocalClassLoader())
                .whitelistPackages(generator.getPackages().toArray(new String[]{}))
                .scan();
    }

    @Override
    public Set<Class<?>> getAllPojoClasses() throws GeneratorException {
        try {
            HashSet<Class<?>> res = new HashSet();
            ClassInfoList list = scan.getAllClasses().getStandardClasses();

            Map<String, ClassInfo> map = new HashMap<>();
            for (ClassInfo info : list) {
                if (info.hasAnnotation(Pojo.class.getName())) {
                    res.add(generator.getGlobalClassLoader().loadClass(info.getName()));
                }
                else {
                    map.put(info.getName(), info);
                }
            }
            // I think there must be a more elegant, possibly functional way of doing this
            ClassInfo add = null;
            do {
                if (add != null) {
                    res.add(generator.getGlobalClassLoader().loadClass(add.getName()));
                    map.remove(add.getName());
                    add=null;
                }
                for (Class<?> clazz : res) {
                    for (String name : map.keySet()) {
                        ClassInfo info = map.get(name);
                        if (clazz.getName().equals(info.getSuperclass().getName())) {
                            // clazz with Pojo annotation is super class for non-Pojo class
                            // this ain't right
                            add = info;
                        }
                    }
                }
            } while (add != null);
            return res;
        } catch (ClassNotFoundException ex) {
            throw new GeneratorException(ex.getMessage(), ex);
        }

    }

    @Override
    public AnnotatedPojoTable getPojoModel(Class clazz, Database database, Set<AnnotatedPojoTable> subTables) {
        return new AnnotatedPojoTable(database, clazz, subTables);
    }

}
