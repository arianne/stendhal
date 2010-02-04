package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.server.entity.PassiveEntity;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

/**
 * Plays an ambient sound in a loop.
 *
 * @author hendrik
 */
public class LoopedAmbientSoundSource extends PassiveEntity {
	private static final String RPCLASS_NAME = "looped_ambient_sound_source";


	/**
	 * Create an ambient sound area.
	 */
	public LoopedAmbientSoundSource() {
		setRPClass(RPCLASS_NAME);
	}


	/**
	 * Create an ambient sound area.
	 */
	public LoopedAmbientSoundSource(String sound, int radius, int volume) {
		setRPClass(RPCLASS_NAME);
		put("sound", sound);
		put("radius", radius);
		put("volume", volume);
	}


	/**
	 * generates the RPClass
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.isA("area");
		rpclass.addAttribute("sound", Type.STRING);
		rpclass.addAttribute("radius", Type.INT);
		rpclass.addAttribute("volume", Type.BYTE);
	}

}
