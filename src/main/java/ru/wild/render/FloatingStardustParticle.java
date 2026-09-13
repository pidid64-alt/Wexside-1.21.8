package ru.wild.render;

import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.Vec3d;
import ru.wild.modules.visuals.Stardust;
import ru.wild.render.shader.StardustShader;

public final class FloatingStardustParticle extends SpriteBillboardParticle {
   public static SimpleParticleType instance = StardustParticleRegistry.context;
   private static int data;
   private static int context;
   private static final float config = 0.035F;
   private static final float state = 0.0238F;
   private static final float cache = (float)Math.sin(0.035F);
   private static final float output = (float)Math.cos(0.035F);
   private static final float current = (float)Math.sin(0.0238F);
   private static final float active = (float)Math.cos(0.0238F);
   private static ClientWorld mode;
   private static long selection = Long.MIN_VALUE;
   private static double enabled;
   private static double renderer;
   private static double handler;
   private static double animationDraw;
   private static float pointEncode;
   private static boolean animator;
   private final int source;
   private final float target;
   private final float pending;
   private final float previous;
   private final float latest;
   private final int summary;
   private final int matrixBlend;
   private final int vectorMatch;
   private float itemProject;
   private float responseCompute;
   private float providerFetch;
   private float profileDraw;
   private float vectorPerform;
   private float eventAttach;
   private float serverRead;
   private float positionAdvance;
   private float frameCheck;
   private boolean moduleCollect = true;

   public FloatingStardustParticle(ClientWorld var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      float var14 = process((float)(var2 * 0.071 + var4 * 0.113 + var6 * 0.197));
      float var15 = process(var14 * 37.13F + (float)var4 * 0.021F);
      float var16 = process(var15 * 51.73F + (float)var6 * 0.017F);
      float var17 = var14 * (float) (Math.PI * 2);
      float var18 = var15 * (float) (Math.PI * 2);
      this.pending = var15;
      this.target = 0.092F + var16 * 0.244F + process(0.74F, 1.0F, var15) * 0.306F;
      float var19 = (1.0F + var14 * 60.0F) * 0.035F;
      float var20 = var19 * 0.68F;
      this.itemProject = (float)Math.sin(var19 + var17);
      this.responseCompute = (float)Math.cos(var19 + var17);
      this.providerFetch = (float)Math.sin(var20 + var18);
      this.profileDraw = (float)Math.cos(var20 + var18);
      this.vectorPerform = (float)Math.sin(var19 + var18);
      this.eventAttach = (float)Math.cos(var19 + var18);
      float var21 = 0.085F + var14 * 0.075F;
      this.previous = (float)Math.sin(var21);
      this.latest = (float)Math.cos(var21);
      this.serverRead = (float)Math.sin(var17);
      this.positionAdvance = (float)Math.cos(var17);
      this.update();
      int var22 = var14 < 0.58F ? Stardust.animate() : Stardust.load();
      float var23 = 0.36F + var15 * 0.36F;
      float var24 = process(0.84F, 1.0F, var16);
      this.summary = handle(handle(184.0F + var24 * 48.0F, handle(var22, 16), var23) + var14 * 18.0F);
      this.matrixBlend = handle(handle(218.0F + var24 * 30.0F, handle(var22, 8), var23) + var15 * 14.0F);
      this.vectorMatch = handle(handle(246.0F, handle(var22, 0), var23) + var16 * 10.0F);
      this.source = context;
      this.maxAge = 142 + (int)(var15 * 138.0F);
      this.collidesWithWorld = false;
      this.gravityStrength = 0.0F;
      this.velocityMultiplier = 0.99F;
      this.setVelocity(var8, var10, var12);
      this.scale = this.target;
      data++;
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.CUSTOM;
   }

   public void tick() {
      this.lastX = this.x;
      this.lastY = this.y;
      this.lastZ = this.z;
      if (Stardust.refresh() && this.source == context && this.age++ < this.maxAge) {
         if (handle(this.world)) {
            double var1 = this.x - enabled;
            double var3 = this.y - renderer;
            double var5 = this.z - handler;
            if (var1 * var1 + var5 * var5 > animationDraw || var3 < -2.4 || var3 > pointEncode) {
               this.markDead();
               return;
            }
         }

         this.x = this.x + (this.velocityX + this.itemProject * 0.0028);
         this.y = this.y + (this.velocityY + this.providerFetch * 0.0022);
         this.z = this.z + (this.velocityZ + this.eventAttach * 0.0028);
         this.velocityX *= 0.973;
         this.velocityY *= 0.97;
         this.velocityZ *= 0.973;
         this.compute();
         this.resolve();
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
         float var6 = process(0.0F, 0.18F, var5) * (1.0F - process(0.76F, 1.0F, var5));
         float var7 = this.serverRead + (this.frameCheck - this.serverRead) * var4;
         float var8 = 0.76F + 0.24F * var7;
         var6 *= var8 * Stardust.render() * (0.68F + this.pending * 0.48F);
         if (!(var6 <= 0.003F)) {
            ParticleCameraTransform.handle(var3);
            double var9 = this.lastX + (this.x - this.lastX) * var4;
            double var11 = this.lastY + (this.y - this.lastY) * var4;
            double var13 = this.lastZ + (this.z - this.lastZ) * var4;
            float var15 = (float)(var9 - ParticleCameraTransform.handle());
            float var16 = (float)(var11 - ParticleCameraTransform.process());
            float var17 = (float)(var13 - ParticleCameraTransform.compute());
            float var18 = ParticleCameraTransform.resolve();
            float var19 = ParticleCameraTransform.update();
            float var20 = ParticleCameraTransform.apply();
            float var21 = ParticleCameraTransform.execute();
            float var22 = ParticleCameraTransform.prepare();
            float var23 = ParticleCameraTransform.check();
            float var24 = (float)Math.sqrt(var15 * var15 + var16 * var16 + var17 * var17);
            float var25 = 1.0F - process(2.8F, 24.0F, var24);
            float var26 = this.target * (0.9F + var8 * 0.24F + var25 * 0.88F);
            float var27 = var26 * 0.5F;
            int var28 = handle(var6 * 255.0F);
            VertexConsumer var29 = var2.getBuffer(StardustShader.process());
            float var30 = var18 * var27;
            float var31 = var19 * var27;
            float var32 = var20 * var27;
            float var33 = var21 * var27;
            float var34 = var22 * var27;
            float var35 = var23 * var27;
            this.handle(var29, var15 - var30 - var33, var16 - var31 - var34, var17 - var32 - var35, 0.0F, 1.0F, var28);
            this.handle(var29, var15 + var30 - var33, var16 + var31 - var34, var17 + var32 - var35, 1.0F, 1.0F, var28);
            this.handle(var29, var15 + var30 + var33, var16 + var31 + var34, var17 + var32 + var35, 1.0F, 0.0F, var28);
            this.handle(var29, var15 - var30 + var33, var16 - var31 + var34, var17 - var32 + var35, 0.0F, 0.0F, var28);
         }
      }
   }

   private static boolean handle(ClientWorld var0) {
      if (var0 == null) {
         animator = false;
         return false;
      } else {
         long var1 = var0.getTime();
         if (var0 == mode && var1 == selection) {
            return animator;
         } else {
            mode = var0;
            selection = var1;
            MinecraftClient var3 = MinecraftClient.getInstance();
            if (var3 != null && var3.gameRenderer != null && var3.gameRenderer.getCamera() != null) {
               Vec3d var4 = var3.gameRenderer.getCamera().getPos();
               double var5 = Stardust.drawAnimation() + 5.0F;
               enabled = var4.x;
               renderer = var4.y;
               handler = var4.z;
               animationDraw = var5 * var5;
               pointEncode = Stardust.encodePoint();
               animator = true;
               return true;
            } else {
               animator = false;
               return false;
            }
         }
      }
   }

   private void compute() {
      float var1 = this.itemProject * output + this.responseCompute * cache;
      this.responseCompute = this.responseCompute * output - this.itemProject * cache;
      this.itemProject = var1;
      var1 = this.providerFetch * active + this.profileDraw * current;
      this.profileDraw = this.profileDraw * active - this.providerFetch * current;
      this.providerFetch = var1;
      var1 = this.vectorPerform * output + this.eventAttach * cache;
      this.eventAttach = this.eventAttach * output - this.vectorPerform * cache;
      this.vectorPerform = var1;
   }

   private void resolve() {
      float var1 = this.frameCheck;
      float var2 = this.positionAdvance * this.latest - this.serverRead * this.previous;
      this.serverRead = var1;
      this.positionAdvance = var2;
      this.update();
   }

   private void update() {
      this.frameCheck = this.serverRead * this.latest + this.positionAdvance * this.previous;
   }

   public void markDead() {
      if (this.moduleCollect) {
         this.moduleCollect = false;
         if (this.source == context && data > 0) {
            data--;
         }
      }

      super.markDead();
   }

   private void handle(VertexConsumer var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      var1.vertex(var2, var3, var4).texture(var5, var6).color(this.summary, this.matrixBlend, this.vectorMatch, var7).normal(0.0F, 1.0F, 0.0F);
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
         return new FloatingStardustParticle(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
