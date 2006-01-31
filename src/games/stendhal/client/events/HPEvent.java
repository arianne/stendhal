package games.stendhal.client.events;


interface HPEvent 
  {
  // When this entity is damaged
  public void onDamaged(int damage);
  // When entity gets healed
  public void onHealed(int amount);
  // When entity eats food 
  public void onEat(int amount);
  // When entity is poisoned
  public void onPoisoned(int amount);    
  }
