package ru.wild.modules.player;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.CameraRotationEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.MouseMotionEvent;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerMotionEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.render.PackedColor;
import ru.wild.util.text.InputNames;

@ModuleRegister(name = "FreeCamera", category = ModuleCategory.Player, description = "Свободная камера", flags = ModuleFlag.RISKY)
public class FreeCamera extends Module {
   private static FreeCamera matrixBlend;
   public final NumberSetting source = new NumberSetting("Скорость", 2.0F, 0.5F, 5.0F, 0.1F, false);
   public final BooleanSetting target = new BooleanSetting("Отменять пакет", false);
   public Vec3d pending;
   public Vec3d previous;
   public float latest;
   public float summary;
   private Vec3d vectorMatch;

   public static FreeCamera refresh() {
      if (matrixBlend == null && WildClient.instance != null && WildClient.instance.data != null) {
         matrixBlend = WildClient.instance.data.handle(FreeCamera.class);
      }

      return matrixBlend;
   }

   public static boolean render() {
      FreeCamera var0 = refresh();
      return var0 != null && var0.enabled;
   }

   public FreeCamera() {
      matrixBlend = this;
      this.handle(this.source, this.target);
   }

   @Override
   public void handle() {
      if (Module.client.player != null && Module.client.world != null) {
         Vec3d var1 = Module.client.getEntityRenderDispatcher().camera != null
            ? Module.client.getEntityRenderDispatcher().camera.getPos()
            : Module.client.player.getEyePos();
         this.previous = this.pending = var1;
         if (Module.client.getEntityRenderDispatcher().camera != null) {
            this.latest = Module.client.getEntityRenderDispatcher().camera.getYaw();
            this.summary = Module.client.getEntityRenderDispatcher().camera.getPitch();
         } else {
            this.latest = Module.client.player.getYaw();
            this.summary = Module.client.player.getPitch();
         }

         this.vectorMatch = null;
         super.handle();
      } else {
         this.toggle();
      }
   }

   @Override
   public void process() {
      this.vectorMatch = null;
      this.pending = null;
      this.previous = null;
      super.process();
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      Packet var2 = var1.resolve();
      if (!(var2 instanceof PlayerRespawnS2CPacket) && !(var2 instanceof GameJoinS2CPacket)) {
         if (var1.compute() && this.target.compute() && var2 instanceof PlayerMoveC2SPacket) {
            var1.process();
         }
      } else {
         this.setEnabled(false);
      }
   }

   @EventHandler
   public void handle(WorldRenderContext var1) {
      if (Module.client.player != null && Module.client.world != null) {
         Vec3d var2 = Module.client.player.getLerpedPos(var1.check());
         Box var3 = Module.client.player.getBoundingBox().offset(var2.subtract(Module.client.player.getPos()));
         this.handle(var1, var3, PackedColor.handle());
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (Module.client.options != null) {
            Module.client.options.setPerspective(Perspective.FIRST_PERSON);
         }

         this.tick();
         this.encodePoint();
      } else {
         this.setEnabled(false);
      }
   }

   private void tick() {
      if (this.pending != null) {
         float var1 = 0.0F;
         float var2 = 0.0F;
         if (InputNames.handle(87)) {
            var1++;
         }

         if (InputNames.handle(83)) {
            var1--;
         }

         if (InputNames.handle(65)) {
            var2++;
         }

         if (InputNames.handle(68)) {
            var2--;
         }

         boolean var3 = InputNames.handle(32);
         boolean var4 = InputNames.handle(340) || InputNames.handle(344);
         float var5 = this.source.compute();
         double[] var6 = this.handle(var1, var2, this.latest, var5);
         this.previous = this.pending;
         this.pending = this.pending.add(var6[0], var3 ? var5 : (var4 ? -var5 : 0.0), var6[1]);
      }
   }

   private double[] handle(float var1, float var2, float var3, double var4) {
      if (var1 != 0.0F) {
         if (var2 > 0.0F) {
            var3 += var1 > 0.0F ? -45.0F : 45.0F;
         } else if (var2 < 0.0F) {
            var3 += var1 > 0.0F ? 45.0F : -45.0F;
         }

         var2 = 0.0F;
         var1 = var1 > 0.0F ? 1.0F : -1.0F;
      }

      double var6 = Math.sin(Math.toRadians(var3 + 90.0F));
      double var8 = Math.cos(Math.toRadians(var3 + 90.0F));
      return new double[]{var1 * var4 * var8 + var2 * var4 * var6, var1 * var4 * var6 - var2 * var4 * var8};
   }

   @EventHandler
   public void handle(MouseMotionEvent var1) {
      if (Module.client.player != null && Module.client.world != null && this.pending != null) {
         this.latest = this.latest + (float)var1.compute() * 0.15F;
         this.summary = NumericTransform.onTick(this.summary + (float)var1.resolve() * 0.15F, -90.0F, 90.0F);
         var1.process();
      }
   }

   @EventHandler
   public void handle(CameraRotationEvent var1) {
      if (Module.client.player != null && Module.client.world != null && this.pending != null) {
         var1.handle(this.latest);
         var1.process(this.summary);
      }
   }

   @EventHandler
   public void handle(PlayerMotionEvent var1) {
      if (Module.client.player != null && this.target.compute()) {
         if (this.vectorMatch == null) {
            this.vectorMatch = Module.client.player.getPos();
         }

         var1.handle(this.vectorMatch.x);
         var1.process(this.vectorMatch.y);
         var1.compute(this.vectorMatch.z);
         Module.client.player.setVelocity(Vec3d.ZERO);
         Module.client.player.fallDistance = 0.0;
      }
   }

   @EventHandler
   public void handle(MovementInputEvent var1) {
      if (Module.client.player != null && Module.client.world != null && this.pending != null) {
         if (!this.drawAnimation()) {
            var1.handle(0.0F);
            var1.process(0.0F);
            var1.handle(false);
            var1.process(false);
         }
      }
   }

   private boolean drawAnimation() {
      try {
         IBaritone var1 = BaritoneAPI.getProvider().getPrimaryBaritone();
         return var1.getPathingBehavior().isPathing() || var1.getCustomGoalProcess() != null && var1.getCustomGoalProcess().isActive();
      } catch (Throwable var2) {
         return false;
      }
   }

   public Vec3d handle(float var1) {
      if (this.enabled && this.previous != null && this.pending != null) {
         if (Module.client.options != null) {
            Module.client.options.setPerspective(Perspective.FIRST_PERSON);
         }

         return this.previous.lerp(this.pending, var1);
      } else {
         return null;
      }
   }

   private void encodePoint() {
      if (Module.client.player != null) {
         if (!this.target.compute()) {
            this.vectorMatch = null;
         } else {
            if (this.vectorMatch == null) {
               this.vectorMatch = Module.client.player.getPos();
            }

            Module.client.player.setVelocity(Vec3d.ZERO);
            Module.client.player.fallDistance = 0.0;
            Module.client.player
               .refreshPositionAndAngles(this.vectorMatch.x, this.vectorMatch.y, this.vectorMatch.z, Module.client.player.getYaw(), Module.client.player.getPitch());
            Module.client.player.lastX = this.vectorMatch.x;
            Module.client.player.lastY = this.vectorMatch.y;
            Module.client.player.lastZ = this.vectorMatch.z;
         }
      }
   }

   private void handle(WorldRenderContext var1, Box var2, int var3) {
      int var4 = PackedColor.handle(var3, 220);
      Vec3d var5 = new Vec3d(var2.minX, var2.minY, var2.minZ);
      Vec3d var6 = new Vec3d(var2.maxX, var2.maxY, var2.maxZ);
      Vec3d var7 = new Vec3d(var5.x, var5.y, var5.z);
      Vec3d var8 = new Vec3d(var5.x, var5.y, var6.z);
      Vec3d var9 = new Vec3d(var5.x, var6.y, var5.z);
      Vec3d var10 = new Vec3d(var5.x, var6.y, var6.z);
      Vec3d var11 = new Vec3d(var6.x, var5.y, var5.z);
      Vec3d var12 = new Vec3d(var6.x, var5.y, var6.z);
      Vec3d var13 = new Vec3d(var6.x, var6.y, var5.z);
      Vec3d var14 = new Vec3d(var6.x, var6.y, var6.z);
      var1.update().handle(var7, var11, 1.0, var4, false);
      var1.update().handle(var11, var12, 1.0, var4, false);
      var1.update().handle(var12, var8, 1.0, var4, false);
      var1.update().handle(var8, var7, 1.0, var4, false);
      var1.update().handle(var9, var13, 1.0, var4, false);
      var1.update().handle(var13, var14, 1.0, var4, false);
      var1.update().handle(var14, var10, 1.0, var4, false);
      var1.update().handle(var10, var9, 1.0, var4, false);
      var1.update().handle(var7, var9, 1.0, var4, false);
      var1.update().handle(var11, var13, 1.0, var4, false);
      var1.update().handle(var12, var14, 1.0, var4, false);
      var1.update().handle(var8, var10, 1.0, var4, false);
   }
}
