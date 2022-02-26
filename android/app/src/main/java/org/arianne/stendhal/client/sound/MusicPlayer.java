/***************************************************************************
 *                     Copyright © 2022 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.arianne.stendhal.client.sound;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import org.arianne.stendhal.client.DebugLog;
import org.arianne.stendhal.client.MainActivity;


public class MusicPlayer {

	private static MediaPlayer mplayer = null;


	public static void playMusic(final Context ctx, final int id, final boolean loop) {
		if (isPlaying()) {
			DebugLog.debug("freeing up MusicPlayer instance to play new song");
			stopMusic();
		}

		DebugLog.debug("starting music (loop: " + loop + ")");

		mplayer = new MediaPlayer();
		mplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			public void onPrepared(final MediaPlayer mp) {
				DebugLog.debug("starting music");
				// FIXME: not working
				//mplayer.setLooping(loop);
				mplayer.start();
			}
		});
		mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(final MediaPlayer mp) {
				// free up resources & reset player to null
				stopMusic();

				// MediaPlayer.setLooping not working so manually loop
				if (loop) {
					playMusic(ctx, id, loop);
				}
			}
		});

		try {
			mplayer.setDataSource(ctx, parseResourceUri(id));
			mplayer.prepareAsync();
		} catch (final IOException e) {
			DebugLog.error("failed to load resource " + id + ":\n" + e.getStackTrace());
		}
	}

	public static void playMusic(final int id, final boolean loop) {
		playMusic(MainActivity.get(), id, loop);
	}

	public static void stopMusic() {
		if (mplayer != null) {
			if (mplayer.isPlaying()) {
				DebugLog.debug("stopping music");
				mplayer.stop();
			}
			mplayer.release();
			mplayer = null;
		}
	}

	public static boolean isPlaying() {
		return (mplayer != null);
	}

	private static Uri parseResourceUri(final int id) {
		return Uri.parse("android.resource://" + MainActivity.get().getPackageName()
			+ "/" + id);
	}
}
