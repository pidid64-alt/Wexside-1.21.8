package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import java.util.List;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.util.math.ResettableTimer;

@ModuleRegister(name = "CreeperFarm", category = ModuleCategory.Misc, description = "Автоматический фарм криперов")
public class CreeperFarm extends Module {
   private static BlockPos source;
   private static BlockPos target;
   private static final double pending = 3.5;
   private static final double previous = 15.0;
   private static final double latest = 4.0;
   private static final long summary = 500L;
   private CreeperFarm.Mode matrixBlend = CreeperFarm.Mode.SEARCH;
   private final ResettableTimer vectorMatch = new ResettableTimer();
   private BlockPos[] itemProject;
   private int responseCompute = 0;

   public static void refresh() {
      source = null;
      target = null;
   }

   @Override
   public void handle() {
      if (Module.client.player != null && Module.client.world != null) {
         Settings var1 = BaritoneAPI.getSettings();
         var1.allowPlace.value = false;
         var1.allowBreak.value = false;
         var1.legitMine.value = true;
         this.encodePoint();
         this.responseCompute = 0;
         this.matrixBlend = CreeperFarm.Mode.SEARCH;
         this.vectorMatch.handle();
         super.handle();
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && source != null && target != null) {
         IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
         if (PlayerHelper.refresh()) {
            var2.getPathingBehavior().cancelEverything();
         } else {
            CreeperEntity var3 = this.load();
            if (var3 != null) {
               Vec3d var9 = Module.client.player.getPos().subtract(var3.getPos()).normalize();
               Vec3d var11 = Module.client.player.getPos().add(var9.multiply(15.0));
               this.matrixBlend = CreeperFarm.Mode.RETREAT;
               this.handle(var2, var11);
               this.handle(var11);
            } else {
               ItemEntity var4 = this.drawAnimation();
               if (var4 != null) {
                  this.matrixBlend = CreeperFarm.Mode.LOOTING;
                  Vec3d var10 = var4.getPos();
                  this.handle(var2, var10);
                  this.handle(var10);
               } else {
                  CreeperEntity var5 = this.save();
                  if (var5 != null) {
                     double var6 = Module.client.player.distanceTo(var5);
                     if (var6 <= 3.5) {
                        this.matrixBlend = CreeperFarm.Mode.ATTACK;
                        var2.getPathingBehavior().cancelEverything();
                        if (this.vectorMatch.handle(500.0)) {
                           Module.client.interactionManager.attackEntity(Module.client.player, var5);
                           Module.client.player.swingHand(Hand.MAIN_HAND);
                           this.vectorMatch.handle();
                        }
                     } else {
                        this.matrixBlend = CreeperFarm.Mode.APPROACH;
                        Vec3d var8 = var5.getPos();
                        this.handle(var2, var8);
                        this.handle(var8);
                     }
                  } else {
                     this.handle(var2);
                  }
               }
            }
         }
      }
   }

   private ItemEntity drawAnimation() {
      ItemEntity var1 = this.handle(Items.GUNPOWDER);
      if (var1 == null) {
         var1 = this.handle(Items.EXPERIENCE_BOTTLE);
      }

      if (var1 != null) {
         List<CreeperEntity> var2 = Module.client.world.getEntitiesByClass(CreeperEntity.class, var1.getBoundingBox().expand(4.0), var0 -> true);
         if (var2.isEmpty()) {
            return var1;
         }
      }

      return null;
   }

   private void handle(IBaritone var1) {
      if (this.itemProject != null && this.itemProject.length != 0) {
         BlockPos var2 = this.itemProject[this.responseCompute];
         double var3 = Module.client.player.squaredDistanceTo(var2.getX() + 0.5, var2.getY(), var2.getZ() + 0.5);
         if (var3 < 2.0) {
            this.responseCompute = (this.responseCompute + 1) % this.itemProject.length;
            var2 = this.itemProject[this.responseCompute];
         }

         this.matrixBlend = CreeperFarm.Mode.PATROL;
         var1.getCustomGoalProcess().setGoalAndPath(new GoalBlock(var2));
         this.handle(new Vec3d(var2.getX() + 0.5, var2.getY(), var2.getZ() + 0.5));
      }
   }

   private void encodePoint() {
      if (source != null && target != null) {
         int var1 = Math.min(source.getX(), target.getX());
         int var2 = Math.max(source.getX(), target.getX());
         int var3 = Math.min(source.getZ(), target.getZ());
         int var4 = Math.max(source.getZ(), target.getZ());
         int var5 = (int)Module.client.player.getY();
         this.itemProject = new BlockPos[]{
            new BlockPos(var1, var5, var3), new BlockPos(var2, var5, var3), new BlockPos(var2, var5, var4), new BlockPos(var1, var5, var4)
         };
      }
   }

   private void handle(IBaritone var1, Vec3d var2) {
      var1.getCustomGoalProcess().setGoalAndPath(new GoalXZ((int)var2.x, (int)var2.z));
   }

   private void handle(Vec3d var1) {
      if (var1 != null) {
         double var2 = var1.x - Module.client.player.getX();
         double var4 = var1.z - Module.client.player.getZ();
         float var6 = Math.abs(var2) > Math.abs(var4) ? (var2 > 0.0 ? -90.0F : 90.0F) : (var4 > 0.0 ? 0.0F : 180.0F);
         float var7 = var6 + (float)(Math.random() * 4.0 - 2.0);
      }
   }

   private void animate() {
      BlockPos var1 = BlockPos.ofFloored(
         Module.client.player.getX(), Module.client.player.getY() + Module.client.player.getStandingEyeHeight(), Module.client.player.getZ()
      );
      if ((
            Module.client.world.getBlockState(var1).getBlock() instanceof TrapdoorBlock
               || Module.client.world.getBlockState(var1.up()).getBlock() instanceof TrapdoorBlock
         )
         && Module.client.player.isOnGround()) {
      }
   }

   private CreeperEntity load() {
      for (CreeperEntity var3 : Module.client.world.getEntitiesByClass(CreeperEntity.class, Module.client.player.getBoundingBox().expand(15.0), var0 -> true)) {
         if (var3.isAlive() && var3.getFuseSpeed() > 0) {
            return var3;
         }
      }

      return null;
   }

   private CreeperEntity save() {
      Box var1 = Box.enclosing(source, target).expand(1.0);
      List<CreeperEntity> var2 = Module.client.world.getEntitiesByClass(CreeperEntity.class, var1, var0 -> true);
      CreeperEntity var3 = null;
      double var4 = Double.MAX_VALUE;

      for (CreeperEntity var7 : var2) {
         if (var7.isAlive()) {
            double var8 = Module.client.player.distanceTo(var7);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }
      }

      return var3;
   }

   private ItemEntity handle(Item var1) {
      Box var2 = Box.enclosing(source, target).expand(1.0);
      List<ItemEntity> var3 = Module.client.world.getEntitiesByClass(ItemEntity.class, var2, var0 -> true);
      ItemEntity var4 = null;
      double var5 = Double.MAX_VALUE;

      for (ItemEntity var8 : var3) {
         if (var8.isAlive() && var8.getStack().getItem() == var1) {
            double var9 = Module.client.player.distanceTo(var8);
            if (var9 < var5) {
               var5 = var9;
               var4 = var8;
            }
         }
      }

      return var4;
   }

   @Override
   public void process() {
      BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      Settings var1 = BaritoneAPI.getSettings();
      var1.allowPlace.value = true;
      var1.allowBreak.value = true;
      var1.legitMine.value = false;
      super.process();
   }
   public static BlockPos render() {
      return source;
   }
   public static void handle(BlockPos var0) {
      source = var0;
   }
   public static BlockPos tick() {
      return target;
   }
   public static void process(BlockPos var0) {
      target = var0;
   }

   enum Mode {
      SEARCH,
      APPROACH,
      ATTACK,
      RETREAT,
      PATROL,
      LOOTING;
   }
}
