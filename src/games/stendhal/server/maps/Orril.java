package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.NPCOwnedChest;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.LockedDoor;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.entity.spawner.CreatureRespawnPoint;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.rule.defaultruleset.DefaultItem;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;


public class Orril implements IContent {

	private NPCList npcs;
	
	private ShopList shops;

	public Orril() {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();
		
		StendhalRPWorld world = StendhalRPWorld.get();

		buildJynathHouseArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_orril_jynath_house")));
		buildDwarfMineArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"-2_orril_dwarf_mine")));
		buildDwarfSmithArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"-3_orril_dwarf_blacksmith")));
		buildCastleArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_orril_castle")));
		buildCastleInsideArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_orril_castle_0")));
		buildCastleDungeonArea();
		buildCampfireArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"0_orril_river_s")));
	}

	private void buildCastleDungeonArea() {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"-1_orril_castle_w"));

		DefaultEntityManager manager = (DefaultEntityManager) world
				.getRuleManager().getEntityManager();
		DefaultItem item = new DefaultItem("key", "silver",
				"dungeon_silver_key", -1);
		item.setWeight(1);
		List<String> bagOnly = new LinkedList<String>();
		bagOnly.add("bag");
		item.setEquipableSlots(bagOnly);
		manager.addItem(item);

		Creature creature = new ItemGuardCreature(manager
				.getCreature("green_dragon"), "dungeon_silver_key");
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 69, 43, creature, 1);
		zone.addRespawnPoint(point);

		Portal door = new LockedDoor("dungeon_silver_key", "skulldoor", Direction.DOWN);
		zone.assignRPObjectID(door);
		door.set(69, 37);
		door.setNumber(0);
		door.setDestination("-2_orril_lich_palace", 0);
		zone.addPortal(door);

		zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"-2_orril_lich_palace"));

		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.set(70, 38);
		portal.setNumber(0);
		portal.setDestination("-1_orril_castle_w", 0);
		zone.addPortal(portal);

		item = new DefaultItem("key", "gold", "lich_gold_key", -1);
		item.setWeight(1);
		item.setEquipableSlots(bagOnly);
		manager.addItem(item);

		creature = new ItemGuardCreature(manager.getCreature("royal_mummy"),
				"lich_gold_key");
		point = new CreatureRespawnPoint(zone, 54, 48, creature, 1);
		zone.addRespawnPoint(point);

		door = new LockedDoor("lich_gold_key", "skulldoor", Direction.UP);
		zone.assignRPObjectID(door);
		door.set(54, 52);
		door.setNumber(1);
		door.setDestination("-2_orril_lich_palace", 2);
		zone.addPortal(door);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.set(54, 57);
		portal.setNumber(2);
		portal.setDestination("-2_orril_lich_palace", 1);
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.set(55, 57);
		portal.setNumber(3);
		portal.setDestination("-2_orril_lich_palace", 1);
		zone.addPortal(portal);
	}

	private void buildCastleInsideArea(StendhalRPZone zone) {
		for (int i = 0; i < 3; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(26 + i);
			portal.setY(62);
			portal.setNumber(i);
			portal.setDestination("0_orril_castle", 11);
			zone.addPortal(portal);
		}

		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(8);
		portal.setY(1);
		portal.setNumber(4);
		portal.setDestination("-1_orril_castle", 1);
		zone.addPortal(portal);

		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"-1_orril_castle"));
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(19);
		portal.setY(22);
		portal.setNumber(0);
		portal.setDestination("int_orril_castle_0", 4);
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(20);
		portal.setY(22);
		portal.setNumber(1);
		portal.setDestination("int_orril_castle_0", 4);
		zone.addPortal(portal);
	}

	private void buildCastleArea(StendhalRPZone zone) {
		for (int i = 0; i < 5; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(60 + i);
			portal.setY(96);
			portal.setNumber(i);
			portal.setDestination("0_orril_castle", 5 + i);
			zone.addPortal(portal);

			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(60 + i);
			portal.setY(93);
			portal.setNumber(5 + i);
			portal.setDestination("0_orril_castle", i);
			zone.addPortal(portal);
		}

		for (int i = 0; i < 3; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(61 + i);
			portal.setY(72);
			portal.setNumber(10 + i);
			portal.setDestination("int_orril_castle_0", 1);
			zone.addPortal(portal);
		}
	}

	private void buildJynathHouseArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(16);
		portal.setY(30);
		portal.setNumber(0);
		portal.setDestination("0_orril_river_s", 0);
		zone.addPortal(portal);

		SpeakerNPC npc = new SpeakerNPC("Jynath") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(24, 6));
				nodes.add(new Path.Node(21, 6));
				nodes.add(new Path.Node(21, 8));
				nodes.add(new Path.Node(15, 8));
				nodes.add(new Path.Node(15, 11));
				nodes.add(new Path.Node(13, 11));
				nodes.add(new Path.Node(13, 26));
				nodes.add(new Path.Node(22, 26));
				nodes.add(new Path.Node(13, 26));
				nodes.add(new Path.Node(13, 11));
				nodes.add(new Path.Node(15, 11));
				nodes.add(new Path.Node(15, 8));
				nodes.add(new Path.Node(21, 8));
				nodes.add(new Path.Node(21, 6));
				nodes.add(new Path.Node(24, 6));
			setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm a witch, since you ask.");
				/* addHelp("You may want to buy some potions or do some #task for me."); */
				addHelp("I can #heal you");
				addHealer(200);
				addGoodbye();
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "witchnpc");
		npc.set(24, 6);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildDwarfMineArea(StendhalRPZone zone) {
		// NOTE: This is a female character ;)
		SpeakerNPC loretta = new SpeakerNPC("Loretta") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(49, 67));
				nodes.add(new Path.Node(45, 67));
				nodes.add(new Path.Node(45, 71));
				nodes.add(new Path.Node(45, 67));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm the supervisor responsible for maintaining the mine-cart rails in this mine. But, ironically, we ran out of cast iron to fix them with! Maybe you can #offer me some?");
				addHelp("If you want some good advice, you'll not go further south; there's an evil dragon living down there!");
				addBuyer(new BuyerBehaviour(shops.get("buyiron")), true);
				addGoodbye("Farewell - and be careful: the other dwarves don't like strangers running around here!");
			}
		};
		npcs.add(loretta);

		loretta.setDescription("You see Loretta, an elderly female dwarf. She is working on the mine-cart rails.");
		zone.assignRPObjectID(loretta);
		loretta.put("class", "greendwarfnpc");
		loretta.set(49, 67);
		loretta.initHP(100);
		zone.addNPC(loretta);
	}

	private void buildDwarfSmithArea(StendhalRPZone zone) {
		SpeakerNPC hogart = new SpeakerNPC("Hogart") {
			protected void createPath() {
				List<Path.Node> nodes=new LinkedList<Path.Node>();
				nodes.add(new Path.Node(12,10));
				nodes.add(new Path.Node(20,10));
				nodes.add(new Path.Node(20,8));
				nodes.add(new Path.Node(20,11));
				nodes.add(new Path.Node(12,11));
				nodes.add(new Path.Node(12,6));
				nodes.add(new Path.Node(12,10));
				setPath(nodes,true);
			}

			protected void createDialog() {
				//addGreeting();
				addJob("I am a master blacksmith. I used to forge weapons in secret for the dwarves in the mine, but they have forgotten me and my #stories.");
				addHelp("I could tell you a #story...");	
				add(ConversationStates.ATTENDING, Arrays.asList("story", "stories"), ConversationStates.ATTENDING, "I expect a scruff like you has never heard of Lady Tembells, huh? She was so beautiful. She died young and her distraught husband asked a powerful Lord to bring her back to life. The fool didn't get what he bargained for, she became a #vampire.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("vampire"), ConversationStates.ATTENDING, "The husband had hired the help of a Vampire Lord! The Lady became his Vampire Bride and her maids became vampiresses. The Catacombs of North Semos are a deadly place now.", null);
				addGoodbye("So long. I bet you won't sleep so well tonight.");
			} //remaining behaviour defined in maps.quests.VampireSword
		};

		hogart.setDescription("You see Hogart, a retired master dwarf smith."); 
		zone.assignRPObjectID(hogart);
		hogart.put("class","olddwarfnpc");
		hogart.set(12,10); 
		hogart.initHP(100);
		zone.addNPC(hogart);    
	} 

	private void buildCampfireArea(StendhalRPZone zone) {
		// create portal to Jynath' house (which is on the same
		// map as the campfire by accident)
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(39);
		portal.setY(5);
		portal.setNumber(0);
		portal.setDestination("int_orril_jynath_house", 0);
		zone.addPortal(portal);

		SpeakerNPC npc = new SpeakerNPC("Sally") {
			@Override
			protected void createPath() {
				// NPC does not move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				//addGreeting();
				addJob("Work? I'm just a little girl! I'm a scout, you know.");
				addHelp("You can find lots of useful stuff in the forest; wood and mushrooms, for example. But beware, some mushrooms are poisonous!");
				addGoodbye();
				// remaining behaviour is defined in maps.quests.Campfire.				
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "littlegirlnpc");
		npc.set(40, 60);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.addNPC(npc);

		Chest chest = new NPCOwnedChest(npc);
		zone.assignRPObjectID(chest);
		chest.set(37, 60);
		zone.add(chest);
	}

}
