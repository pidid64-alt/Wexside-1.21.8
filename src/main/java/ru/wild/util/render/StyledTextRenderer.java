package ru.wild.util.render;

import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.MultiSelectSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.gui.screen.ModernClickGuiState;

public final class StyledTextRenderer {
   public void handle(ModernClickGuiState var1, Setting var2, float var3, float var4, float var5) {
      var1.check(false);
      var1.handle((StringSetting)null);
      if (var2 instanceof BooleanSetting var6) {
         var6.process(!var6.resolve());
         var1.scheduleAnimation();
      } else if (var2 instanceof NumberSetting var7) {
         var1.handle(var7);
         var1.checkFrame(var4);
         var1.collectModule(var5);
         this.handle(var7, var3, var4, var5);
         var1.scheduleAnimation();
      } else if (var2 instanceof ColorSetting var8) {
         var1.handle(var8);
      } else if (var2 instanceof ModeSetting var9) {
         var1.handle(var9);
      } else if (var2 instanceof ShaderPresetSetting var10) {
         var1.handle(var10);
      } else if (var2 instanceof MultiSelectSetting var11) {
         var11.compute();
         if (!var11.config.isEmpty()) {
            this.handle(var11);
            var1.scheduleAnimation();
         }
      } else if (var2 instanceof KeybindSetting var12) {
         var1.handle(var12);
      } else if (var2 instanceof StringSetting var13) {
         var1.handle(var13);
      } else if (var2 instanceof ActionSetting var14) {
         var14.resolve();
      }
   }

   public void handle(ModernClickGuiState var1, ColorSetting var2, float var3, float var4) {
      var1.submit(true);
      var1.unload(false);
      var1.fetch(false);
      this.compute(var1, var2, var3, var4);
   }

   public void process(ModernClickGuiState var1, ColorSetting var2, float var3, float var4) {
      var1.unload(true);
      var1.submit(false);
      var1.fetch(false);
      this.compute(var1, var2, var4);
   }

   public void handle(ModernClickGuiState var1, ColorSetting var2, float var3) {
      var1.fetch(true);
      var1.submit(false);
      var1.unload(false);
      this.resolve(var1, var2, var3);
   }

   public void process(ModernClickGuiState var1, ColorSetting var2, float var3) {
      float var4 = var1.applyEffect();
      float var5 = var1.advanceSession();
      if (!(var5 < 1.0F)) {
         byte var6 = 5;
         int var7 = Math.max(0, Math.min(var6 - 1, (int)((var3 - var4) / var5 * var6)));
         float[] var8 = new float[]{0.0F, 180.0F, -30.0F, 30.0F, 120.0F};
         var2.handle(var2.update() + var8[var7]);
         if (var2.renderer < 0.05F) {
            var2.renderer = 0.65F;
         }

         if (var2.handler < 0.08F) {
            var2.handler = 0.85F;
         }

         var1.scheduleAnimation();
      }
   }

   public void handle(ModernClickGuiState var1, ColorSetting var2, float var3, boolean var4) {
      float var5 = var1.performPlayer();
      float var6 = var1.sendPoint();
      if (!(var6 < 1.0F)) {
         byte var7 = 9;
         int var8 = Math.max(0, Math.min(var7 - 1, (int)((var3 - var5) / var6 * var7)));
         if (var8 == 8) {
            if (!var4) {
               var2.execute();
               var1.scheduleAnimation();
            }
         } else {
            if (var4) {
               var2.resolve(var8);
            } else {
               var2.compute(var8);
            }

            var1.scheduleAnimation();
         }
      }
   }

   public void handle(ModernClickGuiState var1, float var2) {
      if (var1.encodeSession() != null) {
         this.handle(var1.encodeSession(), var2, var1.alignRegion(), var1.clampResource());
      }
   }

   public void handle(ModernClickGuiState var1, float var2, float var3) {
      if (var1.projectPlayer() && var1.evaluateWorld() != null) {
         this.compute(var1, var1.evaluateWorld(), var2, var3);
      }

      if (var1.matchPlayer() && var1.evaluateWorld() != null) {
         this.compute(var1, var1.evaluateWorld(), var3);
      }

      if (var1.closeCache() && var1.evaluateWorld() != null) {
         this.resolve(var1, var1.evaluateWorld(), var2);
      }
   }

   public void handle(ModernClickGuiState var1) {
      if (var1.projectPlayer() || var1.matchPlayer() || var1.closeCache()) {
         var1.submit(false);
         var1.unload(false);
         var1.fetch(false);
         var1.scheduleAnimation();
      }
   }

   private void compute(ModernClickGuiState var1, ColorSetting var2, float var3, float var4) {
      float var5 = var1.setupScale();
      float var6 = var1.synchronizeIndex();
      float var7 = var1.interpolateTask();
      float var8 = var1.refreshSource();
      if (!(var7 < 1.0F) && !(var8 < 1.0F)) {
         float var9 = Math.max(0.0F, Math.min(1.0F, (var3 - var5) / var7));
         float var10 = Math.max(0.0F, Math.min(1.0F, (var4 - var6) / var8));
         var2.renderer = var9;
         var2.handler = 1.0F - var10;
      }
   }

   private void compute(ModernClickGuiState var1, ColorSetting var2, float var3) {
      float var4 = var1.runRequest();
      float var5 = var1.releaseData();
      if (!(var5 < 1.0F)) {
         float var6 = Math.max(0.0F, Math.min(1.0F, (var3 - var4) / var5));
         var2.handle(var6 * 360.0F);
      }
   }

   private void resolve(ModernClickGuiState var1, ColorSetting var2, float var3) {
      float var4 = var1.runVector();
      float var5 = var1.processPath();
      if (!(var5 < 1.0F)) {
         var2.process((var3 - var4) / var5);
      }
   }

   private void handle(NumberSetting var1, float var2, float var3, float var4) {
      float var5 = Math.max(0.0F, Math.min(1.0F, (var2 - var3) / Math.max(1.0F, var4)));
      float var6 = var1.state + (var1.cache - var1.state) * var5;
      if (var1.output > 0.0F) {
         var6 = Math.round(var6 / var1.output) * var1.output;
      }

      var1.config = Math.max(var1.state, Math.min(var1.cache, var6));
   }

   private void handle(MultiSelectSetting var1) {
      var1.compute();
      if (!var1.config.isEmpty()) {
         String var2 = var1.config.get(0);
         if (!var1.output.isEmpty()) {
            int var3 = var1.config.indexOf(var1.output.get(var1.output.size() - 1));
            var2 = var1.config.get((var3 + 1 + var1.config.size()) % var1.config.size());
         }

         var1.output.clear();
         var1.output.add(var2);
      }
   }
}
