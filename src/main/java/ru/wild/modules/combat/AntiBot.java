package ru.wild.modules.combat;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.ModeSetting;

@ModuleRegister(
   name = "AntiBot",
   category = ModuleCategory.Combat,
   description = "Убирает бота позади вас",
   flags = {ModuleFlag.MATRIX, ModuleFlag.GRIM}
)
public class AntiBot extends Module {
   public final ModeSetting source = new ModeSetting("Режим: ", "ALL", "ReallyWorld", "Matrix", "ALL");
   public static final Set<UUID> target = ConcurrentHashMap.newKeySet();
   private static final Set<UUID> previous = ConcurrentHashMap.newKeySet();
   private final Set<UUID> latest = ConcurrentHashMap.newKeySet();
   private final Map<UUID, List<ItemStack>> summary = new ConcurrentHashMap<>();
   public static boolean pending = false;

   public AntiBot() {
      this.handle(this.source);
   }

   @Override
   public void handle() {
      super.handle();
      pending = true;
   }

   @Override
   public void process() {
      pending = false;
      target.clear();
      previous.clear();
      this.latest.clear();
      this.summary.clear();
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.world != null && Module.client.player != null) {
         this.tick();
         String var2 = this.source.compute();
         if (!var2.equals("ReallyWorld") && !this.latest.isEmpty()) {
            Module.client.world.getPlayers().stream().filter(var1x -> this.latest.contains(var1x.getUuid())).forEach(this::compute);
         }

         if (var2.equals("Matrix") || var2.equals("ALL")) {
            this.refresh();
         }

         if (var2.equals("ReallyWorld") || var2.equals("ALL")) {
            this.render();
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof PlayerListS2CPacket var2) {
         this.handle(var2);
      } else if (var1.resolve() instanceof PlayerRemoveS2CPacket var3) {
         this.handle(var3);
      }
   }

   private void handle(PlayerListS2CPacket var1) {
      if (var1.getActions().contains(Action.ADD_PLAYER)) {
         var1.getEntries().forEach(var1x -> {
            GameProfile var2 = var1x.profile();
            if (var2 != null) {
               if (this.handle(var1x, var2)) {
                  this.handle(var2.getId());
               } else {
                  UUID var3 = var2.getId();
                  if (this.handle(var2)) {
                     target.add(var3);
                  } else {
                     this.latest.add(var3);
                  }
               }
            }
         });
      }
   }

   private void handle(PlayerRemoveS2CPacket var1) {
      for (UUID var3 : var1.profileIds()) {
         this.latest.remove(var3);
         target.remove(var3);
         previous.remove(var3);
         this.summary.remove(var3);
      }
   }

   private boolean handle(Entry var1, GameProfile var2) {
      return Module.client.player == null ? false : var2.getId().equals(Module.client.player.getUuid()) || var1.latency() > 0;
   }

   private boolean handle(GameProfile var1) {
      return Module.client.world == null
         ? false
         : Module.client.world
            .getPlayers()
            .stream()
            .anyMatch(var1x -> var1x.getGameProfile().getName().equals(var1.getName()) && !var1x.getUuid().equals(var1.getId()));
   }

   private List<ItemStack> process(PlayerEntity var1) {
      ArrayList var2 = new ArrayList(4);
      var2.add(var1.getEquippedStack(EquipmentSlot.HEAD));
      var2.add(var1.getEquippedStack(EquipmentSlot.CHEST));
      var2.add(var1.getEquippedStack(EquipmentSlot.LEGS));
      var2.add(var1.getEquippedStack(EquipmentSlot.FEET));
      return var2;
   }

   private void compute(PlayerEntity var1) {
      if (resolve(var1)) {
         this.latest.remove(var1.getUuid());
         this.summary.remove(var1.getUuid());
      } else {
         List<ItemStack> var2 = this.process(var1);
         List<ItemStack> var3 = this.summary.get(var1.getUuid());
         if (!this.handle(var2) && !this.handle(var2, var3)) {
            this.summary.put(var1.getUuid(), var2);
         } else {
            target.add(var1.getUuid());
            this.latest.remove(var1.getUuid());
         }
      }
   }

   private void refresh() {
      if (Module.client.world != null && Module.client.player != null) {
         Iterator var1 = this.latest.iterator();

         while (var1.hasNext()) {
            UUID var2 = (UUID)var1.next();
            PlayerEntity var3 = Module.client.world.getPlayerByUuid(var2);
            if (var3 != null) {
               if (resolve(var3)) {
                  continue;
               }

               String var4 = var3.getName().getString();
               boolean var5 = var4.startsWith("CIT-") && !var4.contains("NPC") && !var4.contains("[ZNPC]");
               int var6 = 0;

               for (ItemStack var8 : this.process(var3)) {
                  if (var8 != null && !var8.isEmpty()) {
                     var6++;
                  }
               }

               boolean var9 = var6 == 4;
               boolean var10 = !var3.getUuid().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + var4).getBytes()));
               if (var9 || var5 || var10) {
                  target.add(var2);
               }
            }

            var1.remove();
         }

         if (Module.client.player.age % 100 == 0) {
            target.removeIf(var0 -> Module.client.world.getPlayerByUuid(var0) == null);
         }
      }
   }

   private void render() {
      if (Module.client.world != null && Module.client.player != null) {
         for (PlayerEntity var2 : Module.client.world.getPlayers()) {
            if (var2 != Module.client.player && !resolve(var2)) {
               String var3 = var2.getName().getString();
               boolean var4 = !var2.getUuid().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + var3).getBytes()));
               boolean var5 = var3.contains("NPC") || var3.startsWith("[ZNPC]");
               if (var4 && !var5) {
                  target.add(var2.getUuid());
               }
            }
         }
      }
   }

   private boolean handle(List<ItemStack> var1) {
      for (ItemStack var3 : var1) {
         if (var3 == null || var3.isEmpty() || var3.hasEnchantments()) {
            return false;
         }
      }

      return true;
   }

   private boolean handle(List<ItemStack> var1, List<ItemStack> var2) {
      if (var2 == null) {
         return false;
      }

      for (int var3 = 0; var3 < 4; var3++) {
         ItemStack var4 = (ItemStack)var1.get(var3);
         ItemStack var5 = (ItemStack)var2.get(var3);
         if (var4 != null && var5 != null) {
            if (var4.getItem() != var5.getItem()) {
               return true;
            }
         } else if (var4 != var5) {
            return true;
         }
      }

      return false;
   }

   private void tick() {
      if (Module.client.getNetworkHandler() != null) {
         for (PlayerListEntry var2 : Module.client.getNetworkHandler().getPlayerList()) {
            if (var2 != null && var2.getProfile() != null && this.handle(var2.getProfile().getId(), var2.getLatency())) {
               this.handle(var2.getProfile().getId());
            }
         }

         previous.removeIf(
            var0 -> Module.client.getNetworkHandler().getPlayerListEntry(var0) == null
               && (Module.client.world == null || Module.client.world.getPlayerByUuid(var0) == null)
         );
      }
   }

   private boolean handle(UUID var1, int var2) {
      return Module.client.player != null && var1.equals(Module.client.player.getUuid()) || var2 > 0;
   }

   private void handle(UUID var1) {
      process(var1);
      this.latest.remove(var1);
      this.summary.remove(var1);
   }

   private static void process(UUID var0) {
      if (var0 != null) {
         previous.add(var0);
         target.remove(var0);
      }
   }

   private static boolean resolve(PlayerEntity var0) {
      if (var0 == null) {
         return false;
      } else {
         UUID var1 = var0.getUuid();
         if (previous.contains(var1)) {
            return true;
         } else if (Module.client.player != null && var1.equals(Module.client.player.getUuid())) {
            process(var1);
            return true;
         } else if (Module.client.getNetworkHandler() == null) {
            return false;
         } else {
            PlayerListEntry var2 = Module.client.getNetworkHandler().getPlayerListEntry(var1);
            if (var2 != null && var2.getLatency() > 0) {
               process(var1);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public static boolean handle(PlayerEntity var0) {
      if (!pending) {
         return false;
      } else if (resolve(var0)) {
         return false;
      } else if (target.contains(var0.getUuid())) {
         return true;
      } else {
         String var1 = var0.getName().getString();
         if (var1.startsWith("CIT-") && !var1.contains("NPC") && !var1.startsWith("[ZNPC]")) {
            return true;
         } else {
            return var0.isInvisible() && !var1.contains("NPC") && !var1.startsWith("[ZNPC]")
               ? !var0.getUuid().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + var1).getBytes()))
               : false;
         }
      }
   }
}
