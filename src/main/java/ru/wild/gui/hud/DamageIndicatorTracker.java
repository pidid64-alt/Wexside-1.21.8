package ru.wild.gui.hud;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.UUID;

public final class DamageIndicatorTracker {
   private static final Object2FloatOpenHashMap<UUID> instance = new Object2FloatOpenHashMap();
   private static final Object2LongOpenHashMap<UUID> data = new Object2LongOpenHashMap();
   private static final long context = 160L;

   private DamageIndicatorTracker() {
   }

   public static void handle(UUID var0, float var1) {
      if (var0 != null && Float.isFinite(var1)) {
         long var2 = System.currentTimeMillis();
         boolean var4 = var2 - data.getLong(var0) > 160L;
         if (var4 || var1 < instance.getFloat(var0)) {
            instance.put(var0, var1);
         }

         data.put(var0, var2);
      }
   }

   public static float process(UUID var0, float var1) {
      return var0 != null && System.currentTimeMillis() - data.getLong(var0) <= 160L ? Math.min(var1, instance.getFloat(var0)) : var1;
   }
}
