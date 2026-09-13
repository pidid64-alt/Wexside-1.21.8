package ru.wild.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.SdfTextRenderer;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class WaypointArrowRenderer {
   private static final MinecraftClient state = MinecraftClient.getInstance();
   private static final Identifier cache = Identifier.of("wild", "textures/arrows/arrows.png");
   private static final double output = 32.0;
   private static final float current = 180.0F;
   private static final float active = 16.0F;
   private static final float mode = 22.0F;
   private static final float selection = 25.0F;
   public float instance;
   public float data;
   public double context;
   public boolean config;

   public boolean handle(Vec3d var1) {
      return this.handle(var1, true);
   }

   public boolean handle(Vec3d var1, boolean var2) {
      this.config = false;
      if (state.gameRenderer != null && state.gameRenderer.getCamera() != null) {
         Vec3d var3 = state.gameRenderer.getCamera().getPos();
         double var4 = var1.x - var3.x;
         double var6 = var1.y - var3.y;
         double var8 = var1.z - var3.z;
         this.context = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
         Vec3d var10 = var1;
         if (var2 && this.context > 32.0) {
            double var11 = 32.0 / this.context;
            var10 = new Vec3d(var3.x + var4 * var11, var3.y + var6 * var11, var3.z + var8 * var11);
         }

         Vec3d var13 = ClientMathUtil.handle(var10);
         if (var13 != null && !(var13.z <= 0.001) && !(var13.z > 1.0)) {
            this.instance = (float)var13.x;
            this.data = (float)var13.y;
            this.config = true;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
   public static void handle(RoundedRectRenderer var0, Vec3d var1, String var2, String var3, float var4, int var5, int var6) {
      if (!(var4 <= 0.004F) && state.gameRenderer != null && state.gameRenderer.getCamera() != null) {
         int var7 = handle();
         if (var7 > 0) {
            Vec3d var8 = state.gameRenderer.getCamera().getPos();
            double var9 = var1.x - var8.x;
            double var11 = var1.z - var8.z;
            float var13 = state.gameRenderer.getCamera().getYaw();
            double var14 = MathHelper.cos((float)Math.toRadians(var13));
            double var16 = MathHelper.sin((float)Math.toRadians(var13));
            double var18 = Math.atan2(-(var11 * var14 - var9 * var16), -(var9 * var14 + var11 * var16)) * 180.0 / Math.PI;
            float var20 = process() * 0.5F;
            float var21 = 180.0F * var20 * var4;
            float var22 = var5 * 0.5F + var21 * MathHelper.cos((float)Math.toRadians(var18));
            float var23 = var6 * 0.5F + var21 * MathHelper.sin((float)Math.toRadians(var18));
            int var24 = PackedColor.update(RoundedRectRenderer.ColorState.apply(1, 1), (int)(255.0F * var4));
            int var25 = PackedColor.compute(255, 255, 255, (int)(255.0F * var4));
            float var26 = 16.0F * var20;
            float var27 = 22.0F * var20;
            float var28 = 25.0F * var20;
            var0.handle(var22, var23);

            try {
               var0.process((float)(var18 + 90.0));
               boolean var37 = false /* VF: Semaphore variable */;

               try {
                  var37 = true;
                  var0.handle(var7, -var26, -var26, var26 * 2.0F, var26 * 2.0F, var25, false);
                  var37 = false;
               } finally {
                  if (var37) {
                     var0.execute();
                  }
               }

               var0.execute();
               SdfTextRenderer.State var29 = RoundedRectRenderer.handle(FontRegistry.instance, var2, var27);
               SdfTextRenderer.State var30 = RoundedRectRenderer.handle(FontRegistry.instance, var3, var28);
               float var31 = var26 + 10.0F * var20 + var29.data;
               var0.handle(FontRegistry.instance, -var29.instance * 0.5F, var31, var27, var2, PackedColor.compute(240, 240, 244, (int)(255.0F * var4)));
               var0.handle(FontRegistry.instance, -var30.instance * 0.5F, var31 + var30.data + 5.0F * var20, var28, var3, var24);
            } finally {
               var0.prepare();
            }
         }
      }
   }

   private static int handle() {
      try {
         AbstractTexture var0 = state.getTextureManager().getTexture(cache);
         return var0 != null && var0.getGlTexture() instanceof GlTexture var1 ? var1.getGlId() : -1;
      } catch (Throwable var3) {
         return -1;
      }
   }

   private static float process() {
      if (state != null && state.getWindow() != null) {
         float var0 = state.getWindow().getScaleFactor();
         return var0 <= 0.0F ? 2.0F : var0;
      } else {
         return 2.0F;
      }
   }
}
