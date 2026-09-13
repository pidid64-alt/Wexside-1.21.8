package ru.wild.render;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import ru.wild.render.shader.StardustShader;

public final class StardustParticleRegistry {
   public static final Identifier instance = Identifier.of("wild", "floating_stardust");
   public static final Identifier data = Identifier.of("wild", "shooting_star");
   public static final SimpleParticleType context = FabricParticleTypes.simple(true);
   public static final SimpleParticleType config = FabricParticleTypes.simple(true);
   private static boolean state;

   private StardustParticleRegistry() {
   }

   public static void handle() {
      if (!state) {
         state = true;
         StardustShader.handle();
         StardustSkyRenderer.handle();
         FloatingStardustParticle.instance = context;
         ShootingStarParticle.instance = config;
         Registry.register(Registries.PARTICLE_TYPE, instance, context);
         Registry.register(Registries.PARTICLE_TYPE, data, config);
         ParticleFactoryRegistry.getInstance().register(context, new FloatingStardustParticle.State());
         ParticleFactoryRegistry.getInstance().register(config, new ShootingStarParticle.State());
      }
   }
}
