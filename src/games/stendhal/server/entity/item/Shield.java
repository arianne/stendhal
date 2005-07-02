package games.stendhal.server.entity.item;


public class Shield extends Item
  {
  public Shield()
    {
    super();
    put("class","shield");
    }

  public int getDEF()
    {
    return 14;
    }
  }
