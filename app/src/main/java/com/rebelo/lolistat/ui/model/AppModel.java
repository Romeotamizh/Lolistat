package com.rebelo.lolistat.ui.model;

import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

public class AppModel
{
	public String packageName;
	public String title;
	public WeakReference<Drawable> icon;
	
	public Runnable iconRefreshRunnable;
	
	public void refreshIcon() {
		if (iconRefreshRunnable != null)
			iconRefreshRunnable.run();
	}
}
