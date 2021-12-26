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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.behaviour.impl.TeleporterBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NakedCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerIsAGoodBoyCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Meet Santa anywhere around the World.
 *<p>
 * PARTICIPANTS: <ul><li> Santa Claus</ul>
 *
 * STEPS: <ul><li> Find Santa <li>Say hi <li> Get reward <li> Get hat</ul>
 *
 * REWARD: <ul><li> a stocking which can be opened to obtain a random good reward: food,
 * money, potions, items, etc... </ul>
 *
 * REPETITIONS:None
 */
public class MeetSanta extends AbstractQuest implements LoginListener {

	// quest slot changed ready for 2015
	private static final String QUEST_SLOT = "meet_santa_[seasonyear]";

	public static final String QUEST_NAME = "MeetSanta";

	/** the Santa NPC. */
	protected SpeakerNPC santa;

	private TeleporterBehaviour teleporterBehaviour;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private SpeakerNPC createSanta() {
		santa = new SpeakerNPC("Santa") {
			@Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
							new QuestCompletedCondition(QUEST_SLOT),
                                                        new PlayerIsAGoodBoyCondition(),
                                                        new NotCondition(new NakedCondition())),
					ConversationStates.IDLE,
					"Hi again! Good bye, and remember to behave if you want a present next year!",
				    new ChatAction() {
					    @Override
						public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					    	addHat(player);
					    }
					}
				);

				final List<ChatAction> reward = new LinkedList<ChatAction>();
				reward.add(new EquipItemAction("stocking"));
				reward.add(new SetQuestAction(QUEST_SLOT, "done"));
				reward.add(new ChatAction() {
				        @Override
						public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						    addHat(player);
						}
				    }
				);
				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
							new QuestNotCompletedCondition(QUEST_SLOT),
                                                        new PlayerIsAGoodBoyCondition(),
                                                        new NotCondition(new NakedCondition())),
					ConversationStates.IDLE,
					"Merry Christmas! I have a present and a hat for you. Good bye, and remember to behave if you want a present next year!",
					new MultipleActions(reward));

				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
                                                        new NotCondition(new PlayerIsAGoodBoyCondition())),
					ConversationStates.IDLE,
					"Ho ho ho! Looks like you haven't been a good this year! Only well behaved children get presents! Seasons greetings!",
					null);

				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(super.getName()),
							new NakedCondition()),
					ConversationStates.IDLE,
					"Goodness child, you'll get chilly without any covering! Dress up warm and I'll give you a hat to match! Ho ho ho!",
					null);
			}
		};
		santa.setEntityClass("santaclausnpc");
		santa.initHP(100);
		santa.setSounds(Arrays.asList("ho-ho-ho-1", "ho-ho-ho-2"));

		// start in int_admin_playground
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("int_admin_playground");
		santa.setPosition(17, 13);
		zone.add(santa);

		return santa;
	}

	private void addHat(final Player player) {
		final Outfit origOutfit = player.getOutfit();
		final int currentHat = origOutfit.getLayer("hat");
		int santaHat = 999;
		// unique Santa hats
		if (currentHat == 3 || currentHat == 996) {
			santaHat = 996;
		} else if (currentHat == 4 || currentHat == 997) {
			santaHat = 997;
		}

		final Map<String, String> colors = player.getOutfitColors();
		player.setOutfit(new Outfit(null, null, null, null, null, null, null, santaHat, null).putOver(origOutfit), true);

		// restore colors
		if (colors != null) {
			for (final String key : colors.keySet()) {
				player.setOutfitColor(key, colors.get(key));
			}
		}

		player.registerOutfitExpireTime(43200);
	}


	/**
	 * Removes Santa hat if not Christmas.
	 */
	@Override
	public void onLoggedIn(final Player player) {
		final int currentHat = player.getOutfit().getLayer("hat");
		final boolean wearingSantaHat = currentHat == 999 || currentHat == 996 || currentHat == 997;

		// is it Christmas?
		if (!isChristmasTime(new GregorianCalendar()) && wearingSantaHat) {
			player.returnToOriginalOutfit();
		}
	}

	/**
	 * checks whether now is christmas time
	 *
	 * @param cal current date
	 * @return true, if we are in season, false otherwise
	 */
	static boolean isChristmasTime(Calendar cal) {
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (month >= 1 && month < 10) {
			return false;
		}

		// January
		if (month == 0) {
			return day <= 6;
		}

		// November
		if (month == 10) {
			return day >= 23;
		}
		// December
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
	public void addToWorld() {
		fillQuestInfo(
				"Meet Santa",
				"Jingle bells, jingle bells, jingle all the way... Ho Ho Ho! Santa Claus delivers presents around Christmas time.",
				false);
		SingletonRepository.getLoginNotifier().addListener(this);

		if (System.getProperty("stendhal.santa") != null) {
			// activate santa here
			createSanta();
			teleporterBehaviour = new TeleporterBehaviour(santa, null, "0", "Ho, ho, ho! Merry Christmas!", false);
		}
	}

	/**
	 * removes a quest from the world.
	 *
	 * @return true, if the quest could be removed; false otherwise.
	 */
	@Override
	public boolean removeFromWorld() {
		removeNPC("Santa");
		// remove the turn notifiers left from the TeleporterBehaviour
		SingletonRepository.getTurnNotifier().dontNotify(teleporterBehaviour);
		return true;
	}

	@Override
	public String getName() {
		return "MeetSanta";
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
		return "Santa";
	}

}
