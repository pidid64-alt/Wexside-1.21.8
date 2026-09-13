package ru.wild.api.event;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LocalEventDispatcher {
   private final Map<Class<?>, List<LocalEventDispatcher.State>> instance = new ConcurrentHashMap<>();

   public void handle(Object var1) {
      for (Method var5 : var1.getClass().getDeclaredMethods()) {
         if (var5.getParameterCount() == 1 && var5.isAnnotationPresent(EventHandler.class)) {
            Class<?> var6 = var5.getParameterTypes()[0];
            if (Event.class.isAssignableFrom(var6)) {
               var5.setAccessible(true);
               byte var7 = var5.getAnnotation(EventHandler.class).handle();
               List<LocalEventDispatcher.State> var8 = this.instance.computeIfAbsent(var6, var0 -> new CopyOnWriteArrayList<>());

               for (LocalEventDispatcher.State var10 : var8) {
                  if (var10.instance == var1 && var10.data.equals(var5)) {
                     return;
                  }
               }

               var8.add(new LocalEventDispatcher.State(var1, var5, var7));
               var8.sort(Comparator.comparingInt(var0 -> handle(var0.context)));
            }
         }
      }
   }

   public void process(Object var1) {
      for (Entry<Class<?>, List<LocalEventDispatcher.State>> var3 : this.instance.entrySet()) {
         List<LocalEventDispatcher.State> var4 = var3.getValue();
         var4.removeIf(var1x -> var1x.instance == var1);
         if (var4.isEmpty()) {
            this.instance.remove(var3.getKey(), var4);
         }
      }
   }

   public Event handle(Event var1) {
      List<LocalEventDispatcher.State> var2 = this.instance.get(var1.getClass());
      if (var2 != null && !var2.isEmpty()) {
         LocalEventDispatcher.State[] var3 = var2.toArray(new LocalEventDispatcher.State[0]);
         if (var1 instanceof CancellableEvent var4) {
            for (LocalEventDispatcher.State var8 : var3) {
               var8.handle(var1);
               if (var4.resolve()) {
                  break;
               }
            }
         } else {
            for (LocalEventDispatcher.State var12 : var3) {
               var12.handle(var1);
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   private static int handle(byte var0) {
      for (int var1 = 0; var1 < EventPriority.cache.length; var1++) {
         if (EventPriority.cache[var1] == var0) {
            return var1;
         }
      }

      return EventPriority.cache.length;
   }

   static final class State {
      final Object instance;
      final Method data;
      final byte context;

      State(Object var1, Method var2, byte var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }

      void handle(Event var1) {
         try {
            this.data.invoke(this.instance, var1);
         } catch (Throwable var3) {
            var3.printStackTrace();
         }
      }
   }
}
