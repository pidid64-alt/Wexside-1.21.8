package ru.wild.profile;

public enum Role {
   DEFAULT(0),
   USER(1),
   MEDIA(2),
   SUPPORT(3),
   MODERATOR(4),
   ADMIN(5),
   OWNER(6);

   private final int level;

   Role(int var3) {
      this.level = var3;
   }

   public int getLevel() {
      return this.level;
   }

   public boolean isAtLeast(Role var1) {
      return var1 == null || this.level >= var1.level;
   }
}
