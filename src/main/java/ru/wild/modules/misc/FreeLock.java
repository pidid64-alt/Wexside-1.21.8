package ru.wild.modules.misc;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.Perspective;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.text.ChatLogger;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "FreeLock", category = ModuleCategory.Misc, description = "Вид от третьего лица")
public class FreeLock extends Module {
   private final ModeSetting source = new ModeSetting("Режим", "По удержанию", "По удержанию", "По бинду");
   private final KeybindSetting target = new KeybindSetting("Бинд", -1, true);
   private Perspective pending;

   public FreeLock() {
      this.handle(this.source, this.target);
   }

   public boolean refresh() {
      return this.pending != null;
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (!ServerEnvironment.handle()) {
         if (this.handle(Module.client.currentScreen)) {
            this.tick();
         } else if (var1.resolve() == this.target.compute()) {
            boolean var2 = KeybindSetting.process(this.target.compute());
            if (this.source.process("По удержанию")) {
               if (var2 && this.pending == null) {
                  this.render();
               } else if (!var2 && this.pending != null) {
                  this.tick();
               }
            } else if (this.source.process("По бинду") && var2) {
               if (this.pending != null) {
                  this.tick();
               } else {
                  this.render();
               }
            }
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (this.handle(Module.client.currentScreen)) {
         this.tick();
      }
   }

   @Override
   public void handle() {
      super.handle();
   }

   @Override
   public void process() {
      super.process();
      this.tick();
   }

   private void render() {
      if (!ServerEnvironment.handle() && !this.handle(Module.client.currentScreen)) {
         AttackAura var1 = WildClient.instance.data.handle(AttackAura.class);
         if (var1 != null && var1.enabled) {
            ChatLogger.handle("Отключите ауру для использования фрилука");
         } else if (Module.client.options != null) {
            this.pending = Module.client.options.getPerspective();
            Module.client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            ViewRotationCoordinator.context = Module.client.gameRenderer.getCamera().getYaw();
            ViewRotationCoordinator.config = Module.client.gameRenderer.getCamera().getPitch();
            ViewRotationCoordinator.data = true;
            ViewRotationCoordinator.instance = true;
         }
      }
   }

   private void tick() {
      if (this.pending != null) {
         if (Module.client.options != null) {
            Module.client.options.setPerspective(this.pending);
         }

         this.pending = null;
         ViewRotationCoordinator.data = false;
         ViewRotationCoordinator.instance = false;
      }
   }

   private boolean handle(Screen var1) {
      return var1 != null;
   }
}
