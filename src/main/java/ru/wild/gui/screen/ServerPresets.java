package ru.wild.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.HudRenderContext;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.GuiRippleShader;
import ru.wild.util.inventory.ChaosSphereHelper;
import ru.wild.util.inventory.HolyWorldHelper;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.RoundedRectRenderer;

public class ServerPresets extends Screen {
   private static volatile boolean instance = false;
   private static final String[] data = new String[]{"FunTime", "SpookyTime", "HolyWorld"};
   private static final String[] context = new String[]{"FT", "SP", "HW"};
   private final List<String> config = Arrays.asList(
      "Сфера Хаоса",
      "Сфера Титана",
      "Сфера Ареса",
      "Сфера Бестии",
      "Талисман Демона",
      "Талисман Карателя",
      "Шлем Крушителя",
      "Нагрудник Крушителя",
      "Поножи Крушителя",
      "Ботинки Крушителя",
      "Меч Крушителя",
      "Кирка Крушителя",
      "Лук Крушителя",
      "Арбалет Крушителя",
      "Трезубец Крушителя",
      "Булава Крушителя",
      "Элитры Крушителя",
      "Удочка Крушителя"
   );
   private final List<String> state = Arrays.asList("Явная Пыль", "Дезориентация", "Трапка", "Отмычка к Сферам");
   private final List<String> cache = HolyWorldHelper.handle().stream().map(HolyWorldHelper.SecondaryDataRecord::key).toList();
   private final List<ServerPresets.State> output = new ArrayList<>();
   private final List<ServerPresets.State> current = new ArrayList<>();
   private ServerPresets.State active = null;
   private float mode = 0.0F;
   private float selection = 0.0F;
   private float enabled = 0.0F;
   private String renderer = null;
   private float handler;
   private float animationDraw;
   private float pointEncode;
   private float animator;
   private final int source = RoundedRectRenderer.ColorState.resolve(21, 23, 30, 120);
   private final int target = RoundedRectRenderer.ColorState.resolve(12, 43, 64, 150);
   private final int pending = RoundedRectRenderer.ColorState.resolve(24, 88, 124, 255);
   private final int previous = RoundedRectRenderer.ColorState.resolve(0, 0, 0, 70);

   public ServerPresets() {
      super(Text.literal("AutoBuy Panel"));
      this.process();
      handle();
   }

   private void process() {
      this.output.clear();
      this.current.clear();
      AutoBuy.colorMeasure.forEach((var1, var2) -> this.output.add(new ServerPresets.State(var1, String.valueOf(var2))));
      AutoBuy.outputCollapse.forEach(var1 -> this.current.add(new ServerPresets.State(var1, "")));
   }

   public static void handle() {
      if (!instance) {
         instance = true;
         EventHandlerInvoker.handle(
            new Object() {
               @EventHandler
               public void handle(HudRenderContext var1) {
                  MinecraftClient var2 = var1.compute();
                  if (var2 != null && var2.currentScreen instanceof ServerPresets var3 && var2.getWindow() != null) {
                     int var12 = (int)(var2.mouse.getX() * var2.getWindow().getScaledWidth() / var2.getWindow().getFramebufferWidth());
                     int var5 = (int)(var2.mouse.getY() * var2.getWindow().getScaledHeight() / var2.getWindow().getFramebufferHeight());
                     GuiRippleShader var6 = GuiRippleShader.handle();
                     boolean var7 = var6.handle(var3) && var6.handle(var2.getWindow().getFramebufferWidth(), var2.getWindow().getFramebufferHeight());
                     boolean var10 = false /* VF: Semaphore variable */;

                     try {
                        var10 = true;
                        var3.handle(var1.resolve(), var1.prepare(), var12, var5);
                        var1.resolve().compute();
                        var10 = false;
                     } finally {
                        if (var10) {
                           if (var7) {
                              var6.compute();
                           }
                        }
                     }

                     if (var7) {
                        var6.compute();
                     }
                  }
               }
            }
         );
      }
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
   }

   public void handle(RoundedRectRenderer var1, DrawContext var2, int var3, int var4) {
      AutoBuy var5 = (AutoBuy)WildClient.instance.data.process(AutoBuy.class);
      if (var5 != null && var5.enabled) {
         float var6 = (float)this.client.getWindow().getFramebufferWidth() / this.client.getWindow().getScaledWidth();
         float var7 = 350.0F;
         float var8 = 180.0F;
         float var9 = 15.0F;
         float var10 = var7 + var9 + var8;
         float var11 = 260.0F;
         float var12 = (this.client.getWindow().getScaledWidth() - var10) / 2.0F;
         float var13 = (this.client.getWindow().getScaledHeight() - var11) / 2.0F;
         var1.compute(var6);
         float var14 = var12;
         float var15 = var13;
         var1.handle(23.0F);
         var1.handle((float)var14, var15, var7, var11, (float)6.0F, (float)this.source);
         var1.handle(var14, var15, var7, var11, 6.0F, this.source);
         float var16 = var14 + var7 + var9;
         float var17 = var13;
         var1.handle(23.0F);
         var1.handle((float)var16, var17, var8, var11, (float)6.0F, (float)this.source);
         var1.handle(var16, var17, var8, var11, 6.0F, this.source);
         float var18 = 13.0F;
         float var19 = RoundedRectRenderer.handle(FontRegistry.config, "D", var18).instance;
         float var20 = RoundedRectRenderer.handle(FontRegistry.instance, "Autobuy |", var18).instance;
         float var21 = var19 + 4.0F + var20 + 8.0F;

         for (String var25 : context) {
            var21 += RoundedRectRenderer.handle(FontRegistry.config, var25, var18).instance + 12.0F;
         }

         var1.handle(var14 + 10.0F, var15 + 10.0F, var21 + 10.0F, 24.0F, 6.0F, RoundedRectRenderer.ColorState.resolve(20, 20, 25, 200));
         var1.handle(FontRegistry.config, var14 + 18.0F, var15 + 15.0F, var18, "D", RoundedRectRenderer.ColorState.resolve(80, 90, 160, 255));
         var1.handle(
            FontRegistry.instance, var14 + 18.0F + var19 + 4.0F, var15 + 15.0F, var18, "Autobuy |", RoundedRectRenderer.ColorState.resolve(100, 100, 100, 255)
         );
         float var46 = var14 + 18.0F + var19 + 4.0F + var20 + 10.0F;

         for (int var47 = 0; var47 < data.length; var47++) {
            boolean var49 = var5.latest.compute().equals(data[var47]);
            var1.handle(
               FontRegistry.config, var46, var15 + 15.0F, var18, context[var47], var49 ? -1 : RoundedRectRenderer.ColorState.resolve(120, 120, 120, 255)
            );
            var46 += RoundedRectRenderer.handle(FontRegistry.config, context[var47], var18).instance + 12.0F;
         }

         float var48 = RoundedRectRenderer.handle(FontRegistry.instance, "Autopars", var18).instance;
         var1.handle(var16 + 10.0F, var17 + 10.0F, var19 + 4.0F + var48 + 16.0F, 24.0F, 6.0F, RoundedRectRenderer.ColorState.resolve(20, 20, 25, 200));
         var1.handle(FontRegistry.config, var16 + 18.0F, var17 + 15.0F, var18, "D", RoundedRectRenderer.ColorState.resolve(80, 90, 160, 255));
         var1.handle(
            FontRegistry.instance, var16 + 18.0F + var19 + 4.0F, var17 + 15.0F, var18, "Autopars", RoundedRectRenderer.ColorState.resolve(100, 100, 100, 255)
         );
         float var50 = var15 + 45.0F;
         float var51 = var11 - 55.0F;
         float var26 = 160.0F;
         float var27 = 160.0F;
         float var28 = var14 + 10.0F;
         float var29 = var28 + var26 + 10.0F;
         float var30 = var16 + 10.0F;
         float var31 = var8 - 20.0F;
         var1.handle(var28, var50, var26 + 10.0F + var27, var51, 6.0F, this.target);
         var1.handle(var30, var50, var31, var51, 6.0F, this.target);
         List var32 = this.handle(var5);
         var1.handle(var28, var50, var26, var51, 8.0F, 8.0F, 8.0F, 8.0F);
         float var33 = 32.0F;
         float var34 = 8.0F;
         int var35 = (int)((var26 - 16.0F) / (var33 + var34));
         float var36 = var28 + 10.0F;
         float var37 = var50 + 10.0F + this.mode;

         for (int var38 = 0; var38 < var32.size(); var38++) {
            float var39 = var36 + var38 % var35 * (var33 + var34);
            float var40 = var37 + var38 / var35 * (var33 + var34);
            var1.handle(var39, var40, var33, var33, 6.0F, this.previous);
            if (var2 != null) {
               ChaosSphereHelper.handle(var2, (String)var32.get(var38), var39 + 8.0F, var40 + 8.0F);
            }
         }

         var1.apply();
         var1.handle(var29, var50, var27, var51, 8.0F, 8.0F, 8.0F, 8.0F);
         float var52 = var50 + 10.0F + this.selection;

         for (int var53 = this.output.size() - 1; var53 >= 0; var53--) {
            ServerPresets.State var56 = this.output.get(var53);
            var56.context.handle();
            var56.context.handle(var56.config ? 0.0 : 1.0, 0.2F, Easings.handler, false);
            if (var56.config && var56.context.update() < 0.01F) {
               AutoBuy.colorMeasure.remove(var56.instance);
               this.output.remove(var53);
            } else {
               float var41 = 46.0F * var56.context.update();
               float var42 = var29 + 8.0F;
               var1.handle(var42, var52, var27 - 16.0F, var41, 6.0F, this.pending);
               var1.handle(var42 + 6.0F, var52 + 6.0F, 34.0F, 34.0F, 4.0F, this.previous);
               if (var2 != null) {
                  ChaosSphereHelper.handle(var2, var56.instance, var42 + 15.0F, var52 + 15.0F);
               }

               var1.handle(var42, var52, var27 - 16.0F, var41, 6.0F, 6.0F, 6.0F, 6.0F);
               var1.handle(FontRegistry.instance, var42 + 48.0F, var52 + 10.0F, var18, HolyWorldHelper.resolve(var56.instance), -1);
               var1.handle(FontRegistry.instance, var42 + 48.0F, var52 + 26.0F, 11.0F, "Цена: ", RoundedRectRenderer.ColorState.resolve(180, 180, 180, 255));
               float var43 = var42 + 48.0F + RoundedRectRenderer.handle(FontRegistry.instance, "Цена: ", 11.0F).instance;
               boolean var44 = this.active == var56;
               var1.handle(var43, var52 + 24.0F, 60.0F, 16.0F, 4.0F, RoundedRectRenderer.ColorState.resolve(15, 20, 25, 200));
               if (var44) {
                  var1.handle(var43, var52 + 24.0F, 60.0F, 16.0F, 4.0F, RoundedRectRenderer.ColorState.resolve(80, 150, 220, 255), 1.0F);
               }

               String var45 = var56.data + (var44 && System.currentTimeMillis() % 1000L > 500L ? "_" : "");
               var1.handle(FontRegistry.instance, var43 + 4.0F, var52 + 26.0F, 11.0F, var45, -1);
               var1.apply();
               var52 += var41 + 8.0F;
            }
         }

         var1.apply();
         var1.handle(var30, var50, var31, var51, 8.0F, 8.0F, 8.0F, 8.0F);
         if (this.current.isEmpty()) {
            float var54 = var30 + var31 / 2.0F;
            var1.handle(
               FontRegistry.instance,
               var54 - RoundedRectRenderer.handle(FontRegistry.instance, "ДЛЯ ПАРСА ЦЕНЫ У", 11.0F).instance / 2.0F,
               var50 + var51 / 2.0F - 18.0F,
               11.0F,
               "ДЛЯ ПАРСА ЦЕНЫ У",
               RoundedRectRenderer.ColorState.resolve(80, 110, 130, 180)
            );
            var1.handle(
               FontRegistry.instance,
               var54 - RoundedRectRenderer.handle(FontRegistry.instance, "ПРЕДМЕТА,", 11.0F).instance / 2.0F,
               var50 + var51 / 2.0F - 4.0F,
               11.0F,
               "ПРЕДМЕТА,",
               RoundedRectRenderer.ColorState.resolve(80, 110, 130, 180)
            );
            var1.handle(
               FontRegistry.instance,
               var54 - RoundedRectRenderer.handle(FontRegistry.instance, "ПЕРЕМЕСТИТЕ ЕГО В", 11.0F).instance / 2.0F,
               var50 + var51 / 2.0F + 10.0F,
               11.0F,
               "ПЕРЕМЕСТИТЕ ЕГО В",
               RoundedRectRenderer.ColorState.resolve(80, 110, 130, 180)
            );
            var1.handle(
               FontRegistry.instance,
               var54 - RoundedRectRenderer.handle(FontRegistry.instance, "ОДНУ ИЗ ЯЧЕЕК", 11.0F).instance / 2.0F,
               var50 + var51 / 2.0F + 24.0F,
               11.0F,
               "ОДНУ ИЗ ЯЧЕЕК",
               RoundedRectRenderer.ColorState.resolve(80, 110, 130, 180)
            );
         } else {
            float var55 = var50 + 10.0F + this.enabled;

            for (int var57 = this.current.size() - 1; var57 >= 0; var57--) {
               ServerPresets.State var58 = this.current.get(var57);
               var58.context.handle();
               var58.context.handle(var58.config ? 0.0 : 1.0, 0.2F, Easings.handler, false);
               if (var58.config && var58.context.update() < 0.01F) {
                  AutoBuy.outputCollapse.remove(var58.instance);
                  this.current.remove(var57);
               } else {
                  float var59 = 46.0F * var58.context.update();
                  float var60 = var30 + 8.0F;
                  var1.handle(var60, var55, var31 - 16.0F, var59, 6.0F, this.pending);
                  var1.handle(var60 + 6.0F, var55 + 6.0F, 34.0F, 34.0F, 4.0F, this.previous);
                  if (var2 != null) {
                     ChaosSphereHelper.handle(var2, var58.instance, var60 + 15.0F, var55 + 15.0F);
                  }

                  var1.handle(var60, var55, var31 - 16.0F, var59, 6.0F, 6.0F, 6.0F, 6.0F);
                  var1.handle(FontRegistry.instance, var60 + 48.0F, var55 + 17.0F, var18, HolyWorldHelper.resolve(var58.instance), -1);
                  var1.apply();
                  var55 += var59 + 8.0F;
               }
            }
         }

         var1.apply();
         if (this.renderer != null) {
            var1.handle(this.handler, this.animationDraw, 32.0F, 32.0F, 6.0F, RoundedRectRenderer.ColorState.resolve(255, 255, 255, 180));
            if (var2 != null) {
               ChaosSphereHelper.handle(var2, this.renderer, this.handler + 8.0F, this.animationDraw + 8.0F);
            }

            this.handler = var3 - this.pointEncode;
            this.animationDraw = var4 - this.animator;
         }

         var1.prepare();
      } else {
         this.client.setScreen(null);
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      AutoBuy var6 = (AutoBuy)WildClient.instance.data.process(AutoBuy.class);
      float var7 = 350.0F;
      float var8 = 180.0F;
      float var9 = 15.0F;
      float var10 = var7 + var9 + var8;
      float var11 = 260.0F;
      float var12 = (this.width - var10) / 2.0F;
      float var13 = (this.height - var11) / 2.0F;
      float var14 = var12;
      float var15 = var13;
      float var16 = var14 + var7 + var9;
      float var17 = 13.0F;
      float var18 = RoundedRectRenderer.handle(FontRegistry.config, "Litka", var17).instance;
      float var19 = RoundedRectRenderer.handle(FontRegistry.instance, "Autobuy |", var17).instance;
      float var20 = var14 + 18.0F + var18 + 4.0F + var19 + 10.0F;

      for (int var21 = 0; var21 < data.length; var21++) {
         float var22 = RoundedRectRenderer.handle(FontRegistry.config, context[var21], var17).instance;
         if (var1 >= var20 - 4.0F && var1 <= var20 + var22 + 6.0F && var3 >= var15 + 10.0F && var3 <= var15 + 34.0F) {
            var6.latest.state = data[var21];
            var6.latest.current = var6.latest.config.indexOf(data[var21]);
            return true;
         }

         var20 += var22 + 12.0F;
      }

      float var38 = var15 + 45.0F;
      float var39 = var11 - 55.0F;
      float var23 = var14 + 10.0F;
      float var24 = 160.0F;
      float var25 = var23 + var24 + 10.0F;
      float var26 = 160.0F;
      float var27 = var16 + 10.0F;
      float var28 = var8 - 20.0F;
      if (var5 == 1) {
         if (var1 >= var25 && var1 <= var25 + var26 && var3 >= var38 && var3 <= var38 + var39) {
            float var29 = var38 + 10.0F + this.selection;

            for (ServerPresets.State var31 : this.output) {
               if (var3 >= var29 && var3 <= var29 + 46.0F) {
                  var31.config = true;
                  return true;
               }

               var29 += 54.0F;
            }
         }

         if (var1 >= var27 && var1 <= var27 + var28 && var3 >= var38 && var3 <= var38 + var39) {
            float var40 = var38 + 10.0F + this.enabled;

            for (ServerPresets.State var46 : this.current) {
               if (var3 >= var40 && var3 <= var40 + 46.0F) {
                  var46.config = true;
                  return true;
               }

               var40 += 54.0F;
            }
         }
      }

      this.active = null;
      if (var5 == 0) {
         float var41 = var38 + 10.0F + this.selection;

         for (ServerPresets.State var47 : this.output) {
            if (var3 >= var41 + 24.0F && var3 <= var41 + 40.0F && var1 >= var25 + 48.0F && var1 <= var25 + 150.0F) {
               this.active = var47;
               return true;
            }

            var41 += 54.0F;
         }
      }

      if (var5 == 0 && var1 >= var23 && var1 <= var23 + var24 && var3 >= var38 && var3 <= var38 + var39) {
         List var42 = this.handle(var6);
         float var45 = 32.0F;
         float var48 = 8.0F;
         int var32 = (int)((var24 - 16.0F) / (var45 + var48));
         float var33 = var23 + 10.0F;
         float var34 = var38 + 10.0F + this.mode;

         for (int var35 = 0; var35 < var42.size(); var35++) {
            float var36 = var33 + var35 % var32 * (var45 + var48);
            float var37 = var34 + var35 / var32 * (var45 + var48);
            if (var1 >= var36 && var1 <= var36 + var45 && var3 >= var37 && var3 <= var37 + var45) {
               this.renderer = (String)var42.get(var35);
               this.pointEncode = (float)var1 - var36;
               this.animator = (float)var3 - var37;
               this.handler = var36;
               this.animationDraw = var37;
               return true;
            }
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.renderer != null && var5 == 0) {
         float var6 = 350.0F;
         float var7 = 180.0F;
         float var8 = 15.0F;
         float var9 = var6 + var8 + var7;
         float var10 = 260.0F;
         float var11 = (this.width - var9) / 2.0F;
         float var12 = (this.height - var10) / 2.0F;
         float var13 = var12 + 45.0F;
         float var14 = var10 - 55.0F;
         float var15 = var11 + 10.0F + 160.0F + 10.0F;
         float var16 = 160.0F;
         float var17 = var11 + var6 + var8 + 10.0F;
         float var18 = var7 - 20.0F;
         if (var1 >= var15 && var1 <= var15 + var16 && var3 >= var13 && var3 <= var13 + var14) {
            if (this.output.stream().noneMatch(var1x -> var1x.instance.equals(this.renderer))) {
               this.output.add(new ServerPresets.State(this.renderer, ""));
               AutoBuy.colorMeasure.put(this.renderer, 0L);
            }
         } else if (var1 >= var17
            && var1 <= var17 + var18
            && var3 >= var13
            && var3 <= var13 + var14
            && this.current.stream().noneMatch(var1x -> var1x.instance.equals(this.renderer))) {
            this.current.add(new ServerPresets.State(this.renderer, ""));
            if (!AutoBuy.outputCollapse.contains(this.renderer)) {
               AutoBuy.outputCollapse.add(this.renderer);
            }
         }

         this.renderer = null;
         return true;
      } else {
         return super.mouseReleased(var1, var3, var5);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      float var9 = 350.0F;
      float var10 = 180.0F;
      float var11 = 15.0F;
      float var12 = var9 + var11 + var10;
      float var13 = 260.0F;
      float var14 = (this.width - var12) / 2.0F;
      float var15 = (this.height - var13) / 2.0F;
      float var16 = var15 + 45.0F;
      float var17 = var13 - 55.0F;
      float var18 = var14 + 10.0F;
      float var19 = 160.0F;
      float var20 = var18 + var19 + 10.0F;
      float var21 = 160.0F;
      float var22 = var14 + var9 + var11 + 10.0F;
      float var23 = var10 - 20.0F;
      if (var1 >= var18 && var1 <= var18 + var19 && var3 >= var16 && var3 <= var16 + var17) {
         this.mode += (float)(var7 * 22.0);
         if (this.mode > 0.0F) {
            this.mode = 0.0F;
         }
      } else if (var1 >= var20 && var1 <= var20 + var21 && var3 >= var16 && var3 <= var16 + var17) {
         this.selection += (float)(var7 * 22.0);
         if (this.selection > 0.0F) {
            this.selection = 0.0F;
         }
      } else if (var1 >= var22 && var1 <= var22 + var23 && var3 >= var16 && var3 <= var16 + var17) {
         this.enabled += (float)(var7 * 22.0);
         if (this.enabled > 0.0F) {
            this.enabled = 0.0F;
         }
      }

      return super.mouseScrolled(var1, var3, var5, var7);
   }

   public boolean charTyped(char var1, int var2) {
      if (this.active != null && Character.isDigit(var1) && this.active.data.length() < 12) {
         this.active.data = this.active.data + var1;
         this.handle(this.active);
         return true;
      } else {
         return super.charTyped(var1, var2);
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.active != null) {
         if (var1 == 259 && !this.active.data.isEmpty()) {
            this.active.data = this.active.data.substring(0, this.active.data.length() - 1);
            this.handle(this.active);
            return true;
         }

         if (var1 == 257 || var1 == 256) {
            this.active = null;
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   private List<String> handle(AutoBuy var1) {
      if (var1.latest.compute().equals("SpookyTime")) {
         return this.state;
      } else {
         return var1.latest.compute().equals("HolyWorld") ? this.cache : this.config;
      }
   }

   private void handle(ServerPresets.State var1) {
      try {
         if (this.output.contains(var1)) {
            long var2 = var1.data.isEmpty() ? 0L : Long.parseLong(var1.data);
            AutoBuy.colorMeasure.put(var1.instance, var2);
         }

         if (WildClient.instance.renderer != null) {
            WildClient.instance.renderer.compute();
         }
      } catch (Exception var4) {
      }
   }

   public boolean shouldPause() {
      return false;
   }

   class State {
      String instance;
      String data;
      DoubleAnimator context = new DoubleAnimator();
      boolean config = false;

      State(String var2, String var3) {
         this.instance = var2;
         this.data = var3;
      }
   }
}
