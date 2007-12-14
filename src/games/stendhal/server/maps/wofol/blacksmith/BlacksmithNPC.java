package games.stendhal.server.maps.wofol.blacksmith;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configure Wofol Blacksmith (-1_semos_mine_nw).
 *
 * @author kymara
 */
public class BlacksmithNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildBlacksmith(zone);
	}

	private void buildBlacksmith(StendhalRPZone zone) {
		SpeakerNPC dwarf = new SpeakerNPC("Alrak") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(22, 8));
				nodes.add(new Node(22, 7));
				nodes.add(new Node(17, 7));
				nodes.add(new Node(17, 2));
				nodes.add(new Node(8, 2));
				nodes.add(new Node(8, 8));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				//addGreeting("How did you get down here? I usually only see #kobolds.");
				addJob("I am a blacksmith. I was a mountain dwarf but I left that lot behind me. Good riddance, I say!");
				addHelp("I've heard rumours of a fearsome creature living below these mines, and his small minions, evil imps. I wouldn't go down there even to look, if I were you. It's very dangerous.");
				addOffer("#Wrvil is the one who runs a shop, not me.");
				addReply("kobolds", "You know, those odd furry creatures. Don't get much conversation out of any except #Wrvil.");
				addReply("Wrvil", "He runs a trading business not far from here. I used to make the odd item for him, but don't have any energy left.");
				addGoodbye();
			} //remaining behaviour defined in maps.quests.ObsidianKnife
		};

		dwarf.setDescription("You see Alrak, a reclusive dwarf smith.");
		dwarf.setEntityClass("dwarfnpc");
		dwarf.setPosition(22, 8);
		dwarf.initHP(100);
		zone.add(dwarf);
	}
}
