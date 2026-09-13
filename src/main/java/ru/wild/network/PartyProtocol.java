package ru.wild.network;

import com.google.gson.JsonObject;
import java.util.UUID;

public final class PartyProtocol {
   public static final int instance = 1;
   static final String data = "welcome";
   static final String context = "party_state";
   static final String config = "party_closed";
   static final String state = "screens_state";
   static final String cache = "time_echo";
   static final String output = "error";

   private PartyProtocol() {
   }

   static String handle(PartyIdentity var0) {
      JsonObject var1 = compute("hello");
      var1.addProperty("v", 1);
      var1.addProperty("username", var0.username());
      var1.addProperty("uuid", var0.uuid().toString());
      if (var0.avatarUrl() != null) {
         var1.addProperty("avatar", var0.avatarUrl());
      }

      return var1.toString();
   }

   static String handle(String var0) {
      return handle("party_create", var0);
   }

   static String process(String var0) {
      return handle("party_join", var0);
   }

   static String handle() {
      return compute("party_leave").toString();
   }

   static String handle(UUID var0) {
      JsonObject var1 = compute("party_transfer");
      var1.addProperty("target", var0.toString());
      return var1.toString();
   }

   static String handle(double var0, double var2, double var4, float var6, float var7, float var8) {
      return handle(compute("screen_create"), var0, var2, var4, var6, var7, var8);
   }

   static String handle(UUID var0, double var1, double var3, double var5, float var7, float var8, float var9) {
      JsonObject var10 = compute("screen_move");
      var10.addProperty("id", var0.toString());
      return handle(var10, var1, var3, var5, var7, var8, var9);
   }

   static String process(UUID var0) {
      JsonObject var1 = compute("screen_remove");
      var1.addProperty("id", var0.toString());
      return var1.toString();
   }

   static String handle(UUID var0, String var1, boolean var2, long var3, float var5) {
      JsonObject var6 = compute("screen_playback");
      var6.addProperty("id", var0.toString());
      var6.addProperty("source", var1);
      var6.addProperty("playing", var2);
      var6.addProperty("position_ms", var3);
      var6.addProperty("volume", var5);
      return var6.toString();
   }

   static String handle(long var0) {
      JsonObject var2 = compute("time_sync");
      var2.addProperty("c", var0);
      return var2.toString();
   }

   private static String handle(JsonObject var0, double var1, double var3, double var5, float var7, float var8, float var9) {
      var0.addProperty("x", var1);
      var0.addProperty("y", var3);
      var0.addProperty("z", var5);
      var0.addProperty("yaw", var7);
      var0.addProperty("width", var8);
      var0.addProperty("height", var9);
      return var0.toString();
   }

   private static String handle(String var0, String var1) {
      JsonObject var2 = compute(var0);
      var2.addProperty("code", var1);
      return var2.toString();
   }

   private static JsonObject compute(String var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("type", var0);
      return var1;
   }
}
