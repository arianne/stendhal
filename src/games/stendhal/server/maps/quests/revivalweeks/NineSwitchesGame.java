package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.game.NineSwitchesGameBoard;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * A Game about Nine switches game for one player
 *
 * @author hendrik
 */
public class NineSwitchesGame {
	private StendhalRPZone zone;
	private NineSwitchesGameBoard board;
	private SpeakerNPC npc;

	// 1 min at 300 ms/turn
	private static final int CHAT_TIMEOUT = 200;
	
	private void addBoard() {
		board = new NineSwitchesGameBoard(zone, 87, 120);
	}

	private void addNPC() {
		npc = new SpeakerNPC("Maltos") {
			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, welcome to our small game of nine switches. Your task is to make all arrows point to the right."
						+ "Easy? Well, there is a #catch.");
				addReply("catch", 
						"Each switch is linked to its neighbour and will change them as well. You have one minute to solve the puzzle."
						+ "Do you want to #play?.");
				addJob("I am the supervisor of this game.");
				addGoodbye("It was nice to meet you.");
				add(ConversationStates.ATTENDING,
						Arrays.asList("play", "play?", "game", "yes"),
						ConversationStates.ATTENDING,
						"Good luck.",
						new PlayAction(board));
			}
		};
		npc.setEntityClass("gamesupervisornpc"); 
		npc.setPlayerChatTimeout(CHAT_TIMEOUT);
		npc.setPosition(88, 119);
		npc.setDescription("You see Maltos. Aren't you jealous of his awesome hair?");
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

	/**
	 * handles a play chat action
	 */
	private static class PlayAction implements ChatAction {
		private NineSwitchesGameBoard board;

		/**
		 * creates a new PlayAction.
		 *
		 * @param board
		 */
		public PlayAction(NineSwitchesGameBoard board) {
			this.board = board;
		}

		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			if (board.getPlayerName() != null) {
				npc.say("Sorry, " + player.getName() + " there is already a game in progress. Please wait a little.");
				return;
			}
			board.setPlayerName(player.getName());
		}
	}

	public void addToWorld() {
		zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");

		addBoard();
		addNPC();
		board.setNPC(npc);
	}

}
