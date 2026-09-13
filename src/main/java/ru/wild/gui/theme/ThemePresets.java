package ru.wild.gui.theme;

import net.minecraft.client.MinecraftClient;
import ru.wild.WildClient;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ResettableSettingGroup;
import ru.wild.core.HudFerrofluidRenderer;
import ru.wild.render.shader.PrismaticEdgeShader;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public abstract class ThemePresets extends ResettableSettingGroup {
   public static final String data = "Тёмный";
   public static final String context = "Светлый";
   public static final String config = "Блюр";
   public static final String state = "Неоморфизм";
   public static final String cache = "Феррофлюид";
   public static final String output = "Призма";
   public static final String current = "Призма Core";
   public static final String active = "Нео дистанция";
   public static final String mode = "Нео размытие";
   public static final String selection = "Нео интенсивность";
   public static final String enabled = "Нео форма";
   public static final String renderer = "Плоская";
   public static final String handler = "Выпуклая";
   public static final String animationDraw = "Вогнутая";
   protected static final float pointEncode = 7.0F;
   protected static final float animator = 5.0F;
   protected static final float source = 10.0F;
   public final NumberSetting target = new NumberSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.05F, true);
   public final NumberSetting pending = new NumberSetting("Прозрачность тёмных элементов", 1.0F, 0.0F, 1.0F, 0.05F, true);
   public final ModeSetting previous = new ModeSetting("Стилистика", "Тёмный", "Тёмный", "Светлый", "Блюр", "Неоморфизм", "Феррофлюид", "Призма");
   public final NumberSetting latest = new NumberSetting("Нео дистанция", 5.5F, 2.0F, 18.0F, 0.5F, false).handle(() -> !this.select());
   public final NumberSetting summary = new NumberSetting("Нео размытие", 18.0F, 6.0F, 48.0F, 1.0F, false).handle(() -> !this.select());
   public final NumberSetting matrixBlend = new NumberSetting("Нео интенсивность", 0.72F, 0.1F, 1.0F, 0.05F, true).handle(() -> !this.select());
   public final ModeSetting vectorMatch = new ModeSetting("Нео форма", "Выпуклая", "Плоская", "Выпуклая", "Вогнутая").handle(() -> !this.select());
   public final ChoiceSetting itemProject = new ChoiceSetting(
      "Визуал",
      new BooleanSetting("Тень", true),
      new BooleanSetting("Обводка", true),
      new BooleanSetting("Темные зоны", true),
      new BooleanSetting("Верхняя накладка", true),
      new BooleanSetting("Нижняя накладка", true),
      new BooleanSetting("Тёмный рект поверх", true)
   );
   private ThemePalette instance;
   private boolean responseCompute;
   private ThemeColors providerFetch;
   private ThemePaletteRegistry.ColorState profileDraw;
   private ThemePalette vectorPerform;
   private long eventAttach = Long.MIN_VALUE;
   private int serverRead;
   private float positionAdvance = 0.5F;
   private float frameCheck = 0.5F;
   private int moduleCollect;
   private final SpringAnimation providerClose = new SpringAnimation(0.0F);
   private static final ThemePaletteRegistry presetSave = ThemePaletteRegistry.handle();

   public ThemePresets() {
      this.handle(this.target);
      this.handle(this.pending);
      this.handle(this.previous);
      this.handle(this.latest);
      this.handle(this.summary);
      this.handle(this.matrixBlend);
      this.handle(this.vectorMatch);
      this.handle(this.itemProject);
   }

   public int handle(float var1) {
      if (this.select()) {
         return ThemeShaderApplier.handle(var1);
      }

      if (this.refresh()) {
         int var7 = (int)(232.0F * var1);
         float var8 = this.drawAnimation();
         int var9 = PackedColor.compute(PackedColor.compute(13, 15, 24, var7), PackedColor.handle(this.animate().submit(), var7), 0.12F);
         int var5 = PackedColor.compute(
            PackedColor.compute(250, 253, 255, (int)(152.0F * var1)), PackedColor.handle(this.animate().save(), (int)(132.0F * var1)), 0.055F
         );
         return PackedColor.compute(var9, var5, var8);
      }

      if (this.render()) {
         int var6 = (int)(172.0F * var1);
         return PackedColor.compute(PackedColor.compute(14, 18, 30, var6), PackedColor.handle(this.animate().submit(), var6), 0.1F);
      }

      int var2 = (int)(255.0F * var1);
      if (this.tick()) {
         return PackedColor.handle(this.save(), (int)((this.process() ? 152 : 176) * var1));
      }

      return switch (this.previous.compute()) {
         case "Светлый" -> PackedColor.compute(240, 240, 245, var2);
         case "Блюр" -> PackedColor.compute(21, 22, 26, this.check() ? (int)(122.0F * var1) : 0);
         default -> PackedColor.compute(20, 20, 20, var2);
      };
   }

   public int process(float var1) {
      return this.handle(var1, this.execute());
   }

   public int compute(float var1) {
      return this.handle(var1, this.prepare());
   }

   private int handle(float var1, boolean var2) {
      if (!var2) {
         return PackedColor.compute(0, 0, 0, 0);
      }

      if (this.select()) {
         return ThemeShaderApplier.handle(var1);
      }

      float var3 = this.onTick(var1);
      if (this.refresh()) {
         int var9 = (int)(202.0F * var3);
         float var10 = this.drawAnimation();
         int var11 = PackedColor.compute(PackedColor.compute(15, 18, 30, var9), PackedColor.handle(this.animate().save(), var9), 0.1F);
         int var7 = PackedColor.compute(
            PackedColor.compute(255, 255, 255, (int)(132.0F * var3)), PackedColor.handle(this.animate().submit(), (int)(118.0F * var3)), 0.06F
         );
         return PackedColor.compute(var11, var7, var10);
      }

      if (this.render()) {
         int var8 = (int)(150.0F * var3);
         return PackedColor.compute(PackedColor.compute(12, 16, 26, var8), PackedColor.handle(this.animate().save(), var8), 0.08F);
      }

      int var4 = (int)(255.0F * var3);
      if (this.tick()) {
         return PackedColor.handle(this.submit(), (int)((this.process() ? 138 : 160) * var3));
      }

      return switch (this.previous.compute()) {
         case "Светлый" -> PackedColor.compute(200, 200, 205, var4);
         case "Блюр" -> PackedColor.compute(21, 22, 26, (int)(184.0F * var3));
         default -> PackedColor.compute(25, 25, 25, var4);
      };
   }

   public int resolve(float var1) {
      if (this.select()) {
         return PackedColor.compute(0, 0, 0, 0);
      }

      if (this.refresh()) {
         float var6 = this.drawAnimation();
         int var7 = PackedColor.handle(this.blendMatrix(), (int)(92.0F * var1));
         int var8 = PackedColor.compute(
            PackedColor.compute(20, 28, 42, (int)(56.0F * var1)), PackedColor.handle(this.matchVector(), (int)(76.0F * var1)), 0.35F
         );
         return PackedColor.compute(var7, var8, var6);
      }

      if (this.render()) {
         int var5 = PackedColor.compute(this.animate().save(), this.animate().submit(), 0.5F);
         return PackedColor.handle(var5, (int)(70.0F * var1));
      }

      int var2 = (int)(255.0F * var1);
      if (this.encodePoint() == ThemePalette.VERNAL_SOLSTICE) {
         return PackedColor.compute(5, 17, 5, (int)(46.0F * var1));
      }

      if (this.tick()) {
         return PackedColor.handle(this.unload(), (int)((this.process() ? 38 : 48) * var1));
      }

      return switch (this.previous.compute()) {
         case "Светлый" -> PackedColor.compute(200, 200, 200, var2);
         case "Блюр" -> PackedColor.compute(255, 255, 255, (int)(10.0F * var1));
         default -> PackedColor.compute(45, 45, 45, var2);
      };
   }

   public int update(float var1) {
      if (this.select()) {
         return ThemeShaderApplier.process(var1);
      } else if (this.refresh()) {
         float var5 = this.drawAnimation();
         int var3 = PackedColor.compute(242, 245, 255, (int)(255.0F * var1));
         int var4 = PackedColor.compute(18, 25, 38, (int)(255.0F * var1));
         return PackedColor.compute(var3, var4, var5);
      } else if (this.render()) {
         return PackedColor.compute(244, 247, 255, (int)(255.0F * var1));
      } else {
         int var2 = (int)(255.0F * var1);
         if (this.encodePoint() == ThemePalette.VERNAL_SOLSTICE) {
            return PackedColor.compute(5, 17, 5, var2);
         } else if (this.tick()) {
            return PackedColor.handle(this.fetch(), var2);
         } else {
            return this.previous.compute().equals("Светлый") ? PackedColor.compute(20, 20, 20, var2) : PackedColor.compute(255, 255, 255, var2);
         }
      }
   }

   public int apply(float var1) {
      if (this.select()) {
         return ThemeShaderApplier.compute(var1);
      }

      if (this.refresh()) {
         float var5 = this.drawAnimation();
         int var6 = PackedColor.compute(170, 177, 196, (int)(214.0F * var1));
         int var7 = PackedColor.compute(72, 84, 108, (int)(224.0F * var1));
         return PackedColor.compute(var6, var7, var5);
      }

      if (this.render()) {
         return PackedColor.compute(176, 184, 204, (int)(220.0F * var1));
      }

      int var2 = (int)(255.0F * var1);
      if (this.encodePoint() == ThemePalette.VERNAL_SOLSTICE) {
         return PackedColor.compute(5, 17, 5, (int)(184.0F * var1));
      }

      if (this.tick()) {
         return PackedColor.handle(this.measure(), var2);
      }

      return switch (this.previous.compute()) {
         case "Светлый" -> PackedColor.compute(80, 80, 80, var2);
         case "Блюр" -> PackedColor.compute(255, 255, 255, (int)(122.0F * var1));
         default -> PackedColor.compute(170, 170, 170, var2);
      };
   }

   public int execute(float var1) {
      return this.refresh()
         ? PackedColor.handle(ThemeColors.handle(this.blendMatrix(), this.matchVector(), 0.55F), (int)(255.0F * var1))
         : PackedColor.handle(this.animate().submit(), (int)(255.0F * var1));
   }

   public int prepare(float var1) {
      return this.refresh() ? PackedColor.handle(this.blendMatrix(), (int)(255.0F * var1)) : PackedColor.handle(this.animate().save(), (int)(255.0F * var1));
   }

   public int check(float var1) {
      return this.refresh() ? PackedColor.handle(this.matchVector(), (int)(255.0F * var1)) : PackedColor.handle(this.animate().submit(), (int)(255.0F * var1));
   }

   public float onTick(float var1) {
      return var1 * this.pending.compute();
   }

   public float compute() {
      return this.previous.compute().equals("Блюр") ? 1.0F : 1.5F;
   }

   public boolean resolve() {
      return !this.select() && this.itemProject.process("Тень");
   }

   public boolean update() {
      return !this.select() && this.itemProject.process("Обводка");
   }

   public boolean apply() {
      return this.itemProject.process("Темные зоны");
   }

   public boolean execute() {
      return this.apply() && this.itemProject.process("Верхняя накладка");
   }

   public boolean prepare() {
      return this.apply() && this.itemProject.process("Нижняя накладка");
   }

   public boolean check() {
      return this.itemProject.process("Тёмный рект поверх");
   }

   public boolean onTick() {
      return !this.select() && !this.refresh() && !this.render() ? this.previous.compute().equals("Блюр") : false;
   }

   public boolean select() {
      return "Неоморфизм".equals(this.previous.compute());
   }

   public boolean refresh() {
      return "Феррофлюид".equals(this.previous.compute());
   }

   public boolean render() {
      return "Призма".equals(this.previous.compute()) || "Призма Core".equals(this.previous.compute());
   }

   public static boolean handle(String var0) {
      return "Неоморфизм".equals(var0);
   }

   public static boolean process(String var0) {
      return "Феррофлюид".equals(var0);
   }

   public static boolean compute(String var0) {
      return "Призма".equals(var0);
   }

   public boolean handle(float var1, float var2, float var3, float var4, float var5, boolean var6, float var7) {
      return this.select()
         && ThemeShaderApplier.handle(
            null,
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7,
            ThemeShaderApplier.handle(this.latest.compute(), this.summary.compute(), this.matrixBlend.compute(), this.vectorMatch.compute())
         );
   }

   public boolean handle(float var1, float var2, float var3, float var4, float var5, boolean var6, float var7, int var8) {
      return this.select()
         && ThemeShaderApplier.handle(
            null, var1, var2, var3, var4, var5, this.latest.compute(), this.summary.compute(), this.matrixBlend.compute(), var8, var6, var7
         );
   }

   public boolean handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10, float var11) {
      return this.select() && ThemeShaderApplier.handle(null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.handle(var2, var3, var4, var5);
      if (!ThemeShaderApplier.handle(null, var2, var3, var4, var5, var6, var7)) {
         if (!this.handle(var2, var3, var4, var5, var6, false, var7)) {
            if (!this.handle(var1, var2, var3, var4, var5, var6, false, var7)) {
               if (!this.process(var1, var2, var3, var4, var5, var6, false, var7)) {
                  if (this.resolve()) {
                     var1.handle(var2, var3, var4, var5, var6, this.tick() ? 6.0F : 4.0F, 1.0F, this.select(var7));
                  }

                  if (this.onTick()) {
                     var1.handle(23.0F);
                     var1.handle(var2, var3, var4, var5, var6, var7);
                  }

                  if (this.tick() && !this.onTick()) {
                     this.handle(var1, var2, var3, var4, var5, var6, this.handle(var7), false, var7);
                  } else {
                     var1.handle(var2, var3, var4, var5, var6, this.handle(var7));
                  }

                  if (this.update()) {
                     var1.handle(var2, var3, var4, var5, var6, this.resolve(var7), this.compute());
                  }
               }
            }
         }
      }
   }

   public void process(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.handle(var2, var3, var4, var5);
      if (!this.handle(var1, var2, var3, var4, var5, var6, true, var7)) {
         if (!this.process(var1, var2, var3, var4, var5, var6, true, var7)) {
            if (this.prepare()) {
               if (!this.handle(var2, var3, var4, var5, var6, true, var7)) {
                  if (this.tick() && !this.onTick()) {
                     this.handle(var1, var2, var3, var4, var5, var6, this.compute(var7), true, var7);
                  } else {
                     var1.handle(var2, var3, var4, var5, var6, this.compute(var7));
                  }

                  if (this.update()) {
                     var1.handle(var2, var3, var4, var5, var6, this.resolve(var7), Math.max(1.0F, this.compute() * 0.65F));
                  }
               }
            }
         }
      }
   }

   protected boolean tick() {
      return "Светлый".equals(this.previous.compute()) || ThemeShaderApplier.resolve();
   }

   private boolean process() {
      ThemePalette var1 = this.encodePoint();
      return this.tick() && (var1 == ThemePalette.SAKURA_BREEZE || var1 == ThemePalette.SAKURA);
   }

   protected float drawAnimation() {
      return this.providerClose.handle(this.tick() ? 1.0F : 0.0F, SpringAnimationSpec.encodePoint());
   }

   protected void handle(float var1, float var2, float var3, float var4) {
      int var5 = 0;
      int var6 = 0;

      try {
         MinecraftClient var7 = MinecraftClient.getInstance();
         if (var7 != null && var7.getWindow() != null) {
            var5 = var7.getWindow().getFramebufferWidth();
            var6 = var7.getWindow().getFramebufferHeight();
         }
      } catch (Throwable var11) {
      }

      if (var5 > 0 && var6 > 0 && Float.isFinite(var1) && Float.isFinite(var2) && Float.isFinite(var3) && Float.isFinite(var4)) {
         this.positionAdvance = this.refresh((var1 + var3 * 0.5F) / var5);
         this.frameCheck = this.refresh((var2 + var4 * 0.5F) / var6);
         int var12 = Math.round(this.positionAdvance * 2048.0F);
         int var8 = Math.round(this.frameCheck * 2048.0F);
         int var9 = Math.round(Math.max(1.0F, var3) * 0.25F);
         int var10 = Math.round(Math.max(1.0F, var4) * 0.25F);
         this.moduleCollect = (var12 * 7349 ^ var8 * 9151) * 31 ^ var9 * 131 ^ var10;
      } else {
         this.positionAdvance = 0.5F;
         this.frameCheck = 0.5F;
         this.moduleCollect = 0;
      }
   }

   private boolean handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, boolean var7, float var8) {
      return this.refresh()
         && HudFerrofluidRenderer.handle(
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var8,
            var7,
            this.handle(var8),
            this.resolve(var8),
            this.prepare(var8),
            this.check(var8),
            this.resolve(),
            true,
            this.drawAnimation()
         );
   }

   private boolean process(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, boolean var7, float var8) {
      return this.render()
         && PrismaticEdgeShader.handle(
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var8,
            var7,
            this.handle(var8),
            this.resolve(var8),
            this.prepare(var8),
            this.check(var8),
            this.resolve(),
            this.update(),
            this.drawAnimation()
         );
   }

   private ThemePalette encodePoint() {
      return WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
   }

   private ThemeColors animate() {
      ThemePalette var1 = this.encodePoint();
      boolean var2 = this.tick();
      long var3 = System.currentTimeMillis();
      long var5 = var3 / 16L;
      if (this.providerFetch == null
         || this.instance != var1
         || this.responseCompute != var2
         || this.eventAttach != var5
         || this.serverRead != this.moduleCollect
         || var1 == ThemePalette.CUSTOM) {
         ThemeColors var7 = ThemeColors.handle(var1, var2);
         this.providerFetch = this.handle(var1, ThemeColors.handle(var1, var7, var3), var3);
         this.instance = var1;
         this.responseCompute = var2;
         this.eventAttach = var5;
         this.serverRead = this.moduleCollect;
      }

      return this.providerFetch;
   }

   private ThemeColors handle(ThemePalette var1, ThemeColors var2, long var3) {
      ThemePaletteRegistry.ColorState var5 = this.load();
      int[] var6 = var5 == null ? null : var5.execute();
      if (var2 != null && var6 != null && var6.length >= 2) {
         float var7 = this.handle(var3);
         int var8 = this.handle(var6, var7);
         int var9 = this.handle(var6, var7 + 0.31F);
         int var10 = this.handle(var6, var7 + 0.67F);
         float var11 = var6.length > 2 ? 1.0F : 0.52F;
         float var12 = 0.34F + 0.3F * var11;
         float var13 = var2.unload() ? 0.018F + 0.02F * var11 : 0.045F + 0.05F * var11;
         float var14 = var2.unload() ? 0.06F + 0.035F * var11 : 0.16F + 0.1F * var11;
         int var15 = PackedColor.compute(var2.save(), PackedColor.compute(var8, -1, 0.1F), var12);
         int var16 = PackedColor.compute(var2.submit(), var9, var12);
         int var17 = ThemeColors.process(var2.apply(), var10, var13);
         int var18 = ThemeColors.process(var2.execute(), var9, var13 * 1.08F);
         int var19 = ThemeColors.process(var2.tick(), var8, var14);
         int var20 = ThemeColors.process(var2.drawAnimation(), var9, var14);
         int var21 = var2.unload() ? var2.encodePoint() : ThemeColors.process(var2.encodePoint(), var10, 0.1F + 0.08F * var11);
         int var22 = var2.unload() ? var2.animate() : ThemeColors.process(var2.animate(), var8, 0.08F + 0.06F * var11);
         int var23 = var2.unload() ? var2.load() : PackedColor.compute(var2.load(), var8, 0.025F + 0.025F * var11);
         return ThemeColors.handle(
            ThemeColors.update()
               .handle(var17)
               .process(var18)
               .compute(var2.prepare())
               .resolve(var2.check())
               .update(var2.onTick())
               .apply(var2.select())
               .execute(var2.refresh())
               .prepare(var2.render())
               .check(var19)
               .onTick(var20)
               .select(var21)
               .refresh(var22)
               .render(var23)
               .tick(var15)
               .drawAnimation(var16)
               .handle(var2.unload())
               .handle()
         );
      } else {
         return var2;
      }
   }

   private float handle(long var1) {
      float var3 = (float)(var1 % 14000L) / 14000.0F;
      float var4 = (float)Math.sin((this.positionAdvance * 1.72F - this.frameCheck * 1.18F + var3 * 1.35F) * (float) (Math.PI * 2)) * 0.055F;
      return this.positionAdvance * 0.54F + this.frameCheck * 0.36F + var3 * 0.58F + var4;
   }

   private int handle(int[] var1, float var2) {
      if (var1.length == 1) {
         return var1[0];
      }

      float var3 = var2 - (float)Math.floor(var2);
      float var4 = var3 * (var1.length - 1);
      int var5 = Math.min(var1.length - 2, Math.max(0, (int)Math.floor(var4)));
      return PackedColor.compute(var1[var5], var1[var5 + 1], var4 - var5);
   }

   private float refresh(float var1) {
      return Math.max(0.0F, Math.min(1.0F, var1));
   }

   private ThemePaletteRegistry.ColorState load() {
      ThemePalette var1 = this.encodePoint();
      if (var1 == ThemePalette.CUSTOM) {
         return null;
      }

      if (this.profileDraw == null || this.vectorPerform != var1) {
         this.profileDraw = presetSave.process(var1);
         this.vectorPerform = var1;
      }

      return this.profileDraw;
   }

   private int save() {
      ThemeColors var1 = this.animate();
      if (this.process()) {
         int var4 = PackedColor.compute(-1283, var1.save(), 0.05F);
         return PackedColor.compute(var4, var1.submit(), 0.03F);
      }

      int var2 = PackedColor.compute(-196865, var1.save(), 0.026F);
      ThemePaletteRegistry.ColorState var3 = this.load();
      if (var3 != null && var3.apply()) {
         var2 = PackedColor.compute(var2, var3.resolve(), 0.022F);
      }

      return var2;
   }

   private int submit() {
      ThemeColors var1 = this.animate();
      if (this.process()) {
         int var4 = PackedColor.compute(-1, var1.submit(), 0.07F);
         return PackedColor.compute(var4, var1.save(), 0.03F);
      }

      int var2 = PackedColor.compute(-1, var1.submit(), 0.022F);
      ThemePaletteRegistry.ColorState var3 = this.load();
      if (var3 != null && var3.apply()) {
         var2 = PackedColor.compute(var2, var3.update(), 0.018F);
      }

      return var2;
   }

   private int unload() {
      ThemeColors var1 = this.animate();
      int var2 = PackedColor.compute(var1.save(), var1.submit(), 0.44F);
      return this.process() ? PackedColor.compute(-7582617, var2, 0.48F) : PackedColor.compute(-15261133, var2, 0.34F);
   }

   private int fetch() {
      ThemeColors var1 = this.animate();
      return PackedColor.compute(-15722718, var1.save(), 0.035F);
   }

   private int measure() {
      ThemeColors var1 = this.animate();
      return PackedColor.compute(-12168086, var1.submit(), 0.055F);
   }

   public int select(float var1) {
      if (this.tick()) {
         ThemeColors var2 = this.animate();
         if (this.process()) {
            int var4 = PackedColor.compute(-2779216, var2.submit(), 0.26F);
            return PackedColor.handle(var4, (int)(34.0F * var1));
         } else {
            int var3 = PackedColor.compute(-10787208, var2.submit(), 0.1F);
            return PackedColor.handle(var3, (int)(46.0F * var1));
         }
      } else {
         return PackedColor.compute(0, 0, 0, (int)(80.0F * var1));
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, int var7, boolean var8, float var9) {
      int var10 = PackedColor.handle(var7);
      ThemeColors var11 = this.animate();
      float var12 = this.process() ? 0.115F : 0.055F;
      float var13 = this.process() ? 0.085F : 0.04F;
      int var14 = PackedColor.handle(PackedColor.compute(var7, var11.save(), var8 ? var12 * 0.76F : var12), var10);
      int var15 = PackedColor.handle(PackedColor.compute(var7, var11.submit(), var8 ? var13 * 0.7F : var13), var10);
      var1.process(var2, var3, var4, var5, var6, var14, var15);
      if (!var8 && var5 > 10.0F) {
         float var16 = Math.max(4.0F, Math.min(var5 * 0.36F, 18.0F));
         int var17 = PackedColor.compute(255, 255, 255, (int)((this.process() ? 34 : 24) * var9));
         var1.process(var2 + 1.0F, var3 + 1.0F, Math.max(1.0F, var4 - 2.0F), var16, Math.max(0.0F, var6 - 1.0F), var17, PackedColor.compute(255, 255, 255, 0));
      }
   }

   private int blendMatrix() {
      return ThemeColors.process(PackedColor.handle(this.animate().save(), 255), -1, 0.2F);
   }

   private int matchVector() {
      return ThemeColors.process(PackedColor.handle(this.animate().submit(), 255), -1, 0.12F);
   }
}
