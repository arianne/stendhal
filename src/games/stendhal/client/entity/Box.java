package games.stendhal.client.entity;

public class Box extends Item {
	@Override
	public ActionType defaultAction() {
		return ActionType.OPEN;
	}
}
