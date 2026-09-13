package ru.wild.modules.movement;

import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.SlowdownMultiplierEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.MovementPhysics;
import ru.wild.util.player.SyntheticKeyState;

@ModuleRegister(
   name = "NoSlow",
   description = "Убирает замедление при использовании предметов",
   category = ModuleCategory.Movement,
   flags = {ModuleFlag.RISKY, ModuleFlag.GRIM}
)
public class NoSlow extends Module {
   private static final Logger latest = LogManager.getLogger("NoSlow");
   private static final int summary = 1;
   private static final String matrixBlend = "NoSlow_FT-Snow_Crossbow";
   private static boolean vectorMatch;
   private static ItemStack itemProject = ItemStack.EMPTY;
   public static ModeSetting source = new ModeSetting("Режим", "Grim", "Grim", "Grim Tick", "Grim V2", "FT");
   public static BooleanSetting target = new BooleanSetting("Арбалет", true).handle(() -> !source.process("FT"));
   public static BooleanSetting pending = new BooleanSetting("Точный стоп", true).handle(() -> !source.process("FT-Snow") || !target.compute());
   public static NumberSetting previous = new NumberSetting("Задержка свапа", 70.0F, 0.0F, 250.0F, 5.0F, false)
      .handle(() -> !source.process("FT-Snow") || !target.compute() || !pending.compute());
   private float responseCompute = 0.0F;
   private int providerFetch = -1;
   private int profileDraw;
   private boolean vectorPerform;
   private final Stopwatch eventAttach = new Stopwatch();
   private NoSlow.Mode serverRead = NoSlow.Mode.IDLE;

   public NoSlow() {
      this.handle(source);
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (Module.client.player != null) {
         if (source.process("Grim Tick") || source.process("Grim V2")) {
            if (Module.client.player.isUsingItem()) {
               this.responseCompute++;
            } else {
               this.responseCompute = 0.0F;
            }
         }
      }
   }

   @EventHandler(handle = 0)
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player == null) {
         this.process("player-missing");
      } else {
         if (source.process("FT-Snow") && target.compute() && pending.compute()) {
            this.refresh();
         } else {
            this.matchVector();
         }
      }
   }

   @EventHandler
   public void handle(SlowdownMultiplierEvent var1) {
      if (Module.client.player != null) {
         if (source.process("Grim")) {
            if (Module.client.player.getActiveHand() == Hand.MAIN_HAND) {
               Module.client.interactionManager.interactItem(Module.client.player, Hand.OFF_HAND);
            } else {
               Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            }

            var1.process();
         }

         if (source.process("FT")) {
            if (Module.client.player.isUsingItem() && Module.client.player.getActiveItem().getItem() instanceof CrossbowItem) {
               var1.process();
            }

            if (this.save()) {
               var1.process();
            }
         }

         if (source.process("Grim V2") && Module.client.player.isUsingItem() && !Module.client.player.hasVehicle() && this.responseCompute >= 1.3F) {
            var1.process();
            this.responseCompute = 0.26F;
         }

         if (source.process("Grim Tick") && Module.client.player.isUsingItem() && !Module.client.player.hasVehicle() && this.responseCompute >= 1.2F) {
            var1.process();
            this.responseCompute = 0.0F;
         }
      }
   }

   private void refresh() {
      if (Module.client.interactionManager != null && Module.client.player != null) {
         if (this.serverRead != NoSlow.Mode.IDLE) {
            this.render();
         } else {
            if (this.submit() && !Module.client.player.getOffHandStack().isOf(Items.CROSSBOW) && MovementPhysics.handle()) {
               this.drawAnimation();
            }
         }
      }
   }

   private void render() {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         switch (this.serverRead) {
            case IDLE:
            default:
               break;
            case PRE_SWAP_STOP:
               this.measure();
               if (!this.load()) {
                  return;
               }

               this.resolve(this.providerFetch);
               if (!Module.client.player.getOffHandStack().isOf(Items.CROSSBOW)) {
                  this.handle("swap-failed-after-stop", this.providerFetch);
                  this.compute("swap-failed-after-stop");
                  return;
               }

               this.handle("swapped-to-offhand", this.providerFetch);
               this.encodePoint();
               break;
            case EATING:
               this.tick();
               break;
            case PRE_RESTORE_STOP:
               this.measure();
               if (!this.load()) {
                  return;
               }

               if (Module.client.player.getOffHandStack().isOf(Items.CROSSBOW)) {
                  this.resolve(this.providerFetch);
                  this.handle("back-swapped-after-stop", this.providerFetch);
               } else if (this.process(this.providerFetch)) {
                  this.handle("crossbow-already-at-origin", this.providerFetch);
               } else {
                  this.handle("back-swap-missing-crossbow", this.providerFetch);
               }

               this.compute("restore-finished");
         }
      } else {
         this.compute("client-state-missing");
      }
   }

   private void tick() {
      if (this.submit()) {
         if (!this.vectorPerform) {
            this.blendMatrix();
            this.handle("eating-confirmed-unlock", this.providerFetch);
         }

         this.vectorPerform = true;
         this.profileDraw = 0;
      } else {
         if (!this.vectorPerform) {
            this.measure();
         }

         this.projectItem();
         if (!this.vectorPerform && this.unload()) {
            if (this.profileDraw++ < 1) {
               this.encodePoint();
               this.handle("restart-eating", this.providerFetch);
            } else {
               this.handle("eating-did-not-start");
            }
         } else {
            this.handle(this.vectorPerform ? "eating-finished" : "eating-cancelled");
         }
      }
   }

   private void drawAnimation() {
      int var1 = this.fetch();
      if (var1 == -1) {
         this.handle("no-crossbow-found", -1);
      } else {
         fetchProvider();
         this.computeResponse();
         this.providerFetch = var1;
         this.serverRead = NoSlow.Mode.PRE_SWAP_STOP;
         this.animate();
         this.vectorPerform = false;
         this.profileDraw = 0;
         this.measure();
         this.handle("pre-swap-stop", var1);
      }
   }

   private void encodePoint() {
      if (!this.unload()) {
         this.handle("main-hand-food-missing");
      } else {
         Module.client.options.useKey.setPressed(true);
         Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
         this.blendMatrix();
         this.serverRead = NoSlow.Mode.EATING;
         this.handle("start-eating", this.providerFetch);
      }
   }

   private void handle(String var1) {
      this.computeResponse();
      this.serverRead = NoSlow.Mode.PRE_RESTORE_STOP;
      this.animate();
      this.measure();
      this.handle("pre-back-swap-stop:" + var1, this.providerFetch);
   }

   private void animate() {
      this.eventAttach.handle();
   }

   private boolean load() {
      return pending.compute() && this.eventAttach.prepare((long)previous.compute());
   }

   private boolean save() {
      return !this.submit() ? false : this.serverRead != NoSlow.Mode.IDLE || Module.client.player.getOffHandStack().isOf(Items.CROSSBOW);
   }

   private boolean submit() {
      if (Module.client.player != null && Module.client.player.isUsingItem() && Module.client.player.getActiveHand() == Hand.MAIN_HAND) {
         ItemStack var1 = Module.client.player.getActiveItem();
         return !var1.isEmpty() && var1.getUseAction() == UseAction.EAT;
      } else {
         return false;
      }
   }

   private boolean unload() {
      if (Module.client.player == null) {
         return false;
      }

      ItemStack var1 = Module.client.player.getMainHandStack();
      return !var1.isEmpty() && var1.getUseAction() == UseAction.EAT;
   }

   private int fetch() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (this.handle(var1)) {
            return this.compute(var1);
         }
      }

      for (int var2 = 9; var2 < 36; var2++) {
         if (this.handle(var2)) {
            return this.compute(var2);
         }
      }

      return -1;
   }

   private boolean handle(int var1) {
      return Module.client.player != null && var1 >= 0 && var1 < 36 && Module.client.player.getInventory().getStack(var1).isOf(Items.CROSSBOW);
   }

   private boolean process(int var1) {
      return var1 >= 36 && var1 <= 44 ? this.handle(var1 - 36) : var1 >= 9 && var1 < 36 && this.handle(var1);
   }

   private int compute(int var1) {
      return var1 < 9 ? var1 + 36 : var1;
   }

   private void resolve(int var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var1, 40, SlotActionType.SWAP, Module.client.player);
      }
   }

   private void measure() {
      if (Module.client.player != null && Module.client.world != null) {
         SyntheticKeyState.handle().handle("NoSlow_FT-Snow_Crossbow");
         Sprint.latest = Math.max(Sprint.latest, 1);
         Module.client.options.useKey.setPressed(false);
      }
   }

   private void blendMatrix() {
      if (Module.client.player != null && Module.client.world != null) {
         SyntheticKeyState.handle().process("NoSlow_FT-Snow_Crossbow");
      } else {
         SyntheticKeyState.handle().instance.remove("NoSlow_FT-Snow_Crossbow");
      }
   }

   private void matchVector() {
      this.process("restore-requested");
   }

   private void process(String var1) {
      this.projectItem();
      if (this.serverRead != NoSlow.Mode.IDLE && this.providerFetch != -1) {
         if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.getOffHandStack().isOf(Items.CROSSBOW)) {
            this.resolve(this.providerFetch);
            this.handle(var1, this.providerFetch);
         } else {
            this.handle(var1 + ":offhand-not-crossbow", this.providerFetch);
         }

         this.compute(var1);
      } else {
         this.compute(var1);
      }
   }

   private void compute(String var1) {
      if (this.serverRead != NoSlow.Mode.IDLE || this.providerFetch != -1) {
         this.handle("reset:" + var1, this.providerFetch);
      }

      this.blendMatrix();
      drawProfile();
      this.providerFetch = -1;
      this.profileDraw = 0;
      this.vectorPerform = false;
      this.eventAttach.handle();
      this.serverRead = NoSlow.Mode.IDLE;
   }

   private void projectItem() {
      if (Module.client.player != null && Module.client.interactionManager != null && this.serverRead != NoSlow.Mode.IDLE) {
         if (Module.client.player.isUsingItem() && Module.client.player.getActiveHand() == Hand.OFF_HAND) {
            if (Module.client.player.getActiveItem().isOf(Items.CROSSBOW)) {
               this.handle("stop-offhand-crossbow-use", this.providerFetch);
               this.computeResponse();
            }
         }
      }
   }

   private void computeResponse() {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.isUsingItem()) {
         Module.client.interactionManager.stopUsingItem(Module.client.player);
         Module.client.player.stopUsingItem();
      }
   }

   private static void fetchProvider() {
      if (Module.client.player != null) {
         itemProject = Module.client.player.getOffHandStack().copy();
         vectorMatch = true;
      }
   }

   private static void drawProfile() {
      vectorMatch = false;
      itemProject = ItemStack.EMPTY;
   }

   public static ItemStack handle(ItemStack var0) {
      if (!vectorMatch) {
         return var0;
      } else {
         return itemProject == null ? ItemStack.EMPTY : itemProject;
      }
   }

   private void handle(String var1, int var2) {
      latest.info(
         "",
         new Object[]{
            var1,
            this.serverRead,
            var2,
            pending.compute(),
            this.eventAttach.apply(),
            (long)previous.compute(),
            Module.client.player != null && Module.client.player.isUsingItem(),
            Module.client.player != null ? Module.client.player.getActiveHand() : null,
            Module.client.player != null ? Module.client.player.getMainHandStack().getItem() : null,
            Module.client.player != null ? Module.client.player.getOffHandStack().getItem() : null,
            vectorMatch,
            itemProject != null ? itemProject.getItem() : null,
            Module.client.options != null && Module.client.options.useKey.isPressed()
         }
      );
   }

   @Override
   public void process() {
      this.process("module-disabled");
      super.process();
   }

   enum Mode {
      IDLE,
      PRE_SWAP_STOP,
      EATING,
      PRE_RESTORE_STOP;
   }
}
