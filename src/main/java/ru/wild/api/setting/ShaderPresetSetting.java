package ru.wild.api.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import ru.wild.config.FoundryStorage;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.SavedThemePreset;

public class ShaderPresetSetting extends MultiSelectSetting {
   public static final String active = "None";
   private static final List<ShaderPresetSetting> selection = new CopyOnWriteArrayList<>();
   public final LivePreviewRenderer mode;
   private final Supplier<List<String>> enabled;

   public ShaderPresetSetting(String var1, LivePreviewRenderer var2) {
      this(var1, var2, () -> PresetManager.handle().onTick(var2));
   }

   public ShaderPresetSetting(String var1, LivePreviewRenderer var2, Supplier<List<String>> var3) {
      super(var1, "None");
      this.mode = var2;
      this.enabled = var3;
      this.output = new ArrayList<>();
      this.output.add("None");
      this.compute();
      this.resolve();
      selection.add(this);
   }

   public static void handle(LivePreviewRenderer var0, String var1) {
      if (var0 != null && var1 != null && !var1.isBlank()) {
         for (ShaderPresetSetting var3 : selection) {
            if (var3.mode == var0) {
               var3.compute(var1);
            }
         }
      }
   }

   public static void handle(LivePreviewRenderer var0) {
      if (var0 != null) {
         for (ShaderPresetSetting var2 : selection) {
            if (var2.mode == var0) {
               var2.compute("None");
            }
         }
      }
   }

   public ShaderPresetSetting compute(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   @Override
   public List<String> compute() {
      List<String> var1;
      try {
         var1 = this.enabled == null ? Collections.emptyList() : this.enabled.get();
      } catch (Throwable var5) {
         var1 = Collections.emptyList();
      }

      ArrayList<String> var2 = new ArrayList<>();
      var2.add("None");
      if (var1 != null) {
         for (String var4 : var1) {
            if (var4 != null && !var4.isBlank() && !handle(var2, var4)) {
               var2.add(var4.trim());
            }
         }
      }

      if (var2.size() > 2) {
         var2.subList(1, var2.size()).sort((var0, var1x) -> {
            int var2x = apply(var0);
            int var3 = apply(var1x);
            return var2x != var3 ? Integer.compare(var2x, var3) : var0.compareToIgnoreCase(var1x);
         });
      }

      this.config = var2;
      if (this.output == null) {
         this.output = new ArrayList<>();
      }

      if (this.output.isEmpty()) {
         this.output.add("None");
      } else {
         String var6 = this.output.get(this.output.size() - 1);
         this.output.clear();
         this.output.add(resolve(var6));
      }

      return this.config;
   }

   public String execute() {
      this.compute();
      return this.output.isEmpty() ? "None" : this.output.get(this.output.size() - 1);
   }

   public String prepare() {
      String var1 = this.execute();
      return update(var1) ? "None" : var1;
   }

   public void compute(String var1) {
      if (this.output == null) {
         this.output = new ArrayList<>();
      }

      this.output.clear();
      this.output.add(resolve(var1));
      this.compute();
   }

   public void handle(int var1) {
      this.compute();
      if (var1 >= 0 && var1 < this.config.size()) {
         this.compute(this.config.get(var1));
      }
   }

   public int check() {
      String var1 = this.execute();

      for (int var2 = 0; var2 < this.config.size(); var2++) {
         if (this.config.get(var2).equalsIgnoreCase(var1)) {
            return var2;
         }
      }

      return -1;
   }

   public boolean onTick() {
      String var1 = this.execute();
      return !update(var1) && !handle(this.config, var1);
   }

   public String refresh() {
      return this.tick();
   }

   public boolean render() {
      return update(this.execute());
   }

   public String tick() {
      String var1 = this.execute();
      if (update(var1)) {
         return "";
      } else {
         return PresetManager.handle().update(var1) ? var1 : "";
      }
   }

   public String drawAnimation() {
      if (this.mode != null && this.mode != LivePreviewRenderer.PREVIEW_ONLY) {
         try {
            SavedThemePreset var1 = FoundryStorage.handle().compute(this.mode);
            if (var1 != null && PresetManager.handle().update(var1.process())) {
               return var1.process();
            }
         } catch (Throwable var2) {
         }

         return "";
      } else {
         return "";
      }
   }

   @Override
   public boolean process(String var1) {
      return var1 != null && var1.equalsIgnoreCase(this.execute());
   }

   private static String resolve(String var0) {
      return var0 != null && !var0.isBlank() && !update(var0) ? var0.trim() : "None";
   }

   private static boolean update(String var0) {
      return var0 == null || var0.isBlank() || "None".equalsIgnoreCase(var0.trim());
   }

   private static int apply(String var0) {
      PresetManager.PrimaryMode var1 = PresetManager.handle().apply(var0);

      return switch (var1) {
         case PRESET -> 0;
         case USER -> 1;
         case IMPORTED -> 2;
         case RUNTIME -> 3;
      };
   }

   private static boolean handle(List<String> var0, String var1) {
      if (var0 != null && var1 != null) {
         for (String var3 : var0) {
            if (var3 != null && var3.equalsIgnoreCase(var1.trim())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
