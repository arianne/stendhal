package games.stendhal.server.script;

import games.stendhal.server.scripting.ScriptImpl;

/**
 * Creates an NPC which manages bets.
 *
 * <p>A game master has to tell him on what the players can bet:
 * <pre>accept fire and water</pre></p>
 *
 * <p>Then players can bet by saying something like
 * <pre>bet 50 ham on fire
 * bet 5 cheese on water</pre>
 * The NPC retrieves the items from the player and registers the bet.</p>
 * 
 * <p>The game master starts the action closing the betting time:
 * <pre>Let the fun begin</pre></p>
 *
 * <p>After the game the game master has to tell the NPC who won:</p>
 * <pre>the winner is fire</pre>.
 *
 * <p>The NPC will than tell all players the results and give it to winners:
 * <pre>mort betted 50 ham on fire and won an additional 50 ham
 * hendrik lost 5 cheese betting on water</pre></p>   
 * 
 * Note: Betting is possible in "idle state" to enable interaction of a large
 * number of players in a short time. (The last time i did a show fight i was
 * losing count because there where more than 15 players)
 *
 * @author hendrik
 */
public class BetManager extends ScriptImpl {

	/**
	 * 
	 */
	public BetManager() {
		// TODO Auto-generated constructor stub
	}

}
