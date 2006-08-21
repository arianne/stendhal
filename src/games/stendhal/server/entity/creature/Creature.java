/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature;

import games.stendhal.common.Debug;
import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.RespawnPoint;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.EntityManager;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Serverside representation of a creature.
 * <p>
 * A creature is defined as an entity which can move with certain speed, has
 * life points (HP) and can die.
 * <p>
 * Not all creatures have to be hostile, but at the moment the default behavior
 * is to attack the player.
 * <p>
 * The ai
 */
public class Creature extends NPC {
	public static class DropItem {
		public String name;

		public double probability;

		public int min;

		public int max;

		public DropItem(String name, double probability, int min, int max) {
			this.name = name;
			this.probability = probability;
			this.min = min;
			this.max = max;
		}

		public DropItem(String name, double probability, int amount) {
			this.name = name;
			this.probability = probability;
			this.min = amount;
			this.max = amount;
		}
	}

	public static class EquipItem {
		public String slot;

		public String name;

		public int quantity;

		public EquipItem(String slot, String name, int amount) {
			this.slot = slot;
			this.name = name;
			this.quantity = amount;
		}
	}

	/** Enum classifying the possible (AI) states a creature can be in */
	private enum AiState {
		/** sleeping as there is no enemy in sight */
		SLEEP,
		/** doin' nothing */
		IDLE,
		/** patroling, watching for an enemy */
		PATROL,
		/** moving towards a moving target */
		APPROACHING_MOVING_TARGET,
		/** moving towards a stopped target */
		APPROACHING_STOPPED_TARGET,
		/** attacking */
		ATTACKING;
	}

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Creature.class);

	/**
	 * the number of rounds the creature should wait when the path to the target
	 * is blocked and the target is not moving
	 */
	protected static final int WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED = 9;

	private RespawnPoint point;

	private List<Path.Node> patrolPath;

	private RPEntity target;

	/** the number of rounds to wait for a path the target */
	private int waitRounds;

	/** the current (logic)state */
	protected AiState aiState;

	/** the speed of this creature */
	private double speed;

	private int attackTurn;

	/** size in width of a tile */
	private int width;

	private int height;

	/** Ths list of item names this creature may drop 
     *  Note; per default this list is shared with all creatures
     *  of that class*/
	protected List<Creature.DropItem> dropsItems;

    /** Ths list of item instances this creature may drop for
     *  use in quests. This is always creature specific */
    protected List<Item> dropItemInstances;
    
	protected List<String> noises;

	private int respawnTime;

	private Map<String, String> aiProfiles;

	public static void generateRPClass() {
		try {
			RPClass npc = new RPClass("creature");
			npc.isA("npc");
			npc.add("debug", RPClass.VERY_LONG_STRING, RPClass.VOLATILE);
		} catch (RPClass.SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public Creature(RPObject object) throws AttributeNotFoundException {
		super(object);

		put("type", "creature");
		put("title_type", "enemy");
		if (object.has("title_type")) {
			put("title_type", object.get("title_type"));
		}
		createPath();

		dropsItems = new ArrayList<Creature.DropItem>();
		dropItemInstances = new ArrayList<Item>();
		aiProfiles = new HashMap<String, String>();
		attackTurn = Rand.rand(5);
	}

	public Creature(Creature copy) {
		this();

		this.speed = copy.speed;
		this.width = copy.width;
		this.height = copy.height;

         /** Creatures created with this function will share their
         *  dropsItems with any other creature of that kind. If you want
         *  individual dropsItems, use clearDropItemList first!
         */
		if (copy.dropsItems != null) {
			this.dropsItems = copy.dropsItems;
		}
        // this.dropItemInstances is ignored;
        
		this.aiProfiles = copy.aiProfiles;
		this.noises = copy.noises;

		this.respawnTime = copy.respawnTime;

		put("class", copy.get("class"));
		put("subclass", copy.get("subclass"));
		put("name", copy.get("name"));

		put("x", 0);
		put("y", 0);
		setDescription(copy.getDescription());
		setATK(copy.getATK());
		setDEF(copy.getDEF());
		setXP(copy.getXP());
		initHP(copy.getBaseHP());
		setName(copy.getName());

		setLevel(copy.getLevel());

		update();

		stop();
		attackTurn = Rand.rand(5);
		logger.debug("Created " + get("class") + ":" + this);
	}

	public Creature getInstance() {
		return new Creature(this);
	}

	/**
	 * creates a new creature without properties. These must be set in the
	 * deriving class
	 */
	public Creature() throws AttributeNotFoundException {
		super();
		put("type", "creature");
		put("title_type", "enemy");

		createPath();

		dropsItems = new ArrayList<Creature.DropItem>();
		dropItemInstances = new ArrayList<Item>();
		aiProfiles = new HashMap<String, String>();
		attackTurn = Rand.rand(5);
	}

	/**
	 * Creates a new creature with the given properties.
	 * @param clazz The creature's class, e.g. "golem"
	 * @param subclass The creature's subclass, e.g. "wooden_golem"
	 * @param name Typically the same as clazz, except for NPCs
	 * @param hp The creature's maximum health points
	 * @param attack The creature's attack strength
	 * @param defense The creature's attack strength
	 * @param level The creature's level
	 * @param xp The creature's experience
	 * @param width The creature's width, in squares
	 * @param height The creature's height, in squares
	 * @param speed
	 * @param dropItems
	 * @param aiProfiles
	 * @param noises
	 * @param respawnTime
	 * @param description
	 * @throws AttributeNotFoundException
	 */
	public Creature(String clazz, String subclass, String name, int hp,
			int attack, int defense, int level, int xp, int width, int height,
			double speed, List<DropItem> dropItems,
			Map<String, String> aiProfiles, List<String> noises,
			int respawnTime, String description)
			throws AttributeNotFoundException {
		this();

		this.speed = speed;
		this.width = width;
		this.height = height;

        /** Creatures created with this function will share their
         *  dropItems with any other creature of that kind. If you want
         *  individual dropItems, use clearDropItemList first!
         */
		if (dropItems != null) {
			this.dropsItems = dropItems;
		}
        // this.dropItemInstances is ignored;

		this.aiProfiles = aiProfiles;
		this.noises = noises;

		this.respawnTime = respawnTime;

		put("class", clazz);
		put("subclass", subclass);
		put("name", name);

		put("x", 0);
		put("y", 0);
		setDescription(description);
		setATK(attack);
		setDEF(defense);
		setXP(xp);
		setBaseHP(hp);
		setHP(hp);

		setLevel(level);

		update();

		stop();
		attackTurn = Rand.rand(5);
		logger.debug("Created " + clazz + ":" + this);
	}

	protected void createPath() {
		/** TODO: Create paths in other way */
		patrolPath = new LinkedList<Path.Node>();
		patrolPath.add(new Path.Node(0, 0));
		patrolPath.add(new Path.Node(-6, 0));
		patrolPath.add(new Path.Node(-6, 6));
		patrolPath.add(new Path.Node(0, 6));
		aiState = AiState.IDLE;
	}

	public void setRespawnPoint(RespawnPoint point) {
		this.point = point;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public RespawnPoint getRespawnPoint() {
		return point;
	}
    
    /**
     *  clears the list of predefined dropItems and creates
     *  an empty list specific to this creature
     *
     */
    public void clearDropItemList() {
        dropsItems = new ArrayList<Creature.DropItem>();
        dropItemInstances.clear();
    }

    /**
     *  adds a named item to the List of Items that will be dropped on dead
     *  if clearDropItemList hasn't been called first, this will change
     *  all creatures of this kind
     */
    public void addDropItem(String name, double probability, int min, int max) {
        dropsItems.add(new DropItem(name, probability, min, max));
    }
    
    /**
     *  adds a named item to the List of Items that will be dropped on dead
     *  if clearDropItemList hasn't been called first, this will change
     *  all creatures of this kind
     */
    public void addDropItem(String name, double probability, int amount) {
        dropsItems.add(new DropItem(name, probability, amount));
    }
    
    /**
     * adds a specific item to the List of Items that will be dropped on dead
     * with 100 % probability. this is always for that specific creature only.
     * @param item
     */
    public void addDropItem(Item item) {
        dropItemInstances.add(item);
    }

	@Override
    public void onDead(Entity killer) {
		if (point != null) {
			point.notifyDead(this);
		} else {
			// Perhaps a summoned creature
			rp.removeNPC(this);
		}

		super.onDead(killer);
	}

    @Override
	protected void dropItemsOn(Corpse corpse) {
        for (Item item : dropItemInstances) {
            corpse.add(item);
            if (corpse.isFull())
                break;
        }
        
		for (Item item : createDroppedItems(world.getRuleManager().getEntityManager())) {
			corpse.add(item);

			if (corpse.isFull())
				break;
		}
	}

    @Override
	public void getArea(Rectangle2D rect, double x, double y) {
		if (width == 1 && height == 2) {
			// The size 1,2 is a bit special... :)
			rect.setRect(x, y + 1, 1, 1);
		} else {
			rect.setRect(x, y, width, height);
		}
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	/**
	 * Returns a list of enemies. One of it will be attacked.
	 *
	 * @return list of enemies
	 */
	protected List<RPEntity> getEnemyList() {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(get("zoneid"));
		return zone.getPlayerAndFirends();
	}

	protected RPEntity getNearestEnemy(double range) {
		int x = getx();
		int y = gety();

		double distance = range * range; // We save this way several sqrt
											// operations
		RPEntity chosen = null;

		List<RPEntity> enemyList = getEnemyList();
		
		for (RPEntity playerOrFriend : enemyList) {
			if (playerOrFriend == this) {
				continue;
			}

			if (playerOrFriend.has("invisible")) {
				continue;
			}

			if (playerOrFriend.get("zoneid").equals(get("zoneid"))) {
				java.awt.geom.Rectangle2D rect = playerOrFriend.getArea(playerOrFriend.getx(),
						playerOrFriend.gety());
				int fx = (int) rect.getX();
				int fy = (int) rect.getY();

				if (Math.abs(fx - x) < range && Math.abs(fy - y) < range) {
					if (squaredDistance(playerOrFriend) < distance) {
						chosen = playerOrFriend;
						distance = squaredDistance(playerOrFriend);
					}
				}
			}
		}

		return chosen;
	}

	protected boolean isEnemyNear(double range) {
		int x = getx();
		int y = gety();

		double distance = range * range; // We save this way several sqrt
											// operations

		List<RPEntity> enemyList = getEnemyList();

		for (RPEntity playerOrFriend : enemyList) {
			if (playerOrFriend == this) {
				continue;
			}

			if (playerOrFriend.has("invisible")) {
				continue;
			}

			if (playerOrFriend.get("zoneid").equals(get("zoneid"))) {
				int fx = playerOrFriend.getx();
				int fy = playerOrFriend.gety();

				if (Math.abs(fx - x) < range && Math.abs(fy - y) < range) {
					if (squaredDistance(playerOrFriend) < distance) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/** returns a string-repesentation of the path */
	private String pathToString() {
		int pos = getPathPosition();
		List<Path.Node> thePath = getPath();
		List<Path.Node> nodeList = thePath.subList(pos, thePath.size());

		return nodeList.toString();
	}

	/** need to recalculate the ai when we stop the attack */
	@Override
	public void stopAttack() {
		aiState = AiState.IDLE;
		super.stopAttack();
	}


	@Override
	// TODO: modularize this method!!
	public void logic() {
		// Log4J.startMethod(logger, "logic");

		if (aiProfiles.containsKey("heal")) {
            if(getHP() < getBaseHP()) {
                String[] healingAttributes = aiProfiles.get("heal").split(",");
                int amount = Integer.parseInt(healingAttributes[0]);
                int frequency = Integer.parseInt(healingAttributes[1]);
                healSelf(amount, frequency);
            }
		}

		// if there is no player near and none will see us...
		// sleep so we don't waste cpu resources
		if (!isEnemyNear(30)) { 

			// If we are already sleeping, than don't modify the Entity.
			if (aiState == AiState.SLEEP) {
				return;
			}

			stopAttack();
			stop();

			if (Debug.CREATURES_DEBUG_SERVER)
				put("debug", "sleep");

			aiState = AiState.SLEEP;
			world.modify(this);
			return;
		}

		// this will keep track of the logic so the client can display it
		StringBuilder debug = new StringBuilder(100);

		// are we attacked and we don't attack ourself?
		if (isAttacked() && target == null) {
			// Yep, we're attacked
			clearPath();

			// hit the attacker, but prefer players
			target = getNearestEnemy(8);
			if (target == null) {
				target = this.getAttackSource(0);
			}

			if (Debug.CREATURES_DEBUG_SERVER)
				debug.append("attacked;").append(target.getID().getObjectID())
						.append('|');

			logger.debug("Creature(" + get("type") + ") has been attacked by "
					+ target.get("type"));
		} else if (target == null
				|| (!target.get("zoneid").equals(get("zoneid")) && world
						.has(target.getID())) || !world.has(target.getID())) {
			// no target or current target left the zone (or is dead)
			if (isAttacking()) {
				// stop the attack...
				if (Debug.CREATURES_DEBUG_SERVER)
					debug.append("cancelattack|");
				target = null;
				clearPath();
				stopAttack();
				waitRounds = 0;
			}

			// ...and find another target
			target = getNearestEnemy(8);
			if (target != null) {
				logger
						.debug("Creature(" + get("type")
								+ ") gets a new target.");
				if (Debug.CREATURES_DEBUG_SERVER)
					debug.append("newtarget;").append(
							target.getID().getObjectID()).append('|');
			}
		}

		// now we check our current target
		if (target == null) {
			// No target, so patrol along
			if (aiState != AiState.PATROL || !hasPath()) {
				// Create a patrolpath
				logger.debug("Creating Path for this entity");
				List<Path.Node> nodes = new LinkedList<Path.Node>();

				int size = patrolPath.size();

				long time = System.nanoTime();
				for (int i = 0; i < size; i++) {
					Path.Node actual = patrolPath.get(i);
					Path.Node next = patrolPath.get((i + 1) % size);

					nodes.addAll(Path.searchPath(this, actual.x + getx(),
							actual.y + gety(), new Rectangle2D.Double(next.x
									+ getx(), next.y + gety(), 1.0, 1.0)));
				}
				long time2 = System.nanoTime() - time;

				setPath(nodes, true);

				if (Debug.CREATURES_DEBUG_SERVER)
					debug.append("generatepatrolpath;").append(time2).append(
							"|");

			}
			logger.debug("Following path");
			if (hasPath())
				Path.followPath(this, getSpeed());
			aiState = AiState.PATROL;
			if (Debug.CREATURES_DEBUG_SERVER)
				debug.append("patrol;").append(pathToString()).append('|');
		} else if (squaredDistance(target) > 18 * 18) {
			// target out of reach
			logger.debug("Attacker is too far. Creature stops attack");
			target = null;
			clearPath();
			stopAttack();
			stop();

			if (Debug.CREATURES_DEBUG_SERVER)
				debug.append("outofreachstopped|");
		} else if (!nextTo(target, 0.25) && !target.stopped()) {
			// target not near but in reach and is moving
			logger.debug("Moving to target. Searching new path");
			clearPath();
			setMovement(target, 0, 0, 20.0);
			moveto(getSpeed());
			waitRounds = 0; // clear waitrounds
			aiState = AiState.APPROACHING_MOVING_TARGET; // update ai state
			if (Debug.CREATURES_DEBUG_SERVER) {
				List path = getPath();
				if (path != null) {
					debug.append("targetmoved;").append(pathToString()).append(
							"|");
				}
			}
		} else if (nextTo(target, 0.25)) {
			if (Debug.CREATURES_DEBUG_SERVER)
				debug.append("attacking|");
			// target is near
			logger.debug("Next to target. Creature stops and attacks");
			stop();
			attack(target);
			faceTo(target);
			aiState = AiState.ATTACKING;
		} else {
			// target in reach and not moving
			logger.debug("Moving to target. Creature attacks");
			if (Debug.CREATURES_DEBUG_SERVER) {
				debug.append("movetotarget");
			}
			aiState = AiState.APPROACHING_STOPPED_TARGET;
			attack(target);
			
			if (waitRounds == 0) {
				faceTo(target);
			}

			// our current Path is blocked...mostly by the target or another
			// attacker
			if (collides()) {
				if (Debug.CREATURES_DEBUG_SERVER)
					debug.append(";blocked");
				// invalidate the path and stop
				clearPath();
				
				// Try to fix the issue by moving randomly.
				Direction dir = Direction.rand();
				setDirection(dir);
				setSpeed(getSpeed());

				// wait some rounds so the path can be cleared by other
				// creatures
				// (either they move away or die)
				waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;
			}

			// be sure to let the blocking creatures pass before trying to find a new path
			if (waitRounds > 0) {
				if (Debug.CREATURES_DEBUG_SERVER)
					debug.append(";waiting");
				waitRounds--;
			} else {
				// Are we still patrolling?
				if (isPathLoop() || aiState == AiState.PATROL) {
					// yep, so clear the patrol path
					clearPath();
				}

				setMovement(target, 0, 0, 20.0);
				moveto(getSpeed());
				if (Debug.CREATURES_DEBUG_SERVER)
					debug.append(";newpath");

				if (getPath() == null || getPath().size() == 0) {
					// If creature is blocked, choose a new target
					if (Debug.CREATURES_DEBUG_SERVER)
						debug.append(";blocked");
					logger.debug("Blocked. Choosing a new target.");
					
					target = null;
					clearPath();
					stopAttack();
					stop();
					waitRounds = WAIT_ROUNDS_BECAUSE_TARGET_IS_BLOCKED;
				} else {
					if (Debug.CREATURES_DEBUG_SERVER)
						debug.append(';').append(getPath());
				}
			}

			if (Debug.CREATURES_DEBUG_SERVER)
				debug.append(";dummy|");
		}

		if (!stopped()) {
			StendhalRPAction.move(this);
		}

		if (rp.getTurn() % 5 == attackTurn && isAttacking()) {
			StendhalRPAction.attack(this, getAttackTarget());
			tryToPoison();
		}

		// with a probability of 1 %, a random noise is made.
		if (Rand.roll1D100() == 1 && noises.size() > 0) {
			// Random sound noises.
			int pos = Rand.rand(noises.size());
			say(noises.get(pos));
		}

		if (Debug.CREATURES_DEBUG_SERVER)
			put("debug", debug.toString());
		world.modify(this);
		//Log4J.finishMethod(logger, "logic");
	}

	protected void tryToPoison() {
		if (getAttackTarget() != null && nextTo(getAttackTarget(), 0.25)
					&& aiProfiles.containsKey("poisonous")) {
			// probability of poisoning is 1 %
			int roll = Rand.roll1D100();
			String[] poison = aiProfiles.get("poisonous").split(",");
			int prob = Integer.parseInt(poison[0]);
			String poisonType = poison[1];

			if (roll <= prob) {
				ConsumableItem item = (ConsumableItem) world
						.getRuleManager().getEntityManager().getItem(
								poisonType);
				if (item == null) {
					logger.error("Creature unable to poisoning with "
							+ poisonType);
				} else {
					RPEntity entity = getAttackTarget();

					if (entity instanceof Player) {
						Player player = (Player) entity;

						if (!player.isPoisoned() && player.poison(item)) {
							rp.addGameEvent(getName(), "poison", player
									.getName());

							player.sendPrivateText("You have been poisoned by a "
											+ getName());
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method should be called every turn if the animal is supposed to
	 * heal itself on its own. If it is used, an injured animal will heal
	 * itself by up to <i>amount</i> hitpoints every <i>frequency</i> turns.
	 * @param amount The number of hitpoints that can be restored at a time
	 * @param frequency The number of turns between healings  
	 */
	protected void healSelf(int amount, int frequency) {
        if (rp.getTurn() % frequency == 0 && getHP() > 0 ) {
            if (getHP() + amount < getBaseHP()) {
                setHP(getHP() + amount);
            } else {
                setHP(getBaseHP());
            }
        }
	}

	public void equip(List<EquipItem> items) {
		for (Creature.EquipItem equipedItem : items) {
			if (!hasSlot(equipedItem.slot)) {
				addSlot(new RPSlot(equipedItem.slot));
			}

			RPSlot slot = getSlot(equipedItem.slot);
			EntityManager manager = world.getRuleManager().getEntityManager();

			Item item = manager.getItem(equipedItem.name);

			if (item instanceof StackableItem) {
				((StackableItem) item).setQuantity(equipedItem.quantity);
			}

			slot.assignValidID(item);
			slot.add(item);
		}
	}

	private List<Item> createDroppedItems(EntityManager manager) {
		List<Item> list = new LinkedList<Item>();

		for (Creature.DropItem dropped : dropsItems) {
			int probability = Rand.roll1D100();
			if (dropped.probability >= probability) {
				Item item = manager.getItem(dropped.name);
				if (item == null) {
					logger.error("Unable to create item: " + dropped.name);
					continue;
				}

				if (dropped.min == dropped.max) {
					list.add(item);
				} else {
					StackableItem stackItem = (StackableItem) item;
					stackItem.setQuantity(Rand.rand(dropped.max - dropped.min)
							+ dropped.min);
					list.add(stackItem);
				}
			}
		}
		return list;
	}

	/**
	 * is called after the Creature is added to the zone
	 */
	public void init() {
		// do nothing
	}
}
