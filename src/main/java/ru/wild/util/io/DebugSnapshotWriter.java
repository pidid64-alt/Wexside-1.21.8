package ru.wild.util.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import ru.wild.core.RenderStateValidator;
import ru.wild.render.GlErrorNames;
import ru.wild.render.RenderDiagnostics;
import ru.wild.security.WildSnapshotCrypto;

public final class DebugSnapshotWriter {
   private static final DateTimeFormatter instance = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

   public Path handle(RenderDiagnostics var1, int var2, int var3, int var4) throws Exception {
      ByteArrayOutputStream var5 = new ByteArrayOutputStream(2048);

      try (DataOutputStream var6 = new DataOutputStream(var5)) {
         var6.writeInt(1465077328);
         var6.writeInt(1);
         this.handle(var6);
         this.handle(var6, var1, var2, var3, var4);
         this.process(var6);
         this.compute(var6);
         this.handle(var6, var1);
         this.resolve(var6);
         this.update(var6);
      }

      byte[] var11 = WildSnapshotCrypto.handle(var5.toByteArray());
      Path var7 = this.handle();
      Path var8 = var7.resolve(var1.getName(var2) + "-" + instance.format(LocalDateTime.now()) + ".wildsnap");
      Files.write(var8, var11, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
      return var8;
   }

   public Path handle() throws Exception {
      Path var1 = Path.of(System.getProperty("user.dir", "."), "wild", "debug", "snapshots");
      Files.createDirectories(var1);
      return var1;
   }

   private void handle(DataOutputStream var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(256);

      try (DataOutputStream var3 = new DataOutputStream(var2)) {
         var3.writeUTF("wild-1.21.8-1787661348375");
         var3.writeUTF("1.21.8");
         var3.writeUTF("stable");
      }

      handle(var1, 1, var2.toByteArray());
   }

   private void handle(DataOutputStream var1, RenderDiagnostics var2, int var3, int var4, int var5) throws Exception {
      ByteArrayOutputStream var6 = new ByteArrayOutputStream(128);

      try (DataOutputStream var7 = new DataOutputStream(var6)) {
         var7.writeInt(var3);
         var7.writeInt(var4);
         var7.writeInt(var5);
         var7.writeLong(var2.load());
         var7.writeLong(var2.save());
         var7.writeInt(var2.submit());
      }

      handle(var1, 2, var6.toByteArray());
   }

   private void process(DataOutputStream var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(64);

      try (DataOutputStream var3 = new DataOutputStream(var2)) {
         GlErrorNames.handle(var3);
      }

      handle(var1, 3, var2.toByteArray());
   }

   private void compute(DataOutputStream var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(96);

      try (DataOutputStream var3 = new DataOutputStream(var2)) {
         RenderStateValidator.handle(var3);
      }

      handle(var1, 4, var2.toByteArray());
   }

   private void handle(DataOutputStream var1, RenderDiagnostics var2) throws Exception {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream(1024);

      try (DataOutputStream var4 = new DataOutputStream(var3)) {
         var2.handle(var4);
      }

      handle(var1, 5, var3.toByteArray());
   }

   private void resolve(DataOutputStream var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(256);

      try (DataOutputStream var3 = new DataOutputStream(var2)) {
         var3.writeUTF(handle("os.name"));
         var3.writeUTF(handle("os.arch"));
         var3.writeUTF(handle("java.version"));
         var3.writeUTF(handle("java.vm.name"));
      }

      handle(var1, 6, var2.toByteArray());
   }

   private void update(DataOutputStream var1) throws Exception {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(128);

      try (DataOutputStream var3 = new DataOutputStream(var2)) {
         var3.writeUTF("inject-only-runtime");
         var3.writeUTF("no-lvt-runtime");
      }

      handle(var1, 7, var2.toByteArray());
   }

   private static String handle(String var0) {
      String var1 = System.getProperty(var0, "unknown");
      return var1 != null && !var1.isBlank() ? var1.replace('\n', ' ').replace('\r', ' ').trim() : "unknown";
   }

   private static void handle(DataOutputStream var0, int var1, byte[] var2) throws Exception {
      var0.writeShort(var1);
      var0.writeInt(var2.length);
      var0.write(var2);
   }
}
