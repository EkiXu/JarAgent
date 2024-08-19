package xyz.ieki.jaragent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PatchAnnotation {
    String value();
}
