package org.wild.mixin.acceser;

import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientSessionAccessor {
   @Accessor("session")
   Session litka$getSession();

   @Mutable
   @Accessor("session")
   void litka$setSession(Session var1);

   @Accessor("profileKeys")
   ProfileKeys litka$getProfileKeys();

   @Mutable
   @Accessor("profileKeys")
   void litka$setProfileKeys(ProfileKeys var1);

   @Accessor("gameProfileFuture")
   CompletableFuture<ProfileResult> litka$getGameProfileFuture();

   @Mutable
   @Accessor("gameProfileFuture")
   void litka$setGameProfileFuture(CompletableFuture<ProfileResult> var1);
}
