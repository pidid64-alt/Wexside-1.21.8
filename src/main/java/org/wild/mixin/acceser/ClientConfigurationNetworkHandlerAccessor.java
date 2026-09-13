package org.wild.mixin.acceser;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.hud.ChatHud.ChatState;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientConfigurationNetworkHandler.class)
public interface ClientConfigurationNetworkHandlerAccessor {
   @Accessor("profile")
   GameProfile wild$profile();

   @Accessor("enabledFeatures")
   FeatureSet wild$enabledFeatures();

   @Accessor("registryManager")
   Immutable wild$registryManager();

   @Accessor("clientRegistries")
   ClientRegistries wild$clientRegistries();

   @Accessor("dataPackManager")
   ClientDataPackManager wild$dataPackManager();

   @Accessor("chatState")
   ChatState wild$chatState();
}
