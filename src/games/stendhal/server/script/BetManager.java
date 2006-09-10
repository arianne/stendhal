package games.stendhal.server.script;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.scripting.ScriptImpl;
import games.stendhal.server.scripting.ScriptingNPC;
import games.stendhal.server.scripting.ScriptingSandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
	protected State state = State.IDLE;
	protected List<String> targets = new ArrayList<String>();
	protected List<BetInfo> betInfos = new LinkedList<BetInfo>();
	protected ScriptingNPC npc = null;

	/**
	 * Stores information about a bet
	 */
	private class BetInfo {
		String playerName = null; // do not use Player object because player may reconnect during the show
		String target = null;
		String itemName = null;
		int amount = 0;

		public String betToString() {
			StringBuilder sb = new StringBuilder();
			sb.append(amount);
			sb.append(" ");
			sb.append(itemName);
			sb.append(" on ");
			sb.append(target);
			return sb.toString();
		}
	}

	/**
	 * current state 
	 */
	private enum State {
		/** i now nothing */
		IDLE,
		/** i accept bets */
		ACCEPTING_BETS,
		/** bets are not accepted anymore; enjoy the show */
		ACTION,
		/** now we have a look at the result */
		PAYING_BETS
	}

	/**
	 * Do we accept bets at the moment?
	 */
	private class BetCondition extends SpeakerNPC.ChatCondition {
		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return state == State.ACCEPTING_BETS;
		}
	}

	/**
	 * handles a bet.
	 */
	private class BetAction extends SpeakerNPC.ChatAction {
		@Override
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

			// store bet in list and confirm it
			betInfos.add(betInfo);
			engine.say(player.getName() + " your bet " + betInfo.betToString() + " was accepted");


			// TODO: put items on ground
			// TODO: mark items on ground with: playername "betted" amount itemname "on" target.

		}
	}

	@Override
	public void load(Player admin, List<String> args, ScriptingSandbox sandbox) {

		// Do not load on server startup
		if (admin == null) {
			return;
		}

		// create npc
		npc = new ScriptingNPC("Bet Dialer which needs a name");
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
		npc.behave("help", "Say \"bet 5 cheese on fire\" to get an additional 5 pieces of cheese if fire wins. If he loses, you will lose your 5 cheese.");
		npc.add(ConversationStates.ANY, "bet", new BetCondition(), ConversationStates.ANY, null, new BetAction());

		// TODO: remove warning
		admin.sendPrivateText("BetManager is not fully coded yet");
	}

	@Override
	public void execute(Player admin, List<String> args) {

		// Help
		List<String> commands = Arrays.asList("accept", "action", "winner");
		if ((args.size() == 0) || (!commands.contains(args.get(0)))) {
			admin.sendPrivateText("Syntax: /script BetManager.class accept #fire #water\n"
					+ "/script BetManager.class action\n"
					+ "/script BetManager.class winner #fire");
			return;
		}

		int idx = commands.indexOf(args.get(0));
		switch (idx) {
			case 0: // accept #fire #water 
			{
				if (state != State.IDLE) {
					admin.sendPrivateText("accept command is only valid in state IDLE. But i am in " + state + " now.");
					return;
				}
				for (int i = 1; i < args.size(); i++) {
					targets.add(args.get(i));
				}
				npc.say("Hi, I am accepting bets on " + targets + ". If you want to bet simply say: \"bet 5 cheese on " + targets.get(0) + "\" to get an additional 5 pieces of cheese if " + targets.get(0) + " wins. If he loses, you will lose your 5 cheese.");
				state = State.ACCEPTING_BETS;
				break;
			}

			case 1: // action 
			{
				if (state != State.ACCEPTING_BETS) {
					admin.sendPrivateText("action command is only valid in state ACCEPTING_BETS. But i am in " + state + " now.");
					return;
				}
				state = State.ACTION;
				break;
			}

			case 2: // winner #fire
				if (state != State.ACTION) {
					admin.sendPrivateText("winner command is only valid in state ACTION. But i am in " + state + " now.");
					return;
				}
				// TODO: winner in State.ACTION -> State.PAYING_BETS
				state = State.PAYING_BETS;
				break;
		}

	}

	
}
