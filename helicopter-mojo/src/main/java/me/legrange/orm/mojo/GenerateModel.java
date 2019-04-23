package me.legrange.orm.mojo;

import java.io.File;
import java.io.PrintStream;
import static java.lang.String.format;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import me.legrange.orm.BooleanField;
import me.legrange.orm.DateField;
import me.legrange.orm.Field;
import me.legrange.orm.NumberField;
import me.legrange.orm.OrmMetaDataException;
import me.legrange.orm.StringField;
import me.legrange.orm.Table;
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
            for (ClassModel pm : gen.getPojoModels()) {
                addClassModel(pm);
            }
            emit("");
            for (ClassModel cm : gen.getPojoModels()) {
                addClassField(cm);
            }
            closeMetaModel();
            output();
        } catch (OrmMetaDataException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private void addClassModel(ClassModel cm) throws OrmMetaDataException {
        emit("public static final class %sTable implements Table<%s> {", cm.getJavaName(), cm.getJavaName());
        emit("");
        push();
        StringJoiner fieldNames = new StringJoiner(",");
        for (FieldModel fm : cm.getFields()) {
            addFieldModel(cm, fm);
            fieldNames.add(fm.getJavaName());
        }
        // getFields();
        emit("");
        emit("public List<Field> getFields() {");
        push();
        emit(format("return Arrays.asList(%s);", fieldNames.toString()));
        pop();
        emit("}");
        pop();
        // getSqlTable();
        emit("");
        emit("public String getSqlTable() {");
        push();
        emit("return \"%s\";", cm.getTableName());
        pop();
        emit("}");
        // getObjectClass();
        emit("");
        emit("public Class<%s> getObjectClass() {", cm.getJavaName());
        push();
        emit("return %s.class;", cm.getJavaName());
        pop();
        emit("}");

        emit("");
        emit("}");
        emit("");
    }

    private void addClassField(ClassModel cm) {
        impt(cm.getClassName());
        emit("public static final %sTable %s = new %sTable();", cm.getJavaName(), cm.getJavaName().toUpperCase(), cm.getJavaName());
    }

    private void addFieldModel(ClassModel cm, FieldModel fm) throws OrmMetaDataException {
        switch (fm.getType()) {
            case BYTE:
            case SHORT:
            case INTEGER:
            case LONG:
            case FLOAT:
            case DOUBLE:
                addType3Field(cm, fm, NumberField.class);
                break;
            case BOOLEAN:
                addType2Field(cm, fm, BooleanField.class);
                break;
            case DATE:
                addType2Field(cm, fm, DateField.class);
                break;
            case STRING:
                addType2Field(cm, fm, StringField.class);
                break;
            case UNSUPPORTED:
                throw new OrmMetaDataException(format("Unsupported POJO field type for field on class %s", fm.getJavaName(), cm.getJavaName()));
        }

    }

    private void addType3Field(ClassModel cm, FieldModel fm, Class<? extends Field> fieldClass) {
        impt(fieldClass.getCanonicalName());
        emit("public final %s<%sTable, %s, %s> %s = new %s(%s.class, \"%s\", \"%s\");",
                fieldClass.getSimpleName(),
                cm.getJavaName(),
                cm.getJavaName(),
                fm.getType().javaType(),
                fm.getJavaName(),
                fieldClass.getSimpleName(),
                cm.getJavaName(),
                fm.getJavaName(),
                fm.getSqlName());

    }

    private void addType2Field(ClassModel cm, FieldModel fm, Class<? extends Field> fieldClass) {
        impt(fieldClass.getCanonicalName());
        emit("public final %s<%sTable, %s> %s = new %s(%s.class, \"%s\", \"%s\");",
                fieldClass.getSimpleName(),
                cm.getJavaName(), cm.getJavaName(), fm.getJavaName(),
                fieldClass.getSimpleName(),
                cm.getJavaName(), fm.getJavaName(), fm.getSqlName());
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
            buf.append("\t");
        }
        buf.append(format(fmt, args));
        if (!fmt.endsWith("\n")) {
            buf.append("\n");
        }
    }

    private void impt(String name) {
        imports.add(name);
    }

    private void output() {
        PrintStream out = System.out;
        for (String imp : imports) {
            out.printf("import %s;\n", imp);
        }
        out.println();
        out.print(buf.toString());
    }

}
