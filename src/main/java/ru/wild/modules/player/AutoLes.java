package ru.wild.modules.player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(name = "AutoLes", category = ModuleCategory.Player, description = "Автоматически фармит для вас лес, и зарабатывает на ReallyWorld")
public class AutoLes extends Module {
   public final NumberSetting source = new NumberSetting("Радиус", 4.0F, 1.0F, 6.0F, 0.5F, false);
   public final BooleanSetting target = new BooleanSetting("Махать рукой", true);
   public final BooleanSetting pending = new BooleanSetting("Авто-сдача", true);
   public final BooleanSetting previous = new BooleanSetting("AutoPay", false);
   public final StringSetting latest = new StringSetting("Ник для перевода денег", "");
   public final NumberSetting summary = new NumberSetting("Кол-во монет", 1000.0F, 500.0F, 25000.0F, 1000.0F, false).handle(() -> !this.previous.compute());
   public final NumberSetting matrixBlend = new NumberSetting("Расписание/с", 20.0F, 1.0F, 60.0F, 1.0F, false);
   private final Map<BlockPos, BlockState> vectorMatch = new ConcurrentHashMap<>();
   private final Map<BlockPos, Long> itemProject = new ConcurrentHashMap<>();
   private final Set<BlockPos> responseCompute = ConcurrentHashMap.newKeySet();
   private long providerFetch = 0L;
   private long profileDraw = 0L;
   private long vectorPerform = 0L;
   private BlockPos eventAttach = null;

   public AutoLes() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary, this.matrixBlend);
   }

   @Override
   public void handle() {
      super.handle();
      this.tick();
   }

   @Override
   public void process() {
      super.process();
      this.render();
      this.tick();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         long var2 = System.currentTimeMillis();
         if (this.pending.compute() && (float)(var2 - this.providerFetch) > this.matrixBlend.compute() * 500.0F) {
            Module.client.getNetworkHandler().sendChatCommand("sellwood");
            this.providerFetch = var2;
         }

         if (this.previous.compute() && (float)(var2 - this.profileDraw) > this.matrixBlend.compute() * 500.0F + 200.0F) {
            Module.client.getNetworkHandler().sendChatCommand("pay " + this.latest.compute() + " " + (int)this.summary.compute());
            this.profileDraw = var2;
         }

         this.handle(var2);
         this.process(var2);
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (var1.resolve() instanceof PlayerActionC2SPacket var2) {
            if (var2.getAction() == Action.STOP_DESTROY_BLOCK || var2.getAction() == Action.START_DESTROY_BLOCK) {
               this.compute(var2.getPos());
            }
         } else if (var1.resolve() instanceof PlayerInteractBlockC2SPacket var3) {
            if (Module.client.player.getStackInHand(var3.getHand()).getItem() instanceof BlockItem) {
               BlockPos var7 = var3.getBlockHitResult().getBlockPos().offset(var3.getBlockHitResult().getSide());
               this.responseCompute.add(var7);
               this.vectorMatch.remove(var7);
               this.itemProject.remove(var7);
            }
         } else if (var1.resolve() instanceof BlockUpdateS2CPacket var4) {
            this.handle(var1, var4);
         }
      }
   }

   private void handle(long var1) {
      if (this.eventAttach != null && (!this.handle(this.eventAttach) || !this.process(this.eventAttach))) {
         this.eventAttach = null;
      }

      if (this.eventAttach == null) {
         this.refresh();
      }

      if (this.eventAttach != null && var1 - this.vectorPerform > 0L) {
         Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, this.eventAttach, Direction.UP));
         Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, this.eventAttach, Direction.DOWN));
         this.vectorPerform = var1;
      }
   }

   private void refresh() {
      int var1 = (int)this.source.compute();
      BlockPos var2 = Module.client.player.getBlockPos();
      double var3 = Double.MAX_VALUE;
      BlockPos var5 = null;

      for (BlockPos var7 : BlockPos.iterate(var2.add(-var1, -var1, -var1), var2.add(var1, var1, var1))) {
         if (this.handle(var7)) {
            double var8 = Module.client.player.squaredDistanceTo(var7.toCenterPos());
            if (var8 <= var1 * var1 && var8 < var3) {
               var3 = var8;
               var5 = var7.toImmutable();
            }
         }
      }

      this.eventAttach = var5;
   }

   private boolean handle(BlockPos var1) {
      return Module.client.world.getBlockState(var1).isIn(BlockTags.LOGS);
   }

   private boolean process(BlockPos var1) {
      float var2 = this.source.compute();
      return Module.client.player.squaredDistanceTo(var1.toCenterPos()) <= var2 * var2;
   }

   private void compute(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      if (!var2.isAir()) {
         this.vectorMatch.put(var1, var2);
         this.itemProject.put(var1, System.currentTimeMillis());
         this.handle(var1, var2);
      }
   }

   private void handle(PacketEvent var1, BlockUpdateS2CPacket var2) {
      BlockPos var3 = var2.getPos();
      BlockState var4 = var2.getState();
      if (this.vectorMatch.containsKey(var3)) {
         BlockState var5 = this.vectorMatch.get(var3);
         if (var4.isAir() || !var4.equals(var5)) {
            var1.process();
            this.handle(var3, var5);
         }
      } else if (this.responseCompute.contains(var3) && var4.isAir()) {
         var1.process();
         Module.client.execute(() -> {
            if (Module.client.world != null) {
               Module.client.world.setBlockState(var3, Module.client.world.getBlockState(var3), 0);
            }
         });
      }
   }

   private void process(long var1) {
      this.vectorMatch.forEach((var3, var4) -> {
         BlockState var5 = Module.client.world.getBlockState(var3);
         if (!var5.equals(var4)) {
            Module.client.world.setBlockState(var3, var4, 0);
            if (!var5.isAir()) {
               this.itemProject.put(var3, var1);
            }
         }

         for (Direction var9 : Direction.values()) {
            BlockPos var10 = var3.offset(var9);
            if (this.vectorMatch.containsKey(var10)) {
               BlockState var11 = this.vectorMatch.get(var10);
               if (!Module.client.world.getBlockState(var10).equals(var11)) {
                  Module.client.world.setBlockState(var10, var11, 0);
               }
            }
         }
      });
      this.itemProject.entrySet().removeIf(var3 -> {
         if (var1 - var3.getValue() > 300000L) {
            this.vectorMatch.remove(var3.getKey());
            return true;
         } else {
            return false;
         }
      });
   }

   private void handle(BlockPos var1, BlockState var2) {
      Module.client.execute(() -> {
         if (Module.client.world != null) {
            Module.client.world.setBlockState(var1, var2, 0);
         }
      });
   }

   private void render() {
      if (Module.client.world != null) {
         Module.client.execute(() -> {
            for (BlockPos var2 : this.vectorMatch.keySet()) {
               Module.client.world.setBlockState(var2, Blocks.AIR.getDefaultState(), 0);
            }
         });
      }
   }

   private void tick() {
      this.eventAttach = null;
      this.providerFetch = 0L;
      this.profileDraw = 0L;
      this.vectorPerform = 0L;
      this.vectorMatch.clear();
      this.responseCompute.clear();
      this.itemProject.clear();
   }
}
