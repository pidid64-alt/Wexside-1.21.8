package org.wild.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Style;
import net.minecraft.text.ClickEvent.RunCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.render.GlCompatibilityProbe;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.ScreenRenderDiagnostics;
import ru.wild.render.shader.GuiRippleShader;
import ru.wild.render.shader.ScreenBackdropRenderer;

@Mixin(Screen.class)
public class ScreenMixin {
   @Unique
   private static final ThemePaletteRegistry wild$palette = ThemePaletteRegistry.handle();
   @Unique
   private boolean wild$guiRippleCapture;
   @Unique
   private static boolean wild$panoramaNoticeLogged;

   @Inject(method = "handleTextClick", at = @At("HEAD"), cancellable = true)
   private void litka$interceptClientCommands(Style var1, CallbackInfoReturnable<Boolean> var2) {
      if (WildClient.prepare()) {
         if (var1 != null && var1.getClickEvent() instanceof RunCommand var3) {
            String var5 = var3.command();
            if (var5 != null && var5.startsWith(WildClient.instance.fetchProvider())) {
               WildClient.instance.projectItem().handle(var5);
               var2.setReturnValue(true);
            }
         }
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void wild$diagRenderHead(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      RenderDiagnostics.handle().apply();
      Screen var6 = (Screen)(Object)this;
      if (!(var6 instanceof FrameRenderListener)) {
         GlCompatibilityProbe.handle(MinecraftClient.getInstance());
      }

      ScreenRenderDiagnostics.handle(var6, "render.head");
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void wild$diagRenderTail(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      RenderDiagnostics.handle().execute();
      ScreenRenderDiagnostics.handle((Screen)(Object)this, "render.tail");
   }

   @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
   private void wild$renderThemedVanillaBackdrop(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      Screen var6 = (Screen)(Object)this;
      if (!wild$usesThemedBackdrop(var6)) {
         ScreenRenderDiagnostics.handle(var6, "renderBackground.vanilla");
      } else {
         MinecraftClient var7 = MinecraftClient.getInstance();
         if (var7 != null && var7.getWindow() != null) {
            if (ScreenBackdropRenderer.handle().handle(var7, var2, var3, 1.0F, var6)) {
               ScreenRenderDiagnostics.handle(var6, "renderBackground.backdrop", "shader-backdrop");
               var5.cancel();
            } else {
               wild$drawThemedBackdrop(var1, var7.getWindow().getScaledWidth(), var7.getWindow().getScaledHeight());
               ScreenRenderDiagnostics.handle(var6, "renderBackground.backdrop", "gradient-fallback");
               var5.cancel();
            }
         } else {
            ScreenRenderDiagnostics.handle("renderBackground", var6, "client or window missing", null);
         }
      }
   }

   @WrapMethod(method = "renderPanoramaBackground")
   private void wild$guardPanorama(DrawContext var1, float var2, Operation<Void> var3) {
      try {
         var3.call(new Object[]{var1, var2});
      } catch (Throwable var6) {
         if (!wild$panoramaNoticeLogged) {
            wild$panoramaNoticeLogged = true;
            ScreenRenderDiagnostics.handle("renderPanoramaBackground", (Screen)(Object)this, "vanilla panorama failed -> themed backdrop", var6);
         }

         MinecraftClient var5 = MinecraftClient.getInstance();
         if (var5 != null && var5.getWindow() != null) {
            wild$drawThemedBackdrop(var1, var5.getWindow().getScaledWidth(), var5.getWindow().getScaledHeight());
         }
      }
   }

   @Inject(method = "renderWithTooltip", at = @At("HEAD"))
   private void wild$beginGuiRipplePass(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      Screen var6 = (Screen)(Object)this;
      MinecraftClient var7 = MinecraftClient.getInstance();
      if (var7 != null && var7.getWindow() != null) {
         GuiRippleShader var8 = GuiRippleShader.handle();
         this.wild$guiRippleCapture = var8.handle(var6) && var8.handle(var7.getWindow().getFramebufferWidth(), var7.getWindow().getFramebufferHeight());
         if (this.wild$guiRippleCapture) {
            ScreenRenderDiagnostics.handle(var6, "renderWithTooltip.ripple.begin");
         }
      } else {
         this.wild$guiRippleCapture = false;
      }
   }

   @Inject(method = "renderWithTooltip", at = @At("TAIL"))
   private void wild$endGuiRipplePass(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (!this.wild$guiRippleCapture) {
         ScreenRenderDiagnostics.handle((Screen)(Object)this, "renderWithTooltip.tail");
      } else {
         this.wild$guiRippleCapture = false;

         try {
            GuiRippleShader.handle().compute();
            ScreenRenderDiagnostics.handle((Screen)(Object)this, "renderWithTooltip.ripple.end");
         } catch (Throwable var7) {
            ScreenRenderDiagnostics.handle("gui-ripple", (Screen)(Object)this, "endPass failed", var7);
         }

         ScreenRenderDiagnostics.handle((Screen)(Object)this, "renderWithTooltip.tail");
      }
   }

   @Unique
   private static boolean wild$usesThemedBackdrop(Screen var0) {
      return var0 instanceof SelectWorldScreen
         || var0 instanceof CreateWorldScreen
         || var0 instanceof DownloadingTerrainScreen
         || var0 instanceof ProgressScreen;
   }

   @Unique
   private static void wild$drawThemedBackdrop(DrawContext var0, int var1, int var2) {
      if (WildClient.prepare()) {
         ThemePalette var3 = WildClient.instance != null && WildClient.instance.selection != null
            ? WildClient.instance.selection.process()
            : ThemePalette.AURORA;
         boolean var4 = wild$palette.compute(var3);
         int var5 = wild$palette.resolve(var3);
         int var6 = wild$palette.update(var3);
         int var7 = var4 ? wild$mix(-197121, var5, 0.055F) : wild$mix(-16447732, var5, 0.035F);
         int var8 = var4 ? wild$mix(-3853, var6, 0.09F) : wild$mix(-15658213, var6, 0.06F);
         var0.fillGradient(0, 0, var1, var2, var7, var8);
         int var9 = Math.max(5, Math.min(9, var2 / 72));

         for (int var10 = 0; var10 < var9; var10++) {
            float var11 = (var10 + 1.0F) / (var9 + 1.0F);
            int var12 = Math.round(var2 * var11 - var2 * 0.045F);
            int var13 = Math.max(18, Math.round(var2 * (var4 ? 0.045F : 0.06F)));
            int var14 = wild$withAlpha(wild$mix(var5, -1, var4 ? 0.78F : 0.15F), var4 ? 18 : 24);
            int var15 = wild$withAlpha(wild$mix(var6, -1, var4 ? 0.72F : 0.12F), 0);
            var0.fillGradient(0, Math.max(0, var12), var1, Math.min(var2, var12 + var13), var14, var15);
         }

         if (var4) {
            var0.fillGradient(0, 0, var1, Math.max(24, var2 / 8), 570425344, 0);
            var0.fillGradient(0, Math.max(0, var2 - var2 / 5), var1, var2, 0, 285212671);
         } else {
            var0.fillGradient(0, 0, var1, var2, 570425344, 1711276032);
         }
      }
   }

   @Unique
   private static int wild$withAlpha(int var0, int var1) {
      return (Math.max(0, Math.min(255, var1)) & 0xFF) << 24 | var0 & 16777215;
   }

   @Unique
   private static int wild$mix(int var0, int var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(1.0F, var2));
      int var4 = Math.round(wild$channel(var0, 24) + (wild$channel(var1, 24) - wild$channel(var0, 24)) * var3);
      int var5 = Math.round(wild$channel(var0, 16) + (wild$channel(var1, 16) - wild$channel(var0, 16)) * var3);
      int var6 = Math.round(wild$channel(var0, 8) + (wild$channel(var1, 8) - wild$channel(var0, 8)) * var3);
      int var7 = Math.round(wild$channel(var0, 0) + (wild$channel(var1, 0) - wild$channel(var0, 0)) * var3);
      return var4 << 24 | var5 << 16 | var6 << 8 | var7;
   }

   @Unique
   private static int wild$channel(int var0, int var1) {
      return var0 >> var1 & 0xFF;
   }
}
