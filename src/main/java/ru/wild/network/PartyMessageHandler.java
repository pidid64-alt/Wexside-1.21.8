package ru.wild.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import ru.wild.util.text.ChatLogger;

public final class PartyMessageHandler {
   private static final PartyMessageHandler instance = new PartyMessageHandler();
   private final PartySocketClient data = new PartySocketClient();
   private final PartyMemberProfile context = new PartyMemberProfile();
   private final PartyRoster config = new PartyRoster();
   private final PartyHeartbeat state = new PartyHeartbeat();
   private UUID cache;

   private PartyMessageHandler() {
   }

   public static PartyMessageHandler handle() {
      return instance;
   }

   public PartyMemberProfile process() {
      return this.context;
   }

   public PartyRoster compute() {
      return this.config;
   }

   public PartyHeartbeat resolve() {
      return this.state;
   }

   public PartyConnectionState update() {
      return this.data.compute();
   }

   public boolean apply() {
      return this.data.resolve();
   }

   public String execute() {
      return this.data.update();
   }

   public UUID prepare() {
      return this.cache;
   }

   public void check() {
      this.handle(PartyHostConfig.handle());
   }

   public void handle(PartyHostConfig var1) {
      this.drawAnimation();
      this.data.handle(var1, this.encodePoint());
   }

   public void onTick() {
      this.data.handle();
      this.drawAnimation();
   }

   public boolean handle(String var1) {
      return this.data.handle(PartyProtocol.handle(var1));
   }

   public boolean process(String var1) {
      return this.data.handle(PartyProtocol.process(var1));
   }

   public boolean select() {
      return this.data.handle(PartyProtocol.handle());
   }

   public boolean handle(UUID var1) {
      return this.data.handle(PartyProtocol.handle(var1));
   }

   public boolean handle(double var1, double var3, double var5, float var7, float var8, float var9) {
      return this.data.handle(PartyProtocol.handle(var1, var3, var5, var7, var8, var9));
   }

   public boolean handle(UUID var1, double var2, double var4, double var6, float var8, float var9, float var10) {
      return this.data.handle(PartyProtocol.handle(var1, var2, var4, var6, var8, var9, var10));
   }

   public boolean process(UUID var1) {
      return this.data.handle(PartyProtocol.process(var1));
   }

   public boolean handle(UUID var1, String var2, boolean var3, long var4, float var6) {
      return this.data.handle(PartyProtocol.handle(var1, var2, var3, var4, var6));
   }

   public void refresh() {
      this.tick();

      for (int var1 = 0; var1 < 64; var1++) {
         String var2 = this.data.process();
         if (var2 == null) {
            return;
         }

         this.compute(var2);
      }
   }

   private void tick() {
      if (this.data.resolve()) {
         long var1 = System.currentTimeMillis();
         if (this.state.handle(var1)) {
            if (this.data.handle(PartyProtocol.handle(var1))) {
               this.state.process(var1);
            }
         }
      }
   }

   private void compute(String var1) {
      JsonObject var2 = update(var1);
      if (var2 != null) {
         switch (process(var2, "type")) {
            case "welcome":
               this.handle(var2);
               break;
            case "party_state":
               this.process(var2);
               break;
            case "party_closed":
               this.compute(var2);
               break;
            case "screens_state":
               this.resolve(var2);
               break;
            case "time_echo":
               this.update(var2);
               break;
            case "error":
               this.apply(var2);
         }
      }
   }

   private void handle(JsonObject var1) {
      this.cache = apply(var1, "uuid");
      ChatLogger.handle("Подключено к серверу Wild");
   }

   private void process(JsonObject var1) {
      ArrayList var2 = new ArrayList();

      for (JsonElement var4 : handle(var1, "members")) {
         if (var4.isJsonObject()) {
            JsonObject var5 = var4.getAsJsonObject();
            UUID var6 = apply(var5, "uuid");
            if (var6 != null) {
               var2.add(new PartyMemberProfile.NamedEntry(var6, process(var5, "username")));
            }
         }
      }

      this.context.handle(apply(var1, "party_id"), apply(var1, "leader"), process(var1, "code"), var2);
   }

   private void compute(JsonObject var1) {
      this.context.apply();
      this.config.compute();
      ChatLogger.handle(process(var1, "reason").equals("kicked") ? "Вас исключили из комнаты" : "Вы вышли из комнаты");
   }

   private void resolve(JsonObject var1) {
      ArrayList var2 = new ArrayList();

      for (JsonElement var4 : handle(var1, "screens")) {
         if (var4.isJsonObject()) {
            JsonObject var5 = var4.getAsJsonObject();
            UUID var6 = apply(var5, "id");
            if (var6 != null) {
               var2.add(
                  new PartyRoster.Point3d(
                     var6,
                     apply(var5, "owner"),
                     process(var5, "source"),
                     compute(var5, "x"),
                     compute(var5, "y"),
                     compute(var5, "z"),
                     (float)compute(var5, "yaw"),
                     (float)compute(var5, "width"),
                     (float)compute(var5, "height"),
                     update(var5, "playing"),
                     resolve(var5, "position_ms"),
                     resolve(var5, "stamp_ms"),
                     (float)compute(var5, "volume")
                  )
               );
            }
         }
      }

      this.config.handle(var2);
   }

   private void update(JsonObject var1) {
      this.state.handle(resolve(var1, "c"), resolve(var1, "s"), System.currentTimeMillis());
   }

   private void apply(JsonObject var1) {
      ChatLogger.handle(resolve(process(var1, "code")));
   }

   private static String resolve(String var0) {
      return switch (var0) {
         case "code_taken" -> "Такой код уже занят";
         case "unknown_code" -> "Комната с таким кодом не найдена";
         case "already_in_party" -> "Вы уже в комнате";
         case "not_in_party" -> "Вы не состоите в комнате";
         case "not_leader" -> "Это может сделать только владелец комнаты";
         case "unknown_member" -> "Такого участника нет в комнате";
         case "rate_limited" -> "Слишком много попыток, подождите";
         case "invalid_code" -> "Неверный формат кода";
         case "screen_limit" -> "В комнате уже максимум экранов";
         case "unknown_screen" -> "Такого экрана в комнате нет";
         case "not_screen_owner" -> "Экраном управляет его владелец или владелец комнаты";
         case "invalid_screen" -> "Экран нельзя разместить здесь";
         case "cannot_transfer_to_self" -> "Вы и так управляете комнатой";
         default -> "Ошибка сервера";
      };
   }

   public String render() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      Session var2 = var1 == null ? null : var1.getSession();
      return var2 == null ? "" : var2.getUsername();
   }

   private void drawAnimation() {
      this.cache = null;
      this.context.apply();
      this.config.compute();
      this.state.resolve();
   }

   private PartyIdentity encodePoint() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      Session var2 = var1 == null ? null : var1.getSession();
      UUID var3 = var2 == null ? null : var2.getUuidOrNull();
      String var4 = this.render();
      if (var3 == null) {
         var3 = UUID.nameUUIDFromBytes(("WildOffline:" + var4).getBytes());
      }

      return PartyIdentity.of(var3, var4, null);
   }

   private static JsonObject update(String var0) {
      try {
         JsonElement var1 = JsonParser.parseString(var0);
         return var1.isJsonObject() ? var1.getAsJsonObject() : null;
      } catch (RuntimeException var2) {
         return null;
      }
   }

   private static JsonArray handle(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      return var2 != null && var2.isJsonArray() ? var2.getAsJsonArray() : new JsonArray();
   }

   private static String process(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      return var2 != null && var2.isJsonPrimitive() ? var2.getAsString() : "";
   }

   private static double compute(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);

      try {
         return var2 != null && var2.isJsonPrimitive() ? var2.getAsDouble() : 0.0;
      } catch (NumberFormatException var4) {
         return 0.0;
      }
   }

   private static long resolve(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);

      try {
         return var2 != null && var2.isJsonPrimitive() ? var2.getAsLong() : 0L;
      } catch (NumberFormatException var4) {
         return 0L;
      }
   }

   private static boolean update(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      return var2 != null && var2.isJsonPrimitive() && var2.getAsBoolean();
   }

   private static UUID apply(JsonObject var0, String var1) {
      String var2 = process(var0, var1);
      if (var2.isEmpty()) {
         return null;
      }

      try {
         return UUID.fromString(var2);
      } catch (IllegalArgumentException var4) {
         return null;
      }
   }
}
