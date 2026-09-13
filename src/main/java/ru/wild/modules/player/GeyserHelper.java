package ru.wild.modules.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ResettableSettingGroup;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.modules.visuals.NameTags;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(
   name = "GeyserHelper",
   category = ModuleCategory.Player,
   description = "Подсвечивает лут который можно слутать с ивента 'Гейзер' на FunTime"
)
public class GeyserHelper extends Module {
   private final Map<ItemEntity, Long> previous = new ConcurrentHashMap<>();
   private final List<GeyserHelper.PrimaryDataRecord> latest = new ArrayList<>();
   private final Map<Integer, Long> summary = new ConcurrentHashMap<>();
   public final BooleanSetting source = new BooleanSetting("Синхронизация с NameTags", true);
   public final ModeSetting target = new ModeSetting("Стилистика", "Тёмный", "Тёмный", "Светлый", "Блюр").handle(this.source::compute);
   public final NumberSetting pending = new NumberSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.05F, true).handle(this.source::compute);
   private final ResettableSettingGroup matrixBlend = new ResettableSettingGroup() {};
   private boolean vectorMatch = false;
   private float itemProject = 0.0F;
   private float responseCompute = 0.0F;
   private float providerFetch = 0.0F;
   private float profileDraw = 0.0F;
   private float vectorPerform = 0.0F;
   private boolean eventAttach = false;
   private long serverRead = 0L;
   private final List<GeyserHelper.DataRecord> positionAdvance = new ArrayList<>();
   private int frameCheck;
   private int moduleCollect;
   private float providerClose = 1.0F;
   private String presetSave = "Тёмный";

   public GeyserHelper() {
      this.handle(this.source, this.target, this.pending);
      this.matrixBlend.handle(this.source);
      this.matrixBlend.handle(this.target);
      this.matrixBlend.handle(this.pending);
   }

   @Override
   public void handle() {
      super.handle();
      this.summary.clear();
   }

   @Override
   public void process() {
      super.process();
      this.previous.clear();
      this.latest.clear();
      this.summary.clear();
      this.vectorMatch = false;
      this.vectorPerform = 0.0F;
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.world != null) {
         if (var1.resolve() instanceof PlaySoundS2CPacket var2) {
            String var4 = ((SoundEvent)var2.getSound().value()).toString();
            if (this.handle(var4)) {
               this.handle(var2.getX(), var2.getY(), var2.getZ());
            }
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.world != null) {
         this.previous.keySet().removeIf(var0 -> !var0.isAlive() || var0.getStack().isEmpty());
      }
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (Module.client.world != null && Module.client.player != null) {
         this.positionAdvance.clear();
         this.refresh();
         float var2 = Module.client.getRenderTickCounter().getTickProgress(true);
         RoundedRectRenderer var3 = var1.resolve();
         Camera var4 = Module.client.gameRenderer.getCamera();
         Vec3d var5 = var4.getPos();
         float var6 = (float)Module.client.mouse.getX();
         float var7 = (float)Module.client.mouse.getY();
         boolean var8 = Module.client.currentScreen instanceof ChatScreen;
         this.latest.clear();
         NameTags var9 = this.render();
         boolean var10 = var9 != null && var9.enabled && var9.target.process("Предметы");
         HashSet var11 = new HashSet();
         if (!this.previous.isEmpty()) {
            for (Entry var13 : this.previous.entrySet()) {
               ItemEntity var14 = (ItemEntity)var13.getKey();
               Vec3d var15 = var14.getLerpedPos(var2);
               double var16 = var10 ? 0.52 : 0.7;
               Vec3d var18 = new Vec3d(var15.x, var15.y + var16, var15.z);
               if (!(var18.squaredDistanceTo(var5) < 1.0E-6)) {
                  Vec3d var19 = ClientMathUtil.handle(var18);
                  if (!(var19.z <= 0.001F) && !(var19.z > 1.0)) {
                     double var20 = var5.distanceTo(var18);
                     var11.add(var14.getId());
                     this.summary.putIfAbsent(var14.getId(), System.currentTimeMillis());
                     float var22 = MathHelper.clamp((float)(System.currentTimeMillis() - this.summary.get(var14.getId())) / 300.0F, 0.0F, 1.0F);
                     float var23 = 1.0F - (float)Math.pow(1.0F - var22, 4.0);
                     this.handle(var3, var14, (Long)var13.getValue(), (float)var19.x, (float)var19.y, (float)var20, var10, var23);
                  }
               }
            }
         }

         this.summary.keySet().retainAll(var11);
         this.handle(var3);
         if (var8) {
            boolean var24 = GLFW.glfwGetMouseButton(Module.client.getWindow().getHandle(), 0) == 1;
            boolean var25 = var24 && !this.eventAttach;
            this.eventAttach = var24;
            if (var25 && System.currentTimeMillis() - this.serverRead > 150L) {
               this.serverRead = System.currentTimeMillis();
               boolean var26 = false;

               for (GeyserHelper.DataRecord var30 : this.positionAdvance) {
                  if (this.handle(var6, var7, var30.x, var30.y, var30.w, var30.h)) {
                     var26 = true;
                     this.vectorMatch = !this.vectorMatch;
                     if (this.vectorMatch) {
                        this.itemProject = var30.x;
                        this.responseCompute = var30.y;
                        this.providerFetch = var30.w;
                        this.profileDraw = var30.h;
                     }
                     break;
                  }
               }

               if (!var26 && this.vectorMatch) {
                  boolean var29 = this.handle(var6, var7, this.itemProject - 250.0F, this.responseCompute - 150.0F, 600.0F, 500.0F);
                  if (!var29) {
                     this.vectorMatch = false;
                  }
               }
            }

            float var27 = this.vectorMatch ? 1.0F : 0.0F;
            this.vectorPerform = this.vectorPerform + (var27 - this.vectorPerform) * 0.15F;
            if (this.vectorPerform > 0.01F) {
               NeoStyleOptions.handle(
                  var3,
                  this.matrixBlend,
                  this.itemProject,
                  this.responseCompute,
                  this.providerFetch,
                  this.profileDraw,
                  Module.client.getWindow().getScaledWidth(),
                  Module.client.getWindow().getScaledHeight(),
                  this.vectorPerform,
                  var6,
                  var7,
                  var25,
                  var24
               );
            }
         } else {
            this.vectorMatch = false;
            this.vectorPerform = 0.0F;
            this.eventAttach = false;
         }
      }
   }

   private void handle(RoundedRectRenderer var1, ItemEntity var2, long var3, float var5, float var6, float var7, boolean var8, float var9) {
      float var10 = (float)MathHelper.clamp(16.0 / Math.max(var7, 12.0), 0.75, 1.15);
      float var11 = 6.0F * var10;
      ItemStack var12 = var2.getStack();
      float var13 = 24.0F * var10;
      long var14 = System.currentTimeMillis() - var3;
      String var16 = String.format("%.0f сек", (float)var14 / 1000.0F);
      float var17 = 6.0F * var10;
      float var18 = 20.0F * var10;
      float var19 = RoundedRectRenderer.handle(FontRegistry.instance, var16, var13).instance + var17 * 2.0F;
      if (var8) {
         float var20 = 12.0F * var10;
         float var21 = var20 * var9;
         float var22 = var6 + var21;
         float var23 = var5 - var19 / 2.0F;
         this.handle(var1, var23, var22, var19, var18, var11, var9);
         var1.handle(FontRegistry.instance, var23 + var17, var22 + 14.0F * var10, var13, var16, this.handle(this.moduleCollect, var9));
         this.positionAdvance.add(new GeyserHelper.DataRecord(var23, var22, var19, var18));
      } else {
         float var28 = 8.0F * var10 * (1.0F - var9);
         float var29 = var6 + var28;
         float var30 = 22.0F * var10;
         float var31 = 4.0F * var10;
         float var24 = var30 + var31 + var19;
         float var25 = var5 - var24 / 2.0F;
         this.handle(var1, var25, var29, var30, var30, var11, var9);
         float var26 = var29 + (var30 - var18) / 2.0F;
         this.handle(var1, var25 + var30 + var31, var26, var19, var18, var11, var9);
         var1.handle(FontRegistry.instance, var25 + var30 + var31 + var17, var26 + 14.0F * var10, var13, var16, this.handle(this.moduleCollect, var9));
         float var27 = (var30 - 16.0F * var10) / 2.0F;
         this.latest.add(new GeyserHelper.PrimaryDataRecord(var12, var25 + var27, var29 + var27, var25, var29, var30, var2.getId(), var10));
         this.positionAdvance.add(new GeyserHelper.DataRecord(var25, var29, var24, var30));
      }
   }

   private void refresh() {
      NameTags var1 = this.render();
      boolean var2 = this.source.compute() && var1 != null;
      String var3 = var2 ? var1.serverRead.compute() : this.target.compute();
      float var4 = var2 ? var1.animationSchedule.compute() : this.pending.compute();
      if (var3.equals("Светлый")) {
         this.frameCheck = RoundedRectRenderer.ColorState.compute(240, 240, 245, (int)(255.0F * var4));
         this.moduleCollect = RoundedRectRenderer.ColorState.compute(30, 30, 30, 255);
      } else if (var3.equals("Блюр")) {
         this.frameCheck = RoundedRectRenderer.ColorState.compute(10, 10, 10, (int)(120.0F * var4));
         this.moduleCollect = RoundedRectRenderer.ColorState.compute(250, 250, 250, 255);
      } else {
         this.frameCheck = RoundedRectRenderer.ColorState.compute(25, 25, 26, (int)(255.0F * var4));
         this.moduleCollect = RoundedRectRenderer.ColorState.compute(240, 240, 240, 255);
      }

      this.providerClose = var4;
      this.presetSave = var3;
   }

   private NameTags render() {
      try {
         return (NameTags)WildClient.instance.data.process(NameTags.class);
      } catch (Exception var2) {
         return null;
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = this.providerClose * var7;
      if (!(var8 <= 0.05F)) {
         if (this.presetSave.equals("Блюр")) {
            var1.handle(23.0F);
            var1.handle(var2, var3, var4, var5, var6, var8);
         }

         int var9 = this.handle(this.frameCheck, var7);
         var1.handle(var2, var3, var4, var5, var6, var9);
      }
   }

   private int handle(int var1, float var2) {
      int var3 = var1 >> 24 & 0xFF;
      int var4 = var1 >> 16 & 0xFF;
      int var5 = var1 >> 8 & 0xFF;
      int var6 = var1 & 0xFF;
      return RoundedRectRenderer.ColorState.compute(var4, var5, var6, (int)(var3 * var2));
   }

   private boolean handle(String var1) {
      String var2 = var1.toLowerCase();
      return var2.contains("extinguish") || var2.contains("fizz") || var2.contains("burn") || var2.contains("lava");
   }

   private void handle(double var1, double var3, double var5) {
      Module.client.execute(() -> {
         if (Module.client.world != null) {
            for (Entity var8 : Module.client.world.getEntities()) {
               if (var8 instanceof ItemEntity var9 && var9.squaredDistanceTo(var1, var3, var5) <= 9.0) {
                  this.previous.putIfAbsent(var9, System.currentTimeMillis());
               }
            }
         }
      });
   }

   private boolean handle(float var1, float var2, float var3, float var4, float var5, float var6) {
      return var1 >= var3 && var1 <= var3 + var5 && var2 >= var4 && var2 <= var4 + var6;
   }
   private void handle(RoundedRectRenderer var1) {
      if (var1 != null && !this.latest.isEmpty()) {
         for (GeyserHelper.PrimaryDataRecord var3 : this.latest) {
            float var4 = ItemStackOverlayRenderer.handle(var3.clipX());
            float var5 = ItemStackOverlayRenderer.handle(var3.clipY());
            float var6 = Math.max(1.0F, ItemStackOverlayRenderer.handle(var3.clipSize()));
            var1.compute();
            var1.handle(var4, var5, var6, var6, var6 * 0.27F, var6 * 0.27F, var6 * 0.27F, var6 * 0.27F);
            boolean var9 = false /* VF: Semaphore variable */;

            try {
               var9 = true;
               ItemStackOverlayRenderer.handle(
                  var1,
                  var3.stack(),
                  ItemStackOverlayRenderer.handle(var3.x()),
                  ItemStackOverlayRenderer.handle(var3.y()),
                  ItemStackOverlayRenderer.compute(var3.scale()),
                  var3.seed(),
                  true,
                  var3.seed()
               );
               var9 = false;
            } finally {
               if (var9) {
                  var1.compute();
                  var1.apply();
               }
            }

            var1.compute();
            var1.apply();
         }

         this.latest.clear();
      }
   }

   record DataRecord(float x, float y, float w, float h) {
   }

   record PrimaryDataRecord(ItemStack stack, float x, float y, float clipX, float clipY, float clipSize, int seed, float scale) {
   }
}
