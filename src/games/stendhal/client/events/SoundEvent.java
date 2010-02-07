package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.soundreview.SoundMaster;
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
		SoundMaster.play(SoundLayer.CREATURE_NOISE, event.get("sound") + ".ogg", entity.getX(), entity.getY(), event.getInt("radius"));
	}

}
