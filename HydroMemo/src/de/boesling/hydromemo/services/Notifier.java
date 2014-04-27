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

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import de.boesling.hydromemo.R;
import de.boesling.hydromemo.activities.Preferences;

public class Notifier extends Service {

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
		if (isReminderEnabled()) {
			if (isInConfiguredTimeRange()) {
				sendNotification();
			}
		}
	}

	@SuppressLint("InlinedApi")
	private void sendNotification() {
		Context context = getApplicationContext();

		SharedPreferences sharedPreferences = Preferences
				.getSharedPreferences(this);
		String cfgCommonNameValue = sharedPreferences.getString(
				getString(R.string.cfgCommonNameKey),
				getString(R.string.cfgCommonNameDefaultValue));

		Intent drinkDemandIntent = new Intent(context, DrinkDemand.class);
		PendingIntent pendingDrinkDemandIntent = PendingIntent.getService(
				context, 0, drinkDemandIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		String notificationText = getString(R.string.notificationText);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		builder.setContentTitle(cfgCommonNameValue);
		builder.setContentText(notificationText);
		builder.setSmallIcon(R.drawable.ic_stat_notify_small_bottle);

		Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_stat_notify_large_bottle);
		builder.setLargeIcon(icon);

		builder.setTicker(notificationText);
		builder.setContentIntent(pendingDrinkDemandIntent);
		builder.setAutoCancel(true);
		builder.setOnlyAlertOnce(false);
		builder.setOngoing(true);
		builder.setNumber(++DrinkDemand.nDrinkDemandsPending);
		builder.setPriority(Notification.PRIORITY_DEFAULT);

		boolean isVibrationEnabled = sharedPreferences.getBoolean(
				getString(R.string.cfgVibrationKey),
				getResources().getBoolean(R.bool.cfgVibrationDefaultValue));
		if (isVibrationEnabled == true) {
			builder.setVibrate(new long[] { 0, 300, 100, 300, 300, 300, 100,
					100, 100, 100, 100, 100, 100 });
		}
		builder.setDefaults(Notification.DEFAULT_LIGHTS);
		builder.setUsesChronometer(true);

		Notification notification = builder.build();

		boolean isSoundEnabled = sharedPreferences.getBoolean(
				getString(R.string.cfgSoundKey),
				getResources().getBoolean(R.bool.cfgSoundDefaultValue));
		if (isSoundEnabled == true) {
			notification.sound = Uri.parse("android.resource://"
					+ context.getPackageName() + "/"
					+ chooseNotificationSound());
		}

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(42, notification);
	}

	private int chooseNotificationSound() {
		if (DrinkDemand.nDrinkDemandsPending <= 2) {
			return R.raw.water_drop;
		} else {
			return R.raw.water_in_glass;
		}
	}

	private boolean isReminderEnabled() {
		SharedPreferences sharedPreferences = Preferences
				.getSharedPreferences(this);
		return sharedPreferences.getBoolean(getString(R.string.cfgReminderKey),
				false);
	}

	private boolean isInConfiguredTimeRange() {
		SharedPreferences sharedPreferences = Preferences
				.getSharedPreferences(this);
		int cfgTimeStartValue = Integer.parseInt(sharedPreferences.getString(
				getString(R.string.cfgTimeStartKey),
				getString(R.string.cfgTimeStartDefaultValue)));
		int cfgTimeEndValue = Integer.parseInt(sharedPreferences.getString(
				getString(R.string.cfgTimeEndKey),
				getString(R.string.cfgTimeEndDefaultValue)));
		Calendar now = Calendar.getInstance();

		return (cfgTimeStartValue <= now.get(Calendar.HOUR_OF_DAY))
				&& (now.get(Calendar.HOUR_OF_DAY) < cfgTimeEndValue);
	}
}
