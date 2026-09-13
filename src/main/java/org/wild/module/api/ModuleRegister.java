package org.wild.module.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleRegister {
   String name();

   String description();

   ModuleCategory category();

   ModuleFlag[] flags() default {};
}
