/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package games.stendhal.client.entity;

import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.client.sound.manager.SoundManager.Sound;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.Rand;
import games.stendhal.common.math.Algebra;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author silvio
 */
public abstract class AudibleEntity extends RPEntity {

	private AudibleCircleArea mAudibleArea = new AudibleCircleArea(Algebra.vecf(0, 0), 8, 20);
	private HashMap<String, ArrayList<String>> mSoundGroups = new HashMap<String, ArrayList<String>>();
	private long mWaitTime = 0;

	protected void addSoundsToGroup(String groupName, String... soundNames) {
		ArrayList<String> soundNameList = mSoundGroups.get(groupName);

		if (soundNameList == null) {
			soundNameList = new ArrayList<String>();
			mSoundGroups.put(groupName, soundNameList);
		}

		for (String name : soundNames) {
			if (SoundSystemFacade.get().loadSound(name, "audio:/" + name + ".ogg", Type.OGG, false) != null) {
				soundNameList.add(name);
			}
		}
	}

	protected Sound getRandomSoundFromGroup(String groupName, boolean getCloned) {
		ArrayList<String> soundNameList = mSoundGroups.get(groupName);

		if (soundNameList != null) {
			String name = soundNameList.get(Rand.rand(soundNameList.size()));
			Sound sound = SoundSystemFacade.get().getSound(name);

			if (sound != null && getCloned) {
				return sound.clone();
			}
		}

		return null;
	}

	protected Sound getSound(String soundName, boolean getCloned) {
		Sound sound = SoundSystemFacade.get().getSound(soundName);

		if (sound != null && getCloned) {
			sound = sound.clone();
		}

		return sound;
	}

	@Override
	protected void onPosition(double x, double y) {
		super.onPosition(x, y);
		mAudibleArea.setPosition(Algebra.vecf((float) x, (float) y));
		SoundSystemFacade.get().update();
	}

	protected void playRandomSoundFromGroup(String groupName, float volume) {
		Sound sound = getRandomSoundFromGroup(groupName, true);
		SoundSystemFacade.get().play(sound, volume, 0, mAudibleArea, false, new Time());
	}

	protected void playRandomSoundFromGroup(String groupName, float volume, long waitTimeInMilliSec) {
		if (mWaitTime < System.currentTimeMillis() && Rand.rand(100) < 5) {
			playRandomSoundFromGroup(groupName, volume);
			mWaitTime = System.currentTimeMillis() + waitTimeInMilliSec;
		}
	}

	protected void playSound(String soundName, float volume) {
		Sound sound = getSound(soundName, true);
		SoundSystemFacade.get().play(sound, volume, 0, mAudibleArea, false, new Time());
	}
}
