package com.heliorm.mojo;

import com.heliorm.Database;
import com.heliorm.Table;
import com.heliorm.def.BooleanField;
import com.heliorm.def.ByteField;
import com.heliorm.def.DateField;
import com.heliorm.def.DoubleField;
import com.heliorm.def.DurationField;
import com.heliorm.def.EnumField;
import com.heliorm.def.Field;
import com.heliorm.def.FloatField;
import com.heliorm.def.Index;
import com.heliorm.def.IntegerField;
import com.heliorm.def.LongField;
import com.heliorm.def.ShortField;
import com.heliorm.def.StringField;
import com.heliorm.impl.IndexPart;
import com.heliorm.def.*;
import com.heliorm.impl.FieldBuilder;
import com.heliorm.impl.TableBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author gideon
 */
class Output {

    private final GenerateModel gen;
    private final Database database;
    private final String packageName;
    private final Map<Table, Set<Table>> tables = new HashMap();

    private final StringBuilder buf = new StringBuilder();
    private int depth;
    private PrintWriter out;
    private final Set<String> imports = new HashSet();

    Output(GenerateModel gen, Database database, String packageName) {
        this.packageName = packageName;
        this.database = database;
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
        if (!name.startsWith(packageName)) {
            imports.add("static " + name);
        }
    }

    void impt(String name) {
        imports.add(name);
    }

    void impt(Class clazz) {
        if (!clazz.getPackage().getName().equals(packageName) && (!clazz.isEnum() || (clazz.getEnclosingClass() == null))) {
            imports.add(clazz.getCanonicalName());
        }
    }

    void output(String directory) throws GeneratorException, OrmMetaDataException {
        String databaseClass = gen.getDatabaseClassFor(database);
        String packageName = databaseClass.substring(0,  databaseClass.lastIndexOf('.'));
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
            impt(Database.class.getCanonicalName());
            impt(Table.class.getCanonicalName());
            out.printf("package %s;\n", packageName);
            out.println("");
            for (String imp : imports.stream().sorted().collect(Collectors.toList())) {
                out.printf("import %s;\n", imp);
            }
            out.println("");
            out.println("public final class Tables implements Database {");
            out.println("");
            out.println("");
            out.print(buf.toString());
            out.println("");

            StringJoiner sj = new StringJoiner(", ");
            for (Table table : tables.keySet()) {
                sj.add(shortFieldName(table));
            }

            out.println("@Override");
            out.println("\tpublic final List<Table<?>> getTables() {");
            out.printf("\t\treturn Arrays.asList(%s);", sj.toString());
            out.println("\n\t}");
            out.println("");

            out.println("@Override");
            out.println("\tpublic final String getSqlDatabase() {");
            out.printf("\t\treturn \"%s\";", database.getSqlDatabase());
            out.println("\n\t}");
            out.println("");

            out.printf("\tpublic final static Tables %s = new Tables();\n", shortDatabaseName());
            for (Table table : tables.keySet()) {
                out.printf("\tpublic final static %s %s = new %s();\n", tableName(table), shortFieldName(table), tableName(table));
            }
            out.println("\n}");
        } catch (IOException ex) {
            throw new GeneratorException(format("Error opening output file '%s' (%s)", fileName, ex.getMessage()), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void emit(Table<?> cm) throws OrmMetaDataException, GeneratorException {
        impt(cm.getObjectClass());
        emit("public static class %s implements Table<%s> {",
                tableName(cm), getJavaName(cm));
        emit("");
        push();
        emit("private %s() {", tableName(cm));
        emit("}");
        emit("");
        StringJoiner fieldNames = new StringJoiner(",");
        impt(TableBuilder.class);
        impt(FieldBuilder.class);
        emit("private transient TableBuilder<%s,%s> builder = TableBuilder.create(this, %s.class);",  tableName(cm), getJavaName(cm), getJavaName(cm));
        emit("");
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
        emit("public Set<Table<?>> getSubTables() {", getJavaName(cm));
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

        // getDatabase()
        push();
        emit("");
        emit("@Override");
        emit("public Database getDatabase() {");
        push();
        emit("return %s;", shortDatabaseName());
        pop();
        emit("}");
        pop();

        // isAbstract()
        push();
        emit("");
        emit("@Override");
        emit("public boolean isAbstract() {");
        push();
        emit("return %b;", cm.isAbstract());
        pop();
        emit("}");


        // getIndexes()
        StringJoiner indexNames = new StringJoiner(",");
        for (Index im : cm.getIndexes()) {
            String idxName = addIndexModel(cm, im);
            indexNames.add(idxName);
        }
        impt(Index.class);
        emit("");
        emit("@Override");
        emit("public List<Index> getIndexes() {");
        push();
        emit(format("return Arrays.asList(%s);", indexNames.toString()));
        pop();
        emit("}");
        pop();

        emit("");
        emit("}");
        emit("");


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
            case INSTANT:
                addTimestampField(cm, fm);
                break;
            case DURATION:
                addDurationField(cm, fm);
                break;
            case STRING:
                addStringField(cm, fm);
                break;
            case ENUM:
                addEnumField(cm, fm);
                break;
            case SET :
                addSetField(cm, fm);
                break;
            case LIST :
                addListField(cm, fm);
                break;
            default:
                throw new OrmMetaDataException(format("Unsupported Pojo field type %s for field '%s' on class %s", fm.getFieldType(), fm.getJavaName(), getJavaName(cm)));
        }

    }

    private void addLongField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, LongField.class, "longField");
    }

    private void addIntegerField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, IntegerField.class, "integerField");

    }

    private void addShortField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, ShortField.class, "shortField");
    }

    private void addByteField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, ByteField.class, "byteField");
    }


    private void addDoubleField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, DoubleField.class, "doubleField");
    }

    private void addFloatField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, FloatField.class, "floatField");
    }

    private void addBooleanField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, BooleanField.class, "booleanField");
    }

    private void addEnumField(Table cm, Field fm) throws GeneratorException {
        impt(EnumField.class);
        String enumTypeName;
        if (fm.getJavaType().getEnclosingClass() == null) {
            enumTypeName = fm.getJavaType().getCanonicalName()  ;
        } else {
            enumTypeName = format("%s.%s", cm.getObjectClass().getSimpleName(), fm.getJavaType().getSimpleName());
        }
        emit("public final %s<%s, %s, %s> %s = builder.enumField(\"%s\", %s.class)",
                EnumField.class.getSimpleName(),
                tableName(cm),
                cm.getObjectClass().getSimpleName(),
                enumTypeName,
                fm.getJavaName(),
                fm.getJavaName(),
                enumTypeName);
        completeField(fm);
    }

    private void addDateField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, DateField.class, "dateField");
    }

    private void addTimestampField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, InstantField.class, "timestampField");
    }

    private void addDurationField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, DurationField.class, "durationField");

    }

    private void addStringField(Table cm, Field fm) throws GeneratorException {
        addField(cm, fm, StringField.class, "stringField");
    }


    private void addSetField(Table cm, Field fm) throws GeneratorException {
        addCollectionField(cm, fm, SetField.class, "setField");
    }

    private void addListField(Table cm, Field fm) throws GeneratorException {
        addCollectionField(cm, fm, ListField.class, "listField");
    }

    private void addCollectionField(Table<?> cm, Field<?,?,?> fm,  Class<? extends Field> fieldType,  String buildMethod) throws GeneratorException {
        impt(fieldType);
        impt(fm.getCollectionTable().get().getObjectClass());
        emit("public final %s<%s, %s, %s> %s = builder.%s(\"%s\", %s.class)",
                fieldType.getSimpleName(),
                tableName(cm),
                cm.getObjectClass().getSimpleName(),
                fm.getCollectionTable().get().getObjectClass().getSimpleName(),
                fm.getJavaName(),
                buildMethod,
                fm.getJavaName(),
                fm.getCollectionTable().get().getObjectClass().getSimpleName());
        completeField(fm);
    }

    private String addIndexModel(Table tm, Index<?, ?> im) {
        impt(IndexPart.class);
        StringJoiner sj = new StringJoiner(",");
        for (Field field : im.getFields()) {
            sj.add(field.getJavaName());
        }
        String indexName = indexName(tm, im);
        emit("private final %s %s = new %s(%s,Arrays.asList(%s));",
                IndexPart.class.getSimpleName(),
                indexName,
                IndexPart.class.getSimpleName(),
                im.isUnique(),
                sj.toString());
        return indexName;
    }

    private String indexName(Table tm, Index<?,?> im) {
        StringJoiner sj = new StringJoiner("_");
        for (Field field : im.getFields()) {
            sj.add(field.getJavaName());
        }
        sj.add("idx");
        return sj.toString();
    }

    private void addField(Table<?> cm, Field<?,?,?> fm, Class<? extends Field> fieldType, String buildMethod) {
        impt(fieldType);
        emit("public final %s<%s, %s> %s = builder.%s(\"%s\")",
                fieldType.getSimpleName(),
                tableName(cm),
                cm.getObjectClass().getSimpleName(),
                fm.getJavaName(),
                buildMethod,
                fm.getJavaName());
        completeField(fm);
    }

    private void completeField(Field<?,?,?> fm) {
        push();
        emit(".withPrimaryKey(%b)", fm.isPrimaryKey());
        emit(".withAutoNumber(%b)", fm.isAutoNumber());
        emit(".withForeignKey(%b)", fm.isForeignKey());
        emit(".withNullable(%b)", fm.isNullable());
        emit(".withSqlName(\"%s\")", fm.getSqlName());
        if (fm.getLength().isPresent()) {
            emit(".withLength(%s)", fm.getLength().get());

        }
        if (fm.getForeignTable().isPresent()) {
            emit(".withForeignTable(%s)",  shortFieldName(fm.getForeignTable().get()));
        }
        if (fm.isCollection()) {
            emit(".withCollection(%s)", shortFieldName(fm.getCollectionTable().get()));
        }
        emit(".build();");
        pop();
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
        return gen.getTablesPackageFor(table) + ".Tables." + shortFieldName(table);
    }

    private String shortDatabaseName() {
        return database.getSqlDatabase().toUpperCase();
    }

    @Override
    public String toString() {
        return "Output{" + "packageName=" + packageName + '}';
    }

}
