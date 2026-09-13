package ru.wild.gui.theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import ru.wild.render.shader.ShaderBuildResult;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderGraphCompiler;
import ru.wild.render.shader.ShaderParameter;
import ru.wild.render.shader.ThemeShaderProgramCache;

public final class PresetManager {
   private static final PresetManager instance = new PresetManager();
   private final Map<LivePreviewRenderer, ShaderGraph> data = new EnumMap<>(LivePreviewRenderer.class);
   private final Map<LivePreviewRenderer, ShaderBuildResult> context = new EnumMap<>(LivePreviewRenderer.class);
   private final Map<LivePreviewRenderer, String> config = new EnumMap<>(LivePreviewRenderer.class);
   private final Map<String, ShaderGraph> state = new LinkedHashMap<>();
   private final Map<String, ShaderBuildResult> cache = new LinkedHashMap<>();
   private final Map<String, String> output = new LinkedHashMap<>();
   private final Map<String, PresetManager.PrimaryMode> current = new LinkedHashMap<>();
   private final Map<String, PresetManager.Mode> active = new LinkedHashMap<>();
   private final Map<LivePreviewRenderer, PresetManager.Mode> mode = new EnumMap<>(LivePreviewRenderer.class);
   private final Map<LivePreviewRenderer, Map<String, float[]>> selection = new EnumMap<>(LivePreviewRenderer.class);
   private final Map<String, Map<String, float[]>> enabled = new LinkedHashMap<>();
   private final List<Consumer<LivePreviewRenderer>> renderer = new CopyOnWriteArrayList<>();
   private final List<Consumer<String>> handler = new CopyOnWriteArrayList<>();
   private ShaderGraphCompiler animationDraw;

   private PresetManager() {
   }

   public static PresetManager handle() {
      return instance;
   }

   public synchronized void handle(ShaderGraphCompiler var1) {
      this.animationDraw = var1;
   }

   public synchronized void handle(LivePreviewRenderer var1, ShaderGraph var2, ShaderBuildResult var3) {
      if (var1 != null && var2 != null && var3 != null) {
         this.data.put(var1, var2);
         this.context.put(var1, var3);
         this.mode.put(var1, var3.ok() ? PresetManager.Mode.SAVED : PresetManager.Mode.FAILED);
         handle(this.selection.computeIfAbsent(var1, var0 -> new LinkedHashMap<>()), var3);

         try {
            this.config.put(var1, ThemeKeys.handle(var2));
         } catch (Throwable var5) {
         }

         this.select(var1);
      }
   }

   public synchronized void handle(String var1, ShaderGraph var2, ShaderBuildResult var3) {
      this.handle(var1, var2, var3, handle(var2));
   }

   public synchronized void handle(String var1, ShaderGraph var2, ShaderBuildResult var3, PresetManager.PrimaryMode var4) {
      String var5 = onTick(var1);
      if (!var5.isBlank() && var2 != null && var3 != null) {
         this.state.put(var5, var2);
         this.cache.put(var5, var3);
         this.current.put(var5, var4 == null ? PresetManager.PrimaryMode.USER : var4);
         this.active.put(var5, var3.ok() ? PresetManager.Mode.SAVED : PresetManager.Mode.FAILED);
         handle(this.enabled.computeIfAbsent(var5, var0 -> new LinkedHashMap<>()), var3);

         try {
            this.output.put(var5, ThemeKeys.handle(var2));
         } catch (Throwable var7) {
         }

         this.select(var5);
      }
   }

   public void handle(Consumer<LivePreviewRenderer> var1) {
      if (var1 != null) {
         this.renderer.add(var1);
      }
   }

   public void process(Consumer<String> var1) {
      if (var1 != null) {
         this.handler.add(var1);
      }
   }

   private void select(LivePreviewRenderer var1) {
      for (Consumer var3 : this.renderer) {
         try {
            var3.accept(var1);
         } catch (Throwable var5) {
         }
      }
   }

   private void select(String var1) {
      for (Consumer var3 : this.handler) {
         try {
            var3.accept(var1);
         } catch (Throwable var5) {
         }
      }
   }

   public synchronized void handle(LivePreviewRenderer var1) {
      this.data.remove(var1);
      this.context.remove(var1);
      this.config.remove(var1);
      this.selection.remove(var1);
      this.mode.remove(var1);
      this.select(var1);
   }

   public synchronized void handle(String var1) {
      String var2 = onTick(var1);
      this.state.remove(var2);
      this.cache.remove(var2);
      this.output.remove(var2);
      this.current.remove(var2);
      this.active.remove(var2);
      this.enabled.remove(var2);
      ThemeShaderProgramCache.handle().compute(var2);
      this.select(var2);
   }

   public synchronized ShaderBuildResult process(LivePreviewRenderer var1) {
      return this.context.get(var1);
   }

   public synchronized ShaderBuildResult process(String var1) {
      return this.cache.get(onTick(var1));
   }

   public synchronized ShaderGraph compute(LivePreviewRenderer var1) {
      return this.data.get(var1);
   }

   public synchronized ShaderGraph compute(String var1) {
      return this.state.get(onTick(var1));
   }

   public synchronized String resolve(LivePreviewRenderer var1) {
      return this.config.get(var1);
   }

   public synchronized String resolve(String var1) {
      return this.output.get(onTick(var1));
   }

   public synchronized boolean update(LivePreviewRenderer var1) {
      return var1 != null && this.context.containsKey(var1);
   }

   public synchronized boolean update(String var1) {
      return this.cache.containsKey(onTick(var1));
   }

   public synchronized PresetManager.PrimaryMode apply(String var1) {
      return this.current.getOrDefault(onTick(var1), PresetManager.PrimaryMode.USER);
   }

   public synchronized PresetManager.Mode execute(String var1) {
      return this.active.getOrDefault(onTick(var1), PresetManager.Mode.FAILED);
   }

   public synchronized PresetManager.Mode apply(LivePreviewRenderer var1) {
      return this.mode.getOrDefault(var1, PresetManager.Mode.FAILED);
   }

   public synchronized List<ShaderParameter> execute(LivePreviewRenderer var1) {
      ShaderBuildResult var2 = var1 == null ? null : this.context.get(var1);
      return var2 == null ? List.of() : var2.exposedUniforms();
   }

   public synchronized List<ShaderParameter> prepare(String var1) {
      ShaderBuildResult var2 = this.cache.get(onTick(var1));
      return var2 == null ? List.of() : var2.exposedUniforms();
   }

   public synchronized Map<String, float[]> prepare(LivePreviewRenderer var1) {
      return handle(this.selection.get(var1));
   }

   public synchronized Map<String, float[]> check(String var1) {
      return handle(this.enabled.get(onTick(var1)));
   }

   public synchronized void handle(LivePreviewRenderer var1, String var2, float var3) {
      if (var1 != null && Float.isFinite(var3)) {
         ShaderParameter var4 = handle(this.execute(var1), var2, ShaderParameter.Mode.FLOAT);
         if (var4 != null) {
            this.selection.computeIfAbsent(var1, var0 -> new LinkedHashMap<>()).put(var4.uniformName(), new float[]{var3, 0.0F, 0.0F, 1.0F});
         }
      }
   }

   public synchronized void handle(String var1, String var2, float var3) {
      String var4 = onTick(var1);
      if (!var4.isBlank() && Float.isFinite(var3)) {
         ShaderParameter var5 = handle(this.prepare(var4), var2, ShaderParameter.Mode.FLOAT);
         if (var5 != null) {
            this.enabled.computeIfAbsent(var4, var0 -> new LinkedHashMap<>()).put(var5.uniformName(), new float[]{var3, 0.0F, 0.0F, 1.0F});
         }
      }
   }

   public synchronized void handle(LivePreviewRenderer var1, String var2, int var3) {
      if (var1 != null) {
         ShaderParameter var4 = handle(this.execute(var1), var2, ShaderParameter.Mode.COLOR);
         if (var4 != null) {
            this.selection.computeIfAbsent(var1, var0 -> new LinkedHashMap<>()).put(var4.uniformName(), handle(var3));
         }
      }
   }

   public synchronized void handle(String var1, String var2, int var3) {
      String var4 = onTick(var1);
      if (!var4.isBlank()) {
         ShaderParameter var5 = handle(this.prepare(var4), var2, ShaderParameter.Mode.COLOR);
         if (var5 != null) {
            this.enabled.computeIfAbsent(var4, var0 -> new LinkedHashMap<>()).put(var5.uniformName(), handle(var3));
         }
      }
   }

   public synchronized List<String> process() {
      ArrayList var1 = new ArrayList();

      for (String var3 : this.cache.keySet()) {
         if (!refresh(var3)) {
            var1.add(var3);
         }
      }

      Collections.sort(var1);
      return var1;
   }

   public synchronized List<String> check(LivePreviewRenderer var1) {
      LivePreviewRenderer var2 = var1 == null ? LivePreviewRenderer.PREVIEW_ONLY : var1;
      ArrayList var3 = new ArrayList();

      for (String var5 : this.cache.keySet()) {
         if (!refresh(var5)) {
            ShaderGraph var6 = this.state.get(var5);
            LivePreviewRenderer var7 = LivePreviewRenderer.handle(var6 == null ? null : var6.process());
            if (var7 == var2) {
               var3.add(var5);
            }
         }
      }

      Collections.sort(var3);
      return var3;
   }

   public synchronized List<String> compute() {
      ArrayList var1 = new ArrayList();
      var1.add("None");
      var1.addAll(this.process());
      return var1;
   }

   public synchronized List<String> onTick(LivePreviewRenderer var1) {
      ArrayList var2 = new ArrayList();
      var2.add("None");
      var2.addAll(this.check(var1));
      return var2;
   }

   private static void handle(Map<String, float[]> var0, ShaderBuildResult var1) {
      if (var0 != null && var1 != null) {
         for (ShaderParameter var3 : var1.exposedUniforms()) {
            var0.putIfAbsent(var3.uniformName(), Arrays.copyOf(var3.defaults(), var3.defaults().length));
         }
      }
   }

   private static Map<String, float[]> handle(Map<String, float[]> var0) {
      if (var0 != null && !var0.isEmpty()) {
         HashMap var1 = new HashMap();

         for (Entry var3 : var0.entrySet()) {
            var1.put(
               (String)var3.getKey(),
               var3.getValue() == null ? new float[]{0.0F, 0.0F, 0.0F, 1.0F} : Arrays.copyOf((float[])var3.getValue(), ((float[])var3.getValue()).length)
            );
         }

         return var1;
      } else {
         return Map.of();
      }
   }

   private static ShaderParameter handle(List<ShaderParameter> var0, String var1, ShaderParameter.Mode var2) {
      if (var0 != null && !var0.isEmpty() && var1 != null && !var1.isBlank()) {
         String var3 = onTick(var1);

         for (ShaderParameter var5 : var0) {
            if (var5.kind() == var2 && (onTick(var5.name()).equals(var3) || onTick(var5.uniformName()).equals(var3))) {
               return var5;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static float[] handle(int var0) {
      return new float[]{(var0 >> 16 & 0xFF) / 255.0F, (var0 >> 8 & 0xFF) / 255.0F, (var0 & 0xFF) / 255.0F, (var0 >>> 24 & 0xFF) / 255.0F};
   }

   public static String onTick(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.trim().replaceAll("\\s+", " ");
      return var1.length() > 48 ? var1.substring(0, 48) : var1;
   }

   private static boolean refresh(String var0) {
      return var0 != null && var0.startsWith("__");
   }

   private static PresetManager.PrimaryMode handle(ShaderGraph var0) {
      if (var0 != null && var0.handle() != null) {
         String var1 = var0.handle().apply();
         if ("preset".equalsIgnoreCase(var1)) {
            return PresetManager.PrimaryMode.PRESET;
         } else if ("imported".equalsIgnoreCase(var1) || "shared".equalsIgnoreCase(var1)) {
            return PresetManager.PrimaryMode.IMPORTED;
         } else {
            return "runtime".equalsIgnoreCase(var1) ? PresetManager.PrimaryMode.RUNTIME : PresetManager.PrimaryMode.USER;
         }
      } else {
         return PresetManager.PrimaryMode.USER;
      }
   }

   public enum Mode {
      SAVED,
      DIRTY,
      FAILED,
      COMPILING;
   }

   public enum PrimaryMode {
      PRESET,
      USER,
      IMPORTED,
      RUNTIME;
   }
}
