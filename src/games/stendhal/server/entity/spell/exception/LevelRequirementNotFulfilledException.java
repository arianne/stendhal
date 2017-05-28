package games.stendhal.server.entity.spell.exception;
/**
 * Exception signalling that a player has not reached the minimum level for a spell
 *
 * @author madmetzger
 */
public class LevelRequirementNotFulfilledException extends SpellException {

	/**
	 *
	 */
	private static final long serialVersionUID = -5974619247472565915L;

	public LevelRequirementNotFulfilledException(String message) {
		super(message);
	}

}
