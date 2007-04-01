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

public class GossipNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosCityAreaNomyrAhba(zone);
	}

	private void buildSemosCityAreaNomyrAhba(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Nomyr Ahba") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(46, 19));
				nodes.add(new Path.Node(46, 20));
				nodes.add(new Path.Node(50, 20));
				nodes.add(new Path.Node(50, 19));
				nodes.add(new Path.Node(50, 20));
				nodes.add(new Path.Node(46, 20));
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
						        if (!player.isQuestCompleted("Nomyr")) {
							        engine
							                .say("Heh heh... Oh, hello stranger! You look a bit disoriented... d'you want to hear the latest gossip?");
							        player.setQuest("Nomyr", "done");
						        } else {
							        engine.say("Hi again, " + player.getName() + ". How can I #help you this time?");
						        }
					        }
				        });
				addHelp("I'm a... let's call me an \"observer\". I can tell you about all the latest rumours. Do you want to hear?");
				addJob("I know every rumour that exists in Semos, and I invented most of them! The one about Hackim smuggling in weapons for wandering adventurers like you is true, though.");
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING, "Thanks for asking, but I don't need anything right now.", null);
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "thiefnpc");
		npc.set(46, 19);
		npc.initHP(100);
		zone.add(npc);
	
	}
}
