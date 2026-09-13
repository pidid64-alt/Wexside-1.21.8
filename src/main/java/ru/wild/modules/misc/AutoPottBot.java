package ru.wild.modules.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.text.ChatLogger;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(name = "AutoPottBot", category = ModuleCategory.Misc, description = "")
public class AutoPottBot extends Module {
   private static final long profileDraw = 20000L;
   private static final long vectorPerform = 1500L;
   private static final long eventAttach = 4000L;
   private static final long serverRead = 1600L;
   private static final long positionAdvance = 8000L;
   private static final long frameCheck = 15000L;
   private static final int moduleCollect = 3;
   private static final int providerClose = 4;
   private static final int presetSave = 5;
   private static final int windowConvert = 3;
   private static final int presetWrite = 3;
   private static final int colorMeasure = 5;
   public static volatile boolean source;
   public static volatile String target = "—";
   public static volatile int pending;
   public static volatile int previous;
   public static volatile int latest;
   public static volatile int summary;
   public static volatile int matrixBlend;
   public static volatile int vectorMatch;
   public static volatile int[] itemProject = new int[7];
   public static volatile List<AutoPottBot.ColorStop> responseCompute = List.of();
   public static volatile List<String> providerFetch = List.of();
   private final BooleanSetting animationSchedule = new BooleanSetting("Зелье силы", true);
   private final BooleanSetting rendererScan = new BooleanSetting("Зелье скорости", false);
   private final BooleanSetting sourceBuild = new BooleanSetting("Зелье огнестойкости", false);
   private final ChoiceSetting outputCollapse = new ChoiceSetting("Варить", this.animationSchedule, this.rendererScan, this.sourceBuild);
   private final NumberSetting profileInvoke = new NumberSetting("Задержка кликов", 120.0F, 30.0F, 600.0F, 10.0F, false);
   private final NumberSetting sourceSchedule = new NumberSetting("Радиус варок", 4.5F, 2.0F, 6.0F, 0.5F, false);
   private final BooleanSetting timerRender = new BooleanSetting("Наполнять бутылки", true);
   private final NumberSetting scaleSave = new NumberSetting("Буфер воды", 12.0F, 3.0F, 24.0F, 1.0F, false);
   private final BooleanSetting colorCompute = new BooleanSetting("Складывать в сундук", true);
   private final Stopwatch scaleAdapt = new Stopwatch();
   private final Stopwatch textureRun = new Stopwatch();
   private final Map<BlockPos, AutoPottBot.CacheEntry> indexBind = new LinkedHashMap<>();
   private final Map<String, Long> actionRead = new HashMap<>();
   private AutoPottBot.SecondaryMode configCollapse = AutoPottBot.SecondaryMode.SCAN;
   private BlockPos dataValidate;
   private BlockPos scaleRender;
   private BlockPos clientRefresh;
   private int keyFilter;
   private int requestAdapt;
   private int timerMeasure;
   private long vectorEncode;
   private int requestReceive;

   public AutoPottBot() {
      this.handle(this.outputCollapse, this.profileInvoke, this.sourceSchedule, this.timerRender, this.scaleSave, this.colorCompute);
   }

   @Override
   public void handle() {
      this.refresh();
      source = true;
      super.handle();
   }

   @Override
   public void process() {
      source = false;
      if (Module.client.player != null
         && (
            Module.client.player.currentScreenHandler instanceof BrewingStandScreenHandler
               || Module.client.player.currentScreenHandler instanceof GenericContainerScreenHandler
         )) {
         Module.client.player.closeHandledScreen();
      }

      this.refresh();
      super.process();
   }

   private void refresh() {
      this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
      this.dataValidate = null;
      this.scaleRender = null;
      this.clientRefresh = null;
      this.vectorEncode = 0L;
      this.requestAdapt = 0;
      this.requestReceive = 0;
      this.indexBind.clear();
      this.actionRead.clear();
      this.scaleAdapt.handle();
      this.textureRun.handle();
      responseCompute = List.of();
      providerFetch = List.of();
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         this.render();
         switch (this.configCollapse) {
            case SCAN:
               this.tick();
               break;
            case OPENING:
               this.encodePoint();
               break;
            case SERVICING:
               this.animate();
               break;
            case CLOSING:
               this.load();
               break;
            case FILL_WATER:
               this.save();
               break;
            case DEPOSIT_OPEN:
               this.blendMatrix();
               break;
            case DEPOSIT_MOVE:
               this.matchVector();
         }

         this.collectModule();
      }
   }

   private void render() {
      long var1 = System.currentTimeMillis();
      int var3 = (int)Math.ceil(this.sourceSchedule.compute() + 4.0);
      BlockPos var4 = Module.client.player.getBlockPos();

      for (int var5 = -var3; var5 <= var3; var5++) {
         for (int var6 = -var3; var6 <= var3; var6++) {
            for (int var7 = -var3; var7 <= var3; var7++) {
               BlockPos var8 = var4.add(var5, var6, var7);
               if (Module.client.world.getBlockEntity(var8) instanceof BrewingStandBlockEntity) {
                  AutoPottBot.CacheEntry var9 = this.indexBind.get(var8);
                  if (var9 == null) {
                     this.indexBind.put(var8.toImmutable(), new AutoPottBot.CacheEntry(var8.toImmutable()));
                  } else {
                     var9.mode = var1;
                  }
               }
            }
         }
      }

      Iterator var10 = this.indexBind.entrySet().iterator();

      while (var10.hasNext()) {
         AutoPottBot.CacheEntry var11 = (AutoPottBot.CacheEntry)((Entry)var10.next()).getValue();
         if (Module.client.world.getBlockEntity(var11.instance) instanceof BrewingStandBlockEntity) {
            var11.mode = var1;
         } else if (var1 - var11.mode > 8000L) {
            var10.remove();
         }
      }
   }

   private void tick() {
      if (!(Module.client.player.currentScreenHandler instanceof BrewingStandScreenHandler)
         && !(Module.client.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
         if (this.colorCompute.compute() && this.attachEvent() <= 3 && this.readServer() > 0) {
            BlockPos var1 = this.checkFrame();
            if (var1 != null) {
               this.scaleRender = var1;
               this.configCollapse = AutoPottBot.SecondaryMode.DEPOSIT_OPEN;
               this.textureRun.handle();
               this.scaleAdapt.handle();
               return;
            }
         }

         if (this.timerRender.compute()
            && this.process(Items.GLASS_BOTTLE) > 0
            && this.advancePosition() < 3
            && System.currentTimeMillis() >= this.vectorEncode) {
            int var7 = this.submit();
            int var2 = this.advancePosition();
            int var3 = Math.min((int)this.scaleSave.compute(), Math.max(3, var7));
            int var4 = Math.max(0, this.attachEvent() - 5);
            int var5 = Math.min(var3, var2 + var4);
            if (var7 > 0 && var5 > var2) {
               BlockPos var6 = this.unload();
               if (var6 != null) {
                  if (this.fetch()) {
                     this.clientRefresh = var6;
                     this.keyFilter = var5;
                     this.requestAdapt = 0;
                     this.timerMeasure = var2;
                     this.configCollapse = AutoPottBot.SecondaryMode.FILL_WATER;
                     this.scaleAdapt.handle();
                     return;
                  }

                  this.handle("Бутылочки в хотбар");
               }
            }
         }

         AutoPottBot.CacheEntry var8 = this.drawAnimation();
         if (var8 != null) {
            this.dataValidate = var8.instance;
            this.configCollapse = AutoPottBot.SecondaryMode.OPENING;
            this.textureRun.handle();
            this.scaleAdapt.handle();
         }
      } else {
         Module.client.player.closeHandledScreen();
      }
   }

   private AutoPottBot.CacheEntry drawAnimation() {
      long var1 = System.currentTimeMillis();
      Vec3d var3 = Module.client.player.getEyePos();
      double var4 = this.sourceSchedule.compute() * this.sourceSchedule.compute();
      boolean var6 = this.drawProfile();
      AutoPottBot.CacheEntry var7 = null;
      int var8 = -1;
      double var9 = Double.MAX_VALUE;

      for (AutoPottBot.CacheEntry var12 : this.indexBind.values()) {
         double var13 = Vec3d.ofCenter(var12.instance).squaredDistanceTo(var3);
         if (!(var13 > var4) && var1 >= var12.active && (!var12.cache || var1 >= var12.current)) {
            int var15 = this.handle(var12, var6);
            if (var15 > 0 && (var15 > var8 || var15 == var8 && var13 < var9)) {
               var7 = var12;
               var8 = var15;
               var9 = var13;
            }
         }
      }

      return var7;
   }

   private int handle(AutoPottBot.CacheEntry var1, boolean var2) {
      return switch (var1.context) {
         case UNKNOWN -> 2;
         case EMPTY -> var2 ? 1 : 0;
         case WATER, AWKWARD, BASE -> 3;
         case FINAL -> 4;
         default -> 0;
      };
   }

   private void encodePoint() {
      if (Module.client.player.currentScreenHandler instanceof BrewingStandScreenHandler) {
         this.configCollapse = AutoPottBot.SecondaryMode.SERVICING;
         this.scaleAdapt.handle();
      } else if (this.dataValidate == null || !(Module.client.world.getBlockEntity(this.dataValidate) instanceof BrewingStandBlockEntity)) {
         this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
      } else if (this.textureRun.apply(1600L)) {
         AutoPottBot.CacheEntry var1 = this.indexBind.get(this.dataValidate);
         if (var1 != null) {
            var1.active = System.currentTimeMillis() + 4500L;
         }

         this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
      } else {
         if (this.scaleAdapt.apply(450L)) {
            this.compute(this.dataValidate);
            this.scaleAdapt.handle();
         }
      }
   }

   private void animate() {
      if (Module.client.player.currentScreenHandler instanceof BrewingStandScreenHandler var1) {
         AutoPottBot.CacheEntry var6 = this.indexBind.get(this.dataValidate);
         if (var6 == null) {
            this.configCollapse = AutoPottBot.SecondaryMode.CLOSING;
         } else if (this.scaleAdapt.apply((long)this.profileInvoke.compute())) {
            this.scaleAdapt.handle();
            AutoPottBot.Mode var3 = this.handle(var1, var6);
            switch (var3) {
               case CONTINUE:
               default:
                  break;
               case BREW_STARTED:
                  long var7 = System.currentTimeMillis();
                  var6.cache = true;
                  var6.output = var7;
                  var6.current = var7 + 20000L;
                  var6.active = var6.current;
                  this.configCollapse = AutoPottBot.SecondaryMode.CLOSING;
                  break;
               case DONE:
                  long var4 = var6.context == AutoPottBot.FallbackMode.EMPTY ? 4000L : 1500L;
                  var6.active = System.currentTimeMillis() + var4;
                  this.configCollapse = AutoPottBot.SecondaryMode.CLOSING;
            }
         }
      } else {
         this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
      }
   }

   private AutoPottBot.Mode handle(BrewingStandScreenHandler var1, AutoPottBot.CacheEntry var2) {
      boolean var3 = !var1.getSlot(3).getStack().isEmpty();
      int var4 = this.process(var1, var2);
      boolean var5 = var4 >= 0;
      var2.config = var5;
      var2.state = var5 ? Math.min(3, var4 + (var3 ? 1 : 0)) : 0;
      var2.context = this.handle(var4, var3);
      if (var3) {
         var2.cache = true;
         if (var2.current == 0L) {
            var2.output = System.currentTimeMillis();
            var2.current = var2.output + 20000L;
         }

         return AutoPottBot.Mode.DONE;
      } else {
         var2.cache = false;
         if (var2.context == AutoPottBot.FallbackMode.OTHER) {
            return AutoPottBot.Mode.DONE;
         }

         if (var2.context == AutoPottBot.FallbackMode.FINAL) {
            if (this.attachEvent() <= 0) {
               return AutoPottBot.Mode.DONE;
            }

            for (int var10 = 0; var10 < 3; var10++) {
               if (!var1.getSlot(var10).getStack().isEmpty()) {
                  this.handle(var10);
               }
            }

            var2.config = false;
            var2.state = 0;
            var2.context = AutoPottBot.FallbackMode.EMPTY;
            return AutoPottBot.Mode.CONTINUE;
         } else {
            if (!var5) {
               AutoPottBot.PrimaryMode var6 = this.performVector();
               if (var6 == null) {
                  return AutoPottBot.Mode.DONE;
               }

               var2.data = var6;
            }

            if (var1.getFuel() <= 0 && var1.getSlot(4).getStack().isEmpty() && this.handle(Items.BLAZE_POWDER, 4)) {
               return AutoPottBot.Mode.CONTINUE;
            }

            if (this.handle(var1)) {
               int var8 = this.handle(var1x -> this.handle(var1x, Potions.WATER));
               if (var8 != -1) {
                  this.handle(var8);
                  return AutoPottBot.Mode.CONTINUE;
               }

               if (this.process(var1) == 0) {
                  return AutoPottBot.Mode.DONE;
               }
            }

            AutoPottBot.PrimaryMode var9 = var2.data != null ? var2.data : this.performVector();
            if (var9 == null) {
               return AutoPottBot.Mode.DONE;
            }

            var2.data = var9;

            Item var7 = switch (var2.context) {
               case WATER -> Items.NETHER_WART;
               case AWKWARD -> var9.data;
               case BASE -> var9.context;
               default -> null;
            };
            if (var7 == null) {
               return AutoPottBot.Mode.DONE;
            } else if (!this.handle(var7)) {
               return AutoPottBot.Mode.DONE;
            } else {
               return this.handle(var7, 3) ? AutoPottBot.Mode.BREW_STARTED : AutoPottBot.Mode.DONE;
            }
         }
      }
   }

   private void load() {
      Module.client.player.closeHandledScreen();
      this.dataValidate = null;
      this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
   }

   private void save() {
      if (!(Module.client.player.currentScreenHandler instanceof BrewingStandScreenHandler)
         && !(Module.client.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
         if (this.advancePosition() < this.keyFilter && this.process(Items.GLASS_BOTTLE) > 0) {
            if (this.requestAdapt > this.keyFilter * 2 + 20) {
               if (this.advancePosition() <= this.timerMeasure) {
                  this.vectorEncode = System.currentTimeMillis() + 6000L;
                  this.handle("Источник воды недосягаем");
               }

               this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
            } else {
               if (this.clientRefresh == null || !this.handle(this.clientRefresh)) {
                  this.clientRefresh = this.unload();
                  if (this.clientRefresh == null) {
                     this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
                     return;
                  }
               }

               if (!this.measure()) {
                  this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
               } else if (this.scaleAdapt.apply((long)this.profileInvoke.compute())) {
                  this.scaleAdapt.handle();
                  this.process(this.clientRefresh);
                  Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
                  this.requestAdapt++;
               }
            }
         } else {
            this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
         }
      } else {
         Module.client.player.closeHandledScreen();
      }
   }

   private int submit() {
      int var1 = 0;

      for (AutoPottBot.CacheEntry var3 : this.indexBind.values()) {
         if (var3.context == AutoPottBot.FallbackMode.EMPTY || var3.context == AutoPottBot.FallbackMode.UNKNOWN) {
            var1++;
         }
      }

      return var1 * 3;
   }

   private BlockPos unload() {
      BlockPos var1 = Module.client.player.getBlockPos();
      int var2 = (int)Math.ceil(this.sourceSchedule.compute());
      double var3 = this.sourceSchedule.compute() * this.sourceSchedule.compute();
      Vec3d var5 = Module.client.player.getEyePos();
      BlockPos var6 = null;
      double var7 = Double.MAX_VALUE;

      for (int var9 = -var2; var9 <= var2; var9++) {
         for (int var10 = -var2; var10 <= var2; var10++) {
            for (int var11 = -var2; var11 <= var2; var11++) {
               BlockPos var12 = var1.add(var9, var10, var11);
               if (this.handle(var12)) {
                  double var13 = Vec3d.ofCenter(var12).squaredDistanceTo(var5);
                  if (var13 <= var3 && var13 < var7) {
                     var7 = var13;
                     var6 = var12.toImmutable();
                  }
               }
            }
         }
      }

      return var6;
   }

   private boolean handle(BlockPos var1) {
      FluidState var2 = Module.client.world.getFluidState(var1);
      return var2.isStill() && var2.isIn(FluidTags.WATER);
   }

   private boolean fetch() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).getItem() == Items.GLASS_BOTTLE) {
            return true;
         }
      }

      return false;
   }

   private boolean measure() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).getItem() == Items.GLASS_BOTTLE) {
            if (Module.client.player.getInventory().getSelectedSlot() != var1) {
               Module.client.player.getInventory().setSelectedSlot(var1);
            }

            return true;
         }
      }

      return false;
   }

   private void process(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.getX() + 0.5 - var2.x;
      double var5 = var1.getY() + 0.5 - var2.y;
      double var7 = var1.getZ() + 0.5 - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)(Math.toDegrees(Math.atan2(var7, var3)) - 90.0);
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      Module.client.player.setYaw(var11);
      Module.client.player.setPitch(Math.max(-90.0F, Math.min(90.0F, var12)));
   }

   private void handle(String var1) {
      long var2 = System.currentTimeMillis();
      Long var4 = this.actionRead.get(var1);
      if (var4 == null || var2 - var4 > 15000L) {
         this.actionRead.put(var1, var2);
         ChatLogger.handle("§8[AutoPottBot] §c" + var1);
      }
   }

   private void blendMatrix() {
      if (Module.client.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
         this.configCollapse = AutoPottBot.SecondaryMode.DEPOSIT_MOVE;
         this.scaleAdapt.handle();
      } else if (this.scaleRender == null || !(Module.client.world.getBlockEntity(this.scaleRender) instanceof ChestBlockEntity)) {
         this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
      } else if (this.textureRun.apply(1600L)) {
         this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
      } else {
         if (this.scaleAdapt.apply(450L)) {
            this.compute(this.scaleRender);
            this.scaleAdapt.handle();
         }
      }
   }

   private void matchVector() {
      if (Module.client.player.currentScreenHandler instanceof GenericContainerScreenHandler var1) {
         if (this.scaleAdapt.apply((long)this.profileInvoke.compute())) {
            this.scaleAdapt.handle();
            int var4 = var1.getRows() * 9;

            for (int var3 = var4; var3 < var1.slots.size(); var3++) {
               if (this.compute(((Slot)var1.slots.get(var3)).getStack())) {
                  this.handle(var3, 0, SlotActionType.QUICK_MOVE);
                  return;
               }
            }

            Module.client.player.closeHandledScreen();
            this.scaleRender = null;
            this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
         }
      } else {
         this.configCollapse = AutoPottBot.SecondaryMode.SCAN;
      }
   }

   private List<AutoPottBot.PrimaryMode> projectItem() {
      ArrayList var1 = new ArrayList(3);
      if (this.animationSchedule.compute()) {
         var1.add(AutoPottBot.PrimaryMode.STRENGTH);
      }

      if (this.rendererScan.compute()) {
         var1.add(AutoPottBot.PrimaryMode.SWIFTNESS);
      }

      if (this.sourceBuild.compute()) {
         var1.add(AutoPottBot.PrimaryMode.FIRE_RESISTANCE);
      }

      return var1;
   }

   private Map<Item, Integer> computeResponse() {
      HashMap var1 = new HashMap();

      for (AutoPottBot.CacheEntry var3 : this.indexBind.values()) {
         if (var3.config && var3.data != null) {
            if (var3.state < 1) {
               handle(var1, Items.NETHER_WART, 1);
            }

            if (var3.state < 2) {
               handle(var1, var3.data.data, 1);
            }

            if (var3.state < 3) {
               handle(var1, var3.data.context, 1);
            }
         }
      }

      return var1;
   }

   private List<AutoPottBot.PrimaryMode> fetchProvider() {
      Map<Item, Integer> var1 = this.computeResponse();
      int var2 = this.advancePosition();
      ArrayList<AutoPottBot.PrimaryMode> var3 = new ArrayList<>(3);

      for (AutoPottBot.PrimaryMode var5 : this.projectItem()) {
         int var6 = this.process(Items.NETHER_WART) - var1.getOrDefault(Items.NETHER_WART, 0);
         int var7 = this.process(var5.data) - var1.getOrDefault(var5.data, 0);
         int var8 = this.process(var5.context) - var1.getOrDefault(var5.context, 0);
         if (var2 >= 1 && var6 >= 1 && var7 >= 1 && var8 >= 1) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private boolean drawProfile() {
      return !this.fetchProvider().isEmpty();
   }

   private AutoPottBot.PrimaryMode performVector() {
      List var1 = this.fetchProvider();
      if (var1.isEmpty()) {
         return null;
      }

      AutoPottBot.PrimaryMode var2 = (AutoPottBot.PrimaryMode)var1.get(Math.floorMod(this.requestReceive, var1.size()));
      this.requestReceive++;
      return var2;
   }

   private int process(BrewingStandScreenHandler var1, AutoPottBot.CacheEntry var2) {
      RegistryEntry var3 = null;
      int var4 = 0;

      for (int var5 = 0; var5 < 3; var5++) {
         ItemStack var6 = var1.getSlot(var5).getStack();
         if (var6.getItem() == Items.POTION) {
            var4++;
            if (var3 == null) {
               var3 = this.process(var6);
            }
         }
      }

      if (var4 == 0 || var3 == null) {
         return -1;
      }

      if (this.handle(var3, Potions.WATER)) {
         return 0;
      }

      if (this.handle(var3, Potions.AWKWARD)) {
         return 1;
      }

      for (AutoPottBot.PrimaryMode var8 : AutoPottBot.PrimaryMode.values()) {
         if (this.handle(var3, var8.state)) {
            var2.data = var8;
            return 3;
         }

         if (this.handle(var3, var8.config)) {
            var2.data = var8;
            return 2;
         }
      }

      return -2;
   }

   private AutoPottBot.FallbackMode handle(int var1, boolean var2) {
      return switch (var1) {
         case -1 -> AutoPottBot.FallbackMode.EMPTY;
         case 0 -> AutoPottBot.FallbackMode.WATER;
         case 1 -> AutoPottBot.FallbackMode.AWKWARD;
         case 2 -> AutoPottBot.FallbackMode.BASE;
         case 3 -> AutoPottBot.FallbackMode.FINAL;
         default -> AutoPottBot.FallbackMode.OTHER;
      };
   }

   private boolean handle(BrewingStandScreenHandler var1) {
      for (int var2 = 0; var2 < 3; var2++) {
         if (var1.getSlot(var2).getStack().isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private int process(BrewingStandScreenHandler var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 3; var3++) {
         if (!var1.getSlot(var3).getStack().isEmpty()) {
            var2++;
         }
      }

      return var2;
   }

   private boolean handle(Item var1, int var2) {
      int var3 = this.handle(var1x -> var1x.getItem() == var1);
      if (var3 == -1) {
         return false;
      }

      this.handle(var3, 0, SlotActionType.PICKUP);
      this.handle(var2, 1, SlotActionType.PICKUP);
      this.handle(var3, 0, SlotActionType.PICKUP);
      return true;
   }

   private int handle(Predicate<ItemStack> var1) {
      ScreenHandler var2 = Module.client.player.currentScreenHandler;

      for (int var3 = 5; var3 < var2.slots.size(); var3++) {
         ItemStack var4 = ((Slot)var2.slots.get(var3)).getStack();
         if (!var4.isEmpty() && var1.test(var4)) {
            return var3;
         }
      }

      return -1;
   }

   private boolean handle(Item var1) {
      return this.handle(var1x -> var1x.getItem() == var1) != -1;
   }

   private boolean handle(ItemStack var1, RegistryEntry<Potion> var2) {
      if (var1.getItem() != Items.POTION) {
         return false;
      }

      RegistryEntry var3 = this.process(var1);
      return var3 != null && this.handle(var3, var2);
   }

   private boolean handle(ItemStack var1) {
      return var1.getItem() == Items.POTION || var1.getItem() == Items.SPLASH_POTION || var1.getItem() == Items.LINGERING_POTION;
   }

   private RegistryEntry<Potion> process(ItemStack var1) {
      PotionContentsComponent var2 = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
      return var2 != null && !var2.potion().isEmpty() ? (RegistryEntry)var2.potion().get() : null;
   }

   private boolean handle(RegistryEntry<Potion> var1, RegistryEntry<Potion> var2) {
      return var1 == var2 || var1.getKey().isPresent() && var2.getKey().isPresent() && ((RegistryKey)var1.getKey().get()).equals(var2.getKey().get());
   }

   private int attachEvent() {
      int var1 = 0;

      for (int var2 = 0; var2 < Module.client.player.getInventory().size(); var2++) {
         if (Module.client.player.getInventory().getStack(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   private boolean compute(ItemStack var1) {
      if (!this.handle(var1)) {
         return false;
      }

      RegistryEntry var2 = this.process(var1);
      return var2 == null ? false : !this.handle(var2, Potions.WATER) && !this.handle(var2, Potions.AWKWARD);
   }

   private int readServer() {
      int var1 = 0;

      for (int var2 = 0; var2 < Module.client.player.getInventory().size(); var2++) {
         if (this.compute(Module.client.player.getInventory().getStack(var2))) {
            var1++;
         }
      }

      return var1;
   }

   private int process(Item var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < Module.client.player.getInventory().size(); var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (var4.getItem() == var1) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private int advancePosition() {
      int var1 = 0;

      for (int var2 = 0; var2 < Module.client.player.getInventory().size(); var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (var3.getItem() == Items.POTION) {
            RegistryEntry var4 = this.process(var3);
            if (var4 != null && this.handle(var4, Potions.WATER)) {
               var1 += var3.getCount();
            }
         }
      }

      return var1;
   }

   private BlockPos checkFrame() {
      BlockPos var1 = Module.client.player.getBlockPos();
      int var2 = (int)Math.ceil(this.sourceSchedule.compute() + 1.0);
      BlockPos var3 = null;
      double var4 = Double.MAX_VALUE;
      Vec3d var6 = Module.client.player.getEyePos();

      for (int var7 = -var2; var7 <= var2; var7++) {
         for (int var8 = -var2; var8 <= var2; var8++) {
            for (int var9 = -var2; var9 <= var2; var9++) {
               BlockPos var10 = var1.add(var7, var8, var9);
               if (Module.client.world.getBlockEntity(var10) instanceof ChestBlockEntity) {
                  double var11 = Vec3d.ofCenter(var10).squaredDistanceTo(var6);
                  if (var11 < var4) {
                     var4 = var11;
                     var3 = var10.toImmutable();
                  }
               }
            }
         }
      }

      return var3;
   }

   private void compute(BlockPos var1) {
      Vec3d var2 = new Vec3d(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5);
      BlockHitResult var3 = new BlockHitResult(var2, Direction.UP, var1, false);
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var3);
   }

   private void handle(int var1) {
      this.handle(var1, 0, SlotActionType.QUICK_MOVE);
   }

   private void handle(int var1, int var2, SlotActionType var3) {
      Module.client.interactionManager.clickSlot(Module.client.player.currentScreenHandler.syncId, var1, var2, var3, Module.client.player);
   }

   private void collectModule() {
      long var1 = System.currentTimeMillis();
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      ArrayList<AutoPottBot.ColorStop> var6 = new ArrayList<>(this.indexBind.size());

      for (AutoPottBot.CacheEntry var8 : this.indexBind.values()) {
         if (var8.context == AutoPottBot.FallbackMode.FINAL) {
            var5++;
         } else if (var8.cache && var1 < var8.current) {
            var3++;
         } else if (var8.context != AutoPottBot.FallbackMode.OTHER) {
            var4++;
         }

         var6.add(new AutoPottBot.ColorStop(var8.process(), var8.handle(), var8.handle(var1), var8.process(var1)));
      }

      var6.sort((var0, var1x) -> Float.compare(var1x.progress(), var0.progress()));
      itemProject = new int[]{
         this.advancePosition(),
         this.process(Items.NETHER_WART),
         this.process(Items.BLAZE_POWDER),
         this.process(Items.GLOWSTONE_DUST),
         this.process(Items.SUGAR),
         this.process(Items.MAGMA_CREAM),
         this.process(Items.REDSTONE)
      };
      vectorMatch = this.process(Items.GLASS_BOTTLE);
      pending = this.indexBind.size();
      previous = var3;
      latest = var4;
      summary = var5;
      matrixBlend = this.closeProvider();
      responseCompute = var6;
      target = this.configCollapse.instance;
      providerFetch = this.savePreset();
      this.convertWindow();
   }

   private int closeProvider() {
      List<AutoPottBot.PrimaryMode> var1 = this.projectItem();
      if (var1.isEmpty()) {
         return 0;
      }

      Map<Item, Integer> var2 = this.computeResponse();
      int var3 = this.advancePosition();
      int var4 = Math.max(0, this.process(Items.NETHER_WART) - var2.getOrDefault(Items.NETHER_WART, 0));
      int var5 = 0;

      for (AutoPottBot.PrimaryMode var7 : var1) {
         int var8 = this.process(var7.data) - var2.getOrDefault(var7.data, 0);
         int var9 = this.process(var7.context) - var2.getOrDefault(var7.context, 0);
         var5 += Math.max(0, Math.min(var8, var9));
      }

      var5 = Math.min(var5, var4);
      return Math.max(0, Math.min(var3, var5 * 3));
   }

   private List<String> savePreset() {
      List<AutoPottBot.PrimaryMode> var1 = this.projectItem();
      if (var1.isEmpty()) {
         return List.of("Не выбрано зелье");
      }

      Map<Item, Integer> var2 = this.computeResponse();
      ArrayList var3 = new ArrayList();
      if (this.advancePosition() < 1) {
         boolean var4 = this.timerRender.compute() && this.process(Items.GLASS_BOTTLE) > 0;
         if (!var4) {
            var3.add(this.process(Items.GLASS_BOTTLE) > 0 ? "Источник воды" : "Вода / Бутылочки");
         }
      }

      if (this.process(Items.NETHER_WART) - var2.getOrDefault(Items.NETHER_WART, 0) < 1) {
         var3.add("Адский нарост");
      }

      for (AutoPottBot.PrimaryMode var5 : var1) {
         if (this.process(var5.data) - var2.getOrDefault(var5.data, 0) < 1) {
            handle(var3, var5.data.getName().getString());
         }

         if (this.process(var5.context) - var2.getOrDefault(var5.context, 0) < 1) {
            handle(var3, var5.context.getName().getString());
         }
      }

      if (this.process(Items.BLAZE_POWDER) <= 0) {
         handle(var3, "Огненный порошок (топливо)");
      }

      return var3;
   }

   private void convertWindow() {
      boolean var1 = false;

      for (AutoPottBot.CacheEntry var3 : this.indexBind.values()) {
         if (var3.context == AutoPottBot.FallbackMode.EMPTY || var3.context == AutoPottBot.FallbackMode.UNKNOWN) {
            var1 = true;
            break;
         }
      }

      if (var1 && !providerFetch.isEmpty()) {
         long var7 = System.currentTimeMillis();

         for (String var5 : providerFetch) {
            Long var6 = this.actionRead.get(var5);
            if (var6 == null || var7 - var6 > 15000L) {
               this.actionRead.put(var5, var7);
               ChatLogger.handle("§8[AutoPottBot] §cНе хватает: §f" + var5);
            }
         }
      }
   }

   private static void handle(Map<Item, Integer> var0, Item var1, int var2) {
      var0.merge(var1, var2, Integer::sum);
   }

   private static void handle(List<String> var0, String var1) {
      if (!var0.contains(var1)) {
         var0.add(var1);
      }
   }

   static final class CacheEntry {
      final BlockPos instance;
      AutoPottBot.PrimaryMode data;
      AutoPottBot.FallbackMode context = AutoPottBot.FallbackMode.UNKNOWN;
      boolean config;
      int state;
      boolean cache;
      long output;
      long current;
      long active;
      long mode = System.currentTimeMillis();

      CacheEntry(BlockPos var1) {
         this.instance = var1;
      }

      float handle(long var1) {
         if (this.context == AutoPottBot.FallbackMode.FINAL) {
            return 1.0F;
         } else if (this.cache && this.current > this.output) {
            float var3 = (float)(var1 - this.output) / (float)(this.current - this.output);
            return var3 < 0.0F ? 0.0F : Math.min(var3, 1.0F);
         } else {
            return 0.0F;
         }
      }

      int handle() {
         if (this.context == AutoPottBot.FallbackMode.FINAL) {
            return 5954680;
         } else {
            return this.data != null ? this.data.cache : 9868960;
         }
      }

      String process() {
         return this.data != null ? this.data.instance : "—";
      }

      String process(long var1) {
         if (this.context == AutoPottBot.FallbackMode.FINAL) {
            return this.process() + " ✓";
         } else if (this.cache && var1 < this.current) {
            return this.process() + " " + (int)(this.handle(var1) * 100.0F) + "%";
         } else if (this.context == AutoPottBot.FallbackMode.EMPTY) {
            return "Свободна";
         } else {
            return this.context == AutoPottBot.FallbackMode.UNKNOWN ? "…" : this.process() + " готова";
         }
      }
   }

   public record ColorStop(String name, int color, float progress, String label) {
   }

   enum FallbackMode {
      UNKNOWN,
      EMPTY,
      WATER,
      AWKWARD,
      BASE,
      FINAL,
      OTHER;
   }

   enum Mode {
      CONTINUE,
      BREW_STARTED,
      DONE;
   }

   enum PrimaryMode {
      STRENGTH("Сила", Items.BLAZE_POWDER, Items.GLOWSTONE_DUST, Potions.STRENGTH, Potions.STRONG_STRENGTH, 14042437),
      SWIFTNESS("Скорость", Items.SUGAR, Items.GLOWSTONE_DUST, Potions.SWIFTNESS, Potions.STRONG_SWIFTNESS, 5227511),
      FIRE_RESISTANCE("Огнестойкость", Items.MAGMA_CREAM, Items.REDSTONE, Potions.FIRE_RESISTANCE, Potions.LONG_FIRE_RESISTANCE, 16750592);

      final String instance;
      final Item data;
      final Item context;
      final RegistryEntry<Potion> config;
      final RegistryEntry<Potion> state;
      final int cache;

      PrimaryMode(String var3, Item var4, Item var5, RegistryEntry<Potion> var6, RegistryEntry<Potion> var7, int var8) {
         this.instance = var3;
         this.data = var4;
         this.context = var5;
         this.config = var6;
         this.state = var7;
         this.cache = var8;
      }
   }

   enum SecondaryMode {
      SCAN("Поиск"),
      OPENING("Открытие"),
      SERVICING("Загрузка"),
      CLOSING("Закрытие"),
      FILL_WATER("Налив воды"),
      DEPOSIT_OPEN("Сундук"),
      DEPOSIT_MOVE("Разгрузка");

      final String instance;

      SecondaryMode(String var3) {
         this.instance = var3;
      }
   }
}
