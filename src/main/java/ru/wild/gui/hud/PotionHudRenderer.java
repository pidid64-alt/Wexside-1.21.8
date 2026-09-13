package ru.wild.gui.hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.Identifier;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.manager.HudElementRegistry;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.gui.widget.AnimatedTextLabel;
import ru.wild.modules.misc.Removals;
import ru.wild.modules.visuals.Hud;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.inventory.ChaosSphereHelper;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "PotionsHUD", process = "w")
public final class PotionHudRenderer extends ThemePresets {
   private static final PotionHudRenderer instance = new PotionHudRenderer();
   private static final MinecraftClient responseCompute = MinecraftClient.getInstance();
   private static final List<PotionHudRenderer.TextureState> providerFetch = new ArrayList<>();
   private static final List<PotionHudRenderer.TextureState> profileDraw = new ArrayList<>(16);
   private static final StatusEffectInstance[] vectorPerform = new StatusEffectInstance[8];
   private static final DoubleAnimator eventAttach = new DoubleAnimator();
   private static final DoubleAnimator serverRead = new DoubleAnimator();
   private static final DoubleAnimator positionAdvance = new DoubleAnimator();
   private static final Set<String> frameCheck = new HashSet<>();
   private static final Set<StatusEffectInstance> moduleCollect = new HashSet<>();
   private static final List<StatusEffectInstance> providerClose = new ArrayList<>();
   private final ModeSetting presetSave = new ModeSetting("Вид", "Капсулы", "Капсулы", "Список");
   private final BooleanSetting windowConvert = new BooleanSetting("Показывать верхушку", true).handle(() -> this.presetSave.process("Капсулы"));
   private final BooleanSetting presetWrite = new BooleanSetting("Показывать иконку", true).handle(() -> this.presetSave.process("Капсулы"));
   private final BooleanSetting colorMeasure = new BooleanSetting("Скрыть бесконечные", false);
   private final BooleanSetting animationSchedule = new BooleanSetting("Кастомные зелья", true);
   private final BooleanSetting rendererScan = new BooleanSetting("Шкала времени", false);
   private static final List<PotionHudRenderer.NamedEntry> sourceBuild = List.of(
      new PotionHudRenderer.NamedEntry(
         "custom:hlopushka", "Хлопушка", false, "minecraft:slowness", 9, "minecraft:speed", 4, "minecraft:blindness", 9, "minecraft:glowing", 0
      ),
      new PotionHudRenderer.NamedEntry("custom:holy_water", "Святая Вода", false, "minecraft:regeneration", 2, "minecraft:invisibility", 1),
      new PotionHudRenderer.NamedEntry("custom:gnev", "Зелье Гнева", false, "minecraft:strength", 4, "minecraft:slowness", 3),
      new PotionHudRenderer.NamedEntry(
         "custom:paladin",
         "Зелье Палладина",
         false,
         "minecraft:resistance",
         0,
         "minecraft:fire_resistance",
         0,
         "minecraft:invisibility",
         0,
         "minecraft:health_boost",
         2
      ),
      new PotionHudRenderer.NamedEntry("custom:assassin", "Зелье Ассасина", false, "minecraft:strength", 3, "minecraft:speed", 2, "minecraft:haste", 0),
      new PotionHudRenderer.NamedEntry(
         "custom:radiation",
         "Зелье Радиации",
         true,
         "minecraft:poison",
         1,
         "minecraft:wither",
         1,
         "minecraft:slowness",
         2,
         "minecraft:hunger",
         4,
         "minecraft:glowing",
         0
      ),
      new PotionHudRenderer.NamedEntry(
         "custom:snotvornoye", "Снотворное", true, "minecraft:weakness", 1, "minecraft:mining_fatigue", 1, "minecraft:wither", 2, "minecraft:blindness", 0
      )
   );

   private PotionHudRenderer() {
      this.handle(this.presetSave);
      this.handle(this.windowConvert);
      this.handle(this.presetWrite);
      this.handle(this.colorMeasure);
      this.handle(this.animationSchedule);
      this.handle(this.rendererScan);
      HudProfileConfig.handle(this);
   }

   public static void handle(PacketEvent var0) {
      if (var0 != null && !var0.compute() && responseCompute.player != null) {
         if (var0.resolve() instanceof PlayerRespawnS2CPacket || var0.resolve() instanceof GameJoinS2CPacket) {
            providerFetch.clear();
         }
      }
   }

   public static void handle(RoundedRectRenderer var0, DrawContext var1) {
      instance.process(var0, var1);
   }

   public static PotionHudRenderer process() {
      return instance;
   }

   public void process(RoundedRectRenderer var1, DrawContext var2) {
      if (responseCompute.player != null) {
         this.encodePoint();
         profileDraw.clear();
         boolean var3 = this.colorMeasure.compute();
         boolean var4 = false;

         for (PotionHudRenderer.TextureState var6 : providerFetch) {
            if (!var3 || !var6.process()) {
               profileDraw.add(var6);
               if (var6.mode.update() > 0.01F) {
                  var4 = true;
               }
            }
         }

         boolean var29 = !var4 && !(responseCompute.currentScreen instanceof ChatScreen);
         boolean var30 = !var29;
         eventAttach.handle();
         eventAttach.handle(var30 ? 1.0 : 0.0, 0.22F, Easings.handler, false);
         float var7 = eventAttach.update();
         if (!(var7 <= 0.01F)) {
            boolean var8 = this.presetSave.compute().equals("Капсулы");
            boolean var9 = Hud.render();
            HudElementRegistry.ColorState var10 = var9 ? HudElementRegistry.compute() : null;
            float var11 = 0.0F;
            float var12 = 0.0F;
            if (var8) {
               float var13 = 18.0F;
               float var14 = 14.0F;
               float var15 = var9 ? Math.max(28.0F, var10.selection + 14.0F) : 36.0F;
               float var16 = var9 ? var10.current : 7.0F;
               float var17 = var15 - var16 * 2.0F;
               float var18 = var17 + 4.0F;
               float var19 = var9 ? var10.active : 5.0F;
               float var20 = var9 ? var10.active : 5.0F;

               for (PotionHudRenderer.TextureState var22 : profileDraw) {
                  float var23 = TextMeasureCache.handle(FontRegistry.instance, var22.compute(), var13).instance;
                  float var24 = TextMeasureCache.handle(FontRegistry.instance, var22.resolve(), var14).instance;
                  float var25 = TextMeasureCache.handle(FontRegistry.config, var22.update(), var13).instance;
                  float var26 = var23 + (var24 > 0.0F ? var24 + 8.0F : 0.0F) + 16.0F;
                  float var27 = var25 + 16.0F;
                  float var28 = var16 * 2.0F + var18 + var19 + var26 + var19 + var27;
                  if (var28 > var11) {
                     var11 = var28;
                  }

                  var12 += (var15 + var20) * var22.mode.update();
               }

               if (var12 > 0.0F) {
                  var12 -= var20;
               }
            } else {
               float var31 = 24.0F;
               float var33 = var9 ? var10.current : 7.0F;
               float var35 = this.windowConvert.compute() ? (var9 ? var10.mode : 32.0F) : 0.0F;
               float var37 = var9 ? var10.selection : 22.0F;
               float var39 = var9 ? var10.active : 5.0F;
               float var40 = TextMeasureCache.handle(FontRegistry.config, "Potions", var9 ? var10.enabled : 28.0F).instance;
               float var41 = var40 + 22.0F + (var9 ? var10.renderer : 24.0F);
               float var42 = 0.0F;
               float var43 = 0.0F;

               for (PotionHudRenderer.TextureState var46 : profileDraw) {
                  String var48 = var46.compute() + (var46.resolve().isEmpty() ? "" : " " + var46.resolve());
                  var42 = Math.max(var42, TextMeasureCache.handle(FontRegistry.instance, var48, var31).instance);
                  var43 = Math.max(var43, TextMeasureCache.handle(FontRegistry.instance, var46.update(), var31).instance);
               }

               float var45 = this.presetWrite.compute() ? 22.0F : 0.0F;
               float var47 = var42 + var45 + 24.0F;
               float var49 = var43 + 20.0F + (var9 ? var10.handler : 0.0F);
               float var50 = var47 + var39 + var49;
               var11 = var50 + var33 * 2.0F;
               if (this.windowConvert.compute()) {
                  var11 = Math.max(var11, var41 + var33 * 2.0F);
               }

               float var51 = 0.0F;

               for (PotionHudRenderer.TextureState var53 : profileDraw) {
                  var51 += var37 * var53.mode.update();
               }

               var12 = var33 + var35 + (this.windowConvert.compute() && var51 > 0.01F ? var39 : 0.0F) + var51 + var33;
               if (profileDraw.isEmpty() && this.windowConvert.compute()) {
                  var12 = var33 + var35 + var33;
               }
            }

            serverRead.handle();
            positionAdvance.handle();
            serverRead.handle(var11, 0.18F, Easings.handler, false);
            positionAdvance.handle(var12, 0.18F, Easings.handler, false);
            float var32 = serverRead.update();
            float var34 = positionAdvance.update();
            float var36 = responseCompute.getWindow().getFramebufferWidth();
            ThemeRenderer.PrimaryCacheEntry var38 = ThemeRenderer.handle().handle("HUD_Potions", Math.max(10.0F, var36 - var32 - 10.0F), 70.0F, var32, var34);
            if (var8) {
               this.handle(var1, var2, var38, profileDraw, var7, var32);
            } else {
               this.handle(var1, var2, var38, profileDraw, var7, var32, var34);
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1, DrawContext var2, ThemeRenderer.PrimaryCacheEntry var3, List<PotionHudRenderer.TextureState> var4, float var5, float var6
   ) {
      float var7 = var3.data;
      float var8 = var3.context;
      float var9 = var3.config;
      float var10 = var9 / Math.max(1.0F, var6);
      boolean var11 = Hud.render();
      HudElementRegistry.ColorState var12 = var11 ? HudElementRegistry.compute() : null;
      this.handle(var7, var8, var9, Math.max(36.0F * var10, var3.state));
      float var13 = (var11 ? Math.max(28.0F, var12.selection + 14.0F) : 36.0F) * var10;
      float var14 = (var11 ? var12.current : 7.0F) * var10;
      float var15 = var13 - var14 * 2.0F;
      float var16 = var15 + 4.0F * var10;
      float var17 = (var11 ? var12.active : 5.0F) * var10;
      float var18 = (var11 ? var12.active : 5.0F) * var10;
      float var19 = 18.0F * var10;
      float var20 = 14.0F * var10;
      float var21 = var5 * this.target.compute();
      int var22 = this.handle(var21);
      int var23 = this.compute(var21);
      int var24 = this.resolve(var21);
      int var25 = this.update(var21);
      int var26 = PackedColor.compute(130, 130, 130, (int)(255.0F * var21));
      int var27 = PackedColor.compute(145, 160, 255, (int)(255.0F * var21));
      int var28 = PackedColor.compute(255, 77, 77, (int)(255.0F * var21));
      float var29 = (var11 ? var12.instance : 11.0F) * var10;
      float var30 = (var11 ? var12.output : 8.0F) * var10;
      float var31 = (var11 ? var12.config : 6.0F) * var10;
      float var32 = (var11 ? var12.state : 8.0F) * var10;

      for (PotionHudRenderer.TextureState var34 : var4) {
         float var35 = Math.max(0.0F, Math.min(1.0F, var34.mode.update()));
         if (!(var35 <= 0.01F)) {
            float var36 = TextMeasureCache.handle(FontRegistry.instance, var34.compute(), var19).instance;
            float var37 = TextMeasureCache.handle(FontRegistry.instance, var34.resolve(), var20).instance;
            float var38 = TextMeasureCache.handle(FontRegistry.config, var34.update(), var19).instance;
            float var39 = var36 + (var37 > 0.0F ? var37 + 8.0F * var10 : 0.0F) + 16.0F * var10;
            float var40 = var38 + 16.0F * var10;
            float var41 = var14 * 2.0F + var16 + var17 + var39 + var17 + var40;
            float var42 = var34.check();
            int var43 = (int)(255.0F * var21 * var35 * var42);
            int var44 = PackedColor.handle(var22, (int)((var22 >> 24 & 0xFF) * var35));
            int var45 = PackedColor.handle(var23, (int)((var23 >> 24 & 0xFF) * var35));
            int var46 = PackedColor.handle(var34.apply() ? var28 : var25, var43);
            int var47 = PackedColor.handle(var26, var43);
            int var48 = PackedColor.handle(var27, var43);
            float var49 = (1.0F - var35) * 8.0F * var10;
            float var50 = var7 - var49;
            this.handle(var1, var50, var8, var41, var13, var29, var21 * var35);
            float var51 = var50 + var14;
            float var52 = var8 + var14;
            float var53 = var52 + var15 / 2.0F + 3.5F * var10;
            if (this.select()) {
               this.process(var1, var51, var52, var16, var15, var30, var21 * var35);
            } else {
               var1.handle(var51, var52, var16, var15, var30, 4.0F, 4.0F, var30, var45);
            }

            if (var34.context) {
               this.handle(var1, var34.compute(), var51, var52, var16, var15, var30, 0.7F);
            } else {
               int var54 = handle(var34.data);
               if (var54 > 0) {
                  float var55 = 18.0F * var10;
                  float var56 = var51 + (var16 - var55) / 2.0F;
                  float var57 = var52 + (var15 - var55) / 2.0F;
                  var1.update(var21 * var35 * var42);
                  var1.handle(var54, var56, var57, var55, var55, 0.0F, 0.0F, 1.0F, 1.0F);
                  var1.onTick();
               } else {
                  float var61 = TextMeasureCache.handle(FontRegistry.state, "j", 18.0F * var10).instance;
                  var1.handle(FontRegistry.state, var51 + (var16 - var61) / 2.0F, var52 + var15 / 2.0F + 5.0F * var10, 18.0F * var10, "j", var46);
               }
            }

            var51 += var16 + var17;
            if (this.select()) {
               this.process(var1, var51, var52, var39, var15, var31, var21 * var35);
            } else {
               var1.handle(var51, var52, var39, var15, var11 ? var31 : 4.0F, var45);
            }

            float var60 = var51 + 10.0F * var10;
            var1.handle(FontRegistry.instance, var60, var53, var19, var34.compute(), var46);
            if (var37 > 0.0F) {
               var1.handle(FontRegistry.instance, var60 + var36 + 8.0F * var10, var53, var20, var34.resolve(), var47);
            }

            var51 += var39 + var17;
            if (this.select()) {
               this.process(var1, var51, var52, var40, var15, var32, var21 * var35);
            } else {
               var1.handle(var51, var52, var40, var15, 4.0F, var32, var32, 4.0F, var45);
            }

            if (this.rendererScan.compute() && !var34.process()) {
               float var62 = var34.execute();
               if (var62 > 0.001F) {
                  float var63 = Math.max(3.0F * var10, var40 * var62);
                  int var64 = PackedColor.handle(var34.apply() ? var28 : var27, (int)(60.0F * var21 * var35));
                  var1.handle(var51, var52, var40, var15, 4.0F, var32, var32, 4.0F);
                  var1.handle(var51, var52, var63, var15, 0.0F, var64);
                  var1.apply();
               }
            }

            var34.enabled.handle(var34.update(), var34.prepare());
            var34.enabled
               .handle(var1, FontRegistry.config, var51, var52, var40, var15, Math.min(var32, var15 * 0.5F), var51 + var40 * 0.5F, var53, var19, var48);
            var8 += (var13 + var18) * var35;
         }
      }

      ThemeRenderer.handle().handle(var3);
      NeoStyleOptions.handle(
         var1, this, var3, ThemeRenderer.handle(), responseCompute.getWindow().getScaledWidth(), responseCompute.getWindow().getScaledHeight()
      );
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ThemeRenderer.PrimaryCacheEntry var3,
      List<PotionHudRenderer.TextureState> var4,
      float var5,
      float var6,
      float var7
   ) {
      float var8 = var3.data;
      float var9 = var3.context;
      float var10 = var3.config;
      float var11 = var3.state;
      this.handle(var8, var9, var10, var11);
      float var12 = var10 / Math.max(1.0F, var6);
      float var13 = var11 / Math.max(1.0F, var7);
      float var14 = Math.min(var12, var13);
      boolean var15 = Hud.render();
      HudElementRegistry.ColorState var16 = var15 ? HudElementRegistry.compute() : null;
      float var17 = (var15 ? var16.current : 7.0F) * var12;
      float var18 = (var15 ? var16.current : 7.0F) * var13;
      float var19 = this.windowConvert.compute() ? (var15 ? var16.mode : 32.0F) * var13 : 0.0F;
      float var20 = (var15 ? var16.selection : 22.0F) * var13;
      float var21 = (var15 ? var16.active : 5.0F) * var12;
      float var22 = (var15 ? var16.active : 5.0F) * var13;
      float var23 = 24.0F * var14;
      boolean var24 = this.presetWrite.compute();
      float var25 = var24 ? 22.0F : 0.0F;
      float var26 = 0.0F;
      float var27 = 0.0F;

      for (PotionHudRenderer.TextureState var29 : var4) {
         String var30 = var29.compute() + (var29.resolve().isEmpty() ? "" : " " + var29.resolve());
         var26 = Math.max(var26, TextMeasureCache.handle(FontRegistry.instance, var30, 24.0F).instance);
         var27 = Math.max(var27, TextMeasureCache.handle(FontRegistry.instance, var29.update(), 24.0F).instance);
      }

      float var68 = (var26 + var25 + 24.0F) * var12;
      float var69 = (var27 + 20.0F + (var15 ? var16.handler : 0.0F)) * var12;
      float var70 = var68 + var21 + var69;
      float var31 = var10 - var17 * 2.0F;
      if (var31 > var70) {
         var68 = var31 - var21 - var69;
      }

      float var32 = var5 * this.target.compute();
      int var33 = this.process(var32);
      int var34 = this.compute(var32);
      int var35 = this.update(var32);
      int var36 = this.prepare(var32);
      float var37 = var15 ? var16.instance : 14.0F;
      float var38 = var15 ? var16.data : 11.0F;
      float var39 = var15 ? var16.context : 7.0F;
      float var40 = var15 ? var16.config : var39;
      float var41 = var15 ? var16.state : var39;
      this.handle(var1, var8, var9, var10, var11, var37, var32);
      if (this.windowConvert.compute()) {
         if (this.select()) {
            this.handle(var1, var8 + var17, var9 + var18, var31, var19, var38, var32);
         } else if (var15) {
            var1.handle(var8 + var17, var9 + var18, var31, var19, var38, var33);
         } else {
            var1.handle(var8 + var17, var9 + var18, var31, var19, 11.0F, 11.0F, 4.0F, 4.0F, var33);
         }

         float var42 = var15 ? var8 + var16.pointEncode.instance * var12 : var8 + var17 + 10.0F * var12;
         float var43 = var15 ? var9 + var16.pointEncode.data * var13 : var9 + var18 + var19 / 2.0F + 6.0F * var13;
         var1.handle(FontRegistry.config, var42, var43, (var15 ? var16.enabled : 28.0F) * var14, "Potions", var35);
         float var44 = 22.0F * var13;
         float var45 = var8 + var17 + var31 - 10.0F * var12 - var44;
         float var46 = var9 + var18 + (var19 - var44) / 2.0F;
         float var47 = (var15 ? var16.renderer : 24.0F) * var14;
         float var48 = TextMeasureCache.handle(FontRegistry.state, "t", var47).instance;
         float var49 = var15 ? (var16.animator.context ? var8 + var10 : var8) + var16.animator.instance * var12 : var45 + (var44 - var48) / 2.0F;
         float var50 = var15 ? var9 + var16.animator.data * var13 : var46 + var44 / 2.0F + 5.5F * var13;
         var1.handle(FontRegistry.state, var49, var50, var47, "t", var36);
      }

      float var71 = var9 + var18 + var19 + (this.windowConvert.compute() ? var22 : 0.0F);
      float var72 = var8 + var17 + (var15 ? var16.source.instance * var12 : 0.0F);
      float var73 = var71 + (var15 ? var16.source.data * var13 : 0.0F);
      float var74 = var8 + var17 + var68 + var21 + (var15 ? var16.target.instance * var12 : 0.0F);
      float var75 = var71 + (var15 ? var16.target.data * var13 : 0.0F);
      float var76 = 0.0F;

      for (PotionHudRenderer.TextureState var79 : var4) {
         var76 += var20 * var79.mode.update();
      }

      if (var76 > 0.01F && this.prepare()) {
         if (this.select()) {
            this.process(var1, var72, var73, var68, var76, var40, var32);
            this.process(var1, var74, var75, var69, var76, var41, var32);
         } else if (var15) {
            var1.handle(var72, var73, var68, var76, var40, var34);
            var1.handle(var74, var75, var69, var76, var41, var34);
         } else {
            var1.handle(var72, var73, var68, var76, 4.0F, 4.0F, 4.0F, 11.0F, var34);
            var1.handle(var74, var75, var69, var76, 4.0F, 4.0F, 11.0F, 4.0F, var34);
         }
      }

      var1.handle(var8, var9, var10, var11, var37, var37, var37, var37);
      float var78 = var73;
      float var80 = var75;

      for (PotionHudRenderer.TextureState var51 : var4) {
         float var52 = var51.mode.update();
         if (!(var52 <= 0.01F)) {
            float var53 = var51.check();
            int var54 = (int)(255.0F * var32 * var52 * var53);
            int var55 = PackedColor.handle(this.update(1.0F), var54);
            int var56 = PackedColor.handle(this.prepare(1.0F), var54);
            if (var51.apply()) {
               var55 = PackedColor.compute(255, 85, 85, var54);
               var56 = PackedColor.compute(255, 120, 120, var54);
            }

            float var57 = (1.0F - var52) * 8.0F * var12;
            float var58 = var72 + 10.0F * var12 - var57;
            if (!var15 || var16.animationDraw > 0.05F) {
               float var59 = var15 ? var16.animationDraw * var12 : 1.9F * var12;
               var1.handle(var58, var78 + (var20 - 8.0F * var13) / 2.0F, var59, 8.0F * var13, Math.max(0.7F, var59 * 0.5F), var56);
            }

            var58 += 8.0F * var12;
            if (var24) {
               float var83 = 14.0F * var14;
               float var60 = var78 + (var20 - var83) * 0.5F;
               this.handle(var1, var51, var58, var60, var83, var32 * var52 * var53, var55);
               var58 += var83 + 6.0F * var12;
            }

            String var84 = var51.compute() + (var51.resolve().isEmpty() ? "" : " " + var51.resolve());
            var1.handle(FontRegistry.instance, var58, var78 + var20 / 2.0F + 4.0F * var13, var23, var84, var55);
            if (this.rendererScan.compute() && !var51.process()) {
               float var85 = var51.execute();
               if (var85 > 0.001F) {
                  float var61 = Math.max(2.0F, var20 - 6.0F * var13);
                  float var62 = Math.max(1.0F, var69 - 6.0F * var12);
                  float var63 = Math.max(3.0F * var12, var62 * var85);
                  float var64 = var74 + 3.0F * var12 + var57;
                  float var65 = var80 + (var20 - var61) * 0.5F;
                  float var66 = var61 * 0.4F;
                  int var67 = PackedColor.handle(var56, (int)(PackedColor.handle(var56) * 0.22F));
                  var1.handle(var64, var65, var62, var61, var66, var66, var66, var66);
                  var1.handle(var64, var65, var63, var61, 0.0F, var67);
                  var1.apply();
               }
            }

            var51.enabled.handle(var51.update(), var51.prepare());
            var51.enabled
               .handle(
                  var1,
                  FontRegistry.instance,
                  var74,
                  var80,
                  var69,
                  var20,
                  Math.min(var41, var20 * 0.5F),
                  var74 + var69 * 0.5F + var57,
                  var80 + var20 / 2.0F + 4.0F * var13,
                  var23,
                  var56
               );
            var78 += var20 * var52;
            var80 += var20 * var52;
         }
      }

      var1.apply();
      ThemeRenderer.handle().handle(var3);
      NeoStyleOptions.handle(
         var1, this, var3, ThemeRenderer.handle(), responseCompute.getWindow().getScaledWidth(), responseCompute.getWindow().getScaledHeight()
      );
   }

   private void handle(RoundedRectRenderer var1, PotionHudRenderer.TextureState var2, float var3, float var4, float var5, float var6, int var7) {
      if (var2.context) {
         this.handle(var1, var2.compute(), var3, var4, var5, var5, var5 * 0.25F, 1.0F);
      } else {
         int var8 = handle(var2.data);
         if (var8 > 0) {
            var1.update(var6);
            var1.handle(var8, var3, var4, var5, var5, 0.0F, 0.0F, 1.0F, 1.0F);
            var1.onTick();
         } else {
            float var9 = TextMeasureCache.handle(FontRegistry.state, "j", var5).instance;
            var1.handle(FontRegistry.state, var3 + (var5 - var9) * 0.5F, var4 + var5 * 0.5F + var5 * 0.28F, var5, "j", var7);
         }
      }
   }

   private void handle(RoundedRectRenderer var1, String var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = ChaosSphereHelper.handle(var2);
      if (var9 != null && !var9.isEmpty() && !(var5 <= 0.0F) && !(var6 <= 0.0F)) {
         float var10 = Math.max(1.0F, Math.min(var5, var6) * var8);
         float var11 = ItemStackOverlayRenderer.compute(var10 / 16.0F);
         float var12 = 16.0F * var11;
         float var13 = ItemStackOverlayRenderer.handle(var3);
         float var14 = ItemStackOverlayRenderer.handle(var4);
         float var15 = Math.max(1.0F, ItemStackOverlayRenderer.handle(var5));
         float var16 = Math.max(1.0F, ItemStackOverlayRenderer.handle(var6));
         float var17 = ItemStackOverlayRenderer.handle(var13 + (var15 - var12) * 0.5F);
         float var18 = ItemStackOverlayRenderer.handle(var14 + (var16 - var12) * 0.5F);
         var1.compute();
         var1.handle(var13, var14, var15, var16, var7, var7, var7, var7);

         try {
            ItemStackOverlayRenderer.handle(var1, var9, var17, var18, var11, 0, false, 0);
         } finally {
            var1.compute();
            var1.apply();
         }
      }
   }

   private static int handle(Identifier var0) {
      if (responseCompute != null && responseCompute.getTextureManager() != null) {
         AbstractTexture var1 = responseCompute.getTextureManager().getTexture(var0);
         return var1 != null && var1.getGlTexture() instanceof GlTexture var2 ? var2.getGlId() : -1;
      } else {
         return -1;
      }
   }

   private void encodePoint() {
      if (responseCompute.player != null) {
         frameCheck.clear();
         moduleCollect.clear();
         providerClose.clear();

         for (StatusEffectInstance var2 : responseCompute.player.getStatusEffects()) {
            if (!Removals.handle(var2.getEffectType())) {
               providerClose.add(var2);
            }
         }

         boolean var12 = responseCompute.currentScreen instanceof ChatScreen;
         if (var12 && providerClose.isEmpty()) {
            frameCheck.add("minecraft:fire_resistance");
            process("minecraft:fire_resistance", I18n.translate("effect.minecraft.fire_resistance", new Object[0]), 1, 8000, false);
            frameCheck.add("minecraft:strength");
            process("minecraft:strength", I18n.translate("effect.minecraft.strength", new Object[0]), 3, 2380, false);
            frameCheck.add("minecraft:poison");
            process("minecraft:poison", I18n.translate("effect.minecraft.poison", new Object[0]), 2, 240, true);
         }

         if (this.animationSchedule.compute()) {
            for (PotionHudRenderer.NamedEntry var3 : sourceBuild) {
               boolean var4 = true;
               int var5 = 0;

               for (PotionHudRenderer.DataRecord var7 : var3.reqs()) {
                  StatusEffectInstance var8 = null;
                  int var9 = 0;

                  for (int var10 = providerClose.size(); var9 < var10; var9++) {
                     StatusEffectInstance var11 = providerClose.get(var9);
                     if (var11.getEffectType().getIdAsString().equals(var7.id())
                        && (var11.getAmplifier() == var7.amp() || var11.getAmplifier() == var7.amp() - 1)) {
                        var8 = var11;
                        break;
                     }
                  }

                  if (var8 == null) {
                     var4 = false;
                     break;
                  }

                  vectorPerform[var5++] = var8;
               }

               if (var4) {
                  frameCheck.add(var3.id());
                  int var19 = 0;

                  for (int var20 = 0; var20 < var5; var20++) {
                     StatusEffectInstance var21 = vectorPerform[var20];
                     moduleCollect.add(var21);
                     if (var21.getDuration() > var19) {
                        var19 = var21.getDuration();
                     }

                     vectorPerform[var20] = null;
                  }

                  handle(var3.id(), var3.name(), 1, var19, var3.harmful());
               }
            }
         }

         for (StatusEffectInstance var16 : providerClose) {
            if (!moduleCollect.contains(var16)) {
               String var18 = var16.getEffectType().getIdAsString();
               frameCheck.add(var18);
               handle(var18, var16);
            }
         }

         for (PotionHudRenderer.TextureState var17 : providerFetch) {
            if (!frameCheck.contains(var17.instance)) {
               var17.mode.handle(0.0, 0.15F, Easings.handler, true);
            }

            var17.mode.handle();
         }

         providerFetch.removeIf(var0 -> var0.mode.update() <= 0.01F && !frameCheck.contains(var0.instance));
         providerFetch.sort(Comparator.comparingInt(PotionHudRenderer.TextureState::handle).reversed());
      }
   }

   private static void handle(String var0, String var1, int var2, int var3, boolean var4) {
      PotionHudRenderer.TextureState var5 = resolve(var0);
      if (var5 == null) {
         var5 = new PotionHudRenderer.TextureState(var0);
         var5.context = true;
         var5.config = false;
         var5.state = var1;
         var5.cache = var2;
         var5.current = var4;
         var5.output = var3;
         var5.mode.apply(0.0);
         var5.mode.handle(1.0, 0.15F, Easings.handler, false);
         providerFetch.add(var5);
      } else {
         var5.context = true;
         var5.config = false;
         var5.output = var3;
         var5.mode.handle(1.0, 0.15F, Easings.handler, true);
      }
   }

   private static void process(String var0, String var1, int var2, int var3, boolean var4) {
      PotionHudRenderer.TextureState var5 = resolve(var0);
      if (var5 == null) {
         var5 = new PotionHudRenderer.TextureState(var0);
         var5.mode.apply(0.0);
         var5.mode.handle(1.0, 0.15F, Easings.handler, false);
         providerFetch.add(var5);
      } else {
         var5.mode.handle(1.0, 0.15F, Easings.handler, true);
      }

      var5.context = false;
      var5.config = true;
      var5.active = null;
      var5.state = var1;
      var5.cache = var2;
      var5.output = var3;
      var5.current = var4;
   }

   private static void handle(String var0, StatusEffectInstance var1) {
      PotionHudRenderer.TextureState var2 = resolve(var0);
      if (var2 == null) {
         var2 = new PotionHudRenderer.TextureState(var0);
         var2.context = false;
         var2.config = false;
         var2.active = var1;
         var2.mode.apply(0.0);
         var2.mode.handle(1.0, 0.15F, Easings.handler, false);
         providerFetch.add(var2);
      } else {
         var2.context = false;
         var2.config = false;
         var2.active = var1;
         var2.mode.handle(1.0, 0.15F, Easings.handler, true);
      }
   }

   private static PotionHudRenderer.TextureState resolve(String var0) {
      for (PotionHudRenderer.TextureState var2 : providerFetch) {
         if (var2.instance.equals(var0)) {
            return var2;
         }
      }

      return null;
   }

   static String update(String var0) {
      return var0 != null && !var0.isEmpty()
         ? var0.replaceAll("(?i)\\u0412?\\u00A7[0-9A-FK-OR]", "").replace("§", "").replace("Â", "").replaceAll("\\p{Cntrl}", "").trim()
         : "";
   }

   record DataRecord(String id, int amp) {
   }

   record NamedEntry(String id, String name, boolean harmful, List<PotionHudRenderer.DataRecord> reqs) {
      public NamedEntry(String var1, String var2, boolean var3, Object... var4) {
         this(var1, var2, var3, buildReqs(var4));
      }

      private static List<PotionHudRenderer.DataRecord> buildReqs(Object[] var0) {
         ArrayList var1 = new ArrayList();

         for (byte var2 = 0; var2 < var0.length; var2 += 2) {
            var1.add(new PotionHudRenderer.DataRecord((String)var0[var2], (Integer)var0[var2 + 1]));
         }

         return var1;
      }
   }

   static final class TextureState {
      final String instance;
      final Identifier data;
      boolean context;
      boolean config;
      String state;
      int cache = 1;
      int output;
      boolean current;
      StatusEffectInstance active;
      final DoubleAnimator mode = new DoubleAnimator();
      private final DoubleAnimator selection = new DoubleAnimator();
      final AnimatedTextLabel enabled = new AnimatedTextLabel();
      private int renderer;
      private String handler;
      private String animationDraw;
      private int pointEncode = Integer.MIN_VALUE;
      private String animator;
      private int source = Integer.MIN_VALUE;
      private boolean target;

      TextureState(String var1) {
         this.instance = var1;
         int var2 = var1.indexOf(58);
         String var3 = var2 > 0 ? var1.substring(0, var2) : "minecraft";
         String var4 = var2 > 0 && var2 + 1 < var1.length() ? var1.substring(var2 + 1) : var1;
         this.data = Identifier.of(var3, "textures/mob_effect/" + var4 + ".png");
      }

      public int handle() {
         return !this.context && !this.config && this.active != null ? this.active.getDuration() : this.output;
      }

      public boolean process() {
         return !this.context && this.active != null && this.active.isInfinite();
      }

      public String compute() {
         if (!this.config && !this.context) {
            if (this.handler == null) {
               this.handler = PotionHudRenderer.update(I18n.translate(this.active.getTranslationKey(), new Object[0]));
            }

            return this.handler;
         } else {
            return PotionHudRenderer.update(this.state);
         }
      }

      public String resolve() {
         int var1 = !this.config && !this.context ? this.active.getAmplifier() + 1 : this.cache;
         if (var1 == this.pointEncode && this.animationDraw != null) {
            return this.animationDraw;
         }

         this.pointEncode = var1;
         this.animationDraw = var1 > 1 ? "lvl " + var1 : "";
         return this.animationDraw;
      }

      public String update() {
         boolean var1 = !this.context && !this.config && this.active != null && this.active.isInfinite();
         int var2 = !this.context && !this.config && this.active != null ? this.active.getDuration() : this.output;
         int var3 = var1 ? Integer.MAX_VALUE : Math.max(0, var2 / 20);
         if (var3 == this.source && var1 == this.target && this.animator != null) {
            return this.animator;
         }

         this.source = var3;
         this.target = var1;
         if (var1) {
            String var4 = PotionHudRenderer.update(StatusEffectUtil.getDurationText(this.active, 1.0F, 20.0F).getString());
            this.animator = var4 != null && !var4.isEmpty() ? var4 : "∞";
         } else {
            this.animator = var3 / 60 + (var3 % 60 < 10 ? ":0" : ":") + var3 % 60;
         }

         return this.animator;
      }

      public boolean apply() {
         if (this.config) {
            return this.current;
         } else {
            return this.context ? this.current : ((StatusEffect)this.active.getEffectType().value()).getCategory() == StatusEffectCategory.HARMFUL;
         }
      }

      public float execute() {
         this.selection.handle();
         int var1 = !this.context && !this.config && this.active != null ? this.active.getDuration() : this.output;
         if (var1 > this.renderer) {
            this.renderer = var1;
         }

         float var2 = this.renderer <= 0 ? 0.0F : Math.max(0.0F, Math.min(1.0F, (float)var1 / this.renderer));
         this.selection.handle(var2, 0.2F, Easings.handler, false);
         return this.selection.update();
      }

      public int prepare() {
         return !this.context && !this.config && this.active != null ? this.active.getDuration() : this.output;
      }

      public float check() {
         int var1 = !this.context && !this.config && this.active != null ? this.active.getDuration() : this.output;
         if (!this.context && !this.config && this.active != null && this.active.isInfinite()) {
            return 1.0F;
         }

         float var2 = Math.max(0.0F, var1 / 20.0F);
         if (var2 > 10.0F) {
            return 1.0F;
         }

         float var3 = 1.0F - var2 / 10.0F;
         float var4 = 0.8F + var3 * 4.2F;
         double var5 = System.currentTimeMillis() / 1000.0 * var4 * Math.PI * 2.0;
         return 0.68F + (float)((Math.sin(var5) + 1.0) * 0.5) * 0.32F;
      }
   }
}
