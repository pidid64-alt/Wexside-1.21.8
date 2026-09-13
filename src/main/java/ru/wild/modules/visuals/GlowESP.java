package ru.wild.modules.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Predicate;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.core.GlowEspRenderer;
import ru.wild.core.manager.FriendManager;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.FramebufferCapture;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.render.IrisCompatibility;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "GlowESP", description = "Шейдерная градиентная обводка игроков", category = ModuleCategory.Visuals)
public final class GlowESP extends Module {
   private static final String serverRead = "glow_esp";
   private static final String positionAdvance = "glow_esp_friends";
   private static final GlowEspRenderer.Bounds frameCheck = new GlowEspRenderer.Bounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
   public final ChoiceSetting source = new ChoiceSetting(
      "Цели", new BooleanSetting("Игроки", true), new BooleanSetting("Мобы", false), new BooleanSetting("Предметы", false), new BooleanSetting("Себя", false)
   );
   public final BooleanSetting target = new BooleanSetting("Невидимые", true);
   public final NumberSetting pending = new NumberSetting("Дистанция", 96.0F, 8.0F, 256.0F, 1.0F, false);
   public final ModeSetting previous = new ModeSetting("Эффект", "Свечение + контур", "Свечение + контур", "Свечение", "Контур");
   public final NumberSetting latest = new NumberSetting("Размер свечения", 10.0F, 2.0F, 32.0F, 1.0F, false).handle(this::drawAnimation);
   public final NumberSetting summary = new NumberSetting("Яркость свечения", 2.0F, 0.25F, 5.0F, 0.05F, false).handle(this::drawAnimation);
   public final NumberSetting matrixBlend = new NumberSetting("Толщина контура", 2.0F, 0.5F, 6.0F, 0.5F, false).handle(this::tick);
   public final NumberSetting vectorMatch = new NumberSetting("Прозрачность", 0.92F, 0.05F, 1.0F, 0.01F, true);
   public final ModeSetting itemProject = new ModeSetting("Режим цвета", "Градиент", "Градиент", "Статичный");
   public final ModeSetting responseCompute = new ModeSetting("Источник цвета", "Тема", "Тема", "Свой");
   public final ColorSetting providerFetch = new ColorSetting("Основной цвет", 55.0F, 0.72F, 1.0F).process(() -> this.responseCompute.process("Тема"));
   public final ColorSetting profileDraw = new ColorSetting("Второй цвет", 76.0F, 0.78F, 1.0F)
      .process(() -> this.responseCompute.process("Тема") || this.itemProject.process("Статичный"));
   public final BooleanSetting vectorPerform = new BooleanSetting("Выделять друзей", true);
   public final ColorSetting eventAttach = new ColorSetting("Цвет друзей", 40.0F, 0.8F, 1.0F).process(() -> !this.vectorPerform.compute());
   private final Predicate<Entity> moduleCollect = this::handle;
   private final Predicate<Entity> providerClose = var1 -> this.handle(var1) && this.process(var1);
   private GlowEspRenderer presetSave;
   private static final int windowConvert = 0;
   private static final int presetWrite = 1;
   private static final int colorMeasure = 2;
   private final Matrix4f animationSchedule = new Matrix4f();
   private final Matrix4f rendererScan = new Matrix4f();
   private final Vector4f sourceBuild = new Vector4f();
   private final Vector3f outputCollapse = new Vector3f();
   private final int[] profileInvoke = new int[]{0, 0, 0, 0};
   private final float[] sourceSchedule = new float[4];
   private final float[] timerRender = new float[3];
   private final float[] scaleSave = new float[3];
   private final float[] colorCompute = new float[3];
   private final float[] scaleAdapt = new float[4];
   private boolean textureRun;
   private GlowEspRenderer.Bounds indexBind;

   public GlowESP() {
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
         this.eventAttach
      );
   }

   @Override
   public void handle() {
      super.handle();
      this.render();
   }

   @Override
   public void process() {
      FramebufferCapture var1 = FramebufferCapture.handle();
      var1.process("glow_esp_friends");
      var1.handle("glow_esp");
      this.encodePoint();
      super.process();
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      this.render();
   }

   @EventHandler(handle = 0)
   public void handle(HudRenderContext var1) {
      this.render();
      if (!IrisCompatibility.handle()) {
         if (var1 != null
            && var1.compute() != null
            && Module.client.world != null
            && Module.client.player != null
            && Module.client.getWindow() != null
            && !Module.client.getWindow().hasZeroWidthOrHeight()) {
            int var2 = var1.apply();
            int var3 = var1.execute();
            if (var2 > 0 && var3 > 0) {
               this.handle(var2, var3, var1.resolve());
            }
         }
      }
   }

   public void refresh() {
      if (this.enabled
         && IrisCompatibility.handle()
         && Module.client.world != null
         && Module.client.player != null
         && Module.client.getWindow() != null
         && !Module.client.getWindow().hasZeroWidthOrHeight()) {
         int var1 = Module.client.getWindow().getFramebufferWidth();
         int var2 = Module.client.getWindow().getFramebufferHeight();
         if (var1 > 0 && var2 > 0) {
            this.handle(var1, var2, null);
         }
      }
   }

   private void handle(int var1, int var2, RoundedRectRenderer var3) {
      FramebufferCapture var4 = FramebufferCapture.handle();
      int var5 = var4.resolve();
      int var6 = var4.update();
      if (var5 > 0) {
         int var7 = 0;
         int var8 = 0;
         if (this.vectorPerform.compute() && var6 > 0) {
            var7 = var4.execute();
            var8 = var4.prepare();
         }

         boolean var9 = var7 > 0 && var8 > 0;
         float var10 = this.latest.compute() * 2.0F;
         float var11 = this.matrixBlend.compute();
         GlowEspRenderer.Bounds var12 = this.handle(var1, var2, this.drawAnimation() ? 0.0F : var10, this.tick() ? 0.0F : var11);
         if (var12 != null) {
            GlowEspRenderer.Bounds var13 = var12 == frameCheck ? null : var12;
            if (this.presetSave == null) {
               this.presetSave = new GlowEspRenderer();
            }

            if (var3 != null) {
               var3.compute();
            }

            this.process(this.timerRender, this.scaleSave);
            this.presetSave.handle(var5, var6, var1, var2, this.handle(this.timerRender, this.scaleSave), var13, var7, var8, var9 ? 1 : 0);
            if (var9) {
               handle(this.eventAttach.compute().getRGB(), this.colorCompute);
               this.presetSave.handle(var5, var6, var1, var2, this.handle(this.colorCompute, this.colorCompute), this.indexBind, var7, var8, 2);
            }

            if (var3 != null) {
               var3.compute();
            }
         }
      }
   }

   private GlowEspRenderer.ColorStop handle(float[] var1, float[] var2) {
      return new GlowEspRenderer.ColorStop(
         this.latest.compute() * 2.0F,
         this.matrixBlend.compute(),
         this.drawAnimation() ? 0.0F : this.summary.compute() * 2.0F,
         this.tick() ? 0.0F : 1.35F,
         this.vectorMatch.compute(),
         0,
         this.itemProject.process("Статичный") ? 1 : 0,
         0,
         var1[0],
         var1[1],
         var1[2],
         var2[0],
         var2[1],
         var2[2]
      );
   }

   private boolean handle(Entity var1) {
      if (!this.enabled || Module.client.player == null || var1 == null || !var1.isAlive() || var1.isRemoved()) {
         return false;
      } else if (var1.isInvisible() && !this.target.compute()) {
         return false;
      } else {
         float var2 = Math.max(1.0F, this.pending.compute());
         if (Module.client.player.squaredDistanceTo(var1) > var2 * var2) {
            return false;
         } else if (var1 == Module.client.player) {
            return this.source.process("Себя");
         } else if (var1 instanceof PlayerEntity) {
            return this.source.process("Игроки");
         } else {
            return var1 instanceof ItemEntity ? this.source.process("Предметы") : var1 instanceof LivingEntity && this.source.process("Мобы");
         }
      }
   }

   private GlowEspRenderer.Bounds handle(int var1, int var2, float var3, float var4) {
      this.indexBind = null;
      this.textureRun = false;
      if (Module.client.world != null && Module.client.gameRenderer != null && var1 > 0 && var2 > 0) {
         Camera var5 = Module.client.gameRenderer.getCamera();
         if (var5 == null) {
            return frameCheck;
         }

         Vec3d var6 = var5.getPos();
         this.animationSchedule.set(ClientMathUtil.context);
         this.rendererScan.set(ClientMathUtil.instance).mul(ClientMathUtil.data);
         this.profileInvoke[0] = 0;
         this.profileInvoke[1] = 0;
         this.profileInvoke[2] = var1;
         this.profileInvoke[3] = var2;
         float var7 = Module.client.getRenderTickCounter().getTickProgress(true);
         boolean var8 = this.vectorPerform.compute();
         float var9 = Float.POSITIVE_INFINITY;
         float var10 = Float.POSITIVE_INFINITY;
         float var11 = Float.NEGATIVE_INFINITY;
         float var12 = Float.NEGATIVE_INFINITY;
         boolean var13 = false;

         for (Entity var15 : Module.client.world.getEntities()) {
            if (this.handle(var15)) {
               int var16 = this.handle(var15, var7, var1, var2, var6);
               if (var16 == 2) {
                  return frameCheck;
               }

               if (var16 != 1) {
                  var9 = Math.min(var9, this.sourceSchedule[0]);
                  var10 = Math.min(var10, this.sourceSchedule[1]);
                  var11 = Math.max(var11, this.sourceSchedule[2]);
                  var12 = Math.max(var12, this.sourceSchedule[3]);
                  var13 = true;
                  if (var8 && this.process(var15)) {
                     if (this.textureRun) {
                        this.scaleAdapt[0] = Math.min(this.scaleAdapt[0], this.sourceSchedule[0]);
                        this.scaleAdapt[1] = Math.min(this.scaleAdapt[1], this.sourceSchedule[1]);
                        this.scaleAdapt[2] = Math.max(this.scaleAdapt[2], this.sourceSchedule[2]);
                        this.scaleAdapt[3] = Math.max(this.scaleAdapt[3], this.sourceSchedule[3]);
                     } else {
                        this.scaleAdapt[0] = this.sourceSchedule[0];
                        this.scaleAdapt[1] = this.sourceSchedule[1];
                        this.scaleAdapt[2] = this.sourceSchedule[2];
                        this.scaleAdapt[3] = this.sourceSchedule[3];
                        this.textureRun = true;
                     }
                  }
               }
            }
         }

         if (this.textureRun) {
            this.indexBind = this.handle(this.scaleAdapt, var1, var2, var3, var4);
         }

         if (var13 && Float.isFinite(var9) && Float.isFinite(var10) && Float.isFinite(var11) && Float.isFinite(var12)) {
            int var21 = (int)Math.ceil(Math.max(8.0F, var3 + var4 * 4.0F + 18.0F));
            int var22 = Math.max(0, (int)Math.floor(var9) - var21);
            int var23 = Math.max(0, (int)Math.floor(var10) - var21);
            int var17 = Math.min(var1, (int)Math.ceil(var11) + var21);
            int var18 = Math.min(var2, (int)Math.ceil(var12) + var21);
            int var19 = var17 - var22;
            int var20 = var18 - var23;
            return var19 > 2 && var20 > 2 ? new GlowEspRenderer.Bounds(var22, var23, var19, var20) : frameCheck;
         } else {
            return null;
         }
      } else {
         return frameCheck;
      }
   }

   private int handle(Entity var1, float var2, int var3, int var4, Vec3d var5) {
      Vec3d var6 = var1.getLerpedPos(var2);
      Vec3d var7 = var1.getPos();
      double var8 = var6.x - var7.x;
      double var10 = var6.y - var7.y;
      double var12 = var6.z - var7.z;
      double var14 = var1 instanceof ItemEntity ? 0.45 : 0.18;
      double var16 = Math.max(0.1, var14 * 0.65);
      Box var18 = var1.getBoundingBox();
      double var19 = var18.minX + var8 - var14;
      double var21 = var18.maxX + var8 + var14;
      double var23 = var18.minY + var10 - var16;
      double var25 = var18.maxY + var10 + var16;
      double var27 = var18.minZ + var12 - var14;
      double var29 = var18.maxZ + var12 + var14;
      float var31 = Float.POSITIVE_INFINITY;
      float var32 = Float.POSITIVE_INFINITY;
      float var33 = Float.NEGATIVE_INFINITY;
      float var34 = Float.NEGATIVE_INFINITY;

      for (int var35 = 0; var35 < 2; var35++) {
         double var36 = var35 == 0 ? var19 : var21;

         for (int var38 = 0; var38 < 2; var38++) {
            double var39 = var38 == 0 ? var23 : var25;

            for (int var41 = 0; var41 < 2; var41++) {
               double var42 = var41 == 0 ? var27 : var29;
               if (!this.handle(var36, var39, var42, var5)) {
                  return 2;
               }

               float var44 = this.outputCollapse.z;
               if (var44 <= 0.001F || var44 > 1.0F) {
                  return 2;
               }

               float var45 = this.outputCollapse.x;
               float var46 = var4 - this.outputCollapse.y;
               var31 = Math.min(var31, var45);
               var32 = Math.min(var32, var46);
               var33 = Math.max(var33, var45);
               var34 = Math.max(var34, var46);
            }
         }
      }

      if (!Float.isFinite(var31) || !Float.isFinite(var32) || !Float.isFinite(var33) || !Float.isFinite(var34)) {
         return 1;
      }

      if (!(var33 < 0.0F) && !(var34 < 0.0F) && !(var31 > var3) && !(var32 > var4)) {
         int var47 = Math.max(0, (int)Math.floor(var31));
         int var48 = Math.max(0, (int)Math.floor(var32));
         int var37 = Math.min(var3, (int)Math.ceil(var33));
         int var49 = Math.min(var4, (int)Math.ceil(var34));
         if (var37 - var47 > 0 && var49 - var48 > 0) {
            this.sourceSchedule[0] = var47;
            this.sourceSchedule[1] = var48;
            this.sourceSchedule[2] = var37;
            this.sourceSchedule[3] = var49;
            return 0;
         } else {
            return 1;
         }
      } else {
         return 1;
      }
   }

   private boolean handle(double var1, double var3, double var5, Vec3d var7) {
      this.sourceBuild.set((float)(var1 - var7.x), (float)(var3 - var7.y), (float)(var5 - var7.z), 1.0F).mul(this.animationSchedule);
      this.rendererScan.project(this.sourceBuild.x(), this.sourceBuild.y(), this.sourceBuild.z(), this.profileInvoke, this.outputCollapse);
      return Float.isFinite(this.outputCollapse.x) && Float.isFinite(this.outputCollapse.y) && Float.isFinite(this.outputCollapse.z);
   }

   private void render() {
      FramebufferCapture var1 = FramebufferCapture.handle();
      var1.handle("glow_esp", this.enabled, this.moduleCollect);
      boolean var2 = this.enabled && this.vectorPerform.compute() && !FriendManager.resolve().isEmpty();
      var1.process("glow_esp_friends", var2, this.providerClose);
   }

   private boolean tick() {
      return this.previous.process("Свечение");
   }

   private boolean drawAnimation() {
      return this.previous.process("Контур");
   }

   private boolean process(Entity var1) {
      if (var1 instanceof PlayerEntity var2 && var1 != Module.client.player) {
         String var3 = var2.getGameProfile() != null ? var2.getGameProfile().getName() : var2.getName().getString();
         return FriendManager.handle(var3);
      } else {
         return false;
      }
   }

   private GlowEspRenderer.Bounds handle(float[] var1, int var2, int var3, float var4, float var5) {
      int var6 = (int)Math.ceil(Math.max(8.0F, var4 + var5 * 4.0F + 18.0F));
      int var7 = Math.max(0, (int)Math.floor(var1[0]) - var6);
      int var8 = Math.max(0, (int)Math.floor(var1[1]) - var6);
      int var9 = Math.min(var2, (int)Math.ceil(var1[2]) + var6);
      int var10 = Math.min(var3, (int)Math.ceil(var1[3]) + var6);
      int var11 = var9 - var7;
      int var12 = var10 - var8;
      return var11 > 2 && var12 > 2 ? new GlowEspRenderer.Bounds(var7, var8, var11, var12) : null;
   }

   private void process(float[] var1, float[] var2) {
      if (this.responseCompute.process("Свой")) {
         handle(this.providerFetch.compute().getRGB(), var1);
         handle(this.profileDraw.compute().getRGB(), var2);
      } else {
         ThemePalette var3 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
         ThemeColors var4 = ThemeColors.handle(var3, ThemeShaderApplier.resolve());
         handle(var4.save(), var1);
         handle(var4.submit(), var2);
      }
   }

   private static void handle(int var0, float[] var1) {
      var1[0] = (var0 >> 16 & 0xFF) / 255.0F;
      var1[1] = (var0 >> 8 & 0xFF) / 255.0F;
      var1[2] = (var0 & 0xFF) / 255.0F;
   }

   private void encodePoint() {
      GlowEspRenderer var1 = this.presetSave;
      this.presetSave = null;
      if (var1 != null) {
         if (RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L) {
            var1.close();
         } else {
            if (Module.client != null) {
               Module.client.execute(var1::close);
            }
         }
      }
   }
}
