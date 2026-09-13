package ru.wild.api.event;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import ru.wild.render.font.FontObject;
import ru.wild.util.render.RoundedRectRenderer;

public final class GuiRenderContext extends Event {
   private final MinecraftClient instance;
   private final RoundedRectRenderer data;
   private final FontObject context;
   private final int config;
   private final int state;

   public GuiRenderContext(MinecraftClient var1, RoundedRectRenderer var2, FontObject var3, int var4, int var5) {
      this.instance = Objects.requireNonNull(var1, "client");
      this.data = Objects.requireNonNull(var2, "renderer");
      this.context = Objects.requireNonNull(var3, "defaultFont");
      this.config = var4;
      this.state = var5;
   }

   public MinecraftClient compute() {
      return this.instance;
   }

   public RoundedRectRenderer resolve() {
      return this.data;
   }

   public FontObject update() {
      return this.context;
   }

   public int apply() {
      return this.config;
   }

   public int execute() {
      return this.state;
   }
}
