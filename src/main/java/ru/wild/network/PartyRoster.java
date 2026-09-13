package ru.wild.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class PartyRoster {
   private final List<PartyRoster.Point3d> instance = new ArrayList<>();
   private final List<PartyRoster.Point3d> data = Collections.unmodifiableList(this.instance);

   public List<PartyRoster.Point3d> handle() {
      return this.data;
   }

   public int process() {
      return this.instance.size();
   }

   public PartyRoster.Point3d handle(UUID var1) {
      for (int var2 = 0; var2 < this.instance.size(); var2++) {
         PartyRoster.Point3d var3 = this.instance.get(var2);
         if (var3.id().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public long handle(PartyRoster.Point3d var1, long var2) {
      return !var1.playing() ? var1.positionMs() : var1.positionMs() + Math.max(0L, var2 - var1.stampMs());
   }

   void handle(List<PartyRoster.Point3d> var1) {
      this.instance.clear();
      this.instance.addAll(var1);
   }

   void compute() {
      this.instance.clear();
   }

   public record Point3d(
      UUID id,
      UUID owner,
      String source,
      double x,
      double y,
      double z,
      float yaw,
      float width,
      float height,
      boolean playing,
      long positionMs,
      long stampMs,
      float volume
   ) {
   }
}
