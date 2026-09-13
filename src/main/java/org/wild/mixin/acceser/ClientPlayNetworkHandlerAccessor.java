package org.wild.mixin.acceser;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {
   @Invoker("readLightData")
   void wild$readLightData(int var1, int var2, LightData var3, boolean var4);

   @Invoker("scheduleRenderChunk")
   void wild$scheduleRenderChunk(WorldChunk var1, int var2, int var3);

   @Accessor("combinedDynamicRegistries")
   Immutable wild$combinedDynamicRegistries();

   @Accessor("enabledFeatures")
   FeatureSet wild$enabledFeatures();

   @Mutable
   @Accessor("world")
   void wild$setWorld(ClientWorld var1);

   @Accessor("world")
   ClientWorld wild$getWorld();

   @Mutable
   @Accessor("worldProperties")
   void wild$setWorldProperties(Properties var1);

   @Mutable
   @Accessor("chunkLoadDistance")
   void wild$setChunkLoadDistance(int var1);

   @Accessor("chunkLoadDistance")
   int wild$getChunkLoadDistance();

   @Mutable
   @Accessor("simulationDistance")
   void wild$setSimulationDistance(int var1);

   @Accessor("simulationDistance")
   int wild$getSimulationDistance();
}
