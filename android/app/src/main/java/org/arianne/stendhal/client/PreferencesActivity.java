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
import android.preference.Preference;
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

	/**
	 * Retrieves preferences.
	 *
	 * @return
	 *     <code>SharedPreferences</code> instance.
	 */
	public static SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(MainActivity.get());
	}

	/**
	 * Retrieves a string value from preferences.
	 *
	 * @param key
	 *     Key to search for.
	 * @param defVal
	 *     Default value.
	 * @return
	 *     Value retrieved using <code>key</code>.
	 */
	public static String getString(final String key, final String defVal) {
		return getSharedPreferences().getString(key, defVal);
	}

	/**
	 * Retrieves a string value from preferences.
	 *
	 * @param key
	 *     Key to search for.
	 * @return
	 *     Value retrieved using <code>key</code> (default: "").
	 */
	public static String getString(final String key) {
		return getSharedPreferences().getString(key, "");
	}

	/**
	 * Retrieves a boolean value from preferences.
	 *
	 * @param key
	 *     Key to search for.
	 * @param defVal
	 *     Default value.
	 * @return
	 *     Value retrieved using <code>key</code>.
	 */
	public static boolean getBoolean(final String key, final boolean defVal) {
		return getSharedPreferences().getBoolean(key, defVal);
	}

	/**
	 * Retrieves an integer number value from preferences.
	 *
	 * @param key
	 *     Key to search for.
	 * @param defVal
	 *     Default value.
	 * @return
	 *     Value retrieved using <code>key</code>.
	 */
	public static Integer getInt(final String key, final int defVal) {
		// return getSharedPreferences().getInt(key, defValue);
		try {
			// SharedPreferences.getInt is returning string
			final Object obj = getSharedPreferences().getString(key, String.valueOf(defVal));
			if (obj instanceof String) {
				DebugLog.debug("PreferencesActivity.getInt: casting string return value to integer");
				return Integer.parseInt((String) obj);
			} else {
				DebugLog.debug("PreferencesActivity.getInt return value is integer");
				return (Integer) obj;
			}
		} catch (final NumberFormatException e) {
			final String errMsg = "An error occurred: " + e.getMessage();
			DebugLog.error(errMsg + "\n" + e.getStackTrace());
			Notifier.get().showError(errMsg + "\n\nSee " + DebugLog.getLogsDir()
				+ " for more information.");
		}

		return null;
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


	public static class PFragment extends PreferenceFragment
			implements OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			final PreferenceManager pm = getPreferenceManager();
			final Preference reset = pm.findPreference("reset");
			if (reset != null) {
				reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					public boolean onPreferenceClick(final Preference pref) {
						Notifier.get().showPrompt(getActivity(), "Restore prefrences defaults?",
							new Notifier.Action() {
								protected void onCall() {
									DebugLog.debug("restoring preferences default values");
									restoreDefaults();
								}
							},
							new Notifier.Action() {
								protected void onCall() {
									// do nothing
								}
							});

						return true;
					}
				});
			} else {
				DebugLog.warn("preference \"reset\" not found");
			}
		}

		private void restoreDefaults() {
			final PreferenceManager pm = getPreferenceManager();
			final SharedPreferences prefs = pm.getDefaultSharedPreferences(getActivity());
			final SharedPreferences.Editor editor = prefs.edit();
			editor.clear();
			editor.commit();
			pm.setDefaultValues(getActivity(), R.xml.preferences, true);
			getPreferenceScreen().removeAll();
			onCreate(null);
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
