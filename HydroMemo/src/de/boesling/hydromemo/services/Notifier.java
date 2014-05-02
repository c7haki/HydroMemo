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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import de.boesling.hydromemo.R;
import de.boesling.hydromemo.activities.PreferencesHelper;

public class Notifier extends Service {

	private static final long dit = 100;
	private static final long dah = 3 * dit;
	private static final Long[][] hydromemo = {
			{ dit, dit, dit, dit, dit, dit, dit },
			{ dah, dit, dit, dit, dah, dit, dah }, { dah, dit, dit, dit, dit },
			{ dit, dit, dah, dit, dit }, { dah, dit, dah, dit, dah },
			{ dah, dit, dah }, { dit }, { dah, dit, dah },
			{ dah, dit, dah, dit, dah } };

	private PreferencesHelper preferences;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.preferences = new PreferencesHelper(this);
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
		if (preferences.isReminderEnabled()) {
			if (isInConfiguredTimeRange()) {
				sendNotification();
			}
		}
	}

	@SuppressLint("InlinedApi")
	private void sendNotification() {
		Context context = getApplicationContext();

		Intent drinkDemandIntent = new Intent(context, DrinkDemand.class);
		PendingIntent pendingDrinkDemandIntent = PendingIntent.getService(
				context, 0, drinkDemandIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		String notificationText = getString(R.string.notificationText);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		builder.setContentTitle(preferences.getName());
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

		if (preferences.isVibrationEnabled()) {
			builder.setVibrate(getVibrationPattern());
		}
		builder.setDefaults(Notification.DEFAULT_LIGHTS);
		builder.setUsesChronometer(true);

		Notification notification = builder.build();

		if (preferences.isSoundEnabled()) {
			notification.sound = Uri.parse("android.resource://"
					+ context.getPackageName() + "/"
					+ chooseNotificationSound());
		}

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(42, notification);
	}

	private int chooseNotificationSound() {
		switch (getIncrement()) {
		case 1:
			return R.raw.water_drop;
		case 2:
			return R.raw.water_drop_x2;
		case 3:
			return R.raw.water_in_glass;
		case 4:
			return R.raw.water_in_glass_x2;
		default:
			return R.raw.water_in_glass_x2;
		}
	}

	private long[] getVibrationPattern() {
		ArrayList<Long> vp = new ArrayList<Long>();
		vp.add(0L);
		for (int i = 0; i < Math.min(getIncrement(), hydromemo.length); ++i) {
			if (i > 0) {
				vp.add(dit);
			}
			vp.addAll(Arrays.asList(hydromemo[i]));
		}

		long[] vibrationPattern = new long[vp.size()];
		for (int i = 0; i < vp.size(); ++i) {
			vibrationPattern[i] = vp.get(i).longValue();
		}
		return vibrationPattern;
	}

	private int getIncrement() {
		return preferences.isIncrementEnabled() ? DrinkDemand.nDrinkDemandsPending
				: 1;
	}

	private boolean isInConfiguredTimeRange() {
		int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return (preferences.getTimeStart() <= hourOfDay)
				&& (hourOfDay < preferences.getTimeEnd());
	}
}
