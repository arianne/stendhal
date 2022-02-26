/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Level;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.Nature;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Killer;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Registrator;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;
import games.stendhal.server.entity.creature.impl.attack.AttackStrategy;
import games.stendhal.server.entity.creature.impl.attack.AttackStrategyFactory;
import games.stendhal.server.entity.creature.impl.heal.HealerBehavior;
import games.stendhal.server.entity.creature.impl.heal.HealerBehaviourFactory;
import games.stendhal.server.entity.creature.impl.idle.IdleBehaviour;
import games.stendhal.server.entity.creature.impl.idle.IdleBehaviourFactory;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.status.PoisonAttackerFactory;
import games.stendhal.server.entity.status.StatusAttacker;
import games.stendhal.server.entity.status.StatusAttackerFactory;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.util.CounterMap;
import games.stendhal.server.util.Observer;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SyntaxException;

/**
 * Server-side representation of a creature.
 * <p>
 * A creature is defined as an entity which can move with certain speed, has
 * life points (HP) and can die.
 * <p>
 * Not all creatures have to be hostile, but at the moment the default behavior
 * is to attack the player.
 *
 */
public class Creature extends NPC {
	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(Creature.class);

	/**
	 * The higher the number the less items are dropped. To use numbers
	 * determined at creatures.xml, just make it 1.
	 */
	private static final double SERVER_DROP_GENEROSITY = 1;

	private HealerBehavior healer = HealerBehaviourFactory.get(null);

	private AttackStrategy strategy;


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

	/** Sound played on death */
	private String deathSound;

	// A looped sound to be played while creature is moving
	private String movementSound;
	private SoundEvent movementSoundEvent;

	/**
	 * List of things this creature should say.
	 */
	protected LinkedHashMap<String, LinkedList<String>> noises;

	boolean isRespawned;

	private String corpseName;
	private String harmlessCorpseName;
	private int corpseWidth = 1;
	private int corpseHeight = 1;

	private CreatureRespawnPoint point;

	/** Respawn time in turns */
	private int respawnTime;

	private Map<String, String> aiProfiles;
	private IdleBehaviour idler;

	private int targetX;

	private int targetY;

	private final int attackTurn = Rand.rand(5);

	private boolean isIdle;

	/** The type of the damage this creature does */
	private Nature damageType = Nature.CUT;
	/** The type of the damage this creature does in ranged attacks */
	private Nature rangedDamageType = Nature.CUT;

	/** Susceptibilities to various damage types this creature has */
	private Map<Nature, Double> susceptibilities;

	private CircumstancesOfDeath circumstances;
	private final Registrator registrator = new Registrator();

	private CounterMap<String> hitPlayers;

	/**
	 * creates a new Creature
	 *
	 * @param object serialized creature
	 */
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
		setAIProfiles(new HashMap<String, String>());

		susceptibilities = new EnumMap<Nature, Double>(Nature.class);

		// set the default movement range
		setMovementRange(20);

		updateModifiedAttributes();
	}

	/**
	 * creates a new Creature
	 *
	 * @param copy template to copy
	 */
	public Creature(final Creature copy) {
		this();

		this.baseSpeed = copy.baseSpeed;
		setSize((int) copy.getWidth(), (int) copy.getHeight());

		setCorpse(copy.getCorpseName(), copy.getHarmlessCorpseName(), copy.getCorpseWidth(), copy.getCorpseHeight());
		setBlood(copy.getBloodClass());

		/**
		 * Creatures created with this function will share their dropsItems with
		 * any other creature of that kind. If you want individual dropsItems,
		 * use clearDropItemList first!
		 */
		if (copy.dropsItems != null) {
			this.dropsItems = copy.dropsItems;
		}
		// this.dropItemInstances is ignored;

		this.setAIProfiles(copy.getAIProfiles(), false);
		this.statusAttackers = copy.statusAttackers;
		this.noises = copy.noises;

		this.respawnTime = copy.respawnTime;
		susceptibilities = copy.susceptibilities;
		setDamageTypes(copy.damageType, copy.rangedDamageType);

		setEntityClass(copy.get("class"));
		setEntitySubclass(copy.get("subclass"));

		setDescription(copy.getDescription());
		setAtk(copy.getAtk());
		setRatk(copy.getRatk());
		setDef(copy.getDef());
		setXP(copy.getXP());
		initHP(copy.getBaseHP());
		setName(copy.getName());

		setLevel(copy.getLevel());
		setSounds(copy.getSounds());
		setDeathSound(copy.deathSound);
		setMovementSound(copy.movementSound);

		if (this.aiProfiles.containsKey("active_idle")) {
			put("active_idle", "");
		}
		if (this.aiProfiles.containsKey("flying")) {
			put("flying", "");
		}

		for (RPSlot slot : copy.slots()) {
			this.addSlot((RPSlot) slot.clone());
		}

		if (copy.has("no_shadow")) {
			setShadowStyle(null);
		} else if (copy.has("shadow_style")) {
			setShadowStyle(copy.get("shadow_style"));
		}

		update();
		updateModifiedAttributes();
		stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(getID() + " Created " + get("class") + ":"
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
		setAIProfiles(new HashMap<String, String>());

		susceptibilities = new EnumMap<Nature, Double>(Nature.class);
		updateModifiedAttributes();
	}

	/**
	 * Creates a new creature with the given properties.
	 * <p>
	 * Creatures created with this function will share their dropItems with any
	 * other creature of that kind. If you want individual dropItems, use
	 * clearDropItemList first!
	 *
	 * @param clazz
	 * 		The creature's class, e.g. "golem".
	 * @param subclass
	 * 		The creature's subclass, e.g. "wooden_golem".
	 * @param name
	 * 		Typically the same as clazz, except for NPCs.
	 * @param hp
	 * 		The creature's maximum health points.
	 * @param atk
	 * 		The creature's attack strength.
	 * @param ratk
	 * 		The creature's ranged attack strength.
	 * @param def
	 * 		The creature's attack strength.
	 * @param level
	 * 		The creature's level.
	 * @param xp
	 * 		The creature's experience.
	 * @param width
	 * 		The creature's width, in squares.
	 * @param height
	 * 		The creature's height, in squares.
	 * @param baseSpeed
	 * 		The normal speed at which the creature moves.
	 * @param dropItems
	 * 		List of items that the creature drops on death.
	 * @param aiProfiles
	 * 		Creature's behaviours.
	 * @param noises.
	 * 		Sound effects used by the client when player is near creature.
	 * @param respawnTime
	 * 		How often creature respawns, in turns.
	 * @param description
	 * 		String description displayed when player examines creature.
	 */
	public Creature(final String clazz, final String subclass, final String name, final int hp,
			final int atk, final int ratk, final int def, final int level, final int xp, final int width,
			final int height, final double baseSpeed, final List<DropItem> dropItems,
			final Map<String, String> aiProfiles, final LinkedHashMap<String, LinkedList<String>> noises,
			final int respawnTime, final String description) {
		this();

		this.baseSpeed = baseSpeed;

		setSize(width, height);

		if (dropItems != null) {
			this.dropsItems = dropItems;
		}
		// this.dropItemInstances is ignored;

		this.setAIProfiles(aiProfiles);
		this.noises = new LinkedHashMap<String, LinkedList<String>>();
		this.noises.putAll(noises);
		this.respawnTime = respawnTime;

		setEntityClass(clazz);
		setEntitySubclass(subclass);
		setName(name);

		put("x", 0);
		put("y", 0);
		setDescription(description);
		setAtk(atk);
		setRatk(ratk);
		setDef(def);
		setXP(xp);
		setBaseHP(hp);
		setHP(hp);

		setLevel(level);

		if (Level.getLevel(xp) != level) {
			LOGGER.debug("Wrong level for xp [" + name + "]: " + xp + " -> "
					+ Level.getLevel(xp) + " (!" + level + ")");
		}

		update();
		updateModifiedAttributes();
		stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(getID() + " Created " + clazz + ":" + this);
		}
	}

	/**
	 * creates a new instance, using this creature as template
	 *
	 * @return a new creature
	 */
	public Creature getNewInstance() {
		return new Creature(this);
	}

	/**
	 * Sets the sound played at creature's death
	 *
	 * @param sound Name of sound
	 */
	@Override
	public final void setDeathSound(String sound) {
	    if (deathSound == null) {
	        deathSound = sound;
	    }
	    super.setDeathSound(deathSound);
	}

	/**
	 * Set looped sound to be played while creature is walking
	 * @param sound sound effect file name
	 */
	public final void setMovementSound(String sound) {
	    this.movementSound = sound;
	}

	/**
	 * Override noises for changes.
	 *
	 * @param creatureNoises noises to be used instead of the defaults for the
	 * 	creature
	 */
	public void setNoises(final LinkedHashMap<String, LinkedList<String>> creatureNoises){
		noises.clear();
		noises.putAll(creatureNoises);
	}

	/**
	 * sets new observer
	 * @param observer
	 * 				- observer, which will get info about creature death.
	 */
	public void registerObjectsForNotification(final Observer observer) {
		if(observer!=null) {
			   registrator.setObserver(observer);
		}
	}

	/**
	 * sets new observer
	 * @param observers
	 * 				- observers, which will get info about creature death.
	 */
	public void registerObjectsForNotification(final List<Observer> observers) {
		for(Observer observer : observers) {
			if(observer!=null) {
				   registrator.setObserver(observer);
			}
		}
	}

	/**
	 * unset observer
	 * @param observer
	 * 				- observer to remove.
	 */
	public void unregisterObjectsForNotification(final Observer observer) {
		if(observer!=null) {
			   registrator.removeObserver(observer);
		}
	}

	/**
	 * unset observer
	 * @param observers
	 * 				- observers to remove.
	 */
	public void unregisterObjectsForNotification(final List<Observer> observers) {
		for(Observer observer : observers) {
			if(observer!=null) {
				    registrator.removeObserver(observer);
			}
		}
	}

	/**
	 * Will notify observers when event will occurred (death).
	 */
	public void notifyRegisteredObjects() {
	     registrator.setChanges();
	     registrator.notifyObservers(circumstances);
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

	/**
	 * sets  the aiProfile of this creature
	 *
	 * @param aiProfiles aiProfile
	 */
	public final void setAIProfiles(final Map<String, String> aiProfiles) {
		this.setAIProfiles(aiProfiles, true);
	}

	private void setAIProfiles(final Map<String, String> aiProfiles, boolean initStatusAttacker) {
		this.aiProfiles = aiProfiles;
		setHealer(aiProfiles.get("heal"));
		setAttackStrategy(aiProfiles);
		if (initStatusAttacker) {
			StatusAttacker poisoner = PoisonAttackerFactory.get(aiProfiles.get("poisonous"));
			if (poisoner != null) {
				this.addStatusAttacker(poisoner);
			}

			String statusAttackerProfiles = aiProfiles.get("status_attackers");
			if (statusAttackerProfiles != null) {
    			String[] statusAttackers = statusAttackerProfiles.split(";");
    			int statusCount = statusAttackers.length;
    			for (int index = 0; index < statusCount; index++) {
    			    StatusAttacker statusAttacker = StatusAttackerFactory.get(statusAttackers[index]);
    			    if (statusAttacker != null) {
    			        this.addStatusAttacker(statusAttacker);
    			    }
    			}
			}
		}
		idler = IdleBehaviourFactory.get(aiProfiles);
	}

	/**
	 * gets the aiProfile of this creature
	 *
	 * @return aiProfile
	 */
	public Map<String, String> getAIProfiles() {
		return aiProfiles;
	}

	public void setRespawnPoint(final CreatureRespawnPoint point) {
		this.point = point;
		setRespawned(true);
	}

	/**
	 * gets the respan point of this create
	 *
	 * @return CreatureRespawnPoint
	 */
	public CreatureRespawnPoint getRespawnPoint() {
		return point;
	}

	/**
	 * Get the respawn time of the creature.
	 *
	 * @return respawn time in turns
	 */
	public int getRespawnTime() {
		return respawnTime;
	}

	public final void setCorpse(final String name, final String harmless, final int width, final int height) {
		corpseName = name;
		harmlessCorpseName = harmless;
		corpseWidth = width;
		corpseHeight = height;
		if (corpseName == null) {
			LOGGER.error(getName() + " has null corpse name.");
			/*
			 * Should not happen, but a null corpse would result
			 * in an unkillable creature, so set it to something
			 * workable.
			 */
			corpseName = "animal";
		}
		if (harmlessCorpseName == null) {
			// Set default harmless corpse to "bag.png"
			if (corpseWidth > 1 && corpseHeight > 1) {
				harmlessCorpseName = "bag_2x2";
			} else {
				harmlessCorpseName = "bag";
			}
		}
	}

	@Override
	public String getCorpseName() {
		if (corpseName == null) {
			return "animal";
		}
		return corpseName;
	}

	@Override
	public String getHarmlessCorpseName() {
		if (harmlessCorpseName == null) {
			return "bag";
		}
		return harmlessCorpseName;
	}

	@Override
	public int getCorpseWidth() {
		return corpseWidth;
	}

	@Override
	public int getCorpseHeight() {
		return corpseHeight;
	}

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
	 *
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
	 *
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
	public void onDead(final Killer killer, final boolean remove) {
		if (killer instanceof RPEntity) {
			circumstances = new CircumstancesOfDeath((RPEntity)killer, this, this.getZone());
		}

		notifyRegisteredObjects();

		if (this.point != null) {
			this.point.notifyDead(this);
		}

		super.onDead(killer, remove);
	}

	@Override
	protected void dropItemsOn(final Corpse corpse) {
		for (final Item item : dropItemInstances) {
			item.setFromCorpse(true);
			corpse.add(item);
			if (corpse.isFull(isBoss())) {
				break;
			}
		}

		for (final Item item : createDroppedItems(SingletonRepository.getEntityManager())) {
			if (!corpse.isFull(isBoss())) {
				corpse.add(item);
				item.setFromCorpse(true);
			} else {
				LOGGER.debug("Cannot add item to full corpse: " + item.getName());
			}
		}
	}


	/**
	 * Returns a list of enemies. One of it will be attacked.
	 *
	 * @return list of enemies
	 */
	public List<RPEntity> getEnemyList() {
		if (getAIProfiles().containsKey("offensive")) {
			return getZone().getPlayerAndFriends();
		} else {
			return getAttackingRPEntities();
		}
	}

	/**
	 * Returns the nearest enemy, which is reachable or otherwise attackable.
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

		// now choose the nearest enemy for which there is a path, or is
		// attackable otherwise
		RPEntity chosen = null;
		while ((chosen == null) && !distances.isEmpty()) {
			double shortestDistance = Double.MAX_VALUE;
			for (final Map.Entry<RPEntity, Double> enemy : distances.entrySet()) {
				final double distance = enemy.getValue();
				if (distance < shortestDistance) {
					chosen = enemy.getKey();
					shortestDistance = distance;
				}
			}

			if (shortestDistance >= 1) {
				final List<Node> path = Path.searchPath(this, chosen, getMovementRange());
				if ((path == null) || path.isEmpty() && !strategy.canAttackNow(this, chosen)) {
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
		if (enemyList.isEmpty()) {
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
	 * Check if the entity is a "boss". Bosses have higher capacity corpses.
	 */
	public boolean isBoss() {
		return aiProfiles.containsKey("boss");
	}

	/**
	 * Check if the creature has a rare profile, and thus should not appear in DeathMatch,
	 * or the daily quest.
	 *
	 * @return true if the creature is rare, false otherwise
	 */
	public boolean isRare() {
		return getAIProfiles().containsKey("rare");
	}

	/**
	 * Checks if the creature has "abnormal" or "rare" profile.
	 *
	 * @return
	 * 		<code>true</code> if creature is abnormal or rare.
	 */
	public boolean isAbnormal() {
		return getAIProfiles().containsKey("abnormal") || isRare();
	}

	public void equip(final List<EquipItem> items) {
		for (final EquipItem equippedItem : items) {
			if (!hasSlot(equippedItem.slot)) {
				addSlot(new EntitySlot(equippedItem.slot, equippedItem.slot));
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

				final int quantity;
				if (dropped.min == dropped.max) {
					quantity = dropped.min;
				} else {
					quantity = Rand.randUniform(dropped.max, dropped.min);
				}

				if (item instanceof StackableItem) {
					final StackableItem stackItem = (StackableItem) item;
					stackItem.setQuantity(quantity);
					list.add(stackItem);
				} else {
					for (int count = 0; count < quantity; count++) {
						if (count == 0) {
							list.add(item);
						} else {
							// additional items must be new instances
							list.add(new Item(item));
						}
					}
				}
			}
		}
		return list;
	}

	@Override
	public int getMaxRangeForArcher() {
		if (strategy != null) {
			return strategy.getRange();
		} else {
			return 0;
		}
	}

	/**
	 * returns the value of an ai profile.
	 *
	 * @param key
	 *            as defined in creatures.xml
	 * @return value or null if undefined
	 */
	public String getAIProfile(final String key) {
		return getAIProfiles().get(key);
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
			maybeMakeSound();

			// FIXME: Play a looped sound for walking creatrue
			if (movementSound != null && movementSoundEvent == null) {
				loopMovementSound();
			}
			this.notifyWorldAboutChanges();
		} else {
			/*
			 * Run enough logic to stop attacking, if the zone gets empty.
			 * Otherwise the target attribute does not get changed, and may
			 * be incorrect if the same player that was the target reappears.
			 */
			if (isAttacking()) {
				stopAttack();
			}
		}
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

	/**
	 * Generates a looped sound for creature
	 *
	 * FIXME: doesn't play sound
	 */
	private void loopMovementSound() {
	    movementSoundEvent = new SoundEvent(movementSound, SOUND_RADIUS, 100, SoundLayer.CREATURE_NOISE);
	    this.addEvent(movementSoundEvent);
	    this.notifyWorldAboutChanges();
	}

	/**
	 * Stops the looped sound
	 */
	public void stopMovementSound() {
		movementSoundEvent = null;
	}

	/**
	 *
	 * @return
	 * 		true if looped sound is currently playing
	 */
	public boolean isPlayingMovementSound() {
		return (movementSoundEvent != null);
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

	/**
	 * Set the fighting strategy used by the creature.
	 *
	 * @param aiProfiles AI profiles to be used when deciding the strategy
	 */
	public void setAttackStrategy(final Map<String, String> aiProfiles) {
		strategy = AttackStrategyFactory.get(aiProfiles);
	}

	/**
	 * Get the fighting strategy used by the creature.
	 *
	 * @return strategy
	 */
	public AttackStrategy getAttackStrategy() {
		return strategy;
	}

	public void setHealer(final String aiprofile) {
		healer = HealerBehaviourFactory.get(aiprofile);
	}

	@Override
	public float getItemAtk() {
		// Give creatures a bit weapon atk to prevent having too high
		// personal atk values
		return 5f;
	}

	@Override
	public float getItemRatk() {
		// Just doing the same as getItemAtk().
		return getItemAtk();
	}

	// *** Damage type code ***

	/**
	 * Set the susceptibility mapping of a creature. The mapping is <em>not</em>
	 * copied.
	 * @param susceptibilities The susceptibilities of the creature
	 */
	public void setSusceptibilities(Map<Nature, Double> susceptibilities) {
		this.susceptibilities = susceptibilities;
	}

	@Override
	protected double getSusceptibility(Nature type) {
		Double d = susceptibilities.get(type);

		if (d != null) {
			return d.doubleValue();
		}

		return 1.0;
	}

	@Override
	protected Nature getDamageType() {
		return damageType;
	}

	@Override
	protected Nature getRangedDamageType() {
		return rangedDamageType;
	}

	/**
	 * Set the damage natures the creature inflicts.
	 *
	 * @param type Damage nature.
	 * @param rangedType Damage nature for ranged attacks, or <code>null</code>
	 * 	if the creature uses the same type as for the melee.
	 */
	public final void setDamageTypes(Nature type, Nature rangedType) {
		damageType = type;
		if (rangedType != null) {
			rangedDamageType = rangedType;
		} else {
			rangedDamageType = type;
		}
	}

	@Override
	public boolean attack() {
		boolean res = super.attack();

		// count hits for corpse protection
		final RPEntity defender = this.getAttackTarget();
		if (defender instanceof Player) {
			if (hitPlayers == null) {
				hitPlayers = new CounterMap<String>();
			}
			hitPlayers.add(defender.getName());
		}

		return res;
	}


	/**
	 * gets the name of the player who deserves the corpse
	 *
	 * @return name of player who deserves the corpse or <code>null</code>.
	 */
	@Override
	public String getCorpseDeserver() {
		// which player did we hurt most?
		if (hitPlayers != null) {
			String playerName = hitPlayers.getHighestCountedObject();
			if ((playerName != null) && (hitPlayers.getCount(playerName) > 3)) {
				if (isPlayerInZone(playerName)) {
					return playerName;
				}
			}
		}

		// which player did hurt us most
		Entity entity = damageReceived.getHighestCountedObject();
		if (entity instanceof Player) {
			if (getZone() == entity.getZone()) {
				return ((Player) entity).getName();
			}
		}

		// which player did we attack last?
		RPEntity target = getAttackTarget();
		if (target instanceof Player) {
			if (getZone() == target.getZone()) {
				return target.getName();
			}
		}

		return null;
	}

	/**
	 * checks if the name player is in the same zone
	 *
	 * @param playerName name of player
	 * @return true if he is online and in the same zone as this creature
	 */
	private boolean isPlayerInZone(String playerName) {
		Player player = StendhalRPRuleProcessor.get().getPlayer(playerName);
		if (player == null) {
			return false;
		}
		return getZone() == player.getZone();
	}
}
