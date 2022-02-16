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

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.AppCompatActivity;


public class PreferencesActivity extends AppCompatActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);

		if (findViewById(R.id.preferencesFrame) != null) {
			if (savedInstanceState != null) {
				return;
			}

			final PreferenceFragment frag = new PreferenceFragment() {
				@Override
				public void onCreate(final Bundle savedInstanceState) {
					super.onCreate(savedInstanceState);
					addPreferencesFromResource(R.xml.preferences);
				}
			};

			getFragmentManager().beginTransaction().add(R.id.preferencesFrame, frag).commit();
		}
	}
}
