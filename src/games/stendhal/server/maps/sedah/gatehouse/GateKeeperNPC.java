package games.stendhal.server.maps.sedah.gatehouse;

import games.stendhal.common.Rand;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * Builds a gatekeeper NPC Bribe him with at least 300 money to get the key for
 * the Sedah city walls. He stands in the doorway of the gatehouse till the
 * interior is made.
 *
 * @author kymara
 */
public class GateKeeperNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Revi Borak") {

			@Override
			protected void createPath() {
				// not moving.
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						if (!player.isEquipped("sedah gate key")) {
							engine.say("What do you want?");
						} else {
							// toss a coin to see if he notices player still has
							// the gate key
							if (Rand.throwCoin() == 1) {
								player.drop("sedah gate key");
								engine.say("You shouldn't still have that key! I'll take that right back.");
							} else {
								engine.say("Hi, again.");
							}
						}
					}
				});
				addReply("nothing", "Good.");
				addReply("key", "I'm open to bribery...");
				addJob("I am the gatekeeper for the imperial city of Sedah. I am not supposed to let anyone, but perhaps you can make me an #offer.");
				addHelp("You can't get into the imperial city of Sedah without a key.");
				addQuest("The only favour I need is cold hard cash.");
				addOffer("Only a #bribe could persuade me to hand over the key to that gate.");

				addReply("bribe", null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						Expression object = sentence.getObject(0);
						int amount = object != null ? object.getAmount() : 1;
				        String item = sentence.getObjectName();

				        if (sentence.hasError()) {
				        	engine.say(sentence.getErrorString() + " Are you trying to trick me? Bribe me some number of coins!");
				        } else if (item == null) {
							// player only said 'bribe'
							engine.say("A bribe of no money is no bribe! Bribe me with some amount!");
				        } else if (!item.toLowerCase().equals("money")) {
							// This bit is just in case the player says 'bribe X potatoes', not money
							engine.say("You can't bribe me with anything but money!");
						} else {
							try {
								if (amount < 300) {
									// Less than 300 is not money for him
									engine.say("You think that amount will persuade me?! That's more than my job is worth!");
								} else {
									if (player.isEquipped("money", amount)) {
										player.drop("money", amount);
										engine.say("Ok, I got your money, here's the key.");
										Item key = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
												"sedah gate key");
										player.equip(key, true);
									} else {
										// player bribed enough but doesn't have
										// the cash
										engine.say("Criminal! You don't have "
												+ amount + " money!");
									}
								}
							} catch (NumberFormatException e) {
								// player said bribe followed by a non integer
								engine.say("Are you trying to trick me? Bribe me some number of coins!");
							}
						}
					}
				});
				addGoodbye("Bye. Don't say I didn't warn you!");
			}
		};

		npc.setDescription("You see a tough looking soldier. He looks open to bribery.");
		/*
		 * We don't seem to be using the recruiter images that lenocas made for
		 * the Fado Raid area so I'm going to put him to use here. If the raid
		 * part ever gets done, this image can change.
		 */
		npc.setEntityClass("recruiter2npc");
		npc.setPosition(120, 67);
		npc.initHP(100);
		zone.add(npc);
	}
}
