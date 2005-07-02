package games.stendhal.server.entity.item;


public class Club extends Weapon
  {
  public Club()
    {
    super();
    put("class","club");
    }

  public int getATK()
    {
    return 7;
    }
  }
