package ru.wild.gui.widget;

import java.util.Objects;
import ru.wild.api.setting.KeybindMode;
import ru.wild.api.setting.KeybindTargetModel;
import ru.wild.gui.theme.AccentColorToken;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.render.ColorCompositor;
import ru.wild.util.render.RoundedRectRenderer;

public final class KeybindEditorRenderer {
   private static final float instance = 334.0F;
   private static final float data = 48.0F;
   private static final float context = 62.0F;
   private static final float config = 62.0F;
   private static final float state = 14.0F;
   private static final float cache = 0.0F;
   private static final float output = 0.0F;
   private static final float current = 10.0F;
   private static final float active = 34.0F;
   private static final float mode = 156.0F;
   private static final float selection = 12.0F;
   private static final float enabled = 8.0F;
   private static final float renderer = 143.0F;
   private static final float handler = 38.0F;
   private static final float animationDraw = 6.0F;
   private static final float pointEncode = 12.0F;
   private static final float animator = 18.0F;
   private static final float source = 27.0F;
   private static final float target = 10.0F;
   private static final float pending = 10.0F;
   private static final float previous = 8.0F;
   private static final float latest = 1.0F;
   private static final float summary = 20.0F;
   private static final float matrixBlend = 15.0F;
   private static final float vectorMatch = 17.0F;
   private static final float itemProject = 18.0F;
   private static final float responseCompute = 17.0F;
   private static final int providerFetch = 1447446;
   private static final int profileDraw = 3355443;
   private static final int vectorPerform = 5197646;
   private static final int eventAttach = 6974057;
   private static final int serverRead = 16777215;
   private static final int positionAdvance = 8947848;
   private static final int frameCheck = 7105644;
   private static final int moduleCollect = 14765389;
   private static final double providerClose = 0.76;
   private static final double presetSave = 0.08;
   private static final double windowConvert = 0.05;
   private static final double presetWrite = 0.12;
   private static final double colorMeasure = 0.18;
   private static final double animationSchedule = 0.14;
   private static final double rendererScan = 0.18;
   private static final double sourceBuild = 0.18;
   private static final double outputCollapse = 0.24;
   private static final double profileInvoke = 0.06;

   private KeybindEditorRenderer() {
   }

   public static KeybindEditorRenderer.DataRecord handle(KeybindTargetModel var0, float var1, float var2, float var3) {
      Objects.requireNonNull(var0, "model");
      float var4 = Math.max(0.0F, var3);
      boolean var5 = var4 > 0.001F;
      float var6 = 0.0F;
      float var7 = var5 ? 0.0F : 0.0F;
      float var8 = var5 ? var6 + var4 + var7 : 0.0F;
      float var9 = 124.0F + var8;
      KeybindEditorRenderer.State var10 = new KeybindEditorRenderer.State(var1, var2, 334.0F, var9);
      KeybindEditorRenderer.State var11 = new KeybindEditorRenderer.State(var1, var2, 334.0F, 0.0F);
      KeybindEditorRenderer.State var12 = new KeybindEditorRenderer.State(var1 + 18.0F, var2, 298.0F, 62.0F);
      KeybindEditorRenderer.State var13 = new KeybindEditorRenderer.State(var12.handle(), var12.execute(), var12.compute(), 62.0F);
      KeybindEditorRenderer.State var14 = new KeybindEditorRenderer.State(var12.handle(), var13.execute(), var12.compute(), var8);
      float var15 = var14.process() + (var5 ? 0.0F : 0.0F);
      KeybindEditorRenderer.State var16 = var5
         ? new KeybindEditorRenderer.State(var10.handle(), var15, 334.0F, var4)
         : new KeybindEditorRenderer.State(var10.handle(), var14.process(), 334.0F, 0.0F);
      float var17 = var10.handle() + var10.compute() - 18.0F - 156.0F;
      float var18 = var12.process() + (var12.resolve() - 34.0F) * 0.5F;
      KeybindEditorRenderer.State var19 = new KeybindEditorRenderer.State(var17, var18, 156.0F, 34.0F);
      float var20 = var13.process() + (var13.resolve() - 38.0F) * 0.5F;
      KeybindEditorRenderer.State var21 = new KeybindEditorRenderer.State(var12.handle(), var20, 143.0F, 38.0F);
      KeybindEditorRenderer.State var22 = new KeybindEditorRenderer.State(var21.prepare() + 12.0F, var20, 143.0F, 38.0F);
      float var23 = var12.process() + var12.resolve() * 0.5F + 5.0F;
      float var24 = var13.process() + 27.0F;
      float var25 = var5 ? var14.process() + 27.0F : 0.0F;
      float var26 = var11.process() + 22.0F;
      float var27 = var26 + 20.0F;
      return new KeybindEditorRenderer.DataRecord(var10, var11, var12, var13, var14, var16, var19, var21, var22, var26, var27, var23, var24, var25, var6, var4);
   }

   public static KeybindEditorRenderer.State handle(KeybindEditorRenderer.DataRecord var0, RoundedRectRenderer var1, String var2) {
      Objects.requireNonNull(var0, "layout");
      Objects.requireNonNull(var1, "renderer");
      String var3 = var2 == null ? "" : var2;
      float var4 = 0.0F;
      if (!var3.isEmpty()) {
         var4 = RoundedRectRenderer.handle(FontRegistry.config, var3, 18.0F).instance;
      }

      float var5 = 24.0F;
      float var6 = var4 + var5;
      float var7 = var5;
      float var8 = var0.valueContent().resolve() > 0.0F ? var0.valueContent().handle() : var0.bindBlock().handle();
      float var9 = Math.max(var7, var0.field().prepare() - var8);
      float var10 = Math.min(Math.max(var6, var7), var9);
      float var11 = var0.field().prepare();
      float var12 = var11 - var10;
      return new KeybindEditorRenderer.State(var12, var0.field().process(), var10, var0.field().resolve());
   }

   public static void handle(
      RoundedRectRenderer var0, FontObject var1, KeybindTargetModel var2, KeybindEditorRenderer.DataRecord var3, KeybindEditorRenderer.KeyValueEntry var4
   ) {
      Objects.requireNonNull(var0, "renderer");
      Objects.requireNonNull(var1, "defaultFont");
      Objects.requireNonNull(var2, "model");
      Objects.requireNonNull(var3, "layout");
      Objects.requireNonNull(var4, "state");
      float var5 = handle(var4.alpha());
      if (!(var5 <= 0.001F)) {
         float var6 = handle(var4.blurFactor());
         var0.update(var5);

         try {
            if (var6 > 0.001F) {
               var0.handle(var3.bounds().handle(), var3.bounds().process(), var3.bounds().compute(), var3.bounds().resolve(), 12.0F, var6);
            }

            double var7 = 0.75;
            var0.handle(
               var3.bounds().handle(), var3.bounds().process(), var3.bounds().compute(), var3.bounds().resolve(), 12.0F, ColorCompositor.handle(1447446, var7)
            );
            var0.handle(
               var3.bounds().handle(),
               var3.bounds().process(),
               var3.bounds().compute(),
               var3.bounds().resolve(),
               12.0F,
               ColorCompositor.handle(3355443, 1.0),
               0.5F
            );
            float var9 = var3.bounds().handle() + 18.0F;
            float var10 = Math.max(var4.bindHoverProgress(), var4.bindHovered() ? 1.0F : 0.0F);
            int var11;
            if (var4.listening()) {
               var11 = ColorCompositor.handle(16777215, 0.98);
            } else if (var10 > 0.001F) {
               int var12 = ColorCompositor.handle(8947848, 0.92);
               int var13 = ColorCompositor.handle(16777215, 0.85);
               var11 = ColorCompositor.handle(var12, var13, var10);
            } else {
               var11 = ColorCompositor.handle(8947848, 0.92);
            }

            var0.handle(FontRegistry.config, var9, var3.bindLabelBaseline(), 17.0F, "Bind Key", var11, "l");
            int var21;
            if (var4.listening()) {
               var21 = ColorCompositor.handle(6974057, 1.0);
            } else if (var10 > 0.001F) {
               int var22 = ColorCompositor.handle(5197646, 1.0);
               int var14 = ColorCompositor.handle(6974057, 1.0);
               var21 = ColorCompositor.handle(var22, var14, var10);
            } else {
               var21 = ColorCompositor.handle(5197646, 1.0);
            }

            KeybindEditorRenderer.State var23 = var4.fieldRect();
            var0.handle(var23.handle(), var23.process(), var23.compute(), var23.resolve(), 8.0F, var21, 1.0F);
            float var24 = var23.apply() + 5.0F + 1.0F;
            int var15;
            if (var4.listening()) {
               var15 = ColorCompositor.handle(16777215, 0.98);
            } else if (var10 > 0.001F) {
               int var16 = ColorCompositor.handle(8947848, 0.92);
               int var17 = ColorCompositor.handle(16777215, 0.85);
               var15 = ColorCompositor.handle(var16, var17, var10);
            } else {
               var15 = ColorCompositor.handle(8947848, 0.92);
            }

            var0.handle(FontRegistry.config, var23.update(), var24, 18.0F, var4.keyLabel(), var15, "c");
            if (!var4.statusMessage().isEmpty()) {
               var0.handle(FontRegistry.config, var9, var23.execute() + 8.0F + 18.0F, 15.0F, var4.statusMessage(), ColorCompositor.handle(7105644, 0.9), "l");
            }

            handle(var0, var3.bindBlock().execute(), var3.bounds().handle(), var3.bounds().compute(), var5);
            float var25 = Math.max(var4.toggleHoverProgress(), var4.toggleHovered() ? 1.0F : 0.0F);
            float var26 = Math.max(var4.holdHoverProgress(), var4.holdHovered() ? 1.0F : 0.0F);
            handle(var0, var3.toggleButton(), "Toggle", var4.mode() == KeybindMode.TOGGLE, var25, var5, var4.toggleSelectionProgress());
            handle(var0, var3.holdButton(), "Hold", var4.mode() == KeybindMode.HOLD, var26, var5, var4.holdSelectionProgress());
            if (var3.valueBlock().resolve() > 0.0F) {
               handle(var0, var3, var4);
               handle(var0, var3.modesBlock().execute(), var3.bounds().handle(), var3.bounds().compute(), var5);
            }
         } finally {
            var0.onTick();
         }
      }
   }

   private static void handle(RoundedRectRenderer var0, KeybindEditorRenderer.DataRecord var1, KeybindEditorRenderer.KeyValueEntry var2) {
      if (!(var2.valueBlockHeight() <= 0.001F)) {
         KeybindEditorRenderer.State var3 = var1.valueBlock();
         if (!(var3.resolve() <= 0.001F)) {
            var0.handle(var3.handle(), var3.process(), var3.compute(), var3.resolve(), 10.0F, ColorCompositor.handle(1447446, 0.18));
            float var4 = Math.max(0.0F, var1.valueHeaderHeight());
            if (var4 > 0.001F) {
               var0.handle(var3.handle(), var3.process(), var3.compute(), var4, 10.0F, 10.0F, 0.0F, 0.0F, ColorCompositor.handle(1447446, 0.24));
            }
         }
      }
   }

   private static void handle(RoundedRectRenderer var0, KeybindEditorRenderer.State var1, String var2, boolean var3, float var4, float var5, float var6) {
      double var7 = handle(var5);
      int var9 = ColorCompositor.handle(5197646, 0.9);
      float var10 = handle(var4);
      float var11 = handle(var6);
      double var12 = 0.12 + 0.06 * var10;
      var0.handle(var1.handle(), var1.process(), var1.compute(), var1.resolve(), 6.0F, ColorCompositor.handle(1447446, var12));
      var0.handle(var1.handle(), var1.process(), var1.compute(), var1.resolve(), 6.0F, var9, 1.0F);
      if (var11 > 0.001F) {
         var0.handle(var1.handle(), var1.process(), var1.compute(), var1.resolve(), 6.0F, ColorCompositor.handle(AccentColorToken.handle(), var11));
         var0.handle(var1.handle(), var1.process(), var1.compute(), var1.resolve(), 6.0F, ColorCompositor.handle(AccentColorToken.handle(), var11), 1.0F);
      }

      int var14 = ColorCompositor.handle(8947848, 0.85 * var7);
      int var15 = ColorCompositor.handle(16777215, var7);
      int var16 = ColorCompositor.handle(var14, var15, 0.35F * var10);
      int var17 = ColorCompositor.handle(var16, var15, var11);
      float var18 = var1.apply() + 5.0F;
      var0.handle(FontRegistry.config, var1.update(), var18, 17.0F, var2, var17, "c");
   }

   private static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4) {
      float var5 = 100.0F;
      float var6 = Math.round(var1 * var5) / var5;
      var0.handle(var2 + 18.0F, var6, var3 - 36.0F, 1.0F / var5, ColorCompositor.handle(16777215, 0.05 * var4));
   }

   static float handle(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         return var0 >= 1.0F ? 1.0F : var0;
      }
   }

   public record DataRecord(
      KeybindEditorRenderer.State bounds,
      KeybindEditorRenderer.State header,
      KeybindEditorRenderer.State bindBlock,
      KeybindEditorRenderer.State modesBlock,
      KeybindEditorRenderer.State valueBlock,
      KeybindEditorRenderer.State valueContent,
      KeybindEditorRenderer.State field,
      KeybindEditorRenderer.State toggleButton,
      KeybindEditorRenderer.State holdButton,
      float titleBaseline,
      float subtitleBaseline,
      float bindLabelBaseline,
      float modeLabelBaseline,
      float valueLabelBaseline,
      float valueHeaderHeight,
      float valueContentHeight
   ) {
   }

   public record KeyValueEntry(
      float alpha,
      float blurFactor,
      boolean listening,
      boolean bindHovered,
      boolean toggleHovered,
      boolean holdHovered,
      float bindHoverProgress,
      float toggleHoverProgress,
      float holdHoverProgress,
      float toggleSelectionProgress,
      float holdSelectionProgress,
      KeybindMode mode,
      String keyLabel,
      String statusMessage,
      float valueBlockHeight,
      float valueLabelBaseline,
      KeybindEditorRenderer.State fieldRect
   ) {
      public KeyValueEntry(
         float alpha,
         float blurFactor,
         boolean listening,
         boolean bindHovered,
         boolean toggleHovered,
         boolean holdHovered,
         float bindHoverProgress,
         float toggleHoverProgress,
         float holdHoverProgress,
         float toggleSelectionProgress,
         float holdSelectionProgress,
         KeybindMode mode,
         String keyLabel,
         String statusMessage,
         float valueBlockHeight,
         float valueLabelBaseline,
         KeybindEditorRenderer.State fieldRect
      ) {
         Objects.requireNonNull(mode, "mode");
         keyLabel = keyLabel == null ? "" : keyLabel;
         statusMessage = statusMessage == null ? "" : statusMessage;
         fieldRect = Objects.requireNonNull(fieldRect, "fieldRect");
         bindHoverProgress = KeybindEditorRenderer.handle(bindHoverProgress);
         toggleHoverProgress = KeybindEditorRenderer.handle(toggleHoverProgress);
         holdHoverProgress = KeybindEditorRenderer.handle(holdHoverProgress);
         toggleSelectionProgress = KeybindEditorRenderer.handle(toggleSelectionProgress);
         holdSelectionProgress = KeybindEditorRenderer.handle(holdSelectionProgress);
         valueBlockHeight = Math.max(0.0F, valueBlockHeight);
         if (valueBlockHeight <= 0.0F) {
            valueLabelBaseline = 0.0F;
         }

         this.alpha = alpha;
         this.blurFactor = blurFactor;
         this.listening = listening;
         this.bindHovered = bindHovered;
         this.toggleHovered = toggleHovered;
         this.holdHovered = holdHovered;
         this.bindHoverProgress = bindHoverProgress;
         this.toggleHoverProgress = toggleHoverProgress;
         this.holdHoverProgress = holdHoverProgress;
         this.toggleSelectionProgress = toggleSelectionProgress;
         this.holdSelectionProgress = holdSelectionProgress;
         this.mode = mode;
         this.keyLabel = keyLabel;
         this.statusMessage = statusMessage;
         this.valueBlockHeight = valueBlockHeight;
         this.valueLabelBaseline = valueLabelBaseline;
         this.fieldRect = fieldRect;
      }
   }

   public static final class State {
      private final float instance;
      private final float data;
      private final float context;
      private final float config;

      public State(float var1, float var2, float var3, float var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }

      public float handle() {
         return this.instance;
      }

      public float process() {
         return this.data;
      }

      public float compute() {
         return this.context;
      }

      public float resolve() {
         return this.config;
      }

      public float update() {
         return this.instance + this.context * 0.5F;
      }

      public float apply() {
         return this.data + this.config * 0.5F;
      }

      public float execute() {
         return this.data + this.config;
      }

      public float prepare() {
         return this.instance + this.context;
      }

      public boolean handle(double var1, double var3) {
         return var1 >= this.instance && var1 <= this.instance + this.context && var3 >= this.data && var3 <= this.data + this.config;
      }
   }
}
