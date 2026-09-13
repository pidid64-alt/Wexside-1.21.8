package ru.wild.util.render;

import java.lang.reflect.Method;
import ru.wild.core.BooleanInvocationResult;

public final class IrisCompatibility {
   private static final boolean instance;
   private static final Object data;
   private static final Method context;
   private static final Method config;

   private IrisCompatibility() {
   }

   public static boolean handle() {
      return compute() == BooleanInvocationResult.TRUE;
   }

   public static boolean process() {
      return resolve() == BooleanInvocationResult.TRUE;
   }

   public static BooleanInvocationResult compute() {
      return handle(context);
   }

   public static BooleanInvocationResult resolve() {
      return handle(config);
   }

   public static boolean update() {
      return compute().handle() && resolve().handle();
   }

   private static BooleanInvocationResult handle(Method var0) {
      if (!instance) {
         return BooleanInvocationResult.FALSE;
      }

      if (data != null && var0 != null) {
         try {
            return BooleanInvocationResult.handle(true, true, var0.invoke(data) instanceof Boolean var2 ? var2 : null);
         } catch (ReflectiveOperationException | LinkageError | RuntimeException var3) {
            return BooleanInvocationResult.UNKNOWN;
         }
      } else {
         return BooleanInvocationResult.UNKNOWN;
      }
   }

   static {
      boolean var0 = false;
      Object var1 = null;
      Method var2 = null;
      Method var3 = null;

      try {
         Class var4 = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
         var0 = true;
         var1 = var4.getMethod("getInstance").invoke(null);
         var2 = var4.getMethod("isShaderPackInUse");
         var3 = var4.getMethod("isRenderingShadowPass");
      } catch (ClassNotFoundException var5) {
      } catch (ReflectiveOperationException | LinkageError | RuntimeException var6) {
         var0 = true;
      }

      instance = var0;
      data = var1;
      context = var2;
      config = var3;
   }
}
