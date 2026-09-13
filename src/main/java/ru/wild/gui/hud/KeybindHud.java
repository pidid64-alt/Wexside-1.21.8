package ru.wild.gui.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screen.ChatScreen;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.Setting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.core.manager.HudElementRegistry;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.visuals.Hud;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeycodeNames;

@HudElementMetadata(handle = "KeyBindHUD", process = "q")
public final class KeybindHud extends ThemePresets {
   private static final KeybindHud instance = new KeybindHud();
   private static final List<KeybindHud.TextureState> responseCompute = new ArrayList<>(64);
   private static final Map<Module, KeybindHud.TextureState> providerFetch = new IdentityHashMap<>(128);
   private static final Map<BooleanSetting, KeybindHud.TextureState> profileDraw = new IdentityHashMap<>(64);
   private static final DoubleAnimator vectorPerform = new DoubleAnimator();
   private static final DoubleAnimator eventAttach = new DoubleAnimator();
   private static final DoubleAnimator serverRead = new DoubleAnimator();
   private static final Map<String, DoubleAnimator> positionAdvance = new HashMap<>();
   private static final Map<BooleanSetting, DoubleAnimator> frameCheck = new IdentityHashMap<>(64);
   private static final List<Module> moduleCollect = new ArrayList<>(64);
   private static final List<BooleanSetting> providerClose = new ArrayList<>(8);
   private static final float presetSave = 12.0F;
   private static final float windowConvert = 0.94F;
   private static final float presetWrite = 0.78F;
   private static final float colorMeasure = 3.4F;
   private static final float animationSchedule = 3.5F;
   private static final float rendererScan = 1.1F;
   private final BooleanSetting sourceBuild = new BooleanSetting("Отображать иконки", true);

   private KeybindHud() {
      this.handle(this.sourceBuild);
      HudProfileConfig.handle(this);
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   public static KeybindHud process() {
      return instance;
   }

   public void process(RoundedRectRenderer var1) {
      if (MinecraftContext.toggleState.player != null && WildClient.instance != null && WildClient.instance.data != null) {
         boolean var2 = this.sourceBuild.compute();
         boolean var3 = Hud.render();
         HudElementRegistry.ColorState var4 = var3 ? HudElementRegistry.handle() : null;
         float var5 = 22.0F;
         float var6 = var3 ? var4.current : 7.0F;
         float var7 = var3 ? var4.mode : 32.0F;
         float var8 = var3 ? var4.selection : 22.0F;
         float var9 = var3 ? var4.active : 5.0F;
         float var10 = var3 ? var4.enabled : 28.0F;
         float var11 = var3 ? var4.renderer : var5;
         float var12 = var3 ? var4.animationDraw : 1.9F;
         responseCompute.clear();
         moduleCollect.clear();

         for (Module var14 : WildClient.instance.data.process()) {
            if (!"Menu".equals(var14.displayName) && var14.keyCode != -1) {
               DoubleAnimator var15 = positionAdvance.computeIfAbsent(var14.displayName, var0 -> new DoubleAnimator());
               var15.handle();
               var15.handle(var14.enabled ? 1.0 : 0.0, 0.2F, Easings.handler, false);
               if (var15.update() > 0.001F || var14.enabled) {
                  KeybindHud.TextureState var16 = providerFetch.computeIfAbsent(var14, KeybindHud.TextureState::new);
                  var16.handle(var14, var15, var2, var5);
                  moduleCollect.add(var14);
               }
            }
         }

         moduleCollect.sort((var0, var1x) -> Float.compare(providerFetch.get(var1x).current, providerFetch.get(var0).current));

         for (Module var103 : moduleCollect) {
            responseCompute.add(providerFetch.get(var103));

            for (BooleanSetting var107 : handle(var103)) {
               DoubleAnimator var17 = frameCheck.computeIfAbsent(var107, var0 -> new DoubleAnimator());
               var17.handle();
               var17.handle(var103.enabled ? 1.0 : 0.0, 0.2F, Easings.handler, false);
               if (var17.update() > 0.001F || var103.enabled) {
                  KeybindHud.TextureState var18 = profileDraw.computeIfAbsent(var107, var1x -> new KeybindHud.TextureState(var103, var1x));
                  var18.handle(var107, var17, var5);
                  responseCompute.add(var18);
               }
            }
         }

         boolean var102 = !responseCompute.isEmpty() || MinecraftContext.toggleState.currentScreen instanceof ChatScreen;
         vectorPerform.handle();
         vectorPerform.handle(var102 ? 1.0 : 0.0, 0.22F, Easings.handler, false);
         float var104 = vectorPerform.update();
         if (!(var104 <= 0.01F)) {
            float var106 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
            String var108 = "Binds";
            float var109 = TextMeasureCache.process(FontRegistry.config, var108, var10);
            float var110 = 0.0F;
            float var19 = 0.0F;
            float var20 = 0.0F;

            for (KeybindHud.TextureState var22 : responseCompute) {
               var110 = Math.max(var110, var22.current);
               var19 = Math.max(var19, var22.active);
               var20 += (var22.handle() ? var8 * 0.78F : var8) * var22.config.update();
            }

            float var111 = var110 + 24.0F;
            float var113 = var19 + 20.0F + (var3 ? var4.handler : 0.0F);
            float var23 = var111 + var9 + var113;
            float var24 = var23 + var6 * 2.0F;
            float var25 = var109 + var11 + 34.0F;
            var24 = Math.max(var24, var25 + var6 * 2.0F);
            var23 = var24 - var6 * 2.0F;
            var111 = var23 - var9 - var113;
            boolean var26 = var20 > 0.01F;
            float var27 = var6 + var7 + (var26 ? var9 : 0.0F) + var20 + var6;
            eventAttach.handle();
            serverRead.handle();
            eventAttach.handle(var24, 0.18F, Easings.handler, false);
            serverRead.handle(var27, 0.18F, Easings.handler, false);
            float var28 = eventAttach.update();
            float var29 = serverRead.update();
            float var30 = Math.max(10.0F, var106 - var28 - 10.0F);
            float var31 = 10.0F;
            ThemeRenderer.PrimaryCacheEntry var32 = ThemeRenderer.handle().handle("HUD_HotKeys", var30, var31, var28, var29);
            float var33 = var32.data;
            float var34 = var32.context;
            float var35 = var32.config;
            float var36 = var32.state;
            this.handle(var33, var34, var35, var36);
            float var37 = var35 / Math.max(1.0F, var28);
            float var38 = var36 / Math.max(1.0F, var29);
            float var39 = Math.min(var37, var38);
            float var40 = var6 * var37;
            float var41 = var6 * var38;
            float var42 = var7 * var38;
            float var43 = var8 * var38;
            float var44 = var5 * var39;
            float var45 = var111 * var37;
            float var46 = var113 * var37;
            float var47 = var9 * var37;
            float var48 = var104 * this.target.compute();
            int var49 = this.process(var48);
            int var50 = this.compute(var48);
            int var51 = this.update(var48);
            int var52 = this.prepare(var48);
            float var53 = var3 ? var4.instance : 14.0F;
            float var54 = var3 ? var4.data : 11.0F;
            float var55 = var3 ? var4.context : 7.0F;
            float var56 = var3 ? var4.config : var55;
            float var57 = var3 ? var4.state : var55;
            float var58 = var35 - var40 * 2.0F;
            this.handle(var1, var33, var34, var35, var36, var53, var48);
            if (this.refresh() || this.render()) {
               this.process(var1, var33 + var40, var34 + var41, var58, var42, var54, var48);
            } else if (this.select()) {
               if (!this.handle(var33 + var40, var34 + var41, var58, var42, var54, false, var48, 1)) {
                  var1.handle(var33 + var40, var34 + var41, var58, var42, var54, var49);
               }
            } else if (var3) {
               var1.handle(var33 + var40, var34 + var41, var58, var42, var54, var49);
            } else {
               var1.handle(var33 + var40, var34 + var41, var58, var42, 11.0F, 11.0F, 4.0F, 4.0F, var49);
            }

            float var59 = var3 ? var33 + var4.pointEncode.instance * var37 : var33 + var40 + 10.0F * var37;
            float var60 = var3 ? var34 + var4.pointEncode.data * var38 : handle(var34 + var41, var42, 28.0F * var39);
            var1.handle(FontRegistry.config, var59, var60, var10 * var39, var108, var51);
            float var61 = Math.max(17.0F * var39, 20.0F * var38);
            float var62 = var33 + var40 + var58 - 10.0F * var37 - var61;
            float var63 = var34 + var41 + (var42 - var61) * 0.5F;
            float var64 = var11 * var39;
            float var65 = TextMeasureCache.process(FontRegistry.current, "q", var64);
            float var66 = var3 ? (var4.animator.context ? var33 + var35 : var33) + var4.animator.instance * var37 : var62 + (var61 - var65) * 0.5F - 1.5F;
            float var67 = var3 ? var34 + var4.animator.data * var38 + 1.5F * var38 : handle(var63, var61, var64) + 1.5F * var38;
            var1.handle(FontRegistry.current, var66, var67, var64, "q", var52);
            float var68 = var34 + var41 + var42 + (var26 ? var9 * var38 : 0.0F);
            float var69 = var20 * var38;
            float var70 = var33 + var40 + (var3 ? var4.source.instance * var37 : 0.0F);
            float var71 = var68 + (var3 ? var4.source.data * var38 : 0.0F);
            float var72 = var33 + var40 + var45 + var47 + (var3 ? var4.target.instance * var37 : 0.0F);
            float var73 = var68 + (var3 ? var4.target.data * var38 : 0.0F);
            if (var69 > 0.01F && (this.prepare() || this.refresh() || this.render())) {
               if (this.refresh() || this.render()) {
                  this.process(var1, var70, var71, var45, var69, var56, var48);
                  this.process(var1, var72, var73, var46, var69, var57, var48);
               } else if (this.select()) {
                  if (!this.handle(var70, var71, var45, var69, var56, true, var48, 2)) {
                     var1.handle(var70, var71, var45, var69, var56, var50);
                  }

                  if (!this.handle(var72, var73, var46, var69, var57, true, var48, 2)) {
                     var1.handle(var72, var73, var46, var69, var57, var50);
                  }
               } else if (var3) {
                  var1.handle(var70, var71, var45, var69, var56, var50);
                  var1.handle(var72, var73, var46, var69, var57, var50);
               } else {
                  var1.handle(var70, var71, var45, var69, 4.0F, 4.0F, 4.0F, 11.0F, var50);
                  var1.handle(var72, var73, var46, var69, 4.0F, 4.0F, 11.0F, 4.0F, var50);
               }
            }

            var1.handle(var33, var34, var35, var36, var53, var53, var53, var53);
            float var74 = var71;
            float var75 = var73;

            for (KeybindHud.TextureState var77 : responseCompute) {
               float var78 = var77.config.update();
               if (!(var78 <= 0.01F)) {
                  boolean var79 = var77.handle();
                  float var80 = var79 ? var77.context.update() : 1.0F;
                  float var81 = var79 ? 0.42F + 0.58F * var80 : 1.0F;
                  int var82 = (int)(255.0F * var48 * var78 * var81);
                  int var83 = PackedColor.handle(this.update(1.0F), var82);
                  int var84 = PackedColor.handle(this.prepare(1.0F), var82);
                  int var85 = var79 ? PackedColor.resolve(var83, var84, (double)var80) : var84;
                  float var86 = var79 ? var44 * 0.94F : var44;
                  float var87 = var79 ? var43 * 0.78F : var43;
                  float var88 = (1.0F - var78) * 8.0F * var37;
                  float var89 = var70 + 10.0F * var37 - var88 + (var79 ? 12.0F * var37 : 0.0F);
                  if (var79) {
                     float var90 = var70 + 10.0F * var37 - var88 + var12 * var37 * 0.2F;
                     float var91 = var74 + var87 * 0.5F;
                     float var92 = Math.max(2.0F * var37, var89 - var90);
                     float var93 = Math.min(3.4F * var37, var92 * 0.05F);
                     float var94 = Math.max(1.0F, 1.1F * var37);
                     float var95 = var90 + var93;
                     float var96 = var91 - var93;
                     var1.handle(var95, var96, var93, 90.0F, 0.25F, var94, var85);
                     float var97 = var89 - 3.5F * var37;
                     if (var97 > var95 + 0.5F) {
                        var1.handle(var95, var91 - var94 * 0.5F, var97 - var95, var94, var94 * 0.5F, var85);
                     }
                  } else {
                     if (var12 > 0.05F) {
                        var1.handle(var89, var74 + (var87 - 8.0F * var38) * 0.5F, var12 * var37, 8.0F * var38, Math.max(0.7F, var12 * 0.5F) * var37, var84);
                     }

                     var89 += 8.0F * var37;
                  }

                  float var116 = handle(var74, var87, var86);
                  if (!var79 && var2 && var77.output != null) {
                     var1.handle(FontRegistry.current, var89, var116, var86, var77.output, var83);
                     var89 += var77.mode * var39 + 6.0F * var37;
                  }

                  var1.handle(FontRegistry.instance, var89, var116, var86, var77.state, var83);
                  float var117 = TextMeasureCache.process(FontRegistry.instance, var77.cache, var86);
                  float var118 = var72 + (var46 - var117) * 0.5F + var88;
                  float var119 = var3 ? var75 + var87 * 0.5F + 4.0F * var38 : var116;
                  if (var79 && var80 > 0.02F) {
                     float var120 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 540.0);
                     float var121 = var86 * 1.45F;
                     float var122 = var117 + 13.0F * var37;
                     float var123 = var118 + var117 * 0.5F;
                     if (var3) {
                        float var10000 = var75 + var87 * 0.5F;
                     } else {
                        float var124 = var74 + var87 * 0.5F;
                     }

                     int var99 = Math.max(0, Math.min(255, (int)(var48 * var78 * var80 * (32.0F + 30.0F * var120))));
                     int var100 = PackedColor.handle(this.prepare(1.0F), var99);
                  }

                  var1.handle(FontRegistry.instance, var118, var119, var86, var77.cache, var79 ? var85 : var84);
                  var74 += var87 * var78;
                  var75 += var87 * var78;
               }
            }

            var1.apply();
            ThemeRenderer.handle().handle(var32);
            NeoStyleOptions.handle(
               var1,
               this,
               var32,
               ThemeRenderer.handle(),
               MinecraftContext.toggleState.getWindow().getScaledWidth(),
               MinecraftContext.toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 + var1 * 0.5F + var2 * 0.18F;
   }

   private static List<BooleanSetting> handle(Module var0) {
      providerClose.clear();

      for (Setting var2 : var0.select()) {
         if (var2 instanceof BooleanSetting var3) {
            if (var3.cache != -1) {
               providerClose.add(var3);
            }
         } else if (var2 instanceof ChoiceSetting var4) {
            for (BooleanSetting var6 : var4.config) {
               if (var6.cache != -1) {
                  providerClose.add(var6);
               }
            }
         }
      }

      return providerClose;
   }

   private static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var0 <= var2 + var4 && var1 >= var3 && var1 <= var3 + var5;
   }

   static final class TextureState {
      private final Module instance;
      private final BooleanSetting data;
      final DoubleAnimator context = new DoubleAnimator();
      DoubleAnimator config;
      String state = "";
      String cache = "";
      String output;
      float current;
      float active;
      float mode;
      private int selection = Integer.MIN_VALUE;
      private boolean enabled;

      private TextureState(Module var1) {
         this.instance = var1;
         this.data = null;
      }

      TextureState(Module var1, BooleanSetting var2) {
         this.instance = var1;
         this.data = var2;
      }

      boolean handle() {
         return this.data != null;
      }

      void handle(Module var1, DoubleAnimator var2, boolean var3, float var4) {
         this.config = var2;
         if (!var1.displayName.equals(this.state)) {
            this.state = var1.displayName;
         }

         if (var1.keyCode != this.selection) {
            this.selection = var1.keyCode;
            String var5 = KeycodeNames.handle(var1.keyCode).toUpperCase();
            if (!var5.equals(this.cache)) {
               this.cache = var5;
               this.active = TextMeasureCache.process(FontRegistry.instance, this.cache, var4);
            }
         }

         String var6 = var3 && var1.category != null ? var1.category.handle() : null;
         if (var6 == null) {
            this.output = null;
            this.mode = 0.0F;
            this.current = TextMeasureCache.process(FontRegistry.instance, this.state, var4);
         } else {
            if (!var6.equals(this.output)) {
               this.output = var6;
               this.mode = TextMeasureCache.process(FontRegistry.current, this.output, var4);
            }

            this.current = TextMeasureCache.process(FontRegistry.instance, this.state, var4) + this.mode + 6.0F;
         }
      }

      void handle(BooleanSetting var1, DoubleAnimator var2, float var3) {
         this.config = var2;
         this.context.handle();
         this.context.handle(var1.compute() ? 1.0 : 0.0, 0.26, Easings.handler, false);
         float var4 = var3 * 0.94F;
         if (!var1.instance.equals(this.state)) {
            this.state = var1.instance;
            this.current = TextMeasureCache.process(FontRegistry.instance, this.state, var4) + 12.0F;
         }

         if (var1.cache != this.selection || var1.output != this.enabled) {
            this.selection = var1.cache;
            this.enabled = var1.output;
            String var5 = KeycodeNames.handle(var1.cache).toUpperCase();
            String var6 = this.enabled ? "[HOLD] + " + var5 : var5;
            if (!var6.equals(this.cache)) {
               this.cache = var6;
               this.active = TextMeasureCache.process(FontRegistry.instance, this.cache, var4);
            }
         }

         this.output = null;
         this.mode = 0.0F;
      }
   }
}
