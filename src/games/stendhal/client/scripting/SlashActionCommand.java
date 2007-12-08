package games.stendhal.client.scripting;

import games.stendhal.client.actions.SlashAction;

/**
 * Command line parser for the Stendhal client
 * @author Martin Fuchs
 */
public class SlashActionCommand {

	String _name;
	SlashAction _action;

	String[] _params;
	String _remainder;

	boolean _error = false;

	/**
	 * @return action object
	 */
	SlashAction getAction()
	{
		return _action;
	}

	/**
	 * return command name
	 * @return command name
	 */
	public String getName()
    {
	    return _name;
    }

	/**
	 * return command parameters
	 * @return parameter array
	 */
	public String[] getParams()
    {
	    return _params;
    }

	/**
	 * return trailing parameter text
	 * @return remainder
	 */
	public String getRemainder()
    {
	    return _remainder;
    }

	/**
	 * return whether some error occurred while parsing the input text
	 * @return error flag
	 */
	boolean getError()
	{
		return _error;
	}

	/**
	 * set error flag
	 * @return this
	 */
	public SlashActionCommand setError()
    {
	    _error = true;
	    return this;
    }

}
