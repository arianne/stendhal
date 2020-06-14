/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.dbcommand.WriteHallOfFamePointsCommand;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.mapstuff.game.SokobanBoard;
import games.stendhal.server.entity.mapstuff.game.SokobanListener;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.LoadSignFromHallOfFameAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.AvailabilityCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestSmallerThanCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;
import marauroa.server.db.command.DBCommandPriority;
import marauroa.server.db.command.DBCommandQueue;

/**
 * A Sokoban game.
 *
 * @author hendrik
 */
public class SokobanGame implements LoadableContent, SokobanListener {
	private static final String QUEST_SLOT = "sokoban_20[year]";
	private static final String FAME_TYPE = "S";

	/** start, done */
	private static final int QUEST_IDX_STATUS = 0;
	/** level */
	private static final int QUEST_IDX_LAST_SUCCESSFUL_LEVEL = 1;
	/** sum of times of all past levels in seconds */
	private static final int QUEST_IDX_SUM_OF_TIMES_FOR_PAST_LEVELS = 2;
	/** start time of current level */
	private static final int QUEST_IDX_START_TIME_OF_CURRENT_LEVEL = 3;

	private StendhalRPZone zone = null;
	private SokobanBoard board;
	private SpeakerNPC npc;
	private LoadSignFromHallOfFameAction loadSignFromHallOfFame;

	/**
	 * creates TicTacToeBoard and adds it to the world.
	 */
	private void addBoard() {
		board = new SokobanBoard(this);
		board.setPosition(26, 107);
		zone.add(board);
		board.loadLevel(0);
	}

	private void addSign() {
		Sign sign = new Sign();
		sign.setPosition(50, 119);
		zone.add(sign);
		loadSignFromHallOfFame = new LoadSignFromHallOfFameAction(null, "Best pushers:\n", FAME_TYPE, 2000, false);
		loadSignFromHallOfFame.setSign(sign);
		loadSignFromHallOfFame.fire(null, null, null);

	}

	/**
	 * adds the NPC which moderates the game to the world.
	 */
	private void addNPC() {
		npc = new SpeakerNPC("Hiro") {
			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello, let's #play a game.");
				addJob("I am the manager of this game field.");
				addHelp("Push the pumpins to their target. Say #leave, if you got stuck.");
				addQuest("Let's #play a game.");

				add(ConversationStates.ATTENDING,
					Arrays.asList("play"),
					new NotCondition(new AvailabilityCondition(board)),
					ConversationStates.ATTENDING,
					"Please wait a little until the current game is completed.",
					null);

				add(ConversationStates.ATTENDING,
						Arrays.asList("play"),
						new AndCondition(
							new QuestInStateCondition(
								QUEST_SLOT, QUEST_IDX_LAST_SUCCESSFUL_LEVEL, Integer.toString(board.getLevelCount())),
							new AvailabilityCondition(board)),
						ConversationStates.ATTENDING,
						"Wow! You finished all levels. I have run out of ideas.",
						null);

				List<ChatAction> playActions = new LinkedList<ChatAction>();
				playActions.add(new SetQuestAction(QUEST_SLOT, QUEST_IDX_STATUS, "start"));
				playActions.add(new SetQuestToTimeStampAction(QUEST_SLOT, QUEST_IDX_START_TIME_OF_CURRENT_LEVEL));
				playActions.add(new PlayAction(board));

				add(ConversationStates.ATTENDING,
					Arrays.asList("play"),
					new AndCondition(
						new QuestSmallerThanCondition(
							QUEST_SLOT, QUEST_IDX_LAST_SUCCESSFUL_LEVEL, board.getLevelCount(), true),
						new AvailabilityCondition(board)),
					ConversationStates.IDLE,
					"Good luck. If you get stuck and want to retry, just say #leave.",
					new MultipleActions(playActions));
			}
		};
		npc.setEntityClass("sokobannpc");
		npc.setPosition(48, 118);
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

		addSign();
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
		StendhalRPAction.placeat(npc.getZone(), player, npc.getX() - 2, npc.getY() + 1);
		player.setDirection(Direction.RIGHT);

		long startTime = MathHelper.parseLongDefault(player.getQuest(QUEST_SLOT, QUEST_IDX_START_TIME_OF_CURRENT_LEVEL), 0);
		int totalTime = MathHelper.parseIntDefault(player.getQuest(QUEST_SLOT, QUEST_IDX_SUM_OF_TIMES_FOR_PAST_LEVELS), 0);
		int timeDiff = (int) ((System.currentTimeMillis() - startTime) / 1000);
		totalTime = totalTime + timeDiff;
		player.setQuest(QUEST_SLOT, "done;" + level + ";" + totalTime + ";0");

		npc.say("Congratulations " + playerName + ", you completed the "
				+ Grammar.ordered(level) + " level in "
				+ TimeUtil.approxTimeUntil(timeDiff));

		int points = level * 1000000 - totalTime;
		DBCommandQueue.get().enqueue(new WriteHallOfFamePointsCommand(player.getName(), FAME_TYPE, points, false), DBCommandPriority.LOW);

		loadSignFromHallOfFame.fire(null, null, null);
	}

	@Override
	public void onTimeout(String playerName, int level) {
		Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		if (player == null) {
			return;
		}
		StendhalRPAction.placeat(npc.getZone(), player, npc.getX() - 2, npc.getY() + 1);
		player.setDirection(Direction.RIGHT);

		npc.say("I am sorry " + playerName + ", you have been too slow.");
	}


	@Override
	public void onLeave(String playerName, int level) {
		Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		if (player == null) {
			return;
		}
		StendhalRPAction.placeat(npc.getZone(), player, npc.getX() - 2, npc.getY() + 1);
		player.setDirection(Direction.RIGHT);
	}
}
