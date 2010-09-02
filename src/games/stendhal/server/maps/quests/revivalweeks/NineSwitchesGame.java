package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.game.NineSwitchesGameBoard;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * A Tic Tac Toe game for two players
 *
 * @author hendrik
 */
public class NineSwitchesGame {
	private StendhalRPZone zone;
	private NineSwitchesGameBoard board;
	private SpeakerNPC npc;

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
				add(ConversationStates.IDLE, 
						ConversationPhrases.GREETING_MESSAGES, 
						ConversationStates.IDLE,
						"Hi, welcome to our small game of nine switches. Your task is to let all arrows point to the right."
						+ "Easy? Well, there is a #catch.",
						null);
				add(ConversationStates.IDLE, 
						Arrays.asList("catch"), 
						ConversationStates.IDLE,
						"Each switch is linked to it neighbour and will toggle them as well. You have one minutes to solve the puzzle."
						+ "Do you want to #play?.",
						null);
				add(ConversationStates.IDLE, 
						ConversationPhrases.JOB_MESSAGES, 
						ConversationStates.IDLE,
						"I am the supervisor of this game.",
						null);
				add(ConversationStates.IDLE, 
						ConversationPhrases.GOODBYE_MESSAGES, 
						ConversationStates.IDLE,
						"It was nice to meet you.",
						null);
				add(ConversationStates.IDLE,
						Arrays.asList("play", "play?", "game", "yes"),
						ConversationStates.IDLE,
						"Good luck.",
						new PlayAction(board));
			}
		};
		// TODO: nice outfit
		npc.setEntityClass("paulnpc"); 
		npc.setPosition(88, 119);
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
