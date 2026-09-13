package ru.wild.network;

import java.util.List;
import java.util.UUID;

public final class RemoteScreenViewport {
   private final RemoteCameraState instance = new RemoteCameraState();
   private UUID data;
   private UUID context;
   private RemoteViewportInteractionMode config = RemoteViewportInteractionMode.NONE;
   private UUID state;
   private RemoteViewportInteractionMode cache = RemoteViewportInteractionMode.NONE;
   private double output;
   private double current;
   private double active;
   private double mode;
   private double selection;
   private double enabled;
   private double renderer;
   private float handler;
   private float animationDraw;
   private float pointEncode;
   private long animator;

   public UUID handle() {
      return this.data;
   }

   public UUID process() {
      return this.context;
   }

   public RemoteViewportInteractionMode compute() {
      return this.config;
   }

   public boolean resolve() {
      return this.state != null;
   }

   public UUID update() {
      return this.state;
   }

   public boolean handle(UUID var1) {
      return this.state != null && this.state.equals(var1);
   }

   public double apply() {
      return this.selection;
   }

   public double execute() {
      return this.enabled;
   }

   public double prepare() {
      return this.renderer;
   }

   public float check() {
      return this.handler;
   }

   public float onTick() {
      return this.animationDraw;
   }

   public float select() {
      return this.pointEncode;
   }

   public void handle(List<PartyRoster.Point3d> var1, UUID var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      if (this.state == null) {
         this.data = null;
         this.context = null;
         this.config = RemoteViewportInteractionMode.NONE;
         double var16 = Double.MAX_VALUE;
         double var18 = Double.MAX_VALUE;

         for (int var20 = 0; var20 < var1.size(); var20++) {
            PartyRoster.Point3d var21 = (PartyRoster.Point3d)var1.get(var20);
            if (handle(var21, var2, var3)) {
               this.instance.handle(var21);
               if (this.instance.handle(var4, var6, var8, var10, var12, var14)) {
                  RemoteViewportInteractionMode var22 = handle(this.instance);
                  if (var22 != RemoteViewportInteractionMode.NONE && this.instance.prepare() < var16) {
                     var16 = this.instance.prepare();
                     this.data = var21.id();
                     this.config = var22;
                  }

                  if ((var22 != RemoteViewportInteractionMode.NONE || this.instance.blendMatrix()) && this.instance.prepare() < var18) {
                     var18 = this.instance.prepare();
                     this.context = var21.id();
                  }
               }
            }
         }
      }
   }

   public boolean handle(PartyRoster.Point3d var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      if (var1 != null && this.config != RemoteViewportInteractionMode.NONE) {
         this.instance.handle(var1);
         if (!this.instance.handle(var2, var4, var6, var8, var10, var12)) {
            return false;
         }

         this.state = var1.id();
         this.cache = this.config;
         this.output = this.instance.prepare();
         this.current = var1.x() - (var2 + var8 * this.output);
         this.active = var1.y() - (var4 + var10 * this.output);
         this.mode = var1.z() - (var6 + var12 * this.output);
         this.selection = var1.x();
         this.enabled = var1.y();
         this.renderer = var1.z();
         this.handler = var1.yaw();
         this.animationDraw = var1.width();
         this.pointEncode = var1.height();
         this.animator = 0L;
         return true;
      } else {
         return false;
      }
   }

   public double refresh() {
      return this.output;
   }

   public void handle(double var1) {
      if (this.cache == RemoteViewportInteractionMode.MOVE && var1 < this.output) {
         this.output = var1;
      }
   }

   public void handle(double var1, double var3, double var5, double var7, double var9, double var11) {
      if (this.state != null) {
         if (this.cache == RemoteViewportInteractionMode.MOVE) {
            this.selection = var1 + var7 * this.output + this.current;
            this.enabled = var3 + var9 * this.output + this.active;
            this.renderer = var5 + var11 * this.output + this.mode;
         } else {
            this.instance.handle(this.selection, this.enabled, this.renderer, this.handler, this.animationDraw, this.pointEncode);
            if (this.instance.handle(var1, var3, var5, var7, var9, var11)) {
               double var13 = Math.abs(this.instance.apply());
               double var15 = Math.abs(this.instance.execute()) * 1.7777777777777777;
               double var17 = Math.clamp(Math.max(var13, var15), 0.45, 24.0);
               this.animationDraw = (float)(var17 * 2.0);
               this.pointEncode = this.animationDraw * 9.0F / 16.0F;
            }
         }
      }
   }

   public boolean handle(long var1) {
      if (this.state != null && var1 - this.animator >= 100L) {
         this.animator = var1;
         return true;
      } else {
         return false;
      }
   }

   public UUID render() {
      UUID var1 = this.state;
      this.state = null;
      this.cache = RemoteViewportInteractionMode.NONE;
      return var1;
   }

   public void tick() {
      this.data = null;
      this.context = null;
      this.config = RemoteViewportInteractionMode.NONE;
      this.state = null;
      this.cache = RemoteViewportInteractionMode.NONE;
   }

   public static boolean handle(PartyRoster.Point3d var0, UUID var1, boolean var2) {
      return var1 != null && (var2 || var1.equals(var0.owner()));
   }

   private static RemoteViewportInteractionMode handle(RemoteCameraState var0) {
      if (var0.projectItem()) {
         return RemoteViewportInteractionMode.RESIZE;
      } else {
         return var0.matchVector() ? RemoteViewportInteractionMode.MOVE : RemoteViewportInteractionMode.NONE;
      }
   }
}
