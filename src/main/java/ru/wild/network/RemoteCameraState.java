package ru.wild.network;

public final class RemoteCameraState {
   private double instance;
   private double data;
   private double context;
   private double config;
   private double state;
   private double cache;
   private double output;
   private double current;
   private double active;
   private double mode;
   private double selection;
   private double enabled;

   public void handle(PartyRoster.Point3d var1) {
      this.handle(var1.x(), var1.y(), var1.z(), var1.yaw(), var1.width(), var1.height());
   }

   public void handle(double var1, double var3, double var5, float var7, float var8, float var9) {
      this.instance = var1;
      this.data = var3;
      this.context = var5;
      double var10 = Math.toRadians(var7);
      this.config = Math.cos(var10);
      this.state = Math.sin(var10);
      this.cache = -Math.sin(var10);
      this.output = Math.cos(var10);
      this.current = var8 * 0.5;
      this.active = var9 * 0.5;
   }

   public double handle() {
      return this.instance;
   }

   public double process() {
      return this.data;
   }

   public double compute() {
      return this.context;
   }

   public double resolve() {
      return this.current;
   }

   public double update() {
      return this.active;
   }

   public double handle(double var1, double var3) {
      return this.instance + this.config * var1 + this.cache * var3;
   }

   public double handle(double var1) {
      return this.data + var1;
   }

   public double process(double var1, double var3) {
      return this.context + this.state * var1 + this.output * var3;
   }

   public boolean handle(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = var7 * this.cache + var11 * this.output;
      if (Math.abs(var13) < 1.0E-6) {
         return false;
      }

      double var15 = ((this.instance - var1) * this.cache + (this.context - var5) * this.output) / var13;
      if (var15 <= 0.0) {
         return false;
      }

      double var17 = var1 + var7 * var15 - this.instance;
      double var19 = var3 + var9 * var15 - this.data;
      double var21 = var5 + var11 * var15 - this.context;
      this.mode = var17 * this.config + var21 * this.state;
      this.selection = var19;
      this.enabled = var15;
      return true;
   }

   public double apply() {
      return this.mode;
   }

   public double execute() {
      return this.selection;
   }

   public double prepare() {
      return this.enabled;
   }

   public float check() {
      return (float)((this.mode + this.current) / (this.current * 2.0));
   }

   public float onTick() {
      return (float)((this.active - this.selection) / (this.active * 2.0));
   }

   public double select() {
      return Math.min(0.45, Math.min(this.current, this.active) * 0.14);
   }

   public static double compute(double var0, double var2) {
      return Math.min(0.4, Math.min(var0, var2) * 0.22);
   }

   public double refresh() {
      return Math.min(0.55, this.active * 0.24);
   }

   public double render() {
      return this.refresh() * 16.0 / 9.0;
   }

   public double tick() {
      return this.render() + this.refresh() * 0.18;
   }

   public double drawAnimation() {
      return this.active + this.select() * 0.5;
   }

   public double encodePoint() {
      return this.drawAnimation() + this.refresh();
   }

   public double handle(int var1) {
      return -this.current + var1 * this.tick();
   }

   public boolean animate() {
      return this.selection >= this.drawAnimation() && this.selection <= this.encodePoint() && Math.abs(this.mode) <= this.current;
   }

   public int load() {
      double var1 = this.mode + this.current;
      if (var1 < 0.0) {
         return -1;
      }

      int var3 = (int)(var1 / this.tick());
      return var1 - var3 * this.tick() <= this.render() ? var3 : -1;
   }

   public double save() {
      return this.current * 0.5;
   }

   public double submit() {
      return -this.active - 0.06;
   }

   public double unload() {
      return -this.active - 0.14;
   }

   public double fetch() {
      return this.unload() - 0.12;
   }

   public double measure() {
      return Math.min(0.6, Math.min(this.current, this.active));
   }

   public boolean blendMatrix() {
      return Math.abs(this.mode) <= this.current && Math.abs(this.selection) <= this.active;
   }

   public boolean matchVector() {
      return Math.abs(this.mode) <= this.save() && this.selection <= this.submit() && this.selection >= this.fetch();
   }

   public boolean projectItem() {
      double var1 = this.measure();
      return this.mode >= this.current - var1 && this.mode <= this.current && this.selection >= -this.active && this.selection <= -this.active + var1;
   }
}
