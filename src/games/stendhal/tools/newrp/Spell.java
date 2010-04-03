package games.stendhal.tools.newrp;

import java.util.LinkedList;
import java.util.List;

/**
 * Represent a spell. Conceptually a spell is sometype of item, but it is shared
 * among all the players. There is only need for one instance of each spell.
 *
 * @author miguel
 *
 */
public class Spell {
	/**
	 * Name of the spell *useless*.
	 */
	String name;

	/**
	 * Skill this spell uses..
	 */
	SkillType skill;

	/**
	 * List of the effects this spell causes.
	 */
	List<Effect> effects;

	/**
	 * Required skill level to cast the spell.
	 */
	int level;

	/**
	 * How much time to spend until being able to cast another spell.
	 */
	int delay;

	/**
	 * Constructor.
	 *
	 * @param type
	 * @param name
	 * @param weight
	 * @param skill
	 */
	public Spell(String name, SkillType skill, int level, int delay) {
		this.name = name;
		this.skill = skill;
		this.level = level;
		this.delay = delay;
		this.effects = new LinkedList<Effect>();
	}

	/**
	 * Add damage effect to this spell.
	 *
	 * @param type
	 * @param amount
	 */
	public void setDamageEffect(DamageType type, int amount) {
		effects.add(new SimpleDamageEffect(type, amount));
	}

	/**
	 * Add healing effect to this spell.
	 *
	 * @param amount
	 */
	public void setHealingEffect(int amount) {
		effects.add(new HealEffect(amount));
	}

	/**
	 * Apply the spell to target that is casted by source.
	 *
	 * @param source
	 * @param target
	 */
	public void apply(RPEntity source, RPEntity target) {
		/*
		 * For each effect apply to target.
		 */
		for (Effect effect : effects) {
			/*
			 * Apply bonus/penalty to the effect.
			 */
			effect.apply(source, target, source.intelligence);
		}
	}

	/**
	 * When a spell is failed by the caster it backfires.
	 *
	 * @param source
	 */
	public void backfire(RPEntity source) {
		/*
		 * Apply the effects to ourselves with less inteligence.
		 */
		for (Effect effect : effects) {
			effect.apply(source, source, source.intelligence / 3);
		}
	}
}

/**
 * Represents a subtype of spell that is a range spell.
 *
 * @author miguel
 *
 */
class RangeSpell extends Spell {
	/**
	 * How much range it has if so. -1 stands for self.
	 */
	int range;

	/**
	 * How much area does it affect?
	 */
	int area;

	public RangeSpell(String name, SkillType skill, int level, int delay,
			int range, int area) {
		super(name, skill, level, delay);
		this.range = range;
		this.area = area;
	}

	public void apply(RPEntity source, RPEntity target) {
		/*
		 * Check target is in range.
		 */
		if (source.distanceTo(target) <= range) {
			super.apply(source, target);
		}
	}
}
