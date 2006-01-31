package games.stendhal.client.events;


interface AttributeEvent 
  {
  // Still has old way of access to object
  public void onChanged(String attribute, String value, String oldvalue);
  }
