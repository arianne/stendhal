package games.stendhal.client.events;

import marauroa.common.game.*;

public interface AttributeEvent {

	// Still has old way of access to object
	public void onAdded(RPObject base);

	public void onChangedAdded(RPObject base, RPObject diff);

	public void onChangedRemoved(RPObject base, RPObject diff);

	public void onRemoved();
}
