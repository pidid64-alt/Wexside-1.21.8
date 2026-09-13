package ru.wild.modules.visuals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "VisibleEating", description = "Показывает еду и зелья, которые используют игроки", category = ModuleCategory.Visuals)
public class VisibleEating extends Module {
   public static final NumberSetting source = new NumberSetting("Размер", 24.0F, 16.0F, 42.0F, 1.0F, false);
   public static final NumberSetting target = new NumberSetting("Дистанция", 64.0F, 8.0F, 160.0F, 2.0F, false);
   public static final NumberSetting pending = new NumberSetting("Сдвиг по Y", -28.0F, -120.0F, 120.0F, 1.0F, false);
   public static final BooleanSetting previous = new BooleanSetting("Только видимые", false);
   public static final BooleanSetting latest = new BooleanSetting("Показывать себя", false);
   public static final BooleanSetting summary = new BooleanSetting("Фон", true);
   private static final float matrixBlend = 0.42F;
   private static final float vectorMatch = 6.0F;
   private static final float itemProject = 0.52F;
   private final ThemePresets responseCompute = new ThemePresets() {};
   private final Map<UUID, VisibleEating.State> providerFetch = new HashMap<>();

   public VisibleEating() {
      this.handle(source, target, pending, previous, latest, summary);
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (Module.client.world != null && Module.client.player != null) {
         RoundedRectRenderer var2 = var1.resolve();
         float var3 = Module.client.getRenderTickCounter().getTickProgress(true);
         int var4 = var1.apply();
         int var5 = var1.execute();
         HashSet var6 = new HashSet();

         for (PlayerEntity var8 : Module.client.world.getPlayers()) {
            if (var8 != null) {
               var6.add(var8.getUuid());
               boolean var9 = this.handle(var8) && var8.isUsingItem();
               ItemStack var10 = var9 ? var8.getActiveItem() : ItemStack.EMPTY;
               boolean var11 = var9 && !var10.isEmpty() && (var10.getUseAction() == UseAction.EAT || var10.getUseAction() == UseAction.DRINK);
               VisibleEating.State var12 = this.providerFetch.computeIfAbsent(var8.getUuid(), var0 -> new VisibleEating.State());
               var12.state = var11;
               if (var11) {
                  var12.context = var10.copy();
                  var12.config = this.handle(var8, var10);
               } else {
                  var12.config = 0.0F;
               }
            }
         }

         this.providerFetch.entrySet().removeIf(var1x -> {
            VisibleEating.State var2x = var1x.getValue();
            if (!var6.contains(var1x.getKey())) {
               var2x.state = false;
               var2x.context = null;
            }

            return var2x.instance.select() <= 0.005F && !var2x.state;
         });
         this.handle(var2, var3, var4, var5);
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, int var3, int var4) {
      if (!this.providerFetch.isEmpty()) {
         ThemeColors var5 = ThemeColors.handle(this.refresh(), ThemeShaderApplier.resolve());
         int var6 = var5.save();
         int var7 = PackedColor.compute(255, 255, 255, 42);
         float var8 = source.compute();
         float var9 = var8 * 0.52F;
         float var10 = ItemStackOverlayRenderer.compute(var9 / 16.0F);
         float var11 = var8 * 0.52F;
         float var12 = Math.max(2.0F, var8 * 0.11F);

         for (Entry var14 : this.providerFetch.entrySet()) {
            VisibleEating.State var15 = (VisibleEating.State)var14.getValue();
            var15.instance.handle();
            var15.instance.handle(var15.state ? 1.0 : 0.0, 0.25, EasingFunctions.pending);
            if (!(var15.instance.select() <= 0.005F) && var15.context != null && !var15.context.isEmpty()) {
               PlayerEntity var16 = Module.client.world.getPlayerByUuid((UUID)var14.getKey());
               if (var16 != null) {
                  Vec3d var17 = var16.getLerpedPos(var2).add(0.0, var16.getHeight() + 0.42F, 0.0);
                  Vec3d var18 = ClientMathUtil.handle(var17);
                  if (!(var18.z <= 0.001F) && !(var18.z > 1.0)) {
                     float var19 = (float)var18.x;
                     float var20 = (float)var18.y + pending.compute();
                     if (!(var19 < -6.0F) && !(var19 > var3 + 6.0F) && !(var20 < -6.0F) && !(var20 > var4 + 6.0F)) {
                        float var21 = (float)var15.instance.select();
                        var15.data.handle();
                        var15.data.handle(var15.config, 0.35F, EasingFunctions.pending);
                        float var22 = Math.max(0.01F, (float)var15.data.select());
                        if (summary.compute()) {
                           float var23 = var12 + 5.0F;
                           float var24 = (var11 + var23) * 2.0F;
                           float var25 = var19 - var24 * 0.5F;
                           float var26 = var20 - var24 * 0.5F;
                           this.responseCompute.handle(var1, var25, var26, var24, var24, var24 * 0.32F, var21);
                        }

                        var1.handle(var19, var20, var11, 0.0F, 1.0F, var12, var7);
                        var1.handle(var19, var20, var11, 90.0F, var22, var12, ThemeColors.handle(var6, (int)(235.0F * var21)));
                        ItemStackOverlayRenderer.handle(var1, var15.context, var19 - var9 * 0.5F, var20 - var9 * 0.5F, var10, 0, false, 0);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean handle(PlayerEntity var1) {
      if (var1 == null || !var1.isAlive() || var1.isSpectator()) {
         return false;
      } else if (var1 == Module.client.player && !latest.compute()) {
         return false;
      } else {
         return var1.squaredDistanceTo(Module.client.player) > target.compute() * target.compute() ? false : !previous.compute() || Module.client.player.canSee(var1);
      }
   }

   private float handle(PlayerEntity var1, ItemStack var2) {
      if (var2 != null && !var2.isEmpty()) {
         int var3 = var2.getMaxUseTime(var1);
         return var3 <= 0 ? 0.0F : MathHelper.clamp((float)var1.getItemUseTime() / var3, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   private ThemePalette refresh() {
      return WildClient.instance != null && WildClient.instance.selection != null && WildClient.instance.selection.process() != null
         ? WildClient.instance.selection.process()
         : ThemePalette.WILD;
   }

   @Override
   public void process() {
      this.providerFetch.clear();
      super.process();
   }

   static class State {
      final EasedDoubleAnimator instance = new EasedDoubleAnimator();
      final EasedDoubleAnimator data = new EasedDoubleAnimator();
      ItemStack context;
      float config;
      boolean state;
   }
}
