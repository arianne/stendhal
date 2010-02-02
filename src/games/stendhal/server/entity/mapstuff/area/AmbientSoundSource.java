package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.entity.PassiveEntity;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

/**
 * Plays an ambient sound in a loop.
 *
 * @author hendrik
 */
public class AmbientSoundSource extends PassiveEntity {
	private static final String RPCLASS_NAME = "ambient_sound_source";


	/**
	 * Create an ambient sound area.
	 */
	public AmbientSoundSource() {
		setRPClass(RPCLASS_NAME);
	}


	/**
	 * Create an ambient sound area.
	 */
	public AmbientSoundSource(String sound, int radius, int volume) {
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
