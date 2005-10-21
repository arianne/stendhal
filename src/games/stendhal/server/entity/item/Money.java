package games.stendhal.server.entity.item;

import marauroa.common.game.*;
import java.util.Map;

public class Money extends Item
  {
  private int quantity;
  
  public Money(String name, String clazz, String[] slots, Map<String, String> attributes)
    {
    super("money","money", new String[0], attributes);
    update();
    }

  public Money(int quantity)
    {
    super("money","money", new String[0], null);
    put("quantity",quantity);

    this.quantity=quantity;
    }
  
  public void update() throws AttributeNotFoundException
    {
    if(has("quantity")) quantity=getInt("quantity");
    }
  
  public int getQuantity()
    {
    return quantity;
    }

  public void setQuantity(int amount)
    {
    quantity=amount;
    put("quantity",quantity);
    }

  public int add(int amount)
    {
    setQuantity(amount+quantity);
    return quantity;
    }
  
  public int add(Money money)
    {
    setQuantity(money.quantity+quantity);
    return quantity;
    }
  
  public static void main(String[] args)
    {
    Money a=new Money(1);
    Money b=new Money(1);
    Money c=new Money(2);
    
    System.out.println (a.add(b));
    System.out.println (a.add(c));
    
    System.out.println (a);
    }
  }
