package ru.wild.util.io;

import java.util.ArrayDeque;

public final class UndoHistory {
   private static final int instance = 50;
   private final ArrayDeque<String> data = new ArrayDeque<>();
   private final ArrayDeque<String> context = new ArrayDeque<>();

   public void handle(String var1) {
      if (var1 != null && !var1.equals(this.data.peekLast())) {
         this.data.addLast(var1);

         while (this.data.size() > 50) {
            this.data.pollFirst();
         }

         this.context.clear();
      }
   }

   public String process(String var1) {
      if (var1 == null) {
         return null;
      }

      while (!this.data.isEmpty() && var1.equals(this.data.peekLast())) {
         this.data.pollLast();
      }

      if (this.data.isEmpty()) {
         return null;
      }

      this.context.addLast(var1);
      return this.data.pollLast();
   }

   public String compute(String var1) {
      if (var1 == null) {
         return null;
      }

      while (!this.context.isEmpty() && var1.equals(this.context.peekLast())) {
         this.context.pollLast();
      }

      if (this.context.isEmpty()) {
         return null;
      }

      this.data.addLast(var1);
      return this.context.pollLast();
   }

   public boolean handle() {
      return !this.data.isEmpty();
   }

   public boolean process() {
      return !this.context.isEmpty();
   }

   public void compute() {
      this.data.clear();
      this.context.clear();
   }
}
