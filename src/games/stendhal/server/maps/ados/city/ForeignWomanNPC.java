package games.stendhal.server.maps.ados.city;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ForeignWomanNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Marla") {
		    
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(73, 7));
				nodes.add(new Node(73, 1));
                nodes.add(new Node(70, 1));
                nodes.add(new Node(70, 7));         
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Goeiedag");
				addHelp("Since I am living here in Ados I dont need any kind of help, but thank you.");
				
				addQuest("Relax, relax, relax."); 
				addJob("No, I am too old for working.");
				addOffer("I can offer only this nice air, it smells of the sea.");
				addGoodbye("Totsiens.");
				
			}
		};

		npc.setEntityClass("womanexoticdressnpc");
		npc.setPosition(73, 7);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
