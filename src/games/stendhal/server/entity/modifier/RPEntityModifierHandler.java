package games.stendhal.server.entity.modifier;

import games.stendhal.server.entity.RPEntity;

/**
 * A RPEntityModifierHandler calculates the actual value of an attribute situated in RPEntity based on
 * the actual value and the existing modifiers
 * 
 * @author madmetzger
 */
public class RPEntityModifierHandler extends AbstractModifierHandler {
	
	public RPEntityModifierHandler(RPEntity affectedEntity) {
		super(affectedEntity);
	}
	
	/**
	 * Calculate the modified value of hp
	 * @param hp
	 * @return modified hp
	 */
	public int modifyHp(int hp) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getHpModifier());
		}
		return Double.valueOf(Math.ceil(cumulatedModifier * hp)).intValue();
	}

	/**
	 * Calculate the modified value of base hp
	 * @param base_hp
	 * @return modified base hp
	 */
	public int modifyBaseHp(int baseHp) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getBaseHpModifier());
		}
		return Double.valueOf(Math.ceil(cumulatedModifier * baseHp)).intValue();
	}

	/**
	 * Calculate the modified value of def
	 * @param def
	 * @return modified def
	 */
	public int modifyDef(int def) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getDefModifier());
		}
		return Math.min(Double.valueOf(Math.ceil(cumulatedModifier * def)).intValue(), 597);
	}
	
	/**
	 * Calculate the modified value of atk
	 * @param atk
	 * @return modified atk
	 */
	public int modifyAtk(int atk) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getAtkModifier());
		}
		return Math.min(Double.valueOf(Math.ceil(cumulatedModifier * atk)).intValue(), 597);
	}
	
	/**
	 * Calculate the modified value of mana
	 * @param mana
	 * @return modified mana
	 */
	public int modifyMana(int mana) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getManaModifier());
		}
		return Double.valueOf(Math.ceil(cumulatedModifier * mana)).intValue();
	}
	
	/**
	 * Calculate the modified value of base mana
	 * @param baseMana
	 * @return modified base mana
	 */
	public int modifyBaseMana(int baseMana) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getBaseManaModifier());
		}
		return Double.valueOf(Math.ceil(cumulatedModifier * baseMana)).intValue();
	}

}
