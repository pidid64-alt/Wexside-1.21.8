package ru.wild.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class OpenGlStateSnapshot {
   private static final int instance = 12;
   private static final int data = 12;
   private static final IntBuffer context = BufferUtils.createIntBuffer(16);
   private static final ByteBuffer config = BufferUtils.createByteBuffer(4);
   private static final FloatBuffer state = BufferUtils.createFloatBuffer(2);

   private OpenGlStateSnapshot() {
   }

   public static OpenGlStateSnapshot.NetworkState handle() {
      return process(new OpenGlStateSnapshot.NetworkState());
   }

   public static OpenGlStateSnapshot.NetworkState handle(OpenGlStateSnapshot.NetworkState var0) {
      return var0 == null ? handle() : process(var0);
   }

   public static OpenGlStateSnapshot.NetworkState process(OpenGlStateSnapshot.NetworkState var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("target must not be null");
      }

      OpenGlStateSnapshot.NetworkState var1 = var0;
      IntBuffer var2 = context;
      ByteBuffer var3 = config;
      FloatBuffer var4 = state;
      var2.clear();
      var3.clear();
      var4.clear();
      GL11.glGetIntegerv(36006, var2);
      var1.instance = var2.get(0);
      GL11.glGetIntegerv(36010, var2);
      var1.data = var2.get(0);
      GL11.glGetIntegerv(3073, var2);
      var1.context = var2.get(0);
      GL11.glGetIntegerv(3074, var2);
      var1.config = var2.get(0);
      GL11.glGetIntegerv(35725, var2);
      var1.responseCompute = var2.get(0);
      GL11.glGetIntegerv(34229, var2);
      var1.providerFetch = var2.get(0);
      GL11.glGetIntegerv(34964, var2);
      var1.profileDraw = var2.get(0);
      GL11.glGetIntegerv(34965, var2);
      var1.vectorPerform = var2.get(0);
      GL11.glGetIntegerv(34016, var2);
      int var5 = var2.get(0);
      var1.eventAttach = var5 == 0 ? 33984 : var5;

      for (int var6 = 0; var6 < 12; var6++) {
         GL13.glActiveTexture(33984 + var6);
         GL11.glGetIntegerv(32873, var2);
         var1.positionAdvance[var6] = var2.get(0);
      }

      GL13.glActiveTexture(var1.eventAttach);
      int var7 = var1.eventAttach - 33984;
      var1.serverRead = var7 >= 0 && var7 < 12 ? var1.positionAdvance[var7] : 0;
      GL11.glGetIntegerv(3317, var2);
      var1.frameCheck = var2.get(0) == 0 ? 4 : var2.get(0);
      GL11.glGetIntegerv(2978, var2);
      var1.state[0] = var2.get(0);
      var1.state[1] = var2.get(1);
      var1.state[2] = var2.get(2);
      var1.state[3] = var2.get(3);
      GL11.glGetIntegerv(3088, var2);
      var1.output[0] = var2.get(0);
      var1.output[1] = var2.get(1);
      var1.output[2] = var2.get(2);
      var1.output[3] = var2.get(3);
      GL11.glGetIntegerv(32969, var2);
      var1.enabled = var2.get(0);
      GL11.glGetIntegerv(32968, var2);
      var1.renderer = var2.get(0);
      GL11.glGetIntegerv(32971, var2);
      var1.handler = var2.get(0);
      GL11.glGetIntegerv(32970, var2);
      var1.animationDraw = var2.get(0);
      GL11.glGetBooleanv(3107, var3);
      var1.pointEncode = var3.get(0) != 0;
      var1.animator = var3.get(1) != 0;
      var1.source = var3.get(2) != 0;
      var1.target = var3.get(3) != 0;
      GL11.glGetBooleanv(2930, var3);
      var1.pending = var3.get(0) != 0;
      GL11.glGetIntegerv(2932, var2);
      var1.previous = var2.get(0);
      GL11.glGetFloatv(32824, var4);
      var1.summary = var4.get(0);
      GL11.glGetFloatv(10752, var4);
      var1.matrixBlend = var4.get(0);
      GL11.glGetIntegerv(3056, var2);
      var1.itemProject = var2.get(0);
      var1.mode = GL11.glIsEnabled(3042);
      var1.current = GL11.glIsEnabled(2929);
      var1.active = GL11.glIsEnabled(2884);
      var1.cache = GL11.glIsEnabled(3089);
      var1.selection = GL11.glIsEnabled(36281);
      var1.latest = GL11.glIsEnabled(32823);
      var1.vectorMatch = GL11.glIsEnabled(3058);
      return var1;
   }

   public static void compute(OpenGlStateSnapshot.NetworkState var0) {
      if (var0 != null) {
         int var1 = handle(var0.instance);
         int var2 = var0.data == var0.instance ? var1 : handle(var0.data);
         GL30.glBindFramebuffer(36009, var1);
         GL30.glBindFramebuffer(36008, var2);
         GL11.glDrawBuffer(process(var1, var0.context));
         GL11.glReadBuffer(compute(var2, var0.config));
         GL20.glUseProgram(var0.responseCompute);
         GL30.glBindVertexArray(var0.providerFetch);
         GL15.glBindBuffer(34962, var0.profileDraw);
         GL15.glBindBuffer(34963, var0.vectorPerform);

         for (int var3 = 0; var3 < 12; var3++) {
            GL13.glActiveTexture(33984 + var3);
            GL11.glBindTexture(3553, var0.positionAdvance[var3]);
         }

         GL13.glActiveTexture(var0.eventAttach);
         GL11.glPixelStorei(3317, var0.frameCheck);
         handle(3042, var0.mode);
         handle(2929, var0.current);
         handle(2884, var0.active);
         handle(3089, var0.cache);
         handle(36281, var0.selection);
         GL14.glBlendFuncSeparate(var0.enabled, var0.renderer, var0.handler, var0.animationDraw);
         GL11.glColorMask(var0.pointEncode, var0.animator, var0.source, var0.target);
         GL11.glDepthMask(var0.pending);
         GL11.glDepthFunc(var0.previous);
         GL11.glPolygonOffset(var0.summary, var0.matrixBlend);
         handle(32823, var0.latest);
         GL11.glLogicOp(var0.itemProject);
         handle(3058, var0.vectorMatch);
         GL11.glViewport(var0.state[0], var0.state[1], var0.state[2], var0.state[3]);
         GL11.glScissor(var0.output[0], var0.output[1], var0.output[2], var0.output[3]);
      }
   }

   public static void resolve(OpenGlStateSnapshot.NetworkState var0) {
      if (var0 != null) {
         int var1 = handle(var0.instance);
         int var2 = var0.data == var0.instance ? var1 : handle(var0.data);
         GlStateManager._glBindFramebuffer(36009, var1);
         GlStateManager._glBindFramebuffer(36008, var2);
         int var3 = Math.min(12, 12);

         for (int var4 = 0; var4 < var3; var4++) {
            GlStateManager._activeTexture(33984 + var4);
            GlStateManager._bindTexture(var0.positionAdvance[var4]);
         }

         int var5 = var0.eventAttach - 33984;
         GlStateManager._activeTexture(33984);
         if (var5 > 0 && var5 < var3) {
            GlStateManager._activeTexture(var0.eventAttach);
         }

         if (var0.mode) {
            GlStateManager._enableBlend();
         } else {
            GlStateManager._disableBlend();
         }

         if (var0.current) {
            GlStateManager._enableDepthTest();
         } else {
            GlStateManager._disableDepthTest();
         }

         if (var0.active) {
            GlStateManager._enableCull();
         } else {
            GlStateManager._disableCull();
         }

         if (var0.cache) {
            GlStateManager._enableScissorTest();
         } else {
            GlStateManager._disableScissorTest();
         }

         GlStateManager._blendFuncSeparate(var0.enabled, var0.renderer, var0.handler, var0.animationDraw);
         GlStateManager._colorMask(var0.pointEncode, var0.animator, var0.source, var0.target);
         GlStateManager._depthMask(var0.pending);
         GlStateManager._depthFunc(var0.previous);
         if (var0.latest) {
            GlStateManager._polygonOffset(var0.summary, var0.matrixBlend);
            GlStateManager._enablePolygonOffset();
         } else {
            GlStateManager._disablePolygonOffset();
         }

         if (var0.vectorMatch) {
            GlStateManager._logicOp(var0.itemProject);
            GlStateManager._enableColorLogicOp();
         } else {
            GlStateManager._disableColorLogicOp();
         }

         GlStateManager._viewport(var0.state[0], var0.state[1], var0.state[2], var0.state[3]);
         GlStateManager._scissorBox(var0.output[0], var0.output[1], var0.output[2], var0.output[3]);
      }
   }

   public static boolean handle(int var0, int var1) {
      int var2 = handle(var1);
      GL30.glBindFramebuffer(var0, var2);
      return var2 == var1;
   }

   public static int handle(int var0) {
      if (var0 <= 0) {
         return 0;
      }

      try {
         return GL30.glIsFramebuffer(var0) ? var0 : 0;
      } catch (Throwable var2) {
         return 0;
      }
   }

   private static int process(int var0, int var1) {
      return var0 == 0 && var1 != 1029 && var1 != 1028 && var1 != 1032 ? 1029 : var1;
   }

   private static int compute(int var0, int var1) {
      return var0 == 0 && var1 != 1029 && var1 != 1028 && var1 != 1032 ? 1029 : var1;
   }

   private static void handle(int var0, boolean var1) {
      if (var1) {
         GL11.glEnable(var0);
      } else {
         GL11.glDisable(var0);
      }
   }

   public static final class NetworkState {
      public int instance;
      public int data;
      public int context;
      public int config;
      public final int[] state = new int[4];
      public boolean cache;
      public final int[] output = new int[4];
      public boolean current;
      public boolean active;
      public boolean mode;
      public boolean selection;
      public int enabled;
      public int renderer;
      public int handler;
      public int animationDraw;
      public boolean pointEncode;
      public boolean animator;
      public boolean source;
      public boolean target;
      public boolean pending;
      public int previous;
      public boolean latest;
      public float summary;
      public float matrixBlend;
      public boolean vectorMatch;
      public int itemProject;
      public int responseCompute;
      public int providerFetch;
      public int profileDraw;
      public int vectorPerform;
      public int eventAttach;
      public int serverRead;
      public final int[] positionAdvance = new int[12];
      public int frameCheck;
   }
}
