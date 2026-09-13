package ru.wild.api.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import ru.wild.render.font.FontObject;
import ru.wild.util.render.RoundedRectRenderer;

public class HudRenderContext extends Event {
   private MinecraftClient instance;
   private RoundedRectRenderer data;
   private FontObject context;
   private int config;
   private int state;
   private DrawContext cache;

   public HudRenderContext() {
   }

   public HudRenderContext(MinecraftClient var1, RoundedRectRenderer var2, FontObject var3, int var4, int var5, DrawContext var6) {
      this.handle(var1, var2, var3, var4, var5, var6);
   }

   public HudRenderContext handle(MinecraftClient var1, RoundedRectRenderer var2, FontObject var3, int var4, int var5, DrawContext var6) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5;
      this.cache = var6;
      return this;
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

   public DrawContext prepare() {
      return this.cache;
   }
}
