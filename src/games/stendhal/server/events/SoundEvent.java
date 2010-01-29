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
}
