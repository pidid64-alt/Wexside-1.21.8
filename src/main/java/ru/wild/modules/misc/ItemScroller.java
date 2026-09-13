package ru.wild.modules.misc;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.wild.mixin.acceser.HandledScreenAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.math.Stopwatch;

@ModuleRegister(name = "ItemScroller", description = "Ускоряет перекладывание", category = ModuleCategory.Misc)
public class ItemScroller extends Module {
   public final NumberSetting source = new NumberSetting("Задержка", 10.0F, 0.0F, 100.0F, 1.0F, false);
   private static ItemScroller target;
   private final Stopwatch pending = new Stopwatch();

   public ItemScroller() {
      this.handle(this.source);
      target = this;
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (Module.client.player != null && Module.client.currentScreen != null) {
         if (Module.client.currentScreen instanceof HandledScreen var2) {
            if (Module.client.getWindow() != null) {
               long var3 = Module.client.getWindow().getHandle();
               boolean var5 = GLFW.glfwGetKey(var3, 340) == 1 || GLFW.glfwGetKey(var3, 344) == 1;
               boolean var6 = GLFW.glfwGetMouseButton(var3, 0) == 1;
               if (var5 && var6) {
                  long var7 = (long)this.source.compute();
                  if (this.pending.update(var7)) {
                     double var9 = Module.client.mouse.getX() * Module.client.getWindow().getScaledWidth() / Module.client.getWindow().getWidth();
                     double var11 = Module.client.mouse.getY() * Module.client.getWindow().getScaledHeight() / Module.client.getWindow().getHeight();
                     Slot var13 = ((HandledScreenAccessor)var2).getSlotAtPosition(var9, var11);
                     if (var13 != null && var13.hasStack()) {
                        Module.client.interactionManager.clickSlot(var2.getScreenHandler().syncId, var13.id, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                        this.pending.handle();
                     }
                  }
               }
            }
         }
      }
   }
   public static ItemScroller refresh() {
      return target;
   }
}
