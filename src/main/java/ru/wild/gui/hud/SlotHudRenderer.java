package ru.wild.gui.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.core.manager.HudElementRegistry;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.visuals.Hud;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "InventoryHUD", process = "w")
public final class SlotHudRenderer extends ThemePresets {
   private static final SlotHudRenderer instance = new SlotHudRenderer();
   private static final DoubleAnimator responseCompute = new DoubleAnimator();
   private static final DoubleAnimator providerFetch = new DoubleAnimator();
   private static final DoubleAnimator profileDraw = new DoubleAnimator();
   private static final DoubleAnimator[] vectorPerform = new DoubleAnimator[27];
   private static final Item[] eventAttach = new Item[27];
   private final BooleanSetting serverRead = new BooleanSetting("Показывать верхушку", true);
   private final BooleanSetting positionAdvance = new BooleanSetting("Фон слотов", true);

   private SlotHudRenderer() {
      this.handle(this.serverRead);
      this.handle(this.positionAdvance);
      HudProfileConfig.handle(this);
   }

   public static void handle(RoundedRectRenderer var0, DrawContext var1) {
      instance.process(var0, var1);
   }

   public static SlotHudRenderer process() {
      return instance;
   }

   public void process(RoundedRectRenderer var1, DrawContext var2) {
      if (MinecraftContext.toggleState.player != null) {
         boolean var3 = false;

         for (int var4 = 9; var4 < 36; var4++) {
            ItemStack var5 = MinecraftContext.toggleState.player.getInventory().getStack(var4);
            if (!var5.isEmpty()) {
               var3 = true;
               break;
            }
         }

         boolean var79 = !var3 && !(MinecraftContext.toggleState.currentScreen instanceof ChatScreen);
         boolean var80 = !var79;
         responseCompute.handle();
         responseCompute.handle(var80 ? 1.0 : 0.0, 0.22F, Easings.handler, false);
         float var6 = responseCompute.update();
         if (!(var6 <= 0.01F)) {
            boolean var7 = this.serverRead.compute();
            boolean var8 = Hud.render();
            HudElementRegistry.ColorState var9 = var8 ? HudElementRegistry.process() : null;
            float var10 = 24.0F;
            float var11 = var8 ? var9.current : 7.0F;
            float var12 = var7 ? (var8 ? var9.mode : 32.0F) : 0.0F;
            float var13 = var7 ? (var8 ? var9.active : 5.0F) : 0.0F;
            float var14 = 22.0F;
            float var15 = var8 ? var9.current : 7.0F;
            float var16 = 9.0F * var14;
            float var17 = 3.0F * var14;
            String var18 = "Inventory";
            float var19 = TextMeasureCache.handle(FontRegistry.config, var18, var8 ? var9.enabled : 26.0F).instance;
            float var20 = var16 + var15 * 2.0F;
            float var21 = var17 + var15 * 2.0F;
            float var22 = var20 + var11 * 2.0F;
            if (var7) {
               float var23 = var19 + 22.0F + var15 * 2.0F + (var8 ? var9.renderer : 24.0F);
               var22 = Math.max(var22, var23 + var11 * 2.0F);
            }

            float var81 = var11 + var12 + var13 + var21 + var11;
            providerFetch.handle();
            profileDraw.handle();
            providerFetch.handle(var22, 0.18F, Easings.handler, false);
            profileDraw.handle(var81, 0.18F, Easings.handler, false);
            float var24 = providerFetch.update();
            float var25 = profileDraw.update();
            float var26 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
            float var27 = Math.max(10.0F, var26 - var24 - 10.0F);
            float var28 = 10.0F;
            ThemeRenderer.PrimaryCacheEntry var29 = ThemeRenderer.handle().handle("HUD_Inventory", var27, var28, var24, var25);
            float var30 = var29.data;
            float var31 = var29.context;
            float var32 = var29.config;
            float var33 = var29.state;
            this.handle(var30, var31, var32, var33);
            float var34 = var32 / Math.max(1.0F, var24);
            float var35 = var33 / Math.max(1.0F, var25);
            float var36 = Math.min(var34, var35);
            float var37 = var11 * var34;
            float var38 = var11 * var35;
            float var39 = var7 ? var12 * var35 : 0.0F;
            float var40 = var13 * var35;
            float var41 = var14 * var36;
            float var42 = var6 * this.target.compute();
            float var43 = this.onTick(var42);
            int var44 = (int)(255.0F * var42);
            int var45 = this.process(var42);
            int var46 = this.compute(var42);
            int var47 = this.update(var42);
            int var48 = this.execute(var42);
            int var49 = this.onTick() ? PackedColor.compute(255, 255, 255, (int)(5.0F * var43)) : this.process(var43);
            float var50 = var8 ? var9.instance : 14.0F;
            float var51 = var8 ? var9.data : 11.0F;
            float var52 = var8 ? var9.context : 9.0F;
            float var53 = var8 ? var9.output : 4.0F;
            float var54 = var32 - var37 * 2.0F;
            this.handle(var1, var30, var31, var32, var33, var50, var42);
            if (var7) {
               if (this.select() || this.refresh() || this.render()) {
                  this.handle(var1, var30 + var37, var31 + var38, var54, var39, var51, var42);
               } else if (var8) {
                  var1.handle(var30 + var37, var31 + var38, var54, var39, var51, var45);
               } else {
                  var1.handle(var30 + var37, var31 + var38, var54, var39, 11.0F, 11.0F, 4.0F, 4.0F, var45);
               }

               float var55 = var8 ? var30 + var9.pointEncode.instance * var34 : var30 + var37 + 10.0F * var34;
               float var56 = var8 ? var31 + var9.pointEncode.data * var35 : var31 + var38 + var39 / 2.0F + 6.0F * var35;
               var1.handle(FontRegistry.config, var55, var56, (var8 ? var9.enabled : 26.0F) * var36, var18, var47);
               float var57 = 22.0F * var35;
               float var58 = var30 + var37 + var54 - 10.0F * var34 - var57;
               float var59 = var31 + var38 + (var39 - var57) / 2.0F;
               float var60 = (var8 ? var9.renderer : var10 + 4.0F) * var36;
               float var61 = TextMeasureCache.handle(FontRegistry.state, "h", var60).instance;
               float var62 = var8 ? (var9.animator.context ? var30 + var32 : var30) + var9.animator.instance * var34 : var58 + (var57 - var61) / 2.0F;
               float var63 = var8 ? var31 + var9.animator.data * var35 : var59 + var57 / 2.0F + 7.0F * var35;
               var1.handle(FontRegistry.state, var62, var63, var60, "h", var48);
            }

            float var82 = var31 + var38 + var39 + var40;
            if (!var7) {
               var82 = var31 + var38;
            }

            float var84 = var30 + var37 + (var8 ? var9.source.instance * var34 : 0.0F);
            var82 += var8 ? var9.source.data * var35 : 0.0F;
            float var85 = var21 * var35;
            if (this.select() || this.refresh() || this.render()) {
               this.process(var1, var84, var82, var54, var85, var52, var42);
            } else if (var8) {
               var1.handle(var84, var82, var54, var85, var52, var46);
            } else {
               var1.handle(var84, var82, var54, var85, var7 ? 4.0F : 11.0F, var7 ? 4.0F : 11.0F, 11.0F, 11.0F, var46);
            }

            var1.compute();
            var1.handle(var30, var31, var32, var33, var50, var50, var50, var50);

            try {
               float var86 = var84 + (var54 - 9.0F * var41) / 2.0F;
               float var87 = var82 + (var85 - 3.0F * var41) / 2.0F;

               for (int var88 = 0; var88 < 3; var88++) {
                  for (int var90 = 0; var90 < 9; var90++) {
                     float var92 = var86 + var90 * var41;
                     float var94 = var87 + var88 * var41;
                     if (this.positionAdvance.compute()) {
                        if (!this.select() && !this.refresh() && !this.render()) {
                           var1.handle(var92 + 1.0F, var94 + 1.0F, var41 - 2.0F, var41 - 2.0F, var53 * var36, var49);
                        } else {
                           this.process(var1, var92 + 1.0F, var94 + 1.0F, var41 - 2.0F, var41 - 2.0F, var53 * var36, var42);
                        }
                     }
                  }
               }

               var1.compute();
               int var89 = 9;

               for (int var91 = 0; var91 < 3; var91++) {
                  for (int var93 = 0; var93 < 9; var93++) {
                     float var95 = var86 + var93 * var41;
                     float var64 = var87 + var91 * var41;
                     ItemStack var65 = MinecraftContext.toggleState.player.getInventory().getStack(var89);
                     int var66 = var89 - 9;
                     DoubleAnimator var67 = vectorPerform[var66];
                     var67.handle();
                     boolean var68 = !var65.isEmpty();
                     Item var69 = var68 ? var65.getItem() : null;
                     if (var68 && eventAttach[var66] != var69) {
                        var67.apply(0.0);
                     }

                     var67.handle(var68 ? 1.0 : 0.0, 0.2F, Easings.handler, false);
                     eventAttach[var66] = var69;
                     if (var68 && var67.update() > 0.01F) {
                        float var70 = var67.update();
                        float var71 = 0.4F + 0.6F * var70;
                        float var72 = var36 * var71;
                        float var73 = 16.0F * var72;
                        float var74 = var95 + (var41 - var73) / 2.0F;
                        float var75 = var64 + (var41 - var73) / 2.0F;
                        ItemStackOverlayRenderer.handle(
                           var1,
                           var65,
                           ItemStackOverlayRenderer.handle(var74),
                           ItemStackOverlayRenderer.handle(var75),
                           ItemStackOverlayRenderer.compute(var72),
                           0,
                           true,
                           var66
                        );
                     }

                     var89++;
                  }
               }
            } finally {
               var1.compute();
               var1.apply();
            }

            ThemeRenderer.handle().handle(var29);
            NeoStyleOptions.handle(
               var1,
               this,
               var29,
               ThemeRenderer.handle(),
               MinecraftContext.toggleState.getWindow().getScaledWidth(),
               MinecraftContext.toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   static {
      for (int var0 = 0; var0 < vectorPerform.length; var0++) {
         vectorPerform[var0] = new DoubleAnimator();
      }
   }
}
