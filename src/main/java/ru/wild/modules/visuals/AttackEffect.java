package ru.wild.modules.visuals;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EntityAttackEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.core.HitRefractionRenderer;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.AttackArcSegment;
import ru.wild.render.AttackTrailSegment;
import ru.wild.render.shader.PlasmaPinchRenderer;

@ModuleRegister(name = "AttackEffect", description = "Красивые эффекты в точке удара", category = ModuleCategory.Visuals)
public class AttackEffect extends Module {
   private static final String providerFetch = "Торус";
   private static final String profileDraw = "Плазма";
   private static final int vectorPerform = 6061311;
   private static final int eventAttach = 6748116;
   private static final long serverRead = 30L;
   private static final double positionAdvance = 9.0;
   public final ModeSetting source = new ModeSetting("Режим", "Торус", "Торус", "Плазма");
   public final NumberSetting target = new NumberSetting("Радиус волны", 1.8F, 0.1F, 3.5F, 0.05F, false).handle(() -> !this.source.process("Торус"));
   public final NumberSetting pending = new NumberSetting("Сила преломления", 0.06F, 0.01F, 0.16F, 0.001F, false).handle(() -> !this.source.process("Торус"));
   public final NumberSetting previous = new NumberSetting("Длительность", 420.0F, 200.0F, 1600.0F, 10.0F, false).handle(() -> !this.source.process("Торус"));
   public final BooleanSetting latest = new BooleanSetting("Спектральная дисперсия", true).handle(() -> !this.source.process("Торус"));
   public final BooleanSetting summary = new BooleanSetting("Не сквозь игрока", true).handle(() -> !this.source.process("Торус"));
   public final NumberSetting matrixBlend = new NumberSetting("Радиус плазмы", 1.4F, 1.0F, 3.0F, 0.05F, false).handle(() -> !this.source.process("Плазма"));
   public final NumberSetting vectorMatch = new NumberSetting("Температура плазмы", 1.0F, 0.5F, 2.0F, 0.05F, false)
      .handle(() -> !this.source.process("Плазма"));
   public final NumberSetting itemProject = new NumberSetting("Яркость плазмы", 0.8F, 0.3F, 1.6F, 0.02F, false).handle(() -> !this.source.process("Плазма"));
   public final NumberSetting responseCompute = new NumberSetting("Длительность плазмы", 480.0F, 220.0F, 900.0F, 10.0F, false)
      .handle(() -> !this.source.process("Плазма"));
   private final List<AttackTrailSegment> frameCheck = new CopyOnWriteArrayList<>();
   private final List<AttackArcSegment> moduleCollect = new CopyOnWriteArrayList<>();
   private long providerClose;
   private int presetSave = Integer.MIN_VALUE;
   private int windowConvert;

   public AttackEffect() {
      this.handle(
         this.source,
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute
      );
   }

   @EventHandler
   public void handle(EntityAttackEvent var1) {
      if (var1 != null) {
         this.handle(var1.compute());
      }
   }

   public void handle(Entity var1) {
      if (this.enabled && var1 != null && Module.client.player != null && Module.client.world != null) {
         if (var1 != Module.client.player) {
            long var2 = System.currentTimeMillis();
            if (var1.getId() != this.presetSave || var2 - this.providerClose >= 30L) {
               this.presetSave = var1.getId();
               this.providerClose = var2;
               Vec3d var4 = Module.client.player.getRotationVec(1.0F).normalize();
               if (this.source.process("Плазма")) {
                  this.process(var1, var4, var2);
               } else {
                  this.handle(var1, var4, var2);
               }
            }
         }
      }
   }

   private void handle(Entity var1, Vec3d var2, long var3) {
      Vec3d var5 = var1.getBoundingBox().getCenter();
      AttackTrailSegment var6 = new AttackTrailSegment(var5, var2, var3, (long)this.previous.config, this.target.config, this.pending.config, this.render());
      this.frameCheck.add(var6);

      while (this.frameCheck.size() > 10) {
         this.frameCheck.remove(0);
      }
   }

   private void process(Entity var1, Vec3d var2, long var3) {
      Vec3d var5 = this.handle(var1, var2);
      this.windowConvert = this.windowConvert + 1 & 1023;
      float var6 = this.windowConvert * 7.31F % 41.0F;
      AttackArcSegment var7 = new AttackArcSegment(
         var5, var2, var3, (long)this.responseCompute.config, this.matrixBlend.config, this.vectorMatch.config, this.itemProject.config, var6, this.refresh()
      );
      this.moduleCollect.add(var7);

      while (this.moduleCollect.size() > 12) {
         this.moduleCollect.remove(0);
      }
   }

   private Vec3d handle(Entity var1, Vec3d var2) {
      if (Module.client.crosshairTarget instanceof EntityHitResult var4 && var4.getEntity() == var1) {
         return var4.getPos();
      } else {
         Box var7 = var1.getBoundingBox();
         Vec3d var5 = Module.client.player.getEyePos();
         Vec3d var6 = var5.add(var2.multiply(9.0));
         return var7.raycast(var5, var6).orElse(var7.getCenter());
      }
   }

   @EventHandler
   public void handle(WorldRenderContext var1) {
      if (this.enabled && var1 != null) {
         long var2 = System.currentTimeMillis();
         this.frameCheck.removeIf(var2x -> var2x.process(var2));
         this.moduleCollect.removeIf(var2x -> var2x.process(var2));
         if (!this.frameCheck.isEmpty() || !this.moduleCollect.isEmpty()) {
            Camera var4 = var1.update().handle();
            if (var4 != null) {
               Vec3d var5 = var4.getPos();
               Matrix4f var6 = var1.execute();
               Matrix4f var7 = var1.prepare();
               float var8 = (float)(var2 % 100000L) / 1000.0F;
               if (!this.frameCheck.isEmpty()) {
                  HitRefractionRenderer.handle().handle(Module.client, this.frameCheck, var6, var7, var5, this.latest.compute(), this.summary.compute(), var8);
               }

               if (!this.moduleCollect.isEmpty()) {
                  PlasmaPinchRenderer.handle().handle(Module.client, this.moduleCollect, var6, var7, var5, var8);
               }
            }
         }
      }
   }

   @Override
   public void process() {
      super.process();
      this.frameCheck.clear();
      this.moduleCollect.clear();
   }

   private int refresh() {
      return this.handle(6748116);
   }

   private int render() {
      return this.handle(6061311);
   }

   private int handle(int var1) {
      try {
         if (WildClient.instance != null && WildClient.instance.selection != null) {
            ThemePalette var2 = WildClient.instance.selection.process();
            if (var2 == ThemePalette.CUSTOM) {
               return WildClient.instance.selection.data.prepare() & 16777215;
            }

            if (var2 != null && var2.handle() != null) {
               return var2.handle().getRGB() & 16777215;
            }
         }
      } catch (Throwable var3) {
      }

      return var1;
   }

   private boolean handle(int var1, int var2) {
      int var3 = Math.abs((var1 >> 16 & 0xFF) - (var2 >> 16 & 0xFF));
      int var4 = Math.abs((var1 >> 8 & 0xFF) - (var2 >> 8 & 0xFF));
      int var5 = Math.abs((var1 & 0xFF) - (var2 & 0xFF));
      return var3 <= 3 && var4 <= 3 && var5 <= 3;
   }
}
