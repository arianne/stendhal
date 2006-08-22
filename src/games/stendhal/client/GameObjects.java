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
package games.stendhal.client;

import games.stendhal.client.entity.*;
import games.stendhal.client.events.*;
import games.stendhal.common.Direction;
import games.stendhal.common.Pair;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.*;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;
import org.apache.log4j.Logger;

/** This class stores the objects that exists on the World right now */
public class GameObjects implements Iterable<Entity> {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(GameObjects.class);

	private static Map<Pair<String, String>, Class> entityMap;

	static {
		entityMap = new HashMap<Pair<String, String>, Class>();
		register();
	}

	private static void register() {
		register("player", null, Player.class);

		register("creature", "small_animal", SmallCreature.class);
		register("creature", "giant_animal", BigCreature.class);
		register("creature", "huge_animal", HugeCreature.class);
		register("creature", "mythical_animal", MythicalCreature.class);
		register("creature", null, NormalCreature.class);

		register("sheep", null, Sheep.class);

		register("npc", null, NPC.class);

		register("plant_grower", null, PlantGrower.class);
		register("grain_field", null, GrainField.class);
		register("food", null, SheepFood.class);
		register("chest", null, Chest.class);

		register("corpse", null, Corpse.class);

		register("blood", null, Blood.class);
		register("sign", null, Sign.class);
		register("blackboard", null, Sign.class);

		register("item", null, Item.class);
		register("item", "book", MiscItem.class);
		register("item", "drink", StackableItem.class);
		register("item", "food", StackableItem.class);
		register("item", "herb", MiscItem.class);
		register("item", "key", MiscItem.class);
		register("item", "money", StackableItem.class);
		register("item", "projectiles", StackableItem.class);
		register("item", "resource", StackableItem.class);
		register("item", "scroll", StackableItem.class);

		register("portal", null, Portal.class);
		register("door", null, Door.class);
	}

	public static void register(String type, String eclass, Class entityClass) {
		entityMap.put(new Pair<String, String>(type, eclass), entityClass);
	}

	private Map<RPObject.ID, Entity> objects;

	private Map<RPEntity, RPEntity> attacks;

	private List<Text> texts;

	private List<Text> textsToRemove;

	private List<Entity> sortObjects;

	private StaticGameLayers collisionMap;

	public GameObjects(StaticGameLayers collisionMap) {
		objects = new HashMap<RPObject.ID, Entity>();
		attacks = new HashMap<RPEntity, RPEntity>();

		texts = new LinkedList<Text>();
		textsToRemove = new LinkedList<Text>();
		sortObjects = new LinkedList<Entity>();

		this.collisionMap = collisionMap;
	}

	public Iterator<Entity> iterator() {
		return sortObjects.iterator();
	}

	/** Create a Entity of the correct type depending of the arianne object */
	public Entity entityType(RPObject object) {
		try {
			if (object.get("type").equals("player")) {
				return new Player(this, object);
			}

			String type = object.get("type");
			String eclass = null;
			if (object.has("class")) {
				eclass = object.get("class");
			}

			Class entityClass = entityMap.get(new Pair<String, String>(type,
					eclass));

			if (entityClass == null) {
				// If there is no entity, let's try without using class.
				entityClass = entityMap
						.get(new Pair<String, String>(type, null));
			}

			java.lang.reflect.Constructor constr = entityClass.getConstructor(
					GameObjects.class, RPObject.class);
			return (Entity) constr.newInstance(this, object);
		} catch (Exception e) {
			logger.error("cannot create entity for object " + object, e);
			return null;
		}
	}

	public Sprite spriteType(RPObject object) {
		try {
			return entityType(object).getSprite();
		} catch (Exception e) {
			logger.error("cannot create sprite for object " + object, e);
			return null;
		}
	}

	private void sort() {
		Collections.sort(sortObjects, new Comparator<Entity>() {
			public int compare(Entity o1, Entity o2) {
				// return result(o1,o2);
				return o1.compare(o2);
			}
		});
	}

	/** Add a new Entity to the game */
	public void add(RPObject object) throws AttributeNotFoundException {
		Log4J.startMethod(logger, "add");

		Entity entity = entityType(object);

		entity.onAdded(object);
		fireMovementEvent(entity, object, null);
		fireZoneChangeEvent(entity, object, null);

		if (entity instanceof TalkEvent) {
			fireTalkEvent((TalkEvent) entity, object, null);
		}

		if (entity instanceof HPEvent) {
			fireHPEvent((HPEvent) entity, object, null);
		}

		if (entity instanceof KillEvent) {
			fireKillEvent(((KillEvent) entity), object, null);
		}

		if (entity instanceof AttackEvent) {
			fireAttackEvent(((RPEntity) entity), object, null);
		}

		objects.put(entity.getID(), entity);
		sortObjects.add(entity);

		logger.debug("added " + entity);
		Log4J.finishMethod(logger, "add");
	}

	/** Modify a existing Entity so its propierties change */
	public void modifyAdded(RPObject object, RPObject changes)
			throws AttributeNotFoundException {
		Log4J.startMethod(logger, "modifyAdded");
		Entity entity = objects.get(object.getID());
		if (entity != null) {
			entity.onChangedAdded(object, changes);
			fireMovementEvent(entity, object, changes);

			if (entity instanceof TalkEvent) {
				fireTalkEvent((TalkEvent) entity, object, changes);
			}

			if (entity instanceof HPEvent) {
				fireHPEvent((HPEvent) entity, object, changes);
			}

			if (entity instanceof KillEvent) {
				fireKillEvent(((KillEvent) entity), object, changes);
			}

			if (entity instanceof AttackEvent) {
				fireAttackEvent(((RPEntity) entity), object, changes);
			}
		}

		Log4J.finishMethod(logger, "modifyAdded");
	}

	public void modifyRemoved(RPObject object, RPObject changes)
			throws AttributeNotFoundException {
		Log4J.startMethod(logger, "modifyRemoved");
		Entity entity = objects.get(object.getID());
		if (entity != null) {
			entity.onChangedRemoved(object, changes);

			if (entity instanceof HPEvent) {
				fireHPEventChangedRemoved((HPEvent) entity, object, changes);
			}

			if (entity instanceof AttackEvent) {
				fireAttackEventChangedRemoved(((RPEntity) entity), object,
						changes);
			}
		}

		Log4J.finishMethod(logger, "modifyRemoved");
	}

	public boolean has(Entity entity) {
		return objects.containsKey(entity.getID());
	}

	public Entity get(RPObject.ID id) {
		return objects.get(id);
	}

	/** Removes a Entity from game */
	public void remove(RPObject.ID id) {
		Log4J.startMethod(logger, "remove");
		logger.debug("removed " + id);

		Entity entity = objects.get(id);
		if (entity != null) {
			entity.onRemoved();
			fireMovementEvent(entity, null, null);
			fireZoneChangeEvent(entity, null, null);

			if (entity instanceof TalkEvent) {
				fireTalkEvent((TalkEvent) entity, null, null);
			}

			if (entity instanceof HPEvent) {
				fireHPEvent((HPEvent) entity, null, null);
			}

			if (entity instanceof KillEvent) {
				fireKillEvent(((KillEvent) entity), null, null);
			}

			if (entity instanceof AttackEvent) {
				fireAttackEvent(((RPEntity) entity), null, null);
			}
		}

		Entity object = objects.remove(id);
		sortObjects.remove(object);
		Log4J.finishMethod(logger, "remove");
	}

	/** Removes all the object entities */
	public void clear() {
		Log4J.startMethod(logger, "clear");

		// invalidate all entity objects
		for (Iterator it = iterator(); it.hasNext();)
			((Entity) it.next()).onRemoved();

		objects.clear();
		attacks.clear();
		sortObjects.clear();
		texts.clear();
		Log4J.finishMethod(logger, "clear");
	}

	private void fireTalkEvent(TalkEvent entity, RPObject base, RPObject diff) {
		if (diff == null && base == null) {
			// Remove case
		} else if (diff == null) {
			// First time case.
			if (base.has("text")) {
				String text = base.get("text");
				entity.onTalk(text);
			}

			if (base.has("private_text")) {
				String text = base.get("private_text");
				entity.onPrivateListen(text);
			}
		} else {
			if (diff.has("text")) {
				String text = diff.get("text");
				entity.onTalk(text);
			}

			if (diff.has("private_text")) {
				String text = diff.get("private_text");
				entity.onPrivateListen(text);
			}
		}
	}

	private void fireZoneChangeEvent(Entity entity, RPObject base, RPObject diff) {
		RPObject.ID id = entity.getID();
		if (diff == null && base == null) {
			// Remove case
			entity.onLeaveZone(id.getZoneID());
		} else if (diff == null) {
			// First time case.
			entity.onEnterZone(id.getZoneID());
		}
	}

	private void fireMovementEvent(Entity entity, RPObject base, RPObject diff) {
		if (diff == null && base == null) {
			// Remove case
		} else if (diff == null) {
			// First time case.
			int x = base.getInt("x");
			int y = base.getInt("y");

			Direction direction = Direction.STOP;
			if (base.has("dir"))
				direction = Direction.build(base.getInt("dir"));

			double speed = 0;
			if (base.has("speed"))
				speed = base.getDouble("speed");

			entity.onMove(x, y, direction, speed);
		} else {
			// Real movement case
			int x = base.getInt("x");
			int y = base.getInt("y");

			int oldx = x, oldy = y;

			if (diff.has("x"))
				x = diff.getInt("x");
			if (diff.has("y"))
				y = diff.getInt("y");

			Direction direction = Direction.STOP;
			if (base.has("dir"))
				direction = Direction.build(base.getInt("dir"));
			if (diff.has("dir"))
				direction = Direction.build(diff.getInt("dir"));

			double speed = 0;
			if (base.has("speed"))
				speed = base.getDouble("speed");
			if (diff.has("speed"))
				speed = diff.getDouble("speed");

			entity.onMove(x, y, direction, speed);

			if (direction == Direction.STOP || speed == 0) {
				entity.onStop();
			}

			if (oldx != x && oldy != y) {
				entity.onLeave(oldx, oldy);
				entity.onEnter(x, y);
			}
		}
	}

	private void fireHPEvent(HPEvent entity, RPObject base, RPObject diff) {
		if (diff == null && base == null) {
			// Remove case
		} else if (diff == null) {
			// First time case.
		} else {
			if (diff.has("hp") && base.has("hp")) {
				int healing = diff.getInt("hp") - base.getInt("hp");
				if (healing > 0) {
					entity.onHealed(healing);
				}
			}

			if (diff.has("poisoned")) {
				int poisoned = diff.getInt("poisoned");
				// To remove the - sign on poison.
				entity.onPoisoned(Math.abs(poisoned));
			}

			if (diff.has("eating")) {
				entity.onEat(0);
			}
		}
	}

	private void fireHPEventChangedRemoved(HPEvent entity, RPObject base,
			RPObject diff) {
		if (diff.has("poisoned")) {
			entity.onPoisonEnd();
		}

		if (diff.has("eating")) {
			entity.onEatEnd();
		}
	}

	private void fireKillEvent(KillEvent entity, RPObject base, RPObject diff) {
		if (diff == null && base == null) {
			// Remove case
		} else if (diff == null) {
			// First time case.
		} else {
			if (diff.has("hp/base_hp") && diff.getDouble("hp/base_hp") == 0) {
				RPEntity killer = null;
				for (Map.Entry<RPEntity, RPEntity> entry : attacks.entrySet()) {
					if (entry.getValue() == entity) {
						killer = entry.getKey();
					}
				}

				entity.onDeath(killer);
			}
		}
	}

	private void fireAttackEvent(RPEntity entity, RPObject base, RPObject diff) {
		if (diff == null && base == null) {
			// Remove case
			if (attacks.containsKey(entity)) {
				entity.onStopAttack();

				RPEntity target = attacks.get(entity);
				if (target != null) {
					target.onStopAttacked(entity);
				}

				attacks.remove(entity);
			}
		} else if (diff == null) {
			// Modified case
			if (base.has("target")) {
				int risk = (base.has("risk") ? base.getInt("risk") : 0);
				int damage = (base.has("damage") ? base.getInt("damage") : 0);
				int target = base.getInt("target");

				RPObject.ID targetEntityID = new RPObject.ID(target, base
						.get("zoneid"));
				RPEntity targetEntity = (RPEntity) objects.get(targetEntityID);
				if (targetEntity != null) {
					if (!attacks.containsKey(entity)) {
						entity.onAttack(targetEntity);
						targetEntity.onAttacked(entity);
					}

					if (risk == 0) {
						entity.onAttackMissed(targetEntity);
						targetEntity.onMissed(entity);
					}

					if (risk > 0 && damage == 0) {
						entity.onAttackBlocked(targetEntity);
						targetEntity.onBlocked(entity);
					}

					if (risk > 0 && damage > 0) {
						entity.onAttackDamage(targetEntity, damage);
						targetEntity.onDamaged(entity, damage);
					}

					// targetEntity.onAttack(entity,risk,damage);
					attacks.put(entity, targetEntity);
				}
			}
		} else {
			// Modified case
			if (diff.has("target") && base.has("target")
					&& !base.get("target").equals(diff.get("target"))) {
				System.out.println("Removing target: new target");
				entity.onStopAttack();

				RPEntity target = attacks.get(entity);
				if (target != null) {
					target.onStopAttacked(entity);
				}

				attacks.remove(entity);
			}

			if (diff.has("target") || base.has("target")) {
				boolean thereIsEvent = false;

				int risk = 0;
				if (diff.has("risk")) {
					thereIsEvent = true;
					risk = diff.getInt("risk");
				} else if (base.has("risk")) {
					risk = base.getInt("risk");
				} else {
					risk = 0;
				}

				int damage = 0;
				if (diff.has("damage")) {
					thereIsEvent = true;
					damage = diff.getInt("damage");
				} else if (base.has("damage")) {
					damage = base.getInt("damage");
				} else {
					damage = 0;
				}

				int target = -1;
				if (diff.has("target")) {
					target = diff.getInt("target");
				} else if (base.has("target")) {
					target = base.getInt("target");
				}

				RPObject.ID targetEntityID = new RPObject.ID(target, diff
						.get("zoneid"));
				RPEntity targetEntity = (RPEntity) objects.get(targetEntityID);
				if (targetEntity != null) {
					entity.onAttack(targetEntity);
					targetEntity.onAttacked(entity);

					if (thereIsEvent) {
						if (risk == 0) {
							entity.onAttackMissed(targetEntity);
							targetEntity.onMissed(entity);
						}

						if (risk > 0 && damage == 0) {
							entity.onAttackBlocked(targetEntity);
							targetEntity.onBlocked(entity);
						}

						if (risk > 0 && damage > 0) {
							entity.onAttackDamage(targetEntity, damage);
							targetEntity.onDamaged(entity, damage);
						}
					}

					attacks.put(entity, targetEntity);
				}
			}
		}
	}

	private void fireAttackEventChangedRemoved(RPEntity entity, RPObject base,
			RPObject diff) {
		if (diff.has("target")) {
			entity.onStopAttack();

			RPEntity target = attacks.get(entity);
			if (target != null) {
				target.onStopAttacked(entity);
			}

			attacks.remove(entity);
		}
	}

	private boolean collides(Entity entity) {
		// TODO: Ugly, use similar method that server uses
		Rectangle2D area = entity.getArea();

		for (Entity other : sortObjects) {
			if (!(other instanceof PassiveEntity) && !(other instanceof Blood) && !(other instanceof PlantGrower)) {
				if (area.intersects(other.getArea())
						&& !entity.getID().equals(other.getID())) {
					entity.onCollideWith(other);
					return true;
				}
			}
		}

		return false;
	}

	/** Move objects based on the lapsus of time ellapsed since the last call. */
	public void move(long delta) {
		for (Entity entity : sortObjects) {
			if (!entity.stopped()) {
				if (!collisionMap.collides(entity.getArea())) {
					if (!collides(entity)) {
						entity.move(delta);
					}
				} else {
					entity.onCollide((int) entity.getx(), (int) entity.gety());
				}
			}
		}
	}

	public void addText(Entity speaker, String text, Color color,
			boolean isTalking) {
		double x = speaker.getx();
		double y = speaker.gety();

		boolean found = true;

		while (found == true) {
			found = false;
			for (Text item : texts) {
				if (item.getx() == x && item.gety() == y) {
					found = true;
					y += 0.5;
					break;
				}
			}
		}

		Text entity = new Text(this, text, x, y, color, isTalking);
		texts.add(entity);
	}

	public void addText(Entity speaker, Sprite sprite, long persistTime) {
		Text entity = new Text(this, sprite, speaker.getx(), speaker.gety(),
				persistTime);
		texts.add(entity);
	}

	public void removeText(Text entity) {
		textsToRemove.add(entity);
	}

	public Entity at(double x, double y) {
		ListIterator<Entity> it = sortObjects.listIterator(sortObjects.size());
		while (it.hasPrevious()) {
			Entity entity = it.previous();

			if (entity.getArea().contains(x, y)) {
				return entity;
			}
		}

		// Maybe user clicked outside char but on the drawed area of it
		it = sortObjects.listIterator(sortObjects.size());
		while (it.hasPrevious()) {
			Entity entity = it.previous();

			if (entity.getDrawedArea().contains(x, y)) {
				return entity;
			}
		}

		return null;
	}

	/** Draw all the objects in game */
	public void draw(GameScreen screen) {
		sort();

		for (Entity entity : sortObjects) {
			entity.draw(screen);
		}
	}

	public void drawText(GameScreen screen) {
		texts.removeAll(textsToRemove);
		textsToRemove.clear();

		try {
			for (Text entity : texts) {
				entity.draw(screen);
			}
		} catch (ConcurrentModificationException e) {
			logger.error("cannot draw text", e);
		}
	}
}

