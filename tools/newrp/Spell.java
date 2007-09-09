package newrp;

import java.util.LinkedList;
import java.util.List;

public class Spell {
	/**
	 * Name of the spell *useless*
	 */
	String name;

	/**
	 * Skill this spell uses.
	 */
	Skill skill;

	/**
	 * List of the effects this spell causes
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
	 * If this spell is designed to be thrown.
	 */
	boolean throwable;
	/**
	 * How much range it has if so.
	 * -1 stands for self.
	 */
	int range;
	/**
	 * How much area does it affect
	 */
	int area;

	/**
	 * Constructor.
	 * @param type
	 * @param name
	 * @param weight
	 * @param skill
	 */
	public Spell(String name, Skill skill, int level, int range, int area) {
		this.name=name;
		this.skill=skill;
		this.level=level;
		this.effects=new LinkedList<Effect>();

		if(range>0) {
			throwable=true;
			this.range=range;
			this.area=area;
		} else {
			throwable=false;
			this.range=range;
			this.area=area;
		}
	}

	public void setDamageEffect(DamageType type, int amount) {
		effects.add(new DamageEffect(type, amount));
	}

	public void setHealingEffect(int amount) {
		effects.add(new HealEffect(amount));
	}

	public void apply(RPEntity source, RPEntity target) {
		/*
		 * Check target is in range.
		 */
		if(source.distanceTo(target)<=range || source==target){
			/*
			 * For each effect apply to target.
			 */
			for(Effect effect: effects) {
				/*
				 * Apply bonus/penalty to the effect.
				 */
				effect.apply(source, target, source.inteligence);
			}
		}
	}

	public void backfire(RPEntity source) {
		/*
		 * Apply the effects to ourselves with less inteligence.
		 */
		for(Effect effect: effects) {
			effect.apply(source, source, source.inteligence/3);
		}
	}
}
