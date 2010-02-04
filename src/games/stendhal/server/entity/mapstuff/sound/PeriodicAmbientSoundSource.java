package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.PassiveEntity;

/**
 * Periodicially plays an ambient sound.
 *
 * @author hendrik
 */
public class PeriodicAmbientSoundSource extends PassiveEntity implements TurnListener {
	private String sound;
	private int radius;
	private int volume;
	private int minInterval;
	private int maxInterval;

	/**
	 * Create an ambient sound area.
	 */
	public PeriodicAmbientSoundSource(String sound, int radius, int volume, int minInterval, int maxInterval) {
		this.sound = sound;
		this.radius = radius;
		this.volume = volume;
		this.minInterval = minInterval;
		this.maxInterval = maxInterval;

		setupNotifier();
	}

	private void setupNotifier() {
		// TODO Auto-generated method stub
		
	}

	public void onTurnReached(int currentTurn) {
		// TODO Auto-generated method stub
		
	}

	// TODO: generate turn listener
}
