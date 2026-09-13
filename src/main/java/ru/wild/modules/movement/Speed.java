package ru.wild.modules.movement;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.player.LocalhostHelper;
import ru.wild.util.player.MovementPhysics;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(
   name = "Speed",
   description = "Ускоряет вашего персонажа",
   category = ModuleCategory.Movement,
   flags = {ModuleFlag.RISKY, ModuleFlag.MATRIX, ModuleFlag.GRIM}
)
public class Speed extends Module {
   public static ModeSetting source = new ModeSetting("Режим", "Vanilla", "Vanilla", "ST duel", "HW", "Ares-Entity", "Grim-Entity", "TargetStrafe");
   public static NumberSetting target = new NumberSetting("Пиковая скорость (BPS)", 7.0F, 3.0F, 15.0F, 1.0F, false);
   public static NumberSetting pending = new NumberSetting("Сила ускорения", 0.8F, 0.1F, 2.0F, 0.1F, false);
   public static NumberSetting previous = new NumberSetting("Радиус стрейфа", 2.0F, 0.5F, 5.0F, 0.1F, false).handle(() -> !source.process("TargetStrafe"));
   public static int latest = 1;

   public Speed() {
      this.handle(source, target, pending, previous);
   }

   @Override
   public void process() {
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!LocalhostHelper.handle() && Module.client.player != null && Module.client.world != null) {
         if (Module.client.player.horizontalCollision) {
            latest = -latest;
         }

         switch (source.compute()) {
            case "Vanilla":
               MovementPhysics.process(0.42);
               break;
            case "ST duel":
               this.handle(0.16);
               break;
            case "HW":
               this.handle(0.1);
               break;
            case "Grim-Entity":
               this.refresh();
               break;
            case "Ares-Entity":
               this.render();
               break;
            case "TargetStrafe":
               this.tick();
         }
      }
   }

   private void handle(double var1) {
      if (!Module.client.player.isOnGround()) {
         Box var3 = Module.client.player.getBoundingBox().expand(var1);
         List<Entity> var4 = Module.client.world.getOtherEntities(Module.client.player, var3);
         int var5 = 0;
         int var6 = 0;

         for (Entity var8 : var4) {
            if (var8 instanceof ArmorStandEntity) {
               var5++;
            } else if (var8 instanceof LivingEntity) {
               var6++;
            }

            if (var5 > 1 || var6 > 1) {
               this.drawAnimation();
               return;
            }
         }
      }
   }

   private void refresh() {
      double var1 = 6.0E-4F;
      Entity var3 = null;
      double var4 = Double.MAX_VALUE;
      double var6 = 0.2F;

      for (Entity var9 : Module.client.world.getEntities()) {
         if (var9 != Module.client.player && var9 instanceof PlayerEntity && var9 == AttackAura.textureRun) {
            double var10 = var9.getX() - Module.client.player.getX();
            double var12 = var9.getZ() - Module.client.player.getZ();
            double var14 = var10 * var10 + var12 * var12;
            if (var14 <= var6 && var14 < var4) {
               var4 = var14;
               var3 = var9;
            }
         }
      }

      if (var3 != null) {
         double[] var16 = this.handle(Module.client.player.getPos(), var3.getPos(), var1);
         Module.client.player.addVelocity(var16[0], 0.0, var16[1]);
         Module.client.player.velocityModified = true;
      }
   }

   private void render() {
      Entity var1 = null;
      double var2 = Double.MAX_VALUE;
      double var4 = 2.25;

      for (Entity var7 : Module.client.world.getEntities()) {
         if (var7 != Module.client.player && var7 instanceof PlayerEntity) {
            double var8 = var7.getX() - Module.client.player.getX();
            double var10 = var7.getZ() - Module.client.player.getZ();
            double var12 = var8 * var8 + var10 * var10;
            if (var12 <= var4 && var12 < var2) {
               var2 = var12;
               var1 = var7;
            }
         }
      }

      if (var1 != null && !Module.client.player.isOnGround()) {
         this.drawAnimation();
      }
   }

   private void tick() {
      LivingEntity var1 = AttackAura.textureRun;
      if (var1 != null && !Module.client.player.isOnGround()) {
         Entity var2 = null;
         double var3 = Double.MAX_VALUE;
         double var5 = 2.25;

         for (Entity var8 : Module.client.world.getEntities()) {
            if (var8 != Module.client.player && var8 instanceof PlayerEntity) {
               double var9 = var8.getX() - Module.client.player.getX();
               double var11 = var8.getZ() - Module.client.player.getZ();
               double var13 = var9 * var9 + var11 * var11;
               if (var13 <= var5 && var13 < var3) {
                  var3 = var13;
                  var2 = var8;
               }
            }
         }

         Vec3d var33 = Module.client.player.getVelocity();
         double var34 = Math.sqrt(var33.x * var33.x + var33.z * var33.z);
         if (var2 != null) {
            double var10 = 1.0 + pending.compute() / 10.0;
            var34 *= var10;
         }

         double var35 = target.compute() / 20.0;
         if (var34 > var35) {
            var34 = var35;
         }

         if (var34 < 0.15) {
            var34 = 0.15;
         }

         double var12 = previous.compute();
         double var14 = Module.client.player.distanceTo(var1);
         double var16 = 0.0;
         double var18 = latest;
         if (var14 > var12 + 0.5) {
            var16 = 1.0;
         } else if (var14 < var12 - 0.5) {
            var16 = -1.0;
         }

         double var20 = var1.getX() - Module.client.player.getX();
         double var22 = var1.getZ() - Module.client.player.getZ();
         float var24 = (float)(Math.toDegrees(Math.atan2(var22, var20)) - 90.0);
         if (var16 != 0.0) {
            if (var18 > 0.0) {
               var24 += var16 > 0.0 ? -45 : 45;
            } else if (var18 < 0.0) {
               var24 += var16 > 0.0 ? 45 : -45;
            }

            var18 = 0.0;
            var16 = var16 > 0.0 ? 1.0 : -1.0;
         }

         double var25 = Math.sin(Math.toRadians(var24 + 90.0F));
         double var27 = Math.cos(Math.toRadians(var24 + 90.0F));
         double var29 = var16 * var34 * var27 + var18 * var34 * var25;
         double var31 = var16 * var34 * var25 - var18 * var34 * var27;
         Module.client.player.setVelocity(var29, var33.y, var31);
         Module.client.player.velocityModified = true;
      }
   }

   private void drawAnimation() {
      Vec3d var1 = Module.client.player.getVelocity();
      double var2 = 1.0 + pending.compute() / 10.0;
      double var4 = target.compute() / 20.0;
      double var6 = var1.x;
      double var8 = var1.z;
      double var10 = var6 * var2;
      double var12 = var8 * var2;
      double var14 = Math.sqrt(var10 * var10 + var12 * var12);
      if (var14 > var4) {
         double var16 = var4 / var14;
         var10 *= var16;
         var12 *= var16;
      }

      double var20 = var10 - var6;
      double var18 = var12 - var8;
      Module.client.player.addVelocity(var20, 0.0, var18);
      Module.client.player.velocityModified = true;
   }

   private double[] handle(Vec3d var1, Vec3d var2, double var3) {
      double var5 = var2.x - var1.x;
      double var7 = var2.z - var1.z;
      double var9 = Math.sqrt(var5 * var5 + var7 * var7);
      return var9 == 0.0 ? new double[]{0.0, 0.0} : new double[]{var5 / var9 * var3, var7 / var9 * var3};
   }
}
