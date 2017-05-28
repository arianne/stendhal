/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sound.nosound;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundHandle;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.facade.Time;

public class NoSoundFacade implements SoundSystemFacade {

	@Override
	public void changeVolume(float volume) {
		// do nothing
	}

	@Override
	public void exit() {
		// do nothing
	}

	@Override
	public SoundGroup getGroup(String groupName) {
		// do nothing
		return new NoSoundGroup();
	}

	@Override
	public Collection<String> getGroupNames() {
		// do nothing
		return new LinkedList<String>();
	}

	@Override
	public float getVolume() {
		// do nothing
		return 0;
	}

	@Override
	public void mute(boolean turnOffSound, boolean useFading, Time delay) {
		// do nothing
	}

	@Override
	public void stop(SoundHandle sound, Time fadingDuration) {
		// do nothing
	}

	@Override
	public void update() {
		// do nothing
	}

	@Override
	public void positionChanged(double x, double y) {
		// do nothing
	}

	@Override
	public List<String> getDeviceNames() {
		return new LinkedList<String>();
	}
}
