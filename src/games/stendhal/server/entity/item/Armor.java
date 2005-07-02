package games.stendhal.server.entity.item;


public class Armor extends Item
  {
  public Armor()
    {
    super();
    put("class","armor");
    }

  public int getDEF()
    {
    return 1;
    }
  }
