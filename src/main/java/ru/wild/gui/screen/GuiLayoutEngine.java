package ru.wild.gui.screen;
import net.minecraft.client.MinecraftClient;

public final class GuiLayoutEngine {
   private final GuiLayoutSpec instance;

   public GuiMetrics handle(MinecraftClient var1, ModernClickGuiState var2, ViewportLayoutState var3) {
      GuiMetrics var4 = GuiMetrics.handle(var1, this.instance);
      if (var1 != null && var1.getWindow() != null) {
         float var5 = var1.getWindow().getFramebufferWidth();
         float var6 = var1.getWindow().getFramebufferHeight();
         if (!(var5 <= 0.0F) && !(var6 <= 0.0F)) {
            var2.handle(var4, var5, var6);
            var2.process(var4, var5, var6);
            this.handle(var4, var2, var3);
            return var4;
         } else {
            this.handle(var4, var2, var3);
            return var4;
         }
      } else {
         this.handle(var4, var2, var3);
         return var4;
      }
   }

   public GuiMetrics handle(float var1, float var2, ModernClickGuiState var3, ViewportLayoutState var4) {
      GuiMetrics var5 = GuiMetrics.handle(var1, var2, 1.0F, this.instance);
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         var3.handle(var5, var1, var2);
         var3.process(var5, var1, var2);
         this.handle(var5, var3, var4);
         return var5;
      } else {
         this.handle(var5, var3, var4);
         return var5;
      }
   }

   public GuiMetrics handle(MinecraftClient var1, float var2, float var3, ModernClickGuiState var4, ViewportLayoutState var5) {
      GuiMetrics var6 = var1 == null ? GuiMetrics.handle(var2, var3, this.instance) : GuiMetrics.handle(var1, this.instance);
      if (!(var2 <= 0.0F) && !(var3 <= 0.0F)) {
         var4.handle(var6, var2, var3);
         var4.process(var6, var2, var3);
         this.handle(var6, var4, var5);
         return var6;
      } else {
         this.handle(var6, var4, var5);
         return var6;
      }
   }

   public void handle(GuiMetrics var1, ModernClickGuiState var2, ViewportLayoutState var3) {
      this.process(var1, var2, var3);
      this.compute(var1, var2, var3);
   }

   private void process(GuiMetrics var1, ModernClickGuiState var2, ViewportLayoutState var3) {
      float var4 = this.handle(var2.writeTarget());
      float var5 = this.handle(var2.encodeResult());
      float var6 = this.handle(var4, var1.resolve());
      float var7 = this.handle(var4 + var1.apply());
      float var8 = this.handle(var5 + var1.apply());
      float var9 = this.handle(var7, var1.prepare());
      float var10 = this.handle(var7 + var9 + var1.execute());
      float var11 = var8;
      float var12 = Math.max(0.0F, this.handle(var4 + var6 - var1.apply()) - var10);
      float var13 = Math.max(0.0F, this.handle(var5 + var1.update() - var1.apply()) - var11);
      float var14 = this.handle(var11, var1.select());
      float var15 = this.handle(0.0F, var1.refresh());
      float var16 = Math.max(0.0F, this.handle(var12 - var15 - var1.execute()));
      float var17 = this.handle(var10 + var16 + var1.execute());
      float var18 = this.handle(var11 + var14 + var1.execute());
      float var19 = Math.max(0.0F, this.handle(var11 + var13) - var18);
      float var20 = this.handle(var10 + var1.tick());
      float var21 = this.handle(var18 + var1.tick());
      var3.handle(var4);
      var3.process(var5);
      var3.compute(var7);
      var3.resolve(var8);
      var3.update(var9);
      var3.apply(var13);
      var3.execute(var10);
      var3.prepare(var11);
      var3.check(var10);
      var3.onTick(var11);
      var3.select(var14);
      var3.refresh(var16);
      var3.render(var17);
      var3.tick(var15);
      var3.drawAnimation(var10);
      var3.encodePoint(var18);
      var3.animate(var12);
      var3.load(var19);
      var3.save(var20);
      var3.submit(var21);
      var3.unload(Math.max(0.0F, this.handle(var10 + var12 - var1.tick()) - var20));
      var3.fetch(Math.max(0.0F, this.handle(var18 + var19 - var1.tick()) - var21));
      var3.measure(var3.save());
      var3.blendMatrix(this.handle(var3.measure() + var1.drawAnimation() + var1.execute()));
      var3.matchVector(this.handle(var3.blendMatrix() + var1.drawAnimation() + var1.execute()));
   }

   private void compute(GuiMetrics var1, ModernClickGuiState var2, ViewportLayoutState var3) {
      var3.projectItem(this.handle(var2.parseMessage()));
      var3.computeResponse(this.handle(var2.readProvider()));
      var3.fetchProvider(this.handle(var3.projectItem() + var1.process(7.0F)));
      var3.drawProfile(this.handle(var3.computeResponse() + var1.process(63.0F)));
   }

   private float handle(float var1) {
      return Math.round(var1);
   }

   private float handle(float var1, float var2) {
      return Math.max(0.0F, this.handle(var1 + var2) - this.handle(var1));
   }
   public GuiLayoutEngine(GuiLayoutSpec var1) {
      this.instance = var1;
   }
}
