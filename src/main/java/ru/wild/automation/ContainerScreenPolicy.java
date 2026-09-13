package ru.wild.automation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public final class ContainerScreenPolicy {
   private ContainerScreenPolicy() {
   }

   public static <T extends Screen> T handle(MinecraftClient var0, Screen var1, Class<T> var2) {
      if (var0 != null && var2.isInstance(var0.currentScreen)) {
         return (T)var2.cast(var0.currentScreen);
      } else {
         return (T)(var2.isInstance(var1) && handle(var0, var1) ? var2.cast(var1) : null);
      }
   }

   public static boolean handle(MinecraftClient var0, Screen var1) {
      return var0 != null && var0.player != null && var1 instanceof HandledScreen var2 ? var0.player.currentScreenHandler == var2.getScreenHandler() : false;
   }

   public static boolean handle(MinecraftClient var0) {
      return var0 != null && var0.player != null && var0.player.currentScreenHandler != var0.player.playerScreenHandler;
   }

   public static boolean process(MinecraftClient var0, Screen var1) {
      if (var0 != null) {
         if (var0.currentScreen != null) {
            return true;
         }

         if (handle(var0, var1)) {
            return true;
         }
      }

      return false;
   }
}
