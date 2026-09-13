package ru.wild.gui.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.theme.FoundryPresetLibrary;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeParser;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.texture.AssetCategory;
import ru.wild.render.texture.StudioTextureLoader;
import ru.wild.util.io.AvatarFilePicker;
import ru.wild.util.render.RoundedRectRenderer;

public final class FoundryAssetBrowser {
   private GuiMetrics instance;
   private float data;
   private float context;
   private float config;
   private float state;
   private AssetCategory cache = AssetCategory.MODELS;
   private float output;
   private float current;
   private float active = 200.0F;
   private float mode = -8.0F;
   private float selection = 1.0F;
   private boolean enabled;
   private float renderer;
   private float handler;
   private String animationDraw = "";
   private boolean pointEncode;
   private boolean animator;
   private final ArrayList<Long> source = new ArrayList<>();
   private final ArrayList<FoundryAssetBrowser.DataRecord> target = new ArrayList<>();
   private static final float pending = 170.0F;
   private boolean previous;
   private String latest = "";
   private boolean summary;
   private String matrixBlend = "";
   private boolean vectorMatch;
   private long itemProject;
   private String responseCompute = "";
   private float providerFetch;
   private float profileDraw = 1.0F;
   private int vectorPerform = 1;
   private float eventAttach;
   private float serverRead;
   private boolean positionAdvance;
   private long frameCheck;
   private long moduleCollect;
   private final HashMap<String, Long> providerClose = new HashMap<>();
   private static final String[] presetSave = new String[]{"chip0", "chip1", "chip2", "chip3"};
   private String windowConvert = "";
   private long presetWrite;

   public boolean handle(ModernClickGuiState var1) {
      return var1 != null && var1.renderScale();
   }

   public boolean process(ModernClickGuiState var1) {
      return var1 != null && var1.renderScale();
   }

   public boolean handle() {
      return this.pointEncode || this.previous || this.summary;
   }

   public void process() {
      this.enabled = false;
      this.pointEncode = false;
      this.animator = false;
      this.previous = false;
      this.summary = false;
      this.vectorMatch = false;
   }

   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, float var4, float var5, float var6, float var7) {
      if (var1 != null && var2 != null && var3 != null && !(var6 <= 0.0F) && !(var7 <= 0.0F)) {
         GuiMetrics var8 = var3.update();
         ThemeColors var9 = var3.apply();
         this.instance = var8;
         this.data = var4;
         this.context = var5;
         this.config = var6;
         this.state = var7;
         this.output = this.output + (this.current - this.output) * 0.32F;
         this.providerFetch = this.providerFetch + ((this.responseCompute.isEmpty() ? 0.0F : 1.0F) - this.providerFetch) * 0.3F;
         this.profileDraw = this.profileDraw + (1.0F - this.profileDraw) * 0.18F;
         if (this.profileDraw > 0.999F) {
            this.profileDraw = 1.0F;
         }

         long var10 = System.currentTimeMillis();
         if (var10 - this.moduleCollect > 240L) {
            this.frameCheck = var10;
         }

         this.moduleCollect = var10;
         float var12 = Math.min(1.0F, (float)(var10 - this.frameCheck) / 360.0F);
         float var13 = 1.0F - (1.0F - var12) * (1.0F - var12) * (1.0F - var12);
         FoundryAssetBrowser.PrimaryDataRecord var14 = new FoundryAssetBrowser.PrimaryDataRecord(var4, var5, var6, var7);
         boolean var15 = var13 < 0.999F;
         if (var15) {
            var1.update(Math.max(0.0F, var13));
         }

         try {
            this.handle(var1, var2, var8, var9, var14, 1.0F);
            this.process(var1, var2, var8, var9, var14, 1.0F);
            this.handle(var1, var2, var3, var8, var9, var14, 1.0F);
            this.process(var1, var2, var3, var8, var9, var14, 1.0F);
         } finally {
            if (var15) {
               var1.onTick();
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, FoundryAssetBrowser.PrimaryDataRecord var5, float var6
   ) {
      float var7 = var3.handle(18.0F);
      float var8 = var3.handle(44.0F);
      float var9 = var3.handle(18.0F);
      ModuleStateHelper.handle(
         var1, var3, FontRegistry.current, var5.x + var7, var5.y, var8, 13.0F, "a", ThemeColors.handle(var4.save(), Math.round(255.0F * var6))
      );
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.config,
         var5.x + var7 + var9,
         var5.y,
         var8,
         15.0F,
         "Studio",
         ThemeColors.handle(ModuleStateHelper.handle(var4), Math.round(255.0F * var6))
      );
      FoundryAssetBrowser.PrimaryDataRecord var10 = this.handle(var3, var5);
      boolean var11 = ModuleStateHelper.handle(var2, var10.x, var10.y, var10.w, var10.h);
      var1.handle(
         var10.x, var10.y, var10.w, var10.h, var10.h * 0.5F, ThemeColors.handle(this.pointEncode ? var4.select() : var4.check(), Math.round(255.0F * var6))
      );
      if (this.pointEncode || var11) {
         var1.handle(
            var10.x, var10.y, var10.w, var10.h, var10.h * 0.5F, ThemeColors.handle(var4.save(), Math.round((this.pointEncode ? 150 : 80) * var6)), 0.7F
         );
      }

      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.state,
         var10.x + var3.handle(10.0F),
         var10.y,
         var10.h,
         10.0F,
         "m",
         ThemeColors.handle(ModuleStateHelper.process(var4), Math.round(200.0F * var6))
      );
      float var12 = this.pointEncode ? (float)((Math.sin(System.currentTimeMillis() * 0.006) + 1.0) * 0.5) : 0.0F;
      long var13 = System.currentTimeMillis();
      var1.handle((int)(var10.x + var3.handle(26.0F)), (int)var10.y, (int)(var10.w - var3.handle(34.0F)), (int)var10.h);
      if (this.animationDraw.isEmpty() && !this.pointEncode) {
         ModuleStateHelper.handle(
            var1,
            var3,
            FontRegistry.instance,
            var10.x + var3.handle(26.0F),
            var10.y,
            var10.h,
            10.0F,
            "Поиск...",
            ThemeColors.handle(ModuleStateHelper.process(var4), Math.round(255.0F * var6))
         );
      } else {
         if (this.animator && !this.animationDraw.isEmpty()) {
            float var15 = ModuleStateHelper.handle(FontRegistry.instance, this.animationDraw, 10.0F);
            var1.handle(
               var10.x + var3.handle(24.0F),
               var10.y + (var10.h - var3.handle(16.0F)) * 0.5F,
               var15 + var3.handle(5.0F),
               var3.handle(16.0F),
               var3.handle(3.0F),
               ThemeColors.handle(var4.save(), Math.round(70.0F * var6))
            );
         }

         float var25 = var10.x + var3.handle(26.0F);

         for (int var16 = 0; var16 < this.animationDraw.length(); var16++) {
            String var17 = String.valueOf(this.animationDraw.charAt(var16));
            float var18 = ModuleStateHelper.handle(FontRegistry.instance, var17, 10.0F);
            long var19 = var16 < this.source.size() ? this.source.get(var16) : 0L;
            float var21 = (float)(var13 - var19) / 170.0F;
            float var22 = 0.0F;
            float var23 = 1.0F;
            if (var21 < 1.0F) {
               float var24 = 1.0F - (1.0F - var21) * (1.0F - var21);
               var22 = (1.0F - var24) * var3.handle(6.0F);
               var23 = var24;
            }

            ModuleStateHelper.handle(
               var1,
               var3,
               FontRegistry.instance,
               var25,
               var10.y + var22,
               var10.h,
               10.0F,
               var17,
               ThemeColors.handle(ModuleStateHelper.handle(var4), Math.round(255.0F * var23 * var6))
            );
            var25 += var18;
         }

         if (this.pointEncode && !this.animator) {
            ModuleStateHelper.handle(
               var1, var3, FontRegistry.instance, var25, var10.y, var10.h, 10.0F, "|", ThemeColors.handle(var4.save(), Math.round(255.0F * var12 * var6))
            );
         }
      }

      for (int var26 = this.target.size() - 1; var26 >= 0; var26--) {
         FoundryAssetBrowser.DataRecord var28 = this.target.get(var26);
         float var29 = (float)(var13 - var28.born()) / 170.0F;
         if (var29 >= 1.0F) {
            this.target.remove(var26);
         } else {
            float var30 = 1.0F - (1.0F - var29) * (1.0F - var29);
            ModuleStateHelper.handle(
               var1,
               var3,
               FontRegistry.instance,
               var28.x(),
               var10.y + var30 * var3.handle(7.0F),
               var10.h,
               10.0F,
               var28.ch(),
               ThemeColors.handle(ModuleStateHelper.handle(var4), Math.round(255.0F * (1.0F - var30) * var6))
            );
         }
      }

      var1.apply();
      if (!this.animationDraw.isEmpty()) {
         boolean var27 = ModuleStateHelper.handle(var2, var10.x + var10.w - var3.handle(28.0F), var10.y, var3.handle(28.0F), var10.h);
         ModuleStateHelper.handle(
            var1,
            var3,
            FontRegistry.state,
            var10.x + var10.w - var3.handle(20.0F),
            var10.y,
            var10.h,
            9.0F,
            "l",
            ThemeColors.handle(var27 ? var4.save() : ModuleStateHelper.process(var4), Math.round(220.0F * var6))
         );
      }
   }
   private void process(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, FoundryAssetBrowser.PrimaryDataRecord var5, float var6
   ) {
      AssetCategory[] var7 = AssetCategory.values();
      float var8 = var5.y + var3.handle(44.0F);
      float var9 = var3.handle(34.0F);
      float var10 = var3.handle(18.0F);
      float var11 = var3.handle(26.0F);
      float var12 = var8 + (var9 - var11) * 0.5F;
      float var13 = var5.x + var10;
      float var14 = var13;
      float var15 = var3.handle(40.0F);

      for (AssetCategory var19 : var7) {
         float var20 = ModuleStateHelper.handle(FontRegistry.config, var19.process(), 11.0F) + var3.handle(20.0F);
         if (var19 == this.cache) {
            var14 = var13;
            var15 = var20;
         }

         var13 += var20 + var3.handle(6.0F);
      }

      if (!this.positionAdvance) {
         this.eventAttach = var14;
         this.serverRead = var15;
         this.positionAdvance = true;
      } else {
         this.eventAttach = this.eventAttach + (var14 - this.eventAttach) * 0.3F;
         this.serverRead = this.serverRead + (var15 - this.serverRead) * 0.3F;
      }

      var1.handle(this.eventAttach, var12, this.serverRead, var11, var11 * 0.5F, ThemeColors.handle(var4.save(), Math.round((var4.unload() ? 60 : 86) * var6)));
      var13 = var5.x + var10;

      for (AssetCategory var47 : var7) {
         float var49 = ModuleStateHelper.handle(FontRegistry.config, var47.process(), 11.0F) + var3.handle(20.0F);
         boolean var21 = var47 == this.cache;
         boolean var22 = ModuleStateHelper.handle(var2, var13, var12, var49, var11);
         boolean var23 = this.handle(var1, var47.name(), var13 + var49 * 0.5F, var12 + var11 * 0.5F);

         try {
            if (!var21 && var22) {
               var1.handle(var13, var12, var49, var11, var11 * 0.5F, ThemeColors.handle(var4.select(), Math.round(255.0F * var6)));
            }

            ModuleStateHelper.handle(
               var1,
               var3,
               FontRegistry.config,
               var13 + var3.handle(11.0F),
               var12,
               var11,
               11.0F,
               var47.process(),
               ThemeColors.handle(var21 ? ModuleStateHelper.handle(var4) : ModuleStateHelper.process(var4), Math.round(255.0F * var6))
            );
         } finally {
            this.handle(var1, var23);
         }

         var13 += var49 + var3.handle(6.0F);
      }

      FoundryAssetBrowser.PrimaryDataRecord var42 = this.process(var3, var5);
      FoundryAssetBrowser.PrimaryDataRecord var44 = this.compute(var3, var5);
      boolean var46 = ModuleStateHelper.handle(var2, var44.x, var44.y, var44.w, var44.h);
      boolean var48 = this.handle(var1, "import", var44.x + var44.w * 0.5F, var44.y + var44.h * 0.5F);
      boolean var36 = false /* VF: Semaphore variable */;

      try {
         var36 = true;
         var1.handle(
            var44.x, var44.y, var44.w, var44.h, var44.h * 0.5F, ThemeColors.handle(var46 ? var4.save() : var4.select(), Math.round((var46 ? 70 : 255) * var6))
         );
         var1.handle(var44.x, var44.y, var44.w, var44.h, var44.h * 0.5F, ThemeColors.handle(var4.save(), Math.round(110.0F * var6)), 0.7F);
         String var50 = "Импорт";
         float var52 = ModuleStateHelper.handle(FontRegistry.config, var50, 10.0F);
         ModuleStateHelper.handle(
            var1,
            var3,
            FontRegistry.config,
            var44.x + (var44.w - var52) * 0.5F,
            var44.y,
            var44.h,
            10.0F,
            var50,
            ThemeColors.handle(ModuleStateHelper.handle(var4), Math.round(255.0F * var6))
         );
         var36 = false;
      } finally {
         if (var36) {
            this.handle(var1, var48);
         }
      }

      this.handle(var1, var48);
      boolean var51 = ModuleStateHelper.handle(var2, var42.x, var42.y, var42.w, var42.h);
      boolean var53 = this.handle(var1, "reload", var42.x + var42.w * 0.5F, var42.y + var42.h * 0.5F);

      try {
         var1.handle(var42.x, var42.y, var42.w, var42.h, var42.h * 0.5F, ThemeColors.handle(var51 ? var4.select() : var4.check(), Math.round(255.0F * var6)));
         float var54 = ModuleStateHelper.handle(FontRegistry.state, "r", 10.0F);
         ModuleStateHelper.handle(
            var1,
            var3,
            FontRegistry.state,
            var42.x + (var42.w - var54) * 0.5F,
            var42.y,
            var42.h,
            10.0F,
            "r",
            ThemeColors.handle(var51 ? var4.save() : ModuleStateHelper.process(var4), Math.round(255.0F * var6))
         );
      } finally {
         this.handle(var1, var53);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      ThemeRenderContext var3,
      GuiMetrics var4,
      ThemeColors var5,
      FoundryAssetBrowser.PrimaryDataRecord var6,
      float var7
   ) {
      FoundryAssetBrowser.PrimaryDataRecord var8 = this.apply(var4, var6);
      this.handle(var1, var4, var5, var8.x, var8.y, var8.w, var8.h, var4.handle(10.0F), var7);
      List var9 = this.prepare();
      int var10 = this.render(var4, var8);
      float var11 = var4.handle(10.0F);
      float var12 = (var8.w - var4.handle(12.0F) - (var10 - 1) * var11) / var10;
      float var13 = var12;
      float var14 = var13 + var4.handle(20.0F);
      float var15 = var4.handle(10.0F);
      int var16 = (var9.size() + var10 - 1) / var10;
      float var17 = var16 * (var14 + var15) + var4.handle(6.0F);
      float var18 = Math.max(0.0F, var17 - var8.h);
      this.current = handle(this.current, 0.0F, var18);
      this.output = handle(this.output, 0.0F, var18);
      ThemeParser var19 = FoundryPresetLibrary.handle().apply();
      var1.handle(var8.x, var8.y, var8.w, var8.h, var4.handle(10.0F), var4.handle(10.0F), var4.handle(10.0F), var4.handle(10.0F));

      try {
         boolean var20 = this.profileDraw < 0.999F;
         if (var20) {
            var1.update(Math.max(0.0F, this.profileDraw));
            var1.handle((1.0F - this.profileDraw) * this.vectorPerform * var8.w * 0.16F, 0.0F);
         }

         try {
            float var21 = var8.x + var4.handle(6.0F);
            float var22 = var8.y + var4.handle(6.0F) - this.output;
            String var23 = "";

            for (int var24 = 0; var24 < var9.size(); var24++) {
               int var25 = var24 % var10;
               int var26 = var24 / var10;
               float var27 = var21 + var25 * (var12 + var11);
               float var28 = var22 + var26 * (var14 + var15);
               if (!(var28 + var14 < var8.y) && !(var28 > var8.y + var8.h)) {
                  ThemeParser var29 = (ThemeParser)var9.get(var24);
                  if (ModuleStateHelper.handle(var2, var27, var28, var12, var14)) {
                     var23 = var29.handle();
                  }

                  float var30 = var29.handle().equals(this.responseCompute) ? this.providerFetch : 0.0F;
                  float var31 = 1.0F;
                  float var32 = var28;
                  long var33 = System.currentTimeMillis() - this.frameCheck - var24 * 26L;
                  if (var33 < 240L) {
                     float var35 = Math.max(0.0F, (float)var33) / 240.0F;
                     float var36 = 1.0F - (1.0F - var35) * (1.0F - var35);
                     var31 = var36;
                     var32 = var28 + (1.0F - var36) * var4.handle(14.0F);
                  }

                  boolean var54 = this.handle(var1, var29.handle(), var27 + var12 * 0.5F, var32 + var14 * 0.5F);

                  try {
                     this.handle(var1, var2, var4, var5, var29, var27, var32, var12, var14, var13, var19, var7 * var31, var30);
                  } finally {
                     this.handle(var1, var54);
                  }
               }
            }

            this.responseCompute = var23;
            if (var9.isEmpty()) {
               String var52 = this.animationDraw.isEmpty() ? "Пусто. Нажмите «Импорт»" : "Ничего не найдено";
               float var53 = ModuleStateHelper.handle(FontRegistry.instance, var52, 10.0F);
               ModuleStateHelper.handle(
                  var1,
                  var4,
                  FontRegistry.instance,
                  var8.x + (var8.w - var53) * 0.5F,
                  var8.y + var8.h * 0.42F,
                  var4.handle(14.0F),
                  10.0F,
                  var52,
                  ThemeColors.handle(ModuleStateHelper.process(var5), Math.round(190.0F * var7))
               );
            }
         } finally {
            if (var20) {
               var1.prepare();
               var1.onTick();
            }
         }
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      ThemeParser var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      ThemeParser var11,
      float var12,
      float var13
   ) {
      boolean var14 = var11 != null && var11.handle().equals(var5.handle());
      boolean var15 = var14 && FoundryPresetLibrary.handle().execute();
      float var16 = var3.handle(10.0F);
      if (!var14 && var13 > 0.01F) {
         var1.handle(
            var6, var7, var8, var9, var16, var3.handle(12.0F) * var13, var3.handle(1.0F), ThemeColors.handle(var4.save(), Math.round(46.0F * var13 * var12))
         );
      }

      int var17 = var14
         ? ThemeColors.handle(var4.save(), Math.round((var4.unload() ? 40 : 54) * var12))
         : ThemeColors.handle(ThemeColors.handle(var4.check(), var4.refresh(), var13), Math.round(255.0F * var12));
      var1.handle(var6, var7, var8, var9, var16, var17);
      if (var14) {
         var1.handle(var6, var7, var8, var9, var16, var3.handle(14.0F), var3.handle(1.0F), ThemeColors.handle(var4.save(), Math.round(60.0F * var12)));
         var1.handle(var6, var7, var8, var9, var16, ThemeColors.handle(var4.save(), Math.round(180.0F * var12)), 0.9F);
      } else if (var13 > 0.01F) {
         var1.handle(var6, var7, var8, var9, var16, ThemeColors.handle(var4.save(), Math.round(80.0F * var13 * var12)), 0.7F);
      }

      var1.handle(
         var6 + var3.handle(4.0F), var7 + var3.handle(4.0F), var8 - var3.handle(8.0F), var10 - var3.handle(2.0F), var16 * 0.7F, var16 * 0.7F, 0.0F, 0.0F
      );

      try {
         var1.handle(
            var6 + var3.handle(4.0F),
            var7 + var3.handle(4.0F),
            var8 - var3.handle(8.0F),
            var10 - var3.handle(2.0F),
            0.0F,
            ThemeColors.handle(10, 12, 18, Math.round(230.0F * var12))
         );
         ModelSelectScreen.handle(
            var1, var5.render(), var5.handle(), var6 + var3.handle(4.0F), var7 + var3.handle(4.0F), var8 - var3.handle(8.0F), var10 - var3.handle(2.0F), var12
         );
      } finally {
         var1.compute();
         var1.apply();
      }

      String var18 = var5.check();
      if (var18 != null && !var18.isEmpty()) {
         String var19 = handle(var18, 10);
         float var20 = ModuleStateHelper.handle(FontRegistry.instance, var19, 8.0F) + var3.handle(8.0F);
         var1.handle(
            var6 + var3.handle(6.0F),
            var7 + var3.handle(6.0F),
            var20,
            var3.handle(13.0F),
            var3.handle(6.0F),
            ThemeColors.handle(var4.save(), Math.round(210.0F * var12))
         );
         ModuleStateHelper.handle(
            var1,
            var3,
            FontRegistry.instance,
            var6 + var3.handle(10.0F),
            var7 + var3.handle(6.0F),
            var3.handle(13.0F),
            8.0F,
            var19,
            ThemeColors.handle(-1, Math.round(255.0F * var12))
         );
      }

      float var23 = var7 + var10;
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.instance,
         var6 + var3.handle(8.0F),
         var23,
         var3.handle(20.0F),
         9.0F,
         handle(var5.resolve(), 16),
         ThemeColors.handle(ModuleStateHelper.handle(var4), Math.round(255.0F * var12))
      );
      FoundryAssetBrowser.PrimaryDataRecord var24 = this.handle(var3, var6, var7, var8, var10);
      this.handle(var1, var3, var4, var24, var15, var12);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      var1.handle(var4, var5, var6, var7, var8, var2.handle(9.0F), var2.handle(1.4F), ThemeColors.handle(0, 0, 0, Math.round(46.0F * var9)));
      var1.handle(var4, var5, var6, var7, var8, ThemeColors.handle(var3.check(), Math.round(255.0F * var9)));
      var1.handle(var4, var5, var6, var7, var8, ThemeColors.handle(var3.save(), Math.round(48.0F * var9)), 0.8F);
      float var10 = Math.max(0.0F, (var6 - var8 * 2.0F) * 0.5F);
      int var11 = ThemeColors.handle(ThemeColors.handle(-1, var3.save(), 0.35F), Math.round(48.0F * var9));
      int var12 = ThemeColors.handle(var11, Math.round(8.0F * var9));
      var1.handle(var4 + var8, var5 + var2.handle(1.0F), var10, Math.max(1.0F, var2.handle(1.0F)), 0.0F, var12, var11);
      var1.handle(var4 + var8 + var10, var5 + var2.handle(1.0F), var10, Math.max(1.0F, var2.handle(1.0F)), 0.0F, var11, var12);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, FoundryAssetBrowser.PrimaryDataRecord var4, boolean var5, float var6) {
      var1.handle(
         var4.x, var4.y, var4.w, var4.h, var4.h * 0.5F, ThemeColors.handle(var5 ? var3.save() : var3.encodePoint(), Math.round((var5 ? 220 : 255) * var6))
      );
      float var7 = var4.h - var2.handle(3.0F);
      float var8 = var5 ? var4.x + var4.w - var7 - var2.handle(1.5F) : var4.x + var2.handle(1.5F);
      var1.process(var8 + var7 * 0.5F, var4.y + var4.h * 0.5F, var7 * 0.5F, 0.0F, 1.0F, ThemeColors.handle(-1, Math.round(255.0F * var6)));
   }

   private void process(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      ThemeRenderContext var3,
      GuiMetrics var4,
      ThemeColors var5,
      FoundryAssetBrowser.PrimaryDataRecord var6,
      float var7
   ) {
      FoundryAssetBrowser.PrimaryDataRecord var8 = this.update(var4, var6);
      this.handle(var1, var4, var5, var8.x, var8.y, var8.w, var8.h, var4.handle(12.0F), var7);
      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.config,
         var8.x + var4.handle(14.0F),
         var8.y,
         var4.handle(34.0F),
         11.0F,
         "Превью",
         ThemeColors.handle(ModuleStateHelper.handle(var5), Math.round(235.0F * var7))
      );
      ThemeParser var9 = FoundryPresetLibrary.handle().apply();
      FoundryAssetBrowser.PrimaryDataRecord var10 = this.execute(var4, var6);
      ModelSelectScreen.handle(var1, var3, var10.x, var10.y, var10.w, var10.h, var9, this.active, this.mode, this.selection, var7);
      FoundryAssetBrowser.PrimaryDataRecord var11 = this.prepare(var4, var6);
      if (this.previous && var9 != null) {
         FoundryAssetBrowser.PrimaryDataRecord var42 = this.select(var4, var6);
         var1.handle(var42.x, var42.y, var42.w, var42.h, var4.handle(5.0F), ThemeColors.handle(var5.select(), Math.round(255.0F * var7)));
         var1.handle(var42.x, var42.y, var42.w, var42.h, var4.handle(5.0F), ThemeColors.handle(var5.save(), Math.round(170.0F * var7)), 0.8F);
         float var44 = (float)((Math.sin(System.currentTimeMillis() * 0.006) + 1.0) * 0.5);
         float var14 = ModuleStateHelper.handle(FontRegistry.config, this.latest, 11.0F);
         var1.handle((int)(var42.x + var4.handle(7.0F)), (int)var42.y, (int)(var42.w - var4.handle(12.0F)), (int)var42.h);
         ModuleStateHelper.handle(
            var1,
            var4,
            FontRegistry.config,
            var42.x + var4.handle(7.0F),
            var42.y,
            var42.h,
            11.0F,
            this.latest,
            ThemeColors.handle(ModuleStateHelper.handle(var5), Math.round(255.0F * var7))
         );
         ModuleStateHelper.handle(
            var1,
            var4,
            FontRegistry.config,
            var42.x + var4.handle(7.0F) + var14,
            var42.y,
            var42.h,
            11.0F,
            "|",
            ThemeColors.handle(var5.save(), Math.round(255.0F * var44 * var7))
         );
         var1.apply();
      } else {
         String var12 = var9 == null ? "Ничего не выбрано" : var9.resolve();
         boolean var13 = var9 != null && ModuleStateHelper.handle(var2, var11.x, var11.y, var11.w * 0.7F, var4.handle(16.0F));
         ModuleStateHelper.handle(
            var1,
            var4,
            FontRegistry.config,
            var11.x,
            var11.y,
            var4.handle(18.0F),
            12.0F,
            handle(var12, 22),
            ThemeColors.handle(var13 ? var5.save() : ModuleStateHelper.handle(var5), Math.round(255.0F * var7))
         );
      }

      String var43 = this.resolve(var9);
      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.instance,
         var11.x,
         var11.y + var4.handle(17.0F),
         var4.handle(15.0F),
         9.0F,
         handle(var43, 40),
         ThemeColors.handle(ModuleStateHelper.process(var5), Math.round(200.0F * var7))
      );
      if (var9 != null) {
         AssetCategory[] var45 = AssetCategory.values();
         float var46 = var4.handle(20.0F);
         float var15 = var11.y + var4.handle(34.0F);
         float var16 = var11.x;

         for (AssetCategory var20 : var45) {
            String var21 = var20.process();
            float var22 = ModuleStateHelper.handle(FontRegistry.instance, var21, 9.0F) + var4.handle(12.0F);
            boolean var23 = var9.prepare() == var20;
            boolean var24 = this.handle(var1, presetSave[var20.ordinal()], var16 + var22 * 0.5F, var15 + var46 * 0.5F);

            try {
               var1.handle(
                  var16, var15, var22, var46, var46 * 0.5F, ThemeColors.handle(var23 ? var5.save() : var5.check(), Math.round((var23 ? 70 : 255) * var7))
               );
               ModuleStateHelper.handle(
                  var1,
                  var4,
                  FontRegistry.instance,
                  var16 + var4.handle(6.0F),
                  var15,
                  var46,
                  9.0F,
                  var21,
                  ThemeColors.handle(var23 ? ModuleStateHelper.handle(var5) : ModuleStateHelper.process(var5), Math.round(255.0F * var7))
               );
            } finally {
               this.handle(var1, var24);
            }

            var16 += var22 + var4.handle(4.0F);
         }

         FoundryAssetBrowser.PrimaryDataRecord var47 = this.refresh(var4, var6);
         if (this.summary) {
            var1.handle(var47.x, var47.y, var47.w, var47.h, var4.handle(5.0F), ThemeColors.handle(var5.select(), Math.round(255.0F * var7)));
            var1.handle(var47.x, var47.y, var47.w, var47.h, var4.handle(5.0F), ThemeColors.handle(var5.save(), Math.round(170.0F * var7)), 0.8F);
            float var49 = (float)((Math.sin(System.currentTimeMillis() * 0.006) + 1.0) * 0.5);
            float var52 = ModuleStateHelper.handle(FontRegistry.instance, this.matrixBlend, 9.0F);
            var1.handle((int)(var47.x + var4.handle(7.0F)), (int)var47.y, (int)(var47.w - var4.handle(12.0F)), (int)var47.h);
            ModuleStateHelper.handle(
               var1,
               var4,
               FontRegistry.instance,
               var47.x + var4.handle(7.0F),
               var47.y,
               var47.h,
               9.0F,
               this.matrixBlend,
               ThemeColors.handle(ModuleStateHelper.handle(var5), Math.round(255.0F * var7))
            );
            ModuleStateHelper.handle(
               var1,
               var4,
               FontRegistry.instance,
               var47.x + var4.handle(7.0F) + var52,
               var47.y,
               var47.h,
               9.0F,
               "|",
               ThemeColors.handle(var5.save(), Math.round(255.0F * var49 * var7))
            );
            var1.apply();
         } else {
            boolean var48 = ModuleStateHelper.handle(var2, var47.x, var47.y, var47.w, var47.h);
            var1.handle(
               var47.x, var47.y, var47.w, var47.h, var4.handle(5.0F), ThemeColors.handle(var48 ? var5.select() : var5.check(), Math.round(255.0F * var7))
            );
            String var51 = var9.check();
            if (var51 != null && !var51.isEmpty()) {
               float var54 = ModuleStateHelper.handle(FontRegistry.instance, "Префикс: ", 9.0F);
               ModuleStateHelper.handle(
                  var1,
                  var4,
                  FontRegistry.instance,
                  var47.x + var4.handle(7.0F),
                  var47.y,
                  var47.h,
                  9.0F,
                  "Префикс: ",
                  ThemeColors.handle(ModuleStateHelper.process(var5), Math.round(200.0F * var7))
               );
               ModuleStateHelper.handle(
                  var1,
                  var4,
                  FontRegistry.instance,
                  var47.x + var4.handle(7.0F) + var54,
                  var47.y,
                  var47.h,
                  9.0F,
                  handle(var51, 18),
                  ThemeColors.handle(var5.save(), Math.round(255.0F * var7))
               );
            } else {
               ModuleStateHelper.handle(
                  var1,
                  var4,
                  FontRegistry.instance,
                  var47.x + var4.handle(7.0F),
                  var47.y,
                  var47.h,
                  9.0F,
                  "+ префикс",
                  ThemeColors.handle(ModuleStateHelper.process(var5), Math.round(180.0F * var7))
               );
            }
         }

         FoundryAssetBrowser.PrimaryDataRecord var50 = this.check(var4, var6);
         boolean var53 = FoundryPresetLibrary.handle().execute();
         boolean var55 = ModuleStateHelper.handle(var2, var50.x, var50.y, var50.w, var50.h);
         boolean var56 = this.handle(var1, "equip", var50.x + var50.w * 0.5F, var50.y + var50.h * 0.5F);

         try {
            int var57 = var53
               ? ThemeColors.handle(var5.save(), Math.round((var55 ? 200 : 160) * var7))
               : ThemeColors.handle(var55 ? var5.select() : var5.check(), Math.round(255.0F * var7));
            var1.handle(var50.x, var50.y, var50.w, var50.h, var4.handle(8.0F), var57);
            var1.handle(var50.x, var50.y, var50.w, var50.h, var4.handle(8.0F), ThemeColors.handle(var5.save(), Math.round(140.0F * var7)), 0.7F);
            String var59 = var53 ? "Снять" : "Надеть";
            float var61 = ModuleStateHelper.handle(FontRegistry.config, var59, 11.0F);
            ModuleStateHelper.handle(
               var1,
               var4,
               FontRegistry.config,
               var50.x + (var50.w - var61) * 0.5F,
               var50.y,
               var50.h,
               11.0F,
               var59,
               ThemeColors.handle(var53 ? ModuleStateHelper.handle(var5) : ModuleStateHelper.process(var5), Math.round(255.0F * var7))
            );
         } finally {
            this.handle(var1, var56);
         }

         FoundryAssetBrowser.PrimaryDataRecord var58 = this.onTick(var4, var6);
         boolean var60 = ModuleStateHelper.handle(var2, var58.x, var58.y, var58.w, var58.h);
         boolean var62 = this.vectorMatch && System.currentTimeMillis() - this.itemProject < 2600L;
         boolean var25 = this.handle(var1, "delete", var58.x + var58.w * 0.5F, var58.y + var58.h * 0.5F);

         try {
            int var26 = var62
               ? ThemeColors.handle(196, 64, 64, Math.round(235.0F * var7))
               : ThemeColors.handle(var60 ? var5.refresh() : var5.check(), Math.round(255.0F * var7));
            var1.handle(var58.x, var58.y, var58.w, var58.h, var4.handle(8.0F), var26);
            var1.handle(
               var58.x,
               var58.y,
               var58.w,
               var58.h,
               var4.handle(8.0F),
               var62 ? ThemeColors.handle(255, 120, 120, Math.round(220.0F * var7)) : ThemeColors.handle(var5.tick(), Math.round(190.0F * var7)),
               0.7F
            );
            String var27 = var62 ? "Точно?" : "Удалить";
            float var28 = ModuleStateHelper.handle(FontRegistry.config, var27, 10.0F);
            ModuleStateHelper.handle(
               var1,
               var4,
               FontRegistry.config,
               var58.x + (var58.w - var28) * 0.5F,
               var58.y,
               var58.h,
               10.0F,
               var27,
               ThemeColors.handle(var62 ? -1 : ModuleStateHelper.process(var5), Math.round(255.0F * var7))
            );
         } finally {
            this.handle(var1, var25);
         }
      }
   }

   public boolean handle(ModernClickGuiState var1, ThemeRenderContext var2, float var3, float var4, int var5) {
      if (this.handle(var1) && this.instance != null) {
         GuiMetrics var6 = this.instance;
         FoundryAssetBrowser.PrimaryDataRecord var7 = this.check();
         if (!var7.contains(var3, var4)) {
            return false;
         }

         if (var5 != 0) {
            return true;
         }

         this.pointEncode = false;
         if (this.previous && !this.select(var6, var7).contains(var3, var4)) {
            this.apply();
         }

         if (this.summary && !this.refresh(var6, var7).contains(var3, var4)) {
            this.resolve();
         }

         if (this.handle(var6, var7).contains(var3, var4)) {
            FoundryAssetBrowser.PrimaryDataRecord var18 = this.handle(var6, var7);
            if (!this.animationDraw.isEmpty() && var3 >= var18.x + var18.w - var6.handle(28.0F)) {
               this.select();
            }

            this.pointEncode = true;
            this.animator = false;
            return true;
         } else {
            if (this.compute(var6, var7).contains(var3, var4)) {
               this.process("import");
               this.execute();
               return true;
            }

            if (this.process(var6, var7).contains(var3, var4)) {
               this.process("reload");
               FoundryPresetLibrary.handle().resolve();
               this.handle("Обновлено");
               return true;
            }

            AssetCategory[] var8 = AssetCategory.values();
            float var9 = var7.y + var6.handle(44.0F);
            float var10 = var6.handle(26.0F);
            float var11 = var9 + (var6.handle(34.0F) - var10) * 0.5F;
            float var12 = var7.x + var6.handle(18.0F);

            for (AssetCategory var16 : var8) {
               float var17 = ModuleStateHelper.handle(FontRegistry.config, var16.process(), 11.0F) + var6.handle(20.0F);
               if (var3 >= var12 && var3 <= var12 + var17 && var4 >= var11 && var4 <= var11 + var10) {
                  this.process(var16.name());
                  if (this.cache != var16) {
                     this.vectorPerform = var16.ordinal() > this.cache.ordinal() ? 1 : -1;
                     this.profileDraw = 0.0F;
                     this.cache = var16;
                     this.output = this.current = 0.0F;
                  }

                  return true;
               }

               var12 += var17 + var6.handle(6.0F);
            }

            if (this.apply(var6, var7).contains(var3, var4)) {
               this.handle(var6, var7, var3, var4);
               return true;
            } else if (this.execute(var6, var7).contains(var3, var4)) {
               this.enabled = true;
               this.renderer = var3;
               this.handler = var4;
               return true;
            } else {
               this.process(var6, var7, var3, var4);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private void handle(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2, float var3, float var4) {
      FoundryAssetBrowser.PrimaryDataRecord var5 = this.apply(var1, var2);
      List var6 = this.prepare();
      int var7 = this.render(var1, var5);
      float var8 = var1.handle(10.0F);
      float var9 = (var5.w - var1.handle(12.0F) - (var7 - 1) * var8) / var7;
      float var10 = var9;
      float var11 = var10 + var1.handle(20.0F);
      float var12 = var1.handle(10.0F);
      float var13 = var5.x + var1.handle(6.0F);
      float var14 = var5.y + var1.handle(6.0F) - this.output;
      int var15 = (int)Math.floor((var3 - var13) / (var9 + var8));
      int var16 = (int)Math.floor((var4 - var14) / (var11 + var12));
      if (var15 >= 0 && var15 < var7 && var16 >= 0) {
         int var17 = var16 * var7 + var15;
         if (var17 < var6.size()) {
            float var18 = var13 + var15 * (var9 + var8);
            float var19 = var14 + var16 * (var11 + var12);
            if (!(var3 > var18 + var9) && !(var4 > var19 + var11)) {
               ThemeParser var20 = (ThemeParser)var6.get(var17);
               ThemeParser var21 = FoundryPresetLibrary.handle().apply();
               this.process(var20.handle());
               FoundryAssetBrowser.PrimaryDataRecord var22 = this.handle(var1, var18, var19, var9, var10);
               if (!var22.contains(var3, var4)) {
                  this.handle(var20);
               } else {
                  if (var21 != null && var21.handle().equals(var20.handle())) {
                     FoundryPresetLibrary.handle().handle(!FoundryPresetLibrary.handle().execute());
                  } else {
                     this.handle(var20);
                  }
               }
            }
         }
      }
   }

   private void handle(ThemeParser var1) {
      FoundryPresetLibrary.handle().handle(var1);
      StudioTextureLoader.handle().handle(var1.handle());
      this.active = 200.0F;
      this.mode = -8.0F;
      this.selection = 1.0F;
      this.update();
      this.compute();
      this.vectorMatch = false;
   }

   private void process(ThemeParser var1) {
      this.summary = true;
      this.matrixBlend = var1.check() == null ? "" : var1.check();
      this.previous = false;
      this.vectorMatch = false;
   }

   private void compute() {
      this.summary = false;
      this.matrixBlend = "";
   }

   private void resolve() {
      if (this.summary) {
         ThemeParser var1 = FoundryPresetLibrary.handle().apply();
         if (var1 != null) {
            FoundryPresetLibrary.handle().process(var1, this.matrixBlend);
            this.handle("Префикс сохранён");
         }

         this.summary = false;
         this.matrixBlend = "";
      }
   }

   private void compute(ThemeParser var1) {
      this.previous = true;
      this.latest = var1.resolve() == null ? "" : var1.resolve();
      this.vectorMatch = false;
   }

   private void update() {
      this.previous = false;
      this.latest = "";
   }

   private void apply() {
      if (this.previous) {
         ThemeParser var1 = FoundryPresetLibrary.handle().apply();
         if (var1 != null) {
            FoundryPresetLibrary.handle().handle(var1, this.latest);
            this.handle("Переименовано");
         }

         this.previous = false;
         this.latest = "";
      }
   }

   private void process(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2, float var3, float var4) {
      ThemeParser var5 = FoundryPresetLibrary.handle().apply();
      if (var5 != null) {
         if (this.select(var1, var2).contains(var3, var4)) {
            this.compute(var5);
         } else if (this.refresh(var1, var2).contains(var3, var4)) {
            this.process(var5);
         } else {
            FoundryAssetBrowser.PrimaryDataRecord var6 = this.prepare(var1, var2);
            AssetCategory[] var7 = AssetCategory.values();
            float var8 = var1.handle(20.0F);
            float var9 = var6.y + var1.handle(34.0F);
            float var10 = var6.x;

            for (AssetCategory var14 : var7) {
               float var15 = ModuleStateHelper.handle(FontRegistry.instance, var14.process(), 9.0F) + var1.handle(12.0F);
               if (var3 >= var10 && var3 <= var10 + var15 && var4 >= var9 && var4 <= var9 + var8) {
                  this.process(presetSave[var14.ordinal()]);
                  FoundryPresetLibrary.handle().handle(var5, var14);
                  this.handle("Категория: " + var14.process());
                  return;
               }

               var10 += var15 + var1.handle(4.0F);
            }

            FoundryAssetBrowser.PrimaryDataRecord var16 = this.check(var1, var2);
            if (var16.contains(var3, var4)) {
               this.process("equip");
               FoundryPresetLibrary.handle().handle(!FoundryPresetLibrary.handle().execute());
            } else {
               FoundryAssetBrowser.PrimaryDataRecord var17 = this.onTick(var1, var2);
               if (var17.contains(var3, var4)) {
                  this.process("delete");
                  if (this.vectorMatch && System.currentTimeMillis() - this.itemProject < 2600L) {
                     FoundryPresetLibrary.handle().process(var5);
                     StudioTextureLoader.handle().handle("");
                     this.vectorMatch = false;
                     this.handle("Удалено");
                  } else {
                     this.vectorMatch = true;
                     this.itemProject = System.currentTimeMillis();
                  }
               }
            }
         }
      }
   }

   public boolean handle(ModernClickGuiState var1, float var2, float var3) {
      this.enabled = false;
      return this.handle(var1);
   }

   public boolean process(ModernClickGuiState var1, float var2, float var3) {
      if (!this.enabled) {
         return false;
      }

      this.active = this.active + (var2 - this.renderer) * 0.55F;
      this.mode = handle(this.mode + (var3 - this.handler) * 0.55F, -89.0F, 89.0F);
      this.renderer = var2;
      this.handler = var3;
      return true;
   }

   public boolean handle(ModernClickGuiState var1, float var2, float var3, double var4) {
      if (this.handle(var1) && this.instance != null) {
         GuiMetrics var6 = this.instance;
         FoundryAssetBrowser.PrimaryDataRecord var7 = this.check();
         if (this.execute(var6, var7).contains(var2, var3)) {
            this.selection = handle(this.selection * (float)(1.0 + var4 * 0.12), 0.35F, 4.0F);
            return true;
         } else if (this.apply(var6, var7).contains(var2, var3)) {
            this.current = this.current - (float)var4 * var6.handle(52.0F);
            return true;
         } else {
            return var7.contains(var2, var3);
         }
      } else {
         return false;
      }
   }

   public boolean handle(ModernClickGuiState var1, int var2) {
      if (!this.handle(var1)) {
         return false;
      }

      if (this.previous) {
         if (var2 == 256) {
            this.update();
            return true;
         }

         if (var2 == 257) {
            this.apply();
            return true;
         }

         if (var2 == 259) {
            if (!this.latest.isEmpty()) {
               this.latest = this.latest.substring(0, this.latest.length() - 1);
            }

            return true;
         } else {
            return true;
         }
      } else if (this.summary) {
         if (var2 == 256) {
            this.compute();
            return true;
         }

         if (var2 == 257) {
            this.resolve();
            return true;
         }

         if (var2 == 259) {
            if (!this.matrixBlend.isEmpty()) {
               this.matrixBlend = this.matrixBlend.substring(0, this.matrixBlend.length() - 1);
            }

            return true;
         } else {
            return true;
         }
      } else {
         if (!this.pointEncode) {
            return false;
         }

         if (var2 != 256 && var2 != 257) {
            if (Screen.hasControlDown()) {
               if (var2 == 65) {
                  this.animator = !this.animationDraw.isEmpty();
                  return true;
               }

               if (var2 == 86) {
                  if (this.animator) {
                     this.select();
                     this.animator = false;
                  }

                  String var3 = MinecraftClient.getInstance().keyboard.getClipboard();
                  if (var3 != null) {
                     for (int var4 = 0; var4 < var3.length(); var4++) {
                        this.handle(var3.charAt(var4));
                     }
                  }

                  return true;
               }

               if (var2 == 67 && !this.animationDraw.isEmpty()) {
                  MinecraftClient.getInstance().keyboard.setClipboard(this.animationDraw);
                  this.handle("Скопировано");
                  return true;
               }

               if (var2 == 88) {
                  if (!this.animationDraw.isEmpty()) {
                     MinecraftClient.getInstance().keyboard.setClipboard(this.animationDraw);
                     this.handle("Вырезано");
                  }

                  this.select();
                  this.animator = false;
                  return true;
               }

               if (var2 == 259) {
                  this.select();
                  this.animator = false;
                  return true;
               }
            }

            if (var2 == 259) {
               if (this.animator) {
                  this.select();
                  this.animator = false;
               } else {
                  this.onTick();
               }

               return true;
            } else {
               if (var2 != 263 && var2 != 262) {
                  return true;
               }

               this.animator = false;
               return true;
            }
         } else {
            this.pointEncode = false;
            this.animator = false;
            return true;
         }
      }
   }

   public boolean handle(ModernClickGuiState var1, char var2) {
      if (!this.handle(var1)) {
         return false;
      }

      if (this.previous) {
         if (var2 >= ' ' && var2 != 127 && this.latest.length() < 40) {
            this.latest = this.latest + var2;
         }

         return true;
      } else if (this.summary) {
         if (var2 >= ' ' && var2 != 127 && this.matrixBlend.length() < 24) {
            this.matrixBlend = this.matrixBlend + var2;
         }

         return true;
      } else {
         if (!this.pointEncode) {
            return false;
         }

         if (this.animator) {
            this.select();
            this.animator = false;
         }

         this.handle(var2);
         return true;
      }
   }

   private void execute() {
      File var1 = AvatarFilePicker.handle();
      if (var1 != null) {
         this.handle(FoundryPresetLibrary.handle().handle(var1, this.cache));
      } else {
         AvatarFilePicker.process();
         this.handle("Бросьте .zip в папку и нажмите обновить");
      }
   }

   private List<ThemeParser> prepare() {
      List var1 = FoundryPresetLibrary.handle().handle(this.cache);
      if (this.animationDraw.isEmpty()) {
         return var1;
      }

      String var2 = this.animationDraw.toLowerCase();
      ArrayList var3 = new ArrayList();

      for (ThemeParser var5 : (List<ThemeParser>) var1) {
         String var6 = var5.resolve() == null ? "" : var5.resolve().toLowerCase();
         String var7 = var5.check() == null ? "" : var5.check().toLowerCase();
         String var8 = var5.apply() == null ? "" : var5.apply().toLowerCase();
         if (var6.contains(var2) || var7.contains(var2) || var8.contains(var2)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private void handle(String var1) {
      this.windowConvert = var1 == null ? "" : var1;
      this.presetWrite = System.currentTimeMillis();
   }

   private String resolve(ThemeParser var1) {
      if (!this.windowConvert.isEmpty() && System.currentTimeMillis() - this.presetWrite < 4200L) {
         return this.windowConvert;
      } else if (var1 == null) {
         return "Тяните — вращать · колесо — зум";
      } else {
         return var1.apply() != null && !var1.apply().isEmpty() ? "Автор: " + var1.apply() : "";
      }
   }

   private FoundryAssetBrowser.PrimaryDataRecord check() {
      return new FoundryAssetBrowser.PrimaryDataRecord(this.data, this.context, this.config, this.state);
   }

   private void process(String var1) {
      this.providerClose.put(var1, System.currentTimeMillis());
   }

   private float compute(String var1) {
      Long var2 = this.providerClose.get(var1);
      if (var2 == null) {
         return 1.0F;
      }

      float var3 = (float)(System.currentTimeMillis() - var2) / 320.0F;
      if (var3 >= 1.0F) {
         return 1.0F;
      }

      float var4 = (float)Math.exp(-var3 * 4.0);
      float var5 = (float)Math.cos(var3 * Math.PI * 2.2);
      return 1.0F - 0.14F * var4 * var5;
   }

   private boolean handle(RoundedRectRenderer var1, String var2, float var3, float var4) {
      float var5 = this.compute(var2);
      if (var5 > 0.999F && var5 < 1.001F) {
         return false;
      }

      var1.handle(var5, var3, var4);
      return true;
   }

   private void handle(RoundedRectRenderer var1, boolean var2) {
      if (var2) {
         var1.check();
      }
   }

   private void handle(char var1) {
      if (var1 >= ' ' && var1 != 127 && this.animationDraw.length() < 48) {
         this.animationDraw = this.animationDraw + var1;
         this.source.add(System.currentTimeMillis());
         this.current = 0.0F;
      }
   }

   private void onTick() {
      if (!this.animationDraw.isEmpty()) {
         int var1 = this.animationDraw.length() - 1;
         this.handle(var1);
         this.animationDraw = this.animationDraw.substring(0, var1);
         if (var1 < this.source.size()) {
            this.source.remove(var1);
         }

         this.current = 0.0F;
      }
   }

   private void select() {
      for (int var1 = 0; var1 < this.animationDraw.length(); var1++) {
         this.handle(var1);
      }

      this.animationDraw = "";
      this.source.clear();
      this.current = 0.0F;
   }

   private void handle(int var1) {
      if (this.instance != null && var1 >= 0 && var1 < this.animationDraw.length()) {
         FoundryAssetBrowser.PrimaryDataRecord var2 = this.handle(this.instance, this.check());
         float var3 = var2.x + this.instance.handle(26.0F) + ModuleStateHelper.handle(FontRegistry.instance, this.animationDraw.substring(0, var1), 10.0F);
         this.target.add(new FoundryAssetBrowser.DataRecord(String.valueOf(this.animationDraw.charAt(var1)), var3, System.currentTimeMillis()));
      }
   }

   private FoundryAssetBrowser.PrimaryDataRecord handle(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      float var3 = var1.handle(220.0F);
      float var4 = var1.handle(28.0F);
      float var5 = var2.x + var2.w - var1.handle(18.0F) - var3;
      return new FoundryAssetBrowser.PrimaryDataRecord(var5, var2.y + (var1.handle(44.0F) - var4) * 0.5F, var3, var4);
   }

   private FoundryAssetBrowser.PrimaryDataRecord process(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      float var3 = var1.handle(28.0F);
      float var4 = var2.y + var1.handle(44.0F) + (var1.handle(34.0F) - var3) * 0.5F;
      return new FoundryAssetBrowser.PrimaryDataRecord(var2.x + var2.w - var1.handle(18.0F) - var3, var4, var3, var3);
   }

   private FoundryAssetBrowser.PrimaryDataRecord compute(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      float var3 = var1.handle(28.0F);
      float var4 = var1.handle(86.0F);
      FoundryAssetBrowser.PrimaryDataRecord var5 = this.process(var1, var2);
      return new FoundryAssetBrowser.PrimaryDataRecord(var5.x - var1.handle(8.0F) - var4, var5.y, var4, var3);
   }

   private FoundryAssetBrowser.PrimaryDataRecord resolve(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      float var3 = var1.handle(18.0F);
      float var4 = var2.y + var1.handle(44.0F) + var1.handle(34.0F) + var1.handle(6.0F);
      return new FoundryAssetBrowser.PrimaryDataRecord(var2.x + var3, var4, var2.w - var3 * 2.0F, var2.y + var2.h - var3 - var4);
   }

   private FoundryAssetBrowser.PrimaryDataRecord update(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      FoundryAssetBrowser.PrimaryDataRecord var3 = this.resolve(var1, var2);
      float var4 = handle(var3.w * 0.33F, var1.handle(280.0F), var1.handle(420.0F));
      return new FoundryAssetBrowser.PrimaryDataRecord(var3.x + var3.w - var4, var3.y, var4, var3.h);
   }

   private FoundryAssetBrowser.PrimaryDataRecord apply(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      FoundryAssetBrowser.PrimaryDataRecord var3 = this.resolve(var1, var2);
      FoundryAssetBrowser.PrimaryDataRecord var4 = this.update(var1, var2);
      float var5 = var4.x - var1.handle(12.0F) - var3.x;
      return new FoundryAssetBrowser.PrimaryDataRecord(var3.x, var3.y, var5, var3.h);
   }

   private FoundryAssetBrowser.PrimaryDataRecord execute(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      FoundryAssetBrowser.PrimaryDataRecord var3 = this.update(var1, var2);
      FoundryAssetBrowser.PrimaryDataRecord var4 = this.prepare(var1, var2);
      float var5 = var3.y + var1.handle(34.0F);
      return new FoundryAssetBrowser.PrimaryDataRecord(
         var3.x + var1.handle(10.0F), var5, var3.w - var1.handle(20.0F), Math.max(var1.handle(40.0F), var4.y - var5 - var1.handle(8.0F))
      );
   }

   private FoundryAssetBrowser.PrimaryDataRecord prepare(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      FoundryAssetBrowser.PrimaryDataRecord var3 = this.update(var1, var2);
      float var4 = var1.handle(120.0F);
      return new FoundryAssetBrowser.PrimaryDataRecord(
         var3.x + var1.handle(14.0F), var3.y + var3.h - var1.handle(12.0F) - var4, var3.w - var1.handle(28.0F), var4
      );
   }

   private FoundryAssetBrowser.PrimaryDataRecord check(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      FoundryAssetBrowser.PrimaryDataRecord var3 = this.prepare(var1, var2);
      float var4 = var1.handle(28.0F);
      float var5 = var3.w * 0.6F;
      return new FoundryAssetBrowser.PrimaryDataRecord(var3.x, var3.y + var3.h - var4, var5, var4);
   }

   private FoundryAssetBrowser.PrimaryDataRecord onTick(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      FoundryAssetBrowser.PrimaryDataRecord var3 = this.prepare(var1, var2);
      FoundryAssetBrowser.PrimaryDataRecord var4 = this.check(var1, var2);
      float var5 = var3.w - var4.w - var1.handle(8.0F);
      return new FoundryAssetBrowser.PrimaryDataRecord(var4.x + var4.w + var1.handle(8.0F), var4.y, var5, var4.h);
   }

   private FoundryAssetBrowser.PrimaryDataRecord select(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      FoundryAssetBrowser.PrimaryDataRecord var3 = this.prepare(var1, var2);
      return new FoundryAssetBrowser.PrimaryDataRecord(var3.x, var3.y - var1.handle(2.0F), var3.w, var1.handle(16.0F));
   }

   private FoundryAssetBrowser.PrimaryDataRecord refresh(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      FoundryAssetBrowser.PrimaryDataRecord var3 = this.prepare(var1, var2);
      return new FoundryAssetBrowser.PrimaryDataRecord(var3.x, var3.y + var1.handle(58.0F), var3.w, var1.handle(16.0F));
   }

   private FoundryAssetBrowser.PrimaryDataRecord handle(GuiMetrics var1, float var2, float var3, float var4, float var5) {
      float var6 = var1.handle(28.0F);
      float var7 = var1.handle(15.0F);
      return new FoundryAssetBrowser.PrimaryDataRecord(var2 + var4 - var6 - var1.handle(8.0F), var3 + var5 + (var1.handle(20.0F) - var7) * 0.5F, var6, var7);
   }

   private int render(GuiMetrics var1, FoundryAssetBrowser.PrimaryDataRecord var2) {
      return Math.max(3, Math.min(5, Math.round((var2.w - var1.handle(12.0F)) / var1.handle(132.0F))));
   }

   private static String handle(String var0, int var1) {
      if (var0 == null) {
         return "";
      } else {
         return var0.length() <= var1 ? var0 : var0.substring(0, var1 - 1) + "…";
      }
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 < var1 ? var1 : Math.min(var0, var2);
   }

   record DataRecord(String ch, float x, long born) {
   }

   record PrimaryDataRecord(float x, float y, float w, float h) {

      boolean contains(float var1, float var2) {
         return var1 >= this.x && var2 >= this.y && var1 < this.x + this.w && var2 < this.y + this.h;
      }
   }
}
