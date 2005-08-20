/*
 * DefaultEntityManager.java
 *
 * Created on 19. August 2005, 21:44
 *
 */

package games.stendhal.server.rule.defaultruleset;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.rule.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Matthias Totz
 */
public class DefaultEntityManager implements EntityManager
{
  /** the singleton instance, lazy initialisation */
  private static DefaultEntityManager manager;

  /** maps the creature names to the actual creature enums */
  private Map<String, DefaultCreature> classToCreature;
  /** maps the creature tile-ids to the actual creature enums */
  private Map<Integer, DefaultCreature> idsToCreature;
  
  /** no public constructor */
  private DefaultEntityManager()
  {
    // Build the creatures tables
    classToCreature = new HashMap<String,DefaultCreature>();
    idsToCreature = new HashMap<Integer,DefaultCreature>();
    DefaultCreature[] creatures = DefaultCreature.values();
    for (DefaultCreature creature : creatures )
    {
      classToCreature.put(creature.getCreatureClass(), creature);
      idsToCreature.put(creature.getTileId(), creature);
    }
  }

  /** 
   * returns the instance of this manager.
   * Note: This method is synchonized.
   */
  public static synchronized DefaultEntityManager getInstance()
  {
    if (manager == null)
    {
      manager = new DefaultEntityManager();
    }
    return manager;
  }
  
  /** returns the entity or <code>null</code> if the id is unknown */
  public RPEntity getEntity(int id)
  {
    if (id < 0)
      return null;
    
    // Lookup the id in the creature table
    DefaultCreature creature = idsToCreature.get(id);
    if (creature != null)
      return  creature.getCreature();
    
    return null;
  }
  
  /** 
   * returns the entity or <code>null</code> if the id is unknown 
   * @throws NullPointerException if clazz is <code>null</code>
   */
  public RPEntity getEntity(String clazz)
  {
    if (clazz == null)
      throw new NullPointerException("entity class is null");
    
    // Lookup the clazz in the creature table
    DefaultCreature creature = classToCreature.get(clazz);
    if (creature != null)
      return  creature.getCreature();
    
    return null;
  }

  /** 
   * returns the creature or <code>null</code> if the id is unknown 
   */
  public Creature getCreature(int id)
  {
    if (id < 0)
      return null;
    
    // Lookup the id in the creature table
    DefaultCreature creature = idsToCreature.get(id);
    if (creature != null)
      return  creature.getCreature();
    
    return null;
  }

  /** 
   * returns the entity or <code>null</code> if the clazz is unknown 
   * @throws NullPointerException if clazz is <code>null</code>
   */
  public Creature getCreature(String clazz)
  {
    if (clazz == null)
      throw new NullPointerException("entity class is null");
    
    // Lookup the clazz in the creature table
    DefaultCreature creature = classToCreature.get(clazz);
    if (creature != null)
      return  creature.getCreature();
    
    return null;
  }

  /** return true if the Entity is a creature */
  public boolean isCreature(int id)
  {
    return idsToCreature.containsKey(id);
  }

  /** return true if the Entity is a creature */
  public boolean isCreature(String clazz)
  {
    if (clazz == null)
      throw new NullPointerException("entity class is null");
    return classToCreature.containsKey(clazz);
  }

}
