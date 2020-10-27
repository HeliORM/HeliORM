package com.heliorm.mojo;

import com.heliorm.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author gideon
 */
class Modeller<T extends Table> {

    private final Generator<T> gen;
    private Map<String, String> classPackageMap;
    private Map<String, PackageDatabase> packageDatabases;

    Modeller(Generator gen, Set<String> topLevelPackages) throws GeneratorException {
        this.gen = gen;
        generate(topLevelPackages);
    }

    Map<String, PackageDatabase> getPackageDatabases() {
        return packageDatabases;
    }

    PackageDatabase getPackageDatabase(String className) {
        return packageDatabases.get(classPackageMap.get(className));
    }

    private void generate(Set<String> topLevelPackages) throws GeneratorException {
        Set<Class<?>> allPojoClasses = gen.getAllPojoClasses();
        classPackageMap = makeDatabaseMap(topLevelPackages, allPojoClasses);
        Set<String> uniquePackages = classPackageMap.values().stream()
                .collect(Collectors.toSet());
        packageDatabases = makeDatabases(uniquePackages);
        List<Entry> roots = buildTree(allPojoClasses);
        for (Entry entry : roots) {
            generate(entry);
        }
    }

    private T generate(Entry entry) {
        Set<T> subTables = entry.kids.stream()
                .map(e -> generate(e)).collect(Collectors.toSet());
        Class<?> pojoClass = entry.clazz;
        PackageDatabase db = packageDatabases.get(classPackageMap.get(pojoClass.getCanonicalName()));
        T table = gen.getPojoModel(pojoClass, db, subTables);
        db.addTable(table);
        return table;
    }

    private List<Entry> buildTree(Set<Class<?>> classes) {
        Map<String, Entry> classMap = classes.stream()
                .collect(Collectors.toMap(clazz -> clazz.getName(), clazz -> new Entry(clazz)));
        return buildTree(classMap);
    }

    private List<Entry> buildTree(Map<String, Entry> entryMap) {
        List<Entry> root = new ArrayList<>();
        for (String name : entryMap.keySet()) {
            Entry entry = entryMap.get(name);
            Class<?> superClass = entry.clazz.getSuperclass();
            String parentName = superClass.getName();
            if (entryMap.containsKey(parentName)) {
                Entry parentEntry = entryMap.get(parentName);
                parentEntry.add(entry);
            } else {
                root.add(entry);
            }
        }
        return root;
    }

    private Map<String, String> makeDatabaseMap(Set<String> topLevelPackages, Set<Class<?>> allPojoClasses) throws GeneratorException {
        Map<String, String> map = new HashMap();
        for (Class<?> clazz : allPojoClasses) {
            for (String tlp : topLevelPackages) {
                String name = clazz.getCanonicalName();
                if (name.startsWith(tlp)) {
                    if (map.containsKey(name)) {
                        throw new GeneratorException(format("Class '%s' matches more than one top level package (%s and %s)", tlp, map.get(name)));
                    }
                    map.put(name, tlp);
                    break;
                }
            }
        }
        return map;
    }

    private Map<String, PackageDatabase> makeDatabases(Set<String> packages) {
        return packages.stream()
                .collect(Collectors.toMap(pkg -> pkg, pkg -> new PackageDatabase(pkg)));
    }

    private static class Entry {

        private final Class<?> clazz;
        private final List<Entry> kids = new ArrayList();

        Entry(Class<?> clazz) {
            this.clazz = clazz;
        }


        void add(Entry child) {
            kids.add(child);
        }

    }
}
