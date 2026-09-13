package ru.wild.modules.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.audio.AudioResourceManager;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.math.ActionDelay;

@ModuleRegister(name = "FriendManager", category = ModuleCategory.Misc, description = "Менеджер по управлению в друзьях")
public class FriendManager extends Module {
   public static KeybindSetting source = new KeybindSetting("Бинд друзей", -1);
   public static BooleanSetting target = new BooleanSetting("Не бить друзей", true);
   public static BooleanSetting pending = new BooleanSetting("Убирать хитбокс друга", true);
   private final ActionDelay previous = new ActionDelay();

   public FriendManager() {
      this.handle(source, target, pending);
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (var1.resolve() == source.compute() && var1.apply() == 1 && this.previous.resolve(200L)) {
         if (AttackAura.textureRun != null) {
            return;
         }

         HitResult var2 = Module.client.crosshairTarget;
         if (var2 == null || var2.getType() != Type.ENTITY) {
            return;
         }

         if (!(((EntityHitResult)var2).getEntity() instanceof PlayerEntity var4)) {
            return;
         }

         String var5 = var4.getName().getString();
         String var6 = WildClient.instance.fetchProvider();
         if (!ru.wild.core.manager.FriendManager.handle(var5)) {
            WildClient.instance.projectItem().handle(var6 + "friend add " + var5);
            AudioResourceManager.handle("add", 0.5F);
         } else {
            WildClient.instance.projectItem().handle(var6 + "friend remove " + var5);
            AudioResourceManager.handle("remove", 0.5F);
         }

         this.previous.handle();
      }
   }
}
