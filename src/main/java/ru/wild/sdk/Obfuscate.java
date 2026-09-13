package ru.wild.sdk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Obfuscate {
   Obfuscate.RuntimeMode preset() default Obfuscate.RuntimeMode.DEFAULT;

   boolean const_flow() default false;

   Obfuscate.Mode const_flow_type() default Obfuscate.Mode.MEDIUM;

   boolean control_flow() default false;

   Obfuscate.PrimaryMode control_flow_type() default Obfuscate.PrimaryMode.MIXED;

   boolean string_encrypt() default false;

   Obfuscate.CachedMode string_encrypt_type() default Obfuscate.CachedMode.XOR;

   boolean number_encrypt() default false;

   Obfuscate.FallbackMode number_encrypt_type() default Obfuscate.FallbackMode.LIGHT;

   boolean invoke_dynamic() default false;

   boolean hide_reflection() default false;

   boolean dead_code() default false;

   Obfuscate.SecondaryMode dead_code_type() default Obfuscate.SecondaryMode.LIGHT;

   boolean junk_code() default false;

   boolean strip_debug() default true;

   boolean watermark() default false;

   String watermark_text() default "";

   enum CachedMode {
      XOR,
      AES,
      RC4,
      SHACAL2;
   }

   enum FallbackMode {
      LIGHT,
      HEAVY;
   }

   enum Mode {
      LIGHT,
      MEDIUM,
      HEAVY;
   }

   enum PrimaryMode {
      SPLIT,
      FLAT,
      MIXED;
   }

   enum RuntimeMode {
      DEFAULT,
      LIGHT,
      MEDIUM,
      HEAVY,
      EXTREME;
   }

   enum SecondaryMode {
      LIGHT,
      MEDIUM,
      HEAVY;
   }
}
