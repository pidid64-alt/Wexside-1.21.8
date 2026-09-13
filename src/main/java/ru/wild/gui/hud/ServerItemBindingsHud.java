package ru.wild.gui.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.misc.ServerHelper;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.inventory.HolyWorldHelper;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.inventory.SpecialItemCatalog;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "ServerHelper", process = "w")
public final class ServerItemBindingsHud extends ThemePresets {
   private static final ServerItemBindingsHud instance = new ServerItemBindingsHud();
   private static final MinecraftClient responseCompute = MinecraftClient.getInstance();
   private static final DoubleAnimator providerFetch = new DoubleAnimator();
   private static final DoubleAnimator profileDraw = new DoubleAnimator();
   private static final DoubleAnimator vectorPerform = new DoubleAnimator();
   private static final DoubleAnimator eventAttach = new DoubleAnimator();
   private static final Map<String, DoubleAnimator> serverRead = new HashMap<>();
   static final Map<Item, ItemStack> positionAdvance = new HashMap<>();
   private static final Map<Integer, String> frameCheck = new HashMap<>();
   private static boolean moduleCollect;
   private final BooleanSetting providerClose = new BooleanSetting("Отображать бинды", true);
   private final List<ServerItemBindingsHud.CacheEntry> presetSave = new ArrayList<>(12);
   private final List<ServerItemBindingsHud.CacheEntry> windowConvert = new ArrayList<>(12);
   private final List<ServerItemBindingsHud.ColorStop> presetWrite = new ArrayList<>(12);

   private ServerItemBindingsHud() {
      this.handle(this.providerClose);
      HudProfileConfig.handle(this);
   }

   private void handle(List<ServerItemBindingsHud.CacheEntry> var1, Item var2, int var3) {
      this.handle(var1, var2.getTranslationKey(), var2, var1x -> var1x.isOf(var2), var3);
   }

   private void handle(List<ServerItemBindingsHud.CacheEntry> var1, String var2, Item var3, Predicate<ItemStack> var4, int var5) {
      boolean var6 = var5 != -1 && var5 != 0;
      String var7 = var6 ? handle(var5) : "";
      var1.add(new ServerItemBindingsHud.CacheEntry(var2, var3, var4, var7, var6));
   }

   private static String handle(int var0) {
      String var1 = frameCheck.get(var0);
      if (var1 != null) {
         return var1;
      }

      String var2 = var0 > 0 ? InputUtil.fromKeyCode(var0, -1).getTranslationKey() : "";
      String var3 = ServerHelper.source.handle(var0, var2);
      frameCheck.put(var0, var3);
      return var3;
   }

   private List<ServerItemBindingsHud.CacheEntry> encodePoint() {
      this.presetSave.clear();
      List var1 = this.presetSave;
      ServerHelper var2 = ServerHelper.source;
      if (var2 == null) {
         return var1;
      }

      if (var2.target.process("FunTime")) {
         this.handle(var1, "ft_disorientation", Items.ENDER_EYE, var2.handle(SpecialItemCatalog::computeResponse, "Дезориентация"), var2.latest.compute());
         this.handle(var1, "ft_light_dust", Items.SUGAR, var2.handle(SpecialItemCatalog::projectItem, "Явная пыль"), var2.summary.compute());
         this.handle(var1, "ft_trap", Items.NETHERITE_SCRAP, var2.handle(SpecialItemCatalog::fetchProvider, "Трапка"), var2.itemProject.compute());
         this.handle(
            var1, "ft_freezing_snowball", Items.SNOWBALL, var2.handle(SpecialItemCatalog::renderTimer, "Снежок заморозка"), var2.responseCompute.compute()
         );
         this.handle(var1, "ft_gods_aura", Items.PHANTOM_MEMBRANE, var2.handle(SpecialItemCatalog::saveScale, "Божья аура"), var2.matrixBlend.compute());
         this.handle(var1, "ft_plast", Items.DRIED_KELP, var2.handle(SpecialItemCatalog::performVector, "Пласт"), var2.vectorMatch.compute());
         this.handle(var1, "ft_potion_assassin", Items.SPLASH_POTION, var2.handle(SpecialItemCatalog::save, "Зелье Ассасина"), var2.providerFetch.compute());
         this.handle(
            var1,
            "ft_potion_paladin",
            Items.SPLASH_POTION,
            var2.handle(SpecialItemCatalog::measure, "Зелье Паладина", "Зелье Палладина"),
            var2.profileDraw.compute()
         );
         this.handle(var1, "ft_potion_sleep", Items.SPLASH_POTION, var2.handle(SpecialItemCatalog::matchVector, "Снотворное"), var2.vectorPerform.compute());
         this.handle(var1, "ft_potion_wrath", Items.SPLASH_POTION, var2.handle(SpecialItemCatalog::submit, "Зелье Гнева"), var2.eventAttach.compute());
         this.handle(var1, "ft_potion_holy_water", Items.SPLASH_POTION, var2.handle(SpecialItemCatalog::fetch, "Святая вода"), var2.serverRead.compute());
         this.handle(
            var1, "ft_potion_radiation", Items.SPLASH_POTION, var2.handle(SpecialItemCatalog::blendMatrix, "Зелье Радиации"), var2.positionAdvance.compute()
         );
         this.handle(var1, "ft_potion_hlopushka", Items.SPLASH_POTION, var2.handle(SpecialItemCatalog::unload, "Хлопушка"), var2.frameCheck.compute());
      } else if (var2.target.process("HolyWorld")) {
         this.handle(var1, "hw_trap", Items.POPPED_CHORUS_FRUIT, HolyWorldHelper::handle, var2.providerClose.compute());
         this.handle(var1, "hw_freezing_snowball", Items.SNOWBALL, HolyWorldHelper::process, var2.presetSave.compute());
         this.handle(var1, "hw_stan", Items.NETHER_STAR, HolyWorldHelper::compute, var2.windowConvert.compute());
         this.handle(var1, "hw_explosive_trap", Items.PRISMARINE_SHARD, HolyWorldHelper::resolve, var2.presetWrite.compute());
      }

      this.handle(var1, "utility_shulker", Items.SHULKER_BOX, var0 -> var0.getItem().toString().contains("shulker_box"), var2.colorMeasure.compute());
      this.handle(var1, Items.WIND_CHARGE, var2.animationSchedule.compute());
      this.handle(var1, Items.CHORUS_FRUIT, var2.outputCollapse.compute());
      return var1;
   }

   public static ServerItemBindingsHud process() {
      return instance;
   }

   public static void handle(RoundedRectRenderer var0, DrawContext var1) {
      instance.process(var0, var1);
   }

   private void process(RoundedRectRenderer var1, DrawContext var2) {
      if (responseCompute.player != null) {
         List var3 = this.encodePoint();
         this.windowConvert.clear();
         List var4 = this.windowConvert;

         for (ServerItemBindingsHud.CacheEntry var6 : (List<ServerItemBindingsHud.CacheEntry>) var3) {
            DoubleAnimator var7 = serverRead.computeIfAbsent(var6.instance, var0 -> new DoubleAnimator());
            var7.handle();
            var7.handle(var6.state ? 1.0 : 0.0, 0.2F, Easings.handler, false);
            if (var7.update() > 0.001F || var6.state) {
               var4.add(var6);
            }
         }

         boolean var65 = responseCompute.currentScreen instanceof ChatScreen;
         boolean var66 = !var4.isEmpty() || var65;
         providerFetch.handle();
         profileDraw.handle();
         providerFetch.handle(var66 ? 1.0 : 0.0, 0.18F, Easings.handler, false);
         if (var66) {
            if (!moduleCollect) {
               profileDraw.apply(-10.0);
            }

            profileDraw.handle(0.0, 0.2F, Easings.handler, false);
         } else {
            if (moduleCollect) {
               profileDraw.apply(0.0);
            }

            profileDraw.handle(10.0, 0.2F, Easings.handler, false);
         }

         moduleCollect = var66;
         float var67 = providerFetch.update();
         if (!(var67 <= 0.01F)) {
            float var8 = 7.0F;
            float var9 = 46.0F;
            float var10 = 5.0F;
            float var11 = 0.0F;
            boolean var12 = true;

            for (ServerItemBindingsHud.CacheEntry var14 : (List<ServerItemBindingsHud.CacheEntry>) var4) {
               float var15 = serverRead.get(var14.instance).update();
               if (!(var15 <= 0.01F)) {
                  if (!var12) {
                     var11 += var10 * var15;
                  }

                  var11 += var9 * var15;
                  var12 = false;
               }
            }

            if (var4.isEmpty()) {
               var11 = var9;
            }

            float var69 = var11 + var8 * 2.0F;
            float var70 = var9 + var8 * 2.0F;
            vectorPerform.handle();
            eventAttach.handle();
            vectorPerform.handle(var69, 0.18F, Easings.handler, false);
            eventAttach.handle(var70, 0.18F, Easings.handler, false);
            float var71 = vectorPerform.update();
            float var16 = eventAttach.update();
            float var17 = responseCompute.getWindow().getFramebufferWidth();
            float var18 = responseCompute.getWindow().getFramebufferHeight();
            float var19 = (var17 - var71) / 2.0F;
            float var20 = var18 - var16 - 60.0F;
            ThemeRenderer.PrimaryCacheEntry var21 = ThemeRenderer.handle().handle("HUD_ServerHelper", var19, var20, var71, var16);
            float var22 = var21.data + profileDraw.update();
            float var23 = var21.context;
            float var24 = var21.config;
            float var25 = var21.state;
            this.handle(var22, var23, var24, var25);
            float var26 = var24 / Math.max(1.0F, var71);
            float var27 = var25 / Math.max(1.0F, var16);
            float var28 = Math.min(var26, var27);
            float var29 = var9 * var28;
            float var30 = var10 * var26;
            float var31 = var11 * var26;
            float var32 = var67 * this.target.compute();
            float var33 = this.onTick(var32);
            int var34 = this.handle(var32);
            int var35 = this.resolve(var32);
            boolean var36 = this.select();
            float var37 = 14.0F;
            this.handle(var1, var22, var23, var24, var25, var37, var32);
            var1.handle(var22, var23, var24, var25, var37, var37, var37, var37);
            float var38 = var23 + (var25 - var29) / 2.0F;
            float var39 = var22 + (var24 - var31) / 2.0F;
            this.presetWrite.clear();
            var12 = true;

            for (ServerItemBindingsHud.CacheEntry var41 : (List<ServerItemBindingsHud.CacheEntry>) var4) {
               float var42 = serverRead.get(var41.instance).update();
               if (!(var42 <= 0.01F)) {
                  if (!var12) {
                     var39 += var30 * var42;
                  }

                  var12 = false;
                  float var43 = var39;
                  int var44 = (int)(255.0F * var32 * var42);
                  int var45 = this.onTick() ? PackedColor.compute(255, 255, 255, (int)(5.0F * var33 * var42)) : this.process(var33 * var42);
                  float var46 = (1.0F - var42) * 8.0F * var27;
                  float var47 = var38 + var46;
                  if (!var36 && !this.refresh() && !this.render()) {
                     var1.handle(var43, var47, var29, var29, 6.0F * var28, var45);
                  } else {
                     this.process(var1, var43, var47, var29, var29, 6.0F * var28, var32 * var42);
                  }

                  PlayerInventory var48 = responseCompute.player.getInventory();
                  ItemStack var49 = var41.instance.startsWith("ft_potion_") ? var41.cache : null;
                  int var50 = 0;
                  int var51 = 0;

                  for (int var52 = var48.size(); var51 < var52; var51++) {
                     ItemStack var53 = var48.getStack(var51);
                     if (!var53.isEmpty() && var41.handle(var53)) {
                        var50 += var53.getCount();
                        if (var49 == null) {
                           var49 = var53;
                        }
                     }
                  }

                  if (var49 == null) {
                     var49 = var41.cache;
                  }

                  if (this.providerClose.compute()) {
                     var51 = this.apply(var32 * var42);
                     var1.handle(FontRegistry.config, var43 + 4.0F * var26, var47 + 12.0F * var27, 16.0F * var28, var41.config, var51);
                  }

                  float var82 = 1.3F * var28;
                  float var83 = 16.0F * var82;
                  float var84 = var43 + (var29 - var83) / 2.0F;
                  float var54 = var47 + (var29 - var83) / 2.0F;
                  String var55 = String.valueOf(var50);
                  int var56 = var50 > 0 ? PackedColor.compute(200, 200, 200, var44) : PackedColor.compute(255, 60, 60, var44);
                  float var57 = (this.providerClose.compute() ? 18.0F : 23.0F) * var28;
                  float var58 = TextMeasureCache.handle(FontRegistry.config, var55, var57).instance;
                  float var59 = this.providerClose.compute() ? 4.0F : 5.0F;
                  long var60 = CooldownHud.handle(var41.data);
                  String var62 = var60 > 0L ? handle(var60) : "";
                  float var63 = handle(var62, 20.0F * var28, var29 - 6.0F * var26, var28);
                  float var64 = var62.isEmpty() ? 0.0F : TextMeasureCache.process(FontRegistry.config, var62, var63);
                  this.presetWrite
                     .add(
                        new ServerItemBindingsHud.ColorStop(
                           var49,
                           var43,
                           var47,
                           var29,
                           6.0F * var28,
                           var84,
                           var54,
                           var82,
                           var32 * var42,
                           var43 + var29 - var58 - 4.0F * var26,
                           var47 + var29 - var59 * var27,
                           var57,
                           var55,
                           var56,
                           var62,
                           var43 + (var29 - var64) * 0.5F,
                           var47 + var29 * 0.5F + 5.0F * var28,
                           var63,
                           PackedColor.handle(this.execute(var32 * var42), var44)
                        )
                     );
                  var39 += var29 * var42;
               }
            }

            var1.compute();

            for (int var72 = 0; var72 < this.presetWrite.size(); var72++) {
               ServerItemBindingsHud.ColorStop var74 = this.presetWrite.get(var72);
               if (var74.alpha >= 0.35F) {
                  ItemStackOverlayRenderer.handle(var1, var74.stack, var74.itemX, var74.itemY, var74.itemScale, var72, false, 0);
               }
            }

            var1.compute();
            boolean var73 = false;

            for (int var75 = 0; var75 < this.presetWrite.size(); var75++) {
               if (this.presetWrite.get(var75).hasCooldown()) {
                  var73 = true;
                  break;
               }
            }

            if (var73) {
               var1.handle(18.0F);

               for (ServerItemBindingsHud.ColorStop var78 : this.presetWrite) {
                  if (var78.hasCooldown()) {
                     var1.handle(var78.slotX, var78.slotY, var78.slotSize, var78.slotSize, var78.slotRadius, var78.alpha);
                     var1.handle(
                        var78.slotX, var78.slotY, var78.slotSize, var78.slotSize, var78.slotRadius, PackedColor.compute(0, 0, 0, (int)(116.0F * var78.alpha))
                     );
                  }
               }
            }

            for (ServerItemBindingsHud.ColorStop var79 : this.presetWrite) {
               if (var79.hasCooldown()) {
                  int var80 = PackedColor.compute(0, 0, 0, (int)(130.0F * var79.alpha));
                  var1.handle(FontRegistry.config, var79.cooldownX + 1.0F, var79.cooldownY + 1.0F, var79.cooldownFont, var79.cooldown, var80);
                  var1.handle(FontRegistry.config, var79.cooldownX, var79.cooldownY, var79.cooldownFont, var79.cooldown, var79.cooldownColor);
               } else {
                  var1.handle(FontRegistry.config, var79.countX, var79.countY, var79.countFont, var79.count, var79.countColor);
               }
            }

            var1.compute();
            var1.apply();
            ThemeRenderer.handle().handle(var21);
            NeoStyleOptions.handle(
               var1, this, var21, ThemeRenderer.handle(), responseCompute.getWindow().getScaledWidth(), responseCompute.getWindow().getScaledHeight()
            );
         }
      }
   }

   private static String handle(long var0) {
      int var2 = Math.max(1, (int)Math.ceil(var0 / 1000.0));
      return var2 + "сек";
   }

   private static float handle(String var0, float var1, float var2, float var3) {
      if (var0 != null && !var0.isEmpty()) {
         float var4 = TextMeasureCache.process(FontRegistry.config, var0, var1);
         return var4 <= var2 ? var1 : Math.max(12.0F * var3, var1 * var2 / Math.max(1.0F, var4));
      } else {
         return var1;
      }
   }

   static class CacheEntry {
      final String instance;
      final Item data;
      final Predicate<ItemStack> context;
      final String config;
      final boolean state;
      final ItemStack cache;

      CacheEntry(String var1, Item var2, Predicate<ItemStack> var3, String var4, boolean var5) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = ServerItemBindingsHud.positionAdvance.computeIfAbsent(var2, ItemStack::new);
      }

      boolean handle(ItemStack var1) {
         try {
            return this.context.test(var1);
         } catch (Throwable var3) {
            return false;
         }
      }
   }

   record ColorStop(
      ItemStack stack,
      float slotX,
      float slotY,
      float slotSize,
      float slotRadius,
      float itemX,
      float itemY,
      float itemScale,
      float alpha,
      float countX,
      float countY,
      float countFont,
      String count,
      int countColor,
      String cooldown,
      float cooldownX,
      float cooldownY,
      float cooldownFont,
      int cooldownColor
   ) {

      boolean hasCooldown() {
         return this.cooldown != null && !this.cooldown.isEmpty();
      }
   }
}
