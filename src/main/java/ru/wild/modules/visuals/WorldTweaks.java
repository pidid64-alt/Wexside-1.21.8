package ru.wild.modules.visuals;

import java.awt.Color;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.WorldColorSetting;
import ru.wild.render.shader.WorldEffectsRenderer;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(
   name = "WorldTweaks",
   category = ModuleCategory.Visuals,
   description = "Кинематографичная атмосфера: ветер, туман, тонировка неба",
   flags = ModuleFlag.NEW
)
public final class WorldTweaks extends Module {
   public final NumberSetting source = new NumberSetting("Wind Speed", 0.72F, 0.0F, 2.0F, 0.01F, false);
   public final NumberSetting target = new NumberSetting("Wind Direction", 35.0F, 0.0F, 360.0F, 1.0F, false);
   public final NumberSetting pending = new NumberSetting("Fog Density", 0.032F, 0.0F, 0.1F, 0.001F, false);
   public final NumberSetting previous = new NumberSetting("Horizon Dissolve", 0.82F, 0.0F, 1.0F, 0.01F, true);
   public final NumberSetting latest = new NumberSetting("Sky Lift", 0.64F, 0.0F, 1.0F, 0.01F, true);
   public final NumberSetting summary = new NumberSetting("Edge Softness", 0.72F, 0.0F, 1.0F, 0.01F, true);
   public final WorldColorSetting matrixBlend = new WorldColorSetting("Atmosphere Tint", 6978453);

   public WorldTweaks() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary, this.matrixBlend);
   }

   public static boolean refresh() {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         WorldTweaks var0 = WildClient.instance.data.handle(WorldTweaks.class);
         return var0 != null && var0.enabled;
      } else {
         return false;
      }
   }

   @EventHandler(handle = 0)
   public void handle(WorldRenderContext var1) {
      if (var1 != null && var1.compute() != null && var1.compute().world != null && var1.compute().player != null && var1.update() != null) {
         WorldEffectsRenderer.AnimationState var2 = new WorldEffectsRenderer.AnimationState();
         Color var3 = this.matrixBlend.compute();
         float var4 = (float)Math.toRadians(this.target.compute());
         var2.instance = this.source.compute();
         var2.data = (float)Math.cos(var4);
         var2.context = 0.0F;
         var2.config = (float)Math.sin(var4);
         var2.state = this.pending.compute();
         var2.cache = this.previous.compute();
         var2.output = this.latest.compute();
         var2.current = this.summary.compute();
         var2.active = var3.getRed() / 255.0F;
         var2.mode = var3.getGreen() / 255.0F;
         var2.selection = var3.getBlue() / 255.0F;
         var2.animationDraw = ((float)var1.compute().world.getTime() + var1.check()) * 0.05F;
         WorldEffectsRenderer.handle().handle(var1.compute(), var1.update().handle(), var1.execute(), var1.prepare(), var2);
      }
   }
}
