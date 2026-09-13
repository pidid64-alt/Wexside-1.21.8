package ru.wild.modules.visuals;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ResettableSettingGroup;
import ru.wild.core.manager.FriendManager;
import ru.wild.gui.hud.DamageIndicatorTracker;
import ru.wild.gui.hud.TargetHudRenderer;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.modules.misc.AutoVillageTrade;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.render.EntityOverlayRenderer;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "NameTags", description = "Теги сущностей", category = ModuleCategory.Visuals)
public class NameTags extends Module {
   private static final float rendererScan = 0.0625F;
   private static final long sourceBuild = 250L;
   public final ModeSetting source = new ModeSetting("Режим отображения", "Legacy", "Legacy", "New");
   private static final int outputCollapse = RoundedRectRenderer.ColorState.compute(88, 220, 116, 255);
   private static final String profileInvoke = "Игроки";
   private static final String sourceSchedule = "Голые";
   private static final String timerRender = "Мобы";
   private static final String scaleSave = "Животные";
   private static final String colorCompute = "Предметы";
   public final ChoiceSetting target = new ChoiceSetting(
         "Цели",
         new BooleanSetting("Игроки", true),
         new BooleanSetting("Голые", true),
         new BooleanSetting("Мобы", false),
         new BooleanSetting("Животные", false),
         new BooleanSetting("Предметы", false)
      )
      .handle(() -> this.source.process("New"));
   public final ChoiceSetting pending = new ChoiceSetting("Тип", new BooleanSetting("Player", true), new BooleanSetting("Hologram", true))
      .handle(() -> this.source.process("New"));
   public final BooleanSetting previous = new BooleanSetting("Броня", true).handle(() -> this.source.process("Legacy") && !this.target.process("Игроки"));
   public final BooleanSetting latest = new BooleanSetting("Правая рука", true).handle(() -> this.source.process("Legacy") && !this.target.process("Игроки"));
   public final BooleanSetting summary = new BooleanSetting("Левая рука", true).handle(() -> this.source.process("Legacy") && !this.target.process("Игроки"));
   public final BooleanSetting matrixBlend = new BooleanSetting("Эффекты", true).handle(() -> !this.source.process("Legacy"));
   public final BooleanSetting vectorMatch = new BooleanSetting("Полоса HP", true).handle(() -> !this.source.process("Legacy"));
   public final BooleanSetting itemProject = new BooleanSetting("Невидимки", true).handle(() -> this.source.process("Legacy"));
   public final BooleanSetting responseCompute = new BooleanSetting("Инфо при наводке", true).handle(() -> this.source.process("Legacy"));
   public final NumberSetting providerFetch = new NumberSetting("Размер", 1.2F, 0.75F, 1.9F, 0.05F, true).handle(() -> this.source.process("Legacy"));
   public final NumberSetting profileDraw = new NumberSetting("Размер тега", 1.0F, 0.5F, 2.5F, 0.05F, false).handle(() -> this.source.process("New"));
   public final NumberSetting vectorPerform = new NumberSetting("Радиус деталей", 11.0F, 2.0F, 32.0F, 0.5F, false).handle(() -> this.source.process("Legacy"));
   public final ModeSetting eventAttach = new ModeSetting("Режим обводки", "Боксы", "Боксы", "Скелет", "Не рендерить")
      .handle(() -> !this.source.process("Legacy"));
   public final ModeSetting serverRead = new ModeSetting("Стилистика", "Тёмный", "Тёмный", "Светлый", "Блюр", "Неоморфизм", "Феррофлюид")
      .handle(() -> this.source.process("New"));
   public final BooleanSetting positionAdvance = new BooleanSetting("Показывать голову", true).handle(() -> this.source.process("New"));
   public final BooleanSetting frameCheck = new BooleanSetting("Отображать полные имена", false).handle(() -> this.source.process("New"));
   public final BooleanSetting moduleCollect = new BooleanSetting("Подсветка предметов", true).handle(() -> this.source.process("New"));
   public final BooleanSetting providerClose = new BooleanSetting("Тень плашек", true).handle(() -> this.source.process("New"));
   public final BooleanSetting presetSave = new BooleanSetting("Градиент текста", false).handle(() -> this.source.process("New"));
   public final BooleanSetting windowConvert = new BooleanSetting("Цвет предмета в градиенте", true)
      .handle(() -> this.source.process("New") || !this.presetSave.compute());
   public final ColorSetting presetWrite = new ColorSetting("Второй цвет текста", 47.0F, 0.45F, 1.0F)
      .process(() -> this.source.process("New") || !this.presetSave.compute());
   public final NumberSetting colorMeasure = new NumberSetting("Скорость градиента", 1.0F, 0.2F, 3.0F, 0.1F, false)
      .handle(() -> this.source.process("New") || !this.presetSave.compute());
   public final NumberSetting animationSchedule = new NumberSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.05F, true);
   private final EntityOverlayRenderer scaleAdapt = new EntityOverlayRenderer();
   private final Map<PlayerEntity, NameTags.PrimaryDataRecord> textureRun = new HashMap<>();
   private final Map<LivingEntity, NameTags.PrimaryDataRecord> indexBind = new HashMap<>();
   private final Map<ItemEntity, NameTags.PrimaryDataRecord> actionRead = new HashMap<>();
   private final Map<Integer, NameTags.SecondaryDataRecord> configCollapse = new HashMap<>();
   private final List<NameTags.FallbackDataRecord> dataValidate = new ArrayList<>();
   private final Set<Integer> scaleRender = new HashSet<>();
   private final List<ItemStack> clientRefresh = new ArrayList<>();
   private final List<ItemStack> keyFilter = new ArrayList<>();
   private final MatrixStack requestAdapt = new MatrixStack();
   private final Vector3f timerMeasure = new Vector3f();
   private final List<NameTags.DataRecord> vectorEncode = new ArrayList<>();
   private final List<float[]> requestReceive = new ArrayList<>();
   private boolean windowProcess;
   private float packetSave;
   private float entryAnimate;
   private float playerCollect;
   private float stateApply;
   private final Map<String, Float> matrixFilter = new HashMap<>();
   private final Map<Integer, Long> layerSample = new HashMap<>();
   private final ResettableSettingGroup worldSend = new ResettableSettingGroup() {};
   private boolean targetWrite = false;
   private float resultEncode = 0.0F;
   private float messageParse = 0.0F;
   private float providerRead = 0.0F;
   private float matrixBlend2 = 0.0F;
   private float scalePerform = 0.0F;
   private boolean contextExpand = false;
   private boolean keyProcess = false;
   private long actionConvert = 0L;
   private int screenRead;
   private int animationExpand;
   private int playerRun;
   private int matrixRender;
   private int moduleTick;
   private int playerCollapse;
   private static final ThemePaletteRegistry optionAdvance = ThemePaletteRegistry.handle();

   public NameTags() {
      this.handle(
         this.source,
         this.target,
         this.pending,
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
         this.animationSchedule,
         this.serverRead,
         this.positionAdvance,
         this.frameCheck,
         this.moduleCollect,
         this.providerClose,
         this.presetSave,
         this.windowConvert,
         this.presetWrite,
         this.colorMeasure
      );
      this.worldSend.handle(this.profileDraw);
      this.worldSend.handle(this.target);
      this.worldSend.handle(this.pending);
      this.worldSend.handle(this.previous);
      this.worldSend.handle(this.latest);
      this.worldSend.handle(this.summary);
      this.worldSend.handle(this.matrixBlend);
      this.worldSend.handle(this.vectorMatch);
      this.worldSend.handle(this.eventAttach);
      this.worldSend.handle(this.animationSchedule);
      this.worldSend.handle(this.serverRead);
      this.worldSend.handle(this.positionAdvance);
      this.worldSend.handle(this.frameCheck);
      this.worldSend.handle(this.moduleCollect);
      this.worldSend.handle(this.providerClose);
      this.worldSend.handle(this.presetSave);
      this.worldSend.handle(this.windowConvert);
      this.worldSend.handle(this.presetWrite);
      this.worldSend.handle(this.colorMeasure);
   }

   @Override
   public void handle() {
      this.scaleAdapt.handle();
      this.layerSample.clear();
      this.matrixFilter.clear();
      this.configCollapse.clear();
      super.handle();
   }

   @Override
   public void process() {
      this.scaleAdapt.handle();
      this.layerSample.clear();
      this.matrixFilter.clear();
      this.configCollapse.clear();
      super.process();
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.scaleAdapt.handle();
      this.textureRun.clear();
      this.indexBind.clear();
      this.actionRead.clear();
      this.dataValidate.clear();
      this.configCollapse.clear();
      this.matrixFilter.clear();
      this.layerSample.clear();
   }

   @EventHandler(handle = 0)
   public void handle(HudRenderContext var1) {
      if (this.enabled && !(Module.client.currentScreen instanceof InventoryScreen)) {
         if (this.source.process("New")) {
            this.scaleAdapt.handle(var1, this);
         } else {
            this.process(var1);
         }
      }
   }

   public boolean handle(int var1) {
      return this.enabled && var1 == 60;
   }

   private void process(HudRenderContext var1) {
      if (Module.client.world != null && Module.client.player != null) {
         this.refresh();
         this.vectorEncode.clear();
         this.requestReceive.clear();
         float var2 = Module.client.getRenderTickCounter().getTickProgress(true);
         this.handle(var2);
         this.render();
         RoundedRectRenderer var3 = var1.resolve();
         DrawContext var4 = var1.prepare();
         this.dataValidate.clear();
         float var5 = (float)Module.client.mouse.getX();
         float var6 = (float)Module.client.mouse.getY();
         boolean var7 = Module.client.currentScreen instanceof ChatScreen;
         Set var8 = this.scaleRender;
         var8.clear();
         if (!this.textureRun.isEmpty() || !this.indexBind.isEmpty() || !this.actionRead.isEmpty()) {
            for (Entry var10 : this.textureRun.entrySet()) {
               PlayerEntity var11 = (PlayerEntity)var10.getKey();
               NameTags.PrimaryDataRecord var12 = (NameTags.PrimaryDataRecord)var10.getValue();
               if (!this.handle(var12)) {
                  var8.add(var11.getId());
                  this.layerSample.putIfAbsent(var11.getId(), System.currentTimeMillis());
                  float var13 = MathHelper.clamp((float)(System.currentTimeMillis() - this.layerSample.get(var11.getId())) / 300.0F, 0.0F, 1.0F);
                  this.handle(var3, var4, var11, var12, var5, var6, var7, var2, var13);
               }
            }

            for (Entry var20 : this.indexBind.entrySet()) {
               LivingEntity var23 = (LivingEntity)var20.getKey();
               NameTags.PrimaryDataRecord var26 = (NameTags.PrimaryDataRecord)var20.getValue();
               if (!this.handle(var26)) {
                  var8.add(var23.getId());
                  this.layerSample.putIfAbsent(var23.getId(), System.currentTimeMillis());
                  float var29 = MathHelper.clamp((float)(System.currentTimeMillis() - this.layerSample.get(var23.getId())) / 300.0F, 0.0F, 1.0F);
                  if (!(var23 instanceof VillagerEntity var14 && this.handle(var3, var14, var26, var29))) {
                     this.handle(var3, var23, var26, var5, var6, var7, var29);
                  }
               }
            }

            for (Entry var21 : this.actionRead.entrySet()) {
               ItemEntity var24 = (ItemEntity)var21.getKey();
               NameTags.PrimaryDataRecord var27 = (NameTags.PrimaryDataRecord)var21.getValue();
               if (!this.handle(var27)) {
                  var8.add(var24.getId());
                  this.layerSample.putIfAbsent(var24.getId(), System.currentTimeMillis());
                  float var30 = MathHelper.clamp((float)(System.currentTimeMillis() - this.layerSample.get(var24.getId())) / 300.0F, 0.0F, 1.0F);
                  ItemStack var32 = var24.getStack();
                  this.handle(var3, var24, var27, var32, var30);
               }
            }
         }

         this.layerSample.keySet().retainAll(var8);
         this.handle(var3, var4);
         if (var7) {
            boolean var19 = GLFW.glfwGetMouseButton(Module.client.getWindow().getHandle(), 0) == 1;
            boolean var22 = GLFW.glfwGetMouseButton(Module.client.getWindow().getHandle(), 1) == 1;
            boolean var25 = var19 && !this.contextExpand;
            boolean var28 = var22 && !this.keyProcess;
            this.contextExpand = var19;
            this.keyProcess = var22;
            boolean var31 = this.handle(var3, var5, var6, var19, var25);
            if (!var31 && (var25 || var28) && System.currentTimeMillis() - this.actionConvert > 150L) {
               this.actionConvert = System.currentTimeMillis();
               boolean var33 = false;

               for (NameTags.DataRecord var16 : this.vectorEncode) {
                  if (this.handle(var5, var6, var16.x, var16.y, var16.w, var16.h)) {
                     var33 = true;
                     if (var25) {
                        this.targetWrite = !this.targetWrite;
                        if (this.targetWrite) {
                           this.resultEncode = var16.x;
                           this.messageParse = var16.y;
                           this.providerRead = var16.w;
                           this.matrixBlend2 = var16.h;
                        }
                     } else if (var16.playerName != null) {
                        FriendManager.process(var16.playerName);
                     }
                     break;
                  }
               }

               if (var25 && !var33 && this.targetWrite) {
                  NeoStyleOptions.Bounds var35 = NeoStyleOptions.handle(
                     var3, this.worldSend, this.resultEncode, this.messageParse, this.providerRead, this.matrixBlend2
                  );
                  if (!var35.contains(var5, var6, 8.0F)) {
                     this.targetWrite = false;
                  }
               }
            }

            float var34 = this.targetWrite ? 1.0F : 0.0F;
            this.scalePerform = this.scalePerform + (var34 - this.scalePerform) * 0.15F;
            if (this.scalePerform > 0.01F) {
               NeoStyleOptions.handle(
                  var3,
                  this.worldSend,
                  this.resultEncode,
                  this.messageParse,
                  this.providerRead,
                  this.matrixBlend2,
                  Module.client.getWindow().getScaledWidth(),
                  Module.client.getWindow().getScaledHeight(),
                  this.scalePerform,
                  var5,
                  var6,
                  var25,
                  var19
               );
            }
         } else {
            this.targetWrite = false;
            this.scalePerform = 0.0F;
            this.contextExpand = false;
            this.keyProcess = false;
            this.windowProcess = false;
         }
      }
   }

   private boolean handle(RoundedRectRenderer var1, float var2, float var3, boolean var4, boolean var5) {
      float var6 = 9.0F;
      if (this.windowProcess) {
         if (!var4) {
            this.windowProcess = false;
            if (WildClient.instance != null && WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }
         } else {
            float var7 = (var2 - this.playerCollect + (var3 - this.stateApply)) * 0.5F;
            float var8 = (this.entryAnimate + var7) / Math.max(1.0F, this.entryAnimate);
            this.profileDraw.handle(this.packetSave * var8);
         }
      }

      for (float[] var13 : this.requestReceive) {
         float var9 = var13[0] + var13[2];
         float var10 = var13[1] + var13[3];
         boolean var11 = !this.windowProcess && var2 >= var9 - var6 && var2 <= var9 + 2.0F && var3 >= var10 - var6 && var3 <= var10 + 2.0F;
         if (var11 && var5) {
            this.windowProcess = true;
            this.packetSave = this.profileDraw.compute();
            this.entryAnimate = var13[2];
            this.playerCollect = var2;
            this.stateApply = var3;
         }

         this.handle(var1, var9, var10, var11);
      }

      return this.windowProcess;
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, boolean var4) {
      int var5 = var4 ? RoundedRectRenderer.ColorState.compute(255, 255, 255, 180) : RoundedRectRenderer.ColorState.compute(255, 255, 255, 55);
      float var6 = 2.0F;
      float var7 = 4.0F;
      var1.handle(var2 - var6, var3 - var6, var6, var6, 1.0F, var5);
      var1.handle(var2 - var6 - var7, var3 - var6, var6, var6, 1.0F, var5);
      var1.handle(var2 - var6, var3 - var6 - var7, var6, var6, 1.0F, var5);
   }

   private void refresh() {
      float var1 = this.animationSchedule.compute();
      String var2 = this.serverRead.compute();
      if (var2.equals("Светлый")) {
         this.screenRead = RoundedRectRenderer.ColorState.compute(240, 240, 245, (int)(255.0F * var1));
         this.animationExpand = RoundedRectRenderer.ColorState.compute(220, 220, 225, (int)(200.0F * var1));
         this.playerRun = RoundedRectRenderer.ColorState.compute(200, 200, 200, (int)(180.0F * var1));
         this.matrixRender = RoundedRectRenderer.ColorState.compute(170, 170, 170, (int)(255.0F * var1));
         this.moduleTick = RoundedRectRenderer.ColorState.compute(30, 30, 30, 255);
         this.playerCollapse = RoundedRectRenderer.ColorState.compute(100, 100, 100, 255);
      } else if (var2.equals("Блюр")) {
         this.screenRead = RoundedRectRenderer.ColorState.compute(10, 10, 10, (int)(120.0F * var1));
         this.animationExpand = RoundedRectRenderer.ColorState.compute(30, 30, 30, (int)(90.0F * var1));
         this.playerRun = RoundedRectRenderer.ColorState.compute(255, 255, 255, (int)(40.0F * var1));
         this.matrixRender = RoundedRectRenderer.ColorState.compute(255, 255, 255, (int)(90.0F * var1));
         this.moduleTick = RoundedRectRenderer.ColorState.compute(250, 250, 250, 255);
         this.playerCollapse = RoundedRectRenderer.ColorState.compute(200, 200, 200, 255);
      } else if (var2.equals("Неоморфизм")) {
         this.screenRead = ThemeShaderApplier.handle(var1);
         this.animationExpand = ThemeShaderApplier.handle(var1);
         this.playerRun = RoundedRectRenderer.ColorState.compute(0, 0, 0, 0);
         this.matrixRender = RoundedRectRenderer.ColorState.compute(0, 0, 0, 0);
         this.moduleTick = ThemeShaderApplier.process(1.0F);
         this.playerCollapse = ThemeShaderApplier.compute(1.0F);
      } else {
         this.screenRead = RoundedRectRenderer.ColorState.compute(25, 25, 26, (int)(255.0F * var1));
         this.animationExpand = RoundedRectRenderer.ColorState.compute(35, 35, 35, (int)(170.0F * var1));
         this.playerRun = RoundedRectRenderer.ColorState.compute(78, 78, 78, (int)(176.0F * var1));
         this.matrixRender = RoundedRectRenderer.ColorState.compute(120, 120, 120, (int)(255.0F * var1));
         this.moduleTick = RoundedRectRenderer.ColorState.compute(240, 240, 240, 255);
         this.playerCollapse = RoundedRectRenderer.ColorState.compute(200, 200, 200, 255);
      }

      this.handle(var1, var2);
   }

   private void handle(float var1, String var2) {
      ThemePalette var3 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
      boolean var4 = "Светлый".equals(var2) || optionAdvance.compute(var3) || ThemeShaderApplier.resolve();
      ThemeColors var5 = ThemeColors.handle(var3, var4);
      int var6 = PackedColor.compute(var5.save(), var5.submit(), 0.42F);
      if (var4 && !"Неоморфизм".equals(var2)) {
         this.screenRead = PackedColor.handle(PackedColor.compute(-196865, var5.save(), 0.026F), (int)(184.0F * var1));
         this.animationExpand = PackedColor.handle(PackedColor.compute(-1, var5.submit(), 0.04F), (int)(210.0F * var1));
         this.playerRun = PackedColor.handle(PackedColor.compute(-15261133, var6, 0.34F), (int)(48.0F * var1));
         this.matrixRender = PackedColor.handle(PackedColor.compute(-15261133, var6, 0.56F), (int)(92.0F * var1));
         this.moduleTick = PackedColor.handle(PackedColor.compute(-15722718, var5.save(), 0.035F), 255);
         this.playerCollapse = PackedColor.handle(PackedColor.compute(-12168086, var5.submit(), 0.055F), 255);
      } else if ("Феррофлюид".equals(var2)) {
         this.screenRead = PackedColor.handle(PackedColor.compute(-15657182, var5.save(), 0.1F), (int)(230.0F * var1));
         this.animationExpand = PackedColor.handle(PackedColor.compute(-15393492, var5.submit(), 0.14F), (int)(235.0F * var1));
         this.playerRun = PackedColor.handle(var6, (int)(72.0F * var1));
         this.matrixRender = PackedColor.handle(var6, (int)(122.0F * var1));
         this.moduleTick = RoundedRectRenderer.ColorState.compute(246, 248, 255, 255);
         this.playerCollapse = RoundedRectRenderer.ColorState.compute(188, 197, 214, 255);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      PlayerEntity var3,
      NameTags.PrimaryDataRecord var4,
      float var5,
      float var6,
      boolean var7,
      float var8,
      float var9
   ) {
      float var10 = (float)MathHelper.clamp(16.0 / Math.max(var4.distance(), 12.0), 0.75, 1.15) * this.profileDraw.compute();
      float var11 = Math.abs(var4.feetY() - var4.headY());
      float var12 = Math.max(4.0F * var10, var4.boxRight() - var4.boxLeft());
      float var13 = var4.boxLeft();
      float var14 = Math.min(var4.headY(), var4.feetY());
      float var15 = 6.0F * var10;
      String var16 = var3.getGameProfile() != null ? var3.getGameProfile().getName() : var3.getName().getString();
      String var17 = ProtectInfo.compute(var16);
      String var18 = TargetHudRenderer.handle(var3);
      int var19 = TargetHudRenderer.handle(var3, RoundedRectRenderer.ColorState.handle(255, 70, 70), 255);
      boolean var20 = FriendManager.handle(var16);
      if (this.eventAttach.process("Боксы")) {
         float var21 = var12 * 0.25F;
         float var22 = Math.max(1.0F, 1.5F * var10);
         long var23 = System.currentTimeMillis();
         float var25 = (float)(Math.sin(var23 / 200.0) + 1.0) / 2.0F;
         int var26 = this.handle(
            RoundedRectRenderer.ColorState.compute(150, 150, 150, 150), RoundedRectRenderer.ColorState.compute(255, 255, 255, 220), var25 * 0.4F
         );
         var26 = this.handle(var26, var9);
         var1.handle(var13, var14, var21, var22, 0.0F, var26);
         var1.handle(var13, var14, var22, var21, 0.0F, var26);
         var1.handle(var13 + var12 - var21, var14, var21, var22, 0.0F, var26);
         var1.handle(var13 + var12 - var22, var14, var22, var21, 0.0F, var26);
         var1.handle(var13, var14 + var11 - var22, var21, var22, 0.0F, var26);
         var1.handle(var13, var14 + var11 - var21, var22, var21, 0.0F, var26);
         var1.handle(var13 + var12 - var21, var14 + var11 - var22, var21, var22, 0.0F, var26);
         var1.handle(var13 + var12 - var22, var14 + var11 - var21, var22, var21, 0.0F, var26);
      } else if (this.eventAttach.process("Скелет")) {
         this.handle(var1, var3, var9);
      } else {
         this.eventAttach.process("Не рендерить");
      }

      float var60 = TargetHudRenderer.handle((LivingEntity)var3);
      String var61 = Integer.toString(Math.round(var60));
      String var62 = " HP";
      String var24 = var20 ? "[FRIEND] " : "";
      float var63 = 22.0F * var10;
      float var65 = 22.0F * var10;
      float var27 = 16.0F * var10;
      float var28 = 6.0F * var10;
      float var29 = 4.0F * var10;
      float var30 = 22.0F * var10;
      float var31 = var24.isEmpty() ? 0.0F : RoundedRectRenderer.handle(FontRegistry.instance, var24, var63).instance;
      float var32 = RoundedRectRenderer.handle(FontRegistry.instance, var17, var63).instance;
      float var33 = var18.isEmpty() ? 0.0F : RoundedRectRenderer.handle(FontRegistry.instance, var18, var65).instance;
      float var34 = RoundedRectRenderer.handle(FontRegistry.instance, var61, var63).instance;
      float var35 = RoundedRectRenderer.handle(FontRegistry.instance, var62, var63).instance;
      float var36 = var31 + (var18.isEmpty() ? 0.0F : var33 + var29) + var32 + var29 + var34 + var35;
      float var37 = var36 + var28 * 2.0F;
      float var38 = var37 + (this.positionAdvance.compute() ? var30 + var29 : 0.0F);
      float var39 = var4.screenX() - var38 / 2.0F;
      float var40 = var14 - var30 - 8.0F * var10;
      DamageIndicatorTracker.handle(var3.getUuid(), var40);
      List<ItemStack> var41 = this.clientRefresh;
      var41.clear();
      if (this.previous.compute()) {
         ItemStack var42 = var3.getEquippedStack(EquipmentSlot.HEAD);
         if (!var42.isEmpty()) {
            var41.add(var42);
         }

         ItemStack var43 = var3.getEquippedStack(EquipmentSlot.CHEST);
         if (!var43.isEmpty()) {
            var41.add(var43);
         }

         ItemStack var44 = var3.getEquippedStack(EquipmentSlot.LEGS);
         if (!var44.isEmpty()) {
            var41.add(var44);
         }

         ItemStack var45 = var3.getEquippedStack(EquipmentSlot.FEET);
         if (!var45.isEmpty()) {
            var41.add(var45);
         }
      }

      if (!var41.isEmpty()) {
         float var66 = 18.0F * var10;
         float var71 = 4.0F * var10;
         float var75 = var41.size() * var66 + (var41.size() - 1) * var71;
         float var77 = var4.screenX() - var75 / 2.0F;
         float var46 = var40 - var66 - 6.0F * var10;
         DamageIndicatorTracker.handle(var3.getUuid(), var46);
         int var47 = 0;

         for (ItemStack var49 : var41) {
            this.handle(var1, var77, var46, var66, var66, var15, var9, var49);
            this.handle(var3, var49, var77 + var10, var46 + var10, var47, var10, 0);
            var77 += var66 + var71;
            var47++;
         }
      }

      float var67 = var39;
      if (this.positionAdvance.compute()) {
         this.handle(var1, var67, var40, var30, var30, var15, var9, 0.0F);
         float var72 = var30 - 4.0F * var10;
         this.handle(var1, var3, var67 + 2.0F * var10, var40 + 2.0F * var10, var72, var9);
         var67 += var30 + var29;
      }

      if (var20) {
         int var73 = this.handle(RoundedRectRenderer.ColorState.compute(25, 80, 25, 255), var9);
         var1.handle(var67, var40, var37, var30, var15, var73);
      } else {
         this.handle(var1, var67, var40, var37, var30, var15, var9, 0.0F);
      }

      this.vectorEncode.add(new NameTags.DataRecord(var67, var40, var37, var30, var16));
      if (var7) {
         this.requestReceive.add(new float[]{var67, var40, var37, var30});
      }

      var67 += var28;
      float var74 = var40 + 15.0F * var10;
      if (!var24.isEmpty()) {
         var1.handle(FontRegistry.instance, var67, var74, var63, var24, this.handle(RoundedRectRenderer.ColorState.compute(60, 150, 255, 255), var9));
         var67 += var31;
      }

      if (!var18.isEmpty()) {
         var1.handle(FontRegistry.instance, var67, var74, var65, var18, this.handle(var19, var9));
         var67 += var33 + var29;
      }

      this.handle(var1, FontRegistry.instance, var67, var74, var63, var17, this.handle(var20 ? outputCollapse : this.moduleTick, var9));
      var67 += var32 + var29;
      var1.handle(FontRegistry.instance, var67, var74, var63, var61, this.handle(this.handle(var60, var3.getMaxHealth()), var9));
      var67 += var34;
      var1.handle(FontRegistry.instance, var67, var74, var63, var62, this.handle(RoundedRectRenderer.ColorState.compute(150, 150, 150, 255), var9));
      float var76 = MathHelper.clamp(var60 / var3.getMaxHealth(), 0.0F, 1.0F);
      int var78 = this.handle(RoundedRectRenderer.ColorState.compute(60, 150, 255, 255), var9);
      float var79 = 2.0F * var10;
      float var80 = var13 - var79 - 4.0F * var10;
      if (this.vectorMatch.compute()) {
         var1.handle(var80, var14, var79, var11, 1.0F, this.handle(RoundedRectRenderer.ColorState.compute(0, 0, 0, 100), var9));
         float var81 = var11 * var76;
         var1.handle(var80, var14 + (var11 - var81), var79, var81, 1.0F, var78);
      }

      float var82 = var13 + var12 + 8.0F * var10;
      float var83 = var14;
      float var50 = 20.0F * var10;
      if (this.matrixBlend.compute()) {
         for (StatusEffectInstance var52 : var3.getStatusEffects()) {
            String var53 = Text.translatable(var52.getTranslationKey()).getString();
            int var54 = var52.getAmplifier() + 1;
            String var55 = var53 + (var54 > 1 ? " " + var54 : "");
            int var56 = ((StatusEffect)var52.getEffectType().value()).isBeneficial()
               ? this.moduleTick
               : RoundedRectRenderer.ColorState.compute(255, 60, 60, 255);
            var1.handle(FontRegistry.instance, var82, var83 + 10.0F * var10, var50, var55, this.handle(var56, var9));
            var83 += 12.0F * var10;
         }
      }

      List var84 = this.keyFilter;
      var84.clear();
      if (this.summary.compute() && !var3.getOffHandStack().isEmpty()) {
         var84.add(var3.getOffHandStack());
      }

      if (this.latest.compute() && !var3.getMainHandStack().isEmpty()) {
         var84.add(var3.getMainHandStack());
      }

      float var85 = var14 + var11 + 6.0F * var10;
      float var86 = 22.0F * var10;
      float var87 = 4.0F * var10;
      if (this.frameCheck.compute()) {
         float var88 = var85;

         for (int var90 = 0; var90 < var84.size(); var90++) {
            var88 += this.handle(var1, var3, (ItemStack)var84.get(var90), var4.screenX(), var88, var10, var9, 99 + var90, 1) + 3.0F * var10;
         }
      } else {
         float var89 = var84.size() * var86 + Math.max(0, var84.size() - 1) * var87;
         float var91 = var4.screenX() - var89 / 2.0F;

         for (int var57 = 0; var57 < var84.size(); var57++) {
            ItemStack var58 = (ItemStack)var84.get(var57);
            this.handle(var1, var91, var85, var86, var86, var15, var9, var58);
            float var59 = 3.0F * var10;
            this.handle(var3, var58, var91 + var59, var85 + var59, 99 + var57, var10, 1);
            var91 += var86 + var87;
         }
      }
   }

   private float handle(RoundedRectRenderer var1, PlayerEntity var2, ItemStack var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      float var10 = 22.0F * var6;
      float var11 = var10;
      float var12 = 4.0F * var6;
      float var13 = 7.0F * var6;
      float var14 = 18.0F * var6;
      float var15 = Math.max(70.0F * var6, Math.min(190.0F * var6, Module.client.getWindow().getFramebufferWidth() * 0.28F));
      String var16 = this.handle(this.handle(var3, true), var14, var15);
      float var17 = RoundedRectRenderer.handle(FontRegistry.data, var16, var14).instance;
      float var18 = var17 + var13 * 2.0F;
      float var19 = var11 + var12 + var18;
      float var20 = var4 - var19 / 2.0F;
      float var21 = 6.0F * var6;
      this.handle(var1, var20, var5, var11, var11, var21, var7, var3);
      this.handle(var2, var3, var20 + 3.0F * var6, var5 + 3.0F * var6, var8, var6, var9);
      float var22 = var20 + var11 + var12;
      this.handle(var1, var22, var5, var18, var10, var21, var7, var3);
      this.handle(var1, FontRegistry.data, var22 + var13, var5 + 15.0F * var6, var14, var16, var3, var8, var7);
      return var10;
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7, ItemStack var8) {
      if (this.moduleCollect.compute()) {
         int var9 = this.handle(var8, var7);
         var1.handle(var2, var3, var4, var5, var6, 5.0F, 1.0F, this.handle(var9, 0.55F));
      }

      this.handle(var1, var2, var3, var4, var5, var6, var7, 0.0F);
   }

   public static void handle(PlayerEntityRenderState var0, PlayerEntityModel var1, MatrixStack var2) {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         NameTags var3 = WildClient.instance.data.handle(NameTags.class);
         if (var3 != null) {
            var3.process(var0, var1, var2);
         }
      }
   }

   private void process(PlayerEntityRenderState var1, PlayerEntityModel var2, MatrixStack var3) {
      if (this.enabled && Module.client != null && Module.client.player != null && Module.client.world != null) {
         if (!(Module.client.currentScreen instanceof InventoryScreen)) {
            if (this.source.process("Legacy") && this.eventAttach.process("Скелет")) {
               if (var1 != null && var2 != null && var3 != null) {
                  if (!var1.spectator && !var1.invisible && !var1.invisibleToPlayer) {
                     if (var1.id != Module.client.player.getId() || Module.client.options.getPerspective() != Perspective.FIRST_PERSON) {
                        Vec3d var4 = Module.client.gameRenderer.getCamera().getPos();
                        ArrayList var5 = new ArrayList(14);
                        Vec3d var6 = this.handle(var2.body, var3, var4, 0.0F, 0.0F, 0.0F);
                        Vec3d var7 = this.handle(var2.body, var3, var4, 0.0F, 6.0F, 0.0F);
                        Vec3d var8 = this.handle(var2.body, var3, var4, 0.0F, 12.0F, 0.0F);
                        Vec3d var9 = this.handle(var2.head, var3, var4, 0.0F, -8.0F, 0.0F);
                        Vec3d var10 = this.handle(var2.head, var3, var4, 0.0F, 0.0F, 0.0F);
                        Vec3d var11 = this.handle(var2.rightArm, var3, var4, 0.0F, 0.0F, 0.0F);
                        Vec3d var12 = this.handle(var2.rightArm, var3, var4, 0.0F, 4.5F, 0.0F);
                        Vec3d var13 = this.handle(var2.rightArm, var3, var4, 0.0F, 10.0F, 0.0F);
                        Vec3d var14 = this.handle(var2.leftArm, var3, var4, 0.0F, 0.0F, 0.0F);
                        Vec3d var15 = this.handle(var2.leftArm, var3, var4, 0.0F, 4.5F, 0.0F);
                        Vec3d var16 = this.handle(var2.leftArm, var3, var4, 0.0F, 10.0F, 0.0F);
                        Vec3d var17 = this.handle(var2.rightLeg, var3, var4, 0.0F, 0.0F, 0.0F);
                        Vec3d var18 = this.handle(var2.rightLeg, var3, var4, 0.0F, 6.0F, 0.0F);
                        Vec3d var19 = this.handle(var2.rightLeg, var3, var4, 0.0F, 12.0F, 0.0F);
                        Vec3d var20 = this.handle(var2.leftLeg, var3, var4, 0.0F, 0.0F, 0.0F);
                        Vec3d var21 = this.handle(var2.leftLeg, var3, var4, 0.0F, 6.0F, 0.0F);
                        Vec3d var22 = this.handle(var2.leftLeg, var3, var4, 0.0F, 12.0F, 0.0F);
                        this.handle(var5, var6, var7);
                        this.handle(var5, var7, var8);
                        this.handle(var5, var9, var10);
                        this.handle(var5, var14, var11);
                        this.handle(var5, var20, var17);
                        this.handle(var5, var14, var15);
                        this.handle(var5, var15, var16);
                        this.handle(var5, var11, var12);
                        this.handle(var5, var12, var13);
                        this.handle(var5, var20, var21);
                        this.handle(var5, var21, var22);
                        this.handle(var5, var17, var18);
                        this.handle(var5, var18, var19);
                        if (!var5.isEmpty()) {
                           this.configCollapse.put(var1.id, new NameTags.SecondaryDataRecord(var5, System.currentTimeMillis()));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Vec3d handle(ModelPart var1, MatrixStack var2, Vec3d var3, float var4, float var5, float var6) {
      this.requestAdapt.loadIdentity();
      this.requestAdapt.peek().getPositionMatrix().set(var2.peek().getPositionMatrix());
      var1.applyTransform(this.requestAdapt);
      Matrix4f var7 = this.requestAdapt.peek().getPositionMatrix();
      Vector3f var8 = this.timerMeasure.set(var4 * 0.0625F, var5 * 0.0625F, var6 * 0.0625F);
      var7.transformPosition(var8);
      return var3.add(var8.x, var8.y, var8.z);
   }

   private void handle(List<NameTags.Range> var1, Vec3d var2, Vec3d var3) {
      if (var2 != null && var3 != null) {
         var1.add(new NameTags.Range(var2, var3));
      }
   }

   private void handle(RoundedRectRenderer var1, PlayerEntity var2, float var3) {
      NameTags.SecondaryDataRecord var4 = this.configCollapse.get(var2.getId());
      if (var4 != null && System.currentTimeMillis() - var4.capturedAt() <= 250L) {
         for (NameTags.Range var6 : var4.bones()) {
            this.handle(var1, var6.start(), var6.end(), var3);
         }
      }
   }

   private void render() {
      long var1 = System.currentTimeMillis();
      this.configCollapse.entrySet().removeIf(var2 -> var1 - var2.getValue().capturedAt() > 250L);
   }

   private void handle(RoundedRectRenderer var1, Vec3d var2, Vec3d var3, float var4) {
      Vec3d var5 = this.handle(var2.x, var2.y, var2.z);
      Vec3d var6 = this.handle(var3.x, var3.y, var3.z);
      if (var5 != null && var6 != null) {
         double var7 = Module.client.gameRenderer.getCamera().getPos().distanceTo(var2);
         float var9 = MathHelper.clamp((float)(12.0 / Math.max(var7, 1.0)), 1.0F, 10.0F);
         float var10 = var4 * this.animationSchedule.compute();
         int var11 = this.handle(RoundedRectRenderer.ColorState.compute(200, 200, 210, 255), var10);
         float var12 = (float)Math.hypot(var6.x - var5.x, var6.y - var5.y);
         float var13 = (float)Math.toDegrees(Math.atan2(var6.y - var5.y, var6.x - var5.x));
         var1.handle((float)var5.x, (float)var5.y);
         var1.process(var13);
         if (!(var7 > 12.0) && !(var9 < 2.0F)) {
            int var14 = this.handle(RoundedRectRenderer.ColorState.compute(20, 20, 20, 180), var10);
            int var15 = this.handle(RoundedRectRenderer.ColorState.compute(255, 255, 255, 255), var10);
            var1.handle(0.0F, -var9 * 0.3F, var12, var9 * 0.6F, 0.0F, var11);
            var1.handle(0.0F, -var9 * 0.1F, var12, var9 * 0.2F, 0.0F, var15);
         } else {
            var1.handle(0.0F, -var9 / 2.0F, var12, var9, 0.0F, var11);
         }

         var1.execute();
         var1.prepare();
      }
   }

   private Vec3d handle(double var1, double var3, double var5) {
      Camera var7 = Module.client.gameRenderer.getCamera();
      Vec3d var8 = new Vec3d(var1, var3, var5);
      if (var8.squaredDistanceTo(var7.getPos()) < 1.0E-6) {
         return null;
      }

      Vec3d var9 = ClientMathUtil.handle(var8);
      return !(var9.z <= 0.001F) && !(var9.z > 1.0) ? var9 : null;
   }

   private void handle(float var1) {
      this.textureRun.clear();
      this.indexBind.clear();
      this.actionRead.clear();
      if (Module.client.world != null && Module.client.player != null) {
         if (this.target.process("Игроки")) {
            this.process(var1);
         }

         if (this.target.process("Мобы") || this.target.process("Животные") || this.drawAnimation()) {
            this.compute(var1);
         }

         if (this.target.process("Предметы")) {
            this.resolve(var1);
         }
      }
   }

   private void process(float var1) {
      for (PlayerEntity var3 : Module.client.world.getPlayers()) {
         if (var3 != null
            && var3.isAlive()
            && (var3 != Module.client.player || !Module.client.options.getPerspective().isFirstPerson())
            && (this.target.process("Голые") || var3.getArmor() != 0 || var3 == Module.client.player)) {
            NameTags.PrimaryDataRecord var4 = this.handle(var1, var3, var3.getHeight() + 0.2, 0.02);
            if (var4 != null) {
               this.textureRun.put(var3, var4);
            }
         }
      }
   }

   private void compute(float var1) {
      AutoVillageTrade var2 = this.tick();

      for (Entity var4 : Module.client.world.getEntities()) {
         if (var4 instanceof LivingEntity var5
            && var5.isAlive()
            && var5 != Module.client.player
            && !(var5 instanceof PlayerEntity)
            && !(var5 instanceof ArmorStandEntity)) {
            boolean var6 = var5 instanceof VillagerEntity var7 && var2 != null && var2.handle(var7) != null;
            if ((!this.handle(var5) || this.target.process("Мобы") || var6)
               && (!this.process(var5) || this.target.process("Животные"))
               && (this.handle(var5) || this.process(var5) || var6)) {
               NameTags.PrimaryDataRecord var8 = this.handle(var1, var5, var5.getHeight() + 0.18, 0.02);
               if (var8 != null) {
                  this.indexBind.put(var5, var8);
               }
            }
         }
      }
   }

   private void resolve(float var1) {
      for (Entity var3 : Module.client.world.getEntities()) {
         if (var3 instanceof ItemEntity var4 && var4.isAlive() && !var4.getStack().isEmpty()) {
            NameTags.PrimaryDataRecord var5 = this.handle(var1, var4, 0.52, 0.0);
            if (var5 != null) {
               this.actionRead.put(var4, var5);
            }
         }
      }
   }

   private NameTags.PrimaryDataRecord handle(float var1, Entity var2, double var3, double var5) {
      Camera var7 = Module.client.gameRenderer.getCamera();
      Vec3d var8 = var7.getPos();
      Vec3d var9 = var2.getLerpedPos(var1);
      Box var10 = var2.getBoundingBox();
      Vec3d var11 = var2.getPos();
      Box var12 = var10.offset(var9.x - var11.x, var9.y - var11.y, var9.z - var11.z);
      Box var13 = new Box(var12.minX - 0.02, var9.y + var5, var12.minZ - 0.02, var12.maxX + 0.02, var9.y + var3, var12.maxZ + 0.02);
      NameTags.PrimaryDataRecord var14 = this.handle(var13, var8);
      if (var14 != null) {
         return var14;
      } else {
         Vec3d var15 = new Vec3d(var9.x, var9.y + var3, var9.z);
         Vec3d var16 = new Vec3d(var9.x, var9.y + var5, var9.z);
         if (var15.squaredDistanceTo(var8) < 1.0E-6) {
            return null;
         } else {
            Vec3d var17 = ClientMathUtil.handle(var15);
            Vec3d var18 = ClientMathUtil.handle(var16);
            if (var17.z <= 0.001F || var17.z > 1.0) {
               return null;
            } else if (!(var18.z <= 0.001F) && !(var18.z > 1.0)) {
               double var19 = var8.distanceTo(var15);
               float var21 = Math.abs((float)var18.y - (float)var17.y);
               float var22 = var21 * 0.45F;
               float var23 = (float)var17.x;
               return new NameTags.PrimaryDataRecord(var23, (float)var17.y, (float)var18.y, (float)var17.z, var19, var23 - var22 / 2.0F, var23 + var22 / 2.0F);
            } else {
               return null;
            }
         }
      }
   }

   private NameTags.PrimaryDataRecord handle(Box var1, Vec3d var2) {
      float var3 = Float.POSITIVE_INFINITY;
      float var4 = Float.POSITIVE_INFINITY;
      float var5 = Float.NEGATIVE_INFINITY;
      float var6 = Float.NEGATIVE_INFINITY;
      float var7 = 0.0F;
      double var8 = (var1.minX + var1.maxX) * 0.5;
      double var10 = (var1.minY + var1.maxY) * 0.5;
      double var12 = (var1.minZ + var1.maxZ) * 0.5;

      for (int var14 = 0; var14 < 2; var14++) {
         double var15 = var14 == 0 ? var1.minX : var1.maxX;

         for (int var17 = 0; var17 < 2; var17++) {
            double var18 = var17 == 0 ? var1.minY : var1.maxY;

            for (int var20 = 0; var20 < 2; var20++) {
               double var21 = var20 == 0 ? var1.minZ : var1.maxZ;
               Vec3d var23 = ClientMathUtil.handle(new Vec3d(var15, var18, var21));
               if (var23 == null || var23.z <= 0.001F || var23.z > 1.0) {
                  return null;
               }

               var3 = Math.min(var3, (float)var23.x);
               var4 = Math.min(var4, (float)var23.y);
               var5 = Math.max(var5, (float)var23.x);
               var6 = Math.max(var6, (float)var23.y);
               var7 += (float)var23.z;
            }
         }
      }

      if (Float.isFinite(var3) && Float.isFinite(var4) && Float.isFinite(var5) && Float.isFinite(var6)) {
         double var24 = var2.distanceTo(new Vec3d(var8, var10, var12));
         return new NameTags.PrimaryDataRecord((var3 + var5) * 0.5F, var4, var6, var7 / 8.0F, var24, var3, var5);
      } else {
         return null;
      }
   }

   private boolean handle(NameTags.PrimaryDataRecord var1) {
      return var1 == null || var1.depth() <= 0.001F || var1.depth() > 1.0F;
   }

   private int handle(float var1, float var2) {
      float var3 = MathHelper.clamp(var1 / Math.max(1.0F, var2), 0.0F, 1.0F);
      int var4 = var3 >= 0.5F ? (int)(255.0F * (1.0F - var3) * 2.0F) : 255;
      int var5 = var3 >= 0.5F ? 255 : (int)(255.0F * var3 * 2.0F);
      return RoundedRectRenderer.ColorState.compute(var4, var5, 50, 255);
   }

   private boolean handle(RoundedRectRenderer var1, VillagerEntity var2, NameTags.PrimaryDataRecord var3, float var4) {
      AutoVillageTrade var5 = this.tick();
      if (var5 == null) {
         return false;
      } else {
         AutoVillageTrade.DataRecord var6 = var5.handle(var2);
         if (var6 != null && var6.itemStack() != null && !var6.itemStack().isEmpty()) {
            ItemStack var7 = var6.itemStack();
            float var8 = (float)MathHelper.clamp(16.0 / Math.max(var3.distance(), 12.0), 0.75, 1.15) * this.profileDraw.compute();
            float var9 = 6.0F * var8;
            float var10 = 18.0F * var8;
            float var11 = 22.0F * var8;
            float var12 = 4.0F * var8;
            float var13 = 6.0F * var8;
            String var14 = var6.price() + " изумр. · x" + var6.availableAmount();
            float var15 = RoundedRectRenderer.handle(FontRegistry.data, var14, var10).instance;
            float var16 = var15 + var13 * 2.0F;
            float var17 = var11 + var12 + var16;
            float var18 = var3.screenX() - var17 / 2.0F;
            float var19 = var3.headY() - 18.0F * var8;
            this.handle(var1, var18, var19, var11, var11, var9, var4, var7);
            float var20 = (var11 - 16.0F * var8) / 2.0F;
            this.handle(Module.client.player, var7, var18 + var20, var19 + var20, var2.getId(), var8, 0);
            float var21 = var18 + var11 + var12;
            this.handle(var1, var21, var19, var16, var11, var9, var4, var7);
            this.handle(var1, FontRegistry.data, var21 + var13, var19 + 15.0F * var8, var10, var14, var7, var2.getId(), var4);
            return true;
         } else {
            return false;
         }
      }
   }

   private AutoVillageTrade tick() {
      return WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(AutoVillageTrade.class) : null;
   }

   private boolean drawAnimation() {
      AutoVillageTrade var1 = this.tick();
      return var1 != null && var1.enabled;
   }

   private void handle(RoundedRectRenderer var1, LivingEntity var2, NameTags.PrimaryDataRecord var3, float var4, float var5, boolean var6, float var7) {
      float var8 = (float)MathHelper.clamp(6.0 / Math.max(var3.distance(), 1.0), 0.45, 1.0) * this.profileDraw.compute();
      float var9 = 6.0F * var8;
      String var10 = ProtectInfo.compute(var2.getName().getString());
      float var11 = var2.getHealth() + var2.getAbsorptionAmount();
      float var12 = var2.getMaxHealth();
      String var13 = " " + String.format("%.1f", var11).replace(',', '.');
      if (var13.endsWith(".0")) {
         var13 = var13.substring(0, var13.length() - 2);
      }

      float var14 = 22.0F * var8;
      float var15 = RoundedRectRenderer.handle(FontRegistry.instance, var10, var14).instance;
      float var16 = RoundedRectRenderer.handle(FontRegistry.instance, var13, var14).instance;
      float var17 = this.positionAdvance.compute() ? 14.0F * var8 : 0.0F;
      float var18 = this.positionAdvance.compute() ? 4.0F * var8 : 0.0F;
      float var19 = var17 + var18 + var15 + var16;
      float var20 = var19 + 16.0F * var8;
      float var21 = 17.0F * var8;
      float var22 = var3.screenX() - var20 / 2.0F;
      float var23 = var3.headY() - 18.0F * var8;
      boolean var24 = var6 && this.handle(var4, var5, var22, var23, var20, var21);
      String var25 = var2.getUuidAsString();
      float var26 = this.matrixFilter.getOrDefault(var25, 0.0F);
      float var27 = var24 && !this.targetWrite ? 1.0F : 0.0F;
      var26 += (var27 - var26) * 0.15F;
      this.matrixFilter.put(var25, var26);
      this.handle(var1, var22, var23, var20, var21, var9, var7, var26);
      float var28 = var3.screenX() - var19 / 2.0F;
      if (this.positionAdvance.compute()) {
         this.handle(var1, var2, var28, var23 + 1.5F * var8, var17, var7);
         var28 += var17 + var18;
      }

      this.handle(var1, FontRegistry.instance, var28, var23 + 12.2F * var8, var14, var10, this.handle(this.playerCollapse, var7));
      var28 += var15;
      var1.handle(FontRegistry.instance, var28, var23 + 12.2F * var8, var14, var13, this.handle(this.handle(var11, var12), var7));
   }

   private void handle(RoundedRectRenderer var1, String var2, float var3, float var4, float var5, float var6) {
      if (Module.client.getNetworkHandler() != null) {
         PlayerListEntry var7 = null;

         for (PlayerListEntry var9 : Module.client.getNetworkHandler().getPlayerList()) {
            if (var9.getProfile().getName().equalsIgnoreCase(var2)) {
               var7 = var9;
               break;
            }
         }

         if (var7 != null) {
            try {
               Identifier var13 = var7.getSkinTextures().texture();
               AbstractTexture var14 = Module.client.getTextureManager().getTexture(var13);
               if (var14 != null && var14.getGlTexture() instanceof GlTexture var10 && var10.getGlId() > 0) {
                  int var15 = var10.getGlId();
                  GlStateManager._bindTexture(var15);
                  var1.update(var6);
                  var1.handle(var15, var3, var4, var5, var5, 0.125F, 0.125F, 0.25F, 0.25F, 3.0F);
                  var1.handle(var15, var3, var4, var5, var5, 0.625F, 0.125F, 0.75F, 0.25F, 3.0F);
                  var1.onTick();
               }
            } catch (Throwable var12) {
            }
         }
      }
   }

   private void handle(RoundedRectRenderer var1, LivingEntity var2, float var3, float var4, float var5, float var6) {
      boolean var7 = false;
      if (var2 instanceof PlayerEntity var8) {
         if (var8 instanceof AbstractClientPlayerEntity var9) {
            try {
               Identifier var10 = var9.getSkinTextures().texture();
               AbstractTexture var11 = Module.client.getTextureManager().getTexture(var10);
               if (var11 != null && var11.getGlTexture() instanceof GlTexture var12 && var12.getGlId() > 0) {
                  int var38 = var12.getGlId();
                  GlStateManager._bindTexture(var38);
                  var1.update(var6);
                  var1.handle(var38, var3, var4, var5, var5, 0.125F, 0.125F, 0.25F, 0.25F, 3.0F);
                  var1.handle(var38, var3, var4, var5, var5, 0.625F, 0.125F, 0.75F, 0.25F, 3.0F);
                  var1.onTick();
                  var7 = true;
               }
            } catch (Throwable var19) {
            }
         }

         if (!var7 && Module.client.getNetworkHandler() != null) {
            PlayerListEntry var22 = null;

            for (PlayerListEntry var29 : Module.client.getNetworkHandler().getPlayerList()) {
               if (var29.getProfile().getId().equals(var8.getUuid()) || var29.getProfile().getName().equalsIgnoreCase(var8.getName().getString())) {
                  var22 = var29;
                  break;
               }
            }

            if (var22 != null) {
               try {
                  Identifier var26 = var22.getSkinTextures().texture();
                  AbstractTexture var30 = Module.client.getTextureManager().getTexture(var26);
                  if (var30 != null && var30.getGlTexture() instanceof GlTexture var34 && var34.getGlId() > 0) {
                     int var40 = var34.getGlId();
                     GlStateManager._bindTexture(var40);
                     var1.update(var6);
                     var1.handle(var40, var3, var4, var5, var5, 0.125F, 0.125F, 0.25F, 0.25F, 3.0F);
                     var1.handle(var40, var3, var4, var5, var5, 0.625F, 0.125F, 0.75F, 0.25F, 3.0F);
                     var1.onTick();
                     var7 = true;
                  }
               } catch (Throwable var18) {
               }
            }
         }
      } else {
         try {
            EntityRenderer var23 = Module.client.getEntityRenderDispatcher().getRenderer(var2);
            Identifier var27 = null;

            for (Method var14 : var23.getClass().getMethods()) {
               if (var14.getReturnType() == Identifier.class
                  && var14.getParameterCount() == 1
                  && var14.getParameterTypes()[0].isAssignableFrom(var2.getClass())) {
                  var14.setAccessible(true);
                  var27 = (Identifier)var14.invoke(var23, var2);
                  break;
               }
            }

            if (var27 != null) {
               AbstractTexture var32 = Module.client.getTextureManager().getTexture(var27);
               if (var32 != null && var32.getGlTexture() instanceof GlTexture var36 && var36.getGlId() > 0) {
                  int var43 = var36.getGlId();
                  GlStateManager._bindTexture(var43);
                  var1.update(var6);
                  float var45 = 0.125F;
                  float var15 = 0.125F;
                  float var16 = 0.25F;
                  float var17 = 0.25F;
                  if (var2 instanceof AnimalEntity) {
                     var45 = 0.0F;
                     var15 = 0.125F;
                     var16 = 0.125F;
                     var17 = 0.25F;
                  }

                  var1.handle(var43, var3, var4, var5, var5, var45, var15, var16, var17, 3.0F);
                  var1.onTick();
                  var7 = true;
               }
            }
         } catch (Throwable var20) {
         }
      }

      if (!var7) {
         int var21 = this.handle(PackedColor.compute(30, 30, 30, 120), var6);
         var1.handle(var3, var4, var5, var5, 4.0F, var21);
         String var24 = ProtectInfo.compute(var2.getName().getString());
         String var28 = var24.isEmpty() ? "?" : var24.substring(0, 1).toUpperCase();
         int var33 = this.handle(PackedColor.compute(200, 200, 200, 200), var6);
         float var37 = var5 * 0.65F;
         float var44 = RoundedRectRenderer.handle(FontRegistry.config, var28, var37).instance;
         var1.handle(FontRegistry.config, var3 + (var5 - var44) / 2.0F, var4 + var5 / 2.0F + var37 * 0.35F, var37, var28, var33);
      }
   }

   private void handle(RoundedRectRenderer var1, ItemEntity var2, NameTags.PrimaryDataRecord var3, ItemStack var4, float var5) {
      float var6 = (float)MathHelper.clamp(16.0 / Math.max(var3.distance(), 12.0), 0.75, 1.15) * this.profileDraw.compute();
      float var7 = 6.0F * var6;
      float var8 = 20.0F * var6;
      String var9 = this.handle(
         this.handle(var4, this.frameCheck.compute()),
         var8,
         Math.max(86.0F * var6, Math.min(190.0F * var6, Module.client.getWindow().getFramebufferWidth() * 0.3F))
      );
      float var10 = 22.0F * var6;
      float var11 = 4.0F * var6;
      float var12 = RoundedRectRenderer.handle(FontRegistry.data, var9, var8).instance;
      float var13 = 6.0F * var6;
      float var14 = var12 + var13 * 2.0F;
      float var15 = var10 + var11 + var14;
      float var16 = var3.screenX() - var15 / 2.0F;
      float var17 = var3.headY() - 12.0F * var6;
      this.handle(var1, var16, var17, var10, var10, var7, var5, var4);
      float var18 = (var10 - 16.0F * var6) / 2.0F;
      this.handle(Module.client.player, var4, var16 + var18, var17 + var18, var2.getId(), var6, 0);
      float var19 = var16 + var10 + var11;
      this.handle(var1, var19, var17, var14, var10, var7, var5, var4);
      this.handle(var1, FontRegistry.data, var19 + var13, var17 + 15.0F * var6, var8, var9, var4, var2.getId(), var5);
   }

   private void handle(RoundedRectRenderer var1, FontObject var2, float var3, float var4, float var5, String var6, int var7) {
      if (!this.presetSave.compute()) {
         var1.handle(var2, var3, var4, var5, var6, var7);
      } else {
         int var8 = RoundedRectRenderer.ColorState.select(this.presetWrite.prepare(), var7 >>> 24 & 0xFF);
         var1.handle(var2, var3, var4, var5, var6, var7, var8, this.encodePoint());
      }
   }

   private void handle(RoundedRectRenderer var1, FontObject var2, float var3, float var4, float var5, String var6, ItemStack var7, int var8, float var9) {
      if (!this.presetSave.compute()) {
         var1.handle(var2, var3, var4, var5, var6, this.process(var7, var9));
      } else if (!this.windowConvert.compute()) {
         this.handle(var1, var2, var3, var4, var5, var6, this.process(var7, var9));
      } else {
         int[] var10 = ItemStackOverlayRenderer.handle(var7, var8);
         if (var10 == null) {
            var10 = ItemStackOverlayRenderer.handle();
         }

         int var11 = Math.round(255.0F * MathHelper.clamp(var9, 0.0F, 1.0F));
         int var12 = RoundedRectRenderer.ColorState.compute(0, 0, 0, Math.round(185.0F * MathHelper.clamp(var9, 0.0F, 1.0F)));
         var1.handle(var2, var3 + Math.max(0.45F, var5 * 0.035F), var4 + Math.max(0.45F, var5 * 0.035F), var5, var6, var12);
         var1.handle(
            var2,
            var3,
            var4,
            var5,
            var6,
            RoundedRectRenderer.ColorState.select(var10[0], var11),
            RoundedRectRenderer.ColorState.select(var10[1], var11),
            this.encodePoint()
         );
      }
   }

   private float encodePoint() {
      float var1 = Math.max(600.0F, 2600.0F / Math.max(0.1F, this.colorMeasure.compute()));
      return (float)(System.currentTimeMillis() % (long)var1) / var1;
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!(var7 <= 0.05F)) {
         if (this.serverRead.compute().equals("Неоморфизм")) {
            float var9 = var7 * this.animationSchedule.compute();
            float var10 = 4.8F + var8 * 1.8F;
            float var11 = 16.0F + var8 * 4.0F;
            float var12 = 0.72F + var8 * 0.12F;
            if (ThemeShaderApplier.handle(null, var2, var3, var4, var5, var6, var10, var11, var12, 1, false, var9)) {
               return;
            }
         }

         if (this.serverRead.compute().equals("Блюр")) {
            var1.handle(23.0F);
            var1.handle(var2, var3, var4, var5, var6, var7 * this.animationSchedule.compute());
         }

         int var13 = this.handle(this.playerRun, this.matrixRender, var8);
         int var15 = this.handle(this.screenRead, this.animationExpand, var8);
         var15 = this.handle(var15, var7);
         var13 = this.handle(var13, var7);
         if (this.providerClose.compute()) {
            var1.handle(var2, var3, var4, var5, var6, this.animate() ? 7.0F : 5.0F, 1.0F, this.update(var7));
         }

         var1.handle(var2, var3, var4, var5, var6, var15);
         var1.handle(var2, var3, var4, var5, var6, var13, 1.0F);
      }
   }

   private boolean animate() {
      ThemePalette var1 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
      return "Светлый".equals(this.serverRead.compute()) || optionAdvance.compute(var1) || ThemeShaderApplier.resolve();
   }

   private int update(float var1) {
      if (!this.animate()) {
         return this.handle(RoundedRectRenderer.ColorState.compute(0, 0, 0, 120), var1);
      }

      ThemePalette var2 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
      ThemeColors var3 = ThemeColors.handle(var2, true);
      int var4 = PackedColor.compute(-10787208, var3.submit(), 0.1F);
      return PackedColor.handle(var4, (int)(48.0F * Math.max(0.0F, Math.min(1.0F, var1 * this.animationSchedule.compute()))));
   }

   private int handle(int var1, int var2, float var3) {
      int var4 = var1 >> 24 & 0xFF;
      int var5 = var1 >> 16 & 0xFF;
      int var6 = var1 >> 8 & 0xFF;
      int var7 = var1 & 0xFF;
      int var8 = var2 >> 24 & 0xFF;
      int var9 = var2 >> 16 & 0xFF;
      int var10 = var2 >> 8 & 0xFF;
      int var11 = var2 & 0xFF;
      int var12 = (int)(var4 + (var8 - var4) * var3);
      int var13 = (int)(var5 + (var9 - var5) * var3);
      int var14 = (int)(var6 + (var10 - var6) * var3);
      int var15 = (int)(var7 + (var11 - var7) * var3);
      return RoundedRectRenderer.ColorState.compute(var13, var14, var15, var12);
   }

   private void handle(PlayerEntity var1, ItemStack var2, float var3, float var4, int var5, float var6, int var7) {
      if (var2 != null && !var2.isEmpty()) {
         this.dataValidate.add(new NameTags.FallbackDataRecord(var1, var2.copy(), var3, var4, var5, var6, var7));
      }
   }

   private void handle(RoundedRectRenderer var1, DrawContext var2) {
      if (!this.dataValidate.isEmpty()) {
         this.dataValidate.sort(Comparator.comparingInt(NameTags.FallbackDataRecord::priority));

         for (NameTags.FallbackDataRecord var4 : this.dataValidate) {
            ItemStackOverlayRenderer.handle(var1, var4.stack(), var4.x(), var4.y(), var4.scale(), var4.seed(), false, var4.priority());
         }

         this.dataValidate.clear();
      }
   }

   private boolean handle(LivingEntity var1) {
      return var1 instanceof Monster || var1 instanceof SlimeEntity || var1 instanceof VillagerEntity;
   }

   private boolean process(LivingEntity var1) {
      return var1 instanceof AnimalEntity;
   }

   private int handle(ItemStack var1, float var2) {
      boolean var3 = var1 != null && !var1.isEmpty() && var1.contains(DataComponentTypes.CUSTOM_NAME);
      int var4 = this.process(var1, var2);
      int var5 = var3
         ? RoundedRectRenderer.ColorState.compute(
            RoundedRectRenderer.ColorState.render(var4), RoundedRectRenderer.ColorState.tick(var4), RoundedRectRenderer.ColorState.drawAnimation(var4), 210
         )
         : RoundedRectRenderer.ColorState.compute(142, 148, 158, 135);
      return this.handle(var5, var2);
   }

   private int process(ItemStack var1, float var2) {
      boolean var3 = this.animate();
      int var4 = var3 ? RoundedRectRenderer.ColorState.select(this.moduleTick, 255) : RoundedRectRenderer.ColorState.compute(220, 255, 245, 255);
      if (var1 != null && !var1.isEmpty()) {
         int[] var5 = new int[]{var4};

         try {
            Text var6 = var1.getName();
            var6.visit((var1x, var2x) -> {
               TextColor var3x = var1x.getColor();
               if (var3x != null && var2x != null && !var2x.isBlank()) {
                  var5[0] = RoundedRectRenderer.ColorState.compute(var3x.getRgb() >> 16 & 0xFF, var3x.getRgb() >> 8 & 0xFF, var3x.getRgb() & 0xFF, 255);
                  return Optional.of(Boolean.TRUE);
               } else {
                  return Optional.empty();
               }
            }, Style.EMPTY);
         } catch (Throwable var7) {
         }

         int var8 = var5[0];
         if (var3 && process(var8)) {
            var8 = RoundedRectRenderer.ColorState.select(this.moduleTick, 255);
         }

         return this.handle(var8, var2);
      } else {
         return this.handle(var4, var2);
      }
   }

   private static boolean process(int var0) {
      int var1 = var0 >> 16 & 0xFF;
      int var2 = var0 >> 8 & 0xFF;
      int var3 = var0 & 0xFF;
      return var1 * 0.299F + var2 * 0.587F + var3 * 0.114F >= 205.0F;
   }

   private String handle(ItemStack var1, boolean var2) {
      if (var1 != null && !var1.isEmpty()) {
         int var3 = Math.max(1, var1.getCount());
         String var4 = var1.getName().getString().replaceAll("§.", "").replaceAll("\\p{Cntrl}", "").replaceAll("\\s+", " ").trim();
         if (var4.isEmpty()) {
            var4 = "Предмет";
         }

         if (!var2 && var4.length() > 22) {
            var4 = var4.substring(0, 19).trim() + "...";
         }

         return var4 + (var3 > 1 ? " x" + var3 : "");
      } else {
         return "Пусто";
      }
   }

   private String handle(String var1, float var2, float var3) {
      if (var1 == null || var1.isEmpty()) {
         return "";
      }

      if (RoundedRectRenderer.handle(FontRegistry.data, var1, var2).instance <= var3) {
         return var1;
      }

      String var4 = "...";
      float var5 = RoundedRectRenderer.handle(FontRegistry.data, var4, var2).instance;
      int var6 = var1.length();

      while (var6 > 0 && RoundedRectRenderer.handle(FontRegistry.data, var1.substring(0, var6), var2).instance + var5 > var3) {
         var6--;
      }

      return var6 <= 0 ? var4 : var1.substring(0, var6).trim() + var4;
   }

   private int handle(int var1, float var2) {
      int var3 = var1 >> 24 & 0xFF;
      int var4 = var1 >> 16 & 0xFF;
      int var5 = var1 >> 8 & 0xFF;
      int var6 = var1 & 0xFF;
      return RoundedRectRenderer.ColorState.compute(var4, var5, var6, (int)(var3 * var2));
   }

   private boolean handle(float var1, float var2, float var3, float var4, float var5, float var6) {
      return var1 >= var3 && var1 <= var3 + var5 && var2 >= var4 && var2 <= var4 + var6;
   }

   record DataRecord(float x, float y, float w, float h, String playerName) {
   }

   record FallbackDataRecord(PlayerEntity player, ItemStack stack, float x, float y, int seed, float scale, int priority) {
   }

   record PrimaryDataRecord(float screenX, float headY, float feetY, float depth, double distance, float boxLeft, float boxRight) {
   }

   record Range(Vec3d start, Vec3d end) {
   }

   record SecondaryDataRecord(List<NameTags.Range> bones, long capturedAt) {
   }
}
