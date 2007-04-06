package games.stendhal.client.events;

import marauroa.common.game.RPObject;

public interface AttributeEvent {

	// Still has old way of access to object
	void onAdded(RPObject base);

	void onChangedAdded(RPObject base, RPObject diff);

	void onChangedRemoved(RPObject base, RPObject diff);

	void onRemoved();
}
