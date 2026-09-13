package ru.wild.command;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import org.joml.Matrix4f;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.core.Command;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class XrayCommand extends Command {
   private static final int instance = 16777215;
   private static final int data = 12;
   private static final int context = 1;
   private static final int config = 64;
   private static final long state = 250L;
   private static final int cache = 4096;
   private static final List<String> output = List.of("clear", "off", "reset", "help");
   private boolean current;
   private Block active;
   private Identifier mode;
   private int renderer = 16777215;
   private int handler = 12;
   private long animationDraw;
   private final List<BlockPos> pointEncode = new ArrayList<>();
   private static final RenderPipeline animator = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "xray_box"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer source = RenderLayer.of("xray_box", 4096, false, true, animator, MultiPhaseParameters.builder().build(false));

   public XrayCommand() {
      super("xray", "Подсветка блока в заданном радиусе", ".xray <block|clear/off/reset/help> [r,g,b] [radius]");
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         this.apply();
      } else {
         String var2 = var1[0];
         Locale var3 = Locale.ROOT;
         if (var2 != null) {
            var2 = var2.toLowerCase(var3);
         }

         String var4 = "help";
         if (var2 != null && var2.equals(var4)) {
            this.apply();
         } else if (this.handle(var2)) {
            this.update();
         } else {
            Identifier var5 = this.process(var1[0]);
            if (var5 == null) {
               String var15 = var1[0];
               ChatLogger.handle("§cНекорректный id блока: §f" + var15);
            } else {
               DefaultedRegistry var6 = Registries.BLOCK;
               Optional var7 = var6 == null ? null : var6.getOptionalValue(var5);
               if (var7 != null && var7.isEmpty()) {
                  String var16 = String.valueOf(var5);
                  ChatLogger.handle("§cБлок не найден: §f" + var16);
               } else {
                  int var8 = this.renderer;
                  int var9 = this.handler;
                  if (var1.length >= 2) {
                     String var10 = var1[1];
                     if (var10 != null && var10.contains(",")) {
                        Integer var19 = this.compute(var1[1]);
                        if (var19 == null) {
                           ChatLogger.handle("§cЦвет указывается в RGB: §f255,255,255");
                           return;
                        }

                        var8 = var19;
                     } else {
                        Integer var11 = this.resolve(var1[1]);
                        if (var11 == null) {
                           ChatLogger.handle("§cРадиус должен быть числом от 1 до 64.");
                           return;
                        }

                        var9 = var11;
                     }
                  }

                  if (var1.length >= 3) {
                     Integer var17 = this.resolve(var1[2]);
                     if (var17 == null) {
                        ChatLogger.handle("§cРадиус должен быть числом от 1 до 64.");
                        return;
                     }

                     var9 = var17;
                  }

                  Block var18 = var7 == null ? null : (Block)var7.get();
                  this.active = var18;
                  this.mode = var5;
                  this.renderer = var8;
                  this.handler = var9;
                  this.current = true;
                  this.animationDraw = 0L;
                  List var20 = this.pointEncode;
                  if (var20 != null) {
                     var20.clear();
                  }

                  String var12 = this.handle(this.mode);
                  String var13 = this.handle(this.renderer);
                  int var14 = this.handler;
                  ChatLogger.handle("§aXRay: §f" + var12 + " §7RGB " + var13 + " §7радиус " + var14);
               }
            }
         }
      }
   }

   @Override
   public List<String> handle(String[] var1) {
      if (var1.length != 2) {
         if (var1.length == 3 && !this.handle(var1[1].toLowerCase(Locale.ROOT))) {
            String var8 = var1[2].toLowerCase(Locale.ROOT);
            return List.of("255,255,255", "255,0,0", "0,255,0", "0,128,255", String.valueOf(12)).stream().filter(var1x -> var1x.startsWith(var8)).toList();
         } else if (var1.length == 4 && !this.handle(var1[1].toLowerCase(Locale.ROOT))) {
            String var7 = var1[3].toLowerCase(Locale.ROOT);
            return List.of("8", "12", "15", "24", "32", "64").stream().filter(var1x -> var1x.startsWith(var7)).toList();
         } else {
            return List.of();
         }
      } else {
         String var2 = var1[1].toLowerCase(Locale.ROOT);
         ArrayList var3 = new ArrayList();

         for (String var5 : output) {
            if (var5.startsWith(var2)) {
               var3.add(var5);
            }
         }

         for (Identifier var10 : Registries.BLOCK.getIds()) {
            String var6 = this.handle(var10);
            if (var6.startsWith(var2)) {
               var3.add(var6);
               if (var3.size() < 30) {
                  continue;
               }
               break;
            }
         }

         return var3;
      }
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (this.current && this.active != null && toggleState.world != null && toggleState.player != null) {
         this.resolve();
         if (!this.pointEncode.isEmpty()) {
            Immediate var2 = WorldVertexBuffer.handle();

            try {
               Vec3d var3 = toggleState.gameRenderer.getCamera().getPos();
               Matrix4f var4 = var1.compute().peek().getPositionMatrix();
               VertexConsumer var5 = var2.getBuffer(source);
               Color var6 = new Color(this.renderer);
               Color var7 = new Color(var6.getRed(), var6.getGreen(), var6.getBlue(), 120);
               Color var8 = new Color(var6.getRed(), var6.getGreen(), var6.getBlue(), 0);

               for (BlockPos var10 : this.pointEncode) {
                  if (toggleState.world.getBlockState(var10).isOf(this.active)) {
                     float var11 = (float)(var10.getX() - var3.x);
                     float var12 = (float)(var10.getY() - var3.y);
                     float var13 = (float)(var10.getZ() - var3.z);
                     float var14 = var11 + 1.0F;
                     float var15 = var12 + 1.0F;
                     float var16 = var13 + 1.0F;
                     this.handle(var5, var4, var11, var12, var13, var14, var15, var16, var7, var8);
                  }
               }
            } finally {
               WorldVertexBuffer.process();
            }
         }
      }
   }

   private void resolve() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.animationDraw >= 250L) {
         this.animationDraw = var1;
         this.pointEncode.clear();
         BlockPos var3 = toggleState.player.getBlockPos();
         Mutable var4 = new Mutable();
         int var5 = Math.max(toggleState.world.getBottomY(), var3.getY() - this.handler);
         int var6 = Math.min(toggleState.world.getTopYInclusive(), var3.getY() + this.handler);

         for (int var7 = var3.getX() - this.handler; var7 <= var3.getX() + this.handler; var7++) {
            for (int var8 = var5; var8 <= var6; var8++) {
               for (int var9 = var3.getZ() - this.handler; var9 <= var3.getZ() + this.handler; var9++) {
                  var4.set(var7, var8, var9);
                  if (toggleState.world.getBlockState(var4).isOf(this.active)) {
                     this.pointEncode.add(var4.toImmutable());
                  }
               }
            }
         }
      }
   }

   private void update() {
      this.current = false;
      this.active = null;
      this.mode = null;
      this.pointEncode.clear();
      ChatLogger.handle("§7XRay выключен.");
   }

   private void apply() {
      ChatLogger.handle("§cИспользование: " + this.compute());
      ChatLogger.handle("§7Пример: §f.xray diamond_ore 255,255,255 15");
      ChatLogger.handle("§7Команды: §f.xray clear §7/ §f.xray off §7/ §f.xray reset");
   }

   private boolean handle(String var1) {
      return var1 != null && (var1.equals("off") || var1.equals("clear") || var1.equals("reset") || var1.equals("help"));
   }

   private Identifier process(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.trim().toLowerCase(Locale.ROOT);
         if (!var2.contains(":")) {
            var2 = "minecraft:" + var2;
         }

         return Identifier.tryParse(var2);
      } else {
         return null;
      }
   }

   private Integer compute(String var1) {
      String[] var2 = var1.split(",");
      if (var2.length != 3) {
         return null;
      }

      int[] var3 = new int[3];

      for (int var4 = 0; var4 < 3; var4++) {
         try {
            var3[var4] = Integer.parseInt(var2[var4].trim());
         } catch (NumberFormatException var6) {
            return null;
         }

         if (var3[var4] < 0 || var3[var4] > 255) {
            return null;
         }
      }

      return var3[0] << 16 | var3[1] << 8 | var3[2];
   }

   private Integer resolve(String var1) {
      try {
         int var2 = Integer.parseInt(var1.trim());
         return var2 >= 1 && var2 <= 64 ? var2 : null;
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   private String handle(int var1) {
      return (var1 >> 16 & 0xFF) + "," + (var1 >> 8 & 0xFF) + "," + (var1 & 0xFF);
   }

   private String handle(Identifier var1) {
      if (var1 == null) {
         return "";
      } else {
         return "minecraft".equals(var1.getNamespace()) ? var1.getPath() : var1.toString();
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8, Color var9, Color var10) {
      int var11 = var9.getRed();
      int var12 = var9.getGreen();
      int var13 = var9.getBlue();
      int var14 = var9.getAlpha();
      int var15 = var10.getRed();
      int var16 = var10.getGreen();
      int var17 = var10.getBlue();
      int var18 = var10.getAlpha();
      var1.vertex(var2, var3, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var7, var8).color(var15, var16, var17, var18);
   }

   static {
      Loader.initialize();
   }
}
