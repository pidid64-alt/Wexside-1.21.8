package ru.wild.util.inventory;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.render.RoundedRectRenderer;

public final class ItemStackOverlayRenderer {
   private static final MinecraftClient instance = MinecraftClient.getInstance();
   private static final ItemRenderState data = new ItemRenderState();
   private static final Random context = Random.create(0L);
   private static final String[] config = process();
   private static final Set<Integer> state = ConcurrentHashMap.newKeySet();
   private static final Map<String, ItemStackOverlayRenderer.ColorStop> cache = new ConcurrentHashMap<>();
   private static final long output = 5000L;
   private static Field current;

   private ItemStackOverlayRenderer() {
   }

   public static void handle(DrawContext var0, ItemStack var1, float var2, float var3, float var4, int var5, boolean var6) {
      handle(var0, instance.player, var1, var2, var3, var4, var5, var6);
   }

   public static void handle(DrawContext var0, PlayerEntity var1, ItemStack var2, float var3, float var4, float var5, int var6, boolean var7) {
      Object var8 = var1 != null ? var1 : instance.player;
      if (var0 != null && var8 != null && var2 != null && !var2.isEmpty()) {
         float var9 = instance.getWindow().getScaleFactor();
         float var10 = handle(var3);
         float var11 = handle(var4);
         float var12 = compute(var5);
         var0.getMatrices().pushMatrix();
         var0.getMatrices().translate(var10 / var9, var11 / var9);
         var0.getMatrices().scale(var12 / var9, var12 / var9);
         var0.drawItem((LivingEntity)var8, var2, 0, 0, var6);
         if (var7) {
            var0.drawStackOverlay(instance.textRenderer, var2, 0, 0);
         }

         var0.getMatrices().popMatrix();
      }
   }

   public static void handle(RoundedRectRenderer var0, ItemStack var1, float var2, float var3, float var4, int var5, boolean var6, int var7) {
      if (var0 != null && instance.player != null && var1 != null && !var1.isEmpty()) {
         if (handle(var0, var1, var2, var3, var4)) {
            if (var6) {
               process(var0, var1, var2, var3, var4);
            }
         } else {
            try {
               data.clear();
               instance.getItemModelManager().clearAndUpdate(data, var1, ItemDisplayContext.GUI, instance.world, instance.player, var5);
               context.setSeed(var5);
               Sprite var8 = data.getParticleSprite(context);
               if (var8 == null) {
                  return;
               }

               AbstractTexture var9 = instance.getTextureManager().getTexture(var8.getAtlasId());
               if (var9 == null || !(var9.getGlTexture() instanceof GlTexture var10 && var10.getGlId() > 0)) {
                  return;
               }

               var0.handle(
                  var10.getGlId(),
                  var2,
                  var3,
                  16.0F * var4,
                  16.0F * var4,
                  var8.getMinU(),
                  var8.getMinV(),
                  var8.getMaxU(),
                  var8.getMaxV(),
                  Math.max(2.0F, 4.0F * var4)
               );
               handle(var0, var1, var8.getAtlasId(), var10, var2, var3, var4);
            } catch (Throwable var12) {
            }

            if (var6) {
               process(var0, var1, var2, var3, var4);
            }
         }
      }
   }

   public static int[] handle(ItemStack var0, int var1) {
      if (var0 != null && !var0.isEmpty() && instance.player != null) {
         long var2 = System.currentTimeMillis();
         String var4 = compute(var0, var1);
         ItemStackOverlayRenderer.ColorStop var5 = cache.get(var4);
         if (var5 != null && var2 - var5.createdAt <= 5000L) {
            return handle(var5.colors);
         }

         int[] var6 = handle(var0);
         if (var6 == null) {
            var6 = process(var0, var1);
         }

         cache.put(var4, new ItemStackOverlayRenderer.ColorStop(handle(var6), var2));
         return var6;
      } else {
         return null;
      }
   }

   public static int[] handle() {
      return new int[]{RoundedRectRenderer.ColorState.compute(220, 255, 245, 255), RoundedRectRenderer.ColorState.compute(142, 226, 255, 255)};
   }

   private static int[] process(ItemStack var0, int var1) {
      try {
         data.clear();
         instance.getItemModelManager().clearAndUpdate(data, var0, ItemDisplayContext.GUI, instance.world, instance.player, var1);
         context.setSeed(var1);
         Sprite var2 = data.getParticleSprite(context);
         if (var2 == null) {
            return null;
         }

         NativeImage var3 = handle(var2.getContents());
         if (var3 == null) {
            return null;
         }

         int var4 = Math.min(var2.getContents().getWidth(), var3.getWidth());
         int var5 = Math.min(var2.getContents().getHeight(), var3.getHeight());
         return handle(var3, 0, 0, var4, var5);
      } catch (Throwable var6) {
         return null;
      }
   }

   private static int[] handle(ItemStack var0) {
      if (!var0.isOf(Items.PLAYER_HEAD)) {
         return null;
      }

      try {
         ProfileComponent var1 = (ProfileComponent)var0.get(DataComponentTypes.PROFILE);
         if (var1 == null) {
            return null;
         }

         SkinTextures var2 = instance.getSkinProvider().getSkinTextures(var1.gameProfile());
         if (var2 != null && var2.texture() != null) {
            if (instance.getTextureManager().getTexture(var2.texture()) instanceof NativeImageBackedTexture var4) {
               NativeImage var5 = var4.getImage();
               if (var5 != null && var5.getWidth() >= 16 && var5.getHeight() >= 16) {
                  int[] var6 = handle(var5, 8, 8, 8, 8);
                  int[] var7 = var5.getWidth() >= 48 ? handle(var5, 40, 8, 8, 8) : null;
                  return handle(var6, var7);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (Throwable var8) {
         return null;
      }
   }

   private static boolean handle(RoundedRectRenderer var0, ItemStack var1, float var2, float var3, float var4) {
      if (!var1.isOf(Items.PLAYER_HEAD)) {
         return false;
      }

      try {
         ProfileComponent var5 = (ProfileComponent)var1.get(DataComponentTypes.PROFILE);
         if (var5 == null) {
            return false;
         }

         SkinTextures var6 = instance.getSkinProvider().getSkinTextures(var5.gameProfile());
         if (var6 != null && var6.texture() != null) {
            AbstractTexture var7 = instance.getTextureManager().getTexture(var6.texture());
            if (var7 != null && var7.getGlTexture() instanceof GlTexture var8 && var8.getGlId() > 0) {
               float var13 = 16.0F * var4;
               float var10 = Math.max(2.0F, 4.0F * var4);
               int var11 = var8.getGlId();
               var0.handle(var11, var2, var3, var13, var13, 0.125F, 0.125F, 0.25F, 0.25F, var10);
               var0.handle(var11, var2, var3, var13, var13, 0.625F, 0.125F, 0.75F, 0.25F, var10);
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } catch (Throwable var12) {
         return false;
      }
   }

   private static NativeImage handle(SpriteContents var0) {
      if (var0 == null) {
         return null;
      }

      try {
         Field var1 = current;
         if (var1 == null) {
            for (Field var5 : SpriteContents.class.getDeclaredFields()) {
               if (var5.getType() == NativeImage.class) {
                  var1 = var5;
                  break;
               }
            }

            if (var1 == null) {
               return null;
            }

            var1.setAccessible(true);
            current = var1;
         }

         return var1.get(var0) instanceof NativeImage var8 ? var8 : null;
      } catch (Throwable var6) {
         return null;
      }
   }

   private static int[] handle(NativeImage var0, int var1, int var2, int var3, int var4) {
      if (var0 != null && var3 > 0 && var4 > 0) {
         ItemStackOverlayRenderer.State[] var5 = new ItemStackOverlayRenderer.State[24];

         for (int var6 = 0; var6 < var5.length; var6++) {
            var5[var6] = new ItemStackOverlayRenderer.State();
         }

         int var19 = Math.min(var0.getWidth(), var1 + var3);
         int var7 = Math.min(var0.getHeight(), var2 + var4);

         for (int var8 = Math.max(0, var2); var8 < var7; var8++) {
            for (int var9 = Math.max(0, var1); var9 < var19; var9++) {
               int var10 = var0.getColorArgb(var9, var8);
               int var11 = var10 >>> 24 & 0xFF;
               if (var11 >= 40) {
                  int var12 = var10 >>> 16 & 0xFF;
                  int var13 = var10 >>> 8 & 0xFF;
                  int var14 = var10 & 0xFF;
                  float[] var15 = Color.RGBtoHSB(var12, var13, var14, null);
                  if (!(var15[2] < 0.09F)) {
                     float var16 = var15[1];
                     float var17 = var11 / 255.0F * (0.25F + var16 * 1.9F) * (0.35F + var15[2]);
                     if (var16 < 0.08F) {
                        var17 *= 0.2F;
                     }

                     int var18 = Math.min(var5.length - 1, (int)(var15[0] * var5.length));
                     var5[var18].handle(var12, var13, var14, var17);
                  }
               }
            }
         }

         int var20 = handle(var5, -1);
         if (var20 >= 0 && !(var5[var20].config < 0.75F)) {
            int var21 = handle(var5, var20);
            int var22 = var5[var20].handle();
            int var23 = var21 >= 0 && var5[var21].config >= var5[var20].config * 0.18F ? var5[var21].handle() : handle(var22);
            return handle(var22, var23);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static int handle(ItemStackOverlayRenderer.State[] var0, int var1) {
      int var2 = -1;
      float var3 = 0.0F;

      for (int var4 = 0; var4 < var0.length; var4++) {
         if (var4 != var1 && !(var0[var4].config <= var3) && (var1 < 0 || handle(var4, var1, var0.length) >= 3)) {
            var2 = var4;
            var3 = var0[var4].config;
         }
      }

      return var2;
   }

   private static int handle(int var0, int var1, int var2) {
      int var3 = Math.abs(var0 - var1);
      return Math.min(var3, var2 - var3);
   }

   private static int handle(int var0) {
      float[] var1 = Color.RGBtoHSB(
         RoundedRectRenderer.ColorState.render(var0), RoundedRectRenderer.ColorState.tick(var0), RoundedRectRenderer.ColorState.drawAnimation(var0), null
      );
      float var2 = (var1[0] + 0.08F) % 1.0F;
      int var3 = Color.HSBtoRGB(var2, Math.min(1.0F, var1[1] * 1.08F), Math.min(1.0F, var1[2] * 1.18F));
      return RoundedRectRenderer.ColorState.compute(var3 >>> 16 & 0xFF, var3 >>> 8 & 0xFF, var3 & 0xFF, 255);
   }

   private static int[] handle(int var0, int var1) {
      int var2 = process(var0);
      int var3 = process(var1);
      if (process(var2, var3) < 42) {
         var3 = handle(var2);
         var3 = process(var3);
      }

      return new int[]{var2, var3};
   }

   private static int process(int var0) {
      int var1 = RoundedRectRenderer.ColorState.render(var0);
      int var2 = RoundedRectRenderer.ColorState.tick(var0);
      int var3 = RoundedRectRenderer.ColorState.drawAnimation(var0);
      float[] var4 = Color.RGBtoHSB(var1, var2, var3, null);
      float var5 = var1 * 0.2126F + var2 * 0.7152F + var3 * 0.0722F;
      if (var4[1] < 0.1F && var5 < 150.0F) {
         return handle()[0];
      }

      float var6 = Math.min(1.0F, Math.max(var4[1] * 1.12F, 0.38F));
      float var7 = Math.min(1.0F, Math.max(var4[2], 0.78F));
      if (var5 < 115.0F) {
         var7 = Math.max(var7, 0.88F);
      }

      int var8 = Color.HSBtoRGB(var4[0], var6, var7);
      return RoundedRectRenderer.ColorState.compute(var8 >>> 16 & 0xFF, var8 >>> 8 & 0xFF, var8 & 0xFF, 255);
   }

   private static int process(int var0, int var1) {
      int var2 = RoundedRectRenderer.ColorState.render(var0) - RoundedRectRenderer.ColorState.render(var1);
      int var3 = RoundedRectRenderer.ColorState.tick(var0) - RoundedRectRenderer.ColorState.tick(var1);
      int var4 = RoundedRectRenderer.ColorState.drawAnimation(var0) - RoundedRectRenderer.ColorState.drawAnimation(var1);
      return Math.abs(var2) + Math.abs(var3) + Math.abs(var4);
   }

   private static int[] handle(int[] var0, int[] var1) {
      if (var0 == null) {
         return var1;
      } else {
         return var1 == null ? var0 : new int[]{var0[0], var1[1]};
      }
   }

   private static String compute(ItemStack var0, int var1) {
      return var0.getItem() + "|" + var0.getName().getString() + "|" + var0.getComponents() + "|" + var1;
   }

   private static int[] handle(int[] var0) {
      return var0 == null ? null : new int[]{var0[0], var0[1]};
   }

   public static void handle(Identifier var0) {
      if (var0 != null) {
         try {
            AbstractTexture var1 = instance.getTextureManager().getTexture(var0);
            if (var1 != null && var1.getGlTexture() instanceof GlTexture var2 && var2.getGlId() > 0) {
               int var5 = var2.getGlId();
               if (state.add(var5)) {
                  GlStateManager._bindTexture(var5);
                  GlStateManager._texParameter(3553, 10241, 9728);
                  GlStateManager._texParameter(3553, 10240, 9728);
               }
            }
         } catch (Throwable var4) {
         }
      }
   }

   public static float handle(float var0) {
      return Math.round(var0);
   }

   public static float process(float var0) {
      return Math.max(1.0F, Math.round(var0));
   }

   public static float compute(float var0) {
      return Float.isFinite(var0) && !(var0 <= 0.0F) ? Math.max(0.0625F, Math.round(var0 * 16.0F) / 16.0F) : 1.0F;
   }

   public static void handle(DrawContext var0, RenderPipeline var1, Identifier var2, float var3, float var4, float var5, int var6, int var7) {
      if (var0 != null && var1 != null && var2 != null) {
         float var8 = instance.getWindow().getScaleFactor();
         var0.getMatrices().pushMatrix();
         var0.getMatrices().translate(var3 / var8, var4 / var8);
         var0.getMatrices().scale(var5 / var8, var5 / var8);
         var0.drawTexture(var1, var2, 0, 0, 0.0F, 0.0F, var6, var7, var6, var7);
         var0.getMatrices().popMatrix();
      }
   }

   private static void process(RoundedRectRenderer var0, ItemStack var1, float var2, float var3, float var4) {
      if (var1.getCount() > 1) {
         int var5 = var1.getCount();
         String var6 = var5 >= 0 && var5 < config.length ? config[var5] : "999+";
         float var7 = Math.max(7.0F, 18.0F * var4);
         float var8 = TextMeasureCache.handle(FontRegistry.config, var6, var7).instance;
         float var9 = var2 + 16.0F * var4 - var8 - 1.0F * var4;
         float var10 = var3 + 15.0F * var4;
         var0.handle(FontRegistry.config, var9, var10, var7, var6, RoundedRectRenderer.ColorState.compute(255, 255, 255, 255));
      }
   }

   private static String[] process() {
      String[] var0 = new String[1000];

      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = Integer.toString(var1);
      }

      return var0;
   }

   private static void handle(RoundedRectRenderer var0, ItemStack var1, Identifier var2, GlTexture var3, float var4, float var5, float var6) {
      ArmorTrim var7 = (ArmorTrim)var1.get(DataComponentTypes.TRIM);
      if (var7 != null) {
         String var8 = process(var1);
         if (var8 != null) {
            String var9 = var7.material().getKey().map(var0x -> var0x.getValue().getPath()).orElse(null);
            if (var9 != null && !var9.isEmpty()) {
               SpriteAtlasTexture var10 = instance.getBakedModelManager().getAtlas(var2);
               if (var10 != null) {
                  Identifier var11 = Identifier.of(var2.getNamespace(), "trims/items/" + var8 + "_trim_" + var9);
                  Sprite var12 = var10.getSprite(var11);
                  if (var12 != null && var11.equals(var12.getContents().getId())) {
                     var0.handle(
                        var3.getGlId(),
                        var4,
                        var5,
                        16.0F * var6,
                        16.0F * var6,
                        var12.getMinU(),
                        var12.getMinV(),
                        var12.getMaxU(),
                        var12.getMaxV(),
                        Math.max(2.0F, 4.0F * var6)
                     );
                  }
               }
            }
         }
      }
   }

   private static String process(ItemStack var0) {
      EquippableComponent var1 = (EquippableComponent)var0.get(DataComponentTypes.EQUIPPABLE);
      if (var1 == null) {
         return null;
      }

      return switch (var1.slot()) {
         case HEAD -> "helmet";
         case CHEST -> "chestplate";
         case LEGS -> "leggings";
         case FEET -> "boots";
         default -> null;
      };
   }

   record ColorStop(int[] colors, long createdAt) {
   }

   static final class State {
      private float instance;
      private float data;
      private float context;
      float config;

      void handle(int var1, int var2, int var3, float var4) {
         this.instance += var1 * var4;
         this.data += var2 * var4;
         this.context += var3 * var4;
         this.config += var4;
      }

      int handle() {
         return this.config <= 0.0F
            ? RoundedRectRenderer.ColorState.compute(220, 255, 245, 255)
            : RoundedRectRenderer.ColorState.compute(
               Math.round(this.instance / this.config), Math.round(this.data / this.config), Math.round(this.context / this.config), 255
            );
      }
   }
}
