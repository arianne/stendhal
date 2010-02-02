package games.stendhal.client.sound;

import games.stendhal.client.WorldObjects.WorldListener;
import games.stendhal.client.entity.User;
import games.stendhal.client.sound.manager.SoundManager;

/**
 * this class is the main interface between the game logic and the low level sound system
 *
 * @author hendrik
 */
public class SoundSystemFacade implements WorldListener {
	private static SoundSystemFacade instance;

	public static WorldListener get() {
		if (instance == null) {
			instance = new SoundSystemFacade();
		}
		return instance;
	}
	public void playerMoved() {
		float[] position = new float[] {(float) User.get().getX(), (float) User.get().getY()};
		SoundManager.get().setHearerPosition(position);
	}

	public void zoneEntered(String zoneName) {
		// TODO: setup ambient sounds (which should be done server side using plain entities in the long run)
	}

	public void zoneLeft(String zoneName) {
		// TODO: stop ambient sounds
	}


}
