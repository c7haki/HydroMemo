package de.boesling.hydromemo.activities;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import de.boesling.hydromemo.R;

public class PreferencesHelper {

	private ContextWrapper contextWrapper;
	private SharedPreferences sharedPreferences;

	public PreferencesHelper(ContextWrapper contextWrapper) {
		this.contextWrapper = contextWrapper;
		this.sharedPreferences = contextWrapper.getSharedPreferences(
				contextWrapper.getPackageName() + "_preferences",
				PreferenceActivity.MODE_PRIVATE);
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

	public boolean isIncrementEnabled() {
		return sharedPreferences.getBoolean(
				contextWrapper.getString(R.string.cfgIncrementKey),
				contextWrapper.getResources().getBoolean(
						R.bool.cfgIncrementDefaultValue));
	}

	public long getIntervalMillis() {
		return Long
				.parseLong(sharedPreferences.getString(contextWrapper
						.getString(R.string.cfgTimeIntervalKey), contextWrapper
						.getString(R.string.cfgTimeIntervalDefaultValue))) * 60 * 1000;
	}

	public String getName() {
		return sharedPreferences.getString(
				contextWrapper.getString(R.string.cfgCommonNameKey),
				contextWrapper.getString(R.string.cfgCommonNameDefaultValue));
	}

	public boolean isReminderEnabled() {
		return sharedPreferences.getBoolean(
				contextWrapper.getString(R.string.cfgReminderKey),
				contextWrapper.getResources().getBoolean(
						R.bool.cfgReminderDefaultValue));
	}

	public boolean isSoundEnabled() {
		return sharedPreferences.getBoolean(contextWrapper
				.getString(R.string.cfgSoundKey), contextWrapper.getResources()
				.getBoolean(R.bool.cfgSoundDefaultValue));
	}

	public int getTimeStart() {
		return Integer.parseInt(sharedPreferences.getString(
				contextWrapper.getString(R.string.cfgTimeStartKey),
				contextWrapper.getString(R.string.cfgTimeStartDefaultValue)));
	}

	public int getTimeEnd() {
		return Integer.parseInt(sharedPreferences.getString(
				contextWrapper.getString(R.string.cfgTimeEndKey),
				contextWrapper.getString(R.string.cfgTimeEndDefaultValue)));
	}

	public boolean isVibrationEnabled() {
		return sharedPreferences.getBoolean(
				contextWrapper.getString(R.string.cfgVibrationKey),
				contextWrapper.getResources().getBoolean(
						R.bool.cfgVibrationDefaultValue));
	}

}