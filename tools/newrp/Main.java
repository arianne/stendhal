package newrp;


public class Main {
	static final int LEVELS_TO_DO=100;

	public static void main(String[] args) {
		Item sword=new Item(ItemType.WEAPON, "sword", 4, Skill.SWORDING);
		sword.setDamage(DamageType.SLASH, 10);

		Item armor=new Item(ItemType.ARMOR, "leather armor", 8, Skill.LIGHT_ARMOR);
		armor.setProtection(DamageType.SLASH, 10);
		armor.setProtection(DamageType.BASH, 15);
		armor.setProtection(DamageType.STAB, 6);

		Item shield=new Item(ItemType.SHIELD, "leather shield", 10, Skill.SHIELDING);
		shield.setProtection(DamageType.SLASH, 10);
		shield.setProtection(DamageType.BASH, 10);
		shield.setProtection(DamageType.STAB, 8);

		Spell balloffire=new Spell("Ball of fire",Skill.ILLUSION,0,10,0);
		balloffire.setDamageEffect(DamageType.FIRE, 10);
		balloffire.setDamageEffect(DamageType.BASH, 4);

		Spell heal=new Spell("Heal", Skill.ALCHEMY, 0, 3, 0);
		heal.setHealingEffect(10);

		RPEntity attacker = new RPEntity(Race.HUMAN, School.WARRIOR, Sex.MALE);
		attacker.set(16,12,8,12,7,7);
		attacker.equip(sword);
		attacker.equip(armor);
		attacker.equip(shield);
		attacker.level(1);

		RPEntity defender = new RPEntity(Race.ELF, School.MAGE, Sex.MALE);
		defender.set(6,6,8,12,16,13);
		defender.equip(sword);
		defender.equip(armor);
		defender.level(1);

		attacker.setAttitude(0.66f);
		defender.setAttitude(0.66f);

		int turn=0;
		while(!attacker.isDeath() && !defender.isDeath()) {
			attacker.attack(defender, turn);
			defender.cast(balloffire, attacker, turn);
			turn++;
		}
		System.out.println(turn+"=>"+attacker.hp+":"+defender.hp);
	}

}
