package ru.wild.render.shader;

import java.util.concurrent.ThreadLocalRandom;

public final class ShaderPresetNameGenerator {
   private static final String[] instance = new String[]{
      "Velvet",
      "Aurora",
      "Magnetic",
      "Prismatic",
      "Silent",
      "Crystal",
      "Solar",
      "Lunar",
      "Holographic",
      "Obsidian",
      "Radiant",
      "Neon",
      "Frosted",
      "Kinetic",
      "Vivid",
      "Phantom"
   };
   private static final String[] data = new String[]{
      "Glass", "Halo", "Mica", "Pulse", "Mist", "Bloom", "Signal", "Ribbon", "Veil", "Plate", "Glow", "Drift", "Shell", "Field", "Aura", "Prism"
   };

   private ShaderPresetNameGenerator() {
   }

   public static String handle() {
      ThreadLocalRandom var0 = ThreadLocalRandom.current();
      return instance[var0.nextInt(instance.length)] + " " + data[var0.nextInt(data.length)];
   }
}
