package games.stendhal.server.actions;

import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Seed;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;

public class PlantAction {

	private RPEntity user;
	private Seed seed;

	public void setUser(RPEntity user) {
		this.user = user;

	}

	public void setSeed(Seed seed) {
		this.seed = seed;
	}

	public boolean execute() {
		if (seed == null || user == null) {
			return false;
		} else if (!seed.isContained()) {
			FlowerGrower flowerGrower = new FlowerGrower();
			user.getZone().add(flowerGrower);
			flowerGrower.setPosition(seed.getX(), seed.getY());
			TurnNotifier.get().notifyInTurns(3, flowerGrower);
			seed.removeFromWorld();
			return true;
		}
		return false;

	}

}
