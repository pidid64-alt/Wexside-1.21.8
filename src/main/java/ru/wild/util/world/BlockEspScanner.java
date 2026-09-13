package ru.wild.util.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import ru.wild.util.math.TemporalNoise;

public final class BlockEspScanner {
   public static final int instance = 520;
   private static final long data = 400000L;
   private static final long context = 2000000L;
   private static final int config = 48;
   private static final int state = 33;
   private static final int cache = 35;
   private static final int output = 32768;
   private static final int current = 4096;
   private static final int active = 512;
   private static final int mode = 4000;
   private static final int selection = 4352;
   private static final int enabled = 64;
   private static final int renderer = 8192;
   private static final int[] handler;
   private static final int[] animationDraw;
   private static final int[] pointEncode;
   private final Long2ObjectOpenHashMap<BlockEspScanner.PrimaryState> animator = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet source = new LongLinkedOpenHashSet();
   private final LongOpenHashSet target = new LongOpenHashSet();
   private final LongOpenHashSet pending = new LongOpenHashSet();
   private final LongOpenHashSet previous = new LongOpenHashSet();
   private final Object latest = new Object();
   private final LongOpenHashSet summary = new LongOpenHashSet();
   private final LongOpenHashSet matrixBlend = new LongOpenHashSet();
   private final LongOpenHashSet vectorMatch = new LongOpenHashSet();
   private final long[] itemProject = new long[4352];
   private final byte[] responseCompute = new byte[4352];
   private final Mutable providerFetch = new Mutable();
   private final long[] profileDraw = new long[8192];
   private final byte[] vectorPerform = new byte[8192];
   private final int[] eventAttach = new int[8192];
   private final long[] serverRead = new long[8192];
   private final byte[] positionAdvance = new byte[8192];
   private final int[] frameCheck = new int[65];
   private final int[] moduleCollect = new int[65];
   private int providerClose;
   private final long[] presetSave = new long[512];
   private final byte[] windowConvert = new byte[512];
   private final int[] presetWrite = new int[512];
   private int colorMeasure;
   private final BlockEspScanner.State animationSchedule = new BlockEspScanner.State();
   private int rendererScan;
   private int sourceBuild;
   private int outputCollapse;
   private int profileInvoke = Integer.MIN_VALUE;
   private int sourceSchedule = Integer.MIN_VALUE;
   private boolean timerRender;
   private boolean scaleSave;
   private boolean colorCompute;
   private boolean scaleAdapt;
   private volatile boolean textureRun;
   private double indexBind;
   private double actionRead;
   private int configCollapse;
   private int dataValidate = 33;

   public void handle(int var1) {
      this.dataValidate = MathHelper.clamp(var1, 1, 33);
   }

   public void handle() {
      this.animator.clear();
      this.animator.trim();
      this.source.clear();
      this.source.trim();
      synchronized (this.latest) {
         this.target.clear();
         this.pending.clear();
         this.previous.clear();
      }

      this.summary.clear();
      this.matrixBlend.clear();
      this.vectorMatch.clear();
      this.animationSchedule.process();
      this.colorMeasure = 0;
      this.textureRun = false;
      this.profileInvoke = Integer.MIN_VALUE;
      this.sourceSchedule = Integer.MIN_VALUE;
      this.rendererScan = 0;
      this.sourceBuild = 0;
      this.scaleAdapt = true;
      this.timerRender = true;
   }

   public void process() {
      this.scaleAdapt = true;
   }

   public boolean compute() {
      boolean var1 = this.timerRender;
      this.timerRender = false;
      return var1;
   }

   public void handle(int var1, int var2, int var3) {
      long var4 = BlockPos.asLong(var1, var2, var3);
      synchronized (this.latest) {
         if (this.target.size() >= 32768) {
            this.textureRun = true;
         } else {
            this.target.add(var4);
         }
      }
   }

   public void handle(long[] var1, int var2) {
      if (var2 > 0) {
         synchronized (this.latest) {
            if (this.target.size() + var2 > 32768) {
               this.textureRun = true;
            } else {
               for (int var4 = 0; var4 < var2; var4++) {
                  this.target.add(var1[var4]);
               }
            }
         }
      }
   }

   public void handle(int var1, int var2) {
      long var3 = ChunkPos.toLong(var1, var2);
      synchronized (this.latest) {
         if (this.pending.size() >= 32768) {
            this.textureRun = true;
         } else {
            this.pending.add(var3);
            this.previous.remove(var3);
         }
      }
   }

   public void process(int var1, int var2) {
      long var3 = ChunkPos.toLong(var1, var2);
      synchronized (this.latest) {
         if (this.previous.size() >= 32768) {
            this.textureRun = true;
         } else {
            this.previous.add(var3);
            this.pending.remove(var3);
         }
      }
   }

   public void handle(ClientWorld var1, int var2, int var3) {
      int var4 = var1.countVerticalSections();
      int var5 = var1.getBottomSectionCoord();
      if (var4 != this.sourceBuild || var5 != this.outputCollapse) {
         this.sourceBuild = var4;
         this.rendererScan = Math.min(var4, 64);
         this.outputCollapse = var5;
         this.animator.clear();
         this.source.clear();
         this.scaleAdapt = true;
         this.timerRender = true;
      }

      boolean var6 = var2 != this.profileInvoke || var3 != this.sourceSchedule;
      this.profileInvoke = var2;
      this.sourceSchedule = var3;
      if (var6) {
         this.check();
      }

      if (this.textureRun) {
         this.textureRun = false;
         this.scaleAdapt = true;
      }

      if (this.scaleAdapt) {
         this.scaleAdapt = false;
         this.prepare();
      }

      this.handle(var1);
      this.onTick();
      this.process(var1);
   }

   private void prepare() {
      int var1 = pointEncode[this.dataValidate];

      for (int var2 = 0; var2 < var1; var2++) {
         long var3 = ChunkPos.toLong(this.profileInvoke + handler[var2], this.sourceSchedule + animationDraw[var2]);
         BlockEspScanner.PrimaryState var5 = (BlockEspScanner.PrimaryState)this.animator.get(var3);
         if (var5 != null) {
            var5.instance = 0L;
         }

         this.source.add(var3);
      }
   }

   private void check() {
      if (!this.animator.isEmpty()) {
         LongIterator var1 = this.animator.keySet().iterator();

         while (var1.hasNext()) {
            long var2 = var1.nextLong();
            if (Math.abs(ChunkPos.getPackedX(var2) - this.profileInvoke) > 35 || Math.abs(ChunkPos.getPackedZ(var2) - this.sourceSchedule) > 35) {
               BlockEspScanner.PrimaryState var4 = (BlockEspScanner.PrimaryState)this.animator.get(var2);
               if (var4 != null && var4.data != 0L) {
                  this.timerRender = true;
               }

               var1.remove();
               this.source.remove(var2);
            }
         }
      }
   }
   private void handle(ClientWorld var1) {
      long[] var2 = this.vectorMatch.toLongArray();
      long[] var3 = this.matrixBlend.toLongArray();
      long[] var4 = this.summary.toLongArray();
      this.vectorMatch.clear();
      this.matrixBlend.clear();
      this.summary.clear();
      int var5 = (int)this.latest;
      synchronized (this.latest){} // $VF: monitorenter 
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         if (!this.previous.isEmpty()) {
            this.vectorMatch.addAll(this.previous);
            this.previous.clear();
         }

         if (!this.pending.isEmpty()) {
            this.matrixBlend.addAll(this.pending);
            this.pending.clear();
         }

         if (!this.target.isEmpty()) {
            this.summary.addAll(this.target);
            this.target.clear();
         }
         var12 = false;
      } finally {
         if (var12) {
         }
      }

      for (long var8 : var2) {
         BlockEspScanner.PrimaryState var10 = (BlockEspScanner.PrimaryState)this.animator.remove(var8);
         this.source.remove(var8);
         if (var10 != null && var10.data != 0L) {
            this.timerRender = true;
         }
      }

      for (long var21 : var3) {
         BlockEspScanner.PrimaryState var23 = (BlockEspScanner.PrimaryState)this.animator.get(var21);
         if (var23 != null) {
            var23.instance = 0L;
         }

         this.source.addAndMoveToFirst(var21);
      }

      var5 = 0;

      for (long var9 : var4) {
         if (var5++ >= 4096) {
            this.summary.add(var9);
         } else {
            this.handle(var1, var9);
         }
      }

      if (this.animationSchedule.handle()) {
         this.animationSchedule.handle(TemporalNoise.handle(), 4000);
      }
   }

   private void handle(ClientWorld var1, long var2) {
      int var4 = BlockPos.unpackLongX(var2);
      int var5 = BlockPos.unpackLongY(var2);
      int var6 = BlockPos.unpackLongZ(var2);
      BlockEspScanner.PrimaryState var7 = (BlockEspScanner.PrimaryState)this.animator.get(ChunkPos.toLong(var4 >> 4, var6 >> 4));
      if (var7 != null) {
         int var8 = (var5 >> 4) - this.outputCollapse;
         if (var8 >= 0 && var8 < this.rendererScan) {
            int var9 = this.handle(var1, var4, var5, var6);
            DeterministicNoiseTable var10 = var7.context == null ? null : var7.context[var8];
            byte var11 = var10 == null ? -1 : var10.handle(var2);
            if (var11 != var9) {
               int var12 = TemporalNoise.handle();
               if (var9 < 0) {
                  var10.process(var2);
                  if (var10.handle() == 0) {
                     var7.data &= ~(1L << var8);
                  }

                  this.handle(var2, var11, var12);
               } else {
                  if (var7.context == null) {
                     var7.context = new DeterministicNoiseTable[this.rendererScan];
                  }

                  if (var10 == null) {
                     var10 = new DeterministicNoiseTable();
                     var7.context[var8] = var10;
                  }

                  var10.handle(var2, (byte)var9);
                  var7.data |= 1L << var8;
                  this.animationSchedule.handle(var2, var12);
               }

               this.timerRender = true;
            }
         }
      }
   }

   private int handle(ClientWorld var1, int var2, int var3, int var4) {
      this.providerFetch.set(var2, var3, var4);
      BlockState var5 = var1.getBlockState(this.providerFetch);
      int var6 = BlockRenderTypeRegistry.handle(var5);
      if (var6 >= 0) {
         return var6;
      }

      if (!var5.hasBlockEntity()) {
         return -1;
      }

      BlockEntity var7 = var1.getBlockEntity(this.providerFetch);
      return var7 == null ? -1 : BlockRenderTypeRegistry.handle(var7.getType());
   }

   private void handle(long var1, byte var3, int var4) {
      if (var3 >= 0) {
         for (int var5 = 0; var5 < this.colorMeasure; var5++) {
            if (this.presetSave[var5] == var1) {
               this.windowConvert[var5] = var3;
               this.presetWrite[var5] = var4;
               return;
            }
         }

         if (this.colorMeasure == 512) {
            System.arraycopy(this.presetSave, 1, this.presetSave, 0, 511);
            System.arraycopy(this.windowConvert, 1, this.windowConvert, 0, 511);
            System.arraycopy(this.presetWrite, 1, this.presetWrite, 0, 511);
            this.colorMeasure--;
         }

         this.presetSave[this.colorMeasure] = var1;
         this.windowConvert[this.colorMeasure] = var3;
         this.presetWrite[this.colorMeasure] = var4;
         this.colorMeasure++;
      }
   }

   private void onTick() {
      if (this.colorMeasure != 0) {
         int var1 = TemporalNoise.handle();
         int var2 = 0;

         for (int var3 = 0; var3 < this.colorMeasure; var3++) {
            if (var1 - this.presetWrite[var3] <= 520) {
               this.presetSave[var2] = this.presetSave[var3];
               this.windowConvert[var2] = this.windowConvert[var3];
               this.presetWrite[var2] = this.presetWrite[var3];
               var2++;
            }
         }

         this.colorMeasure = var2;
      }
   }

   private void process(ClientWorld var1) {
      if (!this.source.isEmpty()) {
         long var2 = this.source.size() >= 48 ? 2000000L : 400000L;
         long var4 = System.nanoTime() + var2;
         int var6 = 0;

         while (!this.source.isEmpty()) {
            if ((++var6 & 63) == 0 && System.nanoTime() >= var4) {
               return;
            }

            long var7 = this.source.firstLong();
            int var9 = ChunkPos.getPackedX(var7);
            int var10 = ChunkPos.getPackedZ(var7);
            WorldChunk var11 = var1.getChunkManager().getChunk(var9, var10, ChunkStatus.FULL, false);
            if (var11 == null) {
               this.source.removeFirstLong();
               BlockEspScanner.PrimaryState var12 = (BlockEspScanner.PrimaryState)this.animator.remove(var7);
               if (var12 != null && var12.data != 0L) {
                  this.timerRender = true;
               }
            } else {
               BlockEspScanner.PrimaryState var13 = (BlockEspScanner.PrimaryState)this.animator.get(var7);
               if (var13 == null) {
                  var13 = new BlockEspScanner.PrimaryState();
                  var13.config = TemporalNoise.handle();
                  this.animator.put(var7, var13);
               }

               if (this.handle(var11, var13, var9, var10, var4)) {
                  this.source.removeFirstLong();
               }

               if (System.nanoTime() >= var4) {
                  return;
               }
            }
         }
      }
   }

   private boolean handle(WorldChunk var1, BlockEspScanner.PrimaryState var2, int var3, int var4, long var5) {
      ChunkSection[] var7 = var1.getSectionArray();
      int var8 = Math.min(var7.length, this.rendererScan);
      long var9 = var8 >= 64 ? -1L : (1L << var8) - 1L;
      this.handle(var1, var8);

      for (int var11 = 0; var11 < var8; var11++) {
         if ((var2.instance & 1L << var11) == 0L) {
            this.handle(var1, var7[var11], var2, var11, var3, var4);
            var2.instance |= 1L << var11;
            if (System.nanoTime() >= var5) {
               break;
            }
         }
      }

      return (var2.instance & var9) == var9;
   }

   private void handle(WorldChunk var1, int var2) {
      this.providerClose = 0;
      int var3 = Math.min(var2, 64);
      Arrays.fill(this.frameCheck, 0, var3 + 1, 0);
      Map<BlockPos, BlockEntity> var4 = var1.getBlockEntities();
      if (!var4.isEmpty()) {
         for (BlockEntity var6 : var4.values()) {
            if (this.providerClose == 8192) {
               break;
            }

            int var7 = BlockRenderTypeRegistry.handle(var6.getType());
            if (var7 >= 0) {
               BlockPos var8 = var6.getPos();
               int var9 = (var8.getY() >> 4) - this.outputCollapse;
               if (var9 >= 0 && var9 < var3) {
                  this.profileDraw[this.providerClose] = BlockPos.asLong(var8.getX(), var8.getY(), var8.getZ());
                  this.vectorPerform[this.providerClose] = (byte)var7;
                  this.eventAttach[this.providerClose] = var9;
                  this.providerClose++;
                  this.frameCheck[var9 + 1]++;
               }
            }
         }
      }

      for (int var10 = 0; var10 < var3; var10++) {
         this.frameCheck[var10 + 1] = this.frameCheck[var10 + 1] + this.frameCheck[var10];
      }

      System.arraycopy(this.frameCheck, 0, this.moduleCollect, 0, var3 + 1);

      for (int var11 = 0; var11 < this.providerClose; var11++) {
         int var12 = this.moduleCollect[this.eventAttach[var11]]++;
         this.serverRead[var12] = this.profileDraw[var11];
         this.positionAdvance[var12] = this.vectorPerform[var11];
      }
   }

   private void handle(WorldChunk var1, ChunkSection var2, BlockEspScanner.PrimaryState var3, int var4, int var5, int var6) {
      int var7 = 0;
      int var8 = var5 << 4;
      int var9 = var6 << 4;
      int var10 = this.outputCollapse + var4 << 4;
      if (var2 != null && !var2.isEmpty() && var2.hasAny(BlockRenderTypeRegistry.handle())) {
         for (int var11 = 0; var11 < 16; var11++) {
            for (int var12 = 0; var12 < 16; var12++) {
               for (int var13 = 0; var13 < 16; var13++) {
                  BlockState var14 = var2.getBlockState(var13, var11, var12);
                  int var15 = BlockRenderTypeRegistry.handle(var14);
                  if (var15 >= 0) {
                     this.itemProject[var7] = BlockPos.asLong(var8 + var13, var10 + var11, var9 + var12);
                     this.responseCompute[var7] = (byte)var15;
                     var7++;
                  }
               }
            }
         }
      }

      if (var4 < 64) {
         int var16 = this.frameCheck[var4 + 1];

         for (int var18 = this.frameCheck[var4]; var18 < var16 && var7 != 4352; var18++) {
            this.itemProject[var7] = this.serverRead[var18];
            this.responseCompute[var7] = this.positionAdvance[var18];
            var7++;
         }
      }

      DeterministicNoiseTable var17 = var3.context == null ? null : var3.context[var4];
      if (var7 == 0) {
         if (var17 != null && var17.handle() != 0) {
            var17.update();
            var3.data &= ~(1L << var4);
            this.timerRender = true;
         }
      } else {
         if (var17 != null && var17.handle() == var7) {
            boolean var19 = true;

            for (int var21 = 0; var21 < var7; var21++) {
               if (var17.handle(this.itemProject[var21]) != this.responseCompute[var21]) {
                  var19 = false;
                  break;
               }
            }

            if (var19) {
               var3.data |= 1L << var4;
               return;
            }
         }

         if (var3.context == null) {
            var3.context = new DeterministicNoiseTable[this.rendererScan];
         }

         if (var17 == null) {
            var17 = new DeterministicNoiseTable();
            var3.context[var4] = var17;
         } else {
            var17.update();
         }

         for (int var20 = 0; var20 < var7; var20++) {
            var17.handle(this.itemProject[var20], this.responseCompute[var20]);
         }

         var3.data |= 1L << var4;
         this.timerRender = true;
      }
   }

   private boolean handle(long var1) {
      int var3 = BlockPos.unpackLongX(var1);
      int var4 = BlockPos.unpackLongY(var1);
      int var5 = BlockPos.unpackLongZ(var1);
      BlockEspScanner.PrimaryState var6 = (BlockEspScanner.PrimaryState)this.animator.get(ChunkPos.toLong(var3 >> 4, var5 >> 4));
      if (var6 != null && var6.context != null) {
         int var7 = (var4 >> 4) - this.outputCollapse;
         if (var7 >= 0 && var7 < this.rendererScan) {
            DeterministicNoiseTable var8 = var6.context[var7];
            return var8 != null && var8.handle(var1) != -1;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int handle(long[] var1, byte[] var2, int[] var3, int var4, int var5, int var6, double var7, double var9, double var11, double var13) {
      this.scaleSave = false;
      this.colorCompute = false;
      this.indexBind = 0.0;
      this.configCollapse = 0;
      if (var4 > 0 && var5 != 0 && this.rendererScan != 0 && this.profileInvoke != Integer.MIN_VALUE) {
         int var15 = TemporalNoise.handle();
         double var16 = var13 * var13;
         int var18 = 0;

         for (int var19 = 0; var19 < this.colorMeasure && var18 < var4; var19++) {
            byte var20 = this.windowConvert[var19];
            if ((var5 & 1 << var20) != 0 && var15 - this.presetWrite[var19] <= 520) {
               long var21 = this.presetSave[var19];
               if (!this.handle(var21) && this.handle(var21, var7, var9, var11, var16)) {
                  var1[var18] = var21;
                  var2[var18] = var20;
                  var3[var18] = TemporalNoise.handle(this.presetWrite[var19]) | 65536;
                  var18++;
                  this.configCollapse++;
               }
            }
         }

         boolean var40 = (var5 & 4095) != 0;
         boolean var41 = (var5 & 4190208) != 0;
         int var42 = MathHelper.clamp((int)Math.ceil(var13 / 16.0) + 1, 1, 33);
         int var22 = MathHelper.clamp(ChunkSectionPos.getSectionCoord(MathHelper.floor(var9)) - this.outputCollapse, 0, Math.max(this.rendererScan - 1, 0));
         int var23 = pointEncode[var42];

         for (int var24 = 0; var24 < var23; var24++) {
            if (var18 >= var4) {
               this.scaleSave = true;
               break;
            }

            int var25 = handler[var24];
            int var26 = animationDraw[var24];
            BlockEspScanner.PrimaryState var27 = (BlockEspScanner.PrimaryState)this.animator
               .get(ChunkPos.toLong(this.profileInvoke + var25, this.sourceSchedule + var26));
            if (var27 != null && var27.data != 0L && var27.context != null) {
               boolean var28 = var40 && Math.abs(var25) <= var6 && Math.abs(var26) <= var6;
               if (var41 || var28) {
                  for (int var29 = 0; var29 < this.rendererScan * 2 && var18 < var4; var29++) {
                     int var30 = var29 + 1 >> 1;
                     if (var30 >= this.rendererScan) {
                        break;
                     }

                     int var31 = (var29 & 1) == 0 ? var22 - var30 : var22 + var30;
                     if (var31 >= 0 && var31 < this.rendererScan && (var27.data & 1L << var31) != 0L) {
                        DeterministicNoiseTable var32 = var27.context[var31];
                        if (var32 != null) {
                           long[] var33 = var32.compute();
                           byte[] var34 = var32.resolve();

                           for (int var35 = 0; var35 < var34.length; var35++) {
                              byte var36 = var34[var35];
                              if (var36 != -1 && (var5 & 1 << var36) != 0 && (var36 >= 12 || var28)) {
                                 long var37 = var33[var35];
                                 if (!this.handle(var37, var7, var9, var11, var16)) {
                                    this.scaleSave = true;
                                 } else {
                                    if (var18 >= var4) {
                                       this.scaleSave = true;
                                       break;
                                    }

                                    int var39 = this.animationSchedule.process(var37, var27.config);
                                    var1[var18] = var37;
                                    var2[var18] = var36;
                                    if (var15 - var39 > 4000) {
                                       var3[var18] = 65535;
                                    } else {
                                       var3[var18] = TemporalNoise.handle(var39);
                                       this.colorCompute = true;
                                    }

                                    this.indexBind = Math.max(this.indexBind, this.actionRead);
                                    var18++;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return var18;
      } else {
         return 0;
      }
   }

   public boolean resolve() {
      return this.scaleSave;
   }

   public boolean update() {
      return this.colorCompute;
   }

   public double apply() {
      return Math.sqrt(this.indexBind);
   }

   public int execute() {
      return this.configCollapse;
   }

   private boolean handle(long var1, double var3, double var5, double var7, double var9) {
      double var11 = BlockPos.unpackLongX(var1) + 0.5 - var3;
      double var13 = BlockPos.unpackLongY(var1) + 0.5 - var5;
      double var15 = BlockPos.unpackLongZ(var1) + 0.5 - var7;
      this.actionRead = var11 * var11 + var13 * var13 + var15 * var15;
      return this.actionRead <= var9;
   }

   static {
      byte var0 = 67;
      int var1 = var0 * var0;
      int[] var2 = new int[var1];
      int[] var3 = new int[var1];
      long[] var4 = new long[var1];
      int var5 = 0;

      for (int var6 = -33; var6 <= 33; var6++) {
         for (int var7 = -33; var7 <= 33; var7++) {
            var2[var5] = var6;
            var3[var5] = var7;
            int var8 = Math.max(Math.abs(var6), Math.abs(var7));
            var4[var5] = (long)var8 << 40 | (long)(var6 * var6 + var7 * var7) << 16 | var5;
            var5++;
         }
      }

      Arrays.sort(var4);
      handler = new int[var1];
      animationDraw = new int[var1];

      for (int var9 = 0; var9 < var1; var9++) {
         int var11 = (int)(var4[var9] & 65535L);
         handler[var9] = var2[var11];
         animationDraw[var9] = var3[var11];
      }

      pointEncode = new int[34];
      int var10 = 0;

      for (int var12 = 0; var12 <= 33; var12++) {
         while (var10 < var1 && Math.max(Math.abs(handler[var10]), Math.abs(animationDraw[var10])) <= var12) {
            var10++;
         }

         pointEncode[var12] = var10;
      }
   }

   static final class PrimaryState {
      long instance;
      long data;
      DeterministicNoiseTable[] context;
      int config;
   }

   static final class State {
      private long[] instance = new long[1024];
      private int[] data = new int[1024];
      private boolean[] context = new boolean[1024];
      private int config = 1023;
      private int state;

      int handle(long var1) {
         long var3 = var1 * -7046029254386353131L;
         var3 ^= var3 >>> 32;
         return (int)var3 & this.config;
      }

      void handle(long var1, int var3) {
         int var4;
         for (var4 = this.handle(var1); this.context[var4]; var4 = var4 + 1 & this.config) {
            if (this.instance[var4] == var1) {
               this.data[var4] = var3;
               return;
            }
         }

         this.context[var4] = true;
         this.instance[var4] = var1;
         this.data[var4] = var3;
         this.state++;
      }

      int process(long var1, int var3) {
         for (int var4 = this.handle(var1); this.context[var4]; var4 = var4 + 1 & this.config) {
            if (this.instance[var4] == var1) {
               return this.data[var4];
            }
         }

         return var3;
      }

      boolean handle() {
         return this.state >= this.instance.length >> 1;
      }

      void handle(int var1, int var2) {
         long[] var3 = this.instance;
         int[] var4 = this.data;
         boolean[] var5 = this.context;
         this.instance = new long[var3.length];
         this.data = new int[var3.length];
         this.context = new boolean[var3.length];
         this.state = 0;

         for (int var6 = 0; var6 < var5.length; var6++) {
            if (var5[var6] && var1 - var4[var6] <= var2) {
               this.handle(var3[var6], var4[var6]);
            }
         }
      }

      void process() {
         Arrays.fill(this.context, false);
         this.state = 0;
      }
   }
}
