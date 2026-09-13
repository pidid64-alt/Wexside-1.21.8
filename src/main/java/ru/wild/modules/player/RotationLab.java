package ru.wild.modules.player;

import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.text.Text;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.combat.RotationProfileRepository;
import ru.wild.gui.screen.RotationLabScreen;
import ru.wild.util.text.ChatLogger;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(
   name = "RotationLab",
   category = ModuleCategory.Player,
   description = "Тренажёр человеческих паттернов ротации",
   flags = ModuleFlag.NEW
)
public class RotationLab extends Module {
   private final StringSetting source = new StringSetting("Asset", "rotation_lab").handle(48);
   private final ActionSetting target = new ActionSetting("Delete Asset", 0).process("Delete").handle(this::load);
   private final ModeSetting pending = new ModeSetting("Mode", "Mixed", "Mixed", "Flick", "Tracking", "Micro", "Vertical", "Diagonal", "Idle", "Attack");
   private final BooleanSetting previous = new BooleanSetting("Auto Capture", true);
   private final NumberSetting latest = new NumberSetting("Target Radius", 11.0F, 5.0F, 30.0F, 1.0F, false);
   private final NumberSetting summary = new NumberSetting("Spread", 78.0F, 25.0F, 95.0F, 1.0F, false);
   private final NumberSetting matrixBlend = new NumberSetting("Targets", 80.0F, 5.0F, 500.0F, 1.0F, false);
   private RotationLabScreen vectorMatch;

   public RotationLab() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary, this.matrixBlend);
   }

   @Override
   public void handle() {
      super.handle();
      if (Module.client != null) {
         Module.client.execute(this::save);
      }
   }

   @Override
   public void process() {
      if (this.vectorMatch != null) {
         this.vectorMatch.handle();
      }

      if (this.vectorMatch != null && Module.client.currentScreen == this.vectorMatch) {
         Module.client.setScreen(null);
      }

      this.vectorMatch = null;
      super.process();
   }

   private void save() {
      if (this.enabled && Module.client.getWindow() != null) {
         this.vectorMatch = new RotationLabScreen(this);
         Module.client.setScreen(this.vectorMatch);
         if (Module.client.player != null) {
            Module.client.player.sendMessage(Text.of("RotationLab opened"), true);
         }
      }
   }

   public void handle(RotationLabScreen var1) {
      if (this.vectorMatch == var1) {
         this.vectorMatch = null;
      }

      if (this.enabled) {
         this.setEnabled(false);
      }
   }

   public String refresh() {
      return this.source.compute();
   }

   public String render() {
      return this.pending.compute();
   }

   public boolean tick() {
      return this.previous.compute();
   }

   public int drawAnimation() {
      return Math.max(5, Math.round(this.latest.compute()));
   }

   public float encodePoint() {
      return Math.max(0.25F, Math.min(0.95F, this.summary.compute() / 100.0F));
   }

   public int animate() {
      return Math.max(1, Math.round(this.matrixBlend.compute()));
   }

   public void load() {
      Path var1 = RotationProfileRepository.handle(this.refresh());

      try {
         if (this.vectorMatch != null) {
            this.vectorMatch.process();
         }

         if (Files.deleteIfExists(var1)) {
            ChatLogger.handle("[RotationLab] Deleted " + var1.getFileName());
         } else {
            ChatLogger.handle("[RotationLab] Asset not found: " + var1.getFileName());
         }
      } catch (Throwable var3) {
         ChatLogger.handle("[RotationLab] Delete failed: " + var3.getClass().getSimpleName());
      }
   }
}
