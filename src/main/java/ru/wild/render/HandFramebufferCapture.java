package ru.wild.render;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SequencedMap;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Hand;
import ru.wild.util.render.IrisCompatibility;

public final class HandFramebufferCapture {
   private static final int instance = 262144;
   private static final HandFramebufferCapture data = new HandFramebufferCapture();
   private final Map<Hand, HandFramebufferCapture.VertexEmitter> context = new EnumMap<>(Hand.class);
   private boolean config;
   private int state = -1;
   private int cache = -1;

   private HandFramebufferCapture() {
      this.context.put(Hand.MAIN_HAND, new HandFramebufferCapture.VertexEmitter("wild_hands_main"));
      this.context.put(Hand.OFF_HAND, new HandFramebufferCapture.VertexEmitter("wild_hands_off"));
   }

   public static HandFramebufferCapture handle() {
      return data;
   }

   public void handle(boolean var1, boolean var2, int var3, int var4) {
      this.config = false;
      this.context.values().forEach(HandFramebufferCapture.VertexEmitter::process);
      if ((var1 || var2) && var3 > 0 && var4 > 0 && !IrisCompatibility.process()) {
         this.state = var3;
         this.cache = var4;
         HandFramebufferCapture.VertexEmitter var5 = this.context.get(Hand.MAIN_HAND);
         HandFramebufferCapture.VertexEmitter var6 = this.context.get(Hand.OFF_HAND);

         try {
            if (var1 && var5.handle(var3, var4)) {
               var5.handle();
               this.config = true;
            }

            if (var2 && var6.handle(var3, var4)) {
               var6.handle();
               this.config = true;
            }
         } catch (RuntimeException var8) {
            this.config = false;
            this.context.values().forEach(HandFramebufferCapture.VertexEmitter::process);
         }
      }
   }

   public VertexConsumerProvider handle(Hand var1, VertexConsumerProvider var2) {
      HandFramebufferCapture.VertexEmitter var3 = this.context.get(var1);
      return this.config && var3 != null && var3.context && var2 != null && !IrisCompatibility.process()
         ? var2x -> VertexConsumers.union(var2.getBuffer(var2x), var3.instance.state.handle(var2x))
         : var2;
   }

   public VertexConsumerProvider process(Hand var1, VertexConsumerProvider var2) {
      HandFramebufferCapture.VertexEmitter var3 = this.context.get(var1);
      return this.config && var3 != null && var3.context && var2 != null && !IrisCompatibility.process()
         ? var2x -> VertexConsumers.union(var2.getBuffer(var2x), var3.data.state.handle(var2x))
         : var2;
   }

   public void handle(Hand var1) {
      HandFramebufferCapture.VertexEmitter var2 = this.context.get(var1);
      if (this.config && var2 != null && var2.context) {
         var2.instance.process();
         var2.data.process();
      }
   }

   public boolean process(Hand var1) {
      HandFramebufferCapture.VertexEmitter var2 = this.context.get(var1);
      return var2 != null && var2.instance.current;
   }

   public int compute(Hand var1) {
      HandFramebufferCapture.VertexEmitter var2 = this.context.get(var1);
      return var2 != null && var2.instance.current ? handle(var2.instance.config, false) : 0;
   }

   public int resolve(Hand var1) {
      HandFramebufferCapture.VertexEmitter var2 = this.context.get(var1);
      return var2 != null && var2.instance.current ? handle(var2.instance.config, true) : 0;
   }

   public int update(Hand var1) {
      HandFramebufferCapture.VertexEmitter var2 = this.context.get(var1);
      return var2 != null && var2.data.current ? handle(var2.data.config, false) : 0;
   }

   public void process() {
      this.config = false;
      this.state = -1;
      this.cache = -1;
      this.context.values().forEach(HandFramebufferCapture.VertexEmitter::compute);
   }

   private static int handle(Framebuffer var0, boolean var1) {
      if (var0 == null) {
         return 0;
      } else {
         return (var1 ? var0.getDepthAttachment() : var0.getColorAttachment()) instanceof GlTexture var3 ? var3.getGlId() : 0;
      }
   }

   static final class FramebufferState {
      private final String instance;
      private final BufferAllocator data = new BufferAllocator(262144);
      private final SequencedMap<RenderLayer, BufferAllocator> context = new LinkedHashMap<>();
      SimpleFramebuffer config;
      HandFramebufferCapture.PrimaryVertexEmitter state;
      private int cache = -1;
      private int output = -1;
      boolean current;

      FramebufferState(String var1) {
         this.instance = var1;
      }

      boolean handle(int var1, int var2) {
         if (this.config == null) {
            this.config = new SimpleFramebuffer(this.instance, var1, var2, true);
            this.cache = var1;
            this.output = var2;
         } else if (this.cache != var1 || this.output != var2) {
            this.config.resize(var1, var2);
            this.cache = var1;
            this.output = var2;
         }

         GpuTextureView var3 = this.config.getColorAttachmentView();
         GpuTextureView var4 = this.config.getDepthAttachmentView();
         return var3 != null && !var3.isClosed() && (var4 == null || !var4.isClosed());
      }

      void handle() {
         this.resolve();
         this.compute();
         this.data.clear();
         this.context.values().forEach(BufferAllocator::clear);
         this.state = new HandFramebufferCapture.PrimaryVertexEmitter(this.data, this.context);
      }

      void process() {
         HandFramebufferCapture.PrimaryVertexEmitter var1 = this.state;
         if (var1 != null && var1.config && this.config != null) {
            GpuTextureView var2 = this.config.getColorAttachmentView();
            if (var2 != null && !var2.isClosed()) {
               GpuTextureView var3 = RenderSystem.outputColorTextureOverride;
               GpuTextureView var4 = RenderSystem.outputDepthTextureOverride;
               RenderSystem.outputColorTextureOverride = var2;
               RenderSystem.outputDepthTextureOverride = this.config.getDepthAttachmentView();

               try {
                  var1.handle();
                  this.current = true;
               } finally {
                  RenderSystem.outputColorTextureOverride = var3;
                  RenderSystem.outputDepthTextureOverride = var4;
               }
            }
         }
      }

      private void compute() {
         if (this.config != null) {
            GpuTextureView var1 = this.config.getColorAttachmentView();
            GpuTextureView var2 = this.config.getDepthAttachmentView();
            if (var1 != null && !var1.isClosed()) {
               CommandEncoder var3 = RenderSystem.getDevice().createCommandEncoder();
               if (var2 != null && !var2.isClosed()) {
                  var3.clearColorAndDepthTextures(var1.texture(), 0, var2.texture(), 1.0);
               } else {
                  var3.clearColorTexture(var1.texture(), 0);
               }
            }
         }
      }

      void resolve() {
         this.current = false;
         HandFramebufferCapture.PrimaryVertexEmitter var1 = this.state;
         this.state = null;
         if (var1 != null) {
            var1.close();
         }
      }

      void update() {
         this.resolve();

         for (BufferAllocator var2 : this.context.values()) {
            var2.clear();
         }

         this.data.clear();
         if (this.config != null) {
            this.config.delete();
            this.config = null;
         }

         this.cache = -1;
         this.output = -1;
      }
   }

   static final class PrimaryVertexEmitter implements AutoCloseable {
      private final BufferAllocator instance;
      private final SequencedMap<RenderLayer, BufferAllocator> data;
      private final Immediate context;
      boolean config;
      private boolean state;

      PrimaryVertexEmitter(BufferAllocator var1, SequencedMap<RenderLayer, BufferAllocator> var2) {
         this.instance = var1;
         this.data = var2;
         this.context = VertexConsumerProvider.immediate(var2, var1);
      }

      VertexConsumer handle(RenderLayer var1) {
         this.data.computeIfAbsent(var1, var0 -> new BufferAllocator(Math.max(4096, Math.min(var0.getExpectedBufferSize(), 262144))));
         this.config = true;
         this.state = false;
         return this.context.getBuffer(var1);
      }

      void handle() {
         if (!this.state) {
            this.context.draw();
            this.instance.clear();
            this.data.values().forEach(BufferAllocator::clear);
            this.state = true;
         }
      }

      @Override
      public void close() {
         this.instance.clear();
         this.data.values().forEach(BufferAllocator::clear);
      }
   }

   static final class VertexEmitter {
      final HandFramebufferCapture.FramebufferState instance;
      final HandFramebufferCapture.FramebufferState data;
      boolean context;

      VertexEmitter(String var1) {
         this.instance = new HandFramebufferCapture.FramebufferState(var1 + "_mask");
         this.data = new HandFramebufferCapture.FramebufferState(var1 + "_item");
      }

      boolean handle(int var1, int var2) {
         return this.instance.handle(var1, var2) && this.data.handle(var1, var2);
      }

      void handle() {
         this.instance.handle();
         this.data.handle();
         this.context = true;
      }

      private void process() {
         this.context = false;
         this.instance.resolve();
         this.data.resolve();
      }

      private void compute() {
         this.context = false;
         this.instance.update();
         this.data.update();
      }
   }
}
