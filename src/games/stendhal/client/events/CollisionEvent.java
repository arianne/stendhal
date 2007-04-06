package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;

public interface CollisionEvent {

	// Called when entity collides with another entity
	void onCollideWith(Entity entity);

	// Called when entity collides with collision layer object.
	void onCollide(int x, int y);
}
