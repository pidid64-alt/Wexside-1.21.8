package ru.wild.gui.widget;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import ru.wild.network.PartyRoster;
import ru.wild.network.RemoteCameraState;
import ru.wild.network.RemoteFrameBuffer;
import ru.wild.network.RemoteInputSink;
import ru.wild.network.RemoteScreenSession;
import ru.wild.network.RemoteScreenSessionImpl;
import ru.wild.render.TexturedQuadsRenderer;
import ru.wild.render.WorldQuadBuilder;
import ru.wild.render.WorldRenderContext;

public final class BrowserSurface implements RemoteScreenSession {
   private static final int instance = 1280;
   private static final int data = 720;
   private static final int context = 6;
   private final Map<UUID, BrowserSurface.TextureState> config = new HashMap<>();
   private final List<UUID> state = new ArrayList<>();
   private final RemoteScreenSession cache = new RemoteScreenSessionImpl();

   @Override
   public void handle(List<PartyRoster.Point3d> var1) {
      this.process(var1);
      if (MCEF.isInitialized()) {
         for (int var2 = 0; var2 < var1.size(); var2++) {
            PartyRoster.Point3d var3 = (PartyRoster.Point3d)var1.get(var2);
            if (var3.source().isEmpty()) {
               this.compute(var3.id());
            } else {
               this.config.computeIfAbsent(var3.id(), var0 -> new BrowserSurface.TextureState()).process(var3.source());
            }
         }
      }
   }

   @Override
   public RemoteInputSink handle(UUID var1) {
      BrowserSurface.TextureState var2 = this.config.get(var1);
      MCEFBrowser var3 = var2 == null ? null : var2.compute();
      return var3 == null ? null : new BrowserSurface.DataRecord(var3);
   }

   @Override
   public RemoteFrameBuffer process(UUID var1) {
      return this.config.get(var1);
   }

   @Override
   public void handle(WorldRenderContext var1, RemoteCameraState var2, PartyRoster.Point3d var3, long var4, int var6) {
      BrowserSurface.TextureState var7 = this.config.get(var3.id());
      Identifier var8 = var7 == null ? null : var7.process(var7.process());
      if (var8 == null) {
         this.cache.handle(var1, var2, var3, var4, var6);
      } else {
         WorldQuadBuilder var9 = new WorldQuadBuilder(var1, var1.process().peek(), var1.handle(TexturedQuadsRenderer.process(var8)));
         double var10 = -var2.resolve();
         double var12 = var2.resolve();
         double var14 = -var2.update();
         double var16 = var2.update();
         var9.handle(
            var2.handle(var10, 0.0),
            var2.handle(var14),
            var2.process(var10, 0.0),
            var2.handle(var12, 0.0),
            var2.handle(var14),
            var2.process(var12, 0.0),
            var2.handle(var12, 0.0),
            var2.handle(var16),
            var2.process(var12, 0.0),
            var2.handle(var10, 0.0),
            var2.handle(var16),
            var2.process(var10, 0.0),
            0.0F,
            1.0F,
            1.0F,
            1.0F,
            1.0F,
            0.0F,
            0.0F,
            0.0F,
            -1
         );
      }
   }

   @Override
   public void handle() {
      for (BrowserSurface.TextureState var2 : this.config.values()) {
         var2.resolve();
      }

      this.config.clear();
      this.cache.handle();
   }

   private void compute(UUID var1) {
      BrowserSurface.TextureState var2 = this.config.remove(var1);
      if (var2 != null) {
         var2.resolve();
      }
   }

   private void process(List<PartyRoster.Point3d> var1) {
      this.state.clear();

      for (UUID var3 : this.config.keySet()) {
         if (!handle(var1, var3)) {
            this.state.add(var3);
         }
      }

      for (int var4 = 0; var4 < this.state.size(); var4++) {
         this.compute(this.state.get(var4));
      }
   }

   private static boolean handle(List<PartyRoster.Point3d> var0, UUID var1) {
      for (int var2 = 0; var2 < var0.size(); var2++) {
         if (((PartyRoster.Point3d)var0.get(var2)).id().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   record DataRecord(MCEFBrowser browser) implements RemoteInputSink {
      @Override
      public void moveCursor(float var1, float var2) {
         this.browser.sendMouseMove(pixelX(var1), pixelY(var2));
      }

      @Override
      public void press(float var1, float var2, int var3) {
         this.browser.sendMousePress(pixelX(var1), pixelY(var2), var3);
      }

      @Override
      public void release(float var1, float var2, int var3) {
         this.browser.sendMouseRelease(pixelX(var1), pixelY(var2), var3);
      }

      @Override
      public void scroll(float var1, float var2, double var3) {
         this.browser.sendMouseWheel(pixelX(var1), pixelY(var2), var3, 0);
      }

      @Override
      public void keyPress(int var1, int var2, int var3) {
         this.browser.sendKeyPress(var1, var2, var3);
      }

      @Override
      public void keyRelease(int var1, int var2, int var3) {
         this.browser.sendKeyRelease(var1, var2, var3);
      }

      @Override
      public void type(char var1, int var2) {
         this.browser.sendKeyTyped(var1, var2);
      }

      private static int pixelX(float var0) {
         return Math.round(Math.max(0.0F, Math.min(1.0F, var0)) * 1280.0F);
      }

      private static int pixelY(float var0) {
         return Math.round(Math.max(0.0F, Math.min(1.0F, var0)) * 720.0F);
      }
   }

   static final class TextureState implements RemoteFrameBuffer {
      private final List<MCEFBrowser> instance = new ArrayList<>();
      private final List<String> data = new ArrayList<>();
      private final List<Identifier> textures = new ArrayList<>();
      private int context;

      @Override
      public int handle() {
         return this.instance.size();
      }

      @Override
      public int process() {
         return this.context;
      }

      @Override
      public String handle(int var1) {
         return var1 >= 0 && var1 < this.data.size() ? this.data.get(var1) : "";
      }

      @Override
      public Identifier process(int var1) {
         if (var1 >= 0 && var1 < this.instance.size()) {
            MCEFBrowser var2 = this.instance.get(var1);
            int var3 = var2.getRenderer().getTextureID();
            if (var3 <= 0) {
               return null;
            }

            Identifier var4 = this.textures.get(var1);
            NativeImageBackedTexture var5 = (NativeImageBackedTexture)MinecraftClient.getInstance().getTextureManager().getTexture(var4);
            if (var5.getGlTexture() instanceof GlTexture var6 && var6.getGlId() == var3) {
               return var4;
            }

            return null;
         } else {
            return null;
         }
      }

      @Override
      public void compute(int var1) {
         if (var1 >= 0 && var1 < this.instance.size()) {
            this.context = var1;
         }
      }

      @Override
      public void handle(String var1) {
         if (this.instance.size() < 6 && var1.startsWith("https://")) {
            this.instance.add(MCEF.createBrowser(var1, false, 1280, 720));
            this.data.add(var1);
            Identifier var2 = Identifier.of("wild", "mcef/" + UUID.randomUUID());
            MinecraftClient.getInstance().getTextureManager().registerTexture(var2, new NativeImageBackedTexture(var2::toString, 1, 1, false));
            this.textures.add(var2);
            this.context = this.instance.size() - 1;
         }
      }

      @Override
      public void resolve(int var1) {
         if (var1 >= 0 && var1 < this.instance.size() && this.instance.size() > 1) {
            this.instance.remove(var1).close();
            this.data.remove(var1);
            MinecraftClient.getInstance().getTextureManager().destroyTexture(this.textures.remove(var1));
            this.context = Math.clamp(this.context >= var1 ? this.context - 1 : this.context, 0, this.instance.size() - 1);
         }
      }

      MCEFBrowser compute() {
         return this.context >= 0 && this.context < this.instance.size() ? this.instance.get(this.context) : null;
      }

      void process(String var1) {
         int var2 = this.data.indexOf(var1);
         if (var2 >= 0) {
            this.context = var2;
         } else if (this.instance.isEmpty()) {
            this.handle(var1);
         } else {
            this.instance.get(this.context).loadURL(var1);
            this.data.set(this.context, var1);
         }
      }

      void resolve() {
         for (int var1 = 0; var1 < this.instance.size(); var1++) {
            this.instance.get(var1).close();
         }

         this.instance.clear();
         this.data.clear();
         for (Identifier var2 : this.textures) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(var2);
         }
         this.textures.clear();
         this.context = 0;
      }
   }
}
