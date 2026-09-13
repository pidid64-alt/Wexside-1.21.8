package ru.wild.render.shader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ShaderNodeRegistry {
   private final Map<String, ShaderNodeDefinition> instance = new LinkedHashMap<>();

   public ShaderNodeRegistry() {
      this.process();
   }

   public ShaderNodeDefinition handle(String var1) {
      return this.instance.get(var1);
   }

   public Collection<ShaderNodeDefinition> handle() {
      return this.instance.values();
   }

   public List<ShaderNodeDefinition> process(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.toLowerCase(Locale.ROOT).trim();
         ArrayList var3 = new ArrayList();

         for (ShaderNodeDefinition var5 : this.instance.values()) {
            if (var5.process().toLowerCase(Locale.ROOT).contains(var2)
               || var5.compute().toLowerCase(Locale.ROOT).contains(var2)
               || var5.handle().toLowerCase(Locale.ROOT).contains(var2)) {
               var3.add(var5);
            }
         }

         return var3;
      } else {
         return new ArrayList<>(this.instance.values());
      }
   }

   public void handle(ShaderNodeDefinition var1) {
      this.instance.put(var1.handle(), var1);
   }

   private void process() {
      this.handle(
         new ShaderNodeDefinition(
            "input_uv",
            "Centered UV",
            "Inputs",
            164.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "uv"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_global_uv",
            "Global Screen UV",
            "Inputs",
            180.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "globalUv"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_screen_uv",
            "Screen UV",
            "Inputs",
            180.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "globalUv"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_time",
            "Time",
            "Inputs",
            164.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("time", "time", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "u_Time"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_mouse",
            "Mouse",
            "Inputs",
            164.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("mouse", "mouse", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "(u_Mouse / max(u_Resolution, vec2(1.0)))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_global_mouse",
            "Global Mouse",
            "Inputs",
            178.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("mouse", "mouse", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "((u_ElementRect.xy + u_Mouse) / max(u_Resolution, vec2(1.0)))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_element_uv",
            "Element UV",
            "Context",
            174.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "normalizedUv"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_element_centered_uv",
            "Element Centered UV",
            "Context",
            204.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "(normalizedUv - 0.5)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_element_rect",
            "Element Rect",
            "Context",
            184.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("rect", "rect", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "u_ElementRect"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_element_size",
            "Element Size",
            "Context",
            184.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("size", "size", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "u_ElementRect.zw"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_element_radius",
            "Element Radius",
            "Context",
            190.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("radius", "radius", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "u_ElementRadius"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_local_mouse",
            "Local Mouse",
            "Context",
            180.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("mouse", "mouse", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "clamp(u_Mouse / max(u_ElementRect.zw, vec2(1.0)), vec2(0.0), vec2(1.0))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_aspect",
            "Element Aspect",
            "Context",
            186.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("aspect", "aspect", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(u_ElementRect.z / max(u_ElementRect.w, 1.0))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "theme_top",
            "Accent Top",
            "Inputs",
            174.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(u_AccentTop, 1.0)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "theme_bottom",
            "Accent Bottom",
            "Inputs",
            174.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(u_AccentBottom, 1.0)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "theme_panel",
            "Theme Panel",
            "Inputs",
            174.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "u_ThemeColors[0]"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "exposed_float",
            "Exposed Float",
            "Inputs",
            188.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.handle(var1)
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "exposed_color",
            "Exposed Color",
            "Inputs",
            194.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> var0.handle(var1)
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "float_value",
            "Float",
            "Constants",
            154.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.handle(var1.handle("value", 0.5F))
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec2_value",
            "Vec2",
            "Constants",
            168.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.5"), ShaderPinDefinition.input("y", "y", ShaderValueType.FLOAT, "0.5")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "vec2(" + var0.handle(var1, "x") + ", " + var0.handle(var1, "y") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec3_value",
            "Vec3",
            "Constants",
            168.0F,
            List.of(
               ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("y", "y", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("z", "z", ShaderValueType.FLOAT, "0.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "vec3(" + var0.handle(var1, "x") + ", " + var0.handle(var1, "y") + ", " + var0.handle(var1, "z") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec4_value",
            "Vec4",
            "Constants",
            174.0F,
            List.of(
               ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "1.0"),
               ShaderPinDefinition.input("y", "y", ShaderValueType.FLOAT, "1.0"),
               ShaderPinDefinition.input("z", "z", ShaderValueType.FLOAT, "1.0"),
               ShaderPinDefinition.input("w", "w", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4("
               + var0.handle(var1, "x")
               + ", "
               + var0.handle(var1, "y")
               + ", "
               + var0.handle(var1, "z")
               + ", "
               + var0.handle(var1, "w")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "base_texture",
            "Base Texture",
            "Texture",
            190.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "texture(u_DiffuseMap, wild_diffuse_uv())"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_entity_mask",
            "Entity Mask",
            "Entity Context",
            188.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("mask", "mask", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "step(0.001, texture(u_DiffuseMap, wild_diffuse_uv()).a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_depth",
            "Depth",
            "Entity Context",
            164.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("depth", "depth", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "clamp(1.0 - texture(u_DiffuseMap, wild_diffuse_uv()).a, 0.0, 1.0)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_camera_distance",
            "Camera Distance",
            "Entity Context",
            196.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("distance", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "length((wild_screen_px() - u_Resolution * 0.5) / max(u_Resolution.y, 1.0))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_camera_dir",
            "Camera Direction",
            "World Context",
            198.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("dir", "dir", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "normalize(vec3(wild_view_dir(vUv), 1.0))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_sun_dir",
            "Sun Direction",
            "World Context",
            184.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("dir", "dir", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "normalize(vec3(cos(u_Time * 0.04), 0.42, sin(u_Time * 0.04)))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_world_time",
            "World Time",
            "World Context",
            178.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("time", "time", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "fract(u_Time * 0.012)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_rain",
            "Rain Strength",
            "World Context",
            178.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("rain", "rain", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "0.0"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_biome_tint",
            "Biome Tint",
            "World Context",
            178.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(mix(u_AccentBottom, u_AccentTop, 0.35), 1.0)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_alpha",
            "Alpha Channel",
            "Texture",
            184.0F,
            List.of(ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(1.0)")),
            List.of(ShaderPinDefinition.output("alpha", "alpha", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "color") + ").a"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "float_add",
            "Add Float",
            "Math",
            168.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "0.0"), ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " + " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "float_mul",
            "Multiply Float",
            "Math",
            182.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "1.0"), ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " * " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "float_sin",
            "Sine",
            "Math",
            164.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0"), ShaderPinDefinition.input("freq", "freq", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(0.5 + 0.5 * sin((" + var0.handle(var1, "x") + ") * (" + var0.handle(var1, "freq") + ")))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "float_smoothstep",
            "Smoothstep",
            "Math",
            188.0F,
            List.of(
               ShaderPinDefinition.input("edge0", "edge0", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("edge1", "edge1", ShaderValueType.FLOAT, "1.0"),
               ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "smoothstep(" + var0.handle(var1, "edge0") + ", " + var0.handle(var1, "edge1") + ", " + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec4_mix",
            "Mix Color",
            "Color",
            184.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC4, "vec4(0.0, 0.0, 0.0, 1.0)"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.VEC4, "vec4(1.0, 1.0, 1.0, 1.0)"),
               ShaderPinDefinition.input("t", "t", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "mix(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ", clamp(" + var0.handle(var1, "t") + ", 0.0, 1.0))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_fill",
            "SDF Fill",
            "Color",
            184.0F,
            List.of(
               ShaderPinDefinition.input("mask", "distance", ShaderValueType.FLOAT, "-1.0"),
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "u_ThemeColors[0]"),
               ShaderPinDefinition.input("alpha", "alpha", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(("
               + var0.handle(var1, "color")
               + ").rgb, ("
               + var0.handle(var1, "color")
               + ").a * wild_sdf_alpha("
               + var0.handle(var1, "mask")
               + ") * wild_sat("
               + var0.handle(var1, "alpha")
               + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "element_mask",
            "Element Mask",
            "Base Shape",
            184.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_element_distance()"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "element_alpha",
            "Element Alpha",
            "Base Shape",
            184.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("alpha", "alpha", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_sdf_alpha(wild_element_distance())"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "element_inner_mask",
            "Inset Element Mask",
            "Base Shape",
            206.0F,
            List.of(ShaderPinDefinition.input("inset", "inset", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_element_distance_inset(" + var0.handle(var1, "inset") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "glass_surface",
            "Mica Glass Surface",
            "Material",
            214.0F,
            List.of(
               ShaderPinDefinition.input("mask", "mask", ShaderValueType.FLOAT, "wild_element_distance()"),
               ShaderPinDefinition.input("tint", "tint", ShaderValueType.VEC4, "u_ThemeColors[0]"),
               ShaderPinDefinition.input("opacity", "opacity", ShaderValueType.FLOAT, "0.58"),
               ShaderPinDefinition.input("grain", "grain", ShaderValueType.FLOAT, "0.045")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_glass_surface("
               + var0.handle(var1, "mask")
               + ", "
               + var0.handle(var1, "tint")
               + ", "
               + var0.handle(var1, "opacity")
               + ", "
               + var0.handle(var1, "grain")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "rim_light",
            "Rim Light",
            "Material",
            196.0F,
            List.of(
               ShaderPinDefinition.input("mask", "mask", ShaderValueType.FLOAT, "wild_element_distance()"),
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(u_AccentTop, 1.0)"),
               ShaderPinDefinition.input("thickness", "width", ShaderValueType.FLOAT, "1.0"),
               ShaderPinDefinition.input("intensity", "power", ShaderValueType.FLOAT, "0.18")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_rim_light("
               + var0.handle(var1, "mask")
               + ", "
               + var0.handle(var1, "color")
               + ", "
               + var0.handle(var1, "thickness")
               + ", "
               + var0.handle(var1, "intensity")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "hover_glow",
            "Magnetic Hover Glow",
            "Material",
            220.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "normalizedUv"),
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(u_AccentBottom, 1.0)"),
               ShaderPinDefinition.input("radius", "radius", ShaderValueType.FLOAT, "0.42"),
               ShaderPinDefinition.input("intensity", "power", ShaderValueType.FLOAT, "0.58")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_hover_glow("
               + var0.handle(var1, "uv")
               + ", "
               + var0.handle(var1, "color")
               + ", "
               + var0.handle(var1, "radius")
               + ", "
               + var0.handle(var1, "intensity")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "inner_shadow",
            "Inner Shadow",
            "Material",
            198.0F,
            List.of(
               ShaderPinDefinition.input("mask", "mask", ShaderValueType.FLOAT, "wild_element_distance()"),
               ShaderPinDefinition.input("strength", "power", ShaderValueType.FLOAT, "0.22"),
               ShaderPinDefinition.input("width", "width", ShaderValueType.FLOAT, "12.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_inner_shadow("
               + var0.handle(var1, "mask")
               + ", "
               + var0.handle(var1, "strength")
               + ", "
               + var0.handle(var1, "width")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "exposure_lift",
            "Photographic Exposure",
            "Material",
            226.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "u_ThemeColors[0]"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "0.18"),
               ShaderPinDefinition.input("decay", "decay", ShaderValueType.FLOAT, "2.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_exposure_lift("
               + var0.handle(var1, "color")
               + ", "
               + var0.handle(var1, "amount")
               + ", "
               + var0.handle(var1, "decay")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "alpha_blend",
            "Alpha Blend",
            "Blend",
            190.0F,
            List.of(
               ShaderPinDefinition.input("base", "base", ShaderValueType.VEC4, "vec4(0.0)"),
               ShaderPinDefinition.input("layer", "layer", ShaderValueType.VEC4, "vec4(1.0)")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_alpha_over(" + var0.handle(var1, "base") + ", " + var0.handle(var1, "layer") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_ramp",
            "Color Ramp",
            "Color",
            184.0F,
            List.of(
               ShaderPinDefinition.input("t", "t", ShaderValueType.FLOAT, "0.5"),
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC4, "vec4(u_AccentBottom, 1.0)"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.VEC4, "vec4(u_AccentTop, 1.0)")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "mix(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ", wild_sat(" + var0.handle(var1, "t") + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_multiply_scalar",
            "Color Multiply",
            "Color",
            198.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(1.0)"),
               ShaderPinDefinition.input("factor", "factor", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(("
               + var0.handle(var1, "color")
               + ").rgb * "
               + var0.handle(var1, "factor")
               + ", ("
               + var0.handle(var1, "color")
               + ").a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "blend_screen",
            "Screen Blend",
            "Blend",
            188.0F,
            List.of(
               ShaderPinDefinition.input("base", "base", ShaderValueType.VEC4, "vec4(0.02, 0.022, 0.028, 1.0)"),
               ShaderPinDefinition.input("layer", "layer", ShaderValueType.VEC4, "vec4(u_AccentTop, 1.0)"),
               ShaderPinDefinition.input("opacity", "opacity", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_blend_screen("
               + var0.handle(var1, "base")
               + ", "
               + var0.handle(var1, "layer")
               + ", "
               + var0.handle(var1, "opacity")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "blend_overlay",
            "Overlay Blend",
            "Blend",
            188.0F,
            List.of(
               ShaderPinDefinition.input("base", "base", ShaderValueType.VEC4, "vec4(0.02, 0.022, 0.028, 1.0)"),
               ShaderPinDefinition.input("layer", "layer", ShaderValueType.VEC4, "vec4(u_AccentBottom, 1.0)"),
               ShaderPinDefinition.input("opacity", "opacity", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_blend_overlay("
               + var0.handle(var1, "base")
               + ", "
               + var0.handle(var1, "layer")
               + ", "
               + var0.handle(var1, "opacity")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_circle",
            "SDF Circle",
            "SDF",
            184.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "uv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.0)"),
               ShaderPinDefinition.input("radius", "radius", ShaderValueType.FLOAT, "0.25"),
               ShaderPinDefinition.input("softness", "soft", ShaderValueType.FLOAT, "0.08")
            ),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.process()
               ? "wild_sdf_circle("
                  + var0.handle(var1, "uv")
                  + ", ("
                  + var0.handle(var1, "center")
                  + ") * (u_ElementRect.zw * 0.5), ("
                  + var0.handle(var1, "radius")
                  + ") * min(u_ElementRect.z, u_ElementRect.w) * 0.5, "
                  + var0.handle(var1, "softness")
                  + ")"
               : "wild_sdf_circle("
                  + var0.handle(var1, "uv")
                  + ", "
                  + var0.handle(var1, "center")
                  + ", "
                  + var0.handle(var1, "radius")
                  + ", "
                  + var0.handle(var1, "softness")
                  + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_round_box",
            "SDF Rounded Box",
            "SDF",
            196.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "uv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.0)"),
               ShaderPinDefinition.input("size", "size", ShaderValueType.VEC2, "vec2(0.95, 0.32)"),
               ShaderPinDefinition.input("radius", "radius", ShaderValueType.FLOAT, "0.08"),
               ShaderPinDefinition.input("softness", "soft", ShaderValueType.FLOAT, "0.06")
            ),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.process()
               ? "wild_sdf_round_box("
                  + var0.handle(var1, "uv")
                  + ", ("
                  + var0.handle(var1, "center")
                  + ") * (u_ElementRect.zw * 0.5), ("
                  + var0.handle(var1, "size")
                  + ") * (u_ElementRect.zw * 0.5), ("
                  + var0.handle(var1, "radius")
                  + ") * min(u_ElementRect.z, u_ElementRect.w) * 0.5, "
                  + var0.handle(var1, "softness")
                  + ")"
               : "wild_sdf_round_box("
                  + var0.handle(var1, "uv")
                  + ", "
                  + var0.handle(var1, "center")
                  + ", "
                  + var0.handle(var1, "size")
                  + ", "
                  + var0.handle(var1, "radius")
                  + ", "
                  + var0.handle(var1, "softness")
                  + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "simplex_noise_3d",
            "Simplex Noise 3D",
            "Generator",
            202.0F,
            List.of(
               ShaderPinDefinition.input("p", "p", ShaderValueType.VEC3, "vec3(globalUv * 2.0, u_Time * 0.08)"),
               ShaderPinDefinition.input("scale", "scale", ShaderValueType.FLOAT, "3.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(0.5 + 0.5 * wild_simplex3(" + var0.handle(var1, "p") + " * " + var0.handle(var1, "scale") + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "voronoi",
            "Voronoi",
            "Generator",
            190.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "globalUv"),
               ShaderPinDefinition.input("scale", "scale", ShaderValueType.FLOAT, "7.0"),
               ShaderPinDefinition.input("time", "time", ShaderValueType.FLOAT, "u_Time")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_voronoi(" + var0.handle(var1, "uv") + " * " + var0.handle(var1, "scale") + ", " + var0.handle(var1, "time") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "chromatic_aberration",
            "Chromatic Aberration",
            "VFX",
            218.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(u_AccentTop, 1.0)"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "0.08"),
               ShaderPinDefinition.input("phase", "phase", ShaderValueType.FLOAT, "u_Time")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_chromatic("
               + var0.handle(var1, "color")
               + ", vUv, "
               + var0.handle(var1, "amount")
               + ", "
               + var0.handle(var1, "phase")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "output_color",
            "Master Output",
            "Output",
            184.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(0.02, 0.022, 0.028, 1.0)"),
               ShaderPinDefinition.input("alpha", "alpha", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(),
            (var0, var1, var2) -> "vec4((" + var0.handle(var1, "color") + ").rgb, (" + var0.handle(var1, "color") + ").a * " + var0.handle(var1, "alpha") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_triangle",
            "SDF Triangle",
            "SDF",
            196.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "uv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.0)"),
               ShaderPinDefinition.input("radius", "radius", ShaderValueType.FLOAT, "0.35"),
               ShaderPinDefinition.input("softness", "soft", ShaderValueType.FLOAT, "0.05")
            ),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.process()
               ? "wild_sdf_triangle("
                  + var0.handle(var1, "uv")
                  + ", ("
                  + var0.handle(var1, "center")
                  + ") * (u_ElementRect.zw * 0.5), ("
                  + var0.handle(var1, "radius")
                  + ") * min(u_ElementRect.z, u_ElementRect.w) * 0.5, "
                  + var0.handle(var1, "softness")
                  + ")"
               : "wild_sdf_triangle("
                  + var0.handle(var1, "uv")
                  + ", "
                  + var0.handle(var1, "center")
                  + ", "
                  + var0.handle(var1, "radius")
                  + ", "
                  + var0.handle(var1, "softness")
                  + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_hex",
            "SDF Hexagon",
            "SDF",
            196.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "uv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.0)"),
               ShaderPinDefinition.input("radius", "radius", ShaderValueType.FLOAT, "0.28"),
               ShaderPinDefinition.input("softness", "soft", ShaderValueType.FLOAT, "0.05")
            ),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.process()
               ? "wild_sdf_hex("
                  + var0.handle(var1, "uv")
                  + ", ("
                  + var0.handle(var1, "center")
                  + ") * (u_ElementRect.zw * 0.5), ("
                  + var0.handle(var1, "radius")
                  + ") * min(u_ElementRect.z, u_ElementRect.w) * 0.5, "
                  + var0.handle(var1, "softness")
                  + ")"
               : "wild_sdf_hex("
                  + var0.handle(var1, "uv")
                  + ", "
                  + var0.handle(var1, "center")
                  + ", "
                  + var0.handle(var1, "radius")
                  + ", "
                  + var0.handle(var1, "softness")
                  + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "fbm_noise",
            "FBM Noise",
            "Generator",
            196.0F,
            List.of(
               ShaderPinDefinition.input("p", "p", ShaderValueType.VEC3, "vec3(globalUv * 3.0, u_Time * 0.12)"),
               ShaderPinDefinition.input("octaves", "oct", ShaderValueType.FLOAT, "5.0"),
               ShaderPinDefinition.input("scale", "scale", ShaderValueType.FLOAT, "1.8")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_fbm("
               + var0.handle(var1, "p")
               + " * "
               + var0.handle(var1, "scale")
               + ", int(clamp("
               + var0.handle(var1, "octaves")
               + ", 1.0, 8.0)))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "polar_uv",
            "Polar UV",
            "Coords",
            184.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "vUv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.5)")
            ),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "wild_polar(" + var0.handle(var1, "uv") + ", " + var0.handle(var1, "center") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "rotate_uv",
            "Rotate UV",
            "Coords",
            186.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "vUv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.5)"),
               ShaderPinDefinition.input("angle", "angle", ShaderValueType.FLOAT, "u_Time")
            ),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "wild_rotate_uv(" + var0.handle(var1, "uv") + ", " + var0.handle(var1, "center") + ", " + var0.handle(var1, "angle") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "twist_uv",
            "Twist UV",
            "Coords",
            196.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "vUv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.5)"),
               ShaderPinDefinition.input("strength", "strength", ShaderValueType.FLOAT, "2.6")
            ),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "wild_twist_uv(" + var0.handle(var1, "uv") + ", " + var0.handle(var1, "center") + ", " + var0.handle(var1, "strength") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vignette",
            "Vignette",
            "VFX",
            196.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "vUv"),
               ShaderPinDefinition.input("intensity", "intensity", ShaderValueType.FLOAT, "1.0"),
               ShaderPinDefinition.input("falloff", "falloff", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_vignette("
               + var0.handle(var1, "uv")
               + ", "
               + var0.handle(var1, "intensity")
               + ", "
               + var0.handle(var1, "falloff")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "posterize",
            "Posterize",
            "VFX",
            196.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(0.5)"),
               ShaderPinDefinition.input("steps", "steps", ShaderValueType.FLOAT, "6.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(floor("
               + var0.handle(var1, "color")
               + ".rgb * max("
               + var0.handle(var1, "steps")
               + ", 1.0)) / max("
               + var0.handle(var1, "steps")
               + ", 1.0), "
               + var0.handle(var1, "color")
               + ".a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "bloom_lift",
            "Bloom Lift",
            "VFX",
            196.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(0.5)"),
               ShaderPinDefinition.input("threshold", "threshold", ShaderValueType.FLOAT, "0.6"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "1.2")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_bloom_lift("
               + var0.handle(var1, "color")
               + ", "
               + var0.handle(var1, "threshold")
               + ", "
               + var0.handle(var1, "amount")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_pulse",
            "Color Pulse",
            "Color",
            196.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC4, "vec4(u_AccentBottom, 1.0)"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.VEC4, "vec4(u_AccentTop, 1.0)"),
               ShaderPinDefinition.input("speed", "speed", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "mix("
               + var0.handle(var1, "a")
               + ", "
               + var0.handle(var1, "b")
               + ", 0.5 + 0.5 * sin(u_Time * "
               + var0.handle(var1, "speed")
               + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_screen_split",
            "Channel Split",
            "Color",
            196.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(0.5)"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "0.04")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_channel_split(" + var0.handle(var1, "color") + ", " + var0.handle(var1, "amount") + ", u_Time)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "fresnel",
            "Fresnel Rim",
            "VFX",
            196.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "vUv"), ShaderPinDefinition.input("power", "power", ShaderValueType.FLOAT, "3.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "pow(max(1.0 - 2.0 * length(" + var0.handle(var1, "uv") + " - 0.5), 0.0), max(" + var0.handle(var1, "power") + ", 0.001))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "step_threshold",
            "Step Threshold",
            "Math",
            184.0F,
            List.of(ShaderPinDefinition.input("edge", "edge", ShaderValueType.FLOAT, "0.5"), ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.5")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "step(" + var0.handle(var1, "edge") + ", " + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "fract_node",
            "Fract",
            "Math",
            174.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "fract(" + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "abs_node",
            "Abs",
            "Math",
            168.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "abs(" + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "saturate_node",
            "Saturate",
            "Math",
            174.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "clamp(" + var0.handle(var1, "x") + ", 0.0, 1.0)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "blend_multiply",
            "Multiply Blend",
            "Blend",
            196.0F,
            List.of(
               ShaderPinDefinition.input("base", "base", ShaderValueType.VEC4, "vec4(0.02, 0.022, 0.028, 1.0)"),
               ShaderPinDefinition.input("layer", "layer", ShaderValueType.VEC4, "vec4(u_AccentTop, 1.0)"),
               ShaderPinDefinition.input("opacity", "opacity", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(mix("
               + var0.handle(var1, "base")
               + ".rgb, "
               + var0.handle(var1, "base")
               + ".rgb * "
               + var0.handle(var1, "layer")
               + ".rgb, clamp("
               + var0.handle(var1, "opacity")
               + ", 0.0, 1.0)), max("
               + var0.handle(var1, "base")
               + ".a, "
               + var0.handle(var1, "layer")
               + ".a))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "blend_add",
            "Additive Blend",
            "Blend",
            196.0F,
            List.of(
               ShaderPinDefinition.input("base", "base", ShaderValueType.VEC4, "vec4(0.02, 0.022, 0.028, 1.0)"),
               ShaderPinDefinition.input("layer", "layer", ShaderValueType.VEC4, "vec4(u_AccentTop, 1.0)"),
               ShaderPinDefinition.input("opacity", "opacity", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(clamp("
               + var0.handle(var1, "base")
               + ".rgb + "
               + var0.handle(var1, "layer")
               + ".rgb * clamp("
               + var0.handle(var1, "opacity")
               + ", 0.0, 1.0), 0.0, 1.0), max("
               + var0.handle(var1, "base")
               + ".a, "
               + var0.handle(var1, "layer")
               + ".a))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "iridescence",
            "Iridescence",
            "Color",
            200.0F,
            List.of(
               ShaderPinDefinition.input("t", "t", ShaderValueType.FLOAT, "0.5"), ShaderPinDefinition.input("speed", "speed", ShaderValueType.FLOAT, "0.8")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_iridescence(" + var0.handle(var1, "t") + ", " + var0.handle(var1, "speed") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_union",
            "SDF Union",
            "SDF Booleans",
            196.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("smoothness", "smooth", ShaderValueType.FLOAT, "0.05")
            ),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.process()
               ? "wild_sdf_union("
                  + var0.handle(var1, "a")
                  + ", "
                  + var0.handle(var1, "b")
                  + ", ("
                  + var0.handle(var1, "smoothness")
                  + ") * min(u_ElementRect.z, u_ElementRect.w) * 0.5)"
               : "wild_sdf_union(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ", " + var0.handle(var1, "smoothness") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_subtract",
            "SDF Subtract",
            "SDF Booleans",
            200.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("smoothness", "smooth", ShaderValueType.FLOAT, "0.05")
            ),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.process()
               ? "wild_sdf_subtract("
                  + var0.handle(var1, "a")
                  + ", "
                  + var0.handle(var1, "b")
                  + ", ("
                  + var0.handle(var1, "smoothness")
                  + ") * min(u_ElementRect.z, u_ElementRect.w) * 0.5)"
               : "wild_sdf_subtract(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ", " + var0.handle(var1, "smoothness") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_intersect",
            "SDF Intersect",
            "SDF Booleans",
            204.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("smoothness", "smooth", ShaderValueType.FLOAT, "0.05")
            ),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.process()
               ? "wild_sdf_intersect("
                  + var0.handle(var1, "a")
                  + ", "
                  + var0.handle(var1, "b")
                  + ", ("
                  + var0.handle(var1, "smoothness")
                  + ") * min(u_ElementRect.z, u_ElementRect.w) * 0.5)"
               : "wild_sdf_intersect(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ", " + var0.handle(var1, "smoothness") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "sdf_star",
            "SDF Star",
            "SDF",
            198.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "uv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.0)"),
               ShaderPinDefinition.input("radius", "radius", ShaderValueType.FLOAT, "0.32"),
               ShaderPinDefinition.input("points", "points", ShaderValueType.FLOAT, "5.0"),
               ShaderPinDefinition.input("softness", "soft", ShaderValueType.FLOAT, "0.05")
            ),
            List.of(ShaderPinDefinition.output("mask", "distance", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> var0.process()
               ? "wild_sdf_star("
                  + var0.handle(var1, "uv")
                  + ", ("
                  + var0.handle(var1, "center")
                  + ") * (u_ElementRect.zw * 0.5), ("
                  + var0.handle(var1, "radius")
                  + ") * min(u_ElementRect.z, u_ElementRect.w) * 0.5, "
                  + var0.handle(var1, "points")
                  + ", "
                  + var0.handle(var1, "softness")
                  + ")"
               : "wild_sdf_star("
                  + var0.handle(var1, "uv")
                  + ", "
                  + var0.handle(var1, "center")
                  + ", "
                  + var0.handle(var1, "radius")
                  + ", "
                  + var0.handle(var1, "points")
                  + ", "
                  + var0.handle(var1, "softness")
                  + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_remap",
            "Remap",
            "Math",
            196.0F,
            List.of(
               ShaderPinDefinition.input("v", "v", ShaderValueType.FLOAT, "0.5"),
               ShaderPinDefinition.input("inMin", "inMin", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("inMax", "inMax", ShaderValueType.FLOAT, "1.0"),
               ShaderPinDefinition.input("outMin", "outMin", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("outMax", "outMax", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_remap("
               + var0.handle(var1, "v")
               + ", "
               + var0.handle(var1, "inMin")
               + ", "
               + var0.handle(var1, "inMax")
               + ", "
               + var0.handle(var1, "outMin")
               + ", "
               + var0.handle(var1, "outMax")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_clamp",
            "Clamp",
            "Math",
            184.0F,
            List.of(
               ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.5"),
               ShaderPinDefinition.input("min", "min", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("max", "max", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "clamp(" + var0.handle(var1, "x") + ", " + var0.handle(var1, "min") + ", " + var0.handle(var1, "max") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_floor",
            "Floor",
            "Math",
            168.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "floor(" + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_ceil",
            "Ceil",
            "Math",
            168.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "ceil(" + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_length",
            "Length",
            "Math",
            184.0F,
            List.of(ShaderPinDefinition.input("v", "v", ShaderValueType.VEC2, "vUv - 0.5")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "length(" + var0.handle(var1, "v") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_distance",
            "Distance",
            "Math",
            192.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.VEC2, "vUv"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC2, "vec2(0.5)")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "distance(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_power",
            "Power",
            "Math",
            192.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.5"), ShaderPinDefinition.input("y", "y", ShaderValueType.FLOAT, "2.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "pow(max(" + var0.handle(var1, "x") + ", 0.0), " + var0.handle(var1, "y") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_mod",
            "Mod",
            "Math",
            188.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0"), ShaderPinDefinition.input("y", "y", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "mod(" + var0.handle(var1, "x") + ", (abs(" + var0.handle(var1, "y") + ") < 1e-5 ? 1e-5 : " + var0.handle(var1, "y") + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_dot",
            "Dot",
            "Math",
            188.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.VEC2, "vUv"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC2, "vec2(1.0)")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "dot(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_subtract",
            "Subtract",
            "Math",
            184.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "0.0"), ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " - " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_divide",
            "Divide",
            "Math",
            184.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "1.0"), ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " / max(" + var0.handle(var1, "b") + ", 1e-5))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_gradient_map",
            "Gradient Map",
            "Color",
            220.0F,
            List.of(
               ShaderPinDefinition.input("t", "t", ShaderValueType.FLOAT, "0.5"),
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC4, "vec4(u_AccentBottom, 1.0)"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.VEC4, "vec4(u_AccentTop, 1.0)"),
               ShaderPinDefinition.input("c", "c", ShaderValueType.VEC4, "vec4(1.0)")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "wild_gradient_map("
               + var0.handle(var1, "t")
               + ", "
               + var0.handle(var1, "a")
               + ", "
               + var0.handle(var1, "b")
               + ", "
               + var0.handle(var1, "c")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_desaturate",
            "Desaturate",
            "Color",
            192.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(1.0)"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(wild_desaturate("
               + var0.handle(var1, "color")
               + ".rgb, "
               + var0.handle(var1, "amount")
               + "), "
               + var0.handle(var1, "color")
               + ".a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_invert",
            "Invert",
            "Color",
            188.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(0.5)"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(wild_invert("
               + var0.handle(var1, "color")
               + ".rgb, "
               + var0.handle(var1, "amount")
               + "), "
               + var0.handle(var1, "color")
               + ".a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "color_hsv",
            "HSV → RGB",
            "Color",
            196.0F,
            List.of(
               ShaderPinDefinition.input("h", "hue", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("s", "sat", ShaderValueType.FLOAT, "1.0"),
               ShaderPinDefinition.input("v", "val", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(wild_hsv2rgb(vec3("
               + var0.handle(var1, "h")
               + ", "
               + var0.handle(var1, "s")
               + ", "
               + var0.handle(var1, "v")
               + ")), 1.0)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "time_bpm",
            "BPM Sync",
            "Time",
            192.0F,
            List.of(
               ShaderPinDefinition.input("bpm", "bpm", ShaderValueType.FLOAT, "128.0"),
               ShaderPinDefinition.input("strength", "shape", ShaderValueType.FLOAT, "2.0")
            ),
            List.of(ShaderPinDefinition.output("pulse", "pulse", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_bpm(" + var0.handle(var1, "bpm") + ", " + var0.handle(var1, "strength") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "time_pulse",
            "Pulse",
            "Time",
            184.0F,
            List.of(
               ShaderPinDefinition.input("t", "t", ShaderValueType.FLOAT, "u_Time"), ShaderPinDefinition.input("duty", "duty", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_pulse(" + var0.handle(var1, "t") + ", " + var0.handle(var1, "duty") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_view_dir",
            "View Direction",
            "Inputs",
            178.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("dir", "dir", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "wild_view_dir(vUv)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_normal",
            "Normal (UV slope)",
            "Inputs",
            192.0F,
            List.of(ShaderPinDefinition.input("strength", "strength", ShaderValueType.FLOAT, "4.0")),
            List.of(ShaderPinDefinition.output("normal", "normal", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "wild_normal_from_uv(vUv, " + var0.handle(var1, "strength") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_resolution",
            "Resolution",
            "Inputs",
            178.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("res", "res", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "u_Resolution"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "input_alpha",
            "Alpha",
            "Inputs",
            178.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("alpha", "alpha", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "u_Alpha"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec2_split",
            "Split Vec2",
            "Math",
            188.0F,
            List.of(ShaderPinDefinition.input("v", "v", ShaderValueType.VEC2, "vUv")),
            List.of(ShaderPinDefinition.output("x", "x", ShaderValueType.FLOAT), ShaderPinDefinition.output("y", "y", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "x".equals(var2) ? "(" + var0.handle(var1, "v") + ").x" : "(" + var0.handle(var1, "v") + ").y"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_max",
            "Max",
            "Math",
            184.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "0.0"), ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "max(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_min",
            "Min",
            "Math",
            184.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "0.0"), ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "min(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_sign",
            "Sign",
            "Math",
            172.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "sign(" + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_sqrt",
            "Square Root",
            "Math",
            178.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "sqrt(max(" + var0.handle(var1, "x") + ", 0.0))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_exp",
            "Exp",
            "Math",
            172.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "exp(" + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_log",
            "Log",
            "Math",
            172.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "log(max(" + var0.handle(var1, "x") + ", 1e-6))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_one_minus",
            "One Minus",
            "Math",
            178.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(1.0 - " + var0.handle(var1, "x") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_reciprocal",
            "Reciprocal",
            "Math",
            184.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(1.0 / (abs(" + var0.handle(var1, "x") + ") < 1e-5 ? 1e-5 : " + var0.handle(var1, "x") + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_round",
            "Round",
            "Math",
            172.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "floor(" + var0.handle(var1, "x") + " + 0.5)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_cos",
            "Cosine",
            "Math",
            172.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0"), ShaderPinDefinition.input("freq", "freq", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "cos((" + var0.handle(var1, "x") + ") * (" + var0.handle(var1, "freq") + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_sin_raw",
            "Sine Raw",
            "Math",
            178.0F,
            List.of(ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0"), ShaderPinDefinition.input("freq", "freq", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "sin((" + var0.handle(var1, "x") + ") * (" + var0.handle(var1, "freq") + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "math_atan2",
            "Atan2",
            "Math",
            178.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "1.0"), ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "atan(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_add3",
            "Add Vec3",
            "Vector",
            178.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC3, "vec3(0.0)"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC3, "vec3(0.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " + " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_sub3",
            "Subtract Vec3",
            "Vector",
            190.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC3, "vec3(0.0)"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC3, "vec3(0.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " - " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_mul3",
            "Multiply Vec3",
            "Vector",
            190.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC3, "vec3(1.0)"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC3, "vec3(1.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " * " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_scale3",
            "Scale Vec3",
            "Vector",
            184.0F,
            List.of(ShaderPinDefinition.input("v", "v", ShaderValueType.VEC3, "vec3(1.0)"), ShaderPinDefinition.input("s", "s", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "v") + " * " + var0.handle(var1, "s") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_normalize",
            "Normalize",
            "Vector",
            184.0F,
            List.of(ShaderPinDefinition.input("v", "v", ShaderValueType.VEC3, "vec3(0.0,0.0,1.0)")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "normalize(" + var0.handle(var1, "v") + " + 1e-6)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_cross",
            "Cross",
            "Vector",
            178.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC3, "vec3(0.0)"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC3, "vec3(0.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "cross(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_reflect",
            "Reflect",
            "Vector",
            184.0F,
            List.of(
               ShaderPinDefinition.input("i", "i", ShaderValueType.VEC3, "vec3(0.0,0.0,-1.0)"),
               ShaderPinDefinition.input("n", "n", ShaderValueType.VEC3, "vec3(0.0,0.0,1.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "reflect(" + var0.handle(var1, "i") + ", normalize(" + var0.handle(var1, "n") + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_dot3",
            "Dot Vec3",
            "Vector",
            178.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC3, "vec3(0.0)"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC3, "vec3(0.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "dot(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec_lerp3",
            "Lerp Vec3",
            "Vector",
            184.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC3, "vec3(0.0)"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.VEC3, "vec3(0.0)"),
               ShaderPinDefinition.input("t", "t", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "mix(" + var0.handle(var1, "a") + ", " + var0.handle(var1, "b") + ", " + var0.handle(var1, "t") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec2_add",
            "Add Vec2",
            "Vector",
            178.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC2, "vec2(0.0)"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC2, "vec2(0.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " + " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec2_mul",
            "Multiply Vec2",
            "Vector",
            190.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC2, "vec2(1.0)"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC2, "vec2(1.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " * " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec4_add",
            "Add Vec4",
            "Vector",
            178.0F,
            List.of(
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC4, "vec4(0.0)"), ShaderPinDefinition.input("b", "b", ShaderValueType.VEC4, "vec4(0.0)")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "a") + " + " + var0.handle(var1, "b") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "vec4_scale",
            "Scale Vec4",
            "Vector",
            184.0F,
            List.of(ShaderPinDefinition.input("v", "v", ShaderValueType.VEC4, "vec4(1.0)"), ShaderPinDefinition.input("s", "s", ShaderValueType.FLOAT, "1.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "v") + " * " + var0.handle(var1, "s") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "combine_vec3",
            "Combine Vec3",
            "Vector",
            184.0F,
            List.of(
               ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("y", "y", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("z", "z", ShaderValueType.FLOAT, "0.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC3)),
            (var0, var1, var2) -> "vec3(" + var0.handle(var1, "x") + ", " + var0.handle(var1, "y") + ", " + var0.handle(var1, "z") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "combine_vec4",
            "Combine Vec4",
            "Vector",
            190.0F,
            List.of(
               ShaderPinDefinition.input("x", "x", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("y", "y", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("z", "z", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("w", "w", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4("
               + var0.handle(var1, "x")
               + ", "
               + var0.handle(var1, "y")
               + ", "
               + var0.handle(var1, "z")
               + ", "
               + var0.handle(var1, "w")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "split_vec3",
            "Split Vec3",
            "Vector",
            184.0F,
            List.of(ShaderPinDefinition.input("v", "v", ShaderValueType.VEC3, "vec3(0.0)")),
            List.of(
               ShaderPinDefinition.output("x", "x", ShaderValueType.FLOAT),
               ShaderPinDefinition.output("y", "y", ShaderValueType.FLOAT),
               ShaderPinDefinition.output("z", "z", ShaderValueType.FLOAT)
            ),
            (var0, var1, var2) -> "x".equals(var2)
               ? "(" + var0.handle(var1, "v") + ").x"
               : ("y".equals(var2) ? "(" + var0.handle(var1, "v") + ").y" : "(" + var0.handle(var1, "v") + ").z")
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "split_vec4",
            "Split Vec4",
            "Vector",
            190.0F,
            List.of(ShaderPinDefinition.input("v", "v", ShaderValueType.VEC4, "vec4(0.0)")),
            List.of(
               ShaderPinDefinition.output("x", "x", ShaderValueType.FLOAT),
               ShaderPinDefinition.output("y", "y", ShaderValueType.FLOAT),
               ShaderPinDefinition.output("z", "z", ShaderValueType.FLOAT),
               ShaderPinDefinition.output("w", "w", ShaderValueType.FLOAT)
            ),
            (var0, var1, var2) -> "x".equals(var2)
               ? "(" + var0.handle(var1, "v") + ").x"
               : (
                  "y".equals(var2)
                     ? "(" + var0.handle(var1, "v") + ").y"
                     : ("z".equals(var2) ? "(" + var0.handle(var1, "v") + ").z" : "(" + var0.handle(var1, "v") + ").w")
               )
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "uv_tiling_offset",
            "Tiling And Offset",
            "Coords",
            196.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "uv"),
               ShaderPinDefinition.input("tiling", "tiling", ShaderValueType.VEC2, "vec2(1.0)"),
               ShaderPinDefinition.input("offset", "offset", ShaderValueType.VEC2, "vec2(0.0)")
            ),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "uv") + " * " + var0.handle(var1, "tiling") + " + " + var0.handle(var1, "offset") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "uv_panner",
            "Panner",
            "Coords",
            186.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "uv"),
               ShaderPinDefinition.input("speed", "speed", ShaderValueType.VEC2, "vec2(0.1)"),
               ShaderPinDefinition.input("time", "time", ShaderValueType.FLOAT, "u_Time")
            ),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "(" + var0.handle(var1, "uv") + " + " + var0.handle(var1, "speed") + " * " + var0.handle(var1, "time") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "uv_radial_shear",
            "Radial Shear",
            "Coords",
            196.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "vUv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.5)"),
               ShaderPinDefinition.input("strength", "strength", ShaderValueType.FLOAT, "6.0")
            ),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "wild_radial_shear("
               + var0.handle(var1, "uv")
               + ", "
               + var0.handle(var1, "center")
               + ", "
               + var0.handle(var1, "strength")
               + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "uv_spherize",
            "Spherize",
            "Coords",
            190.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "vUv"),
               ShaderPinDefinition.input("center", "center", ShaderValueType.VEC2, "vec2(0.5)"),
               ShaderPinDefinition.input("strength", "strength", ShaderValueType.FLOAT, "0.5")
            ),
            List.of(ShaderPinDefinition.output("uv", "uv", ShaderValueType.VEC2)),
            (var0, var1, var2) -> "wild_spherize(" + var0.handle(var1, "uv") + ", " + var0.handle(var1, "center") + ", " + var0.handle(var1, "strength") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "checkerboard",
            "Checkerboard",
            "Generator",
            200.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "vUv"),
               ShaderPinDefinition.input("freq", "freq", ShaderValueType.VEC2, "vec2(6.0)"),
               ShaderPinDefinition.input("a", "a", ShaderValueType.VEC4, "vec4(0.05,0.05,0.06,1.0)"),
               ShaderPinDefinition.input("b", "b", ShaderValueType.VEC4, "vec4(u_AccentTop,1.0)")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "mix("
               + var0.handle(var1, "a")
               + ", "
               + var0.handle(var1, "b")
               + ", wild_checker("
               + var0.handle(var1, "uv")
               + ", "
               + var0.handle(var1, "freq")
               + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "simple_noise",
            "Simple Noise",
            "Generator",
            190.0F,
            List.of(
               ShaderPinDefinition.input("uv", "uv", ShaderValueType.VEC2, "globalUv"),
               ShaderPinDefinition.input("scale", "scale", ShaderValueType.FLOAT, "5.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "wild_gnoise2(" + var0.handle(var1, "uv") + " * " + var0.handle(var1, "scale") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "contrast",
            "Contrast",
            "Color",
            190.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(0.5)"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "1.2")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(("
               + var0.handle(var1, "color")
               + ".rgb - 0.5) * "
               + var0.handle(var1, "amount")
               + " + 0.5, "
               + var0.handle(var1, "color")
               + ".a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "saturation",
            "Saturation",
            "Color",
            190.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(0.5)"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "1.2")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(mix(vec3(dot("
               + var0.handle(var1, "color")
               + ".rgb, vec3(0.299,0.587,0.114))), "
               + var0.handle(var1, "color")
               + ".rgb, "
               + var0.handle(var1, "amount")
               + "), "
               + var0.handle(var1, "color")
               + ".a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "brightness",
            "Brightness",
            "Color",
            190.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(0.5)"),
               ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "0.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(" + var0.handle(var1, "color") + ".rgb + " + var0.handle(var1, "amount") + ", " + var0.handle(var1, "color") + ".a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "hue_shift",
            "Hue Shift",
            "Color",
            190.0F,
            List.of(
               ShaderPinDefinition.input("color", "color", ShaderValueType.VEC4, "vec4(u_AccentTop,1.0)"),
               ShaderPinDefinition.input("shift", "shift", ShaderValueType.FLOAT, "0.1")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(wild_hsv2rgb(vec3(fract(wild_rgb2hsv("
               + var0.handle(var1, "color")
               + ".rgb).x + "
               + var0.handle(var1, "shift")
               + "), wild_rgb2hsv("
               + var0.handle(var1, "color")
               + ".rgb).yz)), "
               + var0.handle(var1, "color")
               + ".a)"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "blend_lighten",
            "Lighten Blend",
            "Blend",
            196.0F,
            List.of(
               ShaderPinDefinition.input("base", "base", ShaderValueType.VEC4, "vec4(0.1)"),
               ShaderPinDefinition.input("layer", "layer", ShaderValueType.VEC4, "vec4(u_AccentTop,1.0)"),
               ShaderPinDefinition.input("opacity", "opacity", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(mix("
               + var0.handle(var1, "base")
               + ".rgb, max("
               + var0.handle(var1, "base")
               + ".rgb, "
               + var0.handle(var1, "layer")
               + ".rgb), clamp("
               + var0.handle(var1, "opacity")
               + ",0.0,1.0)), max("
               + var0.handle(var1, "base")
               + ".a, "
               + var0.handle(var1, "layer")
               + ".a))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "blend_darken",
            "Darken Blend",
            "Blend",
            196.0F,
            List.of(
               ShaderPinDefinition.input("base", "base", ShaderValueType.VEC4, "vec4(0.1)"),
               ShaderPinDefinition.input("layer", "layer", ShaderValueType.VEC4, "vec4(u_AccentTop,1.0)"),
               ShaderPinDefinition.input("opacity", "opacity", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(mix("
               + var0.handle(var1, "base")
               + ".rgb, min("
               + var0.handle(var1, "base")
               + ".rgb, "
               + var0.handle(var1, "layer")
               + ".rgb), clamp("
               + var0.handle(var1, "opacity")
               + ",0.0,1.0)), max("
               + var0.handle(var1, "base")
               + ".a, "
               + var0.handle(var1, "layer")
               + ".a))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "blend_difference",
            "Difference Blend",
            "Blend",
            200.0F,
            List.of(
               ShaderPinDefinition.input("base", "base", ShaderValueType.VEC4, "vec4(0.1)"),
               ShaderPinDefinition.input("layer", "layer", ShaderValueType.VEC4, "vec4(u_AccentTop,1.0)"),
               ShaderPinDefinition.input("opacity", "opacity", ShaderValueType.FLOAT, "1.0")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "vec4(mix("
               + var0.handle(var1, "base")
               + ".rgb, abs("
               + var0.handle(var1, "base")
               + ".rgb - "
               + var0.handle(var1, "layer")
               + ".rgb), clamp("
               + var0.handle(var1, "opacity")
               + ",0.0,1.0)), max("
               + var0.handle(var1, "base")
               + ".a, "
               + var0.handle(var1, "layer")
               + ".a))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "branch",
            "Branch",
            "Logic",
            190.0F,
            List.of(
               ShaderPinDefinition.input("pred", "pred", ShaderValueType.FLOAT, "0.0"),
               ShaderPinDefinition.input("whenTrue", "true", ShaderValueType.VEC4, "vec4(u_AccentTop,1.0)"),
               ShaderPinDefinition.input("whenFalse", "false", ShaderValueType.VEC4, "vec4(u_AccentBottom,1.0)")
            ),
            List.of(ShaderPinDefinition.output("color", "color", ShaderValueType.VEC4)),
            (var0, var1, var2) -> "mix("
               + var0.handle(var1, "whenFalse")
               + ", "
               + var0.handle(var1, "whenTrue")
               + ", step(0.5, "
               + var0.handle(var1, "pred")
               + "))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "compare_greater",
            "Greater Than",
            "Logic",
            196.0F,
            List.of(ShaderPinDefinition.input("a", "a", ShaderValueType.FLOAT, "0.5"), ShaderPinDefinition.input("b", "b", ShaderValueType.FLOAT, "0.0")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "step(" + var0.handle(var1, "b") + ", " + var0.handle(var1, "a") + ")"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "time_sine",
            "Time Sine",
            "Time",
            178.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(0.5 + 0.5 * sin(u_Time))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "time_cosine",
            "Time Cosine",
            "Time",
            184.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "(0.5 + 0.5 * cos(u_Time))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "time_raw",
            "Time Raw",
            "Time",
            172.0F,
            List.of(),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "u_Time"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "fresnel_real",
            "Fresnel Effect",
            "VFX",
            200.0F,
            List.of(
               ShaderPinDefinition.input("normal", "normal", ShaderValueType.VEC3, "vec3(0.0,0.0,1.0)"),
               ShaderPinDefinition.input("viewDir", "view", ShaderValueType.VEC3, "vec3(0.0,0.0,1.0)"),
               ShaderPinDefinition.input("power", "power", ShaderValueType.FLOAT, "3.0")
            ),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "pow(1.0 - clamp(dot(normalize("
               + var0.handle(var1, "normal")
               + "), normalize("
               + var0.handle(var1, "viewDir")
               + ")), 0.0, 1.0), max("
               + var0.handle(var1, "power")
               + ", 0.001))"
         )
      );
      this.handle(
         new ShaderNodeDefinition(
            "dither",
            "Dither",
            "VFX",
            178.0F,
            List.of(ShaderPinDefinition.input("amount", "amount", ShaderValueType.FLOAT, "0.02")),
            List.of(ShaderPinDefinition.output("value", "value", ShaderValueType.FLOAT)),
            (var0, var1, var2) -> "((wild_hash12(gl_FragCoord.xy) - 0.5) * " + var0.handle(var1, "amount") + ")"
         )
      );
   }
}
