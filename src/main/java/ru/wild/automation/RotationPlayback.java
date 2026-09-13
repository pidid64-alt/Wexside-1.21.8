package ru.wild.automation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import ru.wild.util.player.RotationAngles;

public final class RotationPlayback {
   private int instance = -1;

   void handle(RotationAngles var1, LivingEntity var2, float var3, float var4) {
      if (var2.getId() != this.instance) {
         this.handle(var2.getId());
      }

      float var5 = var1.instance + var3;
      float var6 = MathHelper.clamp(var1.data + var4, -90.0F, 90.0F);
      RotationController.handle(new RotationAngles(var5, var6), Math.abs(var3), Math.abs(var4), 20.0F, 20.0F, 1, 15, false);
   }

   public void handle(RotationAngles var1, float var2, float var3, int var4, int var5) {
      this.handle(var1, var2, var3, 20.0F, 20.0F, var4, var5);
   }

   public void handle(RotationAngles var1, float var2, float var3, float var4, float var5, int var6, int var7) {
      this.handle(-1);
      RotationController.handle(var1, var2, var3, var4, var5, var6, var7, false);
   }

   public void handle() {
      this.handle(-1);
   }

   private void handle(int var1) {
      this.instance = var1;
   }
}
