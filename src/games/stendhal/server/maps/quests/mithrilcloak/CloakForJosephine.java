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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.player.Player;


/**
 * @author kymara
*/

class CloakForJosephine {

	private final MithrilCloakQuestInfo mithrilcloak;

	private final NPCList npcs = SingletonRepository.getNPCList();

	public CloakForJosephine(final MithrilCloakQuestInfo mithrilcloak) {
		this.mithrilcloak = mithrilcloak;
	}

	private void takeStripedCloakStep() {

		// Deliberately overlap with conversation states from the cloak collector quests.
		// Since if you're on one of these quests she will always ask 'did you bring any cloaks?'
		// and waits for you to say yes or the name of the cloak you brought
		// if she just said about 'blue striped cloak' "well i don't want that" then that's confusing for player
		// so we let player give her that cloak even if she was asking about the other quests
		// of course, she will only take it from ida if player was in quest state for teh mithril cloak quest of "taking_striped_cloak"

		final SpeakerNPC npc = npcs.get("Josephine");

		// overlapping with CloaksCollector quest deliberately
		npc.add(ConversationStates.QUESTION_1, "blue striped cloak", new QuestInStateCondition(mithrilcloak.getQuestSlot(), "taking_striped_cloak"),
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (player.drop("blue striped cloak")) {
							npc.say("Oh, wait, that's from Ida isn't it?! Oh yay! Thank you! Please tell her thanks from me!!");
							player.setQuest(mithrilcloak.getQuestSlot(), "gave_striped_cloak");
							npc.setCurrentState(ConversationStates.ATTENDING);
						} else {
							npc.say("You don't have a blue striped cloak with you.");
						}
					}
			});

		// overlapping with CloaksCollector2 quest deliberately
		npc.add(ConversationStates.QUESTION_2, "blue striped cloak", new QuestInStateCondition(mithrilcloak.getQuestSlot(), "taking_striped_cloak"),
				ConversationStates.QUESTION_2, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (player.drop("blue striped cloak")) {
							npc.say("Oh, wait, that's from Ida isn't it?! Oh yay! Thank you! Please tell her thanks from me!!");
							npc.setCurrentState(ConversationStates.ATTENDING);
							player.setQuest(mithrilcloak.getQuestSlot(), "gave_striped_cloak");
						} else {
							npc.say("You don't have a blue striped cloak with you.");
						}
					}
			});

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("blue striped cloak", "mithril", "mithril cloak", "ida"),
				new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "taking_striped_cloak"), new PlayerHasItemWithHimCondition("blue striped cloak")),
				ConversationStates.ATTENDING,
				"Oh that's from Ida isn't it?! Oh yay! Thank you! Please tell her thanks from me!!",
				new MultipleActions(
									 new DropItemAction("blue striped cloak"),
									 new SetQuestAction(mithrilcloak.getQuestSlot(), "gave_striped_cloak")
									 )
				);


	}
	private void askforClaspStep() {

		final SpeakerNPC npc = npcs.get("Ida");

		// acknowledge that player took cloak and ask for clasp
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("thanks", "josephine", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new QuestInStateCondition(mithrilcloak.getQuestSlot(), "gave_striped_cloak"),
				ConversationStates.ATTENDING,
				"Aw, Josephine is so sweet. I'm glad she liked her blue striped cloak. Now, YOUR cloak is nearly ready, it just needs a clasp to fasten it! My friend #Pedinghaus will make it for you, if you go and ask him.",
				new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "need_clasp", 10.0)
				);

		npc.addReply("Josephine", "Surely you know Josephine? That flirty flighty girl from Fado, bless her heart.");
		npc.addReply("Pedinghaus", "I mean the wizard who works with Joshua in the Ados smithy.");
	}

	public void addToWorld() {
		takeStripedCloakStep();
		askforClaspStep();

	}

}
