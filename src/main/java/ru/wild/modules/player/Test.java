package ru.wild.modules.player;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.util.math.ActionDelay;

@ModuleRoles(compute = {"lichoday", "bitrixtime", "oblamovvv"})
@ModuleRegister(name = "Test", category = ModuleCategory.Player, description = "...")
public class Test extends Module {
   private final KeybindSetting source = new KeybindSetting("Установка точки", -1);
   private static BlockPos target;
   private static BlockPos pending;
   private BlockPos[] previous;
   private int latest = 0;
   private int summary = 0;
   private final ActionDelay matrixBlend = new ActionDelay();

   public Test() {
      this.handle(this.source);
   }

   @Override
   public void handle() {
      this.tick();
      this.latest = 0;
      super.handle();
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (var1.resolve() == this.source.compute() && this.matrixBlend.resolve(300L)) {
         if (this.summary == 0) {
            target = Module.client.player.getBlockPos();
            pending = null;
            this.previous = null;
            this.handle("Точка 1: " + target.toShortString());
            this.summary = 1;
         } else if (this.summary == 1) {
            pending = Module.client.player.getBlockPos();
            this.handle("Точка 2: " + pending.toShortString());
            this.tick();
            this.summary = 2;
         } else {
            target = Module.client.player.getBlockPos();
            pending = null;
            this.previous = null;
            BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
            this.handle("Сброс. Точка 1: " + target.toShortString());
            this.summary = 1;
         }

         this.matrixBlend.handle();
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && this.previous != null && this.previous.length != 0) {
         IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
         this.handle(var2);
      }
   }

   private void handle(IBaritone var1) {
      BlockPos var2 = this.previous[this.latest];
      double var3 = Module.client.player.squaredDistanceTo(var2.getX() + 0.5, var2.getY(), var2.getZ() + 0.5);
      if (var3 < 2.0) {
         this.latest = (this.latest + 1) % this.previous.length;
         var2 = this.previous[this.latest];
      }

      var1.getCustomGoalProcess().setGoalAndPath(new GoalBlock(var2));
   }

   private void tick() {
      if (target != null && pending != null) {
         int var1 = Math.min(target.getX(), pending.getX());
         int var2 = Math.max(target.getX(), pending.getX());
         int var3 = Math.min(target.getZ(), pending.getZ());
         int var4 = Math.max(target.getZ(), pending.getZ());
         int var5 = (int)Module.client.player.getY();
         this.previous = new BlockPos[]{
            new BlockPos(var1, var5, var3), new BlockPos(var2, var5, var3), new BlockPos(var2, var5, var4), new BlockPos(var1, var5, var4)
         };
      }
   }

   private void handle(String var1) {
      if (Module.client.player != null) {
         Module.client.player.sendMessage(Text.of("§7[§bTestModule§7] §f" + var1), false);
      }
   }

   @Override
   public void process() {
      BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      super.process();
   }
   public static BlockPos refresh() {
      return target;
   }
   public static void handle(BlockPos var0) {
      target = var0;
   }
   public static BlockPos render() {
      return pending;
   }
   public static void process(BlockPos var0) {
      pending = var0;
   }
}
