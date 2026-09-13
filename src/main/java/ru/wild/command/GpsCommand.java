package ru.wild.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.PacketEvent;
import ru.wild.core.Command;
import ru.wild.gui.hud.PartyWaypointRenderer;
import ru.wild.gui.hud.WaypointArrowRenderer;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.ChatLogger;
import ru.wild.util.world.DimensionLabelRegistry;
import ru.wild.util.world.Waypoint;

public class GpsCommand extends Command {
   private static final String config = "Метка";
   private static final double state = 3.75;
   public static Vector2f instance = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
   public static float data = Float.MAX_VALUE;
   public static String context = "Метка";
   private static volatile boolean cache;
   private static String output = "Метка";
   private final PartyWaypointRenderer current = new PartyWaypointRenderer();
   private final WaypointArrowRenderer active = new WaypointArrowRenderer();
   private final Map<String, GpsCommand.State> mode = new HashMap<>();
   private final GpsCommand.State renderer = new GpsCommand.State();
   private final StringBuilder handler = new StringBuilder(32);

   public GpsCommand() {
      super("gps", "Добавление меток, для ивентов и тд", ".gps off [название] | .gps <x> <z> [название] | .gps <x> <y> <z> [название]");
      this.handle("off", DimensionLabelRegistry::apply);
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         if (toggleState.player != null) {
            handle((float)toggleState.player.getX(), (float)toggleState.player.getY(), (float)toggleState.player.getZ(), "Моя позиция");
         }
      } else {
         if ("off".equalsIgnoreCase(var1[0])) {
            this.resolve(var1);
         } else if ("add".equalsIgnoreCase(var1[0])) {
            this.compute(Arrays.copyOfRange(var1, 1, var1.length));
         } else {
            this.compute(var1);
         }
      }
   }

   @Compile
   private void compute(String[] var1) {
      int var2 = update(var1);
      if (var2 < 2) {
         if (toggleState.player != null) {
            handle((float)toggleState.player.getX(), (float)toggleState.player.getY(), (float)toggleState.player.getZ(), "Моя позиция");
         } else {
            ChatLogger.handle("§cНужны хотя бы две координаты: §f.gps 100 -200");
         }
      } else if (toggleState.player != null && toggleState.world != null) {
         boolean var3 = var2 >= 3;
         float var4 = Float.parseFloat(var1[0]);
         float var5 = var3 ? Float.parseFloat(var1[1]) : (float)(toggleState.player.getY() + 5.0);
         float var6 = Float.parseFloat(var1[var3 ? 2 : 1]);
         String var7 = handle(var1, var3 ? 3 : 2);
         handle(var4, var5, var6, var7);
         if (!var3) {
            ChatLogger.handle("§7Высота взята на 5 блоков выше игрока");
         }
      }
   }

   @Compile
   private void resolve(String[] var1) {
      if (var1.length == 0) {
         int var3 = DimensionLabelRegistry.handle();
         update();
         if (var3 > 0) {
            ChatLogger.handle("§eGps метки были выключены");
         } else {
            ChatLogger.handle("§7Активных меток нет");
         }
      } else {
         String var2 = handle(var1, 1);
         if (!DimensionLabelRegistry.handle(var2)) {
            ChatLogger.handle("§cМетки с таким названием нет: §f" + var2);
         } else {
            if (var2.equalsIgnoreCase(output)) {
               update();
            }

            ChatLogger.handle("§eМетка убрана: §f" + var2);
         }
      }
   }

   private static int update(String[] var0) {
      int var1 = 0;

      for (String var5 : var0) {
         try {
            Float.parseFloat(var5);
            var1++;
         } catch (NumberFormatException var7) {
            break;
         }
      }

      return var1;
   }

   private static String handle(String[] var0, int var1) {
      if (var0.length <= var1) {
         return "Метка";
      }

      String var2 = String.join(" ", Arrays.copyOfRange(var0, var1, var0.length)).trim();
      return var2.isEmpty() ? "Метка" : var2;
   }

   public static void handle(float var0, float var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      float var3 = var2.player == null ? 64.0F : (float)(var2.player.getY() + 5.0);
      handle(var0, var3, var1, "Метка");
   }

   public static void handle(float var0, float var1, float var2, String var3) {
      MinecraftClient var4 = MinecraftClient.getInstance();
      if (var4.world != null) {
         String var5 = var3 != null && !var3.isBlank() ? var3 : "Метка";
         DimensionLabelRegistry.handle(new Waypoint(var5, var0, var1, var2, var4.world.getRegistryKey(), var4.world.getDimension().coordinateScale()));
         output = var5;
         context = var5;
         instance = new Vector2f(var0, var2);
         data = var1;
         cache = false;
         ChatLogger.handle(
            "§a[GPS] Метка '" + var5 + "' установлена на X: " + MathHelper.floor(var0) + " Y: " + MathHelper.floor(var1) + " Z: " + MathHelper.floor(var2)
         );
      }
   }

   private static void update() {
      instance = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
      data = Float.MAX_VALUE;
      context = "Метка";
      output = "Метка";
      cache = false;
   }

   public static void resolve() {
      cache = true;
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof GameMessageS2CPacket var2) {
         String var4 = var2.content().getString().toLowerCase();
         if (var4.contains("заверш") || var4.contains("окончен") || var4.contains("время вышло") || var4.contains("вы у цели")) {
            resolve();
         }
      }
   }

   @EventHandler(handle = 3)
   public void handle(HudRenderContext var1) {
      this.apply();
      if (!DimensionLabelRegistry.compute() && toggleState.player != null && toggleState.world != null) {
         if (!toggleState.options.hudHidden) {
            DimensionLabelRegistry.process();
            if (cache) {
               cache = false;
               DimensionLabelRegistry.handle(output);
               update();
            }

            RoundedRectRenderer var2 = var1.resolve();
            double var3 = toggleState.world.getDimension().coordinateScale();
            boolean var5 = false;

            for (int var6 = 0; var6 < DimensionLabelRegistry.resolve(); var6++) {
               Waypoint var7 = DimensionLabelRegistry.handle(var6);
               float var8 = var7.prepare();
               if (!(var8 <= 0.01F)) {
                  double var9 = var7.handle(var3);
                  Vec3d var11 = new Vec3d(var7.process() * var9, var7.compute(), var7.resolve() * var9);
                  boolean var12 = this.active.handle(var11);
                  double var13 = this.active.context;
                  if (var7.update() && var13 <= 3.75) {
                     var7.apply();
                     if (var7.handle().equalsIgnoreCase(output)) {
                        update();
                     }
                  }

                  String var15 = this.handle(var7);
                  String var16 = this.handle(var13);
                  if (!var12) {
                     WaypointArrowRenderer.handle(var2, var11, var7.handle(), var16, var8, var1.apply(), var1.execute());
                  } else {
                     if (!var5) {
                        PartyWaypointRenderer.handle(var2);
                        var5 = true;
                     }

                     this.current.handle(var2, this.active.instance, this.active.data, var7.handle(), var15, var16, null, var8, var7.check());
                  }
               }
            }
         }
      }
   }

   private void apply() {
      if (instance.getX() == Float.MAX_VALUE && instance.getY() == Float.MAX_VALUE) {
         if (DimensionLabelRegistry.process(output) != null) {
            DimensionLabelRegistry.handle(output);
            update();
         }
      }
   }

   private String handle(Waypoint var1) {
      int var2 = MathHelper.floor(var1.process());
      int var3 = MathHelper.floor(var1.compute());
      int var4 = MathHelper.floor(var1.resolve());
      GpsCommand.State var5 = this.mode.computeIfAbsent(var1.handle(), var0 -> new GpsCommand.State());
      if (var5.instance == null || var5.data != var2 || var5.context != var3 || var5.config != var4) {
         var5.data = var2;
         var5.context = var3;
         var5.config = var4;
         this.handler.setLength(0);
         this.handler.append(var2).append(", ").append(var3).append(", ").append(var4);
         var5.instance = this.handler.toString();
      }

      return var5.instance;
   }

   private String handle(double var1) {
      int var3 = (int)Math.round(var1);
      if (this.renderer.instance == null || this.renderer.data != var3) {
         this.renderer.data = var3;
         this.handler.setLength(0);
         this.handler.append(var3).append(" м");
         this.renderer.instance = this.handler.toString();
      }

      return this.renderer.instance;
   }

   static {
      Loader.initialize();
   }

   static final class State {
      String instance;
      int data = Integer.MIN_VALUE;
      int context = Integer.MIN_VALUE;
      int config = Integer.MIN_VALUE;
   }
}
