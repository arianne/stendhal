package games.stendhal.server.entity.item;


public class Sword extends Weapon
  {
  public Sword()
    {
    super();
    put("class","sword");
    }

  public int getATK()
    {
    return 14;
    }
  }
