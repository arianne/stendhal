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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.SimilarExprMatcher;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Quest to solve a riddle to leave hell
 * <p>
 *
 * PARTICIPANTS: <ul><li>Grim Reaper</ul>
 *
 *
 * STEPS: <ul><li> Reaper sets you a riddle
 * <li> Player tries to answer
 * <li> Reaper compares answer to configuration file on server
 * </ul>
 *
 *
 * REWARD: <ul><li>100 XP - Leaving hell</ul>
 *
 * REPETITIONS: <ul><li>Any time you wish to leave hell, but if you ask for a riddle when you didn't solve the previous
 *  one yet, nor asked the other NPC to let you leave with karma loss, then you have to solve same one still</ul>
 *
 * @author kymara
 */

public class SolveRiddles extends AbstractQuest {
	private static final String QUEST_SLOT = "solve_riddles";
	private static final int xpreward = 100;
	private Riddles riddles;

	private static class Riddles {
		private static Logger logger = Logger.getLogger(Riddles.class);

		private static final String RIDDLES_XML = "/data/conf/riddles.xml";
		private static final String RIDDLES_EXAMPLE_XML = "/data/conf/riddles-example.xml";

		Map<String, Collection<String>> riddleMap;

		public Riddles() {
			riddleMap = new HashMap<String, Collection<String>>();
			new RiddleLoader().load(riddleMap);
		}

		/**
		 * Check if an answer matches the riddle.
		 *
		 * @param riddle The riddle to be answered
		 * @param sentence The answer given by the player
		 * @return <code>true</code> iff the answer is correct
		 */
		public boolean matches(String riddle, Sentence sentence) {
			final Sentence answer = sentence.parseAsMatchingSource();

			// if the riddle is unknown, teleport the player out.
			// this can happen if the riddle was removed from the xml file
			// (or if the character is copied to a testserver with a different file)
			if (riddleMap.get(riddle) == null) {
				logger.warn("Accepting any answer for unknown riddle: " + riddle);
				return true;
			}
			for (String correct : riddleMap.get(riddle)) {
				final Sentence expected = ConversationParser.parse(correct, new SimilarExprMatcher());
				if (answer.matchesFull(expected)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Get a random riddle.
		 *
		 * @return A random ridde
		 */
		String getRiddle() {
			return Rand.rand(riddleMap.keySet());
		}

		/**
		 * Loader for the riddles xml format.
		 */
		private static class RiddleLoader extends DefaultHandler {
			Map<String, Collection<String>> riddles;
			String currentKey;
			String currentAnswer;

			public void load(Map<String, Collection<String>> riddles) {
				this.riddles = riddles;

				InputStream in = getClass().getResourceAsStream(RIDDLES_XML);

				if (in == null) {
					logger.warn(RIDDLES_XML + " not found. Using " + RIDDLES_EXAMPLE_XML);
					in = getClass().getResourceAsStream(RIDDLES_EXAMPLE_XML);
					if (in == null) {
						logger.error("Failed to load " + RIDDLES_EXAMPLE_XML);
						return;
					}
				}

				SAXParser parser;

				// Use the default (non-validating) parser
				final SAXParserFactory factory = SAXParserFactory.newInstance();
				try {
					parser = factory.newSAXParser();
					parser.parse(in, this);
				} catch (final Exception e) {
					logger.error(e);
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}

			/**
			 * Add an answer to a riddle. Add the riddle too if it did not exist before.
			 * @param riddle The riddle to add an answer to
			 * @param answer Asnwer to the riddle
			 */
			private void addAnswer(String riddle, String answer) {
				Collection<String> answers = riddles.get(riddle);
				if (answers == null) {
					answers = new LinkedList<String>();
					riddles.put(riddle, answers);
				}
				answers.add(answer);
			}

			@Override
			public void startElement(final String uri, final String localName, final String qName, final Attributes attrs) {
				if (qName.equals("entry")) {
					final String key = attrs.getValue("key");
					if (key == null) {
						logger.warn("An entry without a key");
					} else {
						currentKey = key;
					}
				} else if (!(qName.equals("riddles") || qName.equals("comment"))) {
					currentKey = null;
					logger.warn("Unknown XML element: " + qName);
				}
			}

			@Override
			public void endElement(final String uri, final String lName, final String qName) {
				if (qName.equals("entry")) {
					if (currentKey != null && currentAnswer != null) {
						addAnswer(currentKey, currentAnswer);
					} else {
						logger.error("Error reading riddles, Key=" + currentKey + " " + " Answer=" + currentAnswer);
					}
				} else {
					currentKey = null;
					currentAnswer = null;
				}
			}

			@Override
			public void characters(char[] ch, int start, int length) {
				if (currentKey != null) {
					currentAnswer = new String(ch, start, length);
				} else {
					currentAnswer = null;
				}
			}
		}
	}

	public SolveRiddles() {
		riddles = new Riddles();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void setRiddle(String npcName) {
		final SpeakerNPC reaper = npcs.get(npcName);

		// player has no unsolved riddle active
		reaper.add(ConversationStates.ATTENDING,
				"leave",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						// randomly choose from available riddles
						final String riddle = riddles.getRiddle();
						npc.say("Try this riddle: " + riddle);
						player.setQuest(QUEST_SLOT, riddle);
					}
				});

		// player already was set a riddle he couldn't solve
		reaper.add(ConversationStates.ATTENDING,
				"leave",
				new QuestStartedCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String riddle = player.getQuest(QUEST_SLOT);
					npc.say("You must solve the riddle which I previously set you: " + riddle);
				}
		});

		reaper.add(ConversationStates.QUESTION_1, "", null,
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String riddle = player.getQuest(QUEST_SLOT);
					final String triggerText = sentence.getTriggerExpression().getNormalized();

					if (riddles.matches(riddle, sentence)) {
						final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("int_afterlife");
						player.teleport(zone, 31, 23, Direction.UP, player);
						// clear quest slot so riddle is chosen randomly for player next time
						player.removeQuest(QUEST_SLOT);
						int oldXp = player.getXP();
						player.addXP(xpreward);
						int xpDiff = player.getXP() - oldXp;
						StringBuilder msg = new StringBuilder("You solved the riddle correctly");
						if (xpDiff > 0) {
							msg.append(" and earned " + xpreward + " XP");
						}
						msg.append(".");
						player.sendPrivateText(NotificationType.POSITIVE, msg.toString());
						player.notifyWorldAboutChanges();
						npc.setCurrentState(ConversationStates.IDLE);
					} else if (ConversationPhrases.GOODBYE_MESSAGES.contains(triggerText)) {
						npc.say("The old order of things has passed away ... ");
						npc.setCurrentState(ConversationStates.IDLE);
					} else if (triggerText.equals("leave") || triggerText.equals("riddle")) {
						// player didn't answer riddle but tried saying riddle/leave again (to get another maybe?)
						npc.say("You can ask my mirror to let you leave, or you must solve the riddle which I previously set you: " + riddle);
					} else {
						npc.say("Incorrect! Try again, or ask my mirror to let you leave.");
						player.subXP(10 * xpreward);
					}
				}
			});
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Solve Riddles",
				"The Grim Reapers pose tricky riddles for those who want to leave the hottest place in Faiumoni.",
				false);
		setRiddle("Grim Reaper");
		// Reaper clone in the pit
		setRiddle("Grim Reaper clone");
	}

	@Override
	public String getName() {
		return "SolveRiddles";
	}

	// there is a minimum level requirement to get into hell - this quest is in hell
	@Override
	public int getMinLevel() {
		return 200;
	}

	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}

	@Override
	public List<String> getHistory(final Player player) {
		return new ArrayList<String>();
	}

	@Override
	public String getNPCName() {
		return "Grim Reaper";
	}

	@Override
	public String getRegion() {
		return Region.HELL;
	}
}
