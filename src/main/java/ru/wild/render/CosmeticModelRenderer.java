package ru.wild.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.wild.render.texture.StudioTextureLoader;
import ru.wild.render.texture.TextureHolder;
import ru.wild.util.render.RoundedRectRenderer;

public final class CosmeticModelRenderer {
   private static final float context = 1.0F;
   private static final float config = 0.5F;
   private static final float state = 0.82F;
   private static final float cache = 0.66F;
   private static final float output = 0.86F;
   private static final int current = 1;
   private final Vector3f active = new Vector3f();
   private final Vector3f mode = new Vector3f();
   private final Vector3f selection = new Vector3f();
   private final Matrix4f enabled = new Matrix4f();
   private final float[] renderer = new float[3];
   private final List<CosmeticModelRenderer.CacheEntry> handler = new ArrayList<>(256);
   private float animationDraw;
   private boolean pointEncode;
   private TextureHolder animator;
   private String source;
   private StudioTextureLoader target;
   private float pending;
   private float previous;
   private float latest;
   private float summary;
   private float matrixBlend;
   private float vectorMatch;
   public int instance;
   public int data;

   public void handle(
      RoundedRectRenderer var1,
      TextureHolder var2,
      String var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      boolean var11
   ) {
      if (var1 != null && var2 != null) {
         this.animationDraw = var10;
         this.pointEncode = var11;
         this.animator = var2;
         this.source = var3;
         this.target = StudioTextureLoader.handle();
         this.pending = var4;
         this.previous = var5;
         this.latest = var6;
         this.summary = var2.execute();
         this.matrixBlend = var2.prepare();
         this.vectorMatch = var2.check();
         this.enabled.identity().rotateX((float)Math.toRadians(var8)).rotateY((float)Math.toRadians(var7));
         this.handler.clear();
         Matrix4f var12 = new Matrix4f();

         for (TextureHolder.TextureState var14 : var2.resolve()) {
            this.handle(var14, var12);
         }

         this.handler.sort(Comparator.comparingDouble(var0 -> var0.context));
         this.instance = this.handler.size();
         int var16 = 0;

         for (CosmeticModelRenderer.CacheEntry var15 : this.handler) {
            if (this.handle(var1, var15, var9)) {
               var16++;
            }
         }

         this.data = var16;
         this.handler.clear();
      }
   }

   private void handle(TextureHolder.TextureState var1, Matrix4f var2) {
      Matrix4f var3 = new Matrix4f(var2);
      if (var1.prepare()) {
         handle(var3, var1.process(), var1.compute(), var1.resolve(), var1.execute(), var1.apply(), var1.update());
      }

      if (this.pointEncode && this.handle(var1.handle())) {
         var3.translate(var1.process(), var1.compute(), var1.resolve())
            .rotateZYX(this.renderer[2], this.renderer[1], this.renderer[0])
            .translate(-var1.process(), -var1.compute(), -var1.resolve());
      }

      for (TextureHolder.RuntimeTextureState var5 : var1.onTick()) {
         this.handle(var5, var3);
      }

      for (TextureHolder.CachedTextureState var8 : var1.select()) {
         this.handle(var8, var3);
      }

      for (TextureHolder.TextureState var9 : var1.check()) {
         this.handle(var9, var3);
      }
   }

   private static void handle(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      var0.translate(var1, var2, var3)
         .rotateZYX((float)Math.toRadians(var4), (float)Math.toRadians(var5), (float)Math.toRadians(var6))
         .translate(-var1, -var2, -var3);
   }

   private boolean handle(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = var1.toLowerCase();
         float var3 = Math.abs(var1.hashCode()) % 1000 * 0.0123F;
         this.renderer[0] = this.renderer[1] = this.renderer[2] = 0.0F;
         if (var2.contains("tail") || var2.startsWith("seg")) {
            this.renderer[0] = (float)Math.sin(this.animationDraw * 1.9F + var3) * 0.16F;
            this.renderer[1] = (float)Math.sin(this.animationDraw * 1.3F + var3) * 0.1F;
            return true;
         } else if (var2.contains("ear")) {
            float var4 = var2.contains("left") ? 1.0F : -1.0F;
            this.renderer[2] = var4 * (0.05F + (float)Math.sin(this.animationDraw * 2.4F + var3) * 0.08F);
            return true;
         } else if (var2.contains("cape") || var2.contains("wing")) {
            this.renderer[0] = -0.08F + (float)Math.sin(this.animationDraw * 1.6F + var3) * 0.13F;
            return true;
         } else if (var2.equals("head")) {
            this.renderer[1] = (float)Math.sin(this.animationDraw * 0.5F) * 0.1F;
            this.renderer[0] = (float)Math.sin(this.animationDraw * 0.4F) * 0.04F;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void handle(TextureHolder.RuntimeTextureState var1, Matrix4f var2) {
      Matrix4f var3 = new Matrix4f(var2);
      if (var1.tick()) {
         handle(var3, var1.execute(), var1.prepare(), var1.check(), var1.refresh(), var1.select(), var1.onTick());
      }

      float var4 = var1.render();
      float var5 = var1.handle() - var4;
      float var6 = var1.process() - var4;
      float var7 = var1.compute() - var4;
      float var8 = var1.resolve() + var4;
      float var9 = var1.update() + var4;
      float var10 = var1.apply() + var4;
      this.handle(var1.handle(0), var3, 0.82F, 0.0F, 0.0F, -1.0F, var8, var9, var7, var5, var9, var7, var5, var6, var7, var8, var6, var7);
      this.handle(var1.handle(2), var3, 0.82F, 0.0F, 0.0F, 1.0F, var5, var9, var10, var8, var9, var10, var8, var6, var10, var5, var6, var10);
      this.handle(var1.handle(1), var3, 0.66F, 1.0F, 0.0F, 0.0F, var8, var9, var10, var8, var9, var7, var8, var6, var7, var8, var6, var10);
      this.handle(var1.handle(3), var3, 0.66F, -1.0F, 0.0F, 0.0F, var5, var9, var7, var5, var9, var10, var5, var6, var10, var5, var6, var7);
      this.handle(var1.handle(4), var3, 1.0F, 0.0F, 1.0F, 0.0F, var5, var9, var7, var8, var9, var7, var8, var9, var10, var5, var9, var10);
      this.handle(var1.handle(5), var3, 0.5F, 0.0F, -1.0F, 0.0F, var5, var6, var10, var8, var6, var10, var8, var6, var7, var5, var6, var7);
   }

   private void handle(
      TextureHolder.SecondaryTextureState var1,
      Matrix4f var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18
   ) {
      if (var1 != null) {
         this.handle(
            var2,
            var1.handle(),
            var3,
            var4,
            var5,
            var6,
            var1.process(),
            var1.compute(),
            var1.resolve(),
            var1.compute(),
            var1.resolve(),
            var1.update(),
            var1.process(),
            var1.update(),
            var7,
            var8,
            var9,
            var10,
            var11,
            var12,
            var13,
            var14,
            var15,
            var16,
            var17,
            var18,
            false
         );
      }
   }

   private void handle(TextureHolder.CachedTextureState var1, Matrix4f var2) {
      Matrix4f var3 = new Matrix4f(var2);
      var3.translate(var1.handle(), var1.process(), var1.compute());
      if (var1.check()) {
         var3.rotateZYX((float)Math.toRadians(var1.apply()), (float)Math.toRadians(var1.update()), (float)Math.toRadians(var1.resolve()));
      }

      for (TextureHolder.FallbackTextureState var7 : var1.prepare()) {
         int var8 = var7.handle(0);
         int var9 = var7.handle(1);
         int var10 = var7.handle(2);
         int var11 = var7.handle() >= 4 ? var7.handle(3) : var10;
         float var12 = var1.handle(var8);
         float var13 = var1.process(var8);
         float var14 = var1.compute(var8);
         float var15 = var1.handle(var9);
         float var16 = var1.process(var9);
         float var17 = var1.compute(var9);
         float var18 = var1.handle(var10);
         float var19 = var1.process(var10);
         float var20 = var1.compute(var10);
         float var21 = var1.handle(var11);
         float var22 = var1.process(var11);
         float var23 = var1.compute(var11);
         this.mode.set(var15 - var12, var16 - var13, var17 - var14);
         this.selection.set(var18 - var12, var19 - var13, var20 - var14);
         this.mode.cross(this.selection);
         float var24 = var7.handle() >= 4 ? 3.0F : 2.0F;
         this.handle(
            var3,
            var7.process(),
            0.86F,
            this.mode.x,
            this.mode.y,
            this.mode.z,
            var7.process(0),
            var7.compute(0),
            var7.process(1),
            var7.compute(1),
            var7.process((int)var24),
            var7.compute((int)var24),
            var7.process(var7.handle() >= 4 ? 3 : 2),
            var7.compute(var7.handle() >= 4 ? 3 : 2),
            var12,
            var13,
            var14,
            var15,
            var16,
            var17,
            var18,
            var19,
            var20,
            var21,
            var22,
            var23,
            true
         );
      }
   }

   private void handle(
      Matrix4f var1,
      int var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19,
      float var20,
      float var21,
      float var22,
      float var23,
      float var24,
      float var25,
      float var26,
      boolean var27
   ) {
      this.active.set(var4, var5, var6);
      var1.transformDirection(this.active);
      this.enabled.transformDirection(this.active);
      boolean var28 = this.active.z * 1.0F > 0.02F;
      if (var28 || var27) {
         if (!var28 && var27) {
            var3 *= 0.78F;
         }

         CosmeticModelRenderer.CacheEntry var29 = new CosmeticModelRenderer.CacheEntry();
         float var30 = 0.0F;
         var30 += this.handle(var1, var15, var16, var17, var29, 0);
         var30 += this.handle(var1, var18, var19, var20, var29, 1);
         var30 += this.handle(var1, var21, var22, var23, var29, 2);
         var30 += this.handle(var1, var24, var25, var26, var29, 3);
         var29.context = var30 * 0.25F * 1.0F;
         var29.config = var3;
         TextureHolder.PrimaryTextureState var31 = this.animator.handle(var2);
         float var32 = var31 == null ? this.animator.handle() : var31.compute();
         float var33 = var31 == null ? this.animator.process() : var31.resolve();
         var29.cache = var7 / var32;
         var29.output = var8 / var33;
         var29.current = var11 / var32;
         var29.active = var12 / var33;
         var29.state = this.target.handle(this.source, var2, this.animator);
         this.handler.add(var29);
      }
   }

   private float handle(Matrix4f var1, float var2, float var3, float var4, CosmeticModelRenderer.CacheEntry var5, int var6) {
      this.active.set(var2, var3, var4);
      var1.transformPosition(this.active);
      this.active.sub(this.summary, this.matrixBlend, this.vectorMatch);
      this.enabled.transformPosition(this.active);
      var5.instance[var6] = this.pending + this.active.x * this.latest;
      var5.data[var6] = this.previous - this.active.y * this.latest;
      return this.active.z;
   }

   private boolean handle(RoundedRectRenderer var1, CosmeticModelRenderer.CacheEntry var2, float var3) {
      float var4 = var2.instance[0];
      float var5 = var2.data[0];
      float var6 = var2.instance[1] - var4;
      float var7 = var2.data[1] - var5;
      float var8 = var2.instance[3] - var4;
      float var9 = var2.data[3] - var5;
      if (Math.abs(var6 * var9 - var7 * var8) < 0.05F) {
         return false;
      }

      float[] var10 = new float[]{var6, var8, var4, var7, var9, var5, 0.0F, 0.0F, 1.0F};
      var1.process(var10);

      try {
         if (var2.state > 0) {
            var1.handle(var2.state, 0.0F, 0.0F, 1.0F, 1.0F, var2.cache, var2.output, var2.current, var2.active);
            float var11 = (1.0F - var2.config) * 0.55F;
            if (var11 > 0.01F) {
               var1.handle(0.0F, 0.0F, 1.0F, 1.0F, 0.0F, handle(0, 0, 0, Math.round(var11 * 255.0F)));
            }
         } else {
            int var15 = Math.round(205.0F * var2.config);
            var1.handle(0.0F, 0.0F, 1.0F, 1.0F, 0.0F, handle(var15, var15, Math.min(255, var15 + 12), 255));
         }
      } finally {
         var1.prepare();
      }

      return true;
   }

   private static int handle(int var0, int var1, int var2, int var3) {
      return var3 << 24 | var0 << 16 | var1 << 8 | var2;
   }

   static final class CacheEntry {
      final float[] instance = new float[4];
      final float[] data = new float[4];
      float context;
      float config;
      int state;
      float cache;
      float output;
      float current;
      float active;
   }
}
