package ru.wild.modules.visuals;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.NumberSetting;
import ru.wild.render.shader.MotionBlurRenderer;

@ModuleRegister(
   name = "MotionBlur",
   category = ModuleCategory.Visuals,
   description = "Физически основанный MotionBlur, очень сильно повышает плавность картинки.",
   flags = ModuleFlag.NEW
)
public final class MotionBlur extends Module {
   public final NumberSetting source = new NumberSetting("Плавность", 0.68F, 0.0F, 1.0F, 0.01F, true);

   public MotionBlur() {
      this.handle(this.source);
   }

   @Override
   public void handle() {
      MotionBlurRenderer.handle().process();
      super.handle();
   }

   @Override
   public void process() {
      super.process();
      MotionBlurRenderer.handle().process();
   }

   public MotionBlurRenderer.CacheEntry refresh() {
      float var1 = Math.max(0.0F, Math.min(1.0F, this.source.compute()));
      float var2 = var1 * var1 * (3.0F - 2.0F * var1);
      MotionBlurRenderer.CacheEntry var3 = new MotionBlurRenderer.CacheEntry();
      var3.instance = 0.38F + var2 * 0.92F;
      var3.data = 1.1F + var2 * 2.25F;
      var3.context = 5 + Math.round(var2 * 7.0F);
      var3.config = 24.0F + var2 * 82.0F;
      var3.state = 0.3F + var2 * 0.42F;
      var3.cache = 0.22F + var2 * 1.12F;
      var3.output = 0.68F;
      var3.current = 2.85F - var2 * 1.35F;
      var3.active = 0.035F + (1.0F - var2) * 0.075F;
      return var3;
   }
}
