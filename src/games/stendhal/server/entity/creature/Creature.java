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

import games.stendhal.common.Level;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.impl.AttackStrategy;
import games.stendhal.server.entity.creature.impl.AttackStrategyFactory;
import games.stendhal.server.entity.creature.impl.Attacker;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;
import games.stendhal.server.entity.creature.impl.HealerBehavior;
import games.stendhal.server.entity.creature.impl.Healingbehaviourfactory;
import games.stendhal.server.entity.creature.impl.IdleBehaviourFactory;
import games.stendhal.server.entity.creature.impl.Idlebehaviour;
import games.stendhal.server.entity.creature.impl.PoisonerFactory;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.slot.EntitySlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

import org.apache.log4j.Logger;

/**
 * Server-side representation of a creature.
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
	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(Creature.class);

	/**
	 * The higher the number the less items are dropped. To use numbers
	 * determined at creatures.xml, just make it 1.
	 */
	private static final double SERVER_DROP_GENEROSITY = 1;

	public HealerBehavior healer = Healingbehaviourfactory.get(null);

	public AttackStrategy strategy;


	
	/**
	 * This list of item names this creature may drop Note; per default this list
	 * is shared with all creatures of that class.
	 */
	protected List<DropItem> dropsItems;

	/**
	 * This list of item instances this creature may drop for use in quests. This
	 * is always creature specific.
	 */
	protected List<Item> dropItemInstances;

	/**
	 * List of things this creature should say.
	 */
	protected LinkedHashMap<String, LinkedList<String>> noises;
	
	boolean isRespawned;
	
	private String corpseName;
	private int corpseWidth = 1;
	private int corpseHeight = 1;

	private CreatureRespawnPoint point;

	/** Respawn time in turns */
	private int respawnTime;

	private Map<String, String> aiProfiles;
	private Attacker poisoner; 
	private Idlebehaviour idler; 
	
	private int targetX;

	private int targetY;
	
	private final int attackTurn = Rand.rand(5);

	private boolean isIdle; 

	public Creature(final RPObject object) {
		super(object);

		setRPClass("creature");
		put("type", "creature");
		put("title_type", "enemy");
		if (object.has("title_type")) {
			put("title_type", object.get("title_type"));
		}
		
		dropsItems = new ArrayList<DropItem>();
		dropItemInstances = new ArrayList<Item>();
		setAiProfiles(new HashMap<String, String>());
	}

	public Creature(final Creature copy) {
		this();

		this.baseSpeed = copy.baseSpeed;
		setSize((int) copy.getWidth(), (int) copy.getHeight());
		
		setCorpse(copy.getCorpseName(), copy.getCorpseWidth(), copy.getCorpseHeight());

		/**
		 * Creatures created with this function will share their dropsItems with
		 * any other creature of that kind. If you want individual dropsItems,
		 * use clearDropItemList first!
		 */
		if (copy.dropsItems != null) {
			this.dropsItems = copy.dropsItems;
		}
		// this.dropItemInstances is ignored;

		this.setAiProfiles(copy.getAiProfiles());
		this.noises = copy.noises;

		this.respawnTime = copy.respawnTime;

		setEntityClass(copy.get("class"));
		setEntitySubClass(copy.get("subclass"));

		setDescription(copy.getDescription());
		setATK(copy.getATK());
		setDEF(copy.getDEF());
		setXP(copy.getXP());
		initHP(copy.getBaseHP());
		setName(copy.getName());

		setLevel(copy.getLevel());
		
		for (RPSlot slot : copy.slots()) {
			this.addSlot((RPSlot) slot.clone());
		}

		update();

		stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(getIDforDebug() + " Created " + get("class") + ":"
					+ this);
		}
	}

	
	/**
	 * creates a new creature without properties. These must be set in the
	 * deriving class
	 */
	public Creature() {
		super();
		setRPClass("creature");
		put("type", "creature");
		put("title_type", "enemy");
		dropsItems = new ArrayList<DropItem>();
		dropItemInstances = new ArrayList<Item>();
		setAiProfiles(new HashMap<String, String>());
	}

	/**
	 * Creates a new creature with the given properties.
	 * <p>
	 * Creatures created with this function will share their dropItems with any
	 * other creature of that kind. If you want individual dropItems, use
	 * clearDropItemList first!
	 * 
	 * @param clazz
	 *            The creature's class, e.g. "golem"
	 * @param subclass
	 *            The creature's subclass, e.g. "wooden_golem"
	 * @param name
	 *            Typically the same as clazz, except for NPCs
	 * @param hp
	 *            The creature's maximum health points
	 * @param attack
	 *            The creature's attack strength
	 * @param defense
	 *            The creature's attack strength
	 * @param level
	 *            The creature's level
	 * @param xp
	 *            The creature's experience
	 * @param width
	 *            The creature's width, in squares
	 * @param height
	 *            The creature's height, in squares
	 * @param baseSpeed
	 * @param dropItems
	 * @param aiProfiles
	 * @param noises
	 * @param respawnTime in turns
	 * @param description
	 */
	public Creature(final String clazz, final String subclass, final String name, final int hp,
			final int attack, final int defense, final int level, final int xp, final int width, final int height,
			final double baseSpeed, final List<DropItem> dropItems,
			final Map<String, String> aiProfiles, final LinkedHashMap<String, LinkedList<String>> noises,
			final int respawnTime, final String description) {
		this();

		this.baseSpeed = baseSpeed;

		setSize(width, height);

		if (dropItems != null) {
			this.dropsItems = dropItems;
		}
		// this.dropItemInstances is ignored;

		this.setAiProfiles(aiProfiles);
		this.noises = noises;

		this.respawnTime = respawnTime;

		setEntityClass(clazz);
		setEntitySubClass(subclass);
		setName(name);

		put("x", 0);
		put("y", 0);
		setDescription(description);
		setATK(attack);
		setDEF(defense);
		setXP(xp);
		setBaseHP(hp);
		setHP(hp);

		setLevel(level);

		if (Level.getLevel(xp) != level) {
			LOGGER.debug("Wrong level for xp [" + name + "]: " + xp + " -> "
					+ Level.getLevel(xp) + " (!" + level + ")");
		}

		update();

		stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(getIDforDebug() + " Created " + clazz + ":" + this);
		}
	}

	public Creature getNewInstance() {
		return new Creature(this);
	}

	public boolean isSpawned() {
		return isRespawned;
	}

	public void setRespawned(final boolean isRespawned) {
		this.isRespawned = isRespawned;
	}

	public int getAttackTurn() {
		return attackTurn;
	}
	
	public boolean isAttackTurn(final int turn) {
		return ((turn + attackTurn) % getAttackRate() == 0);
	}


	public static void generateRPClass() {
		try {
			final RPClass npc = new RPClass("creature");
			npc.isA("npc");
			npc.addAttribute("debug", Type.VERY_LONG_STRING,
					Definition.VOLATILE);
			npc.addAttribute("metamorphosis", Type.STRING, Definition.VOLATILE);
		} catch (final SyntaxException e) {
			LOGGER.error("cannot generate RPClass", e);
		}
	}


	public RPObject.ID getIDforDebug() {
		return getID();
	}

	private void setAiProfiles(final Map<String, String> aiProfiles) {
		this.aiProfiles = aiProfiles;
		setHealer(aiProfiles.get("heal"));
		setAttackStrategy(aiProfiles);
		poisoner = PoisonerFactory.get(aiProfiles.get("poisonous"));
		idler = IdleBehaviourFactory.get(aiProfiles);
		
	}

	private Map<String, String> getAiProfiles() {
		return aiProfiles;
	}

	public void setRespawnPoint(final CreatureRespawnPoint point) {
		this.point = point;
		setRespawned(true);
	}

	/** Returns the respawn time in turns */
	public int getRespawnTime() {
		return respawnTime;
	}
	
	public void setCorpse(final String name, final int width, final int height) {
		corpseName = name;
		if (corpseName == null) {
			LOGGER.error(getName() + " has null corpse name.");
			/*
			 * Should not happen, but a null corpse would result 
			 * in an unkillable creature, so set it to something
			 * workable. 
			 */ 
			corpseName = "animal";
		}
		corpseWidth = width;
		corpseHeight = height;
	}
	
	@Override
	public String getCorpseName() {
		if (corpseName == null) {
			return "animal";
		}
		return corpseName;
	}
	
	@Override
	public int getCorpseWidth() {
		return corpseWidth;
	};
	
	@Override
	public int getCorpseHeight() {
		return corpseHeight;
	};

	/**
	 * clears the list of predefined dropItems and creates an empty list
	 * specific to this creature.
	 * 
	 */
	public void clearDropItemList() {
		dropsItems = new ArrayList<DropItem>();
		dropItemInstances.clear();
	}

	/**
	 * adds a named item to the List of Items that will be dropped on dead if
	 * clearDropItemList hasn't been called first, this will change all
	 * creatures of this kind.
	 * @param name 
	 * @param probability 
	 * @param min 
	 * @param max 
	 */
	public void addDropItem(final String name, final double probability, final int min, final int max) {
		dropsItems.add(new DropItem(name, probability, min, max));
	}

	/**
	 * adds a named item to the List of Items that will be dropped on dead if
	 * clearDropItemList hasn't been called first, this will change all
	 * creatures of this kind.
	 * @param name 
	 * @param probability 
	 * @param amount 
	 */
	public void addDropItem(final String name, final double probability, final int amount) {
		dropsItems.add(new DropItem(name, probability, amount));
	}

	/**
	 * adds a specific item to the List of Items that will be dropped on dead
	 * with 100 % probability. this is always for that specific creature only.
	 * 
	 * @param item
	 */
	public void addDropItem(final Item item) {
		dropItemInstances.add(item);
	}

	/**
	 * Returns true if this RPEntity is attackable.
	 */
	@Override
	public boolean isAttackable() {
		return true;
	}

	@Override
	public void onDead(final Entity killer, final boolean remove) {
		if (this.point != null) {
			this.point.notifyDead(this);
		}
		super.onDead(killer, remove);
	}

	@Override
	protected void dropItemsOn(final Corpse corpse) {
		for (final Item item : dropItemInstances) {
			corpse.add(item);
			if (corpse.isFull()) {
				break;
			}
		}

		for (final Item item : createDroppedItems(SingletonRepository.getEntityManager())) {
			corpse.add(item);

			if (corpse.isFull()) {
				break;
			}
		}
	}


	/**
	 * Returns a list of enemies. One of it will be attacked.
	 * 
	 * @return list of enemies
	 */
	public List<RPEntity> getEnemyList() {
		if (getAiProfiles().keySet().contains("offensive")) {
			return getZone().getPlayerAndFriends();
		} else {
			return getAttackingRPEntities();
		}
	}

	/**
	 * returns the nearest enemy, which is reachable.
	 * 
	 * @param range
	 *            attack radius
	 * @return chosen enemy or null if no enemy was found.
	 */
	public RPEntity getNearestEnemy(final double range) {
		// create list of enemies
		final List<RPEntity> enemyList = getEnemyList();
		if (enemyList.isEmpty()) {
			return null;
		}

		// calculate the distance of all possible enemies
		final Map<RPEntity, Double> distances = new HashMap<RPEntity, Double>();
		for (final RPEntity enemy : enemyList) {
			if (enemy == this) {
				continue;
			}

			if (enemy.isInvisibleToCreatures()) {
				continue;
			}

			final double squaredDistance = this.squaredDistance(enemy);
			if (squaredDistance <= (range * range)) {
				distances.put(enemy, squaredDistance);
			}
		}

		// now choose the nearest enemy for which there is a path
		RPEntity chosen = null;
		while ((chosen == null) && (distances.size() > 0)) {
			double shortestDistance = Double.MAX_VALUE;
			for (final Map.Entry<RPEntity, Double> enemy : distances.entrySet()) {
				final double distance = enemy.getValue();
				if (distance < shortestDistance) {
					chosen = enemy.getKey();
					shortestDistance = distance;
				}
			}

			if (shortestDistance >= 1) {
				final List<Node> path = Path.searchPath(this, chosen, 20.0);
				if ((path == null) || (path.size() == 0)) {
					distances.remove(chosen);
					chosen = null;
				} else {
					// set the path. if not setMovement() will search a new one
					setPath(new FixedPath(path, false));
				}
			}
		}
		// return the chosen enemy or null if we could not find one in reach
		return chosen;
	}

	public boolean isEnemyNear(final double range) {
		final int x = getX();
		final int y = getY();

		List<RPEntity> enemyList = getEnemyList();
		if (enemyList.size() == 0) {
			final StendhalRPZone zone = getZone();
			enemyList = zone.getPlayerAndFriends();
		}

		for (final RPEntity playerOrFriend : enemyList) {
			if (playerOrFriend == this) {
				continue;
			}

			if (playerOrFriend.isInvisibleToCreatures()) {
				continue;
			}

			if (playerOrFriend.getZone() == getZone()) {
				final int fx = playerOrFriend.getX();
				final int fy = playerOrFriend.getY();

				if ((Math.abs(fx - x) < range) && (Math.abs(fy - y) < range)) {
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Check if the creature has a rare profile, and thus should not appear in DeathMatch,
	 * or the daily quest.
	 * 
	 * @return true if the creature is rare, false otherwise
	 */
	public boolean isRare() {
		return getAiProfiles().keySet().contains("rare");
	}

	/** need to recalculate the ai when we stop the attack. */
	@Override
	public void stopAttack() {
	
		super.stopAttack();
	}

	/**
	 *  poisons attacktarget with the behavior in Poisoner.
	 * 
	 * 
	 * @throws NullPointerException if attacktarget is null
	 */
	
	public void tryToPoison() {
		
		final RPEntity entity = getAttackTarget();
		if (poisoner.attack(entity)) {
			new GameEvent(getName(), "poison", entity.getName()).raise();
			entity.sendPrivateText("You have been poisoned by a " + getName());
		}
	}

	public void equip(final List<EquipItem> items) {
		for (final EquipItem equippedItem : items) {
			if (!hasSlot(equippedItem.slot)) {
				addSlot(new EntitySlot(equippedItem.slot));
			}

			final RPSlot slot = getSlot(equippedItem.slot);
			final Item item = SingletonRepository.getEntityManager().getItem(equippedItem.name);

			if (item instanceof StackableItem) {
				((StackableItem) item).setQuantity(equippedItem.quantity);
			}
			
			slot.add(item);
		}
	}

	private List<Item> createDroppedItems(final EntityManager defaultEntityManager) {
		final List<Item> list = new LinkedList<Item>();

		for (final DropItem dropped : dropsItems) {
			final double probability = Rand.rand(1000000) / 10000.0;

			if (probability <= (dropped.probability / SERVER_DROP_GENEROSITY)) {
				final Item item = defaultEntityManager.getItem(dropped.name);
				if (item == null) {
					LOGGER.error("Unable to create item: " + dropped.name);
					continue;
				}

				if (dropped.min == dropped.max) {
					list.add(item);
				} else {
					final StackableItem stackItem = (StackableItem) item;
					stackItem.setQuantity(Rand.rand(dropped.max - dropped.min)
							+ dropped.min);
					list.add(stackItem);
				}
			}
		}
		return list;
	}

	@Override
	public boolean canDoRangeAttack(final RPEntity target) {
		if (getAiProfiles().containsKey("archer")) {
			// The creature can shoot, but only if the target is at most
			// 7 tiles away.
			return squaredDistance(target) <= 7 * 7;
		}
		return false;
		//  return super.canDoRangeAttack(target);
	}

	/**
	 * returns the value of an ai profile.
	 * 
	 * @param key
	 *            as defined in creatures.xml
	 * @return value or null if undefined
	 */
	public String getAIProfile(final String key) {
		return getAiProfiles().get(key);
	}

	/**
	 * is called after the Creature is added to the zone.
	 */
	public void init() {
		// do nothing
	}

	@Override
	public void logic() {
		healer.heal(this);
		if (!this.getZone().getPlayerAndFriends().isEmpty()) {
		
			if (strategy.hasValidTarget(this)) {
				strategy.getBetterAttackPosition(this);
				this.applyMovement();
				if (strategy.canAttackNow(this)) {
					strategy.attack(this);
					this.makeNoiseChance(100, "fight");					
		        } else {
		        	// can't attack and trying to find better position
		        	// treat it as creature follows player.
		        	this.makeNoiseChance(100, "follow");
		        }
			} else {
				this.stopAttack();
				strategy.findNewTarget(this);
				if (strategy.hasValidTarget(this)) {
					this.setBusy();
					// this event duration usually is only one turn
					this.makeNoiseChance(50, "target");
				} else {
				 	this.setIdle();
					this.makeNoiseChance(100, "idle");	
				}
			}
		}
		this.notifyWorldAboutChanges();
	}

	/**
	 * Random sound noises.
	 * @param state - state for noises
	 */
	public void makeNoise(final String state) {
		if (noises == null) {
			return;
		}
		if (noises.get(state)==null) {
			return;
		}
		if (noises.get(state).size() > 0) {
			final int pos = Rand.rand(noises.get(state).size());
			say(noises.get(state).get(pos));
		}
	}
	
	
	/**
	 * wrapper around makeNoise to simplify a code
	 * @param prob - 1/chance of make noise
	 * @param state - state for noises
	 */
	public void makeNoiseChance(int prob, final String state) {
		if(Rand.rand(prob)==1) {
			makeNoise(state);
		}
	}

	public boolean hasTargetMoved() {
		if ((targetX != getAttackTarget().getX()) || (targetY != getAttackTarget().getY())) {
			targetX = getAttackTarget().getX();
			targetY = getAttackTarget().getY();
				
			return true;
		}
		return false;
	}

	public void setIdle() {
		if (!isIdle) {
			isIdle = true;
			clearPath();
			stopAttack();
			stop();
		
		} else {
			idler.perform(this);
			
		}
	
	}

	public void setBusy() {
		isIdle = false;
	}

	public void setAttackStrategy(final Map<String, String> aiProfiles) {
		strategy = AttackStrategyFactory.get(aiProfiles);
		
	}

	public void setHealer(final String aiprofile) {
		healer = Healingbehaviourfactory.get(aiprofile);
	}

	@Override
	public float getItemAtk() {
		// Give creatures a bit weapon atk to prevent having too high
		// personal atk values
		return 5f;
	}
}
