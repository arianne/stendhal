package games.stendhal.client.entity;


public class BossCreature extends ResizeableCreature {

	@Override
	protected Entity2DView createView() {
		return new BossCreature2DView(this);
	}

}
