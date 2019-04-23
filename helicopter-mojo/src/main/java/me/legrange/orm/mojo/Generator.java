package me.legrange.orm.mojo;

import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;

/**
 *
 * @author gideon
 */
public abstract class Generator {

    public enum PojoStrategy {
        annotated;
    }

    protected final GenerateModel generator;

    public Generator(GenerateModel generator) {
        this.generator = generator;
    }

    public abstract List<ClassModel> getPojoModels() throws MojoExecutionException;

}
