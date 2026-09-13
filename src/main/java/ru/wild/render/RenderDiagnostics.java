package ru.wild.render;

import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import ru.wild.core.DiagnosticCollector;
import ru.wild.core.DiagnosticSnapshot;
import ru.wild.core.RenderStateValidator;
import ru.wild.render.shader.ShaderFailureInfo;
import ru.wild.security.BuildFingerprint;
import ru.wild.util.io.DebugSnapshotWriter;
import ru.wild.util.io.DiagnosticRecordWriter;

public final class RenderDiagnostics {
   private static final RenderDiagnostics instance = new RenderDiagnostics();
   private static final long data = 2400000000L;
   private static final long context = 250000000L;
   private static final long config = 100000000L;
   private static final int state = 65536;
   private static final Logger cache = LogUtils.getLogger();
   private final BuildFingerprint output = new BuildFingerprint();
   private final DiagnosticRecordWriter current = new DiagnosticRecordWriter();
   private final RenderDiagnosticBuffer active = new RenderDiagnosticBuffer();
   private final DebugSnapshotWriter mode = new DebugSnapshotWriter();
   private long selection;
   private long enabled;
   private long renderer;
   private long handler = -1L;
   private long animationDraw = -1L;
   private boolean pointEncode;
   private boolean animator;
   private boolean source;
   private boolean target;
   private boolean pending;
   private boolean previous;
   private long latest;
   private int summary;
   private int matrixBlend;
   private int vectorMatch;
   private String itemProject = "0x0000000000000000";
   private String responseCompute = "none";
   private String providerFetch = "none";
   private String profileDraw = "GL clean";
   private String vectorPerform = "Matrix finite";
   private String eventAttach = "ожидание";
   private String serverRead = "ожидание  none";
   private String positionAdvance = "none";
   private String frameCheck = "none";
   private String moduleCollect = "Ожидание";
   private String providerClose = "Inject HEAD/TAIL";
   private String presetSave = "Local encrypted";
   private String windowConvert = "none";
   private String presetWrite = "none";
   private String colorMeasure = "none";
   private String animationSchedule = "0";
   private String rendererScan = "latest.log";
   private String sourceBuild = "latest.log";
   private final String[] outputCollapse = new String[96];
   private final int[] profileInvoke = new int[96];
   private String sourceSchedule = "0";
   private String timerRender = "0";
   private int scaleSave;
   private int colorCompute;
   private boolean scaleAdapt;

   private RenderDiagnostics() {
      this.current.handle(this.output);
      this.measure();
   }

   public static RenderDiagnostics handle() {
      return instance;
   }

   public void process() {
      if (this.pointEncode) {
         this.process(8193, 257);
      }

      this.pointEncode = true;
      this.process(257);
   }

   public void compute() {
      if (!this.pointEncode) {
         this.process(8193, 258);
      }

      this.process(258);
      this.pointEncode = false;
      this.unload();
   }

   public void resolve() {
      this.selection++;
      if (this.animator) {
         this.process(8193, 513);
      }

      this.animator = true;
      this.process(513);
      boolean var1 = RenderStateValidator.handle(this.output);
      if (!var1) {
         this.process(12289, 513);
      }
   }

   public void update() {
      if (!this.animator) {
         this.process(8193, 514);
      }

      this.process(514);
      this.animator = false;
      int var1 = GlErrorNames.resolve();
      if (var1 != 0) {
         this.compute(var1);
      }
   }

   public void apply() {
      if (this.source) {
         this.process(8193, 769);
      }

      this.source = true;
      this.process(769);
   }

   public void execute() {
      if (!this.source) {
         this.process(8193, 770);
      }

      this.process(770);
      this.source = false;
   }

   public void handle(int var1, int var2) {
      if (this.target) {
         this.process(8193, 1025);
      }

      this.target = true;
      this.process(1025);
      this.output.handle(var1);
      this.output.handle(var2);
   }

   public void prepare() {
      if (!this.target) {
         this.process(8193, 1026);
      }

      this.process(1026);
      this.target = false;
   }

   public void check() {
      if (this.pending) {
         this.process(8193, 1281);
      }

      this.pending = true;
      this.process(1281);
   }

   public void handle(int var1, int var2, int var3) {
      this.process(1282);
      this.pending = false;
      int var4 = GlErrorNames.handle();
      int var5 = GlErrorNames.process();
      int var6 = GlErrorNames.compute();
      if (var1 != var4 || var2 != var5 || var3 != var6) {
         this.process(4098, var4 ^ var5 ^ var6);
      }
   }

   public void handle(DiagnosticCollector var1) {
      if (var1 != null) {
         this.output.handle(var1.handle());
         var1.handle(this.current);
      }
   }

   public void onTick() {
      long var1 = System.nanoTime();
      int var3 = (int)(this.output.handle() ^ var1 >>> 13 ^ 20481L);
      this.responseCompute = resolve(var3);
      this.providerFetch = RenderDiagnosticKind.handle(20481);
      this.previous = true;
      this.summary = var3;
      this.matrixBlend = 20481;
      this.vectorMatch = 0;
      this.latest = var1;
      this.eventAttach = "ожидает";
      this.moduleCollect = "Ручной слепок";
      this.serverRead = "pending  " + this.responseCompute;
      cache.info("[WildCore] tracker={} code={} snapshot=pending", this.responseCompute, this.providerFetch);
   }

   public void select() {
      try {
         Path var1 = this.mode.handle();
         Util.getOperatingSystem().open(var1.toFile());
      } catch (Throwable var2) {
         this.eventAttach = "папка недоступна";
         this.moduleCollect = "Open folder failed";
         cache.warn("[WildCore] tracker={} code=OPEN_FOLDER_FAILED", this.responseCompute);
      }
   }

   public void refresh() {
      try {
         Path var1 = this.fetch();
         Files.createDirectories(var1);
         Util.getOperatingSystem().open(var1.toFile());
         this.rendererScan = "логи открыты";
      } catch (Throwable var2) {
         this.rendererScan = "ошибка открытия";
         cache.warn("[WildCore] tracker={} code=OPEN_LOGS_FAILED", this.responseCompute);
      }
   }

   public void render() {
      this.scaleAdapt = true;
      this.renderer = 0L;
      this.encodePoint();
   }

   public void tick() {
      this.scaleAdapt = !this.scaleAdapt;
      if (this.scaleAdapt) {
         this.renderer = 0L;
         this.encodePoint();
      }
   }

   public void drawAnimation() {
      this.scaleAdapt = false;
   }

   public void encodePoint() {
      Path var1 = this.fetch().resolve("latest.log");
      this.sourceBuild = "latest.log";
      this.measure();
      if (!Files.exists(var1)) {
         this.handler = -1L;
         this.animationDraw = -1L;
         this.rendererScan = "latest.log not found";
         this.compute("WARN latest.log not found", 2);
      } else {
         try (RandomAccessFile var2 = new RandomAccessFile(var1.toFile(), "r")) {
            long var3 = var2.length();
            this.handler = var3;
            this.animationDraw = Files.getLastModifiedTime(var1).toMillis();
            int var5 = (int)Math.min(65536L, var3);
            byte[] var6 = new byte[var5];
            var2.seek(Math.max(0L, var3 - var5));
            var2.readFully(var6);
            this.handle(new String(var6, StandardCharsets.UTF_8));
            this.rendererScan = "loaded " + this.colorCompute;
         } catch (Throwable var9) {
            this.rendererScan = "read failed";
            this.compute("ERROR " + var9.getClass().getSimpleName(), 3);
            cache.warn("[WildCore] tracker={} code=READ_LOG_FAILED", this.responseCompute);
         }
      }
   }

   public boolean animate() {
      return this.scaleAdapt;
   }

   public void handle(String var1, Throwable var2) {
      this.scaleSave++;
      this.windowConvert = process(var1, 96);
      this.presetWrite = var2 == null ? "unknown" : process(var2.getClass().getName(), 96);
      this.colorMeasure = var2 == null ? "no throwable" : process(var2.getMessage(), 160);
      this.animationSchedule = Integer.toString(this.scaleSave);
      this.rendererScan = "shader exception";
      this.compute(ShaderFailureInfo.handle(this.windowConvert, this.scaleSave), 3);
      int var3 = 0;
      Throwable var4 = var2;
      if (var4 == null) {
         this.compute("cause[0]=unknown", 3);
         this.compute("message=no throwable", 3);
      }

      for (int var5 = 0; var4 != null && var5 < 3; var5++) {
         this.compute(ShaderFailureInfo.handle(var5, var4), 3);
         this.compute(ShaderFailureInfo.handle(var4), 3);
         String var6 = ShaderFailureInfo.process(var4);
         if (!"none".equals(var6)) {
            this.compute(var6, 4);
         }

         StackTraceElement[] var7 = var4.getStackTrace();

         for (int var8 = 0; var8 < var7.length && var3 < 14; var8++) {
            this.compute(ShaderFailureInfo.handle(var7[var8]), 3);
            var3++;
         }

         var4 = var4.getCause();
      }

      this.process(24577, this.colorMeasure.hashCode());
      cache.error("[WildCore] tracker={} shaderStage={} exception={}", new Object[]{this.responseCompute, this.windowConvert, this.presetWrite, var2});
   }

   public void handle(String var1, int var2) {
      if (var2 != 0) {
         this.process(4097, var2);
         this.profileDraw = GlErrorNames.handle(var2);
         this.rendererScan = "OpenGL error";
         this.compute(ShaderFailureInfo.process(var1, var2), 4);
         this.compute(ShaderFailureInfo.handle(), 4);
      }
   }

   public void process(String var1, Throwable var2) {
      this.handle(var1, var2);
      if (var2 instanceof Error var4) {
         throw var4;
      } else if (var2 instanceof RuntimeException var3) {
         throw var3;
      } else {
         throw new IllegalStateException("WildCore shader failure at " + this.windowConvert, var2);
      }
   }

   public void handle(DiagnosticSnapshot var1) {
      if (var1 != null) {
         var1.data = this.active.handle() == 0 ? "Nominal" : "Anomaly";
         var1.context = this.itemProject;
         var1.config = this.responseCompute;
         var1.state = this.providerFetch;
         var1.cache = this.profileDraw;
         var1.output = this.vectorPerform;
         var1.current = this.eventAttach;
         var1.active = this.serverRead;
         var1.mode = this.positionAdvance;
         var1.selection = this.frameCheck;
         var1.enabled = this.moduleCollect;
         var1.renderer = this.providerClose;
         var1.handler = this.presetSave;
         var1.animationDraw = this.windowConvert;
         var1.pointEncode = this.presetWrite;
         var1.animator = this.colorMeasure;
         var1.source = this.animationSchedule;
         var1.target = this.rendererScan;
         var1.pending = this.sourceBuild;
         var1.previous = this.sourceSchedule;
         var1.latest = this.timerRender;
         var1.vectorMatch = this.active.handle();
         var1.itemProject = this.active.process();
         var1.responseCompute = this.colorCompute;
         var1.vectorPerform = this.scaleAdapt;

         for (int var2 = 0; var2 < 96; var2++) {
            var1.summary[var2] = this.outputCollapse[var2];
            var1.matrixBlend[var2] = this.profileInvoke[var2];
         }

         var1.providerFetch = this.selection;
         var1.profileDraw = this.previous;
      }
   }

   public long load() {
      return this.output.handle();
   }

   public long save() {
      return this.selection;
   }

   public int submit() {
      return this.active.handle();
   }

   public void handle(DataOutputStream var1) throws IOException {
      this.active.handle(var1);
   }

   public String getName(int var1) {
      return "WS-" + resolve(var1);
   }

   private void process(int var1) {
      this.output.handle(var1);
      this.output.process(this.selection);
   }

   private void compute(int var1) {
      this.handle("GameRenderer.tail", var1);
   }

   private void process(int var1, int var2) {
      long var3 = System.nanoTime();
      int var5 = (int)(this.output.handle() ^ var3 >>> 11 ^ (long)var1 << 16 ^ var2);
      this.active.handle(var3, var5, var1, var2, this.output.handle());
      this.responseCompute = resolve(var5);
      this.providerFetch = RenderDiagnosticKind.handle(var1);
      this.handle(var3, var5, var1, var2);
      cache.warn("[WildCore] tracker={} code={} snapshot=pending", this.responseCompute, this.providerFetch);
   }

   private void handle(long var1, int var3, int var4, int var5) {
      if (!this.previous) {
         this.previous = true;
         this.summary = var3;
         this.matrixBlend = var4;
         this.vectorMatch = var5;
         this.latest = var1 + 2400000000L;
         this.eventAttach = "ожидает";
         this.moduleCollect = "Ожидает запись";
      }
   }

   private void unload() {
      long var1 = System.nanoTime();
      if (var1 - this.enabled >= 250000000L) {
         this.enabled = var1;
         this.itemProject = "0x" + Long.toUnsignedString(this.output.handle(), 16);
         this.sourceSchedule = Integer.toString(this.active.handle());
         this.timerRender = Long.toString(this.selection);
         this.animationSchedule = Integer.toString(this.scaleSave);
         this.serverRead = this.eventAttach + "  " + this.responseCompute;
         this.moduleCollect = this.previous ? "Ожидает запись" : this.eventAttach;
         if (this.active.handle() == 0) {
            this.profileDraw = "GL clean";
            this.vectorPerform = "Matrix finite";
         }
      }

      if (this.scaleAdapt && var1 - this.renderer >= 100000000L) {
         this.renderer = var1;
         this.blendMatrix();
      }

      if (this.previous && var1 >= this.latest) {
         this.previous = false;
         this.eventAttach = "запись";
         this.moduleCollect = "Запись";

         try {
            Path var3 = this.mode.handle(this, this.summary, this.matrixBlend, this.vectorMatch);
            this.positionAdvance = var3.toString();
            this.frameCheck = var3.getFileName().toString();
            this.eventAttach = "записан";
            this.moduleCollect = "Записан";
         } catch (Throwable var9) {
            this.eventAttach = "ошибка";
            this.moduleCollect = "Ошибка слепка";
            short var4 = 16385;
            int var5 = var9.getClass().getName().hashCode();
            long var6 = this.output.handle();
            int var8 = (int)(var6 ^ var5 ^ var4);
            this.active.handle(System.nanoTime(), var8, var4, var5, var6);
            this.responseCompute = resolve(var8);
            this.providerFetch = RenderDiagnosticKind.handle(var4);
            cache.warn("[WildCore] tracker={} code={} snapshot=failed", this.responseCompute, this.providerFetch);
         }
      }
   }

   private static String resolve(int var0) {
      String var1 = Integer.toUnsignedString(var0, 16).toUpperCase(Locale.ROOT);
      return var1.length() >= 8 ? var1.substring(var1.length() - 8) : "00000000".substring(var1.length()) + var1;
   }

   private static String process(String var0, int var1) {
      if (var0 != null && !var0.isBlank()) {
         String var2 = var0.replace('\n', ' ').replace('\r', ' ').trim();
         return var2.length() <= var1 ? var2 : var2.substring(0, Math.max(0, var1 - 3)) + "...";
      } else {
         return "none";
      }
   }

   private Path fetch() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      return (var1 == null ? Path.of(System.getProperty("user.dir", ".")) : var1.runDirectory.toPath()).resolve("logs");
   }

   private void measure() {
      this.colorCompute = 0;

      for (int var1 = 0; var1 < this.outputCollapse.length; var1++) {
         this.outputCollapse[var1] = "";
         this.profileInvoke[var1] = 0;
      }
   }

   private void blendMatrix() {
      Path var1 = this.fetch().resolve("latest.log");

      try {
         if (!Files.exists(var1)) {
            if (this.handler != -1L || this.colorCompute == 0) {
               this.encodePoint();
            }

            return;
         }

         long var2 = Files.size(var1);
         long var4 = Files.getLastModifiedTime(var1).toMillis();
         if (var2 != this.handler || var4 != this.animationDraw) {
            this.encodePoint();
         }
      } catch (Throwable var6) {
         this.encodePoint();
      }
   }

   private void handle(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         int var2 = 0;
         int var3 = var1.length();

         for (int var4 = 0; var4 <= var3; var4++) {
            if (var4 == var3 || var1.charAt(var4) == '\n') {
               int var5 = var4;
               if (var5 > var2 && var1.charAt(var5 - 1) == '\r') {
                  var5--;
               }

               if (var5 > var2) {
                  String var6 = process(var1.substring(var2, var5), 170);
                  this.compute(var6, this.process(var6));
               }

               var2 = var4 + 1;
            }
         }

         if (this.colorCompute == 0) {
            this.compute("INFO latest.log has no visible lines", 1);
         }
      } else {
         this.compute("INFO latest.log is empty", 1);
      }
   }

   private void compute(String var1, int var2) {
      if (this.colorCompute < this.outputCollapse.length) {
         this.outputCollapse[this.colorCompute] = var1;
         this.profileInvoke[this.colorCompute] = var2;
         this.colorCompute++;
      } else {
         for (int var3 = 1; var3 < this.outputCollapse.length; var3++) {
            this.outputCollapse[var3 - 1] = this.outputCollapse[var3];
            this.profileInvoke[var3 - 1] = this.profileInvoke[var3];
         }

         int var4 = this.outputCollapse.length - 1;
         this.outputCollapse[var4] = var1;
         this.profileInvoke[var4] = var2;
      }
   }

   private int process(String var1) {
      if (var1 == null) {
         return 0;
      } else if (this.handle(var1, "ERROR") || this.handle(var1, "Exception") || this.handle(var1, "Crash")) {
         return 3;
      } else if (this.handle(var1, "WARN")) {
         return 2;
      } else if (this.handle(var1, "Shader") || this.handle(var1, "GL_") || this.handle(var1, "OpenGL")) {
         return 4;
      } else {
         return !this.handle(var1, "DEBUG") && !this.handle(var1, "TRACE") ? 1 : 0;
      }
   }

   private boolean handle(String var1, String var2) {
      return var1.indexOf(var2) >= 0;
   }
}
