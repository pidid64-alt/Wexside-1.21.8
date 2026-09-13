package ru.wild.core;

import com.mojang.authlib.yggdrasil.ProfileResult;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;
import org.wild.mixin.acceser.MinecraftClientSessionAccessor;

public final class ClientSessionSwitcher {
   private static ClientSessionSwitcher.DataRecord instance;

   private ClientSessionSwitcher() {
   }

   public static void handle(MinecraftClient var0) {
      if (var0 != null && instance == null) {
         MinecraftClientSessionAccessor var1 = (MinecraftClientSessionAccessor)var0;
         Session var2 = var1.litka$getSession();
         if (var2 != null) {
            ProfileKeys var3 = var1.litka$getProfileKeys();
            CompletableFuture var4 = var1.litka$getGameProfileFuture();
            instance = new ClientSessionSwitcher.DataRecord(
               var2, var3 == null ? ProfileKeys.MISSING : var3, var4 == null ? CompletableFuture.completedFuture(null) : var4
            );
         }
      }
   }

   public static Optional<Session> process(MinecraftClient var0) {
      handle(var0);
      return Optional.ofNullable(instance).map(ClientSessionSwitcher.DataRecord::session);
   }

   public static boolean handle(MinecraftClient var0, String var1) {
      handle(var0);
      if (var0 != null && instance != null) {
         if (var1 != null && !instance.session().getUsername().equals(var1)) {
            return false;
         }

         handle(var0, instance);
         return true;
      } else {
         return false;
      }
   }

   public static Session process(MinecraftClient var0, String var1) {
      if (var0 == null) {
         return null;
      }

      handle(var0);
      String var2 = var1 == null ? "" : var1;
      Session var3 = new Session(
         var2, UUID.nameUUIDFromBytes(("OfflinePlayer:" + var2).getBytes(StandardCharsets.UTF_8)), "", Optional.empty(), Optional.empty(), AccountType.LEGACY
      );
      handle(var0, new ClientSessionSwitcher.DataRecord(var3, ProfileKeys.MISSING, CompletableFuture.completedFuture(null)));
      return var3;
   }

   public static void handle() {
      ClientSessionSwitcher.DataRecord var0 = instance;
      instance = null;
      if (var0 != null && var0.gameProfileFuture() != null && !var0.gameProfileFuture().isDone()) {
         var0.gameProfileFuture().cancel(true);
      }
   }

   private static void handle(MinecraftClient var0, ClientSessionSwitcher.DataRecord var1) {
      MinecraftClientSessionAccessor var2 = (MinecraftClientSessionAccessor)var0;
      var2.litka$setSession(var1.session());
      var2.litka$setProfileKeys(var1.profileKeys());
      var2.litka$setGameProfileFuture(var1.gameProfileFuture());
   }

   record DataRecord(Session session, ProfileKeys profileKeys, CompletableFuture<ProfileResult> gameProfileFuture) {
   }
}
