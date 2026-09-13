package ru.wild.api.setting;

import java.util.function.Supplier;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.wild.core.MinecraftContext;

public class KeybindSetting extends Setting {
   public int config;
   public String state;
   public boolean cache;
   public boolean output;
   private final int current;
   private final boolean active;

   public KeybindSetting(String var1, int var2, boolean var3) {
      this.instance = var1;
      this.config = var2;
      this.cache = var3;
      this.current = var2;
      this.active = var3;
   }

   public KeybindSetting(String var1, int var2) {
      this(var1, var2, false);
   }

   public int compute() {
      return this.config;
   }

   public void handle(int var1) {
      this.config = var1;
   }

   public KeybindSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   @Override
   public void process() {
      this.config = this.current;
      this.cache = this.active;
      this.output = false;
   }

   public static boolean process(int var0) {
      if (MinecraftContext.toggleState.currentScreen != null) {
         return false;
      } else {
         long var1 = MinecraftContext.toggleState.getWindow().getHandle();
         if (var0 >= 0) {
            return InputUtil.isKeyPressed(var1, var0);
         } else if (var0 <= -100) {
            int var3 = -var0 - 100;
            return GLFW.glfwGetMouseButton(var1, var3) == 1;
         } else {
            return false;
         }
      }
   }
}
