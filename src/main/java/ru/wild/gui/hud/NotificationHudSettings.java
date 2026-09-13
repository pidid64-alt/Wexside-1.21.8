package ru.wild.gui.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.misc.Removals;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "Notifications", process = "w")
public final class NotificationHudSettings extends ThemePresets {
   public static final String instance = "Модули";
   public static final String responseCompute = "Свап предметов";
   public static final String providerFetch = "Эффекты";
   public static final String profileDraw = "Низкое HP";
   public static final String vectorPerform = "Предупреждения";
   public static final String eventAttach = "Поломка брони";
   private static final NotificationHudSettings serverRead = new NotificationHudSettings();
   private static final List<NotificationHudSettings.AnimationState> positionAdvance = new ArrayList<>();
   private static final Map<String, NotificationHudSettings.State> frameCheck = new HashMap<>();
   private static final DoubleAnimator moduleCollect = new DoubleAnimator();
   private static final float providerClose = 40.0F;
   private static final float presetSave = 22.0F;
   private static final float windowConvert = 28.0F;
   private static final float presetWrite = 8.0F;
   private static final int colorMeasure = 5;
   private static final float animationSchedule = 220.0F;
   private static final EquipmentSlot[] rendererScan = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
   private static final int[] sourceBuild = new int[]{100, 100, 100, 100};
   private static final Item[] outputCollapse = new Item[4];
   private static final String profileInvoke = "Свапнул на » ";
   private static final String sourceSchedule = "Скоро сломается » ";
   private static final Identifier timerRender = Identifier.of("minecraft", "textures/gui/sprites/hud/heart/full.png");
   private static float scaleSave = Float.NaN;
   private static boolean colorCompute;
   private final ChoiceSetting scaleAdapt = new ChoiceSetting(
      "Показывать",
      new BooleanSetting("Модули", true),
      new BooleanSetting("Свап предметов", true),
      new BooleanSetting("Эффекты", true),
      new BooleanSetting("Низкое HP", true),
      new BooleanSetting("Предупреждения", true),
      new BooleanSetting("Поломка брони", true)
   );

   private NotificationHudSettings() {
      this.handle(this.scaleAdapt);
      HudProfileConfig.handle(this);
   }

   public static NotificationHudSettings process() {
      return serverRead;
   }

   public static boolean resolve(String var0) {
      return serverRead.scaleAdapt.process(var0);
   }

   public static void encodePoint() {
      if (MinecraftContext.toggleState.player != null && MinecraftContext.toggleState.world != null) {
         if (resolve("Эффекты")) {
            animate();
         } else {
            frameCheck.clear();
         }

         if (resolve("Низкое HP")) {
            submit();
         } else {
            colorCompute = false;
         }

         if (resolve("Поломка брони")) {
            load();
         } else {
            save();
         }
      } else {
         frameCheck.clear();
         colorCompute = false;
         save();
      }
   }

   public static void handle(String var0, String var1, long var2) {
      handle(var0, var1, var2, "Предупреждения");
   }

   public static void handle(String var0, String var1, long var2, String var4) {
      if (resolve(var4)) {
         positionAdvance.add(new NotificationHudSettings.AnimationState(var0, var1, var2));
      }
   }

   public static void handle(ItemStack var0, String var1, long var2) {
      if (resolve("Свап предметов")) {
         String var4 = update(var1);
         if ((var4 == null || var4.isEmpty()) && var0 != null && !var0.isEmpty()) {
            var4 = update(var0.getName().getString());
         }

         if (var4 == null || var4.isEmpty()) {
            var4 = "предмет";
         }

         int var5 = apply(var1);
         if (var5 == 0 && var0 != null && var0.contains(DataComponentTypes.CUSTOM_NAME)) {
            var5 = apply(var0.getName().getString());
         }

         String var6 = "Свапнул на » " + var4;
         if (var0 != null && !var0.isEmpty()) {
            positionAdvance.add(new NotificationHudSettings.AnimationState(var0.copy(), var6, "Свапнул на » ".length(), var5, var2));
         } else {
            NotificationHudSettings.AnimationState var7 = new NotificationHudSettings.AnimationState("i", var6, var2);
            var7.current = "Свапнул на » ".length();
            var7.active = var5;
            positionAdvance.add(var7);
         }
      }
   }

   public static void handle(String var0, String var1) {
      if (resolve("Эффекты")) {
         positionAdvance.add(new NotificationHudSettings.AnimationState(execute(var0), var1 + " » Скоро закончится", 2200L));
      }
   }

   public static void process(String var0, String var1) {
      if (resolve("Эффекты")) {
         positionAdvance.add(new NotificationHudSettings.AnimationState(execute(var0), "Истёк » " + var1, 2200L));
      }
   }

   public static void handle(ItemStack var0, int var1) {
      if (resolve("Поломка брони") && var0 != null && !var0.isEmpty()) {
         String var2 = "Скоро сломается » " + var1 + "%";
         int var3 = PackedColor.compute(255, 70, 70, 255);
         positionAdvance.add(new NotificationHudSettings.AnimationState(var0.copy(), var2, "Скоро сломается » ".length(), var3, 2600L));
      }
   }

   public static void handle(String var0, boolean var1) {
      if (resolve("Модули")) {
         for (NotificationHudSettings.AnimationState var3 : positionAdvance) {
            if (var3.resolve() && var3.apply().equals(var0)) {
               var3.process(var1);
               var3.handle(System.currentTimeMillis());
               return;
            }
         }

         positionAdvance.add(new NotificationHudSettings.AnimationState(var0, var1, 1000L));
      }
   }

   public static void handle(RoundedRectRenderer var0) {
      serverRead.process(var0);
   }

   private void process(RoundedRectRenderer var1) {
      if (MinecraftContext.toggleState.player != null) {
         boolean var2 = MinecraftContext.toggleState.currentScreen instanceof ChatScreen;
         positionAdvance.removeIf(var0 -> {
            var0.drawAnimation().handle();
            return var0.compute() && var0.drawAnimation().update() <= 0.01F;
         });
         moduleCollect.handle();
         moduleCollect.handle(positionAdvance.isEmpty() && var2 ? 1.0 : 0.0, 0.22F, Easings.handler, false);
         float var3 = moduleCollect.update();
         float var4 = 38.0F;
         float var5 = 5.0F;
         float var6 = 28.0F;
         if (positionAdvance.isEmpty()) {
            if (!(var3 <= 0.01F)) {
               String var23 = "Настройте позицию";
               float var24 = 24.0F;
               float var26 = TextMeasureCache.handle(FontRegistry.instance, var23, var24).instance;
               float var28 = var26 + 20.0F;
               int var30 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
               int var31 = MinecraftContext.toggleState.getWindow().getFramebufferHeight();
               float var32 = var31 / 2.0F + 140.0F;
               ThemeRenderer.PrimaryCacheEntry var33 = this.handle(var4, var4, var32, var30, var31);
               float var34 = scaleSave - var28 * 0.5F;
               float var35 = var33.context;
               float var36 = var4;
               this.handle(var34, var35, var28, var36);
               float var37 = this.target.compute() * var3;
               int var38 = this.update(var37);
               float var20 = 12.0F;
               this.handle(var1, var34, var35, var28, var36, var20, var37);
               String var21 = handle(var23, var24, Math.max(0.0F, var28 - 20.0F));
               float var22 = TextMeasureCache.handle(FontRegistry.instance, var21, var24).instance;
               var1.handle(var34, var35, Math.max(1.0F, var28), Math.max(1.0F, var36), var20, var20, var20, var20);
               var1.handle(FontRegistry.instance, var34 + (var28 - var22) / 2.0F, var35 + var36 / 2.0F + 3.0F, var24, var21, var38);
               var1.apply();
               ThemeRenderer.handle().handle(var33);
               NeoStyleOptions.handle(
                  var1,
                  this,
                  var33,
                  ThemeRenderer.handle(),
                  MinecraftContext.toggleState.getWindow().getScaledWidth(),
                  MinecraftContext.toggleState.getWindow().getScaledHeight()
               );
            }
         } else {
            float var7 = 0.0F;
            float var8 = 0.0F;

            for (NotificationHudSettings.AnimationState var10 : positionAdvance) {
               float var11 = handle(var10, var6);
               if (var11 > var7) {
                  var7 = var11;
               }

               var8 += (var4 + var5) * var10.drawAnimation().update();
            }

            int var25 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
            int var27 = MinecraftContext.toggleState.getWindow().getFramebufferHeight();
            float var29 = var27 / 2.0F + 140.0F;
            ThemeRenderer.PrimaryCacheEntry var12 = this.handle(var8 > 0.0F ? var8 : var4, var4, var29, var25, var27);
            float var13 = var12.context;
            this.handle(scaleSave - var7 * 0.5F, var13, var7, Math.max(var4, var12.state));

            for (int var14 = positionAdvance.size() - 1; var14 >= 0; var14--) {
               NotificationHudSettings.AnimationState var15 = positionAdvance.get(var14);
               boolean var16 = !var15.compute();
               var15.drawAnimation().handle(var16 ? 1.0 : 0.0, 0.24F, Easings.handler, false);
               float var17 = var15.drawAnimation().update();
               if (!(var17 <= 0.01F)) {
                  float var18 = handle(var15, var6);
                  float var19 = scaleSave - var18 * 0.5F;
                  this.handle(var1, var19, var13, var18, var4, var17, var15, var6);
                  var13 += (var4 + var5) * var17;
               }
            }

            ThemeRenderer.handle().handle(var12);
            NeoStyleOptions.handle(
               var1,
               this,
               var12,
               ThemeRenderer.handle(),
               MinecraftContext.toggleState.getWindow().getScaledWidth(),
               MinecraftContext.toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   private ThemeRenderer.PrimaryCacheEntry handle(float var1, float var2, float var3, int var4, int var5) {
      if (!Float.isFinite(scaleSave)) {
         ThemeRenderer.DataRecord var6 = ThemeRenderer.handle().update().get("HUD_Notifications");
         if (var6 != null && var4 > 0) {
            scaleSave = var6.nx() * var4 + 110.0F;
         } else {
            scaleSave = var4 * 0.5F;
         }
      }

      float var8 = scaleSave - 110.0F;
      ThemeRenderer.PrimaryCacheEntry var7 = ThemeRenderer.handle().handle("HUD_Notifications", var8, var3, 220.0F, var1 > 0.0F ? var1 : var2);
      scaleSave = var7.data + var7.config * 0.5F;
      return var7;
   }

   private static float handle(NotificationHudSettings.AnimationState var0, float var1) {
      float var2 = TextMeasureCache.handle(FontRegistry.instance, var0.apply(), var1).instance;
      return var0.resolve() ? var2 + 20.0F + 5.0F + 40.0F : var2 + 20.0F + 5.0F + 28.0F;
   }

   private void handle(
      RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, NotificationHudSettings.AnimationState var7, float var8
   ) {
      float var9 = this.target.compute() * var6;
      int var10 = (int)(255.0F * var9);
      int var11 = this.handle(var9);
      int var12 = this.resolve(var9);
      int var13 = this.update(var9);
      int var14 = this.execute(var9);
      float var15 = 14.0F;
      float var16 = 0.8F + 0.2F * var6;
      var1.handle(var16, var2 + var4 / 2.0F, var3 + var5 / 2.0F);

      try {
         this.handle(var1, var2, var3, var4, var5, var15, var9);
         var1.handle(var2, var3, Math.max(1.0F, var4), Math.max(1.0F, var5), var15, var15, var15, var15);

         try {
            if (var7.resolve()) {
               var7.encodePoint().handle();
               var7.encodePoint().handle(var7.execute() ? 1.0 : 0.0, 0.15F, Easings.handler, false);
               float var17 = var7.encodePoint().update();
               float var18 = var2 + 10.0F;
               float var19 = var2 + var4 - 10.0F;
               float var20 = Math.max(0.0F, var19 - var18);
               float var21 = Math.min(40.0F, var20);
               float var22 = Math.min(22.0F, Math.max(8.0F, var5 - 8.0F));
               float var23 = var19 - var21;
               float var24 = var3 + var5 / 2.0F - var22 / 2.0F;
               if (var21 >= 8.0F) {
                  if (!this.refresh() && !this.render()) {
                     if (!this.handle(var23, var24, var21, var22, var22 / 2.0F, true, var9, 2)) {
                        var1.handle(var23, var24, var21, var22, var22 / 2.0F, var11);
                     }
                  } else {
                     this.process(var1, var23, var24, var21, var22, var22 / 2.0F, var9);
                  }

                  if (var17 > 0.01F) {
                     float var25 = 3.0F;
                     float var26 = Math.max(1.0F, var21 - var25 * 2.0F);
                     float var27 = Math.max(1.0F, var22 - var25 * 2.0F);
                     float var28 = Math.max(Math.min(var27, var26), var26 * var17);
                     float var29 = var27 / 2.0F;
                     var1.handle(var23 + var25, var24 + var25, var26, var27, var29, var29, var29, var29);
                     var1.process(var23 + var25, var24 + var25, var28, var27, var29, this.prepare(var9), this.check(var9));
                     var1.apply();
                  }

                  float var54 = Math.max(1.0F, Math.min(var22 - 4.0F, var21 - 4.0F) / 2.0F);
                  float var56 = var23 + 2.0F + var54 + Math.max(0.0F, var21 - var22) * var17;
                  float var58 = var24 + var22 / 2.0F;
                  boolean var60 = ThemeRenderer.handle().check()
                     && handle(ThemeRenderer.handle().execute(), ThemeRenderer.handle().prepare(), var23, var24, var21, var22);
                  if (!this.refresh() && !this.render()) {
                     if (!this.handle(var56 - var54, var58 - var54, var54 * 2.0F, var54 * 2.0F, var54, var60, var9, var60 ? 2 : 1)) {
                        var1.process(var56, var58, var54, 0.0F, 360.0F, PackedColor.compute(255, 255, 255, var10));
                     }
                  } else {
                     this.process(var1, var56 - var54, var58 - var54, var54 * 2.0F, var54 * 2.0F, var54, var9);
                  }
               }

               float var55 = var18;
               float var57 = Math.max(0.0F, var23 - 5.0F - var55);
               String var59 = handle(var7.apply(), var8, var57);
               if (!var59.isEmpty()) {
                  var1.handle(FontRegistry.instance, var55, handle(var3, var5), var8, var59, var13);
               }
            } else {
               float var38 = var2 + 10.0F;
               float var39 = var3 + var5 * 0.5F;
               if (var7.handle()) {
                  float var42 = Math.max(0.65F, (var5 - 10.0F) / 20.0F);
                  float var46 = 16.0F * var42;
                  float var50 = var38 + (28.0F - var46) * 0.5F;
                  float var52 = var39 - var46 * 0.5F;
                  var1.compute();
                  ItemStackOverlayRenderer.handle(var1, var7.prepare(), var50, var52, var42, var7.check(), false, 0);
               } else if (var7.process()) {
                  float var41 = Math.max(14.0F, (var5 - 10.0F) * 0.62F);
                  float var45 = var38 + (28.0F - var41) * 0.5F;
                  float var49 = var39 - var41 * 0.5F;
                  handle(var1, var7.onTick(), var45, var49, var41, var9);
               } else {
                  int var40 = PackedColor.handle(var14, (int)(PackedColor.handle(var14) * var6));
                  String var44 = var7.update();
                  float var48 = handle(var3, var5);
                  if (var44.contains("on")) {
                     var1.handle(FontRegistry.state, var38, var48, 28.0F, "n", var40);
                  } else if (var44.contains("off")) {
                     var1.handle(FontRegistry.state, var38, var48, 28.0F, "l", var40);
                  } else if (var44.contains("warn") || var44.contains("gg")) {
                     var1.handle(FontRegistry.cache, var38, var48 - 2.0F, 24.0F, var44.contains("warn") ? "i" : "y", var40);
                  } else if (var44.contains("cfg")) {
                     var1.handle(FontRegistry.context, var38, var48 - 2.0F, 22.0F, "G", var40);
                  } else {
                     var1.handle(FontRegistry.context, var38, var48, 28.0F, var44, var40);
                  }
               }

               float var43 = var38 + 28.0F + 5.0F;
               float var47 = Math.max(0.0F, var2 + var4 - 10.0F - var43);
               String var51 = handle(var7.apply(), var8, var47);
               int var53 = Math.min(var7.select(), var51.length());
               handle(var1, var43, handle(var3, var5), var8, var51, var13, var53, var7.refresh(), var9);
            }
         } finally {
            var1.apply();
         }
      } finally {
         var1.prepare();
      }
   }

   private static float handle(float var0, float var1) {
      return var0 + var1 * 0.5F + 5.0F;
   }

   private static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, String var4, int var5, int var6, int var7, float var8) {
      if (var4 != null && !var4.isEmpty()) {
         if (var7 != 0 && var6 > 0 && var6 < var4.length()) {
            String var9 = var4.substring(0, var6);
            String var10 = var4.substring(var6);
            if (!var9.isEmpty()) {
               var0.handle(FontRegistry.instance, var1, var2, var3, var9, var5);
            }

            float var11 = var9.isEmpty() ? 0.0F : TextMeasureCache.handle(FontRegistry.instance, var9, var3).instance;
            int var12 = PackedColor.handle(var7, Math.round(PackedColor.handle(var7) * var8));
            if (!var10.isEmpty()) {
               var0.handle(FontRegistry.instance, var1 + var11, var2, var3, var10, var12);
            }
         } else {
            var0.handle(FontRegistry.instance, var1, var2, var3, var4, var5);
         }
      }
   }

   private static String handle(String var0, float var1, float var2) {
      if (var0 != null && !var0.isEmpty() && !(var2 <= 1.0F)) {
         if (TextMeasureCache.handle(FontRegistry.instance, var0, var1).instance <= var2) {
            return var0;
         }

         String var3 = "...";
         if (TextMeasureCache.handle(FontRegistry.instance, var3, var1).instance > var2) {
            return "";
         }

         for (int var4 = var0.length(); var4 > 0; var4--) {
            String var5 = var0.substring(0, var4).trim() + var3;
            if (TextMeasureCache.handle(FontRegistry.instance, var5, var1).instance <= var2) {
               return var5;
            }
         }

         return var3;
      } else {
         return "";
      }
   }

   private static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var0 <= var2 + var4 && var1 >= var3 && var1 <= var3 + var5;
   }

   private static void animate() {
      HashSet var0 = new HashSet();

      for (StatusEffectInstance var2 : MinecraftContext.toggleState.player.getStatusEffects()) {
         if (!Removals.handle(var2.getEffectType())) {
            String var3 = var2.getEffectType().getIdAsString();
            var0.add(var3);
            NotificationHudSettings.State var4 = frameCheck.computeIfAbsent(var3, var0x -> new NotificationHudSettings.State());
            var4.instance = update(I18n.translate(var2.getTranslationKey(), new Object[0]));
            if (var2.isInfinite()) {
               var4.handle();
            } else {
               int var5 = Math.max(0, (int)Math.ceil(var2.getDuration() / 20.0));
               handle(var3, var4.instance, var5, var4);
            }
         }
      }

      Iterator var6 = frameCheck.entrySet().iterator();

      while (var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         if (!var0.contains(var7.getKey())) {
            String var8 = ((NotificationHudSettings.State)var7.getValue()).instance;
            if (var8 != null && !var8.isEmpty()) {
               process((String)var7.getKey(), var8);
            }

            var6.remove();
         }
      }
   }

   private static void handle(String var0, String var1, int var2, NotificationHudSettings.State var3) {
      if (var2 > 5) {
         var3.handle();
      } else {
         if (var2 >= 1 && !var3.data) {
            var3.data = true;
            handle(var0, var1);
         }
      }
   }

   private static void load() {
      for (int var0 = 0; var0 < rendererScan.length; var0++) {
         ItemStack var1 = MinecraftContext.toggleState.player.getEquippedStack(rendererScan[var0]);
         if (var1 != null && !var1.isEmpty() && var1.isDamageable()) {
            if (outputCollapse[var0] != var1.getItem()) {
               outputCollapse[var0] = var1.getItem();
               sourceBuild[var0] = 100;
            }

            int var2 = var1.getMaxDamage();
            if (var2 > 0) {
               int var3 = var2 - var1.getDamage();
               int var4 = (int)Math.floor(var3 * 100.0 / var2);
               if (var4 > 30) {
                  sourceBuild[var0] = 100;
               } else {
                  int var5 = var4 <= 10 ? 10 : (var4 <= 20 ? 20 : 30);
                  if (var5 < sourceBuild[var0]) {
                     sourceBuild[var0] = var5;
                     handle(var1, var4);
                  }
               }
            }
         } else {
            sourceBuild[var0] = 100;
            outputCollapse[var0] = null;
         }
      }
   }

   private static void save() {
      for (int var0 = 0; var0 < sourceBuild.length; var0++) {
         sourceBuild[var0] = 100;
         outputCollapse[var0] = null;
      }
   }

   private static void submit() {
      float var0 = MinecraftContext.toggleState.player.getHealth() + MinecraftContext.toggleState.player.getAbsorptionAmount();
      if (var0 <= 8.0F && var0 > 0.0F && !colorCompute) {
         if (resolve("Низкое HP")) {
            positionAdvance.add(new NotificationHudSettings.AnimationState(timerRender, "Низкое HP » " + String.format("%.1f", var0), 2500L));
         }

         colorCompute = true;
      } else {
         if (var0 > 10.0F) {
            colorCompute = false;
         }
      }
   }

   private static String update(String var0) {
      return var0 != null && !var0.isEmpty() ? var0.replaceAll("(?i)\\u0412?\\u00A7[0-9A-FK-OR]", "").replace("§", "").replace(' ', ' ').trim() : "";
   }

   private static int apply(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         Integer var1 = null;

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            if (var3 == 167 || var3 == '&') {
               if (var2 + 1 >= var0.length()) {
                  break;
               }

               if ((var0.charAt(var2 + 1) == 'x' || var0.charAt(var2 + 1) == 'X') && var2 + 13 < var0.length()) {
                  Integer var6 = handle(var0, var2 + 2);
                  if (var6 != null) {
                     var1 = var6;
                  }

                  var2 += 13;
               } else {
                  Formatting var4 = Formatting.byCode(var0.charAt(var2 + 1));
                  if (var4 != null) {
                     if (var4 == Formatting.RESET) {
                        var1 = null;
                     } else {
                        Integer var5 = var4.getColorValue();
                        if (var5 != null) {
                           var1 = 0xFF000000 | var5;
                        }
                     }
                  }
               }
            }
         }

         return var1 == null ? 0 : var1;
      } else {
         return 0;
      }
   }

   private static Integer handle(String var0, int var1) {
      int var2 = 0;
      int var3 = 0;

      for (int var4 = var1; var4 < var0.length() && var3 < 6; var4++) {
         char var5 = var0.charAt(var4);
         if (var5 == 167 || var5 == '&') {
            if (++var4 >= var0.length()) {
               return null;
            }

            var5 = var0.charAt(var4);
         }

         int var6 = handle(var5);
         if (var6 < 0) {
            return null;
         }

         var2 = var2 << 4 | var6;
         var3++;
      }

      return var3 == 6 ? 0xFF000000 | var2 : null;
   }

   private static int handle(char var0) {
      if (var0 >= '0' && var0 <= '9') {
         return var0 - 48;
      } else if (var0 >= 'a' && var0 <= 'f') {
         return var0 - 97 + 10;
      } else {
         return var0 >= 65 && var0 <= 70 ? var0 - 65 + 10 : -1;
      }
   }

   private static Identifier execute(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         int var1 = var0.indexOf(58);
         String var2 = var1 > 0 ? var0.substring(0, var1) : "minecraft";
         String var3 = var1 > 0 && var1 + 1 < var0.length() ? var0.substring(var1 + 1) : var0;
         return Identifier.of(var2, "textures/mob_effect/" + var3 + ".png");
      } else {
         return Identifier.of("minecraft", "textures/mob_effect/strength.png");
      }
   }

   private static void handle(RoundedRectRenderer var0, Identifier var1, float var2, float var3, float var4, float var5) {
      if (var0 != null && var1 != null && MinecraftContext.toggleState != null && MinecraftContext.toggleState.getTextureManager() != null) {
         AbstractTexture var6 = MinecraftContext.toggleState.getTextureManager().getTexture(var1);
         if (var6 != null && var6.getGlTexture() instanceof GlTexture var7 && var7.getGlId() > 0) {
            var0.compute();
            var0.update(var5);
            var0.handle(var7.getGlId(), var2, var3, var4, var4, 0.0F, 0.0F, 1.0F, 1.0F);
            var0.onTick();
         }
      }
   }

   public static class AnimationState {
      private boolean instance;
      private String data;
      private String context;
      private boolean config;
      private ItemStack state;
      private int cache;
      private Identifier output;
      int current;
      int active;
      private long mode;
      private long selection;
      private DoubleAnimator enabled = new DoubleAnimator();
      private DoubleAnimator renderer = new DoubleAnimator();

      public AnimationState(String var1, String var2, long var3) {
         this.instance = false;
         this.data = var1;
         this.context = var2;
         this.selection = var3;
         this.mode = System.currentTimeMillis();
      }

      public AnimationState(ItemStack var1, String var2, int var3, int var4, long var5) {
         this.instance = false;
         this.state = var1;
         this.cache = var1.hashCode();
         this.context = var2;
         this.current = var3;
         this.active = var4;
         this.selection = var5;
         this.mode = System.currentTimeMillis();
      }

      public AnimationState(Identifier var1, String var2, long var3) {
         this.instance = false;
         this.output = var1;
         this.context = var2;
         this.selection = var3;
         this.mode = System.currentTimeMillis();
      }

      public boolean handle() {
         return this.state != null && !this.state.isEmpty();
      }

      public boolean process() {
         return this.output != null;
      }

      public AnimationState(String var1, boolean var2, long var3) {
         this.instance = true;
         this.context = var1;
         this.config = var2;
         this.selection = var3;
         this.mode = System.currentTimeMillis();
         this.renderer.apply(var2 ? 1.0 : 0.0);
      }

      public boolean compute() {
         return System.currentTimeMillis() - this.mode > this.selection;
      }
      public boolean resolve() {
         return this.instance;
      }
      public String update() {
         return this.data;
      }
      public String apply() {
         return this.context;
      }
      public boolean execute() {
         return this.config;
      }
      public ItemStack prepare() {
         return this.state;
      }
      public int check() {
         return this.cache;
      }
      public Identifier onTick() {
         return this.output;
      }
      public int select() {
         return this.current;
      }
      public int refresh() {
         return this.active;
      }
      public long render() {
         return this.mode;
      }
      public long tick() {
         return this.selection;
      }
      public DoubleAnimator drawAnimation() {
         return this.enabled;
      }
      public DoubleAnimator encodePoint() {
         return this.renderer;
      }
      public void handle(boolean var1) {
         this.instance = var1;
      }
      public void handle(String var1) {
         this.data = var1;
      }
      public void process(String var1) {
         this.context = var1;
      }
      public void process(boolean var1) {
         this.config = var1;
      }
      public void handle(ItemStack var1) {
         this.state = var1;
      }
      public void handle(int var1) {
         this.cache = var1;
      }
      public void handle(Identifier var1) {
         this.output = var1;
      }
      public void process(int var1) {
         this.current = var1;
      }
      public void compute(int var1) {
         this.active = var1;
      }
      public void handle(long var1) {
         this.mode = var1;
      }
      public void process(long var1) {
         this.selection = var1;
      }
      public void handle(DoubleAnimator var1) {
         this.enabled = var1;
      }
      public void process(DoubleAnimator var1) {
         this.renderer = var1;
      }
      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof NotificationHudSettings.AnimationState var2)) {
            return false;
         } else {
            if (!var2.handle(this)) {
               return false;
            }

            if (this.resolve() != var2.resolve()) {
               return false;
            }

            if (this.execute() != var2.execute()) {
               return false;
            }

            if (this.check() != var2.check()) {
               return false;
            }

            if (this.select() != var2.select()) {
               return false;
            }

            if (this.refresh() != var2.refresh()) {
               return false;
            }

            if (this.render() != var2.render()) {
               return false;
            }

            if (this.tick() != var2.tick()) {
               return false;
            }

            String var3 = this.update();
            String var4 = var2.update();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.apply();
               String var6 = var2.apply();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  ItemStack var7 = this.prepare();
                  ItemStack var8 = var2.prepare();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     Identifier var9 = this.onTick();
                     Identifier var10 = var2.onTick();
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        DoubleAnimator var11 = this.drawAnimation();
                        DoubleAnimator var12 = var2.drawAnimation();
                        if (var11 == null ? var12 == null : var11.equals(var12)) {
                           DoubleAnimator var13 = this.encodePoint();
                           DoubleAnimator var14 = var2.encodePoint();
                           return var13 == null ? var14 == null : var13.equals(var14);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
      protected boolean handle(Object var1) {
         return var1 instanceof NotificationHudSettings.AnimationState;
      }
      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + (this.resolve() ? 79 : 97);
         var2 = var2 * 59 + (this.execute() ? 79 : 97);
         var2 = var2 * 59 + this.check();
         var2 = var2 * 59 + this.select();
         var2 = var2 * 59 + this.refresh();
         long var3 = this.render();
         var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
         long var5 = this.tick();
         var2 = var2 * 59 + (int)(var5 >>> 32 ^ var5);
         String var7 = this.update();
         var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
         String var8 = this.apply();
         var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
         ItemStack var9 = this.prepare();
         var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
         Identifier var10 = this.onTick();
         var2 = var2 * 59 + (var10 == null ? 43 : var10.hashCode());
         DoubleAnimator var11 = this.drawAnimation();
         var2 = var2 * 59 + (var11 == null ? 43 : var11.hashCode());
         DoubleAnimator var12 = this.encodePoint();
         return var2 * 59 + (var12 == null ? 43 : var12.hashCode());
      }
      @Override
      public String toString() {
         return "NotificationsHUD.Notification(isToggle="
            + this.resolve()
            + ", icon="
            + this.update()
            + ", text="
            + this.apply()
            + ", toggleState="
            + this.execute()
            + ", itemStack="
            + this.prepare()
            + ", itemSeed="
            + this.check()
            + ", textureIconId="
            + this.onTick()
            + ", highlightStart="
            + this.select()
            + ", highlightColor="
            + this.refresh()
            + ", createTime="
            + this.render()
            + ", duration="
            + this.tick()
            + ", animation="
            + this.drawAnimation()
            + ", toggleAnim="
            + this.encodePoint()
            + ")";
      }
   }

   static final class State {
      String instance = "";
      boolean data;

      void handle() {
         this.data = false;
      }
   }
}
