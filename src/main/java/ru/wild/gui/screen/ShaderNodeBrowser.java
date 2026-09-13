package ru.wild.gui.screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.ShaderNodeDefinition;
import ru.wild.render.shader.ShaderNodeRegistry;
import ru.wild.render.shader.ShaderPinDefinition;
import ru.wild.render.shader.ShaderValueType;
import ru.wild.util.math.RectBounds;
import ru.wild.util.render.RoundedRectRenderer;

public final class ShaderNodeBrowser {
   private final ShaderNodeRegistry instance;
   private boolean data;
   private float context;
   private float config;
   private String state = "";
   private int cache;
   private float output;
   private long current;
   private ShaderValueType active;
   private List<ShaderNodeDefinition> mode = new ArrayList<>();
   private final Map<String, Boolean> selection = new LinkedHashMap<>();

   public ShaderNodeBrowser(ShaderNodeRegistry var1) {
      this.instance = var1;
   }

   public boolean handle() {
      return this.data;
   }

   public ShaderValueType process() {
      return this.active;
   }

   public float compute() {
      return this.context;
   }

   public float resolve() {
      return this.config;
   }

   public void handle(float var1, float var2, ShaderValueType var3) {
      this.data = true;
      this.context = var1;
      this.config = var2;
      this.state = "";
      this.cache = 0;
      this.output = 0.0F;
      this.current = System.currentTimeMillis();
      this.active = var3;
      this.onTick();
   }

   public void update() {
      this.data = false;
      this.active = null;
   }

   public void handle(char var1) {
      if (this.data) {
         if ((var1 >= '0' && var1 <= '9' || var1 >= 'a' && var1 <= 'z' || var1 >= 'A' && var1 <= 'Z' || var1 == ' ' || var1 == '_' || var1 == '.')
            && this.state.length() < 32) {
            this.state = this.state + var1;
            this.cache = 0;
            this.output = 0.0F;
            this.current = System.currentTimeMillis();
            this.onTick();
         }
      }
   }

   public void apply() {
      if (this.data && !this.state.isEmpty()) {
         this.state = this.state.substring(0, this.state.length() - 1);
         this.cache = 0;
         this.output = 0.0F;
         this.current = System.currentTimeMillis();
         this.onTick();
      }
   }

   public void execute() {
      if (this.data) {
         this.state = "";
         this.cache = 0;
         this.output = 0.0F;
         this.current = System.currentTimeMillis();
         this.onTick();
      }
   }

   public void handle(int var1) {
      if (this.data && !this.mode.isEmpty()) {
         this.cache = Math.floorMod(this.cache + var1, this.mode.size());
      }
   }

   public ShaderNodeDefinition prepare() {
      return this.mode.isEmpty() ? null : this.mode.get(Math.min(this.cache, this.mode.size() - 1));
   }

   public List<ShaderNodeDefinition> check() {
      return this.mode;
   }

   public void handle(double var1) {
      if (this.data) {
         this.output = Math.max(0.0F, this.output - (float)var1 * 24.0F);
      }
   }

   public void handle(String var1) {
      if (var1 != null) {
         this.selection.put(var1, !this.selection.getOrDefault(var1, false));
      }
   }

   public boolean process(String var1) {
      return this.selection.getOrDefault(var1, false);
   }

   public RectBounds handle(GuiMetrics var1, int var2, int var3) {
      float var4 = var1.handle(340.0F);
      float var5 = var1.handle(440.0F);
      float var6 = Math.max(var1.handle(16.0F), Math.min(this.context - var4 * 0.18F, var2 - var4 - var1.handle(16.0F)));
      float var7 = Math.max(var1.handle(16.0F), Math.min(this.config - var1.handle(28.0F), var3 - var5 - var1.handle(16.0F)));
      return new RectBounds(var6, var7, var4, var5);
   }

   public RectBounds process(GuiMetrics var1, int var2, int var3) {
      RectBounds var4 = this.handle(var1, var2, var3);
      return new RectBounds(var4.x() + var1.handle(12.0F), var4.y() + var1.handle(38.0F), var4.w() - var1.handle(24.0F), var1.handle(30.0F));
   }

   public ShaderNodeDefinition handle(GuiMetrics var1, int var2, int var3, float var4, float var5) {
      if (!this.data) {
         return null;
      }

      RectBounds var6 = this.handle(var1, var2, var3);
      float var7 = var6.y() + var1.handle(80.0F);
      float var8 = var6.y() + var6.h() - var1.handle(40.0F);
      if (!(var4 < var6.x()) && !(var4 > var6.x() + var6.w()) && !(var5 < var7) && !(var5 > var8)) {
         float var9 = var7 - this.output;
         String var10 = "";

         for (ShaderNodeDefinition var12 : this.mode) {
            if (!var12.compute().equals(var10)) {
               var10 = var12.compute();
               if (var5 >= var9 && var5 < var9 + var1.handle(20.0F)) {
                  return null;
               }

               var9 += var1.handle(20.0F);
               if (this.process(var10) && this.state.isBlank()) {
                  continue;
               }
            } else if (this.process(var10) && this.state.isBlank()) {
               continue;
            }

            float var13 = var1.handle(28.0F);
            if (var5 >= var9 && var5 < var9 + var13) {
               return var12;
            }

            var9 += var13;
            if (var9 > var8) {
               break;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public String process(GuiMetrics var1, int var2, int var3, float var4, float var5) {
      if (this.data && this.state.isBlank()) {
         RectBounds var6 = this.handle(var1, var2, var3);
         float var7 = var6.y() + var1.handle(80.0F);
         float var8 = var6.y() + var6.h() - var1.handle(40.0F);
         if (!(var4 < var6.x()) && !(var4 > var6.x() + var6.w()) && !(var5 < var7) && !(var5 > var8)) {
            float var9 = var7 - this.output;
            String var10 = "";

            for (ShaderNodeDefinition var12 : this.mode) {
               if (!var12.compute().equals(var10)) {
                  var10 = var12.compute();
                  if (var5 >= var9 && var5 < var9 + var1.handle(20.0F)) {
                     return var10;
                  }

                  var9 += var1.handle(20.0F);
                  if (this.process(var10)) {
                     continue;
                  }
               } else if (this.process(var10)) {
                  continue;
               }

               var9 += var1.handle(28.0F);
               if (var9 > var8) {
                  break;
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void onTick() {
      String var1 = this.state == null ? "" : this.state.toLowerCase(Locale.ROOT).trim();
      ArrayList<ShaderNodeDefinition> var2 = new ArrayList<>(this.instance.handle());
      if (this.active != null) {
         ArrayList<ShaderNodeDefinition> var3 = new ArrayList<>();

         for (ShaderNodeDefinition var5 : var2) {
            for (ShaderPinDefinition var7 : var5.update()) {
               if (var7.type() == this.active) {
                  var3.add(var5);
                  break;
               }
            }
         }

         var2 = var3;
      }

      if (var1.isEmpty()) {
         var2.sort(Comparator.comparing(ShaderNodeDefinition::compute).thenComparing(ShaderNodeDefinition::process, String.CASE_INSENSITIVE_ORDER));
         this.mode = var2;
      } else {
         ArrayList<ShaderNodeBrowser.DataRecord> var8 = new ArrayList<>();

         for (ShaderNodeDefinition var11 : var2) {
            int var13 = handle(var11, var1);
            if (var13 > 0) {
               var8.add(new ShaderNodeBrowser.DataRecord(var11, var13));
            }
         }

         var8.sort(Comparator.<ShaderNodeBrowser.DataRecord>comparingInt(var0 -> -var0.score).thenComparing(var0 -> var0.def.process()));
         ArrayList<ShaderNodeDefinition> var10 = new ArrayList<>();

         for (ShaderNodeBrowser.DataRecord var14 : var8) {
            var10.add(var14.def);
         }

         this.mode = var10;
      }
   }

   private static int handle(ShaderNodeDefinition var0, String var1) {
      String var2 = var0.process().toLowerCase(Locale.ROOT);
      String var3 = var0.compute().toLowerCase(Locale.ROOT);
      String var4 = var0.handle().toLowerCase(Locale.ROOT);
      byte var5 = 0;
      if (var2.startsWith(var1)) {
         var5 += 80;
      }

      if (var2.contains(var1)) {
         var5 += 40;
      }

      if (var4.contains(var1)) {
         var5 += 30;
      }

      if (var3.contains(var1)) {
         var5 += 15;
      }

      int var6 = 0;
      int var7 = 0;

      for (int var8 = 0; var8 < var1.length(); var8++) {
         int var9 = var2.indexOf(var1.charAt(var8), var7);
         if (var9 < 0) {
            break;
         }

         var6++;
         var7 = var9 + 1;
      }

      if (var6 == var1.length()) {
         var5 += 25;
      }

      return var5;
   }

   public void handle(RoundedRectRenderer var1, ThemeRenderContext var2, ModernClickGuiState var3, int var4, int var5) {
      if (this.data) {
         GuiMetrics var6 = var2.update();
         ThemeColors var7 = var2.apply();
         RectBounds var8 = this.handle(var6, var4, var5);
         float var9 = var6.handle(12.0F);
         var1.handle(
            var8.x(),
            var8.y(),
            var8.w(),
            var8.h(),
            var9,
            var6.handle(28.0F),
            var6.handle(2.0F),
            var7.unload() ? ThemeColors.handle(10, 31, 10, 30) : ThemeColors.handle(0, 0, 0, 168)
         );
         var1.handle(
            var8.x(),
            var8.y(),
            var8.w(),
            var8.h(),
            var9,
            var7.unload()
               ? ThemeColors.handle(ThemeColors.handle(255, 255, 255, 246), ThemeColors.handle(var7.save(), 246), 0.035F)
               : ThemeColors.handle(8, 10, 16, 240)
         );
         var1.handle(var8.x(), var8.y(), var8.w(), var8.h(), var9, ThemeColors.handle(var7.save(), 108), 0.9F);
         ModuleStateHelper.handle(
            var1,
            var6,
            FontRegistry.config,
            var8.x() + var6.handle(14.0F),
            var8.y() + var6.handle(14.0F),
            12.0F,
            this.active != null ? "Connect → " + this.active.handle() : "Node Browser",
            var7.load()
         );
         ModuleStateHelper.handle(
            var1,
            var6,
            FontRegistry.instance,
            var8.x() + var8.w() - var6.handle(70.0F),
            var8.y() + var6.handle(16.0F),
            8.0F,
            "Enter • Esc",
            ThemeColors.handle(var7.save(), 200)
         );
         RectBounds var10 = this.process(var6, var4, var5);
         var1.handle(
            var10.x(),
            var10.y(),
            var10.w(),
            var10.h(),
            var6.handle(7.0F),
            var7.unload()
               ? ThemeColors.handle(ThemeColors.handle(255, 255, 255, 242), ThemeColors.handle(var7.save(), 242), 0.028F)
               : ThemeColors.handle(14, 16, 22, 232)
         );
         var1.handle(var10.x(), var10.y(), var10.w(), var10.h(), var6.handle(7.0F), ThemeColors.handle(var7.save(), 156), 0.8F);
         var1.process(var10.x() + var6.handle(11.0F), var10.y() + var10.h() * 0.5F, var6.handle(3.4F), 0.0F, 1.0F, ThemeColors.handle(var7.save(), 220));
         var1.handle(
            var10.x() + var6.handle(13.5F),
            var10.y() + var10.h() * 0.5F + var6.handle(1.4F),
            var6.handle(6.0F),
            1.1F,
            0.0F,
            ThemeColors.handle(var7.save(), 220)
         );
         String var11 = this.state.isBlank() ? "type to search…" : this.state;
         int var12 = this.state.isBlank() ? var7.animate() : var7.load();
         ModuleStateHelper.handle(var1, var6, FontRegistry.instance, var10.x() + var6.handle(22.0F), var10.y() + var6.handle(8.0F), 10.0F, var11, var12);
         if (!this.state.isBlank()) {
            float var13 = ModuleStateHelper.handle(var6, FontRegistry.instance, this.state, 10.0F);
            boolean var14 = (System.currentTimeMillis() - this.current) / 500L % 2L == 0L;
            if (var14) {
               var1.handle(
                  var10.x() + var6.handle(22.0F) + var13 + 1.0F,
                  var10.y() + var6.handle(6.0F),
                  1.0F,
                  var10.h() - var6.handle(12.0F),
                  0.0F,
                  ThemeColors.handle(var7.save(), 240)
               );
            }
         }

         float var29 = var8.y() + var6.handle(80.0F);
         float var30 = var8.y() + var8.h() - var6.handle(40.0F);
         var1.compute();
         var1.handle(
            var8.x() + var6.handle(8.0F),
            var29,
            var8.w() - var6.handle(16.0F),
            var30 - var29,
            var6.handle(6.0F),
            var6.handle(6.0F),
            var6.handle(6.0F),
            var6.handle(6.0F)
         );

         try {
            float var15 = var29 - this.output;
            String var16 = "";
            int var17 = 0;
            String var18 = this.state.toLowerCase(Locale.ROOT);

            for (ShaderNodeDefinition var20 : this.mode) {
               if (!var20.compute().equals(var16)) {
                  var16 = var20.compute();
                  boolean var21 = this.state.isBlank() && this.process(var16);
                  ModuleStateHelper.handle(
                     var1,
                     var6,
                     FontRegistry.config,
                     var8.x() + var6.handle(20.0F),
                     var15 + var6.handle(6.0F),
                     9.0F,
                     (var21 ? "▸ " : "▾ ") + var16.toUpperCase(Locale.ROOT),
                     ThemeColors.handle(var7.submit(), 220)
                  );
                  var15 += var6.handle(20.0F);
                  if (var21) {
                     continue;
                  }
               } else if (this.state.isBlank() && this.process(var16)) {
                  continue;
               }

               float var31 = var6.handle(28.0F);
               boolean var22 = var3 != null
                  && var3.sampleLayer() >= var8.x() + var6.handle(12.0F)
                  && var3.sampleLayer() <= var8.x() + var8.w() - var6.handle(12.0F)
                  && var3.sendWorld() >= var15
                  && var3.sendWorld() < var15 + var31;
               boolean var23 = var17 == this.cache;
               float var24 = Math.max(var22 ? 0.7F : 0.0F, var23 ? 1.0F : 0.0F);
               var1.handle(
                  var8.x() + var6.handle(12.0F),
                  var15,
                  var8.w() - var6.handle(24.0F),
                  var31 - var6.handle(2.0F),
                  var6.handle(6.0F),
                  ThemeColors.handle(ThemeColors.handle(255, 255, 255, 6), ThemeColors.handle(var7.save(), 72), var24)
               );
               var1.process(
                  var8.x() + var6.handle(22.0F),
                  var15 + var31 * 0.5F - var6.handle(1.0F),
                  var6.handle(2.6F),
                  0.0F,
                  1.0F,
                  ThemeColors.handle(var7.encodePoint(), var7.save(), var24)
               );
               this.handle(var1, var6, var7, var20.process(), var18, var8.x() + var6.handle(34.0F), var15 + var6.handle(5.0F), 10.0F, var24);
               String var25 = var20.apply().isEmpty() ? "output ✕" : var20.apply().get(0).type().handle();
               ModuleStateHelper.handle(
                  var1,
                  var6,
                  FontRegistry.instance,
                  var8.x() + var8.w() - var6.handle(60.0F),
                  var15 + var6.handle(8.0F),
                  8.0F,
                  var25,
                  ThemeColors.handle(var7.submit(), 220)
               );
               var15 += var31;
               var17++;
               if (var15 > var30 + var31) {
                  break;
               }
            }

            if (this.mode.isEmpty()) {
               ModuleStateHelper.handle(
                  var1, var6, FontRegistry.instance, var8.x() + var6.handle(20.0F), var29 + var6.handle(20.0F), 10.0F, "no matches", var7.animate()
               );
            }
         } finally {
            var1.compute();
            var1.apply();
         }

         ModuleStateHelper.handle(
            var1,
            var6,
            FontRegistry.instance,
            var8.x() + var6.handle(14.0F),
            var8.y() + var8.h() - var6.handle(20.0F),
            8.0F,
            "↑↓ navigate • Enter spawn • LMB on category to toggle • Wheel scroll",
            ThemeColors.handle(var7.load(), 156)
         );
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, String var4, String var5, float var6, float var7, float var8, float var9) {
      int var10 = ThemeColors.handle(var3.animate(), var3.load(), 0.6F + var9 * 0.4F);
      if (var5 != null && !var5.isEmpty()) {
         String var11 = var4.toLowerCase(Locale.ROOT);
         int var12 = var11.indexOf(var5);
         if (var12 < 0) {
            ModuleStateHelper.handle(var1, var2, FontRegistry.config, var6, var7, var8, var4, var10);
         } else {
            String var13 = var4.substring(0, var12);
            String var14 = var4.substring(var12, var12 + var5.length());
            String var15 = var4.substring(var12 + var5.length());
            float var16 = ModuleStateHelper.handle(var2, FontRegistry.config, var13, var8);
            float var17 = ModuleStateHelper.handle(var2, FontRegistry.config, var14, var8);
            ModuleStateHelper.handle(var1, var2, FontRegistry.config, var6, var7, var8, var13, var10);
            ModuleStateHelper.handle(var1, var2, FontRegistry.config, var6 + var16, var7, var8, var14, ThemeColors.handle(var3.save(), 245));
            ModuleStateHelper.handle(var1, var2, FontRegistry.config, var6 + var16 + var17, var7, var8, var15, var10);
         }
      } else {
         ModuleStateHelper.handle(var1, var2, FontRegistry.config, var6, var7, var8, var4, var10);
      }
   }

   record DataRecord(ShaderNodeDefinition def, int score) {
   }
}
