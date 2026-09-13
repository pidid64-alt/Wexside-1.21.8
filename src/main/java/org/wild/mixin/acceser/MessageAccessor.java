package org.wild.mixin.acceser;

import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Builder.class)
public interface MessageAccessor {
   @Accessor("message")
   Text getMessage();
}
