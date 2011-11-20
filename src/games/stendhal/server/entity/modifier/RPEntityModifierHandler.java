package games.stendhal.server.entity.modifier;
/**
 * A RPEntityModifierHandler calculates the actual value of an attribute situated in RPEntity based on
 * the actual value and the existing modifiers
 * 
 * @author madmetzger
 */
public class RPEntityModifierHandler extends AbstractModifierHandler {
	
	public RPEntityModifierHandler(ModifiedAttributeUpdater affectedEntity) {
		super(affectedEntity);
	}

	/**
	 * Calculate the modified value of base hp
	 * @param hp
	 * @return modified base hp
	 */
	public int modifyHp(int hp) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getHpModifier());
		}
		return Double.valueOf(Math.ceil(cumulatedModifier * hp)).intValue();
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

}
