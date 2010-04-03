package games.stendhal.tools.newrp;

/**
 * This is a simpler, human understandable implementation of the RP.
 *
 * @author miguel
 *
 */
public class SimpleRPEntity extends RPEntity {
	public SimpleRPEntity(Race race, School type, Sex sex) {
		super(race, type, sex);
	}

	@Override
	protected int getAttackRate() {
		/*
		 * REASONING: The more the weapon weight the slower. The stronger we are
		 * the faster.
		 */
		int rate = 20 + 3 * weapon.weight - (int) (strength * type.strength);

		if (rate <= 0) {
			rate = 1;
		}

		return rate;
	}

	protected int getDodgeRate() {
		/*
		 * REASONING: The more our inventory weight the less often we can dodge.
		 */
		int weaponWeight = 0;
		int armorWeight = 0;
		int shieldWeight = 0;

		if (weapon != null) {
			weaponWeight = weapon.weight;
		}
		if (armor != null) {
			armorWeight = armor.weight;
		}
		if (shield != null) {
			shieldWeight = shield.weight;
		}

		int rate = 10 + 3 * (weaponWeight + armorWeight + shieldWeight)
				- (int) (agility * type.agility);

		if (rate <= 0) {
			rate = 1;
		}

		return rate;
	}

	protected int getShieldRate() {
		/*
		 * REASONING: The more our shield weight the less often we can use it.
		 * The more dextrexity we have the more often we can use it.
		 */
		int rate = 15 + 3 * shield.weight - (int) (dexterity * type.dexterity);

		if (rate <= 0) {
			rate = 1;
		}

		return rate;
	}

	protected RollResult doHit(float attitude) {
		/*
		 * REASONING: The stronger and the more dextrexity we are the simpler is
		 * to do a hit.
		 */
		if (Dice.r1D20() < (strength * type.strength + dexterity
				* type.dexterity)
				* attitude) {
			return RollResult.SUCCESS;
		} else {
			return RollResult.FAILURE;
		}
	}

	protected RollResult doDodge(float attitude) {
		/*
		 * REASONING: The stronger and the more agile we are the simpler is to
		 * dodge a hit. Dodge a hit is harder than doing a hit. ( Twice as
		 * harder )
		 */
		if (Dice.rND20(2) < (strength * type.strength + agility * type.agility)
				* attitude) {
			return RollResult.SUCCESS;
		} else {
			return RollResult.FAILURE;
		}
	}

	protected RollResult doCast(Spell spell, float attitude) {
		/*
		 * REASONING: The more inteligent the simpler. The harder the spell the
		 * more hard to cast it.
		 */
		if (Dice.r1D20() + (spell.level - level) < (intelligence
				* type.inteligence + faith * type.faith)
				* attitude) {
			return RollResult.SUCCESS;
		} else {
			return RollResult.FAILURE;
		}
	}

	static int calculateAbsorb(DamageEffect damage, int level,
			int rulingAttribute) {
		int base = damage.amount + damage.amount / 5 * (rulingAttribute - 10);

		/*
		 * We calculate a min and max values and apply bonus for level.
		 */
		int min = base + base / 4 * level / 3;
		int max = base + base / 2 * level / 3;

		int absorbed = Dice.between(min, max);
		return absorbed;
	}

	protected int shieldAbsorb(DamageType type, int amount, float attitude) {
		for (Effect effect : shield.protect) {
			if (effect instanceof DamageEffect) {
				DamageEffect damage = (DamageEffect) effect;

				if (damage.type == type) {
					int absorbed = calculateAbsorb(damage, level,
							(int) (dexterity * this.type.dexterity));
					amount = amount - absorbed;
				}
			}
		}

		return amount;
	}

	protected int weaponAbsorb(DamageType type, int amount, float attitude) {
		for (Effect effect : weapon.protect) {
			if (effect instanceof DamageEffect) {
				DamageEffect damage = (DamageEffect) effect;

				if (damage.type == type) {
					int absorbed = calculateAbsorb(damage, level,
							(int) (strength * this.type.strength));
					amount = amount - absorbed;
				}
			}
		}

		return amount;
	}

	protected int armorAbsorb(DamageType type, int amount) {
		for (Effect effect : armor.protect) {
			if (effect instanceof DamageEffect) {
				DamageEffect damage = (DamageEffect) effect;

				if (damage.type == type) {
					/*
					 * Armor is a passive element and so is not ruled by any
					 * attribute.
					 */
					int absorbed = calculateAbsorb(damage, level, 10);
					amount = amount - absorbed;
				}
			}
		}

		return amount;
	}
}
