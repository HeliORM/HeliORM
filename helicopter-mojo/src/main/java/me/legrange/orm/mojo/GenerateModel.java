package me.legrange.orm.mojo;

import java.io.File;
import static java.lang.String.format;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
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

    private Map<String, Output> outputs;

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
            outputs = PackageOrganizer.organize(gen.getPojoModels().stream().map(pm -> pm.getObjectClass()).collect(Collectors.toList()));
            for (Table pm : gen.getPojoModels()) {
                addClassModel(pm);
            }
            for (Table cm : gen.getPojoModels()) {
                addClassField(cm);
            }
            for (Output out : outputs.values()) {
                out.output(outputDir);
            }
        } catch (OrmMetaDataException | GeneratorException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private void addClassModel(Table<?> cm) throws OrmMetaDataException, GeneratorException {
        Output out = getOutputFor(cm.getObjectClass());
        out.emit("public static final class %sTable implements Table<%s> {", getJavaName(cm), getJavaName(cm));
        out.emit("");
        out.push();
        StringJoiner fieldNames = new StringJoiner(",");
        for (Field fm : cm.getFields()) {
            addFieldModel(cm, fm);
            fieldNames.add(fm.getJavaName());
        }
        // getFields();
        out.impt(List.class);
        out.impt(Field.class);
        out.impt(Arrays.class);
        out.emit("");
        out.emit("@Override");
        out.emit("public List<Field> getFields() {");
        out.push();
        out.emit(format("return Arrays.asList(%s);", fieldNames.toString()));
        out.pop();
        out.emit("}");
        // getSqlTable();
        out.emit("");
        out.emit("@Override");
        out.emit("public String getSqlTable() {");
        out.push();
        out.emit("return \"%s\";", cm.getSqlTable());
        out.pop();
        out.emit("}");
        // getObjectClass();
        out.emit("");
        out.emit("@Override");
        out.emit("public Class<%s> getObjectClass() {", getJavaName(cm));
        out.push();
        out.emit("return %s.class;", getJavaName(cm));
        out.pop();
        out.emit("}");
        out.pop();

        out.emit("");
        out.emit("}");
        out.emit("");
    }

    private String getJavaName(Table table) {
        return table.getObjectClass().getSimpleName();
    }

    private void addClassField(Table cm) throws GeneratorException {
        Output out = getOutputFor(cm.getObjectClass());
        out.impt(cm.getObjectClass());
        out.emit("public static final %sTable %s = new %sTable();", getJavaName(cm), getJavaName(cm).toUpperCase(), getJavaName(cm));
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
        Output out = getOutputFor(cm.getObjectClass());
        out.impt(fieldClass);
        out.impt(partClass);
        out.emit("public final %s<%sTable, %s> %s = new %s(\"%s\", \"%s\");",
                fieldClass.getSimpleName(),
                getJavaName(cm), getJavaName(cm), fm.getJavaName(),
                partClass.getSimpleName(),
                fm.getJavaName(), fm.getSqlName());
    }

    private void addType3Field(Class<? extends Field> fieldClass, Class<? extends FieldPart> partClass, Table cm, Field fm) throws GeneratorException {
        Output out = getOutputFor(cm.getObjectClass());
        out.impt(fieldClass);
        out.impt(partClass);
        out.impt(fm.getJavaType());
        out.emit("public final %s<%sTable, %s, %s> %s = new %s(%s.class, \"%s\", \"%s\");",
                fieldClass.getSimpleName(),
                getJavaName(cm), getJavaName(cm), fm.getJavaType().getSimpleName(),
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
        addType3Field(EnumField.class, EnumFieldPart.class, cm, fm);
    }

    private void addDateField(Table cm, Field fm) throws GeneratorException {
        addType2Field(DateField.class, DateFieldPart.class, cm, fm);
    }

    private void addStringField(Table cm, Field fm) throws GeneratorException {
        addType2Field(StringField.class, StringFieldPart.class, cm, fm);
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

    private Output getOutputFor(Class clazz) throws GeneratorException {
        Output out = outputs.get(clazz.getCanonicalName());
        if (out == null) {
            throw new GeneratorException(format("Cannot found output table for class '%s'. BUG!", clazz.getCanonicalName()));
        }
        return out;
    }

}
