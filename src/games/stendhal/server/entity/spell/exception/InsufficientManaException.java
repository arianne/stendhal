package games.stendhal.server.entity.spell.exception;
/**
 * Exception signalling that a player has not enough mana
 *
 * @author madmetzger
 */
public class InsufficientManaException extends SpellException {

	/** */
	private static final long serialVersionUID = -831075222525944379L;

	public InsufficientManaException(String message) {
		super(message);
	}

}
