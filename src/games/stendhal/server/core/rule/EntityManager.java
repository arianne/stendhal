package games.stendhal.server.core.rule;

import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;

import java.util.Collection;
/**
 * Ruleset Interface for resolving Entities in Stendhal.
 * 
 * @author Matthias Totz
 */
public interface EntityManager {

	public abstract boolean addItem(DefaultItem item);

	public abstract boolean addCreature(DefaultCreature creature);

	/**
	 * Returns a list of all Creatures that are used at least once.
	 */
	public abstract Collection<Creature> getCreatures();

	/**
	 * returns a list of all Items that are being used at least once.
	 */
	public abstract Collection<Item> getItems();

	/**
	 * Returns the entity or <code>null</code> if the class is unknown.
	 * 
	 * @param clazz
	 *            the creature class, must not be <code>null</code>
	 * @return the entity or <code>null</code>
	 * 
	 */
	public abstract Entity getEntity(String clazz);

	/**
	 * returns the creature or <code>null</code> if the id is unknown.
	 * 
	 * @param id
	 *            the tile id
	 * @return the creature or <code>null</code>
	 */
	public abstract Creature getCreature(String tileset, int id);

	/**
	 * returns the creature or <code>null</code> if the clazz is unknown.
	 * 
	 * @param clazz
	 *            the creature class, must not be <code>null</code>
	 * @return the creature or <code>null</code>
	 * 
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	public abstract Creature getCreature(String clazz);

	/**
	 * Returns the DefaultCreature or <code>null</code> if the clazz is
	 * unknown.
	 * 
	 * @param clazz
	 *            the creature class
	 * @return the creature or <code>null</code>
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	public abstract DefaultCreature getDefaultCreature(String clazz);

	/**
	 * Return true if the Entity is a creature.
	 * 
	 * @param id
	 *            the tile id
	 * @return true if it is a creature, false otherwise
	 */
	public abstract boolean isCreature(String tileset, int id);

	/**
	 * Return true if the Entity is a creature.
	 * 
	 * @param clazz
	 *            the creature class, must not be <code>null</code>
	 * @return true if it is a creature, false otherwise
	 * 
	 */
	public abstract boolean isCreature(String clazz);

	/**
	 * Return true if the Entity is a Item.
	 * 
	 * @param clazz
	 *            the Item class, must not be <code>null</code>
	 * @return true if it is a Item, false otherwise
	 * 
	 */
	public abstract boolean isItem(String clazz);

	/**
	 * Returns the item or <code>null</code> if the clazz is unknown.
	 * 
	 * @param clazz
	 *            the item class, must not be <code>null</code>
	 * @return the item or <code>null</code>
	 * 
	 */
	public abstract Item getItem(String clazz);

}