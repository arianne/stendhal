package games.stendhal.server.maps.semos.house;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.entity.npc.TeleporterBehaviour;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Flower Seller NPC for the Elf Princess quest
 *
 * @author kymara
 */
public class FlowerSellerNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
        	
        	new TeleporterBehaviour(buildSemosHouseArea(zone), "Flowers! Get your fresh flowers here!");
	}

	private SpeakerNPC buildSemosHouseArea(StendhalRPZone zone) {

	    SpeakerNPC rose = new SpeakerNPC("Rose Leigh"){
	                @Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}
	                @Override
			protected void createDialog() {
			    addJob("I'm a wandering flower woman.");
			    addGoodbye("Everything's coming up roses ... bye ...");
			    // the rest is in the ElfPrincess quest
			}

		};
		npcs.add(rose);
		rose.put("class", "gypsywomannpc");
		rose.initHP(100);

		// start in int_semos_house
		zone = StendhalRPWorld.get().getZone("int_semos_house");
		zone.assignRPObjectID(rose);
		rose.set(5, 5);
		zone.add(rose);

		return rose;
	}
	//@Override  <--- this indicates the method exists in the super class which it doesnt
//	public void addToWorld() {
//		//super.addToWorld(); cannot be called from super as super doe not have the class  
//		buildSemosHouseArea();
//		
//	}

}
