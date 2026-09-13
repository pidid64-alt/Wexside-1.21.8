package ru.wild.config;

import com.google.gson.JsonObject;

public interface JsonStateCodec {
   JsonObject compute();

   void handle(JsonObject var1);
}
