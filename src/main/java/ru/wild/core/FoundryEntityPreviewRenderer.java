package ru.wild.core;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.DiffuseShaderRenderer;
import ru.wild.render.shader.ShaderBuildResult;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderPreviewSession;
import ru.wild.render.texture.OffscreenRenderTarget;
import ru.wild.util.render.RoundedRectRenderer;

public final class FoundryEntityPreviewRenderer {
   private static final OffscreenRenderTarget instance = new OffscreenRenderTarget();
   private static final OffscreenRenderTarget data = new OffscreenRenderTarget();
   private static final OffscreenRenderTarget context = new OffscreenRenderTarget();
   private static final int config = -15657957;
   private static final int state = -14670802;
   private static final String cache = "__foundry_preview_live";
   private static String output = "__foundry_preview_live";
   private static String current = "";
   private static final float active = 0.78F;
   private static final float mode = -6.0F;
   private static final float selection = 22.0F;
   private static Boolean enabled;
   private static Method renderer;
   private static Method handler;

   private FoundryEntityPreviewRenderer() {
   }

   public static void handle(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      LivePreviewRenderer var2,
      ShaderPreviewSession var3,
      ShaderGraph var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      float var11,
      float var12,
      float var13
   ) {
      GuiMetrics var14 = var1.update();
      ThemeColors var15 = var1.apply();
      LivePreviewRenderer var16 = var2 == null ? LivePreviewRenderer.PREVIEW_ONLY : var2;
      LivePreviewRenderer var17 = var16.resolve();
      var0.compute();
      var0.handle(var5, var6, var7, var8, var14.handle(10.0F), var14.handle(10.0F), var14.handle(10.0F), var14.handle(10.0F));

      try {
         if (var16 == LivePreviewRenderer.TRAILS) {
            compute(var0, var1, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         } else if (var16 == LivePreviewRenderer.SKY) {
            process(var0, var3, var4, var15, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         } else if (var16 == LivePreviewRenderer.NAMETAG) {
            process(var0, var1, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         } else if (var16 == LivePreviewRenderer.CHAMS) {
            handle(var0, var1, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, true);
         } else if (var16 == LivePreviewRenderer.HEALTH_BAR) {
            resolve(var0, var1, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         } else {
            switch (var17) {
               case HUD:
                  handle(var0, var1, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
                  break;
               case ESP:
                  handle(var0, var1, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, false);
                  break;
               case BACKGROUND:
                  handle(var0, var3, var4, var15, var5, var6, var7, var8, var9, var10, var11, var12, var13);
                  break;
               default:
                  compute(var0, var3, var4, var15, var5, var6, var7, var8, var9, var10, var11, var12, var13);
            }
         }
      } finally {
         var0.compute();
         var0.apply();
      }

      var0.handle(var5, var6, var7, var8, var14.handle(10.0F), ThemeColors.handle(var15.save(), 96), 0.7F);
   }

   public static void handle(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      String var2,
      LivePreviewRenderer var3,
      ShaderGraph var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      float var11,
      float var12,
      float var13
   ) {
      String var14 = output;
      String var15 = current;
      String var16 = PresetManager.onTick(var2);
      output = var16.isBlank() ? "__foundry_preview_live" : "__foundry_slot_preview_" + var16;
      current = var16;

      try {
         handle(var0, var1, var3, null, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      } finally {
         output = var14;
         current = var15;
      }
   }

   private static void handle(
      RoundedRectRenderer var0,
      ShaderPreviewSession var1,
      ShaderGraph var2,
      ThemeColors var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12
   ) {
      var0.handle(var4, var5, var6, var7, 0.0F, -16645366);
      handle(var0, var1, var2, var4, var5, var6, var7, var8, var9, var10, var11, var3, var12);
   }

   private static void process(
      RoundedRectRenderer var0,
      ShaderPreviewSession var1,
      ShaderGraph var2,
      ThemeColors var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12
   ) {
      var0.process(var4, var5, var6, var7, 0.0F, -16381929, -15460309);
      var0.handle(var4, var5 + var7 * 0.58F, var6, var7 * 0.42F, 0.0F, ThemeColors.handle(var3.submit(), 54), ThemeColors.handle(var3.save(), 28));
      handle(var0, var1, var2, var4, var5, var6, var7, var8, var9, var10, var11, var3, var12);
   }

   private static void compute(
      RoundedRectRenderer var0,
      ShaderPreviewSession var1,
      ShaderGraph var2,
      ThemeColors var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12
   ) {
      handle(var0, var3, var4, var5, var6, var7, var12);
      handle(var0, var1, var2, var4, var5, var6, var7, var8, var9, var10, var11, var3, var12);
   }

   private static void handle(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      ShaderPreviewSession var2,
      ShaderGraph var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12
   ) {
      ThemeColors var13 = var1.apply();
      handle(var0, var13, var4, var5, var6, var7, var12);
      ShaderBuildResult var14 = handle(var2, var3);
      float var15 = Math.max(18.0F, Math.min(var6 * 0.84F, 220.0F));
      float var16 = Math.max(12.0F, Math.min(var7 * 0.58F, var15 * 0.42F));
      float var17 = var4 + (var6 - var15) * 0.5F;
      float var18 = var5 + var7 * 0.26F;
      float var19 = Math.min(var15, var16) * 0.18F;
      handle(var0, var2, var3, var14, var4, var5, var6, var7, var17, var18, var15, var16, var19, var10, var11, var13, var12);
   }

   private static void process(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      ShaderPreviewSession var2,
      ShaderGraph var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12
   ) {
      ThemeColors var13 = var1.apply();
      handle(var0, var13, var4, var5, var6, var7, var12);
      float var14 = Math.min(var6 * 0.7F, 230.0F);
      float var15 = Math.min(var7 * 0.24F, 54.0F);
      float var16 = var4 + (var6 - var14) * 0.5F;
      float var17 = var5 + var7 * 0.34F;
      float var18 = Math.min(var14, var15) * 0.22F;
      ShaderBuildResult var19 = handle(var2, var3);
      handle(var0, var2, var3, var19, var4, var5, var6, var7, var16, var17, var14, var15, var18, var10, var11, var13, var12);
      MinecraftClient var20 = MinecraftClient.getInstance();
      String var21 = var20 != null && var20.player != null ? var20.player.getName().getString() : "Player";
      ModuleStateHelper.handle(var0, var1.update(), FontRegistry.config, var16, var17 + var15 * 0.2F, var15 * 0.42F, 10.0F, var21, var13.load());
      ModuleStateHelper.handle(
         var0, var1.update(), FontRegistry.instance, var16, var17 + var15 * 0.52F, var15 * 0.34F, 8.0F, "20.0", ThemeColors.handle(var13.submit(), 220)
      );
   }

   private static void compute(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      ShaderPreviewSession var2,
      ShaderGraph var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12
   ) {
      GuiMetrics var13 = var1.update();
      ThemeColors var14 = var1.apply();
      MinecraftClient var15 = MinecraftClient.getInstance();
      ClientPlayerEntity var16 = var15 != null ? var15.player : null;
      handle(var0, var14, var4, var5, var6, var7, var12);
      float var17 = var5 + var7 * 0.86F;
      var0.handle(var4, var17, var6, var7 - (var17 - var5), 0.0F, -15657957);
      var0.handle(var4, var17, var6, 1.0F, 0.0F, -14670802);
      ShaderBuildResult var18 = handle(var2, var3);
      float var19 = Math.max(var13.handle(10.0F), var7 * 0.085F);
      float var20 = var6 * 0.55F;
      float var21 = var4 + var6 * 0.1F;
      float var22 = var17 - var7 * 0.3F - var19 * 0.5F;
      float var23 = var19 * 2.1F;
      float var24 = var22 - (var23 - var19) * 0.5F;
      var0.handle(var21, var24, var20, var23, var23 * 0.5F, var23 * 0.5F, var23 * 0.5F, var23 * 0.5F);

      try {
         handle(var0, var2, var3, var18, var21, var24, var20, var23, var8, var9, var10, var11, var14, var12 * 0.34F);
      } finally {
         var0.compute();
         var0.apply();
      }

      var0.handle(var21, var22, var20, var19, var19 * 0.5F, var19 * 0.5F, var19 * 0.5F, var19 * 0.5F);

      try {
         handle(var0, var2, var3, var18, var21, var22, var20, var19, var8, var9, var10, var11, var14, var12);
      } finally {
         var0.compute();
         var0.apply();
      }

      if (var16 != null) {
         float var25 = Math.max(24.0F, Math.min(var7 * 0.58F, 105.0F));
         EntityGuiElementRenderState var26 = handle(var15, var16, var4 + var6 * 0.66F, var17, var25);
         handle(var15, var26);
      } else {
         float var35 = Math.min(var6 * 0.18F, 54.0F);
         float var36 = Math.min(var7 * 0.48F, 104.0F);
         float var27 = var4 + var6 * 0.66F - var35 * 0.5F;
         float var28 = var17 - var36;
         var0.handle(var27, var28, var35, var36, var35 * 0.22F, ThemeColors.handle(10, 12, 18, 230));
         var0.handle(var27, var28, var35, var36, var35 * 0.22F, ThemeColors.handle(var14.save(), 140), 0.7F);
      }
   }

   private static void handle(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      ShaderPreviewSession var2,
      ShaderGraph var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12,
      boolean var13
   ) {
      ThemeColors var14 = var1.apply();
      MinecraftClient var15 = MinecraftClient.getInstance();
      ClientPlayerEntity var16 = var15 != null ? var15.player : null;
      handle(var0, var14, var4, var5, var6, var7, var12);
      float var17 = var5 + var7 * 0.88F;
      var0.handle(var4, var17, var6, var7 - (var17 - var5), 0.0F, -15657957);
      var0.handle(var4, var17, var6, 1.0F, 0.0F, -14670802);
      if (var16 != null) {
         float var18 = (var17 - var5) * 0.92F;
         float var19 = Math.max(24.0F, Math.min(var18, var7 * 0.78F) * 0.78F);
         float var20 = var4 + var6 * 0.5F;
         float var21 = var17;
         EntityGuiElementRenderState var22 = handle(var15, var16, var20, var21, var19);
         int var23 = handle(var15, var22, var8, var9);
         handle(var15, var22);
         if (var23 > 0) {
            ShaderBuildResult var24 = handle(var2, var3);
            boolean var25 = DiffuseShaderRenderer.handle(
               handle(), var24, var23, var4, var5, var6, var7, var8, var9, var10, var11, var14, var13 ? var12 : var12 * 0.96F
            );
            process();
            var0.compute();
            if (var25) {
               return;
            }
         }
      }

      handle(var0, var1, var2, var3, var4, var5, var6, var7, var17, var8, var9, var10, var11, var12, var13);
   }

   private static void handle(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      ShaderPreviewSession var2,
      ShaderGraph var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      float var11,
      float var12,
      float var13,
      boolean var14
   ) {
      GuiMetrics var15 = var1.update();
      ThemeColors var16 = var1.apply();
      ShaderBuildResult var17 = handle(var2, var3);
      float var18 = var4 + var6 * 0.5F;
      float var19 = Math.min(var7 * 0.52F, var15.handle(130.0F));
      float var20 = Math.min(var6 * 0.2F, var15.handle(56.0F));
      float var21 = var20 * 0.72F;
      float var22 = var18 - var20 * 0.5F;
      float var23 = var8 - var19;
      float var24 = var18 - var21 * 0.5F;
      float var25 = var23 - var21 * 0.62F;
      float var26 = var20 * 0.3F;
      float var27 = var19 * 0.62F;
      float var28 = var20 * 0.34F;
      float var29 = var19 * 0.42F;
      var0.handle(
         var22 - var15.handle(6.0F),
         var25 - var15.handle(6.0F),
         var20 + var15.handle(12.0F),
         var8 - var25 + var15.handle(6.0F),
         var20 * 0.28F,
         var15.handle(22.0F),
         var15.handle(2.0F),
         ThemeColors.handle(var16.save(), Math.round(64.0F * var13))
      );
      handle(var0, var2, var3, var17, var24, var25, var21, var21, var21 * 0.42F, var9, var10, var11, var12, var16, var13);
      handle(var0, var2, var3, var17, var22 - var26 * 0.72F, var23 + var19 * 0.06F, var26, var27, var26 * 0.5F, var9, var10, var11, var12, var16, var13);
      handle(var0, var2, var3, var17, var22 + var20 - var26 * 0.28F, var23 + var19 * 0.06F, var26, var27, var26 * 0.5F, var9, var10, var11, var12, var16, var13);
      handle(var0, var2, var3, var17, var22 + var20 * 0.1F, var23 + var19 * 0.58F, var28, var29, var28 * 0.4F, var9, var10, var11, var12, var16, var13);
      handle(
         var0,
         var2,
         var3,
         var17,
         var22 + var20 - var28 - var20 * 0.1F,
         var23 + var19 * 0.58F,
         var28,
         var29,
         var28 * 0.4F,
         var9,
         var10,
         var11,
         var12,
         var16,
         var13
      );
      handle(var0, var2, var3, var17, var22, var23, var20, var19 * 0.66F, var20 * 0.3F, var9, var10, var11, var12, var16, var13);
      if (!var14) {
         var0.handle(var24, var25, var21, var21, var21 * 0.42F, ThemeColors.handle(var16.save(), Math.round(150.0F * var13)), 0.7F);
         var0.handle(var22, var23, var20, var19 * 0.66F, var20 * 0.3F, ThemeColors.handle(var16.save(), Math.round(150.0F * var13)), 0.7F);
      }
   }
   private static void handle(
      RoundedRectRenderer var0,
      ShaderPreviewSession var1,
      ShaderGraph var2,
      ShaderBuildResult var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      float var11,
      float var12,
      ThemeColors var13,
      float var14
   ) {
      if (!(var6 <= 1.0F) && !(var7 <= 1.0F)) {
         var0.compute();
         var0.handle(var4, var5, var6, var7, var8, var8, var8, var8);
         boolean var17 = false /* VF: Semaphore variable */;

         try {
            var17 = true;
            handle(var0, var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, var13, var14);
            var17 = false;
         } finally {
            if (var17) {
               var0.compute();
               var0.apply();
            }
         }

         var0.compute();
         var0.apply();
      }
   }

   private static void resolve(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      ShaderPreviewSession var2,
      ShaderGraph var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12
   ) {
      GuiMetrics var13 = var1.update();
      ThemeColors var14 = var1.apply();
      handle(var0, var14, var4, var5, var6, var7, var12);
      float var15 = Math.min(var6 * 0.82F, var13.handle(280.0F));
      float var16 = Math.max(var13.handle(14.0F), Math.min(var7 * 0.16F, var13.handle(26.0F)));
      float var17 = var4 + (var6 - var15) * 0.5F;
      float var18 = var5 + var7 * 0.44F;
      float var19 = var16 * 0.5F;
      float var20 = 0.68F;
      var0.handle(var17, var18, var15, var16, var19, ThemeColors.handle(10, 12, 18, Math.round(220.0F * var12)));
      var0.handle(var17, var18, var15, var16, var19, ThemeColors.handle(var14.load(), Math.round(40.0F * var12)), 0.7F);
      float var21 = Math.max(var16, var15 * var20);
      var0.compute();
      var0.handle(var17, var18, var21, var16, var19, var19, var19, var19);

      try {
         handle(var0, var2, var3, var17, var18, var15, var16, var8, var9, var10, var11, var14, var12);
      } finally {
         var0.compute();
         var0.apply();
      }

      var0.handle(
         var17 + var21 - var13.handle(1.5F),
         var18 + var13.handle(1.5F),
         var13.handle(1.5F),
         var16 - var13.handle(3.0F),
         0.0F,
         ThemeColors.handle(var14.load(), Math.round(150.0F * var12))
      );
      float var22 = var18 + var16 + var13.handle(8.0F);
      float var23 = Math.max(var13.handle(6.0F), var16 * 0.42F);
      var0.handle(var17, var22, var15, var23, var23 * 0.5F, ThemeColors.handle(10, 12, 18, Math.round(200.0F * var12)));
      var0.handle(var17, var22, var15 * 0.5F, var23, var23 * 0.5F, ThemeColors.handle(var14.save(), Math.round(150.0F * var12)));
      ModuleStateHelper.handle(
         var0,
         var13,
         FontRegistry.instance,
         var17,
         var18 - var13.handle(16.0F),
         var13.handle(12.0F),
         8.0F,
         "20.0 / 20.0",
         ThemeColors.handle(var14.load(), Math.round(180.0F * var12))
      );
   }

   private static EntityGuiElementRenderState handle(MinecraftClient var0, ClientPlayerEntity var1, float var2, float var3, float var4) {
      if (var0 != null && var1 != null) {
         int var5 = Math.max(48, Math.round(var4 * 2.24F));
         int var6 = Math.max(36, Math.round(var5 * 0.68F));
         int var7 = Math.round(var2 - var6 * 0.5F);
         int var8 = var7 + var6;
         int var9 = Math.round(var3);
         int var10 = var9 - var5;
         int var11 = Math.max(18, Math.round(var5 * 0.43F));
         float var12 = (var7 + var8) * 0.5F;
         float var13 = (var10 + var9) * 0.5F;
         float var14 = var12 - (float)Math.tan(1.1F) * 40.0F;
         float var15 = var13 - (float)Math.tan(0.3F) * 40.0F;
         GuiRenderState var16 = new GuiRenderState();
         DrawContext var17 = new DrawContext(var0, var16);
         InventoryScreen.drawEntity(var17, var7, var10, var8, var9, var11, 0.0625F, var14, var15, var1);
         EntityGuiElementRenderState[] var18 = new EntityGuiElementRenderState[1];
         var16.forEachSpecialElement(var1x -> {
            if (var1x instanceof EntityGuiElementRenderState var2x) {
               var18[0] = var2x;
            }
         });
         return var18[0];
      } else {
         return null;
      }
   }

   private static int handle(MinecraftClient var0, EntityGuiElementRenderState var1, int var2, int var3) {
      if (var1 == null) {
         return 0;
      }

      int var4 = Math.max(1, var2);
      int var5 = Math.max(1, var3);
      data.handle(var4, var5);
      if (!data.apply()) {
         return 0;
      }

      OpenGlStateSnapshot.NetworkState var6 = OpenGlStateSnapshot.handle();

      try {
         data.handle();
         GL11.glViewport(0, 0, var4, var5);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(16640);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         GL11.glDepthMask(false);
         GlStateManager._enableBlend();
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         GL11.glDisable(36281);
         process(var0, var1);
      } catch (Throwable var8) {
         GL30.glBindFramebuffer(36160, 0);
         OpenGlStateSnapshot.compute(var6);
         process();
         return 0;
      }

      GL30.glBindFramebuffer(36160, 0);
      GL20.glUseProgram(0);
      OpenGlStateSnapshot.compute(var6);
      process();
      return data.compute();
   }

   private static void handle(MinecraftClient var0, EntityGuiElementRenderState var1) {
      if (var1 != null) {
         OpenGlStateSnapshot.NetworkState var2 = OpenGlStateSnapshot.handle();

         try {
            GlStateManager._enableBlend();
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            process(var0, var1);
         } catch (Throwable var7) {
         } finally {
            GL20.glUseProgram(0);
            OpenGlStateSnapshot.compute(var2);
            process();
         }
      }
   }
   private static void process(MinecraftClient var0, EntityGuiElementRenderState var1) {
      if (var0 != null && var1 != null) {
         EntityRenderDispatcher var2 = var0.getEntityRenderDispatcher();
         if (var2 != null) {
            Immediate var3 = var0.getBufferBuilders().getEntityVertexConsumers();
            if (var3 != null) {
               MatrixStack var4 = new MatrixStack();
               var4.push();
               var4.translate((var1.x1() + var1.x2()) * 0.5F, (var1.y1() + var1.y2()) * 0.5F, 1000.0F);
               var4.scale(var1.scale(), var1.scale(), -var1.scale());
               Vector3f var5 = var1.translation();
               var4.translate(var5.x, var5.y, var5.z);
               var4.multiply(var1.rotation());
               Quaternionf var6 = var1.overrideCameraAngle();
               boolean var7 = false;
               if (var6 != null) {
                  var2.setRotation(var6.conjugate(new Quaternionf()).rotateY((float) Math.PI));
                  var7 = true;
               }

               var2.setRenderShadows(false);
               int var8 = LightmapTextureManager.pack(15, 15);
               boolean var13 = false /* VF: Semaphore variable */;

               label88: {
                  label87: {
                     try {
                        var13 = true;
                        var2.render(var1.renderState(), 0.0, 0.0, 0.0, var4, var3, var8);
                        var3.draw();
                        var13 = false;
                        break label87;
                     } catch (Throwable var14) {
                        var13 = false;
                     } finally {
                        if (var13) {
                           var2.setRenderShadows(true);
                           if (var7 && var0.gameRenderer != null && var0.gameRenderer.getCamera() != null) {
                              var2.setRotation(var0.gameRenderer.getCamera().getRotation());
                           }
                        }
                     }

                     var2.setRenderShadows(true);
                     if (var7 && var0.gameRenderer != null && var0.gameRenderer.getCamera() != null) {
                        var2.setRotation(var0.gameRenderer.getCamera().getRotation());
                     }
                     break label88;
                  }

                  var2.setRenderShadows(true);
                  if (var7 && var0.gameRenderer != null && var0.gameRenderer.getCamera() != null) {
                     var2.setRotation(var0.gameRenderer.getCamera().getRotation());
                  }
               }

               var4.pop();
            }
         }
      }
   }
   private static void handle(
      RoundedRectRenderer var0,
      ShaderPreviewSession var1,
      ShaderGraph var2,
      ShaderBuildResult var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      ThemeColors var15,
      float var16
   ) {
      var0.compute();
      int var17 = Math.max(1, (int)Math.ceil(var6));
      int var18 = Math.max(1, (int)Math.ceil(var7));
      instance.handle(var17, var18);
      if (!instance.apply()) {
         handle(var0, var1, var2, var3, var8, var9, var10, var11, Math.max(1, Math.round(var10)), Math.max(1, Math.round(var11)), var13, var14, var15, var16);
      } else {
         OpenGlStateSnapshot.NetworkState var19 = OpenGlStateSnapshot.handle();
         float[] var20 = new float[4];
         GL11.glGetFloatv(3106, var20);
         boolean var26 = false /* VF: Semaphore variable */;

         try {
            var26 = true;
            instance.handle();
            GL11.glViewport(0, 0, var17, var18);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDepthMask(false);
            GlStateManager._enableBlend();
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            GL11.glDisable(36281);
            GL11.glEnable(3089);
            GL11.glScissor(0, 0, var17, var18);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16384);
            float var21 = var8 - var4;
            float var22 = var9 - var5;
            float var23 = Math.max(4.0F, Math.min(28.0F, Math.min(var10, var11) * 0.44F));
            if (var3 != null) {
               DiffuseShaderRenderer.handle(
                  handle(),
                  var3,
                  var21 - var23,
                  var22 - var23,
                  var10 + var23 * 2.0F,
                  var11 + var23 * 2.0F,
                  var21,
                  var22,
                  var10,
                  var11,
                  var12,
                  var17,
                  var18,
                  var13 - var4,
                  var14 - var5,
                  var15,
                  var16
               );
               var26 = false;
            } else if (var1 != null) {
               var1.handle(var2, var21, var22, var10, var11, var17, var18, var13 - var4, var14 - var5, var15, var16);
               var26 = false;
            } else {
               var26 = false;
            }
         } finally {
            if (var26) {
               GL11.glClearColor(var20[0], var20[1], var20[2], var20[3]);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var19);
               process();
               GlStateManager._enableBlend();
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
            }
         }

         GL11.glClearColor(var20[0], var20[1], var20[2], var20[3]);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var19);
         process();
         GlStateManager._enableBlend();
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         var0.process(instance.compute(), var4, var5, var6, var7);
         var0.compute();
      }
   }

   private static void handle(RoundedRectRenderer var0, ThemeColors var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var7 = var1.unload();
      int var8 = Math.round(255.0F * Math.max(0.0F, Math.min(1.0F, var6)));
      int var9 = var7 ? ThemeColors.handle(233, 236, 243, var8) : ThemeColors.handle(26, 28, 37, var8);
      int var10 = var7 ? ThemeColors.handle(212, 216, 227, var8) : ThemeColors.handle(12, 13, 19, var8);
      var0.process(var2, var3, var4, var5, 0.0F, var9, var10);
      var0.process(var2, var3, var4, var5 * 0.6F, 0.0F, ThemeColors.handle(var1.save(), Math.round(16.0F * var6)), ThemeColors.handle(var1.save(), 0));
      float var11 = Math.min(var4, var5) * 0.34F;
      int var12 = ThemeColors.handle(0, 0, 0, Math.round((var7 ? 26.0F : 60.0F) * var6));
      var0.process(var2, var3, var4, var11, 0.0F, var12, ThemeColors.handle(0, 0, 0, 0));
      var0.process(var2, var3 + var5 - var11, var4, var11, 0.0F, ThemeColors.handle(0, 0, 0, 0), var12);
      var0.handle(var2, var3, var11, var5, 0.0F, var12, ThemeColors.handle(0, 0, 0, 0));
      var0.handle(var2 + var4 - var11, var3, var11, var5, 0.0F, ThemeColors.handle(0, 0, 0, 0), var12);
   }

   private static void handle(
      RoundedRectRenderer var0,
      ShaderPreviewSession var1,
      ShaderGraph var2,
      float var3,
      float var4,
      float var5,
      float var6,
      int var7,
      int var8,
      float var9,
      float var10,
      ThemeColors var11,
      float var12
   ) {
      handle(var0, var1, var2, handle(var1, var2), var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }
   private static void handle(
      RoundedRectRenderer var0,
      ShaderPreviewSession var1,
      ShaderGraph var2,
      ShaderBuildResult var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      ThemeColors var12,
      float var13
   ) {
      var0.compute();
      int var14 = Math.max(1, (int)Math.ceil(var6));
      int var15 = Math.max(1, (int)Math.ceil(var7));
      instance.handle(var14, var15);
      if (!instance.apply()) {
         if (var3 != null) {
            DiffuseShaderRenderer.handle(handle(), var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
            process();
         } else if (var1 != null) {
            var1.handle(var2, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         }

         var0.compute();
      } else {
         OpenGlStateSnapshot.NetworkState var16 = OpenGlStateSnapshot.handle();
         float[] var17 = new float[4];
         GL11.glGetFloatv(3106, var17);
         boolean var20 = false /* VF: Semaphore variable */;

         try {
            var20 = true;
            instance.handle();
            GL11.glViewport(0, 0, var14, var15);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDepthMask(false);
            GlStateManager._enableBlend();
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            GL11.glDisable(36281);
            GL11.glEnable(3089);
            GL11.glScissor(0, 0, var14, var15);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16384);
            if (var3 != null) {
               DiffuseShaderRenderer.handle(handle(), var3, 0.0F, 0.0F, var14, var15, var14, var15, var10 - var4, var11 - var5, var12, var13);
               var20 = false;
            } else if (var1 != null) {
               var1.handle(var2, 0.0F, 0.0F, var14, var15, var14, var15, var10 - var4, var11 - var5, var12, var13);
               var20 = false;
            } else {
               var20 = false;
            }
         } finally {
            if (var20) {
               GL11.glClearColor(var17[0], var17[1], var17[2], var17[3]);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var16);
               process();
               GlStateManager._enableBlend();
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
            }
         }

         GL11.glClearColor(var17[0], var17[1], var17[2], var17[3]);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var16);
         process();
         GlStateManager._enableBlend();
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         var0.process(instance.compute(), var4, var5, var6, var7);
         var0.compute();
      }
   }
   public static void handle(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      ShaderBuildResult var2,
      String var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12
   ) {
      ThemeColors var13 = var1.apply();
      GuiMetrics var14 = var1.update();
      float var15 = var14.handle(6.0F);
      var0.compute();
      var0.handle(var4, var5, var6, var7, var15, var15, var15, var15);

      try {
         handle(var0, var13, var4, var5, var6, var7, var12);
         if (var2 != null && var2.ok() && var3 != null && !var3.isBlank() && var6 > 2.0F && var7 > 2.0F) {
            int var16 = Math.max(1, (int)Math.ceil(var6));
            int var17 = Math.max(1, (int)Math.ceil(var7));
            context.handle(var16, var17);
            if (context.apply()) {
               var0.compute();
               OpenGlStateSnapshot.NetworkState var18 = OpenGlStateSnapshot.handle();
               float[] var19 = new float[4];
               GL11.glGetFloatv(3106, var19);
               boolean var26 = false /* VF: Semaphore variable */;

               try {
                  var26 = true;
                  context.handle();
                  GL11.glViewport(0, 0, var16, var17);
                  GL11.glDisable(2929);
                  GL11.glDisable(2884);
                  GL11.glDepthMask(false);
                  GlStateManager._enableBlend();
                  GL11.glEnable(3042);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  GL11.glDisable(36281);
                  GL11.glEnable(3089);
                  GL11.glScissor(0, 0, var16, var17);
                  GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                  GL11.glClear(16384);
                  DiffuseShaderRenderer.handle(var3, var2, 0.0F, 0.0F, var16, var17, var16, var17, var10 - var4, var11 - var5, var13, 1.0F);
                  var26 = false;
               } finally {
                  if (var26) {
                     GL11.glClearColor(var19[0], var19[1], var19[2], var19[3]);
                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(var18);
                     process();
                     GlStateManager._enableBlend();
                     GL11.glEnable(3042);
                     GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  }
               }

               GL11.glClearColor(var19[0], var19[1], var19[2], var19[3]);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var18);
               process();
               GlStateManager._enableBlend();
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
               var0.process(context.compute(), var4, var5, var6, var7);
               var0.compute();
            }
         }
      } finally {
         var0.compute();
         var0.apply();
      }

      var0.handle(var4, var5, var6, var7, var15, ThemeColors.handle(var13.save(), Math.round(70.0F * var12)), 0.6F);
   }

   private static ShaderBuildResult handle(ShaderPreviewSession var0, ShaderGraph var1) {
      if (var0 != null) {
         return var0.process(var1);
      }

      if (var1 == null) {
         return null;
      }

      if (current != null && !current.isBlank()) {
         ShaderBuildResult var2 = PresetManager.handle().process(current);
         if (var2 != null) {
            return var2;
         }
      }

      String var4 = var1.handle() == null ? "" : var1.handle().process();
      if (!var4.isBlank()) {
         ShaderBuildResult var3 = PresetManager.handle().process(var4);
         if (var3 != null) {
            return var3;
         }
      }

      return null;
   }

   private static String handle() {
      return output != null && !output.isBlank() ? output : "__foundry_preview_live";
   }

   private static void process() {
      GL20.glUseProgram(0);
      if (!Boolean.FALSE.equals(enabled)) {
         try {
            if (enabled == null) {
               Class var0 = Class.forName("com.mojang.blaze3d.systems.RenderSystem");
               Class var1 = Class.forName("net.minecraft.client.render.GameRenderer");
               renderer = var0.getMethod("setShader", Supplier.class);
               handler = var1.getMethod("getPositionColorProgram");
               enabled = true;
            }

            Supplier var3 = () -> {
               try {
                  return handler.invoke(null);
               } catch (Throwable var1x) {
                  return null;
               }
            };
            renderer.invoke(null, var3);
         } catch (Throwable var2) {
            enabled = false;
         }
      }
   }
}
