package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.creature.*;
import games.stendhal.server.maps.Orril.QuestDropItemOnDeath;
import games.stendhal.server.rule.defaultruleset.*;

import marauroa.common.game.IRPZone;

import java.util.List;
import java.util.LinkedList;

public class FeaturesTestArea implements IContent {
	private StendhalRPZone zone;
	private DefaultEntityManager manager;
	

	static class QuestRat extends Creature {
		public QuestRat(Creature copy) {
			super(copy);
		}

		@Override
		public void onDead(RPEntity who) {
			if (!who.isEquipped("key_golden")) {
				Item item = world.getRuleManager().getEntityManager().getItem(
						"key_golden");
				who.equip(item, true);
			}

			super.onDead(who);
		}

		@Override
		public void update() {
			noises = new LinkedList<String>(noises);
			noises.add("Thou shall not obtain the key!");
		}
	}

	static class AttackableAnimal extends Creature {
		public AttackableAnimal(Creature copy) {
			super(copy);
		}

		@Override
		public void init() {
			super.init();
			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
			zone.addPlayerAndFriends(this);
		}

		@Override
		public void onDead(RPEntity who) {
			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
			zone.removePlayerAndFriends(this);
			super.onDead(who);
		}


		@Override
		public Creature getInstance() {
			return new AttackableAnimal(this);
		}

	}
	
	public FeaturesTestArea(StendhalRPWorld world) {
		zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_pathfinding"));
		manager = (DefaultEntityManager) world
				.getRuleManager().getEntityManager();
		
		createDoorAndKey();
		attackableAnimal();
	}
	
	private void createDoorAndKey() {
		Portal portal = new Door("key_golden", "skulldoor", Direction.DOWN);
		zone.assignRPObjectID(portal);
		portal.setx(50);
		portal.sety(10);
		portal.setNumber(0);
		portal.setDestination("int_pathfinding", 1);
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(50);
		portal.sety(12);
		portal.setNumber(1);
		portal.setDestination("int_pathfinding", 0);
		zone.addPortal(portal);

		List<String> slots = new LinkedList<String>();
		slots.add("bag");

		DefaultItem item = new DefaultItem("key", "gold", "key_golden", -1);
		item.setWeight(1);
		item.setEquipableSlots(slots);
		manager.addItem(item);

		item = new DefaultItem("key", "golden", "key_silver", -1);
		item.setWeight(1);
		item.setEquipableSlots(slots);
		manager.addItem(item);

		Creature creature = new QuestRat(manager.getCreature("rat"));
		RespawnPoint point = new RespawnPoint(40, 5, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(creature.getRespawnTime());
		zone.addRespawnPoint(point);
	}
	
	
	private void attackableAnimal() {

		Creature creature = new AttackableAnimal(manager.getCreature("orc"));
		RespawnPoint point = new RespawnPoint(4, 56, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(60*60*3);
		zone.addRespawnPoint(point);

		creature = manager.getCreature("deer");
		point = new RespawnPoint(14, 56, 2);
		point.set(zone, creature, 1);
		point.setRespawnTime(60*60*3);
		zone.addRespawnPoint(point);
	}
}
