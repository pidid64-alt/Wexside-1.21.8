package ru.wild.core;

import java.util.Locale;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.automation.combat.AuraRotationPlanner;
import ru.wild.util.player.MouseSensitivityQuantizer;

public class RotationDebugProbe extends ClientComponent {
   public static boolean instance = false;
   public static boolean data = false;
   private static float context;
   private static float config;
   private static boolean state;

   @EventHandler
   public void handle(PacketEvent var1) {
      if (instance && var1.compute() && !var1.handle()) {
         if (var1.resolve() instanceof PlayerMoveC2SPacket var2 && var2.changesLook()) {
            String var9 = AuraRotationPlanner.compute();
            if (!data || !"IDLE".equals(var9)) {
               float var4 = var2.getYaw(context);
               float var5 = var2.getPitch(config);
               float var6 = state ? var4 - context : 0.0F;
               float var7 = state ? var5 - config : 0.0F;
               float var8 = MouseSensitivityQuantizer.handle();
               System.out
                  .printf(
                     Locale.ROOT,
                     "[WildRot] %-6s yaw=%9.4f pitch=%8.4f dYaw=%+9.4f dPitch=%+8.4f steps=%+d/%+d gcd=%.6f onGround=%b camera=%.4f/%.4f%n",
                     var9,
                     var4,
                     var5,
                     var6,
                     var7,
                     Math.round(var6 / var8),
                     Math.round(var7 / var8),
                     var8,
                     var2.isOnGround(),
                     ViewRotationCoordinator.context,
                     ViewRotationCoordinator.config
                  );
               context = var4;
               config = var5;
               state = true;
            }
         }
      }
   }
}
