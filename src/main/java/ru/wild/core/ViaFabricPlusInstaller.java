package ru.wild.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;

public final class ViaFabricPlusInstaller {
   public static final String instance = "viafabricplus";
   public static final String data = "https://modrinth.com/mod/viafabricplus";
   private static final String context = "com.viaversion.viaversion.api.protocol.version.ProtocolVersion";
   private static final String[] config = new String[]{
      "com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator", "de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator"
   };
   private static final String[] state = new String[]{"com.viaversion.vialoader.util.ProtocolVersionList", "net.raphimc.vialoader.util.ProtocolVersionList"};
   private static final String cache = "de.florianmichael.viafabricplus.protocolhack.ProtocolHack";
   private static final String[] output = new String[]{"1.21.5", "1.21.4", "1.21.2", "1.20.6", "1.20.1", "1.19.4", "1.18.2", "1.16.5", "1.12.2", "1.8.x"};
   private static volatile boolean current;
   private static boolean active;
   private static boolean mode;
   private static Method selection;
   private static Method enabled;
   private static Method renderer;
   private static Method handler;
   private static Method animationDraw;
   private static Method pointEncode;
   private static Method animator;
   private static Method source;
   private static Field target;
   private static Field pending;
   private static List<ViaFabricPlusInstaller.DataRecord> previous = List.of();
   private static boolean latest;
   private static long summary;

   private ViaFabricPlusInstaller() {
   }

   public static boolean handle() {
      render();
      return active;
   }

   public static boolean process() {
      render();
      return mode;
   }

   public static String compute() {
      try {
         return FabricLoader.getInstance().getModContainer("viafabricplus").map(var0 -> var0.getMetadata().getVersion().getFriendlyString()).orElse(null);
      } catch (Throwable var1) {
         return null;
      }
   }

   public static String resolve() {
      try {
         return SharedConstants.getGameVersion().name();
      } catch (Throwable var1) {
         return "1.21.8";
      }
   }

   public static List<ViaFabricPlusInstaller.DataRecord> update() {
      if (!handle()) {
         return List.of();
      }

      long var0 = System.nanoTime();
      if (previous.isEmpty() || !latest && var0 - summary >= 1000000000L) {
         summary = var0;
         List var2 = select();
         if (!var2.isEmpty()) {
            previous = var2;
            latest = onTick();
         }

         return previous;
      } else {
         return previous;
      }
   }

   public static List<ViaFabricPlusInstaller.DataRecord> handle(List<ViaFabricPlusInstaller.DataRecord> var0, ViaFabricPlusInstaller.DataRecord var1, int var2) {
      ArrayList var3 = new ArrayList(var2);

      for (ViaFabricPlusInstaller.DataRecord var5 : var0) {
         if (var5.autoDetect()) {
            var3.add(var5);
            break;
         }
      }

      ViaFabricPlusInstaller.DataRecord var10 = handle(var0, resolve());
      if (var10 != null && !var3.contains(var10)) {
         var3.add(var10);
      }

      if (var1 != null && !var3.contains(var1)) {
         var3.add(var1);
      }

      for (String var8 : output) {
         if (var3.size() >= var2) {
            break;
         }

         ViaFabricPlusInstaller.DataRecord var9 = handle(var0, var8);
         if (var9 != null && !var3.contains(var9)) {
            var3.add(var9);
         }
      }

      for (ViaFabricPlusInstaller.DataRecord var13 : var0) {
         if (var3.size() >= var2) {
            break;
         }

         if (!var3.contains(var13) && "RELEASE".equals(var13.group())) {
            var3.add(var13);
         }
      }

      return List.copyOf(var3);
   }

   private static ViaFabricPlusInstaller.DataRecord handle(List<ViaFabricPlusInstaller.DataRecord> var0, String var1) {
      for (ViaFabricPlusInstaller.DataRecord var3 : var0) {
         if (var3.label().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public static ViaFabricPlusInstaller.DataRecord apply() {
      if (!handle()) {
         return null;
      }

      Object var0 = check();
      if (var0 == null) {
         return null;
      }

      for (ViaFabricPlusInstaller.DataRecord var2 : update()) {
         if (Objects.equals(var2.handle(), var0)) {
            return var2;
         }
      }

      return handle(var0);
   }

   public static String execute() {
      ViaFabricPlusInstaller.DataRecord var0 = apply();
      return var0 == null ? resolve() : var0.label();
   }

   public static boolean prepare() {
      if (!handle()) {
         return false;
      }

      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0 != null && var0.getNetworkHandler() == null && var0.world == null;
   }

   public static boolean handle(ViaFabricPlusInstaller.DataRecord var0) {
      if (var0 != null && var0.handle() != null && prepare()) {
         try {
            if (renderer != null) {
               renderer.invoke(null, var0.handle(), Boolean.TRUE);
            } else {
               if (enabled == null) {
                  return false;
               }

               enabled.invoke(null, var0.handle());
            }

            return true;
         } catch (Throwable var2) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static Object check() {
      if (selection != null) {
         try {
            Object var0 = selection.invoke(null);
            if (var0 != null) {
               return var0;
            }
         } catch (Throwable var1) {
         }
      }

      if (target != null) {
         try {
            return target.get(null);
         } catch (Throwable var2) {
         }
      }

      return null;
   }

   private static boolean onTick() {
      if (pending != null && handler != null) {
         try {
            Object var0 = pending.get(null);

            for (Object var2 : (List)handler.invoke(null)) {
               if (var2 == var0) {
                  return true;
               }
            }

            return false;
         } catch (Throwable var3) {
            return true;
         }
      } else {
         return true;
      }
   }

   private static List<ViaFabricPlusInstaller.DataRecord> select() {
      List var0 = refresh();
      if (var0.isEmpty()) {
         return List.of();
      }

      ArrayList var1 = new ArrayList(var0.size());

      for (Object var3 : var0) {
         ViaFabricPlusInstaller.DataRecord var4 = handle(var3);
         if (var4 != null) {
            var1.add(var4);
         }
      }

      return List.copyOf(var1);
   }

   private static List<Object> refresh() {
      if (handler != null) {
         try {
            ArrayList var0 = new ArrayList((List)handler.invoke(null));
            Collections.reverse(var0);
            return var0;
         } catch (Throwable var2) {
         }
      }

      if (animationDraw != null) {
         try {
            return new ArrayList<>((List)animationDraw.invoke(null));
         } catch (Throwable var1) {
         }
      }

      return List.of();
   }

   private static ViaFabricPlusInstaller.DataRecord handle(Object var0) {
      if (var0 != null && pointEncode != null) {
         String var1;
         try {
            var1 = (String)pointEncode.invoke(var0);
         } catch (Throwable var9) {
            return null;
         }

         if (var1 != null && !var1.isEmpty()) {
            int var2 = Integer.MIN_VALUE;
            if (animator != null) {
               try {
                  var2 = (Integer)animator.invoke(var0);
               } catch (Throwable var8) {
               }
            }

            String var3 = "OTHER";
            if (source != null) {
               try {
                  var3 = source.invoke(var0) instanceof Enum var5 ? var5.name() : "OTHER";
               } catch (Throwable var7) {
               }
            }

            boolean var10 = false;
            if (pending != null) {
               try {
                  var10 = pending.get(null) == var0;
               } catch (Throwable var6) {
               }
            }

            return new ViaFabricPlusInstaller.DataRecord(var0, var1, var2, var3, var10);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static void render() {
      if (!current) {
         synchronized (ViaFabricPlusInstaller.class) {
            if (!current) {
               try {
                  tick();
               } catch (Throwable var3) {
                  active = false;
               }

               current = true;
            }
         }
      }
   }

   private static void tick() {
      boolean var0 = false;

      try {
         var0 = FabricLoader.getInstance().isModLoaded("viafabricplus");
      } catch (Throwable var7) {
      }

      Class var1 = handle("com.viaversion.viaversion.api.protocol.version.ProtocolVersion");
      if (var1 == null) {
         mode = var0 && handle("de.florianmichael.viafabricplus.protocolhack.ProtocolHack") != null;
         active = false;
      } else {
         pointEncode = handle(var1, "getName", new Class<?>[0]);
         animator = handle(var1, "getVersion", new Class<?>[0]);
         source = handle(var1, "getVersionType", new Class<?>[0]);
         handler = handle(var1, "getProtocols", new Class<?>[0]);

         for (String var5 : state) {
            Class var6 = handle(var5);
            if (var6 != null) {
               animationDraw = handle(var6, "getProtocolsNewToOld", new Class<?>[0]);
               if (animationDraw != null) {
                  break;
               }
            }
         }

         for (String var11 : config) {
            Class var12 = handle(var11);
            if (var12 != null) {
               selection = handle(var12, "getTargetVersion", new Class<?>[0]);
               enabled = handle(var12, "setTargetVersion", var1);
               renderer = handle(var12, "setTargetVersion", var1, boolean.class);
               target = handle(var12, "NATIVE_VERSION");
               pending = handle(var12, "AUTO_DETECT_PROTOCOL");
               if (selection != null && enabled != null && pointEncode != null) {
                  active = true;
                  return;
               }
            }
         }

         mode = var0 && handle("de.florianmichael.viafabricplus.protocolhack.ProtocolHack") != null;
         active = false;
      }
   }

   private static Class<?> handle(String var0) {
      try {
         return Class.forName(var0, false, ViaFabricPlusInstaller.class.getClassLoader());
      } catch (Throwable var2) {
         return null;
      }
   }

   private static Method handle(Class<?> var0, String var1, Class<?>... var2) {
      try {
         Method var3 = var0.getMethod(var1, var2);
         var3.setAccessible(true);
         return var3;
      } catch (Throwable var4) {
         return null;
      }
   }

   private static Field handle(Class<?> var0, String var1) {
      try {
         Field var2 = var0.getField(var1);
         var2.setAccessible(true);
         return var2;
      } catch (Throwable var3) {
         return null;
      }
   }

   public record DataRecord(Object handle, String label, int protocol, String group, boolean autoDetect) {
   }
}
