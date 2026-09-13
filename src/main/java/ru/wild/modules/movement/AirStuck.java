package ru.wild.modules.movement;

import net.minecraft.client.render.Camera;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;

@ModuleRegister(
   name = "AirStuck",
   category = ModuleCategory.Movement,
   description = "Позволяет застывать в воздухе",
   flags = {ModuleFlag.RISKY, ModuleFlag.PATCHED, ModuleFlag.GRIM}
)
public class AirStuck extends Module {
   public final BooleanSetting source = new BooleanSetting("Обход Grim", true);
   private Vec3d target = null;
   private boolean pending = false;

   public AirStuck() {
      this.handle(this.source);
   }

   @Override
   public void handle() {
      super.handle();
      if (Module.client.player != null) {
         this.target = Module.client.player.getPos();
      }

      this.pending = false;
   }

   @Override
   public void process() {
      this.target = null;
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && this.target != null) {
         Module.client.player.setVelocity(0.0, 0.0, 0.0);
         Module.client.player.setPosition(this.target);
         Module.client.player.lastX = this.target.x;
         Module.client.player.lastY = this.target.y;
         Module.client.player.lastZ = this.target.z;
         Module.client.player.fallDistance = 0.0;
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null && Module.client.gameRenderer != null && !this.pending) {
         if (var1.resolve() instanceof PlayerMoveC2SPacket && this.target != null) {
            if (this.source.compute()) {
               var1.process();
            } else {
               var1.process();
               Camera var2 = Module.client.gameRenderer.getCamera();
               this.pending = true;
               Module.client.getNetworkHandler().sendPacket(new Full(this.target.x, this.target.y, this.target.z, var2.getYaw(), var2.getPitch(), false, false));
               this.pending = false;
            }
         }
      }
   }
}
