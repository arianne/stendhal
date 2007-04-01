package games.stendhal.server.maps.semos.city;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RetiredAdventurerNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosCityAreaHayunnNaratha(zone);
	}

	private void buildSemosCityAreaHayunnNaratha(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Hayunn Naratha") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(27, 37));
				nodes.add(new Path.Node(27, 38));
				nodes.add(new Path.Node(29, 38));
				nodes.add(new Path.Node(29, 37));
				nodes.add(new Path.Node(29, 38));
				nodes.add(new Path.Node(27, 38));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, null, ConversationStates.ATTENDING,
				        null, new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, String text, SpeakerNPC engine) {
						        // A little trick to make NPC remember if it has met
						        // player before anc react accordingly
						        // NPC_name quest doesn't exist anywhere else neither is
						        // used for any other purpose
						        if (!player.hasQuest("meet_hayunn")) {
							        engine
							                .say("You've probably heard of me; Hayunn Naratha, a retired adventurer. Have you read my book? No? It's called \"Know How To Kill Creatures\". Maybe we could talk about adventuring, if you like?");
							        player.setQuest("meet_hayunn", "start");
						        } else {
							        engine.say("Hi again, " + player.getName() + ". How can I #help you this time?");
						        }
					        }
				        });
				addHelp("As I say, I'm a retired adventurer, and now I teach people. Do you want me to teach you about killing creatures?");
				addJob("My job is to guard the people of Semos from any creature that might escape this vile dungeon! With all our young people away battling Blordrough's evil legions to the south, the monsters down there are getting more confident about coming to the surface.");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldheronpc");
		npc.set(27, 37);
		npc.initHP(100);
		zone.add(npc);

	}
}
