package ru.wild.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AnimationClock {
   public static final float instance = 240.0F;
   public static final float data = 0.004166667F;
   public static final int context = 60;
   private static final float config = 1.0E-4F;
   private static final float state = 0.016666668F;
   private static final float cache = 0.1F;
   private static final AnimationClock output = new AnimationClock();
   private final Object current = new Object();
   private final List<AnimationClock.Predicate> active = new ArrayList<>();
   private long mode = System.nanoTime();
   private float selection = 0.016666668F;
   private long enabled = 0L;

   private AnimationClock() {
   }

   public static AnimationClock handle() {
      return output;
   }

   public void process() {
      long var1 = System.nanoTime();
      long var3 = var1 - this.mode;
      this.mode = var1;
      if (var3 < 0L) {
         var3 = 0L;
      }

      float var5 = (float)var3 / 1.0E9F;
      if (var5 < 1.0E-4F) {
         var5 = 1.0E-4F;
      } else if (var5 > 0.1F) {
         var5 = 0.016666668F;
      }

      this.selection = var5;
      this.enabled++;
      synchronized (this.current) {
         if (!this.active.isEmpty()) {
            Iterator var7 = this.active.iterator();

            while (var7.hasNext()) {
               AnimationClock.Predicate var8 = (AnimationClock.Predicate)var7.next();
               boolean var9 = var8.handle(var5);
               if (!var9) {
                  var7.remove();
               }
            }
         }
      }
   }

   public float compute() {
      return this.selection;
   }

   public long resolve() {
      return this.enabled;
   }

   public void update() {
      this.mode = System.nanoTime();
      this.selection = 0.016666668F;
      this.enabled++;
   }

   public void handle(AnimationClock.Predicate var1) {
      if (var1 != null) {
         synchronized (this.current) {
            if (!this.active.contains(var1)) {
               this.active.add(var1);
            }
         }
      }
   }

   public void process(AnimationClock.Predicate var1) {
      if (var1 != null) {
         synchronized (this.current) {
            this.active.remove(var1);
         }
      }
   }

   public interface Predicate {
      boolean handle(float var1);
   }
}
