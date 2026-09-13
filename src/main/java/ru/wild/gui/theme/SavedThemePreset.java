package ru.wild.gui.theme;

public final class SavedThemePreset {
   private final String instance;
   private String data;
   private String context;
   private String config;
   private String state;
   private String cache;
   private String output;
   private String current;
   private String active;
   private long mode;
   private long selection;
   private boolean enabled;

   public SavedThemePreset(String var1, String var2, String var3, String var4, long var5) {
      this(var1, var2, var3, var4, "", "", "Custom", "user", "saved", var5, var5, false);
   }

   public SavedThemePreset(String var1, String var2, String var3, String var4, String var5, String var6, String var7, long var8, long var10, boolean var12) {
      this(var1, var2, var3, var4, var5, var6, var7, "user", "saved", var8, var10, var12);
   }

   public SavedThemePreset(
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      String var7,
      String var8,
      String var9,
      long var10,
      long var12,
      boolean var14
   ) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5 == null ? "" : var5;
      this.cache = var6 == null ? "" : var6;
      this.output = var7 != null && !var7.isBlank() ? var7 : "Custom";
      this.current = var8 != null && !var8.isBlank() ? var8 : "user";
      this.active = var9 != null && !var9.isBlank() ? var9 : "saved";
      this.mode = var10;
      this.selection = var12;
      this.enabled = var14;
   }

   public String handle() {
      return this.instance;
   }

   public String process() {
      return this.data;
   }

   public void handle(String var1) {
      if (var1 != null && !var1.isBlank()) {
         this.data = var1;
      }
   }

   public String compute() {
      return this.context;
   }

   public void process(String var1) {
      this.context = var1;
   }

   public String resolve() {
      return this.config;
   }

   public void compute(String var1) {
      this.config = var1;
   }

   public String update() {
      return this.state;
   }

   public void resolve(String var1) {
      this.state = var1 == null ? "" : var1;
   }

   public String apply() {
      return this.cache;
   }

   public void update(String var1) {
      this.cache = var1 == null ? "" : var1;
   }

   public String execute() {
      return this.output;
   }

   public void apply(String var1) {
      this.output = var1 != null && !var1.isBlank() ? var1 : "Custom";
   }

   public String prepare() {
      return this.current;
   }

   public void execute(String var1) {
      this.current = var1 != null && !var1.isBlank() ? var1 : "user";
   }

   public String check() {
      return this.active;
   }

   public void prepare(String var1) {
      this.active = var1 != null && !var1.isBlank() ? var1 : "saved";
   }

   public long onTick() {
      return this.mode;
   }

   public void handle(long var1) {
      this.mode = var1;
   }

   public long select() {
      return this.selection;
   }

   public void process(long var1) {
      this.selection = var1;
   }

   public boolean refresh() {
      return this.enabled;
   }

   public void handle(boolean var1) {
      this.enabled = var1;
   }
}
