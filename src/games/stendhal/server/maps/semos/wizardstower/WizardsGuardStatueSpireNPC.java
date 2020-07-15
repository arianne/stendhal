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
package games.stendhal.server.maps.semos.wizardstower;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;

/**
 * Zekiel, the guardian statue of the Wizards Tower (Zekiel in the spire)
 *
 * @see games.stendhal.server.maps.quests.ZekielsPracticalTestQuest
 * @see games.stendhal.server.maps.semos.wizardstower.WizardsGuardStatueNPC
 */
public class WizardsGuardStatueSpireNPC implements ZoneConfigurator {

	private static SpeakerNPC zekielspire;

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildZekielSpire(zone);
	}

	private void buildZekielSpire(final StendhalRPZone zone) {
		if (zekielspire == null) {
			zekielspire = new SpeakerNPC("Zekiel") {

				@Override
				protected void createPath() {
					setPath(null);
				}

				@Override
				protected void createDialog() {
					addGreeting("Greetings again, adventurer!");
					addHelp("You are stood in the #store. You can enter the spire by the teleporter in front of me. The one behind me teleports you back to the tower entrance.");
					addJob("I am the guardian and #storekeeper of the #wizards tower.");
					addGoodbye("So long!");
					addOffer("I can create #special items with the materials from the store. Just tell me what you want, but for most items I will need extra ingredients.");
					addReply(Arrays.asList("store", "storekeeper"),
					        "I can create #special items with the materials from the store. Just tell me what you want, but for most items I will need extra ingredients.");

					addReply("special",
					        "For now I can create #Demon #Fire #Swords, and #Enhanced #Lion #Shields. I could read in your mind, adventurer, but it is not allowed of me here. So you have to tell me which special item you want and I will tell you, if I can help you.");
	//				addReply("special",
	//				        "I am sorry, now is not the time. Try again in some weeks, and I may be ready to help you.");

					addReply(Arrays.asList("wizard", "wizards"),
					        "Seven wizards form the wizards circle. These are #Erastus, #Elana, #Ravashack, #Jaer, #Cassandra, #Silvanus and #Malleus");
					addReply("erastus", "Erastus is the archmage of the wizards circle. He is the grandmaster of all magics and the wisest person that is known. He is the only one without a part in the practical test.");
					addReply("elana", "Elana is the warmest and friendliest enchantress. She is the protectress of all living creatures and uses divine magic to save and heal them.");
					addReply("ravashack", "Ravashack is a very mighty necromancer. He has studied the dark magic for ages. Ravashack is a mystery, using dark magic to gain the upper hand on his opponents, but fighting the evil liches, his arch enemies.");
					addReply("jaer", "Jaer is the master of illusion. Charming and flighty like a breeze on a hot summer day. His domain is Air and he has many allies in the plains of mythical ghosts.");
					addReply("cassandra", "Cassandra is a beautiful woman, but foremost a powerful sorceress. Cassandra's domain is Water and she can be cold like ice to achieve her aim.");
					addReply("silvanus", "Silvanus is a sage druid and perhaps the eldest of all elves. He is a friend of all animals, trees, fairy creatures and ents. His domain is Earth and Nature.");
					addReply("malleus", "Malleus is the powerful archetype of a magician and the master of destructive magics. His domain is Fire and he rambled the plains of demons for ages, to understand their ambitions.");

					//behavior for enhancing lion shield
					add(ConversationStates.ATTENDING,
							Arrays.asList("enhanced lion shield", "shields", "shield"),
							ConversationStates.INFORMATION_1,
						    "I can turn a plate shield into an enhanced lion shield with iron, but I need eight pieces of iron and the shield to do that. Do you want an enhanced lion shield?",
						    null);
					add(ConversationStates.INFORMATION_1,
							ConversationPhrases.YES_MESSAGES,
							new AndCondition(
									new NotCondition(new PlayerHasItemWithHimCondition("iron", 8)),
									new PlayerHasItemWithHimCondition("plate shield", 1)),
							ConversationStates.ATTENDING,
							"You don't have enough Iron, I will need 8 iron bars and a plate shield.",
							null);
					add(ConversationStates.INFORMATION_1,
							ConversationPhrases.YES_MESSAGES,
							new AndCondition(
									new NotCondition(new PlayerHasItemWithHimCondition("plate shield", 1)),
									new PlayerHasItemWithHimCondition("iron", 8)),
							ConversationStates.ATTENDING,
							"You do not have a shield for me to enhance, I will need 8 iron bars and a plate shield.",
							null);
					add(ConversationStates.INFORMATION_1,
							ConversationPhrases.YES_MESSAGES,
							new AndCondition(
									new PlayerHasItemWithHimCondition("iron", 8),
									new PlayerHasItemWithHimCondition("plate shield", 1)),
							ConversationStates.ATTENDING,
							"There is your enhanced lion shield.",
							new MultipleActions(
								new DropItemAction("iron", 8),
								new DropItemAction("plate shield", 1),
								new EquipItemAction("enhanced lion shield", 1, true),
								new IncreaseXPAction(250)));
						add(ConversationStates.INFORMATION_1,
							ConversationPhrases.NO_MESSAGES,
							null,
							ConversationStates.ATTENDING,
							"Fine. Just tell me when you want an enhanced lion shield.",
							null);

						//behavior for forging a demon fire sword
						add(ConversationStates.ATTENDING,
								Arrays.asList("demon fire sword", "swords", "sword"),
								ConversationStates.INFORMATION_1,
							    "I can craft for you a demon fire sword if you can procure a demon sword and a fire sword.",
							    null);
						add(ConversationStates.INFORMATION_1,
								ConversationPhrases.YES_MESSAGES,
								new AndCondition(
										new NotCondition(new PlayerHasItemWithHimCondition("fire sword", 1)),
										new PlayerHasItemWithHimCondition("demon sword", 1)),
								ConversationStates.ATTENDING,
								"You don't have a fire sword, I need both a demon sword and a fire sword.",
								null);
						add(ConversationStates.INFORMATION_1,
								ConversationPhrases.YES_MESSAGES,
								new AndCondition(
										new NotCondition(new PlayerHasItemWithHimCondition("demon sword", 1)),
										new PlayerHasItemWithHimCondition("fire sword", 1)),
								ConversationStates.ATTENDING,
								"You don't have a demon sword, I need both a fire sword and a demon sword.",
								null);
						add(ConversationStates.INFORMATION_1,
								ConversationPhrases.YES_MESSAGES,
								new AndCondition(
										new PlayerHasItemWithHimCondition("demon sword", 1),
										new PlayerHasItemWithHimCondition("fire sword", 1)),
								ConversationStates.ATTENDING,
								"There is your Demon Fire Sword.",
								new MultipleActions(
									new DropItemAction("demon sword", 1),
									new DropItemAction("fire sword", 1),
									new EquipItemAction("demon fire sword", 1, true),
									new IncreaseXPAction(11250)));
							add(ConversationStates.INFORMATION_1,
								ConversationPhrases.NO_MESSAGES,
								null,
								ConversationStates.ATTENDING,
								"Fine. Just tell me when you want to forge a demon fire sword.",
								null);

	/**				// behavior on special item BLANK SCROLL
					add(ConversationStates.ATTENDING,
					    Arrays.asList("blank scroll", "scrolls"),
					    ConversationStates.INFORMATION_1,
					    "I will create a blank scroll for you, but I need eight pieces of wood for that. The blank scroll can be enchanted by wizards. Do you want a blank scroll?",
					    null);
					add(ConversationStates.INFORMATION_1,
						ConversationPhrases.YES_MESSAGES,
						new NotCondition(new PlayerHasItemWithHimCondition("wood", 8)),
						ConversationStates.ATTENDING,
						"You don't have enough wood, I will need eight pieces.",
						null);
					add(ConversationStates.INFORMATION_1,
						ConversationPhrases.YES_MESSAGES,
						new PlayerHasItemWithHimCondition("wood", 8),
						ConversationStates.ATTENDING,
						"There is your blank scroll.",
						new MultipleActions(
							new DropItemAction("wood", 8),
							new EquipItemAction("blank scroll", 1, true),
							new IncreaseXPAction(250)));
					add(ConversationStates.INFORMATION_1,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Well, maybe later. Just tell me when you want a blank scroll.",
						null);

					//behavior on special item RIFT CLOAK
					add(ConversationStates.ATTENDING,
					    Arrays.asList("rift cloak"),
					    ConversationStates.INFORMATION_2,
					    "I will create a rift cloak for you, but I have to fuse a carbuncle and a sapphire in the magic. The cloak is useless in battle and will protect you only one time, when entering a magical rift."+
						" The rift disintegrates the cloak instead of you. There is no way to get the cloak back. If you want to enter the rift again, you will need a new rift cloak. Shall I create one for you?",
					     null);
					add(ConversationStates.INFORMATION_2,
						ConversationPhrases.YES_MESSAGES,
						new AndCondition(
								new NotCondition(new PlayerHasItemWithHimCondition("carbuncle", 1)),
								new PlayerHasItemWithHimCondition("sapphire", 1)),
						ConversationStates.ATTENDING,
						"You don't have a carbuncle, I will need a sapphire and a carbuncle.",
						null);
					add(ConversationStates.INFORMATION_2,
						ConversationPhrases.YES_MESSAGES,
						new AndCondition(
								new NotCondition(new PlayerHasItemWithHimCondition("sapphire", 1)),
								new PlayerHasItemWithHimCondition("carbuncle", 1)),
						ConversationStates.ATTENDING,
						"You don't have a sapphire, I will need a carbuncle and a sapphire.",
						null);
					add(ConversationStates.INFORMATION_2, ConversationPhrases.YES_MESSAGES,
							new AndCondition(
									new PlayerHasItemWithHimCondition("sapphire", 1),
									new PlayerHasItemWithHimCondition("carbuncle", 1)),
						ConversationStates.ATTENDING,
						"There is your rift cloak. Don't forget that it protects you only one time, before it is destroyed. So be sure that you are ready for what awaits you in the rift.",
						new MultipleActions(
								new DropItemAction("carbuncle", 1),
								new DropItemAction("sapphire", 1),
								new EquipItemAction("rift cloak", 1, true),
								new IncreaseXPAction(5000)));
					add(ConversationStates.INFORMATION_2,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Don't forget that you can't enter a magical rift without a rift cloak.",
						null);
	*/

					//behavior on special item XARUHWAIYZ PHIAL
				} //remaining behavior defined in maps.quests.ZekielsPracticalTestQuest
			};

			zekielspire.setDescription("You see Zekiel, the guardian of this tower.");
			zekielspire.setEntityClass("transparentnpc");
			zekielspire.setAlternativeImage("zekiel");
			zekielspire.setPosition(15, 15);
			zekielspire.initHP(100);
		}

		if (zone != null) {
			zone.add(zekielspire);
		}
	}

	/**
	 * Retrieves the SpeakerNPC instance for Zekiel.
	 *
	 * @return
	 * 		SpeakerNPC
	 */
	public SpeakerNPC getZekiel() {
		if (zekielspire == null) {
			configureZone(null, null);
		}

		return zekielspire;
	}
}
