package ru.wild.render;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import ru.wild.api.event.FrameRenderListener;

public final class ScreenRenderDiagnostics {
   private static final Logger instance = LogUtils.getLogger();
   private static final boolean data = Boolean.parseBoolean(System.getProperty("wild.debug.screenRender.enable", "false"));
   private static final long context = 2000000000L;
   private static volatile String config = "unknown";
   private static volatile boolean state;
   private static long cache;
   private static long output;

   private ScreenRenderDiagnostics() {
   }

   public static boolean handle() {
      return data;
   }

   public static void handle(Screen var0, Screen var1) {
      output = 0L;
      cache = 0L;
      compute();
      instance.info("[ScreenRender] screen={} kind={} from={} gpu={}", new Object[]{handle(var1), process(var1), handle(var0), config});
   }

   public static void handle(Object var0, String var1) {
      handle(var0, var1, null);
   }

   public static void handle(Object var0, String var1, String var2) {
      if (data) {
         output++;
         long var3 = System.nanoTime();
         boolean var5 = var3 - cache >= 2000000000L;
         if (data || var5) {
            cache = var3;
            ScreenRenderDiagnostics.ColorStop var6 = process();
            if (var2 != null && !var2.isBlank()) {
               instance.info(
                  "[ScreenRender] phase={} screen={} kind={} frame={} detail={} gl={}", new Object[]{var1, handle(var0), process(var0), output, var2, var6}
               );
            } else {
               instance.info("[ScreenRender] phase={} screen={} kind={} frame={} gl={}", new Object[]{var1, handle(var0), process(var0), output, var6});
            }
         }
      }
   }

   public static void handle(Object var0, String var1, boolean var2, String var3) {
      if (!var2 || data) {
         instance.info(
            "[ScreenRender] backdrop={} success={} screen={} kind={} detail={} gl={}",
            new Object[]{var1, var2, handle(var0), process(var0), var3 == null ? "" : var3, process()}
         );
      }
   }

   public static void handle(Object var0, String var1, boolean var2, String var3, Throwable var4) {
      if (!var2 || data) {
         if (var4 != null) {
            instance.warn(
               "[ScreenRender] postRender={} success={} screen={} kind={} detail={} gl={}",
               new Object[]{var1, false, handle(var0), process(var0), var3 == null ? "" : var3, process(), var4}
            );
         } else {
            instance.info(
               "[ScreenRender] postRender={} success={} screen={} kind={} detail={} gl={}",
               new Object[]{var1, var2, handle(var0), process(var0), var3 == null ? "" : var3, process()}
            );
         }
      }
   }

   public static void handle(String var0, Object var1, String var2, Throwable var3) {
      if (var3 != null) {
         instance.warn("[ScreenRender] failure={} screen={} kind={} reason={} gl={}", new Object[]{var0, handle(var1), process(var1), var2, process(), var3});
      } else {
         instance.warn("[ScreenRender] failure={} screen={} kind={} reason={} gl={}", new Object[]{var0, handle(var1), process(var1), var2, process()});
      }
   }

   public static ScreenRenderDiagnostics.ColorStop process() {
      try {
         int var0 = GL11.glGetInteger(36006);
         int var1 = GL11.glGetInteger(36010);
         int var2 = GL11.glGetInteger(35725);
         int[] var3 = new int[4];
         GL11.glGetIntegerv(2978, var3);
         int var4 = var0 == 0 ? 36053 : GL30.glCheckFramebufferStatus(36009);
         boolean var5 = GL11.glGetBoolean(3107);
         boolean var6 = GL11.glGetBoolean(3042);
         boolean var7 = GL11.glGetBoolean(2929);
         boolean var8 = GL11.glGetBoolean(3089);
         return new ScreenRenderDiagnostics.ColorStop(var0, var1, var2, var3, var4, var5, var6, var7, var8);
      } catch (Throwable var9) {
         return new ScreenRenderDiagnostics.ColorStop(-1, -1, -1, new int[4], -1, false, false, false, false);
      }
   }

   private static void compute() {
      if (!state) {
         synchronized (ScreenRenderDiagnostics.class) {
            if (!state) {
               try {
                  String var1 = GL11.glGetString(7936);
                  String var2 = GL11.glGetString(7937);
                  String var3 = GL11.glGetString(7938);
                  config = "vendor=" + handle(var1) + " renderer=" + handle(var2) + " version=" + handle(var3);
               } catch (Throwable var5) {
                  config = "unavailable";
               }

               state = true;
            }
         }
      }
   }

   private static String handle(Object var0) {
      return var0 == null ? "<none>" : var0.getClass().getSimpleName();
   }

   private static String process(Object var0) {
      if (var0 == null) {
         return "none";
      }

      if (var0 instanceof FrameRenderListener) {
         return "raw-overlay";
      }

      if (var0 instanceof Screen) {
         String var1 = var0.getClass().getName();
         if (var1.startsWith("org.wild.")) {
            return "wild-custom";
         } else {
            return var1.startsWith("net.minecraft.") ? "vanilla" : "external";
         }
      } else {
         return "external";
      }
   }

   private static String handle(String var0) {
      return var0 == null ? "?" : var0.replace('\n', ' ').trim();
   }

   public record ColorStop(
      int drawFbo, int readFbo, int program, int[] viewport, int drawFramebufferStatus, boolean colorMask, boolean blend, boolean depthTest, boolean scissor
   ) {
      @Override
      public String toString() {
         return "drawFbo="
            + this.drawFbo
            + " readFbo="
            + this.readFbo
            + " program="
            + this.program
            + " viewport="
            + this.viewport[0]
            + "x"
            + this.viewport[1]
            + "+"
            + this.viewport[2]
            + "x"
            + this.viewport[3]
            + " fbStatus=0x"
            + Integer.toHexString(this.drawFramebufferStatus)
            + " colorMask="
            + this.colorMask
            + " blend="
            + this.blend
            + " depth="
            + this.depthTest
            + " scissor="
            + this.scissor;
      }
   }
}
