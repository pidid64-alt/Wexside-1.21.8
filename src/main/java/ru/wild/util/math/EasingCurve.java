package ru.wild.util.math;
public enum EasingCurve {
   LINEAR(Easings.cache),
   QUAD_OUT(Easings.current),
   CUBIC_OUT(Easings.selection),
   QUART_OUT(Easings.handler),
   QUINT_OUT(Easings.animator),
   SINE_OUT(Easings.pending),
   CIRC_OUT(Easings.summary),
   ELASTIC_OUT(Easings.itemProject),
   EXPO_OUT(Easings.profileDraw),
   BACK_OUT(Easings.serverRead),
   BOUNCE_OUT(Easings.frameCheck);

   private final DoubleEasing instance;

   @Override
   public String toString() {
      String var1 = this.name().toLowerCase();
      String[] var2 = var1.split("_");
      StringBuilder var3 = new StringBuilder();

      for (String var7 : var2) {
         var3.append(Character.toUpperCase(var7.charAt(0))).append(var7.substring(1)).append(" ");
      }

      return var3.toString().trim();
   }
   public DoubleEasing handle() {
      return this.instance;
   }
   EasingCurve(DoubleEasing var3) {
      this.instance = var3;
   }
}
