package games.stendhal.client.scripting;

import games.stendhal.client.actions.SlashAction;
import games.stendhal.common.ErrorBuffer;

/**
 * Command line parser for the Stendhal client.
 * 
 * @author Martin Fuchs
 */
public class SlashActionCommand extends ErrorBuffer {

	private String name;
	private SlashAction action;

	private String[] params;
	private String remainder;

	/**
	 * @return action object
	 */
	SlashAction getAction() {
		return action;
	}

	/**
	 * sets the action to be parsed.
	 * 
	 * @param action
	 *            the action to be parsed
	 */
	void setAction(final SlashAction action) {
		this.action = action;
	}

	/**
	 * return command name.
	 * 
	 * @return command name
	 */
	public String getName() {
		return name;
	}

	/**
	 * return command parameters.
	 * 
	 * @return parameter array
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * set command parameters.
	 * 
	 * @param params
	 *            parameter array
	 */
	void setParams(final String[] params) {
		this.params = params;
	}

	/**
	 * return trailing parameter text.
	 * 
	 * @return remainder
	 */
	public String getRemainder() {
		return remainder;
	}

	/**
	 * sets the trailing text.
	 * 
	 * @param remainder
	 *            the trailing text
	 */
	void setRemainder(final String remainder) {
		this.remainder = remainder;
	}

	/**
	 * set command name.
	 * 
	 * @param name
	 *            the command name
	 */
	void setName(final String name) {
		this.name = name;
	}

}
