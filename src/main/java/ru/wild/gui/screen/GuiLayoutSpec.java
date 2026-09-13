package ru.wild.gui.screen;
public final class GuiLayoutSpec {
   private final float instance;
   private final float data;
   private final float context;
   private final float config;
   private final float state;
   private final float cache;
   private final float output;
   private final float current;
   private final float active;
   private final float mode;
   private final float selection;
   private final float enabled;
   private final float renderer;
   private final float handler;
   private final float animationDraw;
   private final float pointEncode;
   private final float animator;
   private final float source;
   private final float target;

   public static GuiLayoutSpec handle() {
      return process().handle();
   }
   static float fetch() {
      return 832.0F;
   }
   static float measure() {
      return 512.0F;
   }
   static float blendMatrix() {
      return 8.0F;
   }
   static float matchVector() {
      return 8.0F;
   }
   static float projectItem() {
      return 72.0F;
   }
   static float computeResponse() {
      return 736.0F;
   }
   static float fetchProvider() {
      return 496.0F;
   }
   static float drawProfile() {
      return 46.0F;
   }
   static float performVector() {
      return 256.0F;
   }
   static float attachEvent() {
      return 442.0F;
   }
   static float readServer() {
      return 16.0F;
   }
   static float advancePosition() {
      return 343.0F;
   }
   static float checkFrame() {
      return 78.0F;
   }
   static float collectModule() {
      return 8.0F;
   }
   static float closeProvider() {
      return 4.0F;
   }
   static float savePreset() {
      return 310.0F;
   }
   static float convertWindow() {
      return 280.0F;
   }
   static float writePreset() {
      return 0.78F;
   }
   static float measureColor() {
      return 1.85F;
   }
   GuiLayoutSpec(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19
   ) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5;
      this.cache = var6;
      this.output = var7;
      this.current = var8;
      this.active = var9;
      this.mode = var10;
      this.selection = var11;
      this.enabled = var12;
      this.renderer = var13;
      this.handler = var14;
      this.animationDraw = var15;
      this.pointEncode = var16;
      this.animator = var17;
      this.source = var18;
      this.target = var19;
   }
   public static GuiLayoutSpec.NetworkState process() {
      return new GuiLayoutSpec.NetworkState();
   }
   public float compute() {
      return this.instance;
   }
   public float resolve() {
      return this.data;
   }
   public float update() {
      return this.context;
   }
   public float apply() {
      return this.config;
   }
   public float execute() {
      return this.state;
   }
   public float prepare() {
      return this.cache;
   }
   public float check() {
      return this.output;
   }
   public float onTick() {
      return this.current;
   }
   public float select() {
      return this.active;
   }
   public float refresh() {
      return this.mode;
   }
   public float render() {
      return this.selection;
   }
   public float tick() {
      return this.enabled;
   }
   public float drawAnimation() {
      return this.renderer;
   }
   public float encodePoint() {
      return this.handler;
   }
   public float animate() {
      return this.animationDraw;
   }
   public float load() {
      return this.pointEncode;
   }
   public float save() {
      return this.animator;
   }
   public float submit() {
      return this.source;
   }
   public float unload() {
      return this.target;
   }
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof GuiLayoutSpec var2)) {
         return false;
      } else if (Float.compare(this.compute(), var2.compute()) != 0) {
         return false;
      } else if (Float.compare(this.resolve(), var2.resolve()) != 0) {
         return false;
      } else if (Float.compare(this.update(), var2.update()) != 0) {
         return false;
      } else if (Float.compare(this.apply(), var2.apply()) != 0) {
         return false;
      } else if (Float.compare(this.execute(), var2.execute()) != 0) {
         return false;
      } else if (Float.compare(this.prepare(), var2.prepare()) != 0) {
         return false;
      } else if (Float.compare(this.check(), var2.check()) != 0) {
         return false;
      } else if (Float.compare(this.onTick(), var2.onTick()) != 0) {
         return false;
      } else if (Float.compare(this.select(), var2.select()) != 0) {
         return false;
      } else if (Float.compare(this.refresh(), var2.refresh()) != 0) {
         return false;
      } else if (Float.compare(this.render(), var2.render()) != 0) {
         return false;
      } else if (Float.compare(this.tick(), var2.tick()) != 0) {
         return false;
      } else if (Float.compare(this.drawAnimation(), var2.drawAnimation()) != 0) {
         return false;
      } else if (Float.compare(this.encodePoint(), var2.encodePoint()) != 0) {
         return false;
      } else if (Float.compare(this.animate(), var2.animate()) != 0) {
         return false;
      } else if (Float.compare(this.load(), var2.load()) != 0) {
         return false;
      } else if (Float.compare(this.save(), var2.save()) != 0) {
         return false;
      } else {
         return Float.compare(this.submit(), var2.submit()) != 0 ? false : Float.compare(this.unload(), var2.unload()) == 0;
      }
   }
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Float.floatToIntBits(this.compute());
      var2 = var2 * 59 + Float.floatToIntBits(this.resolve());
      var2 = var2 * 59 + Float.floatToIntBits(this.update());
      var2 = var2 * 59 + Float.floatToIntBits(this.apply());
      var2 = var2 * 59 + Float.floatToIntBits(this.execute());
      var2 = var2 * 59 + Float.floatToIntBits(this.prepare());
      var2 = var2 * 59 + Float.floatToIntBits(this.check());
      var2 = var2 * 59 + Float.floatToIntBits(this.onTick());
      var2 = var2 * 59 + Float.floatToIntBits(this.select());
      var2 = var2 * 59 + Float.floatToIntBits(this.refresh());
      var2 = var2 * 59 + Float.floatToIntBits(this.render());
      var2 = var2 * 59 + Float.floatToIntBits(this.tick());
      var2 = var2 * 59 + Float.floatToIntBits(this.drawAnimation());
      var2 = var2 * 59 + Float.floatToIntBits(this.encodePoint());
      var2 = var2 * 59 + Float.floatToIntBits(this.animate());
      var2 = var2 * 59 + Float.floatToIntBits(this.load());
      var2 = var2 * 59 + Float.floatToIntBits(this.save());
      var2 = var2 * 59 + Float.floatToIntBits(this.submit());
      return var2 * 59 + Float.floatToIntBits(this.unload());
   }
   @Override
   public String toString() {
      return "LayoutSpec(designWidth="
         + this.compute()
         + ", designHeight="
         + this.resolve()
         + ", padding="
         + this.update()
         + ", gap="
         + this.apply()
         + ", sidebarWidth="
         + this.execute()
         + ", bodyWidth="
         + this.prepare()
         + ", bodyHeight="
         + this.check()
         + ", headerHeight="
         + this.onTick()
         + ", searchWidth="
         + this.select()
         + ", contentHeight="
         + this.refresh()
         + ", contentPadding="
         + this.render()
         + ", columnWidth="
         + this.tick()
         + ", moduleHeaderHeight="
         + this.drawAnimation()
         + ", moduleGap="
         + this.encodePoint()
         + ", scrollbarWidth="
         + this.animate()
         + ", themeWidth="
         + this.load()
         + ", themeHeight="
         + this.save()
         + ", minPixelScale="
         + this.submit()
         + ", maxPixelScale="
         + this.unload()
         + ")";
   }
   public static class NetworkState {
      private boolean instance;
      private float data;
      private boolean context;
      private float config;
      private boolean state;
      private float cache;
      private boolean output;
      private float current;
      private boolean active;
      private float mode;
      private boolean selection;
      private float enabled;
      private boolean renderer;
      private float handler;
      private boolean animationDraw;
      private float pointEncode;
      private boolean animator;
      private float source;
      private boolean target;
      private float pending;
      private boolean previous;
      private float latest;
      private boolean summary;
      private float matrixBlend;
      private boolean vectorMatch;
      private float itemProject;
      private boolean responseCompute;
      private float providerFetch;
      private boolean profileDraw;
      private float vectorPerform;
      private boolean eventAttach;
      private float serverRead;
      private boolean positionAdvance;
      private float frameCheck;
      private boolean moduleCollect;
      private float providerClose;
      private boolean presetSave;
      private float windowConvert;
      NetworkState() {
      }
      public GuiLayoutSpec.NetworkState handle(float var1) {
         this.data = var1;
         this.instance = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState process(float var1) {
         this.config = var1;
         this.context = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState compute(float var1) {
         this.cache = var1;
         this.state = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState resolve(float var1) {
         this.current = var1;
         this.output = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState update(float var1) {
         this.mode = var1;
         this.active = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState apply(float var1) {
         this.enabled = var1;
         this.selection = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState execute(float var1) {
         this.handler = var1;
         this.renderer = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState prepare(float var1) {
         this.pointEncode = var1;
         this.animationDraw = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState check(float var1) {
         this.source = var1;
         this.animator = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState onTick(float var1) {
         this.pending = var1;
         this.target = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState select(float var1) {
         this.latest = var1;
         this.previous = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState refresh(float var1) {
         this.matrixBlend = var1;
         this.summary = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState render(float var1) {
         this.itemProject = var1;
         this.vectorMatch = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState tick(float var1) {
         this.providerFetch = var1;
         this.responseCompute = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState drawAnimation(float var1) {
         this.vectorPerform = var1;
         this.profileDraw = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState encodePoint(float var1) {
         this.serverRead = var1;
         this.eventAttach = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState animate(float var1) {
         this.frameCheck = var1;
         this.positionAdvance = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState load(float var1) {
         this.providerClose = var1;
         this.moduleCollect = true;
         return this;
      }
      public GuiLayoutSpec.NetworkState save(float var1) {
         this.windowConvert = var1;
         this.presetSave = true;
         return this;
      }
      public GuiLayoutSpec handle() {
         float var1 = this.data;
         if (!this.instance) {
            var1 = GuiLayoutSpec.fetch();
         }

         float var2 = this.config;
         if (!this.context) {
            var2 = GuiLayoutSpec.measure();
         }

         float var3 = this.cache;
         if (!this.state) {
            var3 = GuiLayoutSpec.blendMatrix();
         }

         float var4 = this.current;
         if (!this.output) {
            var4 = GuiLayoutSpec.matchVector();
         }

         float var5 = this.mode;
         if (!this.active) {
            var5 = GuiLayoutSpec.projectItem();
         }

         float var6 = this.enabled;
         if (!this.selection) {
            var6 = GuiLayoutSpec.computeResponse();
         }

         float var7 = this.handler;
         if (!this.renderer) {
            var7 = GuiLayoutSpec.fetchProvider();
         }

         float var8 = this.pointEncode;
         if (!this.animationDraw) {
            var8 = GuiLayoutSpec.drawProfile();
         }

         float var9 = this.source;
         if (!this.animator) {
            var9 = GuiLayoutSpec.performVector();
         }

         float var10 = this.pending;
         if (!this.target) {
            var10 = GuiLayoutSpec.attachEvent();
         }

         float var11 = this.latest;
         if (!this.previous) {
            var11 = GuiLayoutSpec.readServer();
         }

         float var12 = this.matrixBlend;
         if (!this.summary) {
            var12 = GuiLayoutSpec.advancePosition();
         }

         float var13 = this.itemProject;
         if (!this.vectorMatch) {
            var13 = GuiLayoutSpec.checkFrame();
         }

         float var14 = this.providerFetch;
         if (!this.responseCompute) {
            var14 = GuiLayoutSpec.collectModule();
         }

         float var15 = this.vectorPerform;
         if (!this.profileDraw) {
            var15 = GuiLayoutSpec.closeProvider();
         }

         float var16 = this.serverRead;
         if (!this.eventAttach) {
            var16 = GuiLayoutSpec.savePreset();
         }

         float var17 = this.frameCheck;
         if (!this.positionAdvance) {
            var17 = GuiLayoutSpec.convertWindow();
         }

         float var18 = this.providerClose;
         if (!this.moduleCollect) {
            var18 = GuiLayoutSpec.writePreset();
         }

         float var19 = this.windowConvert;
         if (!this.presetSave) {
            var19 = GuiLayoutSpec.measureColor();
         }

         return new GuiLayoutSpec(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19);
      }
      @Override
      public String toString() {
         return "LayoutSpec.LayoutSpecBuilder(designWidth$value="
            + this.data
            + ", designHeight$value="
            + this.config
            + ", padding$value="
            + this.cache
            + ", gap$value="
            + this.current
            + ", sidebarWidth$value="
            + this.mode
            + ", bodyWidth$value="
            + this.enabled
            + ", bodyHeight$value="
            + this.handler
            + ", headerHeight$value="
            + this.pointEncode
            + ", searchWidth$value="
            + this.source
            + ", contentHeight$value="
            + this.pending
            + ", contentPadding$value="
            + this.latest
            + ", columnWidth$value="
            + this.matrixBlend
            + ", moduleHeaderHeight$value="
            + this.itemProject
            + ", moduleGap$value="
            + this.providerFetch
            + ", scrollbarWidth$value="
            + this.vectorPerform
            + ", themeWidth$value="
            + this.serverRead
            + ", themeHeight$value="
            + this.frameCheck
            + ", minPixelScale$value="
            + this.providerClose
            + ", maxPixelScale$value="
            + this.windowConvert
            + ")";
      }
   }
}
