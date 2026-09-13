package ru.wild.gui.widget;

import java.util.Objects;
import org.wild.module.api.Module;
import ru.wild.api.setting.KeybindTargetModel;
import ru.wild.api.setting.SettingValue;
import ru.wild.util.render.RoundedRectRenderer;

public abstract class NewValueWidget<T> implements KeybindEditor {
   private static final SettingPopupHost data = (var0, var1, var2, var4, var6) -> {};
   protected static final String instance = "New Value";
   private final KeybindTargetModel context;
   private final SettingEditor config;
   private KeybindEditorRenderer.State state = new KeybindEditorRenderer.State(0.0F, 0.0F, 0.0F, 0.0F);

   NewValueWidget(KeybindTargetModel var1, SettingEditor var2) {
      this.context = Objects.requireNonNull(var1, "model");
      this.config = Objects.requireNonNull(var2, "widget");
   }

   protected final KeybindTargetModel handle() {
      return this.context;
   }

   protected final SettingEditor process() {
      return this.config;
   }

   protected final SettingPopupHost compute() {
      return data;
   }

   protected static SettingPopupHost resolve() {
      return data;
   }

   protected static Module handle(KeybindTargetModel var0) {
      Module var1 = var0.process();
      if (var1 == null) {
         throw new IllegalStateException("Bind popup model is missing module context");
      } else {
         return var1;
      }
   }

   @Override
   public void handle(KeybindEditorRenderer.State var1) {
      Objects.requireNonNull(var1, "area");
      this.state = var1;
      this.config.handle(var1.handle(), var1.process(), var1.compute());
   }

   @Override
   public float update() {
      return this.config.process();
   }

   @Override
   public void apply() {
      this.config.handle();
   }

   @Override
   public void handle(double var1, double var3) {
      this.config.handle(var1, var3);
   }

   @Override
   public boolean handle(double var1, double var3, int var5) {
      if (this.config.update()) {
         return this.config.process(var1, var3, var5) ? true : true;
      } else {
         return !this.state.handle(var1, var3) ? false : this.config.handle(var1, var3, var5);
      }
   }

   @Override
   public boolean handle(double var1, double var3, double var5, double var7) {
      if (this.config.update()) {
         return this.config.handle(var1, var3, var5, var7) ? true : true;
      } else {
         return !this.state.handle(var1, var3) ? false : this.config.process(var1, var3, var5, var7);
      }
   }

   @Override
   public void handle(RoundedRectRenderer var1, float var2, float var3) {
      this.config.handle(var1, var2, var3, 0.0F);
   }

   @Override
   public void process(RoundedRectRenderer var1, float var2, float var3) {
      this.config.handle(var1, var2, var3);
   }

   @Override
   public boolean execute() {
      return this.config.update();
   }

   protected static <V> SettingValue<V> handle(final KeybindTargetModel var0, final V var1, final NewValueWidget.Callback<V> var2) {
      Objects.requireNonNull(var0, "model");
      Objects.requireNonNull(var2, "adapter");
      return new SettingValue<V>() {
         @Override
         public V handle() {
            return (V)var2.handle(var0);
         }

         @Override
         public void handle(V var1x) {
            var2.handle(var0, var1x);
         }

         @Override
         public V process() {
            return (V)var1;
         }

         @Override
         public void compute() {
         }
      };
   }

   @FunctionalInterface
   protected interface Callback<V> {
      V handle(KeybindTargetModel var1);

      default void handle(KeybindTargetModel var1, V var2) {
         var1.process(var2);
      }
   }
}
