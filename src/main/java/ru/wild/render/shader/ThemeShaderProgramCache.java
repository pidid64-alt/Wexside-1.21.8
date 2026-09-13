package ru.wild.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.VertexArrayBuffer;
import ru.wild.util.io.ResourceReader;

public final class ThemeShaderProgramCache {
   private static final ThemeShaderProgramCache instance = new ThemeShaderProgramCache();
   private static final String data = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private final Map<LivePreviewRenderer, ThemeShaderProgramCache.State> context = new EnumMap<>(LivePreviewRenderer.class);
   private final Map<String, ThemeShaderProgramCache.State> config = new HashMap<>();
   private VertexArrayBuffer state;
   private String cache;
   private long output;
   private int current;

   private ThemeShaderProgramCache() {
   }

   public static ThemeShaderProgramCache handle() {
      return instance;
   }

   public VertexArrayBuffer process() {
      if (this.state == null) {
         this.state = new VertexArrayBuffer();
      }

      return this.state;
   }

   public float compute() {
      if (this.output == 0L) {
         this.output = System.nanoTime();
         return 0.0F;
      } else {
         return (float)(System.nanoTime() - this.output) / 1.0E9F % 720.0F;
      }
   }

   public synchronized ShaderBuildReporter handle(LivePreviewRenderer var1, ShaderBuildResult var2) {
      if (var1 != null && var2 != null && var2.fragmentSource() != null) {
         ThemeShaderProgramCache.State var3 = this.context.get(var1);
         String var4 = var2.hash();
         if (var3 != null && var3.instance != null && var3.data.equals(var4)) {
            return var3.instance;
         }

         if (var3 != null && var3.instance != null) {
            var3.instance.process();
            var3.instance = null;
         }

         if (var3 == null) {
            var3 = new ThemeShaderProgramCache.State();
            this.context.put(var1, var3);
         }

         try {
            String var5 = this.apply();
            var3.instance = new ShaderBuildReporter(var5, var2.fragmentSource());
            var3.data = var4;
            var3.context = var2.error();
            return var3.instance;
         } catch (Throwable var6) {
            var3.context = var6.getMessage() == null ? var6.getClass().getSimpleName() : var6.getMessage();
            var3.instance = null;
            var3.data = "";
            RenderDiagnostics.handle().process("ThemeShaderProgramCache.acquire:" + var1.handle(), var6);
            throw new IllegalStateException("unreachable shader failure", var6);
         }
      } else {
         return null;
      }
   }

   public synchronized ShaderBuildReporter handle(String var1, ShaderBuildResult var2) {
      String var3 = PresetManager.onTick(var1);
      if (!var3.isBlank() && var2 != null && var2.fragmentSource() != null) {
         ThemeShaderProgramCache.State var4 = this.config.get(var3);
         String var5 = var2.hash();
         if (var4 != null && var4.instance != null && var4.data.equals(var5)) {
            return var4.instance;
         }

         if (var4 != null && var4.instance != null) {
            var4.instance.process();
            var4.instance = null;
         }

         if (var4 == null) {
            var4 = new ThemeShaderProgramCache.State();
            this.config.put(var3, var4);
         }

         try {
            String var6 = this.apply();
            var4.instance = new ShaderBuildReporter(var6, var2.fragmentSource());
            var4.data = var5;
            var4.context = var2.error();
            return var4.instance;
         } catch (Throwable var7) {
            var4.context = var7.getMessage() == null ? var7.getClass().getSimpleName() : var7.getMessage();
            var4.instance = null;
            var4.data = "";
            RenderDiagnostics.handle().process("ThemeShaderProgramCache.acquire:" + var3, var7);
            throw new IllegalStateException("unreachable shader failure", var7);
         }
      } else {
         return null;
      }
   }

   public synchronized String handle(LivePreviewRenderer var1) {
      ThemeShaderProgramCache.State var2 = this.context.get(var1);
      return var2 != null && var2.context != null ? var2.context : "";
   }

   public synchronized String handle(String var1) {
      ThemeShaderProgramCache.State var2 = this.config.get(PresetManager.onTick(var1));
      return var2 != null && var2.context != null ? var2.context : "";
   }

   public synchronized String process(LivePreviewRenderer var1) {
      ThemeShaderProgramCache.State var2 = this.context.get(var1);
      return var2 == null ? "" : var2.data;
   }

   public synchronized String process(String var1) {
      ThemeShaderProgramCache.State var2 = this.config.get(PresetManager.onTick(var1));
      return var2 == null ? "" : var2.data;
   }

   public synchronized void compute(LivePreviewRenderer var1) {
      ThemeShaderProgramCache.State var2 = this.context.remove(var1);
      if (var2 != null && var2.instance != null && execute()) {
         var2.instance.process();
         var2.instance = null;
      }
   }

   public synchronized void compute(String var1) {
      ThemeShaderProgramCache.State var2 = this.config.remove(PresetManager.onTick(var1));
      if (var2 != null && var2.instance != null && execute()) {
         var2.instance.process();
         var2.instance = null;
      }
   }

   public synchronized void resolve() {
      boolean var1 = execute();

      for (ThemeShaderProgramCache.State var3 : this.context.values()) {
         if (var3.instance != null && var1) {
            var3.instance.process();
         }

         var3.instance = null;
      }

      for (ThemeShaderProgramCache.State var5 : this.config.values()) {
         if (var5.instance != null && var1) {
            var5.instance.process();
         }

         var5.instance = null;
      }

      this.context.clear();
      this.config.clear();
      if (this.state != null && var1) {
         this.state.close();
      }

      this.state = null;
      if (this.current > 0 && var1) {
         GL11.glDeleteTextures(this.current);
      }

      this.current = 0;
      this.output = 0L;
      this.cache = null;
   }

   public synchronized int update() {
      if (this.current > 0) {
         return this.current;
      }

      ByteBuffer var1 = BufferUtils.createByteBuffer(4);
      var1.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)-1).flip();
      this.current = GL11.glGenTextures();
      GL11.glBindTexture(3553, this.current);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 33071);
      GL11.glTexParameteri(3553, 10243, 33071);
      GL11.glTexImage2D(3553, 0, 32856, 1, 1, 0, 6408, 5121, var1);
      GL11.glBindTexture(3553, 0);
      return this.current;
   }

   private String apply() {
      if (this.cache == null) {
         this.cache = ResourceReader.handle("assets/wild/shaders/mainmenu/menu_quad.vert");
      }

      return this.cache;
   }

   private static boolean execute() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }

   static final class State {
      ShaderBuildReporter instance;
      String data = "";
      String context = "";
   }
}
