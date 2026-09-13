package ru.wild.util.math;

public record RectBounds(float x, float y, float w, float h) {
   public boolean contains(float var1, float var2) {
      return var1 >= this.x && var2 >= this.y && var1 < this.x + this.w && var2 < this.y + this.h;
   }
}
