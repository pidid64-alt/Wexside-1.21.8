package ru.wild.render.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.FramebufferCapture;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexArrayBuffer;
import ru.wild.render.texture.DepthRenderTarget;
import ru.wild.render.texture.OffscreenRenderTarget;

public final class ShaderGraphEditor {
   private static final ShaderGraphEditor instance = new ShaderGraphEditor();
   static final float[] data = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   private static final int context = 48;
   private static final long config = 33L;
   private static final int state = 6;
   private static final int cache = 7;
   private final Map<String, ShaderGraphEditor.SecondaryDataRecord> output = new LinkedHashMap<>();
   private final Map<String, float[]> current = new HashMap<>();
   private final Map<String, Integer> active = new HashMap<>();
   private final Map<String, ShaderGraphEditor.LayoutMetrics> mode = new LinkedHashMap<>(16, 0.75F, true);
   private final ShaderGraphEditor.FramebufferState selection = new ShaderGraphEditor.FramebufferState();
   private final ShaderGraphEditor.ShaderState enabled = new ShaderGraphEditor.ShaderState();
   private boolean renderer;

   private ShaderGraphEditor() {
   }

   public static ShaderGraphEditor handle() {
      return instance;
   }

   public synchronized void handle(ShaderNodeRegistry var1) {
      if (var1 != null) {
         if (!this.renderer) {
            this.prepare();
            ShaderGraphCompiler.handle(this::process);
            this.renderer = true;
         }

         for (ShaderGraphEditor.SecondaryDataRecord var3 : this.output.values()) {
            if (var1.handle(var3.id()) == null) {
               var1.handle(var3.toNodeDefinition());
            }
         }

         process(var1);
      }
   }

   public synchronized void handle(ShaderGraphEditor.SecondaryDataRecord var1) {
      if (var1 != null) {
         this.output.put(var1.id(), var1);
      }
   }

   public synchronized ShaderGraphEditor.SecondaryDataRecord handle(String var1) {
      return this.output.get(var1);
   }

   public synchronized Collection<ShaderGraphEditor.SecondaryDataRecord> process() {
      return Collections.unmodifiableCollection(new ArrayList<>(this.output.values()));
   }

   public synchronized List<ShaderGraphEditor.SecondaryDataRecord> handle(LivePreviewRenderer var1) {
      LivePreviewRenderer var2 = var1 == null ? LivePreviewRenderer.PREVIEW_ONLY : var1.resolve();
      ArrayList var3 = new ArrayList();

      for (ShaderGraphEditor.SecondaryDataRecord var5 : this.output.values()) {
         if (var5.target().resolve() == var2) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public ShaderGraphEditor.FramebufferState compute() {
      return this.selection;
   }

   public ShaderGraphEditor.ShaderState resolve() {
      return this.enabled;
   }

   public synchronized String process(LivePreviewRenderer var1) {
      if (var1 != null && var1 != LivePreviewRenderer.PREVIEW_ONLY) {
         StringBuilder var2 = new StringBuilder();

         for (ShaderGraphEditor.SecondaryDataRecord var4 : this.output.values()) {
            if (var4.target().resolve() == var1 && !var4.glslPreamble().isBlank()) {
               var2.append(var4.glslPreamble());
               if (!var4.glslPreamble().endsWith("\n")) {
                  var2.append('\n');
               }
            }
         }

         return var2.toString();
      } else {
         return "";
      }
   }

   public boolean handle(ShaderNodeDefinition var1, String var2, ShaderNodeDefinition var3, String var4) {
      return this.process(var1, var2, var3, var4) == null;
   }

   public String process(ShaderNodeDefinition var1, String var2, ShaderNodeDefinition var3, String var4) {
      if (var1 != null && var3 != null) {
         ShaderPinDefinition var5 = var1.process(var2);
         if (var5 == null) {
            return var1.handle() + " has no output slot '" + var2 + "'";
         }

         ShaderPinDefinition var6 = var3.handle(var4);
         if (var6 == null) {
            return var3.handle() + " has no input slot '" + var4 + "'";
         }

         ShaderGraphEditor.Mode var7 = ShaderGraphEditor.Mode.handle(var5.type());
         ShaderGraphEditor.Mode var8 = ShaderGraphEditor.Mode.handle(var6.type());
         return var5.type() != var6.type() ? "type mismatch: " + var7.handle() + " -> " + var8.handle() : null;
      } else {
         return "unknown node definition";
      }
   }

   public JSONObject handle(ShaderNodeDefinition var1) {
      JSONObject var2 = new JSONObject();
      if (var1 == null) {
         return var2;
      }

      var2.put("id", var1.handle());
      JSONArray var3 = new JSONArray();

      for (ShaderPinDefinition var5 : var1.update()) {
         var3.put(handle(var5));
      }

      JSONArray var7 = new JSONArray();

      for (ShaderPinDefinition var6 : var1.apply()) {
         var7.put(handle(var6));
      }

      var2.put("inputs", var3);
      var2.put("outputs", var7);
      return var2;
   }

   public JSONArray handle(ShaderGraph var1, ShaderNodeRegistry var2) {
      JSONArray var3 = new JSONArray();
      if (var1 != null && var2 != null) {
         for (ShaderGraphLink var5 : var1.resolve()) {
            ShaderGraphBlock var6 = var1.compute(var5.handle());
            ShaderGraphBlock var7 = var1.compute(var5.compute());
            if (var6 != null && var7 != null) {
               ShaderNodeDefinition var8 = var2.handle(var6.process());
               ShaderNodeDefinition var9 = var2.handle(var7.process());
               if (this.handle(var8, var5.process(), var9, var5.resolve())) {
                  JSONObject var10 = new JSONObject();
                  var10.put("from", var5.handle());
                  var10.put("fromSlot", var5.process());
                  var10.put("to", var5.compute());
                  var10.put("toSlot", var5.resolve());
                  var10.put("type", var8.process(var5.process()).type().handle());
                  var3.put(var10);
               }
            }
         }

         return var3;
      } else {
         return var3;
      }
   }

   public int handle(ShaderGraph var1, JSONArray var2, ShaderNodeRegistry var3) {
      if (var1 != null && var2 != null && var3 != null) {
         int var4 = 0;

         for (int var5 = 0; var5 < var2.length(); var5++) {
            JSONObject var6 = var2.optJSONObject(var5);
            if (var6 != null) {
               String var7 = var6.optString("from", "");
               String var8 = var6.optString("fromSlot", "");
               String var9 = var6.optString("to", "");
               String var10 = var6.optString("toSlot", "");
               ShaderGraphBlock var11 = var1.compute(var7);
               ShaderGraphBlock var12 = var1.compute(var9);
               if (var11 != null && var12 != null) {
                  ShaderNodeDefinition var13 = var3.handle(var11.process());
                  ShaderNodeDefinition var14 = var3.handle(var12.process());
                  if (this.handle(var13, var8, var14, var10)) {
                     String var15 = var6.optString("type", "");
                     if ((var15.isBlank() || var15.equals(var13.process(var8).type().handle())) && var1.handle(var7, var8, var9, var10, var3)) {
                        var4++;
                     }
                  }
               }
            }
         }

         return var4;
      } else {
         return 0;
      }
   }

   public synchronized void handle(String var1, float var2, float var3, float var4, float var5) {
      if (var1 != null && !var1.isBlank()) {
         this.current.put(var1, new float[]{var2, var3, var4, var5});
      }
   }

   public synchronized void handle(String var1, int var2) {
      if (var1 != null && !var1.isBlank()) {
         if (var2 <= 0) {
            this.active.remove(var1);
         } else {
            this.active.put(var1, var2);
         }
      }
   }

   public void handle(float var1, float var2, float var3, float var4) {
      this.handle("uRadii", Math.max(0.0F, var1), Math.max(0.0F, var2), Math.max(0.0F, var3), Math.max(0.0F, var4));
   }

   public synchronized void handle(ShaderBuildReporter var1, LivePreviewRenderer var2) {
      if (var1 != null) {
         LivePreviewRenderer var3 = var2 == null ? LivePreviewRenderer.PREVIEW_ONLY : var2.resolve();

         for (ShaderGraphEditor.SecondaryDataRecord var5 : this.output.values()) {
            if (var5.target().resolve() == var3) {
               for (ShaderGraphEditor.DataRecord var7 : var5.uniforms()) {
                  int var8 = var1.handle(var7.name());
                  if (var8 >= 0) {
                     float[] var9 = this.current.getOrDefault(var7.name(), var7.defaults());
                     switch (var7.kind()) {
                        case SAMPLER2D:
                           GL13.glActiveTexture(33984 + var7.textureUnit());
                           GL11.glBindTexture(3553, this.compute(var7.name()));
                           GL20.glUniform1i(var8, var7.textureUnit());
                           break;
                        case VEC4:
                           GL20.glUniform4f(var8, var9[0], var9[1], var9[2], var9[3]);
                           break;
                        case VEC2:
                           GL20.glUniform2f(var8, var9[0], var9[1]);
                           break;
                        case FLOAT:
                           GL20.glUniform1f(var8, var9[0]);
                           break;
                        case INT:
                           GL20.glUniform1i(var8, Math.round(var9[0]));
                     }
                  }
               }
            }
         }

         GL13.glActiveTexture(33984);
      }
   }

   private int compute(String var1) {
      Integer var2 = this.active.get(var1);
      if (var2 != null && var2 > 0) {
         return var2;
      } else if ("uMask".equals(var1)) {
         int var4 = this.selection.resolve();
         return var4 > 0 ? var4 : ThemeShaderProgramCache.handle().update();
      } else if ("uDepth".equals(var1)) {
         int var3 = this.selection.update();
         return var3 > 0 ? var3 : ThemeShaderProgramCache.handle().update();
      } else {
         return ThemeShaderProgramCache.handle().update();
      }
   }

   public boolean handle(
      String var1,
      ShaderBuildResult var2,
      Map<String, float[]> var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      ThemeColors var12,
      float var13
   ) {
      int var14 = this.selection.resolve();
      return var14 <= 0
         ? false
         : this.handle(
            var1, var2, var3, LivePreviewRenderer.ESP, var14, var4, var5, var6, var7, var4, var5, var6, var7, 0.0F, var8, var9, var10, var11, var12, var13
         );
   }

   public boolean handle(
      String var1,
      ShaderBuildResult var2,
      Map<String, float[]> var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      int var12,
      int var13,
      float var14,
      float var15,
      ThemeColors var16,
      float var17
   ) {
      this.handle(var8, var9, var10, var11);
      float var18 = Math.max(Math.max(var8, var9), Math.max(var10, var11));
      return this.handle(
         var1,
         var2,
         var3,
         LivePreviewRenderer.HUD,
         ThemeShaderProgramCache.handle().update(),
         var4,
         var5,
         var6,
         var7,
         var4,
         var5,
         var6,
         var7,
         var18,
         var12,
         var13,
         var14,
         var15,
         var16,
         var17
      );
   }
   private boolean handle(
      String var1,
      ShaderBuildResult var2,
      Map<String, float[]> var3,
      LivePreviewRenderer var4,
      int var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      int var15,
      int var16,
      float var17,
      float var18,
      ThemeColors var19,
      float var20
   ) {
      if (var2 != null && var2.ok() && var15 > 0 && var16 > 0 && !(var8 <= 0.0F) && !(var9 <= 0.0F) && !(var20 <= 0.001F)) {
         ShaderBuildReporter var21 = ThemeShaderProgramCache.handle().handle(var1, var2);
         VertexArrayBuffer var22 = ThemeShaderProgramCache.handle().process();
         if (var21 != null && var22 != null) {
            OpenGlStateSnapshot.NetworkState var23 = OpenGlStateSnapshot.handle();
            boolean var31 = false /* VF: Semaphore variable */;

            boolean var28;
            try {
               var31 = true;
               GL11.glViewport(0, 0, var15, var16);
               GL11.glDisable(2929);
               GL11.glDisable(2884);
               GL11.glDepthMask(false);
               GlStateManager._enableBlend();
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
               GL11.glDisable(36281);
               var21.handle();
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, var5 > 0 ? var5 : ThemeShaderProgramCache.handle().update());
               handle(var21, "u_DiffuseMap", 0);
               handle(var21, "uViewport", var15, var16);
               handle(var21, "uRect", var6, var7, var8, var9);
               handle(var21, "u_ElementRect", var10, var11, var12, var13);
               handle(var21, "u_ElementRadius", Math.max(0.0F, var14));
               handle(var21, "u_GlobalUV", var10 / Math.max(1.0F, var15), var11 / Math.max(1.0F, var16));
               handle(var21, "u_Resolution", Math.max(1.0F, var15), Math.max(1.0F, var16));
               handle(var21, "u_Time", ThemeShaderProgramCache.handle().compute());
               handle(var21, "u_Mouse", var17 - var10, var18 - var11);
               int var24 = var19 == null ? -1 : var19.save();
               int var25 = var19 == null ? -16777216 : var19.submit();
               int var26 = var19 == null ? -15724520 : var19.apply();
               int var27 = var19 == null ? -14671832 : var19.execute();
               handle(var21, "u_AccentTop", handle(var24, 16), handle(var24, 8), handle(var24, 0));
               handle(var21, "u_AccentBottom", handle(var25, 16), handle(var25, 8), handle(var25, 0));
               handle(var21, "u_ThemeColors[0]", handle(var26, 16), handle(var26, 8), handle(var26, 0), handle(var26, 24));
               handle(var21, "u_ThemeColors[1]", handle(var27, 16), handle(var27, 8), handle(var27, 0), handle(var27, 24));
               handle(var21, "u_ThemeColors[2]", handle(var24, 16), handle(var24, 8), handle(var24, 0), var20);
               handle(var21, "u_ThemeColors[3]", handle(var25, 16), handle(var25, 8), handle(var25, 0), var20);
               handle(var21, "u_Alpha", var20);
               handle(var21, var2, var3);
               this.handle(var21, var4);
               var22.handle();
               var28 = true;
               var31 = false;
            } finally {
               if (var31) {
                  GL13.glActiveTexture(33984);
                  GL11.glBindTexture(3553, 0);
                  OpenGlStateSnapshot.compute(var23);
               }
            }

            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, 0);
            OpenGlStateSnapshot.compute(var23);
            return var28;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public synchronized int handle(
      ShaderGraphCompiler var1, ShaderNodeRegistry var2, ShaderGraph var3, String var4, float var5, float var6, ThemeColors var7, float var8, float var9
   ) {
      if (var1 != null && var2 != null && var3 != null && var4 != null && !(var5 <= 2.0F) && !(var6 <= 2.0F)) {
         ShaderGraphBlock var10 = var3.compute(var4);
         ShaderNodeDefinition var11 = var10 == null ? null : var2.handle(var10.process());
         ShaderPinDefinition var12 = process(var11);
         if (var12 == null) {
            return 0;
         }

         ShaderGraphEditor.LayoutMetrics var13 = this.mode.computeIfAbsent(var4, var0 -> new ShaderGraphEditor.LayoutMetrics());
         int var14 = var3.update();
         if (var13.data == null || var13.state != var14 || !var12.id().equals(var13.context)) {
            ShaderGraph var15 = var3.update(var4);
            var15.handle(LivePreviewRenderer.PREVIEW_ONLY.handle());
            var13.data = var1.handle(var15, var4, var12.id(), var12.type());
            var13.state = var14;
            var13.context = var12.id();
            var13.current = 0L;
            String var16 = var13.data == null ? "" : "__template_preview_" + var13.data.hash();
            if (!var13.config.isEmpty() && !var13.config.equals(var16)) {
               ThemeShaderProgramCache.handle().compute(var13.config);
            }

            var13.config = var16;
         }

         if (var13.data != null && var13.data.ok()) {
            long var24 = System.currentTimeMillis();
            int var17 = Math.max(32, Math.min(512, (int)Math.ceil(var5)));
            int var18 = Math.max(32, Math.min(384, (int)Math.ceil(var6)));
            if (var24 - var13.current >= 33L || var13.cache != var17 || var13.output != var18) {
               OpenGlStateSnapshot.NetworkState var19 = OpenGlStateSnapshot.handle();

               try {
                  var13.instance.handle(var17, var18);
                  if (!var13.instance.apply()) {
                     return 0;
                  }

                  var13.instance.handle();
                  GL11.glDisable(3089);
                  GlStateManager._enableBlend();
                  GL11.glEnable(3042);
                  GL11.glClearColor(0.008F, 0.01F, 0.015F, 0.0F);
                  GL11.glClear(16384);
                  DiffuseShaderRenderer.handle(var13.config, var13.data, 0.0F, 0.0F, var17, var18, var17, var18, var8, var9, var7, 1.0F);
                  var13.current = var24;
                  var13.cache = var17;
                  var13.output = var18;
               } finally {
                  OpenGlStateSnapshot.compute(var19);
               }
            }

            var13.active = var24;
            this.execute();
            return var13.instance.compute();
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public synchronized void process(String var1) {
      ShaderGraphEditor.LayoutMetrics var2 = this.mode.remove(var1);
      if (var2 != null) {
         handle(var2);
      }
   }

   public synchronized void update() {
      for (ShaderGraphEditor.LayoutMetrics var2 : this.mode.values()) {
         handle(var2);
      }

      this.mode.clear();
   }

   public synchronized void apply() {
      this.update();
      this.selection.close();
      this.enabled.close();
   }

   private void execute() {
      while (this.mode.size() > 48) {
         Entry var1 = this.mode.entrySet().iterator().next();
         handle((ShaderGraphEditor.LayoutMetrics)var1.getValue());
         this.mode.remove(var1.getKey());
      }
   }

   private static void handle(ShaderGraphEditor.LayoutMetrics var0) {
      var0.instance.close();
      if (!var0.config.isEmpty()) {
         ThemeShaderProgramCache.handle().compute(var0.config);
         var0.config = "";
      }
   }

   private static ShaderPinDefinition process(ShaderNodeDefinition var0) {
      if (var0 != null && !var0.apply().isEmpty()) {
         for (ShaderPinDefinition var2 : var0.apply()) {
            if ("color".equals(var2.id()) || "mask".equals(var2.id()) || "value".equals(var2.id())) {
               return var2;
            }
         }

         return var0.apply().get(0);
      } else {
         return null;
      }
   }

   private static JSONObject handle(ShaderPinDefinition var0) {
      JSONObject var1 = new JSONObject();
      var1.put("id", var0.id());
      var1.put("label", var0.label());
      var1.put("type", var0.type().handle());
      var1.put("direction", var0.direction().name().toLowerCase(Locale.ROOT));
      return var1;
   }

   private static void handle(ShaderBuildReporter var0, ShaderBuildResult var1, Map<String, float[]> var2) {
      if (var1 != null && !var1.exposedUniforms().isEmpty()) {
         for (ShaderParameter var4 : var1.exposedUniforms()) {
            float[] var5 = var2 == null ? null : (float[])var2.get(var4.uniformName());
            if (var5 == null) {
               var5 = var4.defaults();
            }

            if (var4.kind() == ShaderParameter.Mode.FLOAT) {
               handle(var0, var4.uniformName(), var5[0]);
            } else {
               handle(var0, var4.uniformName(), var5[0], var5[1], var5[2], var5[3]);
            }
         }
      }
   }

   private void prepare() {
      this.handle(
         new ShaderGraphEditor.SecondaryDataRecord(
            "template_esp_dual_pass",
            "ESP Dual-Pass Source",
            "isolated entity mask and scene depth samplers",
            "Template",
            LivePreviewRenderer.ESP,
            216.0F,
            List.of(),
            List.of(
               ShaderGraphEditor.PrimaryDataRecord.output("mask", "mask", ShaderGraphEditor.Mode.FLOAT),
               ShaderGraphEditor.PrimaryDataRecord.output("depth", "depth", ShaderGraphEditor.Mode.FLOAT),
               ShaderGraphEditor.PrimaryDataRecord.output("uv", "uv", ShaderGraphEditor.Mode.VEC2)
            ),
            "uniform sampler2D uMask;\nuniform sampler2D uDepth;\n\nfloat wild_template_mask(vec2 uv) {\n    return step(0.001, texture(uMask, uv).a);\n}\n\nfloat wild_template_depth(vec2 uv) {\n    float d = texture(uDepth, uv).r;\n    float ndc = d * 2.0 - 1.0;\n    float near = 0.05;\n    float far = 1024.0;\n    return clamp((2.0 * near) / (far + near - ndc * (far - near)), 0.0, 1.0);\n}\n",
            List.of(ShaderGraphEditor.DataRecord.sampler("uMask", 6), ShaderGraphEditor.DataRecord.sampler("uDepth", 7)),
            (var0, var1, var2) -> {
               boolean var3 = var0.handle() == LivePreviewRenderer.ESP;

               return switch (var2) {
                  case "mask" -> var3 ? "wild_template_mask(wild_diffuse_uv())" : "step(0.001, texture(u_DiffuseMap, wild_diffuse_uv()).a)";
                  case "depth" -> var3 ? "wild_template_depth(wild_diffuse_uv())" : "clamp(1.0 - texture(u_DiffuseMap, wild_diffuse_uv()).a, 0.0, 1.0)";
                  default -> "wild_diffuse_uv()";
               };
            }
         )
      );
      this.handle(
         new ShaderGraphEditor.SecondaryDataRecord(
            "template_hud_roundrect",
            "SDF RoundRect Plate",
            "per-corner rounded plate driven by uRect and uRadii",
            "Template",
            LivePreviewRenderer.HUD,
            224.0F,
            List.of(
               ShaderGraphEditor.PrimaryDataRecord.input("color", "color", ShaderGraphEditor.Mode.VEC4, "u_ThemeColors[0]"),
               ShaderGraphEditor.PrimaryDataRecord.input("softness", "soft", ShaderGraphEditor.Mode.FLOAT, "1.0")
            ),
            List.of(
               ShaderGraphEditor.PrimaryDataRecord.output("color", "color", ShaderGraphEditor.Mode.VEC4),
               ShaderGraphEditor.PrimaryDataRecord.output("mask", "distance", ShaderGraphEditor.Mode.FLOAT)
            ),
            "uniform vec4 uRadii;\n\nfloat wild_template_corner_pick(vec2 p, vec4 radii) {\n    float top = mix(radii.x, radii.y, step(0.0, p.x));\n    float bottom = mix(radii.w, radii.z, step(0.0, p.x));\n    return mix(top, bottom, step(0.0, p.y));\n}\n\nfloat wild_template_roundrect_distance() {\n    vec2 screenPx = vec2(gl_FragCoord.x, u_Resolution.y - gl_FragCoord.y);\n    vec2 p = screenPx - u_ElementRect.xy - u_ElementRect.zw * 0.5;\n    vec2 halfSize = max(u_ElementRect.zw * 0.5, vec2(0.5));\n    float radiiSum = uRadii.x + uRadii.y + uRadii.z + uRadii.w;\n    vec4 radii = mix(vec4(u_ElementRadius), uRadii, step(0.001, radiiSum));\n    float r = clamp(wild_template_corner_pick(p, radii), 0.0, min(halfSize.x, halfSize.y));\n    vec2 q = abs(p) - halfSize + vec2(r);\n    return length(max(q, vec2(0.0))) - r + min(max(q.x, q.y), 0.0);\n}\n\nfloat wild_template_roundrect_alpha(float d, float softness) {\n    float aa = max(fwidth(d), max(softness, 0.0001));\n    return 1.0 - smoothstep(0.0, aa, d);\n}\n",
            List.of(ShaderGraphEditor.DataRecord.vec4("uRadii", 0.0F, 0.0F, 0.0F, 0.0F)),
            (var0, var1, var2) -> {
               if (var0.process()) {
                  return "mask".equals(var2)
                     ? "wild_template_roundrect_distance()"
                     : "vec4(("
                        + var0.handle(var1, "color")
                        + ").rgb, ("
                        + var0.handle(var1, "color")
                        + ").a * wild_template_roundrect_alpha(wild_template_roundrect_distance(), "
                        + var0.handle(var1, "softness")
                        + "))";
               }

               String var3 = "wild_sdf_round_box(uv, vec2(0.0), vec2(0.42, 0.30), 0.08, 0.0)";
               return "mask".equals(var2)
                  ? var3
                  : "vec4((" + var0.handle(var1, "color") + ").rgb, (" + var0.handle(var1, "color") + ").a * wild_sdf_alpha(" + var3 + "))";
            }
         )
      );
   }

   private static void process(ShaderNodeRegistry var0) {
      if (var0.handle("int_value") == null) {
         var0.handle(
            new ShaderNodeDefinition(
               "int_value",
               "Integer",
               "Constants",
               154.0F,
               List.of(),
               List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.INT)),
               (var0x, var1, var2) -> String.valueOf(Math.round(var1.handle("value", 1.0F)))
            )
         );
      }

      if (var0.handle("int_to_float") == null) {
         var0.handle(
            new ShaderNodeDefinition(
               "int_to_float",
               "Int → Float",
               "Math",
               174.0F,
               List.of(ShaderPinDefinition.input("i", "i", ShaderValueType.INT, "0")),
               List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
               (var0x, var1, var2) -> "float(" + var0x.handle(var1, "i") + ")"
            )
         );
      }

      if (var0.handle("float_to_int") == null) {
         var0.handle(
            new ShaderNodeDefinition(
               "float_to_int",
               "Float → Int",
               "Math",
               174.0F,
               List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
               List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.INT)),
               (var0x, var1, var2) -> "int(floor((" + var0x.handle(var1, "x") + ") + 0.5))"
            )
         );
      }
   }

   static float handle(int var0, int var1) {
      return (var0 >>> var1 & 0xFF) / 255.0F;
   }

   static void handle(ShaderBuildReporter var0, String var1, float var2) {
      int var3 = var0.handle(var1);
      if (var3 >= 0) {
         GL20.glUniform1f(var3, var2);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, int var2) {
      int var3 = var0.handle(var1);
      if (var3 >= 0) {
         GL20.glUniform1i(var3, var2);
      }
   }

   static void handle(ShaderBuildReporter var0, String var1, float var2, float var3) {
      int var4 = var0.handle(var1);
      if (var4 >= 0) {
         GL20.glUniform2f(var4, var2, var3);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, float var2, float var3, float var4) {
      int var5 = var0.handle(var1);
      if (var5 >= 0) {
         GL20.glUniform3f(var5, var2, var3, var4);
      }
   }

   static void handle(ShaderBuildReporter var0, String var1, float var2, float var3, float var4, float var5) {
      int var6 = var0.handle(var1);
      if (var6 >= 0) {
         GL20.glUniform4f(var6, var2, var3, var4, var5);
      }
   }

   public record DataRecord(String name, ShaderSlotType kind, int textureUnit, float[] defaults) {
      public DataRecord(String name, ShaderSlotType kind, int textureUnit, float[] defaults) {
         if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("uniform name required");
         }

         if (kind == null) {
            throw new IllegalArgumentException("uniform kind required for " + name);
         }

         defaults = defaults == null ? new float[4] : Arrays.copyOf(defaults, 4);
         this.name = name;
         this.kind = kind;
         this.textureUnit = textureUnit;
         this.defaults = defaults;
      }

      public static ShaderGraphEditor.DataRecord sampler(String var0, int var1) {
         return new ShaderGraphEditor.DataRecord(var0, ShaderSlotType.SAMPLER2D, var1, null);
      }

      public static ShaderGraphEditor.DataRecord vec4(String var0, float var1, float var2, float var3, float var4) {
         return new ShaderGraphEditor.DataRecord(var0, ShaderSlotType.VEC4, -1, new float[]{var1, var2, var3, var4});
      }

      public static ShaderGraphEditor.DataRecord vec2(String var0, float var1, float var2) {
         return new ShaderGraphEditor.DataRecord(var0, ShaderSlotType.VEC2, -1, new float[]{var1, var2, 0.0F, 0.0F});
      }

      public static ShaderGraphEditor.DataRecord scalar(String var0, float var1) {
         return new ShaderGraphEditor.DataRecord(var0, ShaderSlotType.FLOAT, -1, new float[]{var1, 0.0F, 0.0F, 0.0F});
      }

      public static ShaderGraphEditor.DataRecord integer(String var0, int var1) {
         return new ShaderGraphEditor.DataRecord(var0, ShaderSlotType.INT, -1, new float[]{var1, 0.0F, 0.0F, 0.0F});
      }
   }

   public static final class FramebufferState implements AutoCloseable {
      private static final String instance = "foundry_template_esp";
      private final DepthRenderTarget data = new DepthRenderTarget();
      private OpenGlStateSnapshot.NetworkState context;
      private boolean config;

      public void handle(Predicate<Entity> var1) {
         FramebufferCapture.handle().handle("foundry_template_esp", true, var1);
         this.config = true;
      }

      public void handle() {
         FramebufferCapture.handle().handle("foundry_template_esp");
         this.config = false;
      }

      public boolean process() {
         return this.config;
      }

      public boolean compute() {
         return FramebufferCapture.handle().compute();
      }

      public int resolve() {
         FramebufferCapture var1 = FramebufferCapture.handle();
         return var1.compute() && var1.resolve() > 0 ? var1.resolve() : this.data.data;
      }

      public int update() {
         FramebufferCapture var1 = FramebufferCapture.handle();
         return var1.compute() && var1.update() > 0 ? var1.update() : this.data.context;
      }

      public int apply() {
         FramebufferCapture var1 = FramebufferCapture.handle();
         return var1.compute() && var1.encodePoint() > 0 ? var1.encodePoint() : this.data.config;
      }

      public int execute() {
         FramebufferCapture var1 = FramebufferCapture.handle();
         return var1.compute() && var1.animate() > 0 ? var1.animate() : this.data.state;
      }

      public boolean handle(int var1, int var2) {
         if (var1 > 0 && var2 > 0 && this.context == null) {
            try {
               this.data.handle(var1, var2);
            } catch (IllegalStateException var4) {
               return false;
            }

            this.context = OpenGlStateSnapshot.handle();
            GL30.glBindFramebuffer(36008, OpenGlStateSnapshot.handle(this.context.instance));
            GL30.glBindFramebuffer(36009, this.data.instance);
            GL30.glBlitFramebuffer(0, 0, var1, var2, 0, 0, var1, var2, 256, 9728);
            GL30.glBindFramebuffer(36160, this.data.instance);
            GL11.glViewport(0, 0, var1, var2);
            GL11.glDisable(3089);
            GL30.glClearBufferfv(6144, 0, ShaderGraphEditor.data);
            GL11.glEnable(2929);
            GL11.glDepthMask(false);
            return true;
         } else {
            return false;
         }
      }

      public void prepare() {
         if (this.context != null) {
            OpenGlStateSnapshot.compute(this.context);
            this.context = null;
         }
      }

      @Override
      public void close() {
         if (this.config) {
            this.handle();
         }

         this.prepare();
         this.data.handle();
      }
   }

   static final class LayoutMetrics {
      final OffscreenRenderTarget instance = new OffscreenRenderTarget();
      ShaderBuildResult data;
      String context = "";
      String config = "";
      int state = Integer.MIN_VALUE;
      int cache;
      int output;
      long current;
      long active;
   }

   public enum Mode {
      VEC4("vec4", 4, ThemeColors.handle(255, 61, 158, 255)),
      VEC2("vec2", 2, ThemeColors.handle(177, 140, 255, 255)),
      FLOAT("float", 1, ThemeColors.handle(53, 228, 255, 255)),
      INT("int", 1, ThemeColors.handle(155, 255, 61, 255));

      private final String instance;
      private final int data;
      private final int context;

      Mode(String var3, int var4, int var5) {
         this.instance = var3;
         this.data = var4;
         this.context = var5;
      }

      public String handle() {
         return this.instance;
      }

      public int process() {
         return this.data;
      }

      public int compute() {
         return this.context;
      }

      public boolean handle(ShaderGraphEditor.Mode var1) {
         return this == var1;
      }

      public ShaderValueType resolve() {
         return switch (this) {
            case VEC4 -> ShaderValueType.VEC4;
            case VEC2 -> ShaderValueType.VEC2;
            case FLOAT -> ShaderValueType.FLOAT;
            case INT -> ShaderValueType.INT;
         };
      }

      public static ShaderGraphEditor.Mode handle(ShaderValueType var0) {
         if (var0 == null) {
            return FLOAT;
         }

         return switch (var0) {
            case VEC4 -> VEC4;
            case VEC3 -> VEC4;
            case VEC2 -> VEC2;
            case FLOAT -> FLOAT;
            case INT -> INT;
         };
      }

      public static int process(ShaderValueType var0) {
         if (var0 == null) {
            return FLOAT.context;
         }

         return switch (var0) {
            case VEC4 -> VEC4.context;
            case VEC3 -> ThemeColors.handle(250, 176, 96, 255);
            case VEC2 -> VEC2.context;
            case FLOAT -> FLOAT.context;
            case INT -> INT.context;
         };
      }
   }

   public record PrimaryDataRecord(String id, String label, ShaderGraphEditor.Mode type, ShaderPinDirection direction, String defaultExpression) {
      public PrimaryDataRecord(String id, String label, ShaderGraphEditor.Mode type, ShaderPinDirection direction, String defaultExpression) {
         if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("slot id required");
         }

         if (type != null && direction != null) {
            label = label != null && !label.isBlank() ? label : id;
            defaultExpression = defaultExpression == null ? "" : defaultExpression;
            this.id = id;
            this.label = label;
            this.type = type;
            this.direction = direction;
            this.defaultExpression = defaultExpression;
         } else {
            throw new IllegalArgumentException("slot type and direction required for " + id);
         }
      }

      public static ShaderGraphEditor.PrimaryDataRecord input(String var0, String var1, ShaderGraphEditor.Mode var2, String var3) {
         return new ShaderGraphEditor.PrimaryDataRecord(var0, var1, var2, ShaderPinDirection.INPUT, var3);
      }

      public static ShaderGraphEditor.PrimaryDataRecord output(String var0, String var1, ShaderGraphEditor.Mode var2) {
         return new ShaderGraphEditor.PrimaryDataRecord(var0, var1, var2, ShaderPinDirection.OUTPUT, "");
      }

      public ShaderPinDefinition toPinTemplate() {
         return new ShaderPinDefinition(this.id, this.label, this.type.resolve(), this.direction, this.defaultExpression);
      }
   }

   public record SecondaryDataRecord(
      String id,
      String title,
      String description,
      String category,
      LivePreviewRenderer target,
      float nodeWidth,
      List<ShaderGraphEditor.PrimaryDataRecord> inputs,
      List<ShaderGraphEditor.PrimaryDataRecord> outputs,
      String glslPreamble,
      List<ShaderGraphEditor.DataRecord> uniforms,
      ShaderExpressionEmitter emitter
   ) {
      public SecondaryDataRecord(
         String id,
         String title,
         String description,
         String category,
         LivePreviewRenderer target,
         float nodeWidth,
         List<ShaderGraphEditor.PrimaryDataRecord> inputs,
         List<ShaderGraphEditor.PrimaryDataRecord> outputs,
         String glslPreamble,
         List<ShaderGraphEditor.DataRecord> uniforms,
         ShaderExpressionEmitter emitter
      ) {
         if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("template id required");
         }

         if (target != null && emitter != null) {
            title = title != null && !title.isBlank() ? title : id;
            description = description == null ? "" : description;
            category = category != null && !category.isBlank() ? category : "Template";
            inputs = inputs == null ? List.of() : List.copyOf(inputs);
            outputs = outputs == null ? List.of() : List.copyOf(outputs);
            glslPreamble = glslPreamble == null ? "" : glslPreamble;
            uniforms = uniforms == null ? List.of() : List.copyOf(uniforms);
            this.id = id;
            this.title = title;
            this.description = description;
            this.category = category;
            this.target = target;
            this.nodeWidth = nodeWidth;
            this.inputs = inputs;
            this.outputs = outputs;
            this.glslPreamble = glslPreamble;
            this.uniforms = uniforms;
            this.emitter = emitter;
         } else {
            throw new IllegalArgumentException("template target and emitter required for " + id);
         }
      }

      public ShaderNodeDefinition toNodeDefinition() {
         ArrayList var1 = new ArrayList(this.inputs.size());

         for (ShaderGraphEditor.PrimaryDataRecord var3 : this.inputs) {
            var1.add(var3.toPinTemplate());
         }

         ArrayList var5 = new ArrayList(this.outputs.size());

         for (ShaderGraphEditor.PrimaryDataRecord var4 : this.outputs) {
            var5.add(var4.toPinTemplate());
         }

         return new ShaderNodeDefinition(this.id, this.title, this.category, this.nodeWidth, var1, var5, this.emitter);
      }
   }

   public static final class ShaderState implements AutoCloseable {
      private ShaderBuildReporter instance;
      private boolean data;

      public boolean handle(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         int var9,
         int var10,
         float var11,
         float var12,
         int var13,
         int var14,
         float var15
      ) {
         if (!this.data && !(var3 <= 0.0F) && !(var4 <= 0.0F) && var13 > 0 && var14 > 0 && !(var15 <= 0.001F)) {
            ShaderBuildReporter var16 = this.handle();
            VertexArrayBuffer var17 = ThemeShaderProgramCache.handle().process();
            if (var16 != null && var17 != null) {
               OpenGlStateSnapshot.NetworkState var18 = OpenGlStateSnapshot.handle();

               try {
                  GL11.glViewport(0, 0, var13, var14);
                  GL11.glDisable(2929);
                  GL11.glDisable(2884);
                  GL11.glDepthMask(false);
                  GlStateManager._enableBlend();
                  GL11.glEnable(3042);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  GL11.glDisable(36281);
                  var16.handle();
                  ShaderGraphEditor.handle(var16, "uViewport", var13, var14);
                  ShaderGraphEditor.handle(var16, "uRect", var1, var2, var3, var4);
                  ShaderGraphEditor.handle(var16, "uRadii", Math.max(0.0F, var5), Math.max(0.0F, var6), Math.max(0.0F, var7), Math.max(0.0F, var8));
                  ShaderGraphEditor.handle(
                     var16,
                     "uTint",
                     ShaderGraphEditor.handle(var9, 16),
                     ShaderGraphEditor.handle(var9, 8),
                     ShaderGraphEditor.handle(var9, 0),
                     ShaderGraphEditor.handle(var9, 24)
                  );
                  ShaderGraphEditor.handle(
                     var16,
                     "uStrokeTint",
                     ShaderGraphEditor.handle(var10, 16),
                     ShaderGraphEditor.handle(var10, 8),
                     ShaderGraphEditor.handle(var10, 0),
                     ShaderGraphEditor.handle(var10, 24)
                  );
                  ShaderGraphEditor.handle(var16, "uStrokeWidth", Math.max(0.0F, var11));
                  ShaderGraphEditor.handle(var16, "uSoftness", Math.max(0.0F, var12));
                  ShaderGraphEditor.handle(var16, "uAlpha", var15);
                  var17.handle();
                  return true;
               } finally {
                  OpenGlStateSnapshot.compute(var18);
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      private ShaderBuildReporter handle() {
         if (this.instance != null) {
            return this.instance;
         }

         try {
            this.instance = ShaderBuildReporter.handle("assets/wild/shaders/foundry/roundrect.vert", "assets/wild/shaders/foundry/roundrect.frag");
            return this.instance;
         } catch (Throwable var2) {
            this.data = true;
            return null;
         }
      }

      @Override
      public void close() {
         if (this.instance != null) {
            this.instance.process();
            this.instance = null;
         }

         this.data = false;
      }
   }
}
