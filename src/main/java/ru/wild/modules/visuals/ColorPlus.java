package ru.wild.modules.visuals;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.render.shader.SkyStylePresets;

@ModuleRegister(name = "ColorPlus", category = ModuleCategory.Visuals, description = "Цветокоррекция мира — пресеты и тонкая настройка")
public class ColorPlus extends Module {
   public final ModeSetting source = new ModeSetting("Пресет", "Cinematic", SkyStylePresets.handle());
   public final NumberSetting target = new NumberSetting("Сила эффекта", 1.0F, 0.0F, 1.0F, 0.01F, true);
   public final BooleanSetting pending = new BooleanSetting("Резкость (CAS)", true);
   public final NumberSetting previous = new NumberSetting("Экспозиция", 0.0F, -0.5F, 0.5F, 0.01F, false);
   public final NumberSetting latest = new NumberSetting("Контраст", 0.0F, -0.5F, 0.5F, 0.01F, false);
   public final NumberSetting summary = new NumberSetting("Насыщенность", 0.0F, -0.5F, 0.5F, 0.01F, false);
   public final NumberSetting matrixBlend = new NumberSetting("Vibrance", 0.0F, -0.5F, 0.5F, 0.01F, false);
   public final NumberSetting vectorMatch = new NumberSetting("Гамма", 0.0F, -0.5F, 0.5F, 0.01F, false);
   public final NumberSetting itemProject = new NumberSetting("Температура", 0.0F, -0.5F, 0.5F, 0.01F, false);
   public final NumberSetting responseCompute = new NumberSetting("Оттенок (зелёный/маджента)", 0.0F, -0.5F, 0.5F, 0.01F, false);
   public final NumberSetting providerFetch = new NumberSetting("Интенсивность Bloom", 0.0F, -0.3F, 0.3F, 0.01F, false);
   public final NumberSetting profileDraw = new NumberSetting("Сила резкости", 0.0F, -0.3F, 0.3F, 0.01F, false);
   public final NumberSetting vectorPerform = new NumberSetting("Виньетка", 0.0F, -0.3F, 0.3F, 0.01F, false);

   public ColorPlus() {
      this.handle(
         this.source,
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform
      );
   }

   public SkyStylePresets refresh() {
      return SkyStylePresets.handle(this.source.compute());
   }
}
