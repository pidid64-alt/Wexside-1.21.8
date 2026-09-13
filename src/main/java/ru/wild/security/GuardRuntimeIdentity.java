package ru.wild.security;

import java.util.UUID;

public final class GuardRuntimeIdentity {
   public String instance;
   public String data;
   public long context;
   public boolean config;
   public long state;
   public int cache;
   public int output;
   public String current;

   public static GuardRuntimeIdentity handle() {
      GuardRuntimeIdentity var0 = new GuardRuntimeIdentity();
      var0.instance = UUID.randomUUID().toString();
      var0.data = "wild-1.21.8-1787661348375";
      var0.context = System.currentTimeMillis() / 1000L;
      var0.config = false;
      var0.state = 0L;
      var0.cache = 0;
      var0.output = 0;
      var0.current = "";
      return var0;
   }
}
