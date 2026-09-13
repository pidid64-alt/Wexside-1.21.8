package ru.wild.render.font;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import ru.wild.render.shader.ShaderRenderer;
import ru.wild.render.texture.TextureCoordinateMath;
import ru.wild.util.render.RoundedRectRenderer;

public final class FontRegistry {
   private static final Map<String, TextureAtlasHelper> active = new HashMap<>();
   private static final Map<String, FontObject> mode = new HashMap<>();
   private static final Map<String, FontRegistry.DataRecord> selection = new LinkedHashMap<>();
   private static ShaderRenderer enabled;
   private static boolean renderer = false;
   private static boolean handler = false;
   private static long animationDraw;
   public static FontObject instance;
   public static FontObject data;
   public static FontObject context;
   public static FontObject config;
   public static FontObject state;
   public static FontObject cache;
   public static FontObject output;
   public static FontObject current;

   private FontRegistry() {
   }

   public static synchronized void handle(ShaderRenderer var0, RoundedRectRenderer var1) {
      handle(var0);
      Objects.requireNonNull(var1, "renderer");
      if (!handler) {
         var1.handle(instance, handle(instance));
         var1.handle(data, handle(data));
         var1.handle(context, handle(context));
         var1.handle(config, handle(config));
         var1.handle(state, handle(state));
         var1.handle(cache, handle(cache));
         var1.handle(output, handle(output));
         var1.handle(current, handle(current));
         handler = true;
      }
   }

   public static synchronized FontObject handle(String var0, String var1, String var2) {
      update();
      Objects.requireNonNull(var0, "id");
      Objects.requireNonNull(var1, "jsonResourcePath");
      Objects.requireNonNull(var2, "textureResourcePath");
      if (active.containsKey(var0)) {
         throw new IllegalStateException("Font already registered: " + var0);
      }

      TextureAtlasHelper var3 = TextureAtlasHelper.handle(enabled, var1, var2);
      active.put(var0, var3);
      selection.put(var0, new FontRegistry.DataRecord(var1, var2));
      FontObject var4 = new FontObject(var0);
      mode.put(var0, var4);
      return var4;
   }

   public static synchronized void handle() {
      if (renderer && enabled != null) {
         for (Entry var1 : selection.entrySet()) {
            String var2 = (String)var1.getKey();
            TextureAtlasHelper var3 = active.get(var2);
            if (var3 != null) {
               var3.handle(enabled);
            } else {
               try {
                  FontRegistry.DataRecord var4 = (FontRegistry.DataRecord)var1.getValue();
                  TextureAtlasHelper var5 = TextureAtlasHelper.handle(enabled, var4.json, var4.texture);
                  active.put(var2, var5);
               } catch (Throwable var6) {
               }
            }
         }
      }
   }

   public static synchronized void process() {
      if (renderer && enabled != null) {
         long var0 = System.currentTimeMillis();
         if (var0 - animationDraw >= 1000L) {
            animationDraw = var0;

            for (TextureAtlasHelper var3 : active.values()) {
               if (var3 != null && !var3.process()) {
                  var3.handle(enabled);
               }
            }
         }
      }
   }

   public static synchronized SdfTextRenderer handle(FontObject var0) {
      update();
      TextureAtlasHelper var1 = process(var0);
      return new SdfTextRenderer(enabled, var1);
   }

   public static synchronized float handle(FontObject var0, int var1, float var2) {
      update();
      if (var0 != null && !(var2 <= 0.0F)) {
         TextureAtlasHelper var3 = process(var0);
         TextureAtlasHelper.TextureState var4 = var3.handle(var1);
         if (var4 != null && var4.data) {
            float var5 = Math.max(1.0E-6F, var3.apply());
            float var6 = var2 / var5;
            return TextureCoordinateMath.handle(var4.cache, var4.config, var6);
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   public static synchronized float process(FontObject var0, int var1, float var2) {
      update();
      if (var0 != null && !(var2 <= 0.0F)) {
         TextureAtlasHelper var3 = process(var0);
         TextureAtlasHelper.TextureState var4 = var3.handle(var1);
         if (var4 != null && var4.data) {
            float var5 = Math.max(1.0E-6F, var3.apply());
            float var6 = var2 / var5;
            return TextureCoordinateMath.handle(var4.context, var4.state, var6);
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   public static synchronized FontObject handle(String var0) {
      update();
      FontObject var1 = mode.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Font not registered: " + var0);
      } else {
         return var1;
      }
   }

   public static synchronized FontObject compute() {
      update();
      return current;
   }

   static synchronized TextureAtlasHelper process(FontObject var0) {
      update();
      TextureAtlasHelper var1 = active.get(var0.instance);
      if (var1 == null) {
         throw new IllegalStateException("Font not registered: " + var0.instance);
      } else {
         return var1;
      }
   }

   private static void handle(ShaderRenderer var0) {
      Objects.requireNonNull(var0, "backend");
      if (renderer) {
         if (enabled != var0) {
            throw new IllegalStateException("FontRegistry already initialized with a different backend instance");
         }
      } else {
         enabled = var0;
         renderer = true;
         resolve();
      }
   }

   private static void resolve() {
      instance = handle("inter_medium", "assets/wild/fonts/medium.json", "assets/wild/fonts/medium.png");
      data = handle("inter_medium_ext", "assets/wild/fonts/Inter_Medium.json", "assets/wild/fonts/Inter_Medium.png");
      context = handle("icons", "assets/wild/fonts/icons.json", "assets/wild/fonts/icons.png");
      config = handle("inter_semibold", "assets/wild/fonts/semibold.json", "assets/wild/fonts/semibold.png");
      state = handle("new_ico", "assets/wild/fonts/new_ico.json", "assets/wild/fonts/new_ico.png");
      cache = handle("notifff", "assets/wild/fonts/notifff.json", "assets/wild/fonts/notifff.png");
      output = handle("waypoints", "assets/wild/fonts/waypoint_icons.json", "assets/wild/fonts/waypoint_icons.png");
      current = handle("wild", "assets/wild/fonts/wild.json", "assets/wild/fonts/wildICO.png");
   }

   private static void update() {
      if (!renderer || enabled == null) {
         throw new IllegalStateException("FontRegistry.initialize(backend, renderer) must be called before use");
      }
   }

   record DataRecord(String json, String texture) {
   }
}
