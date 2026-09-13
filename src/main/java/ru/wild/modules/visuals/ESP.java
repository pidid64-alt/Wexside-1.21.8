package ru.wild.modules.visuals;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.core.manager.FriendManager;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.render.ColoredGeometryEmitter;
import ru.wild.util.render.PackedColor;

@ModuleRegister(name = "ESP", description = "Подцветка игроков", category = ModuleCategory.Visuals)
public class ESP extends Module {
   private static final int latest = 2048;
   private static final int summary = 96;
   private static final int matrixBlend = PackedColor.compute(52, 255, 96, 255);
   public final ChoiceSetting source = new ChoiceSetting("Targets", new BooleanSetting("Players", true), new BooleanSetting("Mobs", true));
   public final NumberSetting target = new NumberSetting("Distance", 72.0F, 8.0F, 200.0F, 1.0F, false);
   public final BooleanSetting pending = new BooleanSetting("Fill", true);
   public final BooleanSetting previous = new BooleanSetting("Outline", true);
   private final List<Entity> vectorMatch = new ArrayList<>(96);
   private final int[] itemProject = new int[4];
   private static final RenderPipeline responseCompute = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("minecraft", "rendertype_lequal_depth_test"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderPipeline providerFetch = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("minecraft", "rendertype_lines"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer profileDraw = RenderLayer.of(
      "litka_esp_fill", 2048, false, true, responseCompute, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer vectorPerform = RenderLayer.of(
      "litka_esp_line", 2048, false, true, providerFetch, MultiPhaseParameters.builder().lineWidth(new LineWidth(OptionalDouble.of(2.2))).build(false)
   );

   public ESP() {
      this.handle(this.source, this.target, this.pending, this.previous);
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null) {
         List<Entity> var2 = this.refresh();
         if (!var2.isEmpty()) {
            Immediate var3 = WorldVertexBuffer.handle();

            try {
               for (Entity var5 : var2) {
                  this.handle(var1.compute(), var3, var5, var1.resolve());
               }
            } finally {
               WorldVertexBuffer.process();
            }
         }
      }
   }

   private List<Entity> refresh() {
      this.vectorMatch.clear();
      float var1 = this.target.compute() * this.target.compute();
      boolean var2 = this.source.process("Players");
      boolean var3 = this.source.process("Mobs");

      for (Entity var5 : Module.client.world.getEntities()) {
         if (this.vectorMatch.size() >= 96) {
            break;
         }

         if (this.handle(var5, var2, var3) && !(Module.client.player.squaredDistanceTo(var5) > var1)) {
            this.vectorMatch.add(var5);
         }
      }

      return this.vectorMatch;
   }

   private boolean handle(Entity var1, boolean var2, boolean var3) {
      if (var1 != null && var1 != Module.client.player) {
         if (!(var1 instanceof LivingEntity var4 && var4.isAlive())) {
            return false;
         } else {
            return var1 instanceof PlayerEntity ? var2 : var3;
         }
      } else {
         return false;
      }
   }

   private void handle(MatrixStack var1, Immediate var2, Entity var3, float var4) {
      Vec3d var5 = Module.client.gameRenderer.getCamera().getPos();
      Vec3d var6 = this.handle(var3, var4);
      Box var7 = var3.getBoundingBox().offset(var6.x - var3.getX(), var6.y - var3.getY(), var6.z - var3.getZ());
      float var8 = var3 instanceof PlayerEntity ? 0.09F : 0.06F;
      Box var9 = var7.expand(var8).offset(-var5.x, -var5.y, -var5.z);
      int var10 = this.process(var3);
      float var11 = this.handle(var3);
      int var12 = PackedColor.apply(var10, 0.92F);
      int var13 = PackedColor.update(var10, 0.62F);
      int var14 = PackedColor.update(var10, 0.8F);
      int[] var15 = this.itemProject;
      var15[0] = PackedColor.process(var12, var14, 0, 10);
      var15[1] = PackedColor.process(var14, var13, 90, 10);
      var15[2] = PackedColor.process(var13, var14, 180, 10);
      var15[3] = PackedColor.process(var14, var12, 270, 10);
      Matrix4f var16 = var1.peek().getPositionMatrix();
      if (this.pending.compute()) {
         int var17 = (int)(MathHelper.clamp(var11, 0.1F, 1.0F) * 95.0F);
         VertexConsumer var18 = var2.getBuffer(profileDraw);
         ColoredGeometryEmitter.handle(var18, var16, var9.minX, var9.minY, var9.minZ, var9.maxX, var9.maxY, var9.maxZ, var15, var17);
      }

      if (this.previous.compute()) {
         int var19 = (int)(MathHelper.clamp(var11, 0.2F, 1.0F) * 255.0F);
         VertexConsumer var20 = var2.getBuffer(vectorPerform);
         ColoredGeometryEmitter.handle(var20, var16, var9.minX, var9.minY, var9.minZ, var9.maxX, var9.maxY, var9.maxZ, var15, var19, 0.18, 0.06);
      }
   }

   private Vec3d handle(Entity var1, float var2) {
      double var3 = MathHelper.lerp(var2, var1.lastRenderX, var1.getX());
      double var5 = MathHelper.lerp(var2, var1.lastRenderY, var1.getY());
      double var7 = MathHelper.lerp(var2, var1.lastRenderZ, var1.getZ());
      return new Vec3d(var3, var5, var7);
   }

   private float handle(Entity var1) {
      float var2 = Module.client.player.distanceTo(var1);
      float var3 = Math.max(this.target.compute(), 1.0F);
      return 1.0F - MathHelper.clamp(var2 / var3, 0.0F, 1.0F);
   }

   private int process(Entity var1) {
      if (var1 instanceof PlayerEntity var2) {
         String var3 = var2.getGameProfile() != null ? var2.getGameProfile().getName() : var2.getName().getString();
         return FriendManager.handle(var3) ? matrixBlend : PackedColor.unload(var2.getId() * 17);
      } else {
         return PackedColor.unload(var1.getId() * 11);
      }
   }
}
