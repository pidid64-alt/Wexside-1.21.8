package ru.wild.gui.theme;

import java.util.List;
import java.util.function.Function;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderGraphBlock;
import ru.wild.render.shader.ShaderNodeRegistry;

public final class BuiltinThemePresets {
   public static final List<BuiltinThemePresets.PrimaryDataRecord> instance = List.of(
      new BuiltinThemePresets.PrimaryDataRecord(
         "Ferro HUD Starter",
         "matte host plate with rim, grain and hover light",
         LivePreviewRenderer.HUD,
         "Starter",
         List.of("Element Mask", "Mica Glass", "Rim Light", "Hover Glow"),
         var0 -> handle(var0, LivePreviewRenderer.HUD, "Ferro HUD Starter", "clean HUD plate shader", 0.72F, 0.07F, 0.34F, 0.6F)
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Ferro Module Card",
         "module row glass without pulse or layout noise",
         LivePreviewRenderer.MODULE_CARD,
         "Starter",
         List.of("Element Mask", "Mica Glass", "Rim Light"),
         var0 -> handle(var0, LivePreviewRenderer.MODULE_CARD, "Ferro Module Card", "module card material starter", 0.62F, 0.045F, 0.22F, 0.42F)
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Ferro Panel Surface",
         "dock panel surface with stable mica depth",
         LivePreviewRenderer.PANEL_BACKGROUND,
         "Starter",
         List.of("Element Mask", "Mica Glass", "Rim Light"),
         var0 -> handle(var0, LivePreviewRenderer.PANEL_BACKGROUND, "Ferro Panel Surface", "panel background material starter", 0.68F, 0.055F, 0.26F, 0.48F)
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Ferro Button Surface",
         "button body with compact magnetic response",
         LivePreviewRenderer.BUTTON,
         "Starter",
         List.of("Element Mask", "Mica Glass", "Hover Glow"),
         var0 -> handle(var0, LivePreviewRenderer.BUTTON, "Ferro Button Surface", "interactive button material starter", 0.66F, 0.038F, 0.3F, 0.82F)
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Clean Health Fill",
         "stable gradient fill for bars and shield surfaces",
         LivePreviewRenderer.HEALTH_BAR,
         "Starter",
         List.of("Element UV", "Gradient Map", "SDF Fill"),
         BuiltinThemePresets::process
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Clean Menu Background",
         "quiet full-screen gradient background",
         LivePreviewRenderer.BACKGROUND,
         "Starter",
         List.of("Global UV", "Gradient Map"),
         var0 -> handle(var0, LivePreviewRenderer.BACKGROUND, "Clean Menu Background", "full-screen interface background starter")
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Clean Sky Atmosphere",
         "soft atmospheric wash for sky target",
         LivePreviewRenderer.SKY,
         "Starter",
         List.of("Global UV", "Gradient Map"),
         var0 -> handle(var0, LivePreviewRenderer.SKY, "Clean Sky Atmosphere", "world atmosphere starter")
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Clean ESP Silhouette",
         "entity-target rounded silhouette with rim",
         LivePreviewRenderer.ESP,
         "Starter",
         List.of("Element Mask", "SDF Fill", "Rim Light"),
         BuiltinThemePresets::compute
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Clean Chams Film",
         "texture-preserving entity film with stable fresnel",
         LivePreviewRenderer.CHAMS,
         "Starter",
         List.of("Base Texture", "Fresnel", "Screen Blend"),
         BuiltinThemePresets::resolve
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Clean Nametag Plate",
         "billboard nametag mica plate",
         LivePreviewRenderer.NAMETAG,
         "Starter",
         List.of("Element Mask", "Mica Glass", "Rim Light"),
         var0 -> handle(var0, LivePreviewRenderer.NAMETAG, "Clean Nametag Plate", "nametag plate material starter", 0.64F, 0.042F, 0.28F, 0.34F)
      ),
      new BuiltinThemePresets.PrimaryDataRecord(
         "Clean Trail Ribbon",
         "additive ribbon starter with stable edge energy",
         LivePreviewRenderer.TRAILS,
         "Starter",
         List.of("Fresnel", "Gradient Map", "Bloom Lift"),
         BuiltinThemePresets::update
      )
   );

   private BuiltinThemePresets() {
   }

   public static ShaderGraph handle(BuiltinThemePresets.PrimaryDataRecord var0, ShaderNodeRegistry var1) {
      return var0 == null ? handle(var1) : var0.builder.apply(var1);
   }

   public static ShaderGraph handle(ShaderNodeRegistry var0) {
      return handle(var0, LivePreviewRenderer.HUD, "Ferro HUD Starter", "clean HUD plate shader", 0.72F, 0.07F, 0.34F, 0.6F);
   }

   private static ShaderGraph handle(
      ShaderNodeRegistry var0, LivePreviewRenderer var1, String var2, String var3, float var4, float var5, float var6, float var7
   ) {
      ShaderGraph var8 = new ShaderGraph();
      handle(var8, var2, var3, var1, "Starter");
      BuiltinThemePresets.DataRecord var9 = new BuiltinThemePresets.DataRecord(-780.0F, -180.0F, 224.0F, 108.0F);
      ShaderGraphBlock var10 = handle(var8, var0, "input_element_uv", var9, 0, 0);
      ShaderGraphBlock var11 = handle(var8, var0, "element_mask", var9, 0, 1);
      ShaderGraphBlock var12 = handle(var8, var0, "theme_panel", var9, 0, 3);
      ShaderGraphBlock var13 = handle(var8, var0, "theme_top", var9, 0, 4);
      ShaderGraphBlock var14 = handle(var8, var0, "theme_bottom", var9, 0, 5);
      ShaderGraphBlock var15 = handle(var8, var0, "exposed_float", var9, 1, 0);
      ShaderGraphBlock var16 = handle(var8, var0, "exposed_float", var9, 1, 1);
      ShaderGraphBlock var17 = handle(var8, var0, "exposed_float", var9, 1, 2);
      ShaderGraphBlock var18 = handle(var8, var0, "exposed_float", var9, 1, 3);
      ShaderGraphBlock var19 = handle(var8, var0, "exposed_float", var9, 1, 4);
      ShaderGraphBlock var20 = handle(var8, var0, "exposed_float", var9, 1, 5);
      ShaderGraphBlock var21 = handle(var8, var0, "glass_surface", var9, 2, 0);
      ShaderGraphBlock var22 = handle(var8, var0, "rim_light", var9, 2, 2);
      ShaderGraphBlock var23 = handle(var8, var0, "hover_glow", var9, 2, 4);
      ShaderGraphBlock var24 = handle(var8, var0, "alpha_blend", var9, 3, 1);
      ShaderGraphBlock var25 = handle(var8, var0, "alpha_blend", var9, 4, 1);
      ShaderGraphBlock var26 = handle(var8, var0, "output_color", var9, 5, 1);
      handle(var15, "Opacity", var4, 0.05F, 1.0F, 0.01F);
      handle(var16, "Grain", var5, 0.0F, 0.18F, 0.002F);
      handle(var17, "Rim Width", 1.15F, 0.35F, 4.0F, 0.05F);
      handle(var18, "Rim Power", var6, 0.0F, 1.0F, 0.01F);
      handle(var19, "Hover Radius", 0.44F, 0.05F, 1.2F, 0.01F);
      handle(var20, "Hover Power", var7, 0.0F, 1.8F, 0.01F);
      var8.handle(var11.handle(), "mask", var21.handle(), "mask", var0);
      var8.handle(var12.handle(), "color", var21.handle(), "tint", var0);
      var8.handle(var15.handle(), "value", var21.handle(), "opacity", var0);
      var8.handle(var16.handle(), "value", var21.handle(), "grain", var0);
      var8.handle(var11.handle(), "mask", var22.handle(), "mask", var0);
      var8.handle(var13.handle(), "color", var22.handle(), "color", var0);
      var8.handle(var17.handle(), "value", var22.handle(), "thickness", var0);
      var8.handle(var18.handle(), "value", var22.handle(), "intensity", var0);
      var8.handle(var10.handle(), "uv", var23.handle(), "uv", var0);
      var8.handle(var14.handle(), "color", var23.handle(), "color", var0);
      var8.handle(var19.handle(), "value", var23.handle(), "radius", var0);
      var8.handle(var20.handle(), "value", var23.handle(), "intensity", var0);
      var8.handle(var21.handle(), "color", var24.handle(), "base", var0);
      var8.handle(var22.handle(), "color", var24.handle(), "layer", var0);
      var8.handle(var24.handle(), "color", var25.handle(), "base", var0);
      var8.handle(var23.handle(), "color", var25.handle(), "layer", var0);
      var8.handle(var25.handle(), "color", var26.handle(), "color", var0);
      return var8;
   }

   private static ShaderGraph process(ShaderNodeRegistry var0) {
      ShaderGraph var1 = new ShaderGraph();
      handle(var1, "Clean Health Fill", "stable health bar shader starter", LivePreviewRenderer.HEALTH_BAR, "Starter");
      BuiltinThemePresets.DataRecord var2 = new BuiltinThemePresets.DataRecord(-720.0F, -150.0F, 216.0F, 104.0F);
      ShaderGraphBlock var3 = handle(var1, var0, "input_element_uv", var2, 0, 0);
      ShaderGraphBlock var4 = handle(var1, var0, "vec2_split", var2, 1, 0);
      ShaderGraphBlock var5 = handle(var1, var0, "element_mask", var2, 0, 2);
      ShaderGraphBlock var6 = handle(var1, var0, "theme_bottom", var2, 1, 2);
      ShaderGraphBlock var7 = handle(var1, var0, "theme_top", var2, 1, 3);
      ShaderGraphBlock var8 = handle(var1, var0, "exposed_float", var2, 2, 0);
      ShaderGraphBlock var9 = handle(var1, var0, "color_ramp", var2, 2, 2);
      ShaderGraphBlock var10 = handle(var1, var0, "sdf_fill", var2, 3, 2);
      ShaderGraphBlock var11 = handle(var1, var0, "output_color", var2, 4, 2);
      handle(var8, "Fill Alpha", 0.92F, 0.0F, 1.0F, 0.01F);
      var1.handle(var3.handle(), "uv", var4.handle(), "v", var0);
      var1.handle(var4.handle(), "x", var9.handle(), "t", var0);
      var1.handle(var6.handle(), "color", var9.handle(), "a", var0);
      var1.handle(var7.handle(), "color", var9.handle(), "b", var0);
      var1.handle(var5.handle(), "mask", var10.handle(), "mask", var0);
      var1.handle(var9.handle(), "color", var10.handle(), "color", var0);
      var1.handle(var8.handle(), "value", var10.handle(), "alpha", var0);
      var1.handle(var10.handle(), "color", var11.handle(), "color", var0);
      return var1;
   }

   private static ShaderGraph handle(ShaderNodeRegistry var0, LivePreviewRenderer var1, String var2, String var3) {
      ShaderGraph var4 = new ShaderGraph();
      handle(var4, var2, var3, var1, "Starter");
      BuiltinThemePresets.DataRecord var5 = new BuiltinThemePresets.DataRecord(-700.0F, -130.0F, 216.0F, 104.0F);
      ShaderGraphBlock var6 = handle(var4, var0, "input_global_uv", var5, 0, 0);
      ShaderGraphBlock var7 = handle(var4, var0, "vec2_split", var5, 1, 0);
      ShaderGraphBlock var8 = handle(var4, var0, "theme_bottom", var5, 1, 2);
      ShaderGraphBlock var9 = handle(var4, var0, "theme_panel", var5, 1, 3);
      ShaderGraphBlock var10 = handle(var4, var0, "theme_top", var5, 1, 4);
      ShaderGraphBlock var11 = handle(var4, var0, "color_gradient_map", var5, 2, 1);
      ShaderGraphBlock var12 = handle(var4, var0, "output_color", var5, 3, 1);
      var4.handle(var6.handle(), "uv", var7.handle(), "v", var0);
      var4.handle(var7.handle(), "y", var11.handle(), "t", var0);
      var4.handle(var8.handle(), "color", var11.handle(), "a", var0);
      var4.handle(var9.handle(), "color", var11.handle(), "b", var0);
      var4.handle(var10.handle(), "color", var11.handle(), "c", var0);
      var4.handle(var11.handle(), "color", var12.handle(), "color", var0);
      return var4;
   }

   private static ShaderGraph compute(ShaderNodeRegistry var0) {
      ShaderGraph var1 = new ShaderGraph();
      handle(var1, "Clean ESP Silhouette", "stable entity silhouette shader starter", LivePreviewRenderer.ESP, "Starter");
      BuiltinThemePresets.DataRecord var2 = new BuiltinThemePresets.DataRecord(-720.0F, -160.0F, 216.0F, 106.0F);
      ShaderGraphBlock var3 = handle(var1, var0, "element_mask", var2, 0, 0);
      ShaderGraphBlock var4 = handle(var1, var0, "theme_bottom", var2, 0, 2);
      ShaderGraphBlock var5 = handle(var1, var0, "theme_top", var2, 0, 3);
      ShaderGraphBlock var6 = handle(var1, var0, "exposed_float", var2, 1, 0);
      ShaderGraphBlock var7 = handle(var1, var0, "exposed_float", var2, 1, 1);
      ShaderGraphBlock var8 = handle(var1, var0, "sdf_fill", var2, 2, 0);
      ShaderGraphBlock var9 = handle(var1, var0, "rim_light", var2, 2, 2);
      ShaderGraphBlock var10 = handle(var1, var0, "alpha_blend", var2, 3, 1);
      ShaderGraphBlock var11 = handle(var1, var0, "output_color", var2, 4, 1);
      handle(var6, "Aura Alpha", 0.78F, 0.0F, 1.0F, 0.01F);
      handle(var7, "Rim Power", 0.46F, 0.0F, 1.2F, 0.01F);
      var1.handle(var3.handle(), "mask", var8.handle(), "mask", var0);
      var1.handle(var4.handle(), "color", var8.handle(), "color", var0);
      var1.handle(var6.handle(), "value", var8.handle(), "alpha", var0);
      var1.handle(var3.handle(), "mask", var9.handle(), "mask", var0);
      var1.handle(var5.handle(), "color", var9.handle(), "color", var0);
      var1.handle(var7.handle(), "value", var9.handle(), "intensity", var0);
      var1.handle(var8.handle(), "color", var10.handle(), "base", var0);
      var1.handle(var9.handle(), "color", var10.handle(), "layer", var0);
      var1.handle(var10.handle(), "color", var11.handle(), "color", var0);
      return var1;
   }

   private static ShaderGraph resolve(ShaderNodeRegistry var0) {
      ShaderGraph var1 = new ShaderGraph();
      handle(var1, "Clean Chams Film", "stable chams material starter", LivePreviewRenderer.CHAMS, "Starter");
      BuiltinThemePresets.DataRecord var2 = new BuiltinThemePresets.DataRecord(-740.0F, -150.0F, 216.0F, 106.0F);
      ShaderGraphBlock var3 = handle(var1, var0, "input_uv", var2, 0, 0);
      ShaderGraphBlock var4 = handle(var1, var0, "base_texture", var2, 0, 2);
      ShaderGraphBlock var5 = handle(var1, var0, "color_alpha", var2, 1, 2);
      ShaderGraphBlock var6 = handle(var1, var0, "fresnel", var2, 1, 0);
      ShaderGraphBlock var7 = handle(var1, var0, "theme_top", var2, 1, 4);
      ShaderGraphBlock var8 = handle(var1, var0, "theme_bottom", var2, 1, 5);
      ShaderGraphBlock var9 = handle(var1, var0, "color_ramp", var2, 2, 0);
      ShaderGraphBlock var10 = handle(var1, var0, "color_multiply_scalar", var2, 3, 0);
      ShaderGraphBlock var11 = handle(var1, var0, "blend_screen", var2, 4, 1);
      ShaderGraphBlock var12 = handle(var1, var0, "output_color", var2, 5, 1);
      var1.handle(var3.handle(), "uv", var6.handle(), "uv", var0);
      var1.handle(var6.handle(), "value", var9.handle(), "t", var0);
      var1.handle(var8.handle(), "color", var9.handle(), "a", var0);
      var1.handle(var7.handle(), "color", var9.handle(), "b", var0);
      var1.handle(var4.handle(), "color", var5.handle(), "color", var0);
      var1.handle(var9.handle(), "color", var10.handle(), "color", var0);
      var1.handle(var5.handle(), "alpha", var10.handle(), "factor", var0);
      var1.handle(var4.handle(), "color", var11.handle(), "base", var0);
      var1.handle(var10.handle(), "color", var11.handle(), "layer", var0);
      var1.handle(var5.handle(), "alpha", var11.handle(), "opacity", var0);
      var1.handle(var11.handle(), "color", var12.handle(), "color", var0);
      var1.handle(var5.handle(), "alpha", var12.handle(), "alpha", var0);
      return var1;
   }

   private static ShaderGraph update(ShaderNodeRegistry var0) {
      ShaderGraph var1 = new ShaderGraph();
      handle(var1, "Clean Trail Ribbon", "stable trail ribbon shader starter", LivePreviewRenderer.TRAILS, "Starter");
      BuiltinThemePresets.DataRecord var2 = new BuiltinThemePresets.DataRecord(-720.0F, -145.0F, 216.0F, 104.0F);
      ShaderGraphBlock var3 = handle(var1, var0, "input_uv", var2, 0, 0);
      ShaderGraphBlock var4 = handle(var1, var0, "fresnel", var2, 1, 0);
      ShaderGraphBlock var5 = handle(var1, var0, "theme_top", var2, 1, 2);
      ShaderGraphBlock var6 = handle(var1, var0, "theme_bottom", var2, 1, 3);
      ShaderGraphBlock var7 = handle(var1, var0, "exposed_float", var2, 2, 0);
      ShaderGraphBlock var8 = handle(var1, var0, "color_ramp", var2, 2, 2);
      ShaderGraphBlock var9 = handle(var1, var0, "bloom_lift", var2, 3, 2);
      ShaderGraphBlock var10 = handle(var1, var0, "output_color", var2, 4, 2);
      handle(var7, "Ribbon Alpha", 0.86F, 0.0F, 1.0F, 0.01F);
      var1.handle(var3.handle(), "uv", var4.handle(), "uv", var0);
      var1.handle(var4.handle(), "value", var8.handle(), "t", var0);
      var1.handle(var6.handle(), "color", var8.handle(), "a", var0);
      var1.handle(var5.handle(), "color", var8.handle(), "b", var0);
      var1.handle(var8.handle(), "color", var9.handle(), "color", var0);
      var1.handle(var9.handle(), "color", var10.handle(), "color", var0);
      var1.handle(var7.handle(), "value", var10.handle(), "alpha", var0);
      return var1;
   }

   private static void handle(ShaderGraph var0, String var1, String var2, LivePreviewRenderer var3, String var4) {
      var0.handle().handle(var1);
      var0.handle().compute(var2);
      var0.handle().resolve(var4);
      var0.handle().update("preset");
      if (var3 != null) {
         var0.handle(var3.handle());
      }
   }

   private static ShaderGraphBlock handle(ShaderGraph var0, ShaderNodeRegistry var1, String var2, BuiltinThemePresets.DataRecord var3, int var4, int var5) {
      return var0.handle(var2, var3.x(var4), var3.y(var5), var1);
   }

   private static void handle(ShaderGraphBlock var0, String var1, float var2, float var3, float var4, float var5) {
      var0.process("name", var1);
      var0.process("value", var2);
      var0.process("min", var3);
      var0.process("max", var4);
      var0.process("step", var5);
   }

   record DataRecord(float originX, float originY, float column, float row) {
      float x(int var1) {
         return this.originX + this.column * var1;
      }

      float y(int var1) {
         return this.originY + this.row * var1;
      }
   }

   public record PrimaryDataRecord(
      String title, String description, LivePreviewRenderer target, String complexity, List<String> nodes, Function<ShaderNodeRegistry, ShaderGraph> builder
   ) {
   }
}
