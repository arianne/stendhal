package games.stendhal.server.maps.kalavan.cottage_2;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

/**
 * Provides Martha, the apple pies confectioner NPC.
 * She has a twin sister: Gertha, the cherry pies confectioner NPC.
 *
 * @author omero
 */
public class ConfectionerApplePieNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Martha") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 3));
				nodes.add(new Node(3, 13));
				nodes.add(new Node(10, 13));
				nodes.add(new Node(10, 11));
				nodes.add(new Node(12, 11));
				nodes.add(new Node(12, 13));
				nodes.add(new Node(10, 13));
				nodes.add(new Node(10, 11));
				nodes.add(new Node(3, 11));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {

				addJob("I live here with my twin sister #Gertha and our passion is to bake delicious fruit pies!");

				addReply("gertha",
					"She's my twin sister and we live here together... Like me, she also likes to #bake fruit pies!");
				addReply("honey",
					"You should look for the local beekeeper a little north and then west of here...");
				addReply("milk",
					"Perhaps you should visit some farm where you see they have cows...");
				addReply("flour",
					"Ahh... I get all my flour at the mill just north of Semos city!");
				addReply("egg",
					"Find some hens and you'll easily find some eggs, too!");
				addReply("apple",
					"Mmm... When once in a while I travel from Semos to Ados, I always stop at the orchard near a farm along the road...");


				addHelp("If it would help, I could #bake an apple pie for you!");

				addOffer("I'd love to #bake a delicious apple pie for you. Just ask me!");

                /** this is a teaser for a quest not yet available */
				addQuest("Right now I'm perfecting my apple pie recipe. But in future I might want to try something new - I'll let you know.");

				addGoodbye("Be careful out there!");

				// (uses sorted TreeMap instead of HashMap)
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("flour", Integer.valueOf(2));
				requiredResources.put("honey", Integer.valueOf(1));
				requiredResources.put("milk", Integer.valueOf(1));
				requiredResources.put("egg", Integer.valueOf(1));
				requiredResources.put("apple", Integer.valueOf(1));

				final ProducerBehaviour behaviour = new ProducerBehaviour("martha_bake_applepie", "bake", "apple pie",
				        requiredResources, 15 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				        "Hello! Did you come to taste one of my delicious apple pies? I could #bake one for you right away!");
			}
		};

		npc.setEntityClass("confectionerapplepienpc");
		npc.setDirection(Direction.DOWN);
		npc.setPosition(4, 3);
		npc.initHP(100);
		npc.setDescription("You see Martha. She loves to bake apple pies for her guests.");
		zone.add(npc);
	}
}
