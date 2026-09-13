package ru.wild.render.shader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.gui.screen.ClickGuiModernScreen;
import ru.wild.gui.screen.ClickGuiScreen;
import ru.wild.gui.screen.ServerPresets;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.GlCompatibilityProbe;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.texture.DepthRenderTarget;

public final class GuiRippleShader {
   private static final GuiRippleShader instance = new GuiRippleShader();
   private static final int data = 5;
   private static final long context = 760000000L;
   private static final long config = 1860000000L;
   private final GuiRippleShader.CacheEntry[] state = new GuiRippleShader.CacheEntry[5];
   private final float[] cache = new float[10];
   private final float[] output = new float[5];
   private final float[] current = new float[5];
   private final float[] active = new float[5];
   private final float[] mode = new float[15];
   private final float[] selection = new float[15];
   private final float[] enabled = new float[15];
   private final float[] renderer = new float[15];
   private final float[] handler = new float[4];
   private final float[] animationDraw = new float[4];
   private final float[] pointEncode = new float[2];
   private final DepthRenderTarget animator = new DepthRenderTarget();
   private boolean source;
   private GuiRippleShader.Bounds target;
   private ShaderBuildReporter pending;
   private int previous = -1;
   private int latest = -1;
   private int summary = -1;
   private int matrixBlend = -1;
   private int vectorMatch = -1;
   private int itemProject = -1;
   private int responseCompute = -1;
   private int providerFetch = -1;
   private int profileDraw = -1;
   private int vectorPerform = -1;
   private int eventAttach = -1;
   private int serverRead = -1;
   private int positionAdvance = -1;
   private int frameCheck = -1;

   private GuiRippleShader() {
      for (int var1 = 0; var1 < this.state.length; var1++) {
         this.state[var1] = new GuiRippleShader.CacheEntry();
      }
   }

   public static GuiRippleShader handle() {
      return instance;
   }

   public void process() {
      if (!this.source) {
         this.source = true;
         EventHandlerInvoker.handle(this);
      }
   }

   @EventHandler
   public void handle(MouseButtonEvent var1) {
      if (var1.onTick()) {
         if (Menu.handle(Menu.profileDraw)) {
            MinecraftClient var2 = MinecraftClient.getInstance();
            if (var2 != null && var2.currentScreen != null) {
               Window var3 = var2.getWindow();
               if (var3 != null && !var3.hasZeroWidthOrHeight()) {
                  int var4 = var3.getFramebufferWidth();
                  int var5 = var3.getFramebufferHeight();
                  int var6 = var3.getWidth();
                  int var7 = var3.getHeight();
                  if (var4 > 0 && var5 > 0 && var6 > 0 && var7 > 0) {
                     float var8 = handle((float)(var1.execute() * var4 / var6), 0.0F, Math.max(0.0F, var4 - 1.0F));
                     float var9 = handle((float)(var1.prepare() * var5 / var7), 0.0F, Math.max(0.0F, var5 - 1.0F));
                     this.handle(var8, var9, this.handle(var1.resolve()), -1, -2232577, 0.0F, 760000000L);
                  }
               }
            }
         }
      }
   }

   public void handle(float var1, float var2, int var3, int var4) {
      this.handle(var1, var2, var3, var4, var3, var4);
   }

   public void handle(float var1, float var2, int var3, int var4, int var5, int var6) {
      this.handle(var1, var2, 1.24F, var3, var4, var5, var6, 1.0F, 1860000000L);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      this.handler[0] = Math.max(0.0F, var1);
      this.handler[1] = Math.max(0.0F, var2);
      this.handler[2] = Math.max(0.0F, var3);
      this.handler[3] = Math.max(0.0F, var4);
      this.animationDraw[0] = Math.max(0.0F, var6);
      this.animationDraw[1] = Math.max(0.0F, var7);
      this.animationDraw[2] = Math.max(0.0F, var8);
      this.animationDraw[3] = Math.max(0.0F, var9);
      this.pointEncode[0] = Math.max(0.0F, var5);
      this.pointEncode[1] = Math.max(0.0F, var10);
   }

   public boolean handle(Screen var1) {
      this.process(System.nanoTime());
      return var1 != null && !this.process(var1) && this.prepare();
   }

   public boolean handle(Object var1) {
      this.process(System.nanoTime());
      return var1 != null && this.process(var1) && this.prepare();
   }

   public boolean handle(int var1, int var2) {
      this.process(System.nanoTime());
      if (this.target == null && var1 > 0 && var2 > 0 && this.prepare()) {
         this.compute(var1, var2);
         OpenGlStateSnapshot.NetworkState var3 = OpenGlStateSnapshot.handle();
         this.target = new GuiRippleShader.Bounds(var3, var1, var2);
         GL30.glBindFramebuffer(36160, this.animator.instance);
         GL11.glViewport(0, 0, var1, var2);
         GL11.glDisable(3089);
         GL11.glDisable(36281);
         GL11.glColorMask(true, true, true, true);
         GL11.glDepthMask(true);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(16640);
         return true;
      } else {
         return false;
      }
   }

   public void compute() {
      GuiRippleShader.Bounds var1 = this.target;
      if (var1 != null) {
         this.target = null;
         OpenGlStateSnapshot.compute(var1.snapshot());
         OpenGlStateSnapshot.NetworkState var2 = OpenGlStateSnapshot.handle();

         try {
            this.resolve(var1.width(), var1.height());
         } finally {
            OpenGlStateSnapshot.compute(var2);
         }
      }
   }

   public void resolve() {
      long var1 = System.nanoTime();
      this.process(var1);
      if (this.target == null && this.prepare()) {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3 != null && var3.getWindow() != null && !var3.getWindow().hasZeroWidthOrHeight()) {
            ShaderRenderer var4 = WildClient.compute();
            if (var4 != null) {
               this.apply();
               ShaderRenderer.Bounds var5 = var4.resolve();
               if (var5.colorTexture() > 0 && var5.width() > 0 && var5.height() > 0) {
                  this.handle(var5.colorTexture(), var5.width(), var5.height(), false);
               }
            }
         }
      }
   }

   public void process(int var1, int var2) {
      this.target = null;
      if (var1 > 0 && var2 > 0) {
         if (this.animator.config != var1 || this.animator.state != var2) {
            this.animator.handle();
            this.check();
         }
      } else {
         this.animator.handle();
         this.check();
      }
   }

   public void handle(boolean var1) {
      if (!var1) {
         this.target = null;
         this.check();
      }
   }

   public void update() {
      this.target = null;
      this.check();
   }

   private boolean process(Object var1) {
      return var1 instanceof ClickGuiModernScreen || var1 instanceof ClickGuiScreen || var1 instanceof ServerPresets || var1 instanceof FrameRenderListener;
   }

   private void compute(int var1, int var2) {
      this.animator.handle(var1, var2);
      this.apply();
   }

   private void apply() {
      if (this.pending == null) {
         this.pending = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/postfx/gui_ripple.frag");
         this.previous = this.pending.handle("uSource");
         this.latest = this.pending.handle("uResolution");
         this.summary = this.pending.handle("uRippleCount");
         this.matrixBlend = this.pending.handle("uRippleCenter[0]");
         this.vectorMatch = this.pending.handle("uRippleAge[0]");
         this.itemProject = this.pending.handle("uRipplePower[0]");
         this.responseCompute = this.pending.handle("uRippleKind[0]");
         this.providerFetch = this.pending.handle("uRipplePreviousColorTop[0]");
         this.profileDraw = this.pending.handle("uRipplePreviousColorBottom[0]");
         this.vectorPerform = this.pending.handle("uRippleColorTop[0]");
         this.eventAttach = this.pending.handle("uRippleColorBottom[0]");
         this.serverRead = this.pending.handle("uThemeGuiRect");
         this.positionAdvance = this.pending.handle("uThemePanelRect");
         this.frameCheck = this.pending.handle("uThemeRadii");
      }
   }

   private void resolve(int var1, int var2) {
      this.handle(this.animator.data, var1, var2, true);
   }

   private void handle(int var1, int var2, int var3, boolean var4) {
      long var5 = System.nanoTime();
      int var7 = this.handle(var5);
      ShaderRenderer var8 = WildClient.compute();
      if (var8 != null) {
         var8.handle(var1, var2, var3, this.pending, () -> {
            if (this.previous >= 0) {
               GL20.glUniform1i(this.previous, 0);
            }

            if (this.latest >= 0) {
               GL20.glUniform2f(this.latest, var2, var3);
            }

            if (this.summary >= 0) {
               GL20.glUniform1i(this.summary, var7);
            }

            this.execute();
         }, var4);
      }
   }

   private int handle(long var1) {
      int var3 = 0;

      for (int var4 = 0; var4 < 5; var4++) {
         GuiRippleShader.CacheEntry var5 = this.state[var4];
         if (var5.instance) {
            float var6 = (float)(var1 - var5.data) / (float)var5.selection;
            if (var6 >= 1.0F) {
               var5.instance = false;
            } else {
               this.cache[var3 * 2] = var5.context;
               this.cache[var3 * 2 + 1] = var5.config;
               this.output[var3] = handle(var6, 0.0F, 1.0F);
               this.current[var3] = var5.state;
               this.active[var3] = var5.cache;
               handle(var5.output, this.mode, var3 * 3);
               handle(var5.current, this.selection, var3 * 3);
               handle(var5.active, this.enabled, var3 * 3);
               handle(var5.mode, this.renderer, var3 * 3);
               var3++;
            }
         }
      }

      for (int var7 = var3; var7 < 5; var7++) {
         this.cache[var7 * 2] = 0.0F;
         this.cache[var7 * 2 + 1] = 0.0F;
         this.output[var7] = 1.0F;
         this.current[var7] = 0.0F;
         this.active[var7] = 0.0F;
         handle(-1, this.mode, var7 * 3);
         handle(-2232577, this.selection, var7 * 3);
         handle(-1, this.enabled, var7 * 3);
         handle(-2232577, this.renderer, var7 * 3);
      }

      return var3;
   }

   private void execute() {
      if (this.matrixBlend >= 0) {
         GL20.glUniform2fv(this.matrixBlend, this.cache);
      }

      if (this.vectorMatch >= 0) {
         GL20.glUniform1fv(this.vectorMatch, this.output);
      }

      if (this.itemProject >= 0) {
         GL20.glUniform1fv(this.itemProject, this.current);
      }

      if (this.responseCompute >= 0) {
         GL20.glUniform1fv(this.responseCompute, this.active);
      }

      if (this.providerFetch >= 0) {
         GL20.glUniform3fv(this.providerFetch, this.mode);
      }

      if (this.profileDraw >= 0) {
         GL20.glUniform3fv(this.profileDraw, this.selection);
      }

      if (this.vectorPerform >= 0) {
         GL20.glUniform3fv(this.vectorPerform, this.enabled);
      }

      if (this.eventAttach >= 0) {
         GL20.glUniform3fv(this.eventAttach, this.renderer);
      }

      if (this.serverRead >= 0) {
         GL20.glUniform4fv(this.serverRead, this.handler);
      }

      if (this.positionAdvance >= 0) {
         GL20.glUniform4fv(this.positionAdvance, this.animationDraw);
      }

      if (this.frameCheck >= 0) {
         GL20.glUniform2fv(this.frameCheck, this.pointEncode);
      }
   }

   private void handle(float var1, float var2, float var3, int var4, int var5, float var6, long var7) {
      this.handle(var1, var2, var3, var4, var5, var4, var5, var6, var7);
   }

   private void handle(float var1, float var2, float var3, int var4, int var5, int var6, int var7, float var8, long var9) {
      if (!GlCompatibilityProbe.process()) {
         long var11 = System.nanoTime();
         GuiRippleShader.CacheEntry var13 = null;
         GuiRippleShader.CacheEntry var14 = this.state[0];

         for (GuiRippleShader.CacheEntry var18 : this.state) {
            if (!var18.instance) {
               var13 = var18;
               break;
            }

            if (var18.data < var14.data) {
               var14 = var18;
            }
         }

         if (var13 == null) {
            var13 = var14;
         }

         var13.instance = true;
         var13.context = var1;
         var13.config = var2;
         var13.state = var3;
         var13.cache = var8;
         var13.output = var4;
         var13.current = var5;
         var13.active = var6;
         var13.mode = var7;
         var13.selection = Math.max(1L, var9);
         var13.data = var11;
      }
   }

   private void process(long var1) {
      for (GuiRippleShader.CacheEntry var6 : this.state) {
         if (var6.instance && var1 - var6.data >= var6.selection) {
            var6.instance = false;
         }
      }
   }

   private boolean prepare() {
      for (GuiRippleShader.CacheEntry var4 : this.state) {
         if (var4.instance) {
            return true;
         }
      }

      return false;
   }

   private void check() {
      for (GuiRippleShader.CacheEntry var4 : this.state) {
         var4.instance = false;
         var4.data = 0L;
         var4.context = 0.0F;
         var4.config = 0.0F;
         var4.state = 0.0F;
         var4.cache = 0.0F;
         var4.output = -1;
         var4.current = -2232577;
         var4.active = -1;
         var4.mode = -2232577;
         var4.selection = 760000000L;
      }
   }

   private float handle(int var1) {
      return switch (var1) {
         case 0 -> 0.9F;
         case 1 -> 0.84F;
         case 2 -> 0.96F;
         default -> 0.86F;
      };
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static void handle(int var0, float[] var1, int var2) {
      var1[var2] = (var0 >>> 16 & 0xFF) / 255.0F;
      var1[var2 + 1] = (var0 >>> 8 & 0xFF) / 255.0F;
      var1[var2 + 2] = (var0 & 0xFF) / 255.0F;
   }

   record Bounds(OpenGlStateSnapshot.NetworkState snapshot, int width, int height) {
   }

   static final class CacheEntry {
      boolean instance;
      long data;
      float context;
      float config;
      float state;
      float cache;
      int output = -1;
      int current = -2232577;
      int active = -1;
      int mode = -2232577;
      long selection = 760000000L;
   }
}
