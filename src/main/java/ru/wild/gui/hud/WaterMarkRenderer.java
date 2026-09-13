package ru.wild.gui.hud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.core.manager.HudElementRegistry;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.visuals.Hud;
import ru.wild.profile.Profile;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "WaterMark", process = "w")
public final class WaterMarkRenderer extends ThemePresets {
   private static final WaterMarkRenderer instance = new WaterMarkRenderer();
   private static final DoubleAnimator responseCompute = new DoubleAnimator();
   private static int providerFetch = 0;
   private final SimpleDateFormat profileDraw = new SimpleDateFormat("HH:mm");
   private final Map<String, DoubleAnimator> vectorPerform = new HashMap<>();
   private final List<WaterMarkRenderer.CacheEntry> eventAttach = new ArrayList<>(4);
   private final ChoiceSetting serverRead = new ChoiceSetting(
      "Отображать", new BooleanSetting("Username", true), new BooleanSetting("UID", true), new BooleanSetting("FPS", true), new BooleanSetting("Time", true)
   );
   private float positionAdvance = 0.0F;
   private float frameCheck = 0.0F;
   private float moduleCollect = 0.0F;
   private float providerClose = 0.0F;

   private WaterMarkRenderer() {
      this.handle(this.serverRead);
      HudProfileConfig.handle(this);
   }

   public static WaterMarkRenderer process() {
      return instance;
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   private boolean handle(float var1, float var2, float var3, float var4, float var5, float var6) {
      return var1 >= var3 && var1 <= var3 + var5 && var2 >= var4 && var2 <= var4 + var6;
   }

   private void handle(String var1, String var2, String var3, String var4, List<WaterMarkRenderer.CacheEntry> var5) {
      DoubleAnimator var6 = this.vectorPerform.computeIfAbsent(var1, var0 -> new DoubleAnimator());
      var6.handle();
      var6.handle(this.serverRead.process(var1) ? 1.0 : 0.0, 0.2F, Easings.handler, false);
      if (var6.update() > 0.01F) {
         WaterMarkRenderer.CacheEntry var7 = new WaterMarkRenderer.CacheEntry(var1, var2, var3, var4);
         var7.cache = var6.update();
         var5.add(var7);
      }
   }

   public void process(RoundedRectRenderer var1) {
      if (MinecraftContext.toggleState.player != null) {
         responseCompute.handle();
         responseCompute.handle(1.0, 0.22F, Easings.handler, false);
         float var2 = responseCompute.update();
         if (!(var2 <= 0.01F)) {
            float var3 = ThemeRenderer.handle().execute();
            float var4 = ThemeRenderer.handle().prepare();
            boolean var5 = ThemeRenderer.handle().onTick();
            boolean var6 = ThemeRenderer.handle().check();
            String var7 = ThemeRenderer.handle().select();
            if (this.moduleCollect > 0.0F
               && this.handle(var3, var4, this.positionAdvance, this.frameCheck, this.moduleCollect, this.providerClose)
               && var7 == null) {
               if (var5) {
                  MinecraftContext.toggleState.keyboard.setClipboard(Profile.getUsername());
               }

               if (var6) {
                  ThemeRenderer.handle().process();
               }
            }

            int var8 = MinecraftContext.toggleState.getCurrentFps();
            providerFetch = providerFetch + (int)((var8 - providerFetch) * NumericTransform.apply(0.2F));
            int var9 = Profile.getUid();
            boolean var10 = Hud.render();
            HudElementRegistry.ColorState var11 = var10 ? HudElementRegistry.handle("HUD_WaterMark") : null;
            float var12 = var10 ? var11.enabled : 24.0F;
            float var13 = var10 ? var11.renderer : 24.0F;
            float var14 = var10 ? var11.current : 7.0F;
            float var15 = 10.0F;
            float var16 = var10 ? var11.active : 5.0F;
            float var17 = var10 ? var11.selection : 32.0F;
            this.eventAttach.clear();
            List var18 = this.eventAttach;
            this.handle("Username", "r", Profile.getUsername(), "", var18);
            this.handle("FPS", "u", String.valueOf(providerFetch), "fps", var18);
            this.handle("Time", "y", this.profileDraw.format(System.currentTimeMillis()), "", var18);
            this.handle("UID", "t", String.valueOf(var9), "uid", var18);
            float var19 = 32.0F;
            float var20 = var14 + var19;

            for (WaterMarkRenderer.CacheEntry var22 : (List<WaterMarkRenderer.CacheEntry>) var18) {
               float var23 = TextMeasureCache.handle(FontRegistry.instance, var22.context, var12).instance;
               float var24 = var22.config.isEmpty() ? 0.0F : TextMeasureCache.handle(FontRegistry.instance, var22.config, var12).instance;
               float var25 = TextMeasureCache.handle(FontRegistry.current, var22.data, var13).instance;
               float var26 = var25 + 8.0F + var23 + var24 + var15 * 2.0F;
               var22.state = var26 * var22.cache;
               var20 += var16 * var22.cache + var22.state;
            }

            var20 += var14;
            float var59 = var17 + var14 * 2.0F;
            ThemeRenderer.PrimaryCacheEntry var60 = ThemeRenderer.handle().handle("HUD_WaterMark", 10.0F, 10.0F, var20, var59);
            float var61 = var60.data;
            float var62 = var60.context;
            float var63 = var60.config;
            float var64 = var60.state;
            this.handle(var61, var62, var63, var64);
            float var27 = var63 / Math.max(1.0F, var20);
            float var28 = var64 / Math.max(1.0F, var59);
            float var29 = Math.min(var27, var28);
            float var30 = var14 * var27;
            float var31 = var14 * var28;
            float var32 = var16 * var27;
            float var33 = var19 * var27;
            float var34 = var17 * var28;
            float var35 = var2 * this.target.compute();
            int var36 = this.process(var35);
            int var37 = this.resolve(var35);
            int var38 = this.update(var35);
            int var39 = this.execute(var35);
            float var40 = var10 ? var11.instance : 14.0F;
            this.handle(var1, var61, var62, var63, var64, var40, var35);
            float var41 = var61 + var30;
            float var42 = var62 + var31;
            if (this.refresh() || this.render()) {
               this.process(var1, var41, var42, var33, var34, 11.0F, var35);
            } else if (!this.handle(var41, var42, var33, var34, 11.0F, false, var35, 1)) {
               var1.handle(var41, var42, var33, var34, 11.0F, 4.0F, 4.0F, 11.0F, var36);
               if (this.update()) {
                  var1.handle(var41, var42, var33, var34, 11.0F, 4.0F, 4.0F, 11.0F, var37, Math.max(1.0F, this.compute() * 0.65F));
               }
            }

            float var43 = (var10 ? var11.renderer : 26.0F) * var29;
            float var44 = TextMeasureCache.handle(FontRegistry.current, "w", var43).instance;
            var1.handle(FontRegistry.current, var41 + (var33 - var44) / 2.0F, var42 + var34 / 2.0F + 5.5F * var28, var43, "w", var39);
            float var45 = var41 + var33;

            for (int var46 = 0; var46 < var18.size(); var46++) {
               WaterMarkRenderer.CacheEntry var47 = (WaterMarkRenderer.CacheEntry)var18.get(var46);
               var45 += var32 * var47.cache;
               float var48 = var47.state * var27;
               boolean var49 = var46 == var18.size() - 1;
               if (var47.instance.equals("Username")) {
                  this.positionAdvance = var45;
                  this.frameCheck = var42;
                  this.moduleCollect = var48;
                  this.providerClose = var34;
               }

               int var50 = PackedColor.handle(var36, (int)(PackedColor.handle(var36) * var47.cache));
               int var51 = PackedColor.handle(var39, (int)(PackedColor.handle(var39) * var47.cache));
               int var52 = PackedColor.handle(var38, (int)(PackedColor.handle(var38) * var47.cache));
               boolean var53 = var47.instance.equals("Username") && var6 && var7 == null && this.handle(var3, var4, var45, var42, var48, var34);
               if (!this.refresh() && !this.render()) {
                  if (!this.handle(var45, var42, var48, var34, 11.0F, var53, var35 * var47.cache, var53 ? 2 : 1)) {
                     var1.handle(var45, var42, var48, var34, 4.0F, var49 ? 11.0F : 4.0F, var49 ? 11.0F : 4.0F, 4.0F, var50);
                  }
               } else {
                  this.process(var1, var45, var42, var48, var34, 11.0F, var35 * var47.cache);
               }

               var1.handle(var45, var42, var48, var34, 4.0F, var49 ? 11.0F : 4.0F, var49 ? 11.0F : 4.0F, 4.0F);
               float var54 = var45 + var15 * var27;
               float var55 = var42 + var34 / 2.0F + 4.5F * var28;
               float var56 = var13 * var29;
               float var57 = var12 * var29;
               var1.handle(FontRegistry.current, var54, var55 + 1.0F * var28, var56, var47.data, var51);
               var54 += TextMeasureCache.handle(FontRegistry.current, var47.data, var56).instance + 5.0F * var27;
               var1.handle(FontRegistry.instance, var54, var55, var57, var47.context, var52);
               if (!var47.config.isEmpty()) {
                  var54 += TextMeasureCache.handle(FontRegistry.instance, var47.context, var57).instance;
                  var1.handle(FontRegistry.instance, var54, var55, var57, var47.config, var51);
               }

               var1.apply();
               var45 += var48;
            }

            ThemeRenderer.handle().handle(var60);
            NeoStyleOptions.handle(
               var1,
               this,
               var60,
               ThemeRenderer.handle(),
               MinecraftContext.toggleState.getWindow().getScaledWidth(),
               MinecraftContext.toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   static class CacheEntry {
      final String instance;
      final String data;
      final String context;
      final String config;
      float state;
      float cache = 1.0F;

      CacheEntry(String var1, String var2, String var3, String var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }
   }
}
