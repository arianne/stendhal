package games.stendhal.server.maps.semos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

public class USL3_Catacombs implements ZoneConfigurator {
    private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildSemosCatacombs3Area(zone);
	}


	private void buildSemosCatacombs3Area(StendhalRPZone zone) {
	SpeakerNPC sicky = new SpeakerNPC("Markovich") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes=new LinkedList<Path.Node>();
				nodes.add(new Path.Node(61,60));
				nodes.add(new Path.Node(59,60));
				nodes.add(new Path.Node(59,58));
				nodes.add(new Path.Node(61,58));
				nodes.add(new Path.Node(59,58));
				nodes.add(new Path.Node(59,60));
			     	setPath(nodes,true);
			}

			@Override
			protected void createDialog() {
			        addGoodbye();
			    	add(ConversationStates.ATTENDING,
				                Arrays.asList("blood", "vampirette_entrails", "bat_entrails"),
						null,
						ConversationStates.ATTENDING,
				    "I need blood. I can take it from the entrails of the alive and undead. I wwill mix the bloods together for you and #fill your #goblet, if you let me drink some too. But I'm afraid of the powerful #lord.",
				    null);
				add(ConversationStates.ATTENDING,
						Arrays.asList("lord", "vampire", "skull_ring"),
						null,
						ConversationStates.ATTENDING,
						"The Vampire Lord rules these Catacombs! And I'm afraid of him. I can only help you if you kill him and bring me his skull ring with the #goblet.",
				    null);
				
				add(ConversationStates.ATTENDING,
						Arrays.asList("empty_goblet", "goblet"),
						null,
						ConversationStates.ATTENDING,
						"Only a powerful talisman like this cauldron or a special goblet should contain blood.",
						null);
			    Map<String, Integer> requiredResources = new HashMap<String, Integer>();
			    requiredResources.put("vampirette_entrails", new Integer(7));
			    requiredResources.put("bat_entrails", new Integer(7));
			    requiredResources.put("skull_ring", new Integer(1));
			    requiredResources.put("empty_goblet", new Integer(1));
			    ProducerBehaviour behaviour = new ProducerBehaviour(
						"sicky_fill_goblet", "fill", "goblet", requiredResources, 5 * 60, true);
			    addProducer(behaviour,
							"Please don't try to kill me...I'm just a sick old #vampire. Do you have any #blood I could drink? If you have an #empty_goblet I will #fill it with blood for you in my cauldron.");

			} 
		};

		sicky.setDescription("You see a sick vampire."); 
		zone.assignRPObjectID(sicky);
		sicky.put("class","sickvampirenpc");
		sicky.set(61,60); 
		sicky.initHP(10);
		zone.addNPC(sicky);
		npcs.add(sicky);
	}
}
