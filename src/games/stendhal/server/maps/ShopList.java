package games.stendhal.server.maps;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class ShopList 
  {
  static
    {
    ShopList shops=get();
    
    shops.add("food&drinks","beer",10);
    shops.add("food&drinks","wine",15);
    shops.add("food&drinks","flask",5);
    shops.add("food&drinks","cheese",20);
    shops.add("food&drinks","apple",10);
    shops.add("food&drinks","carrot",10);
    shops.add("food&drinks","meat",40);
    shops.add("food&drinks","ham",80);

    shops.add("healing","antidote",50);
    shops.add("healing","minor_potion",100);
    shops.add("healing","potion",250);
    shops.add("healing","greater_potion",500);

    shops.add("sellstuff","knife",15);
    shops.add("sellstuff","small_axe",15);
    shops.add("sellstuff","club",10);
    shops.add("sellstuff","dagger",25);
    shops.add("sellstuff","wooden_shield",25);
    shops.add("sellstuff","dress",25);
    shops.add("sellstuff","leather_helmet",25);
    shops.add("sellstuff","leather_legs",35);

    shops.add("sellrangedstuff","wooden_bow",300);
    shops.add("sellrangedstuff","wooden_arrow",2);

    shops.add("buystuff","short_sword",15);
    shops.add("buystuff","sword",60);
    shops.add("buystuff","studded_shield",20);
    shops.add("buystuff","studded_armor",22);
    shops.add("buystuff","studded_helmet",17);
    shops.add("buystuff","studded_legs",20);
    shops.add("buystuff","chain_armor",29);
    shops.add("buystuff","chain_helmet",25);
    shops.add("buystuff","chain_legs",27);
    }
  
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
