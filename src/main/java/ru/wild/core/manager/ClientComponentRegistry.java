package ru.wild.core.manager;

import java.util.HashMap;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.automation.RotationController;
import ru.wild.core.ClientComponent;
import ru.wild.core.InteractionStateTracker;
import ru.wild.core.RotationDebugProbe;
import ru.wild.core.ViewRotationCoordinator;

public final class ClientComponentRegistry extends HashMap<Class<? extends ClientComponent>, ClientComponent> {
   public void handle() {
      this.handle(new ViewRotationCoordinator(), new RotationController(), new InteractionStateTracker(), new RotationDebugProbe());
      this.values().forEach(var0 -> EventHandlerInvoker.handle(var0));
   }

   public void handle(ClientComponent... var1) {
      for (ClientComponent var5 : var1) {
         this.put((Class<? extends ClientComponent>)var5.getClass(), var5);
      }
   }

   public void process(ClientComponent... var1) {
      for (ClientComponent var5 : var1) {
         EventHandlerInvoker.process(var5);
         this.remove(var5.getClass());
      }
   }

   public <T extends ClientComponent> T handle(Class<T> var1) {
      return this.values().stream().filter(var1x -> var1x.getClass() == var1).map(var1::cast).findFirst().orElse(null);
   }
}
