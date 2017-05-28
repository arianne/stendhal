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
package games.stendhal.server.maps.quests.mithrilcloak;

import java.util.Arrays;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;


/**
 * @author kymara
*/

class MakingClasp {


	private static final int REQUIRED_MINUTES_CLASP = 60;

	private MithrilCloakQuestInfo mithrilcloak;

	private final NPCList npcs = SingletonRepository.getNPCList();

	public MakingClasp(final MithrilCloakQuestInfo mithrilcloak) {
		this.mithrilcloak = mithrilcloak;
	}

	private void getClaspStep() {

		// don't overlap with any states from producer adder since he is a mithril bar producer

		final SpeakerNPC npc = npcs.get("Pedinghaus");

		// offer the clasp when prompted
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("clasp", "mithril clasp", "ida", "cloak", "mithril cloak"),
			new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_clasp"),
			ConversationStates.SERVICE_OFFERED,
			"A clasp? Whatever you say! I am still so happy from that letter you brought me, it would be my pleasure to make something for you. I only need one mithril bar. Do you have it?",
			null);

		// player says yes they want a clasp made and claim they have the mithril
		npc.add(
			ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_clasp"),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (player.isEquipped("mithril bar")) {
						player.drop("mithril bar");
							npc.say("What a lovely piece of mithril that is, even if I do say so myself ... Good, please come back in "
									   + REQUIRED_MINUTES_CLASP + " minutes and hopefully your clasp will be ready!");
							player.setQuest(mithrilcloak.getQuestSlot(), "forgingclasp;" + System.currentTimeMillis());
							player.notifyWorldAboutChanges();
						} else {
							npc.say("You can't fool an old wizard, and I'd know mithril when I see it. Come back when you have at least one bar.");
						}
				}
			});

		// player says they don't have any mithril yet
		npc.add(
			ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Well, if you should like me to cast any mithril bars just say.",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("clasp", "mithril clasp", "ida", "cloak", "mithril cloak"),
			new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "forgingclasp;"),
			ConversationStates.ATTENDING, null, new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String[] tokens = player.getQuest(mithrilcloak.getQuestSlot()).split(";");
					// minutes -> milliseconds
					final long delay = REQUIRED_MINUTES_CLASP * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
					final long timeRemaining = (Long.parseLong(tokens[1]) + delay)
							- System.currentTimeMillis();
					if (timeRemaining > 0L) {
						npc.say("I haven't finished yet, please return in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
						return;
					}
					npc.say("Here, your clasp is ready!");
					player.addXP(100);
					player.addKarma(15);
					final Item clasp = SingletonRepository.getEntityManager().getItem(
									"mithril clasp");
					clasp.setBoundTo(player.getName());
					player.equipOrPutOnGround(clasp);
					player.setQuest(mithrilcloak.getQuestSlot(), "got_clasp");
					player.notifyWorldAboutChanges();
				}
			});

	}


	private void giveClaspStep() {

		final SpeakerNPC npc = npcs.get("Ida");

		// Player brought the clasp, don't make them wait any longer for the cloak
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("clasp", "mithril clasp", "cloak", "mithril cloak", "task", "quest"),
				new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_clasp"), new PlayerHasItemWithHimCondition("mithril clasp")),
				ConversationStates.ATTENDING,
				"Wow, Pedinghaus really outdid himself this time. It looks wonderful on your new cloak! Wear it with pride.",
				new MultipleActions(
									 new DropItemAction("mithril clasp"),
									 new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "done", 10.0),
									 new EquipItemAction("mithril cloak", 1, true),
									 new IncreaseXPAction(1000)
									 )
				);

		// remind about getting clasp
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("clasp", "mithril clasp", "cloak", "mithril cloak", "task", "quest"),
				new OrCondition(
								new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_clasp"),
								new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "forgingclasp;"),
								new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_clasp"),
												 new NotCondition(new PlayerHasItemWithHimCondition("mithril clasp")))
								),
				ConversationStates.ATTENDING,
				"You haven't got the clasp from #Pedinghaus yet. As soon as I have that your cloak will be finished!",
				null);
	}

	public void addToWorld() {
		getClaspStep();
		giveClaspStep();

	}

}
