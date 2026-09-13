package ru.wild.util.player;

import net.minecraft.entity.Entity;
import org.joml.Vector2f;
import ru.wild.core.MinecraftContext;
import ru.wild.util.math.NumericTransform;

public class RotationAngles implements MinecraftContext {
   public float instance;
   public float data;

   public RotationAngles(Entity var1) {
      this.instance = var1.getYaw();
      this.data = var1.getPitch();
   }

   public RotationAngles(float var1, float var2) {
      this.instance = var1;
      this.data = var2;
   }

   public float handle(RotationAngles var1) {
      float var2 = NumericTransform.execute(var1.instance - this.instance);
      float var3 = var1.data - this.data;
      return (float)Math.hypot(Math.abs(var2), Math.abs(var3));
   }

   public double process(RotationAngles var1) {
      double var2 = NumericTransform.execute(var1.instance - this.instance);
      double var4 = NumericTransform.execute(var1.data - this.data);
      return Math.hypot(var2, var4);
   }

   public static Vector2f handle() {
      return new Vector2f(process(), compute());
   }

   public static float process() {
      return NumericTransform.execute(toggleState.gameRenderer.getCamera().getYaw() + (toggleState.gameRenderer.getCamera().isThirdPerson() ? 180 : 0));
   }

   public static float compute() {
      return (toggleState.gameRenderer.getCamera().isThirdPerson() ? -1 : 1) * toggleState.gameRenderer.getCamera().getPitch();
   }
}
