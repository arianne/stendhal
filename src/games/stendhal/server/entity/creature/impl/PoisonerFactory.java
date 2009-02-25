package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.ConsumableItem;

import org.apache.log4j.Logger;

public class PoisonerFactory {
	static final Attacker nonpoisonous = new NonPoisoner();
	private static Logger logger = Logger.getLogger(PoisonerFactory.class);

	public static Attacker get(final String profile) {
		if (profile != null) {
			final String[] poisonparams = profile.split(",");
			final ConsumableItem poison = (ConsumableItem) SingletonRepository.getEntityManager().getItem(poisonparams[1]);

			if (poison == null) {
				logger .error("Cannot create poisoner with " + poisonparams[1]);
				return nonpoisonous;
			}
			return new Poisoner(Integer.parseInt(poisonparams[0]), poison);
		}
		return nonpoisonous;
	}

}
