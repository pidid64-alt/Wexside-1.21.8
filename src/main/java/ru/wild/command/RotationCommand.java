package ru.wild.command;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import ru.wild.WildClient;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EntityAttackEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.automation.combat.RotationRecorder;
import ru.wild.core.Command;
import ru.wild.gui.screen.RotationAnalyticsScreen;
import ru.wild.modules.combat.AttackAura;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public final class RotationCommand extends Command {
   public RotationCommand() {
      super("ai", "Запись, обучение и воспроизведение AI-ротации", ".ai <train|learn|run|stop|log|lab|profile|list>");
      this.handle("train", List::of);
      this.handle("learn", List::of);
      this.handle("stop", List::of);
      this.handle("run", List::of);
      this.handle("log", List::of);
      this.handle("lab", List::of);
      this.handle("profile", List::of);
      this.handle("list", List::of);
   }

   public static void resolve() {
   }

   @Override
   public List<String> handle(String[] var1) {
      if (var1.length == 2) {
         String var4 = var1[1].toLowerCase(Locale.ROOT);
         return List.of("train", "learn", "run", "stop", "log", "lab", "profile", "list").stream().filter(var1x -> var1x.startsWith(var4)).toList();
      }

      if (var1.length == 3) {
         String var2 = var1[1].toLowerCase(Locale.ROOT);
         if (var2.equals("profile") || var2.equals("run") || var2.equals("train") || var2.equals("learn")) {
            String var3 = var1[2].toLowerCase(Locale.ROOT);
            return RotationRecorder.advancePosition().stream().filter(var1x -> var1x.toLowerCase(Locale.ROOT).startsWith(var3)).toList();
         }
      }

      return List.of();
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         ChatLogger.handle("Использование: " + this.compute());
      } else {
         String var2;
         switch (var1[0].toLowerCase(Locale.ROOT)) {
            case "train":
               if (var1.length >= 2) {
                  RotationRecorder.process(var1[1]);
               }

               var2 = RotationRecorder.handle();
               break;
            case "learn":
               if (var1.length >= 2) {
                  RotationRecorder.process(var1[1]);
               }

               var2 = RotationRecorder.resolve();
               break;
            case "log":
               boolean var5 = !AttackAura.summary.compute();
               AttackAura.summary.process(var5);
               var2 = "AI логи " + (var5 ? "ВКЛ" : "ВЫКЛ") + ". Файл: " + RotationRecorder.unload();
               break;
            case "lab":
               MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new RotationAnalyticsScreen()));
               var2 = "AI Lab открыт.";
               break;
            case "stop":
               var2 = RotationRecorder.process();
               break;
            case "run":
               if (var1.length >= 2) {
                  RotationRecorder.process(var1[1]);
               }

               var2 = RotationRecorder.compute();
               if (RotationRecorder.projectItem()) {
                  this.update();
               }
               break;
            case "profile":
               var2 = var1.length >= 2
                  ? RotationRecorder.process(var1[1])
                  : "Текущий профиль: " + RotationRecorder.readServer() + ". Использование: .ai profile <имя>";
               break;
            case "list":
               var2 = RotationRecorder.checkFrame();
               break;
            default:
               var2 = "Использование: " + this.compute();
         }

         ChatLogger.handle(var2);
      }
   }

   @EventHandler
   public void handle(EntityAttackEvent var1) {
      RotationRecorder.handle(var1);
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      RotationRecorder.update();
   }

   private void update() {
      if (WildClient.instance != null && WildClient.instance.data != null && AttackAura.pending.config.contains("AI")) {
         AttackAura.pending.state = "AI";
         AttackAura.pending.current = AttackAura.pending.config.indexOf("AI");
         AttackAura var1 = WildClient.instance.data.handle(AttackAura.class);
         if (var1 != null && !var1.enabled) {
            var1.setEnabled(true);
         }
      } else {
         ChatLogger.handle("Режим AI недоступен для текущего профиля.");
         RotationRecorder.process();
      }
   }

   static {
      Loader.initialize();
   }
}
