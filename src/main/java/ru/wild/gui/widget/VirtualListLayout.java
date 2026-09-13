package ru.wild.gui.widget;

import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ViewportLayoutState;

public final class VirtualListLayout {
   private final float instance;
   private final float data;
   private final float context;
   private final float config;
   private final float state;
   private final float cache;
   private final float output;
   private final float current;
   private final float active;
   private final float mode;
   private final float selection;
   private final float enabled;
   private final float renderer;
   private final float handler;

   private VirtualListLayout(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14
   ) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5;
      this.cache = var6;
      this.output = var7;
      this.current = var8;
      this.active = var9;
      this.mode = var10;
      this.selection = var11;
      this.enabled = var12;
      this.renderer = var13;
      this.handler = var14;
   }

   public static VirtualListLayout handle(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = var1.process(8.0F);
      float var3 = var1.process(26.0F);
      float var4 = var1.process(6.0F);
      float var5 = var0.computeResponse() + var2 + var1.process(44.0F) + var4;
      float var6 = var5 + var3 + var4;
      float var7 = var0.computeResponse() + var1.submit() - var2 - var6;
      float var8 = var1.save() - var2 * 2.0F;
      float var9 = var1.process(4.0F);
      float var10 = var1.process(18.0F);
      float var11 = var8 - var9 - var10;
      float var12 = var1.process(8.0F);
      float var13 = (var11 - var12) * 0.5F;
      float var14 = var1.process(34.0F);
      float var15 = var0.projectItem() + var2 + var9;
      return new VirtualListLayout(
         var0.projectItem() + var2, var6, var8, var7, var0.projectItem() + var2, var5, var8, var3, var15, var13, var14, var12, var1.process(6.0F), var9
      );
   }

   public VirtualListLayout.Bounds handle(int var1, float var2) {
      int var3 = var1 % 2;
      int var4 = var1 / 2;
      float var5 = this.active + var3 * (this.mode + this.enabled);
      float var6 = this.data + this.handler + var2 + var4 * (this.selection + this.renderer);
      return new VirtualListLayout.Bounds(var5, var6, this.mode, this.selection);
   }

   public float handle(int var1) {
      int var2 = (var1 + 1) / 2;
      return this.handler * 2.0F + var2 * this.selection + Math.max(0, var2 - 1) * this.renderer;
   }

   public boolean handle(VirtualListLayout.Bounds var1, float var2) {
      float var3 = this.data - Math.max(0.0F, var2);
      float var4 = this.data + this.config + Math.max(0.0F, var2);
      return var1.y + var1.height >= var3 && var1.y <= var4;
   }

   public float handle() {
      return this.instance;
   }

   public float process() {
      return this.data;
   }

   public float compute() {
      return this.context;
   }

   public float resolve() {
      return this.config;
   }

   public float update() {
      return this.state;
   }

   public float apply() {
      return this.cache;
   }

   public float execute() {
      return this.output;
   }

   public float prepare() {
      return this.current;
   }

   public float check() {
      return this.state + this.output - this.current;
   }

   public float onTick() {
      return this.current;
   }

   public float select() {
      return this.mode;
   }

   public float refresh() {
      return this.selection;
   }

   public record Bounds(float x, float y, float width, float height) {
   }
}
