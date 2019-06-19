package net.legrange.orm.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import net.legrange.orm.BooleanField;
import net.legrange.orm.ByteField;
import net.legrange.orm.DateField;
import net.legrange.orm.DoubleField;
import net.legrange.orm.EnumField;
import net.legrange.orm.Field;
import net.legrange.orm.FloatField;
import net.legrange.orm.IntegerField;
import net.legrange.orm.LongField;
import net.legrange.orm.OrmMetaDataException;
import net.legrange.orm.ShortField;
import net.legrange.orm.StringField;
import net.legrange.orm.Table;
import net.legrange.orm.impl.BooleanFieldPart;
import net.legrange.orm.impl.ByteFieldPart;
import net.legrange.orm.impl.DateFieldPart;
import net.legrange.orm.impl.DoubleFieldPart;
import net.legrange.orm.impl.EnumFieldPart;
import net.legrange.orm.impl.FieldPart;
import net.legrange.orm.impl.FloatFieldPart;
import net.legrange.orm.impl.IntegerFieldPart;
import net.legrange.orm.impl.LongFieldPart;
import net.legrange.orm.impl.ShortFieldPart;
import net.legrange.orm.impl.StringFieldPart;

/**
 *
 * @author gideon
 */
class Output {

    private final StringBuilder buf = new StringBuilder();
    private int depth;
    private PrintWriter out;
    private final Set<String> imports = new HashSet();
    private final String packageName;
    private final Map<Table, Set<Table>> tables = new HashMap();
    private final GenerateModel gen;

    Output(GenerateModel gen, String packageName) {
        this.packageName = packageName;
        this.gen = gen;
    }

    void addTable(Table table) {
        tables.put(table, table.getSubTables());
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

    private void impts(String name) {
        imports.add("static " + name);
    }

    void impt(String name) {
        imports.add(name);
    }

    void impt(Class clazz) {
        if (!clazz.getPackage().getName().equals(packageName) || clazz.isEnum()) {
            imports.add(clazz.getCanonicalName());
        }
    }

    void output(String directory) throws GeneratorException, OrmMetaDataException {
        String path = directory + "/" + packageName.replace(".", "/");
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileName = path + "/Tables.java";
        try {
            for (Table table : tables.keySet()) {
                push();
                emit(table);
                pop();
                emit("");
            }
            out = new PrintWriter(new FileWriter(fileName));
            impt(Table.class.getCanonicalName());
            out.printf("package %s;\n", packageName);
            out.println("");
            for (String imp : imports.stream().sorted().collect(Collectors.toList())) {
                out.printf("import %s;\n", imp);
            }
            out.println("");
            out.println("public final class Tables {");
            out.println("");
            out.print(buf.toString());
            out.println("");
            for (Table table : tables.keySet()) {
                out.printf("public final static %s %s = new %s();\n", tableName(table), shortFieldName(table), tableName(table));
            }
            out.println("}");
        } catch (IOException ex) {
            throw new GeneratorException(format("Error opening output file '%s' (%s)", fileName, ex.getMessage()), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void emit(Table<?> cm) throws OrmMetaDataException, GeneratorException {
        emit("public static class %s implements Table<%s> {",
                tableName(cm), getJavaName(cm));
        emit("");
        push();
        StringJoiner fieldNames = new StringJoiner(",");
        for (Field fm : cm.getFields()) {
            addFieldModel(cm, fm);
            fieldNames.add(fm.getJavaName());
        }
        // getFields();
        impt(List.class);
        impt(Field.class);
        impt(Arrays.class);
        emit("");
        emit("@Override");
        emit("public List<Field> getFields() {");
        push();
        emit(format("return Arrays.asList(%s);", fieldNames.toString()));
        pop();
        emit("}");
        // getPrimaryKey();
        impt(Optional.class);
        emit("");
        emit("@Override");
        emit("public Optional<Field> getPrimaryKey() {");
        push();
        Optional<Field> opt = cm.getPrimaryKey();
        if (opt.isPresent()) {
            emit("return Optional.of(%s);", opt.get().getJavaName());
        } else {
            emit("return Optional.empty();");
        }
        pop();
        emit("}");
        // getSqlTable();
        emit("");
        emit("@Override");
        emit("public String getSqlTable() {");
        push();
        emit("return \"%s\";", cm.getSqlTable());
        pop();
        emit("}");
        // getObjectClass();
        emit("");
        emit("@Override");
        emit("public Class<%s> getObjectClass() {", getJavaName(cm));
        push();
        emit("return %s.class;", getJavaName(cm));
        pop();
        emit("}");
        // getSubTables();
        emit("");
        emit("@Override");
        emit("public Set<Table> getSubTables() {", getJavaName(cm));
        push();
        Set<Table> subs = tables.get(cm);
        impt(List.class);
        impt(Set.class);
        if (subs.isEmpty()) {
            impt(Collections.class);
            emit("return Collections.EMPTY_SET;");
        } else {
            impt(Arrays.class);
            impt(HashSet.class);
            StringJoiner sj = new StringJoiner(",");
            Set<Table> all = explodeSubs(subs);
            for (Table sub : all) {
                impts(fullFieldName(sub));
                sj.add(shortFieldName(sub));
            }
            emit("return new HashSet(Arrays.asList(%s));", sj.toString());
        }
        pop();
        emit("}");
        pop();

        emit("");
        emit("}");
        emit("");
        gen.addToService(packageName + ".Tables$" + tableName(cm));
    }

    private Set<Table> explodeSubs(Set<Table> subs) {
        Set<Table> res = new HashSet(subs);
        for (Table sub : subs) {
            res.addAll(explodeSubs(sub.getSubTables()));
        }
        return res;
    }

    private void addFieldModel(Table cm, Field fm) throws OrmMetaDataException, GeneratorException {
        switch (fm.getFieldType()) {
            case BYTE:
                addByteField(cm, fm);
                break;
            case SHORT:
                addShortField(cm, fm);
                break;
            case INTEGER:
                addIntegerField(cm, fm);
                break;
            case LONG:
                addLongField(cm, fm);
                break;
            case FLOAT:
                addFloatField(cm, fm);
                break;
            case DOUBLE:
                addDoubleField(cm, fm);
                break;
            case BOOLEAN:
                addBooleanField(cm, fm);
                break;
            case DATE:
                addDateField(cm, fm);
                break;
            case STRING:
                addStringField(cm, fm);
                break;
            case ENUM:
                addEnumField(cm, fm);
                break;
            default:
                throw new OrmMetaDataException(format("Unsupported Pojo field type %s for field '%s' on class %s", fm.getFieldType(), fm.getJavaName(), getJavaName(cm)));
        }

    }

    private void addType2Field(Class<? extends Field> fieldClass, Class<? extends FieldPart> partClass, Table cm, Field fm) throws GeneratorException {
        impt(fieldClass);
        impt(partClass);
        boolean isKey = fm.isPrimaryKey();
        if (isKey) {
            emit("public final %s<%s, %s> %s = new %s(\"%s\", \"%s\", true);",
                    fieldClass.getSimpleName(),
                    tableName(cm),
                    cm.getObjectClass().getSimpleName(),
                    fm.getJavaName(),
                    partClass.getSimpleName(),
                    fm.getJavaName(),
                    fm.getSqlName());
        } else {
            emit("public final %s<%s, %s> %s = new %s(\"%s\", \"%s\");",
                    fieldClass.getSimpleName(),
                    tableName(cm),
                    cm.getObjectClass().getSimpleName(),
                    fm.getJavaName(),
                    partClass.getSimpleName(),
                    fm.getJavaName(),
                    fm.getSqlName());
        }
    }

    private void addType3Field(Class<? extends Field> fieldClass, Class<? extends FieldPart> partClass, Table cm, Field fm) throws GeneratorException {
        impt(fieldClass);
        impt(partClass);
        impt(fm.getJavaType());
        emit("public final %s<%s, %s, %s> %s = new %s(%s.class, \"%s\", \"%s\");",
                fieldClass.getSimpleName(),
                tableName(cm),
                cm.getObjectClass().getSimpleName(),
                fm.getJavaType().getSimpleName(),
                fm.getJavaName(),
                partClass.getSimpleName(),
                fm.getJavaType().getSimpleName(),
                fm.getJavaName(), fm.getSqlName());
    }

    private void addLongField(Table cm, Field fm) throws GeneratorException {
        addType2Field(LongField.class, LongFieldPart.class, cm, fm);
    }

    private void addIntegerField(Table cm, Field fm) throws GeneratorException {
        addType2Field(IntegerField.class, IntegerFieldPart.class, cm, fm);
    }

    private void addShortField(Table cm, Field fm) throws GeneratorException {
        addType2Field(ShortField.class, ShortFieldPart.class, cm, fm);
    }

    private void addByteField(Table cm, Field fm) throws GeneratorException {
        addType2Field(ByteField.class, ByteFieldPart.class, cm, fm);
    }

    private void addDoubleField(Table cm, Field fm) throws GeneratorException {
        addType2Field(DoubleField.class, DoubleFieldPart.class, cm, fm);
    }

    private void addFloatField(Table cm, Field fm) throws GeneratorException {
        addType2Field(FloatField.class, FloatFieldPart.class, cm, fm);
    }

    private void addBooleanField(Table cm, Field fm) throws GeneratorException {
        addType2Field(BooleanField.class, BooleanFieldPart.class, cm, fm);
    }

    private void addEnumField(Table cm, Field fm) throws GeneratorException {
        impt(fm.getJavaType());
        addType3Field(EnumField.class, EnumFieldPart.class, cm, fm);
    }

    private void addDateField(Table cm, Field fm) throws GeneratorException {
        addType2Field(DateField.class, DateFieldPart.class, cm, fm);
    }

    private void addStringField(Table cm, Field fm) throws GeneratorException {
        addType2Field(StringField.class, StringFieldPart.class, cm, fm);
    }

    private String tableName(Table table) {
        return table.getObjectClass().getSimpleName() + "Table";
    }

    private String getJavaName(Table table) {
        return table.getObjectClass().getSimpleName();
    }

    private String shortFieldName(Table table) {
        return getJavaName(table).toUpperCase();
    }

    private String fullFieldName(Table table) throws GeneratorException {
        return gen.getOutputFor(table).getPackageName() + ".Tables." + shortFieldName(table);
    }

    @Override
    public String toString() {
        return "Output{" + "packageName=" + packageName + '}';
    }

}