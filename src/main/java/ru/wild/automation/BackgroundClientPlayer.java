package ru.wild.automation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import ru.wild.api.event.PlayerMoveEvent;
import ru.wild.core.PlayerContextDispatcher;

public final class BackgroundClientPlayer extends ClientPlayerEntity {
   private final HeadlessBotSession instance;
   private double data;
   private double context;
   private double config;
   private float state;
   private float cache;
   private boolean output;
   private boolean current;
   private int active;

   public BackgroundClientPlayer(
      HeadlessBotSession var1, MinecraftClient var2, DetachedClientWorld var3, ClientPlayNetworkHandler var4, StatHandler var5, ClientRecipeBook var6
   ) {
      this(var1, var2, var3, var4, var5, var6, PlayerInput.DEFAULT, false);
   }

   public BackgroundClientPlayer(
      HeadlessBotSession var1,
      MinecraftClient var2,
      DetachedClientWorld var3,
      ClientPlayNetworkHandler var4,
      StatHandler var5,
      ClientRecipeBook var6,
      PlayerInput var7,
      boolean var8
   ) {
      super(var2, var3, var4, var5, var6, var7, var8);
      this.instance = var1;
      SyncedClientOptions var9 = var2.options.getSyncedOptions();
      this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)var9.playerModelParts());
      this.setMainArm(var9.mainArm());
      this.process();
   }

   public HeadlessBotSession handle() {
      return this.instance;
   }

   public void tick() {
      super.tick();
      if (HeadlessBotEngine.handle() != this.instance) {
         PlayerMoveEvent var1 = new PlayerMoveEvent(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.isOnGround());
         PlayerContextDispatcher.handle(this.instance, var1);
         if (!var1.handle()) {
            this.handle(var1);
         }
      }
   }

   public void process() {
      this.data = this.getX();
      this.context = this.getY();
      this.config = this.getZ();
      this.state = this.getYaw();
      this.cache = this.getPitch();
      this.output = this.isOnGround();
      this.current = this.horizontalCollision;
      this.active = 0;
   }

   private void handle(PlayerMoveEvent var1) {
      double var2 = var1.compute();
      double var4 = var1.resolve();
      double var6 = var1.update();
      float var8 = (float)var1.apply();
      float var9 = (float)var1.execute();
      boolean var10 = var1.prepare();
      double var11 = var2 - this.data;
      double var13 = var4 - this.context;
      double var15 = var6 - this.config;
      double var17 = var8 - this.state;
      double var19 = var9 - this.cache;
      this.active++;
      boolean var21 = MathHelper.squaredMagnitude(var11, var13, var15) > MathHelper.square(2.0E-4) || this.active >= 20;
      boolean var22 = var17 != 0.0 || var19 != 0.0;
      if (var21 && var22) {
         this.networkHandler.sendPacket(new Full(var2, var4, var6, var8, var9, var10, this.horizontalCollision));
      } else if (var21) {
         this.networkHandler.sendPacket(new PositionAndOnGround(var2, var4, var6, var10, this.horizontalCollision));
      } else if (var22) {
         this.networkHandler.sendPacket(new LookAndOnGround(var8, var9, var10, this.horizontalCollision));
      } else if (this.output != var10 || this.current != this.horizontalCollision) {
         this.networkHandler.sendPacket(new OnGroundOnly(var10, this.horizontalCollision));
      }

      if (var21) {
         this.data = var2;
         this.context = var4;
         this.config = var6;
         this.active = 0;
      }

      if (var22) {
         this.state = var8;
         this.cache = var9;
      }

      this.output = var10;
      this.current = this.horizontalCollision;
   }
}
