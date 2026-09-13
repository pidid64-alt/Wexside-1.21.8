package org.wild.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.wild.mixin.acceser.HandledScreenAccessor;
import ru.wild.WildClient;
import ru.wild.gui.widget.DiscountSlider;
import ru.wild.modules.misc.AhHelper;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.modules.misc.LockSlots;
import ru.wild.modules.misc.UnHook;
import ru.wild.modules.visuals.Animations;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {
   @Shadow
   protected int x;
   @Shadow
   protected int y;
   @Shadow
   protected int backgroundWidth;
   @Shadow
   protected int backgroundHeight;
   @Unique
   private static final int WILD_AUTOPARSE_CONTROL_WIDTH = 110;
   @Unique
   private static final int WILD_AUTOPARSE_CONTROL_HEIGHT = 20;
   @Unique
   private static final int WILD_AUTOPARSE_CONTROL_GAP = 4;
   @Unique
   private ButtonWidget wild$autoParseButton;
   @Unique
   private DiscountSlider wild$parseDiscountSlider;
   @Unique
   private static final int WILD_QUICK_BUTTON_HEIGHT = 20;
   @Unique
   private static final int WILD_QUICK_BUTTON_GAP = 4;
   @Unique
   private ButtonWidget wild$dropInventoryButton;
   @Unique
   private ButtonWidget wild$takeAllButton;
   @Unique
   private ButtonWidget wild$depositAllButton;
   @Unique
   private ButtonWidget wild$dropContainerButton;

   protected HandledScreenMixin(Text var1) {
      super(var1);
   }

   @Unique
   private boolean litka$shouldAnimate(Animations var1) {
      return var1 != null && var1.handle(this);
   }

   @Unique
   private boolean litka$isRecipeBookScreen() {
      return ((Object)this) instanceof RecipeBookScreen;
   }

   @Unique
   private void litka$applyScale(DrawContext var1, Animations var2) {
      float var3 = var2.process(this);
      var1.getMatrices().pushMatrix();
      float var4 = var1.getScaledWindowWidth() / 2.0F;
      float var5 = var1.getScaledWindowHeight() / 2.0F;
      var1.getMatrices().translate(var4, var5);
      var1.getMatrices().scale(var3, var3);
      var1.getMatrices().translate(-var4, -var5);
   }

   @Inject(
      method = "renderBackground",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawBackground(Lnet/minecraft/client/gui/DrawContext;FII)V")
   )
   private void litka$preDrawBackground(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (WildClient.prepare()) {
         Animations var6 = WildClient.instance.data.handle(Animations.class);
         if (this.litka$shouldAnimate(var6)) {
            this.litka$applyScale(var1, var6);
         }
      }
   }

   @Inject(method = "renderBackground", at = @At("TAIL"))
   private void litka$postDrawBackground(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (WildClient.prepare()) {
         Animations var6 = WildClient.instance.data.handle(Animations.class);
         if (this.litka$shouldAnimate(var6)) {
            var1.getMatrices().popMatrix();
         }
      }
   }

   @Inject(
      method = "renderMain",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = Shift.AFTER)
   )
   private void litka$preRenderForeground(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (WildClient.prepare()) {
         Animations var6 = WildClient.instance.data.handle(Animations.class);
         if (!this.litka$isRecipeBookScreen() && this.litka$shouldAnimate(var6)) {
            this.litka$applyScale(var1, var6);
         }
      }
   }

   @Inject(method = "renderMain", at = @At("TAIL"))
   private void litka$postRenderForeground(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (WildClient.prepare()) {
         Animations var6 = WildClient.instance.data.handle(Animations.class);
         if (!this.litka$isRecipeBookScreen() && this.litka$shouldAnimate(var6)) {
            var1.getMatrices().popMatrix();
         }
      }
   }

   @Inject(method = "close", at = @At("HEAD"), cancellable = true)
   private void litka$animateClose(CallbackInfo var1) {
      if (WildClient.prepare()) {
         if (WildClient.instance != null && WildClient.instance.data != null) {
            Animations var2 = WildClient.instance.data.handle(Animations.class);
            if (this.litka$shouldAnimate(var2) && !var2.measure()) {
               var2.compute(this);
               var1.cancel();
            }
         }
      }
   }

   @Inject(method = "removed", at = @At("HEAD"))
   private void litka$onClose(CallbackInfo var1) {
      if (WildClient.prepare()) {
         Animations var2 = WildClient.instance.data.handle(Animations.class);
         if (var2 != null) {
            var2.blendMatrix();
         }

         AhHelper.previous.clear();
      }
   }

   @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
   private void litka$onDrawSlot(DrawContext var1, Slot var2, CallbackInfo var3) {
      if (AhHelper.handle((HandledScreen<?>)(Object)this, var2)) {
         var3.cancel();
      } else {
         if (AhHelper.target.compute() && AhHelper.previous.contains(var2.id)) {
            int var4 = var2.x;
            int var5 = var2.y;
            var1.fill(var4, var5, var4 + 16, var5 + 16, 1610678016);
         }
      }
   }

   @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"), cancellable = true)
   private void wild$blockFilteredAuctionSlotClick(Slot var1, int var2, int var3, SlotActionType var4, CallbackInfo var5) {
      if (AhHelper.handle((HandledScreen<?>)(Object)this, var1)) {
         var5.cancel();
      } else {
         if (this.wild$isLockedHotbarThrow(var1, var4)) {
            var5.cancel();
         }
      }
   }

   @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"), cancellable = true)
   private void wild$blockFilteredAuctionQuickMove(Slot var1, SlotActionType var2, CallbackInfo var3) {
      if (AhHelper.handle((HandledScreen<?>)(Object)this, var1)) {
         var3.cancel();
      }
   }

   @Inject(method = "getSlotAt", at = @At("RETURN"), cancellable = true)
   private void wild$excludeFilteredAuctionSlot(double var1, double var3, CallbackInfoReturnable<Slot> var5) {
      Slot var6 = (Slot)var5.getReturnValue();
      if (AhHelper.handle((HandledScreen<?>)(Object)this, var6)) {
         var5.setReturnValue(null);
      }
   }

   @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
   private void wild$hideFilteredAuctionTooltip(DrawContext var1, int var2, int var3, CallbackInfo var4) {
      if (this.wild$isFilteredFocusedSlot()) {
         var4.cancel();
      }
   }

   @Inject(method = "drawSlotHighlightBack", at = @At("HEAD"), cancellable = true)
   private void wild$hideFilteredAuctionBackHighlight(DrawContext var1, CallbackInfo var2) {
      if (this.wild$isFilteredFocusedSlot()) {
         var2.cancel();
      }
   }

   @Inject(method = "drawSlotHighlightFront", at = @At("HEAD"), cancellable = true)
   private void wild$hideFilteredAuctionFrontHighlight(DrawContext var1, CallbackInfo var2) {
      if (this.wild$isFilteredFocusedSlot()) {
         var2.cancel();
      }
   }

   @Unique
   private boolean wild$isFilteredFocusedSlot() {
      Slot var1 = ((HandledScreenAccessor)this).litka$getFocusedSlot();
      return AhHelper.handle((HandledScreen<?>)(Object)this, var1);
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void wild$initAutoParseControls(CallbackInfo var1) {
      if (!UnHook.target) {
         AutoBuy var2 = this.wild$getAutoBuy();
         if (var2 != null && this.wild$isAuctionContainer()) {
            int var3 = this.x + this.backgroundWidth + 4;
            int var4 = this.y;
            int var5 = var4 + 20 + 4;
            this.wild$autoParseButton = ButtonWidget.builder(this.wild$autoParseText(var2), var2x -> {
               var2.matchVector();
               var2x.setMessage(this.wild$autoParseText(var2));
               if (this.wild$parseDiscountSlider != null) {
                  this.wild$parseDiscountSlider.handle();
               }
            }).dimensions(var3, var4, 110, 20).build();
            this.addDrawableChild(this.wild$autoParseButton);
            this.wild$parseDiscountSlider = new DiscountSlider(var2, var3, var5, 110, 20);
            this.addDrawableChild(this.wild$parseDiscountSlider);
         }
      }
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void wild$initQuickContainerControls(CallbackInfo var1) {
      if (!UnHook.target) {
         if (((Object)this) instanceof InventoryScreen) {
            byte var5 = 124;
            int var6 = this.x + this.backgroundWidth / 2 - var5 / 2;
            int var7 = this.wild$controlsY();
            this.wild$dropInventoryButton = ButtonWidget.builder(Text.literal("Выбросить все"), var1x -> this.wild$dropInventoryItems())
               .dimensions(var6, var7, var5, 20)
               .build();
            this.addDrawableChild(this.wild$dropInventoryButton);
         } else if (this.wild$isQuickContainer() && !this.wild$isAuctionContainer()) {
            byte var2 = 82;
            int var3 = this.x + this.backgroundWidth + 4;
            int var4 = this.y;
            this.wild$takeAllButton = ButtonWidget.builder(Text.literal("Забрать все"), var1x -> this.wild$takeAllFromContainer())
               .dimensions(var3, var4, var2, 20)
               .build();
            this.wild$depositAllButton = ButtonWidget.builder(Text.literal("Сложить"), var1x -> this.wild$depositAllToContainer())
               .dimensions(var3, var4 + 20 + 4, var2, 20)
               .build();
            this.wild$dropContainerButton = ButtonWidget.builder(Text.literal("Выбросить все"), var1x -> this.wild$dropAllFromContainer())
               .dimensions(var3, var4 + 48, var2, 20)
               .build();
            this.addDrawableChild(this.wild$takeAllButton);
            this.addDrawableChild(this.wild$depositAllButton);
            this.addDrawableChild(this.wild$dropContainerButton);
         }
      }
   }

   @Inject(method = "tick", at = @At("TAIL"))
   private void wild$tickAutoParseControls(CallbackInfo var1) {
      AutoBuy var2 = this.wild$getAutoBuy();
      boolean var3 = !UnHook.target && var2 != null && this.wild$isAuctionContainer();
      if (this.wild$autoParseButton != null) {
         this.wild$autoParseButton.visible = var3;
         this.wild$autoParseButton.active = var3;
         if (var3) {
            this.wild$autoParseButton.setMessage(this.wild$autoParseText(var2));
         }
      }

      if (this.wild$parseDiscountSlider != null) {
         this.wild$parseDiscountSlider.visible = var3;
         this.wild$parseDiscountSlider.active = var3;
         if (var3) {
            this.wild$parseDiscountSlider.handle();
         }
      }

      boolean var4 = !UnHook.target;
      if (this.wild$dropInventoryButton != null) {
         this.wild$dropInventoryButton.visible = var4;
         this.wild$dropInventoryButton.active = var4;
      }

      if (this.wild$takeAllButton != null) {
         this.wild$takeAllButton.visible = var4;
         this.wild$takeAllButton.active = var4;
      }

      if (this.wild$depositAllButton != null) {
         this.wild$depositAllButton.visible = var4;
         this.wild$depositAllButton.active = var4;
      }

      if (this.wild$dropContainerButton != null) {
         this.wild$dropContainerButton.visible = var4;
         this.wild$dropContainerButton.active = var4;
      }
   }

   @Unique
   private AutoBuy wild$getAutoBuy() {
      if (!WildClient.prepare()) {
         return null;
      } else {
         return WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(AutoBuy.class) : null;
      }
   }

   @Unique
   private boolean wild$isAuctionContainer() {
      if (((Object)this) instanceof GenericContainerScreen var1) {
         AutoBuy var3 = this.wild$getAutoBuy();
         return var3 != null && var3.latest.process("HolyWorld") ? var3.handle(var1) : AhHelper.handle(var1);
      } else {
         return false;
      }
   }

   @Unique
   private Text wild$autoParseText(AutoBuy var1) {
      return Text.literal("AutoParse: " + (var1.summary.compute() ? "ON" : "OFF"));
   }

   @Unique
   private int wild$controlsY() {
      int var1 = this.y - 20 - 4;
      return var1 >= 4 ? var1 : this.y + this.backgroundHeight + 4;
   }

   @Unique
   private int wild$centeredControlsX(int var1) {
      int var2 = this.x + this.backgroundWidth / 2 - var1 / 2;
      int var3 = this.width - var1 - 4;
      return Math.max(4, Math.min(var2, var3));
   }

   @Unique
   private void wild$dropInventoryItems() {
      ScreenHandler var1 = this.wild$screenHandler();
      if (this.wild$canInteract(var1)) {
         for (Slot var3 : var1.slots) {
            if (this.wild$isPlayerInventorySlot(var3) && var3.hasStack() && var3.canTakeItems(this.client.player)) {
               this.client.interactionManager.clickSlot(var1.syncId, var3.id, 1, SlotActionType.THROW, this.client.player);
            }
         }
      }
   }

   @Unique
   private void wild$takeAllFromContainer() {
      ScreenHandler var1 = this.wild$screenHandler();
      if (this.wild$canInteract(var1) && this.wild$isQuickContainer(var1)) {
         int var2 = this.wild$containerSlotCount(var1);

         for (int var3 = 0; var3 < var2; var3++) {
            Slot var4 = var1.getSlot(var3);
            if (var4.hasStack() && var4.canTakeItems(this.client.player)) {
               this.client.interactionManager.clickSlot(var1.syncId, var3, 0, SlotActionType.QUICK_MOVE, this.client.player);
            }
         }
      }
   }

   @Unique
   private void wild$depositAllToContainer() {
      ScreenHandler var1 = this.wild$screenHandler();
      if (this.wild$canInteract(var1)) {
         for (Slot var3 : var1.slots) {
            if (this.wild$isPlayerInventorySlot(var3) && var3.hasStack()) {
               this.client.interactionManager.clickSlot(var1.syncId, var3.id, 0, SlotActionType.QUICK_MOVE, this.client.player);
            }
         }
      }
   }

   @Unique
   private void wild$dropAllFromContainer() {
      ScreenHandler var1 = this.wild$screenHandler();
      if (this.wild$canInteract(var1) && this.wild$isQuickContainer(var1)) {
         int var2 = this.wild$containerSlotCount(var1);

         for (int var3 = 0; var3 < var2; var3++) {
            Slot var4 = var1.getSlot(var3);
            if (var4.hasStack() && var4.canTakeItems(this.client.player)) {
               this.client.interactionManager.clickSlot(var1.syncId, var3, 1, SlotActionType.THROW, this.client.player);
            }
         }
      }
   }

   @Unique
   private boolean wild$canInteract(ScreenHandler var1) {
      return this.client != null && this.client.player != null && this.client.interactionManager != null && var1 != null;
   }

   @Unique
   private boolean wild$isLockedHotbarThrow(Slot var1, SlotActionType var2) {
      if (!WildClient.prepare()) {
         return false;
      } else if (var2 != SlotActionType.THROW || !this.wild$isPlayerInventorySlot(var1)) {
         return false;
      } else if (WildClient.instance != null && WildClient.instance.data != null) {
         LockSlots var3 = WildClient.instance.data.handle(LockSlots.class);
         return var3 != null && var3.enabled && var3.handle(var1.getIndex());
      } else {
         return false;
      }
   }

   @Unique
   private boolean wild$isPlayerInventorySlot(Slot var1) {
      return var1 != null && this.client != null && this.client.player != null && var1.inventory == this.client.player.getInventory();
   }

   @Unique
   private ScreenHandler wild$screenHandler() {
      return ((HandledScreen<?>)(Object)this).getScreenHandler();
   }

   @Unique
   private boolean wild$isQuickContainer() {
      return this.wild$isQuickContainer(this.wild$screenHandler());
   }

   @Unique
   private boolean wild$isQuickContainer(ScreenHandler var1) {
      return var1 instanceof GenericContainerScreenHandler || var1 instanceof ShulkerBoxScreenHandler;
   }

   @Unique
   private int wild$containerSlotCount(ScreenHandler var1) {
      int var2;
      if (var1 instanceof GenericContainerScreenHandler var3) {
         var2 = var3.getRows();
      } else {
         if (!(var1 instanceof ShulkerBoxScreenHandler)) {
            return 0;
         }

         var2 = 3;
      }

      int var4 = var1.slots.size();
      return Math.max(0, Math.min(var2 * 9, var4));
   }
}
