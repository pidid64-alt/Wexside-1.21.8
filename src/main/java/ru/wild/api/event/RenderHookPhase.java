package ru.wild.api.event;

public final class RenderHookPhase {
   public static final int instance = 257;
   public static final int data = 258;
   public static final int context = 513;
   public static final int config = 514;
   public static final int state = 769;
   public static final int cache = 770;
   public static final int output = 1025;
   public static final int current = 1026;
   public static final int active = 1281;
   public static final int mode = 1282;

   private RenderHookPhase() {
   }

   public static String handle(int var0) {
      return switch (var0) {
         case 257 -> "CLIENT_TICK_HEAD";
         case 258 -> "CLIENT_TICK_TAIL";
         case 513 -> "GAME_RENDER_HEAD";
         case 514 -> "GAME_RENDER_TAIL";
         case 769 -> "SCREEN_RENDER_HEAD";
         case 770 -> "SCREEN_RENDER_TAIL";
         case 1025 -> "GUI_RENDER_BEGIN";
         case 1026 -> "GUI_RENDER_END";
         case 1281 -> "SHADER_DRAW_BEGIN";
         case 1282 -> "SHADER_DRAW_END";
         default -> "UNKNOWN_" + Integer.toHexString(var0);
      };
   }
}
