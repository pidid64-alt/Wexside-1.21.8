package ru.wild.render.shader;

import java.util.Objects;

public final class ShaderGraphLink {
   private final String instance;
   private final String data;
   private final String context;
   private final String config;

   public ShaderGraphLink(String var1, String var2, String var3, String var4) {
      this.instance = Objects.requireNonNull(var1, "fromNodeId");
      this.data = Objects.requireNonNull(var2, "fromPinId");
      this.context = Objects.requireNonNull(var3, "toNodeId");
      this.config = Objects.requireNonNull(var4, "toPinId");
   }

   public String handle() {
      return this.instance;
   }

   public String process() {
      return this.data;
   }

   public String compute() {
      return this.context;
   }

   public String resolve() {
      return this.config;
   }

   public String update() {
      return this.instance + "." + this.data;
   }

   public String apply() {
      return this.context + "." + this.config;
   }
}
