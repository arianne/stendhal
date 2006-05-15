package games.stendhal.server;

import games.stendhal.server.*;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;


public abstract class StendhalServerExtension
  {
  /** our connection points to the game objects * */
  static protected StendhalRPRuleProcessor rules;
  static protected StendhalRPWorld world;
  /** the logger instance. */
  protected static final Logger logger = Log4J.getLogger(StendhalServerExtension.class);
 
  public StendhalServerExtension(StendhalRPRuleProcessor rules, StendhalRPWorld world)
    {
    this.rules = rules;
    this.world = world;
    }
   
  public abstract void init();
  
  public static StendhalServerExtension getInstance(String name, StendhalRPRuleProcessor pRules, StendhalRPWorld pWorld) 
    {
    try
      {
      Class extensionClass=Class.forName(name);

      if(!StendhalServerExtension.class.isAssignableFrom(extensionClass))
        {
        logger.debug("Class is no instance StendhalServerExtension.");
        return null;
        }

      logger.info("Loading ServerExtension: "+name);
      java.lang.reflect.Constructor constr=extensionClass.getConstructor(StendhalRPRuleProcessor.class,StendhalRPWorld.class);

      // simply return a new instance. The constructor creates all additionally objects    
      return (StendhalServerExtension) constr.newInstance(pRules,pWorld);
      }
    catch(Exception e)
      {
      logger.warn("StendhalServerExtension "+name+" loading failed.",e);
      return null;
      }
    }
  
  }
