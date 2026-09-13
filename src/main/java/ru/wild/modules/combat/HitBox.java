package ru.wild.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.combat.AdaptiveAttackTiming;
import ru.wild.automation.combat.RotationEngine;
import ru.wild.core.manager.FriendManager;

@ModuleRegister(name = "HitBox", description = "Увеличивает хитбокс таргета", category = ModuleCategory.Combat, flags = ModuleFlag.RISKY)
public class HitBox extends Module {
   public static ModeSetting source = new ModeSetting("Режим", "Обычный", "Легит", "Обычный");
   public static NumberSetting target = new NumberSetting("Размер", 0.2F, 0.0F, 5.0F, 0.1F, false);
   public static BooleanSetting pending = new BooleanSetting("Игнор друзей", true);
   public static ModeSetting previous = new ModeSetting("Режим снапа", "Fast", "Fast", "Smooth", "Random").handle(() -> !source.process("Легит"));
   public static LivingEntity latest = null;
   public static int summary = 0;

   public HitBox() {
      this.handle(source, target, pending, previous);
   }

   public static void handle(LivingEntity var0) {
      latest = var0;
      summary = 0;
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (!source.process("Легит")) {
            latest = null;
         } else {
            if (latest != null) {
               if (!latest.isAlive() || Module.client.player.distanceTo(latest) > 3.0F) {
                  latest = null;
                  return;
               }

               RotationEngine.handle(latest, true, previous.compute());
               summary++;
               if (summary >= 2 && AdaptiveAttackTiming.handle(latest, 3.0, false)) {
                  Module.client.interactionManager.attackEntity(Module.client.player, latest);
                  Module.client.player.swingHand(Hand.MAIN_HAND);
                  latest = null;
                  summary = 0;
               } else if (summary >= 6) {
                  latest = null;
                  summary = 0;
               }
            }
         }
      }
   }

   public LivingEntity refresh() {
      LivingEntity var1 = null;
      double var2 = Double.MAX_VALUE;
      Vec3d var4 = Module.client.player.getEyePos();
      Vec3d var5 = Module.client.player.getRotationVec(1.0F).normalize();

      for (Entity var7 : Module.client.world.getEntities()) {
         if (var7 instanceof LivingEntity var8
            && var8 != Module.client.player
            && var8.isAlive()
            && (!(pending.compute() && var8 instanceof PlayerEntity var9) || !FriendManager.handle(var9.getName().getString()))
            && !(Module.client.player.distanceTo(var8) > 3.0F)) {
            Vec3d var17 = var8.getPos().add(0.0, var8.getHeight() / 2.0, 0.0);
            Vec3d var10 = var17.subtract(var4).normalize();
            double var11 = MathHelper.clamp(var5.dotProduct(var10), -1.0, 1.0);
            double var13 = Math.toDegrees(Math.acos(var11));
            double var15 = target.compute() * 30.0;
            if (var13 <= var15 && var13 < var2) {
               var2 = var13;
               var1 = var8;
            }
         }
      }

      return var1;
   }
}
