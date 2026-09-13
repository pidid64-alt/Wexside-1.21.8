package ru.wild.gui.widget;

import org.wild.module.api.Module;
import ru.wild.api.setting.Setting;

@FunctionalInterface
public interface SettingPopupHost {
   void openForSetting(Module var1, Setting var2, double var3, double var5, Object var7);
}
