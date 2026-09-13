package ru.wild.util.io;

import java.io.File;
import java.lang.reflect.Method;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import ru.wild.gui.theme.FoundryPresetLibrary;

public final class AvatarFilePicker {
   private AvatarFilePicker() {
   }

   public static File handle() {
      try {
         Class var0 = Class.forName("org.lwjgl.util.tinyfd.TinyFileDialogs");
         Class var1 = Class.forName("org.lwjgl.PointerBuffer");
         Method var2 = var0.getMethod("tinyfd_openFileDialog", CharSequence.class, CharSequence.class, var1, CharSequence.class, boolean.class);
         if (var2.invoke(null, "Импорт аватара Figura", "", null, "Figura avatar (.zip)", false) instanceof CharSequence var4 && var4.length() > 0) {
            File var5 = new File(var4.toString());
            return var5.exists() ? var5 : null;
         }
      } catch (Throwable var6) {
      }

      return null;
   }

   public static void process() {
      try {
         File var0 = FoundryPresetLibrary.handle().process();
         if (!var0.exists()) {
            var0.mkdirs();
         }

         Util.getOperatingSystem().open(var0);
      } catch (Throwable var1) {
      }

      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null) {
      }
   }
}
