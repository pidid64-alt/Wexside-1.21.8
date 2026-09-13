package ru.wild.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public class InputNames {
   public static final Map<String, Integer> instance = new HashMap<>();
   public static final Map<Integer, String> data = new HashMap<>();
   public static MinecraftClient context = MinecraftClient.getInstance();

   public static boolean handle(int var0) {
      return InputUtil.isKeyPressed(context.getWindow().getHandle(), var0);
   }

   public static String process(int var0) {
      if (var0 == -1) {
         return "KEY";
      } else if (var0 == -200) {
         return "Wheel Up";
      } else if (var0 == -201) {
         return "Wheel Down";
      } else if (var0 == -100) {
         return "Mouse Left";
      } else if (var0 == -101) {
         return "Mouse Right";
      } else if (var0 == -102) {
         return "Mouse Middle";
      } else if (var0 == -103) {
         return "Mouse 4";
      } else if (var0 == -104) {
         return "Mouse 5";
      } else if (var0 == -105) {
         return "Mouse 6";
      } else if (var0 == -106) {
         return "Mouse 7";
      } else if (var0 == -107) {
         return "Mouse 8";
      } else if (var0 == -108) {
         return "Mouse 9";
      } else if (var0 == 32) {
         return "Space";
      } else if (var0 == 39) {
         return "Apostrophe";
      } else if (var0 == 44) {
         return "Comma";
      } else if (var0 == 45) {
         return "Minus";
      } else if (var0 == 46) {
         return "Period";
      } else if (var0 == 47) {
         return "Slash";
      } else if (var0 == 48) {
         return "0";
      } else if (var0 == 49) {
         return "1";
      } else if (var0 == 50) {
         return "2";
      } else if (var0 == 51) {
         return "3";
      } else if (var0 == 52) {
         return "4";
      } else if (var0 == 53) {
         return "5";
      } else if (var0 == 54) {
         return "6";
      } else if (var0 == 55) {
         return "7";
      } else if (var0 == 56) {
         return "8";
      } else if (var0 == 57) {
         return "9";
      } else if (var0 == 59) {
         return "SemiColon";
      } else if (var0 == 61) {
         return "Equal";
      } else if (var0 == 65) {
         return "A";
      } else if (var0 == 66) {
         return "B";
      } else if (var0 == 67) {
         return "C";
      } else if (var0 == 68) {
         return "D";
      } else if (var0 == 69) {
         return "E";
      } else if (var0 == 70) {
         return "F";
      } else if (var0 == 71) {
         return "G";
      } else if (var0 == 72) {
         return "H";
      } else if (var0 == 73) {
         return "I";
      } else if (var0 == 74) {
         return "J";
      } else if (var0 == 75) {
         return "K";
      } else if (var0 == 76) {
         return "L";
      } else if (var0 == 77) {
         return "M";
      } else if (var0 == 78) {
         return "N";
      } else if (var0 == 79) {
         return "O";
      } else if (var0 == 80) {
         return "P";
      } else if (var0 == 81) {
         return "Q";
      } else if (var0 == 82) {
         return "R";
      } else if (var0 == 83) {
         return "S";
      } else if (var0 == 84) {
         return "T";
      } else if (var0 == 85) {
         return "U";
      } else if (var0 == 86) {
         return "V";
      } else if (var0 == 87) {
         return "W";
      } else if (var0 == 88) {
         return "X";
      } else if (var0 == 89) {
         return "Y";
      } else if (var0 == 90) {
         return "Z";
      } else if (var0 == 91) {
         return "LeftBracket";
      } else if (var0 == 92) {
         return "BackSlash";
      } else if (var0 == 93) {
         return "RightBracket";
      } else if (var0 == 96) {
         return "GraveAccent";
      } else if (var0 == 161) {
         return "World1";
      } else if (var0 == 162) {
         return "World2";
      } else if (var0 == 256) {
         return "Escape";
      } else if (var0 == 257) {
         return "Enter";
      } else if (var0 == 258) {
         return "Tab";
      } else if (var0 == 259) {
         return "BackSpace";
      } else if (var0 == 260) {
         return "Insert";
      } else if (var0 == 261) {
         return "Delete";
      } else if (var0 == 262) {
         return "Right";
      } else if (var0 == 263) {
         return "Left";
      } else if (var0 == 264) {
         return "Down";
      } else if (var0 == 265) {
         return "Up";
      } else if (var0 == 266) {
         return "PageUp";
      } else if (var0 == 267) {
         return "PageDown";
      } else if (var0 == 268) {
         return "Home";
      } else if (var0 == 269) {
         return "End";
      } else if (var0 == 280) {
         return "CapsLock";
      } else if (var0 == 281) {
         return "ScrollLock";
      } else if (var0 == 282) {
         return "NumLock";
      } else if (var0 == 283) {
         return "PrintScreen";
      } else if (var0 == 284) {
         return "Pause";
      } else if (var0 == 290) {
         return "F1";
      } else if (var0 == 291) {
         return "F2";
      } else if (var0 == 292) {
         return "F3";
      } else if (var0 == 293) {
         return "F4";
      } else if (var0 == 294) {
         return "F5";
      } else if (var0 == 295) {
         return "F6";
      } else if (var0 == 296) {
         return "F7";
      } else if (var0 == 297) {
         return "F8";
      } else if (var0 == 298) {
         return "F9";
      } else if (var0 == 299) {
         return "F10";
      } else if (var0 == 300) {
         return "F11";
      } else if (var0 == 301) {
         return "F12";
      } else if (var0 == 302) {
         return "F13";
      } else if (var0 == 303) {
         return "F14";
      } else if (var0 == 304) {
         return "F15";
      } else if (var0 == 305) {
         return "F16";
      } else if (var0 == 306) {
         return "F17";
      } else if (var0 == 307) {
         return "F18";
      } else if (var0 == 308) {
         return "F19";
      } else if (var0 == 309) {
         return "F20";
      } else if (var0 == 310) {
         return "F21";
      } else if (var0 == 311) {
         return "F22";
      } else if (var0 == 312) {
         return "F23";
      } else if (var0 == 313) {
         return "F24";
      } else if (var0 == 314) {
         return "F25";
      } else if (var0 == 320) {
         return "NUM 0";
      } else if (var0 == 321) {
         return "NUM 1";
      } else if (var0 == 322) {
         return "NUM 2";
      } else if (var0 == 323) {
         return "NUM 3";
      } else if (var0 == 324) {
         return "NUM 4";
      } else if (var0 == 325) {
         return "NUM 5";
      } else if (var0 == 326) {
         return "NUM 6";
      } else if (var0 == 327) {
         return "NUM 7";
      } else if (var0 == 328) {
         return "NUM 8";
      } else if (var0 == 329) {
         return "NUM 9";
      } else if (var0 == 330) {
         return "Decimal";
      } else if (var0 == 331) {
         return "Divine";
      } else if (var0 == 332) {
         return "Multiply";
      } else if (var0 == 333) {
         return "Subtract";
      } else if (var0 == 334) {
         return "Add";
      } else if (var0 == 335) {
         return "Enter";
      } else if (var0 == 336) {
         return "Equal";
      } else if (var0 == 340) {
         return "LeftShift";
      } else if (var0 == 341) {
         return "LeftControl";
      } else if (var0 == 342) {
         return "LeftAlt";
      } else if (var0 == 343) {
         return "LeftSuper";
      } else if (var0 == 344) {
         return "RightShift";
      } else if (var0 == 345) {
         return "RightControl";
      } else if (var0 == 346) {
         return "RightAlt";
      } else if (var0 == 347) {
         return "RightSuper";
      } else {
         return var0 == 348 ? "Menu" : "error";
      }
   }

   private static void handle() {
      instance.put("A", 65);
      instance.put("B", 66);
      instance.put("C", 67);
      instance.put("D", 68);
      instance.put("E", 69);
      instance.put("F", 70);
      instance.put("G", 71);
      instance.put("H", 72);
      instance.put("I", 73);
      instance.put("J", 74);
      instance.put("K", 75);
      instance.put("L", 76);
      instance.put("M", 77);
      instance.put("N", 78);
      instance.put("O", 79);
      instance.put("P", 80);
      instance.put("Q", 81);
      instance.put("R", 82);
      instance.put("S", 83);
      instance.put("T", 84);
      instance.put("U", 85);
      instance.put("V", 86);
      instance.put("W", 87);
      instance.put("X", 88);
      instance.put("Y", 89);
      instance.put("Z", 90);
      instance.put("0", 48);
      instance.put("1", 49);
      instance.put("2", 50);
      instance.put("3", 51);
      instance.put("4", 52);
      instance.put("5", 53);
      instance.put("6", 54);
      instance.put("7", 55);
      instance.put("8", 56);
      instance.put("9", 57);
      instance.put("F1", 290);
      instance.put("F2", 291);
      instance.put("F3", 292);
      instance.put("F4", 293);
      instance.put("F5", 294);
      instance.put("F6", 295);
      instance.put("F7", 296);
      instance.put("F8", 297);
      instance.put("F9", 298);
      instance.put("F10", 299);
      instance.put("F11", 300);
      instance.put("F12", 301);
      instance.put("NUMPAD1", 321);
      instance.put("NUMPAD2", 322);
      instance.put("NUMPAD3", 323);
      instance.put("NUMPAD4", 324);
      instance.put("NUMPAD5", 325);
      instance.put("NUMPAD6", 326);
      instance.put("NUMPAD7", 327);
      instance.put("NUMPAD8", 328);
      instance.put("NUMPAD9", 329);
      instance.put("SPACE", 32);
      instance.put("ENTER", 257);
      instance.put("ESCAPE", 256);
      instance.put("HOME", 268);
      instance.put("INSERT", 260);
      instance.put("DELETE", 261);
      instance.put("END", 269);
      instance.put("PAGEUP", 266);
      instance.put("PAGEDOWN", 267);
      instance.put("RIGHT", 262);
      instance.put("LEFT", 263);
      instance.put("DOWN", 264);
      instance.put("UP", 265);
      instance.put("RIGHT_SHIFT", 344);
      instance.put("LEFT_SHIFT", 340);
      instance.put("RIGHT_CONTROL", 345);
      instance.put("LEFT_CONTROL", 341);
      instance.put("RIGHT_ALT", 346);
      instance.put("LEFT_ALT", 342);
      instance.put("RIGHT_SUPER", 347);
      instance.put("LEFT_SUPER", 343);
      instance.put("MENU", 348);
      instance.put("CAPS_LOCK", 280);
      instance.put("NUM_LOCK", 282);
      instance.put("SCROLL_LOCK", 281);
      instance.put("KP_DECIMAL", 330);
      instance.put("KP_DIVIDE", 331);
      instance.put("KP_MULTIPLY", 332);
      instance.put("KP_SUBTRACT", 333);
      instance.put("KP_PLUS", 334);
      instance.put("KP_ENTER", 335);
      instance.put("KP_EQUAL", 336);
      instance.put("'", 39);
      instance.put("/", 47);
      instance.put("-", 45);
      instance.put("+", 61);
      instance.put("BACK", 259);
      instance.put("BACKSLASH", 92);
      instance.put(".", 46);
      instance.put("COMMA", 44);
      instance.put("PAUSE", 284);
   }

   private static void process() {
      for (Entry var1 : instance.entrySet()) {
         data.put((Integer)var1.getValue(), (String)var1.getKey());
      }
   }

   static {
      handle();
      process();
   }
}
