package ru.wild.modules.visuals;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HeldItemRenderEvent;
import ru.wild.api.event.MutableHandRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.modules.combat.AttackAura;

@ModuleRegister(name = "Swing Animation", description = "Кастомизация анимации руки", category = ModuleCategory.Visuals)
public class SwingAnimation extends Module {
   public static ModeSetting source = new ModeSetting("Анимация", "Smooth", "Smooth", "Swipe", "Swipe back", "SwipeD", "Down", "Spin", "Off");
   public static NumberSetting target = new NumberSetting("Скорость анимации", 1.0F, 0.1F, 3.0F, 0.1F, false);
   public static NumberSetting pending = new NumberSetting("Размер анимации", 3.7F, 1.0F, 10.0F, 0.1F, false).handle(() -> source.process("Off"));
   public static NumberSetting previous = new NumberSetting("Размер предмета справа", 1.0F, 0.2F, 2.5F, 0.05F, false);
   public static NumberSetting latest = new NumberSetting("Размер предмета слева", 1.0F, 0.2F, 2.5F, 0.05F, false);
   public static BooleanSetting summary = new BooleanSetting("Только Аура", false);
   public static BooleanSetting matrixBlend = new BooleanSetting("Модель Руки", false);
   public static BooleanSetting vectorMatch = new BooleanSetting("Менять обе руки", false).handle(() -> !matrixBlend.compute());
   public static NumberSetting itemProject = new NumberSetting("X", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || !vectorMatch.compute());
   public static NumberSetting responseCompute = new NumberSetting("Y", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || !vectorMatch.compute());
   public static NumberSetting providerFetch = new NumberSetting("Z", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || !vectorMatch.compute());
   public static NumberSetting profileDraw = new NumberSetting("X правая", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || vectorMatch.compute());
   public static NumberSetting vectorPerform = new NumberSetting("Y правая", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || vectorMatch.compute());
   public static NumberSetting eventAttach = new NumberSetting("Z правая", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || vectorMatch.compute());
   public static NumberSetting serverRead = new NumberSetting("X левая", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || vectorMatch.compute());
   public static NumberSetting positionAdvance = new NumberSetting("Y левая", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || vectorMatch.compute());
   public static NumberSetting frameCheck = new NumberSetting("Z левая", 0.0F, -2.0F, 2.0F, 0.01F, false)
      .handle(() -> !matrixBlend.compute() || vectorMatch.compute());

   public SwingAnimation() {
      this.handle(
         source,
         target,
         previous,
         latest,
         pending,
         summary,
         matrixBlend,
         vectorMatch,
         itemProject,
         responseCompute,
         providerFetch,
         profileDraw,
         vectorPerform,
         eventAttach,
         serverRead,
         positionAdvance,
         frameCheck
      );
   }

   @EventHandler
   public void handle(MutableHandRenderEvent var1) {
      if (this.enabled && !source.process("Off")) {
         if (refresh() && var1.resolve().equals(Hand.MAIN_HAND)) {
            String var2 = source.compute();
            if (!var2.equals("Off")) {
               if (var1.resolve().equals(Hand.MAIN_HAND)) {
                  MatrixStack var3 = var1.compute();
                  float var4 = var1.update();
                  int var5 = Module.client.player.getMainArm().equals(Arm.RIGHT) ? 1 : -1;
                  float var6 = (float)Math.sin(var4 * (Math.PI / 2) * 2.0);
                  float var7 = (float)Math.sin(var4 * (Math.PI / 2) * 2.0);
                  float var8 = (float)(Math.sin(var4 * Math.PI) * 0.5);
                  float var9 = MathHelper.sin(var4 * var4 * (float) Math.PI);
                  float var10 = MathHelper.sin(MathHelper.sqrt(var4) * (float) Math.PI);
                  switch (source.compute()) {
                     case "Swipe":
                        var3.translate(var5 * 0.67F, -0.32F, -1.0F);
                        var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * var5));
                        var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-60 * var5));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var6 * -pending.compute() * 10.0F));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                        break;
                     case "Swipe back":
                        var3.translate(var5 * 0.67F, -0.32F, -1.0F);
                        var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * var5));
                        var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-60 * var5));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var6 * pending.compute() * 10.0F));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                        break;
                     case "SwipeD":
                        var3.translate(var5 * 0.67F, -0.32F, -1.0F);
                        var3.translate(var10 * -pending.compute() / 35.0F, 0.0F, var10 * -pending.compute() / 35.0F);
                        var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25.0F));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var10 * -pending.compute() * 5.0F));
                        var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30.0F));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                        var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(50.0F));
                        break;
                     case "Down":
                        var3.translate(var5 * 0.67F, -0.32F, -1.0F);
                        var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(80 * var5));
                        var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-30 * var5));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var6 * -pending.compute() * 10.0F));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-100.0F));
                        break;
                     case "Spin":
                        var3.translate(var5 * 0.56F, -0.42F, -0.72F);
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0F + var4 * 360.0F));
                        var3.translate(0.0, -0.1, 0.0);
                        break;
                     case "Smooth":
                        var3.translate(var5 * 0.56F, -0.42F, -0.72F);
                        var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var5 * (45.0F + var6 * -pending.compute() * 3.0F)));
                        var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var5 * var7 * -pending.compute() * 2.0F));
                        var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var7 * -pending.compute() * 10.0F));
                        var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var5 * -45.0F));
                        var3.translate(0.0, -0.1, 0.0);
                  }

                  var1.process();
               }
            }
         }
      }
   }

   public static boolean refresh() {
      if (!summary.compute()) {
         return true;
      }

      AttackAura var0 = (AttackAura)WildClient.instance.data.process(AttackAura.class);
      return var0 != null && var0.enabled && AttackAura.textureRun != null;
   }

   public static float handle(Hand var0) {
      if (var0 != null && WildClient.instance != null && WildClient.instance.data != null && Module.client.player != null) {
         SwingAnimation var1 = WildClient.instance.data.handle(SwingAnimation.class);
         if (var1 != null && var1.enabled) {
            Arm var2 = var0 == Hand.MAIN_HAND ? Module.client.player.getMainArm() : (Module.client.player.getMainArm() == Arm.RIGHT ? Arm.LEFT : Arm.RIGHT);
            return var2 == Arm.RIGHT ? previous.compute() : latest.compute();
         } else {
            return 1.0F;
         }
      } else {
         return 1.0F;
      }
   }

   @EventHandler
   public void handle(HeldItemRenderEvent var1) {
      boolean var2 = var1.update();
      MatrixStack var3 = var1.compute();
      if (matrixBlend.compute() && vectorMatch.compute()) {
         if (var2) {
            var3.translate(itemProject.compute(), responseCompute.compute(), providerFetch.compute());
         } else {
            var3.translate(-itemProject.compute(), responseCompute.compute(), providerFetch.compute());
         }
      }

      if (matrixBlend.compute() && !vectorMatch.compute()) {
         if (var2) {
            var3.translate(profileDraw.compute(), vectorPerform.compute(), eventAttach.compute());
         } else {
            var3.translate(serverRead.compute(), positionAdvance.compute(), frameCheck.compute());
         }
      }
   }
}
