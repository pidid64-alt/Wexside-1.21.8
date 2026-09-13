package ru.wild.gui.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.gui.widget.AnimatedTextLabel;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "ArmorHUD", process = "w")
public final class ArmorHudRenderer extends ThemePresets {
   private static final ArmorHudRenderer instance = new ArmorHudRenderer();
   private static final DoubleAnimator responseCompute = new DoubleAnimator();
   private static final DoubleAnimator providerFetch = new DoubleAnimator();
   private static final DoubleAnimator profileDraw = new DoubleAnimator();
   private static final ItemStack[] vectorPerform = new ItemStack[4];
   private static final AnimatedTextLabel[] eventAttach = new AnimatedTextLabel[4];
   private final BooleanSetting serverRead = new BooleanSetting("Показывать в процентах", true);
   private final ModeSetting positionAdvance = new ModeSetting("Ориентация", "Горизонтально", "Горизонтально", "Вертикально");

   private ArmorHudRenderer() {
      this.handle(this.serverRead);
      this.handle(this.positionAdvance);
      HudProfileConfig.handle(this);
   }

   public static ArmorHudRenderer process() {
      return instance;
   }

   public static void handle(RoundedRectRenderer var0, DrawContext var1) {
      instance.process(var0, var1);
   }

   public void process(RoundedRectRenderer var1, DrawContext var2) {
      if (MinecraftContext.toggleState.player != null) {
         vectorPerform[0] = MinecraftContext.toggleState.player.getEquippedStack(EquipmentSlot.HEAD);
         vectorPerform[1] = MinecraftContext.toggleState.player.getEquippedStack(EquipmentSlot.CHEST);
         vectorPerform[2] = MinecraftContext.toggleState.player.getEquippedStack(EquipmentSlot.LEGS);
         vectorPerform[3] = MinecraftContext.toggleState.player.getEquippedStack(EquipmentSlot.FEET);
         int var3 = 0;

         for (int var4 = 0; var4 < 4; var4++) {
            ItemStack var5 = vectorPerform[var4];
            if (var5 != null && !var5.isEmpty()) {
               vectorPerform[var3++] = var5;
            }
         }

         boolean var63 = var3 > 0;
         boolean var64 = var63 || MinecraftContext.toggleState.currentScreen instanceof ChatScreen;
         responseCompute.handle();
         responseCompute.handle(var64 ? 1.0 : 0.0, 0.22F, Easings.handler, false);
         float var6 = responseCompute.update();
         if (!(var6 <= 0.01F)) {
            float var7 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
            float var8 = MinecraftContext.toggleState.getWindow().getFramebufferHeight();
            float var9 = 7.0F;
            boolean var10 = this.positionAdvance.process("Вертикально");
            float var11 = var10 ? 56.0F : 42.0F;
            float var12 = 54.0F;
            float var13 = 5.0F;
            int var14 = var63 ? var3 : 4;
            float var15 = var10 ? var11 : var14 * var11 + (var14 - 1) * var13;
            float var16 = var10 ? var14 * var12 + (var14 - 1) * var13 : var12;
            float var17 = var15 + var9 * 2.0F;
            float var18 = var16 + var9 * 2.0F;
            providerFetch.handle();
            profileDraw.handle();
            providerFetch.handle(var17, 0.18F, Easings.handler, false);
            profileDraw.handle(var18, 0.18F, Easings.handler, false);
            float var19 = providerFetch.update();
            float var20 = profileDraw.update();
            float var21 = var7 * 0.5F + 96.0F;
            float var22 = var8 - var20 - 12.0F;
            ThemeRenderer.PrimaryCacheEntry var23 = ThemeRenderer.handle().handle("hud_armor", var21, var22, var19, var20);
            float var24 = var23.data;
            float var25 = var23.context;
            float var26 = var23.config;
            float var27 = var23.state;
            this.handle(var24, var25, var26, var27);
            float var28 = var26 / Math.max(1.0F, var19);
            float var29 = var27 / Math.max(1.0F, var20);
            float var30 = Math.min(var28, var29);
            float var31 = var11 * var28;
            float var32 = var12 * var29;
            float var33 = var13 * (var10 ? var29 : var28);
            float var34 = var15 * var28;
            float var35 = var16 * var29;
            float var36 = var6 * this.target.compute();
            float var37 = this.onTick(var36);
            int var38 = (int)(255.0F * var36);
            int var39 = this.handle(var36);
            int var40 = this.resolve(var36);
            int var41 = this.onTick() ? PackedColor.compute(255, 255, 255, (int)(5.0F * var37)) : this.process(var37);
            float var42 = 10.0F;
            this.handle(var1, var24, var25, var26, var27, var42, var36);
            var1.compute();
            var1.handle(var24, var25, var26, var27, var42, var42, var42, var42);

            try {
               float var43 = var24 + (var26 - var34) * 0.5F;
               float var44 = var25 + (var27 - var35) * 0.5F;

               for (int var45 = 0; var45 < var14; var45++) {
                  float var46 = var10 ? var43 : var43 + var45 * (var31 + var33);
                  float var47 = var10 ? var44 + var45 * (var32 + var33) : var44;
                  if (!this.select() && !this.refresh() && !this.render()) {
                     var1.handle(var46, var47, var31, var32, 6.0F * var30, var41);
                  } else {
                     this.process(var1, var46, var47, var31, var32, 6.0F * var30, var36);
                  }
               }

               if (var63) {
                  var1.compute();
               }

               for (int var65 = 0; var65 < var14 && var63; var65++) {
                  float var66 = var10 ? var43 : var43 + var65 * (var31 + var33);
                  float var67 = var10 ? var44 + var65 * (var32 + var33) : var44;
                  ItemStack var48 = vectorPerform[var65];
                  float var49 = 1.5F * var30;
                  float var50 = 16.0F * var49;
                  float var51 = var66 + (var31 - var50) * 0.5F;
                  float var52 = var67 + 8.0F * var29;
                  ItemStackOverlayRenderer.handle(
                     var1,
                     var48,
                     ItemStackOverlayRenderer.handle(var51),
                     ItemStackOverlayRenderer.handle(var52),
                     ItemStackOverlayRenderer.compute(var49),
                     var65,
                     true,
                     var65
                  );
                  if (var48.isDamageable()) {
                     int var53 = var48.getMaxDamage();
                     int var54 = var53 - var48.getDamage();
                     boolean var55 = this.serverRead.compute();
                     float var56 = var53 <= 0 ? 1.0F : (float)var54 / var53;
                     String var57 = var55 ? (int)(var56 * 100.0F) + "%" : var54 + "/" + var53;
                     int var58 = var56 <= 0.2F ? PackedColor.compute(255, 85, 85, var38) : this.prepare(var36);
                     float var59 = 16.0F * var30;
                     eventAttach[var65].handle(var57, var54);
                     eventAttach[var65]
                        .handle(
                           var1,
                           FontRegistry.config,
                           var66,
                           var67,
                           var31,
                           var32,
                           4.0F * var30,
                           var66 + var31 * 0.5F,
                           var67 + var32 - 6.0F * var29,
                           var59,
                           var58
                        );
                  }
               }
            } finally {
               var1.compute();
               var1.apply();
            }

            ThemeRenderer.handle().handle(var23);
            NeoStyleOptions.handle(
               var1,
               this,
               var23,
               ThemeRenderer.handle(),
               MinecraftContext.toggleState.getWindow().getScaledWidth(),
               MinecraftContext.toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   static {
      for (int var0 = 0; var0 < eventAttach.length; var0++) {
         eventAttach[var0] = new AnimatedTextLabel();
      }
   }
}
