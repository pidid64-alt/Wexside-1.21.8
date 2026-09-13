package ru.wild.modules.player;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.Box;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.util.player.LocalhostHelper;

@ModuleRegister(name = "ChestStealer", description = "Лутает предметы с сундуков", category = ModuleCategory.Player)
public class ChestStealer extends Module {
   public static BooleanSetting source = new BooleanSetting("Убирать игроков", false);
   public final ModeSetting target = new ModeSetting("Режим работы", "Обычный", "Обычный", "FunTime Event");
   private static final int pending = 9;
   private static final double previous = 0.5;
   private static final double latest = 1.0;
   private final Random summary = new Random();

   public ChestStealer() {
      this.handle(source, this.target);
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (!LocalhostHelper.handle()) {
         if (source.compute()) {
            this.handle(0.5);

            for (Entity var3 : Module.client.world.getEntities()) {
               if (var3 instanceof PlayerEntity var4 && var4 != Module.client.player) {
                  double var5 = var3.getX();
                  double var7 = var3.getY();
                  double var9 = var3.getZ();
                  var3.setBoundingBox(new Box(var5 - 1.0E-5, var7, var9 - 1.0E-5, var5 + 1.0E-5, var7 + var3.getHeight(), var9 + 1.0E-5));
               }
            }
         } else {
            this.handle(1.0);
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.currentScreen instanceof GenericContainerScreen var2) {
         GenericContainerScreenHandler var6 = (GenericContainerScreenHandler)var2.getScreenHandler();
         if (var6 instanceof GenericContainerScreenHandler var5) {
            this.handle(var2.getTitle().getString(), var5, var5.getRows() * 9);
         }
      } else if (Module.client.currentScreen instanceof Generic3x3ContainerScreen var3) {
         Generic3x3ContainerScreenHandler var8 = (Generic3x3ContainerScreenHandler)var3.getScreenHandler();
         if (var8 instanceof Generic3x3ContainerScreenHandler var9) {
            this.handle(var3.getTitle().getString(), var9, 9);
         }
      }
   }

   private void handle(String var1, ScreenHandler var2, int var3) {
      ArrayList var4 = new ArrayList();
      switch (this.target.compute()) {
         case "Обычный":
            for (int var12 = 0; var12 < var3; var12++) {
               if (!var2.getSlot(var12).getStack().isEmpty()) {
                  var4.add(var12);
               }
            }
            break;
         case "FunTime Event":
            for (int var11 = 0; var11 < var3; var11++) {
               ItemStack var14 = var2.getSlot(var11).getStack();
               if (!var14.isEmpty()) {
                  Item var15 = var14.getItem();
                  if (var15 == Items.NAUTILUS_SHELL || var15 == Items.GUNPOWDER || var15 == Items.WHITE_DYE || var15 == Items.LIGHT_GRAY_DYE) {
                     var4.add(var11);
                  }
               }
            }
            break;
         case "FunTime AIRDrop":
            if (this.handle(var1)) {
               boolean var7 = false;

               for (int var8 = 0; var8 < var3; var8++) {
                  ItemStack var9 = var2.getSlot(var8).getStack();
                  if (var9.getItem() == Items.BLAZE_POWDER && var9.getName().getString().contains("[★] Предмет еще не остыл")) {
                     var7 = true;
                     break;
                  }
               }

               if (!var7) {
                  for (int var13 = 0; var13 < var3; var13++) {
                     if (!var2.getSlot(var13).getStack().isEmpty()) {
                        var4.add(var13);
                     }
                  }
               }
            }
      }

      if (!var4.isEmpty()) {
         int var10 = (Integer)var4.get(this.summary.nextInt(var4.size()));
         Module.client.interactionManager.clickSlot(var2.syncId, var10, 0, SlotActionType.QUICK_MOVE, Module.client.player);
      }
   }

   private boolean handle(String var1) {
      String var2 = var1.toLowerCase().replaceAll("§.", "").trim();
      return var2.equals("бочка")
         || var2.equals("раздатчик")
         || var2.equals("dispenser")
         || var2.equals("barrel")
         || var2.equals("аир-дроп")
         || var2.equals("аир дроп")
         || var2.equals("air-drop")
         || var2.equals("air drop")
         || var2.equals("airdrop");
   }

   private void handle(double var1) {
      double var3 = (Double)Module.client.options.getEntityDistanceScaling().getValue();
      if (Math.abs(var3 - var1) > 0.001) {
         Module.client.options.getEntityDistanceScaling().setValue(var1);
      }
   }

   @Override
   public void process() {
      super.process();
      this.handle(1.0);
   }
}
