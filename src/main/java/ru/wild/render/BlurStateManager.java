package ru.wild.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import org.wild.module.api.Module;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.util.math.EaseTimer;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunction;
import ru.wild.util.math.ScalarAnimator;
import ru.wild.util.math.SmoothTimer;
import ru.wild.util.math.TimedEasingState;
import ru.wild.util.render.RenderRuntimeContext;

public class BlurStateManager {
   public static MinecraftClient instance = MinecraftClient.getInstance();
   public static SmoothTimer data = new EaseTimer(200, 1.0);
   public static SmoothTimer context = new EaseTimer(500, 1.0);
   public static SmoothTimer config = new EaseTimer(500, 1.0);
   public static SmoothTimer state = new EaseTimer(500, 1.0);
   public static SmoothTimer cache = new EaseTimer(1000, 1.0);
   public static BooleanSetting output = new BooleanSetting("Блюр нада?", true);
   public static TimedEasingState current = new TimedEasingState(EasingFunction.EASE_OUT_SINE, 1500L);
   public static ScalarAnimator active = new ScalarAnimator();
   public static EasedDoubleAnimator mode = new EasedDoubleAnimator();
   public static EasedDoubleAnimator selection = new EasedDoubleAnimator();
   public static EasedDoubleAnimator enabled = new EasedDoubleAnimator();
   public static EasedDoubleAnimator renderer = new EasedDoubleAnimator();
   public static EasedDoubleAnimator handler = new EasedDoubleAnimator();
   public static ColorSetting animationDraw = null;
   public static float pointEncode = 0.0F;
   public static float animator = 0.0F;
   public static boolean source = false;
   public static boolean target = false;
   public static boolean pending = false;
   public static KeybindSetting previous = null;
   public static StringSetting latest = null;
   public static NumberSetting summary = null;
   public static Module matrixBlend = null;
   public static float vectorMatch = 0.0F;
   public static float itemProject = 0.0F;
   public static float responseCompute = 0.0F;
   public static String providerFetch = "";
   public static boolean profileDraw = false;
   public static long vectorPerform = 0L;
   public static boolean eventAttach = false;
   public static long serverRead = 0L;
   public static final int positionAdvance = -200;
   public static final int frameCheck = -201;
   public static boolean moduleCollect = false;
   public static float providerClose;
   public static float presetSave;
   public static float windowConvert;
   public static float presetWrite;
   public static int colorMeasure = 0;
   public static int animationSchedule = 0;
   public static ModuleCategory[] rendererScan;
   public static ThemePalette sourceBuild;
   public static ThemePalette outputCollapse;
   public static ThemePalette[] profileInvoke;
   public static ModuleCategory sourceSchedule;
   public static List<Module> timerRender;
   private static RenderRuntimeContext actionRead;
   public static Set<Module> scaleSave = new HashSet<>();
   public static Map<Module, EasedDoubleAnimator> colorCompute = new HashMap<>();
   public static Map<Module, EasedDoubleAnimator> scaleAdapt = new HashMap<>();
   public static Map<Module, EasedDoubleAnimator> textureRun = new HashMap<>();
   public static Map<NumberSetting, EasedDoubleAnimator> indexBind = new HashMap<>();

   public static RenderRuntimeContext handle() {
      if (actionRead == null) {
         actionRead = new RenderRuntimeContext();
      }

      return actionRead;
   }

   public static EasedDoubleAnimator handle(Module var0) {
      return colorCompute.computeIfAbsent(var0, var0x -> new EasedDoubleAnimator());
   }

   public static EasedDoubleAnimator process(Module var0) {
      return scaleAdapt.computeIfAbsent(var0, var0x -> new EasedDoubleAnimator());
   }

   public static EasedDoubleAnimator compute(Module var0) {
      EasedDoubleAnimator var1 = textureRun.computeIfAbsent(var0, var0x -> new EasedDoubleAnimator());
      if (var0.keyCode != -1 && var1.prepare() == 0.0 && var1.select() == 0.0) {
         var1.resolve(1.0);
      }

      return var1;
   }

   public static EasedDoubleAnimator handle(NumberSetting var0) {
      return indexBind.computeIfAbsent(var0, var1 -> {
         EasedDoubleAnimator var2 = new EasedDoubleAnimator();
         float var3 = (var0.config - var0.state) / (var0.cache - var0.state);
         var2.resolve(var3);
         return var2;
      });
   }
}
