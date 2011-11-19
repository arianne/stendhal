package games.stendhal.server.entity.modifier;


/**
 * A GuidedEntityModifierHandler calculates the actual value of an attribute situated in GuidedEntity based on
 * the actual value and the existing modifiers
 * 
 * @author madmetzger
 */
public class GuidedEntityModifierHandler extends AbstractModifierHandler {
	
	public GuidedEntityModifierHandler(
			ModifiedAttributeUpdater affectedEntity) {
		super(affectedEntity);
	}

	/**
	 * Modify the base speed value of a GuidedEntity
	 * 
	 * @param speed the base speed of a GuidedEntity
	 * @return the modified speed value
	 */
	public double modifySpeed(double speed) {
		double cumulatedModifier = 1d;
		for (AttributeModifier m : this.getModifiers()) {
			cumulatedModifier = cumulatedModifier * (1d + m.getSpeedModifier());
		}
		return speed * cumulatedModifier;
	}

}
