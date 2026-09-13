package org.wild.mixin.acceser;

import java.util.List;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
   @Accessor("messages")
   List<ChatHudLine> litka$getMessages();

   @Accessor("visibleMessages")
   List<Visible> litka$getVisibleMessages();
}
