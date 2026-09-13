package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalNear;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.Map.Entry;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.ScreenOpenedEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.HotbarLayoutSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.ContainerScreenPolicy;
import ru.wild.automation.RotationController;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AutoCraft", category = ModuleCategory.Misc, description = "Автоматически крафтит выбранный рецепт")
public class AutoCraft extends Module {
   public final HotbarLayoutSetting source = new HotbarLayoutSetting("Рецепт");
   public final StringSetting target = new StringSetting("Кол-во предметов", "64").handle(6);
   public final NumberSetting pending = new NumberSetting("Задержка", 80.0F, 20.0F, 500.0F, 10.0F, false);
   public final BooleanSetting previous = new BooleanSetting("Не отображать экран", false);
   private final Stopwatch latest = new Stopwatch();
   private final Stopwatch summary = new Stopwatch();
   private final Stopwatch matrixBlend = new Stopwatch();
   private final Queue<Runnable> vectorMatch = new ArrayDeque<>();
   private IBaritone itemProject;
   private AutoCraft.Mode responseCompute = AutoCraft.Mode.IDLE;
   private BlockPos providerFetch;
   private int profileDraw;
   private int vectorPerform;
   private int eventAttach;
   private String serverRead = "";
   private CraftingScreen positionAdvance;

   public AutoCraft() {
      this.handle(this.source, this.target, this.pending, this.previous);
   }

   @Override
   public void handle() {
      super.handle();
      this.itemProject = BaritoneAPI.getProvider().getPrimaryBaritone();
      this.profileDraw = 0;
      this.vectorPerform = 0;
      this.eventAttach = 0;
      this.vectorMatch.clear();
      this.serverRead = "";
      if (this.source.resolve()) {
         this.compute("§cРецепт пуст.");
      } else if (this.projectItem() <= 0) {
         this.compute("§cНекорректное количество предметов.");
      } else {
         this.responseCompute = AutoCraft.Mode.FINDING_TABLE;
         this.latest.handle();
         this.summary.handle();
         this.matrixBlend.handle();
      }
   }

   @Override
   public void process() {
      super.process();
      this.vectorMatch.clear();
      this.providerFetch = null;
      this.vectorPerform = 0;
      this.eventAttach = 0;
      this.responseCompute = AutoCraft.Mode.IDLE;
      this.positionAdvance = null;
      RotationController.instance = RotationController.Mode.IDLE;
      if (this.itemProject != null) {
         this.itemProject.getPathingBehavior().cancelEverything();
      }
   }

   @EventHandler
   public void handle(ScreenOpenedEvent var1) {
      if (this.previous.compute() && this.responseCompute != AutoCraft.Mode.IDLE && var1.compute() instanceof CraftingScreen var2) {
         this.positionAdvance = var2;
         var1.resolve();
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (!this.refresh()) {
            if (!this.fetch()) {
               if (!this.serverRead.isBlank()) {
                  this.compute("§cНе хватает предмета: §f" + this.process(this.serverRead));
               } else {
                  switch (this.responseCompute) {
                     case IDLE:
                     default:
                        break;
                     case FINDING_TABLE:
                        this.render();
                        break;
                     case GOING_TO_TABLE:
                        this.tick();
                        break;
                     case AIMING_TABLE:
                        this.drawAnimation();
                        break;
                     case OPENING_TABLE:
                        this.encodePoint();
                        break;
                     case CLEARING_GRID:
                        this.animate();
                        break;
                     case PLACING_RECIPE:
                        this.load();
                        break;
                     case WAITING_RESULT:
                        this.save();
                        break;
                     case TAKING_RESULT:
                        this.submit();
                        break;
                     case CLOSING:
                        this.unload();
                  }
               }
            }
         }
      }
   }

   private boolean refresh() {
      if (!PlayerHelper.refresh()) {
         return false;
      }

      if (this.itemProject != null) {
         this.itemProject.getPathingBehavior().cancelEverything();
      }

      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      return true;
   }

   private void render() {
      if (this.latest.update(this.matchVector())) {
         this.providerFetch = this.blendMatrix();
         if (this.providerFetch == null) {
            this.compute("§cВерстак рядом не найден.");
         } else {
            this.responseCompute = AutoCraft.Mode.GOING_TO_TABLE;
            this.latest.handle();
            this.summary.handle();
            this.matrixBlend.handle();
         }
      }
   }

   private void tick() {
      if (!this.handle(this.providerFetch)) {
         this.responseCompute = AutoCraft.Mode.FINDING_TABLE;
         this.latest.handle();
      } else {
         double var1 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.providerFetch));
         if (var1 <= 4.0) {
            if (this.itemProject != null) {
               this.itemProject.getPathingBehavior().cancelEverything();
            }

            this.responseCompute = AutoCraft.Mode.AIMING_TABLE;
            this.latest.handle();
         } else {
            if (this.itemProject != null && (!this.itemProject.getCustomGoalProcess().isActive() || this.summary.update(1500L))) {
               this.itemProject.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.providerFetch, 2));
               this.summary.handle();
            }

            if (this.matrixBlend.update(15000L)) {
               this.compute("§cНе удалось дойти до верстака.");
            }
         }
      }
   }

   private void drawAnimation() {
      RotationAngles var1 = this.handle(Vec3d.ofCenter(this.providerFetch));
      RotationController.handle(var1, 45.0F, 45.0F, 30.0F, 30.0F, 4, 5, false);
      if (!(new RotationAngles(Module.client.player).handle(var1) > 4.0F) && this.latest.update(this.matchVector())) {
         BlockHitResult var2 = new BlockHitResult(Vec3d.ofCenter(this.providerFetch), Direction.UP, this.providerFetch, false);
         Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var2);
         Module.client.player.swingHand(Hand.MAIN_HAND);
         this.responseCompute = AutoCraft.Mode.OPENING_TABLE;
         this.latest.handle();
      }
   }

   private void encodePoint() {
      if (this.measure() != null) {
         this.responseCompute = AutoCraft.Mode.CLEARING_GRID;
         this.latest.handle();
      } else {
         if (this.latest.update(5000L)) {
            this.compute("§cВерстак не открылся.");
         }
      }
   }

   private void animate() {
      CraftingScreen var1 = this.measure();
      if (var1 == null) {
         this.responseCompute = AutoCraft.Mode.FINDING_TABLE;
         this.latest.handle();
      } else {
         CraftingScreenHandler var2 = (CraftingScreenHandler)var1.getScreenHandler();

         for (int var3 = 1; var3 <= 9; var3++) {
            if (var2.getSlot(var3).hasStack()) {
               int var4 = var3;
               this.vectorMatch.add(() -> Module.client.interactionManager.clickSlot(var2.syncId, var4, 0, SlotActionType.QUICK_MOVE, Module.client.player));
            }
         }

         this.responseCompute = AutoCraft.Mode.PLACING_RECIPE;
         this.latest.handle();
      }
   }

   private void load() {
      CraftingScreen var1 = this.measure();
      if (var1 == null) {
         this.responseCompute = AutoCraft.Mode.FINDING_TABLE;
         this.latest.handle();
      } else {
         CraftingScreenHandler var2 = (CraftingScreenHandler)var1.getScreenHandler();
         String var3 = this.handle(var2);
         if (!var3.isBlank()) {
            this.compute("§cНе хватает предмета: §f" + this.process(var3));
         } else {
            for (int var4 = 0; var4 < 9; var4++) {
               String var5 = this.source.handle(var4);
               if (!var5.isBlank()) {
                  int var6 = var4 + 1;
                  this.vectorMatch.add(() -> this.handle(var2, var5, var6));
               }
            }

            this.responseCompute = AutoCraft.Mode.WAITING_RESULT;
            this.latest.handle();
         }
      }
   }

   private void save() {
      CraftingScreen var1 = this.measure();
      if (var1 == null) {
         this.responseCompute = AutoCraft.Mode.FINDING_TABLE;
         this.latest.handle();
      } else if (this.latest.update(Math.max(150, this.matchVector() * 2))) {
         if (!((CraftingScreenHandler)var1.getScreenHandler()).getSlot(0).hasStack()) {
            this.compute("§cРецепт не даёт результат.");
         } else {
            ItemStack var2 = ((CraftingScreenHandler)var1.getScreenHandler()).getSlot(0).getStack().copy();
            int var3 = Math.max(1, var2.getCount());
            int var4 = Math.max(1, this.projectItem() - this.profileDraw);
            int var5 = Math.max(1, (var4 + var3 - 1) / var3);
            this.eventAttach = Math.max(1, Math.min(var5, this.process((CraftingScreenHandler)var1.getScreenHandler())));
            this.vectorPerform = this.eventAttach * var3;
            int var6 = this.eventAttach - 1;
            if (var6 > 0) {
               this.handle((CraftingScreenHandler)var1.getScreenHandler(), var6);
            }

            this.responseCompute = AutoCraft.Mode.TAKING_RESULT;
            this.latest.handle();
         }
      }
   }

   private void submit() {
      CraftingScreen var1 = this.measure();
      if (var1 == null) {
         this.responseCompute = AutoCraft.Mode.FINDING_TABLE;
         this.latest.handle();
      } else if (this.latest.update(this.matchVector())) {
         ItemStack var2 = ((CraftingScreenHandler)var1.getScreenHandler()).getSlot(0).getStack().copy();
         int var3 = Math.max(1, var2.getCount());
         Module.client.interactionManager.clickSlot(((CraftingScreenHandler)var1.getScreenHandler()).syncId, 0, 0, SlotActionType.QUICK_MOVE, Module.client.player);
         this.profileDraw = this.profileDraw + Math.max(var3, this.vectorPerform);
         ChatLogger.handle("§8[§6AutoCraft§8] §aСкрафтил: §f" + Math.min(this.profileDraw, this.projectItem()) + "/" + this.projectItem());
         this.vectorPerform = 0;
         this.eventAttach = 0;
         this.responseCompute = AutoCraft.Mode.CLOSING;
         this.latest.handle();
      }
   }

   private void unload() {
      if (this.latest.update(this.matchVector())) {
         if (this.profileDraw >= this.projectItem()) {
            if (Module.client.player != null) {
               Module.client.player.closeHandledScreen();
            }

            this.positionAdvance = null;
            ChatLogger.handle("§8[§6AutoCraft§8] §aГотово.");
            this.setEnabled(false);
         } else {
            this.responseCompute = AutoCraft.Mode.CLEARING_GRID;
            this.latest.handle();
         }
      }
   }

   private boolean fetch() {
      if (this.vectorMatch.isEmpty()) {
         return false;
      }

      if (!this.latest.update(this.matchVector())) {
         return true;
      }

      this.vectorMatch.poll().run();
      this.latest.handle();
      return true;
   }

   private CraftingScreen measure() {
      CraftingScreen var1 = ContainerScreenPolicy.handle(Module.client, this.positionAdvance, CraftingScreen.class);
      if (var1 == null) {
         this.positionAdvance = null;
      }

      return var1;
   }

   private String handle(CraftingScreenHandler var1) {
      HashMap<String, Integer> var2 = new HashMap<>();

      for (String var6 : this.source.update()) {
         if (var6 != null && !var6.isBlank()) {
            var2.put(var6, var2.getOrDefault(var6, 0) + 1);
         }
      }

      for (Entry var8 : var2.entrySet()) {
         int var9 = this.handle(var1, (String)var8.getKey());
         if (var9 < (Integer)var8.getValue()) {
            return (String)var8.getKey();
         }
      }

      return "";
   }

   private int handle(CraftingScreenHandler var1, String var2) {
      int var3 = 0;

      for (int var4 = 10; var4 < var1.slots.size(); var4++) {
         ItemStack var5 = var1.getSlot(var4).getStack();
         if (this.handle(var5, var2)) {
            var3 += var5.getCount();
         }
      }

      return var3;
   }

   private int process(CraftingScreenHandler var1) {
      HashMap<String, Integer> var2 = new HashMap<>();
      int var3 = 64;

      for (String var7 : this.source.update()) {
         if (var7 != null && !var7.isBlank()) {
            var2.put(var7, var2.getOrDefault(var7, 0) + 1);
            ItemStack var8 = this.handle(var7);
            if (!var8.isEmpty()) {
               var3 = Math.min(var3, var8.getMaxCount());
            }
         }
      }

      int var9 = var3;

      for (Entry var11 : var2.entrySet()) {
         int var12 = (Integer)var11.getValue();
         int var13 = this.handle(var1, (String)var11.getKey()) + var12;
         var9 = Math.min(var9, var13 / (Integer)var11.getValue());
      }

      return Math.max(1, var9);
   }

   private void handle(CraftingScreenHandler var1, int var2) {
      for (int var3 = 0; var3 < 9; var3++) {
         String var4 = this.source.handle(var3);
         if (!var4.isBlank()) {
            int var5 = var3 + 1;
            this.vectorMatch.add(() -> this.handle(var1, var4, var5, var2));
         }
      }
   }

   private void handle(CraftingScreenHandler var1, String var2, int var3) {
      int var4 = this.process(var1, var2);
      if (var4 == -1) {
         this.serverRead = var2;
      } else {
         Module.client.interactionManager.clickSlot(var1.syncId, var4, 0, SlotActionType.PICKUP, Module.client.player);
         Module.client.interactionManager.clickSlot(var1.syncId, var3, 1, SlotActionType.PICKUP, Module.client.player);
         Module.client.interactionManager.clickSlot(var1.syncId, var4, 0, SlotActionType.PICKUP, Module.client.player);
      }
   }

   private void handle(CraftingScreenHandler var1, String var2, int var3, int var4) {
      int var5 = var4;

      while (var5 > 0) {
         int var6 = this.process(var1, var2);
         if (var6 == -1) {
            this.serverRead = var2;
            return;
         }

         Module.client.interactionManager.clickSlot(var1.syncId, var6, 0, SlotActionType.PICKUP, Module.client.player);
         int var7 = var5;
         if (var1.getCursorStack().isEmpty()) {
            this.serverRead = var2;
            return;
         }

         while (var5 > 0 && !var1.getCursorStack().isEmpty()) {
            Module.client.interactionManager.clickSlot(var1.syncId, var3, 1, SlotActionType.PICKUP, Module.client.player);
            var5--;
         }

         if (var5 == var7) {
            this.serverRead = var2;
            return;
         }

         if (!var1.getCursorStack().isEmpty()) {
            Module.client.interactionManager.clickSlot(var1.syncId, var6, 0, SlotActionType.PICKUP, Module.client.player);
         }
      }
   }

   private int process(CraftingScreenHandler var1, String var2) {
      for (int var3 = 10; var3 < var1.slots.size(); var3++) {
         Slot var4 = var1.getSlot(var3);
         if (var4.hasStack() && this.handle(var4.getStack(), var2)) {
            return var3;
         }
      }

      return -1;
   }

   private boolean handle(ItemStack var1, String var2) {
      if (var1 != null && !var1.isEmpty() && var2 != null && !var2.isBlank()) {
         Identifier var3 = Registries.ITEM.getId(var1.getItem());
         return var3 != null && var3.toString().equals(var2);
      } else {
         return false;
      }
   }

   private ItemStack handle(String var1) {
      Identifier var2 = Identifier.tryParse(var1 == null ? "" : var1);
      if (var2 == null) {
         return ItemStack.EMPTY;
      }

      Item var3 = (Item)Registries.ITEM.get(var2);
      return var3 == Items.AIR ? ItemStack.EMPTY : var3.getDefaultStack();
   }

   private BlockPos blendMatrix() {
      BlockPos var1 = Module.client.player.getBlockPos();
      BlockPos var2 = null;
      double var3 = Double.MAX_VALUE;
      byte var5 = 16;

      for (BlockPos var7 : BlockPos.iterate(var1.add(-var5, -5, -var5), var1.add(var5, 5, var5))) {
         if (this.handle(var7)) {
            double var8 = var1.getSquaredDistance(var7);
            if (var8 < var3) {
               var3 = var8;
               var2 = var7.toImmutable();
            }
         }
      }

      return var2;
   }

   private boolean handle(BlockPos var1) {
      return var1 != null && Module.client.world != null && Module.client.world.getBlockState(var1).isOf(Blocks.CRAFTING_TABLE);
   }

   private RotationAngles handle(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      float var9 = (float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F;
      float var10 = (float)(-Math.toDegrees(Math.atan2(var5, Math.sqrt(var3 * var3 + var7 * var7))));
      return new RotationAngles(var9, var10);
   }

   private int matchVector() {
      return Math.max(20, (int)this.pending.compute());
   }

   private int projectItem() {
      String var1 = this.target.compute().trim();
      if (var1.isEmpty()) {
         return 0;
      }

      try {
         return Math.max(0, Math.min(999999, Integer.parseInt(var1)));
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   private String process(String var1) {
      Identifier var2 = Identifier.tryParse(var1);
      if (var2 == null) {
         return var1;
      }

      Item var3 = (Item)Registries.ITEM.get(var2);
      return var3 == Items.AIR ? var1 : var3.getName().getString();
   }

   private void compute(String var1) {
      ChatLogger.handle("§8[§6AutoCraft§8] " + var1);
      this.setEnabled(false);
   }

   enum Mode {
      IDLE,
      FINDING_TABLE,
      GOING_TO_TABLE,
      AIMING_TABLE,
      OPENING_TABLE,
      CLEARING_GRID,
      PLACING_RECIPE,
      WAITING_RESULT,
      TAKING_RESULT,
      CLOSING;
   }
}
