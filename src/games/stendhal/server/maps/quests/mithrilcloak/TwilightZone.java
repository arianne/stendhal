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

import games.stendhal.common.Direction;
import games.stendhal.common.parser.ExpressionType;
import games.stendhal.common.parser.JokerExprMatcher;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TextHasNumberCondition;
import games.stendhal.server.entity.player.Player;


/**
 * @author kymara
*/

class TwilightZone {

	private MithrilCloakQuestInfo mithrilcloak;

	private final NPCList npcs = SingletonRepository.getNPCList();

	public TwilightZone(final MithrilCloakQuestInfo mithrilcloak) {
		this.mithrilcloak = mithrilcloak;
	}


	private void getMossStep() {

		// Careful not to overlap with quest states in RainbowBeans quest

		final int MOSS_COST = 3000;

		final SpeakerNPC npc = npcs.get("Pdiddi");

		// offer moss when prompted
		npc.add(ConversationStates.ANY,
				Arrays.asList("moss", "magical", "twilight", "ida", "cloak", "mithril cloak", "specials", "twilight moss"),
				new QuestInStateCondition(mithrilcloak.getQuestSlot(), "twilight_zone"),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Keep it quiet will you! Yeah, I got moss, it's "
				+ Integer.toString(MOSS_COST) + " money each. How many do you want?",
				null);

		// responding to question of how many they want, with a number
		npc.addMatching(ConversationStates.QUEST_ITEM_QUESTION,
				// match for all numbers as trigger expression
				ExpressionType.NUMERAL, new JokerExprMatcher(),
				new TextHasNumberCondition(1, 5000),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {

                        final int required = (sentence.getNumeral().getAmount());
						if (player.drop("money" , required * MOSS_COST)) {
							npc.say("Ok, here's your " + Integer.toString(required) + " pieces of twilight moss. Don't take too much at once.");
							new EquipItemAction("twilight moss", required, true).fire(player, sentence, npc);
						} else {
							npc.say("Ok, ask me again when you have enough money.");
						}
					}
				});

		// they don't want moss yet
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				Arrays.asList("no", "none", "nothing"),
				null,
				ConversationStates.ATTENDING,
				"Ok, whatever you like.",
				null);
	}

	private void twilightZoneStep() {

		final SpeakerNPC npc = npcs.get("Ida");
		// player hasn't given elixir to lda in the twilight zone yet
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("magical", "mithril", "cloak", "mithril cloak", "task", "quest", "twilight"),
				new QuestInStateCondition(mithrilcloak.getQuestSlot(), "twilight_zone"),
				ConversationStates.ATTENDING,
				"What's happening to me? I'm feverish .. I see twilight .. you can't understand unless you visit me here ... you must ask #Pdiddi how to get to the #twilight.",
				null);

		npc.addReply("Pdiddi", "Oh, I'm too confused... I can't tell you anything about him...");


		// player gave elixir and returned
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("magical", "mithril", "cloak", "mithril cloak", "task", "quest", "twilight", "elixir"),
				new QuestInStateCondition(mithrilcloak.getQuestSlot(), "taking_striped_cloak"),
				ConversationStates.ATTENDING,
				"When I was sick I got behind on my other jobs. I promised #Josephine I'd make her a stripey cloak but I have no time. So please, I'm relying on you to buy one and take it to her. They sell blue striped cloaks in Ados abandoned keep. Thank you!",
				null);


		// use the clone of Ida for twilight zone
		final SpeakerNPC npc2 = npcs.get("twilight_ida");

		npc2.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc2.getName()),
						new QuestInStateCondition(mithrilcloak.getQuestSlot(), "twilight_zone"),
						new NotCondition(new PlayerHasItemWithHimCondition("twilight elixir"))),
				ConversationStates.IDLE,
				"I'm sick .. so sick .. only some powerful medicine will fix me.",
				null);

		npc2.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc2.getName()),
						new QuestInStateCondition(mithrilcloak.getQuestSlot(), "twilight_zone"),
						new PlayerHasItemWithHimCondition("twilight elixir")),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Is that elixir for me? If #yes I will take it immediately. You must return to see me again in my normal state.",
				 null);

		npc2.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "twilight_zone"),
								 new PlayerHasItemWithHimCondition("twilight elixir")
								 ),
				ConversationStates.IDLE,
				"Thank you!",
				new MultipleActions(
								new DropItemAction("twilight elixir"),
								new SetQuestAction(mithrilcloak.getQuestSlot(), "taking_striped_cloak"),
								new TeleportAction("int_ados_sewing_room", 12, 20, Direction.DOWN)
								)
				);

		npc2.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES,
				new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "twilight_zone"),
								 new PlayerHasItemWithHimCondition("twilight elixir")
								 ),
				ConversationStates.IDLE,
				"I'm getting sicker ...",
				 null);
	}

	public void addToWorld() {
		getMossStep();
		twilightZoneStep();
	}

}
