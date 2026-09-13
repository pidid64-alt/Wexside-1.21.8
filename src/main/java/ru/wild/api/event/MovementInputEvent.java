package ru.wild.api.event;

public class MovementInputEvent extends Event {
   private float instance;
   private float data;
   private boolean context;
   private boolean config;
   private boolean state;
   private double cache;

   public MovementInputEvent(float var1, float var2, boolean var3, boolean var4, double var5) {
      this(var1, var2, var3, var4, false, var5);
   }

   public MovementInputEvent(float var1, float var2, boolean var3, boolean var4, boolean var5, double var6) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5;
      this.cache = var6;
   }

   public float compute() {
      return this.instance;
   }

   public void handle(float var1) {
      this.instance = var1;
   }

   public float resolve() {
      return this.data;
   }

   public void process(float var1) {
      this.data = var1;
   }

   public boolean update() {
      return this.context;
   }

   public void handle(boolean var1) {
      this.context = var1;
   }

   public boolean apply() {
      return this.config;
   }

   public void process(boolean var1) {
      this.config = var1;
   }

   public boolean execute() {
      return this.state;
   }

   public void compute(boolean var1) {
      this.state = var1;
   }

   public double prepare() {
      return this.cache;
   }

   public void handle(double var1) {
      this.cache = var1;
   }
}
