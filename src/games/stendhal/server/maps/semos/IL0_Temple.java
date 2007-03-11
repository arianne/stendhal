package games.stendhal.server.maps.semos;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

public class IL0_Temple implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildSemosTempleArea(zone, attributes);
	}


	private void buildSemosTempleArea(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Ilisa") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 5));
				nodes.add(new Path.Node(14, 5));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("My special powers help me to heal wounded people. I also sell potions and antidotes.");
				addHelp("I can #heal you here for free, or you can take one of my prepared medicines with you on your travels; just ask for an #offer.");
				addSeller(new SellerBehaviour(shops.get("healing")));
				addHealer(0);
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "welcomernpc");
		npc.set(9, 5);
		npc.initHP(100);
		zone.addNPC(npc);
		npc = new SpeakerNPC("Io Flotto") {
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
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
					GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// A little trick to make NPC remember if it has met
							// player before anc react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Io")) {
								engine.say("I awaited you, "
										   + player.getName()
										   + ". How do I know your name? Easy, I'm Io Flotto, the telepath. Do you want me to show you the six basic elements of telepathy?");
								player.setQuest("Io", "done");
							} else {
								engine.say("Hi again, "
										   + player.getName()
										   + ". How can I #help you this time? Not that I don't already know...");
							}
						}
				});
				addHelp("I'm a telepath and a telekinetic; I can help you by sharing my mental skills with you. Do you want me to teach you the six basic elements of telepathy? I already know the answer but I'm being polite...");
				addJob("I am committed to harnessing the total power of the human mind. I have already made great advances in telepathy and telekinesis; however, I can't yet foresee the future, so I don't know if we will truly be able to destroy Blordrough's dark legion...");
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Well, there's not really much that I need anyone to do for me right now. And I... Hey! Were you just trying to read my private thoughts? You should always ask permission before doing that!",
					null);
				addGoodbye();
			}
		};
		npcs.add(npc);
		
		zone.assignRPObjectID(npc);
		npc.put("class", "floattingladynpc");
		npc.set(8, 18);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
