package ru.wild.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class EventHandlerInvoker {
   private static final Map<Class<? extends Event>, EventHandlerInvoker.State[]> instance = new ConcurrentHashMap<>();
   private static boolean data = false;
   private static int context = 0;

   public static void handle(Object var0) {
      for (Method var4 : var0.getClass().getDeclaredMethods()) {
         if (!handle(var4)) {
            handle(var4, var0);
         }
      }
   }

   public static void process(Object var0) {
      for (Entry var2 : instance.entrySet()) {
         EventHandlerInvoker.State[] var3 = (EventHandlerInvoker.State[])var2.getValue();
         int var4 = 0;

         for (int var5 = 0; var5 < var3.length; var5++) {
            if (!var3[var5].handle().equals(var0)) {
               var4++;
            }
         }

         if (var4 != var3.length) {
            if (var4 == 0) {
               instance.remove(var2.getKey(), var3);
            } else {
               EventHandlerInvoker.State[] var8 = new EventHandlerInvoker.State[var4];
               int var6 = 0;

               for (int var7 = 0; var7 < var3.length; var7++) {
                  if (!var3[var7].handle().equals(var0)) {
                     var8[var6++] = var3[var7];
                  }
               }

               instance.put((Class<? extends Event>)var2.getKey(), var8);
            }
         }
      }

      handle(true);
   }

   private static void handle(Method var0, Object var1) {
      try {
         Class var2 = var0.getParameterTypes()[0];
         EventHandlerInvoker.State var3 = new EventHandlerInvoker.State(var1, var0, var0.getAnnotation(EventHandler.class).handle());
         if (!var3.process().isAccessible()) {
            var3.process().setAccessible(true);
         }

         EventHandlerInvoker.State[] var4 = instance.get(var2);
         if (var4 != null) {
            for (int var5 = 0; var5 < var4.length; var5++) {
               if (var4[var5].equals(var3)) {
                  return;
               }
            }

            EventHandlerInvoker.State[] var7 = new EventHandlerInvoker.State[var4.length + 1];
            System.arraycopy(var4, 0, var7, 0, var4.length);
            var7[var4.length] = var3;
            instance.put(var2, handle(var7));
         } else {
            instance.put(var2, new EventHandlerInvoker.State[]{var3});
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public static void handle(boolean var0) {
      if (!var0) {
         instance.clear();
      } else {
         for (Entry var2 : instance.entrySet()) {
            EventHandlerInvoker.State[] var3 = (EventHandlerInvoker.State[])var2.getValue();
            if (var3 == null || var3.length == 0) {
               instance.remove(var2.getKey(), var3);
            }
         }
      }
   }

   private static boolean handle(Method var0) {
      return var0.getParameterTypes().length != 1 || !var0.isAnnotationPresent(EventHandler.class);
   }

   public static void handle() {
   }

   public static Event handle(Event var0) {
      EventHandlerInvoker.State[] var1 = instance.get(var0.getClass());
      if (var1 != null && var1.length > 0) {
         if (var0 instanceof CancellableEvent var2) {
            for (int var3 = 0; var3 < var1.length; var3++) {
               handle(var1[var3], var0);
               if (var2.resolve()) {
                  break;
               }
            }
         } else {
            for (int var4 = 0; var4 < var1.length; var4++) {
               handle(var1[var4], var0);
            }
         }
      }

      return var0;
   }

   private static EventHandlerInvoker.State[] handle(EventHandlerInvoker.State[] var0) {
      Arrays.sort(var0, (var0x, var1) -> Integer.compare(handle(var0x.compute()), handle(var1.compute())));
      return var0;
   }

   private static int handle(byte var0) {
      for (int var1 = 0; var1 < EventPriority.cache.length; var1++) {
         if (EventPriority.cache[var1] == var0) {
            return var1;
         }
      }

      return EventPriority.cache.length;
   }

   private static void handle(EventHandlerInvoker.State var0, Event var1) {
      try {
         var0.process().invoke(var0.handle(), var1);
      } catch (IllegalAccessException | IllegalArgumentException var4) {
         System.err
            .println(
               "[EventManager] Failed to invoke " + var0.process().getName() + " on " + var0.handle().getClass().getSimpleName() + ": " + var4.getMessage()
            );
      } catch (InvocationTargetException var5) {
         Throwable var3 = var5.getCause();
         System.err
            .println(
               "[EventManager] Exception in handler "
                  + var0.process().getName()
                  + " on "
                  + var0.handle().getClass().getSimpleName()
                  + ": "
                  + (var3 != null ? var3.getMessage() : var5.getMessage())
            );
         if (var3 != null) {
            var3.printStackTrace();
         }
      }
   }

   static final class State {
      private final Object instance;
      private final Method data;
      private final byte context;

      public State(Object var1, Method var2, byte var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }

      public Object handle() {
         return this.instance;
      }

      public Method process() {
         return this.data;
      }

      public byte compute() {
         return this.context;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            EventHandlerInvoker.State var2 = (EventHandlerInvoker.State)var1;
            return this.context == var2.context && this.instance.equals(var2.instance) && this.data.equals(var2.data);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.instance, this.data, this.context);
      }
   }
}
