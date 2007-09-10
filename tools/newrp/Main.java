package newrp;

/**
 * Main class.
 * 
 * Create a few items and run a combat between two entities.
 * 
 * @author miguel
 *
 */
public class Main {
	public static void main(String[] args) {
		/*
		 * Creates a sword.
		 */
		Item sword = new Weapon("sword", 4, SkillType.SWORDING);
		sword.setDamage(DamageType.SLASH, 10);

		/*
		 * Creates a leather armor.
		 */
		Item armor = new Armor("leather armor", 8, SkillType.LIGHT_ARMOR);
		armor.setProtection(DamageType.SLASH, 10);
		armor.setProtection(DamageType.BASH, 15);
		armor.setProtection(DamageType.STAB, 6);

		/*
		 * Creates a shield.
		 */
		Item shield = new Shield("leather shield", 10, SkillType.SHIELDING);
		shield.setProtection(DamageType.SLASH, 10);
		shield.setProtection(DamageType.BASH, 10);
		shield.setProtection(DamageType.STAB, 8);

		/*
		 * Define the spell Ball of fire.
		 */
		Spell balloffire = new RangeSpell("Ball of fire", SkillType.ILLUSION, 0, 10, 0);
		balloffire.setDamageEffect(DamageType.FIRE, 10);
		balloffire.setDamageEffect(DamageType.BASH, 4);

		/*
		 * Define the spell Heal.
		 */
		Spell heal = new Spell("Heal", SkillType.ALCHEMY, 0);
		heal.setHealingEffect(10);

		/*
		 * Create one of the entities. 
		 */
		RPEntity attacker = new RPEntity(Race.HUMAN, School.WARRIOR, Sex.MALE);
		attacker.set(16, 12, 8, 12, 7, 7);
		attacker.equip(sword);
		attacker.equip(armor);
		attacker.equip(shield);
		attacker.level(1);

		/*
		 * And the other one.
		 */
		RPEntity defender = new RPEntity(Race.ELF, School.MAGE, Sex.MALE);
		defender.set(6, 6, 8, 12, 16, 13);
		defender.equip(sword);
		defender.equip(armor);
		defender.level(1);

		/*
		 * Define for both the combat attitude.
		 */
		attacker.setAttitude(0.66f);
		defender.setAttitude(0.66f);

		int turn = 0;
		/*
		 * And run the combat until one of them die. 
		 */
		while (!attacker.isDeath() && !defender.isDeath()) {
			attacker.attack(defender, turn);
			defender.cast(balloffire, attacker, turn);
			turn++;
		}
		System.out.println(turn + "=>" + attacker.hp + ":" + defender.hp);
	}

}
