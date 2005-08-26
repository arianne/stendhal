/*
 * SimpleEquipableItem.java
 *
 * Created on 20. August 2005, 21:45
 *
 */

package games.stendhal.server.entity.item;

import java.util.Map;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

/**
 * Simplelst implementation of an Item.
 *
 * @author Matthias Totz
 */
public class SimpleEquipableItem extends Item implements Equipable
{
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(SimpleEquipableItem.class);

  /** the type of the item */
  private String type;
  /** the possible slots for this item */
  private String[] slots;
  /** Attributes of the item */
  private Map<String, String> properties;
  /** attack */
  private int attack;
  /** defense */
  private int defense;
  
  /**
   * Creates a new SimpleEquipableItem 
   * @param clazz item class
   * @param attack attack points
   * @param defense defensepoints
   */
  public SimpleEquipableItem(String clazz, String type, String[] slots, Map<String, String> properties)
  {
    put("class",clazz);
    this.slots = slots;
    this.type = type;
    this.properties = properties;
    
    attack = getPropertyAsInt("atk");
    defense = getPropertyAsInt("def");
  }

  /**
   * Returns the attack points of this item. Positive and negative values are
   * allowed. If this item doesn't modify the attack it should return '0'.
   * @return attack points 
   */
  public int getAttack()
  {
    return attack;
  }

  /**
   * Returns the defense points of this item. Positive and negative values are
   * allowed. If this item doesn't modify the defense it should return '0'.
   * @return defense points 
   */
  public int getDefense()
  {
    return defense;
  }

  /**
   * Returns all slots where this item may be equipped. 
   */
  public String[] getPossibleSlots()
  {
    return slots;
  }

  /**
   * Checks if the item is of type <i>type</i>
   * @param type the type to check
   * @return true if the type matches, else false 
   */
  public boolean isOfType(String type)
  {
    return this.type.equals(type);
  }
  
  /** returns the type of the item */
  public String getType()
  {
    return type;
  }

  /**
   * checks if the item has the requested property
   * @param property attribute to check
   * @return true if the item has the property, else false 
   */
  public boolean hasProperty(String property)
  {
    return properties.containsKey(property);
  }

  /**
   * returns the requested property as a <code>String</code>
   * @param property property to get
   * @return value of the property as a string or null if the item does not
   *         have this attribute
   */
  public String getPropertyAsString(String property)
  {
    return properties.get(property);
  }

  /**
   * returns the requested property as an <code>int</code>
   * @param property property to get
   * @return value of the property as a string or '0' if the item does not
   *         have this attribute
   */
  public int getPropertyAsInt(String property)
  {
    String prop = properties.get(property);
    if (prop == null)
      return 0;
    
    int retVal;
    try
    {
      retVal = Integer.parseInt(prop);
    }
    catch (NumberFormatException e)
    {
      logger.warn("property "+property+" of item "+type+"/"+get("class")+" is not an integer");
      retVal = 0;
    }
    return retVal;
  }
}
