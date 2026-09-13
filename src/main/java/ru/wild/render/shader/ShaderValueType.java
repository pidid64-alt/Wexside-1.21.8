package ru.wild.render.shader;

public enum ShaderValueType {
   FLOAT("float", 1),
   VEC2("vec2", 2),
   VEC3("vec3", 3),
   VEC4("vec4", 4),
   INT("int", 1);

   private final String instance;
   private final int data;

   ShaderValueType(String var3, int var4) {
      this.instance = var3;
      this.data = var4;
   }

   public String handle() {
      return this.instance;
   }

   public int process() {
      return this.data;
   }
}
