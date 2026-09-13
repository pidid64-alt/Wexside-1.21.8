package ru.wild.render.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

public final class MainFramebufferBinding {
   private static int instance;
   private static int data;

   private MainFramebufferBinding() {
   }

   public static int handle() {
      if (!resolve()) {
         return 0;
      }

      if (instance != 0 && !GL30.glIsFramebuffer(instance)) {
         instance = 0;
      }

      if (instance == 0) {
         instance = GL30.glGenFramebuffers();
      }

      return instance;
   }

   public static int process() {
      if (!resolve()) {
         return 0;
      }

      if (data != 0 && !GL30.glIsFramebuffer(data)) {
         data = 0;
      }

      if (data == 0) {
         data = GL30.glGenFramebuffers();
      }

      return data;
   }

   public static void handle(int var0) {
      if (instance == var0) {
         instance = 0;
      }
   }

   public static void compute() {
      if (!resolve()) {
         instance = 0;
         data = 0;
      } else {
         if (instance != 0) {
            GL30.glDeleteFramebuffers(instance);
            instance = 0;
         }

         if (data != 0) {
            GL30.glDeleteFramebuffers(data);
            data = 0;
         }
      }
   }

   private static boolean resolve() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }
}
