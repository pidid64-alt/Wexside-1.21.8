package ru.wild.gui.widget;
import org.wild.module.api.Module;

public final class ModulePlacement {
   private final Module instance;
   private final float data;
   private final float context;
   private final float config;
   private final float state;
   private final float cache;
   public ModulePlacement(Module var1, float var2, float var3, float var4, float var5, float var6) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5;
      this.cache = var6;
   }
   public Module handle() {
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
      return this.state;
   }
   public float apply() {
      return this.cache;
   }
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ModulePlacement var2)) {
         return false;
      } else {
         if (Float.compare(this.process(), var2.process()) != 0) {
            return false;
         }

         if (Float.compare(this.compute(), var2.compute()) != 0) {
            return false;
         }

         if (Float.compare(this.resolve(), var2.resolve()) != 0) {
            return false;
         }

         if (Float.compare(this.update(), var2.update()) != 0) {
            return false;
         }

         if (Float.compare(this.apply(), var2.apply()) != 0) {
            return false;
         }

         Module var3 = this.handle();
         Module var4 = var2.handle();
         return var3 == null ? var4 == null : var3.equals(var4);
      }
   }
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Float.floatToIntBits(this.process());
      var2 = var2 * 59 + Float.floatToIntBits(this.compute());
      var2 = var2 * 59 + Float.floatToIntBits(this.resolve());
      var2 = var2 * 59 + Float.floatToIntBits(this.update());
      var2 = var2 * 59 + Float.floatToIntBits(this.apply());
      Module var3 = this.handle();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
   @Override
   public String toString() {
      return "ModulePlacement(module="
         + this.handle()
         + ", x="
         + this.process()
         + ", y="
         + this.compute()
         + ", width="
         + this.resolve()
         + ", height="
         + this.update()
         + ", settingsHeight="
         + this.apply()
         + ")";
   }
}
