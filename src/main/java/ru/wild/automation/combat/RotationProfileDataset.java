package ru.wild.automation.combat;

import java.util.ArrayList;
import java.util.List;

public final class RotationProfileDataset {
   public int instance = 1;
   public long data;
   public long context;
   public String config = "rotation_lab";
   public String state = "RotationLab";
   public List<RotationProfileDataset.CacheEntry> cache = new ArrayList<>();

   public static final class CacheEntry {
      public String instance = "Mixed";
      public long data;
      public float context;
      public float config;
      public float state;
      public float cache;
      public float output;
      public float current;
      public int active;
      public int mode;
      public float selection;
      public List<RotationProfileDataset.PrimaryCacheEntry> enabled = new ArrayList<>();

      public float handle() {
         float var1 = Math.abs(Math.abs(this.state) > 0.001F ? this.state : this.context);
         float var2 = Math.abs(Math.abs(this.cache) > 0.001F ? this.cache : this.config);
         return (float)Math.hypot(var1, var2);
      }
   }

   public static final class PrimaryCacheEntry {
      public int instance;
      public float data;
      public float context;
      public float config;
      public float state;
      public float cache;
      public float output;
      public float current;
   }
}
