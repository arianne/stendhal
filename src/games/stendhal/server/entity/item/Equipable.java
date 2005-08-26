/*
 * Equipable.java
 *
 * Created on 20. August 2005, 21:36
 *
 */

package games.stendhal.server.entity.item;

/**
 * An equipable Item is either a weapon (or other stuff increasing attack 
 * points) or some kind of armor (shields, body armor). Some items may provide 
 * both (spiked gloves etc.)
 *
 * @author Matthias Totz
 */
public interface Equipable
{
  
  /**
   * checks if the item has the requested property
   * @param property attribute to check
   * @return true if the item has the property, else false 
   */
  boolean hasProperty(String property);
  
  /**
   * returns the requested property as a <code>String</code>
   * @param property property to get
   * @return value of the property as a string or null if the item does not
   *         have this attribute
   */
  String getPropertyAsString(String property);
  
  /**
   * returns the requested property as an <code>int</code>
   * @param property property to get
   * @return value of the property as a string or '0' if the item does not
   *         have this attribute
   */
  int getPropertyAsInt(String property);
  
  
  /**
   * Checks if the item is of type <i>type</i>
   * @param type the type to check
   * @return true if the type matches, else false 
   */
  boolean isOfType(String type);
  
  /**
   * returns the type of the item
   * @return type of the item
   */
  String getType();
  
  /**
   * Returns all slots where this item may be equipped. 
   * @return all possible slots
   */
  String[] getPossibleSlots();
}
