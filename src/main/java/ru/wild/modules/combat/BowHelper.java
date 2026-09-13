package ru.wild.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.core.manager.FriendManager;

@ModuleRegister(name = "BowHelper", description = "Плавная наводка без тряски", category = ModuleCategory.Combat)
public class BowHelper extends Module {
   public NumberSetting source = new NumberSetting("Дистанция", 30.0F, 1.0F, 50.0F, 1.0F, false);
   public BooleanSetting target = new BooleanSetting("Игнор друзей", true);
   public static LivingEntity pending = null;
   private boolean previous = false;

   public BowHelper() {
      this.handle(this.source, this.target);
   }

   @Override
   public void handle() {
      super.handle();
   }

   @Override
   public void process() {
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         RotationController.instance = RotationController.Mode.IDLE;
         boolean var2 = Module.client.player.getMainHandStack().getItem() instanceof BowItem
            || Module.client.player.getOffHandStack().getItem() instanceof BowItem
            || Module.client.player.getMainHandStack().getItem() instanceof CrossbowItem
            || Module.client.player.getOffHandStack().getItem() instanceof CrossbowItem;
         if (!var2) {
            this.refresh();
         } else {
            boolean var3 = Module.client.player.isUsingItem() && Module.client.player.getActiveItem().getItem() instanceof BowItem;
            boolean var4 = Module.client.player.getMainHandStack().isOf(Items.CROSSBOW) && CrossbowItem.isCharged(Module.client.player.getMainHandStack())
               || Module.client.player.getOffHandStack().isOf(Items.CROSSBOW) && CrossbowItem.isCharged(Module.client.player.getOffHandStack());
            if (pending != null && !this.handle(pending)) {
               pending = null;
            }

            if (pending == null) {
               pending = this.tick();
            }

            if (pending != null) {
               if (!this.previous) {
                  ViewRotationCoordinator.instance = true;
                  this.previous = true;
               }

               if (var3 || var4) {
                  float var5 = this.render();
                  Vec3d var6 = Module.client.player.getEyePos();
                  Vec3d var7 = pending.getPos().add(0.0, pending.getHeight() * 0.5 + 0.1, 0.0);
                  double var8 = pending.getX() - pending.lastX;
                  double var10 = pending.getZ() - pending.lastZ;
                  double var12 = Math.sqrt(var8 * var8 + var10 * var10);
                  Vec3d var14 = var7;
                  float var15 = 0.0F;

                  for (int var16 = 0; var16 < 3; var16++) {
                     double var17 = Math.cos(Math.toRadians(var15));
                     float var19 = (float)(var5 * Math.max(var17, 0.1));
                     if (var12 > 0.01) {
                        Vec3d var20 = var7;

                        for (int var21 = 0; var21 < 25; var21++) {
                           double var22 = var20.x - var6.x;
                           double var24 = var20.z - var6.z;
                           double var26 = Math.sqrt(var22 * var22 + var24 * var24);
                           double var28 = this.handle(var26, var19);
                           var20 = new Vec3d(var7.x + var8 * var28, var7.y, var7.z + var10 * var28);
                        }

                        var14 = var20;
                     }

                     double var31 = var14.x - var6.x;
                     double var33 = var14.z - var6.z;
                     double var34 = Math.sqrt(var31 * var31 + var33 * var33);
                     double var35 = var14.y - var6.y;
                     var15 = this.handle(var34, var35, var5);
                  }

                  double var30 = var14.x - var6.x;
                  double var18 = var14.z - var6.z;
                  float var32 = (float)Math.toDegrees(Math.atan2(-var30, var18));
                  Module.client.player.setYaw(var32);
                  Module.client.player.setPitch(var15);
                  Module.client.player.headYaw = var32;
               }
            }
         }
      }
   }

   private void refresh() {
      if (this.previous) {
         Module.client.player.setYaw(ViewRotationCoordinator.context);
         Module.client.player.setPitch(ViewRotationCoordinator.config);
         Module.client.player.headYaw = ViewRotationCoordinator.context;
         ViewRotationCoordinator.instance = false;
         this.previous = false;
      }

      pending = null;
   }

   private float handle(double var1, double var3, float var5) {
      if (var1 < 0.5) {
         return 0.0F;
      }

      float var6 = -89.0F;
      float var7 = 89.0F;

      for (int var8 = 0; var8 < 60; var8++) {
         float var9 = (var6 + var7) / 2.0F;
         double var10 = this.handle(var1, var9, var5);
         if (var10 > var3) {
            var6 = var9;
         } else {
            var7 = var9;
         }
      }

      return (var6 + var7) / 2.0F;
   }

   private double handle(double var1, float var3, float var4) {
      double var5 = Math.toRadians(var3);
      double var7 = var4 * Math.cos(var5);
      double var9 = -var4 * Math.sin(var5);
      double var11 = 0.0;
      double var13 = 0.0;

      for (int var15 = 0; var15 < 500; var15++) {
         double var16 = var11;
         double var18 = var13;
         var11 += var7;
         var13 += var9;
         var7 *= 0.99;
         var9 *= 0.99;
         var9 -= 0.05;
         if (var11 >= var1) {
            double var20 = var11 - var16 > 0.001 ? (var1 - var16) / (var11 - var16) : 1.0;
            return var18 + (var13 - var18) * var20;
         }
      }

      return var13;
   }

   private double handle(double var1, float var3) {
      double var4 = var3;
      double var6 = 0.0;

      for (int var8 = 0; var8 < 500; var8++) {
         var6 += var4;
         var4 *= 0.99;
         if (var6 >= var1) {
            return var8 + 1;
         }
      }

      return 500.0;
   }

   private float render() {
      boolean var1 = Module.client.player.getMainHandStack().getItem() instanceof CrossbowItem
         || Module.client.player.getOffHandStack().getItem() instanceof CrossbowItem;
      if (var1) {
         return 3.15F;
      }

      float var2 = 1.0F;
      if (Module.client.player.isUsingItem() && Module.client.player.getActiveItem().getItem() instanceof BowItem) {
         int var3 = Module.client.player.getItemUseTime();
         float var4 = var3 / 20.0F;
         var2 = MathHelper.clamp((var4 * var4 + var4 * 2.0F) / 3.0F, 0.0F, 1.0F);
      }

      return var2 * 3.0F;
   }

   private boolean handle(LivingEntity var1) {
      if (var1 instanceof PlayerEntity var2) {
         if (!var2.isAlive()) {
            return false;
         } else {
            return var2.isInvulnerable() ? false : !(Module.client.player.distanceTo(var2) > this.source.compute());
         }
      } else {
         return false;
      }
   }

   private LivingEntity tick() {
      float var1 = this.source.compute();
      PlayerEntity var2 = null;
      double var3 = Double.MAX_VALUE;
      Vec3d var5 = Module.client.player.getEyePos();
      float var6 = Module.client.gameRenderer.getCamera().getYaw();
      float var7 = Module.client.gameRenderer.getCamera().getPitch();
      Vec3d var8 = Vec3d.fromPolar(var7, var6).normalize();

      for (Entity var10 : Module.client.world.getEntities()) {
         if (var10 instanceof PlayerEntity var11
            && var11 != Module.client.player
            && var11.isAlive()
            && !var11.isInvulnerable()
            && !var11.isCreative()
            && !(Module.client.player.distanceTo(var11) > var1)
            && (!this.target.compute() || !FriendManager.handle(var11.getName().getString()))) {
            Vec3d var12 = var11.getPos().add(0.0, var11.getHeight() * 0.5, 0.0).subtract(var5).normalize();
            double var13 = Math.acos(MathHelper.clamp(var8.dotProduct(var12), -1.0, 1.0));
            if (var13 < var3) {
               var3 = var13;
               var2 = var11;
            }
         }
      }

      return var2;
   }

   @Override
   public void toggle() {
      super.toggle();
      if (Module.client.player != null) {
         this.refresh();
      }
   }
}
