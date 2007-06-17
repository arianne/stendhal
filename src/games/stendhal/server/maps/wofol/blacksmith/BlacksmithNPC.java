package games.stendhal.server.maps.wofol.blacksmith;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configure Wofol Blacksmith (-1_semos_mine_nw).
 * 
 * @author kymara
 */
public class BlacksmithNPC implements ZoneConfigurator {

	private NPCList npcs;

	public BlacksmithNPC() {
		this.npcs = NPCList.get();
	}

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
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(22, 7));
				nodes.add(new Path.Node(22, 6));
				nodes.add(new Path.Node(17, 6));
				nodes.add(new Path.Node(17, 1));
				nodes.add(new Path.Node(8, 1));
				nodes.add(new Path.Node(8, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				//addGreeting("How did you get down here? I usually only see #kobolds.");
				addJob("I am a blacksmith. I was a mountain dwarf but I left that lot behind me. Good riddance, I say!");
				addHelp("Watch out for the house with the green crystals outside. A powerful undead lich lives there.");
				addReply("offer","#Wrvil is the one who runs a shop, not me.");
				addReply("kobolds","You know, those odd furry creatures. Don't get much conversation out of any except #Wrvil.");
				addReply("Wrvil","He runs a trading business not far from here. I used to make the odd item for him, but don't have any energy left.");
				addGoodbye();
			} //remaining behaviour defined in maps.quests.ObsidianKnife
		};

		dwarf.setDescription("You see Alrak, a reclusive dwarf smith.");
		zone.assignRPObjectID(dwarf);
		dwarf.put("class", "dwarfnpc");
		dwarf.set(22, 7);
		dwarf.initHP(100);
		npcs.add(dwarf);
		zone.add(dwarf);
	}
}
