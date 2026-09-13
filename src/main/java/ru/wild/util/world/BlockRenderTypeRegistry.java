package ru.wild.util.world;

import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;

public final class BlockRenderTypeRegistry {
   public static final int instance = 0;
   public static final int data = 1;
   public static final int context = 2;
   public static final int config = 3;
   public static final int state = 4;
   public static final int cache = 5;
   public static final int output = 6;
   public static final int current = 7;
   public static final int active = 8;
   public static final int mode = 9;
   public static final int selection = 10;
   public static final int enabled = 11;
   public static final int renderer = 12;
   public static final int handler = 13;
   public static final int animationDraw = 14;
   public static final int pointEncode = 15;
   public static final int animator = 16;
   public static final int source = 17;
   public static final int target = 18;
   public static final int pending = 19;
   public static final int previous = 20;
   public static final int latest = 21;
   public static final int summary = 22;
   public static final int matrixBlend = 12;
   public static final int vectorMatch = 4190208;
   public static final int itemProject = 4095;
   private static final byte responseCompute = -1;
   private static final Reference2ByteOpenHashMap<Block> providerFetch = new Reference2ByteOpenHashMap();
   private static final Reference2ByteOpenHashMap<BlockEntityType<?>> profileDraw = new Reference2ByteOpenHashMap();
   private static final BlockEntityType<?>[] vectorPerform = new BlockEntityType[12];
   private static final int[] eventAttach = new int[22];
   private static final Predicate<BlockState> serverRead = var0 -> providerFetch.getByte(var0.getBlock()) != -1;

   private BlockRenderTypeRegistry() {
   }

   private static void handle(Block var0, int var1) {
      providerFetch.put(var0, (byte)var1);
   }

   private static void handle(BlockEntityType<?> var0, int var1) {
      profileDraw.put(var0, (byte)var1);
      vectorPerform[var1] = var0;
   }

   public static BlockEntityType<?> handle(int var0) {
      return var0 >= 0 && var0 < vectorPerform.length ? vectorPerform[var0] : null;
   }

   public static Predicate<BlockState> handle() {
      return serverRead;
   }

   public static int handle(BlockState var0) {
      return providerFetch.getByte(var0.getBlock());
   }

   public static int handle(BlockEntityType<?> var0) {
      return profileDraw.getByte(var0);
   }

   public static int process(int var0) {
      return eventAttach[var0];
   }

   static {
      providerFetch.defaultReturnValue((byte)-1);
      profileDraw.defaultReturnValue((byte)-1);
      handle(Blocks.COAL_ORE, 12);
      handle(Blocks.DEEPSLATE_COAL_ORE, 12);
      handle(Blocks.IRON_ORE, 13);
      handle(Blocks.DEEPSLATE_IRON_ORE, 13);
      handle(Blocks.GOLD_ORE, 14);
      handle(Blocks.DEEPSLATE_GOLD_ORE, 14);
      handle(Blocks.NETHER_GOLD_ORE, 14);
      handle(Blocks.COPPER_ORE, 15);
      handle(Blocks.DEEPSLATE_COPPER_ORE, 15);
      handle(Blocks.LAPIS_ORE, 16);
      handle(Blocks.DEEPSLATE_LAPIS_ORE, 16);
      handle(Blocks.REDSTONE_ORE, 17);
      handle(Blocks.DEEPSLATE_REDSTONE_ORE, 17);
      handle(Blocks.DIAMOND_ORE, 18);
      handle(Blocks.DEEPSLATE_DIAMOND_ORE, 18);
      handle(Blocks.EMERALD_ORE, 19);
      handle(Blocks.DEEPSLATE_EMERALD_ORE, 19);
      handle(Blocks.NETHER_QUARTZ_ORE, 20);
      handle(Blocks.ANCIENT_DEBRIS, 21);
      handle(BlockEntityType.CHEST, 0);
      handle(BlockEntityType.TRAPPED_CHEST, 1);
      handle(BlockEntityType.ENDER_CHEST, 2);
      handle(BlockEntityType.MOB_SPAWNER, 3);
      handle(BlockEntityType.BARREL, 4);
      handle(BlockEntityType.HOPPER, 5);
      handle(BlockEntityType.DISPENSER, 6);
      handle(BlockEntityType.DROPPER, 7);
      handle(BlockEntityType.FURNACE, 8);
      handle(BlockEntityType.SHULKER_BOX, 9);
      handle(BlockEntityType.DECORATED_POT, 10);
      handle(BlockEntityType.BRUSHABLE_BLOCK, 11);
      eventAttach[12] = 2368548;
      eventAttach[13] = 14200728;
      eventAttach[14] = 16766720;
      eventAttach[15] = 13137226;
      eventAttach[16] = 2647255;
      eventAttach[17] = 14818075;
      eventAttach[18] = 5629672;
      eventAttach[19] = 2545261;
      eventAttach[20] = 15787216;
      eventAttach[21] = 9128501;
   }
}
