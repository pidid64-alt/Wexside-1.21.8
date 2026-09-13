package ru.wild.modules.movement;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.player.MovementPhysics;

@ModuleRegister(name = "Jesus", category = ModuleCategory.Movement, description = "ходьба по воде")
public class Jesus extends Module {
   private final ModeSetting source = new ModeSetting("Режим", "Авто", "Авто", "Простой");
   private final NumberSetting target = new NumberSetting("Скорость", 0.2F, 0.2F, 1.05F, 0.01F, false).handle(() -> !this.source.process("Простой"));
   private final NumberSetting pending = new NumberSetting("Скорость Funtime", 1.175F, 1.0F, 1.2F, 0.005F, false).handle(() -> !this.source.process("Funtime"));
   private final KeybindSetting previous = new KeybindSetting("Кнопка буста", -1);
   private long latest = 0L;
   private boolean summary = false;
   private boolean matrixBlend = false;
   private final float vectorMatch = 0.47F;
   private final float itemProject = 0.43F;
   private int responseCompute = 0;

   public Jesus() {
      this.handle(this.source, this.target, this.pending, this.previous);
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (Module.client.currentScreen == null && var1.apply() == 1) {
         if (var1.resolve() == this.previous.compute()) {
            this.matrixBlend = true;
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (this.matrixBlend) {
            this.summary = true;
            this.latest = System.currentTimeMillis() + 2000L;
            this.matrixBlend = false;
         }

         if (this.summary && System.currentTimeMillis() > this.latest) {
            this.summary = false;
         }

         if (Module.client.player.isTouchingWater() || Module.client.player.isInLava()) {
            StatusEffectInstance var2 = Module.client.player.getStatusEffect(StatusEffects.SPEED);
            StatusEffectInstance var3 = Module.client.player.getStatusEffect(StatusEffects.SLOWNESS);
            ItemStack var4 = Module.client.player.getOffHandStack();
            String var5 = var4.getName().getString();
            ItemStack var6 = Module.client.player.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack var7 = Module.client.player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack var8 = Module.client.player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack var9 = Module.client.player.getEquippedStack(EquipmentSlot.FEET);
            String var10 = var6.getName().getString();
            String var11 = var7.getName().getString();
            String var12 = var8.getName().getString();
            String var13 = var9.getName().getString();
            if (this.source.process("Funtime")) {
               this.refresh();
               return;
            }

            float var14 = this.handle(var2, var3, var5);
            var14 = this.handle(var14, var6, var10, var7, var11, var8, var12, var9, var13);
            if (this.summary) {
               var14 *= 1.89F;
            }

            MovementPhysics.process(var14);
            boolean var15 = Module.client.options.forwardKey.isPressed()
               || Module.client.options.backKey.isPressed()
               || Module.client.options.leftKey.isPressed()
               || Module.client.options.rightKey.isPressed();
            if (!var15) {
               Module.client.player.setVelocity(0.0, Module.client.player.getVelocity().y, 0.0);
            }

            double var16 = Module.client.options.jumpKey.isPressed() ? 0.019 : 0.003;
            Module.client.player.setVelocity(Module.client.player.getVelocity().x, var16, Module.client.player.getVelocity().z);
         }
      }
   }

   private void refresh() {
      if (Module.client.player != null && Module.client.player.networkHandler != null) {
         double var1 = Math.ceil(Module.client.player.getY()) - 0.001;
         Module.client.player
            .networkHandler
            .sendPacket(new PositionAndOnGround(Module.client.player.getX(), var1, Module.client.player.getZ(), true, Module.client.player.horizontalCollision));
      }
   }

   private float handle(StatusEffectInstance var1, StatusEffectInstance var2, String var3) {
      float var4 = 0.0F;
      if (this.source.process("Авто")) {
         if (var1 != null) {
            if (var1.getAmplifier() == 2) {
               var4 = this.handle(var3) ? 0.58515F : 0.53535F;
            } else if (var1.getAmplifier() == 1) {
               var4 = this.handle(var3) ? 0.47F : 0.43F;
            }
         } else {
            var4 = this.handle(var3) ? 0.3243F : 0.2967F;
         }
      } else if (this.source.process("Простой")) {
         var4 = this.target.compute();
      }

      if (var2 != null) {
         var4 *= 0.85F;
      }

      return var4;
   }

   private boolean handle(String var1) {
      return var1.contains("Шар Геракла 2")
         || var1.contains("Шар CHAMPION")
         || var1.contains("Шар Аида 2")
         || var1.contains("Шар GOD")
         || var1.contains("КУБИК-РУБИК");
   }

   private float handle(float var1, ItemStack var2, String var3, ItemStack var4, String var5, ItemStack var6, String var7, ItemStack var8, String var9) {
      if (var8.getItem() == Items.GOLDEN_BOOTS && var9.contains("Тапочки админа SoveryBRIZ")) {
         var1 *= 1.01F;
      }

      if (var6.getItem() == Items.GOLDEN_LEGGINGS && var7.contains("Штаны админа stqffy")) {
         var1 *= 1.02F;
      }

      if (var2.getItem() == Items.GOLDEN_HELMET && var3.contains("Шляпа админа Vester")) {
         var1 *= 1.05F;
      }

      if (var4.getItem() == Items.GOLDEN_CHESTPLATE && var5.contains("Грудак админа lxckscream")) {
         var1 *= 1.03F;
      }

      if (var2.getItem() == Items.PLAYER_HEAD && var3.contains("Новогодний Подарок")) {
         var1 *= 0.75F;
      }

      return var1;
   }
}
