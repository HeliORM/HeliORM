package net.legrange.orm.mojo;

import net.legrange.orm.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gideon
 */
class Modeller<T extends Table> {

    private final Generator<T> gen;
    private Map<String, String> classPackageMap;
    private Map<String, PackageDatabase> packageDatabases;

    Modeller(Generator gen) throws GeneratorException {
        this.gen = gen;
        generate();
    }

    Map<String, PackageDatabase> getPackageDatabases() {
        return packageDatabases;
    }

    PackageDatabase getPackageDatabase(String className) {
        return packageDatabases.get(classPackageMap.get(className));
    }

    private void generate() throws GeneratorException {
        Set<Class<?>> allPojoClasses = gen.getAllPojoClasses();
        classPackageMap = makeDatabaseMap(allPojoClasses);
        Set<String> uniquePackages = classPackageMap.values().stream()
                .distinct()
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
            String parentName = entry.clazz.getSuperclass().getName();
            if (entryMap.containsKey(parentName)) {
                Entry parentEntry = entryMap.get(parentName);
                parentEntry.add(entry);
            } else {
                root.add(entry);
            }
        }
        return root;
    }

    private Map<String, String> makeDatabaseMap(Set<Class<?>> allPojoClasses) {
        Map<String, String> map = new HashMap();
        for (Class<?> clazz : allPojoClasses) {
            map.put(clazz.getCanonicalName(), clazz.getPackage().getName());
        }
        reduce(map);
        return map;
    }

    private void reduce(Map<String, String> map) {
        boolean changed = false;
        for (String c1 : map.keySet()) {
            String o1 = map.get(c1);
            for (String c2 : map.keySet()) {
                if (c1.equals(c2)) {
                    continue;
                }
                String o2 = map.get(c2);
                String com = common(o1, o2);
                if (com != null) {
                    map.put(c1, com);
                    map.put(c2, com);
                }
            }
        }

    }

    private String common(String o1, String o2) {
        if (o1.equals(o2)) {
            return o1;
        }
        if (o1.startsWith(o2)) {
            return o2;
        }
        if (o2.startsWith(o1)) {
            return o1;
        }
        int idx = o1.lastIndexOf('.');
        while (idx > 0) {
            o1 = o1.substring(0, idx);
            if (o2.startsWith(o1)) {
                return o1;
            }
            idx = o1.lastIndexOf('.');
        }
        return null;

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

        boolean isChildOf(Entry parent) {
            return clazz.getSuperclass().getName().equals(parent.clazz.getName());
        }


        private void dump(int indent) {
            for (int i = 0; i < indent; ++i) {
                System.out.print("  ");
            }
            System.out.println(clazz.getSimpleName());
            for (Entry k : kids) {
                k.dump(indent + 1);
            }
        }

    }
}
