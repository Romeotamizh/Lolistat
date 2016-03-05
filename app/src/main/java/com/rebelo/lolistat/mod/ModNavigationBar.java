package com.rebelo.lolistat.mod;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;

import java.lang.reflect.Field;

import com.rebelo.lolistat.support.Settings;
import com.rebelo.lolistat.support.Utility;

public class ModNavigationBar
{
	public static void hookNavigationBar(ClassLoader loader) throws Throwable {
		final Settings settings = Settings.getInstance(null);
		
		if (!settings.getBoolean("global", "global", Settings.TINT_NAVIGATION, true)) return;

		final Class<?> internalStyleable = XposedHelpers.findClass("com.android.internal.R.styleable", loader);
		final Field internalThemeField = XposedHelpers.findField(internalStyleable, "Theme");
		final Field internalColorPrimaryDarkField = XposedHelpers.findField(internalStyleable, "Theme_colorPrimaryDark");
		final int[] theme = (int[]) internalThemeField.get(null);
		final int theme_colorPrimaryDark = internalColorPrimaryDarkField.getInt(null);

		String class_path;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			class_path = "com.android.internal.policy.PhoneWindow";
		else
			class_path = "com.android.internal.policy.impl.PhoneWindow";

		XposedHelpers.findAndHookMethod(class_path, loader, "setStatusBarColor", int.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				int color = Integer.valueOf(mhparams.args[0].toString());

				//if (color != 0)
				((Window) mhparams.thisObject).setNavigationBarColor(color);
			}
		});

		XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Activity activity = (Activity) mhparams.thisObject;

				String packageName = activity.getApplicationInfo().packageName;
				String className = activity.getClass().getName();

				settings.reload();

				// Ignore if blacklisted
				if (!settings.getBoolean(packageName, className, Settings.ENABLED, true))
					return;

				// Ignore if launcher or recents screen
				if (Utility.isLauncher(activity, packageName) || className.equals("com.android.systemui.recents.RecentsActivity")) return;

				TypedArray a = activity.getTheme().obtainStyledAttributes(theme);
				int colorPrimaryDark = a.getColor(theme_colorPrimaryDark, Color.TRANSPARENT);
				a.recycle();

				if (colorPrimaryDark != Color.TRANSPARENT && colorPrimaryDark != Color.BLACK)
					activity.getWindow().setNavigationBarColor(colorPrimaryDark);
			}
		});
	}
}