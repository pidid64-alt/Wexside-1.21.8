package ru.wild.gui.widget;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.DiffuseShaderRenderer;
import ru.wild.render.shader.ShaderBuildResult;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderGraphBlock;
import ru.wild.render.shader.ShaderGraphCompiler;
import ru.wild.render.shader.ShaderNodeDefinition;
import ru.wild.render.shader.ShaderNodeRegistry;
import ru.wild.render.shader.ShaderPinDefinition;
import ru.wild.render.shader.ShaderPreviewSession;
import ru.wild.render.texture.OffscreenRenderTarget;
import ru.wild.util.render.RoundedRectRenderer;

public final class ColorPickerWidget implements AutoCloseable {
   private final ShaderNodeRegistry instance;
   private final ShaderGraphCompiler data;
   private final OffscreenRenderTarget context = new OffscreenRenderTarget();
   private final Map<String, ColorPickerWidget.DataRecord> config = new HashMap<>();

   public ColorPickerWidget(ShaderNodeRegistry var1, ShaderGraphCompiler var2) {
      this.instance = var1;
      this.data = var2;
   }

   public void handle(
      ShaderGraph var1,
      String var2,
      ShaderPreviewSession var3,
      RoundedRectRenderer var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      ThemeColors var11,
      float var12
   ) {
      if (var1 != null && var2 != null && var4 != null && !(var7 <= 2.0F) && !(var8 <= 2.0F) && !(var12 <= 0.001F)) {
         ShaderGraphBlock var13 = var1.compute(var2);
         ShaderNodeDefinition var14 = var13 == null ? null : this.instance.handle(var13.process());
         ShaderPinDefinition var15 = handle(var14);
         if (var15 != null) {
            String var16 = "__node_preview_" + var2;
            ColorPickerWidget.DataRecord var17 = this.config.get(var16);
            int var18 = var1.update();
            if (var17 == null || var17.version != var18 || !var15.id().equals(var17.pinId)) {
               ShaderGraph var19 = var1.update(var2);
               var19.handle(LivePreviewRenderer.PREVIEW_ONLY.handle());
               ShaderBuildResult var20 = this.data.handle(var19, var2, var15.id(), var15.type());
               var17 = new ColorPickerWidget.DataRecord(var18, var15.id(), var20 == null ? "" : var20.hash(), var20);
               this.config.put(var16, var17);
            }

            ShaderBuildResult var26 = var17.compilation;
            if (var26 != null && var26.ok()) {
               var4.compute();
               OpenGlStateSnapshot.NetworkState var27 = OpenGlStateSnapshot.handle();

               label84: {
                  try {
                     int var21 = Math.max(32, Math.min(512, (int)Math.ceil(var7)));
                     int var22 = Math.max(32, Math.min(384, (int)Math.ceil(var8)));
                     this.context.handle(var21, var22);
                     if (this.context.apply()) {
                        this.context.handle();
                        GL11.glDisable(3089);
                        GlStateManager._enableBlend();
                        GL11.glEnable(3042);
                        GL11.glClearColor(0.008F, 0.01F, 0.015F, 0.0F);
                        GL11.glClear(16384);
                        DiffuseShaderRenderer.handle(
                           "__node_preview_" + var17.hash, var26, 0.0F, 0.0F, var21, var22, var21, var22, var7 * 0.5F, var8 * 0.5F, var11, var12
                        );
                        break label84;
                     }
                  } finally {
                     OpenGlStateSnapshot.compute(var27);
                  }

                  return;
               }

               var4.process(this.context.compute(), var5, var6, var7, var8, ThemeColors.handle(-1, Math.round(255.0F * var12)), true);
            } else {
               handle(var4, var5, var6, var7, var8, var11, var12);
            }
         }
      }
   }

   private static ShaderPinDefinition handle(ShaderNodeDefinition var0) {
      if (var0 != null && !var0.apply().isEmpty()) {
         for (ShaderPinDefinition var2 : var0.apply()) {
            if ("color".equals(var2.id()) || "mask".equals(var2.id()) || "value".equals(var2.id())) {
               return var2;
            }
         }

         return var0.apply().get(0);
      } else {
         return null;
      }
   }

   private static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, ThemeColors var5, float var6) {
      int var7 = ThemeColors.handle(40, 10, 14, Math.round(132.0F * var6));
      int var8 = ThemeColors.handle(255, 134, 146, Math.round(230.0F * var6));
      var0.handle(var1, var2, var3, var4, 8.0F, var7);
      float var9 = ModuleStateHelper.handle(null, FontRegistry.instance, "preview error", 9.0F);
      ModuleStateHelper.handle(var0, null, FontRegistry.instance, var1 + (var3 - var9) * 0.5F, var2, var4, 9.0F, "preview error", var8);
   }

   @Override
   public void close() {
      this.context.close();
      this.config.clear();
   }

   record DataRecord(int version, String pinId, String hash, ShaderBuildResult compilation) {
   }
}
