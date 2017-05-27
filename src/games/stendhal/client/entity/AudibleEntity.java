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
package games.stendhal.client.entity;

import java.util.ArrayList;
import java.util.HashMap;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.sound.facade.AudibleCircleArea;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.Time;
import games.stendhal.common.Rand;
import games.stendhal.common.math.Algebra;

/**
 * An entity that can make noises.
 *
 * @author silvio
 */
abstract class AudibleEntity extends ActiveEntity {
	private final AudibleCircleArea mAudibleArea = new AudibleCircleArea(Algebra.vecf(0, 0), 1.5f, 23);
	private final HashMap<String, ArrayList<String>> mCategorys = new HashMap<String, ArrayList<String>>();
	private long mWaitTime = 0;

	protected void addSounds(String groupName, String categoryName, String... soundNames) {
		ArrayList<String> soundNameList = mCategorys.get(categoryName);
		SoundGroup group = ClientSingletonRepository.getSound().getGroup(groupName);

		if (soundNameList == null) {
			soundNameList = new ArrayList<String>();
		}

		for (String name : soundNames) {
			if (group.loadSound(name, name + ".ogg", SoundFileType.OGG, false)) {
				soundNameList.add(name);
			}
		}

		if (soundNameList.size() > 0) {
			mCategorys.put(categoryName, soundNameList);
		}
	}

	/**
	 * Get a random sound name from named group.
	 *
	 * @param groupName sound group name
	 * @return sound name, or <code>null</code> if the group does not exist or
	 * 	has no sounds
	 */
	private String getRandomSoundFromCategory(String groupName) {
		ArrayList<String> soundNameList = mCategorys.get(groupName);

		if ((soundNameList != null) && !soundNameList.isEmpty()) {
			return Rand.rand(soundNameList);
		}

		return null;
	}

	private String getSoundFromCategory(String groupName) {
	    ArrayList<String> soundNameList = mCategorys.get(groupName);

	    if ((soundNameList != null) && !soundNameList.isEmpty()) {
	        return soundNameList.get(0);
	    }

	    return null;
	}

	private String getSoundFromCategory(String groupName, int index) {
	    ArrayList<String> soundNameList = mCategorys.get(groupName);

	    if ((soundNameList != null) && !soundNameList.isEmpty()) {
	        return soundNameList.get(index);
	    }

	    return null;
	}

	@Override
	protected void onPosition(double x, double y) {
		super.onPosition(x, y);
		mAudibleArea.setPosition(Algebra.vecf((float) x, (float) y));
		ClientSingletonRepository.getSound().update();
	}

	protected void playRandomSoundFromCategory(String groupName, String categoryName) {
		SoundGroup group = ClientSingletonRepository.getSound().getGroup(groupName);
		group.play(getRandomSoundFromCategory(categoryName), 0, mAudibleArea, new Time(), false, true);
	}

	protected void playRandomSoundFromGroup(String groupName, String categoryName, long waitTimeInMilliSec) {
		if (mWaitTime < System.currentTimeMillis() && Rand.rand(100) < 5) {
			playRandomSoundFromCategory(groupName, categoryName);
			mWaitTime = System.currentTimeMillis() + waitTimeInMilliSec;
		}
	}

	protected void playSoundFromCategory(String groupName, String categoryName) {
	    SoundGroup group = ClientSingletonRepository.getSound().getGroup(groupName);
	    group.play(getSoundFromCategory(categoryName), 0, mAudibleArea, new Time(), false, true);
	}

    protected void playSoundFromCategory(String groupName, String categoryName, int index) {
        SoundGroup group = ClientSingletonRepository.getSound().getGroup(groupName);
        group.play(getSoundFromCategory(categoryName, index), 0, mAudibleArea, new Time(), false, true);
    }
}
