package games.stendhal.server.scripting;

import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import groovy.lang.Closure;
import java.io.File;
import java.util.List;
import java.util.LinkedList;
import games.stendhal.common.Pair;
import games.stendhal.server.*;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.rule.defaultruleset.*;
import marauroa.common.game.RPObject;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;
import games.stendhal.server.maps.NPCList;


public class StendhalGroovyScript 
  {
  private String groovyScript;
  private Binding groovyBinding;
  private List<NPC> loadedNPCs = new LinkedList<NPC>();
  private List<RPObject> loadedRPObjects = new LinkedList<RPObject>();
  private List<Pair<ScriptCondition, ScriptAction>> loadedScripts = new LinkedList<Pair<ScriptCondition, ScriptAction>>(); 
  private StendhalScriptSystem scripts;
  private String groovyExceptionMessage;

  StendhalRPRuleProcessor rules;
  StendhalRPWorld world;
  StendhalRPZone zone;

  private static final Logger logger = Log4J.getLogger(StendhalGroovyRunner.class);
 
  public StendhalGroovyScript(String filename, StendhalRPRuleProcessor rp, StendhalRPWorld world)
    {
   	groovyScript = filename;
   	groovyBinding = new Binding();
    this.rules=rp;
    this.world=world;
    this.scripts=StendhalScriptSystem.get();
   	groovyBinding.setVariable("game",this);
    groovyBinding.setVariable("logger",logger);
    }
  
  public boolean setZone(String name) 
    {
    zone=(StendhalRPZone)world.getRPZone(name);
    return(zone!=null);
    }
  
  public void add(NPC npc) 
    {
    if(zone!=null) 
      {
      zone.assignRPObjectID(npc);
      zone.addNPC(npc);
      rules.addNPC(npc);
      loadedNPCs.add(npc);
      logger.warn("Groovy added NPC: " + npc);
      }
    }

  public void add(RPObject object)
    {
    if(zone!=null) 
      {
      zone.assignRPObjectID(object);
      zone.add(object);
      loadedRPObjects.add(object);
      logger.warn("Groovy added object: " + object);
      }
    }
 
  public void add(ScriptCondition condition, ScriptAction action)
    {
    loadedScripts.add(scripts.addScript(condition, action));
    logger.warn("Groovy added a script.");
    }
  
  public Creature [] getCreatures() {
    return(world.getRuleManager().getEntityManager().getCreatures().toArray(new Creature[1])); 
  }
  
  public Item [] getItems() {
    return(world.getRuleManager().getEntityManager().getItems().toArray(new Item[1]));
  }
  
  public Item getItem(String name)
    {    
    Item item = world.getRuleManager().getEntityManager().getItem(name);
    if(zone!=null)
      {
      zone.assignRPObjectID(item);
      }
    return(item);
    }
  
  public boolean load() 
    {
      GroovyShell interp = new GroovyShell(groovyBinding);
      boolean ret = true;
      Log4J.startMethod(logger,"load");
      try {
          File f = new File(groovyScript);
          interp.evaluate(f);
      } catch(Exception e) {
          logger.error("Exception while sourcing file " + groovyScript);
          e.printStackTrace(); 
          groovyExceptionMessage = e.getMessage();
          ret = false;
      }
      Log4J.finishMethod(logger,"load");
      return(ret);
    }
  
  public String getMessage()
    {
    return(groovyExceptionMessage);
    }
  
  public void unload() 
    {
    Log4J.startMethod(logger,"unload");
    
    for(Pair<ScriptCondition, ScriptAction> script: loadedScripts)
      {
      logger.warn("Removing groovy added script.");
      scripts.removeScript(script);
      }  
    
    for(NPC npc: loadedNPCs)
      {
      logger.warn("Removing groovy added NPC: " + npc);
      String id = npc.getID().getZoneID();
      zone=(StendhalRPZone)world.getRPZone(id);
      NPCList.get().remove(npc.getName());
      rules.removeNPC(npc);
      zone.getNPCList().remove(npc);
      zone.remove(npc);
      }

    for(RPObject object: loadedRPObjects)
      {
      logger.warn("Removing groovy added object: " + object);
      String id = object.getID().getZoneID();
      zone=(StendhalRPZone)world.getRPZone(id);
      zone.remove(object);
      }  
    
    Log4J.finishMethod(logger,"unload");
    }
  
  }
