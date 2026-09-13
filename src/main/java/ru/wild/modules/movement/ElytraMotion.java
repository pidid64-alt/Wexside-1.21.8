package ru.wild.modules.movement;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.modules.combat.AttackAura;
import ru.wild.modules.combat.ElytraTarget;
import ru.wild.util.inventory.InventorySlotActions;
import ru.wild.util.math.Stopwatch;

@ModuleRegister(name = "ElytraMotion", description = "Зависает на элитрах перед противником", category = ModuleCategory.Movement)
public class ElytraMotion extends Module {
   public final NumberSetting source = new NumberSetting("Дистанция", 2.5F, 1.0F, 3.0F, 0.1F, false);
   private final BooleanSetting previous = new BooleanSetting("AutoFireworks", false);
   public boolean target;
   private final Stopwatch latest = new Stopwatch();
   public double pending = 0.0;

   public ElytraMotion() {
      this.handle(this.source, this.previous);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player == null) {
         this.target = false;
      } else if (!Module.client.player.isGliding()) {
         this.target = false;
      } else {
         AttackAura var2 = WildClient.instance.data.handle(AttackAura.class);
         ElytraTarget var3 = WildClient.instance.data.handle(ElytraTarget.class);
         if (this.handle(var2, var3)) {
            Module.client.options.forwardKey.setPressed(false);
            this.target = true;
            Module.client.player.setVelocity(0.0, 0.0, 0.0);
         } else {
            Module.client.options.forwardKey.setPressed(true);
            this.target = false;
         }

         if (this.previous.compute() && AttackAura.textureRun != null && this.latest.update(500L)) {
            int var4 = InventorySlotActions.handle(Items.FIREWORK_ROCKET);
            if (var4 != -1) {
               InventorySlotActions.handle(var4);
               Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            }

            this.latest.handle();
         }
      }
   }

   public boolean handle(AttackAura var1, ElytraTarget var2) {
      LivingEntity var3 = AttackAura.textureRun;
      if (var3 != null && Module.client.player.isGliding()) {
         double var4 = var3.getX() - var3.lastX;
         double var6 = var3.getY() - var3.lastY;
         double var8 = var3.getZ() - var3.lastZ;
         double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
         double var12 = var10 * 20.0;
         boolean var14 = var12 < 25.0;
         return var3.distanceTo(Module.client.player) < this.source.compute() + (var3.isGliding() ? 0.5F : 0.0F) && Module.client.player.isGliding() && var14;
      } else {
         return false;
      }
   }

   @Override
   public void process() {
      this.target = false;
      super.process();
   }
}
