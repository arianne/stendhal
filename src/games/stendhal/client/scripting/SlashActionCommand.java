package games.stendhal.client.scripting;

import games.stendhal.client.actions.SlashAction;

/**
 * Command line parser for the Stendhal client.
 * 
 * @author Martin Fuchs
 */
public class SlashActionCommand {

	private String name;
	private SlashAction action;

	private String[] params;
	private String remainder;

	private String error = null;

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
	 * return whether some error occurred while parsing the input text.
	 * 
	 * @return error flag
	 */
	boolean hasError() {
		return error != null;
	}

	/**
	 * return error message.
	 * 
	 * @return error string
	 */
	public String getError() {
		return error;
	}

	/**
	 * set error flag.
	 * 
	 * @param error
	 *            the error message
	 * 
	 * @return this
	 */
	public SlashActionCommand setError(final String error) {
		if (this.error == null) {
			this.error = error;
		} else {
			this.error += "\n" + error;
		}

		return this;
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
