package ru.wild.gui.screen;

import java.util.HashMap;
import java.util.Map;

public final class ServerStatusCache {
   final String instance;
   String data;
   final Map<String, ServerStatusRecord> context = new HashMap<>();

   ServerStatusCache(String var1, String var2) {
      this.instance = var1;
      this.data = var2;
   }
}
