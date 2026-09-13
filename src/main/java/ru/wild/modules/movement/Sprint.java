package ru.wild.modules.movement;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.SprintStateEvent;
import ru.wild.api.event.WorldReadyEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.combat.AdaptiveAttackTiming;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "Sprint", description = "Сложна понять че за модуль", category = ModuleCategory.Movement)
public class Sprint extends Module {
   public static ModeSetting source = new ModeSetting("Режим", "Обычный", "Обычный", "Постоянный");
   public static BooleanSetting target = new BooleanSetting("Сохранять спринт", false);
   public static NumberSetting pending = new NumberSetting("Сила сохранения", 0.6F, 0.2F, 1.0F, 0.1F, false).handle(() -> !target.compute());
   public final BooleanSetting previous = new BooleanSetting("Игнорировать голод", false);
   public static int latest = 0;

   public Sprint() {
      this.handle(source, target, pending, this.previous);
   }

   @EventHandler
   public void handle(SprintStateEvent var1) {
      if (Module.client.player != null && !Module.client.player.hasStatusEffect(StatusEffects.BLINDNESS)) {
         if (target.compute()) {
            Module.client.player
               .setVelocity(
                  Module.client.player.getVelocity().x / pending.compute(),
                  Module.client.player.getVelocity().y,
                  Module.client.player.getVelocity().z / pending.compute()
               );
            Module.client.player.setSprinting(true);
         }
      }
   }

   @EventHandler
   public void handle(WorldReadyEvent var1) {
      if (source.process("Постоянный")) {
         latest += 4;
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (Module.client.player.hasStatusEffect(StatusEffects.BLINDNESS)) {
            Module.client.player.setSprinting(false);
            Module.client.options.sprintKey.setPressed(false);
         } else if (source.process("Постоянный")) {
            this.refresh();
         } else {
            boolean var2 = Module.client.player.horizontalCollision && !Module.client.player.collidedSoftly;
            if (latest != 0) {
               Module.client.player.setSprinting(false);
               Module.client.options.sprintKey.setPressed(false);
               latest--;
            } else {
               if (AdaptiveAttackTiming.process(AttackAura.textureRun, AttackAura.process(AttackAura.textureRun))) {
                  Module.client.player.setSprinting(false);
                  Module.client.options.sprintKey.setPressed(false);
               }

               if (!Module.client.player.isSneaking() && !var2 && Module.client.options.forwardKey.isPressed()) {
                  Module.client.player.setSprinting(true);
                  Module.client.options.sprintKey.setPressed(true);
               }
            }
         }
      }
   }

   private void refresh() {
      if (!Module.client.player.isAlive()) {
         latest += 2;
      }

      if (latest != 0) {
         Module.client.player.setSprinting(false);
         Module.client.options.sprintKey.setPressed(false);
         latest--;
      } else {
         boolean var1 = AdaptiveAttackTiming.process(AttackAura.textureRun, AttackAura.process(AttackAura.textureRun));
         if (var1 && AttackAura.presetWrite.process("Тестовый")) {
            Module.client.options.sprintKey.setPressed(false);
         } else if (var1 && AttackAura.presetWrite.process("Обновленный")) {
            Module.client.player.setSprinting(false);
            Module.client.options.sprintKey.setPressed(false);
         } else {
            boolean var2 = Module.client.player.isSneaking() && !Module.client.player.isSwimming();
            if (!var2) {
               Module.client.options.sprintKey.setPressed(true);
            }
         }
      }
   }

   @Override
   public void process() {
      super.process();
      if (Module.client.player != null) {
         Module.client.player.setSprinting(false);
      }
   }

   @Override
   public void handle() {
      super.handle();
      if (this.previous.compute() && !ServerEnvironment.compute() && Module.client.player != null) {
         Module.client.player.sendMessage(Text.of("§cДанная настройка работает только на FunTime!"), true);
      }
   }
}
