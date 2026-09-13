package ru.wild.api.setting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.gui.hud.HudElementRenderer;
import ru.wild.gui.screen.ModernClickGuiRenderer;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.render.shader.ShaderTargetResolver;

public final class TargetSelectorRegistry {
   private static final TargetSelectorRegistry instance = new TargetSelectorRegistry();
   private final List<TargetSelectorRegistry.State> data = new CopyOnWriteArrayList<>();

   private TargetSelectorRegistry() {
      PresetManager.handle().handle(this::handle);
      PresetManager.handle().process(this::handle);
   }

   public static TargetSelectorRegistry handle() {
      return instance;
   }

   public synchronized void handle(Module var1, HudElementRenderer var2) {
      this.handle((Object)var1, var2);
   }

   public synchronized void handle(ResettableSettingGroup var1, HudElementRenderer var2) {
      this.handle((Object)var1, var2);
   }

   private void handle(Object var1, HudElementRenderer var2) {
      if (var1 != null && var2 != null) {
         this.compute(var1);
         TargetSelectorRegistry.State var3 = new TargetSelectorRegistry.State(var1, var2);
         this.data.add(var3);
         this.handle(var3);
      }
   }

   public synchronized void handle(Module var1) {
      this.handle((Object)var1);
   }

   public synchronized void handle(ResettableSettingGroup var1) {
      this.handle((Object)var1);
   }

   private void handle(Object var1) {
      if (var1 != null) {
         this.compute(var1);
         process();
      }
   }

   public void process(Module var1, HudElementRenderer var2) {
      this.process((Object)var1, var2);
   }

   public void process(ResettableSettingGroup var1, HudElementRenderer var2) {
      this.process((Object)var1, var2);
   }

   private void process(Object var1, HudElementRenderer var2) {
      if (var1 != null && var2 != null) {
         TargetSelectorRegistry.State var3 = this.process(var1);
         if (var3 != null) {
            this.handle(var3);
         }

         ArrayList var4 = new ArrayList();

         for (Setting var6 : resolve(var1)) {
            if (var6 != null && var6.context) {
               var4.add(var6);
            }
         }

         if (!var4.isEmpty()) {
            if (var2.update()) {
               String var7 = var2.resolve();
               if (var7 != null && !var7.isBlank() && !"None".equalsIgnoreCase(var7)) {
                  ShaderTargetResolver.handle(var7, var4);
               }
            } else {
               LivePreviewRenderer var8 = var2.compute();
               if (var8 != null) {
                  ShaderTargetResolver.handle(var8, var4);
               }
            }
         }
      }
   }

   private TargetSelectorRegistry.State process(Object var1) {
      for (TargetSelectorRegistry.State var3 : this.data) {
         Object var4 = var3.instance.get();
         if (var4 == var1) {
            return var3;
         }
      }

      return null;
   }

   private void handle(LivePreviewRenderer var1) {
      if (var1 != null) {
         for (TargetSelectorRegistry.State var3 : this.data) {
            HudElementRenderer var4 = var3.data;
            if (var4 != null && !var4.update() && var4.compute() == var1) {
               this.handle(var3);
            }
         }
      }
   }

   private void handle(String var1) {
      if (var1 != null) {
         String var2 = PresetManager.onTick(var1);

         for (TargetSelectorRegistry.State var4 : this.data) {
            HudElementRenderer var5 = var4.data;
            if (var5 != null && var5.update()) {
               String var6 = var5.resolve();
               if (var6 != null && var2.equals(PresetManager.onTick(var6))) {
                  this.handle(var4);
               }
            }
         }
      }
   }

   private synchronized void compute(Object var1) {
      for (TargetSelectorRegistry.State var3 : this.data) {
         Object var4 = var3.instance.get();
         if (var4 == null) {
            this.data.remove(var3);
         } else if (var4 == var1) {
            if (!var3.context.isEmpty()) {
               process(var4, var3.context);
            }

            this.data.remove(var3);
         }
      }
   }

   private synchronized void handle(TargetSelectorRegistry.State var1) {
      Object var2 = var1.instance.get();
      if (var2 == null) {
         this.data.remove(var1);
      } else {
         String var3;
         List<Setting> var4;
         if (var1.data.update()) {
            String var5 = var1.data.resolve();
            if (var5 == null || var5.isBlank() || "None".equalsIgnoreCase(var5)) {
               handle(var2, var1);
               var1.config = "";
               var1.state = "";
               return;
            }

            var3 = ShaderTargetResolver.compute(var5);
            var4 = ShaderTargetResolver.apply(var5);
            String var6 = "name:" + PresetManager.onTick(var5);
            if (Objects.equals(var1.config, var3) && Objects.equals(var1.state, var6)) {
               return;
            }

            var1.state = var6;
         } else {
            LivePreviewRenderer var7 = var1.data.compute();
            if (var7 == null) {
               handle(var2, var1);
               var1.config = "";
               var1.state = "";
               return;
            }

            var3 = ShaderTargetResolver.compute(var7);
            var4 = ShaderTargetResolver.apply(var7);
            String var9 = "target:" + var7.handle();
            if (Objects.equals(var1.config, var3) && Objects.equals(var1.state, var9)) {
               return;
            }

            var1.state = var9;
         }

         var1.config = var3 == null ? "" : var3;
         handle(var2, var1);
         if (var4 != null && !var4.isEmpty()) {
            for (Setting var10 : var4) {
               if (var10 != null) {
                  var10.context = true;
               }
            }

            var1.context.addAll(var4);
            handle(var2, var4);
            process();
         }
      }
   }

   private static void handle(Object var0, TargetSelectorRegistry.State var1) {
      if (!var1.context.isEmpty()) {
         process(var0, var1.context);
         var1.context.clear();
         process();
      }
   }

   private static List<Setting> resolve(Object var0) {
      if (var0 instanceof Module var2) {
         return var2.select();
      } else {
         return var0 instanceof ResettableSettingGroup var1 ? var1.handle() : List.of();
      }
   }

   private static void handle(Object var0, List<Setting> var1) {
      if (var0 instanceof Module var2) {
         var2.handle(var1.toArray(new Setting[0]));
      } else if (var0 instanceof ResettableSettingGroup var3) {
         var3.handle(var1.toArray(new Setting[0]));
      }
   }

   private static void process(Object var0, List<Setting> var1) {
      if (var0 instanceof Module var2) {
         var2.handle(var1);
      } else if (var0 instanceof ResettableSettingGroup var3) {
         var3.handle(var1);
      }
   }

   private static void process() {
      try {
         if (WildClient.instance != null && WildClient.instance.selection != null) {
            ModernClickGuiRenderer var0 = WildClient.instance.selection.check();
            if (var0 != null) {
               var0.apply();
            }
         }
      } catch (Throwable var1) {
      }
   }

   static final class State {
      final WeakReference<Object> instance;
      final HudElementRenderer data;
      final List<Setting> context = new ArrayList<>();
      String config = "";
      String state = "";

      State(Object var1, HudElementRenderer var2) {
         this.instance = new WeakReference<>(var1);
         this.data = var2;
      }
   }
}
