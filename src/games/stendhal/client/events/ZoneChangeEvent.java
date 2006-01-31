package games.stendhal.client.events;

interface ZoneChangeEvent 
  {
  // Called when entity enters a new zone
  public void onEnterZone(String zone);
  // Called when entity leaves a zone
  public void onLeaveZone(String zone);
  }
