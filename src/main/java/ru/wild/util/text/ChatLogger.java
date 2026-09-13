package ru.wild.util.text;

import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import ru.wild.WildClient;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.ThemePalette;

public final class ChatLogger implements MinecraftContext {
   public static void handle(String var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.inGameHud != null && var1.inGameHud.getChatHud() != null) {
         ThemePalette var2 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
         MutableText var3 = Text.literal("")
            .append(handle("Wild", var2))
            .append(Text.literal(" » ").formatted(Formatting.WHITE))
            .append(Text.literal(var0).formatted(Formatting.GRAY));
         var1.inGameHud.getChatHud().addMessage(var3);
      } else {
         System.out.println("[WILD Log] " + var0);
      }
   }

   public static void process(String var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.inGameHud != null && var1.inGameHud.getChatHud() != null) {
         ThemePalette var2 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
         MutableText var3 = Text.literal("")
            .append(handle("AI", var2))
            .append(Text.literal(" » ").formatted(Formatting.DARK_GRAY))
            .append(Text.literal(var0).formatted(Formatting.WHITE));
         var1.inGameHud.getChatHud().addMessage(var3);
      }
   }

   public static Text handle(String var0, ThemePalette var1) {
      MutableText var2 = Text.empty();
      int var3 = var0.length();
      Color var4 = var1.handle();
      Color var5 = var1.apply();
      long var6 = System.currentTimeMillis();

      for (int var8 = 0; var8 < var3; var8++) {
         float var9 = var8 * 0.15F + (float)var6 / 1500.0F;
         float var10 = (float)(Math.sin(var9) + 1.0) / 2.0F;
         int var11 = (int)(var4.getRed() * (1.0F - var10) + var5.getRed() * var10);
         int var12 = (int)(var4.getGreen() * (1.0F - var10) + var5.getGreen() * var10);
         int var13 = (int)(var4.getBlue() * (1.0F - var10) + var5.getBlue() * var10);
         TextColor var14 = TextColor.fromRgb(var11 << 16 | var12 << 8 | var13);
         MutableText var15 = Text.literal(String.valueOf(var0.charAt(var8))).setStyle(Style.EMPTY.withColor(var14));
         var2.append(var15);
      }

      return var2;
   }
   private ChatLogger() {
   }
}
