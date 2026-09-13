package ru.wild.modules.misc;

import com.mojang.authlib.GameProfile;
import java.util.function.Consumer;
import net.minecraft.util.Identifier;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(name = "Cape", category = ModuleCategory.Misc, description = "Добавляет вам плащик")
public class Cape extends Module {
   public static void handle(GameProfile var0, Consumer<Identifier> var1) {
   }
}
