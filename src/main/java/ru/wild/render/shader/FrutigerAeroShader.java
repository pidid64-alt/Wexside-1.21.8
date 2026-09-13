package ru.wild.render.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexArrayBuffer;

public final class FrutigerAeroShader implements AutoCloseable {
   private static final FrutigerAeroShader instance = new FrutigerAeroShader();
   private static final String data = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private static final String context = "assets/wild/shaders/mainmenu/frutiger_aero.frag";
   private final ShaderFailureCache config = new ShaderFailureCache();
   private VertexArrayBuffer state;
   private ShaderFailureCache.ShaderState cache;
   private long output = System.nanoTime();
   private long current;
   private float active;
   private float mode;
   private float selection;
   private float enabled;

   public static FrutigerAeroShader handle() {
      return instance;
   }
   public void handle(int var1, int var2, float var3, float var4, ThemeColors var5, float var6) {
      if (var1 > 0 && var2 > 0) {
         this.process();
         OpenGlStateSnapshot.NetworkState var7 = OpenGlStateSnapshot.handle();
         boolean var17 = false /* VF: Semaphore variable */;

         try {
            var17 = true;
            GL11.glViewport(0, 0, var1, var2);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDisable(3089);
            GL11.glDisable(36281);
            GlStateManager._enableBlend();
            GlStateManager._blendFuncSeparate(770, 771, 1, 771);
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            this.cache.handle();
            this.cache.handle("uViewport", var1, var2);
            this.cache.handle("uRect", 0.0F, 0.0F, var1, var2);
            long var8 = System.nanoTime();
            float var10 = (float)(var8 - this.output) / 1.0E9F;
            float var11 = this.handle(var3 / Math.max(1.0F, var1));
            float var12 = this.handle(var4 / Math.max(1.0F, var2));
            this.handle(var11, var12, var8);
            this.cache.handle("uTime", var10);
            this.cache.handle("uResolution", var1, var2);
            this.cache.handle("uMouse", var11, var12);
            this.cache.handle("uMouseVelocity", this.selection, this.enabled);
            int var13 = var5 == null ? -8657678 : var5.save();
            int var14 = var5 == null ? -13121888 : var5.submit();
            this.cache.handle("uAccentTop", this.handle(var13, 16), this.handle(var13, 8), this.handle(var13, 0));
            this.cache.handle("uAccentBottom", this.handle(var14, 16), this.handle(var14, 8), this.handle(var14, 0));
            this.cache.handle("uAlpha", this.handle(var6) * 0.9F);
            this.cache.handle("uLightMode", var5 != null && var5.unload() ? 1.0F : 0.0F);
            this.state.handle();
            var17 = false;
         } finally {
            if (var17) {
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var7);
               GlStateManager._enableBlend();
               GlStateManager._blendFuncSeparate(770, 771, 1, 771);
            }
         }

         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, 0);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var7);
         GlStateManager._enableBlend();
         GlStateManager._blendFuncSeparate(770, 771, 1, 771);
      }
   }

   private void handle(float var1, float var2, long var3) {
      if (this.current == 0L) {
         this.current = var3;
         this.active = var1;
         this.mode = var2;
         this.selection = 0.0F;
         this.enabled = 0.0F;
      } else {
         float var5 = (float)(var3 - this.current) / 1.0E9F;
         this.current = var3;
         if (Float.isFinite(var5) && !(var5 <= 0.0F)) {
            var5 = Math.min(var5, 0.08F);
            float var6 = this.handle((var1 - this.active) / var5, 4.0F);
            float var7 = this.handle((var2 - this.mode) / var5, 4.0F);
            this.active = var1;
            this.mode = var2;
            float var8 = 1.0F - (float)Math.exp(-var5 * 16.0F);
            this.selection = this.selection + (var6 - this.selection) * var8;
            this.enabled = this.enabled + (var7 - this.enabled) * var8;
         }
      }
   }

   private void process() {
      if (this.state == null) {
         this.state = new VertexArrayBuffer();
      }

      if (this.cache == null) {
         this.cache = this.config
            .handle("frutiger_aero_click_gui", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/frutiger_aero.frag");
      }
   }

   private float handle(int var1, int var2) {
      return (var1 >> var2 & 0xFF) / 255.0F;
   }

   private float handle(float var1) {
      return Math.max(0.0F, Math.min(1.0F, var1));
   }

   private float handle(float var1, float var2) {
      return Math.max(-var2, Math.min(var2, var1));
   }

   @Override
   public void close() {
      if (this.state != null) {
         this.state.close();
         this.state = null;
      }

      this.config.close();
      this.cache = null;
      this.output = System.nanoTime();
      this.current = 0L;
      this.active = 0.0F;
      this.mode = 0.0F;
      this.selection = 0.0F;
      this.enabled = 0.0F;
   }
}
