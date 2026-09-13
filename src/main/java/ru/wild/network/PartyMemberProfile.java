package ru.wild.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class PartyMemberProfile {
   private final List<PartyMemberProfile.NamedEntry> instance = new ArrayList<>();
   private UUID data;
   private UUID context;
   private String config = "";

   public boolean handle() {
      return this.data != null;
   }

   public String process() {
      return this.config;
   }

   public UUID compute() {
      return this.context;
   }

   public List<PartyMemberProfile.NamedEntry> resolve() {
      return Collections.unmodifiableList(this.instance);
   }

   public int update() {
      return this.instance.size();
   }

   public boolean handle(UUID var1) {
      return this.context != null && this.context.equals(var1);
   }

   public PartyMemberProfile.NamedEntry handle(String var1) {
      for (PartyMemberProfile.NamedEntry var3 : this.instance) {
         if (var3.username().equalsIgnoreCase(var1)) {
            return var3;
         }
      }

      return null;
   }

   void handle(UUID var1, UUID var2, String var3, List<PartyMemberProfile.NamedEntry> var4) {
      this.data = var1;
      this.context = var2;
      this.config = var3 == null ? "" : var3;
      this.instance.clear();
      this.instance.addAll(var4);
   }

   void apply() {
      this.data = null;
      this.context = null;
      this.config = "";
      this.instance.clear();
   }

   public record NamedEntry(UUID uuid, String username) {
   }
}
