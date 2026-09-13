package ru.wild.gui.hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.wild.mixin.acceser.ItemCooldownManagerAccessor;
import org.wild.mixin.acceser.ItemCooldownManagerEntryAccessor;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.core.manager.HudElementRegistry;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.gui.widget.AnimatedTextLabel;
import ru.wild.modules.player.ClickPearl;
import ru.wild.modules.visuals.Hud;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "CoolDownsHUD", process = "i")
public final class CooldownHud extends ThemePresets {
   private static final CooldownHud instance = new CooldownHud();
   private static final Map<Item, CooldownHud.CacheEntry> responseCompute = new ConcurrentHashMap<>();
   private static final List<CooldownHud.CacheEntry> providerFetch = new ArrayList<>(16);
   private static final List<CooldownHud.DataRecord> profileDraw = new ArrayList<>(16);
   private static final DoubleAnimator vectorPerform = new DoubleAnimator();
   private static final DoubleAnimator eventAttach = new DoubleAnimator();
   private static final DoubleAnimator serverRead = new DoubleAnimator();
   private final BooleanSetting positionAdvance = new BooleanSetting("Показывать верхушку", true);
   private final BooleanSetting frameCheck = new BooleanSetting("Показывать иконки", true);

   private CooldownHud() {
      this.handle(this.positionAdvance);
      this.handle(this.frameCheck);
      HudProfileConfig.handle(this);
   }

   public static CooldownHud process() {
      return instance;
   }

   public static long handle(Item var0) {
      return var0 == null ? 0L : handle(new ItemStack(var0));
   }

   public static void handle(PacketEvent var0) {
      if (var0 != null && !var0.compute() && MinecraftContext.toggleState.player != null) {
         if (var0.resolve() instanceof CooldownUpdateS2CPacket var1) {
            Item var5 = (Item)Registries.ITEM.get(var1.cooldownGroup());
            if (var5 == null || var5 == Items.AIR) {
               return;
            }

            int var3 = var1.cooldown();
            if (var3 <= 0) {
               responseCompute.remove(var5);
            } else {
               CooldownHud.CacheEntry var4 = responseCompute.computeIfAbsent(var5, CooldownHud.CacheEntry::new);
               var4.handle(process(var5));
            }
         } else if (var0.resolve() instanceof PlayerRespawnS2CPacket) {
            animate();
         }
      }
   }

   public static void handle(RoundedRectRenderer var0, DrawContext var1) {
      instance.process(var0, var1);
   }

   private void process(RoundedRectRenderer var1, DrawContext var2) {
      if (MinecraftContext.toggleState.player != null && MinecraftContext.toggleState.world != null) {
         encodePoint();
         providerFetch.clear();
         profileDraw.clear();
         Iterator var3 = responseCompute.entrySet().iterator();

         while (var3.hasNext()) {
            CooldownHud.CacheEntry var4 = (CooldownHud.CacheEntry)((Entry)var3.next()).getValue();
            boolean var5 = var4.cache > 0L;
            var4.config.handle();
            var4.config.handle(var5 ? 1.0 : 0.0, 0.15F, Easings.handler, false);
            if (!var5 && !(var4.config.update() > 0.01F)) {
               var3.remove();
            } else {
               providerFetch.add(var4);
            }
         }

         providerFetch.sort(Comparator.<CooldownHud.CacheEntry>comparingLong(var0 -> -var0.cache).thenComparing(var0 -> var0.context));
         boolean var83 = !providerFetch.isEmpty() || MinecraftContext.toggleState.currentScreen instanceof ChatScreen;
         vectorPerform.handle();
         vectorPerform.handle(var83 ? 1.0 : 0.0, 0.22F, Easings.handler, false);
         float var84 = vectorPerform.update();
         if (!(var84 <= 0.01F)) {
            boolean var85 = this.positionAdvance.compute();
            boolean var6 = this.frameCheck.compute();
            boolean var7 = Hud.render();
            HudElementRegistry.ColorState var8 = var7 ? HudElementRegistry.handle("HUD_CoolDowns") : null;
            float var9 = 24.0F;
            float var10 = var7 ? var8.current : 7.0F;
            float var11 = var85 ? (var7 ? var8.mode : 32.0F) : 0.0F;
            float var12 = var7 ? var8.selection : 22.0F;
            float var13 = var7 ? var8.active : 5.0F;
            float var14 = var7 ? var8.enabled : 28.0F;
            String var15 = "Cooldowns";
            float var16 = TextMeasureCache.process(FontRegistry.config, var15, var14);
            float var17 = var85 ? var16 + 46.0F : 0.0F;
            float var18 = 0.0F;
            float var19 = 0.0F;
            float var20 = 0.0F;

            for (CooldownHud.CacheEntry var22 : providerFetch) {
               float var23 = var22.config.update();
               if (!(var23 <= 0.01F)) {
                  String var24 = refresh((float)var22.cache / 1000.0F);
                  var18 = Math.max(var18, TextMeasureCache.process(FontRegistry.instance, var22.context, var9));
                  var19 = Math.max(var19, TextMeasureCache.process(FontRegistry.instance, var24, var9));
                  var20 += var12 * var23;
               }
            }

            float var86 = var6 ? 22.0F : 0.0F;
            float var87 = var18 + var86 + 24.0F;
            float var88 = var19 + 20.0F + (var7 ? var8.handler : 0.0F);
            float var89 = providerFetch.isEmpty() ? 0.0F : var87 + var13 + var88;
            float var25 = Math.max(var17, var89) + var10 * 2.0F;
            var25 = Math.max(var25, var85 ? 104.0F : 74.0F);
            if (var89 > 0.0F) {
               float var26 = var25 - var10 * 2.0F;
               var87 = Math.max(40.0F, var26 - var13 - var88);
            }

            float var91 = var10 + var11 + (var85 && var20 > 0.01F ? var13 : 0.0F) + var20 + var10;
            eventAttach.handle();
            serverRead.handle();
            eventAttach.handle(var25, 0.18F, Easings.handler, false);
            serverRead.handle(var91, 0.18F, Easings.handler, false);
            float var27 = eventAttach.update();
            float var28 = serverRead.update();
            float var29 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
            ThemeRenderer.PrimaryCacheEntry var30 = ThemeRenderer.handle()
               .handle("HUD_CoolDowns", Math.max(10.0F, var29 - var27 - 10.0F), 140.0F, var27, var28);
            float var31 = var30.data;
            float var32 = var30.context;
            float var33 = var30.config;
            float var34 = var30.state;
            this.handle(var31, var32, var33, var34);
            float var35 = var33 / Math.max(1.0F, var27);
            float var36 = var34 / Math.max(1.0F, var28);
            float var37 = Math.min(var35, var36);
            float var38 = var10 * var35;
            float var39 = var10 * var36;
            float var40 = var11 * var36;
            float var41 = var12 * var36;
            float var42 = var13 * var35;
            float var43 = var13 * var36;
            float var44 = var9 * var37;
            float var45 = var87 * var35;
            float var46 = var88 * var35;
            float var47 = var33 - var38 * 2.0F;
            float var48 = var84 * this.target.compute();
            int var49 = this.handle(var48);
            int var50 = this.process(var48);
            int var51 = this.compute(var48);
            int var52 = this.resolve(var48);
            int var53 = this.update(var48);
            int var54 = this.prepare(var48);
            float var55 = var7 ? var8.instance : 14.0F;
            float var56 = var7 ? var8.data : 11.0F;
            float var57 = var7 ? var8.config : 7.0F;
            float var58 = var7 ? var8.state : 7.0F;
            float var59 = var7 ? var8.animationDraw : 1.9F;
            this.handle(var1, var31, var32, var33, var34, var55, var48);
            if (var85) {
               if (this.select()) {
                  this.handle(var1, var31 + var38, var32 + var39, var47, var40, var56, var48);
               } else if (var7) {
                  var1.handle(var31 + var38, var32 + var39, var47, var40, var56, var50);
               } else {
                  var1.handle(var31 + var38, var32 + var39, var47, var40, 11.0F, 11.0F, 4.0F, 4.0F, var50);
               }

               float var60 = var7 ? var31 + var8.pointEncode.instance * var35 : var31 + var38 + 10.0F * var35;
               float var61 = var7 ? var32 + var8.pointEncode.data * var36 : var32 + var39 + var40 * 0.5F + 6.0F * var36;
               var1.handle(FontRegistry.config, var60, var61, var14 * var37, var15, var53);
               float var62 = 22.0F * var36;
               float var63 = var31 + var38 + var47 - 10.0F * var35 - var62;
               float var64 = var32 + var39 + (var40 - var62) * 0.5F;
               float var65 = (var7 ? var8.renderer : var9) * var37;
               float var66 = TextMeasureCache.process(FontRegistry.state, "g", var65);
               float var67 = var7 ? (var8.animator.context ? var31 + var33 : var31) + var8.animator.instance * var35 : var63 + (var62 - var66) * 0.8F;
               float var68 = var7 ? var32 + var8.animator.data * var36 : var64 + var62 * 0.55F + 5.5F * var36;
               var1.handle(FontRegistry.state, var67, var68, var65, "g", var54);
            }

            float var92 = var32 + var39 + var40 + (var85 && var20 > 0.01F ? var43 : 0.0F);
            float var93 = var31 + var38 + (var7 ? var8.source.instance * var35 : 0.0F);
            float var94 = var92 + (var7 ? var8.source.data * var36 : 0.0F);
            float var95 = var31 + var38 + var45 + var42 + (var7 ? var8.target.instance * var35 : 0.0F);
            float var96 = var92 + (var7 ? var8.target.data * var36 : 0.0F);
            float var97 = var20 * var36;
            if (var97 > 0.01F && this.prepare()) {
               if (this.select()) {
                  this.process(var1, var93, var94, var45, var97, var57, var48);
                  this.process(var1, var95, var96, var46, var97, var58, var48);
               } else if (var7) {
                  var1.handle(var93, var94, var45, var97, var57, var51);
                  var1.handle(var95, var96, var46, var97, var58, var51);
               } else {
                  var1.handle(var93, var94, var45, var97, var85 ? 4.0F : 11.0F, var85 ? 4.0F : 11.0F, 4.0F, 11.0F, var51);
                  var1.handle(var95, var96, var46, var97, 4.0F, var85 ? 4.0F : 11.0F, 11.0F, 4.0F, var51);
               }
            }

            var1.compute();
            var1.handle(var31, var32, var33, var34, var55, var55, var55, var55);

            try {
               float var98 = var94;
               float var99 = var96;

               for (int var100 = 0; var100 < providerFetch.size(); var100++) {
                  CooldownHud.CacheEntry var69 = providerFetch.get(var100);
                  float var70 = var69.config.update();
                  if (!(var70 <= 0.01F)) {
                     String var71 = refresh((float)var69.cache / 1000.0F);
                     int var72 = (int)(255.0F * var48 * var70);
                     int var73 = PackedColor.handle(this.update(1.0F), var72);
                     int var74 = PackedColor.handle(this.prepare(1.0F), var72);
                     float var75 = (1.0F - var70) * 8.0F * var35;
                     float var76 = var93 + 10.0F * var35 - var75;
                     if (var59 > 0.05F) {
                        var1.handle(var76, var98 + (var41 - 8.0F * var36) * 0.5F, var59 * var35, 8.0F * var36, Math.max(0.7F, var59 * 0.5F) * var35, var74);
                     }

                     var76 += 8.0F * var35;
                     if (var6) {
                        float var77 = 0.9F * var37;
                        float var78 = 16.0F * var77;
                        float var79 = var98 + (var41 - var78) * 0.5F;
                        profileDraw.add(new CooldownHud.DataRecord(var69.data, var76, var79, var77, var100));
                        var76 += 20.0F * var35;
                     }

                     var1.handle(FontRegistry.instance, var76, var98 + var41 * 0.5F + 4.0F * var36, var44, var69.context, var73);
                     var69.state.handle(var71, var69.cache);
                     float var104 = var95 + var46 * 0.5F + var75;
                     float var105 = var99 + var41 * 0.5F + 4.0F * var36;
                     var69.state.handle(var1, FontRegistry.instance, var95, var99, var46, var41, Math.min(var58, var41 * 0.5F), var104, var105, var44, var74);
                     var98 += var41 * var70;
                     var99 += var41 * var70;
                  }
               }

               if (!profileDraw.isEmpty()) {
                  var1.compute();

                  for (CooldownHud.DataRecord var102 : profileDraw) {
                     ItemStackOverlayRenderer.handle(
                        var1,
                        var102.stack,
                        ItemStackOverlayRenderer.handle(var102.x),
                        ItemStackOverlayRenderer.handle(var102.y),
                        ItemStackOverlayRenderer.compute(var102.scale),
                        var102.seed,
                        false,
                        var102.seed
                     );
                  }
               }
            } finally {
               var1.compute();
               var1.apply();
            }

            ThemeRenderer.handle().handle(var30);
            NeoStyleOptions.handle(
               var1,
               this,
               var30,
               ThemeRenderer.handle(),
               MinecraftContext.toggleState.getWindow().getScaledWidth(),
               MinecraftContext.toggleState.getWindow().getScaledHeight()
            );
         }
      } else {
         animate();
         vectorPerform.apply(0.0);
         providerFetch.clear();
         profileDraw.clear();
      }
   }

   private static String refresh(float var0) {
      int var1 = Math.max(0, Math.round(var0 * 10.0F));
      return var1 / 10 + "." + var1 % 10 + "s";
   }

   private static void encodePoint() {
      if (MinecraftContext.toggleState.player == null) {
         animate();
      } else {
         for (CooldownHud.CacheEntry var1 : responseCompute.values()) {
            var1.cache = 0L;
         }

         ItemCooldownManager var9 = MinecraftContext.toggleState.player.getItemCooldownManager();
         ItemCooldownManagerAccessor var10 = (ItemCooldownManagerAccessor)var9;
         int var2 = var10.wild$getTick();

         for (Entry var4 : var10.wild$getEntries().entrySet()) {
            long var5 = handle(var4.getValue(), var2);
            if (var5 > 0L) {
               Item var7 = (Item)Registries.ITEM.get((Identifier)var4.getKey());
               if (var7 != null && var7 != Items.AIR) {
                  CooldownHud.CacheEntry var8 = responseCompute.computeIfAbsent(var7, CooldownHud.CacheEntry::new);
                  var8.handle(process(var7));
                  var8.cache = Math.max(var8.cache, var5);
               }
            }
         }
      }
   }

   private static long handle(ItemStack var0) {
      if (MinecraftContext.toggleState.player != null && var0 != null && !var0.isEmpty()) {
         ItemCooldownManager var1 = MinecraftContext.toggleState.player.getItemCooldownManager();
         ItemCooldownManagerAccessor var2 = (ItemCooldownManagerAccessor)var1;
         Object var3 = var2.wild$getEntries().get(var1.getGroup(var0));
         return var3 == null ? 0L : handle(var3, var2.wild$getTick());
      } else {
         return 0L;
      }
   }

   private static long handle(Object var0, int var1) {
      int var2 = ((ItemCooldownManagerEntryAccessor)var0).wild$getEndTick() - var1;
      return var2 > 0 ? var2 * 50L : 0L;
   }

   private static void animate() {
      if (!responseCompute.isEmpty()) {
         responseCompute.clear();
      }
   }

   static ItemStack process(Item var0) {
      if (MinecraftContext.toggleState.player != null) {
         for (int var1 = 0; var1 < 36; var1++) {
            ItemStack var2 = MinecraftContext.toggleState.player.getInventory().getStack(var1);
            if (!var2.isEmpty() && var2.isOf(var0)) {
               return var2.copy();
            }
         }

         ItemStack var3 = MinecraftContext.toggleState.player.getOffHandStack();
         if (!var3.isEmpty() && var3.isOf(var0)) {
            return var3.copy();
         }
      }

      if (var0 == Items.ENDER_PEARL) {
         ItemStack var4 = ClickPearl.refresh();
         if (!var4.isEmpty()) {
            return var4;
         }
      }

      return new ItemStack(var0);
   }

   static String handle(ItemStack var0, Item var1) {
      String var2 = var0.getName().getString();
      if (var2 != null && !var2.isBlank()) {
         return var2;
      }

      Identifier var3 = Registries.ITEM.getId(var1);
      String var4 = var3.getPath().replace('_', ' ');
      StringBuilder var5 = new StringBuilder();

      for (String var9 : var4.split(" ")) {
         if (!var9.isEmpty()) {
            var5.append(Character.toUpperCase(var9.charAt(0))).append(var9.substring(1)).append(" ");
         }
      }

      return var5.toString().trim();
   }

   static class CacheEntry {
      final Item instance;
      ItemStack data;
      String context;
      final DoubleAnimator config = new DoubleAnimator();
      final AnimatedTextLabel state = new AnimatedTextLabel();
      long cache;

      CacheEntry(Item var1) {
         this.instance = var1;
         this.handle(CooldownHud.process(var1));
         this.config.apply(0.0);
      }

      void handle(ItemStack var1) {
         this.data = var1;
         this.context = CooldownHud.handle(var1, this.instance);
      }
   }

   record DataRecord(ItemStack stack, float x, float y, float scale, int seed) {
   }
}
