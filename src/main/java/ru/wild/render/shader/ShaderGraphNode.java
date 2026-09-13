package ru.wild.render.shader;

import java.util.Locale;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.RectBounds;
import ru.wild.util.render.RoundedRectRenderer;

public final class ShaderGraphNode {
   private static final long instance = 600L;
   private static final float data = 2.0F;
   private static final int context = 12;
   private static final int config = 48;
   private final ShaderGraphNode.Mode state;
   private float cache;
   private float output;
   private float current;
   private float active;
   private float mode;
   private String selection = "";
   private boolean enabled;
   private boolean renderer;
   private boolean handler;
   private long animationDraw;
   private float pointEncode;
   private float animator;
   private float source;
   private String target = "";
   private long pending;

   public ShaderGraphNode(ShaderGraphNode.Mode var1, float var2, float var3, float var4, float var5) {
      this.state = var1;
      this.cache = var2;
      this.output = var3;
      this.current = var4;
      this.active = var5;
   }

   public static ShaderGraphNode handle(float var0, float var1) {
      return new ShaderGraphNode(ShaderGraphNode.Mode.NUMERIC, var0, var1, 0.012F, 0.001F);
   }

   public static ShaderGraphNode handle() {
      return new ShaderGraphNode(ShaderGraphNode.Mode.TEXT, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public ShaderGraphNode.Mode process() {
      return this.state;
   }

   public void process(float var1, float var2) {
      this.cache = var1;
      this.output = var2;
      this.mode = this.process(this.mode);
   }

   public void compute(float var1, float var2) {
      this.current = var1;
      this.active = var2;
   }

   public void handle(float var1) {
      this.mode = this.process(var1);
   }

   public void handle(String var1) {
      this.selection = var1 == null ? "" : var1;
   }

   public float compute() {
      return this.mode;
   }

   public String resolve() {
      return this.selection;
   }

   public boolean update() {
      return this.enabled;
   }

   public boolean apply() {
      return this.handler;
   }

   public boolean execute() {
      return this.renderer;
   }

   public boolean prepare() {
      return this.enabled || this.handler || this.renderer;
   }

   public boolean handle(float var1, float var2, int var3, RectBounds var4) {
      if (var3 != 0) {
         return false;
      }

      if (var4 == null || !var4.contains(var1, var2)) {
         return false;
      }

      if (this.enabled) {
         return true;
      }

      this.renderer = true;
      this.handler = false;
      this.animationDraw = System.currentTimeMillis();
      this.pointEncode = var1;
      this.animator = var1;
      this.source = this.mode;
      return true;
   }

   public boolean handle(float var1, float var2, boolean var3) {
      if (this.enabled || this.state != ShaderGraphNode.Mode.NUMERIC) {
         return false;
      }

      if (!this.renderer && !this.handler) {
         return false;
      }

      if (!this.handler && Math.abs(var1 - this.pointEncode) > 2.0F) {
         this.handler = true;
      }

      if (this.handler) {
         float var4 = var3 ? this.active : this.current;
         float var5 = (var1 - this.animator) * var4;
         this.mode = this.process(this.source + var5);
         return true;
      } else {
         return false;
      }
   }

   public boolean resolve(float var1, float var2) {
      if (this.enabled) {
         this.renderer = false;
         this.handler = false;
         return false;
      }

      boolean var3 = this.handler;
      if (this.renderer && !this.handler && System.currentTimeMillis() - this.animationDraw < 600L) {
         this.enabled = true;
         this.target = this.state == ShaderGraphNode.Mode.NUMERIC ? process(compute(this.mode)) : this.selection;
         this.pending = System.currentTimeMillis();
      }

      this.renderer = false;
      this.handler = false;
      return var3;
   }

   public boolean handle(char var1) {
      if (!this.enabled) {
         return false;
      }

      if (this.state == ShaderGraphNode.Mode.NUMERIC) {
         if (var1 >= '0' && var1 <= '9' || var1 == '.' || var1 == ',' || var1 == '-') {
            if (var1 == '-' && !this.target.isEmpty()) {
               return true;
            }

            if ((var1 == '.' || var1 == ',') && this.target.contains(".")) {
               return true;
            }

            if (this.target.length() < 12) {
               this.target = this.target + (var1 == ',' ? '.' : var1);
               this.pending = System.currentTimeMillis();
            }
         }

         return true;
      } else {
         if (this.target.length() < 48 && (Character.isLetterOrDigit(var1) || var1 == ' ' || var1 == '_' || var1 == '-' || var1 == '.')) {
            this.target = this.target + var1;
            this.pending = System.currentTimeMillis();
         }

         return true;
      }
   }

   public boolean handle(int var1) {
      if (!this.enabled) {
         return false;
      }

      if (var1 == 256) {
         this.enabled = false;
         this.target = "";
         return true;
      }

      if (var1 == 257 || var1 == 335 || var1 == 258) {
         this.check();
         return true;
      }

      if (var1 == 259) {
         if (!this.target.isEmpty()) {
            this.target = this.target.substring(0, this.target.length() - 1);
            this.pending = System.currentTimeMillis();
         }

         return true;
      } else {
         return true;
      }
   }

   public void check() {
      if (this.enabled) {
         if (this.state == ShaderGraphNode.Mode.NUMERIC) {
            try {
               float var1 = Float.parseFloat(this.target.replace(',', '.'));
               if (Float.isFinite(var1)) {
                  this.mode = this.process(var1);
               }
            } catch (NumberFormatException var2) {
            }
         } else {
            this.selection = this.target;
         }

         this.enabled = false;
         this.target = "";
      }
   }

   public void onTick() {
      this.enabled = false;
      this.target = "";
      this.renderer = false;
      this.handler = false;
   }

   public void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, float var5, float var6) {
      boolean var7 = var4.contains(var5, var6);
      boolean var8 = var7 || this.handler || this.renderer;
      int var9 = this.enabled
         ? ThemeColors.handle(var3.save(), 132)
         : ThemeColors.handle(var3.check(), ThemeColors.handle(var3.submit(), 56), var8 ? 1.0F : 0.0F);
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(6.0F), var9);
      if (!this.enabled && this.state == ShaderGraphNode.Mode.NUMERIC) {
         float var10 = Math.max(1.0E-4F, this.output - this.cache);
         float var11 = Math.max(0.0F, Math.min(1.0F, (this.mode - this.cache) / var10));
         var1.handle(var4.x(), var4.y(), var4.w() * var11, var4.h(), var2.handle(6.0F), ThemeColors.handle(var3.save(), var8 ? 80 : 48));
      }

      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var2.handle(6.0F),
         this.enabled ? ThemeColors.handle(var3.save(), 230) : ThemeColors.handle(var3.select(), ThemeColors.handle(var3.save(), 122), var8 ? 1.0F : 0.0F),
         this.enabled ? 1.0F : 0.6F
      );
      String var14 = this.enabled ? this.target : (this.state == ShaderGraphNode.Mode.NUMERIC ? process(compute(this.mode)) : this.selection);
      float var15 = ModuleStateHelper.handle(var2, FontRegistry.instance, var14, 9.0F);
      int var12 = var3.unload() ? ThemeColors.handle(10, 10, 10, 255) : var3.load();
      ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var4.x() + (var4.w() - var15) * 0.5F, var4.y(), var4.h(), 9.0F, var14, var12);
      if (this.enabled && (System.currentTimeMillis() - this.pending) / 500L % 2L == 0L) {
         float var13 = var4.x() + (var4.w() - var15) * 0.5F + var15 + var2.handle(1.5F);
         var1.handle(var13, var4.y() + var2.handle(3.0F), 1.0F, var4.h() - var2.handle(6.0F), 0.0F, ThemeColors.handle(var3.save(), 240));
      }
   }

   private float process(float var1) {
      return !Float.isFinite(var1) ? this.mode : Math.max(this.cache, Math.min(this.output, var1));
   }

   private static String compute(float var0) {
      return String.format(Locale.ROOT, "%.3f", var0);
   }

   private static String process(String var0) {
      if (var0 != null && var0.contains(".")) {
         int var1 = var0.length();

         while (var1 > 0 && var0.charAt(var1 - 1) == '0') {
            var1--;
         }

         if (var1 > 0 && var0.charAt(var1 - 1) == '.') {
            var1--;
         }

         return var0.substring(0, var1);
      } else {
         return var0;
      }
   }

   public enum Mode {
      NUMERIC,
      TEXT;
   }
}
