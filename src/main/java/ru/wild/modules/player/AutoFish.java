package ru.wild.modules.player;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.util.math.Stopwatch;

@ModuleRegister(name = "AutoFish", description = "Ловит за вас карасей", category = ModuleCategory.Player)
public class AutoFish extends Module {
   public static Stopwatch source = new Stopwatch();
   public static boolean target = false;
   public static boolean pending = false;

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         if (source.compute(600.0) && target) {
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            target = false;
            pending = true;
            source.handle();
         }

         if (source.compute(300.0) && pending) {
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            pending = false;
            source.handle();
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof PlaySoundS2CPacket var2) {
         if (var2.getSound().value() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) {
            FishingBobberEntity var12 = Module.client.player.fishHook;
            if (var12 != null) {
               double var4 = var2.getX() - var12.getX();
               double var6 = var2.getY() - var12.getY();
               double var8 = var2.getZ() - var12.getZ();
               double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
               if (var10 <= 0.5) {
                  target = true;
                  source.handle();
               }
            }
         }
      }
   }
}
