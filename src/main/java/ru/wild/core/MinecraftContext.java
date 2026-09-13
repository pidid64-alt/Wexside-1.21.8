package ru.wild.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public interface MinecraftContext {
   MinecraftClient toggleState = MinecraftClient.getInstance();
   Window serializedState = toggleState.getWindow();
}
