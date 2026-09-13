package ru.wild.modules.misc;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.mixin.acceser.ClientPlayerInteractionManagerAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "PotionCombiner", category = ModuleCategory.Misc, description = "Автоматически объединяет зелья в наковальне")
public class PotionCombiner extends Module {
   private static final String previous = "Сила";
   private static final String latest = "Скорость";
   private static final String summary = "Скорость 3 + Сила 3";
   private static final String matrixBlend = "Сила 3 + Скорость 3";
   private static final float vectorMatch = 0.92F;
   private static final float itemProject = 0.005F;
   private static final float responseCompute = 0.02F;
   private static final int providerFetch = 6;
   private static final double profileDraw = 4.6;
   public final ModeSetting source = new ModeSetting("Зелье", "Сила", "Сила", "Скорость", "Скорость 3 + Сила 3", "Сила 3 + Скорость 3");
   public final NumberSetting target = new NumberSetting("Уровень", 5.0F, 1.0F, 30.0F, 1.0F, false);
   public final BooleanSetting pending = new BooleanSetting("Экономия опыта", true);
   private final Stopwatch vectorPerform = new Stopwatch();
   private final Stopwatch eventAttach = new Stopwatch();
   private final Stopwatch serverRead = new Stopwatch();
   private final Stopwatch positionAdvance = new Stopwatch();
   private final Random frameCheck = new Random();
   private boolean moduleCollect;
   private int providerClose = 8;
   private int presetSave = 300;
   private int windowConvert = 220;
   private int presetWrite = -1;
   private int colorMeasure = -1;
   private float animationSchedule;
   private String rendererScan = "";
   private int sourceBuild;

   public PotionCombiner() {
      this.handle(this.source, this.target, this.pending);
   }

   @Override
   public void handle() {
      super.handle();
      this.rendererScan = "";
      this.positionAdvance.process(-10000L);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player == null || Module.client.world == null || Module.client.interactionManager == null) {
         this.resolve(false);
      } else if (this.moduleCollect) {
         this.encodePoint();
      } else if (Module.client.player.experienceLevel < this.submit()) {
         if (this.load() != -1) {
            this.drawAnimation();
         } else {
            this.compute("§cНет пузырьков опыта. Нужно добить уровень до " + this.submit() + ".");
         }
      } else if (Module.client.currentScreen instanceof AnvilScreen && Module.client.player.currentScreenHandler instanceof AnvilScreenHandler var2) {
         this.handle(var2);
      } else {
         if (Module.client.currentScreen == null) {
            this.refresh();
         }
      }
   }

   private void refresh() {
      BlockPos var1 = this.compute(6);
      if (var1 == null) {
         this.process("§cНаковальня не найдена в радиусе 6 блоков.");
      } else {
         Vec3d var2 = new Vec3d(var1.getX() + 0.5, var1.getY() + 0.9, var1.getZ() + 0.5);
         Vec3d var3 = this.handle(var2, 0.02F);
         RotationAngles var4 = this.handle(var3);
         float var5 = 55.0F + this.handle(-2.0F, 2.0F);
         RotationController.handle(var4, var5 * 0.92F, var5 * 0.92F, 25.0F, 25.0F, 2, 30, false);
         if (this.serverRead.handle((long)this.providerClose)) {
            if (!(new RotationAngles(Module.client.player).handle(var4) > 4.0F)) {
               if (this.handle(var3, 4.6) && this.handle(var1, var3)) {
                  BlockHitResult var6 = new BlockHitResult(this.handle(Vec3d.ofCenter(var1), 0.08F), Direction.UP, var1, false);
                  Module.client.player.swingHand(Hand.MAIN_HAND);
                  Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var6);
                  this.serverRead.handle();
                  this.providerClose = this.handle(0, 1);
               }
            }
         }
      }
   }

   private void handle(AnvilScreenHandler var1) {
      if (!this.apply(var1)) {
         if (!this.eventAttach.handle((long)this.windowConvert) || !this.update(var1)) {
            this.process(var1);
            if (Module.client.player.experienceLevel < this.submit()) {
               this.drawAnimation();
            } else {
               if (this.resolve(var1) && var1.getSlot(2).hasStack() && this.eventAttach.handle((long)this.windowConvert)) {
                  if (this.pending.compute()) {
                     this.execute(var1);
                  }

                  Module.client.interactionManager.clickSlot(var1.syncId, 2, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                  this.eventAttach.handle();
                  this.windowConvert = this.handle(85, 120);
               }
            }
         }
      }
   }

   private void process(AnvilScreenHandler var1) {
      if (this.eventAttach.handle((long)this.windowConvert)) {
         if (this.unload()) {
            this.compute(var1);
         } else {
            for (int var2 = 0; var2 < 2; var2++) {
               ItemStack var3 = this.process(var1, var2);
               if (!var3.isEmpty() && !this.handle(var3)) {
                  this.handle(var1, var2);
                  this.save();
                  return;
               }
            }

            for (int var4 = 0; var4 < 2; var4++) {
               if (this.process(var1, var4).isEmpty()) {
                  int var5 = this.process(var1, this::handle);
                  if (var5 != -1) {
                     this.handle(var1, var5, var4);
                     this.save();
                  }

                  return;
               }
            }
         }
      }
   }

   private void compute(AnvilScreenHandler var1) {
      for (int var2 = 0; var2 < 2; var2++) {
         ItemStack var3 = this.process(var1, var2);
         if (!var3.isEmpty() && !this.handle(var3, var2)) {
            this.handle(var1, var2);
            this.save();
            return;
         }
      }

      for (int var4 = 0; var4 < 2; var4++) {
         if (this.process(var1, var4).isEmpty()) {
            int var5 = this.process(var1, this.handle(var4));
            if (var5 != -1) {
               this.handle(var1, var5, var4);
               this.save();
            }

            return;
         }
      }
   }

   private boolean resolve(AnvilScreenHandler var1) {
      ItemStack var2 = this.process(var1, 0);
      ItemStack var3 = this.process(var1, 1);
      return this.unload() ? this.handle(var2, 0) && this.handle(var3, 1) : this.handle(var2) && this.handle(var3);
   }

   private boolean update(AnvilScreenHandler var1) {
      if (this.unload()) {
         if (this.handle(var1, this::process) <= 0) {
            this.compute("§cНет ингредиента: Скорость III.");
            return true;
         } else if (this.handle(var1, this::compute) <= 0) {
            this.compute("§cНет ингредиента: Сила III.");
            return true;
         } else {
            return false;
         }
      } else {
         int var2 = this.handle(var1, this::handle);
         if (var2 < 2) {
            this.compute("§cНет ингредиента: " + this.render() + " x" + (2 - var2) + ".");
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean apply(AnvilScreenHandler var1) {
      for (int var2 = 0; var2 < 2; var2++) {
         ItemStack var3 = this.process(var1, var2);
         if (!var3.isEmpty() && var3.getCount() > 1) {
            if (this.eventAttach.handle((long)this.windowConvert)) {
               this.handle(var1, var2);
               this.save();
            }

            return true;
         }
      }

      return false;
   }

   private int handle(AnvilScreenHandler var1, Predicate<ItemStack> var2) {
      int var3 = 0;

      for (int var4 = 0; var4 < var1.slots.size(); var4++) {
         if (var4 != 2) {
            ItemStack var5 = var1.getSlot(var4).getStack();
            if (var2.test(var5)) {
               var3 += Math.max(1, var5.getCount());
            }
         }
      }

      return var3;
   }

   private String render() {
      if (this.source.process("Сила")) {
         return "Сила II";
      } else {
         return this.source.process("Скорость") ? "Скорость II" : "зелье";
      }
   }

   private boolean handle(ItemStack var1) {
      if (this.source.process("Сила")) {
         return this.handle(var1, StatusEffects.STRENGTH, 2);
      } else {
         return this.source.process("Скорость") ? this.handle(var1, StatusEffects.SPEED, 2) : this.process(var1) || this.compute(var1);
      }
   }

   private boolean handle(ItemStack var1, int var2) {
      return this.process(var2) ? this.process(var1) : this.compute(var1);
   }

   private Predicate<ItemStack> handle(int var1) {
      return this.process(var1) ? this::process : this::compute;
   }

   private boolean process(int var1) {
      boolean var2 = this.source.process("Скорость 3 + Сила 3");
      return var1 == 0 ? var2 : !var2;
   }

   private boolean handle(ItemStack var1, RegistryEntry<StatusEffect> var2, int var3) {
      if (!this.resolve(var1)) {
         return false;
      }

      PotionContentsComponent var4 = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
      if (var4 == null) {
         return false;
      }

      for (StatusEffectInstance var6 : var4.getEffects()) {
         if (var6.getEffectType().equals(var2) && var6.getAmplifier() == var3 - 1) {
            return true;
         }
      }

      return false;
   }

   private boolean process(ItemStack var1) {
      return this.handle(var1, StatusEffects.SPEED, 3) && !this.handle(var1, StatusEffects.STRENGTH, 3);
   }

   private boolean compute(ItemStack var1) {
      return this.handle(var1, StatusEffects.STRENGTH, 3) && !this.handle(var1, StatusEffects.SPEED, 3);
   }

   private boolean resolve(ItemStack var1) {
      return var1 != null && !var1.isEmpty() && (var1.isOf(Items.POTION) || var1.isOf(Items.SPLASH_POTION) || var1.isOf(Items.LINGERING_POTION));
   }

   private int process(AnvilScreenHandler var1, Predicate<ItemStack> var2) {
      for (int var3 = 3; var3 < var1.slots.size(); var3++) {
         ItemStack var4 = var1.getSlot(var3).getStack();
         if (var2.test(var4)) {
            return var3;
         }
      }

      return -1;
   }

   private void handle(AnvilScreenHandler var1, int var2, int var3) {
      Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
      Module.client.interactionManager.clickSlot(var1.syncId, var3, 1, SlotActionType.PICKUP, Module.client.player);
      Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
   }

   private void handle(AnvilScreenHandler var1, int var2) {
      Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.QUICK_MOVE, Module.client.player);
   }

   private void execute(AnvilScreenHandler var1) {
      if (Module.client.player != null && Module.client.player.networkHandler != null) {
         String var2 = this.prepare(var1);

         for (int var3 = 0; var3 < 10; var3++) {
            String var4 = var3 % 2 == 0 ? var2 + this.tick() : var2;
            var1.setNewItemName(var4);
            Module.client.player.networkHandler.sendPacket(new RenameItemC2SPacket(var4));
         }
      }
   }

   private String prepare(AnvilScreenHandler var1) {
      ItemStack var2 = this.process(var1, 0);
      if (!var2.isEmpty()) {
         return this.handle(var2.getName().getString());
      }

      ItemStack var3 = this.process(var1, 2);
      return !var3.isEmpty() ? this.handle(var3.getName().getString()) : "Potion";
   }

   private String tick() {
      this.sourceBuild++;
      return "_" + Integer.toString(this.sourceBuild, 36) + Integer.toString(this.frameCheck.nextInt(1296), 36);
   }

   private String handle(String var1) {
      if (var1 != null && !var1.isBlank()) {
         return var1.length() > 32 ? var1.substring(0, 32) : var1;
      } else {
         return "Potion";
      }
   }

   private ItemStack process(AnvilScreenHandler var1, int var2) {
      return var1 != null && var2 >= 0 && var2 < var1.slots.size() ? var1.getSlot(var2).getStack() : ItemStack.EMPTY;
   }

   private void drawAnimation() {
      this.moduleCollect = true;
      this.presetWrite = Module.client.player.getInventory().getSelectedSlot();
      this.animationSchedule = Module.client.player.getPitch();
      this.vectorPerform.handle();
   }

   private void encodePoint() {
      if (Module.client.player.experienceLevel >= this.submit()) {
         this.compute(true);
      } else if (Module.client.currentScreen != null) {
         Module.client.player.closeHandledScreen();
      } else {
         float var1 = 87.0F + this.handle(-0.7F, 0.7F);
         Module.client.player.setPitch(process(var1));
         if (!this.animate()) {
            this.compute(true);
            this.compute("§cНет пузырьков опыта. Нужно добить уровень до " + this.submit() + ".");
         } else if (this.vectorPerform.handle((long)this.presetSave)) {
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            this.vectorPerform.handle();
            this.presetSave = this.handle(50, 70);
         }
      }
   }

   private boolean animate() {
      if (Module.client.player.getMainHandStack().isOf(Items.EXPERIENCE_BOTTLE)) {
         return true;
      }

      int var1 = this.load();
      if (var1 == -1) {
         return false;
      }

      if (var1 >= 36 && var1 <= 44) {
         Module.client.player.getInventory().setSelectedSlot(var1 - 36);
         ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
         return true;
      }

      if (this.presetWrite < 0) {
         this.presetWrite = Module.client.player.getInventory().getSelectedSlot();
      }

      this.colorMeasure = var1;
      Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var1, this.presetWrite, SlotActionType.SWAP, Module.client.player);
      ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
      return true;
   }

   private int load() {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var1 = 9; var1 <= 44; var1++) {
         if (Module.client.player.playerScreenHandler.getSlot(var1).getStack().isOf(Items.EXPERIENCE_BOTTLE)) {
            return var1;
         }
      }

      return -1;
   }

   private void compute(boolean var1) {
      if (var1 && Module.client.player != null && Module.client.interactionManager != null) {
         if (this.colorMeasure != -1 && this.presetWrite >= 0) {
            Module.client.interactionManager
               .clickSlot(Module.client.player.playerScreenHandler.syncId, this.colorMeasure, this.presetWrite, SlotActionType.SWAP, Module.client.player);
         }

         if (this.presetWrite >= 0) {
            Module.client.player.getInventory().setSelectedSlot(this.presetWrite);
            ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
         }

         Module.client.player.setPitch(this.animationSchedule);
      }

      this.moduleCollect = false;
      this.colorMeasure = -1;
      this.presetWrite = -1;
   }

   private BlockPos compute(int var1) {
      BlockPos var2 = Module.client.player.getBlockPos();
      Vec3d var3 = Module.client.player.getEyePos();
      BlockPos var4 = null;
      double var5 = Double.MAX_VALUE;

      for (int var7 = -var1; var7 <= var1; var7++) {
         for (int var8 = -2; var8 <= 2; var8++) {
            for (int var9 = -var1; var9 <= var1; var9++) {
               BlockPos var10 = var2.add(var7, var8, var9);
               Block var11 = Module.client.world.getBlockState(var10).getBlock();
               if (this.handle(var11)) {
                  Vec3d var12 = new Vec3d(var10.getX() + 0.5, var10.getY() + 0.9, var10.getZ() + 0.5);
                  double var13 = var3.squaredDistanceTo(var12);
                  if (var13 < var5) {
                     var5 = var13;
                     var4 = var10.toImmutable();
                  }
               }
            }
         }
      }

      return var4;
   }

   private boolean handle(Block var1) {
      return var1 == Blocks.ANVIL || var1 == Blocks.CHIPPED_ANVIL || var1 == Blocks.DAMAGED_ANVIL;
   }

   private boolean handle(BlockPos var1, Vec3d var2) {
      Vec3d var3 = Module.client.player.getEyePos();
      BlockHitResult var4 = Module.client.world.raycast(new RaycastContext(var3, var2, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      return var4.getType() == Type.BLOCK && var4.getBlockPos().equals(var1);
   }

   private boolean handle(Vec3d var1, double var2) {
      return Module.client.player.getEyePos().squaredDistanceTo(var1) <= var2 * var2;
   }

   private RotationAngles handle(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = Math.hypot(var3, var7);
      float var11 = (float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F;
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      var11 += this.handle(-0.03F, 0.03F);
      var12 += this.handle(-0.03F, 0.03F);
      return new RotationAngles(handle(var11), process(var12));
   }

   private Vec3d handle(Vec3d var1, float var2) {
      return new Vec3d(var1.x + this.handle(-var2, var2), var1.y + this.handle(-var2 * 0.5F, var2 * 0.5F), var1.z + this.handle(-var2, var2));
   }

   private void save() {
      this.eventAttach.handle();
      this.windowConvert = this.handle(85, 120);
   }

   private void process(String var1) {
      if (var1 != null && !var1.isBlank()) {
         if (!var1.equals(this.rendererScan) || this.positionAdvance.handle(2500L)) {
            ChatLogger.handle("§8[§dPotionCombiner§8] §f" + var1);
            this.rendererScan = var1;
            this.positionAdvance.handle();
         }
      }
   }

   private void compute(String var1) {
      this.process(var1);
      if (this.enabled) {
         this.toggle();
      }
   }

   private int submit() {
      return Math.max(1, Math.round(this.target.compute()));
   }

   private boolean unload() {
      return this.source.process("Скорость 3 + Сила 3") || this.source.process("Сила 3 + Скорость 3");
   }

   private float handle(float var1, float var2) {
      return var1 + (var2 - var1) * this.frameCheck.nextFloat();
   }

   private int handle(int var1, int var2) {
      return var1 + this.frameCheck.nextInt(Math.max(1, var2 - var1 + 1));
   }

   private static float handle(float var0) {
      var0 %= 360.0F;
      if (var0 >= 180.0F) {
         var0 -= 360.0F;
      }

      if (var0 < -180.0F) {
         var0 += 360.0F;
      }

      return var0;
   }

   private static float process(float var0) {
      return Math.max(-90.0F, Math.min(90.0F, var0));
   }

   private void resolve(boolean var1) {
      this.compute(var1);
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
   }

   @Override
   public void process() {
      this.resolve(true);
      super.process();
   }
}
