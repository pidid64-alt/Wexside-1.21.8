package ru.wild.gui.hud;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ResettableSettingGroup;
import ru.wild.command.PartyCommand;
import ru.wild.config.HudProfileConfig;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "PartyListHUD", process = "w")
public final class PartyListHudRenderer extends ResettableSettingGroup {
   private static final PartyListHudRenderer instance = new PartyListHudRenderer();
   private static final MinecraftClient data = MinecraftClient.getInstance();
   private static final DoubleAnimator context = new DoubleAnimator();
   private static final DoubleAnimator config = new DoubleAnimator();
   private static final DoubleAnimator state = new DoubleAnimator();
   private static final DoubleAnimator cache = new DoubleAnimator();
   private static final Map<String, DoubleAnimator> output = new HashMap<>();
   private static final Map<String, Identifier> current = new HashMap<>();
   private static final Map<String, List<PartyListHudRenderer.State>> active = new HashMap<>();
   private static final List<String> mode = new ArrayList<>(16);
   private static boolean selection;
   private static long enabled;
   private final BooleanSetting renderer = new BooleanSetting("Показывать верхушку", true);
   private final NumberSetting handler = new NumberSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.05F, true);
   private final NumberSetting animationDraw = new NumberSetting("Прозрачность тёмных элементов", 1.0F, 0.0F, 1.0F, 0.05F, true);
   private final ModeSetting pointEncode = new ModeSetting("Стилистика", "Тёмный", "Тёмный", "Светлый", "Блюр", "Феррофлюид");
   private final ChoiceSetting animator = new ChoiceSetting("Визуал", new BooleanSetting("Тень", true), new BooleanSetting("Обводка", true));
   private final BooleanSetting source = new BooleanSetting("Показывать здоровье", true);

   private PartyListHudRenderer() {
      this.handle(this.renderer);
      this.handle(this.handler);
      this.handle(this.animationDraw);
      this.handle(this.pointEncode);
      this.handle(this.animator);
      this.handle(this.source);
      HudProfileConfig.handle(this);
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   private void process(RoundedRectRenderer var1) {
      if (data.player != null) {
         long var2 = System.currentTimeMillis();
         if (var2 - enabled > 1000L) {
            enabled = var2;
            active.clear();
         }

         mode.clear();

         for (String var5 : PartyCommand.update()) {
            mode.add(var5.toLowerCase());
         }

         String var65 = data.player.getName().getString().toLowerCase();
         if (!mode.contains(var65)) {
            mode.add(0, var65);
         }

         for (String var6 : mode) {
            output.computeIfAbsent(var6, var0 -> new DoubleAnimator()).handle(1.0, 0.2F, Easings.handler, false);
         }

         for (Entry var69 : output.entrySet()) {
            ((DoubleAnimator)var69.getValue()).handle();
            if (!mode.contains(((String)var69.getKey()).toLowerCase())) {
               ((DoubleAnimator)var69.getValue()).handle(0.0, 0.2F, Easings.handler, false);
            }
         }

         boolean var68 = mode.isEmpty() && !(data.currentScreen instanceof ChatScreen);
         boolean var70 = !var68;
         context.handle();
         config.handle();
         context.handle(var68 ? 0.0 : 1.0, 0.18F, Easings.handler, false);
         if (var70) {
            if (!selection) {
               config.apply(-10.0);
            }

            config.handle(0.0, 0.2F, Easings.handler, false);
         } else {
            if (selection) {
               config.apply(0.0);
            }

            config.handle(10.0, 0.2F, Easings.handler, false);
         }

         selection = var70;
         float var7 = context.update();
         if (!(var7 <= 0.01F)) {
            float var8 = 24.0F;
            boolean var9 = this.renderer.compute();
            float var10 = var9 ? 7.0F : 0.0F;
            float var11 = var9 ? 32.0F : 0.0F;
            float var12 = 22.0F;
            float var13 = 28.0F;
            float var14 = 10.0F;
            String var15 = "Party";
            float var16 = TextMeasureCache.handle(FontRegistry.config, var15, 26.0F).instance;
            float var17 = var14 * 2.0F + 30.0F;
            if (var9) {
               var17 = Math.max(var17, var16 + var12 + var14 * 2.0F + 24.0F);
            }

            for (Entry var19 : output.entrySet()) {
               if (((DoubleAnimator)var19.getValue()).update() > 0.01F) {
                  List var20 = this.handle((String)var19.getKey(), PackedColor.compute(245, 245, 245, 255));
                  float var21 = 0.0F;

                  for (PartyListHudRenderer.State var23 : (List<PartyListHudRenderer.State>) var20) {
                     var21 += TextMeasureCache.handle(FontRegistry.instance, var23.instance, var8).instance;
                  }

                  float var77 = var21 + var14 * 2.0F + 26.0F;
                  if (this.source.compute()) {
                     var77 += 40.0F;
                  }

                  var17 = Math.max(var17, var77);
               }
            }

            float var71 = 0.0F;

            for (DoubleAnimator var74 : output.values()) {
               var71 += var13 * Math.max(0.0F, Math.min(1.0F, var74.update()));
            }

            if (var71 > 0.01F) {
               var71 += var9 ? 5.0F : 7.0F;
            }

            float var73 = (var9 ? var10 + var11 + 5.0F : 7.0F) + var71;
            state.handle();
            cache.handle();
            state.handle(var17, 0.18F, Easings.handler, false);
            cache.handle(var73, 0.18F, Easings.handler, false);
            float var75 = state.update();
            float var76 = cache.update();
            float var78 = data.getWindow().getFramebufferWidth();
            float var79 = 10.0F;
            float var24 = 100.0F;
            ThemeRenderer.PrimaryCacheEntry var25 = ThemeRenderer.handle().handle("HUD_PartyList", var79, var24, var75, var76);
            float var26 = var25.data + config.update();
            float var27 = var25.context;
            float var28 = var25.config;
            float var29 = var25.state;
            float var30 = var28 / Math.max(1.0F, var75);
            float var31 = var29 / Math.max(1.0F, var76);
            float var32 = Math.min(var30, var31);
            float var33 = var10 * var30;
            float var34 = var9 ? var10 * var31 : 0.0F;
            float var35 = var9 ? var11 * var31 : 0.0F;
            float var36 = var13 * var31;
            float var37 = var14 * var30;
            float var38 = var8 * var32;
            int var39 = (int)(255.0F * var7 * this.handler.compute());
            float var40 = var7 * this.handler.compute() * this.animationDraw.compute();
            int var41 = (int)(255.0F * var40);
            int var42 = PackedColor.compute(24, 24, 24, var39);
            int var43 = PackedColor.compute(40, 37, 40, var41);
            int var44 = PackedColor.compute(45, 45, 45, var39);
            int var45 = PackedColor.compute(255, 255, 255, var39);
            int var46 = PackedColor.compute(255, 255, 255, var39);
            int var47 = PackedColor.compute(22, 22, 22, var41);
            if (this.pointEncode.compute().equals("Светлый")) {
               var42 = PackedColor.compute(240, 240, 245, var39);
               var43 = PackedColor.compute(220, 220, 225, var41);
               var44 = PackedColor.compute(200, 200, 200, var39);
               var45 = PackedColor.compute(20, 20, 20, var39);
               int var48 = RoundedRectRenderer.ColorState.apply(255, 255);
               var46 = PackedColor.update(var48, (int)(255.0F * var7 * this.handler.compute()));
               var47 = PackedColor.compute(200, 200, 200, var41);
            } else if (this.pointEncode.compute().equals("Блюр")) {
               var42 = PackedColor.compute(10, 10, 10, (int)(40.0F * var7 * this.handler.compute()));
               var43 = PackedColor.compute(25, 25, 25, (int)(120.0F * var40));
               var44 = PackedColor.compute(255, 255, 255, (int)(35.0F * var7 * this.handler.compute()));
               var47 = PackedColor.compute(255, 255, 255, (int)(40.0F * var40));
            }

            float var80 = 10.0F;
            float var49 = 6.0F;
            if (this.animator.process("Тень")) {
               var1.handle(var26, var27, var28, var29, var80, 4.0F, 1.0F, PackedColor.compute(0, 0, 0, (int)(80.0F * var7 * this.handler.compute())));
            }

            if (this.pointEncode.compute().equals("Блюр")) {
               var1.handle(23.0F);
               var1.handle(var26, var27, var28, var29, var80, var7 * this.handler.compute());
            }

            var1.handle(var26, var27, var28, var29, var80, var42);
            if (this.animator.process("Обводка")) {
               var1.handle(var26, var27, var28, var29, var80, var44, this.pointEncode.compute().equals("Блюр") ? 1.0F : 1.5F);
            }

            if (var9) {
               float var50 = var28 - var33 * 2.0F;
               if (this.pointEncode.compute().equals("Блюр")) {
                  var1.handle(23.0F);
                  var1.handle(var26 + var33, var27 + var34, var50, var35, var49, var40);
               }

               var1.handle(var26 + var33, var27 + var34, var50, var35, var49, var43);
               var1.handle(FontRegistry.config, var26 + var33 + 10.0F * var30, var27 + var34 + var35 / 2.0F + 6.0F * var31, 26.0F * var32, var15, var45);
               float var51 = var12 * var31;
               float var52 = var26 + var33 + var50 - 6.0F * var30 - var51;
               float var53 = var27 + var34 + (var35 - var51) / 2.0F;
               var1.handle(var52, var53, var51, var51, 6.0F, var47);
               float var54 = (var8 + 4.0F) * var32;
               float var55 = TextMeasureCache.handle(FontRegistry.state, "p", var54).instance;
               var1.handle(FontRegistry.state, var52 + (var51 - var55) / 2.0F, var53 + var51 / 2.0F + 7.0F * var31, var54, "p", var46);
            }

            var1.handle(var26, var27, var28, var29, var80, var80, var80, var80);
            float var81 = var27 + (var9 ? var34 + var35 + 5.0F * var31 : 7.0F * var31);

            for (Entry var83 : output.entrySet()) {
               float var84 = Math.max(0.0F, Math.min(1.0F, ((DoubleAnimator)var83.getValue()).update()));
               if (!(var84 <= 0.01F)) {
                  float var85 = var84 * var84;
                  int var86 = (int)(255.0F * var7 * var85 * this.handler.compute());
                  if (var86 <= 5) {
                     var81 += var36 * var84;
                  } else {
                     String var56 = (String)var83.getKey();
                     float var57 = (1.0F - var84) * 8.0F * var30;
                     float var58 = var26 + var37 - var57;
                     float var59 = 18.0F * var32;
                     handle(var1, var56, var58, var81 + (var36 - var59) / 2.0F, var59, var7 * var85 * this.handler.compute());
                     float var60 = var58 + var59 + 6.0F * var30;

                     for (PartyListHudRenderer.State var63 : this.handle(var56, PackedColor.compute(245, 245, 245, var86))) {
                        int var64 = PackedColor.update(var63.data, var86);
                        var1.handle(FontRegistry.instance, var60, var81 + var36 / 2.0F + 3.0F * var31, var38, var63.instance, var64);
                        var60 += TextMeasureCache.handle(FontRegistry.instance, var63.instance, var38).instance;
                     }

                     if (this.source.compute()) {
                        String var87 = "20.0 HP";
                        float var88 = TextMeasureCache.handle(FontRegistry.instance, var87, var38).instance;
                        var1.handle(
                           FontRegistry.instance,
                           var26 + var28 - var37 - var88 + var57,
                           var81 + var36 / 2.0F + 3.0F * var31,
                           var38,
                           var87,
                           PackedColor.compute(100, 255, 100, var86)
                        );
                     }

                     var81 += var36 * var84;
                  }
               }
            }

            var1.apply();
            ThemeRenderer.handle().handle(var25);
            NeoStyleOptions.handle(var1, this, var25, ThemeRenderer.handle(), data.getWindow().getScaledWidth(), data.getWindow().getScaledHeight());
         }
      }
   }

   private List<PartyListHudRenderer.State> handle(String var1, int var2) {
      ArrayList var3 = new ArrayList();
      if (data.getNetworkHandler() != null) {
         for (PlayerListEntry var5 : data.getNetworkHandler().getPlayerList()) {
            if (var5.getProfile().getName().equalsIgnoreCase(var1)) {
               Text var6 = var5.getDisplayName() != null ? var5.getDisplayName() : Text.literal(var5.getProfile().getName());
               var6.visit((var2x, var3x) -> {
                  String var4 = var3x.replaceAll("(?i)§.", "").replaceAll("[^A-Za-zА-Яа-яЁё0-9\\s\\[\\]()_\\-.,!<>:|]", "");
                  if (!var4.isEmpty()) {
                     int var5x = var2;
                     if (var2x.getColor() != null) {
                        var5x = var2x.getColor().getRgb() | 0xFF000000;
                     }

                     var3.add(new PartyListHudRenderer.State(var4, var5x));
                  }

                  return Optional.empty();
               }, Style.EMPTY);
               if (!var3.isEmpty()) {
                  return var3;
               }
            }
         }
      }

      var3.add(new PartyListHudRenderer.State(var1, var2));
      return var3;
   }

   private static void handle(RoundedRectRenderer var0, String var1, float var2, float var3, float var4, float var5) {
      try {
         String var6 = var1.toLowerCase(Locale.ROOT);
         Identifier var7 = current.computeIfAbsent(var6, var1x -> {
            GameProfile var2x = new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + var1).getBytes()), var1);
            return data.getSkinProvider().getSkinTextures(var2x).texture();
         });
         AbstractTexture var8 = data.getTextureManager().getTexture(var7);
         if (!(var8.getGlTexture() instanceof GlTexture var10)) {
            return;
         }

         int var11 = var10.getGlId();
         if (var11 <= 0) {
            return;
         }

         GlStateManager._bindTexture(var11);
         var0.update(var5);
         var0.handle(var11, var2, var3, var4, var4, 0.125F, 0.125F, 0.25F, 0.25F, 4.0F);
         var0.handle(var11, var2, var3, var4, var4, 0.625F, 0.125F, 0.75F, 0.25F, 4.0F);
         var0.onTick();
      } catch (Throwable var12) {
         var0.handle(var2, var3, var4, var4, 4.0F, PackedColor.resolve(255, (int)(40.0F * var5)));
      }
   }

   static class State {
      String instance;
      int data;

      State(String var1, int var2) {
         this.instance = var1;
         this.data = var2;
      }
   }
}
