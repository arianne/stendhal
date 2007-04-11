package games.stendhal.server.maps.fado.church;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


// TODO: consider splitting parts of this out into a quest class
/**
 * Creates a priest NPC who can celebrate marriages between two
 * players.
 * 
 * Note: in this class, the Player variables are called groom
 * and bride. However, the game doesn't know the concept of
 * genders. The player who initiates the wedding is just called
 * groom, the other bride.
 *   
 * @author daniel
 *
 */
public class PriestNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	private SpeakerNPC priest;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		priest = new SpeakerNPC("Priest") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to the church!");
				addJob("I am the priest.");
				//addHelp("");
				//addQuest("");
				addGoodbye("May the force be with you.");
				
			}
		};
		priest.setDescription("You see the holy Priest of Fado Church");
		npcs.add(priest);
		zone.assignRPObjectID(priest);
		priest.put("class", "priestnpc");
		priest.set(11, 4);
		priest.setDirection(Direction.DOWN);
		priest.initHP(100);
		zone.add(priest);
	}


	
}
