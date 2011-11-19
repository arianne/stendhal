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

	public int modifyHp(int hp) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getHpModifier());
		}
		return Double.valueOf(Math.ceil(cumulatedModifier * hp)).intValue();
	}

}
