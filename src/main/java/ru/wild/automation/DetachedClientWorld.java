package ru.wild.automation;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public final class DetachedClientWorld extends ClientWorld {
   private final HeadlessBotSession instance;

   public DetachedClientWorld(
      HeadlessBotSession var1,
      ClientPlayNetworkHandler var2,
      Properties var3,
      RegistryKey<World> var4,
      RegistryEntry<DimensionType> var5,
      int var6,
      int var7,
      boolean var8,
      long var9,
      int var11
   ) {
      super(var2, var3, var4, var5, var6, var7, MinecraftClient.getInstance().worldRenderer, var8, var9, var11);
      this.instance = var1;
   }

   private boolean handle() {
      return HeadlessBotEngine.handle() == this.instance;
   }

   public void handle(DetachedClientWorld var1) {
      if (var1 != null) {
         this.putMapStates(var1.getMapStates());
      }
   }

   public void scheduleBlockRenders(int var1, int var2, int var3) {
      if (this.handle()) {
         super.scheduleBlockRenders(var1, var2, var3);
      }
   }

   public void scheduleBlockRerenderIfNeeded(BlockPos var1, BlockState var2, BlockState var3) {
      if (this.handle()) {
         super.scheduleBlockRerenderIfNeeded(var1, var2, var3);
      }
   }

   public void updateListeners(BlockPos var1, BlockState var2, BlockState var3, int var4) {
      if (this.handle()) {
         super.updateListeners(var1, var2, var3, var4);
      }
   }

   public void addParticleClient(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      if (this.handle()) {
         super.addParticleClient(var1, var2, var4, var6, var8, var10, var12);
      }
   }

   public void addParticleClient(
      ParticleEffect var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14
   ) {
      if (this.handle()) {
         super.addParticleClient(var1, var2, var3, var4, var6, var8, var10, var12, var14);
      }
   }

   public void addImportantParticleClient(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      if (this.handle()) {
         super.addImportantParticleClient(var1, var2, var4, var6, var8, var10, var12);
      }
   }

   public void addImportantParticleClient(ParticleEffect var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      if (this.handle()) {
         super.addImportantParticleClient(var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public void addFireworkParticle(double var1, double var3, double var5, double var7, double var9, double var11, List<FireworkExplosionComponent> var13) {
      if (this.handle()) {
         super.addFireworkParticle(var1, var3, var5, var7, var9, var11, var13);
      }
   }

   public void setBlockBreakingInfo(int var1, BlockPos var2, int var3) {
      if (this.handle()) {
         super.setBlockBreakingInfo(var1, var2, var3);
      }
   }

   public void syncWorldEvent(Entity var1, int var2, BlockPos var3, int var4) {
      if (this.handle()) {
         super.syncWorldEvent(var1, var2, var3, var4);
      }
   }

   public void syncGlobalEvent(int var1, BlockPos var2, int var3) {
      if (this.handle()) {
         super.syncGlobalEvent(var1, var2, var3);
      }
   }

   public void playSound(
      Entity var1, double var2, double var4, double var6, RegistryEntry<SoundEvent> var8, SoundCategory var9, float var10, float var11, long var12
   ) {
      if (this.handle()) {
         super.playSound(var1, var2, var4, var6, var8, var9, var10, var11, var12);
      }
   }

   public void playSoundFromEntity(Entity var1, Entity var2, RegistryEntry<SoundEvent> var3, SoundCategory var4, float var5, float var6, long var7) {
      if (this.handle()) {
         super.playSoundFromEntity(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public void playSoundClient(SoundEvent var1, SoundCategory var2, float var3, float var4) {
      if (this.handle()) {
         super.playSoundClient(var1, var2, var3, var4);
      }
   }

   public void playSoundClient(double var1, double var3, double var5, SoundEvent var7, SoundCategory var8, float var9, float var10, boolean var11) {
      if (this.handle()) {
         super.playSoundClient(var1, var3, var5, var7, var8, var9, var10, var11);
      }
   }
}
