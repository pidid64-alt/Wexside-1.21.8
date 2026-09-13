package ru.wild.gui.hud;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.combat.AttackAura;
import ru.wild.modules.combat.TriggerBot;
import ru.wild.modules.visuals.Hud;
import ru.wild.modules.visuals.ProtectInfo;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "TargetHUD", process = "w")
public final class TargetHudRenderer extends ThemePresets {
   private static final TargetHudRenderer instance = new TargetHudRenderer();
   private static final Logger responseCompute = LogManager.getLogger("TargetHUD");
   private static final DoubleAnimator providerFetch = new DoubleAnimator();
   private static final DoubleAnimator profileDraw = new DoubleAnimator();
   private static final SpringAnimation vectorPerform = new SpringAnimation(0.0F);
   private static final SpringAnimation eventAttach = new SpringAnimation(0.0F);
   private static final SpringAnimation serverRead = new SpringAnimation(0.0F);
   private static final List<TargetHudRenderer.CacheEntry> positionAdvance = new ArrayList<>();
   private static final ItemStack[] frameCheck = new ItemStack[4];
   private static final Pattern moduleCollect = Pattern.compile("(?i)(?:\\u00A7|\\u0412\\u00A7).");
   private static final Pattern providerClose = Pattern.compile("\\d+(?:[\\.,]\\d+)?");
   private static final Pattern presetSave = Pattern.compile("[^A-Za-z\\u0410-\\u042F\\u0430-\\u044F\\u0401\\u04510-9\\s\\[\\]()_\\-.,!<>:|]");
   private final BooleanSetting windowConvert = new BooleanSetting("При наводке", false);
   private final BooleanSetting presetWrite = new BooleanSetting("Анимировать при ударе", true);
   private final BooleanSetting colorMeasure = new BooleanSetting("Золотые сердца", true);
   private final ModeSetting animationSchedule = new ModeSetting("Вид отображения", "Голова", "Голова", "От 3-лица");
   private final ModeSetting rendererScan = new ModeSetting("Позиция", "На экране", "На экране", "На цели");
   private final NumberSetting sourceBuild = new NumberSetting("Смещение X", 0.0F, -0.25F, 0.25F, 0.01F, false)
      .handle(() -> !this.rendererScan.process("На цели"));
   private static float outputCollapse;
   private static float profileInvoke;
   private static final float sourceSchedule = 0.58F;
   private static final float timerRender = 130.0F;
   private static float scaleSave;
   private static float colorCompute;
   private static float scaleAdapt;
   private static LivingEntity textureRun;
   private static int indexBind = Integer.MIN_VALUE;
   private static int actionRead = Integer.MIN_VALUE;
   private static int configCollapse = Integer.MIN_VALUE;
   private static int dataValidate;
   private static float scaleRender = Float.NaN;
   private static int clientRefresh = 1;
   private static final long keyFilter = 1000L;
   private static final Map<String, Long> requestAdapt = new HashMap<>();
   private static final Map<String, Long> timerMeasure = new HashMap<>();

   private TargetHudRenderer() {
      this.handle(this.windowConvert);
      this.handle(this.presetWrite);
      this.handle(this.colorMeasure);
      this.handle(this.animationSchedule);
      this.handle(this.rendererScan);
      this.handle(this.sourceBuild);
      HudProfileConfig.handle(this);
   }

   public static TargetHudRenderer process() {
      return instance;
   }

   public static float handle(LivingEntity var0) {
      if (var0 instanceof PlayerEntity var1) {
         Float var2 = process(var1);
         if (var2 != null) {
            return Math.max(0.0F, var2);
         }
      }

      float var3 = var0.getHealth() + compute(var0);
      return Math.max(0.0F, var3);
   }

   private static float process(LivingEntity var0) {
      if (var0 instanceof PlayerEntity var1) {
         Float var2 = process(var1);
         if (var2 != null) {
            return Math.max(0.0F, var2);
         }
      }

      return NumericTransform.onTick(var0.getHealth(), 0.0F, var0.getMaxHealth());
   }

   private static Float process(PlayerEntity var0) {
      if (MinecraftContext.toggleState.world != null) {
         Float var1 = handle(var0, MinecraftContext.toggleState.world.getScoreboard());
         if (var1 != null) {
            return var1;
         }
      }

      return handle(var0, var0.getScoreboard());
   }

   private static Float handle(PlayerEntity var0, Scoreboard var1) {
      if (var1 == null) {
         return null;
      }

      ScoreboardObjective var2 = var1.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
      if (var2 == null) {
         return null;
      }

      ReadableScoreboardScore var3 = var1.getScore(var0, var2);
      if (var3 == null) {
         return null;
      }

      MutableText var4 = ReadableScoreboardScore.getFormattedScore(var3, var2.getNumberFormatOr(StyledNumberFormat.EMPTY));
      Float var5 = resolve(var4.getString());
      return var5 != null ? var5 : (float)var3.getScore();
   }

   private static Float resolve(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = moduleCollect.matcher(var0).replaceAll("").replace(',', '.');
         Matcher var2 = providerClose.matcher(var1);
         if (!var2.find()) {
            return null;
         }

         try {
            return Float.parseFloat(var2.group());
         } catch (NumberFormatException var4) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static float compute(LivingEntity var0) {
      try {
         return Math.max(0.0F, var0.getAbsorptionAmount());
      } catch (Throwable var2) {
         return 0.0F;
      }
   }

   public static void handle(RoundedRectRenderer var0, DrawContext var1) {
      instance.process(var0, var1);
   }

   public void process(RoundedRectRenderer var1, DrawContext var2) {
      if (!(MinecraftContext.toggleState.currentScreen instanceof InventoryScreen)) {
         Object var3 = null;
         if (AttackAura.textureRun instanceof LivingEntity var4) {
            var3 = var4;
         }

         if (var3 == null) {
            LivingEntity var95 = TriggerBot.refresh();
            if (var95 != null) {
               var3 = var95;
            }
         }

         if (var3 == null && this.windowConvert.compute() && MinecraftContext.toggleState.targetedEntity instanceof LivingEntity var98 && var98.isAlive()) {
            var3 = var98;
         }

         if (var3 == null && MinecraftContext.toggleState.currentScreen instanceof ChatScreen && MinecraftContext.toggleState.player != null) {
            var3 = MinecraftContext.toggleState.player;
         }

         boolean var97 = var3 != null;
         if (var97) {
            textureRun = (LivingEntity)var3;
         }

         providerFetch.handle();
         providerFetch.handle(var97 ? 1.0 : 0.0, 0.22F, Easings.handler, true);
         float var99 = providerFetch.update();
         LivingEntity var6 = var97 ? (LivingEntity)var3 : textureRun;
         if (!(var99 <= 0.01F) && var6 != null) {
            boolean var7 = this.colorMeasure.compute();
            Float var8 = var6 instanceof PlayerEntity var9 ? process(var9) : null;
            float var100 = var8 != null ? Math.max(0.0F, var8) : (var7 ? process((LivingEntity)var6) : handle((LivingEntity)var6));
            float var10 = var7 && var8 == null ? compute((LivingEntity)var6) : 0.0F;
            float var11 = var7 ? var100 + var10 : var100;
            float var12 = Math.max(1.0F, Math.max(var6.getMaxHealth(), var100));
            float var13 = Math.min(1.0F, var100 / var12);
            float var14 = Math.min(1.0F, var10 / var12);
            float var15 = prepare((LivingEntity)var6);
            if (var8 != null) {
               serverRead.handle(0.0F);
            }

            if (indexBind != var6.getId()) {
               indexBind = var6.getId();
               vectorPerform.handle(var13);
               eventAttach.handle(var15);
               serverRead.handle(var14);
            }

            scaleSave = NumericTransform.onTick(vectorPerform.handle(var13, SpringAnimationSpec.check()), 0.0F, 1.0F);
            colorCompute = NumericTransform.onTick(eventAttach.handle(var15, SpringAnimationSpec.check()), 0.0F, 1.0F);
            scaleAdapt = NumericTransform.onTick(serverRead.handle(var14, SpringAnimationSpec.check()), 0.0F, 1.0F);
            boolean var16 = handle((LivingEntity)var6, var11);
            profileDraw.handle();
            if (var16 && this.presetWrite.compute()) {
               clientRefresh = (System.nanoTime() & 1L) == 0L ? 1 : -1;
               profileDraw.apply(1.0);
            }

            profileDraw.handle(0.0, 0.34F, Easings.handler, false);
            float var17 = this.presetWrite.compute() ? NumericTransform.onTick(profileDraw.update(), 0.0F, 1.0F) : 0.0F;
            String var18 = "";
            float var19 = var99 * this.target.compute();
            boolean var20 = this.select();
            String var21 = var6.getName().getString();
            if (var6 instanceof PlayerEntity var22) {
               var18 = handle(var22);
            }

            var21 = ProtectInfo.compute(var21);
            if (!var18.isEmpty()) {
               var18 = var18 + " ";
            }

            var21 = update(var21);
            String var103 = refresh(var11);
            String var23 = " hp";
            float var24 = 252.204F;
            float var25 = 85.472F;
            float var26 = MinecraftContext.toggleState.getWindow().getFramebufferHeight();
            float var27 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
            boolean var28 = this.rendererScan.process("На цели");
            ThemeRenderer.PrimaryCacheEntry var34 = null;
            TargetHudRenderer.DataRecord var35 = null;
            float var29;
            float var30;
            float var31;
            float var32;
            float var33;
            if (var28) {
               float var36 = MinecraftContext.toggleState.getRenderTickCounter().getTickProgress(true);
               var35 = this.handle((LivingEntity)var6, var36, (int)var27, (int)var26);
               if (var35 == null) {
                  return;
               }

               float var37 = this.encodePoint();
               var33 = var37;
               var31 = var24 * var33;
               var32 = var25 * var33;
               var29 = var35.x - var31 * 0.5F;
               var30 = var35.y - var32 * 0.5F;
               if (MinecraftContext.toggleState.currentScreen instanceof ChatScreen) {
                  var34 = ThemeRenderer.handle().process("HUD_TargetHUD", var29, var30, var24, var25);
                  var33 = Math.min(var34.config / var24, var34.state / var25);
                  var31 = var24 * var33;
                  var32 = var25 * var33;
                  var29 = var35.x - var31 * 0.5F;
                  var30 = var35.y - var32 * 0.5F;
               }
            } else {
               var34 = ThemeRenderer.handle().handle("HUD_TargetHUD", 10.0F, Math.max(10.0F, var26 - var25 - 10.0F), var24, var25);
               float var104 = var34.data;
               float var106 = var34.context;
               float var38 = var34.config;
               float var39 = var34.state;
               var33 = Math.min(var38 / var24, var39 / var25);
               var31 = var24 * var33;
               var32 = var25 * var33;
               var29 = var104 + (var38 - var31) / 2.0F;
               var30 = var106 + (var39 - var32) / 2.0F;
            }

            this.handle(var29, var30, var31, var32);
            int var105 = (int)(255.0F * var19);
            int var107 = this.update(var19);
            int var108 = var107;
            if (var6 instanceof PlayerEntity var109) {
               var108 = handle(var109, var107, var105);
            }

            float var110 = 14.0F * var33;
            int var40 = this.resolve(var19);
            int var41 = this.process(var19);
            int var42 = var40;
            float var43 = NumericTransform.onTick((scaleSave - 0.16F) / 0.84F, 0.0F, 1.0F);
            int var44 = handle(PackedColor.compute(255, 84, 96, var105), PackedColor.compute(128, 255, 171, var105), var43);
            int var45 = handle(PackedColor.compute(210, 35, 52, var105), PackedColor.compute(34, 213, 122, var105), var43);
            int var46 = PackedColor.compute(192, 220, 255, var105);
            int var47 = PackedColor.compute(86, 132, 202, var105);
            int var48 = this.apply(var19);
            float var49 = var29 + 7.0F * var33;
            float var50 = var30 + 6.834F * var33;
            float var51 = 71.799F * var33;
            float var52 = 71.803F * var33;
            float var53 = 54.367F * var33;
            float var54 = var29 + 15.716F * var33;
            float var55 = var30 + 15.552F * var33;
            float var56 = var28 ? var19 : var19 * NumericTransform.onTick((var99 - 0.42F) / 0.58F, 0.0F, 1.0F);
            boolean var57 = this.animationSchedule.config.size() > 1 && this.animationSchedule.compute().equalsIgnoreCase(this.animationSchedule.config.get(1));
            float var58 = var29 + 84.185F * var33;
            float var59 = 161.02F * var33;
            float var60 = var30 + 6.834F * var33;
            float var61 = 31.592F * var33;
            if (var20) {
               ThemeShaderApplier.handle();
            }

            try {
               this.handle(var1, var29, var30, var31, var32, var110, var19);
               this.process(var1, var49, var50, var51, var52, 10.0F * var33, var19);
               this.process(var1, var58, var60, var59, var61, 9.0F * var33, var19);
               if (var20) {
                  ThemeShaderApplier.process();
               }

               float var62 = 30.0F * var33;
               float var63 = 20.0F * var33;
               float var64 = var29 + 94.33F * var33;
               float var65 = var60 + var61 / 2.0F + 6.6F * var33;
               float var66 = var58 + var59 - 10.0F * var33;
               if (!var18.isEmpty()) {
                  int var67 = var108 == var107 ? PackedColor.compute(255, 50, 50, var105) : var108;
                  String var68 = var18.trim().toUpperCase(Locale.ROOT);
                  float var69 = TextMeasureCache.handle(FontRegistry.config, var68, var63).instance;
                  String var70 = handle(var1, var21, var62, Math.max(20.0F * var33, var66 - var64 - var69 - 5.0F * var33));
                  var1.handle(FontRegistry.config, var64, var65, var62, var70, var107);
                  var1.handle(FontRegistry.config, var66 - var69, var65 - 0.5F * var33, var63, var68, var67);
               } else {
                  String var116 = handle(var1, var21, var62, Math.max(20.0F * var33, var66 - var64));
                  var1.handle(FontRegistry.config, var64, var65, var62, var116, var107);
               }
            } finally {
               if (var20) {
                  ThemeShaderApplier.compute();
               }
            }

            float var111 = var30 + 43.426F * var33;
            float var112 = 35.211F * var33;
            this.process(var1, var58, var111, var59, var112, 9.0F * var33, var19);
            float var113 = 16.01F * var33;
            float var114 = var28 ? var19 : var19 * NumericTransform.onTick((var99 - 0.42F) / 0.58F, 0.0F, 1.0F);
            handle(var1, var2, this, var29 + 90.04F * var33, var30 + 48.53F * var33, (LivingEntity)var6, var19, var114, var33, var41, var42, var113, var20);
            float var115 = 24.0F * var33;
            float var117 = TextMeasureCache.handle(FontRegistry.instance, var103, var115).instance;
            float var118 = TextMeasureCache.handle(FontRegistry.instance, var23, var115).instance;
            float var119 = var58 + var59 - var117 - var118 - 9.2F * var33;
            float var120 = var111 + 19.1F * var33;
            var1.handle(FontRegistry.instance, var119, var120, var115, var103, var107);
            var1.handle(FontRegistry.instance, var119 + var117, var120, var115, var23, var48);
            float var71 = var29 + 90.339F * var33;
            float var72 = var29 + 90.339F * var33;
            float var73 = var30 + 65.28F * var33;
            float var74 = 76.0F * var33;
            float var75 = 3.72F * var33;
            float var76 = var75 * 0.5F;
            this.process(var1, var72, var73, var74, var75, var76, var19);
            float var77 = Math.min(var75 * 0.32F, Math.max(0.72F * var33, 0.45F));
            float var78 = Math.max(0.0F, (var74 - var77 * 2.0F) * colorCompute);
            if (var78 > 0.35F) {
               float var79 = Math.max(1.0F, var75 - var77 * 2.0F);
               var1.handle(var72 + var77, var73 + var77, Math.max(1.0F, var74 - var77 * 2.0F), var79, var79 * 0.5F, var79 * 0.5F, var79 * 0.5F, var79 * 0.5F);
               var1.handle(var72 + var77, var73 + var77, var78, var79, var79 * 0.5F, var47, var46);
               var1.apply();
            }

            float var121 = var30 + 70.12F * var33;
            float var80 = 146.92F * var33;
            float var81 = 6.72F * var33;
            float var82 = var81 * 0.5F;
            this.process(var1, var71, var121, var80, var81, var82, var19);
            float var83 = Math.max(1.15F * var33, 0.85F);
            float var84 = var71 + var83;
            float var85 = var121 + var83;
            float var86 = Math.max(1.0F, var81 - var83 * 2.0F);
            float var87 = Math.max(0.0F, (var80 - var83 * 2.0F) * scaleSave);
            float var88 = var86 * 0.5F;
            if (var87 > 0.5F) {
               float var89 = NumericTransform.onTick(Math.abs(vectorPerform.compute()) * 0.018F, 0.0F, 0.075F);
               float var90 = Math.min(var80 - var83 * 2.0F, var87 + (var80 - var83 * 2.0F) * var89);
               var1.handle(var84, var85, Math.max(1.0F, var80 - var83 * 2.0F), var86, var88, var88, var88, var88);
               var1.process(var84, var85, var90, var86, var88, var44, var45);
               var1.handle(
                  var84 + var88 * 0.5F,
                  var85 + var86 * 0.18F,
                  Math.max(0.0F, var90 - var88),
                  Math.max(1.0F, var86 * 0.22F),
                  var86 * 0.11F,
                  PackedColor.compute(255, 255, 255, (int)(58.0F * var19))
               );
               var1.apply();
            }

            if (var7 && scaleAdapt > 0.001F) {
               int var122 = PackedColor.compute(255, 224, 92, (int)(245.0F * var19));
               int var124 = PackedColor.compute(232, 154, 35, (int)(245.0F * var19));
               float var91 = Math.max(1.0F, var80 - var83 * 2.0F);
               float var92 = var91 * NumericTransform.onTick(scaleAdapt, 0.0F, 1.0F);
               if (var92 > 0.5F) {
                  var1.handle(var84, var85, var91, var86, var88, var88, var88, var88);
                  var1.process(var84, var85, var92, var86, var88, var122, var124);
                  var1.apply();
               }
            }

            if (var56 > 0.01F) {
               handle(var1, var2, (LivingEntity)var6, var54, var55, var53, var56, var17, var57);
               compute(var1, var29, var30, var33, var56);
            }

            if (var34 != null) {
               Hud.handle("HUD_TargetHUD", var29, var30, var31, var32);
               ThemeRenderer var123 = ThemeRenderer.handle();
               var123.process(var34, var29, var30, var31, var32);
               if (var28) {
                  NeoStyleOptions.handle(
                     var1,
                     this,
                     var29,
                     var30,
                     var31,
                     var32,
                     MinecraftContext.toggleState.getWindow().getScaledWidth(),
                     MinecraftContext.toggleState.getWindow().getScaledHeight(),
                     var34.output,
                     var123.execute(),
                     var123.prepare(),
                     var123.onTick(),
                     var123.check()
                  );
               } else {
                  NeoStyleOptions.handle(
                     var1,
                     this,
                     var34,
                     var123,
                     MinecraftContext.toggleState.getWindow().getScaledWidth(),
                     MinecraftContext.toggleState.getWindow().getScaledHeight()
                  );
               }
            } else if (var35 != null) {
               Hud.handle("HUD_TargetHUD", var29, var30, var31, var32);
            }
         } else {
            scaleSave = 0.0F;
            colorCompute = 0.0F;
            scaleAdapt = 0.0F;
            vectorPerform.handle(0.0F);
            eventAttach.handle(0.0F);
            serverRead.handle(0.0F);
            indexBind = Integer.MIN_VALUE;
            actionRead = Integer.MIN_VALUE;
            if (!var97) {
               textureRun = null;
            }
         }
      }
   }

   private float encodePoint() {
      ThemeRenderer.DataRecord var1 = ThemeRenderer.handle().update().get("HUD_TargetHUD");
      return var1 == null ? 1.0F : NumericTransform.onTick(Math.min(var1.scaleX(), var1.scaleY()), 0.72F, 1.48F);
   }

   private TargetHudRenderer.DataRecord handle(LivingEntity var1, float var2, int var3, int var4) {
      if (var1 != null
         && !var1.isRemoved()
         && var3 > 1
         && var4 > 1
         && MinecraftContext.toggleState.gameRenderer != null
         && MinecraftContext.toggleState.gameRenderer.getCamera() != null) {
         Vec3d var5 = var1.getLerpedPos(var2);
         double var6 = Math.max(0.65, var1.getHeight());
         Vec3d var8 = new Vec3d(var5.x, var5.y + var6 * 0.5, var5.z);
         Vec3d var9 = ClientMathUtil.handle(var8);
         if (var9 != null && !(var9.z <= 0.001) && !(var9.z > 1.0)) {
            float var10 = (float)var9.x + 130.0F * this.sourceBuild.compute();
            float var11 = (float)var9.y;
            if (var1.getId() != actionRead) {
               actionRead = var1.getId();
               outputCollapse = var10;
               profileInvoke = var11;
            } else {
               outputCollapse = outputCollapse + (var10 - outputCollapse) * 0.58F;
               profileInvoke = profileInvoke + (var11 - profileInvoke) * 0.58F;
            }

            return new TargetHudRenderer.DataRecord(outputCollapse, profileInvoke);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
   private static void handle(
      RoundedRectRenderer var0, DrawContext var1, LivingEntity var2, float var3, float var4, float var5, float var6, float var7, boolean var8
   ) {
      float var9 = ItemStackOverlayRenderer.handle(var3);
      float var10 = ItemStackOverlayRenderer.handle(var4);
      float var11 = ItemStackOverlayRenderer.process(var5);
      float var12 = Math.max(2.0F, Math.round(var11 * 0.11F));
      if (!var8 || !handle(var0, var1, var2, var9, var10, var11, var12, var6, var7, true)) {
         if (var2 instanceof PlayerEntity var13 && MinecraftContext.toggleState.getNetworkHandler() != null) {
            PlayerListEntry var14 = MinecraftContext.toggleState.getNetworkHandler().getPlayerListEntry(var13.getUuid());
            if (var14 != null) {
               try {
                  Identifier var15 = var14.getSkinTextures().texture();
                  AbstractTexture var16 = MinecraftContext.toggleState.getTextureManager().getTexture(var15);
                  if (var16 != null && var16.getGlTexture() instanceof GlTexture var17 && var17.getGlId() > 0) {
                     ItemStackOverlayRenderer.handle(var15);
                     int var37 = var17.getGlId();
                     GlStateManager._bindTexture(var37);
                     handle(var0, var9, var10, var11, var7);
                     boolean var24 = false /* VF: Semaphore variable */;

                     try {
                        var24 = true;
                        var0.update(var6);
                        boolean var28 = false /* VF: Semaphore variable */;

                        try {
                           var28 = true;
                           var0.handle(var37, -var11 * 0.5F, -var11 * 0.5F, var11, var11, 0.125F, 0.125F, 0.25F, 0.25F, var12);
                           var0.handle(var37, -var11 * 0.5F, -var11 * 0.5F, var11, var11, 0.625F, 0.125F, 0.75F, 0.25F, var12);
                           var28 = false;
                        } finally {
                           if (var28) {
                              var0.onTick();
                           }
                        }

                        var0.onTick();
                        process(var0, var11, var12, var6, var7);
                        var24 = false;
                     } finally {
                        if (var24) {
                           handle(var0);
                        }
                     }

                     handle(var0);
                     return;
                  }
               } catch (Throwable var31) {
               }
            }
         }

         int var32 = PackedColor.compute(30, 30, 30, (int)(120.0F * var6));
         var0.handle(var9, var10, var11, var11, var12, var32);
         int var33 = PackedColor.compute(200, 200, 200, (int)(200.0F * var6));
         float var34 = var11 * 1.3F;
         String var35 = "a";
         float var36 = TextMeasureCache.handle(FontRegistry.context, var35, var34).instance;
         var0.handle(FontRegistry.context, var9 + (var11 - var36) / 2.0F, var10 + var11 / 2.0F + var34 * 0.25F, var34, var35, var33);
      }
   }

   private static boolean handle(
      RoundedRectRenderer var0, DrawContext var1, LivingEntity var2, float var3, float var4, float var5, float var6, float var7, float var8, boolean var9
   ) {
      if (var2 != null && MinecraftContext.toggleState != null && var1 != null && !(MinecraftContext.toggleState.currentScreen instanceof InventoryScreen)) {
         float var10 = MinecraftContext.toggleState.getWindow().getScaleFactor();
         if (var10 <= 0.0F) {
            return false;
         }

         float var11 = NumericTransform.onTick(var8, 0.0F, 1.0F);
         float var12 = 1.0F - var11 * 0.085F;
         float var13 = var5 * var12;
         float var14 = var3 + (var5 - var13) * 0.5F;
         float var15 = var4 + (var5 - var13) * 0.5F;
         int var16 = Math.round(var14 / var10);
         int var17 = Math.round(var15 / var10);
         int var18 = Math.max(1, Math.round(var13 / var10));
         float var19 = Math.max(0.65F, var2.getHeight());
         float var20 = NumericTransform.onTick(1.8F / var19, 0.72F, 1.65F);
         int var21 = Math.max(8, Math.round(var18 * (var9 ? 1.02F : 1.15F) * var20));
         int var22 = Math.max(var18 + 1, Math.round(var18 * (var9 ? 2.24F : 2.05F)));
         int var23 = var17 - Math.round(var18 * (var9 ? 0.12F : 0.0F));
         int var24 = var23 + var22;
         float var25 = var16 + var18 * 0.5F;
         float var26 = (var23 + var24) * 0.5F;
         float var27 = (var9 ? 24.0F : 8.0F) + clientRefresh * var11 * 50.0F;
         float var28 = var9 ? -7.0F : -4.0F;
         float var29 = var25 - (float)Math.tan(var27 / 20.0F) * 5.0F;
         float var30 = var26 - (float)Math.tan(-var28 / 20.0F);
         var0.compute();

         try {
            var1.enableScissor(var16, var17, var16 + var18, var17 + var18);
            handle(var1, var16, var23, var16 + var18, var24, var21, 0.0625F, var29, var30, var2);
         } catch (Throwable var42) {
            return false;
         } finally {
            try {
               var1.disableScissor();
            } catch (Throwable var41) {
            }
         }

         if (var11 > 0.001F) {
            var0.handle(var14, var15, var13, var13, var6, PackedColor.compute(255, 55, 55, (int)(58.0F * var7 * var11)));
         }

         return true;
      } else {
         return false;
      }
   }

   private static void handle(DrawContext var0, int var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, LivingEntity var9) {
      float var10 = (var1 + var3) * 0.5F;
      float var11 = (var2 + var4) * 0.5F;
      float var12 = (float)Math.atan((var10 - var7) / 40.0F);
      float var13 = (float)Math.atan((var11 - var8) / 40.0F);
      Quaternionf var14 = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf var15 = new Quaternionf().rotateX(var13 * 20.0F * (float) (Math.PI / 180.0));
      var14.mul(var15);
      EntityRenderer var16 = MinecraftContext.toggleState.getEntityRenderDispatcher().getRenderer(var9);
      EntityRenderState var17 = var16.getAndUpdateRenderState(var9, 1.0F);
      var17.hitbox = null;
      if (var17 instanceof LivingEntityRenderState var18) {
         float var19 = 180.0F + var12 * 20.0F;
         var18.bodyYaw = var19;
         var18.relativeHeadYaw = 180.0F + var12 * 40.0F - var19;
         var18.pitch = -var13 * 20.0F;
      }

      float var20 = Math.max(0.001F, var9.getScale());
      Vector3f var21 = new Vector3f(0.0F, var9.getHeight() / 2.0F + var6 * var20, 0.0F);
      var0.addEntity(var17, var5 / var20, var21, var14, var15, var1, var2, var3, var4);
   }

   private static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4) {
      float var5 = NumericTransform.onTick(var4, 0.0F, 1.0F);
      float var6 = var1 + var3 * 0.5F;
      float var7 = var2 + var3 * 0.5F;
      float var8 = 1.0F - var5 * 0.085F;
      float var9 = clientRefresh * var5 * 8.5F;
      var0.handle(var6, var7);
      var0.process(var9);
      var0.process(var8, var8);
   }

   private static void handle(RoundedRectRenderer var0) {
      var0.check();
      var0.execute();
      var0.prepare();
   }

   private static void process(RoundedRectRenderer var0, float var1, float var2, float var3, float var4) {
      float var5 = NumericTransform.onTick(var4, 0.0F, 1.0F);
      if (!(var5 <= 0.001F)) {
         var0.handle(-var1 * 0.5F, -var1 * 0.5F, var1, var1, var2, PackedColor.compute(255, 55, 55, (int)(58.0F * var3 * var5)));
      }
   }

   private static void handle(LivingEntity var0, boolean var1, String var2, String var3) {
      long var4 = System.currentTimeMillis();
      String var6 = var2 + "|" + resolve(var0) + "|" + var1;
      Long var7 = requestAdapt.get(var6);
      if (var7 == null || var4 - var7 >= 1000L) {
         requestAdapt.put(var6, var4);
      }
   }

   private static void handle(LivingEntity var0, boolean var1, String var2, String var3, Throwable var4) {
      long var5 = System.currentTimeMillis();
      String var7 = var4 == null ? "none" : var4.getClass().getName();
      String var8 = var2 + "|" + resolve(var0) + "|" + var1 + "|" + var7;
      Long var9 = timerMeasure.get(var8);
      if (var9 == null || var5 - var9 >= 1000L) {
         timerMeasure.put(var8, var5);
         responseCompute.warn(
            "[portrait] stage={} target={} id={} type={} class={} thirdPerson={} {}",
            var2,
            update(var0),
            resolve(var0),
            apply(var0),
            execute(var0),
            var1,
            var3,
            var4
         );
      }
   }

   private static int resolve(LivingEntity var0) {
      return var0 == null ? Integer.MIN_VALUE : var0.getId();
   }

   private static String update(LivingEntity var0) {
      if (var0 == null) {
         return "null";
      }

      try {
         return var0.getName().getString();
      } catch (Throwable var2) {
         return "name-error";
      }
   }

   private static String apply(LivingEntity var0) {
      if (var0 == null) {
         return "null";
      }

      try {
         return String.valueOf(var0.getType());
      } catch (Throwable var2) {
         return "type-error";
      }
   }

   private static String execute(LivingEntity var0) {
      return var0 == null ? "null" : var0.getClass().getName();
   }

   private static boolean handle(LivingEntity var0, float var1) {
      int var2 = var0.getId();
      if (var2 != configCollapse) {
         configCollapse = var2;
         dataValidate = 0;
         scaleRender = var1;
         positionAdvance.clear();
         profileDraw.apply(0.0);
         return false;
      }

      boolean var3 = var0.hurtTime > 0 && (dataValidate == 0 || var0.hurtTime > dataValidate);
      boolean var4 = !Float.isNaN(scaleRender) && var1 < scaleRender - 0.05F;
      if (var3 || var4) {
         animate();
      }

      dataValidate = var0.hurtTime;
      scaleRender = var1;
      return var3 || var4;
   }

   private static void animate() {
      byte var0 = 24;
      float var1 = (float)(System.nanoTime() & 7L) * 0.06F;

      for (int var2 = 0; var2 < var0; var2++) {
         float var3 = (float)((Math.PI * 2) * var2 / var0) + var1;
         float var4 = 1.35F + var2 % 5 * 0.15F;
         float var5 = 43.0F + (var2 % 3 - 1) * 3.1F;
         float var6 = 42.8F + (var2 % 2 == 0 ? -2.8F : 2.8F);
         float var7 = (float)Math.cos(var3) * var4;
         float var8 = (float)Math.sin(var3) * var4 - 0.08F;
         float var9 = 1.32F + var2 % 3 * 0.32F;
         int var10 = 56 + var2 % 10;
         positionAdvance.add(new TargetHudRenderer.CacheEntry(var5, var6, var7, var8, var9, var10));
      }
   }

   private static void compute(RoundedRectRenderer var0, float var1, float var2, float var3, float var4) {
      for (int var5 = positionAdvance.size() - 1; var5 >= 0; var5--) {
         TargetHudRenderer.CacheEntry var6 = positionAdvance.get(var5);
         var6.output++;
         if (var6.output >= var6.cache) {
            positionAdvance.remove(var5);
         } else {
            var6.instance = var6.instance + var6.context;
            var6.data = var6.data + var6.config;
            var6.context *= 0.988F;
            var6.config = var6.config * 0.988F + 0.012F;
            float var7 = (float)var6.output / var6.cache;
            float var8 = 1.0F - (1.0F - var7) * (1.0F - var7);
            float var9 = Math.max(0.0F, 1.0F - var7) * var4;
            float var10 = var1 + var6.instance * var3;
            float var11 = var2 + var6.data * var3;
            float var12 = var6.state * var3 * (1.0F + var8 * 0.28F);
            var0.process(var10, var11, var12 * 4.2F, 0.0F, 1.0F, PackedColor.compute(146, 170, 255, (int)(32.0F * var9)));
            var0.process(var10, var11, var12, 0.0F, 1.0F, PackedColor.compute(146, 170, 255, (int)(235.0F * var9)));
         }
      }
   }

   private static String refresh(float var0) {
      int var1 = Math.max(0, Math.round(var0 * 10.0F));
      return var1 / 10 + "." + var1 % 10;
   }

   private static float prepare(LivingEntity var0) {
      if (var0 == null) {
         return 0.0F;
      }

      float var1 = 0.0F;
      EquipmentSlot[] var2 = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

      for (EquipmentSlot var6 : var2) {
         ItemStack var7 = var0.getEquippedStack(var6);
         if (var7 != null && !var7.isEmpty()) {
            if (var7.isDamageable() && var7.getMaxDamage() > 0) {
               var1 += NumericTransform.onTick(1.0F - (float)var7.getDamage() / var7.getMaxDamage(), 0.0F, 1.0F);
            } else {
               var1++;
            }
         }
      }

      return NumericTransform.onTick(var1 / var2.length, 0.0F, 1.0F);
   }

   private static int handle(int var0, int var1, float var2) {
      float var3 = NumericTransform.onTick(var2, 0.0F, 1.0F);
      int var4 = var0 >>> 24 & 0xFF;
      int var5 = var0 >>> 16 & 0xFF;
      int var6 = var0 >>> 8 & 0xFF;
      int var7 = var0 & 0xFF;
      int var8 = var1 >>> 24 & 0xFF;
      int var9 = var1 >>> 16 & 0xFF;
      int var10 = var1 >>> 8 & 0xFF;
      int var11 = var1 & 0xFF;
      int var12 = Math.round(var4 + (var8 - var4) * var3);
      int var13 = Math.round(var5 + (var9 - var5) * var3);
      int var14 = Math.round(var6 + (var10 - var6) * var3);
      int var15 = Math.round(var7 + (var11 - var7) * var3);
      return (var12 & 0xFF) << 24 | (var13 & 0xFF) << 16 | (var14 & 0xFF) << 8 | var15 & 0xFF;
   }

   private static String update(String var0) {
      return var0 != null && !var0.isEmpty() ? presetSave.matcher(moduleCollect.matcher(var0).replaceAll("")).replaceAll("").trim() : "";
   }

   public static String handle(PlayerEntity var0) {
      return var0 != null && var0.getScoreboardTeam() != null ? update(ProtectInfo.compute(var0.getScoreboardTeam().getPrefix().getString())) : "";
   }

   public static int handle(PlayerEntity var0, int var1, int var2) {
      if (var0 != null && var0.getScoreboardTeam() != null) {
         Formatting var3 = var0.getScoreboardTeam().getColor();
         return var3 != null && var3.getColorValue() != null ? PackedColor.update(var3.getColorValue(), var2) : var1;
      } else {
         return var1;
      }
   }

   private static String handle(RoundedRectRenderer var0, String var1, float var2, float var3) {
      if (var1 != null && !var1.isEmpty() && !(TextMeasureCache.handle(FontRegistry.instance, var1, var2).instance <= var3)) {
         String var4 = "...";

         for (int var5 = var1.length(); var5 > 0; var5--) {
            String var6 = var1.substring(0, var5).trim() + var4;
            if (TextMeasureCache.handle(FontRegistry.instance, var6, var2).instance <= var3) {
               return var6;
            }
         }

         return var4;
      } else {
         return var1 == null ? "" : var1;
      }
   }

   private static void handle(
      RoundedRectRenderer var0,
      DrawContext var1,
      ThemePresets var2,
      float var3,
      float var4,
      LivingEntity var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      float var11,
      boolean var12
   ) {
      if (var5 != null) {
         frameCheck[0] = var5.getEquippedStack(EquipmentSlot.HEAD);
         frameCheck[1] = var5.getEquippedStack(EquipmentSlot.CHEST);
         frameCheck[2] = var5.getEquippedStack(EquipmentSlot.LEGS);
         frameCheck[3] = var5.getEquippedStack(EquipmentSlot.FEET);
      } else {
         frameCheck[0] = null;
         frameCheck[1] = null;
         frameCheck[2] = null;
         frameCheck[3] = null;
      }

      float var13 = 3.99F * var8;

      for (int var14 = 0; var14 < 4; var14++) {
         float var15 = var3 + var14 * (var11 + var13);
         if (var2 == null || !var2.refresh() && !var2.render()) {
            if (!var12
               || !ThemeShaderApplier.handle(
                  null, var15, var4, var11, var11, 4.0F * var8, Math.max(1.6F, 2.8F * var8), Math.max(3.0F, 5.5F * var8), 0.82F, 2, true, var6
               )) {
               var0.handle(var15, var4, var11, var11, 4.0F * var8, var9);
               var0.handle(var15, var4, var11, var11, 4.0F * var8, var10, 1.0F * var8);
            }
         } else {
            var2.process(var0, var15, var4, var11, var11, 4.0F * var8, var6);
         }
      }

      ThemeShaderApplier.process();
      var0.compute();
      if (!(var7 <= 0.01F)) {
         var0.update(var7);

         for (int var21 = 0; var21 < 4; var21++) {
            float var22 = var3 + var21 * (var11 + var13);
            ItemStack var16 = frameCheck[var21];
            if (var16 != null && !var16.isEmpty()) {
               float var17 = var11 / 16.0F * 0.72F;
               float var18 = 16.0F * var17;
               float var19 = var22 + (var11 - var18) / 2.0F;
               float var20 = var4 + (var11 - var18) / 2.0F;
               ItemStackOverlayRenderer.handle(var0, var16, var19, var20, var17, var21, false, 0);
            }
         }

         var0.onTick();
      }
   }

   static final class CacheEntry {
      float instance;
      float data;
      float context;
      float config;
      final float state;
      final int cache;
      int output;

      CacheEntry(float var1, float var2, float var3, float var4, float var5, int var6) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
      }
   }

   record DataRecord(float x, float y) {
   }
}
