/* $Id$ */
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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.maze.MazeGenerator;
import games.stendhal.server.maps.quests.maze.MazeSign;

public class Maze extends AbstractQuest {
	/** Minimum time between repeats. */
	private static final int COOLING_TIME = MathHelper.MINUTES_IN_ONE_HOUR * 24;
	private MazeSign sign;
	private MazeGenerator maze = null;

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Maze",
				"Haizen's maze is a great challenge for path finders.",
				false);
		addMazeSign();
		setupConversation();
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(getSlotName())) {
				return res;
			}
			res.add("Haizen created a magical maze for me to solve.");

			if (player.getZone().getName().endsWith("_maze")) {
				res.add("I am currently trapped in the maze.");
			} else {
				if (!isCompleted(player)) {
					res.add("I couldn't solve the last maze.");
				} else {
					res.add("I solved the maze!");
				}
				if (isRepeatable(player)) {
					res.add("I could have another try to solve a maze now.");
				} else {
					res.add("Haizen won't make me a new maze yet.");
				}
			}
			final int repetitions = player.getNumberOfRepetitions(getSlotName(), 2);
			if (repetitions > 1) {
				res.add("So far I've solved the maze " + repetitions + " times already!");
			}

			return res;
	}

	@Override
	public String getName() {
		return "Maze";
	}

	@Override
	public String getSlotName() {
		return "maze";
	}

	@Override
	public boolean isRepeatable(Player player) {
		return new TimePassedCondition(getSlotName(), 1, COOLING_TIME).fire(player, null, null);
	}

	private SpeakerNPC getNPC() {
		return npcs.get("Haizen");
	}

	private void addMazeSign() {
		setSign(new MazeSign());
		getSign().setPosition(10, 7);
		getNPC().getZone().add(getSign());
	}

	private void setupConversation() {
		SpeakerNPC npc = getNPC();

		npc.addQuest("I can send you to a #maze you need to find your way out. I keep the a list of the fast and frequent maze solvers in that blue book on the table.");

		npc.add(ConversationStates.ATTENDING,
				"maze",
				new TimePassedCondition(getSlotName(), 1, COOLING_TIME),
				ConversationStates.QUEST_OFFERED,
				"There will be a portal out in the opposite corner of the maze. I'll also add scrolls to the two other corners you can try to get if you are fast enough. Do you want to try?",
				null);

		npc.add(ConversationStates.ATTENDING,
				"maze",
				new NotCondition(new TimePassedCondition(getSlotName(), 1, COOLING_TIME)),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(getSlotName(), 1,
						COOLING_TIME, "I can send you to the maze only once in a day. You can go there again in"));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new SendToMazeChatAction());

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"OK. You look like you'd only get lost anyway.",
				null);
	}

	private void setSign(MazeSign sign) {
		this.sign = sign;
	}

	public MazeSign getSign() {
		return sign;
	}

	private class SendToMazeChatAction implements ChatAction {
		public SendToMazeChatAction() {
			// empty constructor to prevent warning
		}

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
			maze = new MazeGenerator(player.getName() + "_maze", 128, 128);
			maze.setReturnLocation(player.getZone().getName(), player.getX(), player.getY());
			maze.setSign(getSign());
			StendhalRPZone zone = maze.getZone();
			SingletonRepository.getRPWorld().addRPZone(zone);
			new SetQuestAction(getSlotName(), 0, "start").fire(player, sentence, raiser);
			new SetQuestToTimeStampAction(getSlotName(), 1).fire(player, sentence, raiser);
			maze.startTiming();
			player.teleport(zone, maze.getStartPosition().x, maze.getStartPosition().y, Direction.DOWN, player);
		}
	}

	/**
	 * Access the portal from MazeTest.
	 *
	 * @return return portal from the maze
	 */
	protected Portal getPortal() {
		return maze.getPortal();
	}

	@Override
	public String getNPCName() {
		return "Haizen";
	}

	@Override
	public String getRegion() {
		return Region.ADOS_SURROUNDS;
	}
}
