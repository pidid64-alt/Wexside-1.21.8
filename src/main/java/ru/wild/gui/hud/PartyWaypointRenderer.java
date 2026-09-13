package ru.wild.gui.hud;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.util.Identifier;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.SdfTextRenderer;
import ru.wild.render.texture.SvgHelper;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class PartyWaypointRenderer {
   private static final MinecraftClient data = MinecraftClient.getInstance();
   public static final Identifier instance = Identifier.of("wild", "svg/waypoint.svg");
   private static final float context = 2.0F;
   private static final float config = 11.0F;
   private static final float state = 6.0F;
   private static final float cache = 7.0F;
   private static final float output = 6.0F;
   private static final float current = 2.4F;
   private static final float active = 8.0F;
   private static final float mode = 6.0F;
   private static final float selection = 2.0F;
   private static final float enabled = 4.0F;
   private static final float renderer = 5.0F;
   private static final float handler = 8.0F;
   private static final float animationDraw = 2.0F;
   private static final float pointEncode = 23.0F;
   private static final Map<String, Identifier> animator = new ConcurrentHashMap<>();
   private float source;
   private float target;
   private float pending;
   private float previous;
   private float latest;
   private float summary;
   private float matrixBlend;
   private float vectorMatch;
   private float itemProject;
   private float responseCompute;
   private float providerFetch;
   private float profileDraw;
   private float vectorPerform;

   public static void handle(RoundedRectRenderer var0) {
      var0.handle(23.0F);
   }

   public float handle(String var1, String var2, String var3) {
      this.process(var1, var2, var3);
      return this.latest;
   }

   public void handle(RoundedRectRenderer var1, float var2, float var3, String var4, String var5, String var6, String var7, float var8, float var9) {
      float var10 = handle(var8);
      if (!(var10 <= 0.004F)) {
         this.process(var4, var5, var6);
         float var11 = 0.9F + 0.1F * var10;
         float var12 = this.previous * var11;
         float var13 = this.latest * var11;
         float var14 = var2 - var12 * 0.5F;
         float var15 = var3 + (1.0F - var10) * 6.0F * this.source - var13 * 0.5F;
         this.handle(var1, var14, var15, var12, var13, var11, var10);
         var1.handle(var14, var15);

         try {
            var1.process(var11, var11);

            try {
               this.handle(var1, var4, var5, var6, var7, var10, handle(var9));
            } finally {
               var1.check();
            }
         } finally {
            var1.prepare();
         }
      }
   }

   private void process(String var1, String var2, String var3) {
      this.source = handle();
      this.summary = 14.0F * this.source;
      this.matrixBlend = 12.0F * this.source;
      this.target = 11.0F * this.source;
      this.pending = this.target + 6.0F * this.source;
      SdfTextRenderer.State var4 = RoundedRectRenderer.handle(FontRegistry.instance, var1, this.summary);
      SdfTextRenderer.State var5 = RoundedRectRenderer.handle(FontRegistry.instance, var3, this.matrixBlend);
      SdfTextRenderer.State var6 = RoundedRectRenderer.handle(FontRegistry.instance, var2, this.matrixBlend);
      this.vectorMatch = var4.instance;
      this.responseCompute = var4.data;
      this.itemProject = var5.instance;
      this.providerFetch = var6.data;
      float var7 = 15.4F * this.source;
      this.latest = Math.max(this.target, var7) + 8.0F * this.source;
      float var8 = Math.max(this.vectorMatch + 8.0F * this.source + this.itemProject, var6.instance);
      this.previous = this.pending + 2.0F * this.source + var8 + 2.0F * this.source;
      float var9 = (this.latest - var7) * 0.5F;
      this.profileDraw = var9 + 7.0F * this.source * 0.5F;
      this.vectorPerform = var9 + 9.4F * this.source + 6.0F * this.source * 0.5F;
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = 6.0F * this.source * var6;
      var1.handle(var2, var3, var4, var5, var8, var7);
      var1.handle(var2, var3, var4, var5, var8, PackedColor.compute(22, 22, 25, (int)(133.0F * var7)));
      var1.handle(var2, var3, this.pending * var6, var5, var8, 0.0F, 0.0F, var8, PackedColor.compute(50, 48, 46, (int)(50.0F * var7)));
      var1.handle(var2, var3, var4, var5, var8, PackedColor.compute(255, 255, 255, (int)(56.0F * var7)), Math.max(1.0F, this.source));
   }

   private void handle(RoundedRectRenderer var1, String var2, String var3, String var4, String var5, float var6, float var7) {
      int var8 = PackedColor.update(RoundedRectRenderer.ColorState.apply(1, 1), (int)(255.0F * var6));
      float var9 = 0.4F + 0.6F * var7;
      this.handle(var1, var5, PackedColor.update(var8, (int)(255.0F * var6 * var9)), var6);
      var1.handle(
         FontRegistry.instance,
         this.pending + 4.0F * this.source,
         process(this.profileDraw - 3.0F, this.responseCompute),
         this.summary,
         var2,
         PackedColor.compute(240, 240, 244, (int)(255.0F * var6))
      );
      var1.handle(
         FontRegistry.instance,
         this.previous - 5.0F * this.source - this.itemProject,
         process(this.profileDraw - 3.0F, this.providerFetch),
         this.matrixBlend,
         var4,
         PackedColor.update(var8, (int)(255.0F * var6 * handle(var7, 0.35F)))
      );
      var1.handle(
         FontRegistry.instance,
         this.pending + 4.0F * this.source,
         process(this.vectorPerform - 3.0F, this.providerFetch),
         this.matrixBlend,
         var3,
         PackedColor.compute(168, 170, 178, (int)(255.0F * var6 * handle(var7, 0.18F)))
      );
   }

   private void handle(RoundedRectRenderer var1, String var2, int var3, float var4) {
      float var5 = (this.pending - this.target) * 0.55F;
      float var6 = (this.latest - this.target) * 0.5F;
      if (var2 == null || var2.isEmpty() || !this.handle(var1, var2, var5, var6, var4)) {
         int var7 = SvgHelper.handle(instance, SvgHelper.handle(11.0F, this.source * 2.0F), true);
         if (var7 > 0) {
            GlStateManager._bindTexture(var7);
            var1.handle(var7, var5, var6, this.target, this.target, var3, false);
         } else {
            float var8 = this.target * 2.0F;
            SdfTextRenderer.State var9 = RoundedRectRenderer.handle(FontRegistry.output, "B", var8);
            var1.handle(FontRegistry.output, var5 + (this.target - var9.instance) * 0.5F, var6 + (this.target + var9.data) * 0.5F, var8, "B", var3);
         }
      }
   }

   private boolean handle(RoundedRectRenderer var1, String var2, float var3, float var4, float var5) {
      try {
         Identifier var6 = handle(var2);
         if (var6 == null) {
            return false;
         }

         AbstractTexture var7 = data.getTextureManager().getTexture(var6);
         if (var7 != null && var7.getGlTexture() instanceof GlTexture var8) {
            int var16 = var8.getGlId();
            if (var16 <= 0) {
               return false;
            }

            float var10 = 2.0F * this.source;
            GlStateManager._bindTexture(var16);
            var1.update(var5);

            try {
               var1.handle(var16, var3, var4, this.target, this.target, 0.125F, 0.125F, 0.25F, 0.25F, var10);
               var1.handle(var16, var3, var4, this.target, this.target, 0.625F, 0.125F, 0.75F, 0.25F, var10);
            } finally {
               var1.onTick();
            }

            return true;
         } else {
            return false;
         }
      } catch (Throwable var15) {
         return false;
      }
   }

   private static Identifier handle(String var0) {
      String var1 = var0.toLowerCase(Locale.ROOT);
      Identifier var2 = animator.get(var1);
      if (var2 != null) {
         return var2;
      }

      Identifier var3 = process(var0);
      if (var3 != null) {
         animator.put(var1, var3);
      }

      return var3;
   }

   private static Identifier process(String var0) {
      if (data.getNetworkHandler() != null) {
         for (PlayerListEntry var2 : data.getNetworkHandler().getPlayerList()) {
            if (var2.getProfile().getName().equalsIgnoreCase(var0)) {
               return var2.getSkinTextures().texture();
            }
         }
      }

      if (data.getSkinProvider() == null) {
         return null;
      }

      GameProfile var3 = new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0).getBytes()), var0);
      return data.getSkinProvider().getSkinTextures(var3).texture();
   }

   private static float handle(float var0, float var1) {
      return handle((var0 - var1) / (1.0F - var1));
   }

   private static float process(float var0, float var1) {
      return var0 + var1 * 0.5F;
   }

   private static float handle() {
      if (data != null && data.getWindow() != null) {
         float var0 = data.getWindow().getScaleFactor();
         return var0 <= 0.0F ? 2.0F : var0;
      } else {
         return 2.0F;
      }
   }

   private static float handle(float var0) {
      return var0 < 0.0F ? 0.0F : Math.min(var0, 1.0F);
   }
}
