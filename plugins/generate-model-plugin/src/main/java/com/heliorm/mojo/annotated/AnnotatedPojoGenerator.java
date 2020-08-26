package com.heliorm.mojo.annotated;

import com.heliorm.Database;
import com.heliorm.annotation.Pojo;
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

import com.heliorm.mojo.GenerateModel;
import com.heliorm.mojo.Generator;
import com.heliorm.mojo.GeneratorException;

/**
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
            ClassInfoList all = scan.getAllClasses().getStandardClasses();
            ClassInfoList anno = scan.getClassesWithAnnotation(Pojo.class.getName());
            for (ClassInfo info : findMissingSubClasses(anno, all)) {
                res.add(generator.getGlobalClassLoader().loadClass(info.getName()));
            }
            return res;
        } catch (ClassNotFoundException ex) {
            throw new GeneratorException(ex.getMessage(), ex);
        }

    }

    @Override
    public AnnotatedPojoTable getPojoModel(Class clazz, Database database, Set<AnnotatedPojoTable> subTables) {
        return new AnnotatedPojoTable(database, clazz, subTables);
    }

    private List<ClassInfo> findMissingSubClasses(ClassInfoList annos, ClassInfoList all) {
        List<ClassInfo> res = new ArrayList<>();
        for (ClassInfo info : annos) {
            res.add(info);
            res.addAll(findMissingSubClasses(info, all));
        }
        return res;
    }


    private List<ClassInfo> findMissingSubClasses(ClassInfo superClass, ClassInfoList possibleSubClasses) {
        List<ClassInfo> subClasses = new ArrayList();
        for (ClassInfo info : possibleSubClasses) {
            if (info.hasAnnotation(Pojo.class.getName())) {
                continue;
            }
            if (info.getSuperclass() == null) {
                continue;
            }
            if (info.getSuperclass().equals(superClass) && !info.hasAnnotation(Pojo.class.getName())) {
                subClasses.add(info);
                subClasses.addAll(findMissingSubClasses(info, possibleSubClasses));
            }
        }
        return subClasses;
    }

}
