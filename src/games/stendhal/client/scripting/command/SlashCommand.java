package games.stendhal.client.scripting.command;

//
//

/*
 * Eventually move these out from inner classes, then make them
 * dynamically configurable/loadable.
 */

/**
 * A chat command.
 */
public interface SlashCommand {
	/**
	 * Execute a chat command.
	 *
	 * @param	params		The formal parameters.
	 * @param	remainder	Line content after parameters.
	 *
	 * @return	<code>true</code> if command was handled.
	 */
	public boolean execute(String [] params, String remainder);


	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMaximumParameters();


	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMinimumParameters();


//Not yet
//	/**
//	 * Display usage for this command.
//	 *
//	 * @param	command		The command usage is for.
//	 * @param	detailed	Show detailed help, otherwise
//	 *				just 1-line synopsis.
//	 */
//	public void usage(String command, boolean detailed);
}

