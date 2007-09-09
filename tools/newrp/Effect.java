package newrp;
/**
 * Interface for effect. Mainly healing and damage.
 *
 * @author miguel
 *
 */
public interface Effect {
	/**
	 * Apply the effect from the source to the target considering the ruling
	 * attribute ( STR, AGI, DEX, CON, INT or WIS )
	 *
	 * @param source
	 * @param target
	 * @param rulingAttribute
	 */
	void apply(RPEntity source, RPEntity target, int rulingAttribute);
}

/**
 * Healing effect.
 *
 * @author miguel
 *
 */
class HealEffect implements Effect {
	/**
	 * How much do we heal? 
	 */
	int amount;

	public HealEffect(int amount) {
		this.amount = amount;
	}

	public void apply(RPEntity source, RPEntity target, int rulingAttribute) {
		/*
		 * TODO: Alter the healing effect based on level.
		 */
		
		/*
		 * Simply add the new HP.
		 */
		target.hp = target.hp + amount;
	}
}

/**
 * Type of damage.
 *
 * @author miguel
 *
 */
enum DamageType {
	/** Damage caused by a sword for example. */
	SLASH, 
	/** Damage caused by a hammer. */
	BASH, 
	/** Damage caused by a knife or an arrow */
	STAB, 
	/** Damage caused by a fire */ 
	FIRE, 
	/** Damage caused by ice or by a ice spell */
	COLD, 
	/** Damage caused by acid element */
	ACID, 
	/** Damage because of disease or a spell */
	DISEASE, 
	/** Damage because of light. */
	LIGHT, 
}

/**
 * Damage effect
 *
 * @author miguel
 *
 */
class DamageEffect implements Effect {
	/**
	 * Type of damage done.
	 */
	DamageType type;

	/**
	 * Amount of damage done.
	 */
	int amount;

	public DamageEffect(DamageType type, int amount) {
		this.type = type;
		this.amount = amount;
	}

	public void apply(RPEntity source, RPEntity target, int rulingAttribute) {
		/* Missing skill: *(skill level/10f) */
		int level = source.level;

		int min = (int) ((amount / 10f)
				* ((level * level / 1000.0f) + level / 4f + 2) * (rulingAttribute
				* rulingAttribute / 256f));
		int max = (int) ((amount / 10f)
				* ((level * level / 250.0f) + level + 4) * (rulingAttribute
				* rulingAttribute / 256f));

		int done = Dice.between(min, max);

		source.damage(target, type, done);
	}
}
