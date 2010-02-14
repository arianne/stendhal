package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.common.constants.SoundLayer;

/**
 * plays a sound
 *
 * @author hendrik
 */
public class SoundEvent extends Event<Entity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		SoundLayer layer = SoundLayer.AMBIENT_SOUND;
		int idx = event.getInt("layer");
		if (idx < SoundLayer.values().length) {
			layer = SoundLayer.values()[idx];
		}
		int volume = 100;
		if (event.has("volume")) {
			volume = event.getInt("volume");
		}
		int radius = 100000;
		if (event.has("radius")) {
			radius = event.getInt("radius");
		}
		SoundSystemFacade.get().play(event.get("sound"), entity.getX(), entity.getY(), radius, layer, volume);
	}

}
