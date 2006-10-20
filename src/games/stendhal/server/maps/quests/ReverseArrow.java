package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.MathHelper;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Token;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.OnePlayerRoomDoor;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.rule.EntityManager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * A quest where the player has to invert an arrow build out of stones
 * by moving only up to 3 tokens.
 *
 * @author hendrik
 */
public class ReverseArrow extends AbstractQuest {

	private static final String QUEST_SLOT = "reverse_arrow";
	private static final int MAX_MOVES = 3;
	private static final int OFFSET_X = 5;
	private static final int OFFSET_Y = 5;

	
	private StendhalRPZone zone = null;
	protected SpeakerNPC npc = null;
	protected List<Token> tokens = null;
	private Portal exit = null;
	private OnePlayerRoomDoor door = null;

	/**
	 * Checks the result
	 */
	private class ReverseArrowCheck implements TurnListener {
		private Player player = null;

		/**
		 * create a new ReverseArrowCheck
		 *
		 * @param player player who tried to solve the quest
		 * @param npc the npc guarding the quest
		 */
		ReverseArrowCheck(Player player) {
			this.player = player;
		}

		/**
		 * invoked shortly after the player did his/her third move.
		 */
		public void onTurnReached(int currentTurn, String message) {
			if (false) {
				player.setQuest(QUEST_SLOT, "done");
				npc.say("Congratulations you solved the quizz");
				// TODO: give reward
			} else {
				player.setQuest(QUEST_SLOT, "failed");
				npc.say("I am sorry. This does not look like an arrow pointing upwards to me.");
			}
			// TODO: teleport player out
		}
		
	}

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	/**
	 * creates a token and adds it to the world
	 *
	 * @param x x-position
	 * @param y y-position
	 */
	private void addTokenToWorld(int x, int y) {
		EntityManager entityManager = StendhalRPWorld.get().getRuleManager().getEntityManager();
		Token token = (Token) entityManager.getItem("token");
		zone.assignRPObjectID(token);
		token.set(x, y);
		token.put("persistent", 1);
		zone.add(token);
		tokens.add(token);
	}

	/**
	 * adds the tokens to the game field
	 */
	private void step1AddTokens() {
		// 0 1 2 3 4
		//   5 6 7
		//     8
		tokens = new LinkedList<Token>();
		for (int i = 0; i < 5; i++) {
			addTokenToWorld(OFFSET_X + i, OFFSET_Y);
		}
		for (int i = 1; i < 4; i++) {
			addTokenToWorld(OFFSET_X + i, OFFSET_Y + 1);
		}
		addTokenToWorld(OFFSET_X + 3, OFFSET_Y + 2);
	}

	/**
	 * removes all tokens (called after the player messed them up)
	 */
	private void removeAllTokens() {
		if (tokens != null) {
			for (Token token : tokens) {
				if (token != null) {
					zone.remove(token.getID());
				}
			}
		}
		tokens = null;
	}

	private void step_1() {
		// TODO: create NPC
		// TODO: create zone with tokens
		// TODO: create door
	}

	@Override
	public void convertOnUpdate(Player player) {
		super.convertOnUpdate(player);
		IRPZone playerZone = StendhalRPWorld.get().getRPZone(player.getID());
		IRPZone onePlayerRoomZone = StendhalRPWorld.get().getRPZone(door.getDestinationZone());
		if (playerZone.equals(onePlayerRoomZone)) {
			exit.onUsed(player);
		}
	}

	public void onTokenMoved(Player player) {
		String questState = player.getQuest(QUEST_SLOT);
		int moveCount = MathHelper.parseInt_default(questState, MAX_MOVES);
		moveCount++;
		if (moveCount < 3) {
			npc.say("This was your " + Grammar.ordered(moveCount) + " move.");
		} else {
			npc.say("This was your " + Grammar.ordered(moveCount) + " and final move. Let me check your work.");
			TurnNotifier.get().notifyInTurns(6, new ReverseArrowCheck(player), null); // 2 seconds
		}
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
	}
}
