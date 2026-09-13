package ru.wild.network;

import net.minecraft.util.Identifier;

public interface RemoteFrameBuffer {
   int handle();

   int process();

   String handle(int var1);

   Identifier process(int var1);

   void compute(int var1);

   void handle(String var1);

   void resolve(int var1);
}
