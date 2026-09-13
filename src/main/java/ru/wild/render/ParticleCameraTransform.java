package ru.wild.render;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public final class ParticleCameraTransform {
   private static Camera instance;
   private static double data = Double.NaN;
   private static double context = Double.NaN;
   private static double config = Double.NaN;
   private static float state = Float.NaN;
   private static float cache = Float.NaN;
   private static float output = Float.NaN;
   private static float current = Float.NaN;
   private static double active;
   private static double mode;
   private static double selection;
   private static float enabled;
   private static float renderer;
   private static float handler;
   private static float animationDraw;
   private static float pointEncode;
   private static float animator;

   private ParticleCameraTransform() {
   }

   static void handle(Camera var0) {
      Vec3d var1 = var0.getPos();
      Quaternionf var2 = var0.getRotation();
      float var3 = var2.x();
      float var4 = var2.y();
      float var5 = var2.z();
      float var6 = var2.w();
      if (var0 != instance || var1.x != data || var1.y != context || var1.z != config || var3 != state || var4 != cache || var5 != output || var6 != current) {
         instance = var0;
         data = var1.x;
         context = var1.y;
         config = var1.z;
         state = var3;
         cache = var4;
         output = var5;
         current = var6;
         active = var1.x;
         mode = var1.y;
         selection = var1.z;
         enabled = 1.0F - 2.0F * (var4 * var4 + var5 * var5);
         renderer = 2.0F * (var3 * var4 + var5 * var6);
         handler = 2.0F * (var3 * var5 - var4 * var6);
         animationDraw = 2.0F * (var3 * var4 - var5 * var6);
         pointEncode = 1.0F - 2.0F * (var3 * var3 + var5 * var5);
         animator = 2.0F * (var4 * var5 + var3 * var6);
      }
   }

   static double handle() {
      return active;
   }

   static double process() {
      return mode;
   }

   static double compute() {
      return selection;
   }

   static float resolve() {
      return enabled;
   }

   static float update() {
      return renderer;
   }

   static float apply() {
      return handler;
   }

   static float execute() {
      return animationDraw;
   }

   static float prepare() {
      return pointEncode;
   }

   static float check() {
      return animator;
   }
}
