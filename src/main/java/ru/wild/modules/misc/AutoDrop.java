package ru.wild.modules.misc;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.util.math.Stopwatch;

@ModuleRegister(name = "AutoDrop", category = ModuleCategory.Misc, description = "Автоматически выбрасывает мусор")
public class AutoDrop extends Module {
   public static boolean source = false;
   private final BooleanSetting target = new BooleanSetting("Камень", false);
   private final BooleanSetting pending = new BooleanSetting("Булыжник", false);
   private final BooleanSetting previous = new BooleanSetting("Гранит", false);
   private final BooleanSetting latest = new BooleanSetting("Палки", false);
   private final BooleanSetting summary = new BooleanSetting("Сланец", false);
   private final BooleanSetting matrixBlend = new BooleanSetting("Андезит", false);
   private final BooleanSetting vectorMatch = new BooleanSetting("Незерак", false);
   private final BooleanSetting itemProject = new BooleanSetting("Базальт", false);
   private final BooleanSetting responseCompute = new BooleanSetting("Чернит", false);
   private final BooleanSetting providerFetch = new BooleanSetting("Блоки душ", false);
   private final BooleanSetting profileDraw = new BooleanSetting("Руды ада", false);
   private final BooleanSetting vectorPerform = new BooleanSetting("Гравий", false);
   private int eventAttach = 9;
   private final Stopwatch serverRead = new Stopwatch();

   public AutoDrop() {
      this.handle(
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform
      );
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      source = false;
      this.refresh();
   }

   private void refresh() {
      if (this.eventAttach > 44) {
         this.eventAttach = 9;
      } else {
         Slot var1 = Module.client.player.playerScreenHandler.getSlot(this.eventAttach);
         if (!var1.hasStack()) {
            this.eventAttach++;
         } else {
            Item var2 = var1.getStack().getItem();
            if (this.handle(var2)) {
               source = true;
               int var3 = Module.client.player.playerScreenHandler.syncId;
               Module.client.interactionManager.clickSlot(var3, this.eventAttach, 1, SlotActionType.THROW, Module.client.player);
               this.eventAttach++;
            } else {
               this.eventAttach++;
            }
         }
      }
   }

   private boolean handle(Item var1) {
      if (var1 == Items.STONE && this.target.compute()) {
         return true;
      } else if (var1 == Items.COBBLESTONE && this.pending.compute()) {
         return true;
      } else if (var1 == Items.GRANITE && this.previous.compute()) {
         return true;
      } else if (var1 == Items.STICK && this.latest.compute()) {
         return true;
      } else if (var1 == Items.ANDESITE && this.matrixBlend.compute()) {
         return true;
      } else if ((var1 == Items.DEEPSLATE || var1 == Items.COBBLED_DEEPSLATE) && this.summary.compute()) {
         return true;
      } else if (var1 == Items.NETHERRACK && this.vectorMatch.compute()) {
         return true;
      } else if ((var1 == Items.BASALT || var1 == Items.SMOOTH_BASALT || var1 == Items.POLISHED_BASALT) && this.itemProject.compute()) {
         return true;
      } else if ((var1 == Items.BLACKSTONE || var1 == Items.GILDED_BLACKSTONE) && this.responseCompute.compute()) {
         return true;
      } else if ((var1 == Items.SOUL_SAND || var1 == Items.SOUL_SOIL) && this.providerFetch.compute()) {
         return true;
      } else {
         return (var1 == Items.NETHER_QUARTZ_ORE || var1 == Items.NETHER_GOLD_ORE || var1 == Items.QUARTZ || var1 == Items.GOLD_NUGGET)
               && this.profileDraw.compute()
            ? true
            : var1 == Items.GRAVEL && this.vectorPerform.compute();
      }
   }
}
