package games.stendhal.client.events;

import games.stendhal.client.entity.*;

interface CollisionEvent 
  {
  // Called when entity collides with another entity
  public void onCollideWith(Entity entity);
  // Called when entity collides with collision layer object.
  public void onCollide(int x, int y);
  }
