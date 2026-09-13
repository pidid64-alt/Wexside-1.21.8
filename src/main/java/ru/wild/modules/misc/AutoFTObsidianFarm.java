package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(
   name = "AutoFTObsidianFarm",
   category = ModuleCategory.Misc,
   description = "Лаваход + бур: проходит лаву и чистит обсидиановый след зонами 3x3"
)
public final class AutoFTObsidianFarm extends Module {
   private final NumberSetting source = new NumberSetting("Дистанция хода", 100.0F, 10.0F, 500.0F, 1.0F, false);
   private final NumberSetting target = new NumberSetting("Отступ", 3.0F, 1.0F, 6.0F, 1.0F, false);
   private final NumberSetting pending = new NumberSetting("Задержка (мс)", 100.0F, 0.0F, 1000.0F, 10.0F, false);
   private AutoFTObsidianFarm.Mode previous = AutoFTObsidianFarm.Mode.IDLE;
   private AutoFTObsidianFarm.PrimaryMode latest = AutoFTObsidianFarm.PrimaryMode.FIND;
   private Direction summary = Direction.NORTH;
   private BlockPos matrixBlend;
   private BlockPos vectorMatch;
   private BlockPos itemProject;
   private BlockPos responseCompute;
   private BlockPos providerFetch;
   private int profileDraw;
   private int vectorPerform;
   private int eventAttach;
   private int serverRead;
   private int positionAdvance;
   private long frameCheck;
   private Boolean moduleCollect;

   public AutoFTObsidianFarm() {
      this.handle(this.source, this.target, this.pending);
   }

   @Override
   public void handle() {
      super.handle();
      this.unload();
      this.save();
      if (Module.client.player != null) {
         this.render();
      }
   }

   @Override
   public void process() {
      this.drawAnimation();
      this.encodePoint();
      this.submit();
      this.unload();
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         switch (this.previous) {
            case WALKING:
               if (this.vectorMatch == null || this.handle(this.vectorMatch, 1.5) || !this.animate()) {
                  this.drawAnimation();
                  this.itemProject = new BlockPos(Module.client.player.getBlockPos().getX(), this.profileDraw, Module.client.player.getBlockPos().getZ());
                  this.eventAttach = (int)(this.source.compute() / 3.0F) + 1;
                  this.vectorPerform = 0;
                  this.latest = AutoFTObsidianFarm.PrimaryMode.FIND;
                  this.previous = AutoFTObsidianFarm.Mode.MINING;
               }
               break;
            case MINING:
               this.refresh();
         }
      }
   }

   private void refresh() {
      if (this.latest != AutoFTObsidianFarm.PrimaryMode.BREAK) {
         this.encodePoint();
      }

      switch (this.latest) {
         case FIND:
            if (this.vectorPerform >= this.eventAttach) {
               this.render();
               return;
            }

            this.responseCompute = this.itemProject.offset(this.summary.getOpposite(), this.vectorPerform * 3);
            this.providerFetch = this.responseCompute.up();
            if (!this.handle(this.responseCompute)) {
               this.vectorPerform++;
               return;
            }

            this.latest = AutoFTObsidianFarm.PrimaryMode.RETREAT;
            break;
         case RETREAT:
            BlockPos var4 = this.responseCompute.offset(this.summary.getOpposite(), (int)this.target.compute()).up();
            if (!this.animate()) {
               if (this.handle(var4, 1.5)) {
                  this.drawAnimation();
                  this.latest = AutoFTObsidianFarm.PrimaryMode.PLACE;
               } else {
                  this.apply(var4);
               }
            }
            break;
         case PLACE:
            if (!this.resolve(this.providerFetch)) {
               this.latest = AutoFTObsidianFarm.PrimaryMode.RETREAT;
               return;
            }

            BlockState var3 = Module.client.world.getBlockState(this.providerFetch);
            if (!var3.isAir() && !var3.isReplaceable()) {
               this.frameCheck = System.currentTimeMillis();
               this.positionAdvance = 0;
               this.latest = AutoFTObsidianFarm.PrimaryMode.AIM;
               return;
            }

            if (this.process(this.providerFetch)) {
               this.frameCheck = System.currentTimeMillis();
               this.positionAdvance = 0;
               this.latest = AutoFTObsidianFarm.PrimaryMode.AIM;
            } else {
               ChatLogger.handle("§8[§6AutoFTObsidianFarm§8] §cНет булыжника в хотбаре");
               this.setEnabled(false);
            }
            break;
         case AIM:
            this.drawAnimation();
            if (!this.tick()) {
               return;
            }

            if (Module.client.world.getBlockState(this.providerFetch).isAir()) {
               this.serverRead = 10;
               this.latest = AutoFTObsidianFarm.PrimaryMode.WAIT;
               return;
            }

            AutoFTObsidianFarm.DataRecord var2 = this.compute(this.providerFetch);
            if (var2 == null) {
               return;
            }

            this.handle(var2.hit);
            if (this.positionAdvance++ >= 3) {
               this.latest = AutoFTObsidianFarm.PrimaryMode.BREAK;
            }
            break;
         case BREAK:
            this.drawAnimation();
            if (Module.client.world.getBlockState(this.providerFetch).isAir()) {
               Module.client.options.attackKey.setPressed(false);
               this.serverRead = 10;
               this.latest = AutoFTObsidianFarm.PrimaryMode.WAIT;
               return;
            }

            AutoFTObsidianFarm.DataRecord var1 = this.compute(this.providerFetch);
            if (var1 == null) {
               Module.client.options.attackKey.setPressed(false);
               return;
            }

            this.handle(Module.client.world.getBlockState(this.providerFetch));
            this.handle(var1.hit);
            Module.client.options.attackKey.setPressed(true);
            break;
         case WAIT:
            if (this.serverRead-- <= 0) {
               this.vectorPerform++;
               this.latest = AutoFTObsidianFarm.PrimaryMode.FIND;
            }
      }
   }

   private void render() {
      this.summary = Module.client.player.getHorizontalFacing();
      this.matrixBlend = Module.client.player.getBlockPos();
      this.profileDraw = this.matrixBlend.getY() - 1;
      this.vectorMatch = this.matrixBlend.offset(this.summary, (int)this.source.compute());
      this.previous = AutoFTObsidianFarm.Mode.WALKING;
      this.apply(this.vectorMatch);
   }

   private boolean handle(BlockPos var1) {
      for (int var2 = -1; var2 <= 1; var2++) {
         for (int var3 = -1; var3 <= 1; var3++) {
            if (Module.client.world.getBlockState(var1.add(var2, 0, var3)).isOf(Blocks.OBSIDIAN)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean process(BlockPos var1) {
      int var2 = this.handle(Items.COBBLESTONE);
      if (var2 == -1) {
         return false;
      }

      for (Direction var6 : Direction.values()) {
         BlockPos var7 = var1.offset(var6);
         BlockState var8 = Module.client.world.getBlockState(var7);
         if (!var8.isAir() && !var8.isReplaceable() && !var8.getCollisionShape(Module.client.world, var7).isEmpty()) {
            Vec3d var9 = Vec3d.ofCenter(var7).add(Vec3d.of(var6.getOpposite().getVector()).multiply(0.5));
            int var10 = Module.client.player.getInventory().getSelectedSlot();
            this.handle(var2);
            this.handle(var9);
            Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, new BlockHitResult(var9, var6.getOpposite(), var7, false));
            Module.client.player.swingHand(Hand.MAIN_HAND);
            this.handle(var10);
            return true;
         }
      }

      return false;
   }

   private AutoFTObsidianFarm.DataRecord compute(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos();

      for (Direction var6 : Direction.values()) {
         BlockState var7 = Module.client.world.getBlockState(var1.offset(var6));
         if (var7.isAir() || var7.isOf(Blocks.LAVA) || var7.isOf(Blocks.WATER) || var7.isOf(Blocks.CAVE_AIR)) {
            Vec3d var8 = Vec3d.ofCenter(var1).add(Vec3d.of(var6.getVector()).multiply(0.5));
            BlockHitResult var9 = Module.client.world.raycast(new RaycastContext(var2, var8, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
            if (var9.getType() == Type.BLOCK && var9.getBlockPos().equals(var1)) {
               return new AutoFTObsidianFarm.DataRecord(var6, var8);
            }
         }
      }

      return null;
   }

   private void handle(BlockState var1) {
      int var2 = Module.client.player.getInventory().getSelectedSlot();
      float var3 = Module.client.player.getMainHandStack().getMiningSpeedMultiplier(var1);

      for (int var4 = 0; var4 < 9; var4++) {
         ItemStack var5 = Module.client.player.getInventory().getStack(var4);
         float var6 = var5.getMiningSpeedMultiplier(var1);
         if (var5.isSuitableFor(var1)) {
            var6 += 1000.0F;
         }

         if (var6 > var3) {
            var3 = var6;
            var2 = var4;
         }
      }

      this.handle(var2);
   }

   private void handle(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var7, var3)) - 90.0);
      float var12 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var5, var9)), -90.0, 90.0);
      Module.client.player.setYaw(var11);
      Module.client.player.setPitch(var12);
      Module.client.player.headYaw = var11;
      Module.client.player.bodyYaw = var11;
   }

   private int handle(Item var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         if (Module.client.player.getInventory().getStack(var2).getItem() == var1) {
            return var2;
         }
      }

      return -1;
   }

   private void handle(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         Module.client.player.getInventory().setSelectedSlot(var1);
      }
   }

   private boolean tick() {
      return (float)(System.currentTimeMillis() - this.frameCheck) >= this.pending.compute();
   }

   private boolean resolve(BlockPos var1) {
      return Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var1)) <= 20.25;
   }

   private double update(BlockPos var1) {
      double var2 = var1.getX() + 0.5 - Module.client.player.getX();
      double var4 = var1.getZ() + 0.5 - Module.client.player.getZ();
      return Math.sqrt(var2 * var2 + var4 * var4);
   }

   private void apply(BlockPos var1) {
      IBaritone var2 = this.load();
      if (var2 != null && var1 != null) {
         var2.getCustomGoalProcess().setGoalAndPath(new GoalBlock(var1));
      }
   }

   private void drawAnimation() {
      IBaritone var1 = this.load();
      if (var1 != null) {
         var1.getPathingBehavior().cancelEverything();
         var1.getCustomGoalProcess().setGoal(null);
         var1.getInputOverrideHandler().clearAllKeys();
      }
   }

   private void encodePoint() {
      if (Module.client.options != null) {
         Module.client.options.attackKey.setPressed(false);
      }
   }

   private boolean animate() {
      IBaritone var1 = this.load();
      return var1 != null && var1.getPathingBehavior().isPathing();
   }

   private boolean handle(BlockPos var1, double var2) {
      return var1 != null && this.update(var1) <= var2;
   }

   private IBaritone load() {
      try {
         return BaritoneAPI.getProvider().getPrimaryBaritone();
      } catch (Throwable var2) {
         return null;
      }
   }

   private void save() {
      try {
         if (this.moduleCollect == null) {
            this.moduleCollect = (Boolean)BaritoneAPI.getSettings().assumeWalkOnLava.value;
         }

         BaritoneAPI.getSettings().assumeWalkOnLava.value = true;
      } catch (Throwable var2) {
      }
   }

   private void submit() {
      try {
         if (this.moduleCollect != null) {
            BaritoneAPI.getSettings().assumeWalkOnLava.value = this.moduleCollect;
         }
      } catch (Throwable var2) {
      }

      this.moduleCollect = null;
   }

   private void unload() {
      this.previous = AutoFTObsidianFarm.Mode.IDLE;
      this.latest = AutoFTObsidianFarm.PrimaryMode.FIND;
      this.summary = Direction.NORTH;
      this.matrixBlend = null;
      this.vectorMatch = null;
      this.itemProject = null;
      this.responseCompute = null;
      this.providerFetch = null;
      this.profileDraw = 0;
      this.vectorPerform = 0;
      this.eventAttach = 0;
      this.serverRead = 0;
      this.positionAdvance = 0;
      this.frameCheck = 0L;
   }

   record DataRecord(Direction side, Vec3d hit) {
   }

   enum Mode {
      IDLE,
      WALKING,
      MINING;
   }

   enum PrimaryMode {
      FIND,
      RETREAT,
      PLACE,
      AIM,
      BREAK,
      WAIT;
   }
}
