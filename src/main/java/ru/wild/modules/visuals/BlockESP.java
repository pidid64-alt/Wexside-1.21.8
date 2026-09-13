package ru.wild.modules.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.render.BlockEspGeometry;
import ru.wild.render.BlockEspRenderPipeline;
import ru.wild.render.BlockEspRenderer;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.math.TemporalNoise;
import ru.wild.util.world.BlockEspScanner;
import ru.wild.util.world.BlockRenderTypeRegistry;

@ModuleRegister(name = "BlockESP", category = ModuleCategory.Visuals, description = "Подцветка определенных блоков")
public class BlockESP extends Module {
   public final ModeSetting source = new ModeSetting("Режим", "Обычный", "Обычный", "Производительность");
   public final NumberSetting target = new NumberSetting("Радиус", 48.0F, 16.0F, 128.0F, 8.0F, false).handle(() -> !this.source.process("Производительность"));
   public final BooleanSetting pending = new BooleanSetting("Сквозь стены", true);
   public final NumberSetting previous = new NumberSetting("Лимит боксов", 512.0F, 128.0F, 2048.0F, 64.0F, false)
      .handle(() -> !this.source.process("Производительность"));
   public static ChoiceSetting latest = new ChoiceSetting(
      "Блоки",
      new BooleanSetting("Сундук", true),
      new BooleanSetting("Сундук ловушка", true),
      new BooleanSetting("Эндер сундук", true),
      new BooleanSetting("Спавнер", true),
      new BooleanSetting("Бочка", true),
      new BooleanSetting("Воронка", true),
      new BooleanSetting("Раздатчик", true),
      new BooleanSetting("Выбрасыватель", true),
      new BooleanSetting("Печка", true),
      new BooleanSetting("Шалкер", true),
      new BooleanSetting("Ваза", true),
      new BooleanSetting("Подозрительный песок", true),
      new BooleanSetting("Угольная руда", true),
      new BooleanSetting("Железная руда", true),
      new BooleanSetting("Золотая руда", true),
      new BooleanSetting("Медная руда", true),
      new BooleanSetting("Лазуритовая руда", true),
      new BooleanSetting("Редстоуновая руда", true),
      new BooleanSetting("Алмазная руда", true),
      new BooleanSetting("Изумрудная руда", true),
      new BooleanSetting("Кварцевая руда", true),
      new BooleanSetting("Древние обломки", true)
   );
   public static final Map<BlockEntityType<?>, Integer> summary = new HashMap<>();
   private static final int matrixBlend = 16384;
   private static final double vectorMatch = 6.0;
   private static final int itemProject = 2;
   private static final int responseCompute = 14;
   private static final int providerFetch = 90;
   private static final String[] profileDraw = new String[]{
      "Сундук",
      "Сундук ловушка",
      "Эндер сундук",
      "Спавнер",
      "Бочка",
      "Воронка",
      "Раздатчик",
      "Выбрасыватель",
      "Печка",
      "Шалкер",
      "Ваза",
      "Подозрительный песок",
      "Угольная руда",
      "Железная руда",
      "Золотая руда",
      "Медная руда",
      "Лазуритовая руда",
      "Редстоуновая руда",
      "Алмазная руда",
      "Изумрудная руда",
      "Кварцевая руда",
      "Древние обломки"
   };
   private final BlockEspScanner vectorPerform = new BlockEspScanner();
   private final BlockEspGeometry eventAttach = new BlockEspGeometry();
   private final BlockEspRenderer serverRead = new BlockEspRenderer();
   private final int[] positionAdvance = new int[22];
   private final int[] frameCheck = new int[22];
   private final long[] moduleCollect = new long[4096];
   private int providerClose;
   private final BiConsumer<BlockPos, BlockState> presetSave = (var1, var2) -> {
      if (this.providerClose < this.moduleCollect.length) {
         this.moduleCollect[this.providerClose++] = BlockPos.asLong(var1.getX(), var1.getY(), var1.getZ());
      }
   };
   private long[] windowConvert;
   private byte[] presetWrite;
   private int[] colorMeasure;
   private double animationSchedule;
   private double rendererScan;
   private double sourceBuild;
   private int outputCollapse;
   private int profileInvoke;
   private int sourceSchedule;
   private int timerRender;
   private int scaleSave = -1;
   private int colorCompute = -1;
   private int scaleAdapt = -1;
   private float textureRun = Float.MAX_VALUE;
   private boolean indexBind;

   public BlockESP() {
      this.handle(this.source, this.target, this.previous, this.pending, latest);
      summary.clear();
      summary.put(BlockEntityType.CHEST, new Color(255, 194, 84).getRGB());
      summary.put(BlockEntityType.TRAPPED_CHEST, new Color(143, 109, 62).getRGB());
      summary.put(BlockEntityType.ENDER_CHEST, new Color(153, 49, 238).getRGB());
      summary.put(BlockEntityType.MOB_SPAWNER, 16777215);
      summary.put(BlockEntityType.BARREL, new Color(250, 225, 62).getRGB());
      summary.put(BlockEntityType.HOPPER, new Color(62, 137, 250).getRGB());
      summary.put(BlockEntityType.DISPENSER, new Color(27, 64, 250).getRGB());
      summary.put(BlockEntityType.DROPPER, new Color(0, 23, 255).getRGB());
      summary.put(BlockEntityType.FURNACE, new Color(115, 115, 115).getRGB());
      summary.put(BlockEntityType.SHULKER_BOX, new Color(246, 123, 123).getRGB());
      summary.put(BlockEntityType.DECORATED_POT, new Color(185, 122, 87).getRGB());
      summary.put(BlockEntityType.BRUSHABLE_BLOCK, new Color(227, 203, 153).getRGB());
      this.tick();
      BlockEspRenderPipeline.handle();
   }

   @Override
   public void handle() {
      super.handle();
      this.vectorPerform.handle();
      this.eventAttach.handle();
      this.scaleSave = -1;
      this.colorCompute = -1;
      this.scaleAdapt = -1;
      this.sourceSchedule = 0;
      this.timerRender = 0;
      this.indexBind = true;
   }

   @Override
   public void process() {
      super.process();
      this.eventAttach.process();
      this.vectorPerform.handle();
      this.indexBind = false;
      this.animate();
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.vectorPerform.handle();
      this.eventAttach.process();
      BlockEspGeometry.handle(this.eventAttach.update());
      this.serverRead.handle();
      this.eventAttach.handle();
      this.sourceSchedule = 0;
      this.timerRender = 0;
      this.indexBind = true;
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      Packet var2 = var1.resolve();
      if (!var1.compute()) {
         if (var2 instanceof BlockUpdateS2CPacket var9) {
            BlockPos var8 = var9.getPos();
            this.vectorPerform.handle(var8.getX(), var8.getY(), var8.getZ());
         } else if (var2 instanceof BlockEntityUpdateS2CPacket var10) {
            BlockPos var15 = var10.getPos();
            this.vectorPerform.handle(var15.getX(), var15.getY(), var15.getZ());
         } else if (var2 instanceof ChunkDeltaUpdateS2CPacket var12) {
            this.providerClose = 0;
            var12.visitUpdates(this.presetSave);
            this.vectorPerform.handle(this.moduleCollect, this.providerClose);
         } else if (var2 instanceof ChunkDataS2CPacket var14) {
            this.vectorPerform.handle(var14.getChunkX(), var14.getChunkZ());
         } else if (var2 instanceof UnloadChunkS2CPacket var7) {
            ChunkPos var16 = var7.pos();
            this.vectorPerform.process(var16.x, var16.z);
         }
      } else {
         if (var2 instanceof PlayerActionC2SPacket var3) {
            Action var5 = var3.getAction();
            if (var5 == Action.START_DESTROY_BLOCK || var5 == Action.STOP_DESTROY_BLOCK) {
               BlockPos var6 = var3.getPos();
               this.vectorPerform.handle(var6.getX(), var6.getY(), var6.getZ());
            }
         } else if (var2 instanceof PlayerInteractBlockC2SPacket var4) {
            BlockPos var11 = var4.getBlockHitResult().getBlockPos();
            Direction var13 = var4.getBlockHitResult().getSide();
            this.vectorPerform.handle(var11.getX(), var11.getY(), var11.getZ());
            this.vectorPerform.handle(var11.getX() + var13.getOffsetX(), var11.getY() + var13.getOffsetY(), var11.getZ() + var13.getOffsetZ());
         }
      }
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      ClientWorld var2 = Module.client.world;
      ClientPlayerEntity var3 = Module.client.player;
      if (var2 != null && var3 != null) {
         boolean var4 = this.source.process("Производительность");
         int var5 = Module.client.options.getClampedViewDistance();
         int var6 = var4 ? Math.min(var5, 6) : var5;
         int var7 = var4 ? MathHelper.clamp((int)this.previous.compute(), 1, 4096) : 16384;
         int var8 = this.drawAnimation();
         if (this.encodePoint() || var8 != this.scaleSave || var7 != this.colorCompute || var6 != this.scaleAdapt) {
            this.scaleSave = var8;
            this.colorCompute = var7;
            this.scaleAdapt = var6;
            this.indexBind = true;
         }

         this.vectorPerform.handle(var5 + 1);
         ChunkPos var9 = var3.getChunkPos();
         this.vectorPerform.handle(var2, var9.x, var9.z);
         this.profileInvoke++;
         if (this.vectorPerform.compute() || this.eventAttach.resolve()) {
            this.indexBind = true;
         }

         if (this.sourceSchedule != 0 && this.profileInvoke - this.sourceSchedule >= 0) {
            this.sourceSchedule = 0;
            this.indexBind = true;
         }

         if (this.timerRender != 0 && this.profileInvoke - this.timerRender >= 0) {
            this.timerRender = 0;
            this.indexBind = true;
         }

         if (this.vectorPerform.resolve() && this.handle(var3) > 6.0) {
            this.indexBind = true;
         }

         if (this.indexBind && !this.eventAttach.compute() && this.profileInvoke - this.outputCollapse >= 2) {
            this.process(var3);
         }
      }
   }

   private double handle(ClientPlayerEntity var1) {
      double var2 = var1.getX() - this.animationSchedule;
      double var4 = var1.getY() - this.rendererScan;
      double var6 = var1.getZ() - this.sourceBuild;
      return Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null && WorldVertexBuffer.handle(Module.client)) {
         this.serverRead.handle(this.eventAttach.update());
         if (this.serverRead.update()) {
            Vec3d var2 = Module.client.gameRenderer.getCamera().getPos();
            Matrix4f var3 = var1.compute().peek().getPositionMatrix();
            float var4 = Math.min(this.refresh(), this.textureRun);
            float var5 = var4 * 0.88F;
            this.serverRead
               .handle(
                  var3,
                  (float)(var2.x - this.serverRead.process()),
                  (float)(var2.y - this.serverRead.compute()),
                  (float)(var2.z - this.serverRead.resolve()),
                  handle(var2.x),
                  handle(var2.y),
                  handle(var2.z),
                  TemporalNoise.process(),
                  TemporalNoise.compute(),
                  var5,
                  var4,
                  1.0F,
                  this.pending.compute()
               );
         }
      }
   }

   private static float handle(double var0) {
      return (float)(var0 - Math.floor(var0 * 0.00390625) * 256.0);
   }

   private void process(ClientPlayerEntity var1) {
      if (this.windowConvert == null || this.windowConvert.length < this.colorCompute) {
         this.windowConvert = new long[this.colorCompute];
         this.presetWrite = new byte[this.colorCompute];
         this.colorMeasure = new int[this.colorCompute];
      }

      this.animationSchedule = var1.getX();
      this.rendererScan = var1.getEyeY();
      this.sourceBuild = var1.getZ();
      this.outputCollapse = this.profileInvoke;
      int var2 = this.vectorPerform
         .handle(
            this.windowConvert,
            this.presetWrite,
            this.colorMeasure,
            this.colorCompute,
            this.scaleSave,
            this.scaleAdapt,
            this.animationSchedule,
            this.rendererScan,
            this.sourceBuild,
            this.render()
         );
      this.textureRun = var2 >= this.colorCompute ? (float)this.vectorPerform.apply() : Float.MAX_VALUE;
      BlockPos var3 = var1.getBlockPos();
      int var4 = var3.getX() & -16;
      int var5 = var3.getY() & -16;
      int var6 = var3.getZ() & -16;
      if (this.eventAttach.handle(this.windowConvert, this.presetWrite, this.colorMeasure, var2, (int[])this.positionAdvance.clone(), var4, var5, var6)) {
         this.indexBind = false;
         if (this.vectorPerform.execute() > 0) {
            this.sourceSchedule = this.profileInvoke + 14;
         }

         if (this.vectorPerform.update()) {
            this.timerRender = this.profileInvoke + 90;
         }
      }
   }

   private float refresh() {
      return this.source.process("Производительность") ? this.target.compute() : Module.client.options.getClampedViewDistance() * 16.0F * 1.5F + 48.0F;
   }

   private double render() {
      return this.refresh() + 8.0;
   }

   private void tick() {
      for (int var1 = 0; var1 < 22; var1++) {
         this.frameCheck[var1] = -1;

         for (int var2 = 0; var2 < latest.config.size(); var2++) {
            if (latest.config.get(var2).instance.equals(profileDraw[var1])) {
               this.frameCheck[var1] = var2;
               break;
            }
         }
      }
   }

   private int drawAnimation() {
      int var1 = 0;

      for (int var2 = 0; var2 < 22; var2++) {
         int var3 = this.frameCheck[var2];
         if (var3 >= 0 && latest.handle(var3)) {
            var1 |= 1 << var2;
         }
      }

      return var1;
   }

   private boolean encodePoint() {
      boolean var1 = false;

      for (int var2 = 0; var2 < 22; var2++) {
         BlockEntityType var3 = BlockRenderTypeRegistry.handle(var2);
         int var4;
         if (var3 != null) {
            Integer var5 = summary.get(var3);
            var4 = var5 == null ? 16777215 : var5 & 16777215;
         } else {
            var4 = BlockRenderTypeRegistry.process(var2) & 16777215;
         }

         if (this.positionAdvance[var2] != var4) {
            this.positionAdvance[var2] = var4;
            var1 = true;
         }
      }

      return var1;
   }

   private void animate() {
      if (RenderSystem.isOnRenderThread()) {
         this.serverRead.apply();
      } else {
         Module.client.execute(this.serverRead::apply);
      }
   }
}
