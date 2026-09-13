package ru.wild.modules.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.hit.HitResult.Type;
import org.lwjgl.glfw.GLFW;
import org.wild.mixin.acceser.MinecraftClientAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.math.ResettableTimer;

@ModuleRegister(name = "TapeMouse", description = "Кто ваще это юзает ?-?", category = ModuleCategory.Misc)
public class TapeMouse extends Module {
   private final ModeSetting source = new ModeSetting("Кнопка", "ЛКМ", "ЛКМ", "ПКМ", "Обе");
   private final ModeSetting target = new ModeSetting("Режим ударов", "По кулдауну", "По кулдауну", "По задержке", "CPS");
   private final NumberSetting pending = new NumberSetting("Задержка", 1000.0F, 100.0F, 5000.0F, 100.0F, false)
      .handle(() -> !this.target.process("По задержке"));
   private final NumberSetting previous = new NumberSetting("CPS минимум", 8.0F, 1.0F, 20.0F, 1.0F, false).handle(() -> !this.target.process("CPS"));
   private final NumberSetting latest = new NumberSetting("CPS максимум", 12.0F, 1.0F, 20.0F, 1.0F, false).handle(() -> !this.target.process("CPS"));
   private final BooleanSetting summary = new BooleanSetting("Проверка на энтити", false);
   private final BooleanSetting matrixBlend = new BooleanSetting("Только при зажатии", false);
   private final ResettableTimer vectorMatch = new ResettableTimer();
   private final ResettableTimer itemProject = new ResettableTimer();
   private long responseCompute;
   private long providerFetch;

   public TapeMouse() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary, this.matrixBlend);
   }

   @Override
   public void handle() {
      super.handle();
      this.tick();
   }

   @Override
   public void toggle() {
      super.toggle();
      this.tick();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.currentScreen == null) {
         if (this.source.process("ЛКМ") || this.source.process("Обе")) {
            this.compute(true);
         }

         if (this.source.process("ПКМ") || this.source.process("Обе")) {
            this.compute(false);
         }
      }
   }

   private void compute(boolean var1) {
      if (!var1 || !this.summary.compute() || this.refresh()) {
         if (!this.matrixBlend.compute() || this.handle(var1 ? 0 : 1)) {
            ResettableTimer var2 = var1 ? this.vectorMatch : this.itemProject;
            if (this.target.process("По кулдауну")) {
               if (this.resolve(var1)) {
                  this.update(var1);
               }
            } else if (this.target.process("По задержке")) {
               if (var2.handle(this.pending.compute())) {
                  this.update(var1);
                  var2.handle();
               }
            } else {
               long var3 = var1 ? this.responseCompute : this.providerFetch;
               if (var2.handle((double)var3)) {
                  this.update(var1);
                  var2.handle();
                  long var5 = this.render();
                  if (var1) {
                     this.responseCompute = var5;
                  } else {
                     this.providerFetch = var5;
                  }
               }
            }
         }
      }
   }

   private boolean resolve(boolean var1) {
      return var1 ? Module.client.player.getAttackCooldownProgress(0.0F) >= 1.0F : ((MinecraftClientAccessor)Module.client).getItemUseCooldown() <= 0;
   }

   private void update(boolean var1) {
      MinecraftClientAccessor var2 = (MinecraftClientAccessor)Module.client;
      if (var1) {
         var2.invokeDoAttack();
      } else {
         var2.invokeDoItemUse();
         if (this.target.process("По кулдауну")) {
            var2.setItemUseCooldown(4);
         }
      }
   }

   private boolean refresh() {
      return Module.client.crosshairTarget != null && Module.client.crosshairTarget.getType() == Type.ENTITY;
   }

   private boolean handle(int var1) {
      return Module.client.getWindow() == null ? false : GLFW.glfwGetMouseButton(Module.client.getWindow().getHandle(), var1) == 1;
   }

   private long render() {
      float var1 = Math.min(this.previous.compute(), this.latest.compute());
      float var2 = Math.max(this.previous.compute(), this.latest.compute());
      double var3 = var1 >= var2 ? var1 : var1 + ThreadLocalRandom.current().nextDouble() * (var2 - var1);
      if (var3 < 0.1) {
         var3 = 0.1;
      }

      return (long)(1000.0 / var3);
   }

   private void tick() {
      this.vectorMatch.handle();
      this.itemProject.handle();
      this.responseCompute = this.render();
      this.providerFetch = this.render();
   }
}
