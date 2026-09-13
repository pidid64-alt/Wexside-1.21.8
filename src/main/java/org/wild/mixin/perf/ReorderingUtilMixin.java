package org.wild.mixin.perf;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.resource.language.ReorderingUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReorderingUtil.class)
public class ReorderingUtilMixin {
   private static final Map<StringVisitable, OrderedText> WILD$CACHE = new ConcurrentHashMap<>(2048);
   private static final int WILD$CACHE_LIMIT = 8192;

   @Inject(method = "reorder", at = @At("HEAD"), cancellable = true)
   private static void wild$skipBidi(StringVisitable var0, boolean var1, CallbackInfoReturnable<OrderedText> var2) {
      if (var0 == null) {
         var2.setReturnValue(OrderedText.EMPTY);
      } else {
         OrderedText var3 = WILD$CACHE.get(var0);
         if (var3 != null) {
            var2.setReturnValue(var3);
         } else {
            OrderedText var4 = wild$buildLtr(var0);
            if (WILD$CACHE.size() >= 8192) {
               WILD$CACHE.clear();
            }

            WILD$CACHE.put(var0, var4);
            var2.setReturnValue(var4);
         }
      }
   }

   private static OrderedText wild$buildLtr(StringVisitable var0) {
      ArrayList var1 = new ArrayList(4);
      var0.visit((var1x, var2) -> {
         if (!var2.isEmpty()) {
            var1.add(OrderedText.styledForwardsVisitedString(var2, var1x));
         }

         return Optional.empty();
      }, Style.EMPTY);
      if (var1.isEmpty()) {
         return OrderedText.EMPTY;
      } else {
         return var1.size() == 1 ? (OrderedText)var1.get(0) : OrderedText.concat(var1);
      }
   }
}
