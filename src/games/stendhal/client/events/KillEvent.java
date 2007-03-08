package games.stendhal.client.events;

import games.stendhal.client.entity.*;

public interface KillEvent {
	// Called when entity kills another entity
	public void onKill(Entity killed);

	// Called when entity is killed by killer
	public void onDeath(Entity killer);
}
