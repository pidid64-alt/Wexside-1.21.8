package ru.wild.gui.theme;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntPredicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import ru.wild.automation.BackgroundClientPlayer;
import ru.wild.automation.HeadlessBotConnector;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.automation.HeadlessBotSession;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.screen.ViewportLayoutState;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.render.RoundedRectRenderer;

public final class PreviewPlayerGenerator {
   private static final long instance = 4000L;
   private static final long data = 2500L;
   private static final String[] context = new String[]{"Wild", "Swift", "Nova", "Frost", "Shadow", "Lunar", "Pixel", "Turbo", "Lucky", "Silent"};
   private static final String[] config = new String[]{"Fox", "Wolf", "Bot", "Raven", "Panda", "Ghost", "Tiger", "Moth", "Bee", "Axolotl"};
   private final PreviewPlayerGenerator.ScreenState state = new PreviewPlayerGenerator.ScreenState(
      16, var0 -> var0 < 128 && (Character.isLetterOrDigit(var0) || var0 == 95)
   );
   private final PreviewPlayerGenerator.ScreenState cache = new PreviewPlayerGenerator.ScreenState(
      255, var0 -> var0 >= 32 && var0 < 127 && !Character.isWhitespace(var0)
   );
   private final PreviewPlayerGenerator.ScreenState output = new PreviewPlayerGenerator.ScreenState(256, var0 -> var0 >= 32 && !Character.isISOControl(var0));
   private String current;
   private String active;
   private long mode;
   private boolean selection;
   private float enabled;
   private String renderer = "";
   private boolean handler;
   private long animationDraw;

   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      PreviewPlayerGenerator.ServerEntry var7 = PreviewPlayerGenerator.ServerEntry.of(var3, var5);
      List var8 = this.handle(var2.encodeVector());
      HeadlessBotEngine.ServerEntry var9 = this.handle(var8);
      this.handle(var1, var2, var5, var6, var7, var8, var9);
      this.handle(var1, var2, var5, var6, var7, var9);
      this.handle(var1, var5, var6, var7);
      if (this.selection) {
         this.handle(var1, var2, var5, var6, var7);
      }
   }

   public boolean handle(ModernClickGuiState var1, ViewportLayoutState var2, ThemeRenderContext var3, float var4, float var5, int var6) {
      PreviewPlayerGenerator.ServerEntry var7 = PreviewPlayerGenerator.ServerEntry.of(var2, var3.update());
      if (!var7.content.contains(var4, var5)) {
         this.process();
         return false;
      }

      var1.check(false);
      if (var6 != 0 && var6 != 1) {
         return true;
      }

      if (this.selection) {
         if (var6 != 0) {
            return true;
         }

         if (var7.randomNameButton.contains(var4, var5)) {
            this.state.handle(apply());
            this.state.handle(true);
            this.cache.handle(false);
            return true;
         }

         boolean var14 = this.state.handle(var7.nameField, var4, var5);
         boolean var16 = this.cache.handle(var7.addressField, var4, var5);
         if (!var14 && !var16) {
            this.state.handle(false);
            this.cache.handle(false);
         }

         if (var7.addSubmit.contains(var4, var5)) {
            this.update();
         } else if (var7.addCancel.contains(var4, var5) || !var7.modal.contains(var4, var5)) {
            this.resolve();
         }

         return true;
      } else {
         if (var6 == 0 && var7.addButton.contains(var4, var5)) {
            this.compute();
            return true;
         }

         if (var6 == 0 && var7.hostButton.contains(var4, var5)) {
            this.process();
            var1.unload();
            var1.handle((HeadlessBotSession)null);
            var1.handle(var1.bindIndex());
            return true;
         }

         List<HeadlessBotEngine.ServerEntry> var8 = this.handle(var1.encodeVector());
         if (var7.listViewport.contains(var4, var5)) {
            float var9 = var7.rowHeight;
            float var10 = var7.listViewport.y - this.enabled;

            for (HeadlessBotEngine.ServerEntry var12 : var8) {
               PreviewPlayerGenerator.DataRecord var13 = new PreviewPlayerGenerator.DataRecord(var7.listViewport.x, var10, var7.listViewport.w, var9);
               if (var13.intersects(var7.listViewport) && var13.contains(var4, var5)) {
                  this.process(var12.name());
                  this.output.handle(false);
                  if (var6 == 1 && var12.bot() != null) {
                     if (HeadlessBotEngine.handle() == var12.bot()) {
                        HeadlessBotEngine.resolve();
                        this.handle("Управление возвращено основному аккаунту", false);
                     } else if (!HeadlessBotEngine.handle(var12.bot())) {
                        this.handle("Бот ещё не готов к управлению", true);
                     }
                  }

                  return true;
               }

               var10 += var9 + var7.rowGap;
            }
         }

         if (var6 != 0) {
            this.output.handle(false);
            return true;
         }

         HeadlessBotEngine.ServerEntry var15 = this.handle(var8);
         if (var15 == null) {
            this.output.handle(false);
            return true;
         }

         this.output.handle(var7.chatField.contains(var4, var5));
         if (this.output.process()) {
            return true;
         }

         HeadlessBotSession var17 = var15.bot();
         if (var7.controlButton.contains(var4, var5)) {
            if (var17 != null && var17.onTick()) {
               if (HeadlessBotEngine.handle() == var17) {
                  HeadlessBotEngine.resolve();
                  this.handle("Управление возвращено Host", false);
               } else if (HeadlessBotEngine.handle(var17)) {
                  this.handle("Теперь вы управляете " + var17.handle(), false);
               } else {
                  this.handle("Не удалось переключить управление", true);
               }
            } else {
               this.handle("Бот не находится в игровом мире", true);
            }
         } else if (var7.modulesButton.contains(var4, var5)) {
            if (var17 != null && var17.onTick()) {
               var1.unload();
               var1.handle(var17);
               var1.handle(var1.bindIndex());
            } else {
               this.handle("Модули доступны после входа бота", true);
            }
         } else if (var7.reconnectButton.contains(var4, var5)) {
            if (HeadlessBotEngine.process(var15.name())) {
               this.handle("Переподключение запущено", false);
            } else {
               this.handle("Не удалось запустить переподключение", true);
            }
         } else if (var7.disconnectButton.contains(var4, var5)) {
            if (HeadlessBotEngine.compute(var15.name())) {
               this.handle("Бот отключён, профиль сохранён", false);
            } else {
               this.handle("Бот уже отключён", true);
            }
         } else if (var7.forgetButton.contains(var4, var5)) {
            long var18 = System.currentTimeMillis();
            if (!var15.name().equalsIgnoreCase(this.active) || var18 - this.mode > 2500L) {
               this.active = var15.name();
               this.mode = var18;
               this.handle("Нажмите «Удалить?» ещё раз", true);
            } else if (HeadlessBotEngine.resolve(var15.name())) {
               this.process((String)null);
               this.handle("Профиль удалён", false);
            } else {
               this.handle("Сначала отключите бота", true);
            }
         } else if (var7.sendButton.contains(var4, var5)) {
            this.handle(var15);
         }

         return true;
      }
   }

   public boolean handle(float var1, float var2, int var3) {
      return this.selection || this.state.process() || this.cache.process() || this.output.process();
   }

   public boolean handle(float var1, float var2, int var3, float var4, float var5) {
      return this.selection;
   }

   public boolean handle(ModernClickGuiState var1, ViewportLayoutState var2, ThemeRenderContext var3, float var4, float var5, double var6) {
      PreviewPlayerGenerator.ServerEntry var8 = PreviewPlayerGenerator.ServerEntry.of(var2, var3.update());
      if (!var8.listViewport.contains(var4, var5)) {
         return var8.content.contains(var4, var5);
      }

      List var9 = this.handle(var1.encodeVector());
      float var10 = var9.size() * var8.rowHeight + Math.max(0, var9.size() - 1) * var8.rowGap;
      float var11 = Math.max(0.0F, var10 - var8.listViewport.h);
      this.enabled = handle(this.enabled - (float)var6 * var3.update().handle(34.0F), 0.0F, var11);
      return true;
   }

   public boolean handle(ModernClickGuiState var1, int var2) {
      if (this.selection) {
         if (var2 == 256) {
            this.resolve();
            return true;
         } else if (var2 == 258) {
            boolean var4 = this.state.process();
            this.state.handle(!var4);
            this.cache.handle(var4);
            return true;
         } else if (var2 == 257 || var2 == 335) {
            this.update();
            return true;
         } else {
            return !this.state.handle(var2) && !this.cache.handle(var2) ? true : true;
         }
      } else if (this.output.process()) {
         if (var2 == 256) {
            this.output.handle(false);
            return true;
         }

         if (var2 != 257 && var2 != 335) {
            return this.output.handle(var2);
         }

         HeadlessBotEngine.ServerEntry var3 = this.current == null ? null : HeadlessBotEngine.handle(this.current);
         if (var3 != null) {
            this.handle(var3);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean handle(char var1) {
      return !this.selection ? this.output.handle(var1) : this.state.handle(var1) || this.cache.handle(var1);
   }

   public boolean handle() {
      return this.selection || this.state.process() || this.cache.process() || this.output.process();
   }

   public void process() {
      this.resolve();
      this.output.handle(false);
      this.output.handle("");
      this.active = null;
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      PreviewPlayerGenerator.ServerEntry var5,
      List<HeadlessBotEngine.ServerEntry> var6,
      HeadlessBotEngine.ServerEntry var7
   ) {
      this.handle(var1, var3, var4, var5.listPanel, 0.0F);
      String var8 = "Боты:";
      float var9 = var5.listPanel.x + var3.handle(12.0F);
      float var10 = var5.listPanel.y + var3.handle(11.0F);
      ModuleStateHelper.handle(var1, var3, FontRegistry.config, var9, var10, 11.0F, var8, ModuleStateHelper.handle(var4));
      String var11 = var2.encodeVector();
      int var12 = var11 != null && !var11.isBlank() ? HeadlessBotEngine.process().size() : var6.size();
      String var13 = Integer.toString(var12);
      float var14 = ModuleStateHelper.handle(var3, FontRegistry.config, var8, 11.0F);
      ModuleStateHelper.handle(var1, var3, FontRegistry.config, var9 + var14, var10, 11.0F, var13, var4.resolve());
      this.handle(var1, var2, var3, var4, var5.hostButton, "Host", PreviewPlayerGenerator.Mode.NORMAL, var2.execute() == null);
      this.handle(var1, var2, var3, var4, var5.addButton, "+ Добавить", PreviewPlayerGenerator.Mode.ACCENT, false);
      float var15 = var6.size() * var5.rowHeight + Math.max(0, var6.size() - 1) * var5.rowGap;
      float var16 = Math.max(0.0F, var15 - var5.listViewport.h);
      this.enabled = handle(this.enabled, 0.0F, var16);
      var1.compute();
      var1.handle(
         var5.listViewport.x,
         var5.listViewport.y,
         var5.listViewport.w,
         var5.listViewport.h,
         var3.handle(6.0F),
         var3.handle(6.0F),
         var3.handle(6.0F),
         var3.handle(6.0F)
      );

      try {
         float var17 = var5.listViewport.y - this.enabled;

         for (HeadlessBotEngine.ServerEntry var19 : var6) {
            PreviewPlayerGenerator.DataRecord var20 = new PreviewPlayerGenerator.DataRecord(
               var5.listViewport.x, var17, var5.listViewport.w - (var16 > 0.0F ? var3.handle(5.0F) : 0.0F), var5.rowHeight
            );
            if (var20.intersects(var5.listViewport)) {
               this.handle(var1, var2, var3, var4, var20, var19, var7 != null && var7.name().equalsIgnoreCase(var19.name()));
            }

            var17 += var5.rowHeight + var5.rowGap;
         }

         if (var6.isEmpty()) {
            this.handle(var1, var3, var4, var5.listViewport);
         }
      } finally {
         var1.compute();
         var1.apply();
      }

      if (var16 > 0.0F) {
         float var24 = var5.listViewport.h;
         float var25 = Math.max(var3.handle(28.0F), var24 * (var24 / (var24 + var16)));
         float var26 = var5.listViewport.y + (var24 - var25) * (this.enabled / var16);
         var1.handle(
            var5.listViewport.x + var5.listViewport.w - var3.handle(2.5F), var5.listViewport.y, var3.handle(1.5F), var24, var3.handle(1.0F), var4.onTick()
         );
         var1.handle(
            var5.listViewport.x + var5.listViewport.w - var3.handle(3.0F),
            var26,
            var3.handle(2.5F),
            var25,
            var3.handle(1.5F),
            ThemeColors.handle(var4.save(), 150)
         );
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      PreviewPlayerGenerator.DataRecord var5,
      HeadlessBotEngine.ServerEntry var6,
      boolean var7
   ) {
      boolean var8 = var5.contains(var2.sampleLayer(), var2.sendWorld());
      float var9 = var7 ? 1.0F : (var8 ? 0.55F : 0.0F);
      int var10 = ThemeColors.handle(
         ModuleStateHelper.handle(var4, var8 ? 1.0F : 0.0F), ThemeColors.handle(var4.submit(), var4.unload() ? 34 : 48), var7 ? 0.42F : 0.0F
      );
      var1.handle(var5.x, var5.y, var5.w, var5.h, var3.handle(8.0F), var10);
      var1.handle(
         var5.x,
         var5.y,
         var5.w,
         var5.h,
         var3.handle(8.0F),
         ThemeColors.handle(var4.select(), ThemeColors.handle(var4.save(), 145), var9),
         Math.max(0.55F, var3.handle(0.6F))
      );
      int var11 = handle(var4, var6);
      float var12 = var5.x + var3.handle(13.0F);
      float var13 = var5.y + var3.handle(17.0F);
      var1.process(var12, var13, var3.handle(3.3F), 0.0F, 1.0F, ThemeColors.handle(var11, 48));
      var1.process(var12, var13, var3.handle(1.8F), 0.0F, 1.0F, var11);
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.config,
         var5.x + var3.handle(23.0F),
         var5.y + var3.handle(9.0F),
         10.5F,
         handle(var6.name(), var5.w - var3.handle(36.0F), var3, FontRegistry.config, 10.5F),
         ModuleStateHelper.handle(var4)
      );
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.instance,
         var5.x + var3.handle(13.0F),
         var5.y + var3.handle(29.0F),
         8.2F,
         handle(var6.address(), var5.w - var3.handle(25.0F), var3, FontRegistry.instance, 8.2F),
         ModuleStateHelper.process(var4)
      );
      String var14 = process(var6);
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.instance,
         var5.x + var3.handle(13.0F),
         var5.y + var3.handle(43.0F),
         7.5F,
         handle(var14, var5.w - var3.handle(25.0F), var3, FontRegistry.instance, 7.5F),
         ThemeColors.handle(var11, 215)
      );
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, PreviewPlayerGenerator.DataRecord var4) {
      float var5 = var4.y + var4.h * 0.46F;
      var1.process(var4.x + var4.w * 0.5F, var5 - var2.handle(17.0F), var2.handle(15.0F), 0.0F, 1.0F, var3.onTick());
      handle(var1, var2, var4.x + var4.w * 0.5F, var5 - var2.handle(17.0F), ModuleStateHelper.compute(var3));
      String var6 = "Ботов пока нет";
      float var7 = ModuleStateHelper.handle(var2, FontRegistry.config, var6, 10.0F);
      ModuleStateHelper.handle(
         var1, var2, FontRegistry.config, var4.x + (var4.w - var7) * 0.5F, var5 + var2.handle(5.0F), 10.0F, var6, ModuleStateHelper.process(var3)
      );
      String var8 = "Нажмите «Добавить»";
      float var9 = ModuleStateHelper.handle(var2, FontRegistry.instance, var8, 8.0F);
      ModuleStateHelper.handle(
         var1, var2, FontRegistry.instance, var4.x + (var4.w - var9) * 0.5F, var5 + var2.handle(23.0F), 8.0F, var8, ModuleStateHelper.compute(var3)
      );
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      PreviewPlayerGenerator.ServerEntry var5,
      HeadlessBotEngine.ServerEntry var6
   ) {
      this.handle(var1, var3, var4, var5.detailPanel, 0.0F);
      if (var6 == null) {
         String var13 = "Выберите бота слева";
         float var14 = ModuleStateHelper.handle(var3, FontRegistry.config, var13, 12.0F);
         ModuleStateHelper.handle(
            var1,
            var3,
            FontRegistry.config,
            var5.detailPanel.x + (var5.detailPanel.w - var14) * 0.5F,
            var5.detailPanel.y + var5.detailPanel.h * 0.44F,
            12.0F,
            var13,
            ModuleStateHelper.process(var4)
         );
      } else {
         HeadlessBotSession var7 = var6.bot();
         int var8 = handle(var4, var6);
         var1.process(
            var5.detailPanel.x + var3.handle(17.0F), var5.detailPanel.y + var3.handle(22.0F), var3.handle(4.0F), 0.0F, 1.0F, ThemeColors.handle(var8, 55)
         );
         var1.process(var5.detailPanel.x + var3.handle(17.0F), var5.detailPanel.y + var3.handle(22.0F), var3.handle(2.2F), 0.0F, 1.0F, var8);
         ModuleStateHelper.handle(
            var1,
            var3,
            FontRegistry.config,
            var5.detailPanel.x + var3.handle(29.0F),
            var5.detailPanel.y + var3.handle(11.0F),
            13.0F,
            handle(var6.name(), var5.detailPanel.w - var3.handle(125.0F), var3, FontRegistry.config, 13.0F),
            ModuleStateHelper.handle(var4)
         );
         float var9 = var5.detailPanel.x + var3.handle(29.0F);
         float var10 = Math.max(var3.handle(24.0F), var5.forgetButton.x - var9 - var3.handle(8.0F));
         ModuleStateHelper.handle(
            var1,
            var3,
            FontRegistry.instance,
            var9,
            var5.detailPanel.y + var3.handle(31.0F),
            8.5F,
            handle(var6.address() + "  ·  " + resolve(var6.status()), var10, var3, FontRegistry.instance, 8.5F),
            ModuleStateHelper.process(var4)
         );
         boolean var11 = var6.name().equalsIgnoreCase(this.active) && System.currentTimeMillis() - this.mode <= 2500L;
         this.handle(var1, var2, var3, var4, var5.forgetButton, var11 ? "Удалить?" : "Удалить", PreviewPlayerGenerator.Mode.DANGER, var11);
         String var12 = var7 != null && HeadlessBotEngine.handle() == var7 ? "Вернуться" : "Управлять";
         this.handle(var1, var2, var3, var4, var5.controlButton, var12, PreviewPlayerGenerator.Mode.ACCENT, var7 != null && HeadlessBotEngine.handle() == var7);
         this.handle(var1, var2, var3, var4, var5.modulesButton, "Модули", PreviewPlayerGenerator.Mode.NORMAL, false);
         this.handle(var1, var2, var3, var4, var5.reconnectButton, "Реконнект", PreviewPlayerGenerator.Mode.NORMAL, false);
         this.handle(var1, var2, var3, var4, var5.disconnectButton, "Отключить", PreviewPlayerGenerator.Mode.DANGER, false);
         this.output.handle(var1, var3, var4, var5.chatField, "Сообщение или /команда", var2.sampleLayer(), var2.sendWorld());
         this.handle(var1, var2, var3, var4, var5.sendButton, "Отправить", PreviewPlayerGenerator.Mode.ACCENT, false);
         if (var7 != null && var7.prepare() != null && var7.onTick()) {
            this.handle(var1, var3, var4, var5, var7);
            this.handle(var1, var2, var3, var4, var5, var7);
         }
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, PreviewPlayerGenerator.ServerEntry var4, HeadlessBotSession var5) {
      var1.handle(var4.stats.x, var4.stats.y, var4.stats.w, var4.stats.h, var2.handle(7.0F), ModuleStateHelper.handle(var3, 0.0F));
      var1.handle(var4.stats.x, var4.stats.y, var4.stats.w, var4.stats.h, var2.handle(7.0F), var3.select(), Math.max(0.5F, var2.handle(0.55F)));
      if (var5 != null && var5.prepare() != null && var5.onTick()) {
         BackgroundClientPlayer var6 = var5.prepare();
         String var7 = String.format(Locale.ROOT, "HP %.1f / %.1f", var6.getHealth(), var6.getMaxHealth());
         String var8 = "Еда " + var6.getHungerManager().getFoodLevel();
         String var9 = "XP " + var6.experienceLevel;
         String var10 = "XYZ " + var6.getBlockX() + "  " + var6.getBlockY() + "  " + var6.getBlockZ();
         String[] var11 = new String[]{var7, var8, var9, var10};
         int[] var12 = new int[]{var3.handle(), var3.compute(), var3.resolve(), ModuleStateHelper.process(var3)};
         float var13 = var2.handle(8.0F);
         float var14 = (var4.stats.w - var13 * 2.0F) / var11.length;

         for (int var15 = 0; var15 < var11.length; var15++) {
            String var16 = handle(var11[var15], var14 - var2.handle(5.0F), var2, FontRegistry.instance, 8.2F);
            float var17 = ModuleStateHelper.handle(var2, FontRegistry.instance, var16, 8.2F);
            float var18 = var4.stats.x + var13 + var15 * var14 + (var14 - var17) * 0.5F;
            ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var18, var4.stats.y, var4.stats.h, 8.2F, var16, var12[var15]);
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, PreviewPlayerGenerator.ServerEntry var5, HeadlessBotSession var6
   ) {
      this.handle(var1, var3, var4, var5.inventoryTab, "Инвентарь");
      float var7 = var5.inventoryArea.x - var3.handle(8.0F);
      float var8 = var5.inventoryArea.y - var3.handle(5.0F);
      float var9 = var5.inventoryArea.w + var3.handle(16.0F);
      float var10 = var5.inventoryArea.h + var3.handle(5.0F);
      int var11 = ThemeColors.handle(ModuleStateHelper.prepare(var4), var4.load(), var4.unload() ? 0.025F : 0.045F);
      var1.handle(var7, var8, var9, var10, var3.handle(9.0F), var11);
      var1.handle(
         var7, var8, var9, var10, var3.handle(9.0F), var4.unload() ? ModuleStateHelper.process(var4, 0.92F) : var4.tick(), Math.max(0.65F, var3.handle(0.7F))
      );
      if (var6 != null && var6.prepare() != null && var6.onTick()) {
         ItemStack var12 = this.process(var1, var2, var3, var4, var5, var6);
         if (var12 != null && !var12.isEmpty()) {
            String var13 = var12.getName().getString();
            if (var12.getCount() > 1) {
               var13 = var13 + " ×" + var12.getCount();
            }

            ModuleStateHelper.handle(
               var1,
               var3,
               FontRegistry.instance,
               var5.inventoryArea.x,
               var5.inventoryArea.y + var5.inventoryArea.h - var3.handle(15.0F),
               8.3F,
               handle(var13, var5.inventoryArea.w, var3, FontRegistry.instance, 8.3F),
               ModuleStateHelper.process(var4)
            );
         }
      }
   }

   private ItemStack process(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, PreviewPlayerGenerator.ServerEntry var5, HeadlessBotSession var6
   ) {
      float var7 = var5.slot;
      float var8 = var5.inventoryArea.x;
      float var9 = var5.inventoryArea.y + var3.handle(3.0F);
      ItemStack var10 = ItemStack.EMPTY;

      for (int var11 = 0; var11 < 4; var11++) {
         if (var11 == 3) {
            var9 += var3.handle(5.0F);
         }

         for (int var12 = 0; var12 < 9; var12++) {
            int var13 = var11 < 3 ? 9 + var11 * 9 + var12 : var12;
            PreviewPlayerGenerator.DataRecord var14 = new PreviewPlayerGenerator.DataRecord(
               var8 + var12 * var7, var9 + var11 * var7, var7 - var3.handle(2.0F), var7 - var3.handle(2.0F)
            );
            boolean var15 = var11 == 3 && var6.prepare().getInventory().getSelectedSlot() == var12;
            ItemStack var16 = var6.prepare().getInventory().getStack(var13);
            this.handle(var1, var2, var3, var4, var14, var16, var13, var15);
            if (var14.contains(var2.sampleLayer(), var2.sendWorld())) {
               var10 = var16;
            }
         }
      }

      float var17 = var8 + var7 * 9.0F + var3.handle(10.0F);
      EquipmentSlot[] var18 = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

      for (int var19 = 0; var19 < var18.length; var19++) {
         PreviewPlayerGenerator.DataRecord var21 = new PreviewPlayerGenerator.DataRecord(
            var17, var9 + var19 * var7, var7 - var3.handle(2.0F), var7 - var3.handle(2.0F)
         );
         ItemStack var23 = var6.prepare().getEquippedStack(var18[var19]);
         this.handle(var1, var2, var3, var4, var21, var23, 100 + var19, false);
         if (var21.contains(var2.sampleLayer(), var2.sendWorld())) {
            var10 = var23;
         }
      }

      PreviewPlayerGenerator.DataRecord var20 = new PreviewPlayerGenerator.DataRecord(
         var17 + var7 + var3.handle(4.0F), var9 + var7 * 3.0F, var7 - var3.handle(2.0F), var7 - var3.handle(2.0F)
      );
      ItemStack var22 = var6.prepare().getOffHandStack();
      this.handle(var1, var2, var3, var4, var20, var22, 110, false);
      if (var20.contains(var2.sampleLayer(), var2.sendWorld())) {
         var10 = var22;
      }

      return var10;
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      PreviewPlayerGenerator.DataRecord var5,
      ItemStack var6,
      int var7,
      boolean var8
   ) {
      boolean var9 = var5.contains(var2.sampleLayer(), var2.sendWorld());
      int var10 = ThemeColors.handle(ModuleStateHelper.prepare(var4), var4.load(), var4.unload() ? 0.055F : 0.085F);
      float var11 = var8 ? 0.28F : (var9 ? 0.13F : 0.0F);
      var1.handle(var5.x, var5.y, var5.w, var5.h, var3.handle(5.0F), ThemeColors.handle(var10, var4.submit(), var11));
      var1.handle(
         var5.x,
         var5.y,
         var5.w,
         var5.h,
         var3.handle(5.0F),
         var8 ? ThemeColors.handle(var4.save(), 210) : (var9 ? ThemeColors.handle(var4.save(), 112) : var4.tick()),
         var8 ? Math.max(0.9F, var3.handle(1.0F)) : Math.max(0.65F, var3.handle(0.7F))
      );
      if (var6 != null && !var6.isEmpty()) {
         float var12 = Math.min(var3.handle(17.0F), var5.w - var3.handle(5.0F));
         float var13 = var5.x + (var5.w - var12) * 0.5F;
         float var14 = var5.y + (var5.h - var12) * 0.5F;
         ItemStackOverlayRenderer.handle(var1, var6.copy(), var13, var14, var12 / 16.0F, var7, true, var7);
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, PreviewPlayerGenerator.ServerEntry var5) {
      var1.handle(var5.content.x, var5.content.y, var5.content.w, var5.content.h, var3.handle(4.0F), ThemeColors.handle(0, 0, 0, var4.unload() ? 72 : 124));
      ModuleStateHelper.handle(
         var1, var3, var4, var5.modal.x, var5.modal.y, var5.modal.w, var5.modal.h, var3.handle(14.0F), var3.handle(24.0F), var3.handle(2.0F), 0.9F
      );
      var1.handle(var5.modal.x, var5.modal.y, var5.modal.w, var5.modal.h, var3.handle(14.0F), ModuleStateHelper.prepare(var4));
      var1.handle(
         var5.modal.x, var5.modal.y, var5.modal.w, var5.modal.h, var3.handle(14.0F), ThemeColors.handle(var4.save(), 108), Math.max(0.75F, var3.handle(0.75F))
      );
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.config,
         var5.modal.x + var3.handle(18.0F),
         var5.modal.y + var3.handle(16.0F),
         12.0F,
         "Добавить бота",
         ModuleStateHelper.handle(var4)
      );
      ModuleStateHelper.handle(
         var1, var3, FontRegistry.instance, var5.nameField.x, var5.nameField.y - var3.handle(13.0F), 7.5F, "Ник", ModuleStateHelper.compute(var4)
      );
      ModuleStateHelper.handle(
         var1, var3, FontRegistry.instance, var5.addressField.x, var5.addressField.y - var3.handle(13.0F), 7.5F, "Сервер", ModuleStateHelper.compute(var4)
      );
      this.state.handle(var1, var3, var4, var5.nameField, "Bot_1", var2.sampleLayer(), var2.sendWorld());
      this.handle(var1, var2, var3, var4, var5.randomNameButton, "RND", PreviewPlayerGenerator.Mode.NORMAL, false);
      this.cache.handle(var1, var3, var4, var5.addressField, "play.example.net:25565", var2.sampleLayer(), var2.sendWorld());
      this.handle(var1, var2, var3, var4, var5.addCancel, "Отмена", PreviewPlayerGenerator.Mode.NORMAL, false);
      this.handle(var1, var2, var3, var4, var5.addSubmit, "Подключить", PreviewPlayerGenerator.Mode.ACCENT, false);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, PreviewPlayerGenerator.ServerEntry var4) {
      if (!this.renderer.isBlank() && System.currentTimeMillis() - this.animationDraw <= 4000L) {
         int var5 = this.handler ? var3.process() : var3.handle();
         float var6 = Math.min(
            var4.detailPanel.w - var2.handle(24.0F), ModuleStateHelper.handle(var2, FontRegistry.instance, this.renderer, 8.2F) + var2.handle(20.0F)
         );
         float var7 = var4.detailPanel.x + (var4.detailPanel.w - var6) * 0.5F;
         float var8 = var4.detailPanel.y + var4.detailPanel.h - var2.handle(31.0F);
         var1.handle(var7, var8, var6, var2.handle(22.0F), var2.handle(11.0F), ThemeColors.handle(var5, var3.unload() ? 34 : 45));
         var1.handle(var7, var8, var6, var2.handle(22.0F), var2.handle(11.0F), ThemeColors.handle(var5, 120), Math.max(0.5F, var2.handle(0.55F)));
         String var9 = handle(this.renderer, var6 - var2.handle(16.0F), var2, FontRegistry.instance, 8.2F);
         float var10 = ModuleStateHelper.handle(var2, FontRegistry.instance, var9, 8.2F);
         ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var7 + (var6 - var10) * 0.5F, var8, var2.handle(22.0F), 8.2F, var9, var5);
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, PreviewPlayerGenerator.DataRecord var4, float var5) {
      int var6 = ThemeColors.handle(
         ThemeColors.handle(ModuleStateHelper.execute(var3), ModuleStateHelper.prepare(var3), 0.22F + handle(var5, 0.0F, 1.0F) * 0.08F), 255
      );
      var1.handle(var4.x, var4.y, var4.w, var4.h, var2.handle(10.0F), var6);
      var1.handle(
         var4.x,
         var4.y,
         var4.w,
         var4.h,
         var2.handle(10.0F),
         var3.unload() ? ModuleStateHelper.process(var3, 0.82F) : var3.select(),
         Math.max(0.55F, var2.handle(0.6F))
      );
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      PreviewPlayerGenerator.DataRecord var5,
      String var6,
      PreviewPlayerGenerator.Mode var7,
      boolean var8
   ) {
      boolean var9 = var5.contains(var2.sampleLayer(), var2.sendWorld());
      float var10 = var8 ? 1.0F : (var9 ? 0.72F : 0.0F);
      int var11 = var7 == PreviewPlayerGenerator.Mode.DANGER ? var4.process() : var4.save();
      int var12 = ThemeColors.handle(ModuleStateHelper.prepare(var4), var4.load(), var4.unload() ? 0.045F : 0.085F);
      int var13 = var7 == PreviewPlayerGenerator.Mode.ACCENT
         ? ThemeColors.handle(var12, var4.submit(), 0.24F)
         : (var7 == PreviewPlayerGenerator.Mode.DANGER ? ThemeColors.handle(var12, var4.process(), 0.11F) : var12);
      var1.handle(var5.x, var5.y, var5.w, var5.h, var3.handle(6.0F), ThemeColors.handle(var13, var11, var10 * 0.16F));
      int var14 = var4.unload() ? ModuleStateHelper.process(var4, 0.95F) : var4.drawAnimation();
      var1.handle(
         var5.x,
         var5.y,
         var5.w,
         var5.h,
         var3.handle(6.0F),
         ThemeColors.handle(var14, ThemeColors.handle(var11, 185), var10 * 0.78F),
         Math.max(0.7F, var3.handle(0.75F))
      );
      float var15 = ModuleStateHelper.handle(var3, FontRegistry.config, var6, 8.2F);
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.config,
         var5.x + (var5.w - var15) * 0.5F,
         var5.y,
         var5.h,
         8.2F,
         var6,
         var7 == PreviewPlayerGenerator.Mode.DANGER
            ? ThemeColors.handle(ModuleStateHelper.process(var4), var4.process(), 0.78F + var10 * 0.22F)
            : ThemeColors.handle(ModuleStateHelper.process(var4), ModuleStateHelper.handle(var4), 0.68F + var10 * 0.32F)
      );
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, PreviewPlayerGenerator.DataRecord var4, String var5) {
      int var6 = ThemeColors.handle(ModuleStateHelper.prepare(var3), var3.load(), var3.unload() ? 0.04F : 0.075F);
      var1.handle(var4.x, var4.y, var4.w, var4.h, var2.handle(5.0F), ThemeColors.handle(var6, var3.submit(), 0.24F));
      var1.handle(var4.x, var4.y, var4.w, var4.h, var2.handle(5.0F), ThemeColors.handle(var3.save(), 180), Math.max(0.65F, var2.handle(0.7F)));
      float var7 = ModuleStateHelper.handle(var2, FontRegistry.instance, var5, 8.0F);
      ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var4.x + (var4.w - var7) * 0.5F, var4.y, var4.h, 8.0F, var5, var3.resolve());
   }

   private void compute() {
      this.selection = true;
      this.output.handle(false);
      this.output.handle("");
      this.active = null;
      this.state.handle(apply());
      this.cache.handle(execute());
      this.state.handle(true);
      this.cache.handle(false);
   }

   private void resolve() {
      this.selection = false;
      this.state.handle(false);
      this.cache.handle(false);
   }

   private void update() {
      String var1 = this.state.handle().trim();
      String var2 = this.cache.handle().trim();
      if (!var1.isEmpty() && !var2.isEmpty()) {
         if (HeadlessBotConnector.handle(var1, var2)) {
            this.process(var1);
            this.resolve();
            this.handle("Подключение запущено", false);
         } else {
            this.handle("Проверьте ник, адрес или дубликаты", true);
         }
      } else {
         this.handle("Укажите ник и адрес сервера", true);
      }
   }

   private static String apply() {
      ThreadLocalRandom var0 = ThreadLocalRandom.current();

      for (int var1 = 0; var1 < 24; var1++) {
         String var2 = context[var0.nextInt(context.length)] + config[var0.nextInt(config.length)] + var0.nextInt(10, 1000);
         if (var2.length() > 16) {
            var2 = var2.substring(0, 16);
         }

         if (HeadlessBotEngine.handle(var2) == null) {
            return var2;
         }
      }

      String var3 = "Bot" + Integer.toUnsignedString(var0.nextInt(), 36);
      return var3.substring(0, Math.min(16, var3.length()));
   }

   private void handle(HeadlessBotEngine.ServerEntry var1) {
      HeadlessBotSession var2 = var1.bot();
      String var3 = this.output.handle().trim();
      if (var2 != null && var2.handle(var3)) {
         this.output.handle("");
         this.handle("Сообщение отправлено от " + var1.name(), false);
      } else {
         this.handle("Сообщение не отправлено: бот офлайн или текст некорректен", true);
      }
   }

   private List<HeadlessBotEngine.ServerEntry> handle(String var1) {
      ArrayList<HeadlessBotEngine.ServerEntry> var2 = new ArrayList<>(HeadlessBotEngine.process());
      var2.sort(
         Comparator.<HeadlessBotEngine.ServerEntry>comparingInt(var0 -> compute(var0.state() == null ? "" : var0.state().name()))
            .thenComparing(HeadlessBotEngine.ServerEntry::name, String.CASE_INSENSITIVE_ORDER)
      );
      String var3 = var1 == null ? "" : var1.trim().toLowerCase(Locale.ROOT);
      if (!var3.isEmpty()) {
         var2.removeIf(
            var1x -> !var1x.name().toLowerCase(Locale.ROOT).contains(var3)
               && !var1x.address().toLowerCase(Locale.ROOT).contains(var3)
               && !resolve(var1x.status()).toLowerCase(Locale.ROOT).contains(var3)
         );
      }

      return var2;
   }

   private HeadlessBotEngine.ServerEntry handle(List<HeadlessBotEngine.ServerEntry> var1) {
      HeadlessBotEngine.ServerEntry var2 = this.current == null ? null : HeadlessBotEngine.handle(this.current);
      if (var2 != null) {
         String var3 = var2.name();
         if (var1.stream().noneMatch(var1x -> var1x.name().equalsIgnoreCase(var3))) {
            this.process((String)null);
            var2 = null;
         }
      }

      if (var2 == null && !var1.isEmpty()) {
         var2 = (HeadlessBotEngine.ServerEntry)var1.get(0);
         this.process(var2.name());
      }

      return var2;
   }

   private void process(String var1) {
      boolean var2 = this.current == null ? var1 != null : var1 == null || !this.current.equalsIgnoreCase(var1);
      this.current = var1;
      if (var2) {
         this.output.handle(false);
         this.output.handle("");
         this.active = null;
      }
   }

   private void handle(String var1, boolean var2) {
      this.renderer = var1 == null ? "" : var1;
      this.handler = var2;
      this.animationDraw = System.currentTimeMillis();
   }

   private static int handle(ThemeColors var0, HeadlessBotEngine.ServerEntry var1) {
      String var2 = var1.state() == null ? "" : var1.state().name();
      if ("JOINED".equals(var2)) {
         return var0.handle();
      } else if ("ERROR".equals(var2)) {
         return var0.process();
      } else {
         return !"DISCONNECTED".equals(var2) && !"SAVED".equals(var2) ? var0.compute() : ModuleStateHelper.process(var0);
      }
   }

   private static int compute(String var0) {
      return switch (var0) {
         case "JOINED" -> 0;
         case "RESOLVING", "CONNECTING", "LOGIN", "CONFIGURING", "RECONFIGURING" -> 1;
         case "ERROR" -> 2;
         default -> 3;
      };
   }

   private static String process(HeadlessBotEngine.ServerEntry var0) {
      String var1 = resolve(var0.status());
      if (!var1.isBlank()) {
         return var1;
      }

      String var2 = var0.state() == null ? "SAVED" : var0.state().name();

      return switch (var2) {
         case "JOINED" -> "В игре";
         case "RESOLVING" -> "Поиск сервера";
         case "CONNECTING" -> "Соединение";
         case "LOGIN" -> "Вход";
         case "CONFIGURING" -> "Настройка";
         case "RECONFIGURING" -> "Смена сервера";
         case "ERROR" -> "Ошибка";
         case "DISCONNECTED" -> "Отключён";
         default -> "Сохранён";
      };
   }

   private static String resolve(String var0) {
      return var0 == null ? "" : var0.replaceAll("(?i)§[0-9A-FK-OR]", "").replace("Â", "").trim();
   }

   private static String execute() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0 != null && var0.getCurrentServerEntry() != null ? var0.getCurrentServerEntry().address : "";
   }

   private static String handle(String var0, float var1, GuiMetrics var2, FontObject var3, float var4) {
      String var5 = var0 == null ? "" : var0;
      if (ModuleStateHelper.handle(var2, var3, var5, var4) <= var1) {
         return var5;
      }

      String var6 = "…";
      int var7 = var5.length();

      while (var7 > 0 && ModuleStateHelper.handle(var2, var3, var5.substring(0, var7) + var6, var4) > var1) {
         var7--;
      }

      return var7 <= 0 ? var6 : var5.substring(0, var7) + var6;
   }

   private static void handle(RoundedRectRenderer var0, GuiMetrics var1, float var2, float var3, int var4) {
      float var5 = var1.handle(0.8F);
      var0.handle(var2 - 7.0F * var5, var3 - 5.0F * var5, 14.0F * var5, 10.0F * var5, 3.0F * var5, var4, Math.max(0.7F, var1.handle(0.75F)));
      var0.process(var2 - 3.0F * var5, var3 - 1.0F * var5, 1.3F * var5, 0.0F, 1.0F, var4);
      var0.process(var2 + 3.0F * var5, var3 - 1.0F * var5, 1.3F * var5, 0.0F, 1.0F, var4);
      var0.handle(var2 - 3.0F * var5, var3 + 2.5F * var5, 6.0F * var5, Math.max(0.7F, var1.handle(0.65F)), 0.4F * var5, var4);
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   record DataRecord(float x, float y, float w, float h) {

      boolean contains(float var1, float var2) {
         return var1 >= this.x && var2 >= this.y && var1 < this.x + this.w && var2 < this.y + this.h;
      }

      boolean intersects(PreviewPlayerGenerator.DataRecord var1) {
         return this.x < var1.x + var1.w && this.x + this.w > var1.x && this.y < var1.y + var1.h && this.y + this.h > var1.y;
      }
   }

   enum Mode {
      NORMAL,
      ACCENT,
      DANGER;
   }

   static final class ScreenState {
      private final int instance;
      private final IntPredicate data;
      private String context = "";
      private int config;
      private int state;
      private boolean cache;
      private long output;

      ScreenState(int var1, IntPredicate var2) {
         this.instance = var1;
         this.data = var2;
      }

      String handle() {
         return this.context;
      }

      void handle(String var1) {
         this.context = var1 == null ? "" : var1.substring(0, Math.min(this.instance, var1.length()));
         this.config = this.context.length();
         this.state = this.config;
      }

      boolean process() {
         return this.cache;
      }

      void handle(boolean var1) {
         this.cache = var1;
         if (var1) {
            this.config = Math.min(this.config, this.context.length());
            this.state = this.config;
            this.output = System.currentTimeMillis();
         }
      }

      boolean handle(PreviewPlayerGenerator.DataRecord var1, float var2, float var3) {
         boolean var4 = var1.contains(var2, var3);
         this.handle(var4);
         if (var4) {
            this.config = this.context.length();
            this.state = this.config;
         }

         return var4;
      }

      boolean handle(char var1) {
         if (this.cache && this.data.test(var1)) {
            this.compute(Character.toString(var1));
            return true;
         } else {
            return false;
         }
      }

      boolean handle(int var1) {
         if (!this.cache) {
            return false;
         }

         boolean var2 = Screen.hasControlDown();
         boolean var3 = Screen.hasShiftDown();
         MinecraftClient var4 = MinecraftClient.getInstance();
         if (var2) {
            if (var1 == 65) {
               this.state = 0;
               this.config = this.context.length();
               return true;
            }

            if (var1 == 67) {
               if (this.compute() && var4 != null) {
                  var4.keyboard.setClipboard(this.resolve());
               }

               return true;
            }

            if (var1 == 88) {
               if (this.compute() && var4 != null) {
                  var4.keyboard.setClipboard(this.resolve());
                  this.compute("");
               }

               return true;
            }

            if (var1 == 86) {
               if (var4 != null) {
                  this.process(var4.keyboard.getClipboard());
               }

               return true;
            }
         }

         switch (var1) {
            case 259:
               if (this.compute()) {
                  this.compute("");
               } else if (this.config > 0) {
                  this.context = this.context.substring(0, this.config - 1) + this.context.substring(this.config);
                  this.config--;
                  this.state = this.config;
               }
               break;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               return true;
            case 261:
               if (this.compute()) {
                  this.compute("");
               } else if (this.config < this.context.length()) {
                  this.context = this.context.substring(0, this.config) + this.context.substring(this.config + 1);
                  this.state = this.config;
               }
               break;
            case 262:
               this.handle(this.config + 1, var3);
               break;
            case 263:
               this.handle(this.config - 1, var3);
               break;
            case 268:
               this.handle(0, var3);
               break;
            case 269:
               this.handle(this.context.length(), var3);
         }

         this.output = System.currentTimeMillis();
         return true;
      }

      void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, PreviewPlayerGenerator.DataRecord var4, String var5, float var6, float var7) {
         boolean var8 = var4.contains(var6, var7);
         float var9 = this.cache ? 1.0F : (var8 ? 0.55F : 0.0F);
         int var10 = ThemeColors.handle(ModuleStateHelper.prepare(var3), var3.load(), var3.unload() ? 0.04F : 0.075F);
         var1.handle(var4.x, var4.y, var4.w, var4.h, var2.handle(6.0F), ThemeColors.handle(var10, var3.submit(), var9 * 0.12F));
         var1.handle(
            var4.x,
            var4.y,
            var4.w,
            var4.h,
            var2.handle(6.0F),
            ThemeColors.handle(var3.tick(), ThemeColors.handle(var3.save(), 180), var9),
            this.cache ? Math.max(0.85F, var2.handle(0.9F)) : Math.max(0.65F, var2.handle(0.7F))
         );
         String var11 = this.context.isEmpty() && !this.cache ? var5 : this.context;
         int var12 = this.context.isEmpty() && !this.cache ? ModuleStateHelper.compute(var3) : ModuleStateHelper.handle(var3);
         float var13 = var4.w - var2.handle(16.0F);
         String var14 = handle(var11, var13, var2, 8.7F);
         ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var4.x + var2.handle(8.0F), var4.y, var4.h, 8.7F, var14, var12);
         if (this.cache && (System.currentTimeMillis() - this.output) % 1000L < 530L) {
            String var15 = this.context.substring(0, Math.min(this.config, this.context.length()));
            float var16 = ModuleStateHelper.handle(var2, FontRegistry.instance, var15, 8.7F);
            float var17 = Math.min(var4.x + var4.w - var2.handle(7.0F), var4.x + var2.handle(8.0F) + var16);
            var1.handle(var17, var4.y + var2.handle(6.0F), Math.max(1.0F, var2.handle(0.75F)), var4.h - var2.handle(12.0F), 0.0F, var3.save());
         }
      }

      private void process(String var1) {
         if (var1 != null && !var1.isEmpty()) {
            StringBuilder var2 = new StringBuilder();
            var1.codePoints().forEach(var2x -> {
               if (this.data.test(var2x) && var2.length() < this.instance) {
                  var2.appendCodePoint(var2x);
               }
            });
            this.compute(var2.toString());
         }
      }

      private void compute(String var1) {
         int var2 = Math.min(this.config, this.state);
         int var3 = Math.max(this.config, this.state);
         int var4 = this.instance - (this.context.length() - (var3 - var2));
         String var5 = var1 == null ? "" : var1.substring(0, Math.min(var4, var1.length()));
         this.context = this.context.substring(0, var2) + var5 + this.context.substring(var3);
         this.config = var2 + var5.length();
         this.state = this.config;
         this.output = System.currentTimeMillis();
      }

      private void handle(int var1, boolean var2) {
         this.config = Math.max(0, Math.min(this.context.length(), var1));
         if (!var2) {
            this.state = this.config;
         }

         this.output = System.currentTimeMillis();
      }

      private boolean compute() {
         return this.config != this.state;
      }

      private String resolve() {
         return this.context.substring(Math.min(this.config, this.state), Math.max(this.config, this.state));
      }

      private static String handle(String var0, float var1, GuiMetrics var2, float var3) {
         if (ModuleStateHelper.handle(var2, FontRegistry.instance, var0, var3) <= var1) {
            return var0;
         }

         int var4 = 0;

         while (var4 < var0.length() && ModuleStateHelper.handle(var2, FontRegistry.instance, "…" + var0.substring(var4), var3) > var1) {
            var4++;
         }

         return "…" + var0.substring(Math.min(var4, var0.length()));
      }
   }

   record ServerEntry(
      PreviewPlayerGenerator.DataRecord content,
      PreviewPlayerGenerator.DataRecord listPanel,
      PreviewPlayerGenerator.DataRecord listViewport,
      PreviewPlayerGenerator.DataRecord addButton,
      PreviewPlayerGenerator.DataRecord hostButton,
      PreviewPlayerGenerator.DataRecord detailPanel,
      PreviewPlayerGenerator.DataRecord forgetButton,
      PreviewPlayerGenerator.DataRecord controlButton,
      PreviewPlayerGenerator.DataRecord modulesButton,
      PreviewPlayerGenerator.DataRecord reconnectButton,
      PreviewPlayerGenerator.DataRecord disconnectButton,
      PreviewPlayerGenerator.DataRecord chatField,
      PreviewPlayerGenerator.DataRecord sendButton,
      PreviewPlayerGenerator.DataRecord stats,
      PreviewPlayerGenerator.DataRecord inventoryTab,
      PreviewPlayerGenerator.DataRecord inventoryArea,
      PreviewPlayerGenerator.DataRecord modal,
      PreviewPlayerGenerator.DataRecord nameField,
      PreviewPlayerGenerator.DataRecord randomNameButton,
      PreviewPlayerGenerator.DataRecord addressField,
      PreviewPlayerGenerator.DataRecord addCancel,
      PreviewPlayerGenerator.DataRecord addSubmit,
      float rowHeight,
      float rowGap,
      float slot
   ) {

      static PreviewPlayerGenerator.ServerEntry of(ViewportLayoutState var0, GuiMetrics var1) {
         float var2 = var1.handle(13.0F);
         PreviewPlayerGenerator.DataRecord var3 = new PreviewPlayerGenerator.DataRecord(var0.drawAnimation(), var0.encodePoint(), var0.animate(), var0.load());
         float var4 = var3.x + var2;
         float var5 = var3.y + var2;
         float var6 = var3.w - var2 * 2.0F;
         float var7 = var3.h - var2 * 2.0F;
         float var8 = var1.handle(10.0F);
         float var9 = Math.min(var1.handle(226.0F), var6 * 0.34F);
         PreviewPlayerGenerator.DataRecord var10 = new PreviewPlayerGenerator.DataRecord(var4, var5, var9, var7);
         PreviewPlayerGenerator.DataRecord var11 = new PreviewPlayerGenerator.DataRecord(var4 + var9 + var8, var5, var6 - var9 - var8, var7);
         PreviewPlayerGenerator.DataRecord var12 = new PreviewPlayerGenerator.DataRecord(
            var10.x + var10.w - var1.handle(82.0F), var10.y + var1.handle(7.0F), var1.handle(73.0F), var1.handle(25.0F)
         );
         PreviewPlayerGenerator.DataRecord var13 = new PreviewPlayerGenerator.DataRecord(var12.x - var1.handle(56.0F), var12.y, var1.handle(50.0F), var12.h);
         PreviewPlayerGenerator.DataRecord var14 = new PreviewPlayerGenerator.DataRecord(
            var10.x + var1.handle(8.0F), var10.y + var1.handle(41.0F), var10.w - var1.handle(16.0F), var10.h - var1.handle(49.0F)
         );
         PreviewPlayerGenerator.DataRecord var15 = new PreviewPlayerGenerator.DataRecord(
            var11.x + var11.w - var1.handle(73.0F), var11.y + var1.handle(9.0F), var1.handle(63.0F), var1.handle(25.0F)
         );
         float var16 = var11.y + var1.handle(54.0F);
         float var17 = var1.handle(5.0F);
         float var18 = (var11.w - var1.handle(20.0F) - var17 * 3.0F) / 4.0F;
         float var19 = var11.x + var1.handle(10.0F);
         PreviewPlayerGenerator.DataRecord var20 = new PreviewPlayerGenerator.DataRecord(var19, var16, var18, var1.handle(28.0F));
         PreviewPlayerGenerator.DataRecord var21 = new PreviewPlayerGenerator.DataRecord(var19 + var18 + var17, var16, var18, var1.handle(28.0F));
         PreviewPlayerGenerator.DataRecord var22 = new PreviewPlayerGenerator.DataRecord(var19 + (var18 + var17) * 2.0F, var16, var18, var1.handle(28.0F));
         PreviewPlayerGenerator.DataRecord var23 = new PreviewPlayerGenerator.DataRecord(var19 + (var18 + var17) * 3.0F, var16, var18, var1.handle(28.0F));
         float var24 = var16 + var1.handle(36.0F);
         PreviewPlayerGenerator.DataRecord var25 = new PreviewPlayerGenerator.DataRecord(
            var11.x + var11.w - var1.handle(86.0F), var24, var1.handle(76.0F), var1.handle(29.0F)
         );
         PreviewPlayerGenerator.DataRecord var26 = new PreviewPlayerGenerator.DataRecord(
            var11.x + var1.handle(10.0F), var24, var25.x - var11.x - var1.handle(17.0F), var1.handle(29.0F)
         );
         PreviewPlayerGenerator.DataRecord var27 = new PreviewPlayerGenerator.DataRecord(
            var11.x + var1.handle(10.0F), var24 + var1.handle(38.0F), var11.w - var1.handle(20.0F), var1.handle(34.0F)
         );
         float var28 = var1.handle(25.0F);
         float var29 = Math.min(var11.w - var1.handle(24.0F), var28 * 11.0F + var1.handle(12.0F));
         float var30 = var28 * 4.0F + var1.handle(24.0F);
         float var31 = var11.x + (var11.w - var29) * 0.5F;
         float var32 = var1.handle(30.0F) + var30 + var1.handle(5.0F);
         float var33 = var27.y + var27.h + var1.handle(9.0F);
         float var34 = var11.y + var11.h - var1.handle(39.0F);
         float var35 = Math.max(0.0F, var34 - var33);
         float var36 = var33 + Math.max(0.0F, (var35 - var32) * 0.5F);
         PreviewPlayerGenerator.DataRecord var37 = new PreviewPlayerGenerator.DataRecord(
            var31 + (var29 - var1.handle(80.0F)) * 0.5F, var36, var1.handle(80.0F), var1.handle(24.0F)
         );
         PreviewPlayerGenerator.DataRecord var38 = new PreviewPlayerGenerator.DataRecord(var31, var36 + var1.handle(30.0F), var29, var30);
         float var39 = Math.min(var1.handle(430.0F), var3.w - var1.handle(44.0F));
         float var40 = var1.handle(164.0F);
         PreviewPlayerGenerator.DataRecord var41 = new PreviewPlayerGenerator.DataRecord(
            var3.x + (var3.w - var39) * 0.5F, var3.y + (var3.h - var40) * 0.5F, var39, var40
         );
         float var42 = var41.y + var1.handle(58.0F);
         float var43 = var1.handle(96.0F);
         PreviewPlayerGenerator.DataRecord var44 = new PreviewPlayerGenerator.DataRecord(var41.x + var1.handle(18.0F), var42, var43, var1.handle(30.0F));
         PreviewPlayerGenerator.DataRecord var45 = new PreviewPlayerGenerator.DataRecord(
            var44.x + var44.w + var1.handle(6.0F), var42, var1.handle(30.0F), var1.handle(30.0F)
         );
         PreviewPlayerGenerator.DataRecord var46 = new PreviewPlayerGenerator.DataRecord(
            var45.x + var45.w + var1.handle(9.0F), var42, var41.x + var41.w - var1.handle(18.0F) - var45.x - var45.w - var1.handle(9.0F), var1.handle(30.0F)
         );
         float var47 = var1.handle(181.0F);
         float var48 = var41.x + (var41.w - var47) * 0.5F;
         PreviewPlayerGenerator.DataRecord var49 = new PreviewPlayerGenerator.DataRecord(
            var48, var41.y + var1.handle(103.0F), var1.handle(76.0F), var1.handle(29.0F)
         );
         PreviewPlayerGenerator.DataRecord var50 = new PreviewPlayerGenerator.DataRecord(
            var49.x + var49.w + var1.handle(7.0F), var49.y, var1.handle(98.0F), var1.handle(29.0F)
         );
         return new PreviewPlayerGenerator.ServerEntry(
            var3,
            var10,
            var14,
            var12,
            var13,
            var11,
            var15,
            var20,
            var21,
            var22,
            var23,
            var26,
            var25,
            var27,
            var37,
            var38,
            var41,
            var44,
            var45,
            var46,
            var49,
            var50,
            var1.handle(57.0F),
            var1.handle(6.0F),
            var28
         );
      }
   }
}
