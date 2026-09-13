package ru.wild.gui.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ResettableSettingGroup;
import ru.wild.config.HudProfileConfig;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "StaffListHUD", process = "w")
public final class StaffListHUD extends ResettableSettingGroup {
   private static final StaffListHUD instance = new StaffListHUD();
   private static final MinecraftClient data = MinecraftClient.getInstance();
   private static final DoubleAnimator context = new DoubleAnimator();
   private static final DoubleAnimator config = new DoubleAnimator();
   private static final DoubleAnimator state = new DoubleAnimator();
   private static final DoubleAnimator cache = new DoubleAnimator();
   private static final Map<String, DoubleAnimator> output = new HashMap<>();
   private static final List<StaffListHUD.State> current = new ArrayList<>(32);
   private static boolean active;
   private static long mode;
   private static final List<String> selection = List.of(
      "helper", "moder", "staff", "admin", "curator", "stager", "sotrudnik", "pomoshnik", "стаж", "сотруд", "модер", "админ", "куратор", "хелпер"
   );
   private final BooleanSetting enabled = new BooleanSetting("Показывать верхушку", true);
   private final NumberSetting renderer = new NumberSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.05F, true);
   private final NumberSetting handler = new NumberSetting("Прозрачность тёмных элементов", 1.0F, 0.0F, 1.0F, 0.05F, true);
   private final ModeSetting animationDraw = new ModeSetting("Стилистика", "Тёмный", "Тёмный", "Светлый", "Блюр", "Феррофлюид");
   private final ChoiceSetting pointEncode = new ChoiceSetting("Визуал", new BooleanSetting("Тень", true), new BooleanSetting("Обводка", true));
   private final BooleanSetting animator = new BooleanSetting("Показывать головы", true);
   private static final Map<Character, Integer> source = new HashMap<>();

   private StaffListHUD() {
      this.handle(this.enabled);
      this.handle(this.renderer);
      this.handle(this.handler);
      this.handle(this.animationDraw);
      this.handle(this.pointEncode);
      this.handle(this.animator);
      HudProfileConfig.handle(this);
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   private void process(RoundedRectRenderer var1) {
      if (data.player != null && data.getNetworkHandler() != null) {
         long var2 = System.currentTimeMillis();
         if (var2 - mode > 500L) {
            mode = var2;
            process();
         }

         for (StaffListHUD.State var5 : current) {
            output.computeIfAbsent(var5.instance, var0 -> new DoubleAnimator()).handle(1.0, 0.2F, Easings.handler, false);
         }

         for (Entry var63 : output.entrySet()) {
            ((DoubleAnimator)var63.getValue()).handle();
            boolean var6 = false;

            for (StaffListHUD.State var8 : current) {
               if (var8.instance.equals(var63.getKey())) {
                  var6 = true;
                  break;
               }
            }

            if (!var6) {
               ((DoubleAnimator)var63.getValue()).handle(0.0, 0.2F, Easings.handler, false);
            }
         }

         boolean var62 = current.isEmpty() && !(data.currentScreen instanceof ChatScreen);
         boolean var64 = !var62;
         context.handle();
         config.handle();
         context.handle(var62 ? 0.0 : 1.0, 0.18F, Easings.handler, false);
         if (var64) {
            if (!active) {
               config.apply(-10.0);
            }

            config.handle(0.0, 0.2F, Easings.handler, false);
         } else {
            if (active) {
               config.apply(0.0);
            }

            config.handle(10.0, 0.2F, Easings.handler, false);
         }

         active = var64;
         float var65 = context.update();
         if (!(var65 <= 0.01F)) {
            float var66 = 24.0F;
            boolean var67 = this.enabled.compute();
            float var9 = var67 ? 7.0F : 0.0F;
            float var10 = var67 ? 29.48F : 0.0F;
            float var11 = 22.0F;
            float var12 = 19.37F;
            float var13 = 10.0F;
            String var14 = "Staff";
            float var15 = TextMeasureCache.handle(FontRegistry.config, var14, 30.0F).instance;
            float var16 = var13 * 2.0F + 30.0F;
            if (var67) {
               var16 = Math.max(var16, var15 + var11 + var13 * 2.0F + 24.0F);
            }

            var16 = Math.max(var16, 228.379F);

            for (Entry var18 : output.entrySet()) {
               if (((DoubleAnimator)var18.getValue()).update() > 0.01F) {
                  float var19 = TextMeasureCache.handle(FontRegistry.instance, (String)var18.getKey(), var66).instance + var13 * 2.0F + 20.0F;
                  if (this.animator.compute()) {
                     var19 += 22.0F;
                  }

                  var16 = Math.max(var16, var19);
               }
            }

            float var69 = 0.0F;

            for (DoubleAnimator var72 : output.values()) {
               var69 += var12 * Math.max(0.0F, Math.min(1.0F, var72.update()));
            }

            if (var69 > 0.01F) {
               var69 += var67 ? 5.0F : 7.0F;
            }

            float var71 = (var67 ? var9 + var10 + var9 : 12.0F) + var69;
            state.handle();
            cache.handle();
            state.handle(var16, 0.18F, Easings.handler, false);
            cache.handle(var71, 0.18F, Easings.handler, false);
            float var73 = state.update();
            float var20 = cache.update();
            float var21 = data.getWindow().getFramebufferWidth();
            float var22 = Math.max(10.0F, var21 - var73 - 10.0F);
            float var23 = 100.0F;
            ThemeRenderer.PrimaryCacheEntry var24 = ThemeRenderer.handle().handle("HUD_StaffList", var22, var23, var73, var20);
            float var25 = var24.data + config.update();
            float var26 = var24.context;
            float var27 = var24.config;
            float var28 = var24.state;
            float var29 = var27 / Math.max(1.0F, var73);
            float var30 = var28 / Math.max(1.0F, var20);
            float var31 = Math.min(var29, var30);
            float var32 = var9 * var29;
            float var33 = var67 ? var9 * var30 : 0.0F;
            float var34 = var67 ? var10 * var30 : 0.0F;
            float var35 = var12 * var30;
            float var36 = var13 * var29;
            float var37 = var66 * var31;
            int var38 = (int)(255.0F * var65 * this.renderer.compute());
            float var39 = var65 * this.renderer.compute() * this.handler.compute();
            int var40 = (int)(255.0F * var39);
            int var41 = PackedColor.compute(24, 24, 24, var38);
            int var42 = PackedColor.compute(40, 37, 40, var40);
            int var43 = PackedColor.compute(45, 45, 45, var38);
            int var44 = PackedColor.compute(255, 255, 255, var38);
            int var45 = PackedColor.compute(255, 255, 255, var38);
            int var46 = PackedColor.compute(22, 22, 22, var40);
            if (this.animationDraw.compute().equals("Светлый")) {
               var41 = PackedColor.compute(240, 240, 245, var38);
               var42 = PackedColor.compute(220, 220, 225, var40);
               var43 = PackedColor.compute(200, 200, 200, var38);
               var44 = PackedColor.compute(20, 20, 20, var38);
               int var47 = RoundedRectRenderer.ColorState.apply(255, 255);
               var45 = PackedColor.update(var47, (int)(255.0F * var65 * this.renderer.compute()));
               var46 = PackedColor.compute(200, 200, 200, var40);
            } else if (this.animationDraw.compute().equals("Блюр")) {
               var41 = PackedColor.compute(21, 22, 26, (int)(122.0F * var65 * this.renderer.compute()));
               var42 = PackedColor.compute(21, 22, 26, (int)(184.0F * var39));
               var43 = PackedColor.compute(255, 255, 255, (int)(10.0F * var65 * this.renderer.compute()));
               var46 = PackedColor.compute(255, 255, 255, (int)(10.0F * var39));
            }

            float var74 = 14.0F;
            float var48 = 10.0F;
            if (this.pointEncode.process("Тень")) {
               var1.handle(var25, var26, var27, var28, var74, 4.0F, 1.0F, PackedColor.compute(0, 0, 0, (int)(80.0F * var65 * this.renderer.compute())));
            }

            if (this.animationDraw.compute().equals("Блюр")) {
               var1.handle(23.0F);
               var1.handle(var25, var26, var27, var28, var74, var65 * this.renderer.compute());
            }

            var1.handle(var25, var26, var27, var28, var74, var41);
            if (this.pointEncode.process("Обводка")) {
               var1.handle(var25, var26, var27, var28, var74, var43, this.animationDraw.compute().equals("Блюр") ? 1.0F : 1.5F);
            }

            if (var67) {
               float var49 = var27 - var32 * 2.0F;
               if (this.animationDraw.compute().equals("Блюр")) {
                  var1.handle(23.0F);
                  var1.handle(var25 + var32, var26 + var33, var49, var34, var48, var39);
               }

               var1.handle(var25 + var32, var26 + var33, var49, var34, 10.0F * var31, 10.0F * var31, 4.0F * var31, 4.0F * var31, var42);
               var1.handle(FontRegistry.config, var25 + var32 + 12.4F * var29, var26 + var33 + var34 / 2.0F + 6.0F * var30, 30.0F * var31, var14, var44);
               float var50 = var11 * var30;
               float var51 = var25 + var32 + var49 - 6.0F * var29 - var50;
               float var52 = var26 + var33 + (var34 - var50) / 2.0F;
               var1.handle(var51, var52, var50, var50, 6.0F, var46);
               float var53 = (var66 + 4.0F) * var31;
               float var54 = TextMeasureCache.handle(FontRegistry.state, "f", var53).instance;
               var1.handle(FontRegistry.state, var51 + (var50 - var54) / 2.0F, var52 + var50 / 2.0F + 7.0F * var30, var53, "f", var45);
            }

            var1.handle(var25, var26, var27, var28, var74, var74, var74, var74);
            float var75 = var26 + (var67 ? var33 + var34 + 5.0F * var30 : 7.0F * var30);

            for (Entry var77 : output.entrySet()) {
               float var78 = Math.max(0.0F, Math.min(1.0F, ((DoubleAnimator)var77.getValue()).update()));
               if (!(var78 <= 0.01F)) {
                  StaffListHUD.State var79 = null;

                  for (StaffListHUD.State var55 : current) {
                     if (var55.instance.equals(var77.getKey())) {
                        var79 = var55;
                        break;
                     }
                  }

                  float var81 = (1.0F - var78) * 8.0F * var29;
                  float var82 = var25 + var36 - var81;
                  if (this.animator.compute() && var79 != null) {
                     float var56 = 16.0F * var31;
                     var1.handle(
                        var82,
                        var75 + (var35 - var56) / 2.0F,
                        var56,
                        var56,
                        4.0F,
                        PackedColor.compute(150, 150, 150, (int)(255.0F * var65 * this.renderer.compute()))
                     );
                     var82 += var56 + 6.0F * var29;
                  }

                  if (var79 != null) {
                     float var83 = var82;

                     for (StaffListHUD.PrimaryState var58 : var79.data) {
                        int var59 = (int)(255.0F * var65 * var78 * this.renderer.compute());
                        int var60 = PackedColor.update(var58.data, var59);
                        var1.handle(FontRegistry.instance, var83, var75 + var35 / 2.0F + 3.0F * var30, var37, var58.instance, var60);
                        var83 += TextMeasureCache.handle(FontRegistry.instance, var58.instance, var37).instance;
                     }
                  } else {
                     var1.handle(
                        FontRegistry.instance,
                        var82,
                        var75 + var35 / 2.0F + 3.0F * var30,
                        var37,
                        (String)var77.getKey(),
                        PackedColor.compute(200, 200, 200, (int)(255.0F * var65 * this.renderer.compute()))
                     );
                  }

                  var75 += var35 * var78;
               }
            }

            var1.apply();
            ThemeRenderer.handle().handle(var24);
            NeoStyleOptions.handle(var1, this, var24, ThemeRenderer.handle(), data.getWindow().getScaledWidth(), data.getWindow().getScaledHeight());
         }
      }
   }

   private static void process() {
      current.clear();
      if (data.getNetworkHandler() != null) {
         for (PlayerListEntry var1 : data.getNetworkHandler().getPlayerList()) {
            String var2 = var1.getProfile().getName();
            String var3 = var1.getDisplayName() != null ? var1.getDisplayName().getString() : var2;
            String var4 = var3.toLowerCase(Locale.ROOT);
            boolean var5 = false;

            for (String var7 : selection) {
               if (var4.contains(var7)) {
                  var5 = true;
                  break;
               }
            }

            if (var5) {
               current.add(process(var3));
            }
         }
      }
   }

   private static String handle(String var0) {
      return var0.replaceAll("(?i)§[0-9A-FK-OR]", "");
   }

   private static StaffListHUD.State process(String var0) {
      ArrayList var1 = new ArrayList();
      StringBuilder var2 = new StringBuilder();
      int var3 = -1;

      for (int var4 = 0; var4 < var0.length(); var4++) {
         char var5 = var0.charAt(var4);
         if (var5 == 167 && var4 + 1 < var0.length()) {
            char var6 = Character.toLowerCase(var0.charAt(var4 + 1));
            if (source.containsKey(var6)) {
               if (!var2.isEmpty()) {
                  var1.add(new StaffListHUD.PrimaryState(var2.toString(), var3));
                  var2.setLength(0);
               }

               var3 = source.get(var6);
            }

            var4++;
         } else {
            var2.append(var5);
         }
      }

      if (!var2.isEmpty()) {
         var1.add(new StaffListHUD.PrimaryState(var2.toString(), var3));
      }

      return new StaffListHUD.State(handle(var0), var1);
   }

   static {
      source.put('0', -16777216);
      source.put('1', -16777046);
      source.put('2', -16733696);
      source.put('3', -16733526);
      source.put('4', -5636096);
      source.put('5', -5635926);
      source.put('6', -22016);
      source.put('7', -5592406);
      source.put('8', -11184811);
      source.put('9', -11184641);
      source.put('a', -11141291);
      source.put('b', -11141121);
      source.put('c', -43691);
      source.put('d', -43521);
      source.put('e', -171);
      source.put('f', -1);
   }

   static class PrimaryState {
      final String instance;
      final int data;

      PrimaryState(String var1, int var2) {
         this.instance = var1;
         this.data = var2;
      }
   }

   static class State {
      final String instance;
      final List<StaffListHUD.PrimaryState> data;

      State(String var1, List<StaffListHUD.PrimaryState> var2) {
         this.instance = var1;
         this.data = var2;
      }
   }
}
