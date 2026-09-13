package ru.wild.gui.screen;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ViaInstallScreen {
   private static final String instance = "WildClient/1.21.8 (main-menu protocol selector)";
   private static final String data = "https://api.modrinth.com/v2/project/viafabricplus/version?loaders=%5B%22fabric%22%5D&game_versions=%5B%22";
   private static final Duration context = Duration.ofSeconds(25L);
   private static final long config = 67108864L;
   private static volatile ViaInstallScreen.Mode state = ViaInstallScreen.Mode.IDLE;
   private static volatile String cache = "GPL-3.0 · загружается с Modrinth";
   private static volatile String output;

   private ViaInstallScreen() {
   }

   public static ViaInstallScreen.Mode handle() {
      return state;
   }

   public static String process() {
      return switch (state) {
         case IDLE -> "Установить ViaFabricPlus";
         case WORKING -> "Загрузка…";
         case DONE -> "Готово · перезапустите клиент";
         case FAILED -> "Не удалось · открыть страницу";
      };
   }

   public static String compute() {
      return cache;
   }

   public static String resolve() {
      return state != ViaInstallScreen.Mode.WORKING && state != ViaInstallScreen.Mode.FAILED ? null : cache;
   }

   public static void update() {
      switch (state) {
         case IDLE:
            prepare();
         case WORKING:
         case DONE:
         default:
            break;
         case FAILED:
            apply();
      }
   }

   public static void apply() {
      try {
         Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/viafabricplus"));
      } catch (Throwable var1) {
      }
   }

   private static synchronized void prepare() {
      if (state != ViaInstallScreen.Mode.WORKING) {
         state = ViaInstallScreen.Mode.WORKING;
         cache = "запрос к Modrinth";
         Thread var0 = new Thread(ViaInstallScreen::check, "wild-viafabricplus-install");
         var0.setDaemon(true);
         var0.start();
      }
   }

   private static void check() {
      try {
         Path var0 = FabricLoader.getInstance().getGameDir().resolve("mods");
         Files.createDirectories(var0);
         HttpClient var1 = HttpClient.newBuilder().connectTimeout(context).followRedirects(Redirect.NORMAL).build();
         String var2 = SharedConstants.getGameVersion().name();
         HttpRequest var3 = HttpRequest.newBuilder(
               URI.create("https://api.modrinth.com/v2/project/viafabricplus/version?loaders=%5B%22fabric%22%5D&game_versions=%5B%22" + var2 + "%22%5D")
            )
            .header("User-Agent", "WildClient/1.21.8 (main-menu protocol selector)")
            .timeout(context)
            .GET()
            .build();
         HttpResponse var4 = var1.send(var3, BodyHandlers.ofString());
         if (var4.statusCode() != 200) {
            handle("Modrinth ответил " + var4.statusCode());
            return;
         }

         JSONArray var5 = new JSONArray((String)var4.body());
         if (var5.isEmpty()) {
            handle("нет сборки под " + var2);
            return;
         }

         JSONObject var6 = handle(var5.getJSONObject(0));
         if (var6 == null) {
            handle("в релизе нет основного файла");
            return;
         }

         long var7 = var6.optLong("size", 0L);
         if (var7 > 67108864L) {
            handle("файл слишком большой");
            return;
         }

         String var9 = var6.getJSONObject("hashes").optString("sha512", "");
         String var10 = var6.optString("filename", "viafabricplus.jar");
         cache = "загрузка " + Math.max(1L, var7 / 1024L / 1024L) + " МБ";
         HttpRequest var11 = HttpRequest.newBuilder(URI.create(var6.getString("url")))
            .header("User-Agent", "WildClient/1.21.8 (main-menu protocol selector)")
            .timeout(context)
            .GET()
            .build();
         HttpResponse var12 = var1.send(var11, BodyHandlers.ofByteArray());
         if (var12.statusCode() != 200) {
            handle("загрузка вернула " + var12.statusCode());
            return;
         }

         byte[] var13 = (byte[])var12.body();
         if (var13.length == 0 || var13.length > 67108864L) {
            handle("пустой или слишком большой ответ");
            return;
         }

         if (!var9.isEmpty() && !var9.equalsIgnoreCase(handle(var13))) {
            handle("хэш не совпал");
            return;
         }

         Path var14 = Files.createTempFile("wild-vfp", ".part");
         Files.write(var14, var13);
         Files.move(var14, var0.resolve(var10), StandardCopyOption.REPLACE_EXISTING);
         output = var10;
         cache = var10;
         state = ViaInstallScreen.Mode.DONE;
      } catch (Throwable var15) {
         handle(String.valueOf(var15.getClass().getSimpleName()));
      }
   }

   private static JSONObject handle(JSONObject var0) {
      JSONArray var1 = var0.optJSONArray("files");
      if (var1 != null && !var1.isEmpty()) {
         for (int var2 = 0; var2 < var1.length(); var2++) {
            JSONObject var3 = var1.getJSONObject(var2);
            if (var3.optBoolean("primary", false)) {
               return var3;
            }
         }

         return var1.getJSONObject(0);
      } else {
         return null;
      }
   }

   private static String handle(byte[] var0) throws Exception {
      MessageDigest var1 = MessageDigest.getInstance("SHA-512");
      return HexFormat.of().formatHex(var1.digest(var0));
   }

   private static void handle(String var0) {
      cache = var0;
      state = ViaInstallScreen.Mode.FAILED;
   }

   public static String execute() {
      return output;
   }

   public enum Mode {
      IDLE,
      WORKING,
      DONE,
      FAILED;
   }
}
