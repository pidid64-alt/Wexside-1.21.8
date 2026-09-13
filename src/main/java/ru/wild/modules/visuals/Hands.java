package ru.wild.modules.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.core.GlowEspRenderer;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.HandFramebufferCapture;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.render.IrisCompatibility;

@ModuleRegister(name = "Hands", description = "Свечение и настройка предметов в руках", category = ModuleCategory.Visuals)
public final class Hands extends Module {
   public final ChoiceSetting source = new ChoiceSetting("Руки", new BooleanSetting("Правая", true), new BooleanSetting("Левая", true));
   public final ModeSetting target = new ModeSetting("Эффект", "Свечение + контур", "Свечение + контур", "Свечение", "Контур");
   public final NumberSetting pending = new NumberSetting("Радиус", 8.0F, 2.0F, 24.0F, 1.0F, false).handle(this::tick);
   public final NumberSetting previous = new NumberSetting("Сила свечения", 1.8F, 0.25F, 5.0F, 0.05F, false).handle(this::tick);
   public final NumberSetting latest = new NumberSetting("Толщина контура", 1.5F, 0.5F, 6.0F, 0.5F, false).handle(this::render);
   public final NumberSetting summary = new NumberSetting("Прозрачность", 0.9F, 0.05F, 1.0F, 0.01F, true);
   public final ModeSetting matrixBlend = new ModeSetting("Источник цвета", "Предмет", "Предмет", "Тема", "Свой");
   public final ModeSetting vectorMatch = new ModeSetting("Отображение цвета", "Градиент", "Градиент", "Статичный");
   public final ColorSetting itemProject = new ColorSetting("Основной цвет", 55.0F, 0.72F, 1.0F).process(() -> !this.matrixBlend.process("Свой"));
   public final ColorSetting responseCompute = new ColorSetting("Второй цвет", 76.0F, 0.78F, 1.0F)
      .process(() -> !this.matrixBlend.process("Свой") || this.vectorMatch.process("Статичный"));
   private GlowEspRenderer providerFetch;
   private static final Hand[] profileDraw = Hand.values();
   private final float[] vectorPerform = new float[3];
   private final float[] eventAttach = new float[3];

   public Hands() {
      this.handle(
         this.source,
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute
      );
   }

   @Override
   public void process() {
      this.animate();
      super.process();
   }

   public boolean handle(Hand var1) {
      if (this.enabled && var1 != null && Module.client.player != null && !this.encodePoint() && this.drawAnimation()) {
         Arm var2 = var1 == Hand.MAIN_HAND ? Module.client.player.getMainArm() : handle(Module.client.player.getMainArm());
         return this.source.process(var2 == Arm.RIGHT ? "Правая" : "Левая");
      } else {
         return false;
      }
   }

   @EventHandler(handle = 0)
   public void handle(HudRenderContext var1) {
      if (!IrisCompatibility.handle() && var1 != null && var1.resolve() != null) {
         var1.resolve().compute();
         this.handle(var1.apply(), var1.execute());
         var1.resolve().compute();
      }
   }

   public void refresh() {
      if (IrisCompatibility.handle() && Module.client.getWindow() != null) {
         this.handle(Module.client.getWindow().getFramebufferWidth(), Module.client.getWindow().getFramebufferHeight());
      }
   }

   private void handle(int var1, int var2) {
      if (this.enabled
         && Module.client.world != null
         && Module.client.player != null
         && var1 > 0
         && var2 > 0
         && Module.client.getWindow() != null
         && !Module.client.getWindow().hasZeroWidthOrHeight()) {
         HandFramebufferCapture var3 = HandFramebufferCapture.handle();
         if (this.encodePoint()) {
            var3.handle(false, false, var1, var2);
         } else if (!this.drawAnimation()) {
            var3.handle(false, false, var1, var2);
         } else {
            boolean var4 = false;

            for (Hand var8 : profileDraw) {
               if (this.handle(var8) && var3.process(var8)) {
                  int var9 = var3.compute(var8);
                  if (var9 > 0) {
                     if (this.providerFetch == null) {
                        this.providerFetch = new GlowEspRenderer();
                     }

                     if (!var4) {
                        this.handle(this.vectorPerform, this.eventAttach);
                        var4 = true;
                     }

                     int var10 = var3.update(var8);
                     this.providerFetch
                        .handle(
                           var9,
                           var3.resolve(var8),
                           var10 > 0 ? var10 : var9,
                           var1,
                           var2,
                           new GlowEspRenderer.ColorStop(
                              this.pending.compute() * 2.0F,
                              this.latest.compute(),
                              this.tick() ? 0.0F : this.previous.compute() * 2.0F,
                              this.render() ? 0.0F : 1.35F,
                              this.summary.compute(),
                              0,
                              this.vectorMatch.process("Статичный") ? 1 : 0,
                              this.matrixBlend.process("Предмет") ? 1 : 0,
                              this.vectorPerform[0],
                              this.vectorPerform[1],
                              this.vectorPerform[2],
                              this.eventAttach[0],
                              this.eventAttach[1],
                              this.eventAttach[2]
                           )
                        );
                  }
               }
            }
         }
      }
   }

   private void handle(float[] var1, float[] var2) {
      if (this.matrixBlend.process("Свой")) {
         handle(this.itemProject.compute().getRGB(), var1);
         handle(this.responseCompute.compute().getRGB(), var2);
      } else {
         ThemePalette var3 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
         ThemeColors var4 = ThemeColors.handle(var3, ThemeShaderApplier.resolve());
         handle(var4.save(), var1);
         handle(var4.submit(), var2);
      }
   }

   private boolean render() {
      return this.target.process("Свечение");
   }

   private boolean tick() {
      return this.target.process("Контур");
   }

   private boolean drawAnimation() {
      return Module.client.options != null && Module.client.options.getPerspective() != null && Module.client.options.getPerspective().isFirstPerson();
   }

   private boolean encodePoint() {
      return Module.client.options != null && Module.client.options.hudHidden;
   }

   private void animate() {
      Runnable var1 = () -> {
         HandFramebufferCapture.handle().process();
         GlowEspRenderer var1x = this.providerFetch;
         this.providerFetch = null;
         if (var1x != null) {
            var1x.close();
         }
      };
      if (RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L) {
         var1.run();
      } else if (Module.client != null) {
         Module.client.execute(var1);
      }
   }

   private static Arm handle(Arm var0) {
      return var0 == Arm.RIGHT ? Arm.LEFT : Arm.RIGHT;
   }

   private static void handle(int var0, float[] var1) {
      var1[0] = (var0 >> 16 & 0xFF) / 255.0F;
      var1[1] = (var0 >> 8 & 0xFF) / 255.0F;
      var1[2] = (var0 & 0xFF) / 255.0F;
   }
}
