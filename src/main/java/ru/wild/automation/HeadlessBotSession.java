package ru.wild.automation;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.NetworkEncryptionUtils.SecureRandomUtil;
import net.minecraft.network.message.LastSeenMessageList.Acknowledgment;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.text.Text;

public final class HeadlessBotSession {
   private final String instance;
   private final ClientConnection data;
   private volatile GameProfile context;
   private volatile HeadlessBotPlayHandler config;
   private volatile DetachedClientWorld state;
   private volatile BackgroundClientPlayer cache;
   private volatile ClientPlayerInteractionManager output;
   private volatile boolean current;
   private volatile boolean active;
   private volatile boolean mode;
   private final AtomicBoolean selection = new AtomicBoolean();
   private final HeadlessBotModuleRegistry enabled = new HeadlessBotModuleRegistry(this);

   public HeadlessBotSession(String var1, ClientConnection var2) {
      this.instance = var1;
      this.data = var2;
   }

   public String handle() {
      return this.instance;
   }

   public GameProfile process() {
      return this.context;
   }

   public void handle(GameProfile var1) {
      this.context = var1;
   }

   public ClientConnection compute() {
      return this.data;
   }

   public void handle(Packet<?> var1) {
      if (this.data.isOpen()) {
         this.data.send(var1);
      }
   }

   public boolean handle(String var1) {
      if (!this.current || var1 == null || var1.isBlank() || var1.length() > 256 || !this.data.isOpen()) {
         return false;
      }

      if (!var1.startsWith("/")) {
         this.handle(new ChatMessageC2SPacket(var1, Instant.now(), SecureRandomUtil.nextLong(), null, new Acknowledgment(0, new BitSet(), (byte)0)));
         return true;
      }

      if (var1.length() == 1) {
         return false;
      }

      this.handle(new CommandExecutionC2SPacket(var1.substring(1)));
      return true;
   }

   public void resolve() {
      if (this.data.isOpen()) {
         this.data.disconnect(Text.literal("Bot removed"));
      }
   }

   public boolean update() {
      return this.data.isOpen();
   }

   public HeadlessBotPlayHandler apply() {
      return this.config;
   }

   public void handle(HeadlessBotPlayHandler var1) {
      this.config = var1;
   }

   public DetachedClientWorld execute() {
      return this.state;
   }

   public void handle(DetachedClientWorld var1) {
      this.state = var1;
   }

   public BackgroundClientPlayer prepare() {
      return this.cache;
   }

   public void handle(BackgroundClientPlayer var1) {
      this.cache = var1;
   }

   public ClientPlayerInteractionManager check() {
      return this.output;
   }

   public void handle(ClientPlayerInteractionManager var1) {
      this.output = var1;
   }

   public boolean onTick() {
      return this.current;
   }

   public void handle(boolean var1) {
      this.current = var1;
   }

   public boolean select() {
      return this.active;
   }

   public void process(boolean var1) {
      this.active = var1;
   }

   public HeadlessBotModuleRegistry refresh() {
      return this.enabled;
   }

   public boolean render() {
      return this.mode;
   }

   public void compute(boolean var1) {
      this.mode = var1;
   }

   boolean tick() {
      return this.selection.compareAndSet(false, true);
   }

   boolean drawAnimation() {
      return this.selection.get();
   }

   void encodePoint() {
      this.current = false;
      this.active = false;
      this.mode = false;
      this.config = null;
      this.state = null;
      this.cache = null;
      this.output = null;
   }
}
