package newrp;

import java.util.Random;

/**
 * Represents a active RP entity, like a player or a creature
 *
 * @author miguel
 *
 */
public class RPEntity {

	/**
	 * The weapon it uses.
	 */
	Item weapon;

	/**
	 * The armor it wears
	 */
	Item armor;

	/**
	 * The shield it has.
	 */
	Item shield;

	/**
	 * Determine: - Attack and damage done - Weight that can be carried - Damage
	 * that can be absorbed with the shield.
	 */
	int strengh;

	/**
	 * Determine: - Attack rate - Handle of weapons
	 */
	int dextrexity;

	/**
	 * Determine: - How fast we can move - How good we can dodge
	 */
	int agility;

	/**
	 * Determine: - Amount of HP points - How fast we can restore HP and MP
	 * points
	 */
	int constitution;

	/**
	 * Determine: - Amount of faith to your god. - How strong your prayers are. -
	 * Amount of MP points
	 */
	int wisdom;

	/**
	 * Determine: - Strengh of the spells. - Amount of MP points - Some NPC
	 * related actions and effect of some spells.
	 */
	int inteligence;

	/**
	 * It is an RP related attribute to measure how good/evil you are.
	 */
	int karma;

	/**
	 * Level of the entity.
	 */
	int level;

	/**
	 * Which Race this entity belongs to. Each race has different racial bonus.
	 */
	Race race;

	/**
	 * Class this entity belong to.
	 */
	School type;

	/**
	 * Sex this entity has.
	 */
	Sex sex;

	/**
	 * Base MP (Mana points)
	 */
	int basemp;

	int mp;

	/**
	 * Base HP (Health points)
	 */
	int basehp;

	int hp;

	/**
	 * Max Weight we can carry.
	 */
	float weight;

	/**
	 * Max Speed we can move.
	 */
	float speed;

	/**
	 * How offensive/defensive the entity is, with 0 as most defensive and 1 as
	 * most offensive.
	 */
	float attitude;

	/**
	 * Constructor. Creates the entity with level 0.
	 *
	 * @param race
	 * @param type
	 * @param sex
	 */
	public RPEntity(Race race, School type, Sex sex) {
		this.race = race;
		this.type = type;
		this.sex = sex;

		this.level = 0;
	}

	/**
	 * Equip an item into the entity.
	 *
	 * @param item
	 *            weapon, shield or armor.
	 */
	public void equip(Item item) {
		switch (item.type) {
		case WEAPON:
			weapon = item;
			break;
		case ARMOR:
			armor = item;
			break;
		case SHIELD:
			shield = item;
			break;
		}
	}

	/**
	 * Generate random stats for entity such that all the basic attributes sum
	 * 60.
	 */
	public void rand() {
		Random rand = new Random();
		int sum = 0;

		while (sum != 60) {
			strengh = rand.nextInt(10) + 6;
			dextrexity = rand.nextInt(10) + 6;
			agility = rand.nextInt(10) + 6;
			constitution = rand.nextInt(10) + 6;
			inteligence = rand.nextInt(10) + 6;
			wisdom = rand.nextInt(10) + 6;

			sum = strengh + dextrexity + agility + constitution + inteligence
					+ wisdom;
		}

		initialize();
	}

	/**
	 * Create an entity with the given stats and then apply the racial bonus.
	 *
	 * @param str
	 * @param dex
	 * @param agi
	 * @param con
	 * @param inte
	 * @param wis
	 */
	public void set(int str, int dex, int agi, int con, int inte, int wis) {
		this.strengh = str;
		this.dextrexity = dex;
		this.agility = agi;
		this.constitution = con;
		this.inteligence = inte;
		this.wisdom = wis;

		initialize();
	}

	protected void initialize() {
		applyRaceBonus();

		level = 0;
		basehp = 80;
		hp = basehp;
		basemp = 10;
		weight = 40;
		speed = 0.6f;
	}

	/**
	 * Each race has a bonus/penalty on each of the main attributes.
	 *
	 */
	private void applyRaceBonus() {
		strengh += race.strengh;
		dextrexity += race.strengh;
		agility += race.agility;
		constitution += race.constitution;
		inteligence += race.inteligence;
		wisdom += race.wisdom;
	}

	/**
	 * Level the entity up(or down) to the new level.
	 *
	 * @param newlevel
	 *            the new level the entity will have.
	 */
	public void level(int newlevel) {
		int diff = newlevel - level;
		level = newlevel;

		basehp = basehp
				+ diff
				* (int) (constitution * constitution * type.constitution / 10.0);
		basemp = basemp
				+ diff
				* (int) (((inteligence + wisdom) / 2.0) * ((type.inteligence + type.wisdom) / 2.0));
		weight = weight
				+ diff
				* (int) ((type.strengh + type.constitution + type.agility)
						* (strengh + constitution + agility) / (3.0 * 3.0));
		speed = speed + diff * (agility / 2000f);

		hp = basehp;
		mp = basemp;
	}

	/**
	 * This method is used to cause damage to target of the given type and in
	 * the specified amount.
	 *
	 * @param target
	 * @param type
	 * @param amount
	 */
	void damage(RPEntity target, DamageType type, int amount) {
		int turn = 0;

		/*
		 * TODO: Check bonus/penalty on innate armor.
		 */
		
		/*
		 * Check if we can use shield to block it.
		 */
		if (target.shield != null && turn % target.getShieldRate() == 0) {
			/*
			 * Absorb damage with shield
			 */
			amount = target.shieldAbsorb(type, amount, target.attitude);
		}

		/*
		 * TODO: Check if we can use weapon to block it.
		 */

		/*
		 * Absorb damage with armor
		 */
		if (target.armor != null) {
			amount = target.armorAbsorb(type, amount);
		}

		/*
		 * Apply the rest of damage to defender.
		 */
		target.apply(type, amount);
	}

	/**
	 * Returns how many turns we need to spend between attacks. It is related to
	 * how much weapons weights ( the more the worse ) and how much is our
	 * dextrexity and our strengh ( the better we handle the weapon and the
	 * stronger we are, the better. )
	 *
	 * @return
	 */
	private int getAttackRate() {
		int rate = 1 + (int) (weapon.weight * 256) / (dextrexity * strengh);
		return rate;
	}

	/**
	 * The higher the better. It is a measure of how good are the hits we can
	 * do. Doing a hit is relatively simply as long as you are strong enough to
	 * handle the weapon with your dextrexity.
	 */
	private float getHitQuality(float attitude) {
		float quality = dextrexity * (strengh / weapon.weight) * attitude * 100
				/ 256f;
		return quality;
	}

	/**
	 * Roll a dice to see if hit was successfull. We roll 2D6 and substract our
	 * hit and our level/10. If the result if 0 or above, we failed to do the
	 * hit.
	 *
	 * @param attitude
	 * @return
	 */
	private DiceResult doHit(float attitude) {
		int roll = Dice.rND6(2);
		if ((roll - (int) getHitQuality(attitude) - level / 10f) >= 0) {
			return DiceResult.FAILURE;
		} else {
			return DiceResult.SUCCESS;
		}
	}

	/**
	 * Returns how many turns we need to spend between attacks. It is related to
	 * how much armor weights ( the more the worse ) and how much is our agility
	 * and our strengh ( the more agile and the stronger we are, the better. )
	 *
	 * @return
	 */
	private int getDodgeRate() {
		int rate = 1 + (int) (armor.weight * 256) / (agility * strengh);
		return rate;
	}

	/**
	 * The higher the better. It is a measure of how good are the dodge we can
	 * do. Doing a dodge is hard, even worse when we have armor. Don't expect to
	 * dodge with your full plate armor
	 */
	private float getDodgeQuality(float attitude) {
		float quality = agility * (strengh / armor.weight) * (1 - attitude);
		return quality;
	}

	/**
	 * Roll a dice to see if dodge was successfull. We roll 4D6 and substract
	 * our dodge and our level/20. If the result if 0 or above, we failed to do
	 * the dodge.
	 *
	 * @param attitude
	 * @return
	 */
	private DiceResult doDodge(float attitude) {
		int roll = Dice.rND6(4);
		if ((roll - (int) getDodgeQuality(attitude) - level / 20.0) >= 0) {
			return DiceResult.FAILURE;
		} else {
			return DiceResult.SUCCESS;
		}
	}

	/**
	 * Returns how many turns we need to spend between shield defense. It is
	 * related to how much shield weights ( the more the worse ) and how much is
	 * our dextrexity and our strengh ( the better we handle the shield and the
	 * stronger we are, the better. )
	 *
	 * @return
	 */
	private int getShieldRate() {
		int rate = 1 + (int) (shield.weight * 256) / (dextrexity * strengh);
		return rate;
	}

	/**
	 * The higher the better. It is a measure of how good are we handle the
	 * shield.
	 */
	private float getShieldQuality(float attitude) {
		float quality = dextrexity * (strengh / shield.weight) * (1 - attitude);
		return quality;
	}

	/**
	 * Once damage is done check how much of it is absorbed by shield. Damage
	 * absorbed is always between two not rand values min and max, then damage
	 * absorbed is randomly choosen between them.
	 *
	 * @param damage
	 * @param attitude
	 */
	private int shieldAbsorb(DamageType type, int amount, float attitude) {
		/* Missing skill: *(skill level/10f) */
		float min_coef = getShieldQuality(attitude)
				* ((level * level / 10000.0f) + level / 40f + 0.25f);
		float max_coef = getShieldQuality(attitude)
				* ((level * level / 2500.0f) + level / 10f + 1);

		for (Effect effect : shield.protect) {
			if (effect instanceof DamageEffect) {
				DamageEffect absorb = (DamageEffect) effect;

				if (absorb.type == type) {
					int min = (int) ((absorb.amount / 10f) * min_coef);
					int max = (int) ((absorb.amount / 10f) * max_coef);

					int shieldtakes = Dice.between(min, max);

					// System.out.println("shield removes
					// ["+min+","+max+"]->"+shieldtakes);
					amount = amount - shieldtakes;
				}
			}
		}

		return amount;
	}

	/**
	 * Once damage is done check how much of it is absorbed by armor. Damage
	 * absorbed is always between two not rand values min and max, then damage
	 * absorbed is randomly choosen between them.
	 *
	 * @param damage
	 */
	private int armorAbsorb(DamageType type, int amount) {
		/* Missing skill: *(skill level/10f) */
		float min_coef = ((level * level / 1200.0f) + level / 40f + 0.25f);
		float max_coef = ((level * level / 300.0f) + level / 10f + 1);

		for (Effect effect : armor.protect) {
			if (effect instanceof DamageEffect) {
				DamageEffect absorb = (DamageEffect) effect;

				if (absorb.type == type) {
					int min = (int) ((absorb.amount / 10f) * min_coef);
					int max = (int) ((absorb.amount / 10f) * max_coef);

					int armorTakes = Dice.between(min, max);

					// System.out.println("armor removes
					// ["+min+","+max+"]->"+armorTakes);
					amount = amount - armorTakes;
				}
			}
		}
		return amount;
	}

	/**
	 * Finally apply the rest of the not absorbed damage to entity.
	 *
	 * @param damage
	 */
	private void apply(DamageType type, int amount) {
		hp = hp - amount;
	}

	/**
	 * Attack the entity target in the given turn.
	 * 
	 * @param target
	 * @param turn
	 */
	public void attack(RPEntity target, int turn) {
		/*
		 * Check if it is our turn to attack
		 */
		if (turn % getAttackRate() == 0) {
			/*
			 * Roll dice to see if we are able to do a hit.
			 */
			DiceResult dice = doHit(attitude);
			if (dice.success()) {
				/*
				 * Check if our oponent can dodge it.
				 */
				boolean dodge = false;
				if (turn % target.getDodgeRate() == 0) {
					/*
					 * Roll dice to see if we dodge it.
					 */
					dice = target.doDodge(target.attitude);
					if (dice.success()) {
						dodge = true;
					}
				}

				if (!dodge) {
					for (Effect effect : weapon.damage) {
						effect.apply(this, target, strengh);
					}
				}
			}
		}
	}

	/**
	 * Return true if entity is death.
	 *
	 * @return
	 */
	public boolean isDeath() {
		return hp <= 0;
	}

	/**
	 * Textual representation of the entity.
	 */
	@Override
	public String toString() {
		StringBuffer os = new StringBuffer();
		os.append("ENTITY: " + sex + " " + race + " " + type + "\n");
		os.append("Level: " + level + "\n");
		os.append("STR: " + strengh + "\n");
		os.append("DEX: " + dextrexity + "\n");
		os.append("AGI: " + agility + "\n");
		os.append("CON: " + constitution + "\n");
		os.append("INT: " + inteligence + "\n");
		os.append("WIS: " + wisdom + "\n");
		os.append("\n");
		os.append("HP: " + basehp + "\n");
		os.append("MP: " + basemp + "\n");
		os.append("WEI: " + weight + "\n");
		os.append("SPE: " + speed + "\n");

		return os.toString();
	}

	/**
	 * Set the attitude about how offensive(1) or defensive(0) the entity is.
	 * @param att
	 */
	public void setAttitude(float att) {
		attitude = att;
	}

	/**
	 * How many turns should be spent until we can cast a spell again.
	 */
	int turnToCastAgain;

	/**
	 * How good or bad was the spell we casted.
	 * @param spell
	 * @param attitude
	 * @return
	 */
	public float getCastQuality(Spell spell, float attitude) {
		float quality = ((inteligence + wisdom) / 2 - (level - spell.level))
				* attitude;
		return quality;
	}

	/**
	 * Roll a dice to see if we are able to cast the spell with the given attitude.
	 * @param spell
	 * @param attitude
	 * @return
	 */
	private DiceResult doCast(Spell spell, float attitude) {
		int roll = Dice.rND6(2);
		if ((roll - (int) getCastQuality(spell, attitude) - level / 10f) >= 0) {
			return DiceResult.FAILURE;
		} else {
			return DiceResult.SUCCESS;
		}
	}

	/**
	 * Cast the given spell on the target entity at the given turn.
	 * 
	 * @param spell
	 * @param target
	 * @param turn
	 */
	public void cast(Spell spell, RPEntity target, int turn) {
		/*
		 * Each spell takes a time to reload. The harder the spell the more time
		 * until you can cast another again.
		 */
		if (turn > turnToCastAgain) {
			turnToCastAgain = turn + spell.delay;

			DiceResult dice = doCast(spell, attitude);

			if (dice.success()) {
				/*
				 * The spell is correctly casted.
				 */
				spell.apply(this, target);
			} else {
				/*
				 * The spell fails. There is a 10% + 3 for each level of
				 * difference of backfiring the spell.
				 */
				int backfire = 10 + (spell.level - level) * 3;
				if (Dice.r1D100() < backfire) {
					/*
					 * Backfired.
					 */
					spell.backfire(this);
				}
			}
		}
	}

	public int distanceTo(RPEntity target) {
		/* TODO: implement it */
		return 0;
	}

}

/**
 * Sex of the entity.
 * It is not relevant at all.
 *
 * @author miguel
 *
 */
enum Sex {
	MALE, FEMALE
}

/**
 * Races available and bonus they have for main stats.
 * Each race has bonus and penalties that apply to the stats.
 *
 * @author miguel
 *
 */
enum Race {
		/* STR AGI DEX CON INT WIS */
	HUMAN	(0, 0, 0, 0, 0, 0), 
	DWARF	(3, -1, 0, 1, -1, 0), 
	ORC		(2, -1, 0, 1, -3, 1), 
	ELF		(-1, 2, 1, -1, 1, 0);

	Race(double str, double dex, double agi, double con, double inte, double wis) {
		this.strengh = str;
		this.dextrexity = dex;
		this.agility = agi;
		this.constitution = con;
		this.inteligence = inte;
		this.wisdom = wis;
	}

	double strengh;

	double dextrexity;

	double agility;

	double constitution;

	double wisdom;

	double inteligence;
}

/**
 * Class of the entity. It applies a modifier to each
 * It gives more or less importance to each attribute based on the 
 * class that the player chose for his avatar. 
 *
 * @author miguel
 *
 */
enum School {
	/* STR AGI DEX CON INT WIS */
	SCOUT(0.9, 1.5, 1.5, 0.8, 0.4, 0.8), 
	WARRIOR(1.6, 0.8, 1.0, 1.6, 0.2, 0.6), 
	PRIEST(0.6, 0.8, 0.9, 0.7, 1.0, 2.0), 
	MAGE(0.3, 0.8, 0.7, 0.5, 2.0, 1.0);

	School(double str, double dex, double agi, double con, double inte,
			double wis) {
		this.strengh = str;
		this.dextrexity = dex;
		this.agility = agi;
		this.constitution = con;
		this.inteligence = inte;
		this.wisdom = wis;
	}

	double strengh;

	double dextrexity;

	double agility;

	double constitution;

	double wisdom;

	double inteligence;
}

/**
 * Result of the roll of the Dice.
 *
 * @author miguel
 *
 */
enum DiceResult {
	SUCCESS, 
	CRITICAL_SUCCESS, 
	FAILURE, 
	CRITICAL_FAILURE;

	public boolean success() {
		return this == SUCCESS || this == CRITICAL_SUCCESS;
	}
}
