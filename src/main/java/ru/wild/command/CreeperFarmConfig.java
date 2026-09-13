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
import ru.wild.modules.misc.CreeperFarm;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class CreeperFarmConfig extends Command {
   private final Gson instance = new GsonBuilder().setPrettyPrinting().create();
   private final File data = new File(WildClient.instance.cache, "creeperfarm.cfg");

   public CreeperFarmConfig() {
      super("cf", "Управление границами фермы криперов", ".cf <pos1/pos2/clear/info>");
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
            case "info":
               this.execute();
               break;
            case "pos1":
               this.resolve();
               break;
            case "pos2":
               this.update();
               break;
            case "clear":
               this.apply();
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
         CreeperFarm.handle(var1);
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
         CreeperFarm.process(var1);
         this.check();
         ChatLogger.handle("§aПозиция 2 установлена: §f" + this.handle(var1));
      }
   }

   @Compile
   private void apply() {
      CreeperFarm.refresh();
      this.check();
      ChatLogger.handle("§cКоординаты фермы криперов очищены.");
   }

   @Compile
   private void execute() {
      BlockPos var1 = CreeperFarm.render();
      BlockPos var2 = CreeperFarm.tick();
      if (var1 == null) {
         if (var2 == null) {
            ChatLogger.handle("§7Координаты фермы не установлены.");
         } else {
            ChatLogger.handle("§fИнформация о ферме криперов:");
            ChatLogger.handle(" §7Позиция 1: §cне установлена");
            ChatLogger.handle(" §7Позиция 2: §f" + this.handle(var2));
         }
      } else {
         ChatLogger.handle("§fИнформация о ферме криперов:");
         ChatLogger.handle(" §7Позиция 1: §f" + this.handle(var1));
         if (var2 == null) {
            ChatLogger.handle(" §7Позиция 2: §cне установлена");
         } else {
            ChatLogger.handle(" §7Позиция 2: §f" + this.handle(var2));
            int var3 = Math.abs(var2.getX() - var1.getX()) + 1;
            int var4 = Math.abs(var2.getY() - var1.getY()) + 1;
            int var5 = Math.abs(var2.getZ() - var1.getZ()) + 1;
            ChatLogger.handle(" §7Размер области: §f" + var3 + "x" + var4 + "x" + var5);
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
            CreeperFarmConfig.State var2 = (CreeperFarmConfig.State)this.instance.fromJson(var1, CreeperFarmConfig.State.class);
            if (var2 != null) {
               if (var2.instance != null) {
                  CreeperFarm.handle(new BlockPos(var2.instance.instance, var2.instance.data, var2.instance.context));
               }

               if (var2.data != null) {
                  CreeperFarm.process(new BlockPos(var2.data.instance, var2.data.data, var2.data.context));
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

         CreeperFarmConfig.State var1 = new CreeperFarmConfig.State();
         BlockPos var2 = CreeperFarm.render();
         BlockPos var3 = CreeperFarm.tick();
         if (var2 != null) {
            var1.instance = new CreeperFarmConfig.PrimaryState(var2.getX(), var2.getY(), var2.getZ());
         }

         if (var3 != null) {
            var1.data = new CreeperFarmConfig.PrimaryState(var3.getX(), var3.getY(), var3.getZ());
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
      CreeperFarmConfig.PrimaryState instance;
      CreeperFarmConfig.PrimaryState data;
   }
}
