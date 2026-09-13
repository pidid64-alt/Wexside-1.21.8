package ru.wild.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FilenameUtils;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;

public final class ConfigManager extends ConfigStore<ConfigProfile> {
   public static final File instance = execute();
   private static final ArrayList<ConfigProfile> data = new ArrayList<>();
   private static final long context = 350L;
   private final ScheduledExecutorService config = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Wild-Config-Autosave");
      var1.setDaemon(true);
      return var1;
   });
   private final Object state = new Object();
   private ScheduledFuture<?> cache;
   private boolean output;
   private boolean current;

   @Compile
   private static File execute() {
      File var0 = WildClient.instance != null ? WildClient.instance.cache : WildClient.process();
      return new File(var0, "configs" + File.separator + "cfg");
   }

   public ConfigManager() {
      if (instance != null && !instance.exists() && !instance.mkdirs()) {
         System.out.println("[ConfigManager] Warning: cannot create config directory at " + instance.getAbsolutePath());
      }

      this.handle(prepare());
   }

   @Compile
   private static ArrayList<ConfigProfile> prepare() {
      synchronized (data) {
         File[] var1 = instance == null ? null : instance.listFiles();
         if (var1 != null) {
            for (File var5 : var1) {
               if (FilenameUtils.getExtension(var5.getName()).equals("json")) {
                  data.add(new ConfigProfile(FilenameUtils.removeExtension(var5.getName())));
               }
            }
         }

         return data;
      }
   }

   @Compile
   public static ArrayList<ConfigProfile> handle() {
      return data;
   }

   @Compile
   public void process() {
      if (instance != null) {
         if (!instance.exists() && !instance.mkdirs()) {
            System.out.println("[ConfigManager] Warning: cannot create config dir on load");
         } else {
            synchronized (data) {
               data.clear();
               File[] var2 = instance.listFiles(File::isFile);
               if (var2 != null) {
                  for (File var6 : var2) {
                     String var7 = FilenameUtils.removeExtension(var6.getName()).replace(" ", "");
                     data.add(new ConfigProfile(var7));
                  }
               }
            }
         }
      }
   }

   @Compile
   public synchronized boolean handle(String var1) {
      if (var1 == null) {
         return false;
      }

      ConfigProfile var2 = this.compute(var1);
      if (var2 == null) {
         return false;
      }

      try (BufferedReader var3 = Files.newBufferedReader(var2.handle().toPath(), StandardCharsets.UTF_8)) {
         JsonObject var4 = JsonParser.parseReader(var3).getAsJsonObject();
         return this.handle(var1, var4);
      } catch (Exception var8) {
         var8.printStackTrace();
         return false;
      }
   }

   @Compile
   public synchronized boolean handle(String var1, JsonObject var2) {
      if (var1 != null && var2 != null) {
         ConfigProfile var3 = this.compute(var1);
         if (var3 == null) {
            var3 = new ConfigProfile(var1);
            this.apply().add(var3);
         }

         var3.handle(var2);
         return true;
      } else {
         return false;
      }
   }

   public synchronized boolean process(String var1) {
      if (var1 == null) {
         return false;
      }

      ConfigProfile var2;
      if ((var2 = this.compute(var1)) == null) {
         try {
            ConfigProfile var3 = var2 = new ConfigProfile(var1);
            this.apply().add(var3);
         } catch (Throwable var11) {
            System.out.println("[ConfigManager] Cannot create config '" + var1 + "': " + var11.getMessage());
            return false;
         }
      }

      File var14 = var2.handle();
      if (var14 == null) {
         return false;
      }

      File var4 = var14.getParentFile();
      if (var4 != null && !var4.exists() && !var4.mkdirs()) {
         System.out.println("[ConfigManager] Cannot create directory for '" + var1 + "'");
         return false;
      }

      String var5;
      try {
         var5 = new GsonBuilder().setPrettyPrinting().create().toJson(var2.compute());
      } catch (Throwable var10) {
         System.out.println("[ConfigManager] Failed to serialize config '" + var1 + "': " + var10.getMessage());
         return false;
      }

      try (BufferedWriter var6 = Files.newBufferedWriter(var14.toPath(), StandardCharsets.UTF_8)) {
         var6.write(var5);
         return true;
      } catch (IOException var13) {
         System.out.println("[ConfigManager] I/O error saving '" + var1 + "': " + var13.getMessage());
         return false;
      }
   }

   public ConfigProfile compute(String var1) {
      if (var1 == null) {
         return null;
      }

      for (ConfigProfile var3 : this.apply()) {
         if (var3.process().equalsIgnoreCase(var1)) {
            return var3;
         }
      }

      return new File(instance, var1 + ".json").exists() ? new ConfigProfile(var1) : null;
   }

   @Compile
   public boolean resolve(String var1) {
      if (var1 == null) {
         return false;
      }

      ConfigProfile var2 = this.compute(var1);
      if (var2 == null) {
         return false;
      }

      File var3 = var2.handle();
      this.apply().remove(var2);
      return var3.exists() && var3.delete();
   }

   public void compute() {
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         synchronized (this.state) {
            if (!this.current) {
               this.output = true;
               if (this.cache != null) {
                  this.cache.cancel(false);
               }

               this.cache = this.config.schedule(this::check, 350L, TimeUnit.MILLISECONDS);
            }
         }
      }
   }
   public void resolve() {
      Object var2 = this.state;
      synchronized (this.state){} // $VF: monitorenter 
      boolean var6 = false /* VF: Semaphore variable */;

      boolean var1;
      try {
         var6 = true;
         if (this.current) {
            return;
         }

         this.current = true;
         var1 = this.output;
         this.output = false;
         if (this.cache != null) {
            this.cache.cancel(false);
            this.cache = null;
         }
         var6 = false;
      } finally {
         if (var6) {
         }
      }

      if (var1) {
         this.process("default");
      }

      this.config.shutdown();

      try {
         this.config.awaitTermination(1L, TimeUnit.SECONDS);
      } catch (InterruptedException var7) {
         Thread.currentThread().interrupt();
      }
   }

   private void check() {
      synchronized (this.state) {
         if (!this.output) {
            this.cache = null;
            return;
         }

         this.output = false;
         this.cache = null;
      }

      if (!this.process("default")) {
         synchronized (this.state) {
            if (!this.current) {
               this.output = true;
               this.cache = this.config.schedule(this::check, 350L, TimeUnit.MILLISECONDS);
            }
         }
      }
   }

   @Compile
   public boolean update() {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         for (Module var2 : WildClient.instance.data.instance) {
            var2.disable();
         }

         ThemeRenderer.handle().handle(Map.of());
         HudProfileConfig.compute();
         return this.handle("default");
      } else {
         return false;
      }
   }

   static {
      Loader.initialize();
   }
}
