package ru.wild.modules.combat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType.Default;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.api.event.MouseClickContextEvent;
import ru.wild.api.event.MouseUpdateEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.hud.NotificationHudSettings;
import ru.wild.modules.movement.Sprint;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.inventory.SpecialItemCatalog;
import ru.wild.util.math.ActionDelay;
import ru.wild.util.player.SyntheticKeyState;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(
   name = "AutoSwap",
   description = "Автоматически свапает предметы через бинд",
   category = ModuleCategory.Combat,
   flags = ModuleFlag.GRIM
)
public class AutoSwap extends Module {
   private static final String vectorMatch = "Обычный";
   private static final String itemProject = "Трио свап";
   private static final String responseCompute = "Без фильтра";
   private static final String providerFetch = "FT/RW";
   private static final int profileDraw = 3;
   private static boolean vectorPerform;
   public static ModeSetting source = new ModeSetting("Выбор работы свапов:", "Трио свап", "Обычный", "Трио свап");
   public static ModeSetting target = new ModeSetting("Фильтр свапов", "Без фильтра", "Без фильтра", "FT/RW");
   public static ModeSetting pending = new ModeSetting("Первый предмет", "Шар", "Золотое яблоко", "Щит", "Шар", "Тотем")
      .handle(() -> !source.process("Обычный"));
   public static ModeSetting previous = new ModeSetting("Второй предмет", "Тотем 2", "Золотое яблоко 2", "Щит 2", "Шар 2", "Тотем 2")
      .handle(() -> !source.process("Обычный"));
   public static KeybindSetting latest = new KeybindSetting("Кнопка", -1);
   public static BooleanSetting summary = new BooleanSetting("Только зачарованых тотемов", false).handle(() -> !source.process("Обычный"));
   private boolean eventAttach;
   private final ActionDelay serverRead = new ActionDelay();
   private final ItemStack[] positionAdvance = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
   private final String[] frameCheck = new String[]{"", "", ""};
   private int moduleCollect = 0;
   private int providerClose = 0;
   private int presetSave = -1;
   private String windowConvert = "";
   private ItemStack presetWrite = ItemStack.EMPTY;
   private String colorMeasure = "";
   private int animationSchedule = 0;
   private int rendererScan = 0;
   private int sourceBuild = -1;
   private String outputCollapse = "";
   private boolean profileInvoke = false;
   private boolean sourceSchedule;
   private boolean timerRender;
   private int scaleSave = -1;
   private int colorCompute = -1;
   private float scaleAdapt;
   private float textureRun;
   private float indexBind;
   private float actionRead;
   private long configCollapse;
   private long dataValidate;
   private long scaleRender;
   private float[] clientRefresh = new float[3];
   public static boolean matrixBlend = false;

   public AutoSwap() {
      this.handle(source, target, pending, previous, latest, summary);
   }

   public static boolean refresh() {
      return vectorPerform;
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (Module.client.player == null || Module.client.world == null) {
         this.performVector();
      } else if (this.sourceSchedule) {
         if (!source.process("Трио свап") || latest.compute() == -1) {
            this.compute(false);
         } else if (!this.apply(latest.compute())) {
            this.compute(true);
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player == null || Module.client.world == null) {
         this.performVector();
      } else if (this.profileInvoke) {
         if (this.providerClose > 0) {
            this.providerClose--;
         } else if (!this.load() && this.rendererScan < 25) {
            this.rendererScan++;
            this.providerClose = 1;
         } else {
            int var2 = this.sourceBuild;
            String var3 = this.outputCollapse;
            this.profileInvoke = false;
            this.sourceBuild = -1;
            this.outputCollapse = "";
            this.rendererScan = 0;
            this.providerClose = 0;
            this.process(var2, var3);
         }
      } else if (this.moduleCollect > 0) {
         this.encodePoint();
         if (this.providerClose > 0) {
            this.providerClose--;
         } else {
            this.render();
         }
      }
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (this.sourceSchedule && source.process("Трио свап") && Module.client.player != null && Module.client.world != null) {
         RoundedRectRenderer var2 = var1.resolve();
         DrawContext var3 = var1.prepare();
         if (var2 != null && var3 != null) {
            var2.handle(7.0F);
            this.projectItem();
            int var4 = var1.apply();
            int var5 = var1.execute();
            if (this.timerRender) {
               this.blendMatrix();
               this.process(var2, var3, var4, var5);
            } else {
               this.scaleSave = this.process(this.scaleAdapt, this.textureRun, var4, var5);
               this.blendMatrix();
               this.handle(var2, var3, var4, var5);
            }
         }
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (var1.resolve() == latest.compute() && latest.compute() != -1) {
         if (!source.process("Трио свап")) {
            if (Module.client.currentScreen == null && var1.apply() == 1 && this.serverRead.resolve(300L) && this.moduleCollect == 0) {
               this.serverRead.handle();
               this.tick();
            }
         } else {
            if (var1.apply() == 1 && this.moduleCollect == 0 && !this.sourceSchedule && this.serverRead.resolve(120L)) {
               this.unload();
               this.serverRead.handle();
               var1.process();
            } else if (var1.apply() == 0 && this.sourceSchedule) {
               this.compute(true);
               var1.process();
            }
         }
      }
   }

   @EventHandler
   public void handle(MouseButtonEvent var1) {
      if (!var1.check()) {
         int var2 = -100 - var1.resolve();
         if (source.process("Трио свап") && latest.compute() == var2 && this.moduleCollect == 0) {
            if (var1.onTick() && !this.sourceSchedule && this.serverRead.resolve(120L)) {
               this.handle((float)var1.execute(), (float)var1.prepare());
               this.unload();
               this.serverRead.handle();
               var1.process();
               return;
            }

            if (var1.select() && this.sourceSchedule) {
               this.handle((float)var1.execute(), (float)var1.prepare());
               this.compute(true);
               var1.process();
               return;
            }
         }

         if (this.sourceSchedule) {
            this.handle((float)var1.execute(), (float)var1.prepare());
            if (var1.onTick()) {
               this.compute(var1.resolve());
            }

            var1.process();
         }
      }
   }

   @EventHandler
   public void handle(MouseUpdateEvent var1) {
      if (this.sourceSchedule) {
         var1.process();
      }
   }

   @EventHandler
   public void handle(MouseClickContextEvent var1) {
      if (this.sourceSchedule) {
         var1.process();
      }
   }

   private void render() {
      switch (this.moduleCollect) {
         case 1:
            this.encodePoint();
            this.animate();
            this.moduleCollect = 2;
            this.providerClose = 1;
            break;
         case 2:
            if (this.presetSave < 0 || this.presetSave >= 36) {
               this.moduleCollect = 3;
               this.providerClose = 1;
               return;
            }

            if (Module.client.player.isSprinting()) {
               this.encodePoint();
               this.providerClose = 1;
               return;
            }

            ItemStack var3 = Module.client.player.getInventory().getStack(this.presetSave);
            if (var3.isEmpty()) {
               this.moduleCollect = 3;
               this.providerClose = 1;
               return;
            }

            this.presetWrite = var3.copy();
            this.presetWrite.setCount(1);
            this.colorMeasure = this.compute(var3, this.windowConvert);
            int var2 = Module.client.player.currentScreenHandler.syncId;
            Module.client.interactionManager
               .clickSlot(var2, this.presetSave < 9 ? this.presetSave + 36 : this.presetSave, 40, SlotActionType.SWAP, Module.client.player);
            Module.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(var2));
            this.animationSchedule = 1;
            this.moduleCollect = 3;
            this.providerClose = 1;
            break;
         case 3:
            if (this.animationSchedule > 0) {
               this.animationSchedule--;
               this.providerClose = 1;
               return;
            }

            if (this.save()) {
               ItemStack var1 = Module.client.player.getOffHandStack();
               this.process(var1.isEmpty() ? this.presetWrite : var1, this.colorMeasure);
            }

            SyntheticKeyState.handle().process("AutoSwap");
            this.moduleCollect = 0;
            this.presetSave = -1;
            matrixBlend = false;
            this.presetWrite = ItemStack.EMPTY;
            this.colorMeasure = "";
            this.animationSchedule = 0;
            this.rendererScan = 0;
      }
   }

   private void tick() {
      boolean var1 = false;
      if (this.eventAttach) {
         switch (previous.compute()) {
            case "Шар 2":
               var1 = this.handle(Items.PLAYER_HEAD, "Шар", false);
               break;
            case "Золотое яблоко 2":
               var1 = this.handle(Items.GOLDEN_APPLE, "Золотое яблоко", false);
               break;
            case "Тотем 2":
               var1 = this.handle(Items.TOTEM_OF_UNDYING, "Тотем", summary.compute());
               break;
            case "Щит 2":
               var1 = this.handle(Items.SHIELD, "Щит", false);
         }

         if (var1 || !this.handle(previous.compute())) {
            this.eventAttach = false;
         }
      } else {
         switch (pending.compute()) {
            case "Шар":
               var1 = this.handle(Items.PLAYER_HEAD, "Шар", false);
               break;
            case "Тотем":
               var1 = this.handle(Items.TOTEM_OF_UNDYING, "Тотем", summary.compute());
               break;
            case "Золотое яблоко":
               var1 = this.handle(Items.GOLDEN_APPLE, "Золотое яблоко", false);
               break;
            case "Щит":
               var1 = this.handle(Items.SHIELD, "Щит", false);
         }

         if (var1 || !this.handle(pending.compute())) {
            this.eventAttach = true;
         }
      }
   }

   private boolean handle(String var1) {
      return target.process("FT/RW") && ("Шар".equals(var1) || "Шар 2".equals(var1));
   }

   private boolean handle(Item var1, String var2, boolean var3) {
      int var4 = var1 == Items.PLAYER_HEAD && target.process("FT/RW") ? this.submit() : this.handle(var1, var3);
      if (var4 == -1) {
         return false;
      }

      this.handle(var4, var2);
      return true;
   }

   private boolean handle(int var1) {
      if (var1 >= 0 && var1 < 3) {
         ItemStack var2 = this.positionAdvance[var1];
         String var3 = this.update(var1);
         if ((!var2.isEmpty() || !var3.isEmpty()) && !this.process(var1)) {
            int var4 = this.handle(var2, var3);
            if (var4 == -1) {
               return false;
            }

            this.handle(var4, this.compute(Module.client.player.getInventory().getStack(var4), var2.getName().getString()));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean drawAnimation() {
      try {
         Class var1 = Class.forName("ru.wild.modules.combat.AttackAura");
         Field var2 = var1.getDeclaredField("VNNnnVUuvv");
         var2.setAccessible(true);
         return var2.getInt(null) != 0;
      } catch (Throwable var3) {
         return false;
      }
   }

   private void handle(int var1, String var2) {
      if (!this.drawAnimation()) {
         this.sourceBuild = var1;
         this.outputCollapse = var2;
         this.profileInvoke = true;
         this.rendererScan = 0;
         this.providerClose = 0;
      }
   }

   private void encodePoint() {
      Sprint.latest = 2;
      SyntheticKeyState.handle().handle("AutoSwap");
      Module.client.options.sprintKey.setPressed(false);
      Module.client.player.setSprinting(false);
   }

   private void animate() {
      try {
         Class var1 = Class.forName("ru.wild.automation.combat.AdaptiveAttackTiming");
         var1.getMethod("nvUVNnuu").invoke(null);
      } catch (Throwable var2) {
      }
   }

   private void process(int var1, String var2) {
      if (!this.drawAnimation()) {
         this.presetSave = var1;
         this.windowConvert = var2;
         this.presetWrite = ItemStack.EMPTY;
         this.colorMeasure = var2;
         this.moduleCollect = 1;
         matrixBlend = true;
         this.render();
      }
   }

   private boolean load() {
      if (Module.client.player == null) {
         return true;
      }

      if (Module.client.player.isOnGround()) {
         return true;
      }

      try {
         Class var1 = Class.forName("ru.wild.modules.combat.AttackAura");
         Object var2 = var1.getField("ccOO0COcoco0").get(null);
         if (var2 == null) {
            return true;
         }
      } catch (Throwable var5) {
         return true;
      }

      try {
         Class var7 = Class.forName("ru.wild.automation.combat.AdaptiveAttackTiming");
         boolean var8 = (Boolean)var7.getMethod("nUUVuvU").invoke(null);
         if (!var8) {
            return false;
         }

         boolean var3 = (Boolean)var7.getMethod("isBestMomentToHit", boolean.class).invoke(null, true);
         return !var3 ? false : Module.client.player.fallDistance > 0.0 || Module.client.player.getVelocity().y < -0.08;
      } catch (Throwable var6) {
         return true;
      }
   }

   private boolean save() {
      if (Module.client.player != null && this.presetWrite != null && !this.presetWrite.isEmpty()) {
         ItemStack var1 = Module.client.player.getOffHandStack();
         if (var1 != null && !var1.isEmpty()) {
            if (this.handle(var1, this.presetWrite, this.process(this.presetWrite))) {
               return true;
            } else {
               return ItemStack.areItemsAndComponentsEqual(var1, this.presetWrite) ? true : var1.isOf(this.presetWrite.getItem());
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private int handle(Item var1, boolean var2) {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (var4.isOf(var1) && (!var2 || var4.hasEnchantments() || var4.hasGlint())) {
            return var3;
         }
      }

      return -1;
   }

   private int submit() {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (this.update(var2)) {
            return var1;
         }
      }

      return -1;
   }

   private int handle(ItemStack var1, String var2) {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (this.handle(var4, var1, var2)) {
            return var3;
         }
      }

      return -1;
   }

   private boolean process(int var1) {
      if (Module.client.player != null && var1 >= 0 && var1 < 3 && (!this.positionAdvance[var1].isEmpty() || !this.update(var1).isEmpty())) {
         ItemStack var2 = Module.client.player.getOffHandStack();
         return var2 != null && !var2.isEmpty() ? this.handle(var2, this.positionAdvance[var1], this.update(var1)) : false;
      } else {
         return false;
      }
   }

   private void unload() {
      this.sourceSchedule = true;
      this.scaleSave = -1;
      this.measure();
      matrixBlend = true;
      this.configCollapse = this.dataValidate = this.scaleRender = System.nanoTime();
      Arrays.fill(this.clientRefresh, 0.0F);
      this.computeResponse();
      this.indexBind = this.scaleAdapt;
      this.actionRead = this.textureRun;
      if (Module.client.mouse != null) {
         Module.client.mouse.unlockCursor();
      }

      this.fetchProvider();
   }

   private void compute(boolean var1) {
      if (this.sourceSchedule) {
         this.projectItem();
         boolean var2 = this.timerRender;
         int var3 = var2
            ? -1
            : this.process(this.scaleAdapt, this.textureRun, Module.client.getWindow().getFramebufferWidth(), Module.client.getWindow().getFramebufferHeight());
         this.sourceSchedule = false;
         this.scaleSave = -1;
         this.measure();
         vectorPerform = false;
         boolean var4 = var1 && var3 != -1 && this.handle(var3);
         if (!var4 && this.moduleCollect == 0) {
            matrixBlend = false;
         }

         if (Module.client.currentScreen == null && Module.client.mouse != null) {
            Module.client.mouse.lockCursor();
         }
      }
   }

   private void compute(int var1) {
      if (Module.client.player != null && Module.client.getWindow() != null) {
         if (this.timerRender) {
            if (var1 == 0) {
               this.fetch();
            }

            if (var1 == 1) {
               this.measure();
            }
         } else {
            int var2 = this.process(
               this.scaleAdapt, this.textureRun, Module.client.getWindow().getFramebufferWidth(), Module.client.getWindow().getFramebufferHeight()
            );
            if (var2 != -1) {
               if (var1 == 0) {
                  this.timerRender = true;
                  this.colorCompute = var2;
                  this.dataValidate = System.nanoTime();
                  vectorPerform = true;
               } else if (var1 == 1) {
                  this.positionAdvance[var2] = ItemStack.EMPTY;
                  this.frameCheck[var2] = "";
                  this.drawProfile();
               }
            }
         }
      }
   }

   private void fetch() {
      int var1 = this.resolve(this.scaleAdapt, this.textureRun, Module.client.getWindow().getFramebufferWidth(), Module.client.getWindow().getFramebufferHeight());
      if (var1 >= 0 && this.colorCompute >= 0 && this.colorCompute < 3) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (!var2.isEmpty()) {
            ItemStack var3 = var2.copy();
            var3.setCount(1);
            this.positionAdvance[this.colorCompute] = var3;
            this.frameCheck[this.colorCompute] = this.compute(var3);
            this.measure();
            this.drawProfile();
         }
      } else {
         this.measure();
      }
   }

   private void measure() {
      this.timerRender = false;
      this.colorCompute = -1;
      vectorPerform = false;
   }

   private void blendMatrix() {
      long var1 = System.nanoTime();
      float var3 = this.scaleRender == 0L ? 0.016F : this.handle((float)(var1 - this.scaleRender) / 1.0E9F, 0.001F, 0.05F);
      this.scaleRender = var1;

      for (int var4 = 0; var4 < 3; var4++) {
         this.clientRefresh[var4] = this.handle(this.clientRefresh[var4], var4 == this.scaleSave ? 1.0F : 0.0F, var3, 20.0F);
      }
   }

   private float resolve(int var1) {
      float var2 = (float)(System.nanoTime() - this.configCollapse) / 1000000.0F - var1 * 24.0F;
      return this.handle(this.handle(var2 / 135.0F, 0.0F, 1.0F));
   }

   private float matchVector() {
      return this.handle(this.handle((float)(System.nanoTime() - this.dataValidate) / 1.2E8F, 0.0F, 1.0F));
   }

   private float handle(int var1, int var2) {
      float var3 = (float)(System.nanoTime() - this.dataValidate) / 1000000.0F - (var1 * 9 + var2) * 3.2F;
      return this.handle(this.handle(var3 / 120.0F, 0.0F, 1.0F));
   }

   private void handle(RoundedRectRenderer var1, DrawContext var2, int var3, int var4) {
      for (int var5 = 0; var5 < 3; var5++) {
         AutoSwap.DataRecord var6 = this.handle(var5, var3, var4);
         boolean var7 = var5 == this.scaleSave;
         boolean var8 = this.process(var5);
         float var9 = this.resolve(var5);
         float var10 = this.clientRefresh[var5];
         float var11 = var6.size * (0.86F + var9 * 0.14F + var10 * 0.035F);
         float var12 = this.process(var3 / 2.0F, var6.centerX(), var9);
         float var13 = this.process(var4 / 2.0F, var6.centerY(), var9);
         float var14 = var12 - var11 / 2.0F;
         float var15 = var13 - var11 / 2.0F;
         float var16 = Math.max(8.0F, var11 * 0.16F);
         int var17 = var8
            ? PackedColor.compute(90, 20, 26, var7 ? 180 : 135)
            : (var7 ? PackedColor.compute(70, 66, 28, 168) : PackedColor.compute(24, 26, 32, 132));
         int var18 = var8
            ? PackedColor.compute(44, 14, 18, var7 ? 170 : 120)
            : (var7 ? PackedColor.compute(34, 34, 22, 156) : PackedColor.compute(12, 14, 18, 118));
         int var19 = var8
            ? PackedColor.compute(255, 65, 75, var7 ? 230 : 190)
            : (var7 ? PackedColor.compute(255, 245, 110, 215) : PackedColor.compute(255, 255, 255, 115));
         var1.update(var9);
         var1.handle(
            var14,
            var15,
            var11,
            var11,
            var16,
            var7 ? 10.0F : 6.0F,
            1.5F,
            var8 ? PackedColor.compute(255, 55, 65, var7 ? 70 : 45) : PackedColor.compute(0, 0, 0, var7 ? 90 : 60)
         );
         this.handle(var1, var14, var15, var11, var11, var16, var17, var18, var19, var7 ? 23.0F : 60.0F, var8 ? 2.4F : (var7 ? 2.0F : 1.25F));
         if (this.positionAdvance[var5].isEmpty()) {
            this.handle(var1, var12, var13, var11 * 0.28F, PackedColor.compute(255, 255, 255, var7 ? 230 : 160));
         }

         var1.onTick();
      }

      var1.compute();

      for (int var23 = 0; var23 < 3; var23++) {
         ItemStack var24 = this.positionAdvance[var23];
         if (!var24.isEmpty()) {
            AutoSwap.DataRecord var25 = this.handle(var23, var3, var4);
            float var26 = this.resolve(var23);
            if (!(var26 <= 0.08F)) {
               float var27 = this.clientRefresh[var23];
               float var28 = this.process(var3 / 2.0F, var25.centerX(), var26);
               float var29 = this.process(var4 / 2.0F, var25.centerY(), var26);
               float var30 = (var23 == this.scaleSave ? 2.85F : 2.55F) * (0.84F + var26 * 0.16F + var27 * 0.035F);
               float var31 = 16.0F * var30;
               float var32 = var25.size * (0.86F + var26 * 0.14F + var27 * 0.035F);
               float var33 = var28 - var32 * 0.5F;
               float var34 = var29 - var32 * 0.5F;
               float var35 = Math.max(8.0F, var32 * 0.16F);
               var1.handle(var33, var34, var32, var32, var35, var35, var35, var35);

               try {
                  ItemStackOverlayRenderer.handle(
                     var1,
                     var24,
                     ItemStackOverlayRenderer.handle(var28 - var31 * 0.5F),
                     ItemStackOverlayRenderer.handle(var29 - var31 * 0.5F),
                     ItemStackOverlayRenderer.compute(var30),
                     var23,
                     true,
                     var23
                  );
               } finally {
                  var1.compute();
                  var1.apply();
               }
            }
         }
      }
   }

   private void process(RoundedRectRenderer var1, DrawContext var2, int var3, int var4) {
      AutoSwap.Bounds var5 = this.process(var3, var4);
      float var6 = 10.0F;
      float var7 = var5.startX - var6;
      float var8 = var5.startY - var6;
      float var9 = var5.width + var6 * 2.0F;
      float var10 = var5.height + var6 * 2.0F;
      int var11 = PackedColor.compute(15, 15, 18, 150);
      int var12 = PackedColor.compute(255, 255, 255, 80);
      float var13 = this.matchVector();
      var1.update(var13);
      this.handle(var1, var7, var8, var9, var10, 10.0F, var11, PackedColor.compute(8, 8, 10, 130), var12, 23.0F, 1.25F);
      int var14 = this.resolve(this.scaleAdapt, this.textureRun, var3, var4);

      for (int var15 = 0; var15 < 4; var15++) {
         for (int var16 = 0; var16 < 9; var16++) {
            int var17 = this.compute(var15, var16);
            float var18 = var5.startX + var16 * (var5.slotSize + var5.gap);
            float var19 = var5.startY + var15 * (var5.slotSize + var5.gap);
            ItemStack var20 = Module.client.player.getInventory().getStack(var17);
            boolean var21 = var17 == var14;
            boolean var22 = this.handle(var20);
            int var23 = var21 ? PackedColor.compute(255, 255, 255, 75) : PackedColor.compute(0, 0, 0, 75);
            int var24 = var22
               ? PackedColor.compute(255, 55, 65, 225)
               : (var21 ? PackedColor.compute(255, 245, 120, 210) : PackedColor.compute(255, 255, 255, 45));
            if (var21) {
               this.handle(var1, var18, var19, var5.slotSize, var5.slotSize, 5.0F, var23, PackedColor.compute(0, 0, 0, 62), var24, 23.0F, var22 ? 2.0F : 1.0F);
            } else {
               var1.handle(var18, var19, var5.slotSize, var5.slotSize, 5.0F, var23);
               var1.handle(var18, var19, var5.slotSize, var5.slotSize, 5.0F, var24, var22 ? 2.0F : 1.0F);
            }
         }
      }

      var1.onTick();
      var1.compute();
      var1.handle(var7, var8, var9, var10, 10.0F, 10.0F, 10.0F, 10.0F);

      try {
         for (int var28 = 0; var28 < 4; var28++) {
            for (int var29 = 0; var29 < 9; var29++) {
               int var30 = this.compute(var28, var29);
               ItemStack var31 = Module.client.player.getInventory().getStack(var30);
               if (!var31.isEmpty()) {
                  float var32 = this.handle(var28, var29);
                  if (!(var32 <= 0.05F)) {
                     float var33 = var5.slotSize / 22.0F * (0.76F + var32 * 0.24F);
                     float var34 = 16.0F * var33;
                     float var35 = var5.startX + var29 * (var5.slotSize + var5.gap) + (var5.slotSize - var34) / 2.0F;
                     float var36 = var5.startY + var28 * (var5.slotSize + var5.gap) + (var5.slotSize - var34) / 2.0F;
                     ItemStackOverlayRenderer.handle(
                        var1,
                        var31,
                        ItemStackOverlayRenderer.handle(var35),
                        ItemStackOverlayRenderer.handle(var36),
                        ItemStackOverlayRenderer.compute(var33),
                        var30,
                        true,
                        var30
                     );
                  }
               }
            }
         }
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private int handle(float var1, float var2, int var3, int var4) {
      for (int var5 = 0; var5 < 3; var5++) {
         AutoSwap.DataRecord var6 = this.handle(var5, var3, var4);
         if (ModuleStateHelper.handle(var1, var2, var6.x, var6.y, var6.size, var6.size)) {
            return var5;
         }
      }

      return -1;
   }

   private int process(float var1, float var2, int var3, int var4) {
      int var5 = this.handle(var1, var2, var3, var4);
      return var5 != -1 ? var5 : this.compute(var1, var2, var3, var4);
   }

   private int compute(float var1, float var2, int var3, int var4) {
      float var5 = var1 - this.indexBind;
      float var6 = var2 - this.actionRead;
      float var7 = this.handle(Math.min(var3, var4) * 0.035F, 18.0F, 38.0F);
      float var8 = var5 * var5 + var6 * var6;
      if (var8 < var7 * var7) {
         return -1;
      } else {
         float var9 = 1.0F / (float)Math.sqrt(var8);
         float var10 = var5 * var9;
         float var11 = var6 * var9;
         if (var11 > 0.82F && Math.abs(var10) < 0.38F) {
            return -1;
         } else {
            float var12 = -var11;
            float var13 = var10 * 0.848F + var11 * 0.53F;
            float var14 = -var10 * 0.848F + var11 * 0.53F;
            float var15 = Math.max(var12, Math.max(var13, var14));
            if (var15 < 0.45F) {
               return -1;
            } else if (var15 == var12) {
               return 0;
            } else {
               return var15 == var13 ? 1 : 2;
            }
         }
      }
   }

   private int resolve(float var1, float var2, int var3, int var4) {
      AutoSwap.Bounds var5 = this.process(var3, var4);

      for (int var6 = 0; var6 < 4; var6++) {
         for (int var7 = 0; var7 < 9; var7++) {
            float var8 = var5.startX + var7 * (var5.slotSize + var5.gap);
            float var9 = var5.startY + var6 * (var5.slotSize + var5.gap);
            if (var1 >= var8 && var1 <= var8 + var5.slotSize && var2 >= var9 && var2 <= var9 + var5.slotSize) {
               return this.compute(var6, var7);
            }
         }
      }

      return -1;
   }

   private AutoSwap.Bounds process(int var1, int var2) {
      float var3 = this.handle(Math.min(var1, var2) * 0.042F, 30.0F, 42.0F);
      float var4 = Math.max(4.0F, var3 * 0.14F);
      float var5 = var3 * 9.0F + var4 * 8.0F;
      float var6 = var3 * 4.0F + var4 * 3.0F;
      float var7 = (var1 - var5) / 2.0F;
      float var8 = (var2 - var6) / 2.0F;
      return new AutoSwap.Bounds(var7, var8, var3, var4, var5, var6);
   }

   private int compute(int var1, int var2) {
      return var1 == 3 ? var2 : 9 + var1 * 9 + var2;
   }

   private AutoSwap.DataRecord handle(int var1, int var2, int var3) {
      float var4 = this.resolve(var2, var3);
      float var5 = var2 / 2.0F;
      float var6 = var3 / 2.0F;
      float var7 = var4 * 1.35F;
      float var8 = var4 * 1.25F;
      float var9 = var4 * 0.85F;
      float var10 = var5 - var4 / 2.0F;
      float var11 = var6 - var8 - var4 / 2.0F;
      if (var1 == 1) {
         var10 = var5 + var7 - var4 / 2.0F;
         var11 = var6 + var9 - var4 / 2.0F;
      } else if (var1 == 2) {
         var10 = var5 - var7 - var4 / 2.0F;
         var11 = var6 + var9 - var4 / 2.0F;
      }

      return new AutoSwap.DataRecord(var10, var11, var4, Math.max(8.0F, var4 * 0.16F));
   }

   private float resolve(int var1, int var2) {
      return this.handle(Math.min(var1, var2) * 0.155F, 76.0F, 118.0F);
   }

   private void handle(
      RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10, float var11
   ) {
      ModuleStateHelper.handle(var1, var2, var3, var4, var5, var6, () -> {
         var1.handle(var2, var3, var4, var5, var6, var10);
         var1.process(var2, var3, var4, var5, 0.0F, var7, var8);
      });
      var1.handle(var2, var3, var4, var5, var6, var9, var11);
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, int var5) {
      float var6 = Math.max(2.0F, var4 * 0.16F);
      var1.handle(var2 - var6 / 2.0F, var3 - var4 / 2.0F, var6, var4, var6 / 2.0F, var5);
      var1.handle(var2 - var4 / 2.0F, var3 - var6 / 2.0F, var4, var6, var6 / 2.0F, var5);
   }

   private boolean handle(ItemStack var1) {
      if (Module.client.player != null && var1 != null && !var1.isEmpty()) {
         ItemStack var2 = Module.client.player.getOffHandStack();
         return var2 != null && !var2.isEmpty() && this.handle(var2, var1, this.process(var1));
      } else {
         return false;
      }
   }

   private boolean handle(ItemStack var1, ItemStack var2) {
      return this.handle(var1, var2, this.process(var2));
   }

   private boolean handle(ItemStack var1, ItemStack var2, String var3) {
      if (var1 == null || var1.isEmpty()) {
         return false;
      }

      if (var2 != null && !var2.isEmpty()) {
         String var5 = this.process(var3);
         if (!var5.isEmpty()) {
            return "ft:sphere:any".equals(var5) ? this.update(var1) : var5.equals(this.process(var1));
         } else if (!target.process("FT/RW") || !var2.isOf(Items.PLAYER_HEAD) && !this.update(var2)) {
            return !var1.isOf(var2.getItem()) ? false : var2.getComponentChanges().isEmpty() || ItemStack.areItemsAndComponentsEqual(var1, var2);
         } else {
            return this.update(var1);
         }
      } else {
         String var4 = this.process(var3);
         return "ft:sphere:any".equals(var4) ? this.update(var1) : !var4.isEmpty() && var4.equals(this.process(var1));
      }
   }

   private String update(int var1) {
      if (var1 >= 0 && var1 < 3) {
         String var2 = this.process(this.frameCheck[var1]);
         return !var2.isEmpty() ? var2 : this.process(this.positionAdvance[var1]);
      } else {
         return "";
      }
   }

   private String process(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = this.resolve(var1);
         if (!var2.isEmpty()) {
            return var2;
         }

         if (var1.contains(DataComponentTypes.CUSTOM_NAME)) {
            String var3 = this.compute(var1.getName().getString());
            if (!var3.isEmpty()) {
               return "name:" + Registries.ITEM.getId(var1.getItem()) + ":" + var3;
            }
         }

         return "item:" + Registries.ITEM.getId(var1.getItem());
      } else {
         return "";
      }
   }

   private String compute(ItemStack var1) {
      return this.process(var1);
   }

   private String handle(String var1, ItemStack var2) {
      String var3 = this.process(var1);
      if ("ft:sphere:any".equals(var3) && var2 != null && !var2.isEmpty()) {
         String var4 = this.process(var2);
         if (var4.startsWith("ft:sphere:") && !"ft:sphere:any".equals(var4)) {
            return var4;
         }
      }

      return var3;
   }

   private String resolve(ItemStack var1) {
      if (SpecialItemCatalog.handle(var1)) {
         return "ft:sphere:haos";
      } else if (SpecialItemCatalog.process(var1)) {
         return "ft:sphere:titan";
      } else if (SpecialItemCatalog.compute(var1)) {
         return "ft:sphere:ares";
      } else if (SpecialItemCatalog.resolve(var1)) {
         return "ft:sphere:besti";
      } else if (SpecialItemCatalog.update(var1)) {
         return "ft:sphere:gidra";
      } else if (SpecialItemCatalog.apply(var1)) {
         return "ft:sphere:ikara";
      } else if (SpecialItemCatalog.execute(var1)) {
         return "ft:sphere:erida";
      } else if (SpecialItemCatalog.prepare(var1)) {
         return "ft:sphere:satira";
      } else if (SpecialItemCatalog.check(var1)) {
         return "ft:sphere:moroz";
      } else if (SpecialItemCatalog.onTick(var1)) {
         return "ft:talisman:demon";
      } else if (SpecialItemCatalog.select(var1)) {
         return "ft:talisman:karatel";
      } else if (SpecialItemCatalog.refresh(var1)) {
         return "ft:talisman:mrak";
      } else if (SpecialItemCatalog.render(var1)) {
         return "ft:talisman:yaristi";
      } else if (SpecialItemCatalog.tick(var1)) {
         return "ft:talisman:tiran";
      } else if (SpecialItemCatalog.drawAnimation(var1)) {
         return "ft:talisman:krushitel";
      } else if (SpecialItemCatalog.encodePoint(var1)) {
         return "ft:talisman:razdor";
      } else if (SpecialItemCatalog.animate(var1)) {
         return "ft:talisman:sara";
      } else if (SpecialItemCatalog.save(var1)) {
         return "ft:potion:assassin";
      } else if (SpecialItemCatalog.submit(var1)) {
         return "ft:potion:gnev";
      } else if (SpecialItemCatalog.unload(var1)) {
         return "ft:potion:hlopushka";
      } else if (SpecialItemCatalog.fetch(var1)) {
         return "ft:potion:holy_water";
      } else if (SpecialItemCatalog.measure(var1)) {
         return "ft:potion:paladin";
      } else if (SpecialItemCatalog.blendMatrix(var1)) {
         return "ft:potion:radiation";
      } else if (SpecialItemCatalog.matchVector(var1)) {
         return "ft:potion:snotvornoye";
      } else if (SpecialItemCatalog.projectItem(var1)) {
         return "ft:item:light_dust";
      } else if (SpecialItemCatalog.computeResponse(var1)) {
         return "ft:item:disorientation";
      } else if (SpecialItemCatalog.fetchProvider(var1)) {
         return "ft:item:trapka";
      } else if (SpecialItemCatalog.drawProfile(var1)) {
         return "ft:item:lockpick_spheres";
      } else if (SpecialItemCatalog.performVector(var1)) {
         return "ft:item:plast";
      } else if (SpecialItemCatalog.invokeProfile(var1)) {
         return "ft:item:dragon_skin";
      } else if (SpecialItemCatalog.scheduleSource(var1)) {
         return "ft:item:fire_whirlwind";
      } else if (SpecialItemCatalog.renderTimer(var1)) {
         return "ft:item:freezing_snowball";
      } else if (SpecialItemCatalog.saveScale(var1)) {
         return "ft:item:gods_aura";
      } else {
         return SpecialItemCatalog.computeColor(var1) ? "ft:item:silver" : "";
      }
   }

   private String process(String var1) {
      return var1 == null ? "" : var1.trim().toLowerCase(Locale.ROOT);
   }

   private String compute(String var1) {
      return var1 == null
         ? ""
         : var1.replaceAll("§.", "").replaceAll("В§.", "").replaceAll("&.", "").replace(' ', ' ').replaceAll("\\s+", " ").trim().toLowerCase(Locale.ROOT);
   }

   private boolean update(ItemStack var1) {
      return var1 != null && var1.isOf(Items.PLAYER_HEAD) && (this.prepare(var1) || this.apply(var1) || this.execute(var1));
   }

   private boolean apply(ItemStack var1) {
      AttributeModifiersComponent var2 = (AttributeModifiersComponent)var1.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (var2 == null) {
         return false;
      }

      for (Entry var4 : var2.modifiers()) {
         if (var4.slot().matches(EquipmentSlot.OFFHAND)) {
            return true;
         }
      }

      return false;
   }

   private boolean execute(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         LoreComponent var2 = (LoreComponent)var1.get(DataComponentTypes.LORE);
         if (var2 == null) {
            return false;
         }

         String var3 = this.resolve(I18n.translate("item.modifiers.offhand", new Object[0]));
         StringBuilder var4 = new StringBuilder();

         for (Text var6 : var2.lines()) {
            String var7 = this.resolve(var6.getString());
            var4.append(' ').append(var7);
            if (var7.contains("when in off hand")
               || !var3.isEmpty() && var7.contains(var3)
               || var7.contains("когда во второстепенной")
               || var7.contains("коли в другій руці")
               || var7.contains("при ношении в левой")
               || var7.contains("в лівій руці")) {
               return true;
            }
         }

         String var8 = var4.toString();
         return var8.contains("when in off hand")
            || !var3.isEmpty() && var8.contains(var3)
            || var8.contains("когда во второстепенной")
            || var8.contains("коли в другій руці")
            || var8.contains("при ношении в левой")
            || var8.contains("в лівій руці");
      } else {
         return false;
      }
   }

   private boolean prepare(ItemStack var1) {
      if (Module.client.player != null && Module.client.world != null) {
         try {
            for (Text var3 : var1.getTooltip(TooltipContext.create(Module.client.world), Module.client.player, Default.BASIC)) {
               if (this.update(this.resolve(var3.getString()))) {
                  return true;
               }
            }

            return false;
         } catch (Throwable var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   private String resolve(String var1) {
      return var1 == null ? "" : var1.replaceAll("§[0-9a-fk-orA-FK-OR]", "").replace(' ', ' ').replaceAll("\\s+", " ").trim().toLowerCase(Locale.ROOT);
   }

   private boolean update(String var1) {
      String var2 = this.resolve(I18n.translate("item.modifiers.offhand", new Object[0]));
      return var1.contains("when in off hand")
         || !var2.isEmpty() && var1.contains(var2)
         || var1.contains("когда во второстепенной")
         || var1.contains("коли в другій руці")
         || var1.contains("при ношении в левой")
         || var1.contains("в лівій руці");
   }

   private void projectItem() {
      if (Module.client.getWindow() != null) {
         double[] var1 = new double[1];
         double[] var2 = new double[1];
         GLFW.glfwGetCursorPos(Module.client.getWindow().getHandle(), var1, var2);
         this.handle((float)var1[0], (float)var2[0]);
      }
   }

   private void computeResponse() {
      Window var1 = Module.client.getWindow();
      if (var1 != null && !var1.hasZeroWidthOrHeight() && var1.getFramebufferWidth() > 0 && var1.getFramebufferHeight() > 0) {
         this.scaleAdapt = var1.getFramebufferWidth() * 0.5F;
         this.textureRun = var1.getFramebufferHeight() * 0.5F;
      }
   }

   private void fetchProvider() {
      Window var1 = Module.client.getWindow();
      if (var1 != null && !var1.hasZeroWidthOrHeight() && var1.getWidth() > 0 && var1.getHeight() > 0) {
         GLFW.glfwSetCursorPos(var1.getHandle(), var1.getWidth() * 0.5, var1.getHeight() * 0.5);
      }
   }

   private void handle(float var1, float var2) {
      if (Float.isFinite(var1) && Float.isFinite(var2)) {
         Window var3 = Module.client.getWindow();
         if (var3 != null
            && !var3.hasZeroWidthOrHeight()
            && var3.getFramebufferWidth() > 0
            && var3.getFramebufferHeight() > 0
            && var3.getWidth() > 0
            && var3.getHeight() > 0) {
            this.scaleAdapt = this.handle(
               (float)((double)(var1 * var3.getFramebufferWidth()) / var3.getWidth()), 0.0F, Math.max(0.0F, var3.getFramebufferWidth() - 1.0F)
            );
            this.textureRun = this.handle(
               (float)((double)(var2 * var3.getFramebufferHeight()) / var3.getHeight()), 0.0F, Math.max(0.0F, var3.getFramebufferHeight() - 1.0F)
            );
            return;
         }

         this.scaleAdapt = var1;
         this.textureRun = var2;
      }
   }

   private boolean apply(int var1) {
      if (Module.client.getWindow() == null) {
         return false;
      }

      long var2 = Module.client.getWindow().getHandle();
      if (var1 >= 0) {
         return InputUtil.isKeyPressed(var2, var1);
      }

      if (var1 > -100) {
         return false;
      }

      int var4 = -var1 - 100;
      return var4 >= 0 && var4 <= 7 && GLFW.glfwGetMouseButton(var2, var4) == 1;
   }

   private void process(ItemStack var1, String var2) {
      NotificationHudSettings.handle(var1, var2, 2200L);
   }

   private void drawProfile() {
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   private String compute(ItemStack var1, String var2) {
      if (var1 == null || var1.isEmpty()) {
         return var2;
      } else if (SpecialItemCatalog.handle(var1)) {
         return "Сфера Хаоса";
      } else if (SpecialItemCatalog.process(var1)) {
         return "Сфера Титана";
      } else if (SpecialItemCatalog.compute(var1)) {
         return "Сфера Ареса";
      } else if (SpecialItemCatalog.resolve(var1)) {
         return "Сфера Бестии";
      } else if (SpecialItemCatalog.update(var1)) {
         return "Сфера Гидры";
      } else if (SpecialItemCatalog.apply(var1)) {
         return "Сфера Икара";
      } else if (SpecialItemCatalog.execute(var1)) {
         return "Сфера Эрида";
      } else if (SpecialItemCatalog.prepare(var1)) {
         return "Сфера Сатира";
      } else if (SpecialItemCatalog.check(var1)) {
         return "Сфера Мороз";
      } else if (SpecialItemCatalog.onTick(var1)) {
         return "Талисман Демона";
      } else if (SpecialItemCatalog.select(var1)) {
         return "Талисман Карателя";
      } else if (SpecialItemCatalog.refresh(var1)) {
         return "Талисман Мрака";
      } else if (SpecialItemCatalog.render(var1)) {
         return "Талисман Ярости";
      } else if (SpecialItemCatalog.tick(var1)) {
         return "Талисман Тирана";
      } else if (SpecialItemCatalog.drawAnimation(var1)) {
         return "Талисман Крушителя";
      } else if (SpecialItemCatalog.encodePoint(var1)) {
         return "Талисман Раздора";
      } else {
         return var1.contains(DataComponentTypes.CUSTOM_NAME) ? var1.getName().getString() : var2;
      }
   }

   private void performVector() {
      boolean var1 = this.sourceSchedule;
      if (this.moduleCollect > 0) {
         SyntheticKeyState.handle().process("AutoSwap");
      }

      this.sourceSchedule = false;
      this.scaleSave = -1;
      this.measure();
      vectorPerform = false;
      this.moduleCollect = 0;
      this.providerClose = 0;
      this.presetSave = -1;
      matrixBlend = false;
      this.profileInvoke = false;
      this.sourceBuild = -1;
      this.outputCollapse = "";
      this.presetWrite = ItemStack.EMPTY;
      this.colorMeasure = "";
      this.animationSchedule = 0;
      this.rendererScan = 0;
      if (var1 && Module.client.currentScreen == null && Module.client.mouse != null) {
         Module.client.mouse.lockCursor();
      }
   }

   @Override
   public JsonObject serialize() {
      JsonObject var1 = super.serialize();
      JsonObject var2 = new JsonObject();
      JsonArray var3 = new JsonArray();

      for (int var4 = 0; var4 < 3; var4++) {
         JsonObject var5 = new JsonObject();
         ItemStack var6 = this.positionAdvance[var4];
         if (var6 != null && !var6.isEmpty()) {
            var5.addProperty("item", Registries.ITEM.getId(var6.getItem()).toString());
            String var7 = this.update(var4);
            if (!var7.isEmpty()) {
               var5.addProperty("key", var7);
            }

            ItemStack var8 = var6.copy();
            var8.setCount(1);
            ItemStack.CODEC.encodeStart(this.attachEvent(), var8).result().ifPresent(var1x -> var5.add("stack", var1x));
         }

         var3.add(var5);
      }

      var2.add("Slots", var3);
      var1.add("AutoSwapTrio", var2);
      return var1;
   }

   @Override
   public void handle(JsonObject var1) {
      super.handle(var1);
      if (var1 != null && var1.has("AutoSwapTrio") && var1.get("AutoSwapTrio").isJsonObject()) {
         JsonObject var2 = var1.getAsJsonObject("AutoSwapTrio");
         if (var2.has("Slots") && var2.get("Slots").isJsonArray()) {
            ItemStack[] var3 = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
            String[] var4 = new String[]{"", "", ""};
            JsonArray var5 = var2.getAsJsonArray("Slots");

            for (int var6 = 0; var6 < Math.min(3, var5.size()); var6++) {
               JsonElement var7 = var5.get(var6);
               if (var7 != null && var7.isJsonObject()) {
                  JsonObject var8 = var7.getAsJsonObject();
                  if (var8.has("key")) {
                     var4[var6] = this.process(var8.get("key").getAsString());
                  }

                  if (var8.has("stack")) {
                     ItemStack var9 = ItemStack.CODEC.parse(this.attachEvent(), var8.get("stack")).result().orElse(ItemStack.EMPTY);
                     if (!var9.isEmpty()) {
                        var9.setCount(1);
                        var3[var6] = var9;
                        var4[var6] = this.handle(var4[var6], var9);
                        if (var4[var6].isEmpty()) {
                           var4[var6] = this.compute(var9);
                        }
                        continue;
                     }
                  }

                  if (var8.has("item")) {
                     Identifier var12 = Identifier.tryParse(var8.get("item").getAsString());
                     if (var12 != null) {
                        Item var10 = (Item)Registries.ITEM.get(var12);
                        if (var10 != Items.AIR) {
                           var3[var6] = new ItemStack(var10);
                           if (var4[var6].isEmpty()) {
                              var4[var6] = var10 == Items.PLAYER_HEAD && target.process("FT/RW") ? "ft:sphere:any" : this.compute(var3[var6]);
                           }
                        }
                     }
                  }
               }
            }

            for (int var11 = 0; var11 < 3; var11++) {
               this.positionAdvance[var11] = var3[var11];
               this.frameCheck[var11] = var4[var11];
            }
         }
      }
   }

   private DynamicOps<JsonElement> attachEvent() {
      if (Module.client.world != null) {
         return Module.client.world.getRegistryManager().getOps(JsonOps.INSTANCE);
      } else {
         return Module.client.getNetworkHandler() != null
            ? Module.client.getNetworkHandler().getRegistryManager().getOps(JsonOps.INSTANCE)
            : BuiltinRegistries.createWrapperLookup().getOps(JsonOps.INSTANCE);
      }
   }

   @Override
   public void process() {
      this.performVector();
      super.process();
   }

   private float handle(float var1, float var2, float var3) {
      return Math.max(var2, Math.min(var3, var1));
   }

   private float handle(float var1, float var2, float var3, float var4) {
      return var1 + (var2 - var1) * (1.0F - (float)Math.exp(-var4 * var3));
   }

   private float handle(float var1) {
      float var2 = 1.0F - this.handle(var1, 0.0F, 1.0F);
      return 1.0F - var2 * var2 * var2;
   }

   private float process(float var1, float var2, float var3) {
      return var1 + (var2 - var1) * var3;
   }

   record Bounds(float startX, float startY, float slotSize, float gap, float width, float height) {
   }

   record DataRecord(float x, float y, float size, float radius) {

      float centerX() {
         return this.x + this.size / 2.0F;
      }

      float centerY() {
         return this.y + this.size / 2.0F;
      }
   }
}
