package games.stendhal.server.events;

import games.stendhal.common.constants.Events;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * A sound.
 *
 * @author hendrik
 */
public class SoundEvent extends RPEvent {

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.SOUND);
		rpclass.add(DefinitionClass.ATTRIBUTE, "sound", Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "radius", Type.INT);
		rpclass.add(DefinitionClass.ATTRIBUTE, "volume", Type.INT);
		rpclass.add(DefinitionClass.ATTRIBUTE, "layer", Type.BYTE);
	}

	/**
	 * Creates a new sound event.
	 *
	 * @param sound name of sound to play
	 */
	public SoundEvent(final String sound) {
		super(Events.SOUND);
		put("sound", sound);
	}


	/**
	 * creates a new sound event.
	 *
	 * @param sound name of sound to play
	 * @param radius radius
	 * @param volume volume
	 * @param layer layer (e. g. ambient sound)
	 */
	public SoundEvent(String sound, int radius, int volume, int layer) {
		super(Events.SOUND);
		put("sound", sound);
		put("radius", radius);
		put("volume", volume);
		put("layer", layer);
	}
}
