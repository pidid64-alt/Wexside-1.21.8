package ru.wild;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ResourceReloader.Synchronizer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.lwjgl.glfw.GLFW;
import org.wild.module.api.Module;
import ru.wild.api.event.ClientTickEvent;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.GuiRenderContext;
import ru.wild.audio.AudioResourceManager;
import ru.wild.audio.ProceduralAudioOutput;
import ru.wild.automation.BotProfileManager;
import ru.wild.automation.combat.NeuroEngine;
import ru.wild.automation.combat.RotationRecorder;
import ru.wild.command.CommandManager;
import ru.wild.command.RotationCommand;
import ru.wild.config.ConfigManager;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.AnimationClock;
import ru.wild.core.ClientSessionSwitcher;
import ru.wild.core.EventDispatchBoundary;
import ru.wild.core.MinecraftContext;
import ru.wild.core.manager.ClientComponentRegistry;
import ru.wild.core.manager.FeatureManager;
import ru.wild.core.manager.FriendManager;
import ru.wild.gui.hud.MusicPlayerHudRenderer;
import ru.wild.gui.screen.ClickGuiModernScreen;
import ru.wild.gui.screen.ClickGuiOverlayHook;
import ru.wild.gui.screen.ClickGuiScreen;
import ru.wild.gui.screen.MenuAnimationClock;
import ru.wild.gui.screen.WildMainMenuScreen;
import ru.wild.gui.theme.AnticheatProfileColor;
import ru.wild.gui.theme.ThemeManager;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.combat.HitSounds;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.network.BravoHvhServerRegistrar;
import ru.wild.network.HttpDownloader;
import ru.wild.network.IrcClient;
import ru.wild.network.OnlinePresenceBeacon;
import ru.wild.network.PresenceConnectionListener;
import ru.wild.network.ProxyManager;
import ru.wild.network.ServerTickRateTracker;
import ru.wild.network.SocialLinks;
import ru.wild.profile.Profile;
import ru.wild.render.BlurStateManager;
import ru.wild.render.FramebufferCapture;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.StardustParticleRegistry;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.ColorPickerShader;
import ru.wild.render.shader.GuiRippleShader;
import ru.wild.render.shader.HoloBlurShader;
import ru.wild.render.shader.MidnightAzureShader;
import ru.wild.render.shader.MotionBlurRenderer;
import ru.wild.render.shader.ScreenTransitionRenderer;
import ru.wild.render.shader.ShaderRenderer;
import ru.wild.render.shader.ThemeShaderProgramCache;
import ru.wild.render.shader.TransitionShader;
import ru.wild.render.shader.WorldEffectsRenderer;
import ru.wild.render.texture.MainFramebufferBinding;
import ru.wild.render.texture.SvgHelper;
import ru.wild.sdk.NotCompile;
import ru.wild.security.AccountTierResolver;
import ru.wild.security.GuardViolationException;
import ru.wild.security.LicenseHeartbeatService;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeybindPresets;

public class WildClient implements ClientModInitializer {
   public static WildClient instance;
   public FeatureManager data;
   private static final File animator = new File(System.getProperty("wild.root", "C:/WildClient"));
   public final String context = "Wild";
   public final String config = "v1";
   public final String state = "1.21.8";
   public final File cache = animator;
   public final File output = this.cache;
   public final String current = "wild";
   public boolean active = false;
   public static String mode = null;
   public ThemeManager selection;
   public ClientComponentRegistry enabled;
   public ConfigManager renderer;
   public FriendManager handler;
   public ClickGuiScreen animationDraw;
   public ClickGuiModernScreen pointEncode;
   private final SocialLinks source = new SocialLinks();
   private CommandManager target;
   private IrcClient pending;
   private static ShaderRenderer previous;
   private static RoundedRectRenderer latest;
   private static FontObject summary;
   static volatile boolean matrixBlend = false;
   private static volatile boolean vectorMatch = false;
   private static volatile boolean itemProject = false;
   private static volatile Thread responseCompute;
   private static volatile boolean providerFetch = false;
   private static int profileDraw = -1;
   private static int vectorPerform = -1;
   private static final long eventAttach = 8000L;
   private static final long serverRead = 10000L;
   private static volatile boolean positionAdvance = false;
   private String frameCheck = ".";

   public static RoundedRectRenderer handle() {
      check();
      return latest;
   }

   public static File process() {
      return animator;
   }

   public static ShaderRenderer compute() {
      check();
      return previous;
   }

   public static void handle(int var0, int var1) {
      profileDraw = var0;
      vectorPerform = var1;
      AnimationClock.handle().update();
      MainFramebufferBinding.compute();
      if (previous != null) {
         previous.update(var0, var1);
      }

      FramebufferCapture.handle().handle(var0, var1);
      GuiRippleShader.handle().process(var0, var1);
      TransitionShader.handle().handle(var0, var1);
      ScreenTransitionRenderer.handle().handle(var0, var1);
      WorldEffectsRenderer.handle().handle(var0, var1);
      MotionBlurRenderer.handle().handle(var0, var1);

      try {
         HoloBlurShader.handle().handle(var0, var1);
      } catch (Throwable var8) {
      }

      try {
         MidnightAzureShader.handle().handle(var0, var1);
      } catch (Throwable var7) {
      }

      try {
         ThemeShaderProgramCache.handle().resolve();
      } catch (Throwable var6) {
      }

      try {
         ColorPickerShader.update();
      } catch (Throwable var5) {
      }

      try {
         if (MinecraftContext.toggleState != null && MinecraftContext.toggleState.currentScreen instanceof WildMainMenuScreen var2) {
            var2.handle(var0, var1);
         }
      } catch (Throwable var4) {
      }

      if (instance != null && instance.pointEncode != null) {
         instance.pointEncode.handle(var0, var1);
      }
   }

   @NotCompile
   public static void handle(boolean var0) {
      AnimationClock.handle().update();
      if (!var0) {
         if (previous != null) {
            previous.update(0, 0);
         }

         FramebufferCapture.handle().handle(0, 0);
      } else if (MinecraftContext.toggleState != null && MinecraftContext.toggleState.getWindow() != null) {
         int var1 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
         int var2 = MinecraftContext.toggleState.getWindow().getFramebufferHeight();
         if (var1 == profileDraw && var2 == vectorPerform) {
            profileDraw = var1;
            vectorPerform = var2;
            AnimationClock.handle().update();
            if (previous != null) {
               previous.update(var1, var2);
            }

            FramebufferCapture.handle().handle(var1, var2);
         } else {
            handle(var1, var2);
         }
      }

      GuiRippleShader.handle().handle(var0);
      TransitionShader.handle().handle(var0);
      ScreenTransitionRenderer.handle().handle(var0);
      if (instance != null && instance.pointEncode != null) {
         instance.pointEncode.handle(var0);
      }
   }

   @NotCompile
   public void onInitializeClient() {
      instance = this;
      handle(this::attachEvent);
      handle(StardustParticleRegistry::handle);
      handle(this::checkFrame);
      handle(() -> mode = Profile.getUsername());
      this.data = handle(FeatureManager::new);
      this.handler = handle(FriendManager::new);
      this.renderer = handle(ConfigManager::new);
      this.selection = handle(ThemeManager::new);
      this.enabled = handle(ClientComponentRegistry::new);
      if (this.enabled != null) {
         handle((Runnable) () -> this.enabled.handle());
      }

      if (this.selection != null) {
         handle((Runnable) () -> this.selection.handle());
      }

      handle(() -> AccountTierResolver.handle(MinecraftClient.getInstance()));
      handle((Runnable) () -> ProxyManager.handle());
      handle(() -> BravoHvhServerRegistrar.handle(MinecraftClient.getInstance()));
      handle(() -> EventHandlerInvoker.handle(ServerTickRateTracker.class));
      handle(this::onTick);
      if (this.selection != null) {
         handle(() -> {
            BlurStateManager.sourceBuild = this.selection.process();
            BlurStateManager.outputCollapse = this.selection.process();
            BlurStateManager.sourceSchedule = this.selection.compute();
         });
      }

      handle((Runnable) () -> OnlinePresenceBeacon.handle());
      handle(PresenceConnectionListener::handle);
      handle(this.source::handle);
      handle(() -> KeybindPresets.handle().process());
      if (this.renderer != null) {
         handle(() -> {
            this.renderer.process();
            if (this.renderer.compute("default") != null) {
               this.renderer.handle("default");
            }
         });
      }

      handle(BotProfileManager::handle);
      handle((Runnable) () -> HudProfileConfig.handle());
      handle(this::collectModule);
      handle(() -> {
         RoundedRectRenderer.instance = MenuAnimationClock::handle;
         RoundedRectRenderer.data = MenuAnimationClock::process;
      });
      handle(() -> Runtime.getRuntime().addShutdownHook(new Thread(WildClient::closeProvider, "Wild-Client-Shutdown")));
      this.animationDraw = handle(ClickGuiScreen::new);
      this.pointEncode = handle(ClickGuiModernScreen::new);
      handle(() -> GuiRippleShader.handle().process());
      handle(ClickGuiOverlayHook::handle);
      handle(() -> EventHandlerInvoker.handle(this));
      handle(this::readServer);
      handle(this::advancePosition);
      if (this.data != null && this.renderer != null && this.selection != null && this.enabled != null) {
         vectorMatch = true;
         EventDispatchBoundary.handle();
      }
   }

   public ClickGuiModernScreen resolve() {
      if (this.pointEncode == null) {
         this.pointEncode = handle(ClickGuiModernScreen::new);
      }

      return this.pointEncode;
   }

   private static <T> T handle(Supplier<T> var0) {
      try {
         return (T)var0.get();
      } catch (GuardViolationException var2) {
         throw EventDispatchBoundary.handle(var2);
      } catch (Throwable var3) {
         System.out.println("[Client] init failed: " + var3.getClass().getSimpleName() + ": " + var3.getMessage());
         return null;
      }
   }

   @NotCompile
   private void attachEvent() {
      if (!animator.exists() && !animator.mkdirs()) {
         System.out.println("[Client] cannot create root directory: " + animator.getAbsolutePath());
      } else {
         File var1 = new File(FabricLoader.getInstance().getGameDir().toFile(), "Wild");
         handle(var1.toPath(), animator.toPath());
      }
   }

   private static void handle(Path var0, Path var1) {
      try {
         if (var0 == null || var1 == null || !Files.isDirectory(var0) || Files.isSameFile(var0, var1)) {
            return;
         }
      } catch (IOException var8) {
         return;
      }

      try (Stream<Path> var2 = Files.walk(var0)) {
         var2.forEach(var2x -> {
            try {
               Path var3 = var0.relativize(var2x);
               Path var4 = var1.resolve(var3);
               if (Files.isDirectory(var2x)) {
                  Files.createDirectories(var4);
               } else if (!Files.exists(var4)) {
                  Path var5 = var4.getParent();
                  if (var5 != null) {
                     Files.createDirectories(var5);
                  }

                  Files.copy(var2x, var4, StandardCopyOption.COPY_ATTRIBUTES);
               }
            } catch (Throwable var6) {
            }
         });
      } catch (Throwable var7) {
      }
   }

   public static void handle(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null && GLFW.glfwGetCurrentContext() != 0L) {
         int var1 = var0.getWindow().getFramebufferWidth();
         int var2 = var0.getWindow().getFramebufferHeight();
         if (!var0.getWindow().hasZeroWidthOrHeight() && var1 > 0 && var2 > 0) {
            try {
               check();
            } catch (Throwable var4) {
               return;
            }

            if (profileDraw != var1 || vectorPerform != var2) {
               handle(var1, var2);
            }
         }
      }
   }

   private static void handle(Runnable var0) {
      try {
         var0.run();
      } catch (GuardViolationException var2) {
         throw EventDispatchBoundary.handle(var2);
      } catch (Throwable var3) {
      }
   }

   @NotCompile
   private void readServer() {
      try {
         ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final Identifier instance = Identifier.of("wild", "font_reload");

            @Override
            public Identifier getFabricId() {
               return this.instance;
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer var1, ResourceManager var2, Executor var3, Executor var4) {
               return CompletableFuture.completedFuture(null).<Object>thenCompose(var1::whenPrepared).thenAcceptAsync(var0 -> {
                  MinecraftClient var1x = MinecraftClient.getInstance();
                  if (var1x != null) {
                     var1x.execute(() -> {
                        try {
                           if (WildClient.matrixBlend) {
                              FontRegistry.handle();
                           }
                        } catch (Throwable var1xx) {
                        }
                     });
                  }
               }, var4);
            }
         });
      } catch (Throwable var2) {
      }
   }

   @NotCompile
   private void advancePosition() {
      try {
         ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final Identifier instance = Identifier.of("wild", "theme_shader_reload");

            @Override
            public Identifier getFabricId() {
               return this.instance;
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer var1, ResourceManager var2, Executor var3, Executor var4) {
               return CompletableFuture.completedFuture(null).<Object>thenCompose(var1::whenPrepared).thenAcceptAsync(var0 -> {
                  MinecraftClient var1x = MinecraftClient.getInstance();
                  if (var1x != null) {
                     var1x.execute(() -> {
                        try {
                           SvgHelper.handle();
                        } catch (Throwable var9) {
                        }

                        try {
                           AnticheatProfileColor.process();
                        } catch (Throwable var8) {
                        }

                        try {
                           AnticheatProfileColor.handle();
                        } catch (Throwable var7) {
                        }

                        try {
                           ThemeShaderProgramCache.handle().resolve();
                        } catch (Throwable var6) {
                        }

                        try {
                           ColorPickerShader.update();
                        } catch (Throwable var5) {
                        }

                        try {
                           WorldEffectsRenderer.handle().close();
                        } catch (Throwable var4x) {
                        }

                        try {
                           MotionBlurRenderer.handle().close();
                        } catch (Throwable var3x) {
                        }

                        try {
                           HoloBlurShader.handle().handle(0, 0);
                        } catch (Throwable var2x) {
                        }

                        try {
                           MidnightAzureShader.handle().handle(0, 0);
                        } catch (Throwable var1xx) {
                        }
                     });
                  }
               }, var4);
            }
         });
      } catch (Throwable var2) {
      }
   }

   @NotCompile
   private void checkFrame() {
      String var1 = System.getProperty("wild.loader.pid");
      if (var1 == null || var1.isBlank()) {
         var1 = System.getenv("WILD_LOADER_PID");
      }

      if (var1 != null && !var1.isBlank()) {
         try {
            long var2 = Long.parseLong(var1.trim());
            long var4 = ProcessHandle.current().pid();
            if (var2 <= 0L || var2 == var4) {
               return;
            }

            ProcessHandle.of(var2).ifPresent(var0 -> {
               if (var0.isAlive()) {
                  var0.destroy();
               }
            });
         } catch (Throwable var6) {
         }
      }
   }

   @NotCompile
   private void collectModule() {
      Configurator.setLevel("com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo", Level.OFF);
      Configurator.setLevel("net.minecraft.client.texture.PlayerSkinProvider", Level.ERROR);
      Configurator.setLevel("net.minecraft.client.network.ClientPlayNetworkHandler", Level.ERROR);
      Configurator.setLevel("net.minecraft.client.world.ClientChunkManager", Level.ERROR);
      Configurator.setLevel("net.minecraft.block.entity.BlockEntity", Level.ERROR);
   }

   @NotCompile
   public static void update() {
      if (!providerFetch) {
         providerFetch = true;
         System.out.println("[Wild] shutdown: begin");
         handle(() -> {
            if (instance != null && instance.renderer != null) {
               instance.renderer.resolve();
            }
         });
         handle(BotProfileManager::compute);
         handle(WildClient::convertWindow);
         handle(SocialLinks::process);
         handle(IrcClient::process);
         handle(OnlinePresenceBeacon::process);
         handle(() -> {
            Thread var0 = responseCompute;
            responseCompute = null;
            itemProject = false;
            if (var0 != null) {
               var0.interrupt();
            }
         });
         handle((Runnable) () -> ClientSessionSwitcher.handle());
         handle(ServerTickRateTracker::apply);
         handle(AutoBuy::fetch);
         handle(RotationRecorder::blendMatrix);
         handle(NeuroEngine::process);
         handle(LicenseHeartbeatService::process);
         handle(MusicPlayerHudRenderer::encodePoint);
         handle(RotationCommand::resolve);
         handle(HttpDownloader::compute);
         handle(ProceduralAudioOutput::compute);
         handle((Runnable) () -> AudioResourceManager.handle());
         handle(HitSounds::refresh);
         handle((Runnable) () -> PackedColor.process());
         handle(() -> WorldEffectsRenderer.handle().close());
         handle(() -> MotionBlurRenderer.handle().close());
         handle(ThemeShaderProgramCache.handle()::resolve);
         handle(() -> MainFramebufferBinding.compute());
         System.out.println("[Wild] shutdown: done");
      }
   }

   @NotCompile
   private static void closeProvider() {
      handle(8000L, "shutdown hook");
      update();
   }

   @NotCompile
   public static void apply() {
      handle(10000L, "stop() returned without System.exit");
   }

   private static synchronized void handle(long var0, String var2) {
      if (!positionAdvance) {
         positionAdvance = true;
         Thread var3 = new Thread(() -> {
            try {
               Thread.sleep(var0);
            } catch (InterruptedException var4) {
               Thread.currentThread().interrupt();
               return;
            }

            System.out.println("[Wild] shutdown: process still alive " + var0 + "ms after " + var2);
            savePreset();
            System.out.println("[Wild] shutdown: force-exit failsafe -> halt(0)");
            Runtime.getRuntime().halt(0);
         }, "Wild-ForceExit-Watchdog");
         var3.setDaemon(true);
         var3.setPriority(10);
         var3.start();
      }
   }

   private static void savePreset() {
      try {
         for (Entry var1 : Thread.getAllStackTraces().entrySet()) {
            Thread var2 = (Thread)var1.getKey();
            if (var2 != null && !var2.isDaemon() && var2.isAlive() && var2 != Thread.currentThread()) {
               StackTraceElement[] var3 = (StackTraceElement[])var1.getValue();
               StringBuilder var4 = new StringBuilder("[Wild] shutdown: blocking thread \"").append(var2.getName()).append("\" state=").append(var2.getState());
               int var5 = Math.min(6, var3 == null ? 0 : var3.length);

               for (int var6 = 0; var6 < var5; var6++) {
                  var4.append(System.lineSeparator()).append("    at ").append(var3[var6]);
               }

               System.out.println(var4);
            }
         }
      } catch (Throwable var7) {
      }
   }

   private static void convertWindow() {
      if (instance != null && instance.data != null && instance.data.instance != null) {
         for (Module var1 : instance.data.instance) {
            if (var1 != null && var1.enabled) {
               try {
                  var1.enabled = false;
                  var1.process();
               } catch (Throwable var3) {
               }
            }
         }
      }
   }

   public static void execute() {
      if (!itemProject) {
         synchronized (WildClient.class) {
            if (itemProject) {
               return;
            }

            itemProject = true;
         }

         Thread var3 = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
               try {
                  double var0 = ServerTickRateTracker.handle();
                  if (var0 <= 0.0) {
                     var0 = 20.0;
                  }

                  double var2 = 1.0 / var0;
                  long var4 = (long)(var2 * 1000.0);
                  MinecraftClient var6 = MinecraftClient.getInstance();
                  if (var6 != null && !var6.isOnThread()) {
                     var6.execute(() -> EventHandlerInvoker.handle(new ClientTickEvent()));
                  } else {
                     EventHandlerInvoker.handle(new ClientTickEvent());
                  }

                  Thread.sleep(Math.max(var4, 1L));
               } catch (GuardViolationException var8) {
                  throw EventDispatchBoundary.handle(var8);
               } catch (InterruptedException var9) {
                  Thread.currentThread().interrupt();
                  break;
               } catch (Throwable var10) {
                  try {
                     Thread.sleep(100L);
                  } catch (InterruptedException var7) {
                     Thread.currentThread().interrupt();
                     break;
                  }
               }
            }
         }, "TPS");
         var3.setDaemon(true);
         responseCompute = var3;
         var3.start();
      }
   }

   public static boolean prepare() {
      WildClient var0 = instance;
      return var0 != null && var0.data != null;
   }

   public static void check() {
      if (!matrixBlend) {
         writePreset();
      }
   }

   private static synchronized void writePreset() {
      if (!matrixBlend) {
         if (GLFW.glfwGetCurrentContext() != 0L) {
            previous = new ShaderRenderer();
            latest = new RoundedRectRenderer(previous);
            FontRegistry.handle(previous, latest);
            summary = FontRegistry.instance;
            matrixBlend = true;
         }
      }
   }

   @NotCompile
   public void onTick() {
      this.target = new CommandManager();
   }

   public static void select() {
      EventDispatchBoundary.handle();
      if (vectorMatch) {
         OpenGlStateSnapshot.NetworkState var0 = OpenGlStateSnapshot.handle();

         try {
            if (MinecraftContext.toggleState == null || MinecraftContext.toggleState.getWindow() == null) {
               return;
            }

            int var1 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
            int var2 = MinecraftContext.toggleState.getWindow().getFramebufferHeight();
            if (var1 <= 0 || var2 <= 0) {
               handle(var1, var2);
               return;
            }

            try {
               check();
            } catch (Throwable var28) {
               return;
            }

            AnimationClock.handle().process();
            ThemeRenderer var3 = ThemeRenderer.handle();
            var3.handle(MinecraftContext.toggleState, latest, var1, var2);
            boolean var4 = false;

            try {
               latest.handle(var1, var2);
               var4 = true;
               EventHandlerInvoker.handle(new GuiRenderContext(MinecraftContext.toggleState, latest, summary, var1, var2));
            } catch (Throwable var26) {
            } finally {
               if (var4) {
                  try {
                     latest.process();
                  } catch (Throwable var25) {
                     latest.handle();
                  }
               }
            }
         } catch (Throwable var29) {
         } finally {
            OpenGlStateSnapshot.compute(var0);
         }
      }
   }
   public FeatureManager refresh() {
      return this.data;
   }
   public String render() {
      return "Wild";
   }
   public String tick() {
      return "v1";
   }
   public String drawAnimation() {
      return "1.21.8";
   }
   public File encodePoint() {
      return this.cache;
   }
   public File animate() {
      return this.output;
   }
   public String load() {
      return "wild";
   }
   public boolean save() {
      return this.active;
   }
   public ThemeManager submit() {
      return this.selection;
   }
   public ClientComponentRegistry unload() {
      return this.enabled;
   }
   public ConfigManager fetch() {
      return this.renderer;
   }
   public FriendManager measure() {
      return this.handler;
   }
   public ClickGuiScreen blendMatrix() {
      return this.animationDraw;
   }
   public SocialLinks matchVector() {
      return this.source;
   }
   public CommandManager projectItem() {
      return this.target;
   }
   public IrcClient computeResponse() {
      return this.pending;
   }
   public String fetchProvider() {
      return this.frameCheck;
   }
   public void handle(FeatureManager var1) {
      this.data = var1;
   }
   public void process(boolean var1) {
      this.active = var1;
   }
   public void handle(ThemeManager var1) {
      this.selection = var1;
   }
   public void handle(ClientComponentRegistry var1) {
      this.enabled = var1;
   }
   public void handle(ConfigManager var1) {
      this.renderer = var1;
   }
   public void handle(FriendManager var1) {
      this.handler = var1;
   }
   public void handle(ClickGuiScreen var1) {
      this.animationDraw = var1;
   }
   public void handle(ClickGuiModernScreen var1) {
      this.pointEncode = var1;
   }
   public void handle(CommandManager var1) {
      this.target = var1;
   }
   public void handle(IrcClient var1) {
      this.pending = var1;
   }
   public static boolean drawProfile() {
      return vectorMatch;
   }
   public static boolean performVector() {
      return providerFetch;
   }
   public void handle(String var1) {
      this.frameCheck = var1;
   }
}
