package ru.wild.util.io;

import ru.wild.security.BuildFingerprint;

public final class DiagnosticRecordWriter {
   private BuildFingerprint instance;

   public void handle(BuildFingerprint var1) {
      this.instance = var1;
   }

   public void handle(int var1) {
      if (this.instance != null) {
         this.instance.handle(var1);
      }
   }

   public void handle(long var1) {
      if (this.instance != null) {
         this.instance.process(var1);
      }
   }

   public void handle(float var1) {
      if (this.instance != null) {
         this.instance.handle(var1);
      }
   }
}
