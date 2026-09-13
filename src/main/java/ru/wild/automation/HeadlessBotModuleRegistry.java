package ru.wild.automation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.event.Event;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.LocalEventDispatcher;
import ru.wild.api.module.ModuleCategory;
import ru.wild.core.ClientContextExecutor;
import ru.wild.modules.misc.AutoDrop;
import ru.wild.modules.player.AntiAFK;

public final class HeadlessBotModuleRegistry {
   private static final Set<String> instance = Set.of(
      "AutoBuy",
      "AutoResell",
      "AutoSell",
      "AhHelper",
      "ItemScroller",
      "Removals",
      "ServerHelperModule",
      "AutoPotionModule",
      "ElytraHelper",
      "FreeCamera",
      "MenuSettingsModule",
      "ClientUtilModule",
      "UnHook",
      "Capes",
      "CameraClip",
      "FakePlayer",
      "ActionRecorder",
      "TapeMouse",
      "MiddleClick",
      "RotationLabModule",
      "TestModule",
      "Blink",
      "BaseFinder",
      "FriendManagerModule",
      "ServerJoiner",
      "ServerDHelper",
      "ChatHelper",
      "HitSounds",
      "TotemVoices"
   );
   private final HeadlessBotSession data;
   private final LocalEventDispatcher context = new LocalEventDispatcher();
   private final Map<String, Module> config = new LinkedHashMap<>();
   private boolean state;

   public HeadlessBotModuleRegistry(HeadlessBotSession var1) {
      this.data = var1;
   }

   public void handle() {
      if (!this.state) {
         if (this.data.execute() != null && this.data.prepare() != null && this.data.check() != null && this.data.onTick()) {
            this.state = true;
            ClientContextExecutor.handle(this.data, this::update);
         }
      }
   }

   private void update() {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         for (Module var2 : new ArrayList<>(WildClient.instance.data.instance)) {
            if (var2 != null && var2.category != ModuleCategory.Visuals && !instance.contains(var2.getClass().getSimpleName())) {
               try {
                  Constructor var3 = var2.getClass().getDeclaredConstructor();
                  var3.setAccessible(true);
                  this.handle((Module)var3.newInstance());
               } catch (Throwable var4) {
               }
            }
         }
      } else {
         this.handle(AutoDrop::new);
         this.handle(AntiAFK::new);
      }
   }

   private void handle(Supplier<Module> var1) {
      try {
         this.handle((Module)var1.get());
      } catch (Throwable var3) {
      }
   }

   private void handle(Module var1) {
      if (var1 != null) {
         var1.enabled = false;
         this.config.putIfAbsent(var1.displayName.toLowerCase(Locale.ROOT), var1);
      }
   }

   public Module handle(String var1) {
      this.handle();
      return var1 == null ? null : this.config.get(var1.toLowerCase(Locale.ROOT));
   }

   public List<Module> process() {
      this.handle();
      return new ArrayList<>(this.config.values());
   }

   public String compute() {
      this.handle();
      return String.join(", ", this.config.keySet());
   }

   public boolean process(String var1) {
      Module var2 = this.handle(var1);
      return var2 != null && var2.enabled;
   }

   public void handle(String var1, boolean var2) {
      Module var3 = this.handle(var1);
      if (var3 != null && var3.enabled != var2) {
         if (var2) {
            this.process(var3);
         } else {
            this.compute(var3);
         }
      }
   }

   public void compute(String var1) {
      Module var2 = this.handle(var1);
      if (var2 != null) {
         this.handle(var1, !var2.enabled);
      }
   }

   public void handle(Event var1) {
      this.context.handle(var1);
   }

   public void resolve() {
      for (Module var2 : this.config.values()) {
         if (var2.enabled) {
            this.compute(var2);
         }
      }
   }

   private void process(Module var1) {
      var1.enabled = true;

      try {
         ClientContextExecutor.handle(this.data, () -> {
            boolean var3x = false /* VF: Semaphore variable */;

            try {
               var3x = true;
               var1.handle();
               var3x = false;
            } finally {
               if (var3x) {
                  EventHandlerInvoker.process(var1);
               }
            }

            EventHandlerInvoker.process(var1);
         });
         if (var1.enabled) {
            this.context.handle(var1);
         }
      } catch (Throwable var3) {
         var1.enabled = false;
         this.context.process(var1);
         EventHandlerInvoker.process(var1);
         System.err.println("[WildBot] " + this.data.handle() + ": failed to enable " + var1.displayName);
         var3.printStackTrace();
      }
   }
   private void compute(Module var1) {
      this.context.process(var1);
      var1.enabled = false;
      boolean var6 = false /* VF: Semaphore variable */;

      label41: {
         try {
            var6 = true;
            ClientContextExecutor.handle(this.data, var1::process);
            var6 = false;
            break label41;
         } catch (Throwable var7) {
            System.err.println("[WildBot] " + this.data.handle() + ": failed to disable " + var1.displayName);
            var7.printStackTrace();
            var6 = false;
         } finally {
            if (var6) {
               EventHandlerInvoker.process(var1);
            }
         }

         EventHandlerInvoker.process(var1);
         return;
      }

      EventHandlerInvoker.process(var1);
   }
}
