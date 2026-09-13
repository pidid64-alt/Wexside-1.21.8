package ru.wild.util.text;

import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.gui.screen.ModernClickGuiState;

public final class RichTextParser {
   public boolean handle(ModernClickGuiState var1, int var2) {
      if (var1.runHandler() != null) {
         var1.process(var2);
         return true;
      } else if (var1.checkKey() != null) {
         var1.compute(var2);
         return true;
      } else if (var1.projectLayer() != null) {
         var1.resolve(var2);
         return true;
      } else if (var1.drawClient() != null) {
         this.update(var1, var2);
         return true;
      } else if (var1.writeProfile() != null) {
         this.apply(var1, var2);
         return true;
      } else if (var1.filterEntity() != null) {
         this.process(var1, var2);
         return true;
      } else if (var1.savePacket()) {
         this.resolve(var1, var2);
         return true;
      } else if (var1.receiveRequest()) {
         this.compute(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public boolean handle(ModernClickGuiState var1, char var2) {
      if (var1.drawClient() != null) {
         this.process(var1, var2);
         return true;
      }

      if (var1.writeProfile() != null) {
         this.compute(var1, var2);
         return true;
      }

      if (var1.filterEntity() != null) {
         if (!Character.isISOControl(var2)) {
            StringSetting var10000 = var1.filterEntity();
            var10000.state = var10000.state + var2;
            var1.scheduleAnimation();
         }

         return true;
      } else if (var1.savePacket()) {
         if (!Character.isISOControl(var2)) {
            var1.process(var2);
         }

         return true;
      } else if (var1.receiveRequest()) {
         if (!Character.isISOControl(var2)) {
            var1.handle(var2);
         }

         return true;
      } else {
         return false;
      }
   }

   private void process(ModernClickGuiState var1, int var2) {
      if (var2 == 256 || var2 == 257) {
         var1.handle((StringSetting)null);
      } else if (var2 == 259 && !var1.filterEntity().state.isEmpty()) {
         String var3 = var1.filterEntity().state;
         var1.filterEntity().state = var3.substring(0, var3.length() - 1);
         var1.scheduleAnimation();
      }
   }

   private void compute(ModernClickGuiState var1, int var2) {
      if (var2 == 256 || var2 == 257) {
         var1.check(false);
      } else if (var2 == 259) {
         var1.fetch();
      }
   }

   private void resolve(ModernClickGuiState var1, int var2) {
      if (var2 == 256) {
         var1.blendMatrix();
         var1.onTick(false);
      } else if (var2 == 257) {
         var1.onTick(false);
      } else if (var2 == 259) {
         var1.matchVector();
      }
   }

   private void update(ModernClickGuiState var1, int var2) {
      ColorSetting var3 = var1.drawClient();
      if (var3 != null) {
         if (var2 == 256) {
            var1.update((ColorSetting)null);
            var1.apply("");
         } else if (var2 != 257 && var2 != 258) {
            if (var2 == 259) {
               String var4 = var1.matchProvider();
               if (var4 != null && !var4.isEmpty()) {
                  var1.apply(var4.substring(0, var4.length() - 1));
               }
            }
         } else {
            this.handle(var1, var3);
            var1.update((ColorSetting)null);
            var1.apply("");
         }
      }
   }

   private void process(ModernClickGuiState var1, char var2) {
      if (handle(var2)) {
         String var3 = var1.matchProvider();
         if (var3 == null) {
            var3 = "";
         }

         if (var3.length() < 8) {
            var1.apply(var3 + Character.toUpperCase(var2));
         }
      }
   }

   private void apply(ModernClickGuiState var1, int var2) {
      ColorSetting var3 = var1.writeProfile();
      if (var3 != null) {
         if (var2 == 256) {
            var1.apply((ColorSetting)null);
            var1.execute("");
         } else if (var2 != 257 && var2 != 258) {
            if (var2 == 259) {
               String var4 = var1.matchEffect();
               if (var4 != null && !var4.isEmpty()) {
                  var1.execute(var4.substring(0, var4.length() - 1));
               }
            }
         } else {
            this.process(var1, var3);
            var1.apply((ColorSetting)null);
            var1.execute("");
         }
      }
   }

   private void compute(ModernClickGuiState var1, char var2) {
      if (var2 >= '0' && var2 <= '9') {
         String var3 = var1.matchEffect();
         if (var3 == null) {
            var3 = "";
         }

         if (var3.length() < 3) {
            var1.execute(var3 + var2);
         }
      }
   }

   private void handle(ModernClickGuiState var1, ColorSetting var2) {
      String var3 = var1.matchProvider();
      if (var3 != null) {
         String var4 = var3.trim();
         if (var4.startsWith("#")) {
            var4 = var4.substring(1);
         }

         if (!var4.isEmpty()) {
            try {
               long var5 = Long.parseUnsignedLong(var4, 16);
               int var7;
               switch (var4.length()) {
                  case 3:
                     int var14 = ((int)(var5 >> 8) & 15) * 17;
                     int var16 = ((int)(var5 >> 4) & 15) * 17;
                     int var17 = ((int)var5 & 15) * 17;
                     int var18 = Math.round(var2.animationDraw * 255.0F) & 0xFF;
                     var7 = var18 << 24 | var14 << 16 | var16 << 8 | var17;
                     break;
                  case 4:
                     int var13 = ((int)(var5 >> 12) & 15) * 17;
                     int var15 = ((int)(var5 >> 8) & 15) * 17;
                     int var10 = ((int)(var5 >> 4) & 15) * 17;
                     int var11 = ((int)var5 & 15) * 17;
                     var7 = var11 << 24 | var13 << 16 | var15 << 8 | var10;
                     break;
                  case 5:
                  case 7:
                  default:
                     return;
                  case 6:
                     int var8 = (int)var5 & 16777215;
                     int var9 = Math.round(var2.animationDraw * 255.0F) & 0xFF;
                     var7 = var9 << 24 | var8;
                     break;
                  case 8:
                     var7 = (int)var5;
               }

               var2.handle(var7);
               var1.scheduleAnimation();
            } catch (NumberFormatException var12) {
            }
         }
      }
   }

   private void process(ModernClickGuiState var1, ColorSetting var2) {
      String var3 = var1.matchEffect();
      if (var3 != null && !var3.isEmpty()) {
         try {
            int var4 = Integer.parseUnsignedInt(var3);
            if (var4 < 0) {
               var4 = 0;
            }

            if (var4 > 100) {
               var4 = 100;
            }

            var2.process(var4 / 100.0F);
            var1.scheduleAnimation();
         } catch (NumberFormatException var5) {
         }
      }
   }

   private static boolean handle(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'a' && var0 <= 'f' || var0 >= 'A' && var0 <= 'F';
   }
}
