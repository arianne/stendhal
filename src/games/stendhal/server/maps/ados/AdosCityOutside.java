package games.stendhal.server.maps.ados;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates the NPCs and portals in Ados City.
 *
 * @author hendrik
 */
public class AdosCityOutside implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildFidorea(zone);
		buildKids(zone);
	}

	private void buildFidorea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Fidorea") {

			@Override
			protected void createPath() {
				// npc does not move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, there. Do you need #help with anything?");
				addHelp("If you don't like your mask, you can #return and I will remove it, or you can just wait until it wears off.");
				addQuest("Are you looking for toys for Anna? She loves my costumes, perhaps she'd like a #dress to try on. If you already got her one, I guess she'll have to wait till I make more costumes!");// this is a hint that one of the items Anna wants is a dress (goblin dress)
				addJob("I am a makeup artist.");
				addReply(
				        "dress",
				        "I read stories that goblins wear a dress as a kind of armor! If you're scared of goblins, like me, maybe you can buy a dress somewhere. ");
				//addReply("offer", "Normally I sell masks. But I ran out of clothes and cannot by new ones until the cloth seller gets back from his search.");
				addGoodbye("Bye, come back soon.");

				Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("mask", 2);
				OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(priceList, 100, "Your mask has worn off.");
				addOutfitChanger(behaviour, "buy");
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "woman_008_npc");
		npc.set(20, 12);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}

	private void buildKids(StendhalRPZone zone) {
		String[] names = { "Jens", "George", "Anna" };
		String[] classes = { "kid3npc", "kid4npc", "kid5npc" };
		Path.Node[] start = new Path.Node[] { new Path.Node(40, 28), new Path.Node(40, 40), new Path.Node(45, 28) };
		for (int i = 0; i < 3; i++) {
			SpeakerNPC npc = new SpeakerNPC(names[i]) {

				@Override
				protected void createPath() {
					List<Path.Node> nodes = new LinkedList<Path.Node>();
					nodes.add(new Path.Node(40, 28));
					nodes.add(new Path.Node(40, 31));
					nodes.add(new Path.Node(34, 31));
					nodes.add(new Path.Node(34, 35));
					nodes.add(new Path.Node(39, 35));
					nodes.add(new Path.Node(39, 40));
					nodes.add(new Path.Node(40, 40));
					nodes.add(new Path.Node(40, 38));
					nodes.add(new Path.Node(45, 38));
					nodes.add(new Path.Node(45, 42));
					nodes.add(new Path.Node(51, 42));
					nodes.add(new Path.Node(51, 36));
					nodes.add(new Path.Node(46, 36));
					nodes.add(new Path.Node(46, 29));
					nodes.add(new Path.Node(45, 29));
					nodes.add(new Path.Node(45, 28));
					setPath(nodes, true);
				}

				@Override
				protected void createDialog() {
					// Anna is special because she has a quest
					if (!this.getName().equals("Anna")) {
						add(
						        ConversationStates.IDLE,
						        ConversationPhrases.GREETING_MESSAGES,
						        ConversationStates.IDLE,
						        "Mummy said, we are not allowed to talk to strangers. She is worried about that lost girl. Bye.",
						        null);
					}
					addGoodbye("Bye bye!");
				}
			};
			npcs.add(npc);

			zone.assignRPObjectID(npc);
			npc.put("class", classes[i]);
			npc.set(start[i].x, start[i].y);
			npc.setDirection(Direction.DOWN);
			npc.initHP(100);
			zone.add(npc);
		}
	}
}
