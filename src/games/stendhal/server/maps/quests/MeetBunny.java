/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.behaviour.impl.ItemDroppingTeleporterBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Meet the Easter Bunny anywhere around the World.
 * <p>
 *
 * PARTICIPANTS:<ul><li> Easter Bunny</ul>
 *
 * STEPS: <ul><li> Find Bunny <li> Say hi <li> Get reward </ul>
 *
 * REWARD: <ul><li> a basket which can be opened to obtain a random good reward: food,
 * money, potions, items, etc...</ul>
 *
 * REPETITIONS: None
 */
public class MeetBunny extends AbstractQuest {
	// quest slot changed ready for 2015
	private static final String QUEST_SLOT = "meet_bunny_[year]";

	/** the Bunny NPC. */
	protected SpeakerNPC bunny;

	private ItemDroppingTeleporterBehaviour teleporterBehaviour;

	/** the name of the quest */
	public static final String QUEST_NAME = "MeetBunny";

	// The default is 30 seconds so make ours half this
	private static final int TIME_OUT = 15;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private SpeakerNPC createbunny() {
		bunny = new SpeakerNPC("Easter Bunny") {
			@Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				// Greet players who have a basket but go straight back to idle to give others a chance
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(new GreetingMatchesNameCondition(super.getName()),
								new QuestCompletedCondition(QUEST_SLOT)),
						ConversationStates.IDLE,
						"Hi again! Don't eat too much this Easter!", null);

				final List<ChatAction> reward = new LinkedList<ChatAction>();
				reward.add(new EquipItemAction("basket"));
				reward.add(new SetQuestAction(QUEST_SLOT, "done"));

				// Give unmet players a basket
				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
							new QuestNotCompletedCondition(QUEST_SLOT)),
					ConversationStates.IDLE,
					"Happy Easter! I have an easter basket for you. Bye!",
					new MultipleActions(reward));
			}
		};

		bunny.setEntityClass("easterbunnynpc");
		bunny.initHP(100);
		// times out twice as fast as normal NPCs
		bunny.setPlayerChatTimeout(TIME_OUT);
		bunny.setDescription("You see a friendly bunny carrying brightly coloured Easter baskets.");
		// start in int_admin_playground

		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("int_admin_playground");
		bunny.setPosition(17, 13);
		zone.add(bunny);

		return bunny;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Meet Easter Bunny",
				"At Easter one may see the Easter Bunny hopping around the world of Faiumoni. He is really fast even though he carries some heavy surprises with him...",
				false);

		if (System.getProperty("stendhal.easterbunny") != null) {
		    createbunny();
		    teleporterBehaviour = new ItemDroppingTeleporterBehaviour(bunny, null, "0", "*hop* *hop* *hop* Happy Easter!", true, "small easter egg");
		}
	}

	/**
	 * removes a quest from the world.
	 *
	 * @return true, if the quest could be removed; false otherwise.
	 */
	@Override
	public boolean removeFromWorld() {
		removeNPC("Easter Bunny");
		// remove the turn notifiers left from the TeleporterBehaviour
		SingletonRepository.getTurnNotifier().dontNotify(teleporterBehaviour);
		return true;
	}

	/**
	 * removes an NPC from the world and NPC list
	 *
	 * @param name name of NPC
	 */
	private void removeNPC(String name) {
		SpeakerNPC npc = NPCList.get().get(name);
		if (npc == null) {
			return;
		}
		npc.getZone().remove(npc);
	}

	@Override
	public String getName() {
		return QUEST_NAME;
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
		return "Easter Bunny";
	}

}
