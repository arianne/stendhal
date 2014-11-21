/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.wt.core;

/**
 * SettingChangeListener that calls changed() with the current property value,
 * or with the default value at creation. <b>Note that changed() is called from
 * the SettingChangeAdapter constructor, so it <em>must not</em> rely on a fully
 * constructed object.</b>
 */
public abstract class SettingChangeAdapter implements SettingChangeListener {
	public SettingChangeAdapter(String key, String defaultValue) {
		changed(WtWindowManager.getInstance().getProperty(key, defaultValue));
	}

	@Override
	public abstract void changed(String newValue);
}
