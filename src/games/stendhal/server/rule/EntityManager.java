/*
 * EntityManager.java
 *
 * Created on 19. August 2005, 22:16
 *
 */

package games.stendhal.server.rule;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

/**
 * Ruleset Interface for resolving Entities in Stendhal.
 *
 * @author Matthias Totz
 */
public interface EntityManager
{
  /**
   * returns the entity or <code>null</code> if the id is unknown
   * @param id the tile id
   * @return the entity or <code>null</code>
   */
  RPEntity getEntity(int id);

  /**
   * returns the entity or <code>null</code> if the class is unknown
   * @param clazz the creature class
   * @return the entity or <code>null</code>
   * @throws NullPointerException if clazz is <code>null</code>
   */
  RPEntity getEntity(String clazz) throws NullPointerException;

  /**
   * return true if the Entity is a creature
   * @param id the tile id
   * @return true if it is a creature, false otherwise
   */
  boolean isCreature(int id);
  
  /**
   * return true if the Entity is a creature
   * @param clazz the creature class
   * @return true if it is a creature, false otherwise
   * @throws NullPointerException if clazz is <code>null</code>
   */
  boolean isCreature(String clazz) throws NullPointerException;

  /** 
   * returns the creature or <code>null</code> if the id is unknown.
   * @param id the tile id
   * @return the creature or <code>null</code>
   */
  Creature getCreature(int id);

  /** 
   * returns the entity or <code>null</code> if the clazz is unknown 
   * @param clazz the creature class
   * @return the creature or <code>null</code>
   * @throws NullPointerException if clazz is <code>null</code>
   */
  Creature getCreature(String clazz) throws NullPointerException;
}
