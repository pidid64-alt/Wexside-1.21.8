package ru.wild.core.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.wild.module.api.Module;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;
import ru.wild.modules.combat.AntiBot;
import ru.wild.modules.combat.AntiCrystal;
import ru.wild.modules.combat.AttackAura;
import ru.wild.modules.combat.AutoExplosion;
import ru.wild.modules.combat.AutoGApple;
import ru.wild.modules.combat.AutoSwap;
import ru.wild.modules.combat.AutoTotem;
import ru.wild.modules.combat.Criticals;
import ru.wild.modules.combat.ElytraTarget;
import ru.wild.modules.combat.FastBow;
import ru.wild.modules.combat.HitBox;
import ru.wild.modules.combat.HitSounds;
import ru.wild.modules.combat.TargetPearl;
import ru.wild.modules.combat.TriggerBot;
import ru.wild.modules.combat.Velocity;
import ru.wild.modules.misc.AhHelper;
import ru.wild.modules.misc.AppleFarmer;
import ru.wild.modules.misc.AutoAccept;
import ru.wild.modules.misc.AutoAncientBot;
import ru.wild.modules.misc.AutoAuth;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.modules.misc.AutoCraft;
import ru.wild.modules.misc.AutoDrop;
import ru.wild.modules.misc.AutoFTCraftMembrana;
import ru.wild.modules.misc.AutoFTObsidianFarm;
import ru.wild.modules.misc.AutoLeave;
import ru.wild.modules.misc.AutoPottBot;
import ru.wild.modules.misc.AutoResell;
import ru.wild.modules.misc.AutoSell;
import ru.wild.modules.misc.AutoVillageTrade;
import ru.wild.modules.misc.AutoWood;
import ru.wild.modules.misc.BaseFinder;
import ru.wild.modules.misc.Cape;
import ru.wild.modules.misc.ChatHelper;
import ru.wild.modules.misc.ChorusFarm;
import ru.wild.modules.misc.ClanUpgrade;
import ru.wild.modules.misc.ClientUtil;
import ru.wild.modules.misc.CocoaFarm;
import ru.wild.modules.misc.CreeperFarm;
import ru.wild.modules.misc.EmeraldArmorFarm;
import ru.wild.modules.misc.FreeLock;
import ru.wild.modules.misc.ItemScroller;
import ru.wild.modules.misc.LockSlots;
import ru.wild.modules.misc.MoneyFarm;
import ru.wild.modules.misc.Party;
import ru.wild.modules.misc.PotionCombiner;
import ru.wild.modules.misc.PvPSafe;
import ru.wild.modules.misc.Removals;
import ru.wild.modules.misc.Scaffold;
import ru.wild.modules.misc.SeeInvisibles;
import ru.wild.modules.misc.ServerDHelper;
import ru.wild.modules.misc.ServerHelper;
import ru.wild.modules.misc.ServerJoiner;
import ru.wild.modules.misc.TapeMouse;
import ru.wild.modules.misc.TotemVoices;
import ru.wild.modules.misc.UnHook;
import ru.wild.modules.misc.UseTracker;
import ru.wild.modules.misc.WardenFarm;
import ru.wild.modules.movement.AirStuck;
import ru.wild.modules.movement.AutoDodge;
import ru.wild.modules.movement.DragonFly;
import ru.wild.modules.movement.ElytraMotion;
import ru.wild.modules.movement.GrimGlide;
import ru.wild.modules.movement.InvMove;
import ru.wild.modules.movement.Jesus;
import ru.wild.modules.movement.NoFall;
import ru.wild.modules.movement.NoSlow;
import ru.wild.modules.movement.NoWeb;
import ru.wild.modules.movement.Speed;
import ru.wild.modules.movement.Spider;
import ru.wild.modules.movement.Sprint;
import ru.wild.modules.movement.Timer;
import ru.wild.modules.player.ActionRecorder;
import ru.wild.modules.player.AntiAFK;
import ru.wild.modules.player.AutoDuel;
import ru.wild.modules.player.AutoFish;
import ru.wild.modules.player.AutoInvisible;
import ru.wild.modules.player.AutoLes;
import ru.wild.modules.player.AutoPotion;
import ru.wild.modules.player.AutoTool;
import ru.wild.modules.player.Blink;
import ru.wild.modules.player.CameraClip;
import ru.wild.modules.player.ChestStealer;
import ru.wild.modules.player.ClickPearl;
import ru.wild.modules.player.ElytraHelper;
import ru.wild.modules.player.FakePlayer;
import ru.wild.modules.player.FastBreak;
import ru.wild.modules.player.FreeCamera;
import ru.wild.modules.player.GeyserHelper;
import ru.wild.modules.player.NoDelay;
import ru.wild.modules.player.NoInteract;
import ru.wild.modules.player.NoPush;
import ru.wild.modules.player.OpenWalls;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.modules.player.RotationLab;
import ru.wild.modules.player.Test;
import ru.wild.modules.player.WindHop;
import ru.wild.modules.visuals.AncientXray;
import ru.wild.modules.visuals.Animations;
import ru.wild.modules.visuals.Arrows;
import ru.wild.modules.visuals.AspectRation;
import ru.wild.modules.visuals.AtmoDawnFog;
import ru.wild.modules.visuals.AttackEffect;
import ru.wild.modules.visuals.BlockESP;
import ru.wild.modules.visuals.BlockOutline;
import ru.wild.modules.visuals.Chams;
import ru.wild.modules.visuals.ChinaHat;
import ru.wild.modules.visuals.ColorPlus;
import ru.wild.modules.visuals.DeadEffect;
import ru.wild.modules.visuals.ESP;
import ru.wild.modules.visuals.FullBright;
import ru.wild.modules.visuals.GlowESP;
import ru.wild.modules.visuals.Hands;
import ru.wild.modules.visuals.Hud;
import ru.wild.modules.visuals.ItemPhysic;
import ru.wild.modules.visuals.JumpCircle;
import ru.wild.modules.visuals.Menu;
import ru.wild.modules.visuals.MotionBlur;
import ru.wild.modules.visuals.NameTags;
import ru.wild.modules.visuals.Particles;
import ru.wild.modules.visuals.Predictions;
import ru.wild.modules.visuals.ProtectInfo;
import ru.wild.modules.visuals.Screens;
import ru.wild.modules.visuals.Stardust;
import ru.wild.modules.visuals.SwingAnimation;
import ru.wild.modules.visuals.TargetESP;
import ru.wild.modules.visuals.Trails;
import ru.wild.modules.visuals.VisibleEating;
import ru.wild.modules.visuals.WorldTweaks;
import ru.wild.profile.Profile;
import ru.wild.profile.Role;

public class FeatureManager {
   public final ArrayList<Module> instance = new ArrayList<>();

   public FeatureManager() {
      this.handle();
   }

   public void handle() {
      this.process(new ClientUtil());
      this.process(new Menu());
      this.process(new UnHook());
      this.process(new AntiCrystal());
      this.process(new AutoGApple());
      this.process(new AutoSwap());
      this.process(new AutoExplosion());
      this.process(new AutoTotem());
      this.process(new FastBow());
      this.process(new HitBox());
      this.process(new HitSounds());
      this.process(new ColorPlus());
      this.process(new Velocity());
      this.process(new AhHelper());
      this.process(new AppleFarmer());
      this.process(new Scaffold());
      this.process(new CocoaFarm());
      this.process(new ChorusFarm());
      this.process(new AutoBuy());
      this.process(new AutoDrop());
      this.process(new LockSlots());
      this.process(new AutoLeave());
      this.process(new AutoAncientBot());
      this.process(new AutoFTCraftMembrana());
      this.process(new AutoSell());
      this.process(new AutoResell());
      this.process(new BaseFinder());
      this.process(new FreeLock());
      this.process(new ru.wild.modules.misc.FriendManager());
      this.process(new ItemScroller());
      this.process(new Removals());
      this.process(new SeeInvisibles());
      this.process(new ServerDHelper());
      this.process(new ServerJoiner());
      this.process(new ServerHelper());
      this.process(new TotemVoices());
      this.process(new UseTracker());
      this.process(new AutoDodge());
      this.process(new DragonFly());
      this.process(new GrimGlide());
      this.process(new InvMove());
      this.process(new NoFall());
      this.process(new NoSlow());
      this.process(new NoWeb());
      this.process(new Speed());
      this.process(new Spider());
      this.process(new Sprint());
      this.process(new Timer());
      this.process(new AntiAFK());
      this.process(new ActionRecorder());
      this.process(new RotationLab());
      this.process(new AutoFish());
      this.process(new ChestStealer());
      this.process(new AutoTool());
      this.process(new FakePlayer());
      this.process(new CameraClip());
      this.process(new ElytraHelper());
      this.process(new FreeCamera());
      this.process(new ClickPearl());
      this.process(new NoDelay());
      this.process(new NoInteract());
      this.process(new NoPush());
      this.process(new OpenWalls());
      this.process(new PlayerHelper());
      this.process(new WindHop());
      this.process(new Blink());
      this.process(new AncientXray());
      this.process(new Animations());
      this.process(new AttackEffect());
      this.process(new Arrows());
      this.process(new AspectRation());
      this.process(new BlockOutline());
      this.process(new BlockESP());
      this.process(new WorldTweaks());
      this.process(new MotionBlur());
      this.process(new Chams());
      this.process(new ESP());
      this.process(new GlowESP());
      this.process(new Hands());
      this.process(new ChinaHat());
      this.process(new Hud());
      this.process(new ItemPhysic());
      this.process(new JumpCircle());
      this.process(new AtmoDawnFog());
      this.process(new DeadEffect());
      this.process(new NameTags());
      this.process(new FullBright());
      this.process(new Particles());
      this.process(new Predictions());
      this.process(new ProtectInfo());
      this.process(new PvPSafe());
      this.process(new SwingAnimation());
      this.process(new Stardust());
      this.process(new TargetESP());
      this.process(new VisibleEating());
      this.process(new Jesus());
      this.process(new ElytraMotion());
      this.process(new ChatHelper());
      this.process(new CreeperFarm());
      this.process(new TapeMouse());
      this.process(new AutoCraft());
      this.process(new AttackAura());
      this.process(new TriggerBot());
      this.process(new Criticals());
      this.process(new ElytraTarget());
      this.process(new TargetPearl());
      this.process(new AntiBot());
      this.process(new AutoPotion());
      this.process(new AutoInvisible());
      this.process(new AutoDuel());
      this.process(new AirStuck());
      this.process(new FastBreak());
      this.process(new AutoLes());
      this.process(new Trails());
      this.process(new Screens());
      this.process(new AutoAccept());
      this.process(new GeyserHelper());
      this.process(new Test());
      this.process(new AutoAuth());
      this.process(new PotionCombiner());
      this.process(new MoneyFarm());
      this.process(new EmeraldArmorFarm());
      this.process(new AutoVillageTrade());
      this.process(new ClanUpgrade());
      this.process(new Party());
      this.process(new Cape());
      this.process(new AutoPottBot());
      this.process(new WardenFarm());
      this.process(new AutoWood());
      this.process(new AutoFTObsidianFarm());
      this.instance.sort(Comparator.comparing(module -> module.getSearchName().toLowerCase()));
      System.out.println("[Manager] register " + this.instance.size() + " modules.");
   }

   private void process(Module var1) {
      if (var1 != null) {
         this.instance.add(var1);
      }
   }

   public ArrayList<Module> process() {
      return this.instance.stream().filter(this::handle).collect(Collectors.toCollection(ArrayList::new));
   }

   public <T extends Module> T handle(Class<T> var1) {
      for (Module var3 : this.instance) {
         if (var3.getClass() == var1) {
            return (T)var3;
         }
      }

      return null;
   }

   public Module process(Class<?> var1) {
      for (Module var3 : this.instance) {
         if (var3.getClass() == var1) {
            return var3;
         }
      }

      return null;
   }

   public ArrayList<Module> handle(ModuleCategory var1) {
      return this.instance.stream().filter(this::handle).filter(var1x -> var1x.category == var1).collect(Collectors.toCollection(ArrayList::new));
   }

   public Module[] handle(int var1) {
      return this.instance.stream().filter(this::handle).filter(var1x -> var1x.keyCode == var1).toArray(Module[]::new);
   }

   public boolean handle(Module var1) {
      return var1 != null && handle(var1.getRoles());
   }

   public boolean compute(Class<? extends Module> var1) {
      return var1 == null ? false : handle(var1.getAnnotation(ModuleRoles.class));
   }

   public static boolean handle(ModuleRoles var0) {
      if (var0 == null) {
         return true;
      } else {
         boolean var1 = var0.handle() != Role.DEFAULT || var0.process().length > 0 || var0.compute().length > 0 || var0.resolve().length > 0;
         if (!var1) {
            return true;
         } else if (Profile.isUid(var0.resolve())) {
            return true;
         } else if (Profile.isUsername(var0.compute())) {
            return true;
         } else {
            return Profile.hasRole(var0.process()) ? true : var0.handle() != Role.DEFAULT && Profile.hasRoleAtLeast(var0.handle());
         }
      }
   }
}
