package ru.wild.api.module;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.json.JSONObject;
import org.wild.module.api.Module;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.core.manager.FriendManager;
import ru.wild.modules.combat.AttackAura;
import ru.wild.network.IrcClient;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.player.NicknameUtil;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.world.ServerEnvironment;

public class PlayerMarkerModule extends Module {
   private static final Identifier profileDraw = Identifier.of("wild", "textures/png/skull_state_0.png");
   private static final Identifier vectorPerform = Identifier.of("wild", "textures/png/skull_state_1.png");
   private static final Identifier eventAttach = Identifier.of("wild", "textures/png/skull_state_2.png");
   public final ModeSetting source = new ModeSetting("Отображать: ", "Только у друзей", "Всех", "Только у друзей");
   public final ChoiceSetting target = new ChoiceSetting(
      "Информация", new BooleanSetting("Показ в табе", true), new BooleanSetting("Показ в нейм тегах", false), new BooleanSetting("Показ лого", false)
   );
   public final BooleanSetting pending = new BooleanSetting("Установка меток", true);
   public final KeybindSetting previous = new KeybindSetting("Кнопка установки", -1).handle(this.pending::compute);
   public final BooleanSetting latest = new BooleanSetting("Фокус цели", true);
   public final BooleanSetting summary = new BooleanSetting("Счетчик попнутых тотемов врага", true);
   public static String matrixBlend = "";
   private String serverRead = "";
   private long positionAdvance = 0L;
   public static double vectorMatch;
   public static double itemProject;
   public static double responseCompute;
   private long frameCheck = 0L;

   public PlayerMarkerModule() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary);
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (!ServerEnvironment.handle()) {
         if (var1.resolve() == this.previous.compute()) {
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!ServerEnvironment.handle()) {
         if (IrcClient.instance != null) {
            IrcClient.instance.compute();
         }

         if (System.currentTimeMillis() - this.positionAdvance > 200L) {
            this.positionAdvance = System.currentTimeMillis();
            if (Module.client.player != null && Module.client.world != null) {
               NicknameUtil.instance.handle();
               String var2 = Module.client.world.getRegistryKey().getValue().getPath();
               float var3 = Module.client.player.getHealth() + Module.client.player.getAbsorptionAmount();
               boolean var4 = NicknameUtil.process();
               IrcClient var5 = IrcClient.instance;
               if (var5 != null) {
                  var5.handle(Module.client.player.getX(), Module.client.player.getY(), Module.client.player.getZ(), var2, var3, NicknameUtil.instance.compute(), var4);
               }
            }
         }

         if (AttackAura.textureRun instanceof PlayerEntity var6) {
            matrixBlend = var6.isAlive() && var6.getHealth() > 0.0F ? var6.getName().getString() : "";
         }

         if (!matrixBlend.isEmpty() && FriendManager.handle(matrixBlend)) {
            matrixBlend = "";
         }

         if (Module.client.world != null) {
            PlayerEntity var7 = null;

            for (PlayerEntity var11 : Module.client.world.getPlayers()) {
               if (!var11.isAlive() || var11.getHealth() <= 0.0F) {
                  IrcClient.state.remove(var11.getName().getString());
                  if (var11.getName().getString().equalsIgnoreCase(matrixBlend)) {
                     matrixBlend = "";
                  }
               } else if (var11.getName().getString().equalsIgnoreCase(matrixBlend)) {
                  var7 = var11;
               }
            }

            if (this.latest.compute() && IrcClient.instance != null && IrcClient.instance.isOpen()) {
               long var10 = System.currentTimeMillis();
               if (var7 != null) {
                  vectorMatch = var7.getX();
                  itemProject = var7.getY();
                  responseCompute = var7.getZ();
                  if (var10 - this.frameCheck > 200L) {
                     this.frameCheck = var10;
                     this.handle(matrixBlend, vectorMatch, itemProject, responseCompute);
                  }
               } else if (matrixBlend.isEmpty() && !this.serverRead.isEmpty()) {
                  this.frameCheck = var10;
                  this.handle("", 0.0, 0.0, 0.0);
               }
            }
         }
      }
   }

   private void handle(String var1, double var2, double var4, double var6) {
      try {
         JSONObject var8 = new JSONObject();
         var8.put("type", "target_sync");
         var8.put("user", Module.client.getSession().getUsername());
         var8.put("target", var1);
         var8.put("server", IrcClient.handle());
         if (!var1.isEmpty()) {
            var8.put("x", var2);
            var8.put("y", var4);
            var8.put("z", var6);
         }

         IrcClient.instance.send(var8.toString());
         this.serverRead = var1;
      } catch (Exception var9) {
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (!ServerEnvironment.handle() && this.summary.compute()) {
         if (var1.resolve() instanceof EntityStatusS2CPacket var2 && var2.getStatus() == 35) {
            Entity var9 = var2.getEntity(Module.client.world);
            if (var9 instanceof PlayerEntity var4) {
               String var5 = var4.getName().getString();
               int var6 = IrcClient.state.getOrDefault(var5, 0) + 1;
               IrcClient.state.put(var5, var6);
               IrcClient.cache.put(var5, System.currentTimeMillis());
               if (AttackAura.textureRun != null && AttackAura.textureRun.getId() == var9.getId() && IrcClient.instance != null && IrcClient.instance.isOpen()) {
                  try {
                     JSONObject var7 = new JSONObject();
                     var7.put("type", "totem_pop");
                     var7.put("attacker", Module.client.getSession().getUsername());
                     var7.put("victim", var5);
                     var7.put("count", var6);
                     var7.put("server", IrcClient.handle());
                     IrcClient.instance.send(var7.toString());
                  } catch (Exception var8) {
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (!ServerEnvironment.handle() && this.latest.compute()) {
         HashSet<String> var2 = new HashSet<>();
         if (!matrixBlend.isEmpty()) {
            var2.add(matrixBlend);
         }

         String var3 = IrcClient.handle();
         if (IrcClient.instance != null && IrcClient.instance.isOpen()) {
            boolean var4 = "Только у друзей".equals(this.source.compute());
            String var5 = Module.client.getSession() != null ? Module.client.getSession().getUsername() : "";

            for (Entry var7 : IrcClient.config.entrySet()) {
               String var8 = (String)var7.getKey();
               IrcClient.PrimaryCacheEntry var9 = (IrcClient.PrimaryCacheEntry)var7.getValue();
               if ((!var4 || var8.equals(var5) || FriendManager.handle(var8)) && var9.data.equals(var3)) {
                  var2.add(var9.instance);
               }
            }
         }

         if (!var2.isEmpty()) {
            float var23 = Module.client.getRenderTickCounter().getTickProgress(true);
            long var24 = System.currentTimeMillis();
            assert Module.client.world != null;

            for (String var26 : var2) {
               PlayerEntity var27 = null;

               for (PlayerEntity var11 : Module.client.world.getPlayers()) {
                  if (var11.getName().getString().equalsIgnoreCase(var26) && var11 != Module.client.player) {
                     var27 = var11;
                     break;
                  }
               }

               if (var27 != null) {
                  this.handle(var1.resolve(), var27, var23);
               } else {
                  double var28 = 0.0;
                  double var12 = 0.0;
                  double var14 = 0.0;
                  boolean var16 = false;
                  long var17 = 0L;
                  Iterator var19 = IrcClient.config.values().iterator();

                  while (true) {
                     if (var19.hasNext()) {
                        IrcClient.PrimaryCacheEntry var20 = (IrcClient.PrimaryCacheEntry)var19.next();
                        if (!var20.instance.equalsIgnoreCase(var26) || !var20.data.equals(var3)) {
                           continue;
                        }

                        var17 = var24 - var20.active;
                        if (var17 >= 4000L) {
                           continue;
                        }

                        double var21 = MathHelper.clamp(var17 / 200.0, 0.0, 1.0);
                        var28 = MathHelper.lerp(var21, var20.cache, var20.context);
                        var12 = MathHelper.lerp(var21, var20.output, var20.config);
                        var14 = MathHelper.lerp(var21, var20.current, var20.state);
                        var16 = true;
                     }

                     if (!var16 && var26.equalsIgnoreCase(matrixBlend)) {
                        var28 = vectorMatch;
                        var12 = itemProject;
                        var14 = responseCompute;
                        var16 = true;
                     }

                     if (var16) {
                        float var29 = 1.0F;
                        if (var17 > 3000L) {
                           float var30 = 1.0F - (float)(var17 - 3000L) / 1000.0F;
                           var29 = MathHelper.clamp(var30, 0.0F, 1.0F);
                        }

                        float var31 = 20.0F;
                        this.handle(var1.resolve(), var26, var28, var12 + 2.0, var14, var31, 20.0F, var29);
                     }
                     break;
                  }
               }
            }
         }
      }
   }

   private void handle(RoundedRectRenderer var1, PlayerEntity var2, float var3) {
      double var4 = MathHelper.lerp(var3, var2.lastX, var2.getX());
      double var6 = MathHelper.lerp(var3, var2.lastY, var2.getY());
      double var8 = MathHelper.lerp(var3, var2.lastZ, var2.getZ());
      float var10 = var2.getHealth() + var2.getAbsorptionAmount();
      float var11 = var2.getMaxHealth();
      this.handle(var1, var2.getName().getString(), var4, var6 + var2.getHeight(), var8, var10, var11, 1.0F);
   }

   private void handle(RoundedRectRenderer var1, String var2, double var3, double var5, double var7, float var9, float var10, float var11) {
      Camera var12 = Module.client.gameRenderer.getCamera();
      Vec3d var13 = var12.getPos();
      Vec3d var14 = new Vec3d(var3, var5, var7);
      if (!(var14.squaredDistanceTo(var13) < 1.0E-6)) {
         Vec3d var15 = ClientMathUtil.handle(var14);
         if (var15 != null && !(var15.z <= 0.001F) && !(var15.z > 1.0)) {
            float var16 = (float)var15.x;
            float var17 = (float)var15.y;
            long var18 = IrcClient.cache.getOrDefault(var2, 0L);
            boolean var20 = System.currentTimeMillis() - var18 < 2500L;
            Identifier var21;
            if (var20) {
               var21 = eventAttach;
            } else if (var9 <= var10 / 2.0F) {
               var21 = vectorPerform;
            } else {
               var21 = profileDraw;
            }

            int var22 = this.handle(var21);
            if (var22 > 0 && var11 > 0.05F) {
               float var23 = 28.0F;
               float var24 = 15.0F;
               float var25 = var16 - var23 / 2.0F;
               float var26 = var17 - var23 - var24;
               var1.handle(var25, var26);
               var1.handle(var23 / 2.0F, var23 / 2.0F);
               var1.process(1.0F, -1.0F);
               var1.handle(-var23 / 2.0F, -var23 / 2.0F);
               var1.handle(var22, 0.0F, 0.0F, var23, var23);
               var1.prepare();
               var1.check();
               var1.prepare();
               var1.prepare();
            }

            if (this.summary.compute()) {
               int var27 = IrcClient.state.getOrDefault(var2, 0);
               if (var27 > 0) {
                  String var28 = var27 + " тотемов";
                  float var29 = 22.0F;
                  float var30 = RoundedRectRenderer.handle(FontRegistry.instance, var28, var29).instance;
                  var1.handle(FontRegistry.instance, var16 - var30 / 2.0F, var17 - 5.0F, var29, var28, this.handle(Color.WHITE.getRGB(), var11));
               }
            }
         }
      }
   }

   private int handle(int var1, float var2) {
      int var3 = var1 >> 24 & 0xFF;
      int var4 = var1 >> 16 & 0xFF;
      int var5 = var1 >> 8 & 0xFF;
      int var6 = var1 & 0xFF;
      var3 = (int)(var3 * var2);
      return RoundedRectRenderer.ColorState.compute(var4, var5, var6, var3);
   }

   private int handle(Identifier var1) {
      TextureManager var2 = Module.client.getTextureManager();
      if (var2 == null) {
         return -1;
      } else {
         AbstractTexture var3 = var2.getTexture(var1);
         if (var3 == null) {
            return -1;
         } else if (var3.getGlTexture() instanceof GlTexture var5) {
            int var6 = var5.getGlId();
            return var6 > 0 ? var6 : -1;
         } else {
            return -1;
         }
      }
   }
}
