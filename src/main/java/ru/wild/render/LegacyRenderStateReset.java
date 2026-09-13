package ru.wild.render;

import org.lwjgl.opengl.GL11;

public final class LegacyRenderStateReset {
   private LegacyRenderStateReset() {
   }

   public static void handle() {
      GL11.glEnable(2960);
      GL11.glClearStencil(0);
      GL11.glClear(1024);
      GL11.glStencilFunc(519, 1, 255);
      GL11.glStencilOp(7680, 7680, 7681);
      GL11.glColorMask(false, false, false, false);
      GL11.glDepthMask(false);
   }

   public static void handle(int var0) {
      GL11.glColorMask(true, true, true, true);
      GL11.glDepthMask(true);
      GL11.glStencilFunc(514, var0, 255);
      GL11.glStencilOp(7680, 7680, 7680);
   }

   public static void process() {
      GL11.glDisable(2960);
      GL11.glStencilFunc(519, 0, 255);
      GL11.glStencilOp(7680, 7680, 7680);
   }
}
