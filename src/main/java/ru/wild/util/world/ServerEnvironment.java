package ru.wild.util.world;
import ru.wild.core.MinecraftContext;

public final class ServerEnvironment implements MinecraftContext {
   public static String instance = "Vanilla";

   public static boolean handle() {
      return toggleState.player == null || toggleState.world == null || toggleState.interactionManager == null;
   }

   public static boolean process() {
      return instance.equals("CopyTime") || instance.equals("SpookyTime") || instance.equals("FunTime");
   }

   public static boolean compute() {
      return instance.equals("FunTime");
   }

   public static boolean resolve() {
      return instance.equals("ReallyWorld");
   }

   public static boolean update() {
      return instance.equals("HolyWorld");
   }

   public static boolean apply() {
      return instance.equals("Vanilla");
   }

   public static boolean execute() {
      return instance.equals("AresMine");
   }
   private ServerEnvironment() {
   }
}
