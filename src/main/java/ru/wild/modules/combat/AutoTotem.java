package ru.wild.modules.combat;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PlayerHeadItem;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.Box;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.modules.movement.Sprint;
import ru.wild.util.math.Stopwatch;

@ModuleRegister(
   name = "AutoTotem",
   description = "Автоматически берет тотем в левую руку",
   category = ModuleCategory.Combat,
   flags = ModuleFlag.GRIM
)
public class AutoTotem extends Module {
   private static final String target = "Сохрянять талисманы";
   private static final String pending = "Не свапать если в КД";
   private final ChoiceSetting previous = new ChoiceSetting(
      "Настройки",
      new BooleanSetting("Здоровье с элитрами", true),
      new BooleanSetting("Динамит", true),
      new BooleanSetting("Падение", false),
      new BooleanSetting("Эндер-кристалл", false),
      new BooleanSetting("Не свапать если в КД", false),
      new BooleanSetting("Сохрянять талисманы", true)
   );
   private final NumberSetting latest = new NumberSetting("Здоровье", 4.0F, 1.0F, 20.0F, 0.5F, false);
   private final NumberSetting summary = new NumberSetting("Здоровье на элитре", 9.0F, 0.0F, 20.0F, 0.5F, false)
      .handle(() -> !this.previous.process("Здоровье с элитрами"));
   private final NumberSetting matrixBlend = new NumberSetting("Дистанция до кристалла", 4.0F, 1.0F, 10.0F, 1.0F, false)
      .handle(() -> !this.previous.process("Эндер-кристалл"));
   private final NumberSetting vectorMatch = new NumberSetting("Дистанция до динамита", 30.0F, 3.0F, 50.0F, 1.0F, false)
      .handle(() -> !this.previous.process("Динамит"));
   private final BooleanSetting itemProject = new BooleanSetting("Не свапать если шар", false);
   private int responseCompute = -1;
   private boolean providerFetch = false;
   private AutoTotem.Mode profileDraw = AutoTotem.Mode.IDLE;
   private final Stopwatch vectorPerform = new Stopwatch();
   private int eventAttach = -1;
   private boolean serverRead = false;
   public static boolean source = false;

   public AutoTotem() {
      this.handle(this.previous, this.latest, this.summary, this.matrixBlend, this.vectorMatch, this.itemProject);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player == null || !Module.client.player.isAlive() || Module.client.world == null) {
         this.encodePoint();
      } else if (this.profileDraw != AutoTotem.Mode.IDLE) {
         Sprint.latest = 2;
         Module.client.options.sprintKey.setPressed(false);
         Module.client.player.setSprinting(false);
         this.compute(false);
         this.render();
      } else {
         this.tick();
      }
   }

   private void render() {
      switch (this.profileDraw) {
         case PREPARE:
            if (this.vectorPerform.handle(20L)) {
               this.vectorPerform.handle();
               this.profileDraw = AutoTotem.Mode.SWAP;
            }
            break;
         case SWAP:
            if (!Module.client.player.isSprinting()) {
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, this.eventAttach, 40, SlotActionType.SWAP, Module.client.player);
            }

            Module.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
            if (this.vectorPerform.handle(30L)) {
               this.vectorPerform.handle();
               this.profileDraw = this.serverRead ? AutoTotem.Mode.RESTORE : AutoTotem.Mode.COOLDOWN;
            }
            break;
         case RESTORE:
            if (this.vectorPerform.handle(30L)) {
               this.vectorPerform.handle();
               this.animate();
               this.profileDraw = AutoTotem.Mode.COOLDOWN;
            }
            break;
         case COOLDOWN:
            if (this.vectorPerform.handle(40L)) {
               this.compute(true);
               this.profileDraw = AutoTotem.Mode.IDLE;
               source = false;
            }
      }
   }

   private void tick() {
      boolean var1 = this.submit();
      ItemStack var2 = Module.client.player.getOffHandStack();
      boolean var3 = this.handle(var2);
      boolean var4 = this.previous.process("Не свапать если в КД");
      if (var4 && var3 && this.compute(var2)) {
         if (this.responseCompute != -1 && this.providerFetch) {
            this.eventAttach = this.responseCompute;
            this.serverRead = true;
            this.drawAnimation();
         } else {
            this.animate();
         }
      } else {
         boolean var5 = var1 && this.previous.process("Сохрянять талисманы") && this.resolve(var2);
         if (var1 && (!var3 || var5)) {
            int var6 = var5 ? this.save() : this.load();
            if (var6 >= 0) {
               if (!this.providerFetch) {
                  this.responseCompute = var6;
                  this.providerFetch = true;
               }

               this.eventAttach = var6;
               this.serverRead = false;
               this.drawAnimation();
            }
         } else if (!var1 && this.responseCompute != -1 && this.providerFetch) {
            if (Module.client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
               this.eventAttach = this.responseCompute;
               this.serverRead = true;
               this.drawAnimation();
            } else {
               this.animate();
            }
         }
      }
   }

   private void drawAnimation() {
      this.vectorPerform.handle();
      this.profileDraw = AutoTotem.Mode.PREPARE;
      source = true;
   }

   private void compute(boolean var1) {
      if (Module.client.getWindow() != null) {
         KeyBinding[] var2 = new KeyBinding[]{
            Module.client.options.forwardKey, Module.client.options.backKey, Module.client.options.leftKey, Module.client.options.rightKey, Module.client.options.jumpKey
         };
         long var3 = Module.client.getWindow().getHandle();

         for (KeyBinding var8 : var2) {
            boolean var9 = var1 && InputUtil.isKeyPressed(var3, var8.getDefaultKey().getCode());
            var8.setPressed(var9);
         }
      }
   }

   private void encodePoint() {
      this.compute(true);
      this.profileDraw = AutoTotem.Mode.IDLE;
      this.vectorPerform.handle();
      this.animate();
      source = false;
   }

   private void animate() {
      this.responseCompute = -1;
      this.providerFetch = false;
      this.serverRead = false;
   }

   private int load() {
      int var1 = this.save();
      if (var1 >= 0) {
         return var1;
      }

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (this.process(var3)) {
            return var2 < 9 ? var2 + 36 : var2;
         }
      }

      return -1;
   }

   private int save() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (this.process(var2) && !this.resolve(var2)) {
            return var1 < 9 ? var1 + 36 : var1;
         }
      }

      return -1;
   }

   public boolean refresh() {
      ItemStack var1 = Module.client.player.getOffHandStack();
      return this.handle(var1);
   }

   private boolean handle(ItemStack var1) {
      return var1 != null && var1.isOf(Items.TOTEM_OF_UNDYING);
   }

   private boolean process(ItemStack var1) {
      return this.handle(var1) && (!this.previous.process("Не свапать если в КД") || !this.compute(var1));
   }

   private boolean compute(ItemStack var1) {
      return Module.client.player != null && var1 != null && !var1.isEmpty() && Module.client.player.getItemCooldownManager().isCoolingDown(var1);
   }

   private boolean resolve(ItemStack var1) {
      return this.handle(var1) && (var1.hasEnchantments() || var1.hasGlint());
   }

   private boolean submit() {
      return this.unload()
         || this.measure()
         || this.blendMatrix()
         || this.fetch()
         || Module.client.player.getHealth() + Module.client.player.getAbsorptionAmount() <= this.latest.compute();
   }

   private boolean unload() {
      ItemStack var1 = Module.client.player.getEquippedStack(EquipmentSlot.CHEST);
      return var1.getItem() == Items.ELYTRA
         && this.previous.process("Здоровье с элитрами")
         && Module.client.player.getHealth() + Module.client.player.getAbsorptionAmount() <= this.summary.compute();
   }

   private boolean fetch() {
      return this.previous.process("Падение") && Module.client.player.fallDistance > 12.0;
   }

   private boolean measure() {
      if (!this.previous.process("Эндер-кристалл")) {
         return false;
      }

      double var1 = this.matrixBlend.compute() * this.matrixBlend.compute();
      Box var3 = Module.client.player.getBoundingBox().expand(this.matrixBlend.compute());
      boolean var4 = !Module.client.world.getEntitiesByClass(EndCrystalEntity.class, var3, var2 -> var2.squaredDistanceTo(Module.client.player) <= var1).isEmpty();
      if (var4) {
         if (!(Module.client.player.getOffHandStack().getItem() instanceof PlayerHeadItem)) {
            return true;
         }

         if (!this.itemProject.compute()) {
            return true;
         }
      }

      return false;
   }

   private boolean blendMatrix() {
      if (!this.previous.process("Динамит")) {
         return false;
      }

      double var1 = this.vectorMatch.compute() * this.vectorMatch.compute();
      Box var3 = Module.client.player.getBoundingBox().expand(this.vectorMatch.compute());
      return !Module.client.world
         .getEntitiesByClass(
            Entity.class, var3, var2 -> (var2 instanceof TntEntity || var2 instanceof TntMinecartEntity) && var2.squaredDistanceTo(Module.client.player) <= var1
         )
         .isEmpty();
   }

   @Override
   public void process() {
      super.process();
      this.encodePoint();
   }

   enum Mode {
      IDLE,
      PREPARE,
      SWAP,
      RESTORE,
      COOLDOWN;
   }
}
