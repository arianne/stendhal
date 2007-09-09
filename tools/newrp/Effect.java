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
	int amount;

	public HealEffect(int amount) {
		this.amount = amount;
	}

	public void apply(RPEntity source, RPEntity target, int rulingAttribute) {
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
	SLASH, BASH, STAB, FIRE, COLD, ACID, DISEASE, LIGHT, DEATH,
}

/**
 * Damage effect
 *
 * @author miguel
 *
 */
class DamageEffect implements Effect {
	DamageType type;

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
