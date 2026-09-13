package ru.wild.modules.visuals;

import java.util.ArrayList;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.PlayerMarkerModule;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.core.manager.FriendManager;
import ru.wild.modules.combat.AttackAura;
import ru.wild.network.IrcClient;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.AnimationDirection;
import ru.wild.util.math.EaseTimer;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.math.SmoothTimer;
import ru.wild.util.player.MovementPhysics;
import ru.wild.util.render.GuiScaleMetrics;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "Arrows", description = "Показывает игроков через стрелочки", category = ModuleCategory.Visuals)
public class Arrows extends Module {
   private static final Identifier vectorPerform = Identifier.of("wild", "textures/arrows/arrows.png");
   public static final BooleanSetting source = new BooleanSetting("Показ дистанции", true);
   public static final BooleanSetting target = new BooleanSetting("Показ игроков с бронёй", true);
   public static final BooleanSetting pending = new BooleanSetting("Выделять таргета", true);
   public static final BooleanSetting previous = new BooleanSetting("Анимировать", true);
   public static final NumberSetting latest = new NumberSetting("Размер", 10.0F, 1.0F, 100.0F, 1.0F, false);
   public static final NumberSetting summary = new NumberSetting("Дистанция от центра", 150.0F, 80.0F, 300.0F, 5.0F, false);
   public static final BooleanSetting matrixBlend = new BooleanSetting("Сортировка по дистанции", false);
   public static final BooleanSetting vectorMatch = new BooleanSetting("Только друзья", false);
   public static final BooleanSetting itemProject = new BooleanSetting("Мерцать", true).handle(() -> !source.compute());
   public static LivingEntity responseCompute;
   public ArrayList<Arrows.ColorState> providerFetch = new ArrayList<>();
   public ArrayList<Arrows.NetworkState> profileDraw = new ArrayList<>();

   public Arrows() {
      this.handle(source, target, pending, previous, latest, summary, matrixBlend, vectorMatch, itemProject);
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (AttackAura.textureRun != null) {
            responseCompute = AttackAura.textureRun;
         }

         if (responseCompute != null && (!responseCompute.isAlive() || !Module.client.world.getPlayers().contains(responseCompute))) {
            responseCompute = null;
         }

         if (Module.client.world.getPlayers() != null) {
            for (Entity var3 : Module.client.world.getPlayers()) {
               if (var3 != null && var3 != Module.client.player) {
                  boolean var4 = false;

                  for (Arrows.ColorState var6 : this.providerFetch) {
                     if (var6.data == var3) {
                        var4 = true;
                        break;
                     }
                  }

                  if (!var4) {
                     this.providerFetch.add(new Arrows.ColorState(var3));
                  }
               }
            }
         }

         for (Arrows.ColorState var13 : this.providerFetch) {
            var13.handle(var1.resolve());
         }

         this.providerFetch.removeIf(var0 -> var0.instance.apply() != AnimationDirection.FORWARDS && var0.instance.check() == 0.0F);
         PlayerMarkerModule var12 = WildClient.instance.data != null ? WildClient.instance.data.handle(PlayerMarkerModule.class) : null;
         if (pending.compute() && var12 != null && var12.latest.compute() && var12.enabled) {
            String var14 = IrcClient.handle();
            ArrayList<String> var16 = new ArrayList<>();

            for (IrcClient.PrimaryCacheEntry var20 : IrcClient.config.values()) {
               if (var20.data.equals(var14)) {
                  var16.add(var20.instance);
               }
            }

            if (PlayerMarkerModule.matrixBlend != null && !PlayerMarkerModule.matrixBlend.isEmpty() && !var16.contains(PlayerMarkerModule.matrixBlend)) {
               var16.add(PlayerMarkerModule.matrixBlend);
            }

            for (String var21 : var16) {
               boolean var7 = Module.client.world
                  .getPlayers()
                  .stream()
                  .anyMatch(var1x -> var1x.getName().getString().equalsIgnoreCase(var21) && var1x != Module.client.player);
               if (!var7) {
                  boolean var8 = false;

                  for (Arrows.NetworkState var10 : this.profileDraw) {
                     if (var10.data.equalsIgnoreCase(var21)) {
                        var8 = true;
                        break;
                     }
                  }

                  if (!var8) {
                     this.profileDraw.add(new Arrows.NetworkState(var21));
                  }
               }
            }
         }

         for (Arrows.NetworkState var17 : this.profileDraw) {
            var17.handle(var1.resolve());
         }

         this.profileDraw.removeIf(var0 -> var0.instance.apply() != AnimationDirection.FORWARDS && var0.instance.check() == 0.0F);
      }
   }

   static int refresh() {
      if (Module.client != null && Module.client.getTextureManager() != null) {
         AbstractTexture var0 = Module.client.getTextureManager().getTexture(vectorPerform);
         if (var0 == null) {
            return -1;
         } else if (var0.getGlTexture() instanceof GlTexture var2) {
            int var3 = var2.getGlId();
            return var3 > 0 ? var3 : -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   static int handle(int var0, int var1) {
      return PackedColor.update(var0 | 0xFF000000, var1);
   }

   static float handle(String var0) {
      return -RoundedRectRenderer.handle(FontRegistry.config, var0, 20.0F).instance / 2.0F;
   }

   static double handle(double var0) {
      return !matrixBlend.compute() ? 0.0 : Math.min(summary.config * 0.85, Math.max(0.0, var0 * 0.65));
   }

   public static class ColorState {
      SmoothTimer instance = new EaseTimer(300, 1.0);
      Entity data;
      float context;
      float config;
      float state;
      float cache;

      public ColorState(Entity var1) {
         this.data = var1;
      }

      public void handle() {
         if (Module.client.world != null && Module.client.player != null) {
            boolean var1 = Module.client.world.getPlayers().contains(this.data);
            boolean var2 = this.data.isAlive();
            boolean var3 = this.data == Module.client.player;
            boolean var4 = var1 && var2 && !var3;
            if (var4 && Arrows.vectorMatch.compute()) {
               var4 = this.data instanceof PlayerEntity var5 && FriendManager.handle(var5.getName().getString());
            }

            if (var4 && Arrows.target.compute() && this.data instanceof PlayerEntity var14) {
               boolean var16 = false;
               EquipmentSlot[] var7 = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

               for (EquipmentSlot var11 : var7) {
                  ItemStack var12 = var14.getEquippedStack(var11);
                  if (var12 != null && !var12.isEmpty()) {
                     String var13 = var12.getItem().toString().toUpperCase();
                     if (var13.contains("DIAMOND") || var13.contains("NETHERITE")) {
                        var16 = true;
                        break;
                     }
                  }
               }

               if (!var16) {
                  var4 = false;
               }
            }

            this.instance.process(var4 ? AnimationDirection.FORWARDS : AnimationDirection.BACKWARDS);
         }
      }

      public void handle(RoundedRectRenderer var1) {
         this.handle();
         GuiScaleMetrics var2 = new GuiScaleMetrics(Module.client);
         float[] var3 = MovementPhysics.process();
         float var4 = var3[0];
         float var5 = var3[1];
         if (Arrows.previous.compute()) {
            this.config = NumericTransform.select(this.config, var5 * 10.0F, 5.0F);
            this.state = NumericTransform.select(this.state, var4 * 10.0F, 5.0F);
         } else {
            this.config = 0.0F;
            this.state = 0.0F;
         }

         float var6 = ViewRotationCoordinator.instance ? Module.client.gameRenderer.getCamera().getYaw() : ViewRotationCoordinator.context;
         this.cache = NumericTransform.select(this.cache, var6, 10.0F);
         boolean var7 = Arrows.pending.compute() && Arrows.responseCompute != null && this.data.equals(Arrows.responseCompute);
         if (!var7 && Arrows.pending.compute()) {
            PlayerMarkerModule var8 = WildClient.instance.data != null ? WildClient.instance.data.handle(PlayerMarkerModule.class) : null;
            if (var8 != null && var8.latest.compute() && var8.enabled) {
               String var9 = this.data.getName().getString();
               String var10 = IrcClient.handle();

               for (IrcClient.PrimaryCacheEntry var12 : IrcClient.config.values()) {
                  if (var12.data.equals(var10) && var12.instance.equals(var9)) {
                     var7 = true;
                     break;
                  }
               }

               if (PlayerMarkerModule.matrixBlend != null && PlayerMarkerModule.matrixBlend.equals(var9)) {
                  var7 = true;
               }
            }
         }

         float var43 = var7 ? 1.5F : 1.0F;
         float var44 = this.instance.check() * (Arrows.summary.config * var43);
         if (Module.client.currentScreen instanceof GenericContainerScreen) {
            var44 += 200.0F;
         }

         if (Module.client.currentScreen instanceof InventoryScreen) {
            var44 += 180.0F;
         }

         if (Arrows.previous.compute() && (process() || Module.client.player.isInSneakingPose() || Module.client.player.isSwimming())
            || Module.client.currentScreen instanceof ChatScreen) {
            var44 += 90.0F;
         }

         this.context = Arrows.previous.compute() ? NumericTransform.select(this.context, var44, 6.0F) : var44;
         double var45 = this.data.lastX
            + (this.data.getX() - this.data.lastX) * Module.client.gameRenderer.getCamera().getLastTickProgress()
            - Module.client.gameRenderer.getCamera().getPos().x;
         double var46 = this.data.lastY
            + (this.data.getY() - this.data.lastY) * Module.client.gameRenderer.getCamera().getLastTickProgress()
            + this.data.getHeight() / 2.0F
            - Module.client.gameRenderer.getCamera().getPos().y
            - Module.client.player.getEyeHeight(Module.client.player.getPose());
         double var14 = this.data.lastZ
            + (this.data.getZ() - this.data.lastZ) * Module.client.gameRenderer.getCamera().getLastTickProgress()
            - Module.client.gameRenderer.getCamera().getPos().z;
         double var16 = Math.sqrt(var45 * var45 + var46 * var46 + var14 * var14);
         double var18 = MathHelper.cos((float)(this.cache * (Math.PI / 180.0)));
         double var20 = MathHelper.sin((float)(this.cache * (Math.PI / 180.0)));
         double var22 = -(var14 * var18 - var45 * var20);
         double var24 = -(var45 * var18 + var14 * var20);
         double var26 = Math.atan2(var22, var24) * 180.0 / Math.PI;
         double var28 = this.context + Arrows.handle(var16) * this.instance.check();
         double var30 = Math.min(1.0, var16 / 20.0);
         double var32 = var28 * MathHelper.cos((float)Math.toRadians(var26)) + var2.compute();
         double var34 = var28 * MathHelper.sin((float)Math.toRadians(var26)) + var2.resolve();
         var32 += this.config;
         var34 += this.state + var30;
         int var36 = Arrows.refresh();
         if (var36 > 0) {
            int var37;
            if (var7) {
               var37 = PackedColor.instance;
            } else if (this.data instanceof AbstractClientPlayerEntity var38 && FriendManager.handle(var38.getNameForScoreboard())) {
               var37 = PackedColor.data;
            } else {
               var37 = PackedColor.handle();
            }

            int var49 = (int)(this.instance.check() * 255.0F);
            if (Arrows.source.compute() && Arrows.itemProject.compute() && var16 > 50.0) {
               long var50 = System.currentTimeMillis() % 5000L;
               if (var50 > 2500L) {
                  var49 = 0;
               }
            }

            if (var49 > 5) {
               var1.handle((float)var32, (float)var34);
               var1.process((float)(var26 + 90.0));
               float var51 = Arrows.latest.config * 2.0F;
               var1.handle(var36, -var51 / 2.0F, -var51 / 2.0F, var51, var51, Arrows.handle(var37, var49), false);
               var1.prepare();
               if (Arrows.source.compute()) {
                  String var40;
                  if (var16 > 100.0) {
                     var40 = "100+";
                  } else {
                     var40 = (int)var16 + "m";
                  }

                  float var41 = Arrows.handle(var40);
                  float var42 = Arrows.latest.config + 8.0F;
                  var1.handle(FontRegistry.config, var41, var42, 20.0F, var40, PackedColor.compute(255, 255, 255, var49));
               }

               var1.prepare();
            }
         }
      }

      public static boolean process() {
         float[] var0 = MovementPhysics.process();
         return var0[0] != 0.0F || var0[1] != 0.0F;
      }
   }

   public static class NetworkState {
      SmoothTimer instance = new EaseTimer(300, 1.0);
      String data;
      float context;
      float config;
      float state;
      float cache;

      public NetworkState(String var1) {
         this.data = var1;
      }

      public void handle(RoundedRectRenderer var1) {
         String var2 = IrcClient.handle();
         double var3 = 0.0;
         double var5 = 0.0;
         double var7 = 0.0;
         boolean var9 = false;
         long var10 = 0L;

         for (IrcClient.PrimaryCacheEntry var13 : IrcClient.config.values()) {
            if (var13.instance.equalsIgnoreCase(this.data) && var13.data.equals(var2)) {
               long var14 = System.currentTimeMillis() - var13.active;
               double var16 = MathHelper.clamp(var14 / 200.0, 0.0, 1.0);
               var3 = MathHelper.lerp(var16, var13.cache, var13.context);
               var5 = MathHelper.lerp(var16, var13.output, var13.config);
               var7 = MathHelper.lerp(var16, var13.current, var13.state);
               var10 = var13.active;
               var9 = true;
               break;
            }
         }

         if (!var9 && this.data.equalsIgnoreCase(PlayerMarkerModule.matrixBlend)) {
            var3 = PlayerMarkerModule.vectorMatch;
            var5 = PlayerMarkerModule.itemProject;
            var7 = PlayerMarkerModule.responseCompute;
            var10 = System.currentTimeMillis();
            var9 = true;
         }

         long var55 = System.currentTimeMillis() - var10;
         boolean var56 = var9 && (var55 < 4000L || this.data.equalsIgnoreCase(PlayerMarkerModule.matrixBlend));
         this.instance.process(var56 ? AnimationDirection.FORWARDS : AnimationDirection.BACKWARDS);
         if (this.instance.check() != 0.0F) {
            GuiScaleMetrics var15 = new GuiScaleMetrics(Module.client);
            float[] var57 = MovementPhysics.process();
            float var17 = var57[0];
            float var18 = var57[1];
            if (Arrows.previous.compute()) {
               this.config = NumericTransform.select(this.config, var18 * 10.0F, 5.0F);
               this.state = NumericTransform.select(this.state, var17 * 10.0F, 5.0F);
            } else {
               this.config = 0.0F;
               this.state = 0.0F;
            }

            float var19 = ViewRotationCoordinator.instance ? Module.client.gameRenderer.getCamera().getYaw() : ViewRotationCoordinator.context;
            this.cache = NumericTransform.select(this.cache, var19, 10.0F);
            float var20 = 1.5F;
            float var21 = this.instance.check() * (Arrows.summary.config * var20);
            if (Module.client.currentScreen instanceof GenericContainerScreen) {
               var21 += 200.0F;
            }

            if (Module.client.currentScreen instanceof InventoryScreen) {
               var21 += 180.0F;
            }

            if (Arrows.previous.compute()
                  && (Arrows.ColorState.process() || Module.client.player.isInSneakingPose() || Module.client.player.isSwimming())
               || Module.client.currentScreen instanceof ChatScreen) {
               var21 += 90.0F;
            }

            this.context = Arrows.previous.compute() ? NumericTransform.select(this.context, var21, 6.0F) : var21;
            double var22 = var3 - Module.client.gameRenderer.getCamera().getPos().x;
            double var24 = var5
               + 1.0
               - Module.client.gameRenderer.getCamera().getPos().y
               - Module.client.player.getEyeHeight(Module.client.player.getPose());
            double var26 = var7 - Module.client.gameRenderer.getCamera().getPos().z;
            double var28 = Math.sqrt(var22 * var22 + var24 * var24 + var26 * var26);
            double var30 = MathHelper.cos((float)(this.cache * (Math.PI / 180.0)));
            double var32 = MathHelper.sin((float)(this.cache * (Math.PI / 180.0)));
            double var34 = -(var26 * var30 - var22 * var32);
            double var36 = -(var22 * var30 + var26 * var32);
            double var38 = Math.atan2(var34, var36) * 180.0 / Math.PI;
            double var40 = this.context + Arrows.handle(var28) * this.instance.check();
            double var42 = Math.min(1.0, var28 / 20.0);
            double var44 = var40 * MathHelper.cos((float)Math.toRadians(var38)) + var15.compute();
            double var46 = var40 * MathHelper.sin((float)Math.toRadians(var38)) + var15.resolve();
            var44 += this.config;
            var46 += this.state + var42;
            int var48 = Arrows.refresh();
            if (var48 > 0) {
               int var49 = PackedColor.instance;
               int var50 = (int)(this.instance.check() * 255.0F);
               if (var55 > 3000L && !this.data.equalsIgnoreCase(PlayerMarkerModule.matrixBlend)) {
                  float var51 = 1.0F - (float)(var55 - 3000L) / 1000.0F;
                  var50 = (int)(var50 * MathHelper.clamp(var51, 0.0F, 1.0F));
               }

               if (Arrows.source.compute() && Arrows.itemProject.compute() && var28 > 50.0) {
                  long var60 = System.currentTimeMillis() % 5000L;
                  if (var60 > 2500L) {
                     var50 = 0;
                  }
               }

               if (var50 > 5) {
                  var1.handle((float)var44, (float)var46);
                  var1.process((float)(var38 + 90.0));
                  float var61 = Arrows.latest.config * 2.0F;
                  var1.handle(var48, -var61 / 2.0F, -var61 / 2.0F, var61, var61, Arrows.handle(var49, var50), false);
                  var1.prepare();
                  if (Arrows.source.compute()) {
                     String var52 = var28 > 300.0 ? "300+" : (int)var28 + "m";
                     float var53 = Arrows.handle(var52);
                     float var54 = Arrows.latest.config + 8.0F;
                     var1.handle(FontRegistry.config, var53, var54, 20.0F, var52, PackedColor.compute(255, 255, 255, var50));
                  }

                  var1.prepare();
               }
            }
         }
      }
   }
}
