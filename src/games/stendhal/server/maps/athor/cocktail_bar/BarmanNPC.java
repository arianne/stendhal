package games.stendhal.server.maps.athor.cocktail_bar;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Cocktail Bar at the Athor island beach (Inside / Level 0)
 *
 * @author kymara
 */
public class BarmanNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildBar(zone, attributes);
	}

	private void buildBar(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC barman = new SpeakerNPC("Pedro") {

			@Override
			protected void createPath() {
			        List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(8, 5));
				nodes.add(new Node(11, 5));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I #mix cocktails!");
				addQuest("What you say?");
				addHelp("You want a pina colada mixed, I'm your man!");
				addGoodbye("Cheers!");

				// make cocktail!
				Map<String, Integer> requiredResources = new TreeMap<String, Integer>();	// use sorted TreeMap instead of HashMap
				requiredResources.put("coconut", 1);
				requiredResources.put("pineapple", 1);
				ProducerBehaviour mixerBehaviour = new ProducerBehaviour("barman_mix_pina",
						"mix", "pina_colada", requiredResources, 2 * 60);
				new ProducerAdder().addProducer(this, mixerBehaviour, "Aloha!");

			}
		};

		barman.setEntityClass("barmannpc");
		barman.setPosition(8, 5);
		barman.initHP(100);
		zone.add(barman);
	}
}
