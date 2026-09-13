package ru.wild.network;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
public final class TelegramApi {
   private static String instance = "";
   private static String data = "";

   public static void handle(String var0, String var1) {
      instance = var0;
      data = var1;
   }

   public static boolean handle() {
      return instance != null && !instance.isEmpty() && data != null && !data.isEmpty();
   }

   public static void handle(String var0) {
      if (!handle()) {
         System.out.println("[TelegramApi] Not configured");
      } else {
         try {
            String var1 = "https://api.telegram.org/bot" + instance + "/sendMessage";
            String var2 = "chat_id=" + data + "&text=" + URLEncoder.encode(var0, StandardCharsets.UTF_8);
            URL var3 = new URL(var1);
            HttpURLConnection var4 = (HttpURLConnection)var3.openConnection();
            var4.setRequestMethod("POST");
            var4.setDoOutput(true);
            var4.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            var4.setConnectTimeout(6000);
            var4.setReadTimeout(8000);

            try (OutputStream var5 = var4.getOutputStream()) {
               var5.write(var2.getBytes(StandardCharsets.UTF_8));
            }

            var4.getInputStream().close();
         } catch (Exception var10) {
            var10.printStackTrace();
         }
      }
   }
   private TelegramApi() {
   }
}
