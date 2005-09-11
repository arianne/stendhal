package games.stendhal.server.entity.item;


public class Key extends Item
  {
  public Key()
    {
    super("key","key", new String[0], null);
    put("door","somedoor"); // TODO: make this invisible?
    }
  }
