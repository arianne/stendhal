package games.stendhal.server.entity.spell.exception;
/**
 * Exception signalling that a spell that was tried to cast is not yet cooled down
 *
 * @author madmetzger
 */
public class SpellNotCooledDownException extends SpellException {

	/**
	 *
	 */
	private static final long serialVersionUID = 2568358660593994464L;

	public SpellNotCooledDownException(String message) {
		super(message);
	}

}
