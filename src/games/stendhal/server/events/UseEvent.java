package games.stendhal.server.events;

import games.stendhal.server.entity.RPEntity;

public interface UseEvent 
  {
  public void onUsed(RPEntity user);
  }
