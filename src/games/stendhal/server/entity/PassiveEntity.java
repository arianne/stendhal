package games.stendhal.server.entity;

import marauroa.common.game.*;

public abstract class PassiveEntity extends Entity {
	public PassiveEntity() throws AttributeNotFoundException {
		super();
	}

	public PassiveEntity(RPObject object) throws AttributeNotFoundException {
		super(object);
	}

	public boolean isCollisionable() {
		return false;
	}
}
