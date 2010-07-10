package games.stendhal.server.maps.kirdneh.inn;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.HealerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * Builds a Healer NPC for kirdneh. 
 * She likes a drink
 *
 * @author kymara
 */
public class HealerNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Katerina") {

			@Override
			protected void createPath() {
			    // sits still on stool
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Gis' a kiss!");
				addReply("kiss", "ew sloppy");
				addReply(":*", "*:");
				addJob("Wuh? Uhh. Heal. Yeah. tha's it.");
				addHealer(this, 200);
				addHelp("Gimme money for beer. I heal, gis' cash.");
				addQuest("Bah.");
 				addGoodbye("pffff bye");
			}
		};

		npc.setDescription("You see a woman who was perhaps once beautiful but now a little the worse for wear...");
		npc.setEntityClass("womanonstoolnpc");
		npc.setPosition(25, 9);
		npc.setDirection(Direction.UP);
		npc.initHP(100);
		zone.add(npc);
	}
    // Don't want to use standard responses for Heal, in fact what to modify them all, so just configure it all here.
    private void addHealer(final SpeakerNPC npc, final int cost) {
    final HealerBehaviour healerBehaviour = new HealerBehaviour(cost);
		final Engine engine = npc.getEngine();

		engine.add(ConversationStates.ATTENDING, 
				ConversationPhrases.OFFER_MESSAGES, 
				null, 
				ConversationStates.ATTENDING, 
				"Gimme money for beer. I heal, gis' cash.", 
				null);

		engine.add(ConversationStates.ATTENDING, 
				"heal", 
				null, 
				ConversationStates.HEAL_OFFERED, 
				null,
		        new ChatAction() {
			        public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
                        healerBehaviour.setChosenItemName("heal");
                        healerBehaviour.setAmount(1);
                        final int cost = healerBehaviour.getCharge(player);

                        if (cost != 0) {
                        	raiser.say("For " + cost + " cash, ok?");
                        }
			        }
		        });

		engine.add(ConversationStates.HEAL_OFFERED, 
				ConversationPhrases.YES_MESSAGES, 
				null,
		        ConversationStates.ATTENDING, 
		        null,
		        new ChatAction() {
			        public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				        if (player.drop("money", healerBehaviour.getCharge(player))) {
					        healerBehaviour.heal(player);
					        raiser.say("All better now, everyone better. I love you, I do.");
				        } else {
					        raiser.say("Pff, no money, no heal.");
				        }
			        }
		        });

		engine.add(ConversationStates.HEAL_OFFERED, 
				ConversationPhrases.NO_MESSAGES, 
				null,
		        ConversationStates.ATTENDING, 
		        "Wha you want then?", 
		        null);
	}

}
