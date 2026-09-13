package ru.wild.render.shader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import ru.wild.gui.theme.ThemeSourceMetadata;

public final class ShaderGraph {
   private final Map<String, ShaderGraphBlock> instance = new LinkedHashMap<>();
   private final List<ShaderGraphLink> data = new ArrayList<>();
   private final ThemeSourceMetadata context = new ThemeSourceMetadata();
   private int config;
   private String state = "preview";

   public ThemeSourceMetadata handle() {
      return this.context;
   }

   public void handle(ThemeSourceMetadata var1) {
      this.context.handle(var1);
      this.config++;
   }

   public String process() {
      return this.state;
   }

   public void handle(String var1) {
      if (var1 != null && !var1.isBlank() && !this.state.equals(var1)) {
         this.state = var1;
         this.config++;
      }
   }

   public ShaderGraphBlock handle(String var1, float var2, float var3, ShaderNodeRegistry var4) {
      String var5 = apply(var1);
      ShaderGraphBlock var6 = new ShaderGraphBlock(var5, var1, var2, var3);
      ShaderNodeDefinition var7 = var4.handle(var1);
      if (var7 != null) {
         var6.handle(var7.resolve());
      }

      this.instance.put(var5, var6);
      this.config++;
      return var6;
   }

   public void handle(ShaderGraphBlock var1, ShaderNodeRegistry var2) {
      Objects.requireNonNull(var1, "node");
      ShaderNodeDefinition var3 = var2.handle(var1.process());
      if (var3 != null) {
         var1.handle(var3.resolve());
      }

      this.instance.put(var1.handle(), var1);
      this.config++;
   }

   public boolean process(String var1) {
      ShaderGraphBlock var2 = this.instance.remove(var1);
      if (var2 == null) {
         return false;
      }

      this.data.removeIf(var1x -> var1x.handle().equals(var1) || var1x.compute().equals(var1));
      this.config++;
      return true;
   }

   public boolean handle(String var1, String var2, String var3, String var4, ShaderNodeRegistry var5) {
      ShaderGraphBlock var6 = this.instance.get(var1);
      ShaderGraphBlock var7 = this.instance.get(var3);
      if (var6 != null && var7 != null && var6 != var7) {
         ShaderNodeDefinition var8 = var5.handle(var6.process());
         ShaderNodeDefinition var9 = var5.handle(var7.process());
         if (var8 != null && var9 != null) {
            ShaderPinDefinition var10 = var8.process(var2);
            ShaderPinDefinition var11 = var9.handle(var4);
            if (var10 == null || var11 == null || var10.type() != var11.type()) {
               return false;
            }

            if (this.compute(var1, var3)) {
               return false;
            }

            this.data.removeIf(var2x -> var2x.compute().equals(var3) && var2x.resolve().equals(var4));
            this.data.add(new ShaderGraphLink(var1, var2, var3, var4));
            this.config++;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean handle(String var1, String var2) {
      boolean var3 = this.data.removeIf(var2x -> var2x.compute().equals(var1) && var2x.resolve().equals(var2));
      if (var3) {
         this.config++;
      }

      return var3;
   }

   public Collection<ShaderGraphBlock> compute() {
      return this.instance.values();
   }

   public List<ShaderGraphLink> resolve() {
      return this.data;
   }

   public ShaderGraphBlock compute(String var1) {
      return this.instance.get(var1);
   }

   public ShaderGraphLink process(String var1, String var2) {
      for (ShaderGraphLink var4 : this.data) {
         if (var4.compute().equals(var1) && var4.resolve().equals(var2)) {
            return var4;
         }
      }

      return null;
   }

   public List<ShaderGraphLink> resolve(String var1) {
      ArrayList var2 = new ArrayList();

      for (ShaderGraphLink var4 : this.data) {
         if (var4.handle().equals(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public ShaderGraph update(String var1) {
      ShaderGraph var2 = new ShaderGraph();
      var2.state = this.state;
      var2.context.handle(this.context);
      if (var1 != null && this.instance.containsKey(var1)) {
         LinkedHashSet<String> var3 = new LinkedHashSet<>();
         ArrayDeque<String> var4 = new ArrayDeque<>();
         var4.push(var1);

         while (!var4.isEmpty()) {
            String var5 = (String)var4.pop();
            if (var3.add(var5)) {
               for (ShaderGraphLink var7 : this.data) {
                  if (var7.compute().equals(var5)) {
                     var4.push(var7.handle());
                  }
               }
            }
         }

         for (String var11 : var3) {
            ShaderGraphBlock var13 = this.instance.get(var11);
            if (var13 != null) {
               ShaderGraphBlock var8 = new ShaderGraphBlock(var13.handle(), var13.process(), var13.compute(), var13.resolve());
               var8.handle(var13.update());
               var8.apply().putAll(var13.apply());
               var8.execute().putAll(var13.execute());
               var2.instance.put(var8.handle(), var8);
            }
         }

         for (ShaderGraphLink var12 : this.data) {
            if (var3.contains(var12.handle()) && var3.contains(var12.compute())) {
               var2.data.add(new ShaderGraphLink(var12.handle(), var12.process(), var12.compute(), var12.resolve()));
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public int update() {
      return this.config;
   }

   public void apply() {
      this.config++;
   }

   public void execute() {
      this.instance.clear();
      this.data.clear();
      this.config++;
   }

   public boolean compute(String var1, String var2) {
      if (var1.equals(var2)) {
         return true;
      }

      LinkedHashSet<String> var3 = new LinkedHashSet<>();
      ArrayDeque<String> var4 = new ArrayDeque<>();
      var4.push(var2);

      while (!var4.isEmpty()) {
         String var5 = (String)var4.pop();
         if (var3.add(var5)) {
            if (var5.equals(var1)) {
               return true;
            }

            for (ShaderGraphLink var7 : this.data) {
               if (var7.handle().equals(var5)) {
                  var4.push(var7.compute());
               }
            }
         }
      }

      return false;
   }

   private static String apply(String var0) {
      String var1 = var0 != null && !var0.isBlank() ? var0.toLowerCase().replaceAll("[^a-z0-9]+", "_") : "node";
      return var1 + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
   }
}
