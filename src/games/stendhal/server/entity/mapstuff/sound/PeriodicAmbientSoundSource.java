package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.events.SoundEvent;
import marauroa.common.game.RPEvent;

/**
 * Periodicially plays an ambient sound.
 *
 * @author hendrik
 */
public class PeriodicAmbientSoundSource extends PassiveEntity implements TurnListener {
	private String[] sounds;
	private int radius;
	private int volume;
	private int minInterval;
	private int maxInterval;

	/**
	 * Create an ambient sound area.
	 * 
	 * @param sound   name of sound (or sounds) to play
	 * @param radius  hearing radius
	 * @param volume  volume
	 * @param minInterval minimum time between two sounds in seconds
	 * @param maxInterval maximum time between two sounds in seconds
	 */
	public PeriodicAmbientSoundSource(String sound, int radius, int volume, int minInterval, int maxInterval) {
		setRPClass("entity");
		this.sounds = sound.split(", *");
		this.radius = radius;
		this.volume = volume;
		this.minInterval = minInterval;
		this.maxInterval = maxInterval;

		setupNotifier();
	}

	/**
	 * sets the turn notifier up to notify us at a random point in the 
	 * future between minInterval and maxInterval
	 */
	private void setupNotifier() {
		double seconds = (Math.random() * (maxInterval - minInterval)) + minInterval;
		TurnNotifier.get().notifyInSeconds((int) seconds, this);
	}

	/**
	 * randomly picks a sound, sends a SoundEvent to the client
	 * and sets the notifier up for the next sound.
	 */
	public void onTurnReached(int currentTurn) {
		String sound = sounds[(int) (Math.random() * sounds.length)];
		RPEvent event = new SoundEvent(sound, radius, volume, 1);
		this.addEvent(event);
		setupNotifier();
	}

}
