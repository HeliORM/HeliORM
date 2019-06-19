package net.legrange.orm.mojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gideon
 */
class PackageOrganizer {

    static Map<String, Output> organize(GenerateModel gen, List<Class> classes) {
        Map<String, Output> map = new HashMap();
        for (Class clazz : classes) {
            Output out = new Output(gen, clazz.getPackage().getName());
            map.put(clazz.getCanonicalName(), out);
        }
        reduce(gen, map);
        System.out.println(map);
        return map;
    }

    static private void reduce(GenerateModel gen, Map<String, Output> map) {
        boolean changed = false;
        for (String c1 : map.keySet()) {
            Output o1 = map.get(c1);
            for (String c2 : map.keySet()) {
                if (c1.equals(c2)) {
                    continue;
                }
                Output o2 = map.get(c2);
                Output com = common(gen, o1, o2);
                if (com != null) {
                    map.put(c1, com);
                    map.put(c2, com);
                }
            }
        }
    }

    static private Output common(GenerateModel gen, Output o1, Output o2) {
        if (o1.getPackageName().equals(o2.getPackageName())) {
            return o1;
        }
        if (o1.getPackageName().startsWith(o2.getPackageName())) {
            return o2;
        }
        if (o2.getPackageName().startsWith(o1.getPackageName())) {
            return o1;
        }
        String val1 = o1.getPackageName();
        String val2 = o2.getPackageName();
        int idx = val1.lastIndexOf('.');
        while (idx > 0) {
            val1 = val1.substring(0, idx);
            if (val2.startsWith(val1)) {
                return new Output(gen, val1);
            }
            idx = val1.lastIndexOf('.');
        }
        return null;
    }

}
