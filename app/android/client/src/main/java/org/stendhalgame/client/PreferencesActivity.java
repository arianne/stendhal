/***************************************************************************
 *                 Copyright © 2022-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.stendhalgame.client;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


/**
 * Interface for configuring app preferences.
 */
public class PreferencesActivity extends AppCompatActivity {

	/** Preferences activity instance. */
	private static PreferencesActivity instance;


	/**
	 * Retrieves preferences activity instance.
	 */
	public static PreferencesActivity get() {
		return instance;
	}

	/**
	 * Called when the activity is created.
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// FIXME: may be considered unsafe as this is not technically a singleton
		instance = this;

		// use same orientation as main activity
		setRequestedOrientation(MainActivity.get().getRequestedOrientation());

		setContentView(R.layout.activity_preferences);

		if (findViewById(R.id.preferencesFrame) != null) {
			if (savedInstanceState != null) {
				return;
			}

			getSupportFragmentManager().beginTransaction().add(R.id.preferencesFrame,
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
				Logger.debug("PreferencesActivity.getInt: casting string return value to integer");
				return Integer.parseInt((String) obj);
			} else {
				Logger.debug("PreferencesActivity.getInt return value is integer");
				return (Integer) obj;
			}
		} catch (final NumberFormatException e) {
			final String errMsg = "An error occurred: " + e.getMessage();
			Logger.error(errMsg + "\n" + e.getStackTrace());
			Notifier.showError(errMsg + "\n\nSee " + Logger.getLogsDir()
				+ " for more information.");
		}
		return null;
	}

	@Override
	public void finish() {
		Logger.debug(PreferencesActivity.class.getName() + ".finish() called");
		super.finish();
	}

	@Override
	protected void onDestroy() {
		Logger.debug(PreferencesActivity.class.getName() + ".onDestroy() called");
		super.onDestroy();
	}


	public static class PFragment extends PreferenceFragmentCompat
			implements OnSharedPreferenceChangeListener {

		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
			addPreferencesFromResource(R.xml.preferences);
			initListeners();
		}

		private void initListeners() {
			final PreferenceManager pm = getPreferenceManager();

			// FIXME: should update playing music
			final Preference reset = pm.findPreference("reset");
			if (reset != null) {
				reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(final Preference pref) {
						Notifier.showPrompt(getActivity(), "Restore prefrences defaults?",
							new Notifier.Action() {
								@Override
								protected void onCall() {
									Logger.debug("restoring preferences default values");
									restoreDefaults();
								}
							},
							new Notifier.Action() {
								@Override
								protected void onCall() {
									// do nothing
								}
							});

						return true;
					}
				});
			} else {
				Logger.warn("preference \"reset\" not found");
			}

			final CheckBoxPreference title_music = (CheckBoxPreference) pm.findPreference("title_music");
			if (title_music != null) {
				title_music.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(final Preference pref, final Object obj) {
						// set music state as soon as preference is changed
						if (MainActivity.get().getActiveClientView().onTitleScreen()) {
							if (!((Boolean) obj)) {
								MusicPlayer.stopMusic();
							} else if (!MusicPlayer.isPlaying()) {
								MusicPlayer.playTitleMusic();
							}
						}

						return true;
					}
				});
			}

			final ListPreference song_list = (ListPreference) pm.findPreference("song_list");
			if (song_list != null) {
				// FIXME: not working in .xml config
				final String default_song = "title_01";
				song_list.setDefaultValue(default_song);
				if (song_list.getValue() == null) {
					// FIXME: is this working?
					song_list.setValue(default_song);
				}

				song_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(final Preference pref, final Object obj) {
						// set music state as soon as preference is changed
						if (MainActivity.get().getActiveClientView().onTitleScreen()) {
							Logger.debug("changing title music preference: " + (String) obj);

							if (title_music.isChecked()) {
								MusicPlayer.playTitleMusic((String) obj);
							}
						}

						return true;
					}
				});
			}

			final Preference clear_cache = pm.findPreference("clear_cache");
			if (clear_cache != null) {
				clear_cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(final Preference pref) {
						Notifier.showPrompt(getActivity(), "Delete cached data?",
							new Notifier.Action() {
								@Override
								protected void onCall() {
									Logger.debug("clearing cache");
									for (final ClientView clientView: MainActivity.get().getClientViewList()) {
										clientView.clearCache(true);
									}
								}
							},
							new Notifier.Action() {
								@Override
								protected void onCall() {
									// do nothing
								}
							});

						return true;
					}
				});
			} else {
				Logger.warn("preference \"clear_cache\" not found");
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
			if ("orientation".equals(key)) {
				Logger.debug("orientation set to \"" + PreferencesActivity.getString(key) + "\"");
				MainActivity.get().updateOrientation();
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
