/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package games.stendhal.client.entity;

import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundFile.Type;
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

	private AudibleCircleArea mAudibleArea = new AudibleCircleArea(Algebra.vecf(0, 0), 1.5f, 23);
	private HashMap<String, ArrayList<String>> mCategorys = new HashMap<String, ArrayList<String>>();
	private long mWaitTime = 0;

	protected void addSounds(String groupName, String categoryName, String... soundNames) {
		ArrayList<String> soundNameList = mCategorys.get(categoryName);
		SoundSystemFacade.Group group = SoundSystemFacade.get().getGroup(groupName);

		if (soundNameList == null) {
			soundNameList = new ArrayList<String>();
		}

		for (String name : soundNames) {
			if (group.loadSound(name, "audio:/" + name + ".ogg", Type.OGG, false)) {
				soundNameList.add(name);
			}
		}

		if (soundNameList.size() > 0) {
			mCategorys.put(categoryName, soundNameList);
		}
	}

	protected String getRandomSoundFromCategory(String groupName) {
		ArrayList<String> soundNameList = mCategorys.get(groupName);

		if ((soundNameList != null) && !soundNameList.isEmpty()) {
			return soundNameList.get(Rand.rand(soundNameList.size()));
		}

		return null;
	}

	@Override
	protected void onPosition(double x, double y) {
		super.onPosition(x, y);
		mAudibleArea.setPosition(Algebra.vecf((float) x, (float) y));
		SoundSystemFacade.get().update();
	}

	protected void playSound(String groupName, String soundName) {
		SoundSystemFacade.Group group = SoundSystemFacade.get().getGroup(groupName);
		group.play(soundName, 0, mAudibleArea, new Time(), false, true);
	}

	protected void playRandomSoundFromCategory(String groupName, String categoryName) {
		SoundSystemFacade.Group group = SoundSystemFacade.get().getGroup(groupName);
		group.play(getRandomSoundFromCategory(categoryName), 0, mAudibleArea, new Time(), false, true);
	}

	protected void playRandomSoundFromGroup(String groupName, String categoryName, long waitTimeInMilliSec) {
		if (mWaitTime < System.currentTimeMillis() && Rand.rand(100) < 5) {
			playRandomSoundFromCategory(groupName, categoryName);
			mWaitTime = System.currentTimeMillis() + waitTimeInMilliSec;
		}
	}
}
