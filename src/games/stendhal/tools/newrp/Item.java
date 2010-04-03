package games.stendhal.tools.newrp;

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
	 * Type of item.
	 */
	ItemType type;

	/**
	 * Name of the item *useless*.
	 */
	String name;

	/**
	 * How much kg weigth the item.
	 */
	int weight;

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
	SkillType skill;

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
	public Item(ItemType type, String name, int weight, SkillType skill) {
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
	 *
	 * @param type
	 * @param amount
	 */
	public void setDamage(DamageType type, int amount) {
		damage.add(new SimpleDamageEffect(type, amount));
	}

	/**
	 * Adds a protective effect on the item.
	 *
	 * @param type
	 * @param amount
	 */
	public void setProtection(DamageType type, int amount) {
		protect.add(new SimpleDamageEffect(type, amount));
	}
}

/**
 * Subclass for weapons.
 *
 * @author miguel
 *
 */
class Weapon extends Item {
	public Weapon(String name, int weight, SkillType skill) {
		super(ItemType.WEAPON, name, weight, skill);
		this.throwable = false;
	}
}

class RangeWeapon extends Weapon {
	public RangeWeapon(String name, int weight, SkillType skill, int range) {
		super(name, weight, skill);
		this.throwable = true;
		this.range = range;
	}
}

/**
 * Subclass for armors.
 *
 * @author miguel
 *
 */
class Armor extends Item {
	public Armor(String name, int weight, SkillType skill) {
		super(ItemType.ARMOR, name, weight, skill);
	}
}

/**
 * Subclass for shields.
 *
 * @author miguel
 *
 */
class Shield extends Item {
	public Shield(String name, int weight, SkillType skill) {
		super(ItemType.SHIELD, name, weight, skill);
	}
}
