package games.stendhal.server.maps;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class ShopList 
  {
  static private ShopList instance;
  
  static public ShopList get()
    {
    if(instance==null)
      {
      instance=new ShopList();     
      }
    
    return instance;
    }
  
  private Map<String, Map<String, Integer>> contents;
  
  private ShopList()
    {
    contents=new HashMap<String, Map<String, Integer>>();
    }
  
  public Map<String, Integer> get(String name)
    {
    return contents.get(name);
    }
  
  public void add(String name,String item, int price)
    {
    Map<String, Integer> shop;
    
    if(!contents.containsKey(name))
      {
      shop=new LinkedHashMap<String, Integer>();
      contents.put(name, shop);            
      }
    else
      {
      shop=contents.get(name);
      }

    shop.put(item, price);
    }
  }
