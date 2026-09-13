package ru.wild.gui.theme;

public final class MotionSpringPresets {
   public static final MotionSpringPresets.DataRecord instance = new MotionSpringPresets.DataRecord(1.9F, 0.94F, 0.02F);
   public static final MotionSpringPresets.DataRecord data = new MotionSpringPresets.DataRecord(2.6F, 1.0F, 5.0E-4F);
   public static final MotionSpringPresets.DataRecord context = new MotionSpringPresets.DataRecord(1.1F, 0.92F, 5.0E-4F);
   public static final MotionSpringPresets.DataRecord config = new MotionSpringPresets.DataRecord(4.2F, 0.9F, 6.0E-4F);
   public static final MotionSpringPresets.DataRecord state = new MotionSpringPresets.DataRecord(3.0F, 0.9F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord cache = new MotionSpringPresets.DataRecord(4.4F, 0.9F, 6.0E-4F);
   public static final MotionSpringPresets.DataRecord output = new MotionSpringPresets.DataRecord(7.6F, 0.5F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord current = new MotionSpringPresets.DataRecord(3.6F, 0.55F, 6.0E-4F);
   public static final MotionSpringPresets.DataRecord active = new MotionSpringPresets.DataRecord(4.0F, 0.62F, 6.0E-4F);
   public static final MotionSpringPresets.DataRecord mode = new MotionSpringPresets.DataRecord(2.05F, 0.6F, 6.0E-4F);
   public static final MotionSpringPresets.DataRecord selection = new MotionSpringPresets.DataRecord(1.6F, 1.0F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord enabled = new MotionSpringPresets.DataRecord(2.8F, 1.0F, 5.0E-4F);
   public static final MotionSpringPresets.DataRecord renderer = new MotionSpringPresets.DataRecord(3.4F, 0.86F, 6.0E-4F);
   public static final MotionSpringPresets.DataRecord handler = new MotionSpringPresets.DataRecord(5.2F, 0.84F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord animationDraw = new MotionSpringPresets.DataRecord(4.0F, 0.8F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord pointEncode = new MotionSpringPresets.DataRecord(2.6F, 0.82F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord animator = new MotionSpringPresets.DataRecord(0.9F, 1.0F, 0.0015F);
   public static final MotionSpringPresets.DataRecord source = new MotionSpringPresets.DataRecord(0.7F, 1.0F, 0.0015F);
   public static final MotionSpringPresets.DataRecord target = new MotionSpringPresets.DataRecord(0.8F, 1.0F, 0.0015F);
   public static final MotionSpringPresets.DataRecord pending = new MotionSpringPresets.DataRecord(1.4F, 1.0F, 0.0015F);
   public static final MotionSpringPresets.DataRecord previous = new MotionSpringPresets.DataRecord(3.6F, 0.95F, 4.0E-4F);
   public static final MotionSpringPresets.DataRecord latest = new MotionSpringPresets.DataRecord(1.6F, 0.85F, 6.0E-4F);
   public static final MotionSpringPresets.DataRecord summary = new MotionSpringPresets.DataRecord(1.35F, 0.92F, 6.0E-4F);
   public static final MotionSpringPresets.DataRecord matrixBlend = new MotionSpringPresets.DataRecord(4.6F, 0.78F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord vectorMatch = new MotionSpringPresets.DataRecord(5.4F, 0.86F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord itemProject = new MotionSpringPresets.DataRecord(4.8F, 0.86F, 8.0E-4F);
   public static final MotionSpringPresets.DataRecord responseCompute = new MotionSpringPresets.DataRecord(5.0F, 0.62F, 8.0E-4F);

   private MotionSpringPresets() {
   }

   public record DataRecord(float frequencyHz, float dampingRatio, float settleDistance) {
   }
}
