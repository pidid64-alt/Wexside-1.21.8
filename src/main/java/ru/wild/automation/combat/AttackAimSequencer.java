package ru.wild.automation.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.player.EntityAimGeometry;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;

public final class AttackAimSequencer implements MinecraftContext {
   private static final int instance = 15;
   private static final int data = 3;
   private static final float context = 0.33333334F;
   private static final long config = 17L;
   private static final float state = 2.0F;
   private static final float cache = 0.35F;
   private static final float output = 0.6F;
   private static final float current = 0.35F;
   private static final float active = 0.75F;
   private static final float mode = 180.0F;
   private static final float renderer = 220.0F;
   private static final float handler = 340.0F;
   private static final int animationDraw = 14;
   private static final float pointEncode = 0.6F;
   private static final float animator = 90.0F;
   private static final float source = 170.0F;
   private static final float target = 26.0F;
   private static final float pending = 44.0F;
   private static final float previous = 450.0F;
   private static final long latest = 100L;
   private static final long summary = ThreadLocalRandom.current().nextLong(0L, 86400000L);
   private static final RotationController.Contract matrixBlend = AttackAimSequencer::resolve;
   private static final AttackAimSequencer.ColorState vectorMatch = new AttackAimSequencer.ColorState();

   private AttackAimSequencer() {
   }

   public static void handle(LivingEntity var0, boolean var1) {
      if (toggleState.player != null && toggleState.world != null && var0 != null) {
         long var2 = System.currentTimeMillis();
         vectorMatch.data = true;
         process(var2);
         if (vectorMatch.context != var0.getId()) {
            vectorMatch.context = var0.getId();
            vectorMatch.instance = AttackAimSequencer.Mode.HOLD;
            vectorMatch.current = 0L;
            vectorMatch.output = apply();
            vectorMatch.active = null;
            vectorMatch.renderer = null;
            vectorMatch.handler = null;
         }

         vectorMatch.previous = handle(26.0F, 44.0F);
         switch (vectorMatch.instance) {
            case AIM:
               process(var2, var1, var0);
               break;
            case FLICK:
               handle(var2);
               break;
            default:
               handle(var2, var1, var0);
         }
      }
   }

   public static void handle() {
      long var0 = System.currentTimeMillis();
      compute(var0);
      vectorMatch.latest = AdaptiveAttackTiming.drawAnimation();
   }

   public static void process() {
      if (vectorMatch.data) {
         if (toggleState.player != null && toggleState.world != null) {
            vectorMatch.handle();
            RotationController.Mode var0 = RotationController.instance;
            if (var0 != RotationController.Mode.RESET) {
               if (var0 == RotationController.Mode.AIM) {
                  RotationController.handle(matrixBlend);
               } else {
                  update();
               }
            }
         } else {
            update();
         }
      }
   }

   public static void compute() {
      if (vectorMatch.data && toggleState.player != null) {
         vectorMatch.handle();
         if (RotationController.instance == RotationController.Mode.IDLE) {
            update();
         }
      } else {
         update();
      }
   }

   private static void handle(long var0, boolean var2, LivingEntity var3) {
      if (var0 >= vectorMatch.current && process(var3, var2)) {
         vectorMatch.instance = AttackAimSequencer.Mode.AIM;
         vectorMatch.state = 0;
         vectorMatch.cache = var0;
         vectorMatch.config++;
         vectorMatch.active = null;
         process(var0, var2, var3);
      }
   }

   private static boolean process(LivingEntity var0, boolean var1) {
      if (toggleState.player.isUsingItem()) {
         return false;
      } else {
         return EntityAimGeometry.process(var0).length() > AttackAura.handle(var0) + 0.6F
            ? false
            : var1 || (float)AdaptiveAttackTiming.refresh() - AdaptiveAttackTiming.drawAnimation() <= vectorMatch.output;
      }
   }

   private static void process(long var0, boolean var2, LivingEntity var3) {
      vectorMatch.state++;
      if (!toggleState.player.isUsingItem() && vectorMatch.state <= 14) {
         Vec3d var4 = toggleState.player.getEyePos();
         Vec3d var5 = handle(var3, var4, AttackAura.handle(var3), vectorMatch.config, toggleState.player.age);
         vectorMatch.mode = var5;
         vectorMatch.active = process(vectorMatch.active, var5);
         float[] var6 = compute(var4, vectorMatch.active);
         vectorMatch.selection = var6[0];
         vectorMatch.enabled = var6[1];
         handle(var0, var2, var4);
      } else {
         handle(var0, handle(420.0F, 900.0F));
      }
   }

   private static void handle(long var0) {
      if (vectorMatch.renderer != null && vectorMatch.handler != null) {
         float var2 = (float)Math.max(vectorMatch.pointEncode, 1L);
         float var3 = MathHelper.clamp((float)(var0 - vectorMatch.animationDraw) / var2, 0.0F, 1.0F);
         float var4 = var3 * var3 * (3.0F - 2.0F * var3);
         float var5 = MathHelper.wrapDegrees(vectorMatch.handler.instance - vectorMatch.renderer.instance);
         float var6 = MathHelper.clamp(MathHelper.wrapDegrees(vectorMatch.handler.data - vectorMatch.renderer.data), -90.0F, 90.0F);
         handle(
            new RotationAngles(vectorMatch.renderer.instance + var5 * var4, MathHelper.clamp(vectorMatch.renderer.data + var6 * var4, -90.0F, 90.0F)),
            360.0F,
            360.0F
         );
         if (var3 >= 1.0F) {
            vectorMatch.renderer = null;
            vectorMatch.handler = null;
            handle(var0, handle(40.0F, 120.0F));
         }
      } else {
         handle(var0, handle(40.0F, 120.0F));
      }
   }

   private static void handle(long var0, float var2) {
      vectorMatch.instance = AttackAimSequencer.Mode.HOLD;
      vectorMatch.current = var0 + (long)var2;
      vectorMatch.output = apply();
      vectorMatch.state = 0;
      vectorMatch.active = null;
      vectorMatch.matrixBlend = 0L;
      if (RotationController.instance != RotationController.Mode.IDLE) {
         RotationController.handle(matrixBlend);
      }
   }

   private static void handle(long var0, boolean var2, Vec3d var3) {
      boolean var4 = var2 && AdaptiveAttackTiming.tick() > 0.35F;
      float var5 = vectorMatch.selection;
      float var6 = vectorMatch.enabled;
      if (var4 && vectorMatch.mode != null) {
         float[] var7 = compute(var3, vectorMatch.mode);
         var5 = var7[0];
         var6 = var7[1];
      }

      float var34 = var4 ? 0.1505F : 0.35F;
      float var8 = var4 ? 2.5F : 1.0F;
      float var9 = toggleState.player.getYaw();
      float var10 = toggleState.player.getPitch();

      for (int var11 = 0; var11 < 3; var11++) {
         long var12 = var0 - (2 - var11) * 17L;
         long var14 = var12 + summary;
         float var16 = Math.abs(MathHelper.wrapDegrees(var5 - var9));
         float var17 = Math.abs(var6 - var10);
         float var18 = Math.max(var16, var17);
         float var19 = MathHelper.clamp(1.0F - var18 / 8.0F, 0.0F, 1.0F);
         float var20 = MathHelper.clamp((float)(var12 - vectorMatch.cache) / 180.0F, 0.0F, 1.0F);
         if (var12 > vectorMatch.animator) {
            vectorMatch.animator = var12 + ThreadLocalRandom.current().nextInt(260, 640);
            vectorMatch.source = handle(0.75F, 1.25F);
         }

         if (var12 > vectorMatch.target) {
            vectorMatch.target = var12 + ThreadLocalRandom.current().nextInt(150, 350);
            vectorMatch.pending = handle(0.8F, 1.2F);
         }

         float var21 = MathHelper.clamp(var18 / 30.0F, 0.15F, 0.8F);
         float var22 = 0.8F + 0.2F * (float)(Math.sin(var14 / 137.0) * 0.6 + Math.sin(var14 / 89.0 + 1.7) * 0.3 + Math.sin(var14 / 61.0 + 4.2) * 0.1);
         float var23 = (var2 ? 46.0F : 30.0F) * var21 * var22 * vectorMatch.source * var8 * 2.0F;
         float var24 = Math.signum(MathHelper.wrapDegrees(var5 - var9));
         float var25 = Math.signum(var6 - var10);
         float var26 = var23 * vectorMatch.pending * (1.0F + 0.25F * var24 * vectorMatch.source) * (0.9F + 0.2F * (float)Math.sin(var14 / 173.0));
         float var27 = var23 * 0.55F * (1.0F - 0.2F * var25 * vectorMatch.source) * (0.85F + 0.15F * (float)Math.cos(var14 / 151.0));
         float var28 = var19 * var34 * (float)(Math.sin(var14 / 9.0) * 0.25 + Math.cos(var14 / 13.0) * 0.15);
         float var29 = var34 * (float)Math.sin(var14 / 420.0) * 0.7F * var19;
         float var30 = var34 * (float)(Math.sin(var14 / 110.0) * 4.5 + Math.cos(var14 / 57.0) * 2.0);
         float var31 = var34 * (float)(Math.cos(var14 / 55.0) * 3.2 + Math.sin(var14 / 83.0) * 1.6);
         float var32 = 0.4F + 0.6F * var20;
         RotationAngles var33 = handle(
            var9, var10, var5, var6, var26 * var32 * 0.33333334F, var27 * var32 * 0.33333334F, 0.75F, var30 + var28 + var29, var31 + var28
         );
         var9 = var33.instance;
         var10 = var33.data;
      }

      handle(new RotationAngles(MathHelper.wrapDegrees(var9), MathHelper.clamp(var10, -90.0F, 90.0F)), 360.0F, 360.0F);
   }

   private static RotationAngles handle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = MathHelper.wrapDegrees(var2 - var0);
      float var10 = MathHelper.clamp(MathHelper.wrapDegrees(var3 - var1), -90.0F, 90.0F);
      float var11 = Math.max((float)Math.hypot(Math.abs(var9), Math.abs(var10)), 1.0E-4F);
      float var12 = Math.abs(var9 / var11) * Math.max(var4, 0.0F);
      float var13 = Math.abs(var10 / var11) * Math.max(var5, 0.0F);
      float var14 = var0 + MathHelper.clamp(var9, -var12, var12) + var7;
      float var15 = MathHelper.clamp(var1 + MathHelper.clamp(var10, -var13, var13) + var8, -90.0F, 90.0F);
      return new RotationAngles(var0 + var6 * MathHelper.wrapDegrees(var14 - var0), MathHelper.clamp(var1 + var6 * (var15 - var1), -90.0F, 90.0F));
   }

   private static void handle(RotationAngles var0, float var1, float var2) {
      vectorMatch.matrixBlend = 0L;
      RotationController.handle(var0, Math.max(var1, 0.0F), Math.max(var2, 0.0F), vectorMatch.previous, vectorMatch.previous, 0, 15, false, matrixBlend);
   }

   private static RotationController.State resolve() {
      if (toggleState.player != null && vectorMatch.data) {
         RotationAngles var0 = new RotationAngles(toggleState.player);
         RotationAngles var1 = new RotationAngles(ViewRotationCoordinator.context, ViewRotationCoordinator.config);
         float var2 = var0.handle(var1);
         if (var2 < 1.0F) {
            vectorMatch.matrixBlend = 0L;
            return RotationController.State.handle();
         }

         long var3 = System.currentTimeMillis();
         if (vectorMatch.matrixBlend == 0L) {
            vectorMatch.matrixBlend = var3;
         }

         float var5 = MathHelper.clamp(1.0F - (float)(var3 - vectorMatch.matrixBlend) / 450.0F, 0.0F, 1.0F) * MathHelper.clamp(var2 / 20.0F, 0.0F, 1.0F);
         float var6 = update(var3) * var5;
         RotationAngles var7 = new RotationAngles(var1.instance + var6, MathHelper.clamp(var1.data + var6 * 0.5F, -90.0F, 90.0F));
         float var8 = Math.max(vectorMatch.previous, 1.0F);
         return new RotationController.State(var7, var8, var8, false);
      } else {
         update();
         return RotationController.State.handle();
      }
   }

   private static void update() {
      vectorMatch.process();
      RotationController.process(matrixBlend);
   }

   private static void process(long var0) {
      float var2 = AdaptiveAttackTiming.drawAnimation();
      float var3 = vectorMatch.latest;
      vectorMatch.latest = var2;
      if (var3 >= 0.0F && var2 < var3) {
         compute(var0);
      }
   }

   private static void compute(long var0) {
      if (var0 - vectorMatch.summary >= 100L) {
         vectorMatch.summary = var0;
         if (vectorMatch.data && vectorMatch.instance == AttackAimSequencer.Mode.AIM && toggleState.player != null && toggleState.world != null) {
            resolve(var0);
         }
      }
   }

   private static void resolve(long var0) {
      RotationAngles var2 = new RotationAngles(toggleState.player);
      vectorMatch.renderer = var2;
      vectorMatch.handler = handle(AttackAura.textureRun, var2);
      vectorMatch.animationDraw = var0;
      vectorMatch.pointEncode = (long)handle(90.0F, 170.0F);
      vectorMatch.instance = AttackAimSequencer.Mode.FLICK;
   }

   private static RotationAngles handle(LivingEntity var0, RotationAngles var1) {
      if (var0 != null && var0.isAlive() && toggleState.player != null) {
         Box var2 = var0.getBoundingBox();
         Vec3d var3 = toggleState.player.getEyePos();
         Vec3d var4 = var2.getCenter();
         double var5 = var4.x - var3.x;
         double var7 = var4.z - var3.z;
         double var9 = Math.hypot(var5, var7);
         if (var9 > 1.0E-4) {
            double var11 = ThreadLocalRandom.current().nextBoolean() ? 1.0 : -1.0;
            double var13 = -var7 / var9 * var11;
            double var15 = var5 / var9 * var11;
            double var17 = handle(0.75F, 1.3F);
            double var19 = Math.min(
               (var2.getLengthX() * 0.5 + var17) / Math.max(Math.abs(var13), 1.0E-4), (var2.getLengthZ() * 0.5 + var17) / Math.max(Math.abs(var15), 1.0E-4)
            );
            double var21 = ThreadLocalRandom.current().nextBoolean() ? 1.0 : -1.0;
            Vec3d var23 = new Vec3d(var4.x + var13 * var19, var4.y + var21 * var2.getLengthY() * handle(0.1F, 0.34F), var4.z + var15 * var19);
            float[] var24 = compute(var3, var23);
            return new RotationAngles(var24[0], var24[1]);
         }
      }

      float var25 = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
      return new RotationAngles(var1.instance + var25 * handle(14.0F, 26.0F), MathHelper.clamp(var1.data + handle(-9.0F, 9.0F), -90.0F, 90.0F));
   }

   private static Vec3d handle(LivingEntity var0, Vec3d var1, float var2, int var3, int var4) {
      Box var5 = var0.getBoundingBox();
      double var6 = Math.min(var5.maxX - var5.minX, var5.maxZ - var5.minZ);
      double var8 = var5.maxY - var5.minY;
      double var10 = Math.min(0.1, var6 * 0.25);
      double var12 = var5.minX + var10;
      double var14 = var5.maxX - var10;
      double var16 = var5.minZ + var10;
      double var18 = var5.maxZ - var10;
      double var20 = var5.minY + Math.min(0.15, var8 * 0.15);
      double var22 = var5.maxY - Math.min(0.15, var8 * 0.12);
      if (var22 < var20) {
         var22 = var20;
      }

      Vec3d var24 = new Vec3d(MathHelper.clamp(var1.x, var12, var14), MathHelper.clamp(var1.y, var20, var22), MathHelper.clamp(var1.z, var16, var18));
      Vec3d var25 = var24.subtract(var1);
      double var26 = Math.hypot(var25.x, var25.z);
      Vec3d var28 = var26 < 1.0E-4 ? new Vec3d(1.0, 0.0, 0.0) : new Vec3d(-var25.z / var26, 0.0, var25.x / var26);
      double var29 = var3 * 1.37 + var4 * 0.07;
      double var31 = Math.sin(var29) * 0.6 + Math.sin(var29 * 0.37 + 1.9) * 0.3;
      double var33 = Math.cos(var29 * 0.63 + 0.7) * 0.55 + Math.sin(var29 * 0.23 + 2.6) * 0.25;
      double var35 = (var14 - var12) * 0.5;
      double var37 = (var22 - var20) * 0.5;
      Vec3d var39 = var24.add(var28.multiply(var31 * var35 * 0.6F)).add(0.0, var33 * var37 * 0.6 * 0.6F, 0.0);
      var39 = new Vec3d(MathHelper.clamp(var39.x, var12, var14), MathHelper.clamp(var39.y, var20, var22), MathHelper.clamp(var39.z, var16, var18));
      return !(var1.distanceTo(var39) > var2 - 0.06) && handle(var1, var39) ? var39 : var24;
   }

   private static boolean handle(Vec3d var0, Vec3d var1) {
      if (toggleState.world == null) {
         return true;
      }

      try {
         HitResult var2 = PlayerRaycast.handle(var0, var1, ShapeType.COLLIDER, FluidHandling.NONE);
         return var2 == null || var2.getType() != Type.BLOCK;
      } catch (Throwable var3) {
         return true;
      }
   }

   private static Vec3d process(Vec3d var0, Vec3d var1) {
      if (var0 == null) {
         return var1;
      }

      Vec3d var2 = var1.subtract(var0);
      double var3 = var2.length();
      if (var3 < 0.04) {
         return var1;
      }

      double var5 = Math.max(0.18, var3 * 0.58);
      return var0.add(var2.normalize().multiply(Math.min(var3, var5)));
   }

   private static float[] compute(Vec3d var0, Vec3d var1) {
      Vec3d var2 = var1.subtract(var0);
      double var3 = Math.hypot(var2.x, var2.z);
      return var3 < 1.0E-6 && Math.abs(var2.y) < 1.0E-6
         ? new float[]{toggleState.player.getYaw(), toggleState.player.getPitch()}
         : new float[]{
            MathHelper.wrapDegrees((float)(Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0)),
            (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var2.y, var3)), -90.0, 90.0)
         };
   }

   private static float update(long var0) {
      double var2 = (var0 + summary) / 50.0;
      return (float)((Math.sin(var2 * 0.31) * 0.5 + Math.sin(var2 * 0.73 + 1.1) * 0.3 + Math.sin(var2 * 1.7 + 2.6) * 0.2) * 12.0) / 4.0F;
   }

   static float apply() {
      return handle(220.0F, 340.0F);
   }

   private static float handle(float var0, float var1) {
      return var1 <= var0 ? var0 : (float)ThreadLocalRandom.current().nextDouble(var0, var1);
   }

   static final class ColorState {
      AttackAimSequencer.Mode instance = AttackAimSequencer.Mode.HOLD;
      boolean data;
      int context = Integer.MIN_VALUE;
      int config;
      int state;
      long cache;
      float output = 220.0F;
      long current;
      Vec3d active;
      Vec3d mode;
      float selection;
      float enabled;
      RotationAngles renderer;
      RotationAngles handler;
      long animationDraw;
      long pointEncode;
      long animator;
      float source = 1.0F;
      long target;
      float pending = 1.0F;
      float previous = 60.0F;
      float latest = -1.0F;
      long summary;
      long matrixBlend;

      void handle() {
         this.context = Integer.MIN_VALUE;
         this.instance = AttackAimSequencer.Mode.HOLD;
         this.state = 0;
         this.current = 0L;
         this.active = null;
         this.mode = null;
         this.renderer = null;
         this.handler = null;
      }

      void process() {
         this.data = false;
         this.handle();
         this.config = 0;
         this.output = AttackAimSequencer.apply();
         this.animator = 0L;
         this.source = 1.0F;
         this.target = 0L;
         this.pending = 1.0F;
         this.latest = -1.0F;
         this.summary = 0L;
         this.matrixBlend = 0L;
      }
   }

   enum Mode {
      HOLD,
      AIM,
      FLICK;
   }
}
