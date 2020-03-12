package games.stendhal.server.maps.deniran.caves;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SorcererNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}
	
    private void buildNPC(final StendhalRPZone zone) {
	    final SpeakerNPC npc = new SpeakerNPC("Rer Ecros") {
	    	protected void createPath() {
	    		List<Node> nodes=new LinkedList<Node>();
	            nodes.add(new Node(45,93));
	            nodes.add(new Node(52,99));
	            setPath(new FixedPath(nodes,true));
	        }
	
	        protected void createDialog() {
	            // Lets the NPC reply with "Hallo" when a player greets him. But we could have set a custom greeting inside the ()
	            addGreeting("Ah, you survived?");
	            // Lets the NPC reply when a player says "job"
	            addJob("I am a sorcerer experimenting with the powers of #magical animals");
	            // Lets the NPC reply when a player asks for help
	            addHelp("I could make you stronger if you bring me some items, but first I have to set up my lab. Please return later.");
	            // respond about a special trigger word
	            addReply("magical","Right now my experiments involve unicorns and pegasuses.");
	            // use standard goodbye, but you can also set one inside the ()
	            addGoodbye("Don't expect to live too long in these caves.");
	        }
	    };

    // This determines how the NPC will look like. welcomernpc.png is a picture in data/sprites/npc/
    npc.setEntityClass("chaos_sorcerornpc");
    // set a description for when a player does 'Look'
    npc.setDescription("You see Rer Ecros, pacing through his underground lab.");
    // Set the initial position to be the first node on the Path you defined above.
    npc.setPosition(45, 93);
    npc.initHP(100);

    zone.add(npc);   
}
	
}
