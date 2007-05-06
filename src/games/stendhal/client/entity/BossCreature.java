package games.stendhal.client.entity;


public class BossCreature extends Creature {

	@Override
	protected Entity2DView createView() {
		return new BossCreature2DView(this);
	}

}
