package games.stendhal.server.actions.equip;

import games.stendhal.server.entity.Entity;

public abstract class MoveableObject {
	protected String slot;

	public abstract boolean isValid();
	
	public abstract boolean checkDistance(Entity entity, double distance);
	
	String getSlot() {
		return slot;
	}
}
