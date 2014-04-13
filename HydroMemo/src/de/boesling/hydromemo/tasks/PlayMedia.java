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
package de.boesling.hydromemo.tasks;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

public class PlayMedia extends AsyncTask<Void, Void, Void> {

	private static final String LOG_TAG = PlayMedia.class.getSimpleName();

	private MediaPlayer mediaPlayer;

	public PlayMedia(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			Log.e(LOG_TAG, "", e);
		} catch (SecurityException e) {
			Log.e(LOG_TAG, "", e);
		} catch (IllegalStateException e) {
			Log.e(LOG_TAG, "", e);
		}

		return null;
	}
}
