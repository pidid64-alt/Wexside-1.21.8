package ru.wild.gui.hud;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import ru.wild.audio.NativeMediaController;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "MusicPlayer", process = "w")
public final class MusicPlayerHudRenderer extends ThemePresets {
   private static final MusicPlayerHudRenderer instance = new MusicPlayerHudRenderer();
   private static final String responseCompute = "Ожидание...";
   private static final String providerFetch = "Нет данных";
   private static final long profileDraw = 160L;
   private static final DoubleAnimator vectorPerform = new DoubleAnimator();
   private static final ExecutorService eventAttach = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Wild-Media-Fetch");
      var1.setDaemon(true);
      return var1;
   });
   private volatile String serverRead = "Ожидание...";
   private volatile String positionAdvance = "Нет данных";
   private volatile boolean frameCheck = false;
   private volatile double moduleCollect = 0.0;
   private volatile long providerClose = 0L;
   private volatile long presetSave = 0L;
   private volatile long windowConvert = 0L;
   private volatile long presetWrite = 10000000L;
   private volatile long colorMeasure = 0L;
   private volatile boolean animationSchedule = false;
   private volatile double rendererScan = 0.0;
   private final DoubleAnimator sourceBuild = new DoubleAnimator();
   private volatile float outputCollapse = 0.0F;
   private volatile float profileInvoke = 0.0F;
   private volatile float sourceSchedule = 0.0F;
   private volatile float timerRender = 0.0F;
   private volatile byte[] scaleSave = null;
   private volatile int colorCompute = 0;
   private volatile boolean scaleAdapt = false;
   private int textureRun = Integer.MIN_VALUE;
   private int indexBind = -1;
   private Identifier actionRead = null;
   private volatile int configCollapse = 0;
   private volatile int dataValidate = 0;
   private MediaPlayerInfo scaleRender;
   private long clientRefresh = 0L;
   private static boolean keyFilter = false;
   private final AtomicBoolean requestAdapt = new AtomicBoolean(false);
   private final AtomicReference<MusicPlayerHudRenderer.DataRecord> timerMeasure = new AtomicReference<>();
   private final DoubleAnimator vectorEncode = new DoubleAnimator();

   private MusicPlayerHudRenderer() {
      HudProfileConfig.handle(this);
   }

   public static MusicPlayerHudRenderer process() {
      return instance;
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   public static void encodePoint() {
      eventAttach.shutdownNow();
      instance.timerMeasure.set(null);
      instance.requestAdapt.set(false);
   }

   public void process(RoundedRectRenderer var1) {
      if (MinecraftContext.toggleState.player != null) {
         vectorPerform.handle();
         vectorPerform.handle(1.0, 0.22F, Easings.handler, false);
         float var2 = vectorPerform.update();
         if (!(var2 <= 0.01F)) {
            float var3 = ThemeRenderer.handle().execute();
            float var4 = ThemeRenderer.handle().prepare();
            boolean var5 = ThemeRenderer.handle().onTick();
            boolean var6 = ThemeRenderer.handle().check();
            if (var5
               && this.sourceSchedule > 0.0F
               && this.handle(var3, var4, this.outputCollapse - 4.0F, this.profileInvoke, this.sourceSchedule + 8.0F, this.timerRender)) {
               this.animationSchedule = true;
            }

            if (this.animationSchedule) {
               ThemeRenderer.handle().process();
            }

            this.animate();
            this.save();
            this.submit();
            float var7 = 7.0F;
            float var8 = 5.0F;
            float var9 = 160.0F;
            float var10 = 26.0F;
            float var11 = 24.0F;
            float var12 = var9 + var7 * 2.0F;
            float var13 = var7 + var9 + var8 + var10 + var8 + var11 + var7;
            ThemeRenderer.PrimaryCacheEntry var14 = ThemeRenderer.handle().handle("HUD_MusicPlayer", 10.0F, 10.0F, var12, var13);
            float var15 = var14.data;
            float var16 = var14.context;
            float var17 = var14.config;
            float var18 = var14.state;
            float var19 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
            float var20 = MinecraftContext.toggleState.getWindow().getFramebufferHeight();
            if (var17 > 1.0F && var18 > 1.0F) {
               var15 = Math.max(2.0F, Math.min(var15, var19 - var17 - 2.0F));
               var16 = Math.max(2.0F, Math.min(var16, var20 - var18 - 2.0F));
            }

            this.handle(var15, var16, var17, var18);
            float var21 = var17 / Math.max(1.0F, var12);
            float var22 = var18 / Math.max(1.0F, var13);
            float var23 = Math.min(var21, var22);
            float var24 = var7 * var21;
            float var25 = var7 * var22;
            float var26 = var8 * var22;
            float var27 = var9 * var21;
            float var28 = var9 * var22;
            float var29 = var10 * var22;
            float var30 = var11 * var22;
            float var31 = var2 * this.target.compute();
            float var32 = this.onTick(var31);
            int var33 = (int)(255.0F * var31);
            int var34 = this.handle(var31);
            int var35 = this.process(var31);
            int var36 = this.compute(var31);
            int var37 = this.resolve(var31);
            int var38 = this.update(var31);
            int var39 = this.apply(var31);
            int var40 = this.execute(var31);
            int var41 = this.previous.compute().equals("Светлый") ? var40 : PackedColor.compute(255, 255, 255, var33);
            boolean var42 = this.select();
            float var43 = 14.0F;
            this.handle(var1, var15, var16, var17, var18, var43, var31);
            float var44 = var15 + var24;
            float var45 = var16 + var25;
            if (var42) {
               this.process(var1, var44, var45, var27, var28, 11.0F, var31);
            }

            var1.handle(var44, var45, var27, var28, 11.0F, 11.0F, 4.0F, 4.0F);
            if (this.actionRead != null) {
               int var46 = handle(this.actionRead);
               if (var46 > 0) {
                  float var47 = 0.0F;
                  float var48 = 0.0F;
                  float var49 = 1.0F;
                  float var50 = 1.0F;
                  if (this.configCollapse > 0 && this.dataValidate > 0) {
                     if (this.configCollapse > this.dataValidate) {
                        float var51 = (float)this.dataValidate / this.configCollapse;
                        float var52 = (1.0F - var51) / 2.0F;
                        var47 = var52;
                        var49 = 1.0F - var52;
                     } else if (this.dataValidate > this.configCollapse) {
                        float var83 = (float)this.configCollapse / this.dataValidate;
                        float var85 = (1.0F - var83) / 2.0F;
                        var48 = var85;
                        var50 = 1.0F - var85;
                     }
                  }

                  var1.handle(var46, var44, var45, var27, var28, var47, var48, var49, var50);
               } else if (!var42) {
                  var1.handle(var44, var45, var27, var28, 0.0F, var35);
               }
            } else if (!var42) {
               var1.handle(var44, var45, var27, var28, 0.0F, var35);
            }

            float var78 = 90.0F * var22;
            float var79 = var45 + var28 - var78;
            var1.process(
               var44, var79, var27, var78, 11.0F, 11.0F, 4.0F, 4.0F, PackedColor.compute(0, 0, 0, 0), PackedColor.compute(0, 0, 0, (int)(220.0F * var32))
            );
            float var80 = 26.0F * var23;
            float var81 = 22.0F * var23;
            float var82 = var45 + var28 - 32.0F * var22;
            float var84 = var27 - 16.0F * var21;
            this.handle(var1, FontRegistry.config, this.serverRead, var44 + 10.0F * var21, var82, var80, var38, var79, var78, var84, var44 + var27 / 2.0F);
            this.handle(
               var1,
               FontRegistry.instance,
               this.positionAdvance,
               var44 + 10.0F * var21,
               var82 + 15.0F * var22,
               var81,
               var39,
               var79,
               var78,
               var84,
               var44 + var27 / 2.0F
            );
            var1.apply();
            float var86 = var45 + var28 + var26;
            if (var42) {
               this.process(var1, var44, var86, var27, var29, 7.0F, var31);
            } else {
               var1.handle(var44, var86, var27, var29, 4.0F, 4.0F, 4.0F, 4.0F, var36);
               if (this.update()) {
                  var1.handle(var44, var86, var27, var29, 4.0F, var37, 1.0F);
               }
            }

            float var53 = 20.0F * var23;
            float var54 = var44 + var27 / 2.0F;
            float var55 = var86 + var29 / 2.0F + 4.0F * var22;
            String var56 = this.frameCheck ? "x" : "p";
            String var57 = "z";
            String var58 = "c";
            float var59 = TextMeasureCache.handle(FontRegistry.current, var56, var53).instance;
            float var60 = TextMeasureCache.handle(FontRegistry.current, var57, var53).instance;
            float var61 = TextMeasureCache.handle(FontRegistry.current, var58, var53).instance;
            float var62 = 22.0F * var21;
            var1.handle(FontRegistry.current, var54 - var62 - var60 / 2.0F, var55, var53, var57, var41);
            var1.handle(FontRegistry.current, var54 - var59 / 2.0F, var55, var53, var56, var41);
            var1.handle(FontRegistry.current, var54 + var62 - var61 / 2.0F, var55, var53, var58, var41);
            if (var5 && !this.animationSchedule) {
               float var63 = 24.0F * var21;
               if (this.handle(var3, var4, var54 - var62 - var63 / 2.0F, var86, var63, var29)) {
                  if (NativeMediaController.resolve()) {
                     NativeMediaController.compute();
                  }
               } else if (this.handle(var3, var4, var54 - var63 / 2.0F, var86, var63, var29)) {
                  if (NativeMediaController.resolve()) {
                     NativeMediaController.handle();
                  }
               } else if (this.handle(var3, var4, var54 + var62 - var63 / 2.0F, var86, var63, var29) && NativeMediaController.resolve()) {
                  NativeMediaController.process();
               }
            }

            float var87 = var86 + var29 + var26;
            if (var42) {
               this.process(var1, var44, var87, var27, var30, 8.0F, var31);
            } else {
               var1.handle(var44, var87, var27, var30, 4.0F, 4.0F, 11.0F, 11.0F, var36);
            }

            float var64 = 20.0F * var23;
            String var65 = this.handle(this.presetSave);
            float var66 = TextMeasureCache.handle(FontRegistry.instance, var65, var64).instance;
            float var67 = 10.0F * var21;
            float var68 = 8.0F * var21;
            float var69 = var44 + var67 + var66 + var68;
            float var70 = var27 - var67 * 2.0F - var66 * 2.0F - var68 * 2.0F;
            this.outputCollapse = var69;
            this.profileInvoke = var87;
            this.sourceSchedule = var70;
            this.timerRender = var30;
            long var71 = this.providerClose;
            boolean var73 = this.handle(var3, var4, var69 - 4.0F * var21, var87, var70 + 8.0F * var21, var30);
            if (this.animationSchedule) {
               this.rendererScan = Math.max(0.0, Math.min(1.0, (var3 - var69) / Math.max(1.0F, var70)));
               var71 = (long)(this.rendererScan * this.presetSave);
               if (!var6) {
                  this.animationSchedule = false;
                  if (this.presetSave > 0L && NativeMediaController.resolve()) {
                     long var74 = (long)((double)var71 / this.presetWrite * 1000.0);
                     NativeMediaController.handle(var74);
                     this.providerClose = var71;
                     this.colorMeasure = System.currentTimeMillis();
                     this.windowConvert = System.currentTimeMillis();
                  }
               }
            } else if (this.frameCheck && this.presetSave > 0L) {
               long var88 = System.currentTimeMillis() - this.windowConvert;
               long var76 = (long)(var88 * (this.presetWrite / 1000.0));
               var71 += Math.max(0L, var76);
               if (var71 > this.presetSave) {
                  var71 = this.presetSave;
               }
            }

            this.moduleCollect = this.presetSave > 0L ? (double)var71 / this.presetSave : 0.0;
            String var89 = this.handle(var71);
            float var75 = var87 + var30 / 2.0F + 3.0F * var22;
            var1.handle(FontRegistry.instance, var44 + var67, var75, var64, var89, var39);
            var1.handle(FontRegistry.instance, var44 + var27 - var67 - var66, var75, var64, var65, var39);
            this.sourceBuild.handle();
            this.sourceBuild.handle(!var73 && !this.animationSchedule ? 0.0 : 1.0, 0.15F, Easings.handler, false);
            float var90 = 4.0F * var22 + 4.0F * var22 * this.sourceBuild.update();
            float var77 = var87 + (var30 - var90) / 2.0F;
            this.vectorEncode.handle();
            this.vectorEncode.handle((float)this.moduleCollect, this.animationSchedule ? 0.05F : 0.2F, Easings.cache, false);
            if (var42) {
               this.process(var1, var69, var77, var70, var90, var90 / 2.0F, var31);
            } else {
               var1.handle(var69, var77, var70, var90, var90 / 2.0F, PackedColor.compute(100, 100, 100, (int)(80.0F * var32)));
            }

            var1.handle(var69, var77, var70 * this.vectorEncode.update(), var90, var90 / 2.0F, var40);
            ThemeRenderer.handle().handle(var14);
            NeoStyleOptions.handle(
               var1,
               this,
               var15,
               var16,
               var17,
               var18,
               MinecraftContext.toggleState.getWindow().getScaledWidth(),
               MinecraftContext.toggleState.getWindow().getScaledHeight(),
               var14.output,
               ThemeRenderer.handle().execute(),
               ThemeRenderer.handle().prepare(),
               ThemeRenderer.handle().onTick(),
               ThemeRenderer.handle().check()
            );
         }
      }
   }

   private boolean handle(float var1, float var2, float var3, float var4, float var5, float var6) {
      return var1 >= var3 && var1 <= var3 + var5 && var2 >= var4 && var2 <= var4 + var6;
   }

   private String handle(long var1) {
      if (var1 <= 0L) {
         return "0:00";
      }

      long var3 = var1 / this.presetWrite;
      long var5 = var3 % 60L;
      return var3 / 60L + (var5 < 10L ? ":0" : ":") + var5;
   }

   private void handle(
      RoundedRectRenderer var1, FontObject var2, String var3, float var4, float var5, float var6, int var7, float var8, float var9, float var10, float var11
   ) {
      float var12 = TextMeasureCache.handle(var2, var3, var6).instance;
      if (var12 <= var10) {
         var1.handle(var2, var11 - var12 / 2.0F, var5, var6, var3, var7);
      } else {
         float var13 = var12 - var10;
         long var14 = 8000L;
         float var16 = (float)(System.currentTimeMillis() % var14) / (float)var14;
         float var17 = var16 < 0.2F
            ? 0.0F
            : (
               var16 < 0.45F
                  ? this.refresh((var16 - 0.2F) / 0.3F)
                  : (var16 < 0.7F ? 1.0F : (var16 < 0.95F ? 1.0F - this.refresh((var16 - 0.7F) / 0.25F) : 0.0F))
            );
         var1.handle(var4, var8, var10, var9, 0.0F, 0.0F, 0.0F, 0.0F);
         var1.handle(var2, var4 - var13 * var17, var5, var6, var3, var7);
         var1.apply();
      }
   }

   private float refresh(float var1) {
      float var2 = 2.0F;
      float var3 = var2 + 1.0F;
      float var4 = var1 - 1.0F;
      return 1.0F + var3 * var4 * var4 * var4 + var2 * var4 * var4;
   }

   private void animate() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.clientRefresh >= 160L) {
         this.clientRefresh = var1;
         if (this.requestAdapt.compareAndSet(false, true)) {
            eventAttach.execute(() -> {
               try {
                  this.handle(this.load());
               } catch (Throwable var5) {
                  if (!keyFilter) {
                     keyFilter = true;
                  }

                  this.handle(MusicPlayerHudRenderer.DataRecord.empty());
               } finally {
                  this.requestAdapt.set(false);
               }
            });
         }
      }
   }

   private MusicPlayerHudRenderer.DataRecord load() {
      if (this.scaleRender == null) {
         this.scaleRender = MediaPlayerInfo.INSTANCE;
      }

      List var1 = this.scaleRender.getMediaSessions();
      if (var1 != null && !var1.isEmpty()) {
         MediaInfo var2 = null;

         for (IMediaSession var4 : (List<IMediaSession>) var1) {
            if (var4 != null) {
               MediaInfo var5 = var4.getMedia();
               if (var5 != null && this.handle(var5)) {
                  if (var2 == null) {
                     var2 = var5;
                  }

                  if (var5.isPlaying()) {
                     var2 = var5;
                     break;
                  }
               }
            }
         }

         return var2 == null ? MusicPlayerHudRenderer.DataRecord.empty() : MusicPlayerHudRenderer.DataRecord.from(var2);
      } else {
         return MusicPlayerHudRenderer.DataRecord.empty();
      }
   }

   private void handle(MusicPlayerHudRenderer.DataRecord var1) {
      this.timerMeasure.set(var1);
      if (MinecraftContext.toggleState != null) {
         MinecraftContext.toggleState.execute(this::save);
      }
   }

   private void save() {
      MusicPlayerHudRenderer.DataRecord var1 = this.timerMeasure.getAndSet(null);
      if (var1 != null) {
         if (!var1.available()) {
            this.unload();
         } else {
            this.serverRead = var1.title();
            this.positionAdvance = var1.artist();
            this.frameCheck = var1.playing();
            if (!this.animationSchedule && System.currentTimeMillis() - this.colorMeasure > 2000L) {
               this.providerClose = var1.position();
            }

            this.presetSave = var1.duration();
            this.windowConvert = System.currentTimeMillis();
            if (this.presetSave > 360000000L) {
               this.presetWrite = 10000000L;
            } else if (this.presetSave > 100000L) {
               this.presetWrite = 1000L;
            } else {
               this.presetWrite = 1L;
            }

            this.handle(var1.thumbnail());
         }
      }
   }

   private void handle(byte[] var1) {
      if (var1 != null && var1.length > 0) {
         int var2 = Arrays.hashCode(var1);
         if (var2 != this.colorCompute || this.scaleSave == null || this.scaleSave.length != var1.length) {
            this.scaleSave = Arrays.copyOf(var1, var1.length);
            this.colorCompute = var2;
            this.scaleAdapt = true;
         }
      } else if (this.scaleSave != null || this.colorCompute != 0) {
         this.scaleSave = null;
         this.colorCompute = 0;
         this.scaleAdapt = true;
      }
   }

   private void submit() {
      byte[] var1 = this.scaleSave;
      int var2 = this.colorCompute;
      boolean var3 = this.scaleAdapt;
      if (var1 == null) {
         if (var3) {
            this.scaleAdapt = false;
            this.textureRun = Integer.MIN_VALUE;
            this.indexBind = -1;
            this.configCollapse = 0;
            this.dataValidate = 0;
            if (this.actionRead != null) {
               MinecraftContext.toggleState.getTextureManager().destroyTexture(this.actionRead);
               this.actionRead = null;
            }
         }
      } else if (var3 || var2 != this.textureRun || var1.length != this.indexBind) {
         try {
            this.scaleAdapt = false;
            this.textureRun = var2;
            this.indexBind = var1.length;
            NativeImage var4 = NativeImage.read(new ByteArrayInputStream(var1));
            this.configCollapse = var4.getWidth();
            this.dataValidate = var4.getHeight();
            if (this.actionRead != null) {
               MinecraftContext.toggleState.getTextureManager().destroyTexture(this.actionRead);
            }

            NativeImageBackedTexture var5 = new NativeImageBackedTexture(() -> "media_cover", var4);
            this.actionRead = Identifier.of("wild", "media_cover_" + System.nanoTime());
            MinecraftContext.toggleState.getTextureManager().registerTexture(this.actionRead, var5);
         } catch (Exception var6) {
         }
      }
   }

   private void unload() {
      this.serverRead = "Ожидание...";
      this.positionAdvance = "Нет данных";
      this.frameCheck = false;
      this.moduleCollect = 0.0;
      this.providerClose = 0L;
      this.presetSave = 0L;
      this.windowConvert = System.currentTimeMillis();
      if (this.scaleSave != null || this.colorCompute != 0) {
         this.scaleSave = null;
         this.colorCompute = 0;
         this.scaleAdapt = true;
      }
   }

   private boolean handle(MediaInfo var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = var1.getTitle();
      String var3 = var1.getArtist();
      return var2 != null && !var2.isBlank() || var3 != null && !var3.isBlank() || var1.getDuration() > 0L || var1.getPosition() > 0L || var1.isPlaying();
   }

   private static int handle(Identifier var0) {
      if (MinecraftContext.toggleState == null) {
         return -1;
      }

      AbstractTexture var1 = MinecraftContext.toggleState.getTextureManager().getTexture(var0);
      return var1 != null && var1.getGlTexture() instanceof GlTexture var2 ? var2.getGlId() : -1;
   }

   record DataRecord(boolean available, String title, String artist, long position, long duration, boolean playing, byte[] thumbnail) {
      DataRecord(boolean available, String title, String artist, long position, long duration, boolean playing, byte[] thumbnail) {
         title = title != null && !title.isBlank() ? title : "Ожидание...";
         artist = artist != null && !artist.isBlank() ? artist : "Нет данных";
         position = Math.max(0L, position);
         duration = Math.max(0L, duration);
         thumbnail = thumbnail != null && thumbnail.length != 0 ? Arrays.copyOf(thumbnail, thumbnail.length) : null;
         this.available = available;
         this.title = title;
         this.artist = artist;
         this.position = position;
         this.duration = duration;
         this.playing = playing;
         this.thumbnail = thumbnail;
      }

      static MusicPlayerHudRenderer.DataRecord empty() {
         return new MusicPlayerHudRenderer.DataRecord(false, "Ожидание...", "Нет данных", 0L, 0L, false, null);
      }

      static MusicPlayerHudRenderer.DataRecord from(MediaInfo var0) {
         return new MusicPlayerHudRenderer.DataRecord(
            true, var0.getTitle(), var0.getArtist(), var0.getPosition(), var0.getDuration(), var0.isPlaying(), var0.getArtworkPng()
         );
      }

      public byte[] thumbnail() {
         return this.thumbnail == null ? null : Arrays.copyOf(this.thumbnail, this.thumbnail.length);
      }
   }
}
