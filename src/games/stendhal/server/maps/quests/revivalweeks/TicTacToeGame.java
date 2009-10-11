package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.game.TicTacToeBoard;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Add a sign saying the tower is closed
 *
 * @author hendrik
 */
public class TicTacToeGame {
	private StendhalRPZone zone;
	private TicTacToeBoard board;

	private void addBoard() {
		board = new TicTacToeBoard();
		board.setPosition(105, 120);
		zone.add(board);
		board.addToWorld();
	}

	private void addNPC() {
		// TODO: add name
		SpeakerNPC npc = new SpeakerNPC("Paul Sheriff") {
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
			}
		};
		// TODO change outfit
		npc.setEntityClass("oldwizardnpc"); 
		npc.setPosition(106, 118);
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

	public void addToWorld() {
		zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");

		addBoard();
		addNPC();
	}

}
