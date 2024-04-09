package com.heliorm.mojo;

import com.heliorm.Database;
import com.heliorm.Field;
import com.heliorm.Index;
import com.heliorm.Table;
import com.heliorm.def.BooleanField;
import com.heliorm.def.ByteField;
import com.heliorm.def.DateField;
import com.heliorm.def.DoubleField;
import com.heliorm.def.EnumField;
import com.heliorm.def.FloatField;
import com.heliorm.def.InstantField;
import com.heliorm.def.IntegerField;
import com.heliorm.def.LocalDateTimeField;
import com.heliorm.def.LongField;
import com.heliorm.def.ShortField;
import com.heliorm.def.StringField;
import com.heliorm.impl.FieldBuilder;
import com.heliorm.impl.IndexPart;
import com.heliorm.impl.TableBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import static java.lang.String.format;

/**
 * @author gideon
 */
class Output {

    private final GenerateModel gen;
    private final Database database;
    private final String packageName;
    private final Map<Table<?>, Set<Table<?>>> tables = new HashMap<>();

    private final StringBuilder buf = new StringBuilder();
    private final Set<String> imports = new HashSet<>();
    private int depth;
    private PrintWriter out;

    Output(GenerateModel gen, Database database, String packageName) {
        this.packageName = packageName;
        this.database = database;
        this.gen = gen;
    }

    void addTable(Table<?> table) {
        tables.put(table, table.getSubTables());
    }

    void output(String directory) throws GeneratorException, OrmMetaDataException {
        String databaseClass = gen.getDatabaseClassFor(database);
        String packageName = databaseClass.substring(0, databaseClass.lastIndexOf('.'));
        String path = directory + "/" + packageName.replace(".", "/");
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileName = path + "/Tables.java";
        try {
            for (Table<?> table : tables.keySet()) {
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
            for (String imp : imports.stream().sorted().toList()) {
                out.printf("import %s;\n", imp);
            }
            out.println("");
            out.println("public final class Tables implements Database {");
            out.println("");
            out.println("");
            out.print(buf);
            out.println("");

            StringJoiner sj = new StringJoiner(", ");
            for (Table<?> table : tables.keySet()) {
                sj.add(shortFieldName(table));
            }

            out.println("\t@Override");
            out.println("\tpublic final List<Table<?>> getTables() {");
            out.printf("\t\treturn Arrays.asList(%s);", sj);
            out.println("\n\t}");
            out.println("");

            out.println("\t@Override");
            out.println("\tpublic final String getSqlDatabase() {");
            out.printf("\t\treturn \"%s\";", database.getSqlDatabase());
            out.println("\n\t}");
            out.println("");

            out.printf("\tpublic final static Tables %s = new Tables();\n", shortDatabaseName());
            for (Table<?> table : tables.keySet()) {
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

    private void push() {
        depth++;
    }

    private void pop() {
        depth--;
    }

    private void emit(String fmt, Object... args) {
        buf.append(String.join("",Collections.nCopies(depth*4," ")));
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

    private void impt(String name) {
        imports.add(name);
    }

    private void impt(Class<?> clazz) {
        if (!clazz.getPackage().getName().equals(packageName) && (!clazz.isEnum() || (clazz.getEnclosingClass() == null))) {
            imports.add(clazz.getCanonicalName());
        }
    }

    private <O> void emit(Table<O> cm) throws OrmMetaDataException {
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
        emit("private transient TableBuilder<%s,%s> builder = TableBuilder.create(this);", tableName(cm), getJavaName(cm));
        emit("");
        for (Field<?, ?> fm : cm.getFields()) {
            addFieldModel(cm, fm);
            fieldNames.add(fm.getJavaName());
        }
        // getFields();
        impt(List.class);
        impt(Field.class);
        impt(Arrays.class);
        emit("");
        emit("@Override");
        emit("public List<Field<%s,?>> getFields() {", getJavaName(cm));
        push();
        emit(format("return Arrays.asList(%s);", fieldNames));
        pop();
        emit("}");
        // getPrimaryKey();
        impt(Optional.class);
        emit("");
        emit("@Override");
        emit("public Optional<Field<%s,?>> getPrimaryKey() {", getJavaName(cm));
        push();
        Optional<Field<O, ?>> opt = cm.getPrimaryKey();
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
        Set<Table<?>> subs = tables.get(cm);
        impt(List.class);
        impt(Set.class);
        if (subs.isEmpty()) {
            impt(Collections.class);
            emit("return Collections.EMPTY_SET;");
        } else {
            impt(Arrays.class);
            impt(HashSet.class);
            StringJoiner sj = new StringJoiner(",");
            Set<Table<?>> all = explodeSubs(subs);
            for (Table<?> sub : all) {
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

        // isRecord()
        push();
        emit("");
        emit("@Override");
        emit("public boolean isRecord() {");
        push();
        emit("return %b;", cm.isRecord());
        pop();
        emit("}");


        // getIndexes()
        StringJoiner indexNames = new StringJoiner(",");
        for (Index<?> im : cm.getIndexes()) {
            String idxName = addIndexModel(im);
            indexNames.add(idxName);
        }
        impt(Index.class);
        emit("");
        emit("@Override");
        emit("public List<Index<%s>> getIndexes() {", getJavaName(cm));
        push();
        emit(format("return Arrays.asList(%s);", indexNames));
        pop();
        emit("}");
        pop();

        emit("");
        emit("}");
        emit("");


    }

    private Set<Table<?>> explodeSubs(Set<Table<?>> subs) {
        Set<Table<?>> res = new HashSet<>(subs);
        for (Table<?> sub : subs) {
            res.addAll(explodeSubs(sub.getSubTables()));
        }
        return res;
    }

    private void addFieldModel(Table<?> cm, Field<?, ?> fm) throws OrmMetaDataException {
        switch (fm.getFieldType()) {
            case BYTE -> addByteField(cm, fm);
            case SHORT -> addShortField(cm, fm);
            case INTEGER -> addIntegerField(cm, fm);
            case LONG -> addLongField(cm, fm);
            case FLOAT -> addFloatField(cm, fm);
            case DOUBLE -> addDoubleField(cm, fm);
            case BOOLEAN -> addBooleanField(cm, fm);
            case DATE -> addDateField(cm, fm);
            case INSTANT -> addInstantField(cm, fm);
            case LOCAL_DATE_TIME -> addLocalDateTimeField(cm, fm);
            case STRING -> addStringField(cm, fm);
            case ENUM -> addEnumField(cm, fm);
            default ->
                    throw new OrmMetaDataException(format("Unsupported Pojo field type %s for field '%s' on class %s", fm.getFieldType(), fm.getJavaName(), getJavaName(cm)));
        }

    }

    private void addLongField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, LongField.class, "longField");
    }

    private void addIntegerField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, IntegerField.class, "integerField");

    }

    private void addShortField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, ShortField.class, "shortField");
    }

    private void addByteField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, ByteField.class, "byteField");
    }


    private void addDoubleField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, DoubleField.class, "doubleField");
    }

    private void addFloatField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, FloatField.class, "floatField");
    }

    private void addBooleanField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, BooleanField.class, "booleanField");
    }

    private void addEnumField(Table<?> cm, Field<?, ?> fm) {
        impt(EnumField.class);
        String enumTypeName;
        if (fm.getJavaType().getEnclosingClass() == null) {
            enumTypeName = fm.getJavaType().getCanonicalName();
        } else {
            enumTypeName = format("%s.%s", cm.getObjectClass().getSimpleName(), fm.getJavaType().getSimpleName());
        }
        emit("public final %s<%s, %s> %s = builder.enumField(\"%s\", %s.class)",
                EnumField.class.getSimpleName(),
                cm.getObjectClass().getSimpleName(),
                enumTypeName,
                fm.getJavaName(),
                fm.getJavaName(),
                enumTypeName);
        completeField(fm);
    }

    private void addDateField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, DateField.class, "dateField");
    }

    private void addInstantField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, InstantField.class, "instantField");
    }

    private void addLocalDateTimeField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, LocalDateTimeField.class, "localDateTimeField");
    }

    private void addStringField(Table<?> cm, Field<?, ?> fm) {
        addField(cm, fm, StringField.class, "stringField");
    }

    private String addIndexModel(Index<?> im) {
        impt(IndexPart.class);
        StringJoiner sj = new StringJoiner(",");
        for (Field<?, ?> field : im.getFields()) {
            sj.add(field.getJavaName());
        }
        String indexName = indexName(im);
        emit("private final %s %s = new %s(%s,Arrays.asList(%s));",
                IndexPart.class.getSimpleName(),
                indexName,
                IndexPart.class.getSimpleName(),
                im.isUnique(),
                sj.toString());
        return indexName;
    }

    private String indexName(Index<?> im) {
        StringJoiner sj = new StringJoiner("_");
        for (Field<?, ?> field : im.getFields()) {
            sj.add(field.getJavaName());
        }
        sj.add("idx");
        return sj.toString();
    }

    private void addField(Table<?> cm, Field<?, ?> fm, Class<? extends Field> fieldType, String buildMethod) {
        impt(fieldType);
        emit("public final %s<%s> %s = builder.%s(\"%s\")",
                fieldType.getSimpleName(),
                cm.getObjectClass().getSimpleName(),
                fm.getJavaName(),
                buildMethod,
                fm.getJavaName());
        completeField(fm);
    }

    private void completeField(Field<?, ?> fm) {
        push();
        emit(".withSqlName(\"%s\")", fm.getSqlName());
        if (fm.getLength().isPresent()) {
            emit(".withLength(%s)", fm.getLength().get());

        }
        emit(".withNullable(%b)", fm.isNullable());
        emit(".withPrimaryKey(%b)", fm.isPrimaryKey());
        emit(".withAutoNumber(%b)", fm.isAutoNumber());
        emit(".withForeignKey(%b)", fm.isForeignKey());
        if (fm.getForeignTable().isPresent()) {
            emit(".withForeignTable(%s)", shortFieldName(fm.getForeignTable().get()));
        }
        emit(".build();");
        pop();
    }

    private String tableName(Table<?> table) {
        return table.getObjectClass().getSimpleName() + "Table";
    }

    private String getJavaName(Table<?> table) {
        return table.getObjectClass().getSimpleName();
    }

    private String shortFieldName(Table<?> table) {
        return getJavaName(table).toUpperCase();
    }

    private String fullFieldName(Table<?> table) {
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
