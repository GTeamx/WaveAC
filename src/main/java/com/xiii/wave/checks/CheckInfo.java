package com.xiii.wave.checks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckInfo {

    boolean checkEnabled() default true;

    String checkName() default "UNKNOWN";
    String checkDescription() default "No description.";
    CheckCategory checkCategory() default CheckCategory.COMBAT;
    CheckState checkState() default CheckState.UNSTABLE;

    boolean canBan() default false;
    boolean canKick() default false;
    boolean isSilent() default true;

    int punishVL() default 30;

    double maxBuffer() default 0;
    double removeBuffer() default 0;
    double addBuffer() default 0;
    double currentBuffer() default 0;

}
