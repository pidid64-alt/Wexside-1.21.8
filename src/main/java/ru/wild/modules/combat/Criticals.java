package ru.wild.modules.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.Mutable;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EntityAttackEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.util.player.LocalhostHelper;

@ModuleRegister(
   name = "Criticals",
   description = "Критический удар под плавным падением или в паутине",
   category = ModuleCategory.Combat,
   flags = {ModuleFlag.RISKY, ModuleFlag.GRIM}
)
public class Criticals extends Module {
   public static boolean source;
   public final ChoiceSetting target = new ChoiceSetting("Условия", new BooleanSetting("Паутина", true), new BooleanSetting("Плавное падение", true));

   public Criticals() {
      this.handle(this.target);
   }

   @EventHandler
   public void handle(EntityAttackEvent var1) {
      if (!source) {
         if (!LocalhostHelper.handle() && Module.client.player != null && Module.client.world != null) {
            if (Module.client.player.networkHandler != null) {
               if (!Module.client.player.isGliding()) {
                  Entity var2 = var1.compute();
                  if (var2 != null && var2 != Module.client.player && !(var2 instanceof EndCrystalEntity)) {
                     boolean var3 = this.target.process("Паутина") && this.refresh();
                     boolean var4 = this.target.process("Плавное падение") && Module.client.player.hasStatusEffect(StatusEffects.SLOW_FALLING);
                     if (var3 || var4) {
                        float var5 = MathHelper.lerp(ThreadLocalRandom.current().nextFloat(), 1.0E-7F, 1.0E-6F);
                        Module.client.player.fallDistance = var5;
                        Module.client.player
                           .networkHandler
                           .sendPacket(
                              new Full(
                                 Module.client.player.getX(),
                                 Module.client.player.getY() - var5,
                                 Module.client.player.getZ(),
                                 Module.client.player.getYaw(),
                                 Module.client.player.getPitch(),
                                 false,
                                 Module.client.player.horizontalCollision
                              )
                           );
                     }
                  }
               }
            }
         }
      }
   }

   private boolean refresh() {
      Box var1 = Module.client.player.getBoundingBox().contract(1.0E-7);
      int var2 = MathHelper.floor(var1.minX);
      int var3 = MathHelper.floor(var1.maxX);
      int var4 = MathHelper.floor(var1.minY);
      int var5 = MathHelper.floor(var1.maxY);
      int var6 = MathHelper.floor(var1.minZ);
      int var7 = MathHelper.floor(var1.maxZ);
      Mutable var8 = new Mutable();

      for (int var9 = var2; var9 <= var3; var9++) {
         for (int var10 = var4; var10 <= var5; var10++) {
            for (int var11 = var6; var11 <= var7; var11++) {
               var8.set(var9, var10, var11);
               if (Module.client.world.getBlockState(var8).isOf(Blocks.COBWEB)) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
