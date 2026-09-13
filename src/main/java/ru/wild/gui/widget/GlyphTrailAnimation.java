package ru.wild.gui.widget;

import java.util.ArrayList;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.font.FontObject;
import ru.wild.util.render.RoundedRectRenderer;

public final class GlyphTrailAnimation {
   private static final float instance = 170.0F;
   private String data = "";
   private final ArrayList<Long> context = new ArrayList<>();
   private final ArrayList<GlyphTrailAnimation.DataRecord> config = new ArrayList<>();

   public boolean handle() {
      return !this.config.isEmpty();
   }

   public void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      FontObject var3,
      String var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      boolean var10,
      int var11,
      long var12
   ) {
      this.handle(var4, var3, var5, var8, var12);
      float var14 = var5;

      for (int var15 = 0; var15 < var4.length(); var15++) {
         String var16 = String.valueOf(var4.charAt(var15));
         float var17 = ModuleStateHelper.handle(var3, var16, var8);
         long var18 = var15 < this.context.size() ? this.context.get(var15) : 0L;
         float var20 = (float)(var12 - var18) / 170.0F;
         float var21 = 0.0F;
         int var22 = var9;
         if (var20 < 1.0F) {
            float var23 = 1.0F - (1.0F - var20) * (1.0F - var20);
            var21 = (1.0F - var23) * var2.handle(7.0F);
            var22 = ThemeColors.handle(var9, Math.round(255.0F * var23));
         }

         ModuleStateHelper.handle(var1, var2, var3, var14, var6 + var21, var7, var8, var16, var22);
         var14 += var17;
      }

      if (var10) {
         ModuleStateHelper.handle(var1, var2, var3, var14, var6, var7, var8, "|", var11);
      }

      for (int var24 = this.config.size() - 1; var24 >= 0; var24--) {
         GlyphTrailAnimation.DataRecord var25 = this.config.get(var24);
         float var26 = (float)(var12 - var25.born()) / 170.0F;
         if (var26 >= 1.0F) {
            this.config.remove(var24);
         } else {
            float var27 = 1.0F - (1.0F - var26) * (1.0F - var26);
            ModuleStateHelper.handle(
               var1,
               var2,
               var3,
               var25.x(),
               var6 + var27 * var2.handle(8.0F),
               var7,
               var8,
               var25.ch(),
               ThemeColors.handle(var9, Math.round(255.0F * (1.0F - var27)))
            );
         }
      }
   }

   private void handle(String var1, FontObject var2, float var3, float var4, long var5) {
      if (!var1.equals(this.data)) {
         int var7 = 0;
         int var8 = Math.min(this.data.length(), var1.length());

         while (var7 < var8 && this.data.charAt(var7) == var1.charAt(var7)) {
            var7++;
         }

         float var9 = var3 + ModuleStateHelper.handle(var2, this.data.substring(0, var7), var4);

         for (int var10 = var7; var10 < this.data.length(); var10++) {
            String var11 = String.valueOf(this.data.charAt(var10));
            this.config.add(new GlyphTrailAnimation.DataRecord(var11, var9, var5));
            var9 += ModuleStateHelper.handle(var2, var11, var4);
         }

         while (this.context.size() > var7) {
            this.context.remove(this.context.size() - 1);
         }

         while (this.context.size() < var1.length()) {
            this.context.add(var5);
         }

         this.data = var1;
      }
   }

   record DataRecord(String ch, float x, long born) {
   }
}
