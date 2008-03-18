package games.stendhal.server.entity.creature.impl;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.ConsumableItem;

public class PoisonerFactory {
	static final Attacker nonpoisonous = new NonPoisoner();
	private static Logger logger = Logger.getLogger(PoisonerFactory.class);

	public static Attacker get(String profile) {
		if (profile != null) {
			String[] poisonparams = profile.split(",");
			ConsumableItem poison = (ConsumableItem) SingletonRepository.getEntityManager().getItem(poisonparams[1]);

			if (poison == null) {
				logger .error("Cannot create poisoner with " + poisonparams[1]);
				return nonpoisonous;
			}
			return new Poisoner(Integer.parseInt(poisonparams[0]), poison);
		}
		return nonpoisonous;
	}

}
