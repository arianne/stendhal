/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.atlantis.cityoutside;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.maps.Region;
import marauroa.common.Pair;


public class GreeterNPC implements ZoneConfigurator {

	private static final StendhalRPWorld world = SingletonRepository.getRPWorld();

	private SpeakerNPC greeter;

	private static List<Pair<String, String>> atlantisZones = null;
	private static List<String> atlantisPeople = null;
	private static List<String> atlantisCreatures = null;

	private Integer fee = null;

	private static enum InquiryType {
		ZONE,
		PERSON,
		CREATURE;
	};

	private static Map<String, String> zoneReplies = new HashMap<String, String>() {{
		put("Atlantis",
				"\"Atlantis\" is the name of our world. It consists of the city, " +
				"which we are in now, and the surrounding plains and mountains.");
		put("Atlantis City",
				"This is our beautiful city. We have been protected and " +
				"living in peace for centuries.");
	}};

	private static Map<String, String> peopleReplies = new HashMap<String, String>() {{
		put("Ryla", "Who, me? I don't have much to say about myself.");
		put("Zelan", "He is often looking for unicorn horns. You may want to ask him if needs any help.");
		put("Mirielle", "She runs the potions shop just northeast of here.");
	}};

	private static Map<String, String> creatureReplies = new HashMap<String, String>() {{
		put("baby pegasus",
				"Those poor creatures. Foreigners come here to poach them for the special rings they drop. " +
				"Their population has dwindled in recent years.");
		put("ivory pegasus",
				"They are very protective of their offspring. Don't be surprised if you find a couple guarding " +
				"a group of #'baby pegasus'.");
		put("woolly mammoth",
				"Those behemoths have been around since the beginning of time. Be careful if you come across a " +
				"herd. Their defense is high.");
	}};


	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		initNPC(zone);
		initHealerBehaviour();
	}

	private void initNPC(final StendhalRPZone zone) {
		greeter = new SpeakerNPC("Ryla");
		greeter.setEntityClass("atlantisfemale08npc");
		greeter.setDescription("You see a woman with youth in her face and centuries of experience in her eyes.");

		greeter.addGreeting();
		greeter.addGoodbye();
		greeter.addHelp(
				"This is Atlantis. We have lived here in peace for centries."
				+ " If you are wounded, I can #heal you. Or if you want to know"
				+ " more about this place, there are a few #topics I am schooled in.");
		greeter.addJob("It is my job to help new visitors #learn about our land. I can also #heal wounds.");
		greeter.addReply(
				Arrays.asList("topic", "learn"),
				"What would you like to know about? I can tell you about #zones, #creatures, or the #inhabitants of Atlantis.");
		greeter.addQuest("I don't have anything for you. But perhaps some of the other #people in Atlantis need your help.");

		greeter.add(ConversationStates.ATTENDING,
				Arrays.asList("zone", "area"),
				null,
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						// make sure zone list is ready
						buildAtlantisZoneList();

						final StringBuilder reply = new StringBuilder("The Atlantis zones are");
						final int zoneCount = atlantisZones.size();
						int idx = 0;
						for (final Pair<String, String> zonePair: atlantisZones) {
							final String zoneName = zonePair.first();

							reply.append(" #'" + zoneName + "'");

							if (idx < zoneCount - 2) {
								reply.append(",");
							} else if (idx == zoneCount - 2) {
								if (zoneCount > 2) {
									reply.append(",");
								}
								reply.append(" and");
							}
							idx++;
						}
						reply.append(". Which zone would you like to know more about?");

						greeter.say(reply.toString());
					}
				});

		greeter.add(ConversationStates.ATTENDING,
				Arrays.asList("inhabitant", "people", "npc"),
				null,
				ConversationStates.QUESTION_2,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						// make sure NPC list is ready
						buildAtlantisPeopleList();

						final StringBuilder reply = new StringBuilder("The people of Atlantis are peaceful. " +
								"That is how our civilization has survived for so long. Our citizens include");
						final int npcCount = atlantisPeople.size() - 1; // don't include Ryla
						int idx = 0;
						final String selfName = greeter.getName();
						for (final String npcName: atlantisPeople) {
							if (!npcName.equals(selfName)) {
								reply.append(" #'" + npcName + "'");

								if (idx < npcCount - 1) {
									reply.append(",");
								}
								idx++;
							}
						}

						if (npcCount > 1) {
							reply.append(",");
						}

						reply.append(" and myself, #'" + selfName + "'. Which person would you like to know more about?");

						greeter.say(reply.toString());
					}
				});

		greeter.add(ConversationStates.ATTENDING,
				Arrays.asList("creature", "enemy", "beast"),
				null,
				ConversationStates.QUESTION_3,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						// make sure creature list is ready
						buildAtlantisCreatureList();

						final StringBuilder reply = new StringBuilder("The creatures found in Atlantis are");
						final int creatureCount = atlantisCreatures.size();
						int idx = 0;
						for (final String creatureName: atlantisCreatures) {
							reply.append(" #'" + creatureName + "'");

							if (idx < creatureCount - 2) {
								reply.append(",");
							} else if (idx == creatureCount - 2) {
								if (creatureCount > 2) {
									reply.append(",");
								}
								reply.append(" and");
							}
							idx++;
						}
						reply.append(". Which creature would you like to know more about?");

						greeter.say(reply.toString());
					}
				});

		greeter.add(ConversationStates.QUESTION_1,
				"",
				null,
				ConversationStates.QUESTION_1,
				null,
				createReply(InquiryType.ZONE));

		greeter.add(ConversationStates.QUESTION_2,
				"",
				null,
				ConversationStates.QUESTION_2,
				null,
				createReply(InquiryType.PERSON));

		greeter.add(ConversationStates.QUESTION_3,
				"",
				null,
				ConversationStates.QUESTION_3,
				null,
				createReply(InquiryType.CREATURE));


		greeter.setPosition(65, 75);
		zone.add(greeter);
	}

	private void initHealerBehaviour() {
		final ChatAction calculateCostAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (player.getHP() == player.getBaseHP()) {
					greeter.say("I'm sorry, but it looks like you do not need healed. How else may I help you?");
					return;
				}

				final StringBuilder healMessage = new StringBuilder();
				if (player.isBadBoy()) {
					healMessage.append("Hmmmm, I see you haven't been behaving. Here in Atlantis, we don't take kindly to thugs, so you will pay extra to be healed. It will cost ");
					calculateFee(player, true);
				} else {
					healMessage.append("To heal someone of your stature will cost ");
					calculateFee(player, false);
				}

				healMessage.append(fee + ". Would you like to pay?");

				greeter.say(healMessage.toString());
				greeter.setCurrentState(ConversationStates.HEAL_OFFERED);
			}
		};

		final ChatAction healAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				player.drop("money", fee);
				greeter.addEvent(new SoundEvent(SoundID.HEAL, SoundLayer.CREATURE_NOISE));
				player.setHP(player.getBaseHP());

				// reset fee after healing
				fee = null;

				greeter.say("There, you are all healed. How else can I help you?");
			}
		};

		new HealerAdder().addHealer(greeter, calculateCostAction, healAction);
	}

	private void calculateFee(final Player player, final boolean badBoy) {
		final int diff = player.getBaseHP() - player.getHP();
		final int level = player.getLevel();

		fee = level * 4 + diff * 2;

		if (badBoy) {
			fee = fee * 3;
		}
	}

	private ChatAction createReply(final InquiryType type) {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final String inquiry = sentence.getTrimmedText().toLowerCase();

				if (ConversationPhrases.GOODBYE_MESSAGES.contains(inquiry)) {
					greeter.endConversation();
					return;
				}

				if (Arrays.asList("no", "none", "nothing", "no one").contains(inquiry)) {
					greeter.setCurrentState(ConversationStates.ATTENDING);
					greeter.say("Okay, what else can I help you with?");
					return;
				}

				Map<String, String> replyList;
				String subjectType;
				String noun = "something";

				if (type.equals(InquiryType.ZONE)) {
					replyList = zoneReplies;
					subjectType = "area";
				} else if (type.equals(InquiryType.PERSON)) {
					replyList = peopleReplies;
					subjectType = "person";
					noun = "someone";
				} else {
					replyList = creatureReplies;
					subjectType = "creature";
				}

				String reply = null;
				for (final String key: replyList.keySet()) {
					if (inquiry.equals(key.toLowerCase())) {
						reply = replyList.get(key);
						break;
					}
				}

				if (reply == null) {
					greeter.say("I'm sorry, I don't have any information on that " + subjectType + ". Ask me about " + noun + " else.");
					return;
				}

				if (type.equals(InquiryType.PERSON)) {
					reply += " Who";
				} else {
					reply += " What";
				}

				reply += " else would you like to know about?";
				greeter.say(reply);
			}
		};
	}

	private String formatZoneName(final StendhalRPZone zone) {
		String zoneName = zone.getHumanReadableName().replace("Deniran ", "").split(",")[0].replace("Mtn", "Mountain");

		if (zoneName.equals("Atlantis")) {
			zoneName = "Atlantis City";
		}

		return zoneName;
	}

	private void buildAtlantisZoneList() {
		if (atlantisZones == null) {
			atlantisZones = new ArrayList<>();

			final List<Pair<String, String>> mountains = new ArrayList<>();

			for (final StendhalRPZone z: world.getAllZonesFromRegion(Region.DENIRAN.toLowerCase(), true, false, true)) {
				final String zoneName = z.getName();

				if (zoneName.contains("atlantis")) {
					// format zone name for human readability
					final String formattedName = formatZoneName(z);

					if (formattedName.contains("Mountain")) {
						// store mountains for later adding
						mountains.add(new Pair<String, String>(formattedName, zoneName));
					} else {
						atlantisZones.add(new Pair<String, String>(formattedName, zoneName));
					}
				}
			}

			final Comparator<Pair<String, String>> sorter = new Comparator<Pair<String, String>>() {
				@Override
				public int compare(final Pair<String, String> p1, final Pair<String, String> p2) {
					return p1.first().toLowerCase().compareTo(p2.first().toLowerCase());
				}
			};

			Collections.sort(atlantisZones, sorter);
			Collections.sort(mountains, sorter);
			atlantisZones.addAll(mountains);
		}
	}

	private void buildAtlantisPeopleList() {
		if (atlantisPeople == null) {

			atlantisPeople = new ArrayList<>();

			final NPCList npcList = SingletonRepository.getNPCList();
			for (final String npcName: npcList.getNPCs()) {
				final SpeakerNPC npc = npcList.get(npcName);

				// exclude teleporting NPCs
				if (npc.isTeleporter()) {
					continue;
				}

				final String zoneName = npc.getZone().getName();
				// only include NPC inside city
				if (zoneName.equals("-7_deniran_atlantis") || (zoneName.startsWith("int_") && zoneName.contains("atlantis"))) {
					// use 'npc.getName()' instead of 'npcName' to get proper titleization
					atlantisPeople.add(npc.getName());
				}
			}

			Collections.sort(atlantisPeople, String.CASE_INSENSITIVE_ORDER);
		}
	}

	private void buildAtlantisCreatureList() {
		if (atlantisCreatures == null) {
			buildAtlantisZoneList();

			atlantisCreatures = new ArrayList<>();

			for (final Pair<String, String> zonePair: atlantisZones) {
				final String zoneID = zonePair.second();
				final StendhalRPZone rpZone = world.getZone(zoneID);
				if (rpZone != null) {
					for (final CreatureRespawnPoint spawner: rpZone.getRespawnPointList()) {
						final String creatureName = spawner.getPrototypeCreature().getName();
						if (!atlantisCreatures.contains(creatureName)) {
							atlantisCreatures.add(creatureName);
						}
					}
				}
			}

			Collections.sort(atlantisCreatures, String.CASE_INSENSITIVE_ORDER);
		}
	}
}
