package ru.wild.gui.widget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import ru.wild.core.AnimationClock;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.ShaderGraphEditor;
import ru.wild.render.shader.ShaderNodeDefinition;
import ru.wild.render.shader.ShaderNodeRegistry;
import ru.wild.render.shader.ShaderPinDefinition;
import ru.wild.render.shader.ShaderValueType;
import ru.wild.util.math.RectBounds;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.math.SpringFloat;
import ru.wild.util.math.SpringParameters;
import ru.wild.util.render.RoundedRectRenderer;

public final class FoundryNodeSearchOverlay {
   private static final float instance = 16.0F;
   private static final int data = 40;
   private static final float context = 22.0F;
   private static final float config = 34.0F;
   private final ShaderNodeRegistry state;
   private final SpringFloat cache = new SpringFloat(AnimationClock.handle(), SpringParameters.handle(3.4F, 0.82F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F);
   private final SpringAnimation output = new SpringAnimation(0.0F);
   private final Map<String, SpringAnimation> current = new HashMap<>();
   private final Map<String, Boolean> active = new LinkedHashMap<>();
   private boolean mode;
   private float selection;
   private float enabled;
   private String renderer = "";
   private int handler;
   private float animationDraw;
   private float pointEncode;
   private boolean animator;
   private long source;
   private ShaderValueType target;
   private List<FuzzyToastMatcher.DataRecord> pending = new ArrayList<>();
   private List<ShaderNodeDefinition> previous = new ArrayList<>();

   public FoundryNodeSearchOverlay(ShaderNodeRegistry var1) {
      this.state = var1;
   }

   public boolean handle() {
      return this.mode;
   }

   public ShaderValueType process() {
      return this.target;
   }

   public float compute() {
      return this.selection;
   }

   public float resolve() {
      return this.enabled;
   }

   public void handle(float var1, float var2, ShaderValueType var3) {
      this.mode = true;
      this.selection = var1;
      this.enabled = var2;
      this.renderer = "";
      this.handler = 0;
      this.animationDraw = 0.0F;
      this.output.handle(0.0F);
      this.source = System.currentTimeMillis();
      this.target = var3;
      this.cache.compute(1.0F);
      this.onTick();
   }

   public void update() {
      this.mode = false;
      this.target = null;
      this.cache.compute(0.0F);
   }

   public void handle(char var1) {
      if (this.mode) {
         if ((
               var1 >= '0' && var1 <= '9'
                  || var1 >= 'a' && var1 <= 'z'
                  || var1 >= 'A' && var1 <= 'Z'
                  || var1 == ' '
                  || var1 == '_'
                  || var1 == '.'
                  || var1 == '-'
            )
            && this.renderer.length() < 40) {
            this.renderer = this.renderer + var1;
            this.handler = 0;
            this.animationDraw = 0.0F;
            this.source = System.currentTimeMillis();
            this.onTick();
         }
      }
   }

   public void apply() {
      if (this.mode && !this.renderer.isEmpty()) {
         this.renderer = this.renderer.substring(0, this.renderer.length() - 1);
         this.handler = 0;
         this.animationDraw = 0.0F;
         this.source = System.currentTimeMillis();
         this.onTick();
      }
   }

   public void execute() {
      if (this.mode) {
         this.renderer = "";
         this.handler = 0;
         this.animationDraw = 0.0F;
         this.source = System.currentTimeMillis();
         this.onTick();
      }
   }

   public void handle(int var1) {
      if (this.mode && !this.previous.isEmpty() && var1 != 0) {
         int var2 = var1 < 0 ? -1 : 1;
         int var3 = Math.floorMod(this.handler + var1, this.previous.size());
         if (this.renderer.isBlank()) {
            for (int var4 = 0; var4 < this.previous.size() && this.process(this.previous.get(var3).compute()); var4++) {
               var3 = Math.floorMod(var3 + var2, this.previous.size());
            }

            if (this.process(this.previous.get(var3).compute())) {
               return;
            }
         }

         this.handler = var3;
         this.animator = true;
      }
   }

   public ShaderNodeDefinition prepare() {
      return this.previous.isEmpty() ? null : this.previous.get(Math.min(this.handler, this.previous.size() - 1));
   }

   public List<ShaderNodeDefinition> check() {
      return this.previous;
   }

   public void handle(double var1) {
      if (this.mode) {
         this.animationDraw = Math.max(0.0F, Math.min(this.animationDraw - (float)var1 * 28.0F, Math.max(0.0F, this.pointEncode)));
      }
   }

   public void handle(String var1) {
      if (var1 != null) {
         boolean var2 = !this.active.getOrDefault(var1, false);
         this.active.put(var1, var2);
         if (var2 && this.renderer.isBlank() && !this.previous.isEmpty()) {
            ShaderNodeDefinition var3 = this.previous.get(Math.min(this.handler, this.previous.size() - 1));
            if (var1.equals(var3.compute())) {
               this.handle(1);
            }
         }
      }
   }

   public boolean process(String var1) {
      return this.active.getOrDefault(var1, false);
   }

   public RectBounds handle(GuiMetrics var1, int var2, int var3) {
      float var4 = Math.min(var1.handle(520.0F), var2 - var1.handle(64.0F));
      float var5 = Math.max(var1.handle(28.0F), var3 * 0.14F);
      float var6 = Math.min(var1.handle(500.0F), var3 - var5 - var1.handle(28.0F));
      float var7 = (var2 - var4) * 0.5F;
      return new RectBounds(var7, var5, var4, var6);
   }

   public RectBounds process(GuiMetrics var1, int var2, int var3) {
      RectBounds var4 = this.handle(var1, var2, var3);
      return new RectBounds(var4.x() + var1.handle(16.0F), var4.y() + var1.handle(16.0F), var4.w() - var1.handle(32.0F), var1.handle(46.0F));
   }

   public ShaderNodeDefinition handle(GuiMetrics var1, int var2, int var3, float var4, float var5) {
      FoundryNodeSearchOverlay.DataRecord var6 = this.compute(var1, var2, var3, var4, var5);
      return var6 == null ? null : var6.definition;
   }

   public String process(GuiMetrics var1, int var2, int var3, float var4, float var5) {
      if (this.mode && this.renderer.isBlank()) {
         FoundryNodeSearchOverlay.DataRecord var6 = this.compute(var1, var2, var3, var4, var5);
         return var6 == null ? null : var6.category;
      } else {
         return null;
      }
   }

   private FoundryNodeSearchOverlay.DataRecord compute(GuiMetrics var1, int var2, int var3, float var4, float var5) {
      if (!this.mode) {
         return null;
      }

      RectBounds var6 = this.handle(var1, var2, var3);
      float var7 = this.handle(var1, var6);
      float var8 = this.process(var1, var6);
      if (!(var4 < var6.x()) && !(var4 > var6.x() + var6.w()) && !(var5 < var7) && !(var5 > var8)) {
         float var9 = var7 - this.output.process();
         String var10 = "";
         boolean var11 = !this.renderer.isBlank();

         for (FuzzyToastMatcher.DataRecord var13 : this.pending) {
            ShaderNodeDefinition var14 = var13.def();
            if (!var11 && !var14.compute().equals(var10)) {
               var10 = var14.compute();
               if (var5 >= var9 && var5 < var9 + var1.handle(22.0F)) {
                  return new FoundryNodeSearchOverlay.DataRecord(null, var10);
               }

               var9 += var1.handle(22.0F);
               if (this.process(var10)) {
                  continue;
               }
            } else if (!var11 && this.process(var14.compute())) {
               continue;
            }

            float var15 = var1.handle(34.0F);
            if (var5 >= var9 && var5 < var9 + var15) {
               return new FoundryNodeSearchOverlay.DataRecord(var14, null);
            }

            var9 += var15;
            if (var9 > var8) {
               break;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private float handle(GuiMetrics var1, RectBounds var2) {
      return var2.y() + var1.handle(76.0F);
   }

   private float process(GuiMetrics var1, RectBounds var2) {
      return var2.y() + var2.h() - var1.handle(34.0F);
   }

   private void onTick() {
      String var1 = this.renderer == null ? "" : this.renderer.toLowerCase(Locale.ROOT).trim();
      ArrayList<ShaderNodeDefinition> var2 = new ArrayList<>(this.state.handle());
      if (this.target != null) {
         ArrayList<ShaderNodeDefinition> var3 = new ArrayList<>();

         for (ShaderNodeDefinition var5 : var2) {
            for (ShaderPinDefinition var7 : var5.update()) {
               if (var7.type() == this.target) {
                  var3.add(var5);
                  break;
               }
            }
         }

         var2 = var3;
      }

      ArrayList<FuzzyToastMatcher.DataRecord> var8 = new ArrayList<>();
      if (var1.isEmpty()) {
         var2.sort(Comparator.comparing(ShaderNodeDefinition::compute).thenComparing(ShaderNodeDefinition::process, String.CASE_INSENSITIVE_ORDER));

         for (ShaderNodeDefinition var12 : var2) {
            var8.add(new FuzzyToastMatcher.DataRecord(var12, 0, new int[0]));
         }
      } else {
         for (ShaderNodeDefinition var13 : var2) {
            FuzzyToastMatcher.DataRecord var15 = FuzzyToastMatcher.handle(var13, var1);
            if (var15 != null) {
               var8.add(var15);
            }
         }

         var8.sort(Comparator.<FuzzyToastMatcher.DataRecord>comparingInt(var0 -> -var0.score()).thenComparing(var0 -> var0.def().process()));
      }

      this.pending = var8;
      ArrayList<ShaderNodeDefinition> var11 = new ArrayList<>(var8.size());

      for (FuzzyToastMatcher.DataRecord var16 : var8) {
         var11.add(var16.def());
      }

      this.previous = var11;
      if (this.handler >= this.previous.size()) {
         this.handler = Math.max(0, this.previous.size() - 1);
      }
   }

   public void handle(RoundedRectRenderer var1, ThemeRenderContext var2, ModernClickGuiState var3, int var4, int var5) {
      float var6 = this.cache.handle();
      if (!(var6 <= 0.004F)) {
         GuiMetrics var7 = var2.update();
         ThemeColors var8 = var2.apply();
         boolean var9 = var8.unload();
         RectBounds var10 = this.handle(var7, var4, var5);
         float var11 = this.output.handle(Math.max(0.0F, Math.min(this.animationDraw, Math.max(0.0F, this.pointEncode))), SpringAnimationSpec.execute());
         var1.handle(16.0F);
         var1.handle(0.0F, 0.0F, var4, var5, 0.0F, var6);
         var1.handle(
            0.0F,
            0.0F,
            var4,
            var5,
            0.0F,
            var9 ? ThemeColors.handle(236, 239, 246, Math.round(96.0F * var6)) : ThemeColors.handle(3, 5, 9, Math.round(150.0F * var6))
         );
         float var12 = var10.x() + var10.w() * 0.5F;
         float var13 = var10.y() + var10.h() * 0.42F;
         var1.handle(0.92F + 0.08F * var6, var12, var13);
         var1.update(var6);

         try {
            float var14 = var7.handle(18.0F);
            var1.handle(
               var10.x(),
               var10.y(),
               var10.w(),
               var10.h(),
               var14,
               var7.handle(36.0F),
               var7.handle(2.0F),
               var9 ? ThemeColors.handle(24, 32, 48, 44) : ThemeColors.handle(0, 0, 0, 196)
            );
            var1.handle(var10.x(), var10.y(), var10.w(), var10.h(), var14, var9 ? ThemeColors.handle(250, 251, 254, 246) : ThemeColors.handle(9, 11, 17, 244));
            var1.handle(var10.x(), var10.y(), var10.w(), var10.h(), var14, ThemeColors.handle(var8.save(), 96), 0.9F);
            var1.handle(var10.x() + var14, var10.y(), var10.w() - var14 * 2.0F, 1.2F, ThemeColors.handle(var8.save(), 0), ThemeColors.handle(var8.save(), 170));
            this.handle(var1, var7, var8, var9, var10);
            this.handle(var1, var7, var8, var3, var10, var11, var9);
            this.handle(var1, var7, var8, var10);
         } finally {
            var1.onTick();
            var1.check();
         }
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, boolean var4, RectBounds var5) {
      RectBounds var6 = new RectBounds(var5.x() + var2.handle(16.0F), var5.y() + var2.handle(16.0F), var5.w() - var2.handle(32.0F), var2.handle(46.0F));
      float var7 = var2.handle(11.0F);
      var1.handle(var6.x(), var6.y(), var6.w(), var6.h(), var7, var2.handle(18.0F), 0.0F, ThemeColors.handle(var3.save(), 64));
      var1.handle(var6.x(), var6.y(), var6.w(), var6.h(), var7, var4 ? ThemeColors.handle(255, 255, 255, 244) : ThemeColors.handle(14, 16, 24, 240));
      var1.handle(var6.x(), var6.y(), var6.w(), var6.h(), var7, ThemeColors.handle(var3.save(), 188), 1.1F);
      float var8 = var6.x() + var2.handle(19.0F);
      float var9 = var6.y() + var6.h() * 0.5F - var2.handle(1.0F);
      var1.handle(var8, var9, var2.handle(4.4F), 0.0F, 1.0F, 1.4F, ThemeColors.handle(var3.save(), 230));
      var1.handle(var8 + var2.handle(3.2F), var9 + var2.handle(3.2F), var2.handle(5.4F), 1.4F, 0.7F, ThemeColors.handle(var3.save(), 230));
      float var10 = var6.x() + var2.handle(34.0F);
      float var11 = var6.y() + (var6.h() - var2.handle(15.0F)) * 0.5F;
      String var12 = this.renderer.isBlank() ? "Search nodes…" : this.renderer;
      int var13 = this.renderer.isBlank() ? var3.animate() : var3.load();
      ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var10, var11, 12.0F, var12, var13);
      boolean var14 = (System.currentTimeMillis() - this.source) / 500L % 2L == 0L;
      if (var14) {
         float var15 = var10 + (this.renderer.isBlank() ? 0.0F : ModuleStateHelper.handle(var2, FontRegistry.instance, this.renderer, 12.0F) + 1.5F);
         var1.handle(var15, var6.y() + var2.handle(11.0F), 1.2F, var6.h() - var2.handle(22.0F), 0.0F, ThemeColors.handle(var3.save(), 240));
      }

      if (this.target != null) {
         int var20 = ShaderGraphEditor.Mode.process(this.target);
         String var16 = "Connect → " + this.target.handle();
         float var17 = ModuleStateHelper.handle(var2, FontRegistry.instance, var16, 9.0F) + var2.handle(22.0F);
         float var18 = var6.x() + var6.w() - var17 - var2.handle(10.0F);
         float var19 = var6.y() + (var6.h() - var2.handle(20.0F)) * 0.5F;
         var1.handle(var18, var19, var17, var2.handle(20.0F), var2.handle(10.0F), ThemeColors.handle(var20, 46));
         var1.process(var18 + var2.handle(9.0F), var19 + var2.handle(10.0F), var2.handle(2.6F), 0.0F, 1.0F, var20);
         ModuleStateHelper.handle(
            var1, var2, FontRegistry.instance, var18 + var2.handle(16.0F), var19 + var2.handle(5.5F), 9.0F, var16, ThemeColors.handle(var20, 245)
         );
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, ModernClickGuiState var4, RectBounds var5, float var6, boolean var7) {
      float var8 = this.handle(var2, var5);
      float var9 = this.process(var2, var5);
      float var10 = var5.x() + var2.handle(10.0F);
      float var11 = var5.w() - var2.handle(20.0F);
      var1.compute();
      var1.handle(var10, var8, var11, var9 - var8, var2.handle(8.0F), var2.handle(8.0F), var2.handle(8.0F), var2.handle(8.0F));

      try {
         float var12 = var8 - var6;
         float var13 = 0.0F;
         float var14 = -1.0F;
         String var15 = "";
         int var16 = 0;
         boolean var17 = !this.renderer.isBlank();
         String var18 = this.renderer.toLowerCase(Locale.ROOT).trim();

         for (FuzzyToastMatcher.DataRecord var20 : this.pending) {
            ShaderNodeDefinition var21 = var20.def();
            if (!var17 && !var21.compute().equals(var15)) {
               var15 = var21.compute();
               boolean var22 = this.process(var15);
               if (var12 + var2.handle(22.0F) > var8 && var12 < var9) {
                  ModuleStateHelper.handle(
                     var1,
                     var2,
                     FontRegistry.config,
                     var10 + var2.handle(12.0F),
                     var12 + var2.handle(7.0F),
                     9.0F,
                     (var22 ? "▸ " : "▾ ") + var15.toUpperCase(Locale.ROOT),
                     ThemeColors.handle(var3.submit(), 215)
                  );
               }

               var12 += var2.handle(22.0F);
               var13 += var2.handle(22.0F);
               if (var22) {
                  var16++;
                  continue;
               }
            } else if (!var17 && this.process(var21.compute())) {
               var16++;
               continue;
            }

            float var31 = var2.handle(34.0F);
            if (var16 == this.handler) {
               var14 = var13;
            }

            if (var12 + var31 > var8 && var12 < var9) {
               this.handle(var1, var2, var3, var4, var21, var20, var18, var10, var12, var11, var31, var16 == this.handler, var7);
            }

            var12 += var31;
            var13 += var31;
            var16++;
         }

         this.pointEncode = Math.max(0.0F, var13 - (var9 - var8));
         if (this.animator && var14 >= 0.0F) {
            float var29 = var9 - var8;
            float var30 = var2.handle(34.0F);
            if (var14 < this.animationDraw) {
               this.animationDraw = Math.max(0.0F, var14 - var2.handle(22.0F));
            } else if (var14 + var30 > this.animationDraw + var29) {
               this.animationDraw = Math.min(this.pointEncode, var14 + var30 - var29 + var2.handle(6.0F));
            }

            this.animator = false;
         }

         if (this.pending.isEmpty()) {
            ModuleStateHelper.handle(
               var1, var2, FontRegistry.instance, var10 + var2.handle(14.0F), var8 + var2.handle(18.0F), 11.0F, "no matching nodes", var3.animate()
            );
         }
      } finally {
         var1.compute();
         var1.apply();
      }

      if (this.pointEncode > 0.0F) {
         float var26 = var9 - var8;
         float var27 = Math.max(var2.handle(26.0F), var26 * var26 / (var26 + this.pointEncode));
         float var28 = var8 + (var26 - var27) * (this.pointEncode <= 0.0F ? 0.0F : Math.min(1.0F, var6 / this.pointEncode));
         var1.handle(var5.x() + var5.w() - var2.handle(6.0F), var28, var2.handle(2.4F), var27, var2.handle(1.2F), ThemeColors.handle(var3.save(), 130));
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      ModernClickGuiState var4,
      ShaderNodeDefinition var5,
      FuzzyToastMatcher.DataRecord var6,
      String var7,
      float var8,
      float var9,
      float var10,
      float var11,
      boolean var12,
      boolean var13
   ) {
      boolean var14 = var4 != null
         && var4.sampleLayer() >= var8
         && var4.sampleLayer() <= var8 + var10
         && var4.sendWorld() >= var9
         && var4.sendWorld() < var9 + var11;
      SpringAnimation var15 = this.current.computeIfAbsent(var5.handle(), var0 -> new SpringAnimation(0.0F));
      float var16 = var15.handle(Math.max(var14 ? 0.72F : 0.0F, var12 ? 1.0F : 0.0F), SpringAnimationSpec.select());
      var1.handle(
         var8 + var2.handle(4.0F),
         var9 + var2.handle(1.5F),
         var10 - var2.handle(8.0F),
         var11 - var2.handle(3.0F),
         var2.handle(8.0F),
         ThemeColors.handle(var13 ? ThemeColors.handle(10, 14, 22, 5) : ThemeColors.handle(255, 255, 255, 5), ThemeColors.handle(var3.save(), 62), var16)
      );
      if (var12) {
         var1.handle(
            var8 + var2.handle(4.0F),
            var9 + var2.handle(7.0F),
            var2.handle(2.4F),
            var11 - var2.handle(14.0F),
            var2.handle(1.2F),
            ThemeColors.handle(var3.save(), 235)
         );
      }

      int var17 = var5.apply().isEmpty() ? var3.encodePoint() : ShaderGraphEditor.Mode.process(var5.apply().get(0).type());
      float var18 = var8 + var2.handle(18.0F);
      float var19 = var9 + var11 * 0.5F;
      var1.process(var18, var19, var2.handle(3.4F) + var16 * var2.handle(0.8F), 0.0F, 1.0F, ThemeColors.handle(var17, 235));
      var1.process(var18, var19, var2.handle(1.4F), 0.0F, 1.0F, var13 ? ThemeColors.handle(255, 255, 255, 235) : ThemeColors.handle(9, 11, 17, 235));
      int var20 = ThemeColors.handle(var3.animate(), var3.load(), 0.62F + var16 * 0.38F);
      this.handle(var1, var2, var3, var5.process(), var6.titlePositions(), var7, var8 + var2.handle(32.0F), var9 + var2.handle(7.0F), 11.0F, var20);
      ModuleStateHelper.handle(
         var1, var2, FontRegistry.instance, var8 + var2.handle(32.0F), var9 + var2.handle(20.0F), 7.5F, var5.compute(), ThemeColors.handle(var3.animate(), 200)
      );
      float var21 = var8 + var10 - var2.handle(14.0F);
      int var22 = Math.min(var5.update().size(), 4);

      for (int var23 = var22 - 1; var23 >= 0; var23--) {
         int var24 = ShaderGraphEditor.Mode.process(var5.update().get(var23).type());
         var1.process(var21, var19, var2.handle(2.2F), 0.0F, 1.0F, ThemeColors.handle(var24, 225));
         var21 -= var2.handle(7.0F);
      }

      String var25 = var5.apply().isEmpty() ? "sink" : var5.apply().get(0).type().handle();
      float var26 = ModuleStateHelper.handle(var2, FontRegistry.instance, var25, 8.0F);
      ModuleStateHelper.handle(
         var1, var2, FontRegistry.instance, var21 - var26 - var2.handle(8.0F), var9 + var2.handle(11.0F), 8.0F, var25, ThemeColors.handle(var17, 240)
      );
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, String var4, int[] var5, String var6, float var7, float var8, float var9, int var10
   ) {
      if (var5 != null && var5.length != 0 && !var6.isEmpty()) {
         int var11 = ThemeColors.handle(var3.save(), 250);
         float var12 = var7;
         int var13 = 0;
         int var14 = 0;

         while (var13 < var4.length()) {
            boolean var15 = var14 < var5.length && var5[var14] == var13;
            int var16 = var13;
            if (var15) {
               while (var14 < var5.length && var5[var14] == var16) {
                  var14++;
                  var16++;
               }
            } else {
               int var17 = var14 < var5.length ? var5[var14] : var4.length();
               var16 = Math.max(var13 + 1, var17);
            }

            var16 = Math.min(var16, var4.length());
            String var19 = var4.substring(var13, var16);
            ModuleStateHelper.handle(var1, var2, FontRegistry.config, var12, var8, var9, var19, var15 ? var11 : var10);
            var12 += ModuleStateHelper.handle(var2, FontRegistry.config, var19, var9);
            var13 = var16;
         }
      } else {
         ModuleStateHelper.handle(var1, var2, FontRegistry.config, var7, var8, var9, var4, var10);
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4) {
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.instance,
         var4.x() + var2.handle(16.0F),
         var4.y() + var4.h() - var2.handle(24.0F),
         8.0F,
         "↑↓ navigate • Enter spawn • LMB on category to toggle • Esc close",
         ThemeColors.handle(var3.load(), 150)
      );
      String var5 = this.previous.size() + (this.previous.size() == 1 ? " node" : " nodes");
      float var6 = ModuleStateHelper.handle(var2, FontRegistry.instance, var5, 8.0F);
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.instance,
         var4.x() + var4.w() - var6 - var2.handle(16.0F),
         var4.y() + var4.h() - var2.handle(24.0F),
         8.0F,
         var5,
         ThemeColors.handle(var3.submit(), 210)
      );
   }

   record DataRecord(ShaderNodeDefinition definition, String category) {
   }
}
