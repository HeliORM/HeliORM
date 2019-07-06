package net.legrange.orm.mojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.legrange.orm.Table;

/**
 *
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
        List<Entry> entries = classes.stream().map(clazz -> new Entry(clazz)).collect(Collectors.toList());
        buildTree_(entries);
        return entries;
    }

    private void buildTree_(List<Entry> list) {
        boolean changed = true;
        while (changed) {
            changed = false;
            List<Entry> rootIdx = new ArrayList(list);
            for (Entry root : rootIdx) {
                if (list.contains(root)) {
                    List<Entry> entryIdx = new ArrayList(list);
                    for (Entry entry : entryIdx) {
                        if (list.contains(entry)) {
                            System.out.printf("%s vs %s\n", root.clazz.getCanonicalName(), entry.clazz.getCanonicalName());
                            if (entry.isChildOf(root)) {
                                System.out.println("1");
                                root.add(entry);
                                list.remove(entry);
                                changed = true;
                            } else if (root.isChildOf(entry)) {
                                System.out.println("2");
                                entry.add(root);
                                list.remove(root);
                                changed = true;
                            }
                        }
                    }
                }
            }
        }
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
            return clazz.getSuperclass().getCanonicalName().equals(parent.clazz.getCanonicalName());
        }

    }
}
