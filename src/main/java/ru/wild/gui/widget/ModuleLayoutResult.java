package ru.wild.gui.widget;

import java.util.List;
public final class ModuleLayoutResult {
   private final List<ModulePlacement> instance;
   private final float data;

   public static ModuleLayoutResult handle() {
      return new ModuleLayoutResult(List.of(), 0.0F);
   }
   public ModuleLayoutResult(List<ModulePlacement> var1, float var2) {
      this.instance = var1;
      this.data = var2;
   }
   public List<ModulePlacement> process() {
      return this.instance;
   }
   public float compute() {
      return this.data;
   }
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ModuleLayoutResult var2)) {
         return false;
      } else {
         if (Float.compare(this.compute(), var2.compute()) != 0) {
            return false;
         }

         List var3 = this.process();
         List var4 = var2.process();
         return var3 == null ? var4 == null : var3.equals(var4);
      }
   }
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Float.floatToIntBits(this.compute());
      List var3 = this.process();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
   @Override
   public String toString() {
      return "ModuleLayoutResult(placements=" + this.process() + ", maxScroll=" + this.compute() + ")";
   }
}
