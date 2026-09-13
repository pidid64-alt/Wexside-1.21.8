package ru.wild.modules.player;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ModeSetting;
import ru.wild.util.player.SyntheticKeyState;

@ModuleRegister(name = "AutoTool", category = ModuleCategory.Player, description = "Автоматически берет нужный вам инструмент")
public class AutoTool extends Module {
   private static final String target = "AutoTool";
   private static final long pending = 50L;
   private static final String previous = "Только хотбар";
   private static final String latest = "Инвентарь";
   private static final String summary = "Гибрид";
   public static ModeSetting source = new ModeSetting("Режим", "Гибрид", "Только хотбар", "Инвентарь", "Гибрид");
   private AutoTool.Mode matrixBlend = AutoTool.Mode.IDLE;
   private long vectorMatch;
   private int itemProject = -1;
   private int responseCompute = -1;
   private boolean providerFetch;

   public AutoTool() {
      this.handle(source);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         switch (this.matrixBlend) {
            case IDLE:
               this.refresh();
               break;
            case PREPARE_SWAP:
               this.render();
               break;
            case MINING:
               this.tick();
               break;
            case PREPARE_RESTORE:
               this.drawAnimation();
         }
      } else {
         this.compute(false);
      }
   }

   @Override
   public void process() {
      this.compute(true);
      super.process();
   }

   private void refresh() {
      BlockState var1 = this.save();
      if (var1 != null && Module.client.options.attackKey.isPressed()) {
         int var2 = this.handle(var1);
         int var3 = Module.client.player.getInventory().getSelectedSlot();
         if (var2 != -1 && var2 != var3) {
            this.itemProject = var2;
            this.responseCompute = var3;
            this.providerFetch = var2 >= 9;
            if (this.providerFetch) {
               this.handle(AutoTool.Mode.PREPARE_SWAP);
            } else {
               Module.client.player.getInventory().setSelectedSlot(var2);
               this.matrixBlend = AutoTool.Mode.MINING;
            }
         }
      }
   }

   private void render() {
      if (!Module.client.options.attackKey.isPressed() || this.save() == null) {
         this.compute(false);
      } else if (this.encodePoint()) {
         this.animate();
         SyntheticKeyState.handle().process("AutoTool");
         this.matrixBlend = AutoTool.Mode.MINING;
      }
   }

   private void tick() {
      if (!Module.client.options.attackKey.isPressed() || this.save() == null) {
         if (this.providerFetch) {
            this.handle(AutoTool.Mode.PREPARE_RESTORE);
         } else {
            this.load();
            this.compute(false);
         }
      }
   }

   private void drawAnimation() {
      if (this.encodePoint()) {
         this.animate();
         this.compute(false);
      }
   }

   private void handle(AutoTool.Mode var1) {
      SyntheticKeyState.handle().handle("AutoTool");
      Module.client.options.sprintKey.setPressed(false);
      Module.client.player.setSprinting(false);
      this.vectorMatch = System.currentTimeMillis();
      this.matrixBlend = var1;
   }

   private boolean encodePoint() {
      SyntheticKeyState.handle().handle("AutoTool");
      return System.currentTimeMillis() - this.vectorMatch >= 50L;
   }

   private void animate() {
      if (this.itemProject >= 9 && this.responseCompute >= 0) {
         Module.client.interactionManager
            .clickSlot(Module.client.player.playerScreenHandler.syncId, this.itemProject, this.responseCompute, SlotActionType.SWAP, Module.client.player);
      }
   }

   private void load() {
      if (this.responseCompute >= 0 && this.responseCompute <= 8) {
         Module.client.player.getInventory().setSelectedSlot(this.responseCompute);
      }
   }

   private BlockState save() {
      return Module.client.crosshairTarget instanceof BlockHitResult var1 && var1.getType() == Type.BLOCK
         ? Module.client.world.getBlockState(var1.getBlockPos())
         : null;
   }

   private int handle(BlockState var1) {
      int var2 = source.process("Инвентарь") ? 9 : 0;
      int var3 = source.process("Только хотбар") ? 9 : 36;
      int var4 = Module.client.player.getInventory().getSelectedSlot();
      ItemStack var5 = Module.client.player.getInventory().getStack(var4);
      boolean var6 = this.handle(var5);
      int var7 = var6 ? var4 : -1;
      float var8 = var6 ? var5.getMiningSpeedMultiplier(var1) : 1.0F;
      boolean var9 = !var1.isToolRequired() || var6 && var5.isSuitableFor(var1);

      for (int var10 = var2; var10 < var3; var10++) {
         ItemStack var11 = Module.client.player.getInventory().getStack(var10);
         if (this.handle(var11)) {
            float var12 = var11.getMiningSpeedMultiplier(var1);
            boolean var13 = !var1.isToolRequired() || var11.isSuitableFor(var1);
            if (var13 && !var9 || var13 == var9 && var12 > var8) {
               var7 = var10;
               var8 = var12;
               var9 = var13;
            }
         }
      }

      return var7;
   }

   private boolean handle(ItemStack var1) {
      return !var1.isEmpty() && (!var1.isDamageable() || var1.getMaxDamage() - var1.getDamage() > 1);
   }

   private void compute(boolean var1) {
      if (var1
         && Module.client.player != null
         && Module.client.interactionManager != null
         && (this.matrixBlend == AutoTool.Mode.MINING || this.matrixBlend == AutoTool.Mode.PREPARE_RESTORE)) {
         if (this.providerFetch) {
            this.animate();
         } else {
            this.load();
         }
      }

      SyntheticKeyState.handle().process("AutoTool");
      SyntheticKeyState.handle().instance.remove("AutoTool");
      this.matrixBlend = AutoTool.Mode.IDLE;
      this.vectorMatch = 0L;
      this.itemProject = -1;
      this.responseCompute = -1;
      this.providerFetch = false;
   }

   enum Mode {
      IDLE,
      PREPARE_SWAP,
      MINING,
      PREPARE_RESTORE;
   }
}
