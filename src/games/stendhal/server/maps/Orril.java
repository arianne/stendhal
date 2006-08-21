package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.server.RespawnPoint;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.Door;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.rule.defaultruleset.DefaultItem;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;


public class Orril implements IContent {
	private StendhalRPWorld world;

	private NPCList npcs;
	
	private ShopList shops;

	public Orril(StendhalRPWorld world) {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();
		this.world = world;

		buildJynathHouseArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_orril_jynath_house")));
		buildDwarfMineArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"-2_orril_dwarf_mine")));
		buildCastleArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_orril_castle")));
		buildCastleInsideArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_orril_castle_0")));
		buildCastleDungeonArea();
		buildCampfireArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"0_orril_river_s")));
	}

	private void buildCastleDungeonArea() {
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
		RespawnPoint point = new RespawnPoint(zone, 69, 43, creature, 1);
		zone.addRespawnPoint(point);

		Door door = new Door("dungeon_silver_key", "skulldoor", Direction.DOWN);
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
		point = new RespawnPoint(zone, 54, 48, creature, 1);
		zone.addRespawnPoint(point);

		door = new Door("lich_gold_key", "skulldoor", Direction.UP);
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
			portal.setx(26 + i);
			portal.sety(62);
			portal.setNumber(i);
			portal.setDestination("0_orril_castle", 11);
			zone.addPortal(portal);
		}

		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(8);
		portal.sety(1);
		portal.setNumber(4);
		portal.setDestination("-1_orril_castle", 1);
		zone.addPortal(portal);

		zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"-1_orril_castle"));
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(19);
		portal.sety(22);
		portal.setNumber(0);
		portal.setDestination("int_orril_castle_0", 4);
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(20);
		portal.sety(22);
		portal.setNumber(1);
		portal.setDestination("int_orril_castle_0", 4);
		zone.addPortal(portal);
	}

	private void buildCastleArea(StendhalRPZone zone) {
		for (int i = 0; i < 5; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setx(60 + i);
			portal.sety(96);
			portal.setNumber(i);
			portal.setDestination("0_orril_castle", 5 + i);
			zone.addPortal(portal);

			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setx(60 + i);
			portal.sety(93);
			portal.setNumber(5 + i);
			portal.setDestination("0_orril_castle", i);
			zone.addPortal(portal);
		}

		for (int i = 0; i < 3; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setx(61 + i);
			portal.sety(72);
			portal.setNumber(10 + i);
			portal.setDestination("int_orril_castle_0", 1);
			zone.addPortal(portal);
		}
	}

	private void buildJynathHouseArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(16);
		portal.sety(30);
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
				addJob("*Do you really want to know?* I am a witch");
				addHelp(//"You may want to buy some potions or do some #task for me.");
						"I can #heal you.");
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
				addJob("I'm responsible for the lorry rails in this mine. But, ironically, we ran out of cast iron. Maybe you can #offer me some?");
				addHelp("Do you want a good advise? Don't go further southwards! An evil dragon is living there!");
				addBuyer(new BuyerBehaviour(world, shops.get("buyiron")), true);
				addGoodbye("Farewell - and be careful: the other dwarves don't like strangers running around here!");
			}
		};
		npcs.add(loretta);

		loretta.setDescription("You see Loretta, an elder dwarf lady. She is working on the lorry rails.");
		zone.assignRPObjectID(loretta);
		loretta.put("class", "greendwarfnpc");
		loretta.set(49, 67);
		loretta.initHP(100);
		zone.addNPC(loretta);
	}
	
	private void buildCampfireArea(StendhalRPZone zone) {
		// create portal to Jynath' house (which is on the same
		// map as the campfire by accident)
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(39);
		portal.sety(5);
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
				addJob("Work? I'm just a little girl! I'm a scout.");
				addHelp("You can find lots of useful stuff in the woods, for example wood and mushrooms. But beware, some are poisonous!");
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
	}

}
