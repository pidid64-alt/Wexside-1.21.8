package ru.wild.modules.visuals;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerLandingEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.JumpCircleRenderer;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.math.ScalarAnimator;

@ModuleRegister(name = "JumpCircle", description = "Красивый круг после прыжка", category = ModuleCategory.Visuals)
public class JumpCircle extends Module {
   private static final long latest = 250L;
   private static final long summary = 1500L;
   private static final long matrixBlend = 850L;
   private static final float vectorMatch = 1.95F;
   private static final float itemProject = 0.04F;
   public final BooleanSetting source = new BooleanSetting("Переливание", false);
   public final NumberSetting target = new NumberSetting("Скорость переливания", 1.0F, 0.1F, 3.0F, 0.05F, false);
   public final NumberSetting pending = new NumberSetting("Яркость", 1.0F, 0.25F, 2.0F, 0.05F, false);
   public final NumberSetting previous = new NumberSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.05F, true);
   private final List<JumpCircle.CacheEntry> responseCompute = new ArrayList<>();
   private long providerFetch;

   public JumpCircle() {
      this.handle(this.source, this.target, this.pending, this.previous);
      JumpCircleRenderer.handle();
   }

   @EventHandler
   public void handle(PlayerLandingEvent var1) {
      if (Module.client.player != null) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.providerFetch >= 250L) {
            this.providerFetch = var2;
            this.responseCompute.add(new JumpCircle.CacheEntry(Module.client.player.getPos().add(0.0, 0.04F, 0.0), var2));
         }
      }
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (!this.responseCompute.isEmpty()) {
         long var2 = System.currentTimeMillis();
         this.responseCompute.removeIf(var2x -> var2 - var2x.data > 1500L);
         if (!this.responseCompute.isEmpty()) {
            ThemePalette var4 = WildClient.instance != null && WildClient.instance.selection != null
               ? WildClient.instance.selection.process()
               : ThemePalette.WILD;
            int var5 = var4 == ThemePalette.WILD ? 8108031 : var4.handle().getRGB() & 16777215;
            int var6 = var5 >> 16 & 0xFF;
            int var7 = var5 >> 8 & 0xFF;
            int var8 = var5 & 0xFF;
            boolean var9 = this.source.compute();
            JumpCircleRenderer.handle(this.target.compute(), this.pending.compute(), this.previous.compute());
            Immediate var10 = WorldVertexBuffer.handle();

            try {
               VertexConsumer var11 = var10.getBuffer(var9 ? JumpCircleRenderer.compute() : JumpCircleRenderer.process());
               MatrixStack var12 = var1.compute();
               Vec3d var13 = Module.client.gameRenderer.getCamera().getPos();

               for (JumpCircle.CacheEntry var15 : this.responseCompute) {
                  long var16 = var2 - var15.data;
                  if (var16 >= 850L && !var15.cache) {
                     var15.state.handle(0.0, 0.65, EasingFunctions.selection);
                     var15.config.handle(1.35, 0.65, EasingFunctions.selection);
                     var15.cache = true;
                  }

                  var15.context.handle();
                  var15.config.handle();
                  var15.state.handle();
                  float var18 = Math.max(0.0F, Math.min(1.0F, (float)var15.context.check()));
                  float var19 = (float)var15.config.check();
                  float var20 = Math.max(0.0F, Math.min(1.0F, (float)var15.state.check()));
                  if (!(var20 <= 0.002F) && !(var19 <= 0.001F)) {
                     int var21 = (int)(var20 * var18 * 255.0F);
                     float var22 = 1.95F * var19 * var18;
                     var12.push();
                     var12.translate(var15.instance.x - var13.x, var15.instance.y - var13.y, var15.instance.z - var13.z);
                     var12.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                     this.handle(var11, var12.peek().getPositionMatrix(), var22, var6, var7, var8, var21);
                     var12.pop();
                  }
               }
            } finally {
               WorldVertexBuffer.process();
               JumpCircleRenderer.update();
            }
         }
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, int var4, int var5, int var6, int var7) {
      float var8 = var3 * 0.5F;
      var1.vertex(var2, -var8, -var8, 0.0F).texture(0.0F, 0.0F).color(var4, var5, var6, var7).normal(0.0F, 0.0F, 1.0F);
      var1.vertex(var2, var8, -var8, 0.0F).texture(1.0F, 0.0F).color(var4, var5, var6, var7).normal(0.0F, 0.0F, 1.0F);
      var1.vertex(var2, var8, var8, 0.0F).texture(1.0F, 1.0F).color(var4, var5, var6, var7).normal(0.0F, 0.0F, 1.0F);
      var1.vertex(var2, -var8, var8, 0.0F).texture(0.0F, 1.0F).color(var4, var5, var6, var7).normal(0.0F, 0.0F, 1.0F);
   }

   static final class CacheEntry {
      final Vec3d instance;
      final long data;
      final ScalarAnimator context = new ScalarAnimator();
      final ScalarAnimator config = new ScalarAnimator();
      final ScalarAnimator state = new ScalarAnimator();
      boolean cache;

      CacheEntry(Vec3d var1, long var2) {
         this.instance = var1;
         this.data = var2;
         this.context.handle(1.0, 0.28, EasingFunctions.selection);
         this.config.handle(1.0, 0.4, EasingFunctions.handler);
         this.state.handle(1.0, 0.18, EasingFunctions.handler);
      }
   }
}
