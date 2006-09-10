package games.stendhal.server.script;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.scripting.ScriptImpl;
import games.stendhal.server.scripting.ScriptingNPC;
import games.stendhal.server.scripting.ScriptingSandbox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
 * number of players in a short time. (The last time i did a show-fight i was
 * losing count because there where more than 15 players)
 *
 * @author hendrik
 */
public class BetManager extends ScriptImpl {
	protected Set<String> targets = new HashSet<String>();

	/**
	 * Stores information about a bet
	 */
	private class BetInfo {
		String playerName = null; // do not use Player object because player may reconnect during  the show
		String target = null;
		String itemName = null;
		int amount = 0;
	}

	private class BetAction extends SpeakerNPC.ChatAction {
		private ScriptingSandbox sandbox = null;

		public BetAction(ScriptingSandbox sandbox) {
			this.sandbox = sandbox;
		}

		public void fire(Player player, String text, SpeakerNPC engine) {
			BetInfo betInfo = new BetInfo();
			betInfo.playerName = player.getName();

			// parse the string
			StringTokenizer st = new StringTokenizer(text);
			boolean error = false;
			if (st.countTokens() == 5) {
				st.nextToken(); // bet
				String amountStr = st.nextToken();  // 5
				betInfo.itemName = st.nextToken(); // cheese
				st.nextToken(); // on
				betInfo.target = st.nextToken();

				try {
					betInfo.amount = Integer.parseInt(amountStr);
				} catch (NumberFormatException e) {
					error =true;
				}
			} else {
				error = true;
			}

			// wrong syntax
			if (error) {
				engine.say("Sorry " + player.getName() + ", i did not understand you.");
				return;
			}

			// check that item is a Consumeable Item
			Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(betInfo.itemName);
			if (! (item instanceof ConsumableItem)) {
				engine.say("Sorry " + player.getName() + ", i only accept food and drinks.");
				return;
			}

			// check target
			if (!targets.contains(betInfo.target)) {
				engine.say("Sorry " + player.getName() + ", i only accept bets on " + targets);
				return;
			}

			// drop item
			if (!player.drop(betInfo.itemName, betInfo.amount)) {
				engine.say("Sorry " + player.getName() + ", you don't have " + betInfo.amount + " " + betInfo.itemName);
				return;
			}

			// TODO: confirm bet
			// TODO: store items in list
			// TODO: put items on ground
			// TODO: mark items on ground with: playername "betted" ammount itemname "on" target.

		}
	}

	@Override
	public void load(Player admin, List<String> args, ScriptingSandbox sandbox) {

		// Do not load on server startup
		if (admin == null) {
			return;
		}

		// create npc
		ScriptingNPC npc = new ScriptingNPC("Bet Dialer which needs a name");
		npc.setClass("naughtyteen2npc");

		// place NPC next to admin
		sandbox.setZone(sandbox.getZone(admin));
		int x = admin.getX() + 1;
		int y = admin.getY();
		npc.set(x, y);
		sandbox.add(npc);

		// Create Dialog
		npc.behave("greet", "Hi, do you want to bet?");
		npc.behave("job", "I am the Bet Dialer");
		npc.behave("help", "Say \"bet 5 cheese on fire\" to get an additional 5 pieces of cheese if fire wins. If he loses you lose your 5 cheese.");
	}

	
}
