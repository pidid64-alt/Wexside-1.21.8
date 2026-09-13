package ru.wild.render.font;

import java.util.Objects;

public final class FontObject {
   public final String instance;

   public FontObject(String var1) {
      this.instance = Objects.requireNonNull(var1, "id");
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         FontObject var2 = (FontObject)var1;
         return this.instance.equals(var2.instance);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.instance.hashCode();
   }

   @Override
   public String toString() {
      return "FontObject(" + this.instance + ")";
   }
}
