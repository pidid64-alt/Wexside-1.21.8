package ru.wild.gui.screen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.wild.automation.combat.RotationProfileDataset;
import ru.wild.automation.combat.RotationProfileRepository;
import ru.wild.modules.player.RotationLab;
import ru.wild.util.text.ChatLogger;

public final class RotationLabScreen extends Screen {
   private static final int instance = -234156525;
   private static final int data = -1441326300;
   private static final int context = -1446152;
   private static final int config = -7366230;
   private static final int state = -45462;
   private static final int cache = -1;
   private static final int output = -2142256137;
   private final RotationLab current;
   private final List<RotationProfileDataset.CacheEntry> active = new ArrayList<>();
   private final List<RotationProfileDataset.PrimaryCacheEntry> mode = new ArrayList<>();
   private RotationLabScreen.CacheEntry selection;
   private long enabled;
   private int renderer = -1;
   private double handler;
   private double animationDraw;
   private float pointEncode;
   private float animator;
   private boolean source;

   public RotationLabScreen(RotationLab var1) {
      super(Text.literal("RotationLab"));
      this.current = var1;
   }

   public boolean shouldPause() {
      return false;
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      var1.fill(0, 0, this.width, this.height, -234156525);
      if (this.selection == null) {
         this.handle(var2, var3);
      }

      this.handle(var4);
      this.process(var2, var3);
      if (this.current.tick() && this.selection != null && this.resolve(var2, var3)) {
         this.compute(var2, var3);
         this.handle(var2, var3);
      }

      this.process(var1);
      this.compute(var1);
      this.handle(var1);
      super.render(var1, var2, var3, var4);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0 && this.selection != null && this.resolve(var1, var3)) {
         this.compute(var1, var3);
         this.handle(var1, var3);
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 82) {
         this.process();
         return true;
      }

      if (var1 != 68 && var1 != 261) {
         return super.keyPressed(var1, var2, var3);
      }

      this.current.load();
      return true;
   }

   public void close() {
      if (!this.source) {
         this.handle();
         this.current.handle(this);
      }

      super.close();
   }

   public void handle() {
      if (!this.source) {
         this.source = true;
         this.compute();
      }
   }

   public void process() {
      this.active.clear();
      this.mode.clear();
      this.selection = null;
      this.renderer = -1;
   }

   private void handle(double var1, double var3) {
      if (this.active.size() >= this.current.animate()) {
         this.close();
      } else {
         String var5 = this.resolve();
         double var6 = Math.max(80.0, this.width * this.current.encodePoint());
         double var8 = Math.max(60.0, this.height * this.current.encodePoint());
         double var10 = (this.width - var6) * 0.5;
         double var12 = (this.height - var8) * 0.5;
         double var14 = this.width * 0.5;
         double var16 = this.height * 0.5;

         double var22 = switch (var5) {
            case "Micro", "Idle" -> 22.0;
            case "Vertical" -> 70.0;
            case "Attack" -> 120.0;
            default -> 95.0;
         };
         int var26 = 0;

         double var18;
         double var20;
         do {
            if ("Vertical".equals(var5)) {
               var18 = ThreadLocalRandom.current().nextDouble(-24.0, 24.0);
               var20 = this.update(var22, var8 * 0.42);
            } else if ("Diagonal".equals(var5)) {
               var18 = this.update(var22 * 0.65, var6 * 0.45);
               var20 = this.update(var22 * 0.45, var8 * 0.4);
            } else if (!"Micro".equals(var5) && !"Idle".equals(var5)) {
               var18 = this.update(var22, var6 * 0.48);
               var20 = this.update(12.0, var8 * 0.36);
            } else {
               var18 = this.update(12.0, 58.0);
               var20 = this.update(8.0, 42.0);
            }

            this.selection = new RotationLabScreen.CacheEntry(
               MathHelper.clamp(var1 + var18, var10, var10 + var6), MathHelper.clamp(var3 + var20, var12, var12 + var8), this.current.drawAnimation(), var5
            );
         } while (this.handle(var1, var3, this.selection.instance, this.selection.data) < var22 && ++var26 < 12);

         if (var26 >= 12) {
            this.selection.instance = MathHelper.clamp(var14 + var18, var10, var10 + var6);
            this.selection.data = MathHelper.clamp(var16 + var20, var12, var12 + var8);
         }

         if ("Tracking".equals(var5)) {
            this.selection.context = ThreadLocalRandom.current().nextDouble(-1.15, 1.15);
            this.selection.config = ThreadLocalRandom.current().nextDouble(-0.85, 0.85);
         }

         this.handler = var1;
         this.animationDraw = var3;
         this.enabled = System.currentTimeMillis();
         this.renderer = -1;
         this.pointEncode = 0.0F;
         this.animator = 0.0F;
         this.mode.clear();
      }
   }

   private void handle(float var1) {
      if (this.selection != null && "Tracking".equals(this.selection.cache)) {
         double var2 = Math.max(0.35, var1);
         this.selection.instance = this.selection.instance + this.selection.context * var2;
         this.selection.data = this.selection.data + this.selection.config * var2;
         double var4 = this.selection.state + 18.0;
         if (this.selection.instance < var4 || this.selection.instance > this.width - var4) {
            this.selection.context = -this.selection.context;
         }

         if (this.selection.data < var4 || this.selection.data > this.height - var4) {
            this.selection.config = -this.selection.config;
         }

         this.selection.instance = MathHelper.clamp(this.selection.instance, var4, this.width - var4);
         this.selection.data = MathHelper.clamp(this.selection.data, var4, this.height - var4);
      }
   }

   private void process(double var1, double var3) {
      if (this.selection != null) {
         int var5 = (int)((System.currentTimeMillis() - this.enabled) / 50L);
         if (var5 != this.renderer) {
            this.renderer = var5;
            float var6 = this.handle(var1 - this.handler);
            float var7 = this.process(var3 - this.animationDraw);
            float var8 = this.handle(this.selection.instance - this.handler);
            float var9 = this.process(this.selection.data - this.animationDraw);
            float var10 = (float)Math.max(0.001, Math.hypot(var8, var9));
            RotationProfileDataset.PrimaryCacheEntry var11 = new RotationProfileDataset.PrimaryCacheEntry();
            var11.instance = var5;
            var11.data = var6;
            var11.context = var7;
            var11.config = var6 - this.pointEncode;
            var11.state = var7 - this.animator;
            var11.cache = Math.abs(var11.config);
            var11.output = Math.abs(var11.state);
            var11.current = (float)MathHelper.clamp(Math.hypot(var6, var7) / var10, 0.0, 1.35);
            this.mode.add(var11);
            this.pointEncode = var6;
            this.animator = var7;
            if (var5 > 120) {
               this.handle(var1, var3);
            }
         }
      }
   }

   private void compute(double var1, double var3) {
      if (this.selection != null && this.mode.size() >= 2) {
         RotationProfileDataset.CacheEntry var5 = new RotationProfileDataset.CacheEntry();
         var5.instance = this.selection.cache;
         var5.data = System.currentTimeMillis();
         var5.context = this.handle(this.selection.instance - this.handler);
         var5.config = this.process(this.selection.data - this.animationDraw);
         RotationProfileDataset.PrimaryCacheEntry var6 = this.mode.get(this.mode.size() - 1);
         var5.state = var6.data;
         var5.cache = var6.context;
         var5.active = var6.instance + 1;
         var5.enabled = new ArrayList<>(this.mode);
         var5.output = this.handle(var5);
         var5.current = this.process(var5);
         var5.mode = this.compute(var5);
         double var7 = this.handle(var1, var3, this.selection.instance, this.selection.data);
         float var9 = 1.0F - (float)MathHelper.clamp(var7 / Math.max(1.0, this.selection.state * 1.8), 0.0, 1.0);
         float var10 = MathHelper.clamp(this.mode.size() / 6.0F, 0.0F, 1.0F);
         var5.selection = MathHelper.clamp(var9 * 0.75F + var10 * 0.25F, 0.0F, 1.0F);
         this.active.add(var5);
      }
   }

   private float handle(RotationProfileDataset.CacheEntry var1) {
      float var2 = var1.context;
      float var3 = 0.0F;

      for (RotationProfileDataset.PrimaryCacheEntry var5 : var1.enabled) {
         var3 = Math.max(var3, Math.abs(var5.data) - Math.abs(var2));
      }

      return Math.max(0.0F, var3);
   }

   private float process(RotationProfileDataset.CacheEntry var1) {
      float var2 = var1.config;
      float var3 = 0.0F;

      for (RotationProfileDataset.PrimaryCacheEntry var5 : var1.enabled) {
         var3 = Math.max(var3, Math.abs(var5.context) - Math.abs(var2));
      }

      return Math.max(0.0F, var3);
   }

   private int compute(RotationProfileDataset.CacheEntry var1) {
      int var2 = 0;

      for (int var3 = var1.enabled.size() - 1; var3 >= 0; var3--) {
         RotationProfileDataset.PrimaryCacheEntry var4 = var1.enabled.get(var3);
         float var5 = Math.abs(var1.context - var4.data);
         float var6 = Math.abs(var1.config - var4.context);
         if (!(var5 <= 1.5F) || !(var6 <= 1.5F)) {
            break;
         }

         var2++;
      }

      return var2;
   }

   private void compute() {
      if (!this.active.isEmpty()) {
         Path var1 = RotationProfileRepository.handle(this.current.refresh());
         RotationProfileDataset var2 = RotationProfileRepository.handle(var1);
         if (var2 == null) {
            var2 = new RotationProfileDataset();
            var2.data = System.currentTimeMillis();
            var2.config = RotationProfileRepository.process(this.current.refresh());
         }

         var2.context = System.currentTimeMillis();
         var2.cache.addAll(this.active);
         RotationProfileRepository.handle(var1, var2);
         ChatLogger.handle("[RotationLab] Saved " + this.active.size() + " patterns to " + var1.getFileName());
      }
   }

   private boolean resolve(double var1, double var3) {
      return this.handle(var1, var3, this.selection.instance, this.selection.data) <= this.selection.state;
   }

   private String resolve() {
      String var1 = this.current.render();
      if (!"Mixed".equals(var1)) {
         return var1;
      }

      String[] var2 = new String[]{"Flick", "Tracking", "Micro", "Vertical", "Diagonal", "Attack"};
      return var2[ThreadLocalRandom.current().nextInt(var2.length)];
   }

   private double update(double var1, double var3) {
      double var5 = ThreadLocalRandom.current().nextDouble(var1, Math.max(var1 + 1.0, var3));
      return ThreadLocalRandom.current().nextBoolean() ? var5 : -var5;
   }

   private float handle(double var1) {
      return (float)(var1 / Math.max(1.0, this.width) * 95.0);
   }

   private float process(double var1) {
      return (float)(var1 / Math.max(1.0, this.height) * 70.0);
   }

   private double handle(double var1, double var3, double var5, double var7) {
      return Math.hypot(var1 - var5, var3 - var7);
   }

   private void handle(DrawContext var1) {
      byte var2 = 12;
      byte var3 = 12;
      short var4 = 222;
      byte var5 = 74;
      var1.fill(var2 - 6, var3 - 6, var2 + var4, var3 + var5, -1441326300);
      var1.drawTextWithShadow(this.textRenderer, "RotationLab", var2, var3, -1446152);
      var1.drawTextWithShadow(this.textRenderer, "asset: " + RotationProfileRepository.process(this.current.refresh()), var2, var3 + 14, -7366230);
      var1.drawTextWithShadow(this.textRenderer, "mode: " + this.current.render().toLowerCase(Locale.ROOT), var2, var3 + 28, -7366230);
      var1.drawTextWithShadow(this.textRenderer, "patterns: " + this.active.size() + " / " + this.current.animate(), var2, var3 + 42, -7366230);
      var1.drawTextWithShadow(this.textRenderer, "R reset  D delete  Esc save", var2, var3 + 56, -7366230);
   }

   private void process(DrawContext var1) {
      if (this.mode.size() >= 2) {
         for (int var2 = Math.max(1, this.mode.size() - 20); var2 < this.mode.size(); var2++) {
            RotationProfileDataset.PrimaryCacheEntry var3 = this.mode.get(var2 - 1);
            RotationProfileDataset.PrimaryCacheEntry var4 = this.mode.get(var2);
            int var5 = (int)(this.handler + var3.data / 95.0F * this.width);
            int var6 = (int)(this.animationDraw + var3.context / 70.0F * this.height);
            int var7 = (int)(this.handler + var4.data / 95.0F * this.width);
            int var8 = (int)(this.animationDraw + var4.context / 70.0F * this.height);
            this.handle(var1, var5, var6, var7, var8, -2142256137);
         }
      }
   }

   private void compute(DrawContext var1) {
      if (this.selection != null) {
         this.handle(var1, (int)this.selection.instance, (int)this.selection.data, this.selection.state + 4, 956255850);
         this.handle(var1, (int)this.selection.instance, (int)this.selection.data, this.selection.state, -45462);
         this.handle(var1, (int)this.selection.instance, (int)this.selection.data, Math.max(2, this.selection.state / 4), -1);
         var1.drawTextWithShadow(
            this.textRenderer, this.selection.cache, (int)this.selection.instance + this.selection.state + 8, (int)this.selection.data - 4, -1446152
         );
      }
   }

   private void handle(DrawContext var1, int var2, int var3, int var4, int var5) {
      int var6 = var4 * var4;

      for (int var7 = -var4; var7 <= var4; var7++) {
         int var8 = (int)Math.sqrt(Math.max(0, var6 - var7 * var7));
         var1.fill(var2 - var8, var3 + var7, var2 + var8 + 1, var3 + var7 + 1, var5);
      }
   }

   private void handle(DrawContext var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = Math.abs(var4 - var2);
      int var8 = Math.abs(var5 - var3);
      int var9 = var2 < var4 ? 1 : -1;
      int var10 = var3 < var5 ? 1 : -1;
      int var11 = var7 - var8;

      while (true) {
         var1.fill(var2 - 1, var3 - 1, var2 + 2, var3 + 2, var6);
         if (var2 == var4 && var3 == var5) {
            return;
         }

         int var12 = var11 * 2;
         if (var12 > -var8) {
            var11 -= var8;
            var2 += var9;
         }

         if (var12 < var7) {
            var11 += var7;
            var3 += var10;
         }
      }
   }

   static final class CacheEntry {
      double instance;
      double data;
      double context;
      double config;
      final int state;
      final String cache;

      CacheEntry(double var1, double var3, int var5, String var6) {
         this.instance = var1;
         this.data = var3;
         this.state = var5;
         this.cache = var6;
      }
   }
}
