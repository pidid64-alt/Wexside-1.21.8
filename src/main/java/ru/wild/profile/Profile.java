package ru.wild.profile;

public class Profile {
   public static String username = "SoftArax";
   public static int uid = 1488;
   public static Role role = Role.ADMIN;
   public static String hwid;
   public static String subscriptionEndDate = "05-02-2026";
   public static String avatarUrl = "";

   private Profile() {
   }

   public static String getUsername() {
      return username;
   }

   public static int getUid() {
      return uid;
   }

   public static Role getRole() {
      return role == null ? Role.DEFAULT : role;
   }

   public static String getHwid() {
      return hwid;
   }

   public static String getSubscriptionEndDate() {
      return subscriptionEndDate;
   }

   public static String getAvatarUrl() {
      return avatarUrl;
   }

   public static boolean hasRole(Role... var0) {
      if (var0 != null && var0.length != 0) {
         Role var1 = getRole();

         for (Role var5 : var0) {
            if (var1 == var5) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean hasRoleAtLeast(Role var0) {
      return getRole().isAtLeast(var0);
   }

   public static boolean isUsername(String... var0) {
      String var1 = getUsername();
      if (var0 != null && var0.length != 0 && var1 != null) {
         var1 = var1.trim();

         for (String var5 : var0) {
            if (var5 != null && var1.equalsIgnoreCase(var5.trim())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean isUid(int... var0) {
      if (var0 != null && var0.length != 0) {
         for (int var4 : var0) {
            if (getUid() == var4) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
