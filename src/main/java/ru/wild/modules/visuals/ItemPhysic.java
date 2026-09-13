package ru.wild.modules.visuals;

import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.module.ModuleCategory;
import ru.wild.render.ItemGroundStateCarrier;

@ModuleRegister(name = "ItemPhysic", category = ModuleCategory.Visuals, description = "Рендерит предметы лежащими на поверхности")
public class ItemPhysic extends Module {
   private static final float source = 90.0F;
   private static final float target = 0.0F;
   private static final float pending = 22.0F;
   private static boolean previous;

   @Override
   public void handle() {
      super.handle();
      previous = true;
   }

   @Override
   public void process() {
      super.process();
      previous = false;
   }

   public static boolean handle(ItemEntityRenderState var0) {
      return previous && var0 instanceof ItemGroundStateCarrier var1 && var1.wild$isItemPhysicOnGround();
   }

   public static boolean process(ItemEntityRenderState var0) {
      return previous && var0 instanceof ItemGroundStateCarrier var1 && !var1.wild$isItemPhysicOnGround();
   }

   public static float refresh() {
      return 0.0F;
   }

   public static float render() {
      return 90.0F;
   }

   public static float handle(float var0) {
      return var0 * 22.0F;
   }
}
