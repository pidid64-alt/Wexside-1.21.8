package ru.wild.util.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import ru.wild.WildClient;
import ru.wild.api.event.HudRenderContext;
import ru.wild.core.manager.FriendManager;
import ru.wild.gui.hud.DamageIndicatorTracker;
import ru.wild.gui.hud.TargetHudRenderer;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.modules.visuals.NameTags;
import ru.wild.modules.visuals.ProtectInfo;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.SdfTextRenderer;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.math.DampedFloatTracker;
import ru.wild.util.math.SpringAnimationSpec;

public final class EntityOverlayRenderer {
   private static final MinecraftClient instance = MinecraftClient.getInstance();
   static final SpringAnimationSpec data = new SpringAnimationSpec(0.014F, 0.74F, 0.001F, 0.001F);
   static final SpringAnimationSpec context = new SpringAnimationSpec(0.012F, 0.8F, 0.001F, 0.001F);
   static final SpringAnimationSpec config = new SpringAnimationSpec(0.082F, 0.56F, 0.001F, 0.001F);
   static final SpringAnimationSpec state = new SpringAnimationSpec(0.066F, 0.7F, 0.001F, 0.001F);
   private static final SpringAnimationSpec cache = new SpringAnimationSpec(0.072F, 0.62F, 0.001F, 0.001F);
   private static final SpringAnimationSpec output = new SpringAnimationSpec(0.096F, 0.78F, 0.001F, 0.001F);
   private static final SpringAnimationSpec current = new SpringAnimationSpec(0.028F, 0.92F, 0.001F, 0.001F);
   private static final SpringAnimationSpec active = new SpringAnimationSpec(0.11F, 0.7F, 0.001F, 0.001F);
   private static final SpringAnimationSpec mode = new SpringAnimationSpec(0.062F, 0.76F, 0.001F, 0.001F);
   private static final SpringAnimationSpec selection = new SpringAnimationSpec(0.058F, 0.78F, 0.001F, 0.001F);
   private static final float enabled = 96.0F;
   private static final float renderer = 0.85F;
   private static final float handler = 1.35F;
   private static final float animationDraw = 11.5F;
   private static final float pointEncode = 29.0F;
   private static final float animator = 7.0F;
   private static final float source = 8.0F;
   private static final float target = 5.5F;
   private static final float pending = 11.0F;
   private static final float previous = 22.0F;
   private static final float latest = 8.0F;
   private static final float summary = 10.0F;
   private static final float matrixBlend = 16.0F;
   private static final float vectorMatch = 7.0F;
   private static final float itemProject = 14.8F;
   private static final float responseCompute = 11.2F;
   private static final float providerFetch = 12.8F;
   private static final float profileDraw = 9.4F;
   private static final float vectorPerform = 3.6F;
   private static final float eventAttach = 5.0F;
   private static final float serverRead = 16.0F;
   private static final float positionAdvance = 4.0F;
   private static final float frameCheck = 6.0F;
   private static final float moduleCollect = 22.0F;
   private static final float providerClose = 7.2F;
   private static final float presetSave = 92.0F;
   private static final float windowConvert = 340.0F;
   private static final float presetWrite = 4.0F;
   private static final float colorMeasure = 1.35F;
   private static final float animationSchedule = 14.0F;
   private static final float rendererScan = 0.085F;
   private static final float sourceBuild = 4.0F;
   private static final float outputCollapse = 1.32F;
   private static final long profileInvoke = 480L;
   private final Map<UUID, EntityOverlayRenderer.FileEntry> sourceSchedule = new HashMap<>();

   public void handle() {
      this.sourceSchedule.clear();
   }

   public void handle(HudRenderContext var1, NameTags var2) {
      if (instance.world != null && instance.player != null && !(instance.currentScreen instanceof InventoryScreen)) {
         RoundedRectRenderer var3 = var1.resolve();
         EntityOverlayRenderer.SecondaryDataRecord var4 = this.process();
         long var5 = System.currentTimeMillis();
         float var7 = instance.getRenderTickCounter().getTickProgress(true);
         HashSet var8 = new HashSet();

         for (PlayerEntity var10 : instance.world.getPlayers()) {
            if (this.handle(var10, var2)) {
               EntityOverlayRenderer.DataRecord var11 = this.handle(var10, var7, var1.apply(), var1.execute());
               EntityOverlayRenderer.FileEntry var12 = this.sourceSchedule.get(var10.getUuid());
               if (var11 != null) {
                  if (var12 == null) {
                     var12 = this.sourceSchedule.computeIfAbsent(var10.getUuid(), EntityOverlayRenderer.FileEntry::new);
                  }

                  this.handle(var12, var10, var11, var2, var5);
                  var8.add(var10.getUuid());
               } else if (var12 != null) {
                  this.handle(var12, var10, var2, var5);
                  var8.add(var10.getUuid());
               }
            }
         }

         for (Entry var15 : this.sourceSchedule.entrySet()) {
            if (!var8.contains(var15.getKey())) {
               ((EntityOverlayRenderer.FileEntry)var15.getValue()).handle();
            }
         }

         ArrayList<EntityOverlayRenderer.Bounds> var14 = new ArrayList<>(this.sourceSchedule.size());

         for (EntityOverlayRenderer.FileEntry var18 : this.sourceSchedule.values()) {
            EntityOverlayRenderer.Bounds var20 = this.handle(var18, var2, var5);
            if (var20 != null) {
               var14.add(var20);
            }
         }

         var3.handle(this.handle(var14.size()));
         var14.sort(Comparator.comparingDouble(EntityOverlayRenderer.Bounds::distance).reversed());
         ArrayList var17 = new ArrayList(var14.size());

         for (EntityOverlayRenderer.Bounds var21 : var14) {
            this.handle(var3, var4, var21, var5);
            if (var21.itemReveal > 0.04F && !var21.state.sourceBuild.isEmpty()) {
               this.compute(var3, var4, var21);
               var17.add(var21);
            }
         }

         this.handle(var3, var17);
         this.sourceSchedule.entrySet().removeIf(var2x -> var2x.getValue().handle(var5));
      } else {
         this.handle();
      }
   }

   private void handle(EntityOverlayRenderer.FileEntry var1, PlayerEntity var2, EntityOverlayRenderer.DataRecord var3, NameTags var4, long var5) {
      boolean var7 = var1.pointEncode;
      boolean var8 = var1.animator;
      var1.pointEncode = true;
      var1.animator = true;
      if (!var7 || !var8 && var5 - var1.matrixBlend > 480L) {
         var1.summary = var5;
      }

      var1.matrixBlend = var5;
      if (!this.process(var1, var2, var4, var5)) {
         var1.pointEncode = false;
         var1.animator = false;
      } else {
         var1.handler = var3;
         var1.source = this.process(var2, var4);
         var1.target = var1.source;
         var1.pending = !var1.sourceBuild.isEmpty() && (var1.source || var1.responseCompute <= var4.vectorPerform.compute());
         var1.latest = var5;
         if (!var1.animationDraw) {
            var1.context.handle(0.0F);
            var1.config.handle(0.0F);
            var1.state.handle(0.0F);
            var1.cache.handle(0.0F);
            var1.output.handle(var1.profileDraw);
            var1.current.handle(var1.eventAttach);
            var1.active.handle(var1.providerFetch);
            var1.mode.handle(var1.providerFetch);
            var1.selection.handle(0.0F);
            var1.enabled.handle(0.0F);
            var1.renderer.handle(0.0F);
            var1.animationDraw = true;
         }
      }
   }

   private void handle(EntityOverlayRenderer.FileEntry var1, PlayerEntity var2, NameTags var3, long var4) {
      var1.pointEncode = true;
      var1.animator = false;
      if (!this.process(var1, var2, var3, var4)) {
         var1.pointEncode = false;
      } else {
         var1.latest = var4;
         var1.source = false;
         var1.target = var1.target && var1.state.handle() > 0.08F;
         var1.pending = var1.pending && var1.cache.handle() > 0.08F;
      }
   }

   private boolean process(EntityOverlayRenderer.FileEntry var1, PlayerEntity var2, NameTags var3, long var4) {
      String var6 = this.handle(var2.getGameProfile() != null ? var2.getGameProfile().getName() : var2.getName().getString());
      if (var6.isEmpty()) {
         return false;
      }

      var1.presetSave = ProtectInfo.compute(var6);
      var1.previous = FriendManager.handle(var6);
      var1.windowConvert = TargetHudRenderer.handle(var2);
      var1.presetWrite = TargetHudRenderer.handle(var2, 16734824, 255) & 16777215;
      var1.responseCompute = instance.player == null ? Float.MAX_VALUE : var2.distanceTo(instance.player);
      var1.sourceBuild = this.compute(var2, var3);
      var1.outputCollapse = this.resolve(var2, var3);
      var1.animationSchedule = this.handle(var1);
      var1.rendererScan = this.handle(var2, var1);
      float var7 = this.handle(var2);
      var1.colorMeasure = this.handle(var7);
      float var8 = Math.max(1.0F, var2.getMaxHealth());
      float var9 = this.process(var7 / var8);
      if (var1.animationDraw && var9 + 0.004F < var1.providerFetch) {
         var1.vectorMatch = var4;
      }

      var1.providerFetch = var9;
      int var10 = this.compute(var1);
      if (var10 != var1.itemProject) {
         this.process(var1);
         var1.itemProject = var10;
      }

      return true;
   }

   private EntityOverlayRenderer.Bounds handle(EntityOverlayRenderer.FileEntry var1, NameTags var2, long var3) {
      if (!var1.animationDraw) {
         return null;
      }

      float var5 = var1.context.handle(var1.pointEncode ? 1.0F : 0.0F, data);
      float var6 = var1.config.handle(var1.pointEncode ? 1.0F : 0.0F, context);
      float var7 = var1.active.handle(var1.providerFetch, output);
      float var8 = var1.mode.handle();
      if (var1.providerFetch >= var8) {
         var1.mode.handle(var1.providerFetch);
         var8 = var1.providerFetch;
      } else {
         var8 = var1.mode.handle(var1.providerFetch, current);
      }

      float var9 = var1.enabled.handle(0.0F, mode);
      if (var1.animator && var1.handler != null) {
         float var10 = var1.state.handle(var1.target ? 1.0F : 0.0F, config);
         float var11 = var1.cache.handle(var1.pending ? 1.0F : 0.0F, state);
         float var12 = var1.selection.handle(var1.source ? 1.0F : 0.0F, active);
         float var13 = this.resolve(var1.profileDraw, var1.vectorPerform, var10);
         float var14 = this.resolve(var1.eventAttach, var1.serverRead, var10);
         float var15 = var1.output.handle(var13, cache);
         float var16 = var1.current.handle(var14, cache);
         float var17 = this.handle(var1.handler.distance(), var1.handler.projectedHeight(), var2.providerFetch.compute());
         float var18 = var15 * var17;
         float var19 = var16 * var17;
         float var20 = this.handle(var1, var11, var17);
         float var21 = (1.0F - this.compute(0.1F, 0.95F, var5)) * 5.0F * var17;
         float var22 = this.compute(var1.handler.screenX() - var18 * 0.5F);
         float var23 = this.compute(var1.handler.screenY() - var19 - 8.0F * var17 - var21);
         float var24 = this.compute(0.02F, 0.94F, var5);
         float var25 = this.process(var2.animationSchedule.compute() * (0.14F + 0.86F * var24));
         if (var25 <= 0.01F) {
            return null;
         }

         float var26 = this.handle(var7, var3, var1.data);
         float var27 = this.handle(var3 - var1.summary, 720L);
         float var28 = this.handle(var3 - var1.vectorMatch, 360L);
         float var29 = Math.max(var27, var28);
         var9 = var1.enabled.handle(var29, mode);
         boolean var30 = var5 < 0.985F || var9 > 0.04F || var1.summary >= var3 - 760L || var12 > 0.04F;
         return new EntityOverlayRenderer.Bounds(
            var1, var22, var23, var18, var19, var17, var20, var25, var5, var6, var10, var11, var12, var7, var8, var26, var9, var30
         );
      } else {
         return null;
      }
   }

   private void handle(RoundedRectRenderer var1, EntityOverlayRenderer.SecondaryDataRecord var2, EntityOverlayRenderer.Bounds var3, long var4) {
      DamageIndicatorTracker.handle(var3.state.instance, var3.y - var3.topExtension);
      float var6 = this.handle(var3);
      if (var3.shader) {
         this.handle(var1, var2, var3);
         RoundedRectRenderer.PrimaryColorState var7 = var1.process(var3.x, var3.y, var3.width, var3.height);
         if (var7 != null) {
            try {
               this.handle(var1, var2, var3, 0.0F, 0.0F, EntityOverlayRenderer.Mode.GHOST);
            } finally {
               var1.handle(var7);
            }

            boolean var8 = var1.handle(
               var7,
               var3.x,
               var3.y,
               var3.width,
               var3.height,
               11.5F * var3.scale,
               this.handle(var2.shellTop, var3.alpha * 0.8F),
               this.handle(this.handle(var2, var3), var3.alpha * (0.02F + var3.focus * 0.07F)),
               this.handle(this.process(var2, var3), var3.alpha * (0.38F + var3.focus * 0.2F + var3.threat * 0.16F)),
               this.handle(this.compute(var2, var3), var3.alpha * (0.34F + var3.focus * 0.18F + var3.threat * 0.14F)),
               var3.appear,
               var6,
               this.handle(var4),
               var3.focus,
               var3.threat,
               var3.exposure
            );
            if (var8) {
               this.handle(var1, var2, var3, var3.x, var3.y, EntityOverlayRenderer.Mode.OVERLAY);
               return;
            }
         }
      }

      this.process(var1, var2, var3);
      this.handle(var1, var2, var3, var3.x, var3.y, EntityOverlayRenderer.Mode.DIRECT);
   }

   private void handle(RoundedRectRenderer var1, EntityOverlayRenderer.SecondaryDataRecord var2, EntityOverlayRenderer.Bounds var3) {
      float var4 = 11.5F * var3.scale;
      float var5 = this.handle(var3);
      var1.handle(var3.x, var3.y, var3.width, var3.height, var4, var3.alpha * (0.5F + var3.focus * 0.18F + (1.0F - var5) * 0.12F));
      float var6 = var3.focus * 0.92F + var3.threat * 0.98F + var3.exposure * 0.68F;
      if (var6 > 0.03F) {
         int var7 = this.resolve(var2, var3);
         var1.handle(
            var3.x,
            var3.y + var3.scale,
            var3.width,
            var3.height,
            var4,
            20.0F * var3.scale * var6,
            2.4F * var3.scale,
            this.handle(var7, var3.alpha * var6 * 0.15F)
         );
      }
   }

   private void process(RoundedRectRenderer var1, EntityOverlayRenderer.SecondaryDataRecord var2, EntityOverlayRenderer.Bounds var3) {
      float var4 = 11.5F * var3.scale;
      int var5 = this.handle(this.handle(var2, var3), var3.alpha * (0.1F + var3.focus * 0.08F));
      int var6 = this.handle(var2.shellTop, var3.alpha * 0.72F);
      int var7 = this.handle(var2.shellBottom, var3.alpha * 0.88F);
      int var8 = this.resolve(var2, var3);
      var1.handle(var3.x, var3.y, var3.width, var3.height, var4, var3.alpha * 0.52F);
      var1.process(var3.x, var3.y, var3.width, var3.height, var4, var6, var7);
      var1.process(
         var3.x + 1.0F,
         var3.y + 1.0F,
         Math.max(0.0F, var3.width - 2.0F),
         Math.max(0.0F, var3.height * 0.52F),
         Math.max(0.0F, var4 - 1.0F),
         this.handle(16777215, var3.alpha * 0.016F),
         this.handle(16777215, 0.0F)
      );
      var1.handle(var3.x, var3.y, var3.width, var3.height, var4, var5, Math.max(0.7F, var3.scale * 0.72F));
      float var9 = var3.focus * 0.92F + var3.threat * 0.98F + var3.exposure * 0.68F;
      if (var9 > 0.03F) {
         var1.handle(
            var3.x,
            var3.y + var3.scale,
            var3.width,
            var3.height,
            var4,
            18.0F * var3.scale * var9,
            2.2F * var3.scale,
            this.handle(var8, var3.alpha * var9 * 0.13F)
         );
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      EntityOverlayRenderer.SecondaryDataRecord var2,
      EntityOverlayRenderer.Bounds var3,
      float var4,
      float var5,
      EntityOverlayRenderer.Mode var6
   ) {
      EntityOverlayRenderer.FileEntry var7 = var3.state;
      float var8 = var3.scale;
      float var9 = 10.0F * var8;
      float var10 = 29.0F * var8;
      float var11 = 16.0F * var8;
      float var12 = 7.0F * var8;
      float var13 = 14.8F * var8;
      float var14 = 11.2F * var8;
      float var15 = 12.8F * var8;
      float var16 = 9.4F * var8;
      float var17 = this.handle(var3);
      float var18 = this.handle(var3, var6);
      float var19 = this.handle(var3, var18);
      float var20 = var6 == EntityOverlayRenderer.Mode.GHOST ? 0.48F : 1.0F;
      float var21 = var6 == EntityOverlayRenderer.Mode.GHOST ? 0.15F : 0.32F;
      float var22 = (1.0F - var17) * (var6 == EntityOverlayRenderer.Mode.OVERLAY ? 1.85F : 5.0F) * var8;
      float var23 = var4 + var9;
      float var24 = var5 + (var10 - var11) * 0.5F + var22 * 0.16F;
      float var25 = this.compute(0.18F, 0.74F, var19);
      float var26 = var7.positionAdvance * var8 * var25;
      float var27 = var23 + var11 + var12;
      float var28 = Math.max(0.0F, var4 + var3.width - var9 - var27 - var26 - 42.0F * var8);
      String var29 = var7.windowConvert.isEmpty() ? "" : this.handle(var7.windowConvert, var28, FontRegistry.instance, var14);
      float var30 = var29.isEmpty() ? 0.0F : this.process(FontRegistry.instance, var29, var14);
      float var31 = var29.isEmpty() ? 0.0F : 5.0F * var8;
      float var32 = var27 + var30 + var31;
      float var33 = Math.max(10.0F * var8, var4 + var3.width - var9 - var32 - Math.max(0.0F, var26 + 8.0F * var8 * var25));
      String var34 = this.handle(var7.presetSave, var33, FontRegistry.config, var13);
      EntityOverlayRenderer.PrimaryBounds var35 = this.handle(FontRegistry.config, var34, var13);
      float var36 = this.handle(var5 + var22 * 0.08F, var10 - 5.0F * var8, var35.height);
      var1.handle(var23, var24, var11, var11, var11 * 0.48F, this.handle(var2.avatarBackdrop, var3.alpha * var20 * (0.14F + 0.2F * var18)));
      this.handle(
         var1,
         var7.instance,
         var7.presetSave,
         var23,
         var24,
         var11,
         var3.alpha * (var6 == EntityOverlayRenderer.Mode.GHOST ? 0.18F + 0.42F * var18 : 0.42F + 0.58F * var18)
      );
      int var37 = this.handle(this.update(var2, var3), var3.alpha * var18 * var20);
      if (!var29.isEmpty()) {
         EntityOverlayRenderer.PrimaryBounds var38 = this.handle(FontRegistry.instance, var29, var14);
         float var39 = this.handle(var5 + var22 * 0.08F, var10 - 5.0F * var8, var38.height);
         this.handle(
            var1, FontRegistry.instance, var29, var27, var39, var14, this.handle(var7.presetWrite, var3.alpha * var18 * var20), var3.alpha * var18 * var21
         );
      }

      this.handle(var1, FontRegistry.config, var34, var32, var36, var13, var37, var3.alpha * var18 * var21);
      if (var25 > 0.01F) {
         EntityOverlayRenderer.PrimaryBounds var50 = this.handle(FontRegistry.instance, var7.colorMeasure, var15);
         float var52 = this.process(FontRegistry.instance, var7.colorMeasure, var15);
         float var40 = var4 + var3.width - var9 - var52;
         float var41 = this.handle(var5 + var22 * 0.08F, var10 - 5.0F * var8, var50.height);
         this.handle(
            var1,
            FontRegistry.instance,
            var7.colorMeasure,
            var40,
            var41,
            var15,
            this.handle(var2.textPrimary, var3.alpha * var25 * var20),
            var3.alpha * var25 * var20 * 0.28F
         );
      }

      this.handle(var1, var2, var3, var4 + var9, var5 + var10 - 5.0F * var8 + var22 * 0.1F, var3.width - var9 * 2.0F, 3.6F * var8, var18, var20);
      if (!(var19 <= 0.01F)) {
         boolean var51 = this.resolve(var7);
         float var53 = var5 + var10 + 4.0F * var8;
         int var54 = this.handle(var2.divider, var3.alpha * 0.1F * var19);
         var1.handle(var4 + var9, var53 - 1.5F * var8, var3.width - var9 * 2.0F, Math.max(1.0F, var8), 0.5F * var8, var54, this.handle(var2.divider, 0.0F));
         if (var51) {
            float var55 = this.compute(0.16F, 0.7F, var19) * var18 * var20;
            float var42 = this.handle(var5, var8);
            float var43 = 10.0F * var8;
            float var44 = var7.moduleCollect * var8;
            float var45 = Math.max(0.0F, var3.width - var9 * 2.0F - var44 - (var7.rendererScan.text.isEmpty() ? 0.0F : 8.0F * var8));
            String var46 = this.handle(var7.animationSchedule.text, var45, FontRegistry.instance, var16);
            float var47 = Math.max(0.0F, this.process(FontRegistry.instance, var46, var16) + 8.0F * var8);
            if (!var46.isEmpty()) {
               var1.handle(
                  var4 + var9 - 3.0F * var8,
                  var42 - var43 * 0.72F,
                  var47,
                  var43,
                  var43 * 0.5F,
                  this.handle(this.handle(var2.avatarBackdrop, 592656, 0.3F), var3.alpha * (0.34F * var55))
               );
               this.handle(
                  var1,
                  FontRegistry.instance,
                  var46,
                  var4 + var9,
                  var42,
                  var16,
                  this.handle(var7.animationSchedule.color, var3.alpha * var55),
                  var3.alpha * var55 * 0.26F
               );
            }

            if (!var7.rendererScan.text.isEmpty()) {
               float var48 = Math.max(var7.moduleCollect * var8, this.process(FontRegistry.instance, var7.rendererScan.text, var16));
               float var49 = var4 + var3.width - var9 - var48;
               var1.handle(
                  var49 - 3.0F * var8,
                  var42 - var43 * 0.72F,
                  var48 + 8.0F * var8,
                  var43,
                  var43 * 0.5F,
                  this.handle(this.handle(var2.avatarBackdrop, 592656, 0.3F), var3.alpha * (0.34F * var55))
               );
               this.handle(
                  var1,
                  FontRegistry.instance,
                  var7.rendererScan.text,
                  var49,
                  var42,
                  var16,
                  this.handle(var7.rendererScan.color, var3.alpha * var55),
                  var3.alpha * var55 * 0.26F
               );
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      EntityOverlayRenderer.SecondaryDataRecord var2,
      EntityOverlayRenderer.Bounds var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9
   ) {
      float var10 = var7 * 0.5F;
      float var11 = var6 * this.process(var3.damage);
      float var12 = var6 * this.process(var3.health);
      float var13 = var3.alpha * var9 * (0.24F + 0.76F * var8);
      var1.handle(var4, var5, var6, var7, var10, this.handle(var2.barTrack, var13 * 0.16F));
      var1.process(var4, var5, var6, Math.max(var7 * 0.64F, 1.0F), var10, this.handle(16777215, var13 * 0.02F), this.handle(16777215, 0.0F));
      float var14 = Math.max(0.0F, var11 - var12);
      if (var14 > 0.4F) {
         var1.handle(var4 + var12, var5, var14, var7, var10, this.handle(15988479, var13 * 0.34F), this.handle(16777215, var13 * 0.18F));
      }

      if (var12 > 0.5F) {
         int var15 = this.handle(this.apply(var2, var3), var13);
         int var16 = this.handle(this.execute(var2, var3), var13);
         var1.handle(var4, var5, var12, var7, var10, var15, var16);
         var1.process(var4, var5, var12, var7 * 0.58F, var10, this.handle(16777215, var13 * 0.15F), this.handle(16777215, 0.0F));
         float var17 = Math.max(var7 * 1.2F, 2.0F * var3.scale);
         float var18 = var4 + Math.max(0.0F, var12 - var17);
         var1.process(
            var18,
            var5 - 0.15F * var3.scale,
            var17,
            var7 + 0.3F * var3.scale,
            var10,
            this.handle(16777215, var13 * 0.18F),
            this.handle(this.execute(var2, var3), var13 * 0.1F)
         );
      }

      if (var3.threat > 0.01F) {
         var1.resolve();

         try {
            var1.handle(
               var4,
               var5,
               Math.max(1.0F, Math.max(var12, var11)),
               var7,
               var10,
               7.2F * var3.scale * var3.threat,
               1.65F * var3.scale,
               this.handle(var2.dangerGlow, var3.alpha * (0.1F + var3.threat * 0.12F))
            );
         } finally {
            var1.update();
         }
      }
   }

   private void compute(RoundedRectRenderer var1, EntityOverlayRenderer.SecondaryDataRecord var2, EntityOverlayRenderer.Bounds var3) {
      EntityOverlayRenderer.FileEntry var4 = var3.state;
      if (!var4.sourceBuild.isEmpty() && !(var3.itemReveal <= 0.02F)) {
         float var5 = var3.scale;
         float var6 = 16.0F * var5;
         float var7 = 4.0F * var5;
         float var8 = this.handle(var4.sourceBuild, var6, var7);
         float var9 = var3.x + (var3.width - var8) * 0.5F;
         float var10 = this.compute(var3);
         float var11 = this.compute(0.1F, 0.84F, var3.itemReveal);

         for (int var12 = 0; var12 < var4.sourceBuild.size(); var12++) {
            float var13 = this.process(var11, 0.08F + var12 * 0.06F, 0.2F);
            if (!(var13 <= 0.01F)) {
               float var14 = var9 + var12 * (var6 + var7);
               float var15 = var10 + (1.0F - var13) * 6.0F * var5;
               float var16 = var6 * 0.42F;
               int var17 = this.handle(this.handle(var2.slotFill, 16777215, 0.06F), var3.alpha * (0.12F + var13 * 0.05F));
               int var18 = this.handle(this.handle(var2.avatarBackdrop, 132103, 0.18F), var3.alpha * (0.64F + var13 * 0.06F));
               int var19 = this.handle(this.handle(var2.rim, 16777215, 0.05F), var3.alpha * (0.06F + var13 * 0.04F));
               var1.handle(var14, var15 + 0.8F * var5, var6, var6, var16, 4.8F * var5, 1.05F * var5, this.handle(0, var3.alpha * (0.08F + var13 * 0.05F)));
               var1.process(var14, var15, var6, var6, var16, var17, var18);
               var1.handle(var14, var15, var6, var6, var16, var19, Math.max(0.58F, var5 * 0.7F));
            }
         }
      }
   }

   private void handle(RoundedRectRenderer var1, List<EntityOverlayRenderer.Bounds> var2) {
      if (!var2.isEmpty() && instance.player != null) {
         var1.compute();

         for (EntityOverlayRenderer.Bounds var4 : var2) {
            EntityOverlayRenderer.FileEntry var5 = var4.state;
            float var6 = this.compute(0.1F, 0.84F, var4.itemReveal);
            if (!(var6 <= 0.01F) && !var5.sourceBuild.isEmpty()) {
               float var7 = var4.scale;
               float var8 = 16.0F * var7;
               float var9 = 4.0F * var7;
               float var10 = this.handle(var5.sourceBuild, var8, var9);
               float var11 = var4.x + (var4.width - var10) * 0.5F;
               float var12 = this.compute(var4);

               for (int var13 = 0; var13 < var5.sourceBuild.size(); var13++) {
                  float var14 = this.process(var6, 0.08F + var13 * 0.06F, 0.2F);
                  if (!(var14 <= 0.05F)) {
                     float var15 = var11 + var13 * (var8 + var9);
                     float var16 = var12 + (1.0F - var14) * 6.0F * var7;
                     float var17 = var8 * (0.6F + var14 * 0.24F) / 16.0F;
                     float var18 = 16.0F * var17;
                     float var19 = var15 + (var8 - var18) * 0.5F;
                     float var20 = var16 + (var8 - var18) * 0.5F;
                     int var10005 = var5.data + var13;
                     ItemStackOverlayRenderer.handle(var1, var5.sourceBuild.get(var13).stack, var19, var20, var17, var10005, false, var13);
                  }
               }
            }
         }
      }
   }

   private boolean handle(PlayerEntity var1, NameTags var2) {
      return var1 != null
         && var1.isAlive()
         && !var1.isSpectator()
         && var1 != instance.player
         && (var2.itemProject.compute() || !var1.isInvisibleTo(instance.player));
   }

   private EntityOverlayRenderer.DataRecord handle(PlayerEntity var1, float var2, int var3, int var4) {
      Vec3d var5 = var1.getLerpedPos(var2);
      double var6 = var1.getHeight() + 0.3 - (var1.isSneaking() ? 0.14 : 0.0);
      Vec3d var8 = new Vec3d(var5.x, var5.y + var6, var5.z);
      Vec3d var9 = new Vec3d(var5.x, var5.y + 0.02, var5.z);
      Vec3d var10 = ClientMathUtil.handle(var8);
      Vec3d var11 = ClientMathUtil.handle(var9);
      if (var10 == null || var11 == null) {
         return null;
      }

      if (!(var10.z <= 0.001) && !(var10.z > 1.0) && !(var11.z <= 0.001) && !(var11.z > 1.0)) {
         double var12 = instance.gameRenderer.getCamera().getPos().distanceTo(var8);
         if (var12 > 96.0) {
            return null;
         } else {
            float var14 = Math.max(18.0F, Math.abs((float)(var11.y - var10.y)));
            float var15 = Math.max(18.0F, var14);
            if (!(var10.x < -var15 * 2.0F) && !(var10.x > var3 + var15 * 2.0F) && !(var10.y < -var4 * 0.6F) && !(var10.y > var4 + var15 * 2.0F)) {
               float var16 = this.update((float)var10.x, -var15 * 0.25F, var3 + var15 * 0.25F);
               float var17 = Math.max((float)var10.y, 18.0F);
               return new EntityOverlayRenderer.DataRecord(var16, var17, var12, var14, (float)var10.z);
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private boolean process(PlayerEntity var1, NameTags var2) {
      return instance.currentScreen == null && var2.responseCompute.compute() && instance.targetedEntity == var1;
   }

   private List<EntityOverlayRenderer.PrimaryDataRecord> compute(PlayerEntity var1, NameTags var2) {
      ArrayList var3 = new ArrayList(6);
      if (var2.previous.compute()) {
         this.handle(var3, var1.getEquippedStack(EquipmentSlot.HEAD), EquipmentSlot.HEAD);
         this.handle(var3, var1.getEquippedStack(EquipmentSlot.CHEST), EquipmentSlot.CHEST);
         this.handle(var3, var1.getEquippedStack(EquipmentSlot.LEGS), EquipmentSlot.LEGS);
         this.handle(var3, var1.getEquippedStack(EquipmentSlot.FEET), EquipmentSlot.FEET);
      }

      if (var2.latest.compute()) {
         this.handle(var3, var1.getMainHandStack(), EquipmentSlot.MAINHAND);
      }

      if (var2.summary.compute()) {
         this.handle(var3, var1.getOffHandStack(), EquipmentSlot.OFFHAND);
      }

      return var3;
   }

   private void handle(List<EntityOverlayRenderer.PrimaryDataRecord> var1, ItemStack var2, EquipmentSlot var3) {
      if (var2 != null && !var2.isEmpty()) {
         var1.add(new EntityOverlayRenderer.PrimaryDataRecord(var2.copy(), var3));
      }
   }

   private List<EntityOverlayRenderer.ColorStop> resolve(PlayerEntity var1, NameTags var2) {
      if (!var2.matrixBlend.compute()) {
         return List.of();
      }

      ArrayList var3 = new ArrayList();

      for (StatusEffectInstance var5 : var1.getStatusEffects()) {
         String var6 = this.handle(I18n.translate(((StatusEffect)var5.getEffectType().value()).getTranslationKey(), new Object[0]));
         if (!var6.isEmpty()) {
            String var7 = var6 + " " + this.process(var5.getAmplifier() + 1);
            boolean var8 = ((StatusEffect)var5.getEffectType().value()).getCategory() == StatusEffectCategory.HARMFUL;
            int var9 = var8 ? 16732754 : 15133941;
            var3.add(new EntityOverlayRenderer.ColorStop(var7, var9, var8, var5.getDuration()));
         }
      }

      var3.sort(
         Comparator.<EntityOverlayRenderer.ColorStop, Boolean>comparing(var0 -> !var0.harmful)
            .thenComparingInt(EntityOverlayRenderer.ColorStop::duration)
            .reversed()
            .thenComparing(EntityOverlayRenderer.ColorStop::label)
      );
      return var3.size() > 2 ? List.copyOf(var3.subList(0, 2)) : List.copyOf(var3);
   }

   private EntityOverlayRenderer.PrimaryColorStop handle(EntityOverlayRenderer.FileEntry var1) {
      if (!var1.outputCollapse.isEmpty()) {
         return new EntityOverlayRenderer.PrimaryColorStop(var1.outputCollapse.get(0).label, var1.outputCollapse.get(0).color);
      } else {
         return var1.previous ? new EntityOverlayRenderer.PrimaryColorStop("ALLY", 10284799) : new EntityOverlayRenderer.PrimaryColorStop("", 15133941);
      }
   }

   private EntityOverlayRenderer.PrimaryColorStop handle(PlayerEntity var1, EntityOverlayRenderer.FileEntry var2) {
      if (var2.outputCollapse.size() > 1) {
         return new EntityOverlayRenderer.PrimaryColorStop(var2.outputCollapse.get(1).label, var2.outputCollapse.get(1).color);
      } else {
         return var1.getArmor() > 0
            ? new EntityOverlayRenderer.PrimaryColorStop("ARM " + var1.getArmor(), 12371672)
            : new EntityOverlayRenderer.PrimaryColorStop("", 12371672);
      }
   }

   private void process(EntityOverlayRenderer.FileEntry var1) {
      var1.positionAdvance = this.process(FontRegistry.instance, var1.colorMeasure, 12.8F);
      float var2 = this.process(FontRegistry.config, var1.presetSave, 14.8F);
      float var3 = var1.windowConvert.isEmpty() ? 0.0F : this.process(FontRegistry.instance, var1.windowConvert, 11.2F) + 5.0F;
      var1.frameCheck = this.process(FontRegistry.instance, var1.animationSchedule.text, 9.4F);
      var1.moduleCollect = this.process(FontRegistry.instance, var1.rendererScan.text, 9.4F);
      var1.providerClose = this.handle(var1.sourceBuild, 16.0F, 4.0F);
      var1.profileDraw = this.update(33.0F + var3 + var2 + 10.0F + 10.0F, 92.0F, 340.0F);
      float var4 = 33.0F + var3 + var2 + 12.0F + var1.positionAdvance + 10.0F;
      float var5 = var1.frameCheck + var1.moduleCollect + (!var1.animationSchedule.text.isEmpty() && !var1.rendererScan.text.isEmpty() ? 8.0F : 0.0F);
      var1.vectorPerform = this.update(Math.max(var1.profileDraw + 28.0F, Math.max(var4, var5 + 20.0F + 16.0F)), 92.0F, 340.0F);
      var1.eventAttach = 29.0F;
      float var6 = 0.0F;
      if (this.resolve(var1)) {
         var6 += 15.0F;
         var6 += 11.0F;
      }

      var1.serverRead = 29.0F + var6;
   }

   private int compute(EntityOverlayRenderer.FileEntry var1) {
      int var2 = var1.presetSave.hashCode();
      var2 = 31 * var2 + var1.windowConvert.hashCode();
      var2 = 31 * var2 + var1.presetWrite;
      var2 = 31 * var2 + var1.colorMeasure.hashCode();
      var2 = 31 * var2 + var1.animationSchedule.text.hashCode();
      var2 = 31 * var2 + var1.animationSchedule.color;
      var2 = 31 * var2 + var1.rendererScan.text.hashCode();
      var2 = 31 * var2 + var1.rendererScan.color;

      for (EntityOverlayRenderer.PrimaryDataRecord var4 : var1.sourceBuild) {
         var2 = 31 * var2 + ItemStack.hashCode(var4.stack);
         var2 = 31 * var2 + var4.slot.ordinal();
      }

      for (EntityOverlayRenderer.ColorStop var15 : var1.outputCollapse) {
         var2 = 31 * var2 + var15.label.hashCode();
         var2 = 31 * var2 + var15.color;
      }

      return var2;
   }

   private float handle(double var1, float var3, float var4) {
      float var5 = this.update(var3 / 96.0F, 0.75F, 1.35F);
      float var6 = this.update((float)(1.35 - Math.log(var1 + 1.0) * 0.16), 0.75F, 1.25F);
      float var7 = this.update(var5 * 0.7F + var6 * 0.3F, 0.85F, 1.35F);
      return this.process(var7 * var4, 0.01F);
   }

   private float handle(PlayerEntity var1) {
      float var2 = var1.getHealth() + var1.getAbsorptionAmount();
      if (instance.world != null) {
         Scoreboard var3 = instance.world.getScoreboard();
         ScoreboardObjective var4 = var3.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
         if (var4 != null) {
            ReadableScoreboardScore var5 = var3.getScore(var1, var4);
            if (var5 != null && var5.getScore() > 0) {
               var2 = var5.getScore();
            }
         }
      }

      return Math.max(0.0F, var2);
   }
   private void handle(RoundedRectRenderer var1, UUID var2, String var3, float var4, float var5, float var6, float var7) {
      int var8 = this.handle(var2);
      if (var8 > 0) {
         GlStateManager._bindTexture(var8);
         var1.update(var7);
         boolean var14 = false /* VF: Semaphore variable */;

         try {
            var14 = true;
            float var16 = var6 * 0.48F;
            var1.handle(var8, var4, var5, var6, var6, 0.125F, 0.125F, 0.25F, 0.25F, var16);
            var1.handle(var8, var4, var5, var6, var6, 0.625F, 0.125F, 0.75F, 0.25F, var16);
            var14 = false;
         } finally {
            if (var14) {
               var1.onTick();
            }
         }

         var1.onTick();
      } else {
         var1.handle(var4, var5, var6, var6, var6 * 0.48F, this.handle(1842983, var7 * 0.92F));
         String var9 = var3 != null && !var3.isEmpty() ? var3.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
         float var10 = var6 * 0.62F;
         float var11 = this.process(FontRegistry.config, var9, var10);
         float var12 = this.compute(FontRegistry.config, var9, var10);
         var1.handle(FontRegistry.config, var4 + (var6 - var11) * 0.5F, this.handle(var5, var6, var12), var10, var9, this.handle(15922683, var7));
      }
   }

   private int handle(UUID var1) {
      if (instance.getNetworkHandler() == null) {
         return 0;
      }

      PlayerListEntry var2 = instance.getNetworkHandler().getPlayerListEntry(var1);
      if (var2 == null) {
         return 0;
      }

      Identifier var3 = var2.getSkinTextures().texture();
      if (var3 == null) {
         return 0;
      }

      AbstractTexture var4 = instance.getTextureManager().getTexture(var3);
      return var4 != null && var4.getGlTexture() instanceof GlTexture var5 && var5.getGlId() > 0 ? var5.getGlId() : 0;
   }

   private EntityOverlayRenderer.SecondaryDataRecord process() {
      ThemePalette var1 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
      boolean var2 = ThemeShaderApplier.resolve();
      int var3 = var1.handle().getRGB() & 16777215;
      int var4 = this.process(var3, 1.18F);
      int var5 = this.handle(var3, 16777215, 0.18F);
      int var6 = var2 ? this.handle(var1.compute().getRGB() & 16777215, 16777215, 0.44F) : this.handle(var1.compute().getRGB() & 16777215, 329224, 0.34F);
      int var7 = var2 ? this.handle(var1.process().getRGB() & 16777215, 15265269, 0.5F) : this.handle(var1.process().getRGB() & 16777215, 197638, 0.44F);
      int var8 = var2 ? this.handle(var1.update().getRGB() & 16777215, 1120034, 0.72F) : var1.update().getRGB() & 16777215;
      int var9 = var2 ? this.handle(var1.apply().getRGB() & 16777215, 4147287, 0.62F) : this.handle(var1.apply().getRGB() & 16777215, 15134199, 0.18F);
      int var10 = var2 ? this.handle(var1.resolve().getRGB() & 16777215, var4, 0.34F) : this.handle(var1.resolve().getRGB() & 16777215, var4, 0.18F);
      return new EntityOverlayRenderer.SecondaryDataRecord(
         var4,
         var5,
         var6,
         var7,
         var10,
         var8,
         var9,
         var2 ? 15594234 : 1514017,
         var2 ? 16251647 : 1843241,
         var2 ? 14213614 : 2501688,
         var2 ? 13029857 : 2962497,
         13775174,
         16729440,
         6094796
      );
   }

   private int handle(EntityOverlayRenderer.SecondaryDataRecord var1, EntityOverlayRenderer.Bounds var2) {
      if (var2.state.previous) {
         return this.handle(var1.accentTop, 12122111, 0.34F);
      } else if (var2.focus > 0.02F) {
         return this.handle(var1.accentTop, 16777215, 0.24F);
      } else {
         return var2.threat > 0.01F ? this.handle(var1.danger, var1.accentTop, 0.26F) : var1.rim;
      }
   }

   private int process(EntityOverlayRenderer.SecondaryDataRecord var1, EntityOverlayRenderer.Bounds var2) {
      if (var2.state.previous) {
         return this.handle(9105407, var1.accentTop, 0.44F);
      } else {
         return var2.threat > 0.08F ? this.handle(var1.dangerGlow, var1.danger, 0.36F) : var1.accentTop;
      }
   }

   private int compute(EntityOverlayRenderer.SecondaryDataRecord var1, EntityOverlayRenderer.Bounds var2) {
      if (var2.state.previous) {
         return this.handle(14089215, var1.accentBottom, 0.4F);
      } else {
         return var2.threat > 0.08F ? this.handle(16756920, var1.dangerGlow, 0.46F) : var1.accentBottom;
      }
   }

   private int resolve(EntityOverlayRenderer.SecondaryDataRecord var1, EntityOverlayRenderer.Bounds var2) {
      if (var2.threat > 0.08F) {
         return var1.dangerGlow;
      } else if (var2.state.previous) {
         return 9366527;
      } else {
         return var2.focus > 0.1F ? this.handle(var1.accentTop, 16777215, 0.16F) : var1.accentTop;
      }
   }

   private int handle(EntityOverlayRenderer.SecondaryDataRecord var1, EntityOverlayRenderer.Bounds var2, EquipmentSlot var3) {
      if (var3 == EquipmentSlot.MAINHAND || var3 == EquipmentSlot.OFFHAND) {
         return this.process(var1, var2);
      } else {
         return var2.state.previous ? this.handle(10219519, var1.accentTop, 0.36F) : this.handle(var1.rim, var1.accentBottom, 0.24F);
      }
   }

   private int update(EntityOverlayRenderer.SecondaryDataRecord var1, EntityOverlayRenderer.Bounds var2) {
      if (var2.state.previous) {
         return this.handle(var1.textPrimary, 9433855, 0.34F);
      } else {
         return var2.focus > 0.08F ? this.handle(var1.textPrimary, var1.accentTop, 0.18F) : var1.textPrimary;
      }
   }

   private int apply(EntityOverlayRenderer.SecondaryDataRecord var1, EntityOverlayRenderer.Bounds var2) {
      return var2.state.previous
         ? this.handle(var1.accentTop, var1.safeGlow, 0.32F)
         : this.handle(var2.health, this.handle(12985918, var1.danger, 0.45F), 15245893, 5427594);
   }

   private int execute(EntityOverlayRenderer.SecondaryDataRecord var1, EntityOverlayRenderer.Bounds var2) {
      return var2.state.previous ? this.handle(var1.accentBottom, var1.safeGlow, 0.28F) : this.handle(var2.health, 16743309, 16765559, 10944454);
   }

   private int handle(float var1, int var2, int var3, int var4) {
      float var5 = this.process(var1);
      return var5 < 0.5F ? this.handle(var2, var3, var5 * 2.0F) : this.handle(var3, var4, (var5 - 0.5F) * 2.0F);
   }

   private String handle(float var1) {
      float var2 = Math.round(var1 * 10.0F) / 10.0F;
      return !(var2 >= 10.0F) && var2 != (int)var2 ? String.format(Locale.US, "%.1f HP", var2) : Math.round(var2) + " HP";
   }

   private String handle(String var1, float var2, FontObject var3, float var4) {
      String var5 = this.handle(var1);
      if (var5.isEmpty()) {
         return "";
      }

      if (this.process(var3, var5, var4) <= var2) {
         return var5;
      }

      for (int var6 = var5.length() - 1; var6 > 0; var6--) {
         String var7 = var5.substring(0, var6).trim() + "...";
         if (this.process(var3, var7, var4) <= var2) {
            return var7;
         }
      }

      return "...";
   }

   private String handle(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = var1.replaceAll("(?i)§[0-9A-FK-OR]", "").replace('\n', ' ').replace('\r', ' ').replaceAll("\\p{Cntrl}", "").trim();

         while (var2.contains("  ")) {
            var2 = var2.replace("  ", " ");
         }

         return var2;
      } else {
         return "";
      }
   }

   private void handle(RoundedRectRenderer var1, FontObject var2, String var3, float var4, float var5, float var6, int var7, float var8) {
      if (var3 != null && !var3.isEmpty()) {
         var1.handle(var2, var4 + 1.0F, var5 + 1.0F, var6, var3, this.handle(0, var8));
         var1.handle(var2, var4, var5, var6, var3, var7);
      }
   }

   private EntityOverlayRenderer.PrimaryBounds handle(FontObject var1, String var2, float var3) {
      SdfTextRenderer.State var4 = RoundedRectRenderer.handle(var1, var2, var3);
      return new EntityOverlayRenderer.PrimaryBounds(var4.instance, var4.data);
   }

   private float process(FontObject var1, String var2, float var3) {
      return RoundedRectRenderer.handle(var1, var2, var3).instance;
   }

   private float compute(FontObject var1, String var2, float var3) {
      return RoundedRectRenderer.handle(var1, var2, var3).data;
   }

   private float handle(float var1, float var2, float var3) {
      return var1 + (var2 - var3) * 0.5F + var3 * 0.72F;
   }

   private float handle(float var1, float var2) {
      return var1 + 29.0F * var2 + 7.0F * var2 + 7.2F * var2;
   }

   private float handle(EntityOverlayRenderer.Bounds var1) {
      float var2 = this.compute(0.08F, 0.88F, var1.content);
      float var3 = this.compute(0.04F, 0.58F, var1.appear);
      return this.process(Math.max(var2, var3 * 0.74F));
   }

   private float process(EntityOverlayRenderer.Bounds var1) {
      return this.handle(var1, this.handle(var1));
   }

   private float handle(EntityOverlayRenderer.Bounds var1, float var2) {
      return this.compute(0.1F, 0.92F, var1.detail) * (0.56F + 0.44F * var2);
   }

   private float handle(EntityOverlayRenderer.Bounds var1, EntityOverlayRenderer.Mode var2) {
      float var3 = this.handle(var1);

      return switch (var2) {
         case DIRECT -> var3;
         case GHOST -> this.process(0.18F + var3 * 0.52F);
         case OVERLAY -> this.process(0.62F + var3 * 0.38F);
      };
   }

   private float handle(EntityOverlayRenderer.FileEntry var1, float var2, float var3) {
      return !var1.sourceBuild.isEmpty() && !(var2 <= 0.02F) ? 26.0F * var3 * this.compute(0.1F, 0.72F, var2) : 0.0F;
   }

   private float compute(EntityOverlayRenderer.Bounds var1) {
      return var1.y - var1.topExtension + 2.0F * var1.scale;
   }

   private float handle(int var1) {
      return var1 > 12 ? 10.0F : 13.0F;
   }

   private List<EntityOverlayRenderer.Bounds> handle(List<EntityOverlayRenderer.Bounds> var1, int var2) {
      if (var1.isEmpty()) {
         return var1;
      }

      ArrayList<EntityOverlayRenderer.Bounds> var3 = new ArrayList<>(var1);
      var3.sort(Comparator.comparingDouble(EntityOverlayRenderer.Bounds::y).reversed().thenComparingDouble(EntityOverlayRenderer.Bounds::distance));
      ArrayList<EntityOverlayRenderer.Bounds> var4 = new ArrayList<>(var3.size());

      for (EntityOverlayRenderer.Bounds var6 : var3) {
         float var7 = 14.0F * var6.scale;
         float var8 = var6.y;

         boolean var9;
         do {
            var9 = false;

            for (EntityOverlayRenderer.Bounds var11 : var4) {
               if (this.handle(var6, var11)
                  && this.handle(var6.x, var6.width, var11.x, var11.width, var7 * 0.45F)
                  && this.process(
                     var8 - var6.topExtension, var6.height + var6.topExtension, var11.y - var11.topExtension, var11.height + var11.topExtension, var7 * 0.25F
                  )) {
                  var8 = var11.y - var11.topExtension - var6.height - var7;
                  var9 = true;
               }
            }
         } while (var9);

         float var14 = 8.0F + var6.topExtension;
         float var15 = Math.max(var14, var2 - var6.height - 8.0F);
         float var12 = this.update(var8, var14, var15);
         float var13 = this.update(var6.y + var6.state.renderer.handle(var12 - var6.y, selection), var14, var15);
         var4.add(var6.withY(var13));
      }

      return var4;
   }

   private boolean handle(EntityOverlayRenderer.Bounds var1, EntityOverlayRenderer.Bounds var2) {
      float var3 = Math.abs(var1.state.handler.depth() - var2.state.handler.depth());
      double var4 = Math.min(var1.distance(), var2.distance());
      double var6 = Math.max(var1.distance(), var2.distance());
      double var8 = var6 / Math.max(0.001, var4);
      double var10 = var6 - var4;
      return var3 <= 0.085F || var8 <= 1.32F || var10 <= 4.0;
   }

   private boolean handle(float var1, float var2, float var3, float var4, float var5) {
      return var1 < var3 + var4 + var5 && var1 + var2 + var5 > var3;
   }

   private boolean process(float var1, float var2, float var3, float var4, float var5) {
      return var1 < var3 + var4 + var5 && var1 + var2 + var5 > var3;
   }

   private boolean resolve(EntityOverlayRenderer.FileEntry var1) {
      return !var1.animationSchedule.text.isEmpty() || !var1.rendererScan.text.isEmpty();
   }

   private float handle(List<EntityOverlayRenderer.PrimaryDataRecord> var1, float var2, float var3) {
      return var1.isEmpty() ? 0.0F : var1.size() * var2 + Math.max(0, var1.size() - 1) * var3;
   }

   private float process(float var1, float var2, float var3) {
      return this.compute(var2, var2 + var3, var1);
   }

   private float compute(float var1, float var2, float var3) {
      float var4 = this.process((var3 - var1) / Math.max(1.0E-5F, var2 - var1));
      return var4 * var4 * (3.0F - 2.0F * var4);
   }

   private float handle(float var1, long var2, int var4) {
      float var5 = this.process((0.2F - var1) / 0.2F);
      if (var5 <= 0.0F) {
         return 0.0F;
      }

      float var6 = 0.5F + 0.5F * (float)Math.sin(this.handle(var2) * 9.4F + var4 * 0.173F);
      return var5 * (0.58F + 0.42F * var6);
   }

   private float handle(long var1, long var3) {
      if (var1 >= 0L && var1 < var3) {
         float var5 = 1.0F - (float)var1 / (float)var3;
         return var5 * var5 * (3.0F - 2.0F * var5);
      } else {
         return 0.0F;
      }
   }

   private float handle(long var1) {
      return (float)(var1 % 1000000L) / 1000.0F;
   }

   private String process(int var1) {
      return switch (Math.max(1, Math.min(10, var1))) {
         case 1 -> "I";
         case 2 -> "II";
         case 3 -> "III";
         case 4 -> "IV";
         case 5 -> "V";
         case 6 -> "VI";
         case 7 -> "VII";
         case 8 -> "VIII";
         case 9 -> "IX";
         default -> "X";
      };
   }

   private int handle(int var1, float var2) {
      int var3 = Math.max(0, Math.min(255, Math.round(this.process(var2) * 255.0F)));
      return var3 << 24 | var1 & 16777215;
   }

   private int handle(int var1, int var2, float var3) {
      float var4 = this.process(var3);
      int var5 = Math.round((var1 >> 16 & 0xFF) + ((var2 >> 16 & 0xFF) - (var1 >> 16 & 0xFF)) * var4);
      int var6 = Math.round((var1 >> 8 & 0xFF) + ((var2 >> 8 & 0xFF) - (var1 >> 8 & 0xFF)) * var4);
      int var7 = Math.round((var1 & 0xFF) + ((var2 & 0xFF) - (var1 & 0xFF)) * var4);
      return var5 << 16 | var6 << 8 | var7;
   }

   private int process(int var1, float var2) {
      Color var3 = new Color(var1);
      int var4 = Math.max(0, Math.min(255, Math.round(var3.getRed() * var2)));
      int var5 = Math.max(0, Math.min(255, Math.round(var3.getGreen() * var2)));
      int var6 = Math.max(0, Math.min(255, Math.round(var3.getBlue() * var2)));
      return var4 << 16 | var5 << 8 | var6;
   }

   private float resolve(float var1, float var2, float var3) {
      return var1 + (var2 - var1) * this.process(var3);
   }

   private float update(float var1, float var2, float var3) {
      return Math.max(var2, Math.min(var3, var1));
   }

   private float process(float var1) {
      return this.update(var1, 0.0F, 1.0F);
   }

   private float compute(float var1) {
      return Math.round(var1);
   }

   private float process(float var1, float var2) {
      return var2 <= 0.0F ? var1 : Math.round(var1 / var2) * var2;
   }

   record Bounds(
      EntityOverlayRenderer.FileEntry state,
      float x,
      float y,
      float width,
      float height,
      float scale,
      float topExtension,
      float alpha,
      float appear,
      float content,
      float detail,
      float itemReveal,
      float focus,
      float health,
      float damage,
      float threat,
      float exposure,
      boolean shader
   ) {

      public double distance() {
         return this.state.handler.distance();
      }

      public EntityOverlayRenderer.Bounds withY(float var1) {
         return new EntityOverlayRenderer.Bounds(
            this.state,
            this.x,
            var1,
            this.width,
            this.height,
            this.scale,
            this.topExtension,
            this.alpha,
            this.appear,
            this.content,
            this.detail,
            this.itemReveal,
            this.focus,
            this.health,
            this.damage,
            this.threat,
            this.exposure,
            this.shader
         );
      }
   }

   record ColorStop(String label, int color, boolean harmful, int duration) {
   }

   record DataRecord(float screenX, float screenY, double distance, float projectedHeight, float depth) {
   }

   static final class FileEntry {
      final UUID instance;
      final int data;
      final DampedFloatTracker context = new DampedFloatTracker(0.0F);
      final DampedFloatTracker config = new DampedFloatTracker(0.0F);
      final DampedFloatTracker state = new DampedFloatTracker(0.0F);
      final DampedFloatTracker cache = new DampedFloatTracker(0.0F);
      final DampedFloatTracker output = new DampedFloatTracker(92.0F);
      final DampedFloatTracker current = new DampedFloatTracker(29.0F);
      final DampedFloatTracker active = new DampedFloatTracker(1.0F);
      final DampedFloatTracker mode = new DampedFloatTracker(1.0F);
      final DampedFloatTracker selection = new DampedFloatTracker(0.0F);
      final DampedFloatTracker enabled = new DampedFloatTracker(0.0F);
      final DampedFloatTracker renderer = new DampedFloatTracker(0.0F);
      EntityOverlayRenderer.DataRecord handler;
      boolean animationDraw;
      boolean pointEncode;
      boolean animator;
      boolean source;
      boolean target;
      boolean pending;
      boolean previous;
      long latest;
      long summary;
      long matrixBlend;
      long vectorMatch = Long.MIN_VALUE;
      int itemProject;
      float responseCompute = Float.MAX_VALUE;
      float providerFetch = 1.0F;
      float profileDraw = 92.0F;
      float vectorPerform = 92.0F;
      float eventAttach = 29.0F;
      float serverRead = 29.0F;
      float positionAdvance;
      float frameCheck;
      float moduleCollect;
      float providerClose;
      String presetSave = "";
      String windowConvert = "";
      int presetWrite = 16734824;
      String colorMeasure = "20 HP";
      EntityOverlayRenderer.PrimaryColorStop animationSchedule = new EntityOverlayRenderer.PrimaryColorStop("", 15133941);
      EntityOverlayRenderer.PrimaryColorStop rendererScan = new EntityOverlayRenderer.PrimaryColorStop("", 12371672);
      List<EntityOverlayRenderer.PrimaryDataRecord> sourceBuild = List.of();
      List<EntityOverlayRenderer.ColorStop> outputCollapse = List.of();

      private FileEntry(UUID var1) {
         this.instance = var1;
         this.data = var1.hashCode();
      }

      void handle() {
         this.pointEncode = false;
         this.animator = false;
         this.source = false;
         this.target = false;
         this.pending = false;
      }

      boolean handle(long var1) {
         return !this.pointEncode
            && this.context.process(0.0F, EntityOverlayRenderer.data)
            && this.config.process(0.0F, EntityOverlayRenderer.context)
            && this.state.process(0.0F, EntityOverlayRenderer.config)
            && this.cache.process(0.0F, EntityOverlayRenderer.state)
            && var1 - this.latest > 180L;
      }
   }

   enum Mode {
      DIRECT,
      GHOST,
      OVERLAY;
   }

   record PrimaryBounds(float width, float height) {
   }

   record PrimaryColorStop(String text, int color) {

      PrimaryColorStop(String text, int color) {
         text = Objects.requireNonNullElse(text, "");
         this.text = text;
         this.color = color;
      }
   }

   record PrimaryDataRecord(ItemStack stack, EquipmentSlot slot) {
   }

   record SecondaryDataRecord(
      int accentTop,
      int accentBottom,
      int shellTop,
      int shellBottom,
      int rim,
      int textPrimary,
      int textSecondary,
      int avatarBackdrop,
      int slotFill,
      int barTrack,
      int divider,
      int danger,
      int dangerGlow,
      int safeGlow
   ) {
   }
}
