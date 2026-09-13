package ru.wild.modules.visuals;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase.Texture;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap.Type;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EntityAttackEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerMotionEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.math.ResettableTimer;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "Particles", description = "Улучшенные частицы при атаках и бросках", category = ModuleCategory.Visuals)
public class Particles extends Module {
   public static ChoiceSetting source = new ChoiceSetting(
      "Спавнить при", new BooleanSetting("Атаке", true), new BooleanSetting("Бросок", true), new BooleanSetting("В мире", false)
   );
   public static ModeSetting target = new ModeSetting(
      "Тип частиц", "Bloom", "Bloom", "Star", "Snow", "Heart", "Dollar", "Triangle", "Sakura", "Genshin", "Rhombus"
   );
   public static NumberSetting pending = new NumberSetting("Размер", 0.5F, 0.0F, 1.0F, 0.1F, false);
   public static NumberSetting previous = new NumberSetting("Количество", 10.0F, 10.0F, 100.0F, 10.0F, false);
   public static NumberSetting latest = new NumberSetting("Время жизни", 2.0F, 0.5F, 10.0F, 0.5F, false);
   public static NumberSetting summary = new NumberSetting("Радиус в мире", 12.0F, 2.0F, 50.0F, 1.0F, false);
   public static BooleanSetting matrixBlend = new BooleanSetting("Физика", true);
   public static ModeSetting vectorMatch = new ModeSetting("Режим цвета", "Клиентовский", "Клиентовский", "Свой");
   public static ColorSetting itemProject = new ColorSetting("Кастом цвет", 15.0F, 1.0F, 1.0F).process(() -> !vectorMatch.process("Свой"));
   private static final int responseCompute = 1024;
   private long providerFetch = System.nanoTime();
   private static final String profileDraw = "wild";
   private static final RenderPipeline vectorPerform = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/textured_quads"))
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final Map<Particles.Mode, RenderLayer> eventAttach = new ConcurrentHashMap<>();
   private final List<Particles.AnimationState> serverRead = new ArrayList<>();
   private final List<Particles.AnimationState> positionAdvance = new ArrayList<>();
   private final List<Particles.AnimationState> frameCheck = new ArrayList<>();
   private static final Vector3f moduleCollect = new Vector3f(0.0F, 0.0F, 1.0F);

   public Particles() {
      this.handle(source, target, vectorMatch, itemProject, pending, previous, latest, summary, matrixBlend);
   }

   private void refresh() {
      this.serverRead.clear();
      this.frameCheck.clear();
      this.positionAdvance.clear();
   }

   private void handle(List<Particles.AnimationState> var1, Vec3d var2, Vec3d var3) {
      float var4 = 0.05F + pending.compute() * 0.2F;
      int var5 = vectorMatch.process("Свой") ? itemProject.compute().getRGB() : PackedColor.unload(var1.size() * 100);

      Particles.Mode var6 = switch (target.compute()) {
         case "Heart" -> Particles.Mode.HEART;
         case "Star" -> Particles.Mode.STAR;
         case "Snow" -> Particles.Mode.SNOW;
         case "Bloom" -> Particles.Mode.BLOOM;
         case "Dollar" -> Particles.Mode.DOLLAR;
         case "Triangle" -> Particles.Mode.TRIANGLE;
         case "Sakura" -> Particles.Mode.SAKURA;
         case "Genshin" -> Particles.Mode.GEMINI;
         case "Rhombus" -> Particles.Mode.SIMS;
         default -> Particles.Mode.BLOOM;
      };
      var1.add(
         new Particles.AnimationState(
            var6, var2.add(0.0, var4, 0.0), var3, var1.size(), (int)ClientMathUtil.update(ClientMathUtil.resolve(0.0F, 360.0F), 15.0), var5, var4, 0.2F
         )
      );
   }

   @EventHandler
   public void handle(EntityAttackEvent var1) {
      Entity var2 = var1.compute();
      float var3 = 6.0F;
      if (source.process("Атаке")) {
         int var4 = (int)previous.compute();

         for (int var5 = 0; var5 < var4; var5++) {
            this.handle(
               this.serverRead,
               new Vec3d(var2.getX(), var2.getY() + ClientMathUtil.resolve(0.0F, var2.getHeight()), var2.getZ()),
               new Vec3d(ClientMathUtil.resolve(-var3, var3), ClientMathUtil.resolve(-var3, var3), ClientMathUtil.resolve(-var3, var3))
            );
         }
      }
   }

   @EventHandler
   public void handle(PlayerMotionEvent var1) {
      if (source.process("Бросок")) {
         if (Module.client.world == null) {
            return;
         }

         for (Entity var3 : Module.client.world.getEntities()) {
            if ((var3 instanceof EnderPearlEntity || var3 instanceof ArrowEntity || var3 instanceof TridentEntity)
               && (!(var3 instanceof TridentEntity var4) || !var4.isOnGround())) {
               boolean var17 = var3.lastX != var3.getX() || var3.lastY != var3.getY() || var3.lastZ != var3.getZ();
               if (var17) {
                  Vec3d var5 = var3.getPos();
                  int var6 = Math.max(1, (int)(previous.compute() / 10.0F));

                  for (int var7 = 0; var7 < var6; var7++) {
                     this.handle(
                        this.frameCheck,
                        new Vec3d(
                           var5.x + MathHelper.nextDouble(Random.create(), -0.2, 0.2),
                           var5.y + MathHelper.nextDouble(Random.create(), -0.2, 0.2),
                           var5.z + MathHelper.nextDouble(Random.create(), -0.2, 0.2)
                        ),
                        new Vec3d(
                           MathHelper.nextDouble(Random.create(), -1.0, 1.0),
                           MathHelper.nextDouble(Random.create(), -0.3, 0.3),
                           MathHelper.nextDouble(Random.create(), -1.0, 1.0)
                        )
                     );
                  }
               }
            }
         }
      }

      if (source.process("В мире")) {
         if (Module.client.world == null || Module.client.player == null) {
            return;
         }

         int var14 = (int)summary.compute();
         int var16 = Math.max(1, (int)(previous.compute() / 2.0F));

         for (int var18 = 0; var18 < var16; var18++) {
            Vec3d var19 = Module.client.player.getPos().add(ClientMathUtil.resolve(-var14, var14), 0.0, ClientMathUtil.resolve(-var14, var14));
            BlockPos var20 = Module.client.world.getTopPosition(Type.MOTION_BLOCKING, BlockPos.ofFloored(var19));
            double var21 = var20.getX() + ClientMathUtil.resolve(0.0F, 1.0F);
            double var9 = var20.getZ() + ClientMathUtil.resolve(0.0F, 1.0F);
            double var11 = Module.client.player.getY() + ClientMathUtil.resolve(Module.client.player.getHeight(), var14);
            Vec3d var13 = new Vec3d(var21, var11, var9);

            while (!Module.client.world.isAir(BlockPos.ofFloored(var13)) && var13.y < Module.client.world.getTopYInclusive()) {
               var13 = var13.add(0.0, 1.0, 0.0);
            }

            this.handle(
               this.positionAdvance,
               var13,
               new Vec3d(
                  Module.client.player.getVelocity().x + ClientMathUtil.resolve(-2.0F, 2.0F),
                  ClientMathUtil.process(-0.2, 0.2),
                  Module.client.player.getVelocity().z + ClientMathUtil.resolve(-2.0F, 2.0F)
               )
            );
         }
      }

      long var15 = this.render();
      this.handle(this.serverRead, var15);
      this.handle(this.frameCheck, var15);
      this.handle(this.positionAdvance, var15);
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      MatrixStack var2 = var1.compute();
      Vec3d var3 = Module.client.gameRenderer.getCamera().getPos();
      long var4 = System.nanoTime();
      double var6 = (var4 - this.providerFetch) / 1.0E9;
      this.providerFetch = var4;
      BufferAllocator var8 = new BufferAllocator(262144);
      Immediate var9 = VertexConsumerProvider.immediate(var8);

      try {
         long var10 = this.render();
         long var12 = Math.min(400L, Math.max(100L, var10 / 5L));
         long var14 = Math.max(var12 + 1L, (long)((float)var10 * 0.62F));
         this.handle(var2, var9, var3, this.serverRead, var12, var14, var6);
         this.handle(var2, var9, var3, this.frameCheck, var12, var14, var6);
         this.handle(var2, var9, var3, this.positionAdvance, var12, var14, var6);
         var9.draw();
      } finally {
         var8.close();
      }
   }

   private long render() {
      return Math.max(250L, (long)(latest.compute() * 1000.0F));
   }

   private void handle(List<Particles.AnimationState> var1, long var2) {
      var1.removeIf(var2x -> var2x.onTick().handle((double)var2));
   }

   private void handle(MatrixStack var1, Immediate var2, Vec3d var3, List<Particles.AnimationState> var4, long var5, long var7, double var9) {
      if (!var4.isEmpty()) {
         var1.push();

         for (Particles.AnimationState var12 : var4) {
            var12.handle(matrixBlend.compute(), var9);
            boolean var13 = !var12.onTick().handle((double)var5);
            boolean var14 = var12.onTick().handle((double)var7);
            if (var13) {
               var12.select().handle(1.0, 0.4, Easings.current, true);
            } else if (var14) {
               var12.select().handle(0.0, 0.4, Easings.current, true);
            }

            if (var12.source.process()) {
               var12.source.handle();
            }

            float var15 = var12.source.update();
            int var16 = (int)(var15 * 255.0F);
            if (var16 > 0) {
               int var17 = PackedColor.update(var12.execute(), var16);
               Vec3d var18 = var12.compute();
               this.handle(var1, var2, var12, (float)var18.x, (float)var18.y, (float)var18.z, var12.current, var17, var16);
            }
         }

         var1.pop();
      }
   }

   private void handle(MatrixStack var1, Immediate var2, Particles.AnimationState var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      var1.push();
      RoundedRectRenderer.handle(var1, var4, var5, var6);
      var1.multiply(Module.client.gameRenderer.getCamera().getRotation());
      RenderLayer var10 = eventAttach.computeIfAbsent(
         var3.process(),
         var0 -> {
            Identifier var1x = var0.handle();
            return RenderLayer.of(
               var1x.toString(), 1024, false, true, vectorPerform, MultiPhaseParameters.builder().texture(new Texture(var1x, false)).build(false)
            );
         }
      );
      Entry var11 = var1.peek();
      Matrix4f var12 = var11.getPositionMatrix();
      Matrix3f var13 = var11.getNormalMatrix();
      VertexConsumer var14 = var2.getBuffer(var10);
      this.handle(var14, var12, var13, -var7, -var7, var7 * 2.0F, var7 * 2.0F, var8, var9);
      if (var3.data == Particles.Mode.BLOOM) {
         this.handle(var14, var12, var13, -var7 / 2.0F, -var7 / 2.0F, var7, var7, var8, var9);
      }

      var1.pop();
   }

   private void handle(VertexConsumer var1, Matrix4f var2, Matrix3f var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      int var10 = var8 >> 16 & 0xFF;
      int var11 = var8 >> 8 & 0xFF;
      int var12 = var8 & 0xFF;
      moduleCollect.set(0.0F, 0.0F, 1.0F);
      var3.transform(moduleCollect);
      moduleCollect.normalize();
      float var13 = var4;
      float var14 = var5;
      float var15 = var4 + var6;
      float var16 = var5 + var7;
      var1.vertex(var2, var13, var14, 0.0F)
         .color(var10, var11, var12, var9)
         .texture(0.0F, 1.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(moduleCollect.x, moduleCollect.y, moduleCollect.z);
      var1.vertex(var2, var15, var14, 0.0F)
         .color(var10, var11, var12, var9)
         .texture(1.0F, 1.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(moduleCollect.x, moduleCollect.y, moduleCollect.z);
      var1.vertex(var2, var15, var16, 0.0F)
         .color(var10, var11, var12, var9)
         .texture(1.0F, 0.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(moduleCollect.x, moduleCollect.y, moduleCollect.z);
      var1.vertex(var2, var13, var16, 0.0F)
         .color(var10, var11, var12, var9)
         .texture(0.0F, 0.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(moduleCollect.x, moduleCollect.y, moduleCollect.z);
   }

   @Override
   public void toggle() {
      super.toggle();
      this.refresh();
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.refresh();
   }

   public static class AnimationState {
      private Box instance;
      final Particles.Mode data;
      private Vec3d context;
      private Vec3d config;
      private final int state;
      private final int cache;
      private final int output;
      final float current;
      private static final double active = 0.05;
      private static final double mode = 0.0035;
      private static final double selection = 0.985;
      private static final double enabled = 0.55;
      private static final double renderer = 0.72;
      private static final double handler = 0.003;
      private static final double animationDraw = 1.0E-6;
      private final double pointEncode;
      private final ResettableTimer animator = new ResettableTimer();
      final DoubleAnimator source = new DoubleAnimator();

      public AnimationState(Particles.Mode var1, Vec3d var2, Vec3d var3, int var4, int var5, int var6, float var7, double var8) {
         double var10 = var7 / 2.0;
         this.instance = new Box(new Vec3d(var2.x - var10, var2.y - var10, var2.z - var10), new Vec3d(var2.x + var10, var2.y + var10, var2.z + var10));
         this.data = var1;
         this.context = var2;
         this.config = var3.multiply(0.05);
         this.state = var4;
         this.cache = var5;
         this.output = var6;
         this.current = var7;
         this.pointEncode = var8;
         this.animator.handle();
      }

      public void handle(boolean var1, double var2) {
         double var4 = var2 * 60.0 * this.pointEncode;
         if (var1 && Module.client.world != null) {
            this.config = this.config.multiply(Math.pow(0.985, var2 * 60.0)).subtract(0.0, 0.0035 * var2 * 60.0, 0.0);
            this.handle(this.config.x * var4, 0);
            this.handle(this.config.y * var4, 1);
            this.handle(this.config.z * var4, 2);
         } else {
            this.context = this.context.add(this.config.multiply(var4));
            this.refresh();
         }
      }

      private void handle(double var1, int var3) {
         if (!(Math.abs(var1) <= 1.0E-6)) {
            Box var4 = switch (var3) {
               case 0 -> this.instance.offset(var1, 0.0, 0.0);
               case 1 -> this.instance.offset(0.0, var1, 0.0);
               default -> this.instance.offset(0.0, 0.0, var1);
            };
            if (this.handle(var4)) {
               this.handle(var3);
            } else {
               this.instance = var4;

               this.context = switch (var3) {
                  case 0 -> this.context.add(var1, 0.0, 0.0);
                  case 1 -> this.context.add(0.0, var1, 0.0);
                  default -> this.context.add(0.0, 0.0, var1);
               };
            }
         }
      }

      private void handle(int var1) {
         double var2 = this.config.x;
         double var4 = this.config.y;
         double var6 = this.config.z;
         switch (var1) {
            case 0:
               var2 = -var2 * 0.55;
               break;
            case 1:
               if (var4 < 0.0) {
                  var2 *= 0.72;
                  var6 *= 0.72;
               }

               var4 = -var4 * 0.55;
               break;
            default:
               var6 = -var6 * 0.55;
         }

         this.config = new Vec3d(this.handle(var2), this.handle(var4), this.handle(var6));
      }

      private double handle(double var1) {
         return Math.abs(var1) < 0.003 ? 0.0 : var1;
      }

      private boolean handle(Box var1) {
         int var2 = MathHelper.floor(var1.minX + 1.0E-6);
         int var3 = MathHelper.floor(var1.minY + 1.0E-6);
         int var4 = MathHelper.floor(var1.minZ + 1.0E-6);
         int var5 = MathHelper.floor(var1.maxX - 1.0E-6);
         int var6 = MathHelper.floor(var1.maxY - 1.0E-6);
         int var7 = MathHelper.floor(var1.maxZ - 1.0E-6);
         Mutable var8 = new Mutable();

         for (int var9 = var2; var9 <= var5; var9++) {
            for (int var10 = var3; var10 <= var6; var10++) {
               for (int var11 = var4; var11 <= var7; var11++) {
                  var8.set(var9, var10, var11);
                  VoxelShape var12 = Module.client.world.getBlockState(var8).getCollisionShape(Module.client.world, var8);
                  if (!var12.isEmpty()) {
                     for (Box var14 : var12.getBoundingBoxes()) {
                        if (var1.intersects(var14.offset(var9, var10, var11))) {
                           return true;
                        }
                     }
                  }
               }
            }
         }

         return false;
      }

      private void refresh() {
         double var1 = this.current / 2.0;
         this.instance = new Box(
            new Vec3d(this.context.x - var1, this.context.y - var1, this.context.z - var1),
            new Vec3d(this.context.x + var1, this.context.y + var1, this.context.z + var1)
         );
      }
      public Box handle() {
         return this.instance;
      }
      public Particles.Mode process() {
         return this.data;
      }
      public Vec3d compute() {
         return this.context;
      }
      public Vec3d resolve() {
         return this.config;
      }
      public int update() {
         return this.state;
      }
      public int apply() {
         return this.cache;
      }
      public int execute() {
         return this.output;
      }
      public float prepare() {
         return this.current;
      }
      public double check() {
         return this.pointEncode;
      }
      public ResettableTimer onTick() {
         return this.animator;
      }
      public DoubleAnimator select() {
         return this.source;
      }
   }

   enum Mode {
      HEART("heart", false),
      STAR("star", false),
      SNOW("snowflake", false),
      BLOOM("firefly", false),
      DOLLAR("dollar", false),
      TRIANGLE("triangle", false),
      SAKURA("sakura", false),
      GEMINI("genshin", false),
      SIMS("rhombus", false);

      private final Identifier instance;
      private final boolean data;

      Mode(String var3, boolean var4) {
         this.instance = Identifier.of("wild", "textures/world/" + var3 + ".png");
         this.data = var4;
      }
      public Identifier handle() {
         return this.instance;
      }
      public boolean process() {
         return this.data;
      }
   }
}
