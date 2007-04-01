package games.stendhal.server.maps.semos.blacksmith;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

public class BlacksmithAssistantNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosBlacksmithArea(zone, attributes);
	}

	private void buildSemosBlacksmithArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC hackim = new SpeakerNPC("Hackim Easso") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(5, 1));
				nodes.add(new Path.Node(8, 1));
				nodes.add(new Path.Node(7, 1));
				nodes.add(new Path.Node(7, 6));
				nodes.add(new Path.Node(16, 6));
				nodes.add(new Path.Node(16, 1));
				nodes.add(new Path.Node(15, 1));
				nodes.add(new Path.Node(16, 1));
				nodes.add(new Path.Node(16, 6));
				nodes.add(new Path.Node(7, 6));
				nodes.add(new Path.Node(7, 1));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, null, ConversationStates.ATTENDING,
				        null, new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, String text, SpeakerNPC engine) {
						        if (!player.hasQuest("meet_hackim")) {
							        engine
							                .say("Hi stranger, I'm Hackim Easso, the blacksmith's assistant. Have you come here to buy weapons?");
							        player.setQuest("meet_hackim", "start");
						        } else {
							        engine.say("Hi again, " + player.getName() + ". How can I #help you this time?");
						        }
					        }
				        });
				addHelp("I'm the blacksmith's assistant. Tell me... Have you come here to buy weapons?");
				addJob("I help Xoderos the blacksmith to make weapons for Deniran's army. I mostly only bring the coal for the fire and put the weapons up on the shelves. Sometimes, when Xoderos isn't looking, I like to use one of the swords to pretend I'm a famous adventurer!");
				addGoodbye();
			}
		};
		npcs.add(hackim);
		zone.assignRPObjectID(hackim);
		hackim.put("class", "naughtyteennpc");
		hackim.set(5, 1);
		hackim.initHP(100);
		zone.add(hackim);

	}
}
