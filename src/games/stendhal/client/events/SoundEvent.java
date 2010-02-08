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
		SoundSystemFacade.get().play(event.get("sound") + ".ogg", entity.getX(), entity.getY(), event.getInt("radius"), layer, 100);
	}

}
