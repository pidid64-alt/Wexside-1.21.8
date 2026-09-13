package ru.wild.api.event;
public class PlayerMoveEvent extends Event {
   private double instance;
   private double data;
   private double context;
   private double config;
   private double state;
   private boolean cache;
   public double compute() {
      return this.instance;
   }
   public double resolve() {
      return this.data;
   }
   public double update() {
      return this.context;
   }
   public double apply() {
      return this.config;
   }
   public double execute() {
      return this.state;
   }
   public boolean prepare() {
      return this.cache;
   }
   public PlayerMoveEvent handle(double var1) {
      this.instance = var1;
      return this;
   }
   public PlayerMoveEvent process(double var1) {
      this.data = var1;
      return this;
   }
   public PlayerMoveEvent compute(double var1) {
      this.context = var1;
      return this;
   }
   public PlayerMoveEvent resolve(double var1) {
      this.config = var1;
      return this;
   }
   public PlayerMoveEvent update(double var1) {
      this.state = var1;
      return this;
   }
   public PlayerMoveEvent handle(boolean var1) {
      this.cache = var1;
      return this;
   }
   public PlayerMoveEvent(double var1, double var3, double var5, double var7, double var9, boolean var11) {
      this.instance = var1;
      this.data = var3;
      this.context = var5;
      this.config = var7;
      this.state = var9;
      this.cache = var11;
   }
}
