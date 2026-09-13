package ru.wild.modules.player;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.core.manager.FriendManager;
import ru.wild.util.inventory.InventorySlotActions;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.NicknameUtil;
import ru.wild.util.player.SyntheticKeyState;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "PlayerHelper", category = ModuleCategory.Player, description = "Полезные твики для игрока")
public class PlayerHelper extends Module {
   private static final String presetWrite = "PlayerHelper_AutoArmor";
   public final ModeSetting source = new ModeSetting("Режим ресурс паков", "Load", "Load", "Skip", "Vanilla");
   public final BooleanSetting target = new BooleanSetting("Авто респавн", true);
   public final BooleanSetting pending = new BooleanSetting("Скип ресурс паков", true);
   public final BooleanSetting previous = new BooleanSetting("Писать координаты смерти", false);
   public final BooleanSetting latest = new BooleanSetting("Автоматически кушать", false);
   public final NumberSetting summary = new NumberSetting("Порог голода", 10.0F, 1.0F, 20.0F, 1.0F, false).handle(() -> !this.latest.compute());
   public final BooleanSetting matrixBlend = new BooleanSetting("Отправлять координаты", false);
   public final ModeSetting vectorMatch = new ModeSetting("Кому отправлять: ", "СОО.Клановцам", "Друзьям", "Общий чат", "СОО.Клановцам")
      .handle(() -> !this.matrixBlend.compute());
   public final KeybindSetting itemProject = new KeybindSetting("Бинд на отправку", -1).handle(() -> !this.matrixBlend.compute());
   public final BooleanSetting responseCompute = new BooleanSetting("Не ломать предмет", false);
   public final BooleanSetting providerFetch = new BooleanSetting("Автоматически чинить", false);
   public final NumberSetting profileDraw = new NumberSetting("Порог прочности", 100.0F, 1.0F, 500.0F, 1.0F, false).handle(() -> !this.providerFetch.compute());
   public final BooleanSetting vectorPerform = new BooleanSetting("AutoArmor", false);
   public final NumberSetting eventAttach = new NumberSetting("Скорость надевания", 150.0F, 50.0F, 1000.0F, 50.0F, false)
      .handle(() -> !this.vectorPerform.compute());
   public final KeybindSetting serverRead = new KeybindSetting("Бинд зума", -1, true);
   public final BooleanSetting positionAdvance = new BooleanSetting("При заходе на новую анархию писать /event delay", true);
   public final BooleanSetting frameCheck = new BooleanSetting("Перезаход при афк", true);
   private int colorMeasure = -1;
   private boolean animationSchedule = false;
   public static boolean moduleCollect = false;
   public static boolean providerClose = false;
   private int rendererScan = -1;
   private float sourceBuild = 0.0F;
   public static boolean presetSave = false;
   public static float windowConvert = 0.25F;
   private final Stopwatch outputCollapse = new Stopwatch();
   private final Stopwatch profileInvoke = new Stopwatch();
   private PlayerHelper.DataRecord sourceSchedule = null;
   private int timerRender = 0;
   private int scaleSave = 0;
   private String colorCompute = "N/A";
   private String scaleAdapt = "N/A";
   private String textureRun = "N/A";
   private boolean indexBind = false;

   public PlayerHelper() {
      this.handle(
         this.target,
         this.source,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform,
         this.eventAttach,
         this.serverRead,
         this.positionAdvance,
         this.frameCheck
      );
   }

   @Override
   public void handle(JsonObject var1) {
      super.handle(var1);
      if (var1 != null) {
         JsonObject var2 = null;

         try {
            var2 = var1.getAsJsonObject("Settings");
         } catch (Throwable var5) {
         }

         if (var2 != null && !var2.has(this.source.instance) && var2.has(this.pending.instance)) {
            try {
               boolean var3 = var2.get(this.pending.instance).getAsBoolean();
               this.source.state = var3 ? "Skip" : "Load";
               this.source.current = this.source.config.indexOf(this.source.state);
            } catch (Throwable var4) {
            }
         }
      }
   }

   public static boolean refresh() {
      return moduleCollect || providerClose;
   }

   @Override
   public void handle() {
      super.handle();
      this.colorCompute = "N/A";
      this.scaleAdapt = "N/A";
      this.textureRun = "N/A";
      this.indexBind = false;
      this.outputCollapse.handle();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!ServerEnvironment.handle() && Module.client.player != null && Module.client.world != null) {
         this.encodePoint();
         this.render();
         this.tick();
         if (!(Module.client.player.getHealth() <= 0.0F) && !(Module.client.currentScreen instanceof DeathScreen)) {
            if (this.latest.compute()) {
               this.fetchProvider();
            }

            if (this.responseCompute.compute()) {
               this.animate();
            }

            if (this.providerFetch.compute() && !moduleCollect) {
               this.load();
            }

            if (this.vectorPerform.compute()) {
               this.fetch();
            }
         } else {
            if (this.previous.compute() && Module.client.player.deathTime < 2) {
               Module.client.player
                  .sendMessage(
                     Text.of(
                        String.format(
                           "§cDeathCoords: §fX: %d Y: %d Z: %d", (int)Module.client.player.getX(), (int)Module.client.player.getY(), (int)Module.client.player.getZ()
                        )
                     ),
                     false
                  );
            }

            if (this.target.compute()) {
               Module.client.player.requestRespawn();
               Module.client.setScreen(null);
            }

            this.performVector();
            this.unload();
            this.blendMatrix();
         }
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (Module.client.currentScreen == null && Module.client.player != null && var1.apply() == 1) {
         if (var1.resolve() == this.itemProject.compute() && this.itemProject.compute() != -1 && this.matrixBlend.compute()) {
            this.computeResponse();
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null) {
         if (this.source.process("Skip") && var1.resolve() instanceof ResourcePackSendS2CPacket var2) {
            Module.client.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(var2.id(), Status.ACCEPTED));
            Module.client.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(var2.id(), Status.SUCCESSFULLY_LOADED));
            var1.process();
         }

         if (this.frameCheck.compute() && var1.resolve() instanceof GameMessageS2CPacket var4) {
            String var7 = var4.content().getString();
            if (this.handle(var7)) {
               this.drawAnimation();
            }
         }

         if (this.responseCompute.compute()) {
            ItemStack var5 = Module.client.player.getMainHandStack();
            if (this.handle(var5)
               && (
                  var1.resolve() instanceof PlayerActionC2SPacket
                     || var1.resolve() instanceof PlayerInteractBlockC2SPacket
                     || var1.resolve() instanceof PlayerInteractEntityC2SPacket
                     || var1.resolve() instanceof PlayerInteractItemC2SPacket
               )) {
               var1.process();
            }
         }
      }
   }

   private void render() {
      NicknameUtil.instance.handle(200L);
      String var1 = this.process(NicknameUtil.instance.compute());
      if (this.compute(var1)) {
         boolean var2 = !var1.equals(this.colorCompute);
         this.colorCompute = var1;
         if (this.positionAdvance.compute() && !var1.equals(this.scaleAdapt) && Module.client.player.networkHandler != null) {
            Module.client.player.networkHandler.sendChatCommand("event delay");
            this.scaleAdapt = var1;
         }
      }
   }

   private void tick() {
      if (this.indexBind && Module.client.player != null && Module.client.player.networkHandler != null && !NicknameUtil.process()) {
         if (this.outputCollapse.update(1000L)) {
            Module.client.player.networkHandler.sendChatCommand("an" + this.textureRun);
            this.indexBind = false;
            this.outputCollapse.handle();
         }
      }
   }

   private void drawAnimation() {
      if (!this.indexBind && Module.client.player != null && Module.client.player.networkHandler != null) {
         NicknameUtil.instance.handle();
         String var1 = this.process(NicknameUtil.instance.compute());
         this.textureRun = this.compute(var1) ? var1 : this.colorCompute;
         if (this.compute(this.textureRun)) {
            if (Module.client.currentScreen != null) {
               Module.client.player.closeScreen();
            }

            Module.client.player.networkHandler.sendChatCommand("hub");
            this.indexBind = true;
            this.outputCollapse.handle();
         }
      }
   }

   private boolean handle(String var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = var1.replaceAll("§.", "").toLowerCase(Locale.ROOT);
      return var2.contains("недоступна в режиме afk") || var2.contains("недопустимо нажимать в режиме afk");
   }

   private String process(String var1) {
      if (var1 == null) {
         return "N/A";
      }

      String var2 = var1.replaceAll("\\D+", "");
      return var2.isEmpty() ? "N/A" : var2;
   }

   private boolean compute(String var1) {
      return var1 != null && !"N/A".equals(var1) && !var1.isBlank();
   }

   private boolean handle(KeybindSetting var1) {
      return var1 != null && var1.compute() != -1;
   }

   private void encodePoint() {
      if (this.serverRead.compute() != -1) {
         boolean var1 = KeybindSetting.process(this.serverRead.compute());
         if (presetSave && !var1) {
            windowConvert = 0.25F;
         }

         presetSave = var1;
      } else {
         presetSave = false;
         windowConvert = 0.25F;
      }
   }

   private void animate() {
      ItemStack var1 = Module.client.player.getMainHandStack();
      if (this.handle(var1)) {
         Module.client.options.attackKey.setPressed(false);
         Module.client.options.useKey.setPressed(false);
      }
   }

   private boolean handle(ItemStack var1) {
      if (var1 != null && var1.isDamageable()) {
         int var2 = var1.getMaxDamage();
         if (var2 <= 0) {
            return false;
         }

         int var3 = var2 - var1.getDamage();
         int var4 = var2 < 70 ? Math.max(1, (int)Math.ceil(var2 * 0.12)) : 70;
         return var3 <= var4;
      } else {
         return false;
      }
   }

   private void load() {
      if (Module.client.currentScreen != null) {
         if (providerClose) {
            this.unload();
         }
      } else {
         ItemStack var1 = Module.client.player.getMainHandStack();
         ItemStack var2 = Module.client.player.getOffHandStack();
         if (!providerClose) {
            if (Module.client.player.isUsingItem()) {
               return;
            }

            if (var1.isDamageable() && var1.getMaxDamage() - var1.getDamage() <= this.profileDraw.compute()) {
               if (this.submit() == -1) {
                  return;
               }

               providerClose = true;
               this.rendererScan = Module.client.player.getInventory().getSelectedSlot();
               this.sourceBuild = Module.client.player.getPitch();
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, 45, this.rendererScan, SlotActionType.SWAP, Module.client.player);
               this.save();
            }
         } else {
            Module.client.player.setPitch(90.0F);
            if (var2.isEmpty() || var2.getDamage() == 0 || !var2.isDamageable()) {
               this.unload();
               return;
            }

            if (Module.client.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE && !this.save()) {
               this.unload();
               return;
            }

            Module.client.options.useKey.setPressed(true);
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
         }
      }
   }

   private boolean save() {
      int var1 = this.submit();
      if (var1 == -1) {
         return false;
      }

      if (var1 >= 36 && var1 <= 44) {
         Module.client.player.getInventory().setSelectedSlot(var1 - 36);
      } else {
         Module.client.interactionManager
            .clickSlot(
               Module.client.player.playerScreenHandler.syncId, var1, Module.client.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, Module.client.player
            );
      }

      return true;
   }

   private int submit() {
      for (int var1 = 9; var1 <= 44; var1++) {
         if (((Slot)Module.client.player.playerScreenHandler.slots.get(var1)).getStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            return var1;
         }
      }

      return -1;
   }

   private void unload() {
      if (providerClose) {
         providerClose = false;
         Module.client.options.useKey.setPressed(false);
         Module.client.player.setPitch(this.sourceBuild);
         if (this.rendererScan != -1) {
            Module.client.interactionManager
               .clickSlot(Module.client.player.playerScreenHandler.syncId, 45, this.rendererScan, SlotActionType.SWAP, Module.client.player);
            Module.client.player.getInventory().setSelectedSlot(this.rendererScan);
            this.rendererScan = -1;
         }
      }
   }

   private void fetch() {
      if (this.timerRender > 0) {
         this.measure();
      } else if (Module.client.interactionManager != null && !moduleCollect && !providerClose && !Module.client.player.isUsingItem()) {
         if (this.profileInvoke.update((long)this.eventAttach.compute())) {
            PlayerHelper.DataRecord var1 = this.matchVector();
            if (var1 != null) {
               this.sourceSchedule = var1;
               this.timerRender = 1;
               this.scaleSave = 0;
               this.measure();
            }
         }
      }
   }

   private void measure() {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null && this.sourceSchedule != null) {
         switch (this.timerRender) {
            case 1:
               SyntheticKeyState.handle().handle("PlayerHelper_AutoArmor");
               Module.client.options.sprintKey.setPressed(false);
               Module.client.player.setSprinting(false);
               this.timerRender = 2;
               this.scaleSave = 1;
               break;
            case 2:
               if (this.scaleSave-- > 0) {
                  return;
               }

               if (this.sourceSchedule.fromBundle()) {
                  if (!this.handle(this.sourceSchedule)) {
                     this.blendMatrix();
                     return;
                  }

                  this.timerRender = 3;
                  this.scaleSave = 1;
                  return;
               }

               InventorySlotActions.handle(this.sourceSchedule.sourceSlot(), this.sourceSchedule.armorSlotId());
               Module.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
               this.profileInvoke.handle();
               this.timerRender = 3;
               this.scaleSave = 1;
               break;
            case 3:
               if (this.scaleSave-- > 0) {
                  return;
               }

               if (this.sourceSchedule.fromBundle()) {
                  InventorySlotActions.handle(this.sourceSchedule.sourceSlot(), this.sourceSchedule.armorSlotId());
                  Module.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
                  this.profileInvoke.handle();
               }

               this.blendMatrix();
               break;
            default:
               this.blendMatrix();
         }
      } else {
         this.blendMatrix();
      }
   }

   private boolean handle(PlayerHelper.DataRecord var1) {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.playerScreenHandler.getCursorStack().isEmpty()) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1.bundleSlot());
         BundleContentsComponent var3 = (BundleContentsComponent)var2.get(DataComponentTypes.BUNDLE_CONTENTS);
         if (var2.getItem() instanceof BundleItem && var3 != null && var1.bundleIndex() < var3.size()) {
            int var4 = var1.bundleSlot() < 9 ? var1.bundleSlot() + 36 : var1.bundleSlot();
            BundleItem.setSelectedStackIndex(var2, var1.bundleIndex());
            Module.client.player.networkHandler.sendPacket(new BundleItemSelectedC2SPacket(var4, var1.bundleIndex()));
            Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var4, 1, SlotActionType.PICKUP, Module.client.player);
            Module.client.interactionManager
               .clickSlot(Module.client.player.playerScreenHandler.syncId, var1.sourceSlot(), 0, SlotActionType.PICKUP, Module.client.player);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void blendMatrix() {
      if (this.timerRender > 0) {
         SyntheticKeyState.handle().process("PlayerHelper_AutoArmor");
      }

      this.sourceSchedule = null;
      this.timerRender = 0;
      this.scaleSave = 0;
   }

   private PlayerHelper.DataRecord matchVector() {
      PlayerHelper.DataRecord var1 = null;
      var1 = this.handle(var1, this.handle(EquipmentSlot.HEAD, 5));
      var1 = this.handle(var1, this.handle(EquipmentSlot.CHEST, 6));
      var1 = this.handle(var1, this.handle(EquipmentSlot.LEGS, 7));
      return this.handle(var1, this.handle(EquipmentSlot.FEET, 8));
   }

   private PlayerHelper.DataRecord handle(PlayerHelper.DataRecord var1, PlayerHelper.DataRecord var2) {
      if (var2 == null) {
         return var1;
      } else if (var1 == null) {
         return var2;
      } else {
         return var2.improvement() > var1.improvement() ? var2 : var1;
      }
   }

   private PlayerHelper.DataRecord handle(EquipmentSlot var1, int var2) {
      ItemStack var3 = Module.client.player.getEquippedStack(var1);
      int var4 = this.handle(var3, var1);
      int var5 = -1;
      int var6 = var4;

      for (int var7 = 0; var7 < 36; var7++) {
         ItemStack var8 = Module.client.player.getInventory().getStack(var7);
         int var9 = this.handle(var8, var1);
         if (var9 > var6) {
            var6 = var9;
            var5 = var7 < 9 ? var7 + 36 : var7;
         }
      }

      int var15 = this.projectItem();
      if (var15 != -1) {
         for (int var16 = 0; var16 < 36; var16++) {
            ItemStack var17 = Module.client.player.getInventory().getStack(var16);
            if (var17.getItem() instanceof BundleItem) {
               BundleContentsComponent var10 = (BundleContentsComponent)var17.get(DataComponentTypes.BUNDLE_CONTENTS);
               if (var10 != null) {
                  for (int var11 = 0; var11 < var10.size(); var11++) {
                     int var12 = this.handle(var10.get(var11), var1);
                     if (var12 > var6) {
                        var6 = var12;
                        var5 = var15 < 9 ? var15 + 36 : var15;
                        return new PlayerHelper.DataRecord(var5, var2, var6 - var4, var16, var11);
                     }
                  }
               }
            }
         }
      }

      return var5 == -1 ? null : new PlayerHelper.DataRecord(var5, var2, var6 - var4);
   }

   private int projectItem() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   private int handle(ItemStack var1, EquipmentSlot var2) {
      if (var1 != null && !var1.isEmpty() && this.handle(var1.getItem()) == var2) {
         int var3 = this.process(var1.getItem()) * 10000;
         ItemEnchantmentsComponent var4 = (ItemEnchantmentsComponent)var1.get(DataComponentTypes.ENCHANTMENTS);
         if (var4 != null && !var4.isEmpty()) {
            for (Entry var6 : var4.getEnchantmentEntries()) {
               var3 += var6.getIntValue() * 100;
            }
         }

         if (var1.isDamageable()) {
            var3 += Math.max(0, var1.getMaxDamage() - var1.getDamage()) * 100 / Math.max(1, var1.getMaxDamage());
         }

         return var3;
      } else {
         return -1;
      }
   }

   private EquipmentSlot handle(Item var1) {
      if (var1 == Items.NETHERITE_HELMET
         || var1 == Items.DIAMOND_HELMET
         || var1 == Items.IRON_HELMET
         || var1 == Items.CHAINMAIL_HELMET
         || var1 == Items.GOLDEN_HELMET
         || var1 == Items.LEATHER_HELMET
         || var1 == Items.TURTLE_HELMET) {
         return EquipmentSlot.HEAD;
      } else if (var1 == Items.NETHERITE_CHESTPLATE
         || var1 == Items.DIAMOND_CHESTPLATE
         || var1 == Items.IRON_CHESTPLATE
         || var1 == Items.CHAINMAIL_CHESTPLATE
         || var1 == Items.GOLDEN_CHESTPLATE
         || var1 == Items.LEATHER_CHESTPLATE) {
         return EquipmentSlot.CHEST;
      } else if (var1 == Items.NETHERITE_LEGGINGS
         || var1 == Items.DIAMOND_LEGGINGS
         || var1 == Items.IRON_LEGGINGS
         || var1 == Items.CHAINMAIL_LEGGINGS
         || var1 == Items.GOLDEN_LEGGINGS
         || var1 == Items.LEATHER_LEGGINGS) {
         return EquipmentSlot.LEGS;
      } else {
         return var1 != Items.NETHERITE_BOOTS
               && var1 != Items.DIAMOND_BOOTS
               && var1 != Items.IRON_BOOTS
               && var1 != Items.CHAINMAIL_BOOTS
               && var1 != Items.GOLDEN_BOOTS
               && var1 != Items.LEATHER_BOOTS
            ? null
            : EquipmentSlot.FEET;
      }
   }

   private int process(Item var1) {
      if (var1 == Items.NETHERITE_HELMET || var1 == Items.NETHERITE_CHESTPLATE || var1 == Items.NETHERITE_LEGGINGS || var1 == Items.NETHERITE_BOOTS) {
         return 6;
      } else if (var1 == Items.DIAMOND_HELMET || var1 == Items.DIAMOND_CHESTPLATE || var1 == Items.DIAMOND_LEGGINGS || var1 == Items.DIAMOND_BOOTS) {
         return 5;
      } else if (var1 == Items.IRON_HELMET || var1 == Items.IRON_CHESTPLATE || var1 == Items.IRON_LEGGINGS || var1 == Items.IRON_BOOTS) {
         return 4;
      } else if (var1 == Items.CHAINMAIL_HELMET || var1 == Items.CHAINMAIL_CHESTPLATE || var1 == Items.CHAINMAIL_LEGGINGS || var1 == Items.CHAINMAIL_BOOTS) {
         return 3;
      } else if (var1 == Items.GOLDEN_HELMET || var1 == Items.GOLDEN_CHESTPLATE || var1 == Items.GOLDEN_LEGGINGS || var1 == Items.GOLDEN_BOOTS) {
         return 2;
      } else if (var1 == Items.LEATHER_HELMET || var1 == Items.LEATHER_CHESTPLATE || var1 == Items.LEATHER_LEGGINGS || var1 == Items.LEATHER_BOOTS) {
         return 1;
      } else {
         return var1 == Items.TURTLE_HELMET ? 2 : 0;
      }
   }

   private void computeResponse() {
      int var1 = (int)Module.client.player.getX();
      int var2 = (int)Module.client.player.getY();
      int var3 = (int)Module.client.player.getZ();
      String var4 = String.format(" %d %d %d", var1, var2, var3);
      String var5 = this.vectorMatch.compute();
      switch (var5) {
         case "Общий чат":
            Module.client.getNetworkHandler().sendChatMessage("! Мои координаты:" + var4);
            break;
         case "Друзья":
            List<String> var8 = FriendManager.resolve();
            if (var8.isEmpty()) {
               Module.client.player.sendMessage(Text.of("§cСписок друзей пуст!"), true);
               return;
            }

            for (String var10 : var8) {
               Module.client.getNetworkHandler().sendChatMessage("/msg " + var10 + " Мои координаты:" + var4);
            }

            Module.client.player.sendMessage(Text.of("§aКоординаты отправлены друзьям."), true);
            break;
         case "СОО.Клановцам":
            Module.client.getNetworkHandler().sendChatMessage("/clan chat" + var4);
      }
   }

   private void fetchProvider() {
      if (Module.client.currentScreen != null && !(Module.client.currentScreen instanceof ChatScreen)) {
         if (moduleCollect) {
            this.performVector();
         }
      } else if (!(Module.client.player.getHungerManager().getFoodLevel() >= this.summary.compute()) || moduleCollect && Module.client.player.isUsingItem()) {
         if (moduleCollect || !Module.client.player.isUsingItem()) {
            Hand var1 = this.drawProfile();
            if (var1 == null) {
               if (moduleCollect && !Module.client.player.isUsingItem()) {
                  this.performVector();
               }
            } else {
               if (!moduleCollect) {
                  this.handle(var1);
               } else {
                  this.process(var1);
               }
            }
         }
      } else {
         if (moduleCollect) {
            this.performVector();
         }
      }
   }

   private Hand drawProfile() {
      ItemStack var1 = Module.client.player.getMainHandStack();
      if (var1.contains(DataComponentTypes.FOOD)) {
         return Hand.MAIN_HAND;
      }

      ItemStack var2 = Module.client.player.getOffHandStack();
      if (var2.contains(DataComponentTypes.FOOD)) {
         return Hand.OFF_HAND;
      }

      for (int var3 = 0; var3 < 9; var3++) {
         if (Module.client.player.getInventory().getStack(var3).contains(DataComponentTypes.FOOD)) {
            return Hand.MAIN_HAND;
         }
      }

      return null;
   }

   private void handle(Hand var1) {
      if (var1 == Hand.MAIN_HAND) {
         int var2 = this.attachEvent();
         if (var2 == -1) {
            return;
         }

         this.colorMeasure = Module.client.player.getInventory().getSelectedSlot();
         Module.client.player.getInventory().setSelectedSlot(var2);
      }

      Module.client.options.useKey.setPressed(true);
      if (Module.client.interactionManager != null) {
         Module.client.interactionManager.interactItem(Module.client.player, var1);
      }

      this.animationSchedule = true;
      moduleCollect = true;
   }

   private void process(Hand var1) {
      if (Module.client.player.isUsingItem()) {
         Module.client.options.useKey.setPressed(true);
         this.animationSchedule = true;
      } else if (this.animationSchedule) {
         this.animationSchedule = false;
         this.performVector();
      } else {
         Module.client.options.useKey.setPressed(true);
         if (Module.client.interactionManager != null) {
            Module.client.interactionManager.interactItem(Module.client.player, var1);
         }
      }
   }

   private void performVector() {
      if (moduleCollect) {
         Module.client.options.useKey.setPressed(false);
         if (this.colorMeasure != -1 && Module.client.player != null) {
            Module.client.player.getInventory().setSelectedSlot(this.colorMeasure);
            this.colorMeasure = -1;
         }

         this.animationSchedule = false;
         moduleCollect = false;
      }
   }

   private int attachEvent() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).contains(DataComponentTypes.FOOD)) {
            return var1;
         }
      }

      return -1;
   }

   @Override
   public void process() {
      this.performVector();
      this.unload();
      this.blendMatrix();
      presetSave = false;
      windowConvert = 0.25F;
      this.indexBind = false;
      super.process();
   }

   record DataRecord(int sourceSlot, int armorSlotId, int improvement, int bundleSlot, int bundleIndex) {
      DataRecord(int var1, int var2, int var3) {
         this(var1, var2, var3, -1, -1);
      }

      boolean fromBundle() {
         return this.bundleSlot >= 0 && this.bundleIndex >= 0;
      }
   }
}
