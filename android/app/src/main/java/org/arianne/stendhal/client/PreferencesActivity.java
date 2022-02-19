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
package org.arianne.stendhal.client;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;


public class PreferencesActivity extends AppCompatActivity {

	private static PreferencesActivity instance;


	public static PreferencesActivity get() {
		return instance;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;

		setContentView(R.layout.activity_preferences);

		if (findViewById(R.id.preferencesFrame) != null) {
			if (savedInstanceState != null) {
				return;
			}

			getFragmentManager().beginTransaction().add(R.id.preferencesFrame,
				new PFragment()).commit();
		}
	}

	public static SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(MainActivity.get());
	}

	@Override
	public void finish() {
		DebugLog.debug(PreferencesActivity.class.getName() + ".finish() called");

		super.finish();
	}

	@Override
	protected void onDestroy() {
		DebugLog.debug(PreferencesActivity.class.getName() + ".onDestroy() called");

		super.onDestroy();
	}


	private static class PFragment extends PreferenceFragment
			implements OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}

		@Override
		public void onSharedPreferenceChanged(final SharedPreferences sp, final String key) {
			if (key.equals("show_dpad")) {
				DPad.get().onRefreshView();
			}
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
			super.onPause();
		}
	}
}
