package ru.wild.config;

import ru.wild.profile.Profile;

public final class HudProfileGate {
   private static final String[] instance = new String[]{"lichoday"};

   private HudProfileGate() {
   }

   public static boolean handle() {
      return Profile.isUsername(instance);
   }
}
