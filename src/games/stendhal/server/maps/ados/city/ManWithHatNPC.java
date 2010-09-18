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

/**
 * Builds a sad NPC (name: Andy) who lost his wife
 * 
 * @author Erdnuggel (idea) and Vanessa Julius (implemented)
 * 
 */

public class ManWithHatNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Andy") {
		    
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(41, 6));
				nodes.add(new Node(41, 11));
                nodes.add(new Node(64, 11));
                nodes.add(new Node(64, 6));  
                nodes.add(new Node(63, 6));
                nodes.add(new Node(63, 10)); 
                nodes.add(new Node(42, 10)); 
                nodes.add(new Node(42, 6));
                nodes.add(new Node(41, 6)); 
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Sniff!");
				addHelp("When I lived together with my beloved wife, we used to travell a lot. We loved the beach on Athor Island! *Sigh* These memories are making me even more sad...Please, leave me alone now...");
				addQuest("I am too old and too sad to think about jobs for others...*sniff-sniff*"); 
				addJob("*Cry* I had to stop working after my wife died...");
				addOffer("Nothing, I lost my darling!");
				addGoodbye("Buuaaahaaaahaa!");
				
			}
		};

		npc.setDescription("You see a man with a hat. His name is Andy and he looks really sad.");
		npc.setEntityClass("manwithhatnpc");
		npc.setPosition(41, 6);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
