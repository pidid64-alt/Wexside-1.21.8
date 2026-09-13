package ru.wild.modules.combat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.core.manager.FriendManager;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.player.SyntheticKeyState;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(name = "TargetPearl", description = "Кидает эндер-перл вслед за перлом ближайшего игрока", category = ModuleCategory.Combat)
public class TargetPearl extends Module {
   private static final double source = 0.03;
   private static final double target = 0.99;
   private static final double pending = 0.8;
   private static final double previous = 1.5;
   private static final int latest = 240;
   private static final int summary = 160;
   private static final String matrixBlend = "TargetPearl";
   private final NumberSetting vectorMatch = new NumberSetting("Радиус реакции", 48.0F, 8.0F, 128.0F, 1.0F, false);
   private final BooleanSetting itemProject = new BooleanSetting("Использовать ротацию", true);
   private final NumberSetting responseCompute = new NumberSetting("Скорость поворота", 40.0F, 5.0F, 180.0F, 1.0F, false)
      .handle(() -> !this.itemProject.compute());
   private final NumberSetting providerFetch = new NumberSetting("Точность прицела", 2.5F, 0.5F, 10.0F, 0.1F, false).handle(() -> !this.itemProject.compute());
   private final NumberSetting profileDraw = new NumberSetting("Макс. промах (блоки)", 2.5F, 0.5F, 8.0F, 0.1F, false);
   private final NumberSetting vectorPerform = new NumberSetting("Задержка броска (мс)", 600.0F, 0.0F, 3000.0F, 50.0F, false);
   private final BooleanSetting eventAttach = new BooleanSetting("Только из хотбара", false);
   private final BooleanSetting serverRead = new BooleanSetting("Только из инвентаря", false);
   private final BooleanSetting positionAdvance = new BooleanSetting("Игнорировать друзей", true);
   private final BooleanSetting frameCheck = new BooleanSetting("Требовать владельца", false);
   private static final double moduleCollect = 3.0;
   private final Set<Integer> providerClose = new HashSet<>();
   private final Set<Integer> presetSave = new HashSet<>();
   private final Map<Integer, Vec3d> windowConvert = new HashMap<>();
   private final Map<Integer, Vec3d> presetWrite = new HashMap<>();
   private long colorMeasure;
   private int animationSchedule = -1;
   private int rendererScan;
   private int sourceBuild;
   private int outputCollapse = -1;
   private int profileInvoke = -1;
   private float sourceSchedule;
   private float timerRender;
   private float scaleSave;
   private float colorCompute;

   public TargetPearl() {
      this.handle(
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

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         this.encodePoint();
         if (this.rendererScan > 0) {
            this.render();
         } else {
            this.save();
            EnderPearlEntity var2 = this.animate();
            if (var2 != null) {
               Vec3d var3 = this.handle(var2, this.handle(var2));
               if (var3 != null) {
                  Vec3d var4 = Module.client.player.getEyePos().subtract(0.0, 0.1, 0.0);
                  TargetPearl.State var5 = this.handle(var4, var3);
                  if (var5 != null && !(var5.context > this.profileDraw.compute())) {
                     if (this.itemProject.compute()) {
                        float var6 = this.responseCompute.compute();
                        RotationController.handle(new RotationAngles(var5.instance, var5.data), var6, var6, 6, 5);
                     }

                     if (!this.providerClose.contains(var2.getId())) {
                        if (System.currentTimeMillis() - this.colorMeasure >= (long)this.vectorPerform.compute()) {
                           if (this.itemProject.compute()) {
                              float var7 = new RotationAngles(Module.client.player).handle(new RotationAngles(var5.instance, var5.data));
                              if (var7 > this.providerFetch.compute()) {
                                 return;
                              }
                           }

                           int var8 = this.load();
                           if (var8 != -1) {
                              this.handle(var8, var5, var2.getId());
                              this.providerClose.add(var2.getId());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void handle(int var1, TargetPearl.State var2, int var3) {
      this.outputCollapse = var1;
      this.animationSchedule = var3;
      this.sourceSchedule = var2.instance;
      this.timerRender = var2.data;
      this.profileInvoke = -1;
      this.sourceBuild = 0;
      this.rendererScan = 1;
      this.render();
   }

   private void refresh() {
      if (this.animationSchedule != -1 && Module.client.world != null) {
         if (Module.client.world.getEntityById(this.animationSchedule) instanceof EnderPearlEntity var1) {
            Vec3d var5 = this.handle(var1, this.handle(var1));
            if (var5 != null) {
               Vec3d var3 = Module.client.player.getEyePos().subtract(0.0, 0.1, 0.0);
               TargetPearl.State var4 = this.handle(var3, var5);
               if (var4 != null && var4.context <= this.profileDraw.compute()) {
                  this.sourceSchedule = var4.instance;
                  this.timerRender = var4.data;
               }
            }
         }
      }
   }

   private void render() {
      boolean var1 = this.handle(this.outputCollapse);
      if (!var1) {
         this.sourceBuild = 2;
         Module.client.options.sprintKey.setPressed(false);
         Module.client.player.setSprinting(false);
         SyntheticKeyState.handle().handle("TargetPearl");
      }

      if (this.sourceBuild > 0) {
         this.sourceBuild--;
      } else if (var1) {
         int var2 = this.process(this.outputCollapse);
         switch (this.rendererScan) {
            case 1:
               this.profileInvoke = Module.client.player.getInventory().getSelectedSlot();
               if (this.profileInvoke != var2) {
                  Module.client.player.getInventory().setSelectedSlot(var2);
               }

               this.tick();
               this.rendererScan = 2;
               this.sourceBuild = 1;
               break;
            case 2:
               if (this.profileInvoke != var2) {
                  Module.client.player.getInventory().setSelectedSlot(this.profileInvoke);
               }

               this.drawAnimation();
         }
      } else {
         switch (this.rendererScan) {
            case 1:
               this.rendererScan = 2;
               this.sourceBuild = 3;
               break;
            case 2:
               this.profileInvoke = Module.client.player.getInventory().getSelectedSlot();
               if (!Module.client.player.isSprinting()) {
                  Module.client.interactionManager
                     .clickSlot(Module.client.player.playerScreenHandler.syncId, this.outputCollapse, this.profileInvoke, SlotActionType.SWAP, Module.client.player);
               }

               this.rendererScan = 3;
               this.sourceBuild = 1;
               break;
            case 3:
               this.tick();
               this.rendererScan = 4;
               this.sourceBuild = 1;
               break;
            case 4:
               if (!Module.client.player.isSprinting()) {
                  Module.client.interactionManager
                     .clickSlot(Module.client.player.playerScreenHandler.syncId, this.outputCollapse, this.profileInvoke, SlotActionType.SWAP, Module.client.player);
               }

               if (Module.client.getNetworkHandler() != null) {
                  Module.client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
               }

               this.rendererScan = 5;
               this.sourceBuild = 1;
               break;
            case 5:
               SyntheticKeyState.handle().process("TargetPearl");
               this.drawAnimation();
         }
      }
   }

   private void tick() {
      this.refresh();
      this.scaleSave = Module.client.player.getYaw();
      this.colorCompute = Module.client.player.getPitch();
      Module.client.player.setYaw(this.sourceSchedule);
      Module.client.player.headYaw = this.sourceSchedule;
      Module.client.player.setPitch(this.timerRender);
      Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
      Module.client.player.swingHand(Hand.MAIN_HAND);
      Module.client.player.setYaw(this.scaleSave);
      Module.client.player.headYaw = this.scaleSave;
      Module.client.player.setPitch(this.colorCompute);
   }

   private void drawAnimation() {
      this.colorMeasure = System.currentTimeMillis();
      this.rendererScan = 0;
      this.sourceBuild = 0;
      this.outputCollapse = -1;
      this.animationSchedule = -1;
      this.profileInvoke = -1;
   }

   private void encodePoint() {
      this.presetWrite.clear();
      HashSet var1 = new HashSet();

      for (Entity var3 : Module.client.world.getEntities()) {
         if (var3 instanceof EnderPearlEntity var4) {
            int var5 = var4.getId();
            var1.add(var5);
            Vec3d var6 = var4.getPos();
            Vec3d var7 = this.windowConvert.get(var5);
            if (var7 == null && Module.client.player.getEyePos().squaredDistanceTo(var6) < 9.0) {
               this.presetSave.add(var5);
            }

            this.presetWrite.put(var5, var7 != null ? var6.subtract(var7) : var4.getVelocity());
            this.windowConvert.put(var5, var6);
         }
      }

      this.windowConvert.keySet().retainAll(var1);
      this.presetSave.retainAll(var1);
   }

   private Vec3d handle(EnderPearlEntity var1) {
      Vec3d var2 = var1.getVelocity();
      if (var2.lengthSquared() > 0.001) {
         return var2;
      }

      Vec3d var3 = this.presetWrite.get(var1.getId());
      return var3 != null ? var3 : var2;
   }

   private EnderPearlEntity animate() {
      EnderPearlEntity var1 = null;
      double var2 = Double.MAX_VALUE;
      double var4 = this.vectorMatch.compute() * this.vectorMatch.compute();

      for (Entity var7 : Module.client.world.getEntities()) {
         if (var7 instanceof EnderPearlEntity var8 && !(this.handle(var8).lengthSquared() < 0.001) && !this.presetSave.contains(var8.getId())) {
            PlayerEntity var10 = var8.getOwner() instanceof PlayerEntity var11 ? var11 : null;
            if (var10 != Module.client.player
               && (var10 == null ? !this.frameCheck.compute() : !this.positionAdvance.compute() || !FriendManager.handle(var10.getName().getString()))) {
               double var13 = Module.client.player.squaredDistanceTo(var8);
               if (!(var13 > var4) && var13 < var2) {
                  var2 = var13;
                  var1 = var8;
               }
            }
         }
      }

      return var1;
   }

   private Vec3d handle(EnderPearlEntity var1, Vec3d var2) {
      double var3 = var1.getFinalGravity();
      if (var3 <= 0.0) {
         var3 = 0.03;
      }

      double var5 = var1.isTouchingWater() ? 0.8 : 0.99;
      return this.handle(var1.getPos(), var2, var3, var5, var1, 240);
   }

   private TargetPearl.State handle(Vec3d var1, Vec3d var2) {
      double var3 = var2.x - var1.x;
      double var5 = var2.z - var1.z;
      float var7 = (float)Math.toDegrees(Math.atan2(-var3, var5));
      TargetPearl.State var8 = null;

      for (float var9 = -10.0F; var9 <= 10.0F; var9 += 2.0F) {
         float var10 = var7 + var9;

         for (float var11 = -90.0F; var11 <= 90.0F; var11 += 1.5F) {
            TargetPearl.State var12 = this.handle(var1, var2, var10, var11);
            if (var12 != null && (var8 == null || var12.context < var8.context)) {
               var8 = var12;
            }
         }
      }

      if (var8 == null) {
         return null;
      }

      TargetPearl.State var13 = var8;

      for (float var14 = var8.instance - 2.0F; var14 <= var8.instance + 2.0F; var14 += 0.5F) {
         for (float var15 = var8.data - 2.0F; var15 <= var8.data + 2.0F; var15 += 0.3F) {
            TargetPearl.State var16 = this.handle(var1, var2, var14, var15);
            if (var16 != null && var16.context < var13.context) {
               var13 = var16;
            }
         }
      }

      return var13;
   }

   private TargetPearl.State handle(Vec3d var1, Vec3d var2, float var3, float var4) {
      Vec3d var5 = this.handle(var3, var4);
      Vec3d var6 = this.handle(var1, var5, 0.03, 0.99, Module.client.player, 160);
      if (var6 == null) {
         return null;
      }

      double var7 = Math.sqrt(var6.squaredDistanceTo(var2));
      return new TargetPearl.State(MathHelper.wrapDegrees(var3), MathHelper.clamp(var4, -90.0F, 90.0F), var7);
   }

   private Vec3d handle(float var1, float var2) {
      float var3 = var1 * (float) (Math.PI / 180.0);
      float var4 = var2 * (float) (Math.PI / 180.0);
      double var5 = -MathHelper.sin(var3) * MathHelper.cos(var4);
      double var7 = -MathHelper.sin(var4);
      double var9 = MathHelper.cos(var3) * MathHelper.cos(var4);
      Vec3d var11 = new Vec3d(var5, var7, var9).normalize().multiply(1.5);
      Vec3d var12 = Module.client.player.getMovement();
      return var11.add(var12.x, Module.client.player.isOnGround() ? 0.0 : var12.y, var12.z);
   }

   private Vec3d handle(Vec3d var1, Vec3d var2, double var3, double var5, Entity var7, int var8) {
      if (Module.client.world == null) {
         return null;
      }

      Vec3d var9 = var1;
      Vec3d var10 = var2;

      for (int var11 = 0; var11 < var8; var11++) {
         var10 = var10.subtract(0.0, var3, 0.0).multiply(var5);
         Vec3d var12 = var9.add(var10);
         BlockHitResult var13 = Module.client.world.raycast(new RaycastContext(var9, var12, ShapeType.COLLIDER, FluidHandling.NONE, var7));
         if (var13.getType() != Type.MISS) {
            return var13.getPos();
         }

         Box var14 = new Box(var9, var12).expand(1.0);
         double var15 = Double.MAX_VALUE;
         Vec3d var17 = null;

         for (Entity var19 : Module.client.world.getOtherEntities(var7, var14, var0 -> var0.isAlive() && !var0.isSpectator() && var0 instanceof PlayerEntity)) {
            Box var20 = var19.getBoundingBox().expand(0.3);
            Optional var21 = var20.raycast(var9, var12);
            if (var21.isPresent()) {
               double var22 = var9.squaredDistanceTo((Vec3d)var21.get());
               if (var22 < var15) {
                  var15 = var22;
                  var17 = (Vec3d)var21.get();
               }
            }
         }

         if (var17 != null) {
            return var17;
         }

         var9 = var12;
      }

      return var9;
   }

   private int load() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).getItem() == Items.ENDER_PEARL) {
            boolean var2 = var1 < 9;
            if ((!this.serverRead.compute() || !var2) && (!this.eventAttach.compute() || var2)) {
               return var2 ? var1 + 36 : var1;
            }
         }
      }

      return -1;
   }

   private boolean handle(int var1) {
      return var1 >= 0 && var1 <= 8 || var1 >= 36 && var1 <= 44;
   }

   private int process(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         return var1;
      } else {
         return var1 >= 36 && var1 <= 44 ? var1 - 36 : -1;
      }
   }

   private void save() {
      if (!this.providerClose.isEmpty()) {
         this.providerClose.removeIf(var0 -> Module.client.world.getEntityById(var0) == null);
      }
   }

   @Override
   public void process() {
      if (this.rendererScan > 0 && !this.handle(this.outputCollapse)) {
         SyntheticKeyState.handle().process("TargetPearl");
      }

      this.providerClose.clear();
      this.presetSave.clear();
      this.windowConvert.clear();
      this.presetWrite.clear();
      this.rendererScan = 0;
      this.sourceBuild = 0;
      this.outputCollapse = -1;
      this.animationSchedule = -1;
      ViewRotationCoordinator.instance = ViewRotationCoordinator.data;
      super.process();
   }

   static final class State {
      final float instance;
      final float data;
      final double context;

      State(float var1, float var2, double var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }
   }
}
