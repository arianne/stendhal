package games.stendhal.server.actions;

import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Seed;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;

public class PlantAction {

	private RPEntity user;
	private Seed seed;

	public void setUser(final RPEntity user) {
		this.user = user;

	}

	public void setSeed(final Seed seed) {
		this.seed = seed;
	}

	public boolean execute() {
		if ((seed == null) || (user == null)) {
			return false;
		} else if (!seed.isContained()) {
			if (!seed.nextTo(user)) {
				user.sendPrivateText("The seed is too far away");
				return false;
			}
			
			final String infostring = seed.getInfoString();
			FlowerGrower flowerGrower;
			if (infostring == null) {
				flowerGrower = new FlowerGrower();
			} else {
				flowerGrower = new FlowerGrower(seed.getInfoString());
			}
			user.getZone().add(flowerGrower);
			flowerGrower.setPosition(seed.getX(), seed.getY());
			TurnNotifier.get().notifyInTurns(3, flowerGrower);
			seed.removeOne();
			return true;
		}
		
		user.sendPrivateText("You have to put the seed on the ground to plant it, silly!");
		return false;

	}

}
