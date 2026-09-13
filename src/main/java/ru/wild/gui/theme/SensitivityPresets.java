package ru.wild.gui.theme;

import ru.wild.modules.visuals.Menu;

public enum SensitivityPresets {
   LOW("Низкое"),
   MEDIUM("Баланс"),
   HIGH("Высокое"),
   ULTRA("Ультра");

   private final String instance;

   SensitivityPresets(String var3) {
      this.instance = var3;
   }

   public String handle() {
      return this.instance;
   }

   public static String[] process() {
      SensitivityPresets[] var0 = values();
      String[] var1 = new String[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = var0[var2].instance;
      }

      return var1;
   }

   public static SensitivityPresets handle(int var0) {
      SensitivityPresets[] var1 = values();
      int var2 = Math.max(0, Math.min(var1.length - 1, var0));
      return var1[var2];
   }

   public void compute() {
      boolean var1 = this == LOW;
      boolean var2 = this == MEDIUM;
      boolean var3 = this == HIGH;
      boolean var4 = this == ULTRA;
      boolean var5 = var2 || var3 || var4;
      boolean var6 = var3 || var4;
      boolean var7 = var4;
      Menu.profileDraw.process(var5);
      Menu.vectorPerform.process(var6);
      Menu.eventAttach.process(var7);
      Menu.serverRead.process(var6);
      Menu.positionAdvance.process(var5);
      Menu.frameCheck.process(var5);
      Menu.moduleCollect.process(var6);
      Menu.providerClose.process(var7);
      Menu.presetSave.process(var6);
      Menu.windowConvert.process(var5);
      Menu.presetWrite.process(var5);
      Menu.colorMeasure.process(var5);
      Menu.animationSchedule.process(var6);
      Menu.rendererScan.process(var6);
      Menu.sourceBuild.process(var5);
      Menu.outputCollapse.process(var5);
      Menu.dataValidate.handle(var1 ? 12.0F : (var2 ? 26.0F : (var3 ? 42.0F : 58.0F)));
      Menu.scaleRender.handle(var1 ? 0.26F : (var2 ? 0.46F : (var3 ? 0.64F : 0.82F)));
      Menu.clientRefresh.handle(var1 ? 0.06F : (var2 ? 0.16F : (var3 ? 0.27F : 0.36F)));
      Menu.keyFilter.handle(var1 ? 0.18F : (var2 ? 0.26F : (var3 ? 0.36F : 0.48F)));
      Menu.requestAdapt.handle(var1 ? 1.05F : (var2 ? 1.55F : (var3 ? 2.15F : 2.8F)));
      Menu.timerMeasure.handle(var1 ? 0.18F : (var2 ? 0.46F : (var3 ? 0.72F : 1.02F)));
      Menu.vectorEncode.handle(var1 ? 0.32F : (var2 ? 0.52F : (var3 ? 0.68F : 0.82F)));
      Menu.requestReceive.handle(var1 ? 0.2F : (var2 ? 0.34F : (var3 ? 0.46F : 0.58F)));
      Menu.windowProcess.handle(var1 ? 0.48F : (var2 ? 0.56F : (var3 ? 0.62F : 0.7F)));
      Menu.packetSave.handle(var1 ? 0.32F : (var2 ? 0.48F : (var3 ? 0.62F : 0.78F)));
      Menu.playerCollect.process(var1);
      Menu.entryAnimate.process(var1 || var2);
      Menu.stateApply.process(var1);
      Menu.matrixFilter.process(var1);
   }
}
