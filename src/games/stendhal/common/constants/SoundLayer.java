package games.stendhal.common.constants;

/**
 * the layer on which a sound is played
 *
 * @author hendrik
 */
public enum SoundLayer {

	/** nice background music which is considered out-of-game */
	BACKGROUND_MUSIC,

	/**
	 * sounds that are related to the zone and are in-game, 
	 * like a waterfall, wind, the hammering at the blacksmith
	 */
	AMBIENT_SOUND,

	/** noise made by creatures and NPCs */
	CREATURE_NOISE,

	/** noise made by weapons and armor */
	FIGHTING_NOISE,

	/** user interface feedback, opening of windows, private message notification */
	USER_INTERFACE;
}
