package games.stendhal.server.entity.mapstuff.sound;

import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.entity.PassiveEntity;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * Plays a sound and music in a loop.
 *
 * @author hendrik
 */
public class LoopedSoundSource extends PassiveEntity {
	private static final String RPCLASS_NAME = "looped_sound_source";


	/**
	 * Create an ambient sound area.
	 */
	public LoopedSoundSource() {
		setRPClass(RPCLASS_NAME);
		put("type", RPCLASS_NAME);
	}


	/**
	 * Create an ambient sound area.
	 */
	public LoopedSoundSource(String sound, int radius, int volume, SoundLayer layer) {
		setRPClass(RPCLASS_NAME);
		put("type", RPCLASS_NAME);
		put("sound", sound);
		put("radius", radius);
		put("volume", volume);
		put("layer", layer.ordinal());
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
		rpclass.add(DefinitionClass.ATTRIBUTE, "layer", Type.BYTE);
	}

}
