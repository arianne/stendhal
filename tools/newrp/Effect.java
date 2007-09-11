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
	
	/* TODO: Add apply interface for a area too 
	 *
	 * void apply(RPEntity source, int x, int y, int rulingAttribute);
	 */
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

		/*
		 * Compute the min and the max value.
		 * TODO: The result is not clearly related to <b>amount</b> attribute.
		 *   It would be good that it is a simple relation damage<->amount.
		 */
		int min = (int) ((amount / 10f)
				* ((level * level / 1000.0f) + level / 4f + 2) * (rulingAttribute
				* rulingAttribute / 256f));
		int max = (int) ((amount / 10f)
				* ((level * level / 250.0f) + level + 4) * (rulingAttribute
				* rulingAttribute / 256f));

		/*
		 * Randomly choose a value between min and max
		 */
		int done = Dice.between(min, max);

		/*
		 * Make source does that amount to target.
		 */
		source.damage(target, type, done);
	}
}

/**
 * A simpler approach to damage effect
 * @author miguel
 *
 */
class SimpleDamageEffect extends DamageEffect {

	public SimpleDamageEffect(DamageType type, int amount) {
	    super(type, amount);
    }
	
	public void apply(RPEntity source, RPEntity target, int rulingAttribute) {
		int done=SimpleRPEntity.calculateAbsorb(this, source.level, rulingAttribute);
		
		source.damage(target, type, done);
	}
}
