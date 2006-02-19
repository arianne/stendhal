package games.stendhal.client.events;


public interface HPEvent 
  {
  // When entity gets healed
  public void onHealed(int amount);
  // When entity eats food 
  public void onEat(int amount);
  public void onEatEnd();
  // When entity is poisoned
  public void onPoisoned(int amount);    
  public void onPoisonEnd();
  }
