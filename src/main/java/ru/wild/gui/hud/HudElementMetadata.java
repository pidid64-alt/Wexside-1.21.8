package ru.wild.gui.hud;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HudElementMetadata {
   String handle();

   String process();
}
