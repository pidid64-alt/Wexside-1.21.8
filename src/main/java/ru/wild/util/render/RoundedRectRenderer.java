package ru.wild.util.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.wild.mixin.acceser.GameRendererAccessor;
import ru.wild.WildClient;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.BlurStateManager;
import ru.wild.render.GlCompatibilityProbe;
import ru.wild.render.RenderAttemptGuard;
import ru.wild.render.RoundedGeometryBatch;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.SdfTextRenderer;
import ru.wild.render.shader.ShaderRenderer;
import ru.wild.render.shader.ShaderViewportTracker;
import ru.wild.util.math.NumericTransform;

public final class RoundedRectRenderer {
   private static final float config = 0.5F;
   private static final float state = 0.05F;
   private static final Float cache = 1.0F;
   public static volatile BooleanSupplier instance = () -> true;
   public static volatile BooleanSupplier data = () -> true;
   private final ShaderRenderer output;
   private final RenderAttemptGuard current = new RenderAttemptGuard();
   private final ArrayDeque<RoundedRectRenderer.PrimaryDataRecord> active = new ArrayDeque<>();
   private final ArrayDeque<Float> mode = new ArrayDeque<>();
   private final ArrayDeque<Boolean> selection = new ArrayDeque<>();
   private final ArrayDeque<RoundedRectRenderer.PrimaryColorState> enabled = new ArrayDeque<>();
   private final ArrayList<RoundedRectRenderer.PrimaryColorState> renderer = new ArrayList<>();
   private final AffineTransformStack handler = new AffineTransformStack();
   private static Map<String, SdfTextRenderer> animationDraw = new HashMap<>();
   private final RoundedGeometryBatch pointEncode;
   private boolean animator = false;
   private int source = 0;
   private int target = 0;
   private boolean pending = false;
   private float previous = 0.0F;
   private int latest = 0;
   private int summary = 0;
   private boolean matrixBlend = false;
   private float vectorMatch = 0.0F;
   private int itemProject = 0;
   private int responseCompute = 0;
   private int providerFetch = 0;
   private int profileDraw = 0;
   private static final ThreadLocal<float[]> vectorPerform = ThreadLocal.withInitial(() -> new float[4]);
   private int eventAttach = 0;
   private int serverRead = 0;
   public static MinecraftClient context = MinecraftClient.getInstance();
   private static boolean font;

   public RoundedRectRenderer(ShaderRenderer var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("GlBackend cannot be null");
      }

      this.output = var1;
      this.pointEncode = new RoundedGeometryBatch(var1);
      this.drawAnimation();
   }

   public void handle(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         if (this.animator) {
            this.handle();
         }

         this.animator = true;
         this.source = var1;
         this.target = var2;
         this.pending = false;
         this.matrixBlend = false;
         this.previous = 0.0F;
         this.vectorMatch = 0.0F;
         this.latest = 0;
         this.summary = 0;
         this.itemProject = 0;
         this.responseCompute = 0;
         this.providerFetch = 0;
         this.profileDraw = 0;
         if (this.output != null) {
            ShaderViewportTracker.handle().handle(var1, var2);
            this.output.compute(var1, var2);
            if (var1 != this.eventAttach || var2 != this.serverRead) {
               this.eventAttach = var1;
               this.serverRead = var2;
            }

            this.output.process(false);
         }

         if (!this.active.isEmpty()) {
            this.active.clear();
         }

         this.enabled.clear();
         this.handler.handle();
         this.drawAnimation();
         this.encodePoint();
      } else {
         throw new IllegalArgumentException("Width and height must be positive, got: " + var1 + "x" + var2);
      }
   }
   public void handle() {
      boolean var5 = false /* VF: Semaphore variable */;

      label56: {
         try {
            var5 = true;
            if (this.pointEncode != null) {
               this.pointEncode.handle();
            }

            if (this.output != null) {
               this.output.handle();
            }

            ShaderViewportTracker.handle().process();
            var5 = false;
            break label56;
         } catch (Throwable var6) {
            var5 = false;
         } finally {
            if (var5) {
               this.animator = false;
               this.source = 0;
               this.target = 0;
               this.pending = false;
               this.previous = 0.0F;
               this.latest = 0;
               this.summary = 0;
               this.matrixBlend = false;
               this.vectorMatch = 0.0F;
               this.itemProject = 0;
               this.responseCompute = 0;
               this.providerFetch = 0;
               this.profileDraw = 0;
               this.active.clear();
               this.enabled.clear();
               this.handler.handle();
               this.drawAnimation();
               this.encodePoint();
            }
         }

         this.animator = false;
         this.source = 0;
         this.target = 0;
         this.pending = false;
         this.previous = 0.0F;
         this.latest = 0;
         this.summary = 0;
         this.matrixBlend = false;
         this.vectorMatch = 0.0F;
         this.itemProject = 0;
         this.responseCompute = 0;
         this.providerFetch = 0;
         this.profileDraw = 0;
         this.active.clear();
         this.enabled.clear();
         this.handler.handle();
         this.drawAnimation();
         this.encodePoint();
         return;
      }

      this.animator = false;
      this.source = 0;
      this.target = 0;
      this.pending = false;
      this.previous = 0.0F;
      this.latest = 0;
      this.summary = 0;
      this.matrixBlend = false;
      this.vectorMatch = 0.0F;
      this.itemProject = 0;
      this.responseCompute = 0;
      this.providerFetch = 0;
      this.profileDraw = 0;
      this.active.clear();
      this.enabled.clear();
      this.handler.handle();
      this.drawAnimation();
      this.encodePoint();
   }

   private void tick() {
      if (!this.animator) {
         throw new IllegalStateException("begin() must be called before issuing draw commands");
      }

      if (this.output == null) {
         throw new IllegalStateException("Renderer2D backend is null - initialization failed");
      }

      if (this.pointEncode == null) {
         throw new IllegalStateException("Renderer2D batcher is null - initialization failed");
      }
   }

   private float[] compute(float var1, float var2, float var3, float var4, float var5, float var6) {
      float[] var7 = vectorPerform.get();
      var7[0] = Math.max(0.0F, var3);
      var7[1] = Math.max(0.0F, var4);
      var7[2] = Math.max(0.0F, var5);
      var7[3] = Math.max(0.0F, var6);
      float var8 = Math.min(Math.abs(var1), Math.abs(var2)) * 0.5F;
      if (var8 <= 0.0F) {
         var7[0] = var7[1] = var7[2] = var7[3] = 0.0F;
         return var7;
      } else {
         var7[0] = Math.min(var7[0], var8);
         var7[1] = Math.min(var7[1], var8);
         var7[2] = Math.min(var7[2], var8);
         var7[3] = Math.min(var7[3], var8);
         return var7;
      }
   }

   public void handle(float var1, float var2, float var3, float var4, int var5) {
      this.tick();
      this.pointEncode.handle(var1, var2, var3, var4, 0.0F, 0.0F, 0.0F, 0.0F, this.process(var5), this.handler.update());
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, int var6) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      this.tick();
      float[] var10 = this.compute(var3, var4, var5, var6, var7, var8);
      this.pointEncode.handle(var1, var2, var3, var4, var10[0], var10[1], var10[2], var10[3], this.process(var9), this.handler.update());
   }

   public void handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12,
      float var13,
      boolean var14
   ) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, 0);
   }

   public void handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12,
      float var13,
      boolean var14,
      int var15
   ) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
   }

   public void handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      int var11,
      int var12,
      float var13,
      float var14,
      float var15,
      float var16,
      boolean var17
   ) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, 0);
   }

   public void handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      int var11,
      int var12,
      float var13,
      float var14,
      float var15,
      float var16,
      boolean var17,
      int var18
   ) {
      this.tick();
      float[] var19 = this.compute(var3, var4, var5, var6, var7, var8);
      this.pointEncode
         .handle(
            var1,
            var2,
            var3,
            var4,
            var19[0],
            var19[1],
            var19[2],
            var19[3],
            this.process(var9),
            this.process(var10),
            this.process(var11),
            this.process(var12),
            var13,
            var14,
            var15,
            var16,
            var17,
            var18,
            this.handler.update()
         );
   }

   public void handle(int var1, float var2, float var3, float var4, float var5) {
      this.handle(var1, var2, var3, var4, var5, -1, true, false);
   }

   public void handle(int var1, float var2, float var3, float var4, float var5, int var6) {
      this.handle(var1, var2, var3, var4, var5, var6, true, false);
   }

   public void handle(int var1, float var2, float var3, float var4, float var5, int var6, boolean var7) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, false);
   }

   public void process(int var1, float var2, float var3, float var4, float var5) {
      this.handle(var1, var2, var3, var4, var5, -1, true, true);
   }

   public void process(int var1, float var2, float var3, float var4, float var5, int var6, boolean var7) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, true);
   }

   public void handle(int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      this.tick();
      if (var1 > 0) {
         this.compute();
         this.output.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, this.process(-1), this.handler.update(), false);
      }
   }

   public void handle(int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      this.tick();
      if (var1 > 0) {
         this.compute();
         this.output.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, this.process(-1), this.handler.update(), false);
      }
   }

   public void process(int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      this.tick();
      if (var1 > 0) {
         this.compute();
         this.output.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, this.process(-1), this.handler.update(), true);
      }
   }

   private void handle(int var1, float var2, float var3, float var4, float var5, int var6, boolean var7, boolean var8) {
      this.tick();
      if (var1 > 0) {
         this.compute();
         float var9 = var7 ? 1.0F : 0.0F;
         float var10 = var7 ? 0.0F : 1.0F;
         this.output.handle(var1, var2, var3, var4, var5, 0.0F, var9, 1.0F, var10, this.process(var6), this.handler.update(), var8);
      }
   }

   public RoundedRectRenderer.PrimaryColorState handle(float var1, float var2, float var3, float var4) {
      return GlCompatibilityProbe.process() ? null : this.handle(var1, var2, var3, var4, false);
   }

   public RoundedRectRenderer.PrimaryColorState process(float var1, float var2, float var3, float var4) {
      return !GlCompatibilityProbe.process() && this.current.handle() ? this.handle(var1, var2, var3, var4, true) : null;
   }

   private RoundedRectRenderer.PrimaryColorState handle(float var1, float var2, float var3, float var4, boolean var5) {
      this.tick();
      if (this.source > 0 && this.target > 0 && !(var3 <= 0.0F) && !(var4 <= 0.0F)) {
         int var6 = (int)Math.ceil(var3);
         int var7 = (int)Math.ceil(var4);
         if (var6 > 0 && var7 > 0) {
            this.compute();
            ShaderRenderer.PrimaryColorState var8;
            if (var5) {
               try {
                  var8 = this.output.process(var6, var7);
               } catch (RuntimeException var11) {
                  this.current.process();
                  return null;
               }
            } else {
               var8 = this.output.handle(var6, var7);
            }

            if (var8 == null) {
               return null;
            }

            float[] var9 = this.enabled.isEmpty() ? this.handler.update() : this.enabled.peek().handler;
            RoundedRectRenderer.PrimaryColorState var10 = this.handle(this.enabled.size());
            var10.instance = var8;
            var10.data = this.source;
            var10.context = this.target;
            var10.config = this.pending;
            var10.state = this.previous;
            var10.cache = this.latest;
            var10.output = this.summary;
            var10.current = this.matrixBlend;
            var10.active = this.vectorMatch;
            var10.mode = this.itemProject;
            var10.selection = this.responseCompute;
            var10.enabled = this.providerFetch;
            var10.renderer = this.profileDraw;
            var10.handler = var9;
            var10.animationDraw = var1;
            var10.pointEncode = var2;
            this.handler.handle(var10.source);
            var10.target.clear();
            var10.target.addAll(this.active);
            var10.pending.clear();
            var10.pending.addAll(this.mode);
            var10.previous.clear();
            var10.previous.addAll(this.selection);
            this.enabled.push(var10);
            this.source = var6;
            this.target = var7;
            this.pending = false;
            this.previous = 0.0F;
            this.latest = 0;
            this.summary = 0;
            this.matrixBlend = false;
            this.vectorMatch = 0.0F;
            this.itemProject = 0;
            this.responseCompute = 0;
            this.providerFetch = 0;
            this.profileDraw = 0;
            this.active.clear();
            this.handler.handle(var10.animator, -var1, -var2);
            this.drawAnimation();
            this.encodePoint();
            this.output.process(false);
            return var10;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private RoundedRectRenderer.PrimaryColorState handle(int var1) {
      while (this.renderer.size() <= var1) {
         this.renderer.add(new RoundedRectRenderer.PrimaryColorState());
      }

      return this.renderer.get(var1);
   }

   public void handle(RoundedRectRenderer.PrimaryColorState var1) {
      this.tick();
      if (var1 != null && var1.instance != null) {
         this.compute();
         this.output.handle(var1.instance);
         this.source = var1.data;
         this.target = var1.context;
         this.pending = var1.config;
         this.previous = var1.state;
         this.latest = var1.cache;
         this.summary = var1.output;
         this.matrixBlend = var1.current;
         this.vectorMatch = var1.active;
         this.itemProject = var1.mode;
         this.responseCompute = var1.selection;
         this.providerFetch = var1.enabled;
         this.profileDraw = var1.renderer;
         this.handler.compute(var1.source);
         if (!this.enabled.isEmpty()) {
            this.enabled.pop();
         }

         this.active.clear();
         this.active.addAll(var1.target);
         this.mode.clear();
         this.mode.addAll(var1.pending);
         this.selection.clear();
         this.selection.addAll(var1.previous);
         if (this.mode.isEmpty()) {
            this.drawAnimation();
         }

         if (this.selection.isEmpty()) {
            this.selection.push(false);
         }

         this.output.handle(this.selection.peek());
         this.output.compute();
         if (this.active.isEmpty()) {
            this.output.process(false);
         } else {
            this.handle(this.active.peek());
         }
      }
   }

   public void handle(
      RoundedRectRenderer.PrimaryColorState var1,
      float var2,
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
      float var15
   ) {
      this.tick();
      if (var1 != null && var1.instance != null) {
         float var16 = this.animate();
         if (!(var16 <= 1.0E-4F)) {
            RoundedRectRenderer.PrimaryDataRecord var17 = this.active.peek();
            int var18 = var17 == null ? 0 : var17.x();
            int var19 = var17 == null ? 0 : var17.y();
            int var20 = var17 == null ? this.source : var17.w();
            int var21 = var17 == null ? this.target : var17.h();
            float var22 = var17 == null ? 0.0F : var17.roundTopLeft();
            float var23 = var17 == null ? 0.0F : var17.roundTopRight();
            float var24 = var17 == null ? 0.0F : var17.roundBottomRight();
            float var25 = var17 == null ? 0.0F : var17.roundBottomLeft();
            this.output
               .handle(
                  var1.instance.handle(),
                  var1.instance.process(),
                  var1.instance.compute(),
                  var2,
                  var3,
                  var4,
                  var5,
                  var6,
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
                  this.handler.update(),
                  var18,
                  var19,
                  var20,
                  var21,
                  var22,
                  var23,
                  var24,
                  var25
               );
         }
      }
   }

   public boolean handle(
      RoundedRectRenderer.PrimaryColorState var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      int var7,
      int var8,
      int var9,
      int var10,
      float var11,
      float var12
   ) {
      this.tick();
      if (var1 != null && var1.instance != null) {
         float var13 = this.animate();
         if (var13 <= 1.0E-4F) {
            return true;
         }

         if (!this.current.handle()) {
            return false;
         }

         RoundedRectRenderer.PrimaryDataRecord var14 = this.active.peek();
         int var15 = var14 == null ? 0 : var14.x();
         int var16 = var14 == null ? 0 : var14.y();
         int var17 = var14 == null ? this.source : var14.w();
         int var18 = var14 == null ? this.target : var14.h();
         float var19 = var14 == null ? 0.0F : var14.roundTopLeft();
         float var20 = var14 == null ? 0.0F : var14.roundTopRight();
         float var21 = var14 == null ? 0.0F : var14.roundBottomRight();
         float var22 = var14 == null ? 0.0F : var14.roundBottomLeft();

         try {
            this.output
               .handle(
                  var1.instance.handle(),
                  var1.instance.resolve(),
                  var1.instance.update(),
                  var1.instance.process(),
                  var1.instance.compute(),
                  var2,
                  var3,
                  var4,
                  var5,
                  var6,
                  var7,
                  var8,
                  var9,
                  var10,
                  var11,
                  var12,
                  var13,
                  this.handler.update(),
                  var15,
                  var16,
                  var17,
                  var18,
                  var19,
                  var20,
                  var21,
                  var22
               );
            return true;
         } catch (RuntimeException var24) {
            this.current.process();
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean handle(
      RoundedRectRenderer.PrimaryColorState var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      int var7,
      int var8,
      int var9,
      int var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16
   ) {
      this.tick();
      if (var1 != null && var1.instance != null) {
         float var17 = this.animate();
         if (var17 <= 1.0E-4F) {
            return true;
         }

         if (!this.current.handle()) {
            return false;
         }

         RoundedRectRenderer.PrimaryDataRecord var18 = this.active.peek();
         int var19 = var18 == null ? 0 : var18.x();
         int var20 = var18 == null ? 0 : var18.y();
         int var21 = var18 == null ? this.source : var18.w();
         int var22 = var18 == null ? this.target : var18.h();
         float var23 = var18 == null ? 0.0F : var18.roundTopLeft();
         float var24 = var18 == null ? 0.0F : var18.roundTopRight();
         float var25 = var18 == null ? 0.0F : var18.roundBottomRight();
         float var26 = var18 == null ? 0.0F : var18.roundBottomLeft();

         try {
            this.output
               .handle(
                  var1.instance.handle(),
                  var1.instance.resolve(),
                  var1.instance.update(),
                  var1.instance.process(),
                  var1.instance.compute(),
                  var2,
                  var3,
                  var4,
                  var5,
                  var6,
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
                  this.handler.update(),
                  var19,
                  var20,
                  var21,
                  var22,
                  var23,
                  var24,
                  var25,
                  var26
               );
            return true;
         } catch (RuntimeException var28) {
            this.current.process();
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean handle(
      RoundedRectRenderer.PrimaryColorState var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      int var7,
      int var8,
      int var9,
      float var10,
      float var11
   ) {
      this.tick();
      if (var1 != null && var1.instance != null) {
         float var12 = this.animate();
         if (var12 <= 1.0E-4F) {
            return true;
         }

         if (!this.current.handle()) {
            return false;
         }

         RoundedRectRenderer.PrimaryDataRecord var13 = this.active.peek();
         int var14 = var13 == null ? 0 : var13.x();
         int var15 = var13 == null ? 0 : var13.y();
         int var16 = var13 == null ? this.source : var13.w();
         int var17 = var13 == null ? this.target : var13.h();
         float var18 = var13 == null ? 0.0F : var13.roundTopLeft();
         float var19 = var13 == null ? 0.0F : var13.roundTopRight();
         float var20 = var13 == null ? 0.0F : var13.roundBottomRight();
         float var21 = var13 == null ? 0.0F : var13.roundBottomLeft();

         try {
            this.output
               .handle(
                  var1.instance.handle(),
                  var1.instance.resolve(),
                  var1.instance.update(),
                  var1.instance.process(),
                  var1.instance.compute(),
                  var2,
                  var3,
                  var4,
                  var5,
                  var6,
                  var7,
                  var8,
                  var9,
                  var10,
                  var11,
                  var12,
                  this.handler.update(),
                  var14,
                  var15,
                  var16,
                  var17,
                  var18,
                  var19,
                  var20,
                  var21
               );
            return true;
         } catch (RuntimeException var23) {
            this.current.process();
            return false;
         }
      } else {
         return false;
      }
   }
   public void process() {
      if (this.animator) {
         boolean var5 = false /* VF: Semaphore variable */;

         label66: {
            try {
               var5 = true;
               if (this.pointEncode != null) {
                  this.pointEncode.handle();
               }

               if (this.output != null) {
                  this.output.handle();
               }

               ShaderViewportTracker.handle().process();
               var5 = false;
               break label66;
            } catch (Exception var6) {
               System.err.println("Error in Renderer2D.end(): " + var6.getMessage());
               var6.printStackTrace();
               var5 = false;
            } finally {
               if (var5) {
                  this.animator = false;
                  this.source = 0;
                  this.target = 0;
                  this.pending = false;
                  this.previous = 0.0F;
                  this.latest = 0;
                  this.summary = 0;
                  this.matrixBlend = false;
                  this.vectorMatch = 0.0F;
                  this.itemProject = 0;
                  this.responseCompute = 0;
                  this.providerFetch = 0;
                  this.profileDraw = 0;
                  this.active.clear();
                  this.handler.handle();
                  this.drawAnimation();
                  this.encodePoint();
               }
            }

            this.animator = false;
            this.source = 0;
            this.target = 0;
            this.pending = false;
            this.previous = 0.0F;
            this.latest = 0;
            this.summary = 0;
            this.matrixBlend = false;
            this.vectorMatch = 0.0F;
            this.itemProject = 0;
            this.responseCompute = 0;
            this.providerFetch = 0;
            this.profileDraw = 0;
            this.active.clear();
            this.handler.handle();
            this.drawAnimation();
            this.encodePoint();
            return;
         }

         this.animator = false;
         this.source = 0;
         this.target = 0;
         this.pending = false;
         this.previous = 0.0F;
         this.latest = 0;
         this.summary = 0;
         this.matrixBlend = false;
         this.vectorMatch = 0.0F;
         this.itemProject = 0;
         this.responseCompute = 0;
         this.providerFetch = 0;
         this.profileDraw = 0;
         this.active.clear();
         this.handler.handle();
         this.drawAnimation();
         this.encodePoint();
      }
   }

   public void compute() {
      this.tick();
      this.pointEncode.handle();
   }

   public void resolve() {
      this.tick();
      this.compute();
      this.selection.push(true);
      this.output.handle(true);
   }

   public void update() {
      this.tick();
      if (this.selection.size() > 1) {
         this.compute();
         this.selection.pop();
         this.output.handle(this.selection.peek());
         this.output.compute();
      }
   }

   public void handle(int var1, int var2, int var3, int var4) {
      this.handle(var1, var2, var3, var4, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.tick();
      RoundedRectRenderer.PrimaryDataRecord var9 = RoundedRectRenderer.PrimaryDataRecord.fromRect(
         var1, var2, var3, var4, var5, var6, var7, var8, this.handler.update()
      );
      RoundedRectRenderer.PrimaryDataRecord var10 = this.active.isEmpty() ? var9 : RoundedRectRenderer.PrimaryDataRecord.intersect(this.active.peek(), var9);
      this.active.push(var10);
      this.handle(var10);
   }

   public void apply() {
      this.tick();
      if (!this.active.isEmpty()) {
         this.active.pop();
         if (this.active.isEmpty()) {
            this.output.process(false);
         } else {
            this.handle(this.active.peek());
         }
      }
   }

   private void handle(RoundedRectRenderer.PrimaryDataRecord var1) {
      if (var1 == null) {
         this.output.process(false);
      } else {
         this.output.process(true);
         this.output.handle(var1.x(), var1.y(), var1.w(), var1.h(), var1.roundTopLeft(), var1.roundTopRight(), var1.roundBottomRight(), var1.roundBottomLeft());
      }
   }

   public void handle(float var1, float var2, float var3, float var4, int var5, float var6) {
      this.tick();
      var1--;
      var2--;
      var3 += 2.0F;
      var4 += 2.0F;
      this.pointEncode.handle(var1, var2, var3, var4, 0.0F, 0.0F, 0.0F, 0.0F, this.process(var5), Math.max(1.0F, var6), this.handler.update());
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, int var6, float var7) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6, var7);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float var10) {
      this.tick();
      float[] var11 = this.compute(var3, var4, var5, var6, var7, var8);
      var1--;
      var2--;
      var3 += 2.0F;
      var4 += 2.0F;
      if (var11[0] > 0.0F) {
         var11[0]++;
      }

      if (var11[1] > 0.0F) {
         var11[1]++;
      }

      if (var11[2] > 0.0F) {
         var11[2]++;
      }

      if (var11[3] > 0.0F) {
         var11[3]++;
      }

      this.pointEncode.handle(var1, var2, var3, var4, var11[0], var11[1], var11[2], var11[3], this.process(var9), Math.max(1.0F, var10), this.handler.update());
   }

   public void handle(float var1, float var2, float var3, float var4, int var5, int var6, int var7, int var8) {
      this.tick();
      this.pointEncode
         .handle(
            var1,
            var2,
            var3,
            var4,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            this.process(var5),
            this.process(var6),
            this.process(var7),
            this.process(var8),
            this.handler.update()
         );
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8, var9);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10, int var11, int var12) {
      this.tick();
      float[] var13 = this.compute(var3, var4, var5, var6, var7, var8);
      this.pointEncode
         .handle(
            var1,
            var2,
            var3,
            var4,
            var13[0],
            var13[1],
            var13[2],
            var13[3],
            this.process(var9),
            this.process(var10),
            this.process(var11),
            this.process(var12),
            this.handler.update()
         );
   }

   public void handle(float var1, float var2, float var3, float var4, int var5, int var6) {
      this.handle(var1, var2, var3, var4, var5, var6, var6, var5);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, int var6, int var7) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var7, var6);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var10, var9);
   }

   public void process(float var1, float var2, float var3, float var4, int var5, int var6) {
      this.handle(var1, var2, var3, var4, var5, var5, var6, var6);
   }

   public void process(float var1, float var2, float var3, float var4, float var5, int var6, int var7) {
      this.handle(var1, var2, var3, var4, var5, var6, var6, var7, var7);
   }

   public void process(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var9, var10, var10);
   }

   public void process(float var1, float var2, float var3, float var4, float var5, int var6) {
      this.tick();
      this.pointEncode.handle(var1, var2, var3, var4, var5, this.process(var6), this.handler.update());
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      this.tick();
      this.pointEncode.handle(var1, var2, var3, var4, var5, var6, this.process(var7), this.handler.update());
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11) {
      this.tick();
      if (!(var3 <= 0.0F) && !(var4 <= 0.0F)) {
         if (!GlCompatibilityProbe.process()) {
            boolean var12 = true;

            try {
               var12 = data == null || data.getAsBoolean();
            } catch (Throwable var16) {
            }

            float var13 = Math.max(0.0F, var9);
            if (!var12 && var13 > 6.0F) {
               var13 = Math.min(var13, 6.0F);
            }

            float var14 = Math.max(0.0F, var10);
            if (!(var13 <= 0.0F) || !(var14 <= 0.0F)) {
               float[] var15 = resolve(var5, var6, var7, var8);
               handle(var3, var4, var15);
               this.compute();
               this.output.handle(var1, var2, var3, var4, var15[0], var15[1], var15[2], var15[3], var13, var14, this.process(var11), this.handler.update());
            }
         }
      }
   }

   public void handle(float var1, float var2, float var3, float var4, float var5) {
      this.handle(var1, var2, var3, var4, var5, 1.0F);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      this.tick();
      if (!GlCompatibilityProbe.process()) {
         if (this.pending) {
            float var10 = apply(var9) * this.animate();
            if (!(var10 <= 1.0E-4F)) {
               float[] var11 = resolve(var5, var6, var7, var8);
               handle(var3, var4, var11);
               this.compute();
               this.output.handle(var1, var2, var3, var4, var11[0], var11[1], var11[2], var11[3], var10, this.handler.update());
            }
         }
      }
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, int var6, float var7, int var8) {
      this.tick();
      float var9 = this.animate();
      if (!(var9 <= 1.0E-4F)) {
         if (!GlCompatibilityProbe.process() && this.pending) {
            float[] var10 = this.compute(var3, var4, var5, var5, var5, var5);
            this.compute();
            if (!this.output.handle(var1, var2, var3, var4, var10[0], var10[1], var10[2], var10[3], var6, apply(var7), var8, var9, this.handler.update())) {
               this.handle(var1, var2, var3, var4, var5, var6, var8, var9);
            }
         } else {
            this.handle(var1, var2, var3, var4, var5, var6, var8, var9);
         }
      }
   }

   private void handle(float var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8) {
      float[] var9 = this.compute(var3, var4, var5, var5, var5, var5);
      int var10 = AlphaBlendMath.handle(AlphaBlendMath.handle(var6, var7), var8);
      this.pointEncode.handle(var1, var2, var3, var4, var9[0], var9[1], var9[2], var9[3], var10, this.handler.update());
   }

   public void process(float var1, float var2, float var3, float var4, float var5) {
      this.process(var1, var2, var3, var4, var5, 1.0F);
   }

   public void process(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.tick();
      if (!GlCompatibilityProbe.process()) {
         if (this.matrixBlend) {
            float var7 = apply(var6) * this.animate();
            if (!(var7 <= 1.0E-4F)) {
               this.compute();
               this.output
                  .handle(
                     var1,
                     var2,
                     var3,
                     var4,
                     Math.max(0.0F, var5),
                     var7,
                     this.handler.update(),
                     this.itemProject,
                     this.responseCompute,
                     this.providerFetch,
                     this.profileDraw
                  );
            }
         }
      }
   }

   public void handle(float var1) {
      this.tick();
      if (GlCompatibilityProbe.process()) {
         this.pending = false;
         this.latest = 0;
         this.summary = 0;
      } else {
         try {
            if (instance != null && !instance.getAsBoolean()) {
               this.pending = false;
               this.latest = 0;
               this.summary = 0;
               return;
            }
         } catch (Throwable var6) {
         }

         int var2 = this.source;
         int var3 = this.target;
         if (var2 > 0 && var3 > 0) {
            float var4 = Math.max(0.5F, var1);
            boolean var5 = this.pending && this.latest == var2 && this.summary == var3 && Math.abs(this.previous - var4) <= 0.05F;
            if (!var5) {
               this.compute();
               this.pending = this.output.handle(var2, var3, var4);
               this.previous = var4;
               this.latest = this.pending ? var2 : 0;
               this.summary = this.pending ? var3 : 0;
            }
         } else {
            this.pending = false;
            this.latest = 0;
            this.summary = 0;
         }
      }
   }

   public static void handle(boolean var0) {
      if (var0) {
         GlStateManager._enableBlend();
         GL11.glBlendFunc(770, 771);
         GlStateManager._disableCull();
         GlStateManager._blendFuncSeparate(770, 771, 1, 0);
         GlStateManager._colorMask(true, true, true, true);
      } else {
         GlStateManager._colorMask(true, true, true, true);
         GlStateManager._enableBlend();
      }
   }

   public static void process(boolean var0) {
      if (var0) {
         GlStateManager._colorMask(true, true, true, true);
         GlStateManager._blendFuncSeparate(770, 771, 1, 0);
         GlStateManager._enableCull();
         GlStateManager._disableBlend();
      } else {
         GlStateManager._colorMask(true, true, true, true);
         GlStateManager._enableBlend();
      }
   }

   public void compute(float var1, float var2, float var3, float var4, float var5) {
      this.tick();
      if (GlCompatibilityProbe.process()) {
         this.matrixBlend = false;
         this.providerFetch = 0;
         this.profileDraw = 0;
      } else if (this.source > 0 && this.target > 0 && !(var3 <= 0.0F) && !(var4 <= 0.0F)) {
         float[] var6 = this.handler.update();
         RoundedRectRenderer.DataRecord var7 = handle(var6, var1, var2, var3, var4);
         int var8 = handle(var7.minX, this.source);
         int var9 = handle(var7.minY, this.target);
         int var10 = process(var7.maxX, this.source);
         int var11 = process(var7.maxY, this.target);
         int var12 = Math.max(0, var10 - var8);
         int var13 = Math.max(0, var11 - var9);
         if (var12 > 0 && var13 > 0) {
            float var14 = Math.max(0.5F, var5);
            boolean var15 = this.matrixBlend
               && this.itemProject == var8
               && this.responseCompute == var9
               && this.providerFetch == var12
               && this.profileDraw == var13
               && Math.abs(this.vectorMatch - var14) <= 0.05F;
            if (!var15) {
               this.compute();
               boolean var16 = this.output.handle(var8, var9, var12, var13, var14);
               this.matrixBlend = var16;
               if (var16) {
                  this.vectorMatch = var14;
                  this.itemProject = var8;
                  this.responseCompute = var9;
                  this.providerFetch = var12;
                  this.profileDraw = var13;
               } else {
                  this.vectorMatch = 0.0F;
                  this.providerFetch = 0;
                  this.profileDraw = 0;
               }
            }
         } else {
            this.matrixBlend = false;
            this.providerFetch = 0;
            this.profileDraw = 0;
         }
      } else {
         this.matrixBlend = false;
         this.providerFetch = 0;
         this.profileDraw = 0;
      }
   }

   private static int handle(float var0, int var1) {
      int var2 = (int)Math.floor(var0);
      if (var2 < 0) {
         return 0;
      } else {
         return var2 > var1 ? var1 : var2;
      }
   }

   private static int process(float var0, int var1) {
      int var2 = (int)Math.ceil(var0);
      if (var2 < 0) {
         return 0;
      } else {
         return var2 > var1 ? var1 : var2;
      }
   }

   private static RoundedRectRenderer.DataRecord handle(float[] var0, float var1, float var2, float var3, float var4) {
      float var5 = var1;
      float var6 = var2;
      float var7 = var1 + var3;
      float var8 = var2 + var4;
      float var9 = handle(var0, var5, var6);
      float var10 = process(var0, var5, var6);
      float var11 = handle(var0, var7, var6);
      float var12 = process(var0, var7, var6);
      float var13 = handle(var0, var7, var8);
      float var14 = process(var0, var7, var8);
      float var15 = handle(var0, var5, var8);
      float var16 = process(var0, var5, var8);
      float var17 = Math.min(Math.min(var9, var11), Math.min(var13, var15));
      float var18 = Math.max(Math.max(var9, var11), Math.max(var13, var15));
      float var19 = Math.min(Math.min(var10, var12), Math.min(var14, var16));
      float var20 = Math.max(Math.max(var10, var12), Math.max(var14, var16));
      return new RoundedRectRenderer.DataRecord(var17, var19, var18, var20);
   }

   private static float handle(float[] var0, float var1, float var2) {
      return var0 != null && var0.length >= 6 ? var0[0] * var1 + var0[1] * var2 + var0[2] : var1;
   }

   private static float process(float[] var0, float var1, float var2) {
      return var0 != null && var0.length >= 6 ? var0[3] * var1 + var0[4] * var2 + var0[5] : var2;
   }

   public void handle(float[] var1) {
      this.tick();
      this.handler.handle();
      this.handler.process(var1);
   }

   public void process(float[] var1) {
      this.tick();
      this.handler.handle(var1);
   }

   public void process(float var1) {
      this.tick();
      this.handler.handle(var1);
   }

   public void execute() {
      this.tick();
      this.handler.resolve();
   }

   public void handle(float var1, float var2) {
      this.tick();
      this.handler.handle(var1, var2);
   }

   public void prepare() {
      this.tick();
      this.handler.resolve();
   }

   public void compute(float var1) {
      this.process(var1, var1);
   }

   public void process(float var1, float var2) {
      this.tick();
      this.handler.handle(var1, var2, 0.0F, 0.0F);
   }

   public void resolve(float var1) {
      this.compute(var1, var1);
   }

   public void compute(float var1, float var2) {
      this.tick();
      if (this.source > 0 && this.target > 0) {
         this.handler.handle(var1, var2, this.source * 0.5F, this.target * 0.5F);
      } else {
         throw new IllegalStateException("Cannot compute frame center before begin(width, height) is called with positive dimensions");
      }
   }

   public void handle(float var1, float var2, float var3) {
      this.compute(var1, var1, var2, var3);
   }

   public void compute(float var1, float var2, float var3, float var4) {
      this.tick();
      this.handler.handle(var1, var2, var3, var4);
   }

   public void check() {
      this.tick();
      this.handler.resolve();
   }

   public void update(float var1) {
      this.tick();
      float var2 = this.animate();
      float var3 = apply(var1);
      this.mode.push(var2 * var3);
   }

   public void onTick() {
      this.tick();
      if (this.mode.size() > 1) {
         this.mode.pop();
      }
   }

   public static SdfTextRenderer handle(FontObject var0) {
      return var0 == null ? null : animationDraw.get(var0.instance);
   }

   public void handle(String var1, SdfTextRenderer var2) {
      if (var2 != null) {
         animationDraw.put(var1, var2);
      }
   }

   public void handle(FontObject var1, SdfTextRenderer var2) {
      if (var2 != null) {
         animationDraw.put(var1.instance, var2);
      }
   }

   public AffineTransformStack select() {
      return this.handler;
   }

   public float[] refresh() {
      this.tick();
      if (this.enabled.isEmpty()) {
         return this.handler.update();
      }

      RoundedRectRenderer.PrimaryColorState var1 = this.enabled.peek();
      float[] var2 = this.handler.update();
      float[] var3 = new float[]{var2[0], var2[1], var2[2] + var1.animationDraw, var2[3], var2[4], var2[5] + var1.pointEncode, var2[6], var2[7], var2[8]};
      return handle(var1.handler, var3);
   }

   public float render() {
      return this.animate();
   }

   public void handle(FontObject var1, float var2, float var3, float var4, String var5, int var6) {
      this.tick();
      if (var1 == null) {
         throw new IllegalArgumentException("FontObject must not be null");
      }

      if (!(var4 <= 0.0F)) {
         SdfTextRenderer var7 = animationDraw.get(var1.instance);
         if (var7 != null) {
            var7.handle(var2, var3, var4 / 2.0F, var5, this.process(var6), this.handler.update());
         }
      }
   }

   public void handle(FontObject var1, float var2, float var3, float var4, String var5, int var6, String var7) {
      this.tick();
      if (var1 == null) {
         throw new IllegalArgumentException("FontObject must not be null");
      }

      if (!(var4 <= 0.0F)) {
         SdfTextRenderer var8 = animationDraw.get(var1.instance);
         if (var8 != null) {
            var8.handle(var2, var3, var4 / 2.0F, var5, this.process(var6), var7, this.handler.update());
         }
      }
   }

   public void handle(FontObject var1, float var2, float var3, float var4, String var5, int var6, int var7, float var8) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, "l");
   }

   public void handle(FontObject var1, float var2, float var3, float var4, String var5, int var6, int var7, float var8, String var9) {
      this.tick();
      if (var1 == null) {
         throw new IllegalArgumentException("FontObject must not be null");
      }

      if (!(var4 <= 0.0F)) {
         SdfTextRenderer var10 = animationDraw.get(var1.instance);
         if (var10 != null) {
            var10.handle(var2, var3, var4 / 2.0F, var5, this.process(var6), this.process(var7), var8, var9, this.handler.update());
         }
      }
   }

   public static SdfTextRenderer.State handle(FontObject var0, String var1, float var2) {
      if (var0 == null) {
         throw new IllegalArgumentException("FontObject must not be null");
      }

      if (var2 <= 0.0F) {
         return new SdfTextRenderer.State(0.0F, 0.0F);
      }

      SdfTextRenderer var3 = animationDraw.get(var0.instance);
      if (var3 == null) {
         return new SdfTextRenderer.State(0.0F, 0.0F);
      }

      String var4 = var1 == null ? "" : var1;
      return var3.resolve(var4, var2 / 2.0F);
   }

   private void drawAnimation() {
      this.mode.clear();
      this.mode.push(cache);
   }

   private void encodePoint() {
      this.selection.clear();
      this.selection.push(false);
      if (this.output != null) {
         this.output.handle(false);
      }
   }

   private float animate() {
      return this.mode.isEmpty() ? 1.0F : this.mode.peek();
   }

   private int process(int var1) {
      float var2 = this.animate();
      if (var2 >= 0.999F) {
         return var1;
      }

      int var3 = var1 >>> 24 & 0xFF;
      int var4 = var1 >>> 16 & 0xFF;
      int var5 = var1 >>> 8 & 0xFF;
      int var6 = var1 & 0xFF;
      int var7 = handle(var3, var2);
      int var8 = handle(var4, var2);
      int var9 = handle(var5, var2);
      int var10 = handle(var6, var2);
      return var7 << 24 | var8 << 16 | var9 << 8 | var10;
   }

   private static int handle(int var0, float var1) {
      float var2 = var0 * var1;
      if (var2 <= 0.0F) {
         return 0;
      } else {
         return var2 >= 255.0F ? 255 : Math.round(var2);
      }
   }

   private static float apply(float var0) {
      if (var0 < 0.0F) {
         return 0.0F;
      } else {
         return var0 > 1.0F ? 1.0F : var0;
      }
   }

   static float[] resolve(float var0, float var1, float var2, float var3) {
      float[] var4 = vectorPerform.get();
      var4[0] = var0;
      var4[1] = var1;
      var4[2] = var2;
      var4[3] = var3;
      return var4;
   }

   static void handle(float var0, float var1, float[] var2) {
      if (var2 != null && var2.length >= 4) {
         float var3 = Math.abs(var0);
         float var4 = Math.abs(var1);

         for (int var5 = 0; var5 < 4; var5++) {
            float var6 = var2[var5];
            if (!Float.isFinite(var6)) {
               var6 = 0.0F;
            }

            var2[var5] = Math.max(0.0F, var6);
         }

         if (!(var3 <= 0.0F) && !(var4 <= 0.0F)) {
            float var7 = Math.min(var3, var4) * 0.5F;

            for (int var8 = 0; var8 < 4; var8++) {
               var2[var8] = Math.min(var2[var8], var7);
            }
         } else {
            Arrays.fill(var2, 0.0F);
         }
      } else {
         throw new IllegalArgumentException("radii");
      }
   }

   private static boolean resolve(float var0, float var1) {
      return Math.abs(var0 - var1) <= 1.0E-4F;
   }

   private static boolean execute(float var0) {
      return Math.abs(var0) <= 1.0E-4F;
   }

   static boolean compute(float[] var0) {
      return var0 != null && var0.length >= 9
         ? resolve(var0[0], 1.0F)
            && execute(var0[1])
            && execute(var0[2])
            && execute(var0[3])
            && resolve(var0[4], 1.0F)
            && execute(var0[5])
            && execute(var0[6])
            && execute(var0[7])
            && resolve(var0[8], 1.0F)
         : true;
   }

   static boolean resolve(float[] var0) {
      return var0 != null && var0.length >= 9 ? execute(var0[1]) && execute(var0[3]) && execute(var0[6]) && execute(var0[7]) && resolve(var0[8], 1.0F) : true;
   }

   static float compute(float[] var0, float var1, float var2) {
      return var0 != null && var0.length >= 9 ? var0[0] * var1 + var0[1] * var2 + var0[2] : var1;
   }

   static float resolve(float[] var0, float var1, float var2) {
      return var0 != null && var0.length >= 9 ? var0[3] * var1 + var0[4] * var2 + var0[5] : var2;
   }

   static float update(float[] var0) {
      if (var0 != null && var0.length >= 9) {
         float var1 = Math.abs(var0[0]);
         float var2 = Math.abs(var0[4]);
         float var3 = Math.min(var1, var2);
         return var3 <= 1.0E-4F ? 0.0F : var3;
      } else {
         return 1.0F;
      }
   }

   private static float[] handle(float[] var0, float[] var1) {
      return new float[]{
         var0[0] * var1[0] + var0[1] * var1[3] + var0[2] * var1[6],
         var0[0] * var1[1] + var0[1] * var1[4] + var0[2] * var1[7],
         var0[0] * var1[2] + var0[1] * var1[5] + var0[2] * var1[8],
         var0[3] * var1[0] + var0[4] * var1[3] + var0[5] * var1[6],
         var0[3] * var1[1] + var0[4] * var1[4] + var0[5] * var1[7],
         var0[3] * var1[2] + var0[4] * var1[5] + var0[5] * var1[8],
         var0[6] * var1[0] + var0[7] * var1[3] + var0[8] * var1[6],
         var0[6] * var1[1] + var0[7] * var1[4] + var0[8] * var1[7],
         var0[6] * var1[2] + var0[7] * var1[5] + var0[8] * var1[8]
      };
   }

   public static void handle(MatrixStack var0, float var1, float var2, float var3) {
      handle(var0, (double)var1, (double)var2, (double)var3);
   }

   public static void handle(MatrixStack var0, double var1, double var3, double var5) {
      Vec3d var7 = context.getEntityRenderDispatcher().camera.getPos();
      var0.translate(var1 - var7.x, var3 - var7.y, var5 - var7.z);
   }

   public static Vector2d handle(double var0, double var2, double var4) {
      Camera var6 = context.getEntityRenderDispatcher().camera;
      if (var6 == null) {
         return new Vector2d(0.0, 0.0);
      }

      Vec3d var7 = var6.getPos();
      Quaternionf var8 = new Quaternionf(var6.getRotation());
      var8.conjugate();
      Vector3f var9 = new Vector3f((float)(var7.x - var0), (float)(var7.y - var2), (float)(var7.z - var4));
      var9.rotate(var8);
      float var10 = context.getRenderTickCounter().getDynamicDeltaTicks();
      if ((Boolean)context.options.getBobView().getValue() && context.getCameraEntity() instanceof PlayerEntity var12) {
         float var13 = var12.strideDistance;
         float var14 = var13 - var12.lastStrideDistance;
         float var15 = -(var13 + var14 * var10);
         float var16 = var6.getYaw();
         float var17 = Math.abs(MathHelper.cos(var15 * (float) Math.PI - 0.2F) * var16) * 5.0F;
         Quaternionf var18 = new Quaternionf().rotateAxis((float)Math.toRadians(var17), new Vector3f(1.0F, 0.0F, 0.0F));
         var18.conjugate();
         var9.rotate(var18);
         float var19 = MathHelper.sin(var15 * (float) Math.PI) * var16 * 3.0F;
         Quaternionf var20 = new Quaternionf().rotateAxis((float)Math.toRadians(var19), new Vector3f(0.0F, 0.0F, 1.0F));
         var20.conjugate();
         var9.rotate(var20);
         Vector3f var21 = new Vector3f(MathHelper.sin(var15 * (float) Math.PI) * var16 * 0.5F, -Math.abs(MathHelper.cos(var15 * (float) Math.PI) * var16), 0.0F);
         var21.y = -var21.y;
         var9.add(var21);
      }

      double var22 = ((GameRendererAccessor)context.gameRenderer).invokeGetFov(var6, var10, true);
      float var23 = context.getWindow().getScaledHeight() / 2.0F;
      float var24 = var23 / (var9.z() * (float)Math.tan(Math.toRadians(var22 / 2.0)));
      return var9.z() < 0.0F
         ? new Vector2d(-var9.x() * var24 + context.getWindow().getScaledWidth() / 2, context.getWindow().getScaledHeight() / 2 - var9.y() * var24)
         : null;
   }

   public static class ColorState {
      public static float handle(int var0) {
         return (var0 >> 16 & 0xFF) / 255.0F;
      }

      public static float process(int var0) {
         return (var0 >> 8 & 0xFF) / 255.0F;
      }

      public static float compute(int var0) {
         return (var0 & 0xFF) / 255.0F;
      }

      public static float resolve(int var0) {
         return (var0 >> 24 & 0xFF) / 255.0F;
      }

      public static Color handle(Color var0, int var1) {
         return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), var1);
      }

      public static Color handle(Color var0, Color var1, double var2) {
         float var4 = NumericTransform.onTick((float)Math.sin((Math.PI * 6) * (var2 / 4.0 % 1.0)) / 2.0F + 0.5F, 0.0F, 1.0F);
         return new Color(PackedColor.compute(var0.getRGB(), var1.getRGB(), var4), true);
      }

      public static Color process(Color var0, int var1) {
         return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), var1);
      }

      public static int handle(int var0, int var1) {
         return var0 & 16777215 | var1 << 24;
      }

      public static int handle() {
         return apply(10, 255);
      }

      private static ThemePalette process() {
         if (WildClient.instance != null && WildClient.instance.selection != null) {
            return WildClient.instance.selection.process();
         } else {
            return BlurStateManager.sourceBuild != null ? BlurStateManager.sourceBuild : ThemePalette.WILD;
         }
      }

      private static ThemePalette compute() {
         return BlurStateManager.outputCollapse != null ? BlurStateManager.outputCollapse : process();
      }

      public static int[] process(int var0, int var1) {
         ThemePalette var2 = process();
         ThemePalette var3 = compute();
         return new int[]{
            process(handle(var0, 0, new int[]{handle(var2.handle().getRGB(), var3.handle().getRGB(), (double)(1.0F - BlurStateManager.state.check()))}), (float)var1),
            process(handle(var0, 90, new int[]{handle(var2.handle().getRGB(), var3.handle().getRGB(), (double)(1.0F - BlurStateManager.state.check()))}), (float)var1),
            process(handle(var0, 180, new int[]{handle(var2.handle().getRGB(), var3.handle().getRGB(), (double)(1.0F - BlurStateManager.state.check()))}), (float)var1),
            process(handle(var0, 270, new int[]{handle(var2.handle().getRGB(), var3.handle().getRGB(), (double)(1.0F - BlurStateManager.state.check()))}), (float)var1)
         };
      }

      public static int compute(int var0, int var1) {
         ThemePalette var2 = process();
         ThemePalette var3 = compute();
         return handle(
            handle(var2.process().getRGB(), var3.process().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            handle(var2.process().getRGB(), var3.process().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            var0,
            var1
         );
      }

      public static int resolve(int var0, int var1) {
         ThemePalette var2 = process();
         ThemePalette var3 = compute();
         return handle(
            handle(var2.compute().getRGB(), var3.compute().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            handle(var2.compute().getRGB(), var3.compute().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            var0,
            var1
         );
      }

      public static int update(int var0, int var1) {
         ThemePalette var2 = process();
         ThemePalette var3 = compute();
         return handle(
            handle(var2.resolve().getRGB(), var3.resolve().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            handle(var2.resolve().getRGB(), var3.resolve().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            var0,
            var1
         );
      }

      public static int apply(int var0, int var1) {
         ThemePalette var2 = process();
         ThemePalette var3 = compute();
         return handle(
            handle(var2.handle().getRGB(), var3.handle().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            handle(var2.handle().getRGB(), var3.handle().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            var0,
            var1
         );
      }

      public static int execute(int var0, int var1) {
         ThemePalette var2 = process();
         ThemePalette var3 = compute();
         return handle(
            handle(var2.update().getRGB(), var3.update().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            handle(var2.update().getRGB(), var3.update().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            var0,
            var1
         );
      }

      public static int prepare(int var0, int var1) {
         ThemePalette var2 = process();
         ThemePalette var3 = compute();
         return handle(
            handle(var2.apply().getRGB(), var3.apply().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            handle(var2.apply().getRGB(), var3.apply().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            var0,
            var1
         );
      }

      public Color process(Color var1, Color var2, double var3) {
         var3 = 1.0 - var3;
         return new Color(PackedColor.process(var1.getRGB(), var2.getRGB(), var3), true);
      }

      public static Color handle(int var0, int var1, Color var2, Color var3, boolean var4) {
         int var5 = 0;
         if (var0 == 0) {
            var5 = var1 % 360;
         } else {
            var5 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
         }

         var5 = (var5 >= 180 ? 360 - var5 : var5) * 2;
         boolean var6 = var4;
         return var6 ? handle(var2, var3, var5 / 360.0F) : process(var2, var3, var5 / 360.0F);
      }

      public static Color handle(Color var0, Color var1, float var2) {
         var2 = Math.min(1.0F, Math.max(0.0F, var2));
         float[] var3 = Color.RGBtoHSB(var0.getRed(), var0.getGreen(), var0.getBlue(), null);
         float[] var4 = Color.RGBtoHSB(var1.getRed(), var1.getGreen(), var1.getBlue(), null);
         Color var5 = Color.getHSBColor(handle(var3[0], var4[0], var2), handle(var3[1], var4[1], var2), handle(var3[2], var4[2], var2));
         return new Color(var5.getRed(), var5.getGreen(), var5.getBlue(), (int)handle((float)var0.getAlpha(), (float)var1.getAlpha(), var2));
      }

      public static Color process(Color var0, Color var1, float var2) {
         return new Color(PackedColor.compute(var0.getRGB(), var1.getRGB(), var2), true);
      }

      private static float handle(float var0, float var1, float var2) {
         float var3 = Math.max(0.0F, Math.min(1.0F, var2));
         return var0 + (var1 - var0) * var3;
      }

      public static int handle(int var0, int var1, int var2, int var3) {
         double var4 = (System.currentTimeMillis() / var2 + var3) % 360L;
         double var7;
         float var6 = (float)((var7 = var4 % 360.0) / 360.0);
         return PackedColor.resolve(var0, var1, var6);
      }

      public static int handle(int var0, float var1) {
         int var2 = var0 >> 16 & 0xFF;
         int var3 = var0 >> 8 & 0xFF;
         int var4 = var0 & 0xFF;
         int var5 = var0 >> 24 & 0xFF;
         float[] var6 = Color.RGBtoHSB(var2, var3, var4, null);
         float var7 = Math.max(0.0F, Math.min(1.0F, var6[2] * var1));
         int var8 = Color.HSBtoRGB(var6[0], var6[1], var7);
         return var8 & 16777215 | var5 << 24;
      }

      public static int handle(int var0, int var1, double var2) {
         return PackedColor.process(var0, var1, var2);
      }

      public static int[] update(int var0) {
         int[] var1 = new int[4];
         if (var0 == 0) {
            var0 = 1;
         }

         var1[0] = handle(var0, 1, 1.0F, 1.0F, 1.0F);
         var1[1] = handle(var0, 90, 1.0F, 1.0F, 1.0F);
         var1[2] = handle(var0, 180, 1.0F, 1.0F, 1.0F);
         var1[3] = handle(var0, 270, 1.0F, 1.0F, 1.0F);
         return var1;
      }

      public static int handle(int var0, int var1, float var2, float var3, float var4) {
         int var5 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
         float var6 = var5 / 360.0F;
         int var7 = Color.HSBtoRGB(var6, var2, var3);
         return compute(render(var7), tick(var7), drawAnimation(var7), Math.max(0, Math.min(255, (int)(var4 * 255.0F))));
      }

      public static int handle(int var0, int var1, int... var2) {
         int var3 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
         var3 = (var3 > 180 ? 360 - var3 : var3) + 180;
         int var4 = (int)(var3 / 360.0F * var2.length);
         if (var4 == var2.length) {
            var4--;
         }

         int var5 = var2[var4];
         int var6 = var2[var4 == var2.length - 1 ? 0 : var4 + 1];
         return process(var5, var6, var3 / 360.0F * var2.length - var4);
      }

      public static int process(int var0, int var1, double var2) {
         return PackedColor.process(var0, var1, var2);
      }

      public static float[] apply(int var0) {
         return new float[]{render(var0) / 255.0F, tick(var0) / 255.0F, drawAnimation(var0) / 255.0F, encodePoint(var0) / 255.0F};
      }

      public static int check(int var0, int var1) {
         double var2 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
         double var4;
         return Color.getHSBColor((var4 = var2 % 360.0) / 360.0 < 0.5 ? -((float)(var4 / 360.0)) : (float)(var4 / 360.0), 0.5F, 1.0F).hashCode();
      }

      public static int[] execute(int var0) {
         int[] var1 = new int[4];
         if (var0 == 0) {
            boolean var2 = true;
         }

         var1[0] = check(25, 1);
         var1[1] = check(25, 90);
         var1[2] = check(25, 180);
         var1[3] = check(25, 270);
         return var1;
      }

      public static int process(int var0, float var1) {
         return process(prepare(var0), check(var0), onTick(var0), (int)(select(var0) * var1 / 255.0F));
      }

      public static int process(int var0, int var1, int var2, int var3) {
         return var3 << 24 | var0 << 16 | var1 << 8 | var2;
      }

      public static int prepare(int var0) {
         return var0 >> 16 & 0xFF;
      }

      public static int check(int var0) {
         return var0 >> 8 & 0xFF;
      }

      public static int onTick(int var0) {
         return var0 & 0xFF;
      }

      public static int select(int var0) {
         return var0 >> 24 & 0xFF;
      }

      public static float[] handle(Color var0) {
         return new float[]{var0.getRed() / 255.0F, var0.getGreen() / 255.0F, var0.getBlue() / 255.0F, var0.getAlpha() / 255.0F};
      }

      public static int onTick(int var0, int var1) {
         return handle(
            handle(BlurStateManager.sourceBuild.handle().getRGB(), BlurStateManager.outputCollapse.handle().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            handle(BlurStateManager.sourceBuild.handle().getRGB(), BlurStateManager.outputCollapse.handle().getRGB(), (double)(1.0F - BlurStateManager.state.check())),
            var0,
            var1
         );
      }

      public static int compute(int var0, float var1) {
         int var2 = var0 >> 16 & 0xFF;
         int var3 = var0 >> 8 & 0xFF;
         int var4 = var0 & 0xFF;
         return compute(var2, var3, var4, (int)var1);
      }

      public static Color refresh(int var0) {
         int var1 = var0 >> 16 & 0xFF;
         int var2 = var0 >> 8 & 0xFF;
         int var3 = var0 & 0xFF;
         int var4 = var0 >> 24 & 0xFF;
         return new Color(var1, var2, var3, var4);
      }

      public static int select(int var0, int var1) {
         return compute(render(var0), tick(var0), drawAnimation(var0), var1);
      }

      public static int resolve(int var0, float var1) {
         return handle(render(var0) * var1, tick(var0) * var1, drawAnimation(var0) * var1, encodePoint(var0));
      }

      public static int render(int var0) {
         return var0 >> 16 & 0xFF;
      }

      public static int tick(int var0) {
         return var0 >> 8 & 0xFF;
      }

      public static int drawAnimation(int var0) {
         return var0 & 0xFF;
      }

      public static int encodePoint(int var0) {
         return var0 >> 24 & 0xFF;
      }

      public static int handle(float var0, float var1, float var2, float var3) {
         return compute(
            Math.max(0, Math.min(255, Math.round(var0))),
            Math.max(0, Math.min(255, Math.round(var1))),
            Math.max(0, Math.min(255, Math.round(var2))),
            Math.max(0, Math.min(255, Math.round(var3)))
         );
      }

      public static int handle(int var0, int var1, int var2) {
         return compute(var0, var1, var2, 255);
      }

      public static int compute(int var0, int var1, int var2, int var3) {
         int var4 = 0;
         var4 |= var3 << 24;
         var4 |= var0 << 16;
         var4 |= var1 << 8;
         return var4 | var2;
      }

      public static int animate(int var0) {
         return var0 >> 16 & 0xFF;
      }

      public static int load(int var0) {
         return var0 >> 8 & 0xFF;
      }

      public static int save(int var0) {
         return var0 & 0xFF;
      }

      public static int submit(int var0) {
         return var0 >> 24 & 0xFF;
      }

      public static float[] unload(int var0) {
         return new float[]{(var0 >> 16 & 0xFF) / 255.0F, (var0 >> 8 & 0xFF) / 255.0F, (var0 & 0xFF) / 255.0F, (var0 >> 24 & 0xFF) / 255.0F};
      }

      public static int resolve(int var0, int var1, int var2, int var3) {
         return var3 << 24 | var0 << 16 | var1 << 8 | var2;
      }

      public static int process(Color var0) {
         int var1 = var0.getAlpha();
         int var2 = var0.getRed();
         int var3 = var0.getGreen();
         int var4 = var0.getBlue();
         return var1 << 24 | var2 << 16 | var3 << 8 | var4;
      }

      public static float[] fetch(int var0) {
         return new float[]{(var0 >> 16 & 0xFF) / 255.0F, (var0 >> 8 & 0xFF) / 255.0F, (var0 & 0xFF) / 255.0F, (var0 >> 24 & 0xFF) / 255.0F};
      }
   }

   record DataRecord(float minX, float minY, float maxX, float maxY) {
   }

   public static final class PrimaryColorState {
      ShaderRenderer.PrimaryColorState instance;
      int data;
      int context;
      boolean config;
      float state;
      int cache;
      int output;
      boolean current;
      float active;
      int mode;
      int selection;
      int enabled;
      int renderer;
      float[] handler;
      float animationDraw;
      float pointEncode;
      final float[] animator = new float[9];
      final ArrayDeque<float[]> source = new ArrayDeque<>();
      final ArrayDeque<RoundedRectRenderer.PrimaryDataRecord> target = new ArrayDeque<>();
      final ArrayDeque<Float> pending = new ArrayDeque<>();
      final ArrayDeque<Boolean> previous = new ArrayDeque<>();

      PrimaryColorState() {
      }
   }

   record PrimaryDataRecord(int x, int y, int w, int h, float roundTopLeft, float roundTopRight, float roundBottomRight, float roundBottomLeft) {
      private static RoundedRectRenderer.PrimaryDataRecord fromRect(
         float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7
      ) {
         return fromRect(var0, var1, var2, var3, var4, var5, var6, var7, null);
      }

      static RoundedRectRenderer.PrimaryDataRecord fromRect(
         float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float[] var8
      ) {
         if (Float.isFinite(var0) && Float.isFinite(var1) && Float.isFinite(var2) && Float.isFinite(var3)) {
            boolean var9 = var8 != null && var8.length >= 9 && !RoundedRectRenderer.compute(var8);
            float[] var10 = RoundedRectRenderer.resolve(var4, var5, var6, var7);
            RoundedRectRenderer.handle(Math.abs(var2), Math.abs(var3), var10);
            if (!var9) {
               float var27 = (float)Math.floor(Math.min(var0, var0 + var2));
               float var28 = (float)Math.floor(Math.min(var1, var1 + var3));
               float var29 = (float)Math.ceil(Math.max(var0, var0 + var2));
               float var30 = (float)Math.ceil(Math.max(var1, var1 + var3));
               int var31 = (int)var27;
               int var32 = (int)var28;
               int var34 = Math.max(0, (int)(var29 - var27));
               int var36 = Math.max(0, (int)(var30 - var28));
               return var34 > 0 && var36 > 0
                  ? new RoundedRectRenderer.PrimaryDataRecord(var31, var32, var34, var36, var10[0], var10[1], var10[2], var10[3])
                  : new RoundedRectRenderer.PrimaryDataRecord(var31, var32, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F);
            }

            float var11 = var0 + var2;
            float var12 = var1 + var3;
            float var13 = Float.POSITIVE_INFINITY;
            float var14 = Float.POSITIVE_INFINITY;
            float var15 = Float.NEGATIVE_INFINITY;
            float var16 = Float.NEGATIVE_INFINITY;

            for (int var17 = 0; var17 < 4; var17++) {
               float var35 = (var17 & 1) == 0 ? var0 : var11;
               float var37 = var17 < 2 ? var1 : var12;
               float var38 = RoundedRectRenderer.compute(var8, var35, var37);
               float var39 = RoundedRectRenderer.resolve(var8, var35, var37);
               if (!Float.isFinite(var38) || !Float.isFinite(var39)) {
                  return new RoundedRectRenderer.PrimaryDataRecord(0, 0, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F);
               }

               if (var38 < var13) {
                  var13 = var38;
               }

               if (var38 > var15) {
                  var15 = var38;
               }

               if (var39 < var14) {
                  var14 = var39;
               }

               if (var39 > var16) {
                  var16 = var39;
               }
            }

            float var33 = (float)Math.floor(Math.min(var13, var15));
            float var18 = (float)Math.floor(Math.min(var14, var16));
            float var19 = (float)Math.ceil(Math.max(var13, var15));
            float var20 = (float)Math.ceil(Math.max(var14, var16));
            int var21 = (int)var33;
            int var22 = (int)var18;
            int var23 = Math.max(0, (int)(var19 - var33));
            int var24 = Math.max(0, (int)(var20 - var18));
            if (var23 > 0 && var24 > 0) {
               if (RoundedRectRenderer.resolve(var8)) {
                  float var25 = RoundedRectRenderer.update(var8);
                  if (var25 > 0.0F) {
                     for (int var26 = 0; var26 < var10.length; var26++) {
                        var10[var26] *= var25;
                     }
                  } else {
                     Arrays.fill(var10, 0.0F);
                  }
               } else {
                  Arrays.fill(var10, 0.0F);
               }

               RoundedRectRenderer.handle(Math.abs(var19 - var33), Math.abs(var20 - var18), var10);
               return new RoundedRectRenderer.PrimaryDataRecord(var21, var22, var23, var24, var10[0], var10[1], var10[2], var10[3]);
            } else {
               return new RoundedRectRenderer.PrimaryDataRecord(var21, var22, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F);
            }
         } else {
            return new RoundedRectRenderer.PrimaryDataRecord(0, 0, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F);
         }
      }

      static RoundedRectRenderer.PrimaryDataRecord intersect(RoundedRectRenderer.PrimaryDataRecord var0, RoundedRectRenderer.PrimaryDataRecord var1) {
         if (var0 == null) {
            return var1;
         } else if (var1 == null) {
            return var0;
         } else {
            int var2 = Math.max(var0.x, var1.x);
            int var3 = Math.max(var0.y, var1.y);
            int var4 = Math.min(var0.x + var0.w, var1.x + var1.w);
            int var5 = Math.min(var0.y + var0.h, var1.y + var1.h);
            int var6 = Math.max(0, var4 - var2);
            int var7 = Math.max(0, var5 - var3);
            if (var6 <= 0 || var7 <= 0) {
               return new RoundedRectRenderer.PrimaryDataRecord(var2, var3, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F);
            } else if (matchesRect(var2, var3, var6, var7, var1)) {
               return new RoundedRectRenderer.PrimaryDataRecord(
                  var2, var3, var6, var7, var1.roundTopLeft, var1.roundTopRight, var1.roundBottomRight, var1.roundBottomLeft
               );
            } else {
               return matchesRect(var2, var3, var6, var7, var0)
                  ? new RoundedRectRenderer.PrimaryDataRecord(
                     var2, var3, var6, var7, var0.roundTopLeft, var0.roundTopRight, var0.roundBottomRight, var0.roundBottomLeft
                  )
                  : new RoundedRectRenderer.PrimaryDataRecord(var2, var3, var6, var7, 0.0F, 0.0F, 0.0F, 0.0F);
            }
         }
      }

      private static boolean matchesRect(int var0, int var1, int var2, int var3, RoundedRectRenderer.PrimaryDataRecord var4) {
         return var4 != null && var4.x == var0 && var4.y == var1 && var4.w == var2 && var4.h == var3;
      }
   }
}
