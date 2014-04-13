/*
Copyright (C) 2014 Markus Bšsling
This file is part of HydroMemo.

HydroMemo is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

HydroMemo is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
package de.boesling.hydromemo.services;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import de.boesling.hydromemo.BuildConfig;
import de.boesling.hydromemo.R;
import de.boesling.hydromemo.activities.Preferences;

public class Scheduler extends Service {

	private static final String LOG_TAG = Scheduler.class
			.getSimpleName();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	// This is the old onStart method that will be called on the pre-2.0
	// platform. On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) {
		handleCommand(intent);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return Service.START_NOT_STICKY;
	}

	private void handleCommand(Intent intent) {
		Context context = getApplicationContext();
		Intent notifierIntent = new Intent(context, Notifier.class);
		PendingIntent pendingNotifierIntent = PendingIntent.getService(context,
				0, notifierIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		SharedPreferences sharedPreferences = Preferences
				.getSharedPreferences(this);
		if (sharedPreferences.getBoolean(
				context.getString(R.string.cfgReminderKey), false)) {
			Integer cfgTimeIntervalValue = Integer
					.parseInt(sharedPreferences.getString(context
							.getString(R.string.cfgTimeIntervalKey), context
							.getString(R.string.cfgTimeIntervalDefaultValue))) * 60 * 1000;
			if (BuildConfig.DEBUG) {
				Log.d(LOG_TAG, "repeating interval " + cfgTimeIntervalValue
						+ " ms");
			}

			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + cfgTimeIntervalValue,
					cfgTimeIntervalValue, pendingNotifierIntent);
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(LOG_TAG, "cancel");
			}
			alarmManager.cancel(pendingNotifierIntent);
		}
	}

}
