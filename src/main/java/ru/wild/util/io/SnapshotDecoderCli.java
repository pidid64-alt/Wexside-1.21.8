package ru.wild.util.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Locale;
import ru.wild.render.GlErrorNames;
import ru.wild.render.RenderDiagnosticKind;
import ru.wild.security.WildSnapshotCrypto;

public final class SnapshotDecoderCli {
   private SnapshotDecoderCli() {
   }

   public static void main(String[] var0) throws Exception {
      if (var0.length < 2) {
         System.out.println("usage: WildSnapDecoder <x25519-private-der-b64-or-file> <snapshot.wildsnap>");
      } else {
         byte[] var1 = handle(var0[0]);
         byte[] var2 = Files.readAllBytes(Path.of(var0[1]));
         byte[] var3 = WildSnapshotCrypto.handle(var2, var1);

         try (DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(var3))) {
            int var5 = var4.readInt();
            int var6 = var4.readInt();
            System.out.println("# WildSnap Report");
            System.out.println();
            System.out.println("- magic: 0x" + Integer.toHexString(var5));
            System.out.println("- version: " + var6);

            while (var4.available() > 0) {
               int var7 = Short.toUnsignedInt(var4.readShort());
               int var8 = var4.readInt();
               byte[] var9 = var4.readNBytes(var8);
               handle(var7, var9);
            }
         }
      }
   }

   private static byte[] handle(String var0) throws Exception {
      Path var1 = Path.of(var0);
      String var2 = Files.exists(var1) ? Files.readString(var1) : var0;
      return Base64.getDecoder().decode(var2.replace("\n", "").replace("\r", "").trim());
   }

   private static void handle(int var0, byte[] var1) throws Exception {
      try (DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(var1))) {
         switch (var0) {
            case 1:
               handle(var2);
               break;
            case 2:
               process(var2);
               break;
            case 3:
               compute(var2);
               break;
            case 4:
               resolve(var2);
               break;
            case 5:
               update(var2);
               break;
            case 6:
               apply(var2);
               break;
            case 7:
               execute(var2);
               break;
            default:
               System.out.println("- record[" + var0 + "]: " + handle(var1, Math.min(var1.length, 96)));
         }
      }
   }

   private static void handle(DataInputStream var0) throws Exception {
      System.out.println();
      System.out.println("## Build");
      System.out.println("- id: " + var0.readUTF());
      System.out.println("- version: " + var0.readUTF());
      System.out.println("- channel: " + var0.readUTF());
   }

   private static void process(DataInputStream var0) throws Exception {
      int var1 = var0.readInt();
      int var2 = var0.readInt();
      int var3 = var0.readInt();
      long var4 = var0.readLong();
      long var6 = var0.readLong();
      int var8 = var0.readInt();
      System.out.println();
      System.out.println("## Core");
      System.out.println("- tracker: " + handle(var1));
      System.out.println("- code: " + RenderDiagnosticKind.handle(var2));
      System.out.println("- detail: 0x" + Integer.toHexString(var3));
      System.out.println("- cfi: 0x" + Long.toUnsignedString(var4, 16));
      System.out.println("- frame: " + var6);
      System.out.println("- anomalyTotal: " + var8);
   }

   private static void compute(DataInputStream var0) throws Exception {
      int var1 = var0.readInt();
      int var2 = var0.readInt();
      int var3 = var0.readInt();
      int var4 = var0.readInt();
      System.out.println();
      System.out.println("## GL");
      System.out.println("- currentProgram: " + var1);
      System.out.println("- activeTexture: " + var2);
      System.out.println("- texture2D: " + var3);
      System.out.println("- error: " + GlErrorNames.handle(var4));
   }

   private static void resolve(DataInputStream var0) throws Exception {
      long var1 = var0.readLong();
      boolean var3 = var0.readBoolean();
      System.out.println();
      System.out.println("## Matrix");
      System.out.println("- hash: 0x" + Long.toUnsignedString(var1, 16));
      System.out.println("- finite: " + var3);
      System.out.print("- modelView: [");

      for (int var4 = 0; var4 < 16; var4++) {
         if (var4 > 0) {
            System.out.print(", ");
         }

         System.out.print(var0.readFloat());
      }

      System.out.println("]");
   }

   private static void update(DataInputStream var0) throws Exception {
      int var1 = var0.readInt();
      System.out.println();
      System.out.println("## Anomalies");
      System.out.println("- count: " + var1);

      for (int var2 = 0; var2 < var1; var2++) {
         long var3 = var0.readLong();
         int var5 = var0.readInt();
         int var6 = var0.readInt();
         int var7 = var0.readInt();
         long var8 = var0.readLong();
         System.out
            .println(
               "- "
                  + handle(var5)
                  + " code="
                  + RenderDiagnosticKind.handle(var6)
                  + " detail=0x"
                  + Integer.toHexString(var7)
                  + " nanos="
                  + var3
                  + " cfi=0x"
                  + Long.toUnsignedString(var8, 16)
            );
      }
   }

   private static void apply(DataInputStream var0) throws Exception {
      System.out.println();
      System.out.println("## Environment");
      System.out.println("- os.name: " + var0.readUTF());
      System.out.println("- os.arch: " + var0.readUTF());
      System.out.println("- java.version: " + var0.readUTF());
      System.out.println("- java.vm.name: " + var0.readUTF());
   }

   private static void execute(DataInputStream var0) throws Exception {
      System.out.println();
      System.out.println("## Mixin Audit");
      System.out.println("- policy: " + var0.readUTF());
      System.out.println("- locals: " + var0.readUTF());
   }

   private static String handle(int var0) {
      String var1 = Integer.toUnsignedString(var0, 16).toUpperCase(Locale.ROOT);
      return var1.length() >= 8 ? "WS-" + var1.substring(var1.length() - 8) : "WS-" + "00000000".substring(var1.length()) + var1;
   }

   private static String handle(byte[] var0, int var1) {
      StringBuilder var2 = new StringBuilder(var1 * 2);

      for (int var3 = 0; var3 < var1; var3++) {
         int var4 = var0[var3] & 255;
         if (var4 < 16) {
            var2.append('0');
         }

         var2.append(Integer.toHexString(var4));
      }

      return var2.toString();
   }
}
