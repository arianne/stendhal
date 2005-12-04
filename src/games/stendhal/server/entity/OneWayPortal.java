package games.stendhal.server.entity;


public class OneWayPortal extends Portal 
  {
  public void setDestination(String zone, int number)
    {
    throw new IllegalArgumentException("One way portals are only destination of other portals");
    }    

  public boolean isCollisionable()
    {
    return false;
    }
  }
