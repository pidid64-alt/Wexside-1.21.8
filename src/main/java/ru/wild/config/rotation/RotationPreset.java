package ru.wild.config.rotation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import ru.wild.WildClient;

public final class RotationPreset {
   public static final String[] instance = new String[]{"Multipoint", "Center", "Eyes", "Closest"};
   public static final String[] data = new String[]{"Cycle", "Closest", "Random"};
   public static final String[] context = new String[]{"Smooth", "Static", "Locked"};
   public static final String[] config = new String[]{"FunTime", "Spooky", "Holy", "Matrix", "Smooth", "Snap"};
   public static final String state = "Custom";
   public static final int cache = 12;
   private static final Gson animationSchedule = new GsonBuilder().setPrettyPrinting().create();
   private static RotationPreset rendererScan;
   @SerializedName("name")
   public String output = "FunTime";
   @SerializedName("engine")
   public String current = "FunTime";
   @SerializedName("preset")
   public String active = "FunTime";
   @SerializedName("pointMode")
   public String mode = "Multipoint";
   @SerializedName("yawSpeedMin")
   public float selection = 35.0F;
   @SerializedName("yawSpeedMax")
   public float enabled = 55.0F;
   @SerializedName("pitchSpeedMin")
   public float renderer = 6.0F;
   @SerializedName("pitchSpeedMax")
   public float handler = 12.0F;
   @SerializedName("attackYawSpeed")
   public float animationDraw = 65.0F;
   @SerializedName("attackPitchSpeed")
   public float pointEncode = 22.0F;
   @SerializedName("yawRandom")
   public float animator = 4.0F;
   @SerializedName("pitchRandom")
   public float source = 3.0F;
   @SerializedName("oscillateX")
   public float target = 0.2F;
   @SerializedName("oscillateY")
   public float pending = 0.12F;
   @SerializedName("oscillateSpeed")
   public float previous = 1.0F;
   @SerializedName("sidePointOffset")
   public float latest = 0.0F;
   @SerializedName("returnSpeed")
   public float summary = 30.0F;
   @SerializedName("moveHead")
   public boolean matrixBlend = false;
   @SerializedName("multipointMode")
   public String vectorMatch = "Cycle";
   @SerializedName("pointDwell")
   public float itemProject = 0.9F;
   @SerializedName("pitchFollow")
   public String responseCompute = "Smooth";
   @SerializedName("yawOffset")
   public float providerFetch = 0.0F;
   @SerializedName("pitchOffset")
   public float profileDraw = 0.0F;
   @SerializedName("pitchMin")
   public float vectorPerform = -90.0F;
   @SerializedName("pitchMax")
   public float eventAttach = 90.0F;
   @SerializedName("headLead")
   public float serverRead = 0.0F;
   @SerializedName("overlayLerp")
   public float positionAdvance = 0.35F;
   @SerializedName("overlayAimSpeed")
   public float frameCheck = 1.0F;
   @SerializedName("pointSwitchSpeed")
   public float moduleCollect = 1.0F;
   @SerializedName("lookAway")
   public boolean providerClose = false;
   @SerializedName("lookAwayAngle")
   public float presetSave = 80.0F;
   @SerializedName("lookAwayInterval")
   public float windowConvert = 5.0F;
   @SerializedName("points")
   public List<RotationPreset.State> presetWrite = new ArrayList<>();
   public static final String colorMeasure = "WILD-ROT:";

   private RotationPreset() {
   }

   public static synchronized RotationPreset handle() {
      if (rendererScan == null) {
         rendererScan = onTick();
      }

      rendererScan.process();
      return rendererScan;
   }

   public synchronized void process() {
      this.execute();
      if (this.mode == null || !handle(instance, this.mode)) {
         this.mode = "Multipoint";
      }

      if (this.vectorMatch == null || !handle(data, this.vectorMatch)) {
         this.vectorMatch = "Cycle";
      }

      if (this.responseCompute == null || !handle(context, this.responseCompute)) {
         this.responseCompute = "Smooth";
      }

      if (this.presetWrite == null) {
         this.presetWrite = new ArrayList<>();
      }

      while (this.presetWrite.size() > 12) {
         this.presetWrite.remove(this.presetWrite.size() - 1);
      }

      for (RotationPreset.State var2 : this.presetWrite) {
         var2.instance = handle(var2.instance, -0.5F, 0.5F);
         var2.data = handle(var2.data, 0.0F, 1.0F);
      }

      this.selection = handle(this.selection, 0.0F, 200.0F);
      this.enabled = handle(this.enabled, 0.0F, 200.0F);
      if (this.enabled < this.selection) {
         this.enabled = this.selection;
      }

      this.renderer = handle(this.renderer, 0.0F, 200.0F);
      this.handler = handle(this.handler, 0.0F, 200.0F);
      if (this.handler < this.renderer) {
         this.handler = this.renderer;
      }

      this.animationDraw = handle(this.animationDraw, 0.0F, 240.0F);
      this.pointEncode = handle(this.pointEncode, 0.0F, 240.0F);
      this.animator = handle(this.animator, 0.0F, 20.0F);
      this.source = handle(this.source, 0.0F, 20.0F);
      this.target = handle(this.target, 0.0F, 1.0F);
      this.pending = handle(this.pending, 0.0F, 1.0F);
      this.previous = handle(this.previous, 0.2F, 3.0F);
      this.latest = handle(this.latest, 0.0F, 0.6F);
      this.summary = handle(this.summary, 5.0F, 120.0F);
      this.itemProject = handle(this.itemProject, 0.1F, 3.0F);
      this.providerFetch = handle(this.providerFetch, -30.0F, 30.0F);
      this.profileDraw = handle(this.profileDraw, -30.0F, 30.0F);
      this.vectorPerform = handle(this.vectorPerform, -90.0F, 0.0F);
      this.eventAttach = handle(this.eventAttach, 0.0F, 90.0F);
      this.serverRead = handle(this.serverRead, 0.0F, 0.6F);
      this.positionAdvance = handle(this.positionAdvance, 0.05F, 1.0F);
      this.frameCheck = handle(this.frameCheck, 0.2F, 3.0F);
      this.moduleCollect = handle(this.moduleCollect, 0.1F, 3.0F);
      this.presetSave = handle(this.presetSave, 0.0F, 90.0F);
      this.windowConvert = handle(this.windowConvert, 1.5F, 15.0F);
   }

   public synchronized void handle(float var1, float var2) {
      if (this.presetWrite.size() < 12) {
         this.presetWrite.add(new RotationPreset.State(handle(var1, -0.5F, 0.5F), handle(var2, 0.0F, 1.0F)));
         apply();
      }
   }

   public synchronized void compute() {
      this.presetWrite.clear();
      apply();
   }

   public synchronized void handle(RotationPreset.State var1) {
      this.presetWrite.remove(var1);
      apply();
   }

   private static boolean handle(String[] var0, String var1) {
      for (String var5 : var0) {
         if (var5.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public synchronized void handle(String var1) {
      switch (var1) {
         case "FunTime":
            this.mode = "Multipoint";
            this.selection = 35.0F;
            this.enabled = 55.0F;
            this.renderer = 5.0F;
            this.handler = 10.0F;
            this.animationDraw = 65.0F;
            this.pointEncode = 22.0F;
            this.animator = 4.3F;
            this.source = 3.6F;
            this.target = 0.2F;
            this.pending = 0.13F;
            this.previous = 1.0F;
            this.latest = 0.0F;
            this.summary = 30.0F;
            break;
         case "Spooky":
            this.mode = "Multipoint";
            this.selection = 40.0F;
            this.enabled = 60.0F;
            this.renderer = 10.0F;
            this.handler = 21.0F;
            this.animationDraw = 60.0F;
            this.pointEncode = 25.0F;
            this.animator = 3.0F;
            this.source = 6.0F;
            this.target = 0.12F;
            this.pending = 0.05F;
            this.previous = 1.2F;
            this.latest = 0.0F;
            this.summary = 30.0F;
            break;
         case "Holy":
            this.mode = "Center";
            this.selection = 50.0F;
            this.enabled = 70.0F;
            this.renderer = 10.0F;
            this.handler = 20.0F;
            this.animationDraw = 70.0F;
            this.pointEncode = 24.0F;
            this.animator = 2.0F;
            this.source = 2.0F;
            this.target = 0.2F;
            this.pending = 0.3F;
            this.previous = 0.7F;
            this.latest = 0.0F;
            this.summary = 30.0F;
            break;
         case "Matrix":
            this.mode = "Eyes";
            this.selection = 38.0F;
            this.enabled = 43.0F;
            this.renderer = 3.0F;
            this.handler = 5.0F;
            this.animationDraw = 43.0F;
            this.pointEncode = 6.0F;
            this.animator = 3.0F;
            this.source = 4.0F;
            this.target = 0.4F;
            this.pending = 0.02F;
            this.previous = 1.4F;
            this.latest = 0.0F;
            this.summary = 30.0F;
            break;
         case "Smooth":
            this.mode = "Center";
            this.selection = 18.0F;
            this.enabled = 26.0F;
            this.renderer = 4.0F;
            this.handler = 8.0F;
            this.animationDraw = 30.0F;
            this.pointEncode = 12.0F;
            this.animator = 0.5F;
            this.source = 0.5F;
            this.target = 0.0F;
            this.pending = 0.0F;
            this.previous = 1.0F;
            this.latest = 0.0F;
            this.summary = 20.0F;
            break;
         case "Snap":
            this.mode = "Closest";
            this.selection = 120.0F;
            this.enabled = 180.0F;
            this.renderer = 80.0F;
            this.handler = 120.0F;
            this.animationDraw = 200.0F;
            this.pointEncode = 160.0F;
            this.animator = 1.0F;
            this.source = 1.0F;
            this.target = 0.0F;
            this.pending = 0.0F;
            this.previous = 1.0F;
            this.latest = 0.0F;
            this.summary = 40.0F;
         case "Custom":
      }

      this.output = var1;
      if ("Custom".equals(var1)) {
         this.current = "Custom";
         this.active = "Custom";
      } else if (handle(config, var1)) {
         this.current = var1;
         this.active = var1;
      } else {
         this.current = "Custom";
         this.active = "Custom";
      }

      this.process();
      apply();
   }

   private synchronized void execute() {
      String var1 = resolve(this.active);
      if (var1 == null) {
         var1 = resolve(this.output);
      }

      if (var1 == null) {
         var1 = resolve(this.current);
      }

      if (var1 != null) {
         this.active = var1;
         this.output = var1;
         this.current = var1;
      } else if (this.prepare()) {
         this.output = "Custom";
         this.current = "Custom";
         this.active = "Custom";
      } else {
         if (this.current == null || this.current.isBlank()) {
            this.current = "Custom";
         }

         if (!"Custom".equals(this.current) && !handle(config, this.current)) {
            this.current = "Custom";
         }

         if (this.output == null || this.output.isBlank()) {
            this.output = this.current;
         }

         if (this.active == null || this.active.isBlank()) {
            this.active = "Custom".equals(this.current) ? "Custom" : this.current;
         }
      }
   }

   private boolean prepare() {
      return "Custom".equalsIgnoreCase(compute(this.active))
         || "Custom".equalsIgnoreCase(compute(this.output))
         || "custom".equalsIgnoreCase(compute(this.output));
   }

   private static String compute(String var0) {
      return var0 == null ? "" : var0.trim();
   }

   private static String resolve(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim();
         if (!"Custom".equalsIgnoreCase(var1) && !"custom".equalsIgnoreCase(var1)) {
            for (String var5 : config) {
               if (var5.equalsIgnoreCase(var1)) {
                  return var5;
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public synchronized void resolve() {
      this.handle("FunTime");
      this.presetWrite.clear();
      this.vectorMatch = "Cycle";
      this.itemProject = 0.9F;
      this.moduleCollect = 1.0F;
      this.matrixBlend = false;
      this.check();
      this.process();
      apply();
   }

   private synchronized void check() {
      this.responseCompute = "Smooth";
      this.providerFetch = 0.0F;
      this.profileDraw = 0.0F;
      this.vectorPerform = -90.0F;
      this.eventAttach = 90.0F;
      this.serverRead = 0.0F;
      this.positionAdvance = 0.35F;
      this.frameCheck = 1.0F;
      this.providerClose = false;
      this.presetSave = 80.0F;
      this.windowConvert = 5.0F;
   }

   public synchronized String update() {
      this.process();
      String var1 = animationSchedule.toJson(this);
      String var2 = Base64.getUrlEncoder().withoutPadding().encodeToString(var1.getBytes(StandardCharsets.UTF_8));
      return "WILD-ROT:" + var2;
   }

   public static synchronized boolean process(String var0) {
      if (var0 == null) {
         return false;
      }

      String var1 = var0.trim();
      if (var1.startsWith("WILD-ROT:")) {
         var1 = var1.substring("WILD-ROT:".length());
      }

      var1 = var1.trim();
      if (var1.isEmpty()) {
         return false;
      }

      try {
         byte[] var2 = Base64.getUrlDecoder().decode(var1);
         String var3 = new String(var2, StandardCharsets.UTF_8);
         RotationPreset var4 = (RotationPreset)animationSchedule.fromJson(var3, RotationPreset.class);
         if (var4 == null) {
            return false;
         }

         RotationPreset var5 = handle();
         var5.handle(var4);
         var5.process();
         apply();
         return true;
      } catch (Throwable var6) {
         return false;
      }
   }

   private synchronized void handle(RotationPreset var1) {
      this.output = var1.output;
      this.current = var1.current;
      this.active = var1.active;
      this.mode = var1.mode;
      this.selection = var1.selection;
      this.enabled = var1.enabled;
      this.renderer = var1.renderer;
      this.handler = var1.handler;
      this.animationDraw = var1.animationDraw;
      this.pointEncode = var1.pointEncode;
      this.animator = var1.animator;
      this.source = var1.source;
      this.target = var1.target;
      this.pending = var1.pending;
      this.previous = var1.previous;
      this.latest = var1.latest;
      this.summary = var1.summary;
      this.matrixBlend = var1.matrixBlend;
      this.vectorMatch = var1.vectorMatch;
      this.itemProject = var1.itemProject;
      this.responseCompute = var1.responseCompute;
      this.providerFetch = var1.providerFetch;
      this.profileDraw = var1.profileDraw;
      this.vectorPerform = var1.vectorPerform;
      this.eventAttach = var1.eventAttach;
      this.serverRead = var1.serverRead;
      this.positionAdvance = var1.positionAdvance;
      this.frameCheck = var1.frameCheck;
      this.moduleCollect = var1.moduleCollect;
      this.providerClose = var1.providerClose;
      this.presetSave = var1.presetSave;
      this.windowConvert = var1.windowConvert;
      this.presetWrite = new ArrayList<>();
      if (var1.presetWrite != null) {
         for (RotationPreset.State var3 : var1.presetWrite) {
            if (var3 != null) {
               this.presetWrite.add(new RotationPreset.State(var3.instance, var3.data));
            }
         }
      }
   }

   public static synchronized void apply() {
      if (rendererScan != null) {
         File var0 = select();
         if (var0 != null) {
            try {
               File var1 = var0.getParentFile();
               if (var1 != null && !var1.exists()) {
                  var1.mkdirs();
               }

               try (FileWriter var2 = new FileWriter(var0)) {
                  animationSchedule.toJson(rendererScan, var2);
               }
            } catch (Throwable var7) {
            }
         }
      }
   }

   private static RotationPreset onTick() {
      File var0 = select();
      if (var0 != null && var0.exists()) {
         try (FileReader var7 = new FileReader(var0)) {
            RotationPreset var2 = (RotationPreset)animationSchedule.fromJson(var7, RotationPreset.class);
            if (var2 != null) {
               return var2;
            }
         } catch (Throwable var6) {
         }

         return new RotationPreset();
      } else {
         RotationPreset var1 = new RotationPreset();
         var1.process();
         return var1;
      }
   }

   private static File select() {
      return WildClient.instance != null && WildClient.instance.cache != null ? new File(WildClient.instance.cache, "custom-rotation.json") : null;
   }

   private static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   public static final class State {
      @SerializedName("x")
      public float instance;
      @SerializedName("y")
      public float data;

      public State() {
      }

      public State(float var1, float var2) {
         this.instance = var1;
         this.data = var2;
      }
   }
}
