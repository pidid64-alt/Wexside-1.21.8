package ru.wild.render.font;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import ru.wild.util.render.RoundedRectRenderer;

public final class TextMeasureCache {
   private static final int instance = 4096;
   private static final Map<TextMeasureCache.DataRecord, TextMeasureCache.State> data = new LinkedHashMap<TextMeasureCache.DataRecord, TextMeasureCache.State>(
      1024, 0.75F, true
   ) {
      @Override
      protected boolean removeEldestEntry(Entry<TextMeasureCache.DataRecord, TextMeasureCache.State> var1) {
         return this.size() > 4096;
      }
   };

   private TextMeasureCache() {
   }

   public static TextMeasureCache.State handle(FontObject var0, String var1, float var2) {
      if (var1 == null) {
         var1 = "";
      }

      TextMeasureCache.DataRecord var3 = new TextMeasureCache.DataRecord(var0, var1, Float.floatToIntBits(var2));
      TextMeasureCache.State var4 = data.get(var3);
      if (var4 != null) {
         return var4;
      }

      SdfTextRenderer.State var5 = RoundedRectRenderer.handle(var0, var1, var2);
      var4 = new TextMeasureCache.State(var5.instance, var5.data);
      data.put(var3, var4);
      return var4;
   }

   public static float process(FontObject var0, String var1, float var2) {
      return handle(var0, var1, var2).instance;
   }

   public static float compute(FontObject var0, String var1, float var2) {
      return handle(var0, var1, var2).data;
   }

   public static void handle() {
      data.clear();
   }

   record DataRecord(FontObject font, String text, int sizeBits) {
   }

   public static final class State {
      public final float instance;
      public final float data;

      State(float var1, float var2) {
         this.instance = var1;
         this.data = var2;
      }
   }
}
