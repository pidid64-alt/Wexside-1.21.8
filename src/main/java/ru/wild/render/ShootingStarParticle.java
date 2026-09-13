package ru.wild.render;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import ru.wild.modules.visuals.Stardust;
import ru.wild.render.shader.StardustShader;

public final class ShootingStarParticle extends SpriteBillboardParticle {
   public static SimpleParticleType instance = StardustParticleRegistry.config;
   private static int data;
   private static int context;
   private final int config;
   private final float state;
   private final float cache;
   private final float output;
   private final float current;
   private final float active;
   private final float mode;
   private final float selection;
   private final int enabled;
   private final int renderer;
   private final int handler;
   private float animationDraw;
   private float pointEncode;
   private float animator;
   private boolean source = true;

   public ShootingStarParticle(ClientWorld var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      float var14 = process((float)(var2 * 0.047 + var4 * 0.131 + var6 * 0.089));
      float var15 = process(var14 * 43.19F + (float)var8 * 7.71F);
      float var16 = process((float)var8, (float)var10, (float)var12);
      if (var16 <= 0.0F) {
         var8 = 0.028;
         var10 = -0.07;
         var12 = 0.018;
         var16 = process((float)var8, (float)var10, (float)var12);
      }

      this.state = 0.036F + var14 * 0.026F;
      this.cache = 3.4F + var15 * 3.2F;
      this.output = (float)var8 * var16;
      this.current = (float)var10 * var16;
      this.active = (float)var12 * var16;
      float var17 = 0.2F + var14 * 0.18F;
      this.mode = (float)Math.sin(var17);
      this.selection = (float)Math.cos(var17);
      this.animationDraw = 0.0F;
      this.pointEncode = 1.0F;
      this.resolve();
      int var18 = var14 < 0.5F ? Stardust.animate() : Stardust.load();
      this.enabled = handle(handle(214.0F, handle(var18, 16), 0.44F + var14 * 0.28F) + 18.0F);
      this.renderer = handle(handle(236.0F, handle(var18, 8), 0.38F + var15 * 0.3F) + 12.0F);
      this.handler = handle(handle(255.0F, handle(var18, 0), 0.32F + var15 * 0.24F));
      this.config = context;
      this.maxAge = 34 + (int)(var15 * 28.0F);
      this.collidesWithWorld = false;
      this.gravityStrength = 0.0F;
      this.velocityMultiplier = 0.986F;
      this.setVelocity(var8, var10, var12);
      data++;
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.CUSTOM;
   }

   public void tick() {
      this.lastX = this.x;
      this.lastY = this.y;
      this.lastZ = this.z;
      if (Stardust.refresh() && this.config == context && this.age++ < this.maxAge) {
         this.x = this.x + this.velocityX;
         this.y = this.y + this.velocityY;
         this.z = this.z + this.velocityZ;
         this.velocityX *= 0.985;
         this.velocityY *= 0.985;
         this.velocityZ *= 0.985;
         this.compute();
      } else {
         this.markDead();
      }
   }

   public void render(VertexConsumer var1, Camera var2, float var3) {
   }

   public void renderCustom(MatrixStack var1, VertexConsumerProvider var2, Camera var3, float var4) {
      if (!Stardust.refresh()) {
         this.markDead();
      } else {
         float var5 = (this.age + var4) / this.maxAge;
         float var6 = compute(0.0F, 0.12F, var5) * (1.0F - compute(0.7F, 1.0F, var5)) * Stardust.render() * 0.92F;
         if (!(var6 <= 0.003F)) {
            ParticleCameraTransform.handle(var3);
            double var7 = this.lastX + (this.x - this.lastX) * var4;
            double var9 = this.lastY + (this.y - this.lastY) * var4;
            double var11 = this.lastZ + (this.z - this.lastZ) * var4;
            float var13 = (float)(var7 - ParticleCameraTransform.handle());
            float var14 = (float)(var9 - ParticleCameraTransform.process());
            float var15 = (float)(var11 - ParticleCameraTransform.compute());
            float var16 = ParticleCameraTransform.resolve();
            float var17 = ParticleCameraTransform.update();
            float var18 = ParticleCameraTransform.apply();
            float var19 = ParticleCameraTransform.execute();
            float var20 = ParticleCameraTransform.prepare();
            float var21 = ParticleCameraTransform.check();
            float var22 = this.output;
            float var23 = this.current;
            float var24 = this.active;
            float var25 = var22 * var16 + var23 * var17 + var24 * var18;
            float var26 = var22 * var19 + var23 * var20 + var24 * var21;
            float var27 = process(var25, var26, 0.0F);
            if (var27 <= 0.0F) {
               var25 = 1.0F;
               var26 = 0.0F;
               var27 = 1.0F;
            }

            var25 *= var27;
            var26 *= var27;
            float var28 = var16 * var25 + var19 * var26;
            float var29 = var17 * var25 + var20 * var26;
            float var30 = var18 * var25 + var21 * var26;
            float var31 = var16 * -var26 + var19 * var25;
            float var32 = var17 * -var26 + var20 * var25;
            float var33 = var18 * -var26 + var21 * var25;
            float var34 = this.animationDraw + (this.animator - this.animationDraw) * var4;
            float var35 = 0.84F + 0.16F * var34;
            float var36 = this.state * var35;
            float var37 = this.state * 0.26F;
            float var38 = this.cache * (0.86F + var35 * 0.24F);
            float var39 = var28 * var36;
            float var40 = var29 * var36;
            float var41 = var30 * var36;
            float var42 = var28 * var38;
            float var43 = var29 * var38;
            float var44 = var30 * var38;
            float var45 = var31 * var36;
            float var46 = var32 * var36;
            float var47 = var33 * var36;
            float var48 = var31 * var37;
            float var49 = var32 * var37;
            float var50 = var33 * var37;
            int var51 = handle(var6 * 255.0F);
            VertexConsumer var52 = var2.getBuffer(StardustShader.process());
            this.handle(var52, var13 - var42 - var48, var14 - var43 - var49, var15 - var44 - var50, 0.0F, 1.0F, var51);
            this.handle(var52, var13 + var39 - var45, var14 + var40 - var46, var15 + var41 - var47, 1.0F, 0.0F, var51);
            this.handle(var52, var13 + var39 + var45, var14 + var40 + var46, var15 + var41 + var47, 1.0F, 0.0F, var51);
            this.handle(var52, var13 - var42 + var48, var14 - var43 + var49, var15 - var44 + var50, 0.0F, 1.0F, var51);
         }
      }
   }

   private void compute() {
      float var1 = this.animator;
      float var2 = this.pointEncode * this.selection - this.animationDraw * this.mode;
      this.animationDraw = var1;
      this.pointEncode = var2;
      this.resolve();
   }

   private void resolve() {
      this.animator = this.animationDraw * this.selection + this.pointEncode * this.mode;
   }

   public void markDead() {
      if (this.source) {
         this.source = false;
         if (this.config == context && data > 0) {
            data--;
         }
      }

      super.markDead();
   }

   private void handle(VertexConsumer var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      var1.vertex(var2, var3, var4).texture(var5, var6).color(this.enabled, this.renderer, this.handler, var7).normal(1.0F, 0.0F, 0.0F);
   }

   public static int handle() {
      return data;
   }

   public static void process() {
      data = 0;
      context++;
   }

   private static int handle(float var0) {
      if (var0 <= 0.0F) {
         return 0;
      } else {
         return var0 >= 255.0F ? 255 : (int)var0;
      }
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static int handle(int var0, int var1) {
      return var0 >>> var1 & 0xFF;
   }

   private static float process(float var0, float var1, float var2) {
      float var3 = var0 * var0 + var1 * var1 + var2 * var2;
      return var3 <= 1.0E-8F ? 0.0F : (float)(1.0 / Math.sqrt(var3));
   }

   private static float compute(float var0, float var1, float var2) {
      float var3 = (var2 - var0) / (var1 - var0);
      if (var3 <= 0.0F) {
         return 0.0F;
      } else {
         return var3 >= 1.0F ? 1.0F : var3 * var3 * (3.0F - 2.0F * var3);
      }
   }

   private static float process(float var0) {
      return compute((float)Math.sin(var0 * 12.9898F + 78.233F) * 43758.547F);
   }

   private static float compute(float var0) {
      return var0 - (float)Math.floor(var0);
   }

   public static final class State implements ParticleFactory<SimpleParticleType> {
      @Override
      public Particle createParticle(SimpleParticleType var1, ClientWorld var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ShootingStarParticle(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
