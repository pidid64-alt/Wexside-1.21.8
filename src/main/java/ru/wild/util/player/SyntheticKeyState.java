package ru.wild.util.player;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.PlayerInput;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;

public class SyntheticKeyState implements MinecraftContext {
   private static final SyntheticKeyState data = new SyntheticKeyState();
   public final Set<String> instance = new HashSet<>();

   private SyntheticKeyState() {
   }

   public static SyntheticKeyState handle() {
      return data;
   }

   public void handle(String var1) {
      if (toggleState.player != null && toggleState.player.isAlive() && toggleState.world != null) {
         AttackAura.indexBind = true;
         this.instance.add(var1);
         this.handle(false);
         if (toggleState.player.isSprinting()) {
            toggleState.player.setSprinting(false);
         }

         if (toggleState.player.input != null) {
            toggleState.player.input.playerInput = PlayerInput.DEFAULT;
         }
      }
   }

   public void process(String var1) {
      if (toggleState.player != null && toggleState.player.isAlive() && toggleState.world != null) {
         this.instance.remove(var1);
         if (this.instance.isEmpty() && toggleState.currentScreen == null) {
            this.handle(true);
            AttackAura.indexBind = false;
         }
      }
   }

   private void handle(boolean var1) {
      if (toggleState.options != null && toggleState.getWindow() != null) {
         KeyBinding[] var2 = new KeyBinding[]{
            toggleState.options.forwardKey,
            toggleState.options.backKey,
            toggleState.options.leftKey,
            toggleState.options.rightKey,
            toggleState.options.jumpKey,
            toggleState.options.sprintKey
         };
         long var3 = toggleState.getWindow().getHandle();

         for (KeyBinding var8 : var2) {
            boolean var9 = var1 && InputUtil.isKeyPressed(var3, var8.getDefaultKey().getCode());
            var8.setPressed(var9);
         }
      }
   }
}
