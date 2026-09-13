package ru.wild.render.texture;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public final class SvgDocumentRenderer {
   static final Pattern instance = Pattern.compile("[-+]?(?:\\d*\\.\\d+|\\d+\\.?)(?:[eE][-+]?\\d+)?");
   private static final Pattern data = Pattern.compile("([a-zA-Z]+)\\s*\\(([^)]*)\\)");
   private static final Pattern context = Pattern.compile("url\\(\\s*#([^)\\s]+)\\s*\\)");
   private static final Map<String, Integer> config = handle();

   private SvgDocumentRenderer() {
   }

   public static int[] handle(InputStream var0, int var1, int var2, boolean var3, int var4) throws Exception {
      Element var5 = handle(var0);
      int var6 = Math.max(1, var4);
      BufferedImage var7 = handle(var5, var1 * var6, var2 * var6, var3);
      int[] var8 = handle(var7, var1, var2, var6);
      handle(var8);
      return var8;
   }

   private static Element handle(InputStream var0) throws Exception {
      DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
      var1.setNamespaceAware(false);
      var1.setExpandEntityReferences(false);
      var1.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
      var1.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
      var1.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      var1.setFeature("http://xml.org/sax/features/external-general-entities", false);
      var1.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      DocumentBuilder var2 = var1.newDocumentBuilder();
      var2.setErrorHandler(new ErrorHandler() {
         @Override
         public void warning(SAXParseException var1) {
         }

         @Override
         public void error(SAXParseException var1) {
         }

         @Override
         public void fatalError(SAXParseException var1) throws SAXParseException {
            throw var1;
         }
      });
      return var2.parse(new InputSource(var0)).getDocumentElement();
   }

   private static BufferedImage handle(Element var0, int var1, int var2, boolean var3) {
      float[] var4 = handle(var0);
      BufferedImage var5 = new BufferedImage(var1, var2, 3);
      Graphics2D var6 = var5.createGraphics();
      var6.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      var6.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
      var6.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      var6.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      var6.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      float var7 = Math.min(var1 / var4[2], var2 / var4[3]);
      var6.translate((var1 - var4[2] * var7) * 0.5F, (var2 - var4[3] * var7) * 0.5F);
      var6.scale(var7, var7);
      var6.translate(-var4[0], -var4[1]);
      var6.transform(process(var0.getAttribute("transform")));
      HashMap var8 = new HashMap();
      handle(var0, var8);
      handle(var0, var6, SvgDocumentRenderer.DataRecord.DEFAULT.derive(var0), var3, var8);
      var6.dispose();
      return var5;
   }

   private static void handle(Element var0, Map<String, Element> var1) {
      NodeList var2 = var0.getChildNodes();

      for (int var3 = 0; var3 < var2.getLength(); var3++) {
         if (var2.item(var3) instanceof Element var4) {
            String var6 = var4.getAttribute("id");
            if (!var6.isBlank()) {
               var1.putIfAbsent(var6, var4);
            }

            handle(var4, var1);
         }
      }
   }

   private static float[] handle(Element var0) {
      List<Double> var1 = compute(var0.getAttribute("viewBox"));
      return var1.size() >= 4 && var1.get(2) > 0.0 && var1.get(3) > 0.0
         ? new float[]{
            ((Double)var1.get(0)).floatValue(), ((Double)var1.get(1)).floatValue(), ((Double)var1.get(2)).floatValue(), ((Double)var1.get(3)).floatValue()
         }
         : new float[]{0.0F, 0.0F, Math.max(1.0F, compute(var0.getAttribute("width"), 24.0F)), Math.max(1.0F, compute(var0.getAttribute("height"), 24.0F))};
   }

   private static void handle(Element var0, Graphics2D var1, SvgDocumentRenderer.DataRecord var2, boolean var3, Map<String, Element> var4) {
      NodeList var5 = var0.getChildNodes();

      for (int var6 = 0; var6 < var5.getLength(); var6++) {
         if (var5.item(var6) instanceof Element var7) {
            process(var7, var1, var2, var3, var4);
         }
      }
   }

   private static void process(Element var0, Graphics2D var1, SvgDocumentRenderer.DataRecord var2, boolean var3, Map<String, Element> var4) {
      String var5 = handle(var0.getTagName());
      switch (var5) {
         case "defs":
         case "title":
         case "desc":
         case "metadata":
         case "style":
         case "lineargradient":
         case "radialgradient":
         case "clippath":
         case "mask":
         case "filter":
         case "symbol":
         case "marker":
            return;
         default:
            SvgDocumentRenderer.DataRecord var11 = var2.derive(var0);
            if (var11.visible()) {
               Graphics2D var12 = (Graphics2D)var1.create();
               var12.transform(process(var0.getAttribute("transform")));

               Shape var8 = switch (var5) {
                  case "path" -> handle(var0.getAttribute("d"), var11.evenOdd());
                  case "rect" -> process(var0);
                  case "circle" -> compute(var0);
                  case "ellipse" -> resolve(var0);
                  case "line" -> update(var0);
                  case "polygon" -> handle(var0.getAttribute("points"), true, var11.evenOdd());
                  case "polyline" -> handle(var0.getAttribute("points"), false, var11.evenOdd());
                  default -> null;
               };
               if (var8 != null) {
                  handle(var12, var8, var11, var3, var4);
               }

               handle(var0, var12, var11, var3, var4);
               var12.dispose();
            }
      }
   }

   private static String handle(String var0) {
      String var1 = var0.toLowerCase(Locale.ROOT);
      int var2 = var1.indexOf(58);
      return var2 >= 0 ? var1.substring(var2 + 1) : var1;
   }

   private static void handle(Graphics2D var0, Shape var1, SvgDocumentRenderer.DataRecord var2, boolean var3, Map<String, Element> var4) {
      Paint var5 = handle(var2.fill(), var2.fillAlpha(), var3, var1, var4);
      if (var5 != null) {
         var0.setPaint(var5);
         var0.fill(var1);
      }

      Paint var6 = handle(var2.stroke(), var2.strokeAlpha(), var3, var1, var4);
      if (var6 != null && !(var2.strokeWidth() <= 0.0F)) {
         byte var7 = switch (var2.lineCap().toLowerCase(Locale.ROOT)) {
            case "round" -> 1;
            case "square" -> 2;
            default -> 0;
         };

         byte var11 = switch (var2.lineJoin().toLowerCase(Locale.ROOT)) {
            case "round" -> 1;
            case "bevel" -> 2;
            default -> 0;
         };
         float var13 = Math.max(1.0F, var2.miterLimit());
         float[] var14 = var2.dashArray();
         var0.setPaint(var6);
         var0.setStroke(
            var14 == null
               ? new BasicStroke(var2.strokeWidth(), var7, var11, var13)
               : new BasicStroke(var2.strokeWidth(), var7, var11, var13, var14, var2.dashOffset())
         );
         var0.draw(var1);
      }
   }

   private static Paint handle(String var0, float var1, boolean var2, Shape var3, Map<String, Element> var4) {
      if (var0 != null && !var0.isBlank() && !var0.equalsIgnoreCase("none") && !(var1 <= 0.0F)) {
         Matcher var5 = context.matcher(var0);
         if (var5.find()) {
            Element var6 = (Element)var4.get(var5.group(1));
            Paint var7 = var6 == null ? null : handle(var6, var1, var2, var3, var4);
            return var7 != null ? var7 : handle(var1);
         } else {
            return handle(var0, var1, var2);
         }
      } else {
         return null;
      }
   }

   private static Paint handle(Element var0, float var1, boolean var2, Shape var3, Map<String, Element> var4) {
      String var5 = handle(var0.getTagName());
      boolean var6 = var5.equals("radialgradient");
      if (!var6 && !var5.equals("lineargradient")) {
         return null;
      }

      List var7 = handle(var0, var4, 0);
      if (var7.isEmpty()) {
         return null;
      }

      int var8 = var7.size();
      float[] var9 = new float[var8];
      Color[] var10 = new Color[var8];
      float var11 = 0.0F;

      for (int var12 = 0; var12 < var8; var12++) {
         Element var13 = (Element)var7.get(var12);
         float var14 = compute(compute(handle(var13, var4, "offset", "0"), 0.0F));
         if (handle(var13, var4, "offset", "0").trim().endsWith("%")) {
            var14 = compute(var14 / 100.0F);
         }

         var11 = var12 == 0 ? var14 : Math.max(var11 + 1.0E-5F, var14);
         var9[var12] = compute(var11);
         float var15 = var1 * process(handle(var13, var4, "stop-opacity", "1"), 1.0F);
         var10[var12] = handle(handle(var13, var4, "stop-color", "black"), var15, var2) instanceof Color var17 ? var17 : handle(var1);
      }

      if (var8 == 1) {
         return var10[0];
      }

      boolean var22 = handle(var0, var4, "gradientUnits", "objectBoundingBox").equalsIgnoreCase("userSpaceOnUse");
      Rectangle2D var23 = var3.getBounds2D();
      if (!(var23.getWidth() <= 0.0) && !(var23.getHeight() <= 0.0)) {
         AffineTransform var24 = process(handle(var0, var4, "gradientTransform", ""));
         AffineTransform var25 = new AffineTransform();
         if (!var22) {
            var25.translate(var23.getX(), var23.getY());
            var25.scale(var23.getWidth(), var23.getHeight());
         }

         var25.concatenate(var24);

         try {
            if (var6) {
               float var27 = compute(handle(var0, var4, "cx", "0.5"), 0.5F);
               float var29 = compute(handle(var0, var4, "cy", "0.5"), 0.5F);
               float var30 = compute(handle(var0, var4, "r", "0.5"), 0.5F);
               if (var30 <= 0.0F) {
                  return var10[var8 - 1];
               }

               float var31 = compute(handle(var0, var4, "fx", Float.toString(var27)), var27);
               float var20 = compute(handle(var0, var4, "fy", Float.toString(var29)), var29);
               return new RadialGradientPaint(
                  new java.awt.geom.Point2D.Float(var27, var29),
                  var30,
                  new java.awt.geom.Point2D.Float(var31, var20),
                  var9,
                  var10,
                  CycleMethod.NO_CYCLE,
                  ColorSpaceType.SRGB,
                  var25
               );
            } else {
               float var26 = compute(handle(var0, var4, "x1", "0"), 0.0F);
               float var28 = compute(handle(var0, var4, "y1", "0"), 0.0F);
               float var18 = compute(handle(var0, var4, "x2", "1"), 1.0F);
               float var19 = compute(handle(var0, var4, "y2", "0"), 0.0F);
               return var26 == var18 && var28 == var19
                  ? var10[var8 - 1]
                  : new LinearGradientPaint(
                     new java.awt.geom.Point2D.Float(var26, var28),
                     new java.awt.geom.Point2D.Float(var18, var19),
                     var9,
                     var10,
                     CycleMethod.NO_CYCLE,
                     ColorSpaceType.SRGB,
                     var25
                  );
            }
         } catch (RuntimeException var21) {
            return var10[0];
         }
      } else {
         return var10[0];
      }
   }

   private static List<Element> handle(Element var0, Map<String, Element> var1, int var2) {
      ArrayList var3 = new ArrayList();
      NodeList var4 = var0.getChildNodes();

      for (int var5 = 0; var5 < var4.getLength(); var5++) {
         if (var4.item(var5) instanceof Element var6 && handle(var6.getTagName()).equals("stop")) {
            var3.add(var6);
         }
      }

      if (var3.isEmpty() && var2 <= 4) {
         Element var8 = process(var0, var1);
         return var8 == null ? var3 : handle(var8, var1, var2 + 1);
      } else {
         return var3;
      }
   }

   private static Element process(Element var0, Map<String, Element> var1) {
      String var2 = var0.getAttribute("href");
      if (var2.isBlank()) {
         var2 = var0.getAttribute("xlink:href");
      }

      return var2.startsWith("#") ? (Element)var1.get(var2.substring(1)) : null;
   }

   private static String handle(Element var0, Map<String, Element> var1, String var2, String var3) {
      Element var4 = var0;

      for (int var5 = 0; var4 != null && var5 < 5; var5++) {
         String var6 = var4.getAttribute(var2);
         if (!var6.isBlank()) {
            return var6;
         }

         var4 = process(var4, var1);
      }

      return var3;
   }

   private static Color handle(float var0) {
      return new Color(255, 255, 255, process(var0));
   }

   private static Paint handle(String var0, float var1, boolean var2) {
      if (var0 == null || var0.isBlank() || var0.equalsIgnoreCase("none") || var1 <= 0.0F) {
         return null;
      }

      if (!var2 && !var0.equalsIgnoreCase("currentColor")) {
         String var3 = var0.trim().toLowerCase(Locale.ROOT);

         try {
            if (var3.startsWith("#")) {
               return handle(var3.substring(1), var1);
            }

            if (var3.startsWith("rgb")) {
               List var7 = compute(var3);
               if (var7.size() >= 3) {
                  float var5 = var7.size() >= 4 ? ((Double)var7.get(3)).floatValue() : 1.0F;
                  return new Color(handle((Double)var7.get(0)), handle((Double)var7.get(1)), handle((Double)var7.get(2)), process(var1 * compute(var5)));
               } else {
                  return handle(var1);
               }
            } else {
               if (var3.equals("transparent")) {
                  return null;
               }

               Integer var4 = config.get(var3);
               return var4 == null ? handle(var1) : new Color(var4 >> 16 & 0xFF, var4 >> 8 & 0xFF, var4 & 0xFF, process(var1));
            }
         } catch (RuntimeException var6) {
            return handle(var1);
         }
      } else {
         return handle(var1);
      }
   }

   private static Color handle(String var0, float var1) {
      String var2 = var0;
      if (var2.length() == 3 || var2.length() == 4) {
         StringBuilder var3 = new StringBuilder(var2.length() * 2);

         for (int var4 = 0; var4 < var2.length(); var4++) {
            var3.append(var2.charAt(var4)).append(var2.charAt(var4));
         }

         var2 = var3.toString();
      }

      long var5 = Long.parseLong(var2, 16);
      return var2.length() == 8
         ? new Color((int)(var5 >> 24) & 0xFF, (int)(var5 >> 16) & 0xFF, (int)(var5 >> 8) & 0xFF, process(var1 * ((float)(var5 & 255L) / 255.0F)))
         : new Color((int)(var5 >> 16) & 0xFF, (int)(var5 >> 8) & 0xFF, (int)var5 & 0xFF, process(var1));
   }

   private static int[] handle(BufferedImage var0, int var1, int var2, int var3) {
      int[] var4 = ((DataBufferInt)var0.getRaster().getDataBuffer()).getData();
      int var5 = var0.getWidth();
      int[] var6 = new int[var1 * var2];
      if (var3 == 1) {
         System.arraycopy(var4, 0, var6, 0, Math.min(var4.length, var6.length));
         return var6;
      }

      int var7 = var3 * var3;
      int var8 = var7 / 2;

      for (int var9 = 0; var9 < var2; var9++) {
         int var10 = var9 * var3 * var5;

         for (int var11 = 0; var11 < var1; var11++) {
            int var12 = 0;
            int var13 = 0;
            int var14 = 0;
            int var15 = 0;
            int var16 = var10 + var11 * var3;

            for (int var17 = 0; var17 < var3; var17++) {
               int var18 = var16 + var17 * var5;

               for (int var19 = 0; var19 < var3; var19++) {
                  int var20 = var4[var18 + var19];
                  var12 += var20 >>> 24 & 0xFF;
                  var13 += var20 >> 16 & 0xFF;
                  var14 += var20 >> 8 & 0xFF;
                  var15 += var20 & 0xFF;
               }
            }

            var6[var9 * var1 + var11] = (var12 + var8) / var7 << 24 | (var13 + var8) / var7 << 16 | (var14 + var8) / var7 << 8 | (var15 + var8) / var7;
         }
      }

      return var6;
   }

   private static void handle(int[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         int var2 = var0[var1];
         int var3 = var2 >>> 24 & 0xFF;
         if (var3 == 0) {
            var0[var1] = 0;
         } else if (var3 != 255) {
            int var4 = Math.min(255, ((var2 >> 16 & 0xFF) * 255 + var3 / 2) / var3);
            int var5 = Math.min(255, ((var2 >> 8 & 0xFF) * 255 + var3 / 2) / var3);
            int var6 = Math.min(255, ((var2 & 0xFF) * 255 + var3 / 2) / var3);
            var0[var1] = var3 << 24 | var4 << 16 | var5 << 8 | var6;
         }
      }
   }

   private static Shape process(Element var0) {
      float var1 = compute(var0.getAttribute("x"), 0.0F);
      float var2 = compute(var0.getAttribute("y"), 0.0F);
      float var3 = compute(var0.getAttribute("width"), 0.0F);
      float var4 = compute(var0.getAttribute("height"), 0.0F);
      float var5 = compute(var0.getAttribute("rx"), -1.0F);
      float var6 = compute(var0.getAttribute("ry"), -1.0F);
      if (var5 < 0.0F && var6 < 0.0F) {
         return new java.awt.geom.Rectangle2D.Float(var1, var2, var3, var4);
      }

      if (var5 < 0.0F) {
         var5 = var6;
      }

      if (var6 < 0.0F) {
         var6 = var5;
      }

      var5 = Math.min(var5, var3 * 0.5F);
      var6 = Math.min(var6, var4 * 0.5F);
      return new java.awt.geom.RoundRectangle2D.Float(var1, var2, var3, var4, var5 * 2.0F, var6 * 2.0F);
   }

   private static Shape compute(Element var0) {
      float var1 = compute(var0.getAttribute("cx"), 0.0F);
      float var2 = compute(var0.getAttribute("cy"), 0.0F);
      float var3 = compute(var0.getAttribute("r"), 0.0F);
      return new java.awt.geom.Ellipse2D.Float(var1 - var3, var2 - var3, var3 * 2.0F, var3 * 2.0F);
   }

   private static Shape resolve(Element var0) {
      float var1 = compute(var0.getAttribute("cx"), 0.0F);
      float var2 = compute(var0.getAttribute("cy"), 0.0F);
      float var3 = compute(var0.getAttribute("rx"), 0.0F);
      float var4 = compute(var0.getAttribute("ry"), 0.0F);
      return new java.awt.geom.Ellipse2D.Float(var1 - var3, var2 - var4, var3 * 2.0F, var4 * 2.0F);
   }

   private static Shape update(Element var0) {
      java.awt.geom.Path2D.Float var1 = new java.awt.geom.Path2D.Float();
      var1.moveTo(compute(var0.getAttribute("x1"), 0.0F), compute(var0.getAttribute("y1"), 0.0F));
      var1.lineTo(compute(var0.getAttribute("x2"), 0.0F), compute(var0.getAttribute("y2"), 0.0F));
      return var1;
   }

   private static Shape handle(String var0, boolean var1, boolean var2) {
      List var3 = compute(var0);
      java.awt.geom.Path2D.Float var4 = new java.awt.geom.Path2D.Float(var2 ? 0 : 1);
      if (var3.size() < 2) {
         return var4;
      }

      var4.moveTo((Double)var3.get(0), (Double)var3.get(1));

      for (byte var5 = 2; var5 + 1 < var3.size(); var5 += 2) {
         var4.lineTo((Double)var3.get(var5), (Double)var3.get(var5 + 1));
      }

      if (var1) {
         var4.closePath();
      }

      return var4;
   }

   static Path2D handle(String var0, boolean var1) {
      SvgDocumentRenderer.LayoutMetrics var2 = new SvgDocumentRenderer.LayoutMetrics(var0);
      java.awt.geom.Path2D.Double var3 = new java.awt.geom.Path2D.Double(var1 ? 0 : 1);
      char var4 = ' ';
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = 0.0;
      double var11 = 0.0;
      double var13 = 0.0;
      double var15 = 0.0;
      char var17 = ' ';

      while (var2.handle()) {
         if (var2.process()) {
            var4 = var2.compute();
         }

         if (var4 == ' ') {
            break;
         }

         boolean var18 = Character.isLowerCase(var4);
         char var19 = Character.toUpperCase(var4);
         if (var19 == 'Z') {
            var3.closePath();
            var5 = var9;
            var7 = var11;
            var17 = var4;
            var4 = ' ';
         } else {
            int var20 = handle(var19);
            if (var20 == 0 || !var2.handle(var20)) {
               break;
            }

            double[] var21 = var2.process(var20);
            switch (var19) {
               case 'A':
                  double var39 = var18 ? var5 + var21[5] : var21[5];
                  double var43 = var18 ? var7 + var21[6] : var21[6];
                  handle(var3, var5, var7, Math.abs(var21[0]), Math.abs(var21[1]), var21[2], var21[3] != 0.0, var21[4] != 0.0, var39, var43);
                  var5 = var39;
                  var7 = var43;
               case 'B':
               case 'D':
               case 'E':
               case 'F':
               case 'G':
               case 'I':
               case 'J':
               case 'K':
               case 'N':
               case 'O':
               case 'P':
               case 'R':
               case 'U':
               default:
                  break;
               case 'C':
                  double var38 = var18 ? var5 + var21[0] : var21[0];
                  double var42 = var18 ? var7 + var21[1] : var21[1];
                  double var45 = var18 ? var5 + var21[2] : var21[2];
                  double var47 = var18 ? var7 + var21[3] : var21[3];
                  double var30 = var18 ? var5 + var21[4] : var21[4];
                  double var32 = var18 ? var7 + var21[5] : var21[5];
                  var3.curveTo(var38, var42, var45, var47, var30, var32);
                  var13 = var45;
                  var15 = var47;
                  var5 = var30;
                  var7 = var32;
                  break;
               case 'H':
                  var5 = var18 ? var5 + var21[0] : var21[0];
                  var3.lineTo(var5, var7);
                  break;
               case 'L':
                  var5 = var18 ? var5 + var21[0] : var21[0];
                  var7 = var18 ? var7 + var21[1] : var21[1];
                  var3.lineTo(var5, var7);
                  break;
               case 'M':
                  double var37 = var18 ? var5 + var21[0] : var21[0];
                  double var41 = var18 ? var7 + var21[1] : var21[1];
                  if (var17 != ' ' && Character.toUpperCase(var17) != 'Z') {
                     var3.lineTo(var37, var41);
                  } else {
                     var3.moveTo(var37, var41);
                     var9 = var37;
                     var11 = var41;
                  }

                  var5 = var37;
                  var7 = var41;
                  var4 = (char)(var18 ? 108 : 76);
                  break;
               case 'Q':
                  double var36 = var18 ? var5 + var21[0] : var21[0];
                  double var24 = var18 ? var7 + var21[1] : var21[1];
                  double var26 = var18 ? var5 + var21[2] : var21[2];
                  double var28 = var18 ? var7 + var21[3] : var21[3];
                  var3.quadTo(var36, var24, var26, var28);
                  var13 = var36;
                  var15 = var24;
                  var5 = var26;
                  var7 = var28;
                  break;
               case 'S':
                  boolean var35 = Character.toUpperCase(var17) == 'C' || Character.toUpperCase(var17) == 'S';
                  double var40 = var35 ? var5 * 2.0 - var13 : var5;
                  double var44 = var35 ? var7 * 2.0 - var15 : var7;
                  double var46 = var18 ? var5 + var21[0] : var21[0];
                  double var48 = var18 ? var7 + var21[1] : var21[1];
                  double var31 = var18 ? var5 + var21[2] : var21[2];
                  double var33 = var18 ? var7 + var21[3] : var21[3];
                  var3.curveTo(var40, var44, var46, var48, var31, var33);
                  var13 = var46;
                  var15 = var48;
                  var5 = var31;
                  var7 = var33;
                  break;
               case 'T':
                  boolean var22 = Character.toUpperCase(var17) == 'Q' || Character.toUpperCase(var17) == 'T';
                  double var23 = var22 ? var5 * 2.0 - var13 : var5;
                  double var25 = var22 ? var7 * 2.0 - var15 : var7;
                  double var27 = var18 ? var5 + var21[0] : var21[0];
                  double var29 = var18 ? var7 + var21[1] : var21[1];
                  var3.quadTo(var23, var25, var27, var29);
                  var13 = var23;
                  var15 = var25;
                  var5 = var27;
                  var7 = var29;
                  break;
               case 'V':
                  var7 = var18 ? var7 + var21[0] : var21[0];
                  var3.lineTo(var5, var7);
            }

            var17 = var4;
         }
      }

      return var3;
   }

   private static int handle(char var0) {
      return switch (var0) {
         case 'A' -> 7;
         default -> 0;
         case 'C' -> 6;
         case 'H', 'V' -> 1;
         case 'L', 'M', 'T' -> 2;
         case 'Q', 'S' -> 4;
      };
   }

   private static void handle(
      Path2D var0, double var1, double var3, double var5, double var7, double var9, boolean var11, boolean var12, double var13, double var15
   ) {
      if (var5 != 0.0 && var7 != 0.0 && (var1 != var13 || var3 != var15)) {
         double var17 = Math.toRadians(var9 % 360.0);
         double var19 = Math.cos(var17);
         double var21 = Math.sin(var17);
         double var23 = (var1 - var13) / 2.0;
         double var25 = (var3 - var15) / 2.0;
         double var27 = var19 * var23 + var21 * var25;
         double var29 = -var21 * var23 + var19 * var25;
         double var31 = var27 * var27 / (var5 * var5) + var29 * var29 / (var7 * var7);
         if (var31 > 1.0) {
            double var33 = Math.sqrt(var31);
            var5 *= var33;
            var7 *= var33;
         }

         double var69 = var5 * var5 * var7 * var7 - var5 * var5 * var29 * var29 - var7 * var7 * var27 * var27;
         double var35 = var5 * var5 * var29 * var29 + var7 * var7 * var27 * var27;
         double var37 = (var11 == var12 ? -1 : 1) * Math.sqrt(Math.max(0.0, var69 / var35));
         double var39 = var37 * var5 * var29 / var7;
         double var41 = var37 * -var7 * var27 / var5;
         double var43 = var19 * var39 - var21 * var41 + (var1 + var13) / 2.0;
         double var45 = var21 * var39 + var19 * var41 + (var3 + var15) / 2.0;
         double var47 = handle(1.0, 0.0, (var27 - var39) / var5, (var29 - var41) / var7);
         double var49 = handle((var27 - var39) / var5, (var29 - var41) / var7, (-var27 - var39) / var5, (-var29 - var41) / var7);
         if (!var12 && var49 > 0.0) {
            var49 -= Math.PI * 2;
         }

         if (var12 && var49 < 0.0) {
            var49 += Math.PI * 2;
         }

         int var51 = Math.max(1, (int)Math.ceil(Math.abs(var49) / (Math.PI / 2)));
         double var52 = var49 / var51;

         for (int var54 = 0; var54 < var51; var54++) {
            double var55 = var47 + var54 * var52;
            double var57 = var55 + var52;
            double var59 = 1.3333333333333333 * Math.tan((var57 - var55) / 4.0);
            double var61 = Math.cos(var55) - var59 * Math.sin(var55);
            double var63 = Math.sin(var55) + var59 * Math.cos(var55);
            double var65 = Math.cos(var57) + var59 * Math.sin(var57);
            double var67 = Math.sin(var57) - var59 * Math.cos(var57);
            var0.curveTo(
               handle(var61, var63, var5, var7, var19, var21, var43),
               process(var61, var63, var5, var7, var19, var21, var45),
               handle(var65, var67, var5, var7, var19, var21, var43),
               process(var65, var67, var5, var7, var19, var21, var45),
               handle(Math.cos(var57), Math.sin(var57), var5, var7, var19, var21, var43),
               process(Math.cos(var57), Math.sin(var57), var5, var7, var19, var21, var45)
            );
         }
      } else {
         var0.lineTo(var13, var15);
      }
   }

   private static double handle(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      return var12 + var4 * var8 * var0 - var6 * var10 * var2;
   }

   private static double process(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      return var12 + var4 * var10 * var0 + var6 * var8 * var2;
   }

   private static double handle(double var0, double var2, double var4, double var6) {
      double var8 = var0 * var4 + var2 * var6;
      double var10 = Math.sqrt((var0 * var0 + var2 * var2) * (var4 * var4 + var6 * var6));
      double var12 = Math.acos(Math.max(-1.0, Math.min(1.0, var8 / var10)));
      return var0 * var6 - var2 * var4 < 0.0 ? -var12 : var12;
   }

   private static AffineTransform process(String var0) {
      AffineTransform var1 = new AffineTransform();
      Matcher var2 = data.matcher(var0 == null ? "" : var0);

      while (var2.find()) {
         List var3 = compute(var2.group(2));
         switch (var2.group(1).toLowerCase(Locale.ROOT)) {
            case "matrix":
               if (var3.size() >= 6) {
                  var1.concatenate(
                     new AffineTransform(
                        (Double)var3.get(0), (Double)var3.get(1), (Double)var3.get(2), (Double)var3.get(3), (Double)var3.get(4), (Double)var3.get(5)
                     )
                  );
               }
               break;
            case "translate":
               if (!var3.isEmpty()) {
                  var1.translate((Double)var3.get(0), var3.size() > 1 ? (Double)var3.get(1) : 0.0);
               }
               break;
            case "scale":
               if (!var3.isEmpty()) {
                  var1.scale((Double)var3.get(0), var3.size() > 1 ? (Double)var3.get(1) : (Double)var3.get(0));
               }
               break;
            case "rotate":
               if (!var3.isEmpty()) {
                  double var6 = Math.toRadians((Double)var3.get(0));
                  if (var3.size() >= 3) {
                     var1.rotate(var6, (Double)var3.get(1), (Double)var3.get(2));
                  } else {
                     var1.rotate(var6);
                  }
               }
               break;
            case "skewx":
               if (!var3.isEmpty()) {
                  var1.shear(Math.tan(Math.toRadians((Double)var3.get(0))), 0.0);
               }
               break;
            case "skewy":
               if (!var3.isEmpty()) {
                  var1.shear(0.0, Math.tan(Math.toRadians((Double)var3.get(0))));
               }
         }
      }

      return var1;
   }

   private static int handle(double var0) {
      return Math.max(0, Math.min(255, (int)Math.round(var0)));
   }

   private static int process(float var0) {
      return Math.max(0, Math.min(255, Math.round(var0 * 255.0F)));
   }

   static float compute(float var0) {
      return var0 < 0.0F ? 0.0F : Math.min(var0, 1.0F);
   }

   static float process(String var0, float var1) {
      try {
         return Float.parseFloat(var0.trim());
      } catch (RuntimeException var3) {
         return var1;
      }
   }

   static float compute(String var0, float var1) {
      if (var0 != null && !var0.isBlank()) {
         Matcher var2 = instance.matcher(var0);
         if (!var2.find()) {
            return var1;
         }

         float var3 = Float.parseFloat(var2.group());
         return var0.trim().endsWith("%") ? var3 / 100.0F : var3;
      } else {
         return var1;
      }
   }

   static List<Double> compute(String var0) {
      ArrayList var1 = new ArrayList();
      Matcher var2 = instance.matcher(var0 == null ? "" : var0);

      while (var2.find()) {
         var1.add(Double.parseDouble(var2.group()));
      }

      return var1;
   }

   private static Map<String, Integer> handle() {
      HashMap var0 = new HashMap();
      var0.put("black", 0);
      var0.put("white", 16777215);
      var0.put("red", 16711680);
      var0.put("lime", 65280);
      var0.put("green", 32768);
      var0.put("blue", 255);
      var0.put("yellow", 16776960);
      var0.put("cyan", 65535);
      var0.put("aqua", 65535);
      var0.put("magenta", 16711935);
      var0.put("fuchsia", 16711935);
      var0.put("silver", 12632256);
      var0.put("gray", 8421504);
      var0.put("grey", 8421504);
      var0.put("maroon", 8388608);
      var0.put("olive", 8421376);
      var0.put("purple", 8388736);
      var0.put("teal", 32896);
      var0.put("navy", 128);
      var0.put("orange", 16753920);
      var0.put("pink", 16761035);
      var0.put("brown", 10824234);
      var0.put("gold", 16766720);
      var0.put("indigo", 4915330);
      var0.put("violet", 15631086);
      var0.put("crimson", 14423100);
      var0.put("salmon", 16416882);
      var0.put("khaki", 15787660);
      var0.put("lavender", 15132410);
      var0.put("beige", 16119260);
      var0.put("ivory", 16777200);
      var0.put("turquoise", 4251856);
      var0.put("darkgray", 11119017);
      var0.put("darkgrey", 11119017);
      var0.put("lightgray", 13882323);
      var0.put("lightgrey", 13882323);
      var0.put("dimgray", 6908265);
      var0.put("dimgrey", 6908265);
      var0.put("whitesmoke", 16119285);
      return var0;
   }

   record DataRecord(
      String fill,
      String stroke,
      float opacity,
      float fillOpacity,
      float strokeOpacity,
      float strokeWidth,
      float miterLimit,
      float[] dashArray,
      float dashOffset,
      String lineCap,
      String lineJoin,
      String fillRule,
      boolean visible
   ) {
      static final SvgDocumentRenderer.DataRecord DEFAULT = new SvgDocumentRenderer.DataRecord(
         "black", "none", 1.0F, 1.0F, 1.0F, 1.0F, 4.0F, null, 0.0F, "butt", "miter", "nonzero", true
      );

      SvgDocumentRenderer.DataRecord derive(Element var1) {
         LinkedHashMap var2 = new LinkedHashMap();
         String var3 = var1.getAttribute("style");
         if (!var3.isBlank()) {
            for (String var7 : var3.split(";")) {
               int var8 = var7.indexOf(58);
               if (var8 > 0) {
                  var2.put(var7.substring(0, var8).trim().toLowerCase(Locale.ROOT), var7.substring(var8 + 1).trim());
               }
            }
         }

         String var9 = property(var1, var2, "display", "inline");
         String var10 = property(var1, var2, "visibility", "visible");
         return new SvgDocumentRenderer.DataRecord(
            property(var1, var2, "fill", this.fill),
            property(var1, var2, "stroke", this.stroke),
            this.opacity * SvgDocumentRenderer.process(property(var1, var2, "opacity", "1"), 1.0F),
            SvgDocumentRenderer.process(property(var1, var2, "fill-opacity", Float.toString(this.fillOpacity)), this.fillOpacity),
            SvgDocumentRenderer.process(property(var1, var2, "stroke-opacity", Float.toString(this.strokeOpacity)), this.strokeOpacity),
            SvgDocumentRenderer.compute(property(var1, var2, "stroke-width", Float.toString(this.strokeWidth)), this.strokeWidth),
            SvgDocumentRenderer.process(property(var1, var2, "stroke-miterlimit", Float.toString(this.miterLimit)), this.miterLimit),
            dashes(property(var1, var2, "stroke-dasharray", ""), this.dashArray),
            SvgDocumentRenderer.compute(property(var1, var2, "stroke-dashoffset", Float.toString(this.dashOffset)), this.dashOffset),
            property(var1, var2, "stroke-linecap", this.lineCap),
            property(var1, var2, "stroke-linejoin", this.lineJoin),
            property(var1, var2, "fill-rule", this.fillRule),
            this.visible && !var9.equalsIgnoreCase("none") && !var10.equalsIgnoreCase("hidden")
         );
      }

      private static float[] dashes(String var0, float[] var1) {
         if (var0 != null && !var0.isBlank()) {
            if (var0.trim().equalsIgnoreCase("none")) {
               return null;
            }

            List var2 = SvgDocumentRenderer.compute(var0);
            if (var2.isEmpty()) {
               return var1;
            }

            float var3 = 0.0F;
            float[] var4 = new float[var2.size()];

            for (int var5 = 0; var5 < var4.length; var5++) {
               var4[var5] = Math.max(0.0F, ((Double)var2.get(var5)).floatValue());
               var3 += var4[var5];
            }

            return var3 <= 0.0F ? null : var4;
         } else {
            return var1;
         }
      }

      float fillAlpha() {
         return SvgDocumentRenderer.compute(this.opacity * this.fillOpacity);
      }

      float strokeAlpha() {
         return SvgDocumentRenderer.compute(this.opacity * this.strokeOpacity);
      }

      boolean evenOdd() {
         return this.fillRule.equalsIgnoreCase("evenodd");
      }

      private static String property(Element var0, Map<String, String> var1, String var2, String var3) {
         String var4 = (String)var1.get(var2);
         if (var4 != null && !var4.isBlank()) {
            return var4;
         }

         var4 = var0.getAttribute(var2);
         return var4.isBlank() ? var3 : var4;
      }
   }

   static final class LayoutMetrics {
      private final String instance;
      private final Matcher data;
      private int context;

      LayoutMetrics(String var1) {
         this.instance = var1 == null ? "" : var1;
         this.data = SvgDocumentRenderer.instance.matcher(this.instance);
      }

      boolean handle() {
         this.resolve();
         return this.context < this.instance.length();
      }

      boolean process() {
         this.resolve();
         return this.context < this.instance.length() && Character.isLetter(this.instance.charAt(this.context));
      }

      char compute() {
         return this.instance.charAt(this.context++);
      }

      boolean handle(int var1) {
         int var2 = this.context;

         try {
            for (int var3 = 0; var3 < var1; var3++) {
               this.resolve();
               this.data.region(this.context, this.instance.length());
               if (!this.data.lookingAt()) {
                  return false;
               }

               this.context = this.data.end();
            }

            return true;
         } finally {
            this.context = var2;
         }
      }

      double[] process(int var1) {
         double[] var2 = new double[var1];

         for (int var3 = 0; var3 < var1; var3++) {
            this.resolve();
            this.data.region(this.context, this.instance.length());
            if (!this.data.lookingAt()) {
               throw new IllegalStateException("malformed svg path: expected a number at offset " + this.context);
            }

            var2[var3] = Double.parseDouble(this.data.group());
            this.context = this.data.end();
         }

         return var2;
      }

      private void resolve() {
         while (this.context < this.instance.length()) {
            char var1 = this.instance.charAt(this.context);
            if (Character.isWhitespace(var1) || var1 == ',') {
               this.context++;
               continue;
            }
            break;
         }
      }
   }
}
