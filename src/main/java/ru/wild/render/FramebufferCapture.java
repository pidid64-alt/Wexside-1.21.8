package ru.wild.render;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.wild.mixin.acceser.EntityRenderDispatcherAccessor;
import ru.wild.util.render.IrisCompatibility;
import ru.wild.util.render.RoundedRectRenderer;

public final class FramebufferCapture {
   private static final int instance = 1048576;
   private static final Logger data = LogManager.getLogger("EntityFramebufferCapture");
   private static final FramebufferCapture context = new FramebufferCapture();
   private static final Predicate<Entity> config = var0 -> true;
   private volatile SimpleFramebuffer state;
   private volatile SimpleFramebuffer cache;
   private final Map<String, Predicate<Entity>> output = new ConcurrentHashMap<>();
   private final Map<String, Predicate<Entity>> current = new ConcurrentHashMap<>();
   private volatile boolean active;
   private volatile boolean mode;
   private volatile boolean selection;
   private volatile boolean enabled;
   private volatile boolean renderer;
   private volatile boolean handler;
   private volatile int animationDraw = -1;
   private volatile int pointEncode = -1;
   private volatile int animator;
   private volatile int source;
   private volatile int target = Integer.MIN_VALUE;
   private int pending;
   private FramebufferCapture.VertexEmitter previous;
   private final BufferAllocator latest = new BufferAllocator(1048576);
   private final SequencedMap<RenderLayer, BufferAllocator> summary = new LinkedHashMap<>();

   private FramebufferCapture() {
   }

   public static FramebufferCapture handle() {
      return context;
   }

   public void handle(boolean var1) {
      if (this.active != var1) {
         this.active = var1;
         this.unload();
      }
   }

   public void handle(String var1, boolean var2, Predicate<Entity> var3) {
      if (var1 == null || var1.isBlank()) {
         throw new IllegalArgumentException("owner");
      }

      if (!var2) {
         this.handle(var1);
      } else {
         Predicate var4 = var3 == null ? config : var3;
         if (this.output.get(var1) != var4) {
            this.output.put(var1, var4);
         }
      }
   }

   public void handle(String var1) {
      if (var1 != null && !var1.isBlank()) {
         this.output.remove(var1);
         this.unload();
      }
   }

   public void process(String var1, boolean var2, Predicate<Entity> var3) {
      if (var1 == null || var1.isBlank()) {
         throw new IllegalArgumentException("owner");
      }

      if (var2 && var3 != null) {
         if (this.current.get(var1) != var3) {
            this.current.put(var1, var3);
         }
      } else {
         this.process(var1);
      }
   }

   public void process(String var1) {
      if (var1 != null && !var1.isBlank()) {
         this.current.remove(var1);
         if (this.current.isEmpty()) {
            this.selection = false;
         }
      }
   }

   private boolean load() {
      return !this.current.isEmpty();
   }

   private boolean handle(Entity var1) {
      if (this.current.isEmpty()) {
         return false;
      }

      for (Predicate var3 : this.current.values()) {
         try {
            if (var3.test(var1)) {
               return true;
            }
         } catch (RuntimeException var5) {
            data.warn("Entity tag filter failed for {}", var1.getName().getString(), var5);
         }
      }

      return false;
   }

   public boolean process() {
      return this.active || !this.output.isEmpty();
   }

   public boolean compute() {
      return this.process() && this.mode && this.animationDraw > 0 && this.pointEncode > 0 && compute(this.state);
   }

   public int resolve() {
      return this.compute() ? resolve(this.state) : 0;
   }

   public int update() {
      return this.compute() ? update(this.state) : 0;
   }

   public boolean apply() {
      return this.process() && this.load() && this.selection && compute(this.cache);
   }

   public int execute() {
      return this.apply() ? resolve(this.cache) : 0;
   }

   public int prepare() {
      return this.apply() ? update(this.cache) : 0;
   }

   public boolean check() {
      return this.enabled || this.renderer;
   }

   public boolean onTick() {
      return this.enabled;
   }

   public void handle(WorldRenderer var1, RenderTickCounter var2, Camera var3) {
      if (!this.process()) {
         this.fetch();
      } else {
         Objects.requireNonNull(var1, "worldRenderer");
         Objects.requireNonNull(var2, "tickCounter");
         MinecraftClient var4 = MinecraftClient.getInstance();
         if (var4 == null || var4.world == null || var4.gameRenderer == null) {
            this.fetch();
         } else if (!var4.gameRenderer.isRenderingPanorama() && var3 != null) {
            Framebuffer var5 = var4.getFramebuffer();
            if (var5 == null) {
               this.fetch();
            } else {
               Window var6 = var4.getWindow();
               int var7 = var6 != null ? var6.getFramebufferWidth() : var5.textureWidth;
               int var8 = var6 != null ? var6.getFramebufferHeight() : var5.textureHeight;
               if (var7 <= 0 || var8 <= 0) {
                  this.fetch();
                  this.submit();
                  this.animationDraw = -1;
                  this.pointEncode = -1;
               } else if (!this.compute(var7, var8)) {
                  this.fetch();
               } else {
                  SimpleFramebuffer var9 = this.state;
                  if (var9 == null) {
                     this.fetch();
                  } else {
                     GpuTextureView var10 = var9.getColorAttachmentView();
                     if (var10 != null && !var10.isClosed()) {
                        GpuTextureView var11 = var9.getDepthAttachmentView();
                        if (!this.handle(var9)) {
                           CommandEncoder var12 = RenderSystem.getDevice().createCommandEncoder();
                           GpuTexture var13 = var10.texture();
                           if (var11 != null && !var11.isClosed()) {
                              var12.clearColorAndDepthTextures(var13, 0, var11.texture(), 1.0);
                           } else {
                              var12.clearColorTexture(var13, 0);
                           }
                        }

                        this.measure();

                        try {
                           this.latest.clear();
                           this.summary.values().forEach(BufferAllocator::clear);
                           this.previous = new FramebufferCapture.VertexEmitter(this.latest, this.summary);
                        } catch (RuntimeException var14) {
                           data.warn("Failed to allocate capture resources", var14);
                           this.fetch();
                           return;
                        }

                        this.process(var7, var8);
                        this.mode = false;
                        this.handler = true;
                        this.animator = 0;
                     } else {
                        this.fetch();
                     }
                  }
               }
            }
         } else {
            this.fetch();
         }
      }
   }

   private void process(int var1, int var2) {
      this.selection = false;
      if (!this.load()) {
         this.save();
      } else if (this.resolve(var1, var2)) {
         SimpleFramebuffer var3 = this.cache;
         if (var3 != null) {
            GpuTextureView var4 = var3.getColorAttachmentView();
            if (var4 != null && !var4.isClosed()) {
               GpuTextureView var5 = var3.getDepthAttachmentView();
               if (!this.handle(var3)) {
                  CommandEncoder var6 = RenderSystem.getDevice().createCommandEncoder();
                  if (var5 != null && !var5.isClosed()) {
                     var6.clearColorAndDepthTextures(var4.texture(), 0, var5.texture(), 1.0);
                  } else {
                     var6.clearColorTexture(var4.texture(), 0);
                  }
               }
            }
         }
      }
   }

   public void select() {
      FramebufferCapture.VertexEmitter var1 = this.previous;

      try {
         if (var1 != null) {
            try {
               this.handle(var1);
            } finally {
               var1.close();
            }
         }
      } catch (RuntimeException var11) {
         data.warn("Failed to finalize capture frame", var11);
         this.mode = false;
      } finally {
         this.previous = null;
         this.handler = false;
         this.source = this.animator;
      }

      this.mode = this.mode && compute(this.state);
      this.selection = this.selection && compute(this.cache);
   }
   public void handle(Entity var1, double var2, double var4, double var6, float var8, MatrixStack var9) {
      if (this.process() && this.handler && !this.enabled && !this.renderer && !IrisCompatibility.process() && var1 != null) {
         if (var1 instanceof LivingEntity) {
            if (this.handle(var1)) {
               this.process(var1, var2, var4, var6, var8, var9);
            }
         } else if (this.process(var1)) {
            Objects.requireNonNull(var9, "matrices");
            FramebufferCapture.VertexEmitter var10 = this.previous;
            SimpleFramebuffer var11 = this.state;
            if (var10 != null && var11 != null && this.animationDraw > 0 && this.pointEncode > 0) {
               GpuTextureView var12 = var11.getColorAttachmentView();
               if (var12 != null && !var12.isClosed()) {
                  GpuTextureView var13 = var11.getDepthAttachmentView();
                  MinecraftClient var14 = MinecraftClient.getInstance();
                  if (var14 != null && var14.world != null) {
                     EntityRenderDispatcher var15 = var14.getEntityRenderDispatcher();
                     if (var15 != null) {
                        MatrixStack var16 = var10.handle(var9);
                        if (var16 != null) {
                           Vec3d var17 = var1.getLerpedPos(var8);
                           double var18 = var17.x - var2;
                           double var20 = var17.y - var4;
                           double var22 = var17.z - var6;
                           BlockPos var24 = BlockPos.ofFloored(var17);
                           int var25 = var14.world.getLightLevel(LightType.BLOCK, var24);
                           int var26 = var14.world.getLightLevel(LightType.SKY, var24);
                           int var27 = LightmapTextureManager.pack(var26, var25);
                           GpuTextureView var28 = RenderSystem.outputColorTextureOverride;
                           GpuTextureView var29 = RenderSystem.outputDepthTextureOverride;
                           RenderSystem.outputColorTextureOverride = var12;
                           RenderSystem.outputDepthTextureOverride = var13;
                           EntityRenderDispatcherAccessor var30 = var15 instanceof EntityRenderDispatcherAccessor var31 ? var31 : null;
                           boolean var45 = var30 != null;
                           boolean var32 = false;
                           if (var30 != null) {
                              var32 = var30.night$getRenderShadows();
                              var30.night$setRenderShadows(false);
                           }

                           this.enabled = true;
                           boolean var38 = false /* VF: Semaphore variable */;

                           label252: {
                              try {
                                 try {
                                    var38 = true;
                                    var15.render(var1, var18, var20, var22, var8, var16, var10.handle(), var27);
                                    var10.process();
                                    this.animator++;
                                    this.mode = true;
                                 } finally {
                                    var10.compute();
                                 }

                                 var38 = false;
                                 break label252;
                              } catch (RuntimeException var43) {
                                 data.warn("Failed to visuals entity {} into capture framebuffer", var1.getName().getString(), var43);
                                 this.mode = false;
                                 var38 = false;
                              } finally {
                                 if (var38) {
                                    this.enabled = false;
                                    if (var45) {
                                       var30.night$setRenderShadows(var32);
                                    }

                                    RenderSystem.outputColorTextureOverride = var28;
                                    RenderSystem.outputDepthTextureOverride = var29;
                                 }
                              }

                              this.enabled = false;
                              if (var45) {
                                 var30.night$setRenderShadows(var32);
                              }

                              RenderSystem.outputColorTextureOverride = var28;
                              RenderSystem.outputDepthTextureOverride = var29;
                              return;
                           }

                           this.enabled = false;
                           if (var45) {
                              var30.night$setRenderShadows(var32);
                           }

                           RenderSystem.outputColorTextureOverride = var28;
                           RenderSystem.outputDepthTextureOverride = var29;
                        }
                     }
                  }
               }
            }
         }
      }
   }
   private void process(Entity var1, double var2, double var4, double var6, float var8, MatrixStack var9) {
      FramebufferCapture.VertexEmitter var10 = this.previous;
      SimpleFramebuffer var11 = this.cache;
      if (var10 != null && var11 != null && compute(var11) && var9 != null) {
         GpuTextureView var12 = var11.getColorAttachmentView();
         if (var12 != null && !var12.isClosed()) {
            GpuTextureView var13 = var11.getDepthAttachmentView();
            MinecraftClient var14 = MinecraftClient.getInstance();
            if (var14 != null && var14.world != null) {
               EntityRenderDispatcher var15 = var14.getEntityRenderDispatcher();
               if (var15 != null) {
                  MatrixStack var16 = var10.handle(var9);
                  if (var16 != null) {
                     double var17 = MathHelper.lerp(var8, var1.lastRenderX, var1.getX());
                     double var19 = MathHelper.lerp(var8, var1.lastRenderY, var1.getY());
                     double var21 = MathHelper.lerp(var8, var1.lastRenderZ, var1.getZ());
                     double var23 = var17 - var2;
                     double var25 = var19 - var4;
                     double var27 = var21 - var6;
                     BlockPos var29 = BlockPos.ofFloored(var17, var19, var21);
                     int var30 = var14.world.getLightLevel(LightType.BLOCK, var29);
                     int var31 = var14.world.getLightLevel(LightType.SKY, var29);
                     int var32 = LightmapTextureManager.pack(var31, var30);
                     GpuTextureView var33 = RenderSystem.outputColorTextureOverride;
                     GpuTextureView var34 = RenderSystem.outputDepthTextureOverride;
                     RenderSystem.outputColorTextureOverride = var12;
                     RenderSystem.outputDepthTextureOverride = var13;
                     EntityRenderDispatcherAccessor var35 = var15 instanceof EntityRenderDispatcherAccessor var36 ? var36 : null;
                     boolean var49 = false;
                     if (var35 != null) {
                        var49 = var35.night$getRenderShadows();
                        var35.night$setRenderShadows(false);
                     }

                     this.renderer = true;
                     boolean var42 = false /* VF: Semaphore variable */;

                     label187: {
                        try {
                           try {
                              var42 = true;
                              var15.render(var1, var23, var25, var27, var8, var16, var10.handle(), var32);
                              var10.process();
                              this.selection = true;
                           } finally {
                              var10.compute();
                           }

                           var42 = false;
                           break label187;
                        } catch (RuntimeException var47) {
                           data.warn("Failed to render tagged entity {} into capture framebuffer", var1.getName().getString(), var47);
                           this.selection = false;
                           var42 = false;
                        } finally {
                           if (var42) {
                              this.renderer = false;
                              if (var35 != null) {
                                 var35.night$setRenderShadows(var49);
                              }

                              RenderSystem.outputColorTextureOverride = var33;
                              RenderSystem.outputDepthTextureOverride = var34;
                           }
                        }

                        this.renderer = false;
                        if (var35 != null) {
                           var35.night$setRenderShadows(var49);
                        }

                        RenderSystem.outputColorTextureOverride = var33;
                        RenderSystem.outputDepthTextureOverride = var34;
                        return;
                     }

                     this.renderer = false;
                     if (var35 != null) {
                        var35.night$setRenderShadows(var49);
                     }

                     RenderSystem.outputColorTextureOverride = var33;
                     RenderSystem.outputDepthTextureOverride = var34;
                  }
               }
            }
         }
      }
   }

   public boolean refresh() {
      return this.process() && this.handler && this.animationDraw > 0 && this.pointEncode > 0 && compute(this.state);
   }
   public VertexConsumer handle(VertexConsumer var1, RenderLayer var2, LivingEntityRenderState var3) {
      if (var1 != null && var2 != null && this.process() && this.handler && !this.enabled && !this.renderer && !IrisCompatibility.process()) {
         LivingEntity var4 = this.process(var3);
         FramebufferCapture.VertexEmitter var5 = this.previous;
         SimpleFramebuffer var6 = this.state;
         if (var4 != null && var5 != null && var6 != null) {
            GpuTextureView var7 = var6.getColorAttachmentView();
            if (var7 != null && !var7.isClosed()) {
               GpuTextureView var8 = RenderSystem.outputColorTextureOverride;
               GpuTextureView var9 = RenderSystem.outputDepthTextureOverride;
               RenderSystem.outputColorTextureOverride = var7;
               RenderSystem.outputDepthTextureOverride = var6.getDepthAttachmentView();
               boolean var15 = false /* VF: Semaphore variable */;

               VertexConsumer var18;
               label60: {
                  try {
                     var15 = true;
                     VertexConsumer var10 = var5.handle(var2);
                     var5.process();
                     this.target = var4.getId();
                     var18 = VertexConsumers.union(var1, var10);
                     var15 = false;
                     break label60;
                  } catch (RuntimeException var16) {
                     data.warn("Failed to prepare living layer {} for {}", var2, var4.getName().getString(), var16);
                     var18 = var1;
                     var15 = false;
                  } finally {
                     if (var15) {
                        RenderSystem.outputColorTextureOverride = var8;
                        RenderSystem.outputDepthTextureOverride = var9;
                     }
                  }

                  RenderSystem.outputColorTextureOverride = var8;
                  RenderSystem.outputDepthTextureOverride = var9;
                  return var18;
               }

               RenderSystem.outputColorTextureOverride = var8;
               RenderSystem.outputDepthTextureOverride = var9;
               return var18;
            } else {
               return var1;
            }
         } else {
            return var1;
         }
      } else {
         return var1;
      }
   }

   public void handle(FeatureRenderer var1, MatrixStack var2, VertexConsumerProvider var3, int var4, LivingEntityRenderState var5, float var6, float var7) {
      LivingEntity var8 = this.process(var5);
      if (this.process() && this.handler && !this.enabled && !this.renderer && !IrisCompatibility.process() && var8 != null) {
         VertexConsumerProvider var9 = var3x -> this.handle(var3.getBuffer(var3x), var3x, var5);
         var1.render(var2, var9, var4, var5, var6, var7);
      } else {
         var1.render(var2, var3, var4, var5, var6, var7);
      }
   }

   public void handle(LivingEntityRenderState var1) {
      if (!this.renderer) {
         if (IrisCompatibility.process()) {
            this.target = Integer.MIN_VALUE;
         } else {
            LivingEntity var2 = this.process(var1);
            if (var2 != null && this.target == var2.getId()) {
               FramebufferCapture.VertexEmitter var3 = this.previous;
               this.target = Integer.MIN_VALUE;
               if (var3 != null) {
                  try {
                     this.handle(var3);
                     this.animator++;
                     this.mode = true;
                  } catch (RuntimeException var5) {
                     data.warn("Failed to finish living capture for {}", var2.getName().getString(), var5);
                     this.mode = false;
                  }
               }
            }
         }
      }
   }

   public int render() {
      return this.animator;
   }

   public int tick() {
      return this.source;
   }

   public int drawAnimation() {
      return this.summary.size();
   }

   public int encodePoint() {
      return this.animationDraw;
   }

   public int animate() {
      return this.pointEncode;
   }

   public void handle(RoundedRectRenderer var1, int var2, int var3) {
      if (var1 != null && var2 > 0 && var3 > 0) {
         if (this.compute()) {
            int var4 = resolve(this.state);
            if (var4 > 0) {
               var1.process(var4, 0.0F, 0.0F, var2, var3);
            }
         }
      }
   }

   public void handle(int var1, int var2) {
      this.fetch();
      this.mode = false;
      if (var1 <= 0 || var2 <= 0 || var1 != this.animationDraw || var2 != this.pointEncode) {
         this.submit();
         this.save();
         this.animationDraw = -1;
         this.pointEncode = -1;
      }
   }

   private boolean compute(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         SimpleFramebuffer var3 = this.state;
         if (var3 != null && !compute(var3)) {
            this.submit();
            this.animationDraw = -1;
            this.pointEncode = -1;
            var3 = null;
         }

         if (var3 == null) {
            try {
               var3 = new SimpleFramebuffer("night_entity_capture", var1, var2, true);
               this.state = var3;
               this.animationDraw = var1;
               this.pointEncode = var2;
            } catch (RuntimeException var6) {
               data.warn("Failed to create capture framebuffer {}x{}", var1, var2, var6);
               this.state = null;
               this.animationDraw = -1;
               this.pointEncode = -1;
               return false;
            }
         }

         if (this.animationDraw != var1 || this.pointEncode != var2) {
            try {
               var3.resize(var1, var2);
               this.animationDraw = var1;
               this.pointEncode = var2;
            } catch (RuntimeException var5) {
               data.warn("Failed to resize capture framebuffer to {}x{}", var1, var2, var5);
               this.submit();
               this.animationDraw = -1;
               this.pointEncode = -1;
               return false;
            }
         }

         return compute(var3);
      } else {
         this.submit();
         this.animationDraw = -1;
         this.pointEncode = -1;
         return false;
      }
   }

   private boolean resolve(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         SimpleFramebuffer var3 = this.cache;
         if (var3 != null && !compute(var3)) {
            this.save();
            var3 = null;
         }

         if (var3 == null) {
            try {
               var3 = new SimpleFramebuffer("wild_tagged_capture", var1, var2, true);
               this.cache = var3;
            } catch (RuntimeException var6) {
               data.warn("Failed to create tagged capture framebuffer {}x{}", var1, var2, var6);
               this.cache = null;
               return false;
            }
         }

         if (var3.textureWidth != var1 || var3.textureHeight != var2) {
            try {
               var3.resize(var1, var2);
            } catch (RuntimeException var5) {
               data.warn("Failed to resize tagged capture framebuffer to {}x{}", var1, var2, var5);
               this.save();
               return false;
            }
         }

         return compute(var3);
      } else {
         this.save();
         return false;
      }
   }

   private void save() {
      SimpleFramebuffer var1 = this.cache;
      if (var1 != null) {
         if (!RenderSystem.isOnRenderThread()) {
            this.cache = null;
         } else {
            try {
               var1.delete();
            } catch (RuntimeException var3) {
               data.warn("Failed to delete tagged capture framebuffer", var3);
            }

            this.cache = null;
         }
      }
   }

   private void submit() {
      SimpleFramebuffer var1 = this.state;
      if (var1 != null || this.pending != 0) {
         if (!RenderSystem.isOnRenderThread()) {
            this.state = null;
            this.pending = 0;
         } else {
            if (var1 != null) {
               try {
                  var1.delete();
               } catch (RuntimeException var3) {
                  data.warn("Failed to delete capture framebuffer", var3);
               }

               this.state = null;
            }

            if (this.pending != 0) {
               GL30.glDeleteFramebuffers(this.pending);
               this.pending = 0;
            }
         }
      }
   }
   private boolean handle(Framebuffer var1) {
      if (var1.getColorAttachment() instanceof GlTexture var2) {
         int var19 = var2.getGlId();
         int var4 = var1.getDepthAttachment() instanceof GlTexture var5 ? var5.getGlId() : 0;
         if (var19 <= 0) {
            return false;
         }

         OpenGlStateSnapshot.NetworkState var20 = OpenGlStateSnapshot.handle();
         boolean var14 = false /* VF: Semaphore variable */;

         boolean var7;
         label157: {
            label158: {
               boolean var24;
               try {
                  label144: {
                     MemoryStack var21;
                     label159: {
                        var14 = true;
                        var21 = MemoryStack.stackPush();

                        try {
                           if (this.pending == 0) {
                              this.pending = GL30.glGenFramebuffers();
                           }

                           GL30.glBindFramebuffer(36160, this.pending);
                           GL30.glFramebufferTexture2D(36160, 36064, 3553, var19, 0);
                           GL30.glFramebufferTexture2D(36160, 36096, 3553, var4, 0);
                           GL11.glDrawBuffer(36064);
                           if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                              var7 = false;
                              break label159;
                           }

                           GL11.glColorMask(true, true, true, true);
                           GL11.glDepthMask(true);
                           FloatBuffer var22 = var21.floats(0.0F, 0.0F, 0.0F, 0.0F);
                           GL30.glClearBufferfv(6144, 0, var22);
                           if (var4 > 0) {
                              FloatBuffer var8 = var21.floats(1.0F);
                              GL30.glClearBufferfv(6145, 0, var8);
                           }

                           var24 = true;
                        } catch (Throwable var16) {
                           if (var21 != null) {
                              try {
                                 var21.close();
                              } catch (Throwable var15) {
                                 var16.addSuppressed(var15);
                              }
                           }

                           throw var16;
                        }

                        if (var21 != null) {
                           var21.close();
                           var14 = false;
                        } else {
                           var14 = false;
                        }
                        break label144;
                     }

                     if (var21 != null) {
                        var21.close();
                        var14 = false;
                     } else {
                        var14 = false;
                     }
                     break label158;
                  }
               } catch (RuntimeException var17) {
                  data.warn("Failed to clear capture framebuffer directly", var17);
                  var7 = false;
                  var14 = false;
                  break label157;
               } finally {
                  if (var14) {
                     if (this.pending != 0) {
                        GL30.glBindFramebuffer(36160, this.pending);
                        GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                        GL30.glFramebufferTexture2D(36160, 36096, 3553, 0, 0);
                     }

                     OpenGlStateSnapshot.compute(var20);
                  }
               }

               if (this.pending != 0) {
                  GL30.glBindFramebuffer(36160, this.pending);
                  GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                  GL30.glFramebufferTexture2D(36160, 36096, 3553, 0, 0);
               }

               OpenGlStateSnapshot.compute(var20);
               return var24;
            }

            if (this.pending != 0) {
               GL30.glBindFramebuffer(36160, this.pending);
               GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
               GL30.glFramebufferTexture2D(36160, 36096, 3553, 0, 0);
            }

            OpenGlStateSnapshot.compute(var20);
            return var7;
         }

         if (this.pending != 0) {
            GL30.glBindFramebuffer(36160, this.pending);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
            GL30.glFramebufferTexture2D(36160, 36096, 3553, 0, 0);
         }

         OpenGlStateSnapshot.compute(var20);
         return var7;
      } else {
         return false;
      }
   }

   private boolean process(Entity var1) {
      if (this.active) {
         return true;
      }

      for (Predicate var3 : this.output.values()) {
         try {
            if (var3.test(var1)) {
               return true;
            }
         } catch (RuntimeException var5) {
            data.warn("Entity capture filter failed for {}", var1.getName().getString(), var5);
         }
      }

      return false;
   }

   private LivingEntity process(LivingEntityRenderState var1) {
      if (var1 == null) {
         return null;
      }

      int var2 = ((EntityRenderIdAccess)var1).wild$getEntityId();
      MinecraftClient var3 = MinecraftClient.getInstance();
      return (var3 != null && var3.world != null && var2 != Integer.MIN_VALUE ? var3.world.getEntityById(var2) : null) instanceof LivingEntity var5
            && this.process(var5)
         ? var5
         : null;
   }
   private void handle(FramebufferCapture.VertexEmitter var1) {
      SimpleFramebuffer var2 = this.state;
      if (var1 != null && var2 != null) {
         GpuTextureView var3 = var2.getColorAttachmentView();
         if (var3 != null && !var3.isClosed()) {
            GpuTextureView var4 = RenderSystem.outputColorTextureOverride;
            GpuTextureView var5 = RenderSystem.outputDepthTextureOverride;
            RenderSystem.outputColorTextureOverride = var3;
            RenderSystem.outputDepthTextureOverride = var2.getDepthAttachmentView();
            this.enabled = true;
            boolean var8 = false /* VF: Semaphore variable */;

            try {
               var8 = true;
               var1.compute();
               var8 = false;
            } finally {
               if (var8) {
                  this.enabled = false;
                  RenderSystem.outputColorTextureOverride = var4;
                  RenderSystem.outputDepthTextureOverride = var5;
               }
            }

            this.enabled = false;
            RenderSystem.outputColorTextureOverride = var4;
            RenderSystem.outputDepthTextureOverride = var5;
         }
      }
   }

   private void unload() {
      if (!this.process()) {
         this.mode = false;
         this.selection = false;
         this.enabled = false;
         this.renderer = false;
         this.handler = false;
         this.animationDraw = -1;
         this.pointEncode = -1;
         this.animator = 0;
         this.source = 0;
         this.target = Integer.MIN_VALUE;
         this.measure();
         this.blendMatrix();
         this.submit();
         this.save();
      }
   }

   private void fetch() {
      this.mode = false;
      this.selection = false;
      this.handler = false;
      this.animator = 0;
      this.target = Integer.MIN_VALUE;
      this.measure();
   }

   private void measure() {
      FramebufferCapture.VertexEmitter var1 = this.previous;
      if (var1 != null) {
         try {
            try {
               var1.compute();
            } catch (RuntimeException var3) {
               data.warn("Failed to flush capture resources during reset", var3);
            }

            var1.close();
         } catch (RuntimeException var4) {
            data.warn("Failed to release capture resources", var4);
         }

         this.previous = null;
      }
   }

   private void blendMatrix() {
      for (BufferAllocator var2 : this.summary.values()) {
         try {
            var2.close();
         } catch (RuntimeException var4) {
            data.warn("Failed to close capture layer allocator", var4);
         }
      }

      this.summary.clear();
   }

   private static boolean process(Framebuffer var0) {
      if (var0 == null) {
         return false;
      } else {
         return var0.getColorAttachment() instanceof GlTexture var2 ? var2.getGlId() > 0 : false;
      }
   }

   private static boolean compute(Framebuffer var0) {
      if (!process(var0)) {
         return false;
      } else if (!(var0 instanceof SimpleFramebuffer var1)) {
         return true;
      } else {
         GpuTextureView var2 = var1.getColorAttachmentView();
         if (var2 != null && !var2.isClosed()) {
            GpuTextureView var3 = var1.getDepthAttachmentView();
            return var3 == null || !var3.isClosed();
         } else {
            return false;
         }
      }
   }

   private static int resolve(Framebuffer var0) {
      if (var0 == null) {
         return 0;
      } else {
         return var0.getColorAttachment() instanceof GlTexture var1 ? var1.getGlId() : 0;
      }
   }

   private static int update(Framebuffer var0) {
      if (var0 == null) {
         return 0;
      } else {
         return var0.getDepthAttachment() instanceof GlTexture var1 ? var1.getGlId() : 0;
      }
   }

   static final class VertexEmitter implements AutoCloseable {
      private final BufferAllocator instance;
      private final SequencedMap<RenderLayer, BufferAllocator> data;
      private final Immediate context;
      private final VertexConsumerProvider config;
      private boolean state;

      VertexEmitter(BufferAllocator var1, SequencedMap<RenderLayer, BufferAllocator> var2) {
         this.instance = var1;
         this.data = var2;
         this.context = VertexConsumerProvider.immediate(var2, var1);
         this.config = this::handle;
      }

      VertexConsumerProvider handle() {
         return this.config;
      }

      VertexConsumer handle(RenderLayer var1) {
         this.data.computeIfAbsent(var1, var0 -> new BufferAllocator(Math.max(4096, Math.min(var0.getExpectedBufferSize(), 262144))));
         this.state = false;
         return this.context.getBuffer(var1);
      }

      MatrixStack handle(MatrixStack var1) {
         if (var1 == null) {
            return null;
         }

         MatrixStack var2 = new MatrixStack();
         Entry var3 = var1.peek();
         Entry var4 = var2.peek();
         var4.getPositionMatrix().set(var3.getPositionMatrix());
         var4.getNormalMatrix().set(var3.getNormalMatrix());
         return var2;
      }

      void process() {
         this.state = false;
      }

      void compute() {
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
}
