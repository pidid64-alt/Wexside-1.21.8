package ru.wild.gui.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.movement.NoSlow;
import ru.wild.modules.visuals.Hud;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "HotBar", process = "d")
public final class HotbarHud extends ThemePresets {
   private static final HotbarHud responseCompute = new HotbarHud();
   private static float providerFetch = 0.0F;
   private static final DoubleAnimator profileDraw = new DoubleAnimator();
   private static final DoubleAnimator vectorPerform = new DoubleAnimator();
   private static String eventAttach = "";
   private static long serverRead;
   public final ChoiceSetting instance = new ChoiceSetting(
      "Элементы статуса",
      new BooleanSetting("Здоровье", true),
      new BooleanSetting("Голод", true),
      new BooleanSetting("Броня", true),
      new BooleanSetting("Воздух", true),
      new BooleanSetting("Поглощение", true)
   );

   private HotbarHud() {
      HudProfileConfig.handle(this);
      this.handle(this.instance);
   }

   public static HotbarHud process() {
      return responseCompute;
   }

   public static void handle(RoundedRectRenderer var0, DrawContext var1) {
      responseCompute.process(var0, var1);
   }

   public void process(RoundedRectRenderer var1, DrawContext var2) {
      if (MinecraftContext.toggleState != null && MinecraftContext.toggleState.player != null && MinecraftContext.toggleState.world != null) {
         if (MinecraftContext.toggleState.getWindow() != null) {
            profileDraw.handle();
            profileDraw.handle(1.0, 0.18F, Easings.handler, false);
            float var3 = profileDraw.update();
            if (!(var3 <= 0.01F)) {
               PlayerInventory var4 = MinecraftContext.toggleState.player.getInventory();
               if (var4 != null) {
                  ItemStack var5 = var4.getStack(var4.getSelectedSlot());
                  String var6 = var5 != null && !var5.isEmpty() ? var5.getName().getString() : "";
                  String var7 = var6.isEmpty() ? "" : var4.getSelectedSlot() + ":" + var5.getItem().toString() + ":" + var6;
                  long var8 = System.currentTimeMillis();
                  if (!var7.equals(eventAttach)) {
                     eventAttach = var7;
                     serverRead = var7.isEmpty() ? 0L : var8 + 2200L;
                  }

                  vectorPerform.handle();
                  vectorPerform.handle(!var6.isEmpty() && var8 <= serverRead ? 1.0 : 0.0, 0.18, Easings.handler, true);
                  float var10 = Math.max(0.0F, Math.min(1.0F, vectorPerform.update()));
                  float var11 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
                  float var12 = MinecraftContext.toggleState.getWindow().getFramebufferHeight();
                  if (!(var11 <= 0.0F) && !(var12 <= 0.0F)) {
                     float var13 = 42.0F;
                     float var14 = 5.0F;
                     float var15 = 1.75F;
                     float var16 = 16.0F * var15;
                     float var17 = 7.0F;
                     float var18 = var13 * 9.0F + var14 * 8.0F + var17 * 2.0F;
                     float var19 = var13 + var17 * 2.0F;
                     float var20 = (var11 - var18) / 2.0F;
                     float var21 = var12 - var19 - 3.0F;
                     ThemeRenderer.PrimaryCacheEntry var22 = ThemeRenderer.handle().handle("HUD_HotBar", var20, var21, var18, var19);
                     float var23 = Math.min(var22.config / Math.max(1.0F, var18), var22.state / Math.max(1.0F, var19));
                     float var24 = this.refresh(var23) + this.render(var23) + this.handle(var23, var10);
                     Hud.State var25 = Hud.handle("HUD_HotBar", var22.data, var22.context - var24, var22.config, var22.state + var24, 8.0F);
                     float var26 = var25.data;
                     float var27 = var25.context + var24;
                     float var28 = var22.config;
                     float var29 = var22.state;
                     this.handle(var26, var27, var28, var29);
                     float var30 = var28 / Math.max(1.0F, var18);
                     float var31 = var29 / Math.max(1.0F, var19);
                     float var32 = Math.min(var30, var31);
                     float var33 = var13 * var30;
                     float var34 = var14 * var30;
                     float var35 = var17 * var30;
                     float var36 = var17 * var31;
                     float var37 = var16 * var32;
                     float var38 = var15 * var32;
                     float var39 = var4.getSelectedSlot() * (var33 + var34);
                     providerFetch = providerFetch + (var39 - providerFetch) * 0.25F;
                     float var40 = var3 * this.target.compute();
                     float var41 = this.onTick(var40);
                     int var42 = (int)(255.0F * var40);
                     int var43 = this.handle(var40);
                     int var44 = this.resolve(var40);
                     int var45 = this.prepare(var40);
                     int var46 = this.check(var40);
                     int var47 = this.onTick() ? PackedColor.compute(255, 255, 255, (int)(5.0F * var41)) : this.process(var41);
                     float var48 = 12.0F * var32;
                     boolean var49 = this.select();
                     if (var49) {
                        ThemeShaderApplier.handle();
                     }

                     try {
                        this.handle(var1, var26, var27, var28, var29, var48, var40);

                        for (int var50 = 0; var50 < 9; var50++) {
                           float var51 = var26 + var35 + var50 * (var33 + var34);
                           float var52 = var50 == 0 ? 8.0F * var32 : 4.0F * var32;
                           float var53 = var50 == 8 ? 8.0F * var32 : 4.0F * var32;
                           if (!this.refresh() && !this.render()) {
                              if (!var49
                                 || !this.handle(var51, var27 + var36, var33, var33, Math.min(var52, var53), 2.8F * var32, 6.0F * var32, 0.86F, 2, true, var40)
                                 )
                               {
                                 var1.handle(var51, var27 + var36, var33, var33, var52, var53, var53, var52, var47);
                              }
                           } else {
                              this.process(var1, var51, var27 + var36, var33, var33, Math.min(var52, var53), var40);
                           }
                        }
                     } finally {
                        if (var49) {
                           ThemeShaderApplier.compute();
                        }
                     }

                     float var85 = var26 + var35 + providerFetch;
                     float var86 = var27 + var36;
                     var1.handle(
                        var85 + 3.0F * var30,
                        var86 + var33 - Math.max(2.0F, 2.0F * var31),
                        var33 - 4.0F * var30,
                        Math.max(1.0F, 2.0F * var31),
                        Math.max(0.5F, 0.8F * var32),
                        PackedColor.handle(var46, (int)(140.0F * var40))
                     );
                     var1.compute();
                     var1.handle(var26, var27, var28, var29, var48, var48, var48, var48);

                     try {
                        for (int var87 = 0; var87 < 9; var87++) {
                           ItemStack var89 = var4.getStack(var87);
                           float var54 = var26 + var35 + var87 * (var33 + var34);
                           float var55 = var54 + (var33 - var37) * 0.5F;
                           float var56 = var27 + var36 + (var33 - var37) * 0.5F;
                           if (var89 != null && !var89.isEmpty()) {
                              ItemStackOverlayRenderer.handle(
                                 var1,
                                 var89,
                                 ItemStackOverlayRenderer.handle(var55),
                                 ItemStackOverlayRenderer.handle(var56),
                                 ItemStackOverlayRenderer.compute(var38),
                                 var87,
                                 true,
                                 var87
                              );
                           }

                           String var57 = String.valueOf(var87 + 1);
                           float var58 = 22.0F * var32;
                           int var59 = var87 == var4.getSelectedSlot()
                              ? PackedColor.compute(255, 255, 255, (int)(245.0F * var40))
                              : PackedColor.handle(this.apply(1.0F), (int)(175.0F * var40));
                           float var60 = var54 + 4.0F * var30;
                           float var61 = var27 + var36 + var33 - var58 * var31 - 8.0F;
                           var1.handle(FontRegistry.config, var60, var61, var58, var57, var59);
                        }
                     } finally {
                        var1.compute();
                        var1.apply();
                     }

                     ItemStack var88 = NoSlow.handle(MinecraftContext.toggleState.player.getOffHandStack());
                     if (var88 != null && !var88.isEmpty()) {
                        float var90 = MinecraftContext.toggleState.player.getMainArm() == Arm.RIGHT
                           ? var26 - var29 - 5.0F * var30
                           : var26 + var28 + 5.0F * var30;
                        if (var49) {
                           ThemeShaderApplier.handle();
                        }

                        try {
                           if (this.refresh() || this.render()) {
                              this.handle(var1, var90, var27, var29, var29, var48, var40);
                              this.process(var1, var90 + var35, var27 + var36, var33, var33, 8.0F * var32, var40);
                           } else if (var49) {
                              if (!this.handle(var90, var27, var29, var29, var48, false, var40, 1)) {
                                 var1.handle(var90, var27, var29, var29, var48, var43);
                              }

                              if (!this.handle(var90 + var35, var27 + var36, var33, var33, 8.0F * var32, 2.8F * var32, 6.0F * var32, 0.86F, 2, true, var40)) {
                                 var1.handle(var90 + var35, var27 + var36, var33, var33, 8.0F * var32, var47);
                              }
                           } else {
                              if (this.resolve()) {
                                 var1.handle(var90, var27, var29, var29, var48, this.tick() ? 6.0F : 4.0F, 1.0F, this.select(var40));
                              }

                              if (this.onTick()) {
                                 var1.handle(23.0F);
                                 var1.handle(var90, var27, var29, var29, var48, var40);
                              }

                              var1.handle(var90, var27, var29, var29, var48, var43);
                              if (this.update()) {
                                 var1.handle(var90, var27, var29, var29, var48, var44, this.compute());
                              }

                              var1.handle(var90 + var35, var27 + var36, var33, var33, 8.0F * var32, var47);
                           }
                        } finally {
                           if (var49) {
                              ThemeShaderApplier.compute();
                           }
                        }

                        float var93 = var90 + var35 + (var33 - var37) * 0.5F;
                        float var96 = var27 + var36 + (var33 - var37) * 0.5F;
                        var1.compute();
                        var1.handle(var90, var27, var29, var29, var48, var48, var48, var48);

                        try {
                           ItemStackOverlayRenderer.handle(
                              var1,
                              var88,
                              ItemStackOverlayRenderer.handle(var93),
                              ItemStackOverlayRenderer.handle(var96),
                              ItemStackOverlayRenderer.compute(var38),
                              0,
                              true,
                              0
                           );
                        } finally {
                           var1.compute();
                           var1.apply();
                        }
                     }

                     PlayerStatusHud.handle().handle(var1, this, var26, var27, var28, var30, var31, var40);
                     if (MinecraftContext.toggleState.player.experienceLevel > 0) {
                        String var91 = String.valueOf(MinecraftContext.toggleState.player.experienceLevel);
                        float var94 = 12.0F * var32;
                        float var97 = 8.0F * var32;
                        float var99 = 26.0F * var32;
                        float var101 = TextMeasureCache.handle(FontRegistry.config, var91, var99).instance;
                        int var103 = PackedColor.handle(var45, var42);
                        int var105 = this.process(var40);
                        float var107 = this.refresh(var32);
                        float var109 = Math.max(34.0F * var32, var101 + 16.0F * var32);
                        float var62 = var94;
                        float var63 = var26 + (var28 - var109) * 0.5F;
                        float var64 = var27 - var107 - var97 - var62;
                        if (var49) {
                           if (!this.handle(var63, var64, var109, var62, var62 * 0.5F, 2.4F * var32, 5.5F * var32, 0.82F, 1, false, var40)) {
                              var1.handle(var63, var64, var109, var62, var62 * 0.5F, var105);
                           }
                        } else if (this.refresh() || this.render()) {
                           this.process(var1, var63, var64, var109, var62, var62 * 0.5F, var40);
                        }

                        var1.handle(FontRegistry.config, var63 + (var109 - var101) * 0.5F, var64 + var62 * 0.5F + 3.7F * var32, var99, var91, var103);
                     }

                     if (var10 > 0.01F && !var6.isEmpty()) {
                        float var92 = var40 * var10;
                        float var95 = 16.0F * var32;
                        float var98 = 32.0F * var32;
                        float var100 = 4.0F * var32;
                        float var102 = Math.clamp(var28 * 0.72F, 20.0F * var32, 190.0F * var32);
                        String var104 = this.handle(var6, var98, var102);
                        float var106 = TextMeasureCache.process(FontRegistry.instance, var104, var98);
                        float var108 = Math.max(54.0F * var32, var106 + 20.0F * var32);
                        float var110 = this.refresh(var32);
                        float var111 = this.render(var32);
                        float var112 = var26 + (var28 - var108) * 0.5F;
                        float var113 = var27 - var110 - var111 - var100 - var95;
                        var1.handle(
                           FontRegistry.instance,
                           var112 + (var108 - var106) * 0.5F,
                           var113 + var95 * 0.5F + 1.05F * var32,
                           var98,
                           var104,
                           PackedColor.handle(this.update(1.0F), (int)(255.0F * var92))
                        );
                     }

                     Hud.handle("HUD_HotBar", var26, var27 - var24, var28, var29 + var24);
                     ThemeRenderer.handle().handle(var22);
                     NeoStyleOptions.handle(
                        var1,
                        this,
                        var22,
                        ThemeRenderer.handle(),
                        MinecraftContext.toggleState.getWindow().getScaledWidth(),
                        MinecraftContext.toggleState.getWindow().getScaledHeight()
                     );
                  }
               }
            }
         }
      }
   }

   private float refresh(float var1) {
      if (MinecraftContext.toggleState != null && MinecraftContext.toggleState.player != null) {
         boolean var2 = this.instance.process("Здоровье");
         boolean var3 = this.instance.process("Голод");
         boolean var4 = this.instance.process("Броня") && MinecraftContext.toggleState.player.getArmor() > 0;
         boolean var5 = this.instance.process("Воздух") && MinecraftContext.toggleState.player.getAir() < MinecraftContext.toggleState.player.getMaxAir();
         float var6 = 12.0F * var1;
         float var7 = 4.0F * var1;
         int var8 = 0;
         if (var2 || var3) {
            var8++;
         }

         if (var4 || var5) {
            var8++;
         }

         return var8 == 0 ? 0.0F : var8 * var6 + var8 * var7;
      } else {
         return 0.0F;
      }
   }

   private float render(float var1) {
      return MinecraftContext.toggleState != null && MinecraftContext.toggleState.player != null && MinecraftContext.toggleState.player.experienceLevel > 0
         ? 16.0F * var1
         : 0.0F;
   }

   private float handle(float var1, float var2) {
      return var2 > 0.01F ? 20.0F * var1 : 0.0F;
   }

   private String handle(String var1, float var2, float var3) {
      if (var1 != null && !var1.isEmpty()) {
         if (TextMeasureCache.process(FontRegistry.instance, var1, var2) <= var3) {
            return var1;
         }

         String var4 = "...";
         float var5 = TextMeasureCache.process(FontRegistry.instance, var4, var2);
         if (var5 >= var3) {
            return var4;
         }

         int var6 = 0;
         int var7 = var1.length();

         while (var6 < var7) {
            int var8 = var6 + var7 + 1 >>> 1;
            String var9 = var1.substring(0, var8).trim();
            float var10 = TextMeasureCache.process(FontRegistry.instance, var9, var2) + var5;
            if (var10 <= var3) {
               var6 = var8;
            } else {
               var7 = var8 - 1;
            }
         }

         String var11 = var1.substring(0, Math.max(0, var6)).trim();
         return var11.isEmpty() ? var4 : var11 + var4;
      } else {
         return "";
      }
   }
}
