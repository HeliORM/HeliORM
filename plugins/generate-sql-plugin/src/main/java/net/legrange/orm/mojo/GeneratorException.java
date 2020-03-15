package net.legrange.orm.mojo;

import org.apache.maven.plugin.MojoExecutionException;

public class GeneratorException extends MojoExecutionException {

    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneratorException(String message) {
        super(message);
    }
}
