package games.stendhal.client.sound.sound;

import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.entity.User;
import games.stendhal.client.sound.SoundGroup;
import games.stendhal.client.sound.SoundHandle;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.manager.SoundManagerNG.Sound;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.math.Algebra;

import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * this class is the interface between the game logic and the
 * sound system.
 * 
 * @author hendrik, silvio
 */
public class SoundSystemFacadeImpl implements SoundSystemFacade, WorldListener {
	private static Logger logger = Logger.getLogger(SoundSystemFacadeImpl.class);
	
	private ExtendedSoundManager manager = new ExtendedSoundManager();

	public void playerMoved() {
		try {
			float[] position = Algebra.vecf((float) User.get().getX(), (float) User.get().getY());
			manager.setHearerPosition(position);
			manager.update();
		} catch (RuntimeException e) {
			logger.error(e, e);
		}
	}

	public void zoneEntered(String zoneName) {
		// ignored
	}

	public void zoneLeft(String zoneName) {
		// ignored
	}

	public void exit() {
		try {
			manager.exit();
		} catch (RuntimeException e) {
			logger.error(e, e);
		}
	}

	public SoundGroup getGroup(String groupName) {
		return manager.getGroup(groupName);
	}

	public void update() {
		try {
			manager.update();
		} catch (RuntimeException e) {
			logger.error(e, e);
		}
	}

	public void stop(SoundHandle sound, Time fadingDuration) {
		try {
			manager.stop((Sound) sound, fadingDuration);
		} catch (RuntimeException e) {
			logger.error(e, e);
		}
	}

	public void mute(boolean turnOffSound, boolean useFading, Time delay) {
		manager.mute(turnOffSound, useFading, delay);
	}

	public float getVolume() {
		return manager.getVolume();
	}

	public Collection<String> getGroupNames() {
		return manager.getGroupNames();
	}

	public void changeVolume(float volume) {
		manager.changeVolume(volume);
	}
}
