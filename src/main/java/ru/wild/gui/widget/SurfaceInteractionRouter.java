package ru.wild.gui.widget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import ru.wild.util.math.SpringAnimation;

public final class SurfaceInteractionRouter {
   public static final long instance = 1L;
   public static final long data = 2L;
   public static final long context = 3L;
   private static final long config = 0L;
   private static final long state = 2L;
   private static final Map<Long, SurfaceInteractionRouter.CacheEntry> cache = new HashMap<>();
   private static long output;
   private static long current;
   private static long active;
   private static float mode;

   private SurfaceInteractionRouter() {
   }

   public static void handle() {
      output++;
      Iterator var0 = cache.entrySet().iterator();

      while (var0.hasNext()) {
         Entry var1 = (Entry)var0.next();
         if (output - ((SurfaceInteractionRouter.CacheEntry)var1.getValue()).active > 2L) {
            if (active == (Long)var1.getKey()) {
               active = 0L;
            }

            var0.remove();
         }
      }
   }

   public static float handle(
      long var0,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      SurfaceInteractionRouter.Callback var11
   ) {
      if (var0 != 0L && var11 != null) {
         SurfaceInteractionRouter.CacheEntry var12 = cache.computeIfAbsent(var0, var0x -> new SurfaceInteractionRouter.CacheEntry());
         long var13 = System.currentTimeMillis();
         float var15 = var12.selection == 0L ? 16.0F : Math.min(80.0F, Math.max(1.0F, (float)(var13 - var12.selection)));
         var12.instance = var2;
         var12.data = var3;
         var12.context = var4;
         var12.config = var5;
         var12.state = var6;
         var12.cache = var7;
         var12.output = Math.max(2.0F, var8);
         var12.current = var11;
         var12.active = output;
         var12.mode = ++current;
         var12.selection = var13;
         boolean var16 = active == var0;
         boolean var17 = var16 || handle(var12, var9, var10) != 0;
         float var18 = var16 ? 1.0F : (var17 ? 0.55F : 0.0F);
         float var19 = var18 > var12.enabled ? 90.0F : 260.0F;
         var12.enabled = SpringAnimation.process(var12.enabled, var18, var15, var19);
         return var12.enabled;
      } else {
         return 0.0F;
      }
   }

   public static boolean handle(long var0) {
      return var0 != 0L && active == var0;
   }

   public static boolean handle(float var0, float var1) {
      return handle(var0, var1, 0);
   }

   public static boolean handle(float var0, float var1, SurfaceHitResolver.Mode var2) {
      int var3 = var2 == SurfaceHitResolver.Mode.THEME ? 2 : (var2 == SurfaceHitResolver.Mode.MAIN ? 1 : 0);
      return handle(var0, var1, var3);
   }

   private static boolean handle(float var0, float var1, int var2) {
      long var3 = 0L;
      SurfaceInteractionRouter.CacheEntry var5 = null;
      int var6 = 0;

      for (Entry var8 : cache.entrySet()) {
         long var9 = (Long)var8.getKey();
         if ((var2 != 2 || var9 == 2L) && (var2 != 1 || var9 == 1L || var9 == 3L)) {
            SurfaceInteractionRouter.CacheEntry var11 = (SurfaceInteractionRouter.CacheEntry)var8.getValue();
            if (handle(var11) && var11.current != null) {
               int var12 = handle(var11, var0, var1);
               if (var12 != 0 && (var5 == null || var11.mode > var5.mode)) {
                  var5 = var11;
                  var3 = var9;
                  var6 = var12;
               }
            }
         }
      }

      if (var5 == null) {
         return false;
      }

      active = var3;
      if (var6 == 1) {
         mode = var1 - var5.state;
      } else {
         mode = var5.cache * 0.5F;
         handle(var5, var1);
      }

      return true;
   }

   public static boolean process(float var0, float var1) {
      if (active == 0L) {
         return false;
      } else {
         SurfaceInteractionRouter.CacheEntry var2 = cache.get(active);
         if (var2 != null && var2.current != null && handle(var2)) {
            handle(var2, var1);
            return true;
         } else {
            active = 0L;
            return false;
         }
      }
   }

   public static boolean process() {
      boolean var0 = active != 0L;
      active = 0L;
      return var0;
   }

   public static void compute() {
      active = 0L;
      current = 0L;
      cache.clear();
   }

   public static void resolve() {
      if (active == 0L) {
         cache.clear();
      }
   }

   private static boolean handle(SurfaceInteractionRouter.CacheEntry var0) {
      return output - var0.active <= 0L;
   }

   private static void handle(SurfaceInteractionRouter.CacheEntry var0, float var1) {
      float var2 = Math.max(1.0F, var0.config - var0.cache);
      float var3 = (var1 - mode - var0.data) / var2;
      var0.current.applyRatio(Math.max(0.0F, Math.min(1.0F, var3)));
   }

   private static int handle(SurfaceInteractionRouter.CacheEntry var0, float var1, float var2) {
      if (!(var0.context <= 0.0F) && !(var0.config <= 0.0F)) {
         float var3 = var0.output;
         if (var1 < var0.instance - var3 || var1 > var0.instance + var0.context + var3) {
            return 0;
         } else if (!(var2 < var0.data - var3 * 0.5F) && !(var2 > var0.data + var0.config + var3 * 0.5F)) {
            float var4 = Math.min(var3, 4.0F);
            return var2 >= var0.state - var4 && var2 <= var0.state + var0.cache + var4 ? 1 : 2;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   static final class CacheEntry {
      float instance;
      float data;
      float context;
      float config;
      float state;
      float cache;
      float output;
      SurfaceInteractionRouter.Callback current;
      long active;
      long mode;
      long selection;
      float enabled;
   }

   @FunctionalInterface
   public interface Callback {
      void applyRatio(float var1);
   }
}
