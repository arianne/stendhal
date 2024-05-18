/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sound.facade;

import java.util.Collection;
import java.util.List;

import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.common.constants.SoundLayer;

/**
 * this class is the interface between the game logic and the
 * sound system.
 *
 * @author hendrik, silvio
 */
public interface SoundSystemFacade extends PositionChangeListener {
	public void exit();

	public SoundGroup getGroup(String groupName);

	public void update();

	public void stop(SoundHandle sound, Time fadingDuration);

	public void mute(boolean turnOffSound, boolean useFading, Time delay);

	public float getVolume();

	public Collection<String> getGroupNames();

	public void changeVolume(float volume);

	public List<String> getDeviceNames();

	public SoundHandle playLocalizedEffect(String name, int x, int y, int radius, SoundLayer layer,
			float volume, boolean loop);

	public SoundHandle playLocalizedEffect(String name, int x, int y, SoundLayer layer);

	public SoundHandle playGlobalizedEffect(String name, SoundLayer layer, float volume, boolean loop);

	public SoundHandle playGlobalizedEffect(String name, SoundLayer layer);
}
