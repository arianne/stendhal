/***************************************************************************
 *                   Copyright (C) 2003-2022 - Arianne                     *
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

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;
import games.stendhal.common.parser.Sentence;
import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;


/**
 * Creates a sandboxed map for new players to progress through
 * a tutorial scenario. Player will be teleported to island on
 * login automatically if this quest has not been completed.
 *
 * TODO:
 * - create JUnit test
 * - finish NPC interaction step
 *   - teach about buying/selling
 *   - teach about questing
 *   - teach about producing
 * - create item usage tutorial step
 *   - teach about using healing items
 *   - teach about using scrolls
 *   - teach about using tools
 *   - teach about equipping items
 * - create combat tutorial step
 *   - teach about stats
 *   - teach about weapon rate
 *   - teach about targeting
 *   - teach about ranged attacks
 *   - teach about element susceptibilities
 *   - teach about status effects & status resistant items
 * - create tool usage tutorial step
 *   - teach about fishing
 *   - teach about mining
 *   - teach about harvesting
 * - create pets tutorial step
 * - create chat commands tutorial step
 * - create achievement for finishing tutorial
 * - create admin script to reset/restart tutorial
 * - rewared karma for completing tutorial
 * - check if player is new & make sure all equipment & items are removed
 *   - can detect if player is new by checking visited zones
 * - make sure new players are teleported directly to tutorial island &
 *   not to int_semos_guard_house or int_semos_town_hall
 * - give new players weapon & armor after tutorial is complete
 * - make sure any items given to player during tutorial have infostring
 *   and are removed from player & ground when tutorial is finished
 * - handle player dieing in tutorial zone
 * - make visible in travel log
 */
public class TutorialIsland extends AbstractQuest {

	private static final Logger logger = Logger.getLogger(TutorialIsland.class);

	private final String SLOT = "tutorial_island";

	private final String tutorBasename = "tutor";
	private final String tutorTitle = "Tutor";
	// NPCs created per player
	private Map<String, SpeakerNPC> activeTutors;

	// the quest states
	private static final String ST_RULES = "rules";
	private static final String ST_NPCS = "npcs";
	private static final String ST_CHAT = "chat";
	private static final String ST_ITEMS = "items";
	private static final String ST_COMBAT = "combat";
	private static final String ST_QUESTS = "quests";
	private static final String ST_PETS = "pets";
	private static final String ST_FINAL = "final";


	@Override
	public void addToWorld() {
		activeTutors = new HashMap<>();
	}

	@Override
	public List<String> getHistory(final Player player) {
		return new LinkedList<>();
	}

	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}

	@Override
	public String getSlotName() {
		return SLOT;
	}

	@Override
	public String getName() {
		return "TutorialIsland";
	}

	@Override
	public String getNPCName() {
		return tutorTitle;
	}

	public void startTutorialForPlayer(final Player player) {
		// TODO: check if player is level 0 & never visited 0_semos_village_w

		final String pname = player.getName();

		createNPC(pname);
		prepareSteps(pname);
		createIsland(player);
	}

	private void createNPC(final String pname) {
		final SpeakerNPC tutor = new SpeakerNPC(tutorBasename + "_" + pname);
		tutor.setTitle(tutorTitle);
		tutor.put("cloned", tutorBasename); // hide from website

		// set attributes
		tutor.setEntityClass("floattingladynpc");
		tutor.setIdleDirection(Direction.DOWN);
		tutor.setPosition(23, 8);
		tutor.put("flying", ""); // animate sprite when idle

		tutor.addGoodbye("Just say #hi when you are ready to continue.");

		activeTutors.put(pname, tutor);
	}

	private void createIsland(final Player player) {
		// FIXME: need to dismantle island on logout

		final String pname = player.getName();
		final SpeakerNPC tutor = activeTutors.get(pname);

		final StendhalRPWorld world = SingletonRepository.getRPWorld();

		final IRPZone templateZone = world.getRPZone(SLOT);
		if (templateZone == null) {
			logger.error("could not get zone: " + SLOT);
			return;
		}

		final StendhalRPZone zone = new StendhalRPZone(pname + "_" + SLOT,
			(StendhalRPZone) templateZone);
		zone.setEntryPoint(20, 14);
		zone.disallowIn();
		zone.disallowOut();

		final ZoneAttributes attr = new ZoneAttributes(zone);
		attr.setBaseName(SLOT);
		zone.setAttributes(attr);

		world.addRPZone(zone);

		zone.add(tutor);
		zone.placeObjectAtEntryPoint(player);

		final TurnNotifier tn = SingletonRepository.getTurnNotifier();

		tn.notifyInTurns(15, new TurnListener() {
			@Override
			public void onTurnReached(final int currentTurn) {
				final Engine en = tutor.getEngine();

				// don't interrupt if player already conversing
				if (en.getCurrentState() == ConversationStates.IDLE) {
					tutor.say("Hey! " + pname + ", come say #hi.");

					tn.notifyInTurns(10, new TurnListener() {
						@Override
						public void onTurnReached(final int currentTurn) {
							if (en.getCurrentState() == ConversationStates.IDLE) {
								tutor.say("I have something to tell you.");
							}
						}
					});
				}
			}
		});
	}

	public void dismantleIsland(final Player player) {
		final String pname = player.getName();
		if (activeTutors.containsKey(pname)) {
			activeTutors.remove(pname);
		}

		final String tname = tutorBasename + "_" + pname;
		final String zname = pname + "_" + SLOT;

		// we get the NPC from the main list in case player already completed
		// tutorial
		final SpeakerNPC tutor = npcs.get(tname);
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		final StendhalRPZone zone = world.getZone(zname);

		if (zone != null) {
			if (tutor != null) {
				zone.remove(tutor);
				world.remove(tutor.getID());
			}
			world.removeZone(zone);
		}

		if (npcs.get(tname) != null) {
			logger.warn("failed to remove NPC: " + tname);
		}
		if (world.getZone(zname) != null) {
			logger.warn("failed to remove zone: " + zname);
		}
	}

	private void onCompleted(final Player player) {
		SingletonRepository.getTurnNotifier().notifyInTurns(10, new TurnListener() {
			@Override
			public void onTurnReached(final int currentTurn) {
				final StendhalRPZone guardhouse = SingletonRepository.getRPWorld()
					.getZone("int_semos_guard_house");
				if (guardhouse == null) {
					logger.error("could not get guardhouse zone to teleport player");
					return;
				}

				guardhouse.placeObjectAtEntryPoint(player);

				if (player.getZone().getName().equals(player.getName() + "_" + SLOT)) {
					logger.error("failed to teleport player out of tutorial zone");
					return;
				}

				dismantleIsland(player);
			}
		});
	}

	private void prepareSteps(final String pname) {
		final SpeakerNPC tutor = activeTutors.get(pname);

		prepareOfferQuest(tutor);
		prepareRulesStep(tutor);
		prepareNPCsStep(tutor);
		prepareChatStep(tutor);
		prepareItemsStep(tutor);
		prepareCombatStep(tutor);
		prepareQuestsStep(tutor);
		preparePetsStep(tutor);
		prepareFinalStep(tutor);
	}

	/**
	 * Creates a chat action that has NPC say a phrase after a set
	 * number of turns has passed.
	 *
	 * @param delay
	 *     Number of turns to wait.
	 * @param msg
	 *     The phrase that the NPC will say.
	 * @return
	 *     New ChatAction instance.
	 */
	private ChatAction delayMessage(final int delay, final String msg) {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				SingletonRepository.getTurnNotifier().notifyInTurns(delay, new TurnListener() {
					@Override
					public void onTurnReached(final int currentTurn) {
						raiser.say(msg);
					}
				});
			}
		};
	}

	private void prepareOfferQuest(final SpeakerNPC tutor) {
		final ChatCondition questStarted = new QuestStartedCondition(SLOT);
		final ChatCondition questNotStarted = new QuestNotStartedCondition(SLOT);

		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			questNotStarted,
			ConversationStates.QUEST_OFFERED,
			"You look like a fast learner. Want to learn about this world?"
				+ " If so, just say #yes.",
			null);

		tutor.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			questNotStarted,
			ConversationStates.IDLE,
			"Oh really? Well, good luck with that. If you change your mind, just say #hi.",
			null);

		tutor.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			questNotStarted,
			ConversationStates.ATTENDING,
			"I thought so! Okay, the first thing you will need to learn is how to"
				+ " communicate. So far you are doing great! There are many helpful"
				+ " people in this #world who are always responsive to a friendly"
				+ " #hello. You will need to talk with them if you want to get"
				+ " anywhere.",
			new MultipleActions(
				new SetQuestAction(SLOT, ST_RULES),
				delayMessage(10, "If their response contains a highlighted word, like"
					+ " #this, that is an indication that they have some information"
					+ " on the subject. For example, ask me about #rules.")));

		tutor.add(ConversationStates.ATTENDING,
			ConversationPhrases.GREETING_MESSAGES,
			questStarted,
			ConversationStates.ATTENDING,
			"I'm already engaged in conversation with you. No need to greet me again.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			Arrays.asList("faiumoni", "world"),
			questStarted,
			ConversationStates.ATTENDING,
			"Faiumoni is the name of our world, the world that you will soon enter.",
			null);
	}

	private void prepareRulesStep(final SpeakerNPC tutor) {
		final ChatCondition onRulesStep = new QuestInStateCondition(SLOT, 0, ST_RULES);

		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			onRulesStep,
			ConversationStates.ATTENDING,
			"If you are ready for the next step, just tell me #next. Or I can go over"
				+ " the #rules again.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			Arrays.asList("rule", "rules"),
			new QuestStartedCondition(SLOT),
			ConversationStates.ATTENDING,
			"The rules of #Faiumoni are simple. First, always be polite in conversation."
				+ " Don't just walk away from someone without saying #bye. Secondly,"
				+ " don't cheat. We don't like it when we have to remove others from"
				+ " the community. It makes for a much smaller world with less people"
				+ " to talk to. But don't doubt for a second that we would rather keep"
				+ " it clean and fair.",
			new MultipleActions(
				delayMessage(10, "Never share your account password with another player,"
					+ " not even with a #'game master'. Your account is sure to get banned."),
				delayMessage(20, "For more information on our rules, you can read a more"
					+ " complete guide here: #https://stendhalgame.org/player-guide/rules.html"),
				delayMessage(30, "If you you are clear on the rules, tell me #next if you"
					+ " feel you are ready to move on.")));

		// TODO: change this to move on to NPCs step
		tutor.add(ConversationStates.ATTENDING,
			"next",
			onRulesStep,
			ConversationStates.ATTENDING,
			"Unfortunately, that is as far as this tutorial goes for now. So until I can come"
				+ " up with more stuff to teach, our session here will have to be #done.",
			new SetQuestAction(SLOT, 0, ST_FINAL));
	}

	private void prepareNPCsStep(final SpeakerNPC tutor) {
		final ChatCondition onNPCsStep = new QuestInStateCondition(SLOT, 0, ST_NPCS);
	}

	private void prepareChatStep(final SpeakerNPC tutor) {
		final ChatCondition onChatStep = new QuestInStateCondition(SLOT, 0, ST_CHAT);
	}

	private void prepareItemsStep(final SpeakerNPC tutor) {
		final ChatCondition onItemsStep = new QuestInStateCondition(SLOT, 0, ST_ITEMS);
	}

	private void prepareCombatStep(final SpeakerNPC tutor) {
		final ChatCondition onCombtaStep = new QuestInStateCondition(SLOT, 0, ST_COMBAT);
	}

	private void prepareQuestsStep(final SpeakerNPC tutor) {
		final ChatCondition onQuestsStep = new QuestInStateCondition(SLOT, 0, ST_QUESTS);
	}

	private void preparePetsStep(final SpeakerNPC tutor) {
		final ChatCondition onPetsStep = new QuestInStateCondition(SLOT, 0, ST_PETS);
	}

	private void prepareFinalStep(final SpeakerNPC tutor) {
		final ChatCondition onFinalStep = new QuestInStateCondition(SLOT, 0, ST_FINAL);
		final ChatCondition questDone = new QuestInStateCondition(SLOT, 0, "done");

		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			onFinalStep,
			ConversationStates.ATTENDING,
			"I think we are all #done. There is nothing more I can teach you now.",
			null);

		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			questDone,
			ConversationStates.QUESTION_1,
			"We are all done here. Just tell me if you want to #leave.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			ConversationPhrases.FINISH_MESSAGES,
			onFinalStep,
			ConversationStates.QUESTION_1,
			"Good work! You are now ready to enter the world of Faiumoni. Just let me"
				+ " know when you want to #leave, and I will send you on your new"
				+ " adventure.",
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					player.setQuest(SLOT, "done");
					player.setQuest(SLOT, 1, Long.toString(System.currentTimeMillis()));

					// TODO:
					// - create GameEvent
				}
			});

		tutor.add(ConversationStates.QUESTION_1,
			"leave",
			new OrCondition(
				onFinalStep,
				questDone),
			ConversationStates.IDLE,
			"Good luck in your future endeavors!",
			new ChatAction(){
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					// TODO: play a sound & post area message about NPC casting a spell
					//       to teleport player
					onCompleted(player);
				}
			});
	}
}
