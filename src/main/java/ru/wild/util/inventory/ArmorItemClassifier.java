package ru.wild.util.inventory;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;

public class ArmorItemClassifier {
   private static Map<RegistryEntry<EntityAttribute>, Double> apply(ItemStack var0) {
      AttributeModifiersComponent var1 = (AttributeModifiersComponent)var0.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      HashMap var2 = new HashMap();
      if (var1 == null) {
         return var2;
      }

      for (Entry var4 : var1.modifiers()) {
         if (var4.slot().matches(EquipmentSlot.OFFHAND)) {
            EntityAttributeModifier var5 = var4.modifier();
            if (var5.operation() == Operation.ADD_VALUE) {
               var2.put(var4.attribute(), var5.value());
            }
         }
      }

      return var2;
   }

   private static boolean handle(Map<RegistryEntry<EntityAttribute>, Double> var0, RegistryEntry<EntityAttribute> var1, double var2) {
      return Double.compare(var0.getOrDefault(var1, 0.0), var2) == 0;
   }

   public static boolean handle(ItemStack var0) {
      return var0.isOf(Items.ENCHANTED_GOLDEN_APPLE);
   }

   public static boolean process(ItemStack var0) {
      return var0.isOf(Items.TOTEM_OF_UNDYING);
   }

   public static boolean compute(ItemStack var0) {
      return var0.isOf(Items.DIAMOND);
   }

   public static boolean resolve(ItemStack var0) {
      return var0.isOf(Items.SPAWNER);
   }

   public static boolean update(ItemStack var0) {
      return var0.isOf(Items.GOLDEN_APPLE);
   }
}
