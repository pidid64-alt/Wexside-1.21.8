package ru.wild.gui.theme;

public enum LivePreviewRenderer {
   PREVIEW_ONLY("preview", "Preview", "live editor preview", false, false, false, false, false),
   HUD("hud", "HUD", "screen-space HUD shader", false, true, false, false, true),
   MODULE_CARD("module_card", "Module Card", "module row surface and hover body", false, true, false, false, true),
   PANEL_BACKGROUND("panel_background", "Panel Background", "dock and settings panel surface", true, false, false, false, true),
   AUDIT_PANEL("audit_panel", "Audit Panel", "verification and diagnostics panel surface", true, true, false, false, false),
   BUTTON("button", "Button", "interactive button surface", false, true, false, false, true),
   HEALTH_BAR("health_bar", "Health Bar", "bar fill and shield style shader", false, true, false, false, true),
   ESP("esp", "ESP", "entity silhouette shader", false, false, true, false, true),
   CHAMS("chams", "Chams", "model-space entity material overlay", false, false, true, true, true),
   SKY("sky", "Sky", "world sky and atmospheric pass", false, false, false, false, true),
   NAMETAG("nametag", "Nametag", "billboard nametag surface", false, true, false, true, true),
   TRAILS("trails", "Trails", "motion trail ribbon material", false, false, true, true, true),
   BACKGROUND("background", "Background", "full-screen interface background", true, false, false, false, true),
   MENU_BACKGROUND("menu_bg", "Menu Background", "legacy full ClickGUI background", true, false, false, false, false),
   MENU_PANEL_BG("menu_panel", "Panel Background", "legacy panel surface", true, false, false, false, false),
   HUD_OVERLAY("hud_overlay", "HUD Overlay", "legacy HUD overlay", false, true, false, false, false),
   ESP_OVERLAY("esp_overlay", "ESP Overlay", "legacy ESP fill", false, false, true, false, false),
   ENTITY_HIGHLIGHT("entity_highlight", "Entity Highlight", "legacy entity highlight", false, false, false, true, false);

   private final String instance;
   private final String data;
   private final String context;
   private final boolean config;
   private final boolean state;
   private final boolean cache;
   private final boolean output;
   private final boolean current;

   LivePreviewRenderer(String var3, String var4, String var5, boolean var6, boolean var7, boolean var8, boolean var9, boolean var10) {
      this.instance = var3;
      this.data = var4;
      this.context = var5;
      this.config = var6;
      this.state = var7;
      this.cache = var8;
      this.output = var9;
      this.current = var10;
   }

   public String handle() {
      return this.instance;
   }

   public String process() {
      return this.data;
   }

   public String compute() {
      return this.context;
   }

   public LivePreviewRenderer resolve() {
      return switch (this) {
         case PREVIEW_ONLY -> PREVIEW_ONLY;
         case HUD, MODULE_CARD, PANEL_BACKGROUND, AUDIT_PANEL, BUTTON, HEALTH_BAR, NAMETAG, MENU_PANEL_BG, HUD_OVERLAY -> HUD;
         case ESP, CHAMS, TRAILS, ESP_OVERLAY, ENTITY_HIGHLIGHT -> ESP;
         case SKY, BACKGROUND, MENU_BACKGROUND -> BACKGROUND;
      };
   }

   public String update() {
      return switch (this) {
         case PREVIEW_ONLY -> "System";
         case HUD, MODULE_CARD, BUTTON, HEALTH_BAR, HUD_OVERLAY -> "HUD";
         case PANEL_BACKGROUND, AUDIT_PANEL, BACKGROUND, MENU_BACKGROUND, MENU_PANEL_BG -> "Interface";
         case ESP, CHAMS, NAMETAG, TRAILS, ESP_OVERLAY, ENTITY_HIGHLIGHT -> "Entity";
         case SKY -> "World";
      };
   }

   public boolean apply() {
      return this.resolve() == HUD || this == PANEL_BACKGROUND || this == AUDIT_PANEL || this == MENU_PANEL_BG;
   }

   public boolean execute() {
      return this.current;
   }

   public boolean prepare() {
      return this.resolve() == ESP;
   }

   public boolean check() {
      if (this.resolve() == BACKGROUND) {
         if (this == BACKGROUND) {
            return true;
         }

         if (this == MENU_BACKGROUND) {
            return true;
         }
      }

      return false;
   }

   public boolean onTick() {
      return this.config && (this == MENU_PANEL_BG || this == PANEL_BACKGROUND || this == AUDIT_PANEL);
   }

   public boolean select() {
      return this.resolve() == HUD;
   }

   public boolean refresh() {
      return this.resolve() == ESP && !this.output;
   }

   public boolean render() {
      return this.output;
   }

   public static LivePreviewRenderer[] tick() {
      return new LivePreviewRenderer[]{HUD, BACKGROUND, ESP};
   }

   public static LivePreviewRenderer handle(String var0) {
      if (var0 == null) {
         return PREVIEW_ONLY;
      }

      String var1 = var0.trim();

      for (LivePreviewRenderer var5 : values()) {
         if (var5.instance.equals(var1) || var5.name().equalsIgnoreCase(var1)) {
            return var5;
         }
      }

      return PREVIEW_ONLY;
   }
}
