package me.legrange.orm.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.orm.BooleanField;
import me.legrange.orm.ByteField;
import me.legrange.orm.DateField;
import me.legrange.orm.DoubleField;
import me.legrange.orm.EnumField;
import me.legrange.orm.Field;
import me.legrange.orm.FloatField;
import me.legrange.orm.IntegerField;
import me.legrange.orm.LongField;
import me.legrange.orm.OrmMetaDataException;
import me.legrange.orm.ShortField;
import me.legrange.orm.StringField;
import me.legrange.orm.Table;
import me.legrange.orm.impl.BooleanFieldPart;
import me.legrange.orm.impl.ByteFieldPart;
import me.legrange.orm.impl.DateFieldPart;
import me.legrange.orm.impl.DoubleFieldPart;
import me.legrange.orm.impl.EnumFieldPart;
import me.legrange.orm.impl.FieldPart;
import me.legrange.orm.impl.FloatFieldPart;
import me.legrange.orm.impl.IntegerFieldPart;
import me.legrange.orm.impl.LongFieldPart;
import me.legrange.orm.impl.ShortFieldPart;
import me.legrange.orm.impl.StringFieldPart;
import me.legrange.orm.mojo.pojo.AnnotatedPojoGenerator;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author gideon
 */
@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateModel extends AbstractMojo {

    @Parameter(property = "strategy", required = true)
    private Generator.PojoStrategy strategy;
    @Parameter(property = "packages", required = true)
    private Set<String> packages;
    @Parameter(property = "outputDir", required = false)
    private String outputDir;
    @Component
    private MavenProject project;

    private final Set<String> imports = new HashSet();
    private final StringBuilder buf = new StringBuilder();
    private int depth;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Generator gen;
            switch (strategy) {
                case annotated:
                    gen = new AnnotatedPojoGenerator(this);
                    break;
                default:
                    throw new MojoExecutionException(format("Unsupported POJO strategy '%s'. BUG?", strategy));
            }
            openMetaModel();
            for (Table pm : gen.getPojoModels()) {
                addClassModel(pm);
            }
            emit("");
            for (Table cm : gen.getPojoModels()) {
                addClassField(cm);
            }
            closeMetaModel();
            output();
        } catch (OrmMetaDataException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private void addClassModel(Table<?> cm) throws OrmMetaDataException {
        emit("public static final class %sTable implements Table<%s> {", getJavaName(cm), getJavaName(cm));
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
        pop();

        emit("");
        emit("}");
        emit("");
    }

    private String getJavaName(Table table) {
        return table.getObjectClass().getSimpleName();
    }

    private void addClassField(Table cm) {
        impt(cm.getObjectClass());
        emit("public static final %sTable %s = new %sTable();", getJavaName(cm), getJavaName(cm).toUpperCase(), getJavaName(cm));
    }

    private void addFieldModel(Table cm, Field fm) throws OrmMetaDataException {
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

    private void addType2Field(Class<? extends Field> fieldClass, Class<? extends FieldPart> partClass, Table cm, Field fm) {
        impt(fieldClass);
        impt(partClass);
        emit("public final %s<%sTable, %s> %s = new %s(\"%s\", \"%s\");",
                fieldClass.getSimpleName(),
                getJavaName(cm), getJavaName(cm), fm.getJavaName(),
                partClass.getSimpleName(),
                fm.getJavaName(), fm.getSqlName());
    }

    private void addType3Field(Class<? extends Field> fieldClass, Class<? extends FieldPart> partClass, Table cm, Field fm) {
        impt(fieldClass);
        impt(partClass);
        impt(fm.getJavaType());
        emit("public final %s<%sTable, %s, %s> %s = new %s(%s.class, \"%s\", \"%s\");",
                fieldClass.getSimpleName(),
                getJavaName(cm), getJavaName(cm), fm.getJavaType().getSimpleName(),
                fm.getJavaName(),
                partClass.getSimpleName(),
                fm.getJavaType().getSimpleName(),
                fm.getJavaName(), fm.getSqlName());
    }

    private void addLongField(Table cm, Field fm) {
        addType2Field(LongField.class, LongFieldPart.class, cm, fm);
    }

    private void addIntegerField(Table cm, Field fm) {
        addType2Field(IntegerField.class, IntegerFieldPart.class, cm, fm);
    }

    private void addShortField(Table cm, Field fm) {
        addType2Field(ShortField.class, ShortFieldPart.class, cm, fm);
    }

    private void addByteField(Table cm, Field fm) {
        addType2Field(ByteField.class, ByteFieldPart.class, cm, fm);
    }

    private void addDoubleField(Table cm, Field fm) {
        addType2Field(DoubleField.class, DoubleFieldPart.class, cm, fm);
    }

    private void addFloatField(Table cm, Field fm) {
        addType2Field(FloatField.class, FloatFieldPart.class, cm, fm);
    }

    private void addBooleanField(Table cm, Field fm) {
        addType2Field(BooleanField.class, BooleanFieldPart.class, cm, fm);
    }

    private void addEnumField(Table cm, Field fm) {
        addType3Field(EnumField.class, EnumFieldPart.class, cm, fm);
    }

    private void addDateField(Table cm, Field fm) {
        addType2Field(DateField.class, DateFieldPart.class, cm, fm);
    }

    private void addStringField(Table cm, Field fm) {
        addType2Field(StringField.class, StringFieldPart.class, cm, fm);
    }

    /**
     * Get the required value from the map based on key, or raise an exception
     * if it's missing.
     *
     * @param map The map to read from
     * @param key The key to lookup with
     * @return The value
     * @throws MojoExecutionException Raised if there is no such key/value found
     */
    private String getOrFail(Map<String, String> map, String key) throws MojoExecutionException {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        throw new MojoExecutionException(format("Could not find configuration option '%s'", key));
    }

    /**
     * Get a class loader that will load classes compiled during the build
     *
     * @return The class loader
     * @throws MojoExecutionException
     */
    public ClassLoader getCompiledClassesLoader() throws MojoExecutionException, DependencyResolutionRequiredException {
        List<String> classpathElements = project.getCompileClasspathElements();
        List<URL> projectClasspathList = new ArrayList<>();
        for (String element : classpathElements) {
            try {
                projectClasspathList.add(new File(element).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(element + " is an invalid classpath element", e);
            }
        }
        return new URLClassLoader(projectClasspathList.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());

    }

    public Set<String> getPackages() {
        return packages;
    }

    private void openMetaModel() {
        impt(Table.class.getCanonicalName());
        emit("public final class Tables {");
        emit("");
        push();
    }

    private void closeMetaModel() {
        pop();
        emit("}");
    }

    private void push() {
        depth++;
    }

    private void pop() {
        depth--;
    }

    private void emit(String fmt, Object... args) {
        for (int i = 0; i < depth; ++i) {
            buf.append("    ");
        }
        buf.append(format(fmt, args));
        if (!fmt.endsWith("\n")) {
            buf.append("\n");
        }
    }

    private void impt(String name) {
        imports.add(name);
    }

    private void impt(Class clazz) {
        imports.add(clazz.getCanonicalName());
    }

    private void output() {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(outputDir + "/Tables.java"));
            for (String imp : imports) {
                out.printf("import %s;\n", imp);
            }
            out.println();
            out.print(buf.toString());
        } catch (IOException ex) {
            Logger.getLogger(GenerateModel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }

}
