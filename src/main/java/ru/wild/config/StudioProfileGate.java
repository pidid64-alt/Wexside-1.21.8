package ru.wild.config;

import ru.wild.profile.Profile;

public final class StudioProfileGate {
   private static final String[] instance = new String[]{"lichoday"};

   private StudioProfileGate() {
   }

   public static boolean handle() {
      return Profile.isUsername(instance);
   }
}
