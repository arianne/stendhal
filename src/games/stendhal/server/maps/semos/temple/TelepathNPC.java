package games.stendhal.server.maps.semos.temple;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TelepathNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosTempleArea(zone, attributes);
	}

	private void buildSemosTempleArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Io Flotto") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(8, 18));
				nodes.add(new Path.Node(8, 19));
				nodes.add(new Path.Node(15, 19));
				nodes.add(new Path.Node(15, 18));
				nodes.add(new Path.Node(16, 18));
				nodes.add(new Path.Node(16, 13));
				nodes.add(new Path.Node(15, 13));
				nodes.add(new Path.Node(15, 12));
				nodes.add(new Path.Node(12, 12));
				nodes.add(new Path.Node(8, 12));
				nodes.add(new Path.Node(8, 13));
				nodes.add(new Path.Node(7, 13));
				nodes.add(new Path.Node(7, 18));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, null, ConversationStates.ATTENDING,
				        null, new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, String text, SpeakerNPC engine) {
						        if (!player.hasQuest("meet_io")) {
							        engine
							                .say("I awaited you, "
							                        + player.getName()
							                        + ". How do I know your name? Easy, I'm Io Flotto, the telepath. Do you want me to show you the six basic elements of telepathy?");
							        player.setQuest("meet_io", "start");
						        } else {
							        engine.say("Hi again, " + player.getName()
							                + ". How can I #help you this time? Not that I don't already know...");
						        }
					        }
				        });
				addJob("I am committed to harnessing the total power of the human mind. I have already made great advances in telepathy and telekinesis; however, I can't yet foresee the future, so I don't know if we will truly be able to destroy Blordrough's dark legion...");
				addReply(
				        ConversationPhrases.QUEST_MESSAGES,
				        "Well, there's not really much that I need anyone to do for me right now. And I... Hey! Were you just trying to read my private thoughts? You should always ask permission before doing that!");
				addGoodbye();
				// further behaviour is defined in the MeetIo quest.
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "floattingladynpc");
		npc.set(8, 18);
		npc.initHP(100);
		zone.add(npc);
	}
}
