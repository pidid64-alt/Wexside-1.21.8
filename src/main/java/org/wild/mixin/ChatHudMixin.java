package org.wild.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.RunCommand;
import net.minecraft.text.HoverEvent.ShowText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.wild.mixin.acceser.ChatHudAccessor;
import ru.wild.WildClient;
import ru.wild.modules.misc.ChatHelper;
import ru.wild.modules.misc.UnHook;
import ru.wild.modules.visuals.ProtectInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
   private static boolean litka$updating;
   private static String litka$lastMessageKey;
   private static int litka$lastMessageCount;
   String currentPrefix = WildClient.instance.fetchProvider();
   private static final Pattern GENERAL_COORD_PATTERN = Pattern.compile("(-?\\d+)[\\s,]+(-?\\d+)[\\s,]+(-?\\d+)");

   @ModifyVariable(
      method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
      at = @At("HEAD"),
      argsOnly = true
   )
   private Text litka$nameProtectAndExpand(Text var1) {
      if (!WildClient.prepare()) {
         return var1;
      }

      Text var2 = ProtectInfo.handle(var1);
      MutableText var3 = var2.copy();
      String var4 = var3.getString();
      Matcher var5 = GENERAL_COORD_PATTERN.matcher(var4);
      if (var5.find()) {
         String var6 = var5.group(1);
         String var7 = var5.group(3);
         Style var8 = var3.getStyle()
            .withClickEvent(new RunCommand(this.currentPrefix + "gps " + var6 + " " + var7))
            .withHoverEvent(new ShowText(Text.literal("§a[GPS] Нажми, чтобы поставить метку на " + var6 + ", " + var7)));
         var3.setStyle(var8);
      }

      if (WildClient.instance != null && WildClient.instance.data != null) {
         ChatHelper var11 = WildClient.instance.data.handle(ChatHelper.class);
         if (var11 != null && var11.enabled && ChatHelper.previous.compute() && var4.contains("[Подробнее]")) {
            ArrayList<Text> var12 = new ArrayList<>();
            this.litka$extractHoverText(var2, var12);

            for (Text var9 : var12) {
               String var10 = var9.getString();
               if (var10.contains("Причина:") || var10.contains("Окончание:") || var10.contains("[БАН]")) {
                  var3.append(Text.literal("\n").formatted(Formatting.RESET));
                  var3.append(var9);
               }
            }
         }
      }

      return var3;
   }

   @Inject(method = "getMessageHistory", at = @At("RETURN"))
   private void litka$cleanHistoryOnUnhook(CallbackInfoReturnable<Object> var1) {
      if (WildClient.prepare()) {
         if (UnHook.target && var1.getReturnValue() instanceof Collection var3) {
            String var4 = WildClient.instance.fetchProvider();
            var3.removeIf(var1x -> !(var1x instanceof String var2) ? false : var2.startsWith(var4) || var2.startsWith("#"));
         }
      }
   }

   @Inject(method = "addToMessageHistory", at = @At("HEAD"), cancellable = true)
   private void litka$blockHistoryWhenUnhooked(String var1, CallbackInfo var2) {
      if (WildClient.prepare()) {
         if (UnHook.target && var1 != null && (var1.startsWith(WildClient.instance.fetchProvider()) || var1.startsWith("#"))) {
            var2.cancel();
         }
      }
   }

   private void litka$extractHoverText(Text var1, List<Text> var2) {
      Style var3 = var1.getStyle();
      if (var3 != null && var3.getHoverEvent() != null && var3.getHoverEvent() instanceof ShowText var5) {
         Text var6 = var5.value();
         if (var6 != null) {
            boolean var7 = var2.stream().anyMatch(var1x -> var1x.getString().equals(var6.getString()));
            if (!var7) {
               var2.add(var6);
            }
         }
      }

      for (Text var9 : var1.getSiblings()) {
         this.litka$extractHoverText(var9, var2);
      }
   }

   @Inject(
      method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void litka$mergeSpam(Text var1, MessageSignatureData var2, MessageIndicator var3, CallbackInfo var4) {
      if (WildClient.prepare()) {
         if (!litka$updating) {
            if (WildClient.instance != null && WildClient.instance.data != null) {
               ChatHelper var5 = WildClient.instance.data.handle(ChatHelper.class);
               if (var5 != null && var5.enabled && ChatHelper.source.compute()) {
                  String var6 = var1.getString();
                  if (var6 != null && !var6.isBlank()) {
                     if (var6.equals(litka$lastMessageKey)) {
                        litka$lastMessageCount++;
                        MutableText var7 = var1.copy().append(Text.literal(" [x" + litka$lastMessageCount + "]").formatted(Formatting.GRAY));
                        litka$updating = true;

                        try {
                           this.removeLastEntry();
                           ((ChatHud)(Object)this).addMessage(var7, var2, var3);
                        } finally {
                           litka$updating = false;
                        }

                        var4.cancel();
                     } else {
                        litka$lastMessageKey = var6;
                        litka$lastMessageCount = 1;
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
   private void litka$preserveChat(boolean var1, CallbackInfo var2) {
      if (WildClient.prepare()) {
         if (WildClient.instance != null && WildClient.instance.data != null) {
            ChatHelper var3 = WildClient.instance.data.handle(ChatHelper.class);
            if (var3 != null && var3.enabled && ChatHelper.target.compute()) {
               var2.cancel();
            } else {
               litka$lastMessageKey = null;
               litka$lastMessageCount = 0;
            }
         }
      }
   }

   private void removeLastEntry() {
      ChatHudAccessor var1 = (ChatHudAccessor)this;
      List var2 = var1.litka$getMessages();
      if (!var2.isEmpty()) {
         var2.remove(0);
      }

      List var3 = var1.litka$getVisibleMessages();
      Visible var4;
      if (!var3.isEmpty()) {
         do {
            var4 = (Visible)var3.remove(0);
         } while (!var3.isEmpty() && !var4.endOfEntry());
      }
   }
}
