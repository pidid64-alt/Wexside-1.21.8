package ru.wild.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import ru.wild.WildClient;
import ru.wild.core.Command;
import ru.wild.modules.misc.CocoaFarm;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class CocoaFarmConfig extends Command {
   private final Gson instance = new GsonBuilder().setPrettyPrinting().create();
   private final File data = new File(WildClient.instance.cache, "cocoafarm.cfg");

   public CocoaFarmConfig() {
      super("cocoa", "Управление границами фермы какао", ".cocoa <pos1/pos2/clear/info>");
      this.handle("pos1", List::of);
      this.handle("pos2", List::of);
      this.handle("clear", List::of);
      this.handle("info", List::of);
      this.prepare();
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         ChatLogger.handle("§cИспользование: " + this.compute());
      } else {
         switch (var1[0].toLowerCase()) {
            case "pos1":
               this.resolve();
               break;
            case "pos2":
               this.update();
               break;
            case "clear":
               this.apply();
               break;
            case "info":
               this.execute();
               break;
            default:
               ChatLogger.handle("§cНеизвестная подкоманда.");
         }
      }
   }

   @Compile
   private void resolve() {
      if (toggleState.player == null) {
         ChatLogger.handle("§cВы должны быть в игре!");
      } else {
         BlockPos var1 = toggleState.player.getBlockPos();
         CocoaFarm.handle(var1);
         this.check();
         ChatLogger.handle("§aПозиция 1 установлена: §f" + this.handle(var1));
      }
   }

   @Compile
   private void update() {
      if (toggleState.player == null) {
         ChatLogger.handle("§cВы должны быть в игре!");
      } else {
         BlockPos var1 = toggleState.player.getBlockPos();
         CocoaFarm.process(var1);
         this.check();
         ChatLogger.handle("§aПозиция 2 установлена: §f" + this.handle(var1));
      }
   }

   @Compile
   private void apply() {
      CocoaFarm.refresh();
      this.check();
      ChatLogger.handle("§cКоординаты фермы какао очищены.");
   }

   @Compile
   private void execute() {
      BlockPos var1 = CocoaFarm.render();
      BlockPos var2 = CocoaFarm.tick();
      if (var1 == null) {
         if (var2 == null) {
            ChatLogger.handle("§7Координаты фермы не установлены.");
         } else {
            ChatLogger.handle("§fИнформация о ферме какао:");
            ChatLogger.handle(" §7Позиция 1: §cне установлена");
            ChatLogger.handle(" §7Позиция 2: §f" + this.handle(var2));
         }
      } else {
         ChatLogger.handle("§fИнформация о ферме какао:");
         ChatLogger.handle(" §7Позиция 1: §f" + this.handle(var1));
         if (var2 == null) {
            ChatLogger.handle(" §7Позиция 2: §cне установлена");
         } else {
            ChatLogger.handle(" §7Позиция 2: §f" + this.handle(var2));
            ChatLogger.handle(
               " §7Размер области: §f"
                  + (Math.abs(var2.getX() - var1.getX()) + 1)
                  + "x"
                  + (Math.abs(var2.getY() - var1.getY()) + 1)
                  + "x"
                  + (Math.abs(var2.getZ() - var1.getZ()) + 1)
            );
         }
      }
   }

   @Compile
   private String handle(BlockPos var1) {
      return var1.getX() + ", " + var1.getY() + ", " + var1.getZ();
   }

   @Compile
   private void prepare() {
      if (this.data.exists()) {
         try (FileReader var1 = new FileReader(this.data)) {
            CocoaFarmConfig.State var2 = (CocoaFarmConfig.State)this.instance.fromJson(var1, CocoaFarmConfig.State.class);
            if (var2 != null) {
               if (var2.instance != null) {
                  CocoaFarm.handle(new BlockPos(var2.instance.instance, var2.instance.data, var2.instance.context));
               }

               if (var2.data != null) {
                  CocoaFarm.process(new BlockPos(var2.data.instance, var2.data.data, var2.data.context));
               }
            }
         } catch (Exception var6) {
         }
      }
   }

   @Compile
   private void check() {
      try {
         if (!this.data.getParentFile().exists()) {
            this.data.getParentFile().mkdirs();
         }

         CocoaFarmConfig.State var1 = new CocoaFarmConfig.State();
         BlockPos var2 = CocoaFarm.render();
         BlockPos var3 = CocoaFarm.tick();
         if (var2 != null) {
            var1.instance = new CocoaFarmConfig.PrimaryState(var2.getX(), var2.getY(), var2.getZ());
         }

         if (var3 != null) {
            var1.data = new CocoaFarmConfig.PrimaryState(var3.getX(), var3.getY(), var3.getZ());
         }

         try (FileWriter var4 = new FileWriter(this.data)) {
            this.instance.toJson(var1, var4);
         }
      } catch (Exception var9) {
      }
   }

   static {
      Loader.initialize();
   }

   static class PrimaryState {
      int instance;
      int data;
      int context;

      PrimaryState(int var1, int var2, int var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }
   }

   static class State {
      CocoaFarmConfig.PrimaryState instance;
      CocoaFarmConfig.PrimaryState data;
   }
}
