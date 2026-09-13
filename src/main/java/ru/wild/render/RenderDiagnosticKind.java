package ru.wild.render;

public final class RenderDiagnosticKind {
   public static final int instance = 4097;
   public static final int data = 4098;
   public static final int context = 8193;
   public static final int config = 12289;
   public static final int state = 16385;
   public static final int cache = 20481;
   public static final int output = 24577;

   private RenderDiagnosticKind() {
   }

   public static String handle(int var0) {
      return switch (var0) {
         case 4097 -> "GL_ERROR";
         case 4098 -> "GL_STATE_LEAK";
         case 8193 -> "PHASE_ORDER";
         case 12289 -> "MATRIX_INVALID";
         case 16385 -> "SNAPSHOT_FAILURE";
         case 20481 -> "MANUAL_SNAPSHOT";
         case 24577 -> "SHADER_EXCEPTION";
         default -> "0x" + Integer.toHexString(var0);
      };
   }
}
