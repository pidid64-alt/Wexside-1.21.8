package ru.wild.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.wild.audio.ProceduralSoundSynthesizer;
import ru.wild.modules.visuals.Menu;
import ru.wild.util.render.RoundedRectRenderer;

public final class ClickGuiModernScreen extends Screen {
   private final ModernClickGuiRenderer instance = new ModernClickGuiRenderer();

   public ClickGuiModernScreen() {
      super(Text.literal("Wild Modern ClickGUI"));
   }

   public ModernClickGuiRenderer handle() {
      return this.instance;
   }

   protected void init() {
      super.init();
      this.instance.handle(this.client);
      ProceduralSoundSynthesizer.compute();
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      if (!this.compute()) {
         this.instance.execute();
      } else {
         this.instance.handle(this.handle(var2), this.process(var3));
      }
   }

   public void renderBackground(DrawContext var1, int var2, int var3, float var4) {
   }

   public void renderInGameBackground(DrawContext var1) {
   }

   public void handle(RoundedRectRenderer var1, DrawContext var2, int var3, int var4, float var5) {
      if (!this.process(var3, var4)) {
         this.instance.execute();
      } else {
         this.instance.handle(this.client, var2, var1, var3, var4, var5);
         if (this.instance.compute()) {
            this.update();
         }
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (!this.compute()) {
         this.instance.execute();
         return true;
      }

      if (this.instance.resolve()) {
         return true;
      }

      this.instance.handle(this.handle(var1), this.process(var3), var5);
      return true;
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (!this.compute()) {
         this.instance.execute();
         return true;
      }

      if (this.instance.resolve()) {
         return true;
      }

      this.instance.process(this.handle(var1), this.process(var3), var5);
      return true;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (!this.compute()) {
         this.instance.execute();
         return true;
      }

      if (this.instance.resolve()) {
         return true;
      }

      this.instance.handle(this.handle(var1), this.process(var3), var5, this.compute(var6), this.resolve(var8));
      return true;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (!this.compute()) {
         this.instance.execute();
         return true;
      }

      if (this.instance.resolve()) {
         return true;
      }

      double var9 = var5;
      double var11 = var7;
      if (var9 == 0.0 && (hasShiftDown() || hasControlDown())) {
         var9 = var7;
         var11 = 0.0;
      }

      this.instance.handle(this.handle(var1), this.process(var3), var9, var11);
      return true;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 300) {
         return super.keyPressed(var1, var2, var3);
      } else if (!this.compute()) {
         this.instance.execute();
         return true;
      } else if (var1 == 256 && this.instance.resolve()) {
         this.update();
         return true;
      } else if (var1 == this.resolve() && !this.instance.update()) {
         this.close();
         return true;
      } else if (this.instance.resolve()) {
         return true;
      } else {
         return this.instance.handle(var1) ? true : super.keyPressed(var1, var2, var3);
      }
   }

   public boolean charTyped(char var1, int var2) {
      if (!this.compute()) {
         this.instance.execute();
         return true;
      } else if (this.instance.resolve()) {
         return true;
      } else {
         return this.instance.handle(var1) ? true : super.charTyped(var1, var2);
      }
   }

   public void close() {
      if (!this.instance.process()) {
         this.update();
      }
   }

   public void removed() {
      this.instance.prepare();
      super.removed();
   }

   public boolean shouldPause() {
      return false;
   }

   public void handle(int var1, int var2) {
      if (var1 <= 0 || var2 <= 0) {
         this.instance.execute();
      }
   }

   public void handle(boolean var1) {
      if (!var1) {
         this.instance.execute();
      }
   }

   private float handle(double var1) {
      if (this.client != null && this.client.getWindow() != null) {
         int var3 = this.client.getWindow().getFramebufferWidth();
         int var4 = this.client.getWindow().getScaledWidth();
         return var3 > 0 && var4 > 0 ? (float)(var1 * var3 / Math.max(1.0, var4)) : (float)var1;
      } else {
         return (float)var1;
      }
   }

   private float process(double var1) {
      if (this.client != null && this.client.getWindow() != null) {
         int var3 = this.client.getWindow().getFramebufferHeight();
         int var4 = this.client.getWindow().getScaledHeight();
         return var3 > 0 && var4 > 0 ? (float)(var1 * var3 / Math.max(1.0, var4)) : (float)var1;
      } else {
         return (float)var1;
      }
   }

   private float compute(double var1) {
      if (this.client != null && this.client.getWindow() != null) {
         int var3 = this.client.getWindow().getFramebufferWidth();
         int var4 = this.client.getWindow().getScaledWidth();
         return var3 > 0 && var4 > 0 ? (float)(var1 * var3 / Math.max(1.0, var4)) : (float)var1;
      } else {
         return (float)var1;
      }
   }

   private float resolve(double var1) {
      if (this.client != null && this.client.getWindow() != null) {
         int var3 = this.client.getWindow().getFramebufferHeight();
         int var4 = this.client.getWindow().getScaledHeight();
         return var3 > 0 && var4 > 0 ? (float)(var1 * var3 / Math.max(1.0, var4)) : (float)var1;
      } else {
         return (float)var1;
      }
   }

   private boolean process() {
      return this.client != null && this.client.getWindow() != null
         ? this.process(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight())
         : false;
   }

   private boolean process(int var1, int var2) {
      return this.client != null && this.client.getWindow() != null
         ? var1 > 0
            && var2 > 0
            && this.client.getWindow().getFramebufferWidth() > 0
            && this.client.getWindow().getFramebufferHeight() > 0
            && this.client.getWindow().getScaledWidth() > 0
            && this.client.getWindow().getScaledHeight() > 0
         : false;
   }

   private boolean compute() {
      return this.process() && this.client.isWindowFocused();
   }

   private int resolve() {
      Menu var1 = Menu.drawAnimation();
      return var1 != null && var1.keyCode != -1 ? var1.keyCode : 344;
   }

   private void update() {
      this.instance.prepare();
      if (this.client != null) {
         this.client.setScreen(null);
      }
   }
}
