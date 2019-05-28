package me.legrange.orm.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
class Output {

    private final Set<String> imports = new HashSet();
    private final StringBuilder buf = new StringBuilder();
    private int depth;
    private final String packageName;
    private PrintWriter out;

    Output(String packageName) {
//        this.fileName = fileName;
        this.packageName = packageName;
    }

    String getPackageName() {
        return packageName;
    }

    void push() {
        depth++;
    }

    void pop() {
        depth--;
    }

    void emit(String fmt, Object... args) {
        for (int i = 0; i < depth; ++i) {
            buf.append("    ");
        }
        buf.append(format(fmt, args));
        if (!fmt.endsWith("\n")) {
            buf.append("\n");
        }
    }

    void impt(String name) {
        imports.add(name);
    }

    void impt(Class clazz) {
        if (!clazz.getPackageName().equals(packageName) || clazz.isEnum()) {
            imports.add(clazz.getCanonicalName());
        }
    }

    void output(String directory) throws GeneratorException {
        String path = directory + "/" + packageName.replace(".", "/");
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileName = path + "/Tables.java";
        try {
            out = new PrintWriter(new FileWriter(fileName));
            impt(Table.class.getCanonicalName());
            out.printf("package %s;\n", packageName);
            out.printf("\n");
            for (String imp : imports.stream().sorted().collect(Collectors.toList())) {
                out.printf("import %s;\n", imp);
            }
            out.printf("\n");
            out.printf("public final class Tables {\n");
            out.printf("\n");
            push();

            out.println();
            out.print(buf.toString());
            out.println();
            out.println("}");
        } catch (IOException ex) {
            throw new GeneratorException(format("Error opening output file '%s' (%s)", fileName, ex.getMessage()), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public String toString() {
        return "Output{" + "packageName=" + packageName + '}';
    }

}
