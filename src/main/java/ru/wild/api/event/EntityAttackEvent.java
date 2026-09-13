package ru.wild.api.event;
import net.minecraft.entity.Entity;

public class EntityAttackEvent extends Event {
   private Entity instance;
   public Entity compute() {
      return this.instance;
   }
   public void handle(Entity var1) {
      this.instance = var1;
   }
   public EntityAttackEvent(Entity var1) {
      this.instance = var1;
   }
}
