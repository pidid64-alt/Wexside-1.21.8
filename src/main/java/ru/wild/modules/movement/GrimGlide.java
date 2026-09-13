package ru.wild.modules.movement;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EntityVelocityEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(
   name = "GrimGlide",
   category = ModuleCategory.Movement,
   description = "Обход Grim при полёте на элитрах",
   flags = {ModuleFlag.RISKY, ModuleFlag.GRIM}
)
public class GrimGlide extends Module {
   public final NumberSetting source = new NumberSetting("Скорость", 1.0F, 1.0F, 2.0F, 0.05F, false);
   private int target;
   private boolean pending;
   private boolean previous;

   public GrimGlide() {
      this.handle(this.source);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null && !this.previous) {
         if (!var1.compute() && var1.resolve() instanceof PlayerPositionLookS2CPacket) {
            this.target = 2;
            this.pending = true;
         }

         if (var1.compute() && var1.resolve() instanceof PlayerMoveC2SPacket) {
            if (Module.client.player.isGliding() && this.target == 0 && !this.pending) {
               this.previous = true;
               Module.client.getNetworkHandler().sendPacket(new OnGroundOnly(true, true));
               this.previous = false;
               var1.process();
            }

            this.pending = false;
         }
      }
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (Module.client.player != null) {
         if (this.target > 0) {
            this.target--;
         }
      }
   }

   @EventHandler
   public void handle(EntityVelocityEvent var1) {
      if (Module.client.player != null) {
         Vec3d var2 = Module.client.player.getVelocity();
         Vec3d var3 = Module.client.player.getRotationVector();
         float var4 = Module.client.player.getPitch() * (float) (Math.PI / 180.0);
         double var5 = Math.sqrt(var3.x * var3.x + var3.z * var3.z);
         double var7 = var2.horizontalLength();
         boolean var9 = Module.client.player.getVelocity().y <= 0.0;
         double var10 = var9 && Module.client.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
            ? Math.min(Module.client.player.getFinalGravity(), 0.01)
            : Module.client.player.getFinalGravity();
         double var12 = MathHelper.square(Math.cos(var4));
         var2 = var2.add(0.0, var10 * (-1.0 + var12 * 0.75), 0.0);
         if (var2.y < 0.0 && var5 > 0.0) {
            double var14 = var2.y * -0.1 * var12;
            var2 = var2.add(var3.x * var14 / var5, var14, var3.z * var14 / var5);
         }

         if (var4 < 0.0F && var5 > 0.0) {
            double var26 = var7 * -MathHelper.sin(var4) * 0.04F;
            var2 = var2.add(-var3.x * var26 / var5, var26 * 3.2, -var3.z * var26 / var5);
         }

         if (var5 > 0.0) {
            var2 = var2.add((var3.x / var5 * var7 - var2.x) * 0.1, 0.0, (var3.z / var5 * var7 - var2.z) * 0.1);
         }

         double var16 = Math.toRadians(Module.client.player.getYaw());
         double var18 = -Math.sin(var16);
         double var20 = Math.cos(var16);
         float var22 = this.source.compute();
         if (this.target >= 1) {
            double var23 = 0.09F * var22;
            var1.handle(var2.multiply(0.99F, 0.98F, 0.99F).add(var18 * var23, 0.03F * var22, var20 * var23));
         } else {
            float var27 = MathHelper.clamp(0.3F * var22, 0.3F, 0.85F);
            var1.handle(var2.multiply(var27, var27, var27));
         }
      }
   }
}
