package games.stendhal.client.gui.wt;


/** Every GUI component willing to generate messages must implement this, for
 *  example buttons, droppable areas on drop and move, etc... */
public interface wtEvent 
  {
  public void onAction(Object... param);
  }
