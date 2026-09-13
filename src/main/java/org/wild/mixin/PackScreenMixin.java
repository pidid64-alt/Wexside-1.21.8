package org.wild.mixin;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer.Pack;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wild.mixin.acceser.MessageAccessor;
import ru.wild.modules.misc.UnHook;

@Mixin(PackScreen.class)
public abstract class PackScreenMixin extends Screen {
   @Unique
   private static final Logger LOGGER = LoggerFactory.getLogger(PackScreenMixin.class);
   @Unique
   private static final Text OPEN_FOLDER = Text.translatable("pack.openFolder");
   @Unique
   private static final Text FOLDER_INFO = Text.translatable("pack.folderInfo");
   @Unique
   private static final Text SEARCH_TITLE = Text.literal("Search resource packs");
   @Unique
   private static final Text SEARCH_PLACEHOLDER = Text.literal("Search...");
   @Unique
   private TextFieldWidget wild$packSearchField;
   @Unique
   private String wild$packSearchQuery = "";
   @Shadow
   private Path file;

   protected PackScreenMixin(Text var1) {
      super(var1);
   }

   @Invoker("updatePackLists")
   public abstract void wild$updatePackLists();

   @Inject(method = "init", at = @At("RETURN"))
   private void wild$addPackSearch(CallbackInfo var1) {
      if (!UnHook.target) {
         this.wild$packSearchField = new TextFieldWidget(this.textRenderer, 0, 0, 160, 20, SEARCH_TITLE);
         this.wild$packSearchField.setMaxLength(128);
         this.wild$packSearchField.setPlaceholder(SEARCH_PLACEHOLDER);
         this.wild$packSearchField.setText(this.wild$packSearchQuery);
         this.wild$packSearchField.setChangedListener(var1x -> {
            this.wild$packSearchQuery = var1x == null ? "" : var1x;
            this.wild$updatePackLists();
         });
         this.wild$positionPackSearchField();
         this.addDrawableChild(this.wild$packSearchField);
      }
   }

   @Inject(method = "refreshWidgetPositions", at = @At("RETURN"))
   private void wild$refreshPackSearchPosition(CallbackInfo var1) {
      this.wild$positionPackSearchField();
   }

   @Inject(method = "tick", at = @At("TAIL"))
   private void wild$tickPackSearchVisibility(CallbackInfo var1) {
      this.wild$syncPackSearchVisibility();
   }

   @ModifyVariable(method = "updatePackList", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
   private Stream<Pack> wild$filterPackList(Stream<Pack> var1) {
      if (UnHook.target) {
         return var1;
      }

      String var2 = this.wild$packSearchQuery == null ? "" : this.wild$packSearchQuery.trim().toLowerCase(Locale.ROOT);
      return var2.isEmpty() ? var1 : var1.filter(var2x -> this.wild$matchesPackSearch(var2x, var2));
   }

   @Unique
   private void wild$positionPackSearchField() {
      if (this.wild$packSearchField != null) {
         int var1 = Math.min(180, Math.max(120, this.width / 5));
         this.wild$packSearchField.setDimensions(var1, 20);
         this.wild$packSearchField.setX(this.width - var1 - 8);
         this.wild$packSearchField.setY(8);
      }
   }

   @Unique
   private void wild$syncPackSearchVisibility() {
      if (this.wild$packSearchField != null) {
         boolean var1 = !UnHook.target;
         this.wild$packSearchField.visible = var1;
         this.wild$packSearchField.active = var1;
         if (!var1) {
            this.wild$packSearchField.setFocused(false);
         }
      }
   }

   @Unique
   private boolean wild$matchesPackSearch(Pack var1, String var2) {
      return var1 == null
         ? false
         : this.wild$contains(var1.getName(), var2) || this.wild$contains(var1.getDisplayName(), var2) || this.wild$contains(var1.getDescription(), var2);
   }

   @Unique
   private boolean wild$contains(Text var1, String var2) {
      return var1 != null && this.wild$contains(var1.getString(), var2);
   }

   @Unique
   private boolean wild$contains(String var1, String var2) {
      return var1 != null && var1.toLowerCase(Locale.ROOT).contains(var2);
   }

   @Redirect(
      method = "init",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;build()Lnet/minecraft/client/gui/widget/ButtonWidget;")
   )
   private ButtonWidget redirectOpenFolderButton(Builder var1) {
      Text var2 = ((MessageAccessor)var1).getMessage();
      return var2.equals(OPEN_FOLDER) ? ButtonWidget.builder(OPEN_FOLDER, var1x -> {
         File var2x;
         if (UnHook.target && UnHook.pending != null) {
            var2x = UnHook.pending;
         } else {
            var2x = this.file.toFile();
         }

         if (!var2x.exists()) {
            var2x.mkdirs();
         }

         LOGGER.info("Opening folder: {}", var2x.getPath());
         Util.getOperatingSystem().open(var2x);
      }).tooltip(Tooltip.of(FOLDER_INFO)).build() : var1.build();
   }
}
