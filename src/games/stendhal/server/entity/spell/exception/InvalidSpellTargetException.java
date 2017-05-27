package games.stendhal.server.entity.spell.exception;

/**
 * Exception signalling that a spell target is invalid for the spell
 *
 * @author madmetzger
 */
public class InvalidSpellTargetException extends SpellException {

	/**
	 *
	 */
	private static final long serialVersionUID = -1841020776046182426L;

	public InvalidSpellTargetException(String message) {
		super(message);
	}

}
