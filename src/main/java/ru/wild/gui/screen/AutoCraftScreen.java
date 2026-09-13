package ru.wild.gui.screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.wild.module.api.Module;
import ru.wild.api.setting.StringSetting;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.AnimatedUiElement;
import ru.wild.gui.widget.ModuleLayoutResult;
import ru.wild.gui.widget.ModulePanelRenderer;
import ru.wild.gui.widget.ModulePlacement;
import ru.wild.gui.widget.SettingControlRenderer;
import ru.wild.modules.misc.AutoCraft;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.render.StyledTextRenderer;

public final class AutoCraftScreen implements ModulePanelRenderer {
   private static final int instance = 6;
   private static final int data = 3;
   private static List<AutoCraftScreen.DataRecord> context;
   private final SettingControlRenderer config = new SettingControlRenderer();
   private final StyledTextRenderer state = new StyledTextRenderer();
   private final StringSetting cache = new StringSetting("AutoCraft Search", "");
   private final SpringAnimation output = new SpringAnimation(0.0F);
   private final long[] current = new long[9];
   private final Map<String, Long> active = new HashMap<>();
   private String mode = "minecraft:oak_log";
   private String selection = "";
   private boolean enabled;
   private long renderer;
   private long handler;
   private float animationDraw;
   private AutoCraft pointEncode;
   private AutoCraftScreen.Bounds animator;
   private GuiMetrics source;
   private float target;
   private float pending;
   private float previous = 1.0F;

   @Override
   public boolean handle(Module var1) {
      return var1 instanceof AutoCraft;
   }

   @Override
   public boolean handle(Module var1, ModernClickGuiState var2) {
      return false;
   }

   @Override
   public void handle(ModernClickGuiState var1) {
      this.compute(var1);
      this.animationDraw = 0.0F;
      this.output.handle(0.0F);
   }

   @Override
   public void process(ModernClickGuiState var1) {
      this.compute(var1);
   }

   @Override
   public void compute(ModernClickGuiState var1) {
      this.selection = "";
      this.enabled = false;
   }

   @Override
   public float handle(Module var1, GuiMetrics var2, ModernClickGuiState var3) {
      return var2.handle(238.0F);
   }

   @Override
   public void handle(Module var1, ModernClickGuiState var2, SpringAnimationSpec var3, SpringAnimationSpec var4) {
      var2.process("autocraft:panel", var2.invokeProfile().contains(var1) ? 1.0F : 0.0F, var3);
   }

   @Override
   public void handle(RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, ModulePlacement var4, ThemeRenderContext var5) {
      if (var4.handle() instanceof AutoCraft var6) {
         GuiMetrics var15 = var5.update();
         ThemeColors var8 = var5.apply();
         AutoCraftScreen.Bounds var9 = this.handle(var4, var15);
         this.pointEncode = var6;
         this.animator = var9;
         this.source = var15;
         this.target = this.process(var15);
         this.pending = var15.handle(3.0F);
         float var10 = Math.max(0.05F, var3.handle("autocraft:panel"));
         this.previous = !var3.filterMatrix() && var3.invokeProfile().contains(var6) ? var10 : 0.0F;
         float var11 = this.output.handle(this.animationDraw, SpringAnimation.State.handle());
         var1.update(var10);

         try {
            this.handle(var1, var9, var15, var8);
            this.handle(var1, var3, var6, var9, var15, var8);
            this.handle(var1, var3, var6, var9, var15, var8, var11);
            this.handle(var1, var3, var6, var9, var15, var5);
            this.handle(var1, var3, var9, var15);
         } finally {
            var1.onTick();
         }
      }
   }

   @Override
   public void handle(List<AnimatedUiElement> var1, ModernClickGuiState var2, ModulePlacement var3, GuiMetrics var4) {
      if (var3.handle() instanceof AutoCraft var5) {
         AutoCraftScreen.Bounds var21 = this.handle(var3, var4);
         this.pointEncode = var5;
         this.animator = var21;
         this.source = var4;
         this.target = this.process(var4);
         this.pending = var4.handle(3.0F);
         float var7 = this.process(var4);
         float var8 = var4.handle(3.0F);

         for (int var9 = 0; var9 < 9; var9++) {
            int var10 = var9;
            int var11 = var9 / 3;
            int var12 = var9 % 3;
            float var13 = var21.gridX() + var12 * (var7 + var8);
            float var14 = var21.gridY() + var11 * (var7 + var8);
            var1.add(AnimatedUiElement.handle().handle(0).handle(var13).process(var14).compute(var7).resolve(var7).handle(var3x -> {
               if (!this.mode.isBlank()) {
                  var5.source.handle(var10, this.mode);
                  this.handle(var10);
                  var3x.scheduleAnimation();
               }
            }).handle());
            var1.add(AnimatedUiElement.handle().handle(1).handle(var13).process(var14).compute(var7).resolve(var7).handle(var3x -> {
               var5.source.process(var10);
               this.handle(var10);
               var3x.scheduleAnimation();
            }).handle());
         }

         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(var21.clearX())
               .process(var21.clearY())
               .compute(var21.clearW())
               .resolve(var4.handle(14.0F))
               .handle(var2x -> {
                  var5.source.compute();
                  this.renderer = System.currentTimeMillis();

                  for (int var3x = 0; var3x < this.current.length; var3x++) {
                     this.handle(var3x);
                  }

                  var2x.scheduleAnimation();
               })
               .handle()
         );
         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(var21.searchX())
               .process(var21.searchY())
               .compute(var21.searchW())
               .resolve(var21.searchH())
               .handle(var1x -> {
                  var1x.check(false);
                  var1x.handle(this.cache);
               })
               .handle()
         );
         float var22 = this.output.process();
         List var23 = handle(this.cache.state);
         int var24 = this.handle(var21, var4);
         float var25 = this.handle(var4);
         float var26 = var4.handle(3.0F);
         float var27 = var21.catalogY() + var22;

         for (int var15 = 0; var15 < var23.size(); var15++) {
            AutoCraftScreen.DataRecord var16 = (AutoCraftScreen.DataRecord)var23.get(var15);
            int var17 = var15 / var24;
            int var18 = var15 % var24;
            float var19 = var21.catalogX() + var18 * (var25 + var26);
            float var20 = var27 + var17 * (var25 + var26);
            if (!(var20 + var25 < var21.catalogY()) && !(var20 > var21.catalogY() + var21.catalogH())) {
               var1.add(
                  AnimatedUiElement.handle()
                     .handle(0)
                     .handle(var19)
                     .process(var20)
                     .compute(var25)
                     .resolve(var25)
                     .update(var21.catalogX())
                     .apply(var21.catalogY())
                     .execute(var21.catalogW())
                     .prepare(var21.catalogH())
                     .handle(var2x -> {
                        this.mode = var16.id();
                        this.selection = var16.id();
                        this.compute(var16.id());
                        var2x.check(false);
                        var2x.handle((StringSetting)null);
                     })
                     .handle()
               );
            }
         }

         float var28 = this.process(var21, var4);
         float var29 = this.compute(var21, var4);
         float var30 = this.resolve(var21, var4);
         var1.add(
            AnimatedUiElement.handle().handle(0).handle(var28).process(var29 - var4.handle(2.0F)).compute(var30).resolve(var4.handle(18.0F)).handle(var1x -> {
               var1x.check(false);
               var1x.handle(var5.target);
            }).handle()
         );
         float var31 = var29 + var4.handle(24.0F);
         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(var28)
               .process(var31 + var4.handle(3.0F))
               .compute(var30)
               .resolve(var4.handle(26.0F))
               .handle(var4x -> this.state.handle(var4x, var5.pending, var4x.sampleLayer(), var28, var30))
               .handle()
         );
         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(this.update(var21, var4) - var4.handle(3.0F))
               .process(var21.catalogY())
               .compute(var4.handle(9.0F))
               .resolve(var21.catalogH())
               .handle(var4x -> {
                  this.enabled = true;
                  this.handle(var5, var21, var4, var4x.sendWorld());
               })
               .handle()
         );
      }
   }

   @Override
   public boolean handle(ModernClickGuiState var1, ModuleLayoutResult var2, GuiMetrics var3, float var4, float var5, double var6) {
      for (ModulePlacement var9 : var2.process()) {
         if (var9.handle() instanceof AutoCraft var10) {
            AutoCraftScreen.Bounds var13 = this.handle(var9, var3);
            if (ModuleStateHelper.handle(var4, var5, var13.catalogX(), var13.catalogY(), var13.catalogW() + var3.handle(8.0F), var13.catalogH())) {
               float var12 = this.handle(var10, var13, var3);
               this.animationDraw = this.handle(this.animationDraw + (float)var6 * var3.handle(28.0F), -var12, 0.0F);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean handle(ModernClickGuiState var1, float var2, float var3) {
      if (this.enabled) {
         this.handle(this.pointEncode, this.animator, this.source, var3);
         return true;
      }

      if (this.pointEncode != null && this.animator != null) {
         String var4 = !this.selection.isBlank() ? this.selection : this.mode;
         if (var4.isBlank()) {
            return false;
         }

         int var5 = this.handle(var2, var3);
         if (var5 == -1) {
            return !this.selection.isBlank();
         }

         if (!var4.equals(this.pointEncode.source.handle(var5))) {
            this.pointEncode.source.handle(var5, var4);
            this.handle(var5);
            var1.scheduleAnimation();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean resolve(ModernClickGuiState var1) {
      boolean var2 = this.enabled;
      boolean var3 = !this.selection.isBlank();
      this.enabled = false;
      if (var3 && this.pointEncode != null && this.animator != null) {
         int var4 = this.handle(var1.sampleLayer(), var1.sendWorld());
         if (var4 != -1 && !this.selection.equals(this.pointEncode.source.handle(var4))) {
            this.pointEncode.source.handle(var4, this.selection);
            this.handle(var4);
            var1.scheduleAnimation();
         }
      }

      this.selection = "";
      return var2 || var3;
   }

   @Override
   public boolean handle(ModernClickGuiState var1, int var2) {
      StringSetting var3 = var1.filterEntity();
      if (var3 == this.cache || this.pointEncode != null && var3 == this.pointEncode.target) {
         if (var2 == 256 || var2 == 257) {
            var1.handle((StringSetting)null);
            return true;
         }

         if (var2 == 259 && !var3.state.isEmpty()) {
            var3.state = var3.state.substring(0, var3.state.length() - 1);
            if (var3 == this.cache) {
               this.process();
               this.compute();
            } else {
               var1.scheduleAnimation();
            }

            return true;
         } else if (var3 == this.cache && var2 == 261 && !this.cache.state.isEmpty()) {
            this.cache.state = "";
            this.process();
            this.compute();
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean handle(ModernClickGuiState var1, char var2) {
      StringSetting var3 = var1.filterEntity();
      if (var3 == this.cache || this.pointEncode != null && var3 == this.pointEncode.target) {
         if (!Character.isISOControl(var2)) {
            if (var3 == this.cache && this.cache.state.length() < 64) {
               this.cache.state = this.cache.state + var2;
               this.process();
               this.compute();
            } else if (Character.isDigit(var2) && var3.state.length() < var3.output) {
               var3.state = var3.state + var2;
               var1.scheduleAnimation();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void handle(RoundedRectRenderer var1, AutoCraftScreen.Bounds var2, GuiMetrics var3, ThemeColors var4) {
      int var5 = ThemeColors.handle(var4.select(), ThemeColors.handle(var4.submit(), 34), 0.16F);
      int var6 = ThemeColors.handle(var4.drawAnimation(), ThemeColors.handle(var4.save(), 150), 0.24F);
      var1.handle(
         var2.leftX(), var2.panelY(), var2.leftW(), var2.panelH(), var3.handle(6.0F), var3.handle(7.0F), var3.handle(1.0F), ThemeColors.handle(var4.save(), 13)
      );
      var1.handle(var2.leftX(), var2.panelY(), var2.leftW(), var2.panelH(), var3.handle(6.0F), var5);
      var1.handle(var2.leftX(), var2.panelY(), var2.leftW(), var2.panelH(), var3.handle(6.0F), var6, 0.7F);
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.config,
         var2.leftX() + var3.handle(12.0F),
         var2.panelY() + var3.handle(8.0F),
         var3.handle(12.0F),
         10.0F,
         "Рецепт крафта",
         ModuleStateHelper.handle(var4)
      );
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, AutoCraft var3, AutoCraftScreen.Bounds var4, GuiMetrics var5, ThemeColors var6) {
      float var7 = this.process(var5);
      float var8 = var5.handle(3.0F);

      for (int var9 = 0; var9 < 9; var9++) {
         int var10 = var9 / 3;
         int var11 = var9 % 3;
         float var12 = var4.gridX() + var11 * (var7 + var8);
         float var13 = var4.gridY() + var10 * (var7 + var8);
         boolean var14 = var3.source.handle(var9).equals(this.mode);
         float var15 = var2.handle(
            "autocraft:slot:hover:" + var9, ModuleStateHelper.handle(var2, var12, var13, var7, var7) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
         );
         float var16 = this.handle(this.current[var9], 430L);
         float var17 = 1.0F + var15 * 0.035F + var16 * 0.08F;
         var1.handle(var17, var12 + var7 * 0.5F, var13 + var7 * 0.5F);

         try {
            if (var16 > 0.01F) {
               var1.handle(
                  var12,
                  var13,
                  var7,
                  var7,
                  var5.handle(3.0F),
                  var5.handle(8.0F) * var16,
                  var5.handle(1.0F),
                  ThemeColors.handle(var6.save(), Math.round(80.0F * var16))
               );
            }

            int var18 = var14
               ? ThemeColors.handle(var6.submit(), Math.round(46.0F + 24.0F * var15 + 38.0F * var16))
               : ThemeColors.handle(var6.onTick(), var6.refresh(), var15);
            int var19 = !var14 && !(var16 > 0.01F) ? var6.tick() : ThemeColors.handle(var6.submit(), var6.save(), Math.max(var15, var16));
            var1.handle(var12, var13, var7, var7, var5.handle(3.0F), var18);
            var1.handle(var12, var13, var7, var7, var5.handle(3.0F), var19, !var14 && !(var16 > 0.01F) ? 0.55F : 0.95F);
            ItemStack var20 = this.process(var3.source.handle(var9));
            if (!var20.isEmpty()) {
               this.handle(var1, var20, var12 + var7 * 0.23F, var13 + var7 * 0.18F, var7 * 0.54F, var4.leftX(), var4.panelY(), var4.leftW(), var4.panelH());
            }
         } finally {
            var1.check();
         }
      }

      float var24 = var2.handle(
         "autocraft:clear:hover",
         ModuleStateHelper.handle(var2, var4.clearX(), var4.clearY(), var4.clearW(), var5.handle(14.0F)) ? 1.0F : 0.0F,
         SpringAnimationSpec.onTick()
      );
      float var25 = this.handle(this.renderer, 450L);
      var1.handle(
         var4.clearX(),
         var4.clearY(),
         var4.clearW(),
         var5.handle(14.0F),
         var5.handle(3.0F),
         ThemeColors.handle(var6.select(), ThemeColors.handle(var6.submit(), 64), Math.max(var24, var25))
      );
      var1.handle(
         var4.clearX(),
         var4.clearY(),
         var4.clearW(),
         var5.handle(14.0F),
         var5.handle(3.0F),
         ThemeColors.handle(var6.render(), var6.save(), Math.max(var24, var25)),
         0.5F + var25 * 0.4F
      );
      ModuleStateHelper.handle(
         var1,
         var5,
         FontRegistry.config,
         var4.clearX() + var4.clearW() * 0.5F,
         var4.clearY(),
         var5.handle(14.0F),
         7.0F,
         "Очистить",
         ModuleStateHelper.handle(var6),
         "c"
      );
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, AutoCraft var3, AutoCraftScreen.Bounds var4, GuiMetrics var5, ThemeColors var6, float var7
   ) {
      List var8 = handle(this.cache.state);
      float var9 = var2.handle("autocraft:search:focus", var2.filterEntity() == this.cache ? 1.0F : 0.0F, SpringAnimationSpec.prepare());
      float var10 = var2.handle("autocraft:search:query", this.cache.state.isBlank() ? 0.0F : 1.0F, SpringAnimationSpec.prepare());
      float var11 = this.handle(this.handler, 360L);
      int var12 = ThemeColors.handle(var6.refresh(), ThemeColors.handle(var6.submit(), 58), Math.max(var10 * 0.45F, var9 * 0.7F));
      int var13 = ThemeColors.handle(var6.drawAnimation(), var6.save(), Math.max(var9, var11));
      var1.handle(var4.searchX(), var4.searchY(), var4.searchW(), var4.searchH(), var5.handle(4.0F), var12);
      var1.handle(var4.searchX(), var4.searchY(), var4.searchW(), var4.searchH(), var5.handle(4.0F), var13, 0.55F + var9 * 0.25F + var11 * 0.35F);
      if (var9 > 0.01F || var11 > 0.01F) {
         var1.handle(
            var4.searchX(),
            var4.searchY(),
            var4.searchW(),
            var4.searchH(),
            var5.handle(4.0F),
            var5.handle(8.0F) * Math.max(var9, var11),
            var5.handle(1.0F),
            ThemeColors.handle(var6.save(), Math.round(32.0F * Math.max(var9, var11)))
         );
      }

      String var14 = this.cache.state.isBlank() ? "Поиск" : this.cache.state + (var2.filterEntity() == this.cache ? "|" : "");
      int var15 = this.cache.state.isBlank() ? ModuleStateHelper.compute(var6) : ModuleStateHelper.handle(var6);
      String var16 = this.cache.state.isBlank() ? "" : Integer.toString(var8.size());
      float var17 = var16.isBlank() ? 0.0F : ModuleStateHelper.handle(var5, FontRegistry.config, var16, 8.0F) + var5.handle(12.0F);
      String var18 = ModuleStateHelper.handle(var5, FontRegistry.instance, var14, 8.0F, var4.searchW() - var5.handle(12.0F) - var17);
      ModuleStateHelper.handle(var1, var5, FontRegistry.instance, var4.searchX() + var5.handle(6.0F), var4.searchY(), var4.searchH(), 8.0F, var18, var15);
      if (!var16.isBlank()) {
         var1.handle(
            var4.searchX() + var4.searchW() - var17 - var5.handle(4.0F),
            var4.searchY() + var5.handle(4.0F),
            var17,
            var4.searchH() - var5.handle(8.0F),
            var5.handle(4.0F),
            ThemeColors.handle(var6.submit(), 55)
         );
         ModuleStateHelper.handle(
            var1,
            var5,
            FontRegistry.config,
            var4.searchX() + var4.searchW() - var17 * 0.5F - var5.handle(4.0F),
            var4.searchY(),
            var4.searchH(),
            8.0F,
            var16,
            var6.save(),
            "c"
         );
      }

      var1.compute();
      var1.handle(Math.round(var4.catalogX()), Math.round(var4.catalogY()), Math.round(var4.catalogW()), Math.round(var4.catalogH()));

      try {
         if (var8.isEmpty()) {
            ModuleStateHelper.handle(
               var1,
               var5,
               FontRegistry.instance,
               var4.catalogX() + var4.catalogW() * 0.5F,
               var4.catalogY() + var4.catalogH() * 0.5F - var5.handle(5.0F),
               var5.handle(10.0F),
               8.0F,
               "Нет совпадений",
               ModuleStateHelper.compute(var6),
               "c"
            );
            return;
         }

         int var19 = this.handle(var4, var5);
         float var20 = this.handle(var5);
         float var21 = var5.handle(3.0F);
         float var22 = var4.catalogY() + var7;

         for (int var23 = 0; var23 < var8.size(); var23++) {
            AutoCraftScreen.DataRecord var24 = (AutoCraftScreen.DataRecord)var8.get(var23);
            int var25 = var23 / var19;
            int var26 = var23 % var19;
            float var27 = var4.catalogX() + var26 * (var20 + var21);
            float var28 = var22 + var25 * (var20 + var21);
            if (!(var28 + var20 < var4.catalogY()) && !(var28 > var4.catalogY() + var4.catalogH())) {
               boolean var29 = var24.id().equals(this.mode);
               float var30 = var2.handle(
                  "autocraft:catalog:hover:" + var24.id(),
                  ModuleStateHelper.handle(var2, var27, var28, var20, var20) ? 1.0F : 0.0F,
                  SpringAnimationSpec.onTick()
               );
               float var31 = var2.handle("autocraft:catalog:selected:" + var24.id(), var29 ? 1.0F : 0.0F, SpringAnimationSpec.prepare());
               float var32 = this.handle(this.active.getOrDefault(var24.id(), 0L), 430L);
               float var33 = 1.0F + var30 * 0.04F + var32 * 0.1F;
               var1.handle(var33, var27 + var20 * 0.5F, var28 + var20 * 0.5F);

               try {
                  if (var32 > 0.01F) {
                     var1.handle(
                        var27,
                        var28,
                        var20,
                        var20,
                        var5.handle(3.0F),
                        var5.handle(7.0F) * var32,
                        var5.handle(1.0F),
                        ThemeColors.handle(var6.save(), Math.round(72.0F * var32))
                     );
                  }

                  var1.handle(
                     var27,
                     var28,
                     var20,
                     var20,
                     var5.handle(3.0F),
                     ThemeColors.handle(var6.select(), ThemeColors.handle(var6.submit(), 72), Math.max(var31, var30 * 0.45F))
                  );
                  var1.handle(
                     var27,
                     var28,
                     var20,
                     var20,
                     var5.handle(3.0F),
                     ThemeColors.handle(var6.render(), var6.save(), Math.max(var31, var32)),
                     !(var31 > 0.01F) && !(var32 > 0.01F) ? 0.45F : 0.9F
                  );
                  this.handle(
                     var1,
                     var24.stack(),
                     var27 + var20 * 0.16F,
                     var28 + var20 * 0.16F,
                     var20 * 0.68F,
                     var4.catalogX(),
                     var4.catalogY(),
                     var4.catalogW(),
                     var4.catalogH()
                  );
               } finally {
                  var1.check();
               }
            }
         }
      } finally {
         var1.compute();
         var1.apply();
      }

      this.handle(var1, var3, var4, var5, var6, var7);
   }

   private void handle(RoundedRectRenderer var1, AutoCraft var2, AutoCraftScreen.Bounds var3, GuiMetrics var4, ThemeColors var5, float var6) {
      float var7 = this.handle(var2, var3, var4);
      float var8 = var4.handle(3.0F);
      float var9 = this.update(var3, var4);
      float var10 = var3.catalogY();
      float var11 = var3.catalogH();
      var1.handle(var9, var10, var8, var11, var8 * 0.5F, var5.select());
      float var12 = var7 <= 0.0F ? var11 : Math.max(var4.handle(16.0F), var11 * (var11 / (var11 + var7)));
      float var13 = var7 <= 0.0F ? 0.0F : this.handle(-var6 / var7, 0.0F, 1.0F);
      float var14 = var10 + (var11 - var12) * var13;
      var1.handle(var9, var14, var8, var12, var8 * 0.5F, ThemeColors.handle(var5.encodePoint(), var5.save(), 0.45F));
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, AutoCraft var3, AutoCraftScreen.Bounds var4, GuiMetrics var5, ThemeRenderContext var6
   ) {
      float var7 = this.process(var4, var5);
      float var8 = this.compute(var4, var5);
      float var9 = this.resolve(var4, var5);
      this.config.handle(var1, var2, var3.target, var7, var8, var9, var6);
      this.config.handle(var1, var2, var3.pending, var7, var8 + var5.handle(24.0F), var9, var6);
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, AutoCraftScreen.Bounds var3, GuiMetrics var4) {
      if (!this.selection.isBlank()) {
         ItemStack var5 = this.process(this.selection);
         if (!var5.isEmpty()) {
            float var6 = var4.handle(18.0F);
            this.handle(
               var1,
               var5,
               var2.sampleLayer() - var6 * 0.5F,
               var2.sendWorld() - var6 * 0.5F,
               var6,
               var3.x() - var4.handle(20.0F),
               var3.y() - var4.handle(20.0F),
               var3.width() + var4.handle(40.0F),
               var3.height() + var4.handle(80.0F)
            );
         }
      }
   }

   private AutoCraftScreen.Bounds handle(ModulePlacement var1, GuiMetrics var2) {
      float var3 = var2.encodePoint();
      float var4 = var1.process() + var2.handle(14.0F);
      float var5 = var1.compute() + var3 + var2.handle(8.0F);
      float var6 = var1.resolve() - var2.handle(28.0F);
      float var7 = var2.handle(12.0F);
      float var8 = var2.handle(14.0F);
      float var9 = this.handle(var2);
      float var10 = var2.handle(3.0F);
      float var11 = var9 * 6.0F + var10 * 5.0F;
      float var12 = var9 * 3.0F + var10 * 2.0F;
      float var13 = this.process(var2);
      float var14 = var2.handle(3.0F);
      float var15 = var13 * 3.0F + var14 * 2.0F;
      float var16 = var15 + var8 + var11 + var2.handle(8.0F);
      float var17 = var6;
      float var18 = var2.handle(162.0F);
      float var19 = var4;
      float var20 = var19 + var7;
      float var21 = var20 + var15 + var8;
      float var22 = var5;
      float var23 = var18;
      float var24 = var20;
      float var25 = var22 + var7 + var2.handle(14.0F);
      float var26 = var17 - var7 * 2.0F;
      float var27 = var2.handle(18.0F);
      float var28 = var25 + var27 + var2.handle(8.0F);
      float var29 = var15;
      float var30 = var20;
      float var31 = var28 + var15 + var2.handle(6.0F);
      float var32 = var21;
      float var33 = var28;
      float var34 = var11;
      float var35 = var12;
      return new AutoCraftScreen.Bounds(
         var4,
         var5,
         var6,
         var18,
         var19,
         var21,
         var17,
         var11,
         var22,
         var23,
         var20,
         var28,
         var30,
         var31,
         var29,
         var24,
         var25,
         var26,
         var27,
         var32,
         var33,
         var34,
         var35
      );
   }

   private static List<AutoCraftScreen.DataRecord> handle(String var0) {
      String var1 = var0 == null ? "" : var0.trim().toLowerCase(Locale.ROOT);
      if (var1.isEmpty()) {
         return handle();
      }

      ArrayList var2 = new ArrayList();

      for (AutoCraftScreen.DataRecord var4 : handle()) {
         if (var4.id().toLowerCase(Locale.ROOT).contains(var1) || var4.label().toLowerCase(Locale.ROOT).contains(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   private static List<AutoCraftScreen.DataRecord> handle() {
      if (context != null) {
         return context;
      }

      ArrayList var0 = new ArrayList();

      for (Item var2 : Registries.ITEM) {
         if (var2 != Items.AIR) {
            Identifier var3 = Registries.ITEM.getId(var2);
            if (var3 != null && "minecraft".equals(var3.getNamespace())) {
               ItemStack var4 = var2.getDefaultStack();
               var0.add(new AutoCraftScreen.DataRecord(var3.toString(), var4.getName().getString(), var4));
            }
         }
      }

      var0.sort(Comparator.comparing(AutoCraftScreen.DataRecord::label, String.CASE_INSENSITIVE_ORDER));
      context = List.copyOf(var0);
      return context;
   }

   private ItemStack process(String var1) {
      Identifier var2 = Identifier.tryParse(var1 == null ? "" : var1);
      if (var2 == null) {
         return ItemStack.EMPTY;
      }

      Item var3 = (Item)Registries.ITEM.get(var2);
      return var3 == Items.AIR ? ItemStack.EMPTY : var3.getDefaultStack();
   }

   private void handle(RoundedRectRenderer var1, ItemStack var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      if (!(this.previous < 0.15F)) {
         if (var2 != null && !var2.isEmpty() && !(var5 <= 0.0F) && !(var8 <= 0.0F) && !(var9 <= 0.0F)) {
            if (!(var3 + var5 <= var6) && !(var4 + var5 <= var7) && !(var3 >= var6 + var8) && !(var4 >= var7 + var9)) {
               var1.handle(var6, var7, var8, var9, 0.0F, 0.0F, 0.0F, 0.0F);

               try {
                  ItemStackOverlayRenderer.handle(var1, var2, var3, var4, var5 / 16.0F, 0, false, 0);
               } finally {
                  var1.apply();
               }
            }
         }
      }
   }

   private float handle(AutoCraft var1, AutoCraftScreen.Bounds var2, GuiMetrics var3) {
      int var4 = this.handle(var2, var3);
      int var5 = Math.max(1, (handle(this.cache.state).size() + var4 - 1) / var4);
      float var6 = var5 * this.handle(var3) + Math.max(0, var5 - 1) * var3.handle(3.0F);
      return Math.max(0.0F, var6 - var2.catalogH());
   }

   private int handle(AutoCraftScreen.Bounds var1, GuiMetrics var2) {
      return 6;
   }

   private float handle(GuiMetrics var1) {
      return var1.handle(28.0F);
   }

   private float process(GuiMetrics var1) {
      return var1.handle(24.0F);
   }

   private float process(AutoCraftScreen.Bounds var1, GuiMetrics var2) {
      return var1.x() + var2.handle(12.0F);
   }

   private float compute(AutoCraftScreen.Bounds var1, GuiMetrics var2) {
      return var1.panelY() + var1.panelH() + var2.handle(10.0F);
   }

   private float resolve(AutoCraftScreen.Bounds var1, GuiMetrics var2) {
      return var1.width() - var2.handle(24.0F);
   }

   private float update(AutoCraftScreen.Bounds var1, GuiMetrics var2) {
      return var1.catalogX() + var1.catalogW() + var2.handle(3.0F);
   }

   private void handle(AutoCraft var1, AutoCraftScreen.Bounds var2, GuiMetrics var3, float var4) {
      if (var1 != null && var2 != null && var3 != null) {
         float var5 = this.handle(var1, var2, var3);
         if (var5 <= 0.0F) {
            this.animationDraw = 0.0F;
            this.output.handle(0.0F);
         } else {
            float var6 = this.handle((var4 - var2.catalogY()) / Math.max(1.0F, var2.catalogH()), 0.0F, 1.0F);
            this.animationDraw = -var5 * var6;
            this.output.handle(this.animationDraw);
         }
      }
   }

   private void process() {
      this.animationDraw = 0.0F;
      this.output.handle(0.0F);
   }

   private void handle(int var1) {
      if (var1 >= 0 && var1 < this.current.length) {
         this.current[var1] = System.currentTimeMillis();
      }
   }

   private void compute(String var1) {
      if (var1 != null && !var1.isBlank()) {
         this.active.put(var1, System.currentTimeMillis());
      }
   }

   private void compute() {
      this.handler = System.currentTimeMillis();
   }

   private float handle(long var1, long var3) {
      if (var1 > 0L && var3 > 0L) {
         float var5 = (float)(System.currentTimeMillis() - var1);
         if (var5 >= (float)var3) {
            return 0.0F;
         }

         float var6 = 1.0F - var5 / (float)var3;
         return var6 * var6;
      } else {
         return 0.0F;
      }
   }

   private int handle(float var1, float var2) {
      for (int var3 = 0; var3 < 9; var3++) {
         int var4 = var3 / 3;
         int var5 = var3 % 3;
         float var6 = this.animator.gridX() + var5 * (this.target + this.pending);
         float var7 = this.animator.gridY() + var4 * (this.target + this.pending);
         if (var1 >= var6 && var2 >= var7 && var1 < var6 + this.target && var2 < var7 + this.target) {
            return var3;
         }
      }

      return -1;
   }

   private float handle(float var1, float var2, float var3) {
      return Math.max(var2, Math.min(var3, var1));
   }

   record Bounds(
      float x,
      float y,
      float width,
      float height,
      float leftX,
      float rightX,
      float leftW,
      float rightW,
      float panelY,
      float panelH,
      float gridX,
      float gridY,
      float clearX,
      float clearY,
      float clearW,
      float searchX,
      float searchY,
      float searchW,
      float searchH,
      float catalogX,
      float catalogY,
      float catalogW,
      float catalogH
   ) {
   }

   record DataRecord(String id, String label, ItemStack stack) {
   }
}
