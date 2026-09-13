package ru.wild.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.Window;

public final class WorldVertexBuffer {
   private static final int instance = 262144;
   private static final BufferAllocator data = new BufferAllocator(262144);
   private static final Immediate context = VertexConsumerProvider.immediate(data);

   private WorldVertexBuffer() {
   }

   public static Immediate handle() {
      return context;
   }

   public static boolean handle(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         Window var1 = var0.getWindow();
         return !var1.hasZeroWidthOrHeight() && var1.getFramebufferWidth() > 0 && var1.getFramebufferHeight() > 0;
      } else {
         return false;
      }
   }

   public static void process() {
      context.draw();
      data.clear();
   }
}
