package ru.wild.modules.player;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(name = "FastBreak", category = ModuleCategory.Player, description = "Быстрая поломка блоков")
public class FastBreak extends Module {
   public final NumberSetting source = new NumberSetting("Скорость", 0.5F, 0.1F, 1.0F, 0.1F, false);

   public FastBreak() {
      this.handle(this.source);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (Module.client.options.attackKey.isPressed()) {
            if (Module.client.crosshairTarget instanceof BlockHitResult var2) {
               BlockPos var9 = var2.getBlockPos();
               BlockState var4 = Module.client.world.getBlockState(var9);
               if (var4 != null && !var4.isAir()) {
                  Direction var5 = var2.getSide();
                  float var6 = this.source.compute();
                  if (var6 > 4.0F) {
                     Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var9, var5));
                     Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var9, var5));
                     Module.client.player.swingHand(Hand.MAIN_HAND);
                     Module.client.interactionManager.cancelBlockBreaking();
                  } else {
                     int var7 = (int)(var6 * 50.85F);

                     for (int var8 = 0; var8 < var7; var8++) {
                        Module.client.interactionManager.updateBlockBreakingProgress(var9, var5);
                     }

                     Module.client.player.swingHand(Hand.MAIN_HAND);
                  }
               }
            }
         }
      }
   }
}
