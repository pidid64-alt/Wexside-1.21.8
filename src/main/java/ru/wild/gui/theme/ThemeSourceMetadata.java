package ru.wild.gui.theme;

public final class ThemeSourceMetadata {
   private String instance = "";
   private String data = "";
   private String context = "";
   private String config = "Custom";
   private String state = "local";
   private String cache = "Host Rectangle";
   private String output = "";
   private long current;
   private long active;
   private boolean mode;

   public ThemeSourceMetadata() {
      long var1 = System.currentTimeMillis();
      this.current = var1;
      this.active = var1;
   }

   public ThemeSourceMetadata handle() {
      ThemeSourceMetadata var1 = new ThemeSourceMetadata();
      var1.handle(this);
      return var1;
   }

   public void handle(ThemeSourceMetadata var1) {
      if (var1 != null) {
         this.instance = var1.instance;
         this.data = var1.data;
         this.context = var1.context;
         this.config = var1.config;
         this.state = var1.state;
         this.cache = var1.cache;
         this.output = var1.output;
         this.current = var1.current;
         this.active = var1.active;
         this.mode = var1.mode;
      }
   }

   public String process() {
      return this.instance;
   }

   public void handle(String var1) {
      this.instance = prepare(var1);
   }

   public String compute() {
      return this.data;
   }

   public void process(String var1) {
      this.data = prepare(var1);
   }

   public String resolve() {
      return this.context;
   }

   public void compute(String var1) {
      this.context = prepare(var1);
   }

   public String update() {
      return this.config;
   }

   public void resolve(String var1) {
      String var2 = prepare(var1);
      this.config = var2.isBlank() ? "Custom" : var2;
   }

   public String apply() {
      return this.state;
   }

   public void update(String var1) {
      String var2 = prepare(var1);
      this.state = var2.isBlank() ? "local" : var2;
   }

   public String execute() {
      return this.cache;
   }

   public void apply(String var1) {
      String var2 = prepare(var1);
      if (!"Inset Shape".equals(var2) && !"Full Quad".equals(var2)) {
         this.cache = "Host Rectangle";
      } else {
         this.cache = var2;
      }
   }

   public String prepare() {
      return this.output;
   }

   public void execute(String var1) {
      this.output = prepare(var1);
   }

   public long check() {
      return this.current;
   }

   public void handle(long var1) {
      this.current = Math.max(0L, var1);
   }

   public long onTick() {
      return this.active;
   }

   public void process(long var1) {
      this.active = Math.max(0L, var1);
   }

   public boolean select() {
      return this.mode;
   }

   public void handle(boolean var1) {
      this.mode = var1;
   }

   public void handle(String var1, String var2) {
      long var3 = System.currentTimeMillis();
      if (this.current <= 0L) {
         this.current = var3;
      }

      if (this.active <= 0L) {
         this.active = var3;
      }

      if (this.instance.isBlank()) {
         this.handle(var1);
      }

      if (this.data.isBlank()) {
         this.process(var2);
      }

      if (this.config.isBlank()) {
         this.config = "Custom";
      }

      if (this.state.isBlank()) {
         this.state = "local";
      }

      if (this.cache.isBlank()) {
         this.cache = "Host Rectangle";
      }
   }

   private static String prepare(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.trim().replaceAll("\\s+", " ");
      return var1.length() > 128 ? var1.substring(0, 128) : var1;
   }
}
