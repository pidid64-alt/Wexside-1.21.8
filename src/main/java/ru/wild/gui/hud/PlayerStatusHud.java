package ru.wild.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.modules.visuals.Menu;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class PlayerStatusHud {
   private static final PlayerStatusHud instance = new PlayerStatusHud();
   private final DoubleAnimator data = new DoubleAnimator();
   private final DoubleAnimator context = new DoubleAnimator();
   private final DoubleAnimator config = new DoubleAnimator();
   private final DoubleAnimator state = new DoubleAnimator();
   private final DoubleAnimator cache = new DoubleAnimator();
   private final DoubleAnimator output = new DoubleAnimator();
   private final DoubleAnimator current = new DoubleAnimator();
   private float active = -1.0F;
   private long mode;
   private boolean selection;

   public static PlayerStatusHud handle() {
      return instance;
   }

   public void handle(RoundedRectRenderer var1, ThemePresets var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      MinecraftClient var9 = MinecraftClient.getInstance();
      if (var9 != null && var9.player != null && var9.world != null) {
         ClientPlayerEntity var10 = var9.player;
         boolean var11 = HotbarHud.process().instance.process("Здоровье");
         boolean var12 = HotbarHud.process().instance.process("Голод");
         boolean var13 = HotbarHud.process().instance.process("Броня");
         boolean var14 = HotbarHud.process().instance.process("Воздух");
         boolean var15 = HotbarHud.process().instance.process("Поглощение");
         if (var11 || var12 || var13 || var14) {
            float var16;
            try {
               var16 = (float)var10.getAttributeValue(EntityAttributes.MAX_HEALTH);
            } catch (Throwable var53) {
               var16 = 20.0F;
            }

            if (var16 <= 0.0F || Float.isNaN(var16) || Float.isInfinite(var16)) {
               var16 = 20.0F;
            }

            float var17 = 0.0F;
            float var18 = 0.0F;

            try {
               var17 = var10.getHealth();
               var18 = var10.getAbsorptionAmount();
            } catch (Throwable var52) {
            }

            float var19 = Math.max(0.0F, Math.min(var16, var17));
            float var20 = Math.max(0.0F, var18);
            float var21 = 20.0F;
            HungerManager var22 = var10.getHungerManager();
            float var23 = var22 == null ? 20.0F : Math.max(0.0F, Math.min(var21, var22.getFoodLevel()));
            int var24 = 0;

            try {
               var24 = var10.getArmor();
            } catch (Throwable var51) {
            }

            float var25 = 20.0F;
            int var26 = 0;
            int var27 = 300;

            try {
               var26 = var10.getAir();
               int var28 = var10.getMaxAir();
               if (var28 > 0) {
                  var27 = var28;
               }
            } catch (Throwable var50) {
            }

            boolean var54 = var26 >= var27;
            float var29 = Math.min(var6, var7);
            if (!this.selection) {
               this.data.apply(var19);
               this.context.apply(var23);
               this.config.apply(var24);
               this.state.apply(var20);
               this.cache.apply(var26);
               this.current.apply(var24 > 0 ? 1.0 : 0.0);
               this.output.apply(var54 ? 0.0 : 1.0);
               this.active = var19;
               this.selection = true;
            }

            if (this.active >= 0.0F && var19 < this.active - 0.05F) {
               this.mode = System.currentTimeMillis();
            }

            this.active = var19;
            this.data.handle();
            this.data.handle(var19, 0.22, Easings.selection, true);
            this.context.handle();
            this.context.handle(var23, 0.22, Easings.selection, true);
            this.config.handle();
            this.config.handle(var24, 0.22, Easings.selection, true);
            this.state.handle();
            this.state.handle(var20, 0.22, Easings.selection, true);
            this.cache.handle();
            this.cache.handle(var26, 0.18, Easings.selection, true);
            this.current.handle();
            this.current.handle(var24 > 0 ? 1.0 : 0.0, 0.3, Easings.handler, true);
            this.output.handle();
            this.output.handle(var54 ? 0.0 : 1.0, 0.3, Easings.handler, true);
            float var30 = 6.0F * var29;
            float var31 = (var5 - var30) * 0.5F;
            float var32 = 12.0F * var29;
            float var33 = 4.0F * var29;
            float var34 = var4 - var33 - var32;
            boolean var35 = var13 && this.current.update() > 0.01F || var14 && this.output.update() > 0.01F;
            float var36 = var34 - var33 - var32;
            long var37 = System.currentTimeMillis();
            float var39 = 0.0F;
            if (var11 && var19 / Math.max(1.0F, var16) < 0.2F && var19 > 0.0F && Menu.handle(Menu.rendererScan)) {
               var39 = (float)Math.sin(var37 / 90.0) * 1.2F * var29;
            }

            boolean var40 = var10.hasStatusEffect(StatusEffects.REGENERATION);
            float var41 = 0.0F;
            if (var11 && var40 && Menu.handle(Menu.animationSchedule)) {
               var41 = 0.5F + 0.5F * (float)Math.sin(var37 / 230.0);
            }

            float var42 = 0.0F;
            if (var11 && Menu.handle(Menu.colorMeasure)) {
               long var43 = var37 - this.mode;
               if (this.mode > 0L && var43 < 180L) {
                  var42 = 1.0F - (float)var43 / 180.0F;
               }
            }

            float var55 = 0.0F;
            if (var12 && var23 / var21 < 0.3F && var23 > 0.0F && Menu.handle(Menu.presetWrite)) {
               var55 = 0.4F + 0.6F * (float)Math.sin(var37 / 200.0);
            }

            if (var11) {
               float var44 = this.data.update() / Math.max(1.0F, var16);
               float var45 = this.state.update() / Math.max(1.0F, var16);
               int var46 = PackedColor.compute(255, 90, 96, (int)(255.0F * var8));
               int var47 = PackedColor.compute(220, 36, 50, (int)(255.0F * var8));
               if (var41 > 0.0F) {
                  int var48 = PackedColor.compute(255, 220, 110, (int)(255.0F * var8));
                  var46 = handle(var46, var48, var41 * 0.55F);
                  var47 = handle(var47, var48, var41 * 0.55F);
               }

               this.handle(var1, var2, var3 + var39, var34, var31, var32, var44, 10, var46, var47, var8, var29);
               if (var42 > 0.0F) {
                  int var69 = PackedColor.compute(255, 250, 250, (int)(220.0F * var42 * var8));
                  var1.handle(var3 + var39, var34, var31, var32, var32 * 0.45F, var69);
               }

               if (var15 && var45 > 0.001F) {
                  int var70 = PackedColor.compute(255, 220, 110, (int)(220.0F * var8));
                  int var49 = PackedColor.compute(255, 180, 60, (int)(220.0F * var8));
                  this.compute(var1, var2, var3 + var39, var34, var31, var32, var45, 10, var70, var49, 0.92F, var29);
               }
            }

            if (var12) {
               float var56 = this.context.update() / var21;
               int var59 = PackedColor.compute(220, 158, 92, (int)(255.0F * var8));
               int var62 = PackedColor.compute(150, 92, 44, (int)(255.0F * var8));
               if (var55 > 0.0F) {
                  int var65 = PackedColor.compute(255, 120, 60, (int)(255.0F * var8));
                  var59 = handle(var59, var65, var55 * 0.6F);
                  var62 = handle(var62, var65, var55 * 0.6F);
               }

               float var66 = var3 + var31 + var30;
               this.handle(var1, var2, var66, var34, var31, var32, var56, 10, var59, var62, var8, var29);
            }

            if (var35) {
               if (var13 && this.current.update() > 0.01F) {
                  float var57 = this.current.update();
                  float var60 = this.config.update() / var25;
                  int var63 = PackedColor.compute(180, 200, 230, (int)(255.0F * var8 * var57));
                  int var67 = PackedColor.compute(110, 130, 170, (int)(255.0F * var8 * var57));
                  this.handle(var1, var2, var3, var36 + (1.0F - var57) * var32 * 0.5F, var31, var32, var60, 10, var63, var67, var8 * var57, var29);
               }

               if (var14 && this.output.update() > 0.01F) {
                  float var58 = this.output.update();
                  float var61 = this.cache.update() / Math.max(1.0F, var27);
                  int var64 = PackedColor.compute(120, 200, 255, (int)(255.0F * var8 * var58));
                  int var68 = PackedColor.compute(60, 130, 220, (int)(255.0F * var8 * var58));
                  float var71 = var3 + var31 + var30;
                  this.handle(var1, var2, var71, var36 + (1.0F - var58) * var32 * 0.5F, var31, var32, var61, 10, var64, var68, var8 * var58, var29);
               }
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ThemePresets var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      int var10,
      float var11,
      float var12
   ) {
      var7 = Math.max(0.0F, Math.min(1.0F, var7));
      float var13 = var6 * 0.45F;
      if (var2.select()) {
         this.process(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      } else {
         if (var2.refresh() || var2.render()) {
            var2.process(var1, var3, var4, var5, var6, var13, var11);
         } else if (var2.resolve()) {
            var1.handle(var3, var4, var5, var6, var13, (var2.check() ? 6.0F : 4.0F) * var12, 1.0F, var2.select(var11));
            int var14 = var2.handle(var11);
            var1.handle(var3, var4, var5, var6, var13, var14);
            if (var2.update()) {
               var1.handle(var3, var4, var5, var6, var13, var2.resolve(var11), var2.compute());
            }
         } else {
            int var26 = var2.handle(var11);
            var1.handle(var3, var4, var5, var6, var13, var26);
            if (var2.update()) {
               var1.handle(var3, var4, var5, var6, var13, var2.resolve(var11), var2.compute());
            }
         }

         float var27 = 1.5F * var12;
         float var15 = (var5 - var27 * (var8 - 1)) / var8;
         float var16 = var6 - 4.0F * var12;
         float var17 = var4 + (var6 - var16) * 0.5F;
         float var18 = var16 * 0.4F;
         float var19 = var7 * var8;

         for (int var20 = 0; var20 < var8; var20++) {
            float var21 = var3 + var20 * (var15 + var27);
            float var22 = Math.max(0.0F, Math.min(1.0F, var19 - var20));
            int var23 = var2.process(var2.onTick(var11));
            var1.handle(var21, var17, var15, var16, var18, var23);
            if (var22 > 0.01F) {
               float var24 = var15 * var22;
               var1.handle(var21, var17, var24, var16, var18, var10, var9);
            }
         }

         var1.compute();
      }
   }

   private void process(
      RoundedRectRenderer var1,
      ThemePresets var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      int var10,
      float var11,
      float var12
   ) {
      float var13 = var6 * 0.5F;
      var2.process(var1, var3, var4, var5, var6, var13, var11);
      float var14 = Math.max(1.5F * var12, 1.0F);
      float var15 = var3 + var14;
      float var16 = var4 + var14;
      float var17 = Math.max(1.0F, var5 - var14 * 2.0F);
      float var18 = Math.max(1.0F, var6 - var14 * 2.0F);
      float var19 = var17 * var7;
      float var20 = var18 * 0.5F;
      if (var19 > 0.5F) {
         var1.handle(var15, var16, var17, var18, var20, var20, var20, var20);
         var1.handle(var15, var16, var19, var18, var20, var10, var9);
         var1.handle(
            var15 + var20 * 0.6F,
            var16 + var18 * 0.16F,
            Math.max(0.0F, var19 - var20),
            Math.max(1.0F, var18 * 0.22F),
            var18 * 0.11F,
            PackedColor.compute(255, 255, 255, (int)(48.0F * var11))
         );
         var1.apply();
      }

      float var21 = var17 / Math.max(1, var8);
      int var22 = PackedColor.handle(var2.prepare(1.0F), (int)(36.0F * var11));

      for (int var23 = 1; var23 < var8; var23++) {
         float var24 = var15 + var21 * var23;
         var1.handle(var24 - 0.35F * var12, var16 + var18 * 0.18F, 0.7F * var12, var18 * 0.64F, 0.35F * var12, var22);
      }

      var1.compute();
   }

   private void compute(
      RoundedRectRenderer var1,
      ThemePresets var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      int var10,
      float var11,
      float var12
   ) {
      var7 = Math.max(0.0F, Math.min(1.0F, var7));
      if (var2.select()) {
         float var25 = Math.max(2.6F * var12, 1.5F);
         float var26 = var3 + var25;
         float var27 = var4 + var25;
         float var28 = Math.max(1.0F, var5 - var25 * 2.0F);
         float var29 = Math.max(1.0F, var6 - var25 * 2.0F);
         float var30 = var28 * var7;
         if (var30 > 0.5F) {
            var1.handle(var26, var27, var28, var29, var29 * 0.5F, var29 * 0.5F, var29 * 0.5F, var29 * 0.5F);
            int var31 = PackedColor.handle(var9, (int)(PackedColor.handle(var9) * var11));
            int var32 = PackedColor.handle(var10, (int)(PackedColor.handle(var10) * var11));
            var1.handle(var26, var27, var30, var29, var29 * 0.5F, var32, var31);
            var1.apply();
         }

         var1.compute();
      } else {
         float var13 = 1.5F * var12;
         float var14 = (var5 - var13 * (var8 - 1)) / var8;
         float var15 = var6 - 4.0F * var12;
         float var16 = var4 + (var6 - var15) * 0.5F;
         float var17 = var15 * 0.4F;
         float var18 = var7 * var8;

         for (int var19 = 0; var19 < var8; var19++) {
            float var20 = Math.max(0.0F, Math.min(1.0F, var18 - var19));
            if (!(var20 <= 0.01F)) {
               float var21 = var3 + var19 * (var14 + var13);
               int var22 = PackedColor.handle(var9, (int)(PackedColor.handle(var9) * var11));
               int var23 = PackedColor.handle(var10, (int)(PackedColor.handle(var10) * var11));
               var1.handle(var21, var16, var14 * var20, var15, var17, var23, var22);
            }
         }

         var1.compute();
      }
   }

   private static int handle(int var0, int var1, float var2) {
      var2 = Math.max(0.0F, Math.min(1.0F, var2));
      int var3 = var0 >>> 24 & 0xFF;
      int var4 = var0 >>> 16 & 0xFF;
      int var5 = var0 >>> 8 & 0xFF;
      int var6 = var0 & 0xFF;
      int var7 = var1 >>> 24 & 0xFF;
      int var8 = var1 >>> 16 & 0xFF;
      int var9 = var1 >>> 8 & 0xFF;
      int var10 = var1 & 0xFF;
      int var11 = Math.round(var3 + (var7 - var3) * var2);
      int var12 = Math.round(var4 + (var8 - var4) * var2);
      int var13 = Math.round(var5 + (var9 - var5) * var2);
      int var14 = Math.round(var6 + (var10 - var6) * var2);
      return (var11 & 0xFF) << 24 | (var12 & 0xFF) << 16 | (var13 & 0xFF) << 8 | var14 & 0xFF;
   }
}
