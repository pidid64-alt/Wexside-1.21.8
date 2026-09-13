package org.wild.mixin.acceser;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
   @Accessor("x")
   int litka$getX();

   @Accessor("y")
   int litka$getY();

   @Accessor("focusedSlot")
   Slot litka$getFocusedSlot();

   @Invoker("getSlotAt")
   Slot getSlotAtPosition(double var1, double var3);
}
