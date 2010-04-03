package games.stendhal.tools.newrp;

import java.util.List;
import java.util.Random;

/**
 * Represents an active RP entity, like a player or a creature.
 *
 * @author miguel
 *
 */
public abstract class RPEntity {

	/**
	 * The weapon it uses.
	 */
	Item weapon;

	/**
	 * The armor it wears.
	 */
	Item armor;

	/**
	 * The shield it has.
	 */
	Item shield;

	/**
	 * List of skills that this entity has.
	 */
	List<Skill> skills;

	/**
	 * Determine: - Attack and damage done - Weight that can be carried - Damage
	 * that can be absorbed with the shield.
	 */
	int strength;

	/**
	 * Determine: - Attack rate - Handle of weapons.
	 */
	int dexterity;

	/**
	 * Determine: - How fast we can move - How good we can dodge.
	 */
	int agility;

	/**
	 * Determines <li> Amount of HP points <li> How fast we can restore HP and MP
	 * points.
	 */
	int constitution;

	/**
	 * Determine: - Amount of faith to your god. - How strong your prayers are. -
	 * Amount of MP points
	 */
	int faith;

	/**
	 * Determine: - Strengh of the spells. - Amount of MP points - Some NPC
	 * related actions and effect of some spells.
	 */
	int intelligence;

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
	 * Base MP (Mana points).
	 */
	int basemp;

	/**
	 * Actual MP.
	 */
	int mp;

	/**
	 * Base HP (Health points).
	 */
	int basehp;

	/**
	 * Actual HP.
	 */
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
	 * How many turns should be spent until we can cast a spell again.
	 */
	int turnToCastAgain;

	/**
	 * How many turns should be spent until we can attack again?
	 */
	int turnToAttackAgain;

	/**
	 * How many turns should be spent until we can use shield again.
	 */
	int turnToUseShieldAgain;

	/**
	 * How many turns should be spent until we can dodge again.
	 */
	int turnToDodgeAgain;

	/**
	 * How many turns should be spent until we can use weapon as a shield again.
	 */
	int turnToUseWeaponAsShieldAgain;

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
	public void randomizeStats() {
		Random rand = new Random();
		int sum = 0;

		while (sum != 60) {
			strength = rand.nextInt(10) + 6;
			dexterity = rand.nextInt(10) + 6;
			agility = rand.nextInt(10) + 6;
			constitution = rand.nextInt(10) + 6;
			intelligence = rand.nextInt(10) + 6;
			faith = rand.nextInt(10) + 6;

			sum = strength + dexterity + agility + constitution + intelligence
					+ faith;
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
	public void setStats(int str, int dex, int agi, int con, int inte, int wis) {
		this.strength = str;
		this.dexterity = dex;
		this.agility = agi;
		this.constitution = con;
		this.intelligence = inte;
		this.faith = wis;

		initialize();
	}

	/**
	 * Initialize the secondary attributes and apply the race bonus.
	 *
	 */
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
		strength += race.strengh;
		dexterity += race.strengh;
		agility += race.agility;
		constitution += race.constitution;
		intelligence += race.inteligence;
		faith += race.faith;
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
				* (int) (((intelligence + faith) / 2.0) * ((type.inteligence + type.faith) / 2.0));
		weight = weight
				+ diff
				* (int) ((type.strength + type.constitution + type.agility)
						* (strength + constitution + agility) / (3.0 * 3.0));
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
		/**
		 * NOTE: Turn is obtained from RP.
		 */
		int turn = Main.getTurn();

		/*
		 * TODO: Check bonus/penalty on innate armor. For example, trolls get a
		 * penalty for fire attack but a fire giant won't get it. NOTE: It can
		 * be done later
		 */

		/*
		 * Check if we can use shield to block it.
		 */
		if (target.shield != null && turn >= target.turnToUseShieldAgain) {
			/*
			 * We make shield usable only after <i>rate</i> turns
			 */
			target.turnToUseShieldAgain = turn + target.getShieldRate();

			/*
			 * Absorb damage with shield
			 */
			amount = target.shieldAbsorb(type, amount, target.attitude);
		}

		/*
		 * Check if we can use weapon to block it.
		 */
		if (target.weapon != null
				&& turn >= target.turnToUseWeaponAsShieldAgain) {
			/*
			 * We make weapon as a shield usable only after <i>rate</i> turns
			 */
			target.turnToUseWeaponAsShieldAgain = turn + target.getAttackRate();

			/*
			 * Absorb damage with shield
			 */
			amount = target.weaponAbsorb(type, amount, target.attitude);
		}

		/*
		 * Absorb damage with armor. We can always use armor because it is a
		 * passive defense.
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
	protected abstract int getAttackRate();

	/**
	 * Roll a dice to see if hit was successful. We roll 2D6 and substract our
	 * hit and our level/10. If the result if 0 or above, we failed to do the
	 * hit.
	 *
	 * @param attitude
	 * @return
	 */
	protected abstract RollResult doHit(float attitude);

	/**
	 * Returns how many turns we need to spend between attacks. It is related to
	 * how much armor weights ( the more the worse ) and how much is our agility
	 * and our strengh ( the more agile and the stronger we are, the better. )
	 *
	 * @return
	 */
	protected abstract int getDodgeRate();

	/**
	 * Roll a dice to see if dodge was successful. We roll 4D6 and substract
	 * our dodge and our level/20. If the result if 0 or above, we failed to do
	 * the dodge.
	 *
	 * @param attitude
	 * @return
	 */
	protected abstract RollResult doDodge(float attitude);

	/**
	 * Returns how many turns we need to spend between shield defense. It is
	 * related to how much shield weights ( the more the worse ) and how much is
	 * our dextrexity and our strengh ( the better we handle the shield and the
	 * stronger we are, the better. )
	 *
	 * @return
	 */
	protected abstract int getShieldRate();

	/**
	 * Once damage is done check how much of it is absorbed by shield. Damage
	 * absorbed is always between two not rand values min and max, then damage
	 * absorbed is randomly choosen between them.
	 *
	 * @param damage
	 * @param attitude
	 */
	protected abstract int shieldAbsorb(DamageType type, int amount,
			float attitude);

	/**
	 * Once damage is done check how much of it is absorbed by the weapon used
	 * as a shield. Damage absorbed is always between two not rand values min
	 * and max, then damage absorbed is randomly choosen between them.
	 *
	 * @param damage
	 * @param attitude
	 */
	protected abstract int weaponAbsorb(DamageType type, int amount,
			float attitude);

	/**
	 * Once damage is done check how much of it is absorbed by armor. Damage
	 * absorbed is always between two not rand values min and max, then damage
	 * absorbed is randomly choosen between them.
	 *
	 * @param damage
	 */
	protected abstract int armorAbsorb(DamageType type, int amount);

	/**
	 * Finally apply the rest of the not absorbed damage to entity.
	 *
	 * @param damage
	 */
	private void apply(DamageType type, int amount) {
		if (amount > 0) {
			hp = hp - amount;
		}
	}

	/**
	 * Attack the entity target in the given turn.
	 *
	 * @param target
	 * @param turn
	 */
	public void rangeAttack(RPEntity target, int turn) {
		/*
		 * Check if it is our turn to attack
		 */
		if (turn >= turnToAttackAgain && distanceTo(target) <= weapon.range) {
			/*
			 * We make weapon usable only after <i>rate</i> turns
			 */
			turnToAttackAgain = turn + getAttackRate();

			/*
			 * Roll dice to see if we are able to do a hit.
			 */
			RollResult dice = doHit(attitude);
			if (dice.success()) {
				for (Effect effect : weapon.damage) {
					effect.apply(this, target, strength);
				}
			}
		}
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
		if (turn >= turnToAttackAgain) {
			/*
			 * We make weapon usable only after <i>rate</i> turns
			 */
			turnToAttackAgain = turn + getAttackRate();

			/*
			 * Roll dice to see if we are able to do a hit.
			 */
			RollResult dice = doHit(attitude);
			if (dice.success()) {
				/*
				 * Check if our oponent can dodge it.
				 */
				boolean dodge = false;
				if (turn >= target.turnToDodgeAgain) {
					target.turnToDodgeAgain = turn + target.getDodgeRate();

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
						effect.apply(this, target, strength);
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
		StringBuilder os = new StringBuilder();
		os.append("ENTITY: " + sex + " " + race + " " + type + "\n");
		os.append("Level: " + level + "\n");
		os.append("STR: " + strength + "\n");
		os.append("DEX: " + dexterity + "\n");
		os.append("AGI: " + agility + "\n");
		os.append("CON: " + constitution + "\n");
		os.append("INT: " + intelligence + "\n");
		os.append("WIS: " + faith + "\n");
		os.append("\n");
		os.append("HP: " + basehp + "\n");
		os.append("MP: " + basemp + "\n");
		os.append("WEI: " + weight + "\n");
		os.append("SPE: " + speed + "\n");

		return os.toString();
	}

	/**
	 * Set the attitude about how offensive(1) or defensive(0) the entity is.
	 *
	 * @param att
	 */
	public void setAttitude(float att) {
		attitude = att;
	}

	/**
	 * Roll a dice to see if we are able to cast the spell with the given
	 * attitude.
	 *
	 * @param spell
	 * @param attitude
	 * @return
	 */
	protected abstract RollResult doCast(Spell spell, float attitude);

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
			/*
			 * TODO: We could have a turn to cast delay for each spell.
			 */
			turnToCastAgain = turn + spell.delay;

			RollResult dice = doCast(spell, attitude);

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
 * Sex of the entity. It is not relevant at all.
 *
 * @author miguel
 *
 */
enum Sex {
	MALE, FEMALE
}

/**
 * Races available and bonus they have for main stats. Each race has bonus and
 * penalties that apply to the stats.
 *
 * @author miguel
 *
 */
enum Race {
	/* STR AGI DEX CON INT WIS */
	HUMAN(0, 0, 0, 0, 0, 0),
	DWARF(3, -1, 0, 1, -1, 0),
	ORC(2, -1, 0, 1, -3, 1),
	ELF(-1, 2, 1, -1, 1, 0);

	Race(double str, double dex, double agi, double con, double inte, double wis) {
		this.strengh = str;
		this.dexterity = dex;
		this.agility = agi;
		this.constitution = con;
		this.inteligence = inte;
		this.faith = wis;
	}

	double strengh;

	double dexterity;

	double agility;

	double constitution;

	double faith;

	double inteligence;
}

/**
 * Class of the entity. It applies a modifier to each It gives more or less
 * importance to each attribute based on the class that the player chose for his
 * avatar.
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
		this.strength = str;
		this.dexterity = dex;
		this.agility = agi;
		this.constitution = con;
		this.inteligence = inte;
		this.faith = wis;
	}

	double strength;

	double dexterity;

	double agility;

	double constitution;

	double faith;

	double inteligence;
}
