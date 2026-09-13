package ru.wild.api.event;
import net.minecraft.util.math.Vec3d;

public class EntityVelocityEvent extends Event {
   private Vec3d instance;

   public EntityVelocityEvent(Vec3d var1) {
      this.instance = var1;
   }
   public Vec3d compute() {
      return this.instance;
   }
   public void handle(Vec3d var1) {
      this.instance = var1;
   }
}
