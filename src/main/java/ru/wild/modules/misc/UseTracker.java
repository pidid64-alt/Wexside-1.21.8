package ru.wild.modules.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "UseTracker", category = ModuleCategory.Misc, description = "Отслеживание тотемов/эффектов/расходников у игроков")
public class UseTracker extends Module {
   private static final String source = "Снос тотема";
   private static final String target = "Полученные зелья";
   private static final String pending = "Съеденные предметы";
   private static final int previous = 31;
   private static final long latest = 500L;
   private final ChoiceSetting summary = new ChoiceSetting(
      "Отслеживать", new BooleanSetting("Снос тотема", true), new BooleanSetting("Полученные зелья", true), new BooleanSetting("Съеденные предметы", true)
   );
   private final Map<UUID, Map<String, StatusEffectInstance>> matrixBlend = new HashMap<>();
   private static final Map<UUID, Boolean> vectorMatch = new HashMap<>();
   private final Map<UUID, ItemStack> itemProject = new HashMap<>();
   private final Map<UUID, Integer> responseCompute = new HashMap<>();
   private final Stopwatch providerFetch = new Stopwatch();

   public UseTracker() {
      this.handle(this.summary);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (this.encodePoint()) {
            this.refresh();
         } else {
            this.itemProject.clear();
            this.responseCompute.clear();
         }

         if (!this.drawAnimation()) {
            this.matrixBlend.clear();
         } else if (this.providerFetch.update(500L)) {
            this.providerFetch.handle();
            this.render();
         }
      } else {
         this.animate();
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.update().equals(PacketEvent.Mode.RECEIVE) && Module.client.world != null && Module.client.player != null) {
         if (var1.resolve() instanceof EntityStatusS2CPacket var2) {
            this.handle(var2);
         }

         if (var1.resolve() instanceof EntityStatusEffectS2CPacket var4) {
            this.handle(var4);
         }
      }
   }

   private void refresh() {
      HashSet var1 = new HashSet();

      for (PlayerEntity var3 : Module.client.world.getPlayers()) {
         if (var3 != null && var3.isAlive() && !this.process(var3)) {
            UUID var4 = var3.getUuid();
            var1.add(var4);
            if (var3.isUsingItem()) {
               this.itemProject.computeIfAbsent(var4, var1x -> var3.getActiveItem().copy());
               this.responseCompute.putIfAbsent(var4, var3.age);
            } else {
               ItemStack var5 = this.itemProject.remove(var4);
               Integer var6 = this.responseCompute.remove(var4);
               if (var5 != null && !var5.isEmpty() && var6 != null && var3.age - var6 >= 31) {
                  UseAction var7 = var5.getUseAction();

                  String var8 = switch (var7) {
                     case DRINK -> "выпил";
                     case EAT -> "съел";
                     default -> null;
                  };
                  if (var8 != null) {
                     String var9 = this.handle(var5.getName().getString());
                     String var10 = this.handle(var5);
                     String var11 = var10.isEmpty() ? "" : " §8(§7" + var10 + "§8)";
                     this.process("§f" + var3.getName().getString() + "§7 " + var8 + " §f" + var9 + var11);
                  }
               }
            }
         }
      }

      this.itemProject.keySet().removeIf(var1x -> !var1.contains(var1x));
      this.responseCompute.keySet().removeIf(var1x -> !var1.contains(var1x));
   }

   private void handle(EntityStatusS2CPacket var1) {
      if (this.tick() && var1.getStatus() == 35) {
         if (var1.getEntity(Module.client.world) instanceof PlayerEntity var3 && !this.process(var3)) {
            boolean var4 = EnchantmentHelper.hasEnchantments(var3.getOffHandStack())
               || EnchantmentHelper.hasEnchantments(var3.getMainHandStack())
               || var3.getOffHandStack().hasGlint()
               || var3.getMainHandStack().hasGlint();
            vectorMatch.put(var3.getUuid(), var4);
            this.process("§f" + var3.getName().getString() + "§7 потерял тотем бессмертия, зачарован: " + (var4 ? "§a" : "§c") + "⬤");
         }
      }
   }

   private void handle(EntityStatusEffectS2CPacket var1) {
      if (this.drawAnimation()) {
         if (Module.client.world.getEntityById(var1.getEntityId()) instanceof PlayerEntity var3) {
            UUID var4 = var3.getUuid();
            HashMap var5 = new HashMap<>(this.matrixBlend.getOrDefault(var4, Map.of()));
            var5.putAll(this.handle(var3));
            StatusEffectInstance var6 = new StatusEffectInstance(
               var1.getEffectId(), var1.getDuration(), var1.getAmplifier(), var1.isAmbient(), var1.shouldShowParticles(), var1.shouldShowIcon()
            );
            String var7 = this.handle(var1.getEffectId(), var1.getAmplifier());
            var5.put(var7, var6);
            HashSet var8 = new HashSet();
            var8.add(var7);
            this.handle(var3, var5, var8);
            this.matrixBlend.put(var4, var5);
         }
      }
   }

   private void render() {
      HashSet var1 = new HashSet();

      for (PlayerEntity var3 : Module.client.world.getPlayers()) {
         if (var3 != null && var3.isAlive()) {
            UUID var4 = var3.getUuid();
            var1.add(var4);
            Map var5 = this.matrixBlend.getOrDefault(var4, Map.of());
            Map<String, StatusEffectInstance> var6 = this.handle(var3);
            HashSet var7 = new HashSet();

            for (Entry<String, StatusEffectInstance> var9 : var6.entrySet()) {
               StatusEffectInstance var10 = (StatusEffectInstance)var5.get(var9.getKey());
               if (var10 == null || this.handle(var10, (StatusEffectInstance)var9.getValue())) {
                  var7.add((String)var9.getKey());
               }
            }

            if (!var7.isEmpty()) {
               this.handle(var3, var6, var7);
            }

            this.matrixBlend.put(var4, var6);
         }
      }

      this.matrixBlend.keySet().removeIf(var1x -> !var1.contains(var1x));
   }

   private boolean handle(StatusEffectInstance var1, StatusEffectInstance var2) {
      return var1.getAmplifier() != var2.getAmplifier() ? true : var2.getDuration() > var1.getDuration() + 20;
   }

   private void handle(PlayerEntity var1, Map<String, StatusEffectInstance> var2, Set<String> var3) {
      ArrayList var4 = new ArrayList();
      this.handle(var2, var3, var4, UseTracker.Mode.KILLER, "effect.minecraft.strength:3", "effect.minecraft.resistance:0");
      this.handle(var2, var3, var4, UseTracker.Mode.URINE, "effect.minecraft.jump_boost:0", "effect.minecraft.speed:2");
      this.handle(var2, var3, var4, UseTracker.Mode.MEDIC, "effect.minecraft.health_boost:2", "effect.minecraft.regeneration:2");
      this.handle(
         var2,
         var3,
         var4,
         UseTracker.Mode.BURP,
         "effect.minecraft.blindness:0",
         "effect.minecraft.glowing:0",
         "effect.minecraft.hunger:9",
         "effect.minecraft.slowness:2",
         "effect.minecraft.wither:4"
      );
      this.handle(var2, var3, var4, UseTracker.Mode.FLASH, "effect.minecraft.blindness:0", "effect.minecraft.glowing:0");
      this.handle(
         var2,
         var3,
         var4,
         UseTracker.Mode.SULFURIC_ACID,
         "effect.minecraft.poison:1",
         "effect.minecraft.slowness:3",
         "effect.minecraft.weakness:2",
         "effect.minecraft.wither:4"
      );
      this.handle(
         var2,
         var3,
         var4,
         UseTracker.Mode.WINNER,
         "effect.minecraft.health_boost:1",
         "effect.minecraft.invisibility:0",
         "effect.minecraft.regeneration:1",
         "effect.minecraft.resistance:0"
      );
      if (var4.isEmpty()) {
         for (String var9 : var3) {
            StatusEffectInstance var7 = (StatusEffectInstance)var2.get(var9);
            if (var7 != null) {
               this.handle(var1, var7);
            }
         }
      } else {
         for (UseTracker.Mode var6 : (List<UseTracker.Mode>) var4) {
            this.handle(var1, var6);
         }
      }
   }

   private void handle(PlayerEntity var1, StatusEffectInstance var2) {
      String var3 = this.handle(Text.translatable(((StatusEffect)var2.getEffectType().value()).getTranslationKey()).getString());
      int var4 = Math.max(0, var2.getAmplifier()) + 1;
      String var5 = this.handle(var2);
      this.process("§f" + var1.getName().getString() + "§7 получил §f" + var3 + " " + var4 + "§7 на §f" + var5);
   }

   private boolean handle(Map<String, StatusEffectInstance> var1, Set<String> var2, List<UseTracker.Mode> var3, UseTracker.Mode var4, String... var5) {
      HashSet var6 = new HashSet<>(Arrays.asList(var5));
      boolean var7 = var6.stream().allMatch(var1::containsKey);
      boolean var8 = var6.stream().anyMatch(var2::contains);
      if (var7 && var8) {
         var3.add(var4);
         var2.removeAll(var6);
         return true;
      } else {
         return false;
      }
   }

   private Map<String, StatusEffectInstance> handle(PlayerEntity var1) {
      HashMap var2 = new HashMap();

      for (StatusEffectInstance var4 : var1.getStatusEffects()) {
         var2.put(this.handle(var4.getEffectType(), var4.getAmplifier()), var4);
      }

      return var2;
   }

   private String handle(RegistryEntry<StatusEffect> var1, int var2) {
      return ((StatusEffect)var1.value()).getTranslationKey() + ":" + var2;
   }

   private void handle(PlayerEntity var1, UseTracker.Mode var2) {
      this.process("§f" + var1.getName().getString() + "§7 получил §f" + this.handle(var2.instance));
   }

   private String handle(ItemStack var1) {
      PotionContentsComponent var2 = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
      if (var2 == null) {
         return "";
      }

      StringBuilder var3 = new StringBuilder();

      for (StatusEffectInstance var5 : var2.getEffects()) {
         if (!var3.isEmpty()) {
            var3.append("§8, §7");
         }

         String var6 = this.handle(Text.translatable(((StatusEffect)var5.getEffectType().value()).getTranslationKey()).getString());
         int var7 = Math.max(0, var5.getAmplifier()) + 1;
         var3.append(var6).append(" ").append(var7).append("§7 на §f").append(this.handle(var5));
      }

      return var3.toString();
   }

   private String handle(StatusEffectInstance var1) {
      if (var1.isInfinite()) {
         return "∞";
      }

      int var2 = var1.getDuration() / 20;
      int var3 = var2 / 60;
      var2 %= 60;
      return var3 > 0 ? var3 + " мин " + var2 + " сек" : var2 + " сек";
   }

   private boolean tick() {
      return this.summary.process("Снос тотема");
   }

   private boolean drawAnimation() {
      return this.summary.process("Полученные зелья");
   }

   private boolean encodePoint() {
      return this.summary.process("Съеденные предметы");
   }

   private boolean process(PlayerEntity var1) {
      return Module.client.player != null && var1.getUuid().equals(Module.client.player.getUuid());
   }

   private String handle(String var1) {
      return var1 == null ? "" : var1.replaceAll("§[0-9a-fk-orA-FK-OR]", "");
   }

   private void process(String var1) {
      ChatLogger.handle(var1);
   }

   private void animate() {
      this.matrixBlend.clear();
      vectorMatch.clear();
      this.itemProject.clear();
      this.responseCompute.clear();
   }

   @Override
   public void process() {
      this.animate();
      super.process();
   }

   record DataRecord(RegistryEntry<StatusEffect> effect, int durationSeconds, int amplifier) {
      int durationTicks() {
         return this.durationSeconds * 20;
      }
   }

   enum Mode {
      FLASH("§6[★] §eВспышка", List.of(new UseTracker.DataRecord(StatusEffects.BLINDNESS, 20, 0), new UseTracker.DataRecord(StatusEffects.GLOWING, 240, 0))),
      KILLER(
         "§4[★] §cЗелье Киллера",
         List.of(new UseTracker.DataRecord(StatusEffects.RESISTANCE, 180, 0), new UseTracker.DataRecord(StatusEffects.STRENGTH, 90, 3))
      ),
      BURP(
         "§c[★] §6Зелье Отрыжки",
         List.of(
            new UseTracker.DataRecord(StatusEffects.BLINDNESS, 10, 0),
            new UseTracker.DataRecord(StatusEffects.GLOWING, 180, 0),
            new UseTracker.DataRecord(StatusEffects.HUNGER, 90, 9),
            new UseTracker.DataRecord(StatusEffects.SLOWNESS, 180, 2),
            new UseTracker.DataRecord(StatusEffects.WITHER, 30, 4)
         )
      ),
      SULFURIC_ACID(
         "§2[★] §aСерная кислота",
         List.of(
            new UseTracker.DataRecord(StatusEffects.POISON, 50, 1),
            new UseTracker.DataRecord(StatusEffects.SLOWNESS, 90, 3),
            new UseTracker.DataRecord(StatusEffects.WEAKNESS, 90, 2),
            new UseTracker.DataRecord(StatusEffects.WITHER, 30, 4)
         )
      ),
      MEDIC(
         "§5[★] §dЗелье Медика",
         List.of(new UseTracker.DataRecord(StatusEffects.HEALTH_BOOST, 45, 2), new UseTracker.DataRecord(StatusEffects.REGENERATION, 45, 2))
      ),
      WINNER(
         "§2[★] §aЗелье Победителя",
         List.of(
            new UseTracker.DataRecord(StatusEffects.HEALTH_BOOST, 180, 1),
            new UseTracker.DataRecord(StatusEffects.INVISIBILITY, 900, 0),
            new UseTracker.DataRecord(StatusEffects.REGENERATION, 60, 1),
            new UseTracker.DataRecord(StatusEffects.RESISTANCE, 60, 0)
         )
      ),
      URINE("§3[★] §bМоча Флеша", List.of(new UseTracker.DataRecord(StatusEffects.JUMP_BOOST, 120, 1), new UseTracker.DataRecord(StatusEffects.SPEED, 120, 2)));

      final String instance;
      private final List<UseTracker.DataRecord> data;

      Mode(String var3, List<UseTracker.DataRecord> var4) {
         this.instance = var3;
         this.data = var4;
      }
   }
}
