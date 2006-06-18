package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.server.RespawnPoint;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Door;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.rule.defaultruleset.DefaultItem;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;

import org.apache.log4j.Logger;

public class Orril implements IContent {
	private StendhalRPWorld world;

	private NPCList npcs;

	public Orril(StendhalRPWorld world) {
		this.npcs = NPCList.get();
		this.world = world;

		buildJynathHouseArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_orril_jynath_house")));

		buildCastleArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_orril_castle")));
		buildCastleInsideArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_orril_castle_0")));
		buildCastleDungeonArea();
		buildCampfireArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"0_orril_river_s")));
	}

	public static class QuestDropItemOnDeath extends Creature {
		  /** the logger instance. */
		  private static final Logger logger = Log4J.getLogger(QuestDropItemOnDeath.class);

		  private String itemType;

		public QuestDropItemOnDeath(Creature copy, String itemType) {
			super(copy);
			this.itemType = itemType;

			noises = new LinkedList<String>(noises);
			noises.add("Thou shall not obtain the "
					+ itemType.replace("_", " ") + "!");

			if (!world.getRuleManager().getEntityManager().isItem(itemType)) {
				logger.error(copy.getName() + " drops unexisting item "
						+ itemType);
			}
		}

		@Override
		public Creature getInstance() {
			return new QuestDropItemOnDeath(this, itemType);
		}

		@Override
		public void onDead(RPEntity who) {
			if (!who.isEquipped(itemType)) {
				Item item = world.getRuleManager().getEntityManager().getItem(
						itemType);
				if (!who.equip(item)) {
					StendhalRPZone zone = (StendhalRPZone) world.getRPZone(who
							.getID());

					zone.assignRPObjectID(item);
					item.setx(who.getx());
					item.sety(who.gety());
					zone.add(item);
				}
			}
			super.onDead(who);
		}
	}

	private void buildCastleDungeonArea() {
		DefaultEntityManager manager = (DefaultEntityManager) world
				.getRuleManager().getEntityManager();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"-1_orril_castle_w"));

		List<String> slots = new LinkedList<String>();
		slots.add("bag");

		DefaultItem item = new DefaultItem("key", "silver",
				"dungeon_silver_key", -1);
		item.setWeight(1);
		item.setEquipableSlots(slots);
		manager.addItem(item);

		Creature creature = new QuestDropItemOnDeath(manager
				.getCreature("green_dragon"), "dungeon_silver_key");
		RespawnPoint point = new RespawnPoint(69, 43, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(creature.getRespawnTime());
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
		item.setEquipableSlots(slots);
		manager.addItem(item);

		creature = new QuestDropItemOnDeath(manager.getCreature("royal_mummy"),
				"lich_gold_key");
		point = new RespawnPoint(54, 48, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(creature.getRespawnTime());
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
