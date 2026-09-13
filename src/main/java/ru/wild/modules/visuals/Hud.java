package ru.wild.modules.visuals;

import java.util.ArrayList;
import net.minecraft.client.gui.DrawContext;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.TargetSelectorRegistry;
import ru.wild.config.HudProfileConfig;
import ru.wild.config.HudProfileGate;
import ru.wild.gui.hud.AiStatusHudRenderer;
import ru.wild.gui.hud.ArmorHudRenderer;
import ru.wild.gui.hud.BrewingStatusHudRenderer;
import ru.wild.gui.hud.CooldownHud;
import ru.wild.gui.hud.HotbarHud;
import ru.wild.gui.hud.HudElementRenderer;
import ru.wild.gui.hud.InformationHudRenderer;
import ru.wild.gui.hud.KeybindHud;
import ru.wild.gui.hud.ModuleListHudRenderer;
import ru.wild.gui.hud.MusicPlayerHudRenderer;
import ru.wild.gui.hud.NotificationHudSettings;
import ru.wild.gui.hud.PotionHudRenderer;
import ru.wild.gui.hud.ServerItemBindingsHud;
import ru.wild.gui.hud.SlotHudRenderer;
import ru.wild.gui.hud.StatusHudRenderer;
import ru.wild.gui.hud.TargetHudRenderer;
import ru.wild.gui.hud.WaterMarkRenderer;
import ru.wild.gui.screen.HudEditorScreen;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "Hud", description = "Интерфейс клиента", category = ModuleCategory.Visuals)
public class Hud extends Module implements HudElementRenderer {
   private static final Hud.State[] vectorMatch = new Hud.State[32];
   private static int itemProject;
   private static int responseCompute;
   private static int providerFetch;
   public static final ChoiceSetting source = new ChoiceSetting("Elements", tick());
   public static final String target = "Client";
   public static final String pending = "Custom";
   public static final ChoiceSetting previous = source;
   public static final ModeSetting latest = new ModeSetting("HUD Mode", "Client", "Client", "Custom").handle(() -> !HudProfileGate.handle());
   public static final ActionSetting summary = new ActionSetting("HUD Constructor", 0)
      .process("Open")
      .handle(() -> !HudProfileGate.handle() || !render())
      .handle(Hud::drawAnimation);
   public static final ShaderPresetSetting matrixBlend = new ShaderPresetSetting("Foundry Shader", LivePreviewRenderer.HUD);

   public Hud() {
      this.handle(source, latest, summary, matrixBlend);
   }

   private static BooleanSetting[] tick() {
      ArrayList<BooleanSetting> var0 = new ArrayList<>();
      var0.add(new BooleanSetting("Watermark", true));
      var0.add(new BooleanSetting("ArrayList", true));
      var0.add(new BooleanSetting("HotKeys", true));
      var0.add(new BooleanSetting("Potions", true));
      var0.add(new BooleanSetting("Cool Downs", true));
      var0.add(new BooleanSetting("TargetHud", true));
      var0.add(new BooleanSetting("Armor", true));
      var0.add(new BooleanSetting("Inventory", true));
      var0.add(new BooleanSetting("PlayerInfo", true));
      if (HudProfileConfig.handle(StatusHudRenderer.class)) {
         var0.add(new BooleanSetting("AutoBuy Info", true));
      }

      var0.add(new BooleanSetting("Notifications", true));
      if (HudProfileConfig.handle(AiStatusHudRenderer.class)) {
         var0.add(new BooleanSetting("AI Status", true));
      }

      var0.add(new BooleanSetting("Brew Monitor", true));
      var0.add(new BooleanSetting("HotBar", false));
      var0.add(new BooleanSetting("MediaPlayer", true));
      var0.add(new BooleanSetting("Server Helper", false));
      return var0.toArray(BooleanSetting[]::new);
   }

   @Override
   public void handle() {
      super.handle();
      TargetSelectorRegistry.handle().handle(this, this);
   }

   @Override
   public void process() {
      TargetSelectorRegistry.handle().handle(this);
      super.process();
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      TargetSelectorRegistry.handle().process(this, this);
      if (Module.client.player != null && Module.client.world != null) {
         DrawContext var2 = var1.prepare();
         RoundedRectRenderer var3 = var1.resolve();
         if (var3 != null) {
            handle(var1.apply(), var1.execute());
            if (source.process("Notifications")) {
               NotificationHudSettings.encodePoint();
               NotificationHudSettings.handle(var3);
            }

            if (HudProfileConfig.handle(AiStatusHudRenderer.class) && source.process("AI Status")) {
               AiStatusHudRenderer.handle(var3);
            }

            if (source.process("Brew Monitor")) {
               BrewingStatusHudRenderer.handle(var3);
            }

            if (source.process("Watermark")) {
               WaterMarkRenderer.handle(var3);
            }

            if (source.process("ArrayList")) {
               ModuleListHudRenderer.handle(var3);
            }

            if (source.process("PlayerInfo")) {
               InformationHudRenderer.handle(var3);
            }

            if (HudProfileConfig.handle(StatusHudRenderer.class) && source.process("AutoBuy Info")) {
               StatusHudRenderer.handle(var3);
            }

            if (source.process("TargetHud")) {
               TargetHudRenderer.handle(var3, var1.prepare());
            }

            if (source.process("Potions")) {
               PotionHudRenderer.handle(var3, var1.prepare());
            }

            if (source.process("Cool Downs")) {
               CooldownHud.handle(var3, var2);
            }

            if (source.process("Armor")) {
               ArmorHudRenderer.handle(var3, var1.prepare());
            }

            if (source.process("HotKeys")) {
               KeybindHud.handle(var3);
            }

            if (source.process("Inventory")) {
               SlotHudRenderer.handle(var3, var1.prepare());
            }

            if (source.process("HotBar")) {
               HotbarHud.handle(var3, var1.prepare());
            }

            if (source.process("MediaPlayer")) {
               MusicPlayerHudRenderer.handle(var3);
            }

            if (source.process("Server Helper")) {
               ServerItemBindingsHud.handle(var3, var2);
            }
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      CooldownHud.handle(var1);
      PotionHudRenderer.handle(var1);
   }

   public static String refresh() {
      String var0 = matrixBlend.refresh();
      return var0 == null ? "" : var0;
   }

   public static boolean render() {
      return HudProfileGate.handle() && "Custom".equals(latest.compute());
   }

   private static void drawAnimation() {
      if (Module.client != null && HudProfileGate.handle()) {
         Module.client.execute(() -> Module.client.setScreen(new HudEditorScreen()));
      }
   }

   @Override
   public LivePreviewRenderer compute() {
      return LivePreviewRenderer.HUD;
   }

   @Override
   public String resolve() {
      String var1 = refresh();
      return var1 != null && !var1.isBlank() ? var1 : null;
   }

   @Override
   public boolean update() {
      return true;
   }

   public static void handle(int var0, int var1) {
      responseCompute = Math.max(1, var0);
      providerFetch = Math.max(1, var1);
      itemProject = 0;
   }

   public static void handle(String var0, float var1, float var2, float var3, float var4) {
      if (!(var3 <= 0.0F) && !(var4 <= 0.0F) && Float.isFinite(var1) && Float.isFinite(var2)) {
         if (itemProject < vectorMatch.length) {
            Hud.State var5 = vectorMatch[itemProject];
            if (var5 == null) {
               var5 = new Hud.State();
               vectorMatch[itemProject] = var5;
            }

            var5.handle(var0, var1, var2, var3, var4);
            itemProject++;
         }
      }
   }

   public static void handle(String var0, RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      if (var1 == null) {
         handle(var0, var2, var3, var4, var5);
      } else {
         float[] var6 = var1.select().update();
         if (var6 != null && var6.length >= 6) {
            float var7 = var2;
            float var8 = var3;
            float var9 = var2 + var4;
            float var10 = var3 + var5;
            float var11 = var6[0] * var7 + var6[1] * var8 + var6[2];
            float var12 = var6[3] * var7 + var6[4] * var8 + var6[5];
            float var13 = var6[0] * var9 + var6[1] * var8 + var6[2];
            float var14 = var6[3] * var9 + var6[4] * var8 + var6[5];
            float var15 = var6[0] * var9 + var6[1] * var10 + var6[2];
            float var16 = var6[3] * var9 + var6[4] * var10 + var6[5];
            float var17 = var6[0] * var7 + var6[1] * var10 + var6[2];
            float var18 = var6[3] * var7 + var6[4] * var10 + var6[5];
            float var19 = Math.min(Math.min(var11, var13), Math.min(var15, var17));
            float var20 = Math.min(Math.min(var12, var14), Math.min(var16, var18));
            float var21 = Math.max(Math.max(var11, var13), Math.max(var15, var17));
            float var22 = Math.max(Math.max(var12, var14), Math.max(var16, var18));
            handle(var0, var19, var20, var21 - var19, var22 - var20);
         } else {
            handle(var0, var2, var3, var4, var5);
         }
      }
   }

   public static Hud.State handle(String var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = handle(var1, 0.0F, Math.max(0.0F, responseCompute - var3));
      float var7 = handle(var2, 0.0F, Math.max(0.0F, providerFetch - var4));

      for (int var8 = 0; var8 < 6; var8++) {
         boolean var9 = false;

         for (int var10 = 0; var10 < itemProject; var10++) {
            Hud.State var11 = vectorMatch[var10];
            if (var11 != null
               && !var0.equals(var11.instance)
               && handle(var6, var7, var3, var4, var11.data - var5, var11.context - var5, var11.config + var5 * 2.0F, var11.state + var5 * 2.0F)) {
               float var12 = var11.context - var4 - var5;
               float var13 = var11.context + var11.state + var5;
               float var14 = var11.data - var3 - var5;
               float var15 = var11.data + var11.config + var5;
               float var16 = var6;
               float var17 = var7;
               float var18 = Float.MAX_VALUE;
               float var19 = handle(var6, var12, var1, var2, var3, var4);
               if (var12 >= 0.0F && var19 < var18) {
                  var18 = var19;
                  var17 = var12;
                  var16 = var6;
               }

               float var20 = handle(var6, var13, var1, var2, var3, var4);
               if (var13 + var4 <= providerFetch && var20 < var18) {
                  var18 = var20;
                  var17 = var13;
                  var16 = var6;
               }

               float var21 = handle(var14, var7, var1, var2, var3, var4);
               if (var14 >= 0.0F && var21 < var18) {
                  var18 = var21;
                  var16 = var14;
                  var17 = var7;
               }

               float var22 = handle(var15, var7, var1, var2, var3, var4);
               if (var15 + var3 <= responseCompute && var22 < var18) {
                  var16 = var15;
                  var17 = var7;
               }

               var6 = handle(var16, 0.0F, Math.max(0.0F, responseCompute - var3));
               var7 = handle(var17, 0.0F, Math.max(0.0F, providerFetch - var4));
               var9 = true;
            }
         }

         if (!var9) {
            break;
         }
      }

      return new Hud.State(var0, var6, var7, var3, var4);
   }

   private static float handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      if (Float.isFinite(var0) && Float.isFinite(var1)) {
         float var6 = var0 - var2;
         float var7 = var1 - var3;
         float var8 = Math.abs(var0 + var4 * 0.5F - responseCompute * 0.5F) * 0.012F;
         float var9 = Math.abs(var1 + var5 - providerFetch) * 0.004F;
         return var6 * var6 + var7 * var7 + var8 + var9;
      } else {
         return Float.MAX_VALUE;
      }
   }

   private static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      return var0 < var4 + var6 && var0 + var2 > var4 && var1 < var5 + var7 && var1 + var3 > var5;
   }

   private static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   public static final class State {
      public String instance;
      public float data;
      public float context;
      public float config;
      public float state;

      public State() {
      }

      public State(String var1, float var2, float var3, float var4, float var5) {
         this.handle(var1, var2, var3, var4, var5);
      }

      public void handle(String var1, float var2, float var3, float var4, float var5) {
         this.instance = var1 == null ? "" : var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
      }
   }
}
