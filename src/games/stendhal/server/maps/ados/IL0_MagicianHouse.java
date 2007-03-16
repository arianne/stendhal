package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.entity.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IL0_MagicianHouse implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();;
	private ShopList shops = ShopList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildMagicianHouseArea(zone, attributes);
	}


	private void buildMagicianHouseArea(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Haizen") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(7, 1));
				nodes.add(new Path.Node(7, 3));
				nodes.add(new Path.Node(13, 3));
				nodes.add(new Path.Node(13, 8));
				nodes.add(new Path.Node(9, 8));
				nodes.add(new Path.Node(9, 7));
				nodes.add(new Path.Node(9, 8));
				nodes.add(new Path.Node(2, 8));
				nodes.add(new Path.Node(2, 2));
				nodes.add(new Path.Node(7, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am a wizard who sells #magic #scrolls. Just ask me for an #offer!");
				addHelp("You can take powerful magic with you on your adventures with the aid of my #magic #scrolls!");

				addSeller(new SellerBehaviour(shops.get("scrolls")));
				
				add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I don't have any tasks for you right now. If you need anything from me, just ask.",
					null);
				add(ConversationStates.ATTENDING,
					Arrays.asList("magic", "scroll", "scrolls"),
					null,
					ConversationStates.ATTENDING,
					"I #offer scrolls that help you to travel faster: #home scrolls and the #markable #empty scrolls. For the more advanced customer, I also have #summon scrolls!",
					null);
				add(ConversationStates.ATTENDING,
					Arrays.asList("home", "home_scroll"),
					null,
					ConversationStates.ATTENDING,
					"Home scrolls take you home immediately, a good way to escape danger!",
					null);
				add(ConversationStates.ATTENDING,
					Arrays.asList("empty", "marked", "empty_scroll", "markable", "marked_scroll"),
					null,
					ConversationStates.ATTENDING,
					"Empty scrolls are used to mark a position. Those marked scrolls can take you back to that position. They are a little expensive, though.",
					null);
				add(ConversationStates.ATTENDING,
					"summon",
					null,
					ConversationStates.ATTENDING,
					"A summon scroll empowers you to summon animals to you; advanced magicians will be able to summon stronger monsters than others. Of course, these scrolls can be dangerous if misused.",
					null);
			
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "wisemannpc");
		npc.set(7, 1);
		npc.initHP(100);
		zone.addNPC(npc);

		// Summon scroll
		Item item = addPersistentItem("summon_scroll", zone, 7, 6);
		item.put("infostring", "red_dragon");

		// Plant grower for poison
		PassiveEntityRespawnPoint plantGrower = new PassiveEntityRespawnPoint("poison", 1500);
		zone.assignRPObjectID(plantGrower);
		plantGrower.setX(3);
		plantGrower.setY(6);
		plantGrower.setDescription("Haizen tends to put his magic drinks here.");
		plantGrower.setToFullGrowth();
		
		zone.add(plantGrower);
		StendhalRPRuleProcessor.get().getPlantGrowers().add(plantGrower);
	}
	
	private Item addPersistentItem(String name, StendhalRPZone zone, int x, int y) {
		Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(name);
		zone.assignRPObjectID(item);
		item.setX(x);
		item.setY(y);
		item.put("persistent", 1);
		zone.add(item);
		return item;
	}
}
