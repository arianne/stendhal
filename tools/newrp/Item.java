package newrp;

import java.util.LinkedList;
import java.util.List;

/**
 * We have several types of weapons.
 * @author miguel
 *
 */
enum ItemType {
	/**
	 * Anything with an offensive profile.
	 */
	WEAPON,
	/**
	 * Passive defense item.
	 */
	ARMOR,
	/**
	 * A active defense item.
	 */
	SHIELD
}

/**
 * Represent an item.
 *
 * @author miguel
 *
 */
public class Item {
	/**
	 * Type of item
	 */
	ItemType type;

	/**
	 * Name of the item *useless*
	 */
	String name;

	/**
	 * How much kg weigth the item.
	 */
	float weight;

	/**
	 * List of damages the item does.
	 */
	List<Effect> damage;

	/**
	 * List of damages this item protect from.
	 */
	List<Effect> protect;

	/**
	 * Skill this item uses.
	 */
	Skill skill;

	/**
	 * If this weapon is designed to be thrown.
	 */
	boolean throwable;

	/**
	 * How much range it has if so.
	 */
	int range;

	/**
	 * Constructor.
	 *
	 * @param type
	 * @param name
	 * @param weight
	 * @param skill
	 */
	public Item(ItemType type, String name, float weight, Skill skill) {
		this.type = type;
		this.name = name;
		this.weight = weight;
		this.skill = skill;
		this.throwable = false;
		this.range = 0;
		damage = new LinkedList<Effect>();
		protect = new LinkedList<Effect>();
	}

	/**
	 * Adds a damage effect on the item.
	 * @param type
	 * @param amount
	 */
	public void setDamage(DamageType type, int amount) {
		damage.add(new DamageEffect(type, amount));
	}

	/**
	 * Adds a protective effect on the item.
	 * @param type
	 * @param amount
	 */
	public void setProtection(DamageType type, int amount) {
		protect.add(new DamageEffect(type, amount));
	}

}
