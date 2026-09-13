package ru.wild.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.blaze3d.vertex.VertexFormat.IndexType;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public final class BlockEspRenderer {
   private static final Logger data = LogUtils.getLogger();
   private static final Supplier<String> context = () -> "Wild BlockESP";
   private static final Supplier<String> config = () -> "Wild BlockESP vertices";
   private static final Supplier<String> state = () -> "Wild BlockESP uniforms";
   private static final int cache = 40;
   private static final int output = 136;
   private static final int current = 2097152;
   private static final int active = 6;
   private static final float mode = 1.0F;
   private static final float selection = 0.68F;
   private static final float enabled = 0.55F;
   private static final float renderer = 0.45F;
   private static final float handler = 0.1F;
   private static final float animationDraw = 1.25F;
   private static final float pointEncode = 1.22F;
   private static final float animator = 0.55F;
   private static final float source = 0.46F;
   private static final float target = 0.5F;
   private static final float pending = 1.0F;
   private static final float previous = 0.85F;
   private static final float latest = 0.66F;
   private static final float summary = 0.75F;
   private static final float matrixBlend = 0.3F;
   public static final double instance = 256.0;
   private static final float vectorMatch = 2.0F;
   private static final float itemProject = 0.03F;
   private static final Matrix4f responseCompute = new Matrix4f();
   private static volatile boolean providerFetch;
   private GpuBuffer profileDraw;
   private GpuBuffer vectorPerform;
   private int eventAttach;
   private int serverRead;
   private int positionAdvance;
   private int frameCheck;
   private int moduleCollect;
   private float[] providerClose = new float[0];
   private int[] presetSave = new int[0];
   private int[] windowConvert = new int[0];
   private BlockEspGeometry.BufferState presetWrite;
   private BlockEspGeometry.BufferState colorMeasure;
   private int animationSchedule;
   private GpuBuffer rendererScan;
   private ByteBuffer sourceBuild;
   private final GpuBufferSlice[] outputCollapse = new GpuBufferSlice[6];
   private boolean profileInvoke;
   private int sourceSchedule;
   private final Matrix4f timerRender = new Matrix4f();
   private final float[] scaleSave = new float[24];
   private int[] colorCompute = new int[64];
   private int[] scaleAdapt = new int[64];

   public static void handle(Matrix4f var0) {
      if (var0 == null) {
         providerFetch = false;
      } else {
         synchronized (responseCompute) {
            responseCompute.set(var0);
         }

         providerFetch = Math.abs(var0.m23()) > 1.0E-6F;
      }
   }

   public void handle(BlockEspGeometry.BufferState var1) {
      if (this.profileInvoke) {
         BlockEspGeometry.handle(var1);
      } else {
         if (var1 != null && var1.data == 0 && this.presetWrite != null) {
            BlockEspGeometry.handle(this.presetWrite);
            this.presetWrite = null;
            BlockEspGeometry.handle(this.colorMeasure);
            this.colorMeasure = null;
         }

         try {
            this.process(var1);
         } catch (Throwable var3) {
            this.profileInvoke = true;
            data.error("BlockESP geometry upload disabled after failure", var3);
         }
      }
   }

   private void process(BlockEspGeometry.BufferState var1) {
      if (var1 != null) {
         if (this.presetWrite == null) {
            this.resolve(var1);
         } else {
            BlockEspGeometry.handle(this.colorMeasure);
            this.colorMeasure = var1;
         }
      }

      if (this.presetWrite != null && this.vectorPerform != null) {
         int var2 = this.presetWrite.handle();
         int var3 = Math.min(var2 - this.animationSchedule, 2097152);
         if (var3 > 0) {
            ByteBuffer var4 = this.presetWrite.instance;
            var4.limit(this.animationSchedule + var3).position(this.animationSchedule);
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.vectorPerform.slice(this.animationSchedule, var3), var4);
            this.animationSchedule += var3;
         }

         if (this.animationSchedule >= var2) {
            GpuBuffer var6 = this.profileDraw;
            this.profileDraw = this.vectorPerform;
            this.vectorPerform = var6;
            this.eventAttach = this.presetWrite.data;
            this.serverRead = this.presetWrite.context;
            this.positionAdvance = this.presetWrite.config;
            this.frameCheck = this.presetWrite.state;
            this.compute(this.presetWrite);
            BlockEspGeometry.handle(this.presetWrite);
            this.presetWrite = null;
            if (this.colorMeasure != null) {
               BlockEspGeometry.BufferState var5 = this.colorMeasure;
               this.colorMeasure = null;
               this.resolve(var5);
            }
         }
      }
   }

   private void compute(BlockEspGeometry.BufferState var1) {
      this.moduleCollect = var1.cache;
      if (this.moduleCollect != 0) {
         if (this.presetSave.length < this.moduleCollect) {
            this.presetSave = new int[this.moduleCollect];
            this.windowConvert = new int[this.moduleCollect];
            this.providerClose = new float[this.moduleCollect * 6];
         }

         System.arraycopy(var1.current, 0, this.presetSave, 0, this.moduleCollect);
         System.arraycopy(var1.active, 0, this.windowConvert, 0, this.moduleCollect);
         System.arraycopy(var1.output, 0, this.providerClose, 0, this.moduleCollect * 6);
         if (this.colorCompute.length < this.moduleCollect) {
            this.colorCompute = new int[this.moduleCollect];
            this.scaleAdapt = new int[this.moduleCollect];
         }
      }
   }

   private void resolve(BlockEspGeometry.BufferState var1) {
      if (var1.data == 0) {
         this.eventAttach = 0;
         this.moduleCollect = 0;
         this.serverRead = var1.context;
         this.positionAdvance = var1.config;
         this.frameCheck = var1.state;
         BlockEspGeometry.handle(var1);
      } else {
         this.presetWrite = var1;
         this.animationSchedule = 0;
         this.vectorPerform = this.handle(this.vectorPerform, var1.handle());
      }
   }

   public void handle() {
      BlockEspGeometry.handle(this.presetWrite);
      this.presetWrite = null;
      BlockEspGeometry.handle(this.colorMeasure);
      this.colorMeasure = null;
      this.animationSchedule = 0;
      this.eventAttach = 0;
      this.moduleCollect = 0;
   }

   public int process() {
      return this.serverRead;
   }

   public int compute() {
      return this.positionAdvance;
   }

   public int resolve() {
      return this.frameCheck;
   }

   public boolean update() {
      return this.profileDraw != null && this.eventAttach > 0;
   }

   public void handle(
      Matrix4f var1,
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
      boolean var13
   ) {
      if (!this.profileInvoke && this.update()) {
         try {
            this.process(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         } catch (Throwable var15) {
            this.profileInvoke = true;
            data.error("BlockESP render pass disabled after failure", var15);
         }
      }
   }

   private void process(
      Matrix4f var1,
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
      boolean var13
   ) {
      GpuBufferSlice var14 = RenderSystem.getProjectionMatrixBuffer();
      if (var14 != null) {
         MinecraftClient var15 = MinecraftClient.getInstance();
         Framebuffer var16 = var15.getFramebuffer();
         if (var16 != null) {
            int var17 = this.handle(var1, var2, var3, var4, var11);
            if (var17 != 0) {
               float var18 = MathHelper.clamp(var11 * 0.2F, 10.0F, 45.0F);
               float var19 = MathHelper.clamp(var11 * 0.62F, 28.0F, 170.0F);
               GpuBufferSlice var20 = var13
                  ? this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, 0.55F, var18, var19)
                  : null;
               GpuBufferSlice var21 = this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, 1.0F, var18, var19);
               int var22 = this.eventAttach / 4 * 6;
               ShapeIndexBuffer var23 = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
               GpuBuffer var24 = var23.getIndexBuffer(var22);
               IndexType var25 = var23.getIndexType();
               GpuTextureView var26 = RenderSystem.outputColorTextureOverride != null
                  ? RenderSystem.outputColorTextureOverride
                  : var16.getColorAttachmentView();
               GpuTextureView var27 = var16.useDepthAttachment
                  ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : var16.getDepthAttachmentView())
                  : null;
               if (var27 != null) {
                  RenderPass var28 = RenderSystem.getDevice()
                     .createCommandEncoder()
                     .createRenderPass(context, var26, OptionalInt.empty(), var27, OptionalDouble.empty());

                  try {
                     if (var20 != null) {
                        this.handle(var28, BlockEspRenderPipeline.compute(), var14, var20, var24, var25, var17);
                     }

                     this.handle(var28, BlockEspRenderPipeline.process(), var14, var21, var24, var25, var17);
                  } catch (Throwable var32) {
                     if (var28 != null) {
                        try {
                           var28.close();
                        } catch (Throwable var31) {
                           var32.addSuppressed(var31);
                        }
                     }

                     throw var32;
                  }

                  if (var28 != null) {
                     var28.close();
                  }
               }
            }
         }
      }
   }

   private void handle(RenderPass var1, RenderPipeline var2, GpuBufferSlice var3, GpuBufferSlice var4, GpuBuffer var5, IndexType var6, int var7) {
      var1.setPipeline(var2);
      var1.setUniform("Projection", var3);
      var1.setUniform("BlockEsp", var4);
      var1.setVertexBuffer(0, this.profileDraw);
      var1.setIndexBuffer(var5, var6);

      for (int var8 = 0; var8 < var7; var8++) {
         var1.drawIndexed(0, this.colorCompute[var8] / 4 * 6, this.scaleAdapt[var8] / 4 * 6, 1);
      }
   }

   private int handle(Matrix4f var1, float var2, float var3, float var4, float var5) {
      if (this.moduleCollect == 0) {
         this.colorCompute[0] = 0;
         this.scaleAdapt[0] = this.eventAttach;
         return 1;
      }

      if (!providerFetch) {
         return this.execute();
      }

      synchronized (responseCompute) {
         this.timerRender.set(responseCompute);
      }

      this.timerRender.mul(var1);
      this.process(this.timerRender);
      float var26 = (var5 + 24.0F) * (var5 + 24.0F);
      int var7 = 0;
      int var8 = -1;
      int var9 = -1;

      for (int var10 = 0; var10 < this.moduleCollect; var10++) {
         int var11 = this.windowConvert[var10];
         if (var11 > 0) {
            int var12 = var10 * 6;
            float var13 = this.providerClose[var12] - var2;
            float var14 = this.providerClose[var12 + 1] - var3;
            float var15 = this.providerClose[var12 + 2] - var4;
            float var16 = this.providerClose[var12 + 3] - var2;
            float var17 = this.providerClose[var12 + 4] - var3;
            float var18 = this.providerClose[var12 + 5] - var4;
            float var19 = Math.max(Math.max(var13, -var16), 0.0F);
            float var20 = Math.max(Math.max(var14, -var17), 0.0F);
            float var21 = Math.max(Math.max(var15, -var18), 0.0F);
            float var22 = var19 * var19 + var20 * var20 + var21 * var21;
            if (!(var22 > var26)) {
               float var23 = 2.0F + 0.03F * (float)Math.sqrt(var22);
               if (!this.handle(var13 - var23, var14 - var23, var15 - var23, var16 + var23, var17 + var23, var18 + var23)) {
                  int var24 = this.presetSave[var10];
                  if (var8 >= 0 && var24 == var9) {
                     var9 = var24 + var11;
                  } else {
                     if (var8 >= 0) {
                        this.colorCompute[var7] = var8;
                        this.scaleAdapt[var7] = var9 - var8;
                        var7++;
                     }

                     var8 = var24;
                     var9 = var24 + var11;
                  }
               }
            }
         }
      }

      if (var8 >= 0) {
         this.colorCompute[var7] = var8;
         this.scaleAdapt[var7] = var9 - var8;
         var7++;
      }

      return var7;
   }

   private int execute() {
      this.colorCompute[0] = 0;
      this.scaleAdapt[0] = this.eventAttach;
      return 1;
   }

   private void process(Matrix4f var1) {
      this.handle(0, var1.m03() + var1.m00(), var1.m13() + var1.m10(), var1.m23() + var1.m20(), var1.m33() + var1.m30());
      this.handle(1, var1.m03() - var1.m00(), var1.m13() - var1.m10(), var1.m23() - var1.m20(), var1.m33() - var1.m30());
      this.handle(2, var1.m03() + var1.m01(), var1.m13() + var1.m11(), var1.m23() + var1.m21(), var1.m33() + var1.m31());
      this.handle(3, var1.m03() - var1.m01(), var1.m13() - var1.m11(), var1.m23() - var1.m21(), var1.m33() - var1.m31());
      this.handle(4, var1.m03() + var1.m02(), var1.m13() + var1.m12(), var1.m23() + var1.m22(), var1.m33() + var1.m32());
      this.handle(5, var1.m03() - var1.m02(), var1.m13() - var1.m12(), var1.m23() - var1.m22(), var1.m33() - var1.m32());
   }

   private void handle(int var1, float var2, float var3, float var4, float var5) {
      float var6 = (float)Math.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
      if (var6 < 1.0E-8F) {
         var6 = 1.0F;
      }

      int var7 = var1 * 4;
      this.scaleSave[var7] = var2 / var6;
      this.scaleSave[var7 + 1] = var3 / var6;
      this.scaleSave[var7 + 2] = var4 / var6;
      this.scaleSave[var7 + 3] = var5 / var6;
   }

   private boolean handle(float var1, float var2, float var3, float var4, float var5, float var6) {
      for (int var7 = 0; var7 < 6; var7++) {
         int var8 = var7 * 4;
         float var9 = this.scaleSave[var8];
         float var10 = this.scaleSave[var8 + 1];
         float var11 = this.scaleSave[var8 + 2];
         float var12 = this.scaleSave[var8 + 3];
         float var13 = var9 > 0.0F ? var4 : var1;
         float var14 = var10 > 0.0F ? var5 : var2;
         float var15 = var11 > 0.0F ? var6 : var3;
         if (var9 * var13 + var10 * var14 + var11 * var15 + var12 < 0.0F) {
            return true;
         }
      }

      return false;
   }

   private GpuBufferSlice handle(
      Matrix4f var1,
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
      int var16 = BlockEspRenderPipeline.data;
      if (this.rendererScan == null) {
         int var17 = MathHelper.roundUpToMultiple(var16, RenderSystem.getDevice().getUniformOffsetAlignment());
         this.rendererScan = RenderSystem.getDevice().createBuffer(state, 136, var17 * 6);
         this.sourceBuild = MemoryUtil.memAlloc(var16);

         for (int var18 = 0; var18 < 6; var18++) {
            this.outputCollapse[var18] = this.rendererScan.slice(var18 * var17, var16);
         }
      }

      ByteBuffer var19 = this.sourceBuild;
      var1.get(0, var19);
      var19.position(64);
      var19.putFloat(var2).putFloat(var3).putFloat(var4).putFloat(var8);
      var19.putFloat(var10).putFloat(var11).putFloat(var12).putFloat(var9);
      var19.putFloat(1.0F).putFloat(0.68F).putFloat(0.55F).putFloat(0.45F);
      var19.putFloat(0.1F).putFloat(var13).putFloat(1.25F).putFloat(1.22F);
      var19.putFloat(var14).putFloat(var15).putFloat(0.46F).putFloat(0.5F);
      var19.putFloat(var5).putFloat(var6).putFloat(var7).putFloat(1.0F);
      var19.putFloat(0.85F).putFloat(0.66F).putFloat(0.75F).putFloat(0.3F);
      var19.position(0).limit(var16);
      this.sourceSchedule = (this.sourceSchedule + 1) % 6;
      GpuBufferSlice var20 = this.outputCollapse[this.sourceSchedule];
      RenderSystem.getDevice().createCommandEncoder().writeToBuffer(var20, var19);
      var19.clear();
      return var20;
   }

   private GpuBuffer handle(GpuBuffer var1, int var2) {
      int var3 = Math.max(256, (var2 + 128 - 1) / 128);
      int var4 = MathHelper.smallestEncompassingPowerOfTwo(var3) * 128;
      if (var1 != null && var1.size() == var4) {
         return var1;
      }

      if (var1 != null) {
         var1.close();
      }

      return RenderSystem.getDevice().createBuffer(config, 40, var4);
   }

   public void apply() {
      if (this.presetWrite != null) {
         BlockEspGeometry.handle(this.presetWrite);
         this.presetWrite = null;
      }

      if (this.colorMeasure != null) {
         BlockEspGeometry.handle(this.colorMeasure);
         this.colorMeasure = null;
      }

      if (this.profileDraw != null) {
         this.profileDraw.close();
         this.profileDraw = null;
      }

      if (this.vectorPerform != null) {
         this.vectorPerform.close();
         this.vectorPerform = null;
      }

      if (this.rendererScan != null) {
         this.rendererScan.close();
         this.rendererScan = null;
         Arrays.fill(this.outputCollapse, null);
      }

      if (this.sourceBuild != null) {
         MemoryUtil.memFree(this.sourceBuild);
         this.sourceBuild = null;
      }

      this.eventAttach = 0;
      this.moduleCollect = 0;
      this.animationSchedule = 0;
      this.sourceSchedule = 0;
      this.profileInvoke = false;
   }
}
