package ru.wild.render.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import ru.wild.render.shader.ShaderRenderer;
import ru.wild.util.io.ResourceReader;

public final class TextureAtlasHelper {
   private final String instance;
   private final String data;
   private final Int2ObjectMap<TextureAtlasHelper.TextureState> context;
   private final Long2FloatMap config;
   private volatile int state;
   private final int cache;
   private final int output;
   private final float current;
   private final float active;
   private final float mode;
   private final float selection;
   private final float enabled;
   private float renderer;

   private TextureAtlasHelper(
      String var1,
      String var2,
      int var3,
      int var4,
      int var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      Int2ObjectMap<TextureAtlasHelper.TextureState> var11,
      Long2FloatMap var12
   ) {
      this.instance = var1;
      this.data = var2;
      this.state = var3;
      this.cache = var4;
      this.output = var5;
      this.current = var6;
      this.active = var7;
      this.mode = var8;
      this.selection = var9;
      this.enabled = var10;
      this.context = var11;
      this.config = var12;
   }

   static TextureAtlasHelper handle(ShaderRenderer var0, String var1, String var2) {
      Objects.requireNonNull(var0, "backend");
      Objects.requireNonNull(var1, "jsonResourcePath");
      Objects.requireNonNull(var2, "textureResourcePath");
      String var3 = ResourceReader.handle(var1);
      JsonObject var4 = JsonParser.parseString(var3).getAsJsonObject();
      JsonObject var5 = var4.getAsJsonObject("atlas");
      if (var5 == null) {
         throw new IllegalStateException("Missing 'atlas' section in MSDF font: " + var1);
      }

      int var6 = var5.get("width").getAsInt();
      int var7 = var5.get("height").getAsInt();
      if (var6 > 0 && var7 > 0) {
         float var8 = var5.has("distanceRange") ? var5.get("distanceRange").getAsFloat() : 6.0F;
         JsonObject var9 = var4.getAsJsonObject("metrics");
         if (var9 == null) {
            throw new IllegalStateException("Missing 'metrics' section in MSDF font: " + var1);
         }

         float var10 = var9.has("emSize") ? var9.get("emSize").getAsFloat() : 1.0F;
         float var11 = var9.has("lineHeight") ? var9.get("lineHeight").getAsFloat() : var10;
         float var12 = var9.has("ascender") ? var9.get("ascender").getAsFloat() : var11;
         float var13 = var9.has("descender") ? var9.get("descender").getAsFloat() : 0.0F;
         float var14 = Math.abs(var13);
         Int2ObjectOpenHashMap var15 = new Int2ObjectOpenHashMap();
         JsonArray var16 = var4.getAsJsonArray("glyphs");
         if (var16 != null) {
            for (JsonElement var18 : var16) {
               JsonObject var19 = var18.getAsJsonObject();
               int var20 = var19.get("unicode").getAsInt();
               float var21 = var19.has("advance") ? var19.get("advance").getAsFloat() : 0.0F;
               JsonObject var22 = var19.has("planeBounds") ? var19.getAsJsonObject("planeBounds") : null;
               JsonObject var23 = var19.has("atlasBounds") ? var19.getAsJsonObject("atlasBounds") : null;
               TextureAtlasHelper.TextureState var24;
               if (var22 != null && var23 != null) {
                  float var25 = var22.get("left").getAsFloat();
                  float var26 = var22.get("bottom").getAsFloat();
                  float var27 = var22.get("right").getAsFloat();
                  float var28 = var22.get("top").getAsFloat();
                  float var29 = var23.get("left").getAsFloat();
                  float var30 = var23.get("bottom").getAsFloat();
                  float var31 = var23.get("right").getAsFloat();
                  float var32 = var23.get("top").getAsFloat();
                  var24 = new TextureAtlasHelper.TextureState(var21, var25, var26, var27, var28, var29, var30, var31, var32, var6, var7);
               } else {
                  var24 = new TextureAtlasHelper.TextureState(var21);
               }

               var15.put(var20, var24);
            }
         }

         Long2FloatOpenHashMap var33 = new Long2FloatOpenHashMap();
         var33.defaultReturnValue(0.0F);
         JsonArray var34 = var4.getAsJsonArray("kerning");
         if (var34 != null) {
            for (JsonElement var37 : var34) {
               JsonObject var38 = var37.getAsJsonObject();
               int var39 = var38.get("unicode1").getAsInt();
               int var40 = var38.get("unicode2").getAsInt();
               float var41 = var38.has("advance") ? var38.get("advance").getAsFloat() : 0.0F;
               var33.put(process(var39, var40), var41);
            }
         }

         TextureAtlasHelper.Bounds var36 = handle(var0, var2);
         return new TextureAtlasHelper(var1, var2, var36.textureId, var36.width, var36.height, var8, var10, var11, var12, var14, var15, var33);
      } else {
         throw new IllegalStateException("Invalid MSDF atlas dimensions in font: " + var1);
      }
   }

   void handle(ShaderRenderer var1) {
      Objects.requireNonNull(var1, "backend");
      int var2 = this.state;

      try {
         TextureAtlasHelper.Bounds var3 = handle(var1, this.data);
         if (var3.width != this.cache || var3.height != this.output) {
            if (var3.textureId > 0) {
               GL11.glDeleteTextures(var3.textureId);
            }

            return;
         }

         this.state = var3.textureId;
      } catch (Throwable var5) {
         return;
      }

      if (var2 > 0 && var2 != this.state) {
         try {
            GL11.glDeleteTextures(var2);
         } catch (Throwable var4) {
         }
      }
   }

   private static TextureAtlasHelper.Bounds handle(ShaderRenderer var0, String var1) {
      ByteBuffer var2 = ResourceReader.process(var1);
      MemoryStack var3 = MemoryStack.stackPush();

      TextureAtlasHelper.Bounds var11;
      try {
         IntBuffer var4 = var3.mallocInt(1);
         IntBuffer var5 = var3.mallocInt(1);
         IntBuffer var6 = var3.mallocInt(1);
         ByteBuffer var7 = STBImage.stbi_load_from_memory(var2, var4, var5, var6, 4);
         if (var7 == null) {
            throw new IllegalStateException("Failed to load MSDF atlas '" + var1 + "': " + STBImage.stbi_failure_reason());
         }

         try {
            int var8 = var4.get(0);
            int var9 = var5.get(0);
            int var10 = var0.handle(var8, var9, var7);
            var11 = new TextureAtlasHelper.Bounds(var10, var8, var9);
         } finally {
            STBImage.stbi_image_free(var7);
         }
      } catch (Throwable var18) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var16) {
               var18.addSuppressed(var16);
            }
         }

         throw var18;
      }

      if (var3 != null) {
         var3.close();
      }

      return var11;
   }

   int handle() {
      return this.state;
   }

   boolean process() {
      int var1 = this.state;
      return var1 > 0 && GL11.glIsTexture(var1);
   }

   int compute() {
      return this.cache;
   }

   int resolve() {
      return this.output;
   }

   float update() {
      return this.current;
   }

   float apply() {
      return this.active;
   }

   float execute() {
      return this.mode;
   }

   float prepare() {
      return this.selection;
   }

   float check() {
      return this.enabled;
   }

   TextureAtlasHelper.TextureState handle(int var1) {
      return (TextureAtlasHelper.TextureState)this.context.get(var1);
   }

   float onTick() {
      float var1 = this.renderer;
      if (var1 > 0.0F) {
         return var1;
      }

      float var2 = 0.0F;
      ObjectIterator var3 = this.context.values().iterator();

      while (var3.hasNext()) {
         TextureAtlasHelper.TextureState var4 = (TextureAtlasHelper.TextureState)var3.next();
         if (var4.data) {
            float var5 = var4.state - var4.context;
            float var6 = Math.abs(var4.active - var4.output);
            if (var5 > 1.0E-5F && var6 > 1.0E-6F) {
               var2 = var6 * this.cache / var5;
               break;
            }
         }
      }

      this.renderer = var2 > 0.0F ? var2 : 1.0F;
      return this.renderer;
   }

   float select() {
      return this.current * 0.5F / this.onTick();
   }

   float handle(int var1, int var2) {
      return this.config.get(process(var1, var2));
   }

   private static long process(int var0, int var1) {
      return (long)var0 << 32 | var1 & 4294967295L;
   }

   static float handle(float var0) {
      return handle(var0, 0.0F, 1.0F);
   }

   static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   record Bounds(int textureId, int width, int height) {
   }

   static final class TextureState {
      final float instance;
      final boolean data;
      final float context;
      final float config;
      final float state;
      final float cache;
      final float output;
      final float current;
      final float active;
      final float mode;

      TextureState(float var1) {
         this.instance = var1;
         this.data = false;
         this.context = 0.0F;
         this.config = 0.0F;
         this.state = 0.0F;
         this.cache = 0.0F;
         this.output = 0.0F;
         this.current = 0.0F;
         this.active = 0.0F;
         this.mode = 0.0F;
      }

      TextureState(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11) {
         this.instance = var1;
         this.data = true;
         this.context = var2;
         this.config = var3;
         this.state = var4;
         this.cache = var5;
         float var12 = 1.0F / Math.max(1.0F, var10);
         float var13 = 1.0F / Math.max(1.0F, var11);
         float var14 = TextureAtlasHelper.handle(Math.min(var6, var8) * var12);
         float var15 = TextureAtlasHelper.handle(Math.max(var6, var8) * var12);
         float var16 = TextureAtlasHelper.handle(Math.min(var7, var9) * var13);
         float var17 = TextureAtlasHelper.handle(Math.max(var7, var9) * var13);
         this.output = TextureAtlasHelper.handle(var14, 0.0F, 1.0F);
         this.active = TextureAtlasHelper.handle(var15, 0.0F, 1.0F);
         float var18 = TextureAtlasHelper.handle(var16, 0.0F, 1.0F);
         float var19 = TextureAtlasHelper.handle(var17, 0.0F, 1.0F);
         this.current = 1.0F - var18;
         this.mode = 1.0F - var19;
      }
   }
}
