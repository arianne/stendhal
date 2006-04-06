package games.stendhal.server;

import marauroa.common.Log4J;
import org.apache.log4j.Logger;
import games.stendhal.common.Pair;
import games.stendhal.server.scripting.ScriptAction;
import games.stendhal.server.scripting.ScriptCondition;

import java.util.*;

public class StendhalScriptSystem 
  {
  private List<Pair<ScriptCondition, ScriptAction>> scripts;
  
  private StendhalScriptSystem()
    {
    scripts=new LinkedList<Pair<ScriptCondition, ScriptAction>>();
    }
  
  private static StendhalScriptSystem instance;

  public static StendhalScriptSystem get()
    {
    if(instance==null)
      {
      instance=new StendhalScriptSystem();
      }
    
    return instance;
    }
  
  public void addScript(ScriptCondition condition, ScriptAction action)
    {
    scripts.add(new Pair<ScriptCondition, ScriptAction>(condition,action));
    }
  
  public void logic()
    {
    for(Pair<ScriptCondition, ScriptAction> script: scripts)
      {
      if(script.first()==null || script.first().fire())
        {
        script.second().fire();
        }
      }
    }
  }
