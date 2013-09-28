/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.game.SokobanBoard;
import games.stendhal.server.entity.mapstuff.game.SokobanListener;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.QuestSmallerThanCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A Sokoban game.
 *
 * @author hendrik
 */
public class SokobanGame implements LoadableContent, SokobanListener {
	// 0: start, done
	// 1: level
	// 2: sum of times for past levels in seconds
	// 3: start time of current level
	private static final String QUEST_SLOT = "sokoban";
	private static final int QUEST_IDX_STATUS = 0;
	private static final int QUEST_IDX_LAST_SUCCESSFUL_LEVEL = 1;
	private static final int QUEST_IDX_SUM_OF_TIMES_FOR_PAST_LEVELS = 2;
	private static final int QUEST_IDX_START_TIME_OF_CURRENT_LEVEL = 3;

	private StendhalRPZone zone = null;
	private SokobanBoard board;
	private SpeakerNPC npc;

	/**
	 * creates TicTacToeBoard and adds it to the world.
	 */
	private void addBoard() {
		board = new SokobanBoard();
		board.setPosition(26, 107);
		zone.add(board);
	}

	/**
	 * adds the NPC which moderates the game to the world.
	 */
	private void addNPC() {
		npc = new SpeakerNPC("TODO") {
			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello, let's #play a game.");

				List<ChatAction> playActions = new LinkedList<ChatAction>();
				playActions.add(new SetQuestAction(QUEST_SLOT, QUEST_IDX_STATUS, "start"));
				playActions.add(new SetQuestToTimeStampAction(QUEST_SLOT, QUEST_IDX_START_TIME_OF_CURRENT_LEVEL));
				playActions.add(new PlayAction(board));

				add(ConversationStates.ATTENDING,
						Arrays.asList("play"),
						new QuestSmallerThanCondition(
							QUEST_SLOT, QUEST_IDX_LAST_SUCCESSFUL_LEVEL, board.getLevelCount(), true),
						ConversationStates.IDLE,
						"Good luck.",
						new MultipleActions(playActions));
			}
		};
		npc.setEntityClass("paulnpc");
		npc.setPosition(46, 125);
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

	private static class PlayAction implements ChatAction {
		private final SokobanBoard board;

		public PlayAction(SokobanBoard board) {
			this.board = board;
		}

		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			board.setPlayer(player);
			int level = MathHelper.parseIntDefault(player.getQuest(QUEST_SLOT, QUEST_IDX_LAST_SUCCESSFUL_LEVEL), 0);
			board.loadLevel(level + 1);
		}
	}

	@Override
	public void addToWorld() {
		zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");

		addBoard();
		addNPC();
	}

	/**
	 * try to remove the content from the world-
	 *
	 * @return <code>true</code>
	 */
	@Override
	public boolean removeFromWorld() {
		zone.remove(npc);
		board.clear();
		zone.remove(board);
		return true;
	}

	@Override
	public void onSuccess(String playerName, int level) {
		Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		if (player == null) {
			return;
		}
		player.setPosition(npc.getX() - 1, npc.getY() + 1);
		player.setDirection(Direction.RIGHT);

		long startTime = MathHelper.parseLongDefault(player.getQuest(QUEST_SLOT, QUEST_IDX_START_TIME_OF_CURRENT_LEVEL), 0);
		int totalTime = MathHelper.parseIntDefault(player.getQuest(QUEST_SLOT, QUEST_IDX_SUM_OF_TIMES_FOR_PAST_LEVELS), 0);
		int timeDiff = (int) ((System.currentTimeMillis() - startTime) / 1000);
		totalTime = totalTime + timeDiff;
		player.setQuest(QUEST_SLOT, "done;" + level + ";" + totalTime + ";0");

		npc.say("Congratulations " + playerName + ", you completed the "
				+ Grammar.ordered(level) + " level in "
				+ TimeUtil.approxTimeUntil(timeDiff));
	}

	@Override
	public void onTimeout(String playerName, int level) {
		Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		if (player == null) {
			return;
		}
		player.setPosition(npc.getX() - 1, npc.getY() + 1);
		player.setDirection(Direction.RIGHT);

		npc.say("I am sorry " + playerName + ", you have been too slow.");
	}
}

/*
TODO-List
- "exit" chat command
- only start a new game, if there is no player already playing
- prevent login/teleport into gameboard
- handle highest level
- sign with hall of fame (highest level, quickest time)
- reward?

- nice name for npc
- nice outfit for npc
- nicer tiles for the game

- larger collisions entities
- minimap highlight
*/
