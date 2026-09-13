package ru.wild.render.shader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ShaderGraphBlock {
   private final String instance;
   private final String data;
   private float context;
   private float config;
   private float state;
   private final Map<String, Float> cache = new LinkedHashMap<>();
   private final Map<String, String> output = new LinkedHashMap<>();

   public ShaderGraphBlock(String var1, String var2, float var3, float var4) {
      this.instance = Objects.requireNonNull(var1, "id");
      this.data = Objects.requireNonNull(var2, "kind");
      this.context = var3;
      this.config = var4;
      this.state = 188.0F;
   }

   public String handle() {
      return this.instance;
   }

   public String process() {
      return this.data;
   }

   public float compute() {
      return this.context;
   }

   public float resolve() {
      return this.config;
   }

   public void handle(float var1, float var2) {
      this.context = var1;
      this.config = var2;
   }

   public float update() {
      return this.state;
   }

   public void handle(float var1) {
      this.state = Math.max(132.0F, var1);
   }

   public Map<String, Float> apply() {
      return this.cache;
   }

   public Map<String, String> execute() {
      return this.output;
   }

   public float handle(String var1, float var2) {
      Float var3 = this.cache.get(var1);
      return var3 != null && Float.isFinite(var3) ? var3 : var2;
   }

   public void process(String var1, float var2) {
      if (var1 != null && Float.isFinite(var2)) {
         this.cache.put(var1, var2);
      }
   }

   public String handle(String var1, String var2) {
      String var3 = this.output.get(var1);
      return var3 != null && !var3.isBlank() ? var3 : var2;
   }

   public void process(String var1, String var2) {
      if (var1 != null) {
         this.output.put(var1, var2 == null ? "" : var2);
      }
   }
}
