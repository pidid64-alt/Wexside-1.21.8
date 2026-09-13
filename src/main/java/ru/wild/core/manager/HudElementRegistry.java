package ru.wild.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import ru.wild.WildClient;
import ru.wild.gui.theme.ThemeRenderer;

public final class HudElementRegistry {
   public static final String instance = "HUD_HotKeys";
   public static final String data = "HUD_Inventory";
   public static final String context = "HUD_Potions";
   public static final String config = "HUD_CoolDowns";
   public static final String state = "HUD_Info";
   public static final String cache = "HUD_WaterMark";
   public static final String output = "HUD_ArrayList";
   public static final String current = "HUD_TargetHUD";
   public static final String active = "hud_armor";
   public static final String mode = "HUD_HotBar";
   public static final String selection = "HUD_Notifications";
   public static final String enabled = "HUD_AutoBuyInfo";
   public static final String renderer = "HUD_AIStatus";
   public static final String handler = "HUD_MusicPlayer";
   public static final String animationDraw = "HUD_ServerHelper";
   static final String[] pointEncode = new String[]{
      "HUD_HotKeys",
      "HUD_Inventory",
      "HUD_Potions",
      "HUD_CoolDowns",
      "HUD_Info",
      "HUD_WaterMark",
      "HUD_ArrayList",
      "HUD_TargetHUD",
      "hud_armor",
      "HUD_HotBar",
      "HUD_Notifications",
      "HUD_AutoBuyInfo",
      "HUD_AIStatus",
      "HUD_MusicPlayer",
      "HUD_ServerHelper"
   };
   private static final Gson animator = new GsonBuilder().setPrettyPrinting().create();
   private static HudElementRegistry.PrimaryCacheEntry source;
   private static boolean target;

   private HudElementRegistry() {
   }

   public static synchronized HudElementRegistry.ColorState handle() {
      return handle("HUD_HotKeys");
   }

   public static synchronized HudElementRegistry.ColorState process() {
      return handle("HUD_Inventory");
   }

   public static synchronized HudElementRegistry.ColorState compute() {
      return handle("HUD_Potions");
   }

   public static synchronized HudElementRegistry.ColorState handle(String var0) {
      apply();
      String var1 = compute(var0);
      HudElementRegistry.ColorState var2 = source.context.get(var1);
      if (var2 == null) {
         var2 = HudElementRegistry.ColorState.handle(var1);
         source.context.put(var1, var2);
      }

      var2.process();
      return var2;
   }

   public static synchronized void resolve() {
      process("HUD_HotKeys");
   }

   public static synchronized void process(String var0) {
      apply();
      String var1 = compute(var0);
      source.context.put(var1, HudElementRegistry.ColorState.handle(var1));
      ThemeRenderer.handle().process(var1);
      update();
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   public static synchronized void update() {
      apply();
      File var0 = execute();
      if (var0 != null) {
         try {
            File var1 = var0.getParentFile();
            if (var1 != null && !var1.exists()) {
               var1.mkdirs();
            }

            try (FileWriter var2 = new FileWriter(var0)) {
               animator.toJson(source, var2);
            }
         } catch (Throwable var7) {
         }
      }
   }

   private static void apply() {
      if (!target) {
         target = true;
         source = new HudElementRegistry.PrimaryCacheEntry();
         File var0 = execute();
         if (var0 != null && var0.exists()) {
            try (FileReader var1 = new FileReader(var0)) {
               HudElementRegistry.PrimaryCacheEntry var2 = (HudElementRegistry.PrimaryCacheEntry)animator.fromJson(
                  var1, HudElementRegistry.PrimaryCacheEntry.class
               );
               if (var2 != null) {
                  source = var2;
               }
            } catch (Throwable var6) {
               source = new HudElementRegistry.PrimaryCacheEntry();
            }

            source.handle();
         } else {
            source.handle();
         }
      }
   }

   private static File execute() {
      return WildClient.instance != null && WildClient.instance.cache != null ? new File(WildClient.instance.cache, "hud-layouts.json") : null;
   }

   static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   private static String compute(String var0) {
      for (String var4 : pointEncode) {
         if (var4.equals(var0)) {
            return var4;
         }
      }

      return "HUD_HotKeys";
   }

   public static final class CacheEntry {
      public float instance;
      public float data;
      public boolean context;

      public CacheEntry() {
      }

      public CacheEntry(float var1, float var2, boolean var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }
   }

   public static final class ColorState {
      public float instance = 14.0F;
      public float data = 11.0F;
      public float context = 7.0F;
      public float config = 7.0F;
      public float state = 7.0F;
      public float cache = 6.0F;
      public float output = 4.0F;
      public float current = 7.0F;
      public float active = 5.0F;
      public float mode = 32.0F;
      public float selection = 22.0F;
      public float enabled = 28.0F;
      public float renderer = 22.0F;
      public float handler = 0.0F;
      public float animationDraw = 2.0F;
      public HudElementRegistry.CacheEntry pointEncode = new HudElementRegistry.CacheEntry(17.0F, 29.0F, false);
      public HudElementRegistry.CacheEntry animator = new HudElementRegistry.CacheEntry(-34.0F, 29.5F, true);
      public HudElementRegistry.CacheEntry source = new HudElementRegistry.CacheEntry(0.0F, 0.0F, false);
      public HudElementRegistry.CacheEntry target = new HudElementRegistry.CacheEntry(0.0F, 0.0F, false);

      public static HudElementRegistry.ColorState handle() {
         return handle("HUD_HotKeys");
      }

      public static HudElementRegistry.ColorState handle(String var0) {
         HudElementRegistry.ColorState var1 = new HudElementRegistry.ColorState();
         if ("HUD_Inventory".equals(var0)) {
            var1.pointEncode = new HudElementRegistry.CacheEntry(17.0F, 29.0F, false);
            var1.animator = new HudElementRegistry.CacheEntry(-34.0F, 30.0F, true);
            var1.context = 9.0F;
            var1.config = 9.0F;
            var1.enabled = 26.0F;
            var1.renderer = 28.0F;
         } else if ("HUD_Potions".equals(var0)) {
            var1.pointEncode = new HudElementRegistry.CacheEntry(17.0F, 29.0F, false);
            var1.animator = new HudElementRegistry.CacheEntry(-34.0F, 28.5F, true);
            var1.renderer = 24.0F;
         } else if ("HUD_WaterMark".equals(var0)) {
            var1.mode = 32.0F;
            var1.selection = 32.0F;
            var1.enabled = 24.0F;
            var1.renderer = 26.0F;
            var1.instance = 14.0F;
            var1.current = 7.0F;
            var1.active = 5.0F;
         } else if ("HUD_ArrayList".equals(var0)) {
            var1.instance = 15.0F;
            var1.data = 15.0F;
            var1.context = 15.0F;
            var1.config = 15.0F;
            var1.cache = 15.0F;
            var1.current = 4.0F;
            var1.active = 0.0F;
            var1.mode = 0.0F;
            var1.selection = 32.0F;
         } else if ("HUD_TargetHUD".equals(var0)) {
            var1.instance = 15.0F;
            var1.data = 12.0F;
            var1.context = 10.0F;
            var1.config = 10.0F;
            var1.state = 10.0F;
            var1.selection = 24.0F;
            var1.renderer = 28.0F;
         } else if ("HUD_HotBar".equals(var0) || "hud_armor".equals(var0)) {
            var1.instance = 10.0F;
            var1.context = 5.0F;
            var1.output = 4.0F;
            var1.current = 5.0F;
            var1.active = 3.0F;
         }

         return var1;
      }

      public void process() {
         this.instance = HudElementRegistry.handle(this.instance, 0.0F, 32.0F);
         this.data = HudElementRegistry.handle(this.data, 0.0F, 28.0F);
         this.context = HudElementRegistry.handle(this.context, 0.0F, 24.0F);
         this.config = HudElementRegistry.handle(this.config, 0.0F, 24.0F);
         this.state = HudElementRegistry.handle(this.state, 0.0F, 24.0F);
         this.cache = HudElementRegistry.handle(this.cache, 0.0F, 22.0F);
         this.output = HudElementRegistry.handle(this.output, 0.0F, 14.0F);
         this.current = HudElementRegistry.handle(this.current, 2.0F, 18.0F);
         this.active = HudElementRegistry.handle(this.active, 0.0F, 18.0F);
         this.mode = HudElementRegistry.handle(this.mode, 0.0F, 48.0F);
         this.selection = HudElementRegistry.handle(this.selection, 14.0F, 42.0F);
         this.enabled = HudElementRegistry.handle(this.enabled, 14.0F, 38.0F);
         this.renderer = HudElementRegistry.handle(this.renderer, 12.0F, 38.0F);
         this.handler = HudElementRegistry.handle(this.handler, -24.0F, 90.0F);
         this.animationDraw = HudElementRegistry.handle(this.animationDraw, 0.0F, 7.0F);
         if (this.pointEncode == null) {
            this.pointEncode = new HudElementRegistry.CacheEntry(17.0F, 29.0F, false);
         }

         if (this.animator == null) {
            this.animator = new HudElementRegistry.CacheEntry(-34.0F, 29.5F, true);
         }

         if (this.source == null) {
            this.source = new HudElementRegistry.CacheEntry(0.0F, 0.0F, false);
         }

         if (this.target == null) {
            this.target = new HudElementRegistry.CacheEntry(0.0F, 0.0F, false);
         }

         this.pointEncode.instance = HudElementRegistry.handle(this.pointEncode.instance, -80.0F, 260.0F);
         this.pointEncode.data = HudElementRegistry.handle(this.pointEncode.data, -40.0F, 180.0F);
         this.animator.instance = HudElementRegistry.handle(this.animator.instance, -220.0F, 80.0F);
         this.animator.data = HudElementRegistry.handle(this.animator.data, -40.0F, 180.0F);
         this.source.instance = HudElementRegistry.handle(this.source.instance, -100.0F, 140.0F);
         this.source.data = HudElementRegistry.handle(this.source.data, -60.0F, 140.0F);
         this.target.instance = HudElementRegistry.handle(this.target.instance, -100.0F, 140.0F);
         this.target.data = HudElementRegistry.handle(this.target.data, -60.0F, 140.0F);
      }
   }

   static final class PrimaryCacheEntry {
      public int instance = 1;
      public HudElementRegistry.ColorState data;
      public Map<String, HudElementRegistry.ColorState> context = new HashMap<>();

      void handle() {
         if (this.context == null) {
            this.context = new HashMap<>();
         }

         if (this.data != null) {
            this.context.putIfAbsent("HUD_HotKeys", this.data);
            this.data = null;
         }

         for (String var4 : HudElementRegistry.pointEncode) {
            this.context.putIfAbsent(var4, HudElementRegistry.ColorState.handle(var4));
         }

         for (HudElementRegistry.ColorState var6 : this.context.values()) {
            if (var6 != null) {
               var6.process();
            }
         }
      }
   }
}
