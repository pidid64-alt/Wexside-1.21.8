package ru.wild.modules.visuals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.api.event.MouseScrollEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.ScreenResizeEvent;
import ru.wild.api.event.WorldRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.gui.widget.BrowserSurface;
import ru.wild.network.PartyHostConfig;
import ru.wild.network.PartyMemberProfile;
import ru.wild.network.PartyMessageHandler;
import ru.wild.network.PartyRoster;
import ru.wild.network.RemoteCameraState;
import ru.wild.network.RemoteFrameBuffer;
import ru.wild.network.RemoteInputSink;
import ru.wild.network.RemoteScreenRenderer;
import ru.wild.network.RemoteScreenSession;
import ru.wild.network.RemoteScreenSessionImpl;
import ru.wild.network.RemoteScreenViewport;
import ru.wild.network.RemoteViewportInteractionMode;
import ru.wild.util.text.Base32Codec;
import ru.wild.util.text.ChatLogger;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(name = "Screens", category = ModuleCategory.Visuals, description = "Общие экраны комнаты: создание, размещение и синхронный просмотр")
public class Screens extends Module {
   public final StringSetting source = new StringSetting("Сервер", "49.12.210.82");
   public final ActionSetting target = new ActionSetting("Подключиться к серверу", 0).handle(this::refresh);
   public final ActionSetting pending = new ActionSetting("Создать комнату", 0).handle(this::render);
   public final StringSetting previous = new StringSetting("Код комнаты", "");
   public final ActionSetting latest = new ActionSetting("Подключиться", 0).handle(this::tick);
   public final KeybindSetting summary = new KeybindSetting("Создать экран", -1);
   public final ActionSetting matrixBlend = new ActionSetting("Убрать экраны", 0).handle(this::animate);
   public final NumberSetting vectorMatch = new NumberSetting("Поворот", 0.0F, 0.0F, 355.0F, 5.0F, false);
   public final BooleanSetting itemProject = new BooleanSetting("Следить за направлением", false);
   public final StringSetting responseCompute = new StringSetting("Ссылка", "https://www.google.com");
   public final ActionSetting providerFetch = new ActionSetting("Открыть на экране", 0).handle(this::load);
   public final StringSetting profileDraw = new StringSetting("Передать управление", "");
   public final ActionSetting vectorPerform = new ActionSetting("Передать", 0).handle(this::drawAnimation);
   private final RemoteScreenViewport eventAttach = new RemoteScreenViewport();
   private final RemoteCameraState serverRead = new RemoteCameraState();
   private final RemoteScreenRenderer positionAdvance = new RemoteScreenRenderer(attachEvent());
   private final Matrix4f frameCheck = new Matrix4f();
   private final Vector3f moduleCollect = new Vector3f();
   private final Vector3f providerClose = new Vector3f();
   private final int[] presetSave = new int[4];
   private final Map<UUID, double[]> windowConvert = new HashMap<>();
   private final Set<UUID> presetWrite = new HashSet<>();
   private Vec3d colorMeasure = Vec3d.ZERO;
   private boolean animationSchedule;
   private long rendererScan;
   private UUID sourceBuild;
   private long outputCollapse;
   private double profileInvoke;
   private double sourceSchedule;
   private double timerRender;
   private double scaleSave;
   private double colorCompute;
   private double scaleAdapt;

   public Screens() {
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
         this.vectorPerform
      );
   }

   @Override
   public void process() {
      this.eventAttach.tick();
      this.windowConvert.clear();
      this.presetWrite.clear();
      this.positionAdvance.handle().handle();
      super.process();
   }

   @EventHandler
   public void handle(ScreenResizeEvent var1) {
      if (Module.client.player != null && Module.client.currentScreen == null && var1.resolve() == 1) {
         if (this.summary.compute() != -1 && var1.compute() == this.summary.compute()) {
            this.encodePoint();
         }
      }
   }

   @EventHandler
   public void handle(MouseScrollEvent var1) {
      if (Module.client.player != null && Module.client.currentScreen instanceof ChatScreen) {
         RemoteInputSink var2 = this.blendMatrix();
         if (var2 != null) {
            var2.scroll(this.serverRead.check(), this.serverRead.onTick(), var1.update());
            var1.process();
         }
      }
   }

   @EventHandler
   public void handle(MouseButtonEvent var1) {
      if (Module.client.player != null) {
         if (Module.client.currentScreen instanceof ChatScreen) {
            this.process(var1);
         } else if (Module.client.currentScreen == null && var1.resolve() == 0) {
            if (var1.select()) {
               this.submit();
            } else if (var1.onTick() && this.eventAttach.compute() != RemoteViewportInteractionMode.NONE) {
               PartyRoster.Point3d var2 = PartyMessageHandler.handle().compute().handle(this.eventAttach.handle());
               if (var2 != null) {
                  this.process(1.0F);
                  if (this.eventAttach
                     .handle(var2, this.profileInvoke, this.sourceSchedule, this.timerRender, this.scaleSave, this.colorCompute, this.scaleAdapt)) {
                     var1.process();
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      PartyMessageHandler var2 = PartyMessageHandler.handle();
      var2.refresh();
      if (Module.client.player == null) {
         this.eventAttach.tick();
      } else {
         this.positionAdvance.handle().handle(var2.compute().handle());
         this.computeResponse();
         this.fetchProvider();
         if (!this.eventAttach.resolve()) {
            this.process(1.0F);
            this.eventAttach
               .handle(
                  var2.compute().handle(),
                  var2.prepare(),
                  this.performVector(),
                  this.profileInvoke,
                  this.sourceSchedule,
                  this.timerRender,
                  this.scaleSave,
                  this.colorCompute,
                  this.scaleAdapt
               );
            this.drawProfile();
         }
      }
   }

   @EventHandler
   public void handle(WorldRenderContext var1) {
      if (Module.client.player != null) {
         PartyMessageHandler var2 = PartyMessageHandler.handle();
         List var3 = var2.compute().handle();
         this.process(var1);
         if (!var3.isEmpty()) {
            this.measure();
            this.handle(var1.update().apply());
            this.positionAdvance
               .handle(var1.update(), var3, var2.compute(), this.eventAttach, var2.prepare(), this.performVector(), var2.resolve().process(), -8426497);
         }
      }
   }

   private void process(WorldRenderContext var1) {
      this.frameCheck.set(var1.update().update()).mul(var1.update().compute());
      this.colorMeasure = var1.update().handle().getPos();
      this.animationSchedule = true;
   }

   private void refresh() {
      String var1 = this.source.compute().trim();
      if (var1.isEmpty()) {
         ChatLogger.handle("Укажите адрес сервера");
      } else {
         PartyMessageHandler.handle().handle(PartyHostConfig.handle(var1, 7331));
         ChatLogger.handle("Подключаюсь к " + var1 + ":7331");
      }
   }

   private void render() {
      if (!readServer()) {
         String var1 = Base32Codec.handle();
         if (!PartyMessageHandler.handle().handle(var1)) {
            ChatLogger.handle("Нет связи с сервером Wild");
         } else {
            this.previous.process(var1);
            ChatLogger.handle("Код комнаты: " + var1);
         }
      }
   }

   private void tick() {
      if (!readServer()) {
         String var1 = Base32Codec.handle(this.previous.compute());
         if (!Base32Codec.process(var1)) {
            ChatLogger.handle("Неверный формат кода");
         } else {
            PartyMessageHandler.handle().process(var1);
         }
      }
   }

   private void drawAnimation() {
      PartyMemberProfile var1 = PartyMessageHandler.handle().process();
      PartyMemberProfile.NamedEntry var2 = var1.handle(this.profileDraw.compute().trim());
      if (var2 == null) {
         ChatLogger.handle("Такого участника нет в комнате");
      } else {
         PartyMessageHandler.handle().handle(var2.uuid());
      }
   }

   private void encodePoint() {
      PartyMessageHandler var1 = PartyMessageHandler.handle();
      if (!var1.process().handle()) {
         ChatLogger.handle("Сначала создайте комнату или войдите в неё");
      } else if (var1.compute().process() >= 5) {
         ChatLogger.handle("В комнате уже максимум экранов");
      } else {
         double var2 = Math.toRadians(Module.client.player.getYaw());
         var1.handle(
            Module.client.player.getX() - Math.sin(var2) * 5.0,
            Module.client.player.getEyeY(),
            Module.client.player.getZ() + Math.cos(var2) * 5.0,
            Module.client.player.getYaw() + 180.0F,
            6.4F,
            3.6F
         );
      }
   }

   private void animate() {
      PartyMessageHandler var1 = PartyMessageHandler.handle();
      List var2 = var1.compute().handle();
      UUID var3 = var1.prepare();
      boolean var4 = this.performVector();

      for (int var5 = 0; var5 < var2.size(); var5++) {
         PartyRoster.Point3d var6 = (PartyRoster.Point3d)var2.get(var5);
         if (RemoteScreenViewport.handle(var6, var3, var4)) {
            var1.process(var6.id());
         }
      }

      this.eventAttach.tick();
   }

   private void load() {
      PartyRoster.Point3d var1 = PartyMessageHandler.handle().compute().handle(this.eventAttach.process());
      if (var1 == null) {
         ChatLogger.handle("Наведитесь на экран, которым управляете");
      } else {
         String var2 = this.responseCompute.compute().trim();
         if (!var2.startsWith("https://")) {
            ChatLogger.handle("Ссылка должна начинаться с https://");
         } else {
            PartyMessageHandler.handle().handle(var1.id(), var2, true, 0L, 1.0F);
         }
      }
   }

   private void handle(float var1) {
      if (this.eventAttach.resolve()) {
         this.process(var1);
         this.eventAttach.handle(this.save());
         this.eventAttach.handle(this.profileInvoke, this.sourceSchedule, this.timerRender, this.scaleSave, this.colorCompute, this.scaleAdapt);
         if (this.eventAttach.handle(System.currentTimeMillis())) {
            this.unload();
         }
      }
   }

   private double save() {
      double var1 = this.eventAttach.refresh();
      BlockHitResult var3 = Module.client.world
         .raycast(
            new RaycastContext(
               new Vec3d(this.profileInvoke, this.sourceSchedule, this.timerRender),
               new Vec3d(this.profileInvoke + this.scaleSave * var1, this.sourceSchedule + this.colorCompute * var1, this.timerRender + this.scaleAdapt * var1),
               ShapeType.COLLIDER,
               FluidHandling.NONE,
               Module.client.player
            )
         );
      if (var3.getType() != Type.BLOCK) {
         return var1;
      }

      double var4 = var3.getPos().distanceTo(new Vec3d(this.profileInvoke, this.sourceSchedule, this.timerRender));
      return Math.max(1.0, var4 - this.eventAttach.onTick() * 0.5 - 0.35);
   }

   private void submit() {
      if (this.eventAttach.resolve()) {
         this.unload();
         this.eventAttach.render();
      }
   }

   private void unload() {
      UUID var1 = this.eventAttach.update();
      if (var1 != null) {
         PartyMessageHandler.handle()
            .handle(
               var1,
               this.eventAttach.apply(),
               this.eventAttach.execute(),
               this.eventAttach.prepare(),
               this.eventAttach.check(),
               this.eventAttach.onTick(),
               this.eventAttach.select()
            );
      }
   }

   private void process(MouseButtonEvent var1) {
      if (var1.onTick() && this.fetch()) {
         var1.process();
      } else {
         RemoteInputSink var2 = this.blendMatrix();
         if (var2 != null) {
            if (var1.onTick()) {
               var2.press(this.serverRead.check(), this.serverRead.onTick(), var1.resolve());
            } else {
               if (!var1.select()) {
                  return;
               }

               var2.release(this.serverRead.check(), this.serverRead.onTick(), var1.resolve());
            }

            var1.process();
         }
      }
   }

   private boolean fetch() {
      PartyRoster.Point3d var1 = this.matchVector();
      if (var1 != null && this.serverRead.animate()) {
         RemoteFrameBuffer var2 = this.positionAdvance.handle().process(var1.id());
         if (var2 == null) {
            return false;
         }

         int var3 = this.serverRead.load();
         if (var3 >= 0 && var3 <= var2.handle()) {
            if (var3 == var2.handle()) {
               var2.handle(this.responseCompute.compute().trim());
            } else {
               var2.compute(var3);
            }

            PartyMessageHandler.handle().handle(var1.id(), var2.handle(var2.process()), true, 0L, 1.0F);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void measure() {
      RemoteInputSink var1 = this.blendMatrix();
      if (var1 != null) {
         var1.moveCursor(this.serverRead.check(), this.serverRead.onTick());
      }
   }

   private RemoteInputSink blendMatrix() {
      PartyRoster.Point3d var1 = this.matchVector();
      return var1 != null && this.serverRead.blendMatrix() ? this.positionAdvance.handle().handle(var1.id()) : null;
   }

   private PartyRoster.Point3d matchVector() {
      if (Module.client.currentScreen instanceof ChatScreen && this.projectItem()) {
         PartyMessageHandler var1 = PartyMessageHandler.handle();
         List var2 = var1.compute().handle();
         UUID var3 = var1.prepare();
         boolean var4 = this.performVector();
         PartyRoster.Point3d var5 = null;
         double var6 = Double.MAX_VALUE;

         for (int var8 = 0; var8 < var2.size(); var8++) {
            PartyRoster.Point3d var9 = (PartyRoster.Point3d)var2.get(var8);
            if (RemoteScreenViewport.handle(var9, var3, var4)) {
               this.serverRead.handle(var9);
               if (this.serverRead.handle(this.profileInvoke, this.sourceSchedule, this.timerRender, this.scaleSave, this.colorCompute, this.scaleAdapt)
                  && (this.serverRead.blendMatrix() || this.serverRead.animate())
                  && this.serverRead.prepare() < var6) {
                  var6 = this.serverRead.prepare();
                  var5 = var9;
               }
            }
         }

         if (var5 != null) {
            this.serverRead.handle(var5);
            this.serverRead.handle(this.profileInvoke, this.sourceSchedule, this.timerRender, this.scaleSave, this.colorCompute, this.scaleAdapt);
         }

         return var5;
      } else {
         return null;
      }
   }

   private boolean projectItem() {
      int var1 = Module.client.getWindow().getFramebufferWidth();
      int var2 = Module.client.getWindow().getFramebufferHeight();
      float var3 = Module.client.getWindow().getScaleFactor();
      if (this.animationSchedule && var1 > 0 && var2 > 0 && !(var3 <= 0.0F)) {
         this.presetSave[0] = 0;
         this.presetSave[1] = 0;
         this.presetSave[2] = var1;
         this.presetSave[3] = var2;
         this.frameCheck
            .unprojectRay((float)Module.client.mouse.getX(), var2 - (float)Module.client.mouse.getY(), this.presetSave, this.moduleCollect, this.providerClose);
         this.profileInvoke = this.colorMeasure.x + this.moduleCollect.x;
         this.sourceSchedule = this.colorMeasure.y + this.moduleCollect.y;
         this.timerRender = this.colorMeasure.z + this.moduleCollect.z;
         this.scaleSave = this.providerClose.x;
         this.colorCompute = this.providerClose.y;
         this.scaleAdapt = this.providerClose.z;
         return true;
      } else {
         return false;
      }
   }

   private void computeResponse() {
      PartyMessageHandler var1 = PartyMessageHandler.handle();
      String var2 = this.responseCompute.compute().trim();
      if (var2.startsWith("https://")) {
         List var3 = var1.compute().handle();
         UUID var4 = var1.prepare();

         for (int var5 = 0; var5 < var3.size(); var5++) {
            PartyRoster.Point3d var6 = (PartyRoster.Point3d)var3.get(var5);
            if (var4 != null && var4.equals(var6.owner()) && var6.source().isEmpty() && this.presetWrite.add(var6.id())) {
               var1.handle(var6.id(), var2, true, 0L, 1.0F);
            }
         }
      }
   }

   private void fetchProvider() {
      if (!this.itemProject.compute()) {
         this.windowConvert.clear();
      } else {
         PartyMessageHandler var1 = PartyMessageHandler.handle();
         List var2 = var1.compute().handle();
         UUID var3 = var1.prepare();
         float var4 = Module.client.player.getYaw();

         for (int var5 = 0; var5 < var2.size(); var5++) {
            PartyRoster.Point3d var6 = (PartyRoster.Point3d)var2.get(var5);
            if (var3 != null && var3.equals(var6.owner())) {
               double[] var7 = this.windowConvert.get(var6.id());
               if (var7 == null) {
                  this.windowConvert.put(var6.id(), this.handle(var6, var4));
               } else {
                  this.handle(var6, var7, var4);
               }
            }
         }
      }
   }

   private double[] handle(PartyRoster.Point3d var1, float var2) {
      double var3 = var1.x() - Module.client.player.getX();
      double var5 = var1.z() - Module.client.player.getZ();
      return new double[]{
         Math.sqrt(var3 * var3 + var5 * var5), Math.toDegrees(Math.atan2(var5, var3)) - var2, var1.y() - Module.client.player.getEyeY(), var1.yaw() - var2
      };
   }

   private void handle(PartyRoster.Point3d var1, double[] var2, float var3) {
      double var4 = Math.toRadians(var2[1] + var3);
      double var6 = Module.client.player.getX() + Math.cos(var4) * var2[0];
      double var8 = Module.client.player.getZ() + Math.sin(var4) * var2[0];
      double var10 = Module.client.player.getEyeY() + var2[2];
      if (!(Math.abs(var6 - var1.x()) < 0.02) || !(Math.abs(var10 - var1.y()) < 0.02) || !(Math.abs(var8 - var1.z()) < 0.02)) {
         long var12 = System.currentTimeMillis();
         if (var12 - this.rendererScan >= 100L) {
            this.rendererScan = var12;
            PartyMessageHandler.handle().handle(var1.id(), var6, var10, var8, (float)(var2[3] + var3), var1.width(), var1.height());
         }
      }
   }

   private void drawProfile() {
      PartyRoster.Point3d var1 = PartyMessageHandler.handle().compute().handle(this.eventAttach.process());
      if (var1 == null) {
         this.sourceBuild = null;
      } else if (!var1.id().equals(this.sourceBuild)) {
         this.sourceBuild = var1.id();
         this.vectorMatch.handle(var1.yaw());
      } else if (!(Math.abs(this.vectorMatch.compute() - var1.yaw()) < 2.5F)) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.outputCollapse >= 100L) {
            this.outputCollapse = var2;
            PartyMessageHandler.handle().handle(var1.id(), var1.x(), var1.y(), var1.z(), this.vectorMatch.compute(), var1.width(), var1.height());
         }
      }
   }

   private void process(float var1) {
      Vec3d var2 = Module.client.player.getRotationVec(var1);
      this.profileInvoke = MathHelper.lerp(var1, Module.client.player.lastRenderX, Module.client.player.getX());
      this.sourceSchedule = MathHelper.lerp(var1, Module.client.player.lastRenderY, Module.client.player.getY())
         + Module.client.player.getEyeY()
         - Module.client.player.getY();
      this.timerRender = MathHelper.lerp(var1, Module.client.player.lastRenderZ, Module.client.player.getZ());
      this.scaleSave = var2.x;
      this.colorCompute = var2.y;
      this.scaleAdapt = var2.z;
   }

   private boolean performVector() {
      UUID var1 = PartyMessageHandler.handle().prepare();
      return var1 != null && PartyMessageHandler.handle().process().handle(var1);
   }

   private static RemoteScreenSession attachEvent() {
      try {
         return new BrowserSurface();
      } catch (Throwable var1) {
         return new RemoteScreenSessionImpl();
      }
   }

   private static boolean readServer() {
      if (PartyMessageHandler.handle().apply()) {
         return false;
      }

      ChatLogger.handle("Нет связи с сервером Wild");
      return true;
   }
}
