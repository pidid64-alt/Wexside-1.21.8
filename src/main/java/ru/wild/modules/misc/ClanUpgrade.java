package ru.wild.modules.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.ModeSetting;
import ru.wild.automation.RotationPlayback;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "ClanUpgrade", category = ModuleCategory.Misc, description = "Прокачивает за вас клан", flags = ModuleFlag.RISKY)
public class ClanUpgrade extends Module {
   private static final String source = "Факел";
   private static final String target = "Красной пылью";
   private static final Item[] pending = new Item[]{Items.TORCH, Items.REDSTONE_TORCH};
   private static final Item[] previous = new Item[]{Items.REDSTONE};
   private static final int latest = 545;
   private static final int summary = 1;
   private static final float matrixBlend = -1170.1321F;
   private static final float vectorMatch = 90.0F;
   private static final float itemProject = 180.0F;
   private static final ClanUpgrade.DataRecord[] responseCompute = new ClanUpgrade.DataRecord[]{
      new ClanUpgrade.DataRecord(7, 1, true),
      new ClanUpgrade.DataRecord(11, 0, true),
      new ClanUpgrade.DataRecord(28, 0, false),
      new ClanUpgrade.DataRecord(31, 0, true),
      new ClanUpgrade.DataRecord(32, 0, false),
      new ClanUpgrade.DataRecord(34, 0, true),
      new ClanUpgrade.DataRecord(539, 0, false),
      new ClanUpgrade.DataRecord(539, 1, false)
   };
   private final ModeSetting providerFetch = new ModeSetting("Режим", "Красной пылью", "Факел", "Красной пылью");
   private final RotationPlayback profileDraw = new RotationPlayback();
   private int vectorPerform;
   private boolean eventAttach;
   private boolean serverRead;

   public ClanUpgrade() {
      this.handle(this.providerFetch);
   }

   @Override
   public void handle() {
      this.save();
      super.handle();
   }

   @Override
   public void process() {
      this.profileDraw.handle();
      this.animate();
      this.save();
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null && Module.client.getNetworkHandler() != null) {
         Item[] var2 = this.refresh();
         if (!this.handle(var2)) {
            ChatLogger.handle("§c[ClanUpgrade] §fНет предметов для режима: " + this.providerFetch.compute());
            this.load();
         } else {
            BlockPos var3 = Module.client.player.getBlockPos().down();
            BlockState var4 = Module.client.world.getBlockState(var3);
            if (!var4.isReplaceable() && var4.getFluidState().isEmpty()) {
               this.render();
               if (!this.tick()) {
                  this.encodePoint();
               } else {
                  this.drawAnimation();
                  this.handle(var3, var3.up());
                  this.vectorPerform++;
                  if (this.vectorPerform >= 545) {
                     this.vectorPerform = 0;
                     this.eventAttach = false;
                     this.serverRead = false;
                  }
               }
            } else {
               this.encodePoint();
            }
         }
      }
   }

   private Item[] refresh() {
      return this.providerFetch.process("Факел") ? pending : previous;
   }

   private boolean handle(Item[] var1) {
      ItemStack var2 = Module.client.player.getInventory().getStack(1);
      if (this.handle(var2, var1)) {
         this.process(1);
         return true;
      }

      int var3 = this.process(var1);
      if (var3 == -1) {
         return false;
      }

      this.handle(var3);
      this.process(1);
      return this.handle(Module.client.player.getInventory().getStack(1), var1);
   }

   private int process(Item[] var1) {
      for (int var2 = 0; var2 < 36; var2++) {
         if (var2 != 1 && this.handle(Module.client.player.getInventory().getStack(var2), var1)) {
            return var2;
         }
      }

      return -1;
   }

   private boolean handle(ItemStack var1, Item[] var2) {
      if (var1 != null && !var1.isEmpty()) {
         for (Item var6 : var2) {
            if (var1.isOf(var6)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void handle(int var1) {
      if (var1 != 1) {
         int var2 = var1 < 9 ? 36 + var1 : var1;
         Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var2, 1, SlotActionType.SWAP, Module.client.player);
      }
   }

   private void process(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         if (Module.client.player.getInventory().getSelectedSlot() != var1) {
            Module.client.player.getInventory().setSelectedSlot(var1);
            Module.client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
         }
      }
   }

   private void render() {
      this.profileDraw.handle(new RotationAngles(-1170.1321F, 90.0F), 180.0F, 180.0F, 1, 15);
   }

   private boolean tick() {
      float var1 = Math.abs(MathHelper.wrapDegrees(-1170.1321F - Module.client.player.getYaw()));
      float var2 = Math.abs(90.0F - Module.client.player.getPitch());
      return var1 <= 1.0F && var2 <= 1.0F;
   }

   private void handle(BlockPos var1, BlockPos var2) {
      for (ClanUpgrade.DataRecord var6 : responseCompute) {
         if (var6.tick == this.vectorPerform) {
            if (var6.button == 1) {
               this.serverRead = var6.press;
               Module.client.options.useKey.setPressed(this.serverRead);
               if (var6.press) {
                  this.handle(var1);
               }
            } else if (var6.button == 0) {
               this.eventAttach = var6.press;
               Module.client.options.attackKey.setPressed(this.eventAttach);
               if (var6.press) {
                  this.process(var2);
               } else {
                  Module.client.interactionManager.cancelBlockBreaking();
               }
            }
         }
      }
   }

   private void handle(BlockPos var1) {
      if (this.handle(Module.client.player.getMainHandStack(), this.refresh())) {
         Vec3d var2 = new Vec3d(var1.getX() + 0.5, var1.getY() + 1.0, var1.getZ() + 0.5);
         BlockHitResult var3 = new BlockHitResult(var2, Direction.UP, var1, false);
         ActionResult var4 = Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var3);
         if (var4 != ActionResult.PASS && var4 != ActionResult.FAIL) {
            Module.client.player.swingHand(Hand.MAIN_HAND);
         } else {
            var4 = Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            if (var4 != ActionResult.PASS && var4 != ActionResult.FAIL) {
               Module.client.player.swingHand(Hand.MAIN_HAND);
            }
         }
      }
   }

   private void process(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      if (this.handle(var2)) {
         Module.client.interactionManager.attackBlock(var1, Direction.UP);
         Module.client.player.swingHand(Hand.MAIN_HAND);
      }
   }

   private void drawAnimation() {
      this.process(1);
      Module.client.options.forwardKey.setPressed(false);
      Module.client.options.backKey.setPressed(false);
      Module.client.options.leftKey.setPressed(false);
      Module.client.options.rightKey.setPressed(false);
      Module.client.options.jumpKey.setPressed(false);
      Module.client.options.sneakKey.setPressed(false);
      Module.client.options.sprintKey.setPressed(false);
      Module.client.options.useKey.setPressed(this.serverRead);
      Module.client.options.attackKey.setPressed(this.eventAttach);
      if (Module.client.player.isSprinting()) {
         Module.client.player.setSprinting(false);
      }
   }

   private void encodePoint() {
      this.eventAttach = false;
      this.serverRead = false;
      this.drawAnimation();
   }

   private void animate() {
      if (Module.client.options != null) {
         Module.client.options.forwardKey.setPressed(false);
         Module.client.options.backKey.setPressed(false);
         Module.client.options.leftKey.setPressed(false);
         Module.client.options.rightKey.setPressed(false);
         Module.client.options.jumpKey.setPressed(false);
         Module.client.options.sneakKey.setPressed(false);
         Module.client.options.sprintKey.setPressed(false);
         Module.client.options.useKey.setPressed(false);
         Module.client.options.attackKey.setPressed(false);
      }
   }

   private boolean handle(BlockState var1) {
      Block var2 = var1.getBlock();
      return var2 == Blocks.REDSTONE_WIRE
         || var2 == Blocks.TORCH
         || var2 == Blocks.WALL_TORCH
         || var2 == Blocks.REDSTONE_TORCH
         || var2 == Blocks.REDSTONE_WALL_TORCH;
   }

   private void load() {
      if (this.enabled) {
         this.toggle();
      }
   }

   private void save() {
      this.vectorPerform = 0;
      this.eventAttach = false;
      this.serverRead = false;
   }

   record DataRecord(int tick, int button, boolean press) {
   }
}
