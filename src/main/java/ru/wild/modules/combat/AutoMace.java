package ru.wild.modules.combat;

import java.util.Comparator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(name = "AutoMace", description = "Автоматический идеальный удар булавой на падении", category = ModuleCategory.Combat)
public class AutoMace extends Module {
   private final NumberSetting source = new NumberSetting("Дистанция", 4.2F, 2.8F, 6.0F, 0.1F, false);
   private final NumberSetting target = new NumberSetting("Мин. падение", 3.0F, 1.5F, 24.0F, 0.5F, false);
   private final NumberSetting pending = new NumberSetting("Точка удара", 1.15F, 0.25F, 3.0F, 0.05F, false);
   private final NumberSetting previous = new NumberSetting("Кулдаун", 92.0F, 70.0F, 100.0F, 1.0F, false);
   private final BooleanSetting latest = new BooleanSetting("Только игроки", false);
   private final BooleanSetting summary = new BooleanSetting("Только булава", true);
   private long matrixBlend;

   public AutoMace() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (!this.summary.compute() || Module.client.player.getMainHandStack().isOf(Items.MACE)) {
            if (!Module.client.player.isOnGround() && !(Module.client.player.getVelocity().y >= -0.08) && !(Module.client.player.fallDistance < this.target.compute())) {
               if (!(Module.client.player.getAttackCooldownProgress(0.0F) * 100.0F < this.previous.compute())) {
                  AutoMace.DataRecord var2 = this.render();
                  if (var2 != null && !(var2.heightToGround > this.pending.compute()) && var2.ticksToGround <= 4) {
                     LivingEntity var3 = this.refresh();
                     if (var3 != null && this.process(var3)) {
                        long var4 = System.currentTimeMillis();
                        if (var4 - this.matrixBlend >= 250L) {
                           Module.client.interactionManager.attackEntity(Module.client.player, var3);
                           Module.client.player.swingHand(Hand.MAIN_HAND);
                           this.matrixBlend = var4;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private LivingEntity refresh() {
      double var1 = this.source.compute();
      Box var3 = Module.client.player.getBoundingBox().expand(var1);
      return Module.client.world
         .getEntitiesByClass(LivingEntity.class, var3, this::handle)
         .stream()
         .min(Comparator.comparingDouble(var0 -> Module.client.player.squaredDistanceTo(var0)))
         .orElse(null);
   }

   private boolean handle(LivingEntity var1) {
      if (var1 == Module.client.player || !var1.isAlive() || var1.isSpectator() || var1 instanceof ArmorStandEntity) {
         return false;
      }

      if (this.latest.compute() && !(var1 instanceof PlayerEntity)) {
         return false;
      }

      double var2 = this.source.compute();
      return Module.client.player.squaredDistanceTo(var1) <= var2 * var2;
   }

   private boolean process(LivingEntity var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      Vec3d var3 = var1.getBoundingBox().getCenter();
      BlockHitResult var4 = Module.client.world.raycast(new RaycastContext(var2, var3, ShapeType.COLLIDER, FluidHandling.NONE, Module.client.player));
      return var4.getType() == Type.MISS;
   }

   private AutoMace.DataRecord render() {
      Vec3d var1 = Module.client.player.getPos();
      Vec3d var2 = Module.client.player.getVelocity();
      double var3 = var1.y;

      for (int var5 = 0; var5 < 20; var5++) {
         double var6 = var3 + var2.y;
         Vec3d var8 = new Vec3d(var1.x, var3, var1.z);
         Vec3d var9 = new Vec3d(var1.x, var6, var1.z);
         BlockHitResult var10 = Module.client.world.raycast(new RaycastContext(var8, var9, ShapeType.COLLIDER, FluidHandling.NONE, Module.client.player));
         if (var10.getType() == Type.BLOCK) {
            double var11 = Math.max(0.0, Module.client.player.getY() - var10.getPos().y);
            return new AutoMace.DataRecord(var5 + 1, var11);
         }

         var3 = var6;
         var2 = var2.multiply(0.98, 0.98, 0.98).subtract(0.0, 0.08, 0.0);
      }

      Vec3d var13 = Module.client.player.getPos();
      Vec3d var14 = var13.subtract(0.0, 32.0, 0.0);
      BlockHitResult var7 = Module.client.world.raycast(new RaycastContext(var13, var14, ShapeType.COLLIDER, FluidHandling.NONE, Module.client.player));
      return var7.getType() != Type.BLOCK ? null : new AutoMace.DataRecord(20, Math.max(0.0, Module.client.player.getY() - var7.getPos().y));
   }

   record DataRecord(int ticksToGround, double heightToGround) {
   }
}
