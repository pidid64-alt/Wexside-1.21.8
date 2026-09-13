package ru.wild.render;

import com.mojang.blaze3d.systems.RenderPass;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.render.RenderLayer.MultiPhase;

public interface RenderPassMutableLayer {
   MultiPhase withRenderPassSetup(Consumer<RenderPass> var1);

   static RenderPassMutableLayer handle(MultiPhase var0) {
      Objects.requireNonNull(var0, "multiPhase");
      return (RenderPassMutableLayer)(Object)var0;
   }
}
