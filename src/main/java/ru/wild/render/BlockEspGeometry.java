package ru.wild.render;

import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public final class BlockEspGeometry {
   private static final Logger state = LogUtils.getLogger();
   public static final int instance = 32;
   public static final int data = 8;
   public static final int context = 4;
   public static final int config = 128;
   private static final float cache = 0.004F;
   private static final long output = 150L;
   private static final int current = 6;
   private static final int active = 64;
   private static final int mode = 127;
   private static final boolean selection = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
   private static final int[][] enabled = new int[][]{
      {0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0},
      {1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
      {0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1},
      {0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0},
      {0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0},
      {0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1}
   };
   private static final int[][] renderer = new int[][]{{4, 5, 2, 3}, {2, 3, 4, 5}, {0, 1, 4, 5}, {4, 5, 0, 1}, {2, 3, 0, 1}, {0, 1, 2, 3}};
   private static final int[] handler = new int[]{-1, 1, 0, 0, 0, 0};
   private static final int[] animationDraw = new int[]{0, 0, -1, 1, 0, 0};
   private static final int[] pointEncode = new int[]{0, 0, 0, 0, -1, 1};
   private static final int[] animator = new int[6];
   private static final int[] source = new int[8];
   private final AtomicReference<BlockEspGeometry.BufferState> target = new AtomicReference<>();
   private final AtomicBoolean pending = new AtomicBoolean();
   private final AtomicBoolean previous = new AtomicBoolean();
   private final Object latest = new Object();
   private final BlockEspGeometry.CacheEntry summary = new BlockEspGeometry.CacheEntry();
   private final int[] matrixBlend = new int[32];
   private int[] vectorMatch = new int[0];
   private byte[] itemProject = new byte[0];
   private int[] responseCompute = new int[0];
   private int[] providerFetch = new int[0];
   private int[] profileDraw = new int[0];
   private long[] vectorPerform = new long[0];
   private boolean[] eventAttach = new boolean[0];
   private long[] serverRead = new long[0];
   private float[] positionAdvance = new float[0];
   private int[] frameCheck = new int[0];
   private int[] moduleCollect = new int[0];
   private BlockEspGeometry.PrimaryCacheEntry providerClose;
   private volatile boolean presetSave;
   private volatile int windowConvert;
   private Thread presetWrite;

   public void handle() {
      if (this.presetWrite == null) {
         this.presetSave = true;
         int var1 = ++this.windowConvert;
         this.presetWrite = new Thread(() -> this.handle(var1), "Wild BlockESP Geometry");
         this.presetWrite.setDaemon(true);
         this.presetWrite.setPriority(4);
         this.presetWrite.start();
      }
   }

   public void process() {
      Thread var1 = this.presetWrite;
      this.presetWrite = null;
      this.presetSave = false;
      this.windowConvert++;
      synchronized (this.latest) {
         this.providerClose = null;
         this.latest.notifyAll();
      }

      if (var1 != null && this.pending.get()) {
         try {
            var1.join(150L);
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
         }
      }

      handle(this.target.getAndSet(null));
      this.previous.set(false);
      this.pending.set(false);
   }

   public boolean compute() {
      return this.pending.get();
   }

   public boolean resolve() {
      return this.previous.compareAndSet(true, false);
   }

   public boolean handle(long[] var1, byte[] var2, int[] var3, int var4, int[] var5, int var6, int var7, int var8) {
      if (!this.pending.compareAndSet(false, true)) {
         return false;
      }

      BlockEspGeometry.PrimaryCacheEntry var9 = new BlockEspGeometry.PrimaryCacheEntry();
      var9.instance = var1;
      var9.data = var2;
      var9.context = var3;
      var9.config = var4;
      var9.state = var5;
      var9.cache = var6;
      var9.output = var7;
      var9.current = var8;
      synchronized (this.latest) {
         this.providerClose = var9;
         this.latest.notifyAll();
         return true;
      }
   }

   public BlockEspGeometry.BufferState update() {
      return this.target.getAndSet(null);
   }

   public static void handle(BlockEspGeometry.BufferState var0) {
      if (var0 != null && var0.instance != null) {
         MemoryUtil.memFree(var0.instance);
      }
   }
   private void handle(int var1) {
      while (this.presetSave) {
         BlockEspGeometry.PrimaryCacheEntry var2;
         synchronized (this.latest) {
            while (this.presetSave && this.providerClose == null) {
               try {
                  this.latest.wait();
               } catch (InterruptedException var12) {
                  Thread.currentThread().interrupt();
                  return;
               }
            }

            if (!this.presetSave) {
               return;
            }

            var2 = this.providerClose;
            this.providerClose = null;
         }

         BlockEspGeometry.BufferState var16 = null;
         boolean var4 = false;
         boolean var11 = false /* VF: Semaphore variable */;

         label168: {
            try {
               var11 = true;
               var16 = this.handle(var2);
               var4 = true;
               var11 = false;
               break label168;
            } catch (Throwable var13) {
               this.previous.set(true);
               state.error("BlockESP geometry build failed", var13);
               var11 = false;
            } finally {
               if (var11) {
                  if (!var4) {
                     handle(var16);
                  } else if (this.presetSave && var1 == this.windowConvert) {
                     handle(this.target.getAndSet(var16));
                     if (!this.presetSave) {
                        handle(this.target.getAndSet(null));
                     }
                  } else {
                     handle(var16);
                  }

                  this.pending.set(false);
               }
            }

            if (!var4) {
               handle(var16);
            } else if (this.presetSave && var1 == this.windowConvert) {
               handle(this.target.getAndSet(var16));
               if (!this.presetSave) {
                  handle(this.target.getAndSet(null));
               }
            } else {
               handle(var16);
            }

            this.pending.set(false);
            continue;
         }

         if (!var4) {
            handle(var16);
         } else if (this.presetSave && var1 == this.windowConvert) {
            handle(this.target.getAndSet(var16));
            if (!this.presetSave) {
               handle(this.target.getAndSet(null));
            }
         } else {
            handle(var16);
         }

         this.pending.set(false);
      }
   }

   private BlockEspGeometry.BufferState handle(BlockEspGeometry.PrimaryCacheEntry var1) {
      int var2 = var1.config;
      if (var2 <= 0) {
         return new BlockEspGeometry.BufferState(null, 0, var1.cache, var1.output, var1.current, 0, null, null, null);
      }

      this.process(var2);
      long[] var3 = var1.instance;
      int[] var4 = var1.context;
      this.summary.handle(var2);

      for (int var5 = 0; var5 < var2; this.vectorMatch[var5] = var5++) {
         if ((var4[var5] & 65536) == 0) {
            this.summary.handle(var3[var5], var5);
         }
      }

      int var16 = 0;

      for (int var6 = 0; var6 < var2; var6++) {
         long var7 = var3[var6];
         int var9 = BlockPos.unpackLongX(var7);
         int var10 = BlockPos.unpackLongY(var7);
         int var11 = BlockPos.unpackLongZ(var7);
         int var12 = 0;

         for (int var13 = 0; var13 < 6; var13++) {
            int var14 = this.summary.handle(BlockPos.asLong(var9 + handler[var13], var10 + animationDraw[var13], var11 + pointEncode[var13]));
            if (var14 < 0) {
               var12 |= 1 << var13;
            } else if (var14 != var6) {
               this.handle(var6, var14);
            }
         }

         this.itemProject[var6] = (byte)var12;
         var16 += Integer.bitCount(var12);
      }

      if (var16 == 0) {
         return new BlockEspGeometry.BufferState(null, 0, var1.cache, var1.output, var1.current, 0, null, null, null);
      }

      for (int var17 = 0; var17 < var2; var17++) {
         this.responseCompute[var17] = Integer.MAX_VALUE;
         this.providerFetch[var17] = Integer.MIN_VALUE;
         this.vectorPerform[var17] = Long.MAX_VALUE;
         this.eventAttach[var17] = false;
      }

      for (int var18 = 0; var18 < var2; var18++) {
         if ((var4[var18] & 65536) == 0) {
            int var23 = this.resolve(var18);
            this.eventAttach[var23] = true;
            this.handle(var23, var3[var18]);
         }
      }

      for (int var19 = 0; var19 < var2; var19++) {
         int var24 = this.resolve(var19);
         if (!this.eventAttach[var24]) {
            this.handle(var24, var3[var19]);
         }
      }

      for (int var20 = 0; var20 < var2; var20++) {
         if (this.vectorMatch[var20] == var20) {
            long var25 = this.vectorPerform[var20];
            this.profileDraw[var20] = (int)((var25 == Long.MAX_VALUE ? handle(var3[var20]) : var25) & 255L);
         }
      }

      for (int var21 = 0; var21 < var2; var21++) {
         long var26 = var3[var21];
         int var29 = update((BlockPos.unpackLongX(var26) - var1.cache >> 6) + 64);
         int var30 = update((BlockPos.unpackLongY(var26) - var1.output >> 6) + 64);
         int var31 = update((BlockPos.unpackLongZ(var26) - var1.current >> 6) + 64);
         long var32 = (long)var29 << 14 | (long)var30 << 7 | var31;
         this.serverRead[var21] = var32 << 21 | var21;
      }

      Arrays.sort(this.serverRead, 0, var2);
      int var22 = 1;

      for (int var27 = 1; var27 < var2; var27++) {
         if (this.serverRead[var27] >>> 21 != this.serverRead[var27 - 1] >>> 21) {
            var22++;
         }
      }

      this.compute(var22);
      ByteBuffer var28 = MemoryUtil.memAlloc(var16 * 128);

      try {
         return this.handle(var1, var28, var2, var22);
      } catch (Throwable var15) {
         MemoryUtil.memFree(var28);
         throw var15;
      }
   }

   private BlockEspGeometry.BufferState handle(BlockEspGeometry.PrimaryCacheEntry var1, ByteBuffer var2, int var3, int var4) {
      IntBuffer var5 = var2.asIntBuffer();
      int[] var6 = this.matrixBlend;
      int[] var7 = var1.state;
      long[] var8 = var1.instance;
      byte[] var9 = var1.data;
      int[] var10 = var1.context;
      byte var11 = 0;
      int var12 = -1;
      long var13 = Long.MIN_VALUE;
      float var15 = 0.0F;
      float var16 = 0.0F;
      float var17 = 0.0F;
      float var18 = 0.0F;
      float var19 = 0.0F;
      float var20 = 0.0F;

      for (int var21 = 0; var21 < var3; var21++) {
         long var22 = this.serverRead[var21];
         int var24 = (int)(var22 & 2097151L);
         long var25 = var22 >>> 21;
         if (var25 != var13) {
            if (var12 >= 0) {
               this.handle(var12, var11, var15, var16, var17, var18, var19, var20);
            }

            var13 = var25;
            this.frameCheck[++var12] = var11;
            var15 = Float.MAX_VALUE;
            var16 = Float.MAX_VALUE;
            var17 = Float.MAX_VALUE;
            var18 = -Float.MAX_VALUE;
            var19 = -Float.MAX_VALUE;
            var20 = -Float.MAX_VALUE;
         }

         int var27 = this.itemProject[var24] & 255;
         long var28 = var8[var24];
         int var30 = BlockPos.unpackLongX(var28);
         int var31 = BlockPos.unpackLongY(var28);
         int var32 = BlockPos.unpackLongZ(var28);
         float var33 = var30 - var1.cache;
         float var34 = var31 - var1.output;
         float var35 = var32 - var1.current;
         if (var33 - 0.004F < var15) {
            var15 = var33 - 0.004F;
         }

         if (var34 - 0.004F < var16) {
            var16 = var34 - 0.004F;
         }

         if (var35 - 0.004F < var17) {
            var17 = var35 - 0.004F;
         }

         if (var33 + 1.0F + 0.004F > var18) {
            var18 = var33 + 1.0F + 0.004F;
         }

         if (var34 + 1.0F + 0.004F > var19) {
            var19 = var34 + 1.0F + 0.004F;
         }

         if (var35 + 1.0F + 0.004F > var20) {
            var20 = var35 + 1.0F + 0.004F;
         }

         if (var27 != 0) {
            int var36 = this.resolve(var24);
            int var37 = this.responseCompute[var36];
            int var38 = this.providerFetch[var36] + 1 - var37;
            float var39 = 1.0F / var38;
            int var40 = this.profileDraw[var36];
            int var41 = var7[var9[var24] & 0xFF];
            int var42 = handle(var41 >> 16 & 0xFF, var41 >> 8 & 0xFF, var41 & 0xFF, var40);
            int var43 = var10[var24];
            int var44 = (var43 & 65536) != 0 ? 16 : 0;
            int var45 = var43 & 65535;

            for (int var46 = 0; var46 < 6; var46++) {
               if ((var27 & 1 << var46) != 0) {
                  int[] var47 = renderer[var46];
                  byte var48 = 0;
                  if ((var27 & 1 << var47[0]) != 0) {
                     var48 |= 1;
                  }

                  if ((var27 & 1 << var47[1]) != 0) {
                     var48 |= 2;
                  }

                  if ((var27 & 1 << var47[2]) != 0) {
                     var48 |= 4;
                  }

                  if ((var27 & 1 << var47[3]) != 0) {
                     var48 |= 8;
                  }

                  int[] var49 = enabled[var46];
                  int var50 = animator[var46];
                  int var51 = var46 >> 1;
                  float var52 = var51 == 0 ? 0.004F : 0.0F;
                  float var53 = var51 == 1 ? 0.004F : 0.0F;
                  float var54 = var51 == 2 ? 0.004F : 0.0F;

                  for (int var55 = 0; var55 < 4; var55++) {
                     int var56 = var49[var55 * 3];
                     int var57 = var49[var55 * 3 + 1];
                     int var58 = var49[var55 * 3 + 2];
                     float var59 = 0.0F;
                     float var60 = 0.0F;
                     float var61 = 0.0F;

                     for (int var62 = 0; var62 < 4; var62++) {
                        if ((var48 & 1 << var62) != 0) {
                           int var63 = var47[var62];
                           int var64 = var63 & 1;
                           switch (var63 >> 1) {
                              case 0:
                                 if (var56 == var64) {
                                    var59 = var64 == 1 ? 0.004F : -0.004F;
                                 }
                                 break;
                              case 1:
                                 if (var57 == var64) {
                                    var60 = var64 == 1 ? 0.004F : -0.004F;
                                 }
                                 break;
                              default:
                                 if (var58 == var64) {
                                    var61 = var64 == 1 ? 0.004F : -0.004F;
                                 }
                           }
                        }
                     }

                     int var66 = var55 * 8;
                     var6[var66] = Float.floatToRawIntBits(var33 + (var56 == 0 ? -var52 : 1.0F + var52) + var59);
                     var6[var66 + 1] = Float.floatToRawIntBits(var34 + (var57 == 0 ? -var53 : 1.0F + var53) + var60);
                     var6[var66 + 2] = Float.floatToRawIntBits(var35 + (var58 == 0 ? -var54 : 1.0F + var54) + var61);
                     var6[var66 + 3] = source[var55 * 2];
                     var6[var66 + 4] = source[var55 * 2 + 1];
                     var6[var66 + 5] = var42;
                     int var67 = Math.round((var31 + var57 - var37) * var39 * 255.0F);
                     if (var67 < 0) {
                        var67 = 0;
                     } else if (var67 > 255) {
                        var67 = 255;
                     }

                     var6[var66 + 6] = process(var48 | var44 | var67 << 8, var45);
                     var6[var66 + 7] = var50;
                  }

                  var5.put(var6, 0, var6.length);
                  var11 += 4;
               }
            }
         }
      }

      if (var12 >= 0) {
         this.handle(var12, var11, var15, var16, var17, var18, var19, var20);
      }

      var2.position(0).limit(var11 * 32);
      int var65 = var12 + 1;
      return new BlockEspGeometry.BufferState(
         var2,
         var11,
         var1.cache,
         var1.output,
         var1.current,
         var65,
         Arrays.copyOf(this.positionAdvance, var65 * 6),
         Arrays.copyOf(this.frameCheck, var65),
         Arrays.copyOf(this.moduleCollect, var65)
      );
   }

   private void handle(int var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.moduleCollect[var1] = var2 - this.frameCheck[var1];
      int var9 = var1 * 6;
      this.positionAdvance[var9] = var3;
      this.positionAdvance[var9 + 1] = var4;
      this.positionAdvance[var9 + 2] = var5;
      this.positionAdvance[var9 + 3] = var6;
      this.positionAdvance[var9 + 4] = var7;
      this.positionAdvance[var9 + 5] = var8;
   }

   private void process(int var1) {
      if (this.vectorMatch.length < var1) {
         int var2 = Integer.highestOneBit(Math.max(1024, var1 - 1)) << 1;
         this.vectorMatch = new int[var2];
         this.itemProject = new byte[var2];
         this.responseCompute = new int[var2];
         this.providerFetch = new int[var2];
         this.profileDraw = new int[var2];
         this.vectorPerform = new long[var2];
         this.eventAttach = new boolean[var2];
         this.serverRead = new long[var2];
      }
   }

   private void compute(int var1) {
      if (this.frameCheck.length < var1) {
         int var2 = Integer.highestOneBit(Math.max(64, var1 - 1)) << 1;
         this.frameCheck = new int[var2];
         this.moduleCollect = new int[var2];
         this.positionAdvance = new float[var2 * 6];
      }
   }

   private int resolve(int var1) {
      int var2 = var1;

      while (this.vectorMatch[var2] != var2) {
         var2 = this.vectorMatch[var2];
      }

      while (this.vectorMatch[var1] != var2) {
         int var3 = this.vectorMatch[var1];
         this.vectorMatch[var1] = var2;
         var1 = var3;
      }

      return var2;
   }

   private void handle(int var1, long var2) {
      int var4 = BlockPos.unpackLongY(var2);
      if (var4 < this.responseCompute[var1]) {
         this.responseCompute[var1] = var4;
      }

      if (var4 > this.providerFetch[var1]) {
         this.providerFetch[var1] = var4;
      }

      long var5 = handle(var2);
      if (var5 < this.vectorPerform[var1]) {
         this.vectorPerform[var1] = var5;
      }
   }

   private void handle(int var1, int var2) {
      int var3 = this.resolve(var1);
      int var4 = this.resolve(var2);
      if (var3 != var4) {
         if (var3 < var4) {
            this.vectorMatch[var4] = var3;
         } else {
            this.vectorMatch[var3] = var4;
         }
      }
   }

   private static int update(int var0) {
      return var0 < 0 ? 0 : Math.min(var0, 127);
   }

   private static long handle(long var0) {
      long var2 = var0 * -7046029254386353131L;
      var2 ^= var2 >>> 29;
      var2 *= -4658895280553007687L;
      return var2 ^ var2 >>> 32;
   }

   private static int apply(int var0) {
      return var0 > 0 ? 127 : (var0 < 0 ? -127 : 0);
   }

   private static int handle(int var0, int var1, int var2, int var3) {
      return selection
         ? var0 & 0xFF | (var1 & 0xFF) << 8 | (var2 & 0xFF) << 16 | (var3 & 0xFF) << 24
         : (var0 & 0xFF) << 24 | (var1 & 0xFF) << 16 | (var2 & 0xFF) << 8 | var3 & 0xFF;
   }

   private static int process(int var0, int var1) {
      return selection ? var0 & 65535 | (var1 & 65535) << 16 : (var0 & 65535) << 16 | var1 & 65535;
   }

   static {
      for (int var0 = 0; var0 < 6; var0++) {
         animator[var0] = handle(apply(handler[var0]), apply(animationDraw[var0]), apply(pointEncode[var0]), 0);
      }

      float[] var3 = new float[]{0.0F, 1.0F, 1.0F, 0.0F};
      float[] var1 = new float[]{0.0F, 0.0F, 1.0F, 1.0F};

      for (int var2 = 0; var2 < 4; var2++) {
         source[var2 * 2] = Float.floatToRawIntBits(var3[var2]);
         source[var2 * 2 + 1] = Float.floatToRawIntBits(var1[var2]);
      }
   }

   public static final class BufferState {
      public final ByteBuffer instance;
      public final int data;
      public final int context;
      public final int config;
      public final int state;
      public final int cache;
      public final float[] output;
      public final int[] current;
      public final int[] active;

      BufferState(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6, float[] var7, int[] var8, int[] var9) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
         this.active = var9;
      }

      public int handle() {
         return this.data * 32;
      }
   }

   static final class CacheEntry {
      private long[] instance = new long[1024];
      private int[] data = new int[1024];
      private boolean[] context = new boolean[1024];
      private int config = 1023;

      void handle(int var1) {
         int var2 = Integer.highestOneBit(Math.max(16, var1 * 2 - 1)) << 1;
         if (var2 > this.instance.length) {
            this.instance = new long[var2];
            this.data = new int[var2];
            this.context = new boolean[var2];
            this.config = var2 - 1;
         } else {
            Arrays.fill(this.context, false);
         }
      }

      private int process(long var1) {
         long var3 = var1 * -7046029254386353131L;
         var3 ^= var3 >>> 32;
         return (int)var3 & this.config;
      }

      void handle(long var1, int var3) {
         int var4;
         for (var4 = this.process(var1); this.context[var4]; var4 = var4 + 1 & this.config) {
            if (this.instance[var4] == var1) {
               this.data[var4] = var3;
               return;
            }
         }

         this.context[var4] = true;
         this.instance[var4] = var1;
         this.data[var4] = var3;
      }

      int handle(long var1) {
         for (int var3 = this.process(var1); this.context[var3]; var3 = var3 + 1 & this.config) {
            if (this.instance[var3] == var1) {
               return this.data[var3];
            }
         }

         return -1;
      }
   }

   static final class PrimaryCacheEntry {
      long[] instance;
      byte[] data;
      int[] context;
      int config;
      int[] state;
      int cache;
      int output;
      int current;
   }
}
