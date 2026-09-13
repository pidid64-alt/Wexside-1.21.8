package ru.wild.gui.widget;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import org.wild.module.api.Module;
import ru.wild.api.event.MouseUpdateEvent;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.KeybindMode;
import ru.wild.api.setting.KeybindTargetModel;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.MultiSelectSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.core.AnimationClock;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.font.FontObject;
import ru.wild.util.math.PopupAnimationCurve;
import ru.wild.util.math.SpringFloat;
import ru.wild.util.math.SpringParameters;
import ru.wild.util.render.ColorVector;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeybindPresets;

public final class KeybindSelectorScreen {
   static final SpringParameters instance = SpringParameters.handle(2.2F, 0.72F);
   private static final SpringParameters data = SpringParameters.handle(1.9F, 0.68F);
   private static final float context = 16.0F;
   private static final float config = 8.0F;
   private static final float state = 8.0F;
   private static final float cache = 0.001F;
   private static final long output = 1200000000L;
   private final SpringFloat current;
   private final SpringFloat active;
   private final SpringFloat mode;
   private final SpringFloat selection;
   private final SpringFloat enabled;
   private final SpringFloat renderer;
   private final SpringFloat handler;
   private final PopupPlacementEngine animationDraw;
   private KeybindTargetModel pointEncode;
   private KeybindEditorRenderer.DataRecord animator;
   private KeybindEditorRenderer.State source = new KeybindEditorRenderer.State(0.0F, 0.0F, 0.0F, 0.0F);
   private KeybindEditor target;
   private float pending;
   private float previous;
   private float latest = Float.NaN;
   private float summary = Float.NaN;
   private float matrixBlend = 1.0F;
   private boolean vectorMatch;
   private boolean itemProject;
   private boolean responseCompute;
   private long providerFetch;
   private double profileDraw = -1.0;
   private double vectorPerform = -1.0;

   private static SpringFloat prepare() {
      SpringFloat var0 = new SpringFloat(AnimationClock.handle(), data, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      var0.handle(PopupAnimationCurve.context);
      return var0;
   }

   KeybindSelectorScreen(SpringFloat var1) {
      this.current = Objects.requireNonNull(var1, "visibilityAnimator");
      this.active = prepare();
      this.mode = prepare();
      this.selection = prepare();
      this.enabled = prepare();
      this.renderer = prepare();
      this.handler = prepare();
      this.animationDraw = new PopupPlacementEngine(16.0F, 8.0F, 8.0F);
   }

   private void check() {
      this.target = null;
      if (this.pointEncode != null && this.pointEncode.refresh()) {
         Setting var1 = this.pointEncode.compute();
         if (var1 instanceof BooleanSetting var2) {
            this.target = new BooleanValueWidget(this.pointEncode, var2);
         } else if (var1 instanceof NumberSetting var3) {
            this.target = new NumberValueWidget(this.pointEncode, var3);
         } else if (var1 instanceof ModeSetting var4) {
            this.target = new StringValueWidget(this.pointEncode, var4);
         } else if (var1 instanceof MultiSelectSetting var5) {
            this.target = new ModelWidget(this.pointEncode, var5);
         }
      }
   }

   private float onTick() {
      return this.target == null ? 0.0F : Math.max(0.0F, this.target.update());
   }

   private void select() {
      if (this.target != null && this.animator != null) {
         KeybindEditorRenderer.State var1 = this.animator.valueContent();
         if (!(var1.resolve() <= 0.0F)) {
            this.target.handle(var1);
         }
      }
   }

   private boolean process(double var1, double var3, int var5) {
      if (this.target == null) {
         return false;
      } else if (this.target.execute()) {
         return this.target.handle(var1, var3, var5) ? true : true;
      } else {
         return this.animator != null && this.animator.valueContent().handle(var1, var3) ? this.target.handle(var1, var3, var5) : false;
      }
   }

   private boolean process(double var1, double var3, double var5, double var7) {
      if (this.target == null) {
         return false;
      } else if (this.target.execute()) {
         return this.target.handle(var1, var3, var5, var7) ? true : true;
      } else {
         return this.animator != null && this.animator.valueContent().handle(var1, var3) ? this.target.handle(var1, var3, var5, var7) : false;
      }
   }

   public static KeybindSelectorScreen handle() {
      return KeybindSelectorScreen.ScreenState.instance;
   }

   public synchronized void handle(Module var1, double var2, double var4, int var6, int var7) {
      Objects.requireNonNull(var1, "module");
      KeybindTargetModel var8 = KeybindTargetModel.handle(var1);
      this.handle(var8, var2, var4, var6, var7);
   }

   public synchronized void handle(Module var1, Setting var2, double var3, double var5, Object var7) {
      MinecraftClient var8 = MinecraftClient.getInstance();
      int var9 = 1;
      int var10 = 1;
      if (var8 != null && var8.getWindow() != null) {
         var9 = Math.max(1, var8.getWindow().getFramebufferWidth());
         var10 = Math.max(1, var8.getWindow().getFramebufferHeight());
      }

      this.handle(var1, var2, var3, var5, var9, var10, var7);
   }

   public synchronized void handle(Module var1, Setting var2, double var3, double var5, int var7, int var8, Object var9) {
      Objects.requireNonNull(var1, "module");
      Objects.requireNonNull(var2, "setting");
      Object var10 = var9 != null ? var9 : handle(var2);
      Object var11 = process(var2);
      byte var12 = -1;
      int var13 = Math.max(1, var7);
      int var14 = Math.max(1, var8);
      KeybindTargetModel var15 = KeybindTargetModel.handle(var1, var2, var10, var11, var12, KeybindMode.TOGGLE);
      this.handle(var15, var3, var5, var13, var14);
   }

   private void handle(KeybindTargetModel var1, double var2, double var4, int var6, int var7) {
      this.pointEncode = Objects.requireNonNull(var1, "newModel");
      this.vectorMatch = false;
      this.responseCompute = false;
      this.providerFetch = 0L;
      this.itemProject = false;
      KeybindPresets.handle().handle(false);
      this.check();
      KeybindEditorRenderer.DataRecord var8 = KeybindEditorRenderer.handle(this.pointEncode, 0.0F, 0.0F, this.onTick());
      float var9 = var8.bounds().compute();
      float var10 = var8.bounds().resolve();
      this.latest = handle(var2);
      this.summary = handle(var4);
      this.matrixBlend = this.save();
      this.handle(var9, var10, var6, var7);
      this.animator = KeybindEditorRenderer.handle(this.pointEncode, this.pending, this.previous, this.onTick());
      this.source = this.animator.field();
      this.select();
      this.tick();
      this.animate();
      this.handler.compute(1.0F);
      this.current.compute(1.0F);
   }

   private static Object handle(Setting var0) {
      if (var0 instanceof BooleanSetting) {
         return ((BooleanSetting)var0).compute();
      } else if (var0 instanceof ModeSetting) {
         return ((ModeSetting)var0).state;
      } else if (var0 instanceof NumberSetting) {
         return (double)((NumberSetting)var0).config;
      } else if (var0 instanceof MultiSelectSetting) {
         return new LinkedHashSet<>(((MultiSelectSetting)var0).output);
      } else {
         return var0 instanceof ColorSetting var1 ? ColorVector.handle(var1.update(), var1.renderer, var1.handler, var1.animationDraw) : null;
      }
   }

   private static Object process(Setting var0) {
      if (var0 instanceof BooleanSetting) {
         return Boolean.FALSE;
      } else if (var0 instanceof ModeSetting) {
         return ((ModeSetting)var0).state != null ? ((ModeSetting)var0).state : "";
      } else if (var0 instanceof NumberSetting) {
         return (double)((NumberSetting)var0).config;
      } else if (var0 instanceof MultiSelectSetting) {
         return new LinkedHashSet<>(((MultiSelectSetting)var0).output);
      } else {
         return var0 instanceof ColorSetting var1 ? ColorVector.handle(var1.update(), var1.renderer, var1.handler, var1.animationDraw) : null;
      }
   }

   private static JsonElement handle(Setting var0, Object var1) {
      if (var0 instanceof BooleanSetting) {
         return new JsonPrimitive(process(var0, var1));
      } else if (var0 instanceof NumberSetting) {
         return new JsonPrimitive(handle((NumberSetting)var0, var1));
      } else if (var0 instanceof ModeSetting) {
         return new JsonPrimitive(handle((ModeSetting)var0, var1));
      } else if (var0 instanceof MultiSelectSetting) {
         return handle((MultiSelectSetting)var0, var1);
      } else {
         return (JsonElement)(var0 instanceof ColorSetting ? compute(var0, var1) : new JsonPrimitive(var1 != null ? var1.toString() : ""));
      }
   }

   private static boolean process(Setting var0, Object var1) {
      if (var1 instanceof Boolean var3) {
         return var3;
      } else if (var1 instanceof Number var2) {
         return var2.doubleValue() != 0.0;
      } else {
         return var0 instanceof BooleanSetting ? ((BooleanSetting)var0).compute() : false;
      }
   }

   private static JsonElement compute(Setting var0, Object var1) {
      if (var0 instanceof ColorSetting var2) {
         ColorVector var3;
         if (var1 instanceof ColorVector var4) {
            var3 = var4;
         } else if (var1 instanceof Number var5) {
            var3 = ColorVector.handle(var5.intValue());
         } else if (var1 instanceof String var6) {
            try {
               String var7 = var6.startsWith("#") ? var6.substring(1) : var6;
               int var8 = (int)Long.parseUnsignedLong(var7, 16);
               int var9 = var7.length() > 6 ? var8 : 0xFF000000 | var8;
               var3 = ColorVector.handle(var9);
            } catch (NumberFormatException var10) {
               var3 = ColorVector.handle(var2.update(), var2.renderer, var2.handler, var2.animationDraw);
            }
         } else {
            var3 = ColorVector.handle(var2.update(), var2.renderer, var2.handler, var2.animationDraw);
         }

         return new JsonPrimitive(var3.apply());
      } else {
         throw new IllegalStateException("Expected HueSetting for colour type");
      }
   }

   private static double handle(NumberSetting var0, Object var1) {
      double var2;
      if (var1 instanceof Number var4) {
         var2 = var4.doubleValue();
      } else {
         var2 = var0.config;
      }

      if (!Double.isFinite(var2)) {
         var2 = var0.config;
      }

      double var16 = var0.state;
      double var6 = var0.cache;
      double var8 = var0.output;
      if (!Double.isFinite(var8) || var8 <= 0.0) {
         var8 = 1.0;
      }

      double var10 = Math.min(Math.max(var2, var16), var6);
      double var12 = Math.round((var10 - var16) / var8);
      double var14 = var16 + var12 * var8;
      if (var14 < var16) {
         var14 = var16;
      } else if (var14 > var6) {
         var14 = var6;
      }

      return var14;
   }

   private static String handle(ModeSetting var0, Object var1) {
      String var2 = var1 != null ? var1.toString() : null;
      if (var2 == null || var2.isBlank() || var0.config != null && !var0.config.contains(var2)) {
         var2 = var0.state != null ? var0.state : "";
      }

      return var2;
   }

   private static String resolve(Setting var0, Object var1) {
      Object var2 = var1 != null ? var1 : "";
      return var2 == null ? "" : var2.toString();
   }

   private static JsonElement handle(MultiSelectSetting var0, Object var1) {
      var0.compute();
      Collection var2;
      if (var1 instanceof Collection var3) {
         var2 = var3;
      } else {
         var2 = var0.output != null ? var0.output : List.of();
      }

      LinkedHashSet var7 = new LinkedHashSet();
      if (var2 != null) {
         for (Object var5 : var2) {
            if (var5 != null) {
               String var6 = var5.toString();
               if (var0.config != null && var0.config.contains(var6)) {
                  var7.add(var6);
               }
            }
         }
      }

      if (var7.isEmpty() && var0.output != null) {
         var7.addAll(var0.output);
      }

      JsonArray var8 = new JsonArray();

      for (String var10 : (List<String>) var7) {
         var8.add(var10);
      }

      return var8;
   }

   private void handle(float var1, float var2, int var3, int var4) {
      PopupPlacementEngine.DataRecord var5 = this.animationDraw.handle(this.latest, this.summary, var1, var2, var3, var4, this.matrixBlend);
      this.pending = var5.x();
      this.previous = var5.y();
   }

   public synchronized boolean handle(double var1, double var3, int var5) {
      if (!this.render()) {
         return false;
      }

      if (this.animator == null) {
         return false;
      }

      if (this.process(var1, var3, var5)) {
         return true;
      }

      boolean var6 = this.animator.bounds().handle(var1, var3);
      if (!var6) {
         this.process();
         return true;
      }

      if (var5 == 0) {
         if (this.source.handle(var1, var3)) {
            if (this.vectorMatch) {
               this.encodePoint();
            } else {
               this.drawAnimation();
            }

            return true;
         } else if (this.animator.toggleButton().handle(var1, var3)) {
            this.handle(KeybindMode.TOGGLE);
            return true;
         } else if (this.animator.holdButton().handle(var1, var3)) {
            this.handle(KeybindMode.HOLD);
            return true;
         } else {
            this.process();
            return true;
         }
      } else if (var5 == 1) {
         this.process();
         return true;
      } else {
         return var6;
      }
   }

   public synchronized boolean handle(double var1, double var3, double var5, double var7) {
      if (!this.render()) {
         return false;
      } else {
         return this.process(var1, var3, var5, var7) ? true : true;
      }
   }

   public synchronized boolean handle(MouseUpdateEvent var1) {
      Objects.requireNonNull(var1, "event");
      return this.resolve();
   }

   public synchronized boolean handle(int var1, int var2, int var3, int var4) {
      if (!this.render()) {
         return false;
      } else if (!this.vectorMatch) {
         int var5 = refresh();
         return var5 != -1 && var1 == var5 ? false : this.pointEncode != null;
      } else if (var3 != 1) {
         return true;
      } else if (var1 == 261 || var1 == 259 || var1 == 256) {
         this.pointEncode.unload();
         this.responseCompute = false;
         this.providerFetch = 0L;
         this.encodePoint();
         this.execute();
         return true;
      } else if (var1 == -1) {
         return true;
      } else if (this.handle(var1)) {
         this.responseCompute = true;
         this.providerFetch = System.nanoTime();
         return true;
      } else {
         this.pointEncode.handle(var1);
         this.responseCompute = false;
         this.providerFetch = 0L;
         this.encodePoint();
         this.execute();
         return true;
      }
   }

   private static int refresh() {
      Menu var0 = Menu.drawAnimation();
      if (var0 == null) {
         return 344;
      } else {
         return var0.keyCode > 0 ? var0.keyCode : 344;
      }
   }

   public synchronized void handle(RoundedRectRenderer var1, FontObject var2, int var3, int var4, float var5) {
      Objects.requireNonNull(var1, "renderer");
      Objects.requireNonNull(var2, "defaultFont");
      if (this.pointEncode == null) {
         if (this.itemProject && this.current.handle() <= 0.001F) {
            this.load();
         }
      } else {
         this.handle(var3, var4);
         float var6 = process(this.current.handle());
         if (var6 <= 0.001F && this.current.compute() <= 0.0F) {
            if (this.itemProject) {
               this.load();
            }
         } else {
            float var7 = this.onTick();
            if (this.animator == null || Math.abs(this.animator.valueBlock().resolve() - var7) > 0.001F) {
               this.animator = KeybindEditorRenderer.handle(this.pointEncode, this.pending, this.previous, var7);
               this.select();
            }

            if (this.target != null) {
               this.target.apply();
            }

            String var8;
            if (this.vectorMatch) {
               var8 = "Press a key";
            } else {
               int var9 = this.pointEncode.check();
               if (var9 == -1) {
                  var8 = "None";
               } else {
                  var8 = process(var9);
               }
            }

            this.source = KeybindEditorRenderer.handle(this.animator, var1, var8);
            boolean var21 = this.source.handle(this.profileDraw, this.vectorPerform);
            boolean var10 = this.animator.toggleButton().handle(this.profileDraw, this.vectorPerform);
            boolean var11 = this.animator.holdButton().handle(this.profileDraw, this.vectorPerform);
            this.handle(var21, var10, var11);
            float var12 = this.active.handle();
            float var13 = this.mode.handle();
            float var14 = this.selection.handle();
            boolean var15 = this.responseCompute && System.nanoTime() - this.providerFetch <= 1200000000L;
            String var16 = "";
            if (var15) {
               var16 = "";
            }

            float var17 = this.enabled.handle();
            float var18 = this.renderer.handle();
            float var19 = this.handler.handle() * var5;
            KeybindEditorRenderer.KeyValueEntry var20 = new KeybindEditorRenderer.KeyValueEntry(
               var6,
               var19,
               this.vectorMatch,
               var21,
               var10,
               var11,
               var12,
               var13,
               var14,
               var17,
               var18,
               this.pointEncode.onTick(),
               var8,
               var16,
               this.animator.valueBlock().resolve(),
               this.animator.valueLabelBaseline(),
               this.source
            );
            KeybindEditorRenderer.handle(var1, var2, this.pointEncode, this.animator, var20);
            if (this.target != null) {
               this.target.handle(var1, var6, 1.0F);
               this.target.process(var1, var6, 1.0F);
            }

            if (!var15) {
               this.responseCompute = false;
            }
         }
      }
   }

   public synchronized void handle(double var1, double var3) {
      this.profileDraw = var1;
      this.vectorPerform = var3;
      if (this.target != null) {
         this.target.handle(var1, var3);
      }
   }

   public synchronized void process() {
      if (this.pointEncode != null || !(this.current.handle() <= 0.001F)) {
         if (this.pointEncode != null) {
            this.execute();
         }

         this.encodePoint();
         this.handle(false, false, false);
         this.handler.compute(0.0F);
         this.current.compute(0.0F);
         this.itemProject = true;
      }
   }

   public synchronized void compute() {
      if (this.pointEncode != null || !(this.current.handle() <= 0.001F)) {
         if (this.pointEncode != null) {
            this.execute();
         }

         this.encodePoint();
         this.handle(false, false, false);
         this.handler.process(0.0F);
         this.current.process(0.0F);
         this.load();
      }
   }

   public synchronized boolean resolve() {
      return this.pointEncode != null ? true : this.current.handle() > 0.001F;
   }

   public synchronized boolean update() {
      return this.pointEncode != null;
   }

   public synchronized KeybindEditorRenderer.DataRecord apply() {
      return this.animator;
   }

   public synchronized KeybindEditorRenderer.KeyValueEntry handle(float var1) {
      if (this.pointEncode != null && this.animator != null) {
         float var2 = process(this.current.handle());
         boolean var3 = this.source.handle(this.profileDraw, this.vectorPerform);
         boolean var4 = this.animator.toggleButton().handle(this.profileDraw, this.vectorPerform);
         boolean var5 = this.animator.holdButton().handle(this.profileDraw, this.vectorPerform);
         float var6 = this.active.handle();
         float var7 = this.mode.handle();
         float var8 = this.selection.handle();
         String var9;
         if (this.vectorMatch) {
            var9 = "Press a key";
         } else {
            int var10 = this.pointEncode.check();
            if (var10 == -1) {
               var9 = "None";
            } else {
               var9 = process(var10);
            }
         }

         boolean var15 = this.responseCompute && System.nanoTime() - this.providerFetch <= 1200000000L;
         String var11 = "";
         if (var15) {
            var11 = "";
         }

         float var12 = this.enabled.handle();
         float var13 = this.renderer.handle();
         float var14 = this.handler.handle() * var1;
         return new KeybindEditorRenderer.KeyValueEntry(
            var2,
            var14,
            this.vectorMatch,
            var3,
            var4,
            var5,
            var6,
            var7,
            var8,
            var12,
            var13,
            this.pointEncode.onTick(),
            var9,
            var11,
            this.animator.valueBlock().resolve(),
            this.animator.valueLabelBaseline(),
            this.source
         );
      } else {
         return null;
      }
   }

   public synchronized void execute() {
      if (this.pointEncode != null) {
         if (this.pointEncode.drawAnimation()) {
            KeybindPresets var1 = KeybindPresets.handle();
            if (this.pointEncode.select()) {
               Module var2 = this.pointEncode.process();
               if (var2 != null) {
                  var1.handle(var2, this.pointEncode.check(), this.pointEncode.onTick());
               }
            } else if (this.pointEncode.refresh()) {
               Module var5 = this.pointEncode.process();
               Setting var3 = this.pointEncode.compute();
               if (var5 != null && var3 != null) {
                  if (this.pointEncode.fetch()) {
                     var1.handle(var5.displayName, var3.instance);
                  } else {
                     Object var4 = this.pointEncode.prepare();
                     if (var4 != null) {
                        update(var3, var4);
                        var1.handle(var5, var3, this.pointEncode.onTick(), this.pointEncode.check(), var4);
                     }
                  }
               }
            }

            this.pointEncode.save();
         }
      }
   }

   private boolean render() {
      return this.pointEncode != null ? true : this.current.handle() > 0.001F && this.animator != null;
   }

   private void handle(int var1, int var2) {
      if (this.pointEncode != null && this.animator != null) {
         float var3 = this.pending;
         float var4 = this.previous;
         this.matrixBlend = this.save();
         this.handle(this.animator.bounds().compute(), this.animator.bounds().resolve(), var1, var2);
         if (this.pending != var3 || this.previous != var4) {
            this.animator = KeybindEditorRenderer.handle(this.pointEncode, this.pending, this.previous, this.onTick());
            this.source = this.animator.field();
            this.select();
         }
      }
   }

   private void tick() {
      this.active.compute(0.0F);
      this.mode.compute(0.0F);
      this.selection.compute(0.0F);
      this.handler.compute(0.0F);
      this.active.process(0.0F);
      this.mode.process(0.0F);
      this.selection.process(0.0F);
      this.handler.process(0.0F);
   }

   private void handle(boolean var1, boolean var2, boolean var3) {
      this.active.compute(var1 ? 1.0F : 0.0F);
      this.mode.compute(var2 ? 1.0F : 0.0F);
      this.selection.compute(var3 ? 1.0F : 0.0F);
   }

   private void drawAnimation() {
      this.vectorMatch = true;
      this.responseCompute = false;
      this.providerFetch = 0L;
      KeybindPresets.handle().handle(true);
   }

   private void encodePoint() {
      if (this.vectorMatch) {
         this.vectorMatch = false;
         KeybindPresets.handle().handle(false);
      }
   }

   private void handle(KeybindMode var1) {
      if (this.pointEncode != null && var1 != null) {
         this.pointEncode.handle(var1);
         this.animate();
         this.execute();
      }
   }

   private void animate() {
      if (this.pointEncode != null) {
         this.enabled.compute(this.pointEncode.onTick() == KeybindMode.TOGGLE ? 1.0F : 0.0F);
         this.renderer.compute(this.pointEncode.onTick() == KeybindMode.HOLD ? 1.0F : 0.0F);
      }
   }

   private boolean handle(int var1) {
      return false;
   }

   private void load() {
      this.pointEncode = null;
      this.animator = null;
      this.source = new KeybindEditorRenderer.State(0.0F, 0.0F, 0.0F, 0.0F);
      this.target = null;
      this.itemProject = false;
      this.responseCompute = false;
      this.providerFetch = 0L;
      this.latest = Float.NaN;
      this.summary = Float.NaN;
      this.matrixBlend = 1.0F;
      this.tick();
   }

   private float save() {
      float var1 = 1.0F;
      if (!Float.isFinite(var1)) {
         return 1.0F;
      } else {
         return var1 <= 0.001F ? 1.0F : var1;
      }
   }

   private static float handle(double var0) {
      if (!Double.isFinite(var0)) {
         return Float.NaN;
      } else if (var0 > Float.MAX_VALUE) {
         return Float.MAX_VALUE;
      } else {
         return var0 < -Float.MAX_VALUE ? -Float.MAX_VALUE : (float)var0;
      }
   }

   private static float handle(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   private static float process(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         return var0 >= 1.0F ? 1.0F : var0;
      }
   }

   private static String process(int var0) {
      if (var0 == -1) {
         return "None";
      } else if (var0 >= 65 && var0 <= 90) {
         return String.valueOf((char)(65 + (var0 - 65)));
      } else {
         return var0 >= 48 && var0 <= 57 ? String.valueOf((char)(48 + (var0 - 48))) : "Key " + var0;
      }
   }

   private static void update(Setting var0, Object var1) {
      if (var0 instanceof BooleanSetting && var1 instanceof Boolean) {
         ((BooleanSetting)var0).process((Boolean)var1);
      } else if (var0 instanceof ModeSetting && var1 instanceof String) {
         ((ModeSetting)var0).state = (String)var1;
         if (((ModeSetting)var0).config != null && ((ModeSetting)var0).config.contains((String)var1)) {
            ((ModeSetting)var0).current = ((ModeSetting)var0).config.indexOf((String)var1);
         }
      } else if (var0 instanceof NumberSetting && var1 instanceof Number) {
         double var4 = ((Number)var1).doubleValue();
         ((NumberSetting)var0).config = (float)Math.max(((NumberSetting)var0).state, Math.min(((NumberSetting)var0).cache, var4));
      } else if (var0 instanceof MultiSelectSetting && var1 instanceof Collection) {
         ((MultiSelectSetting)var0).output = new ArrayList<>((Collection<? extends String>)var1);
      } else if (var0 instanceof ColorSetting && var1 instanceof ColorVector var2) {
         ColorSetting var3 = (ColorSetting)var0;
         var3.handle(var2.handle());
         var3.renderer = var2.process();
         var3.handler = var2.compute();
         var3.animationDraw = var2.resolve();
      }
   }

   static final class ScreenState {
      static final KeybindSelectorScreen instance = new KeybindSelectorScreen(handle());

      private ScreenState() {
      }

      private static SpringFloat handle() {
         SpringFloat var0 = new SpringFloat(AnimationClock.handle(), KeybindSelectorScreen.instance, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
         var0.handle(PopupAnimationCurve.context);
         return var0;
      }
   }
}
