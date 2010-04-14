package games.stendhal.client.events;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.RPEntity;
import games.stendhal.common.constants.DamageType;

/**
 * Client side attack event
 */
public class AttackEvent extends Event<RPEntity> {
	private static final Logger logger = Logger.getLogger(AttackEvent.class);
	
	@Override
	public void execute() {
		DamageType dtype;
		int idx = event.getInt("type");
		try {
			dtype = DamageType.values()[idx];
		} catch (ArrayIndexOutOfBoundsException exc) {
			logger.warn("Unknown damage type: " + idx);
			dtype = DamageType.CUT;
		}
		
		RPEntity target = entity.getAttackTarget();
		if (target != null) {
			if (event.has("hit")) {
				int damage = event.getInt("damage");
				if (damage != 0) {
					entity.onAttackDamage(dtype);
					target.onDamaged(entity, damage);
				} else {
					entity.onAttackBlocked(dtype);
					target.onBlocked(entity);
				}
			} else {
				entity.onAttackMissed(dtype);
				target.onMissed(entity);
			}
		}
	}
}
