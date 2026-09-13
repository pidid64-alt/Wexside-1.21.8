package ru.wild.gui.theme;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.MinecraftClient;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ColorSetting;
import ru.wild.gui.screen.ClickGuiModernScreen;
import ru.wild.gui.screen.ModernClickGuiRenderer;

public class ThemeManager {
   public static MinecraftClient instance = MinecraftClient.getInstance();
   private File context;
   private ThemePalette config = ThemePalette.WILD;
   private ModuleCategory state = ModuleCategory.Visuals;
   private boolean cache;
   private float output;
   private float current;
   private boolean active;
   private boolean mode;
   public ColorSetting data = new ColorSetting("Custom Theme Color", Color.WHITE.getRGB());

   public void handle() {
      this.context = new File(new File(WildClient.instance.cache, "configs"), "gui.cfg");

      try {
         if (!this.context.getParentFile().exists()) {
            this.context.getParentFile().mkdirs();
         }

         if (!this.context.exists()) {
            this.context.createNewFile();
            this.onTick();
         } else {
            this.select();
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }

   public void handle(ThemePalette var1) {
      this.config = var1;
      this.onTick();
   }

   public void handle(ModuleCategory var1) {
      this.state = var1;
      this.onTick();
   }

   public ThemePalette process() {
      return this.config;
   }

   public ModuleCategory compute() {
      return this.state;
   }

   public boolean resolve() {
      return this.cache;
   }

   public float update() {
      return this.output;
   }

   public float apply() {
      return this.current;
   }

   public void handle(float var1, float var2) {
      if (Float.isFinite(var1) && Float.isFinite(var2)) {
         if (!this.cache || !(Math.abs(this.output - var1) < 0.5F) || !(Math.abs(this.current - var2) < 0.5F)) {
            this.cache = true;
            this.output = var1;
            this.current = var2;
            this.onTick();
         }
      }
   }

   public boolean execute() {
      return this.active;
   }

   public boolean prepare() {
      return this.mode;
   }

   public void handle(boolean var1) {
      if (!this.active || this.mode != var1) {
         this.active = true;
         this.mode = var1;
         this.onTick();
      }
   }

   public ModernClickGuiRenderer check() {
      return instance != null && instance.currentScreen instanceof ClickGuiModernScreen var1 ? var1.handle() : null;
   }

   private void onTick() {
      if (this.context != null) {
         try (FileWriter var1 = new FileWriter(this.context)) {
            Properties var2 = new Properties();
            var2.setProperty("theme", this.config.name());
            var2.setProperty("category", this.state.name());
            var2.setProperty("customColor", String.valueOf(this.data.prepare()));
            var2.setProperty("customColorAlpha", String.valueOf(this.data.animationDraw));
            var2.setProperty("customColorPresets", this.handle(this.data));
            if (this.cache) {
               var2.setProperty("themeScreenX", String.valueOf(this.output));
               var2.setProperty("themeScreenY", String.valueOf(this.current));
            }

            if (this.active) {
               var2.setProperty("themePanelVisible", String.valueOf(this.mode));
            }

            var2.store(var1, "GUI Settings");
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }
   }

   private void select() {
      try (FileReader var1 = new FileReader(this.context)) {
         Properties var2 = new Properties();
         var2.load(var1);
         this.config = ThemePalette.valueOf(var2.getProperty("theme", ThemePalette.WILD.name()));
         this.state = ModuleCategory.valueOf(var2.getProperty("category", ModuleCategory.Visuals.name()));
         if (var2.containsKey("customColor")) {
            int var3 = Integer.parseInt(var2.getProperty("customColor"));
            this.data.handle(var3);
            if (var2.containsKey("customColorAlpha")) {
               this.data.process(Float.parseFloat(var2.getProperty("customColorAlpha")));
            }

            this.handle(this.data, var2.getProperty("customColorPresets", ""));
         }

         if (var2.containsKey("themeScreenX") && var2.containsKey("themeScreenY")) {
            this.output = Float.parseFloat(var2.getProperty("themeScreenX"));
            this.current = Float.parseFloat(var2.getProperty("themeScreenY"));
            this.cache = Float.isFinite(this.output) && Float.isFinite(this.current);
         }

         if (var2.containsKey("themePanelVisible")) {
            this.mode = Boolean.parseBoolean(var2.getProperty("themePanelVisible"));
            this.active = true;
         }
      } catch (IOException | IllegalArgumentException var6) {
         var6.printStackTrace();
      }
   }

   private String handle(ColorSetting var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var1.pointEncode.size(); var3++) {
         if (var3 > 0) {
            var2.append(',');
         }

         var2.append(var1.pointEncode.get(var3));
      }

      return var2.toString();
   }

   private void handle(ColorSetting var1, String var2) {
      var1.pointEncode.clear();
      if (var2 != null && !var2.isBlank()) {
         String[] var3 = var2.split(",");

         for (String var7 : var3) {
            if (var1.pointEncode.size() >= 8) {
               break;
            }

            try {
               var1.pointEncode.add(Integer.parseInt(var7.trim()));
            } catch (NumberFormatException var9) {
            }
         }
      }
   }
}
