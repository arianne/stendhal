package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.game.TicTacToeBoard;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * Add a sign saying the tower is closed
 *
 * @author hendrik
 */
public class TicTacToeGame {
	private StendhalRPZone zone;
	private TicTacToeBoard board;
	private SpeakerNPC npc;

	private void addBoard() {
		board = new TicTacToeBoard();
		board.setPosition(105, 119);
		zone.add(board);
		board.addToWorld();
	}

	private void addNPC() {
		npc = new SpeakerNPC("Paul Sheriff") {
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
						"Hi, welcome to our small game of Tic Tac Toe. Your task is to fill a row "
						+ "(vertical, horizontal, diagonal) with the same type of tokens. "
						+ "You need an opponent to #play against.",
						null);

				add(ConversationStates.IDLE, 
						ConversationPhrases.HELP_MESSAGES, 
						ConversationStates.IDLE,
						"You have to stand next to a token in order to move it.",
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
						Arrays.asList("play", "game", "yes"),
						ConversationStates.IDLE,
						"",
						new PlayAction(board));
			}
		};
		npc.setEntityClass("paulnpc"); 
		npc.setPosition(106, 117);
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

	/**
	 * handles a play chat action
	 */
	private static class PlayAction implements ChatAction {
		private TicTacToeBoard board;

		/**
		 * creates a new PlayAction.
		 *
		 * @param board
		 */
		public PlayAction(TicTacToeBoard board) {
			this.board = board;
		}

		public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
			if (board.isGameActive()) {
				npc.say("Sorry, " + player.getName() + " there is already a game in progress. Please wait a little.");
				return;
			}

			if (board.getPlayers().isEmpty()) {
				// TODO implement timeout for waiting for second player
				npc.say("Okay, " + player.getName() + " you are registered for the next game. Does anyone want to #play against " + player.getName() + "?"); 
				board.getPlayers().add(player.getName());
			} else {
				if (board.getPlayers().get(0).equals(player.getName())) {
					npc.say("Okay " + player.getName() + ", you are registered for the next game. Does anyone want to #play against " + player.getName() + "?");
					return;
				}

				npc.say(board.getPlayers().get(0) + ", you are playing the blue X. " + player.getName() + ", you are playing the red O. May the best man win");
				board.startGame();
				board.getPlayers().add(player.getName());
			}
		}
	}

	public void addToWorld() {
		zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");

		addBoard();
		addNPC();
		board.setNPC(npc);
	}

}
