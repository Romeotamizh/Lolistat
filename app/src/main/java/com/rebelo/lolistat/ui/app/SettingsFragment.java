package com.rebelo.lolistat.ui.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rebelo.lolistat.R;
import com.rebelo.lolistat.support.Settings;
import com.rebelo.lolistat.ui.base.BasePreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import static com.rebelo.lolistat.ui.utils.UiUtility.$;

public class SettingsFragment extends BasePreferenceFragment
{
	private Settings mSettings;
	
	private SwitchPreference mEnable;
	private CheckBoxPreference mNav, mStatus, mForceTint, mUseCustom;
	private ColorPickerPreference mColor;

	private String mPackageName, mClassName;

	@Override
	protected int getPreferenceXml() {
		return R.xml.pref;
	}

	@Override
	protected void onPreferenceLoaded() {
		initPackage();
		setHasOptionsMenu(true);
		mSettings = Settings.getInstance(getActivity());
		
		// Obtain preferences
		mEnable = $(this, Settings.ENABLED);
		mNav = $(this, Settings.TINT_NAVIGATION);
		mStatus = $(this, Settings.TINT_ICONS);
		mColor = $(this, Settings.CUSTOM_COLOR);
		mForceTint = $(this, Settings.FORCE_TINT);
		mUseCustom = $(this, Settings.USE_CUSTOM);

		// Default values
		reload();
		
		// Bind
		$$(mEnable, mNav, mStatus, mColor, mForceTint, mUseCustom);

		mColor.setOnPreferenceClickListener(mColor);
	}

	@Override
    @SuppressWarnings("unchecked")
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mEnable) {
			putBoolean(Settings.ENABLED, (Boolean) newValue);
			return true;
		} else if (preference == mNav) {
			putBoolean(Settings.TINT_NAVIGATION, (Boolean) newValue);
			return true;
		} else if (preference == mStatus) {
			putBoolean(Settings.TINT_ICONS, (Boolean) newValue);
			return true;
		} else if (preference == mColor) {
			putInt(Settings.CUSTOM_COLOR, (Integer)newValue);
			return true;
		} else if (preference == mForceTint) {
			putBoolean(Settings.FORCE_TINT, (Boolean) newValue);
			return true;
		} else if(preference == mUseCustom) {
			putBoolean(Settings.USE_CUSTOM, (Boolean) newValue);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.settings, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.reset:
				doReset();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void initPackage() {
		String[] str = getExtraPass().split(",");
		mPackageName = str[0];
		mClassName = str[1];
		
		if (str[2].equals(getString(R.string.global))) {
			str[2] = getString(R.string.global_short);
		}
		
		setTitle(str[2] + " - " + str[3]);
		showHomeAsUp();
	}
	
	private void reload() {
		mEnable.setChecked(getBoolean(Settings.ENABLED, true));
		mForceTint.setChecked(getBoolean(Settings.FORCE_TINT, false));
		mUseCustom.setChecked(getBoolean(Settings.USE_CUSTOM, false));
		mColor.onColorChanged(getInt(Settings.CUSTOM_COLOR, 0xFF000000));

		if (!mPackageName.equals("global") || !mClassName.equals("global")) {
			getPreferenceScreen().removePreference(mNav);
			getPreferenceScreen().removePreference(mStatus);
		} else {
			getPreferenceScreen().removePreference(mUseCustom);
			getPreferenceScreen().removePreference(mColor);
			mNav.setChecked(getBoolean(Settings.TINT_NAVIGATION, true));
			mStatus.setChecked(getBoolean(Settings.TINT_ICONS, false));
		}
	}
	
	private void doReset() {
		new AlertDialog.Builder(getActivity())
			.setMessage(R.string.reset_confirm)
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int buttonId) {
					
				}
			})
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int buttonId) {
					mSettings.reset(mPackageName, mClassName);
					reload();
				}
			})
			.show();
	}
	
	private boolean getBoolean(String key, boolean defValue) {
		return mSettings.getBoolean(mPackageName, mClassName, key, defValue);
	}
	
	private int getInt(String key, int defValue) {
		return mSettings.getInt(mPackageName, mClassName, key, defValue);
	}
	
	private void putBoolean(String key, boolean value) {
		mSettings.putBoolean(mPackageName, mClassName, key, value);
	}
	
	private void putInt(String key, int value) {
		mSettings.putInt(mPackageName, mClassName, key, value);
	}
}
