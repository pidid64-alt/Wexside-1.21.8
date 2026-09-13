package ru.wild.api.event;

import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.Box;

public class PlayerMotionEvent extends Event {
   private float data;
   private float context;
   private double config;
   private double state;
   private double cache;
   private boolean output;
   private Box current;
   Runnable instance;

   public PlayerMotionEvent(float var1, float var2, double var3, double var5, double var7, boolean var9, Box var10, Runnable var11) {
      this.data = var1;
      this.context = var2;
      this.config = var3;
      this.state = var5;
      this.cache = var7;
      this.output = var9;
      this.current = var10;
      this.instance = var11;
   }

   public void handle(Vector2f var1) {
      this.handle(var1.getX());
      this.process(var1.getY());
   }

   public Box compute() {
      return this.current;
   }

   public void handle(Box var1) {
      this.current = var1;
   }

   public Runnable resolve() {
      return this.instance;
   }

   public float update() {
      return this.data;
   }

   public float apply() {
      return this.context;
   }

   public double execute() {
      return this.config;
   }

   public double prepare() {
      return this.state;
   }

   public double check() {
      return this.cache;
   }

   public boolean onTick() {
      return this.output;
   }

   public void handle(Runnable var1) {
      this.instance = var1;
   }

   public void handle(float var1) {
      this.data = var1;
   }

   public void process(float var1) {
      this.context = var1;
   }

   public void handle(double var1) {
      this.config = var1;
   }

   public void process(double var1) {
      this.state = var1;
   }

   public void compute(double var1) {
      this.cache = var1;
   }

   public void handle(boolean var1) {
      this.output = var1;
   }
}
