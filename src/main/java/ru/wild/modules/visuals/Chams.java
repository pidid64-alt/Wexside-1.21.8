package ru.wild.modules.visuals;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.render.EntityRenderIdAccess;
import ru.wild.render.shader.PrismaticChamsPipeline;

@ModuleRegister(name = "Chams", description = "Красивая шейдерная заливка моделей сущностей за стенами.", category = ModuleCategory.Visuals)
public final class Chams extends Module {
   public static final String source = "Crystal";
   public static final String target = "Void";
   public static final String pending = "Phantom";
   public static final String previous = "Гибрид";
   public static final String latest = "По сцене";
   public static final String summary = "Сквозь стены";
   public final ChoiceSetting matrixBlend = new ChoiceSetting(
      "Цели", new BooleanSetting("Игроки", true), new BooleanSetting("Мобы", true), new BooleanSetting("Себя", false), new BooleanSetting("Невидимые", true)
   );
   public final ModeSetting vectorMatch = new ModeSetting("Режим", "Crystal", "Crystal", "Void", "Phantom");
   public final ModeSetting itemProject = new ModeSetting("Глубина", "Гибрид", "Гибрид", "По сцене", "Сквозь стены");
   public final BooleanSetting responseCompute = new BooleanSetting("Скрывать броню и предметы", true);
   public final BooleanSetting providerFetch = new BooleanSetting("Скрывать ванильную тень", true);
   public final NumberSetting profileDraw = new NumberSetting("Дистанция", 96.0F, 8.0F, 256.0F, 1.0F, false);
   public final ColorSetting vectorPerform = new ColorSetting("Верхний акцент", 58.0F, 0.72F, 1.0F, 1.0F);
   public final ColorSetting eventAttach = new ColorSetting("Нижний акцент", 76.0F, 0.82F, 1.0F, 1.0F);
   public final NumberSetting serverRead = new NumberSetting("Интенсивность", 1.35F, 0.35F, 3.0F, 0.05F, false);
   public final NumberSetting positionAdvance = new NumberSetting("Прозрачность", 1.0F, 0.25F, 1.0F, 0.01F, true);
   public final NumberSetting frameCheck = new NumberSetting("Преломление", 0.72F, 0.35F, 1.15F, 0.01F, false);
   private final Map<Integer, Chams.State> moduleCollect = new ConcurrentHashMap<>();

   public Chams() {
      PrismaticChamsPipeline.handle();
      this.vectorMatch.cache = "Screen-space шейдер чамсов: Crystal, Void или Phantom";
      this.itemProject.cache = "Гибрид рисует скрытый проход через стены и основной проход по depth buffer";
      this.responseCompute.config = "Отключает броню, предметы в руках и остальные feature layers у подсвеченной сущности";
      this.providerFetch.config = "Убирает стандартную круглую тень Minecraft под подсвеченной сущностью";
      this.handle(
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform,
         this.eventAttach,
         this.serverRead,
         this.positionAdvance,
         this.frameCheck
      );
   }

   public boolean handle(LivingEntityRenderState var1) {
      return this.resolve(var1) > 0.001F;
   }

   public boolean process(LivingEntityRenderState var1) {
      return this.responseCompute.compute() && this.resolve(var1) > 0.001F;
   }

   public boolean compute(LivingEntityRenderState var1) {
      return this.providerFetch.compute() && this.resolve(var1) > 0.001F;
   }

   public float resolve(LivingEntityRenderState var1) {
      if (var1 == null) {
         return 0.0F;
      }

      int var2 = apply(var1);
      boolean var3 = this.update(var1);
      Chams.State var4 = this.moduleCollect.get(var2);
      if (var4 == null && !var3) {
         return 0.0F;
      }

      if (var4 == null) {
         var4 = new Chams.State();
         this.moduleCollect.put(var2, var4);
      }

      long var5 = System.nanoTime();
      float var7 = var4.context == 0L ? 0.0F : Math.min((float)(var5 - var4.context) / 1.0E9F, 0.05F);
      var4.context = var5;
      float var8 = var3 ? 1.0F : 0.0F;
      float var9 = var8 - var4.instance;
      var4.data += var9 * 42.0F * var7;
      var4.data = var4.data * (float)Math.exp(-13.0F * var7);
      var4.instance = var4.instance + var4.data * var7;
      if (var4.instance < 0.0F) {
         var4.instance = 0.0F;
         var4.data = 0.0F;
      } else if (var4.instance > 1.12F) {
         var4.instance = 1.12F;
         var4.data *= -0.22F;
      }

      if (!var3 && var4.instance <= 0.001F) {
         this.moduleCollect.remove(var2);
         return 0.0F;
      } else {
         return var4.instance;
      }
   }

   public boolean refresh() {
      Iterator var1 = this.moduleCollect.entrySet().iterator();

      while (var1.hasNext()) {
         Chams.State var2 = (Chams.State)((Entry)var1.next()).getValue();
         if (!(var2.instance <= 0.001F) || !(var2.data <= 0.001F)) {
            return true;
         }

         var1.remove();
      }

      return false;
   }

   public int render() {
      if (this.vectorMatch.process("Void")) {
         return 1;
      } else {
         return this.vectorMatch.process("Phantom") ? 2 : 0;
      }
   }

   public boolean tick() {
      return this.itemProject.process("Гибрид");
   }

   public boolean drawAnimation() {
      return this.itemProject.process("По сцене");
   }

   private boolean update(LivingEntityRenderState var1) {
      if (!this.enabled) {
         return false;
      } else if (var1 == null || Module.client == null || Module.client.world == null) {
         return false;
      } else if (var1.invisible && !this.matrixBlend.process("Невидимые")) {
         return false;
      } else {
         float var2 = Math.max(1.0F, this.profileDraw.compute());
         if (var1.squaredDistanceToCamera > var2 * var2) {
            return false;
         } else if (var1 instanceof PlayerEntityRenderState var3) {
            return Module.client.player != null && var3.name != null && var3.name.equals(Module.client.player.getName().getString())
               ? this.matrixBlend.process("Себя")
               : this.matrixBlend.process("Игроки");
         } else {
            return this.matrixBlend.process("Мобы");
         }
      }
   }

   public float[] encodePoint() {
      return handle(this.vectorPerform.compute());
   }

   public float[] animate() {
      return handle(this.eventAttach.compute());
   }

   public static Chams load() {
      Chams var0 = save();
      return var0 == null || !var0.enabled && !var0.refresh() ? null : var0;
   }

   public static Chams save() {
      return WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(Chams.class) : null;
   }

   private static int apply(LivingEntityRenderState var0) {
      int var1 = ((EntityRenderIdAccess)var0).wild$getEntityId();
      if (var1 != Integer.MIN_VALUE) {
         return var1;
      }

      int var2 = var0.entityType == null ? 0 : var0.entityType.hashCode();
      int var3 = Math.round((float)var0.x * 8.0F);
      int var4 = Math.round((float)var0.y * 8.0F);
      int var5 = Math.round((float)var0.z * 8.0F);
      int var6 = var2;
      var6 = var6 * 31 + var3;
      var6 = var6 * 31 + var4;
      return var6 * 31 + var5;
   }

   private static float[] handle(Color var0) {
      return new float[]{var0.getRed() / 255.0F, var0.getGreen() / 255.0F, var0.getBlue() / 255.0F, var0.getAlpha() / 255.0F};
   }

   static final class State {
      float instance;
      float data;
      long context;
   }
}
