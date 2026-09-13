package ru.wild.util.world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunctions;

public final class Waypoint {
   private final String instance;
   private final double data;
   private final double context;
   private final double config;
   private final RegistryKey<World> state;
   private final double cache;
   private final EasedDoubleAnimator output = new EasedDoubleAnimator();
   private final EasedDoubleAnimator current = new EasedDoubleAnimator();
   private boolean active = true;

   public Waypoint(String var1, double var2, double var4, double var6, RegistryKey<World> var8, double var9) {
      this.instance = var1;
      this.data = var2;
      this.context = var4;
      this.config = var6;
      this.state = var8;
      this.cache = var9;
      this.output.handle(1.0, 0.42, EasingFunctions.serverRead);
      this.current.handle(1.0, 0.7, EasingFunctions.selection);
   }

   public String handle() {
      return this.instance;
   }

   public double process() {
      return this.data;
   }

   public double compute() {
      return this.context;
   }

   public double resolve() {
      return this.config;
   }

   public boolean update() {
      return this.active;
   }

   public void apply() {
      if (this.active) {
         this.active = false;
         this.output.handle(0.0, 0.22, EasingFunctions.current);
         this.current.handle(0.0, 0.16, EasingFunctions.current);
      }
   }

   public boolean execute() {
      return !this.active && this.output.update() <= 0.01F;
   }

   public float prepare() {
      this.current.handle();
      this.output.handle();
      return this.output.update();
   }

   public float check() {
      return this.current.update();
   }

   public boolean handle(RegistryKey<World> var1) {
      return this.state == null || this.state.equals(var1);
   }

   public double handle(double var1) {
      return var1 <= 0.0 ? 1.0 : this.cache / var1;
   }

   public String onTick() {
      if (this.state == null) {
         return "";
      } else if (World.NETHER.equals(this.state)) {
         return "Ад";
      } else if (World.END.equals(this.state)) {
         return "Край";
      } else {
         return World.OVERWORLD.equals(this.state) ? "Обычный мир" : this.state.getValue().getPath();
      }
   }
}
