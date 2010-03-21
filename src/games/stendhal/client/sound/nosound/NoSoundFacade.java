package games.stendhal.client.sound.nosound;

import games.stendhal.client.sound.SoundGroup;
import games.stendhal.client.sound.SoundHandle;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.system.Time;

import java.util.Collection;
import java.util.LinkedList;

public class NoSoundFacade implements SoundSystemFacade {

	public void changeVolume(float volume) {
		// do nothing
	}

	public void exit() {
		// do nothing
	}

	public SoundGroup getGroup(String groupName) {
		// do nothing
		return new NoSoundGroup();
	}

	public Collection<String> getGroupNames() {
		// do nothing
		return new LinkedList<String>();
	}

	public float getVolume() {
		// do nothing
		return 0;
	}

	public void mute(boolean turnOffSound, boolean useFading, Time delay) {
		// do nothing
	}

	public void playerMoved() {
		// do nothing
	}

	public void stop(SoundHandle sound, Time fadingDuration) {
		// do nothing
	}

	public void update() {
		// do nothing
	}

	public void zoneEntered(String zoneName) {
		// do nothing
	}

	public void zoneLeft(String zoneName) {
		// do nothing
	}

}
