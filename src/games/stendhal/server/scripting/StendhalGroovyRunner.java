package games.stendhal.server.scripting;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.util.List;
import java.util.LinkedList;
import games.stendhal.server.*;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.RPWorld;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;


public class StendhalGroovyRunner extends StendhalServerExtension
  {
  private Map<String, StendhalGroovyScript> scripts;
  private final String scriptDir = "data/script/";

  public StendhalGroovyRunner(StendhalRPRuleProcessor rp, StendhalRPWorld world)
    {
    super(rp, world);
    scripts = new HashMap<String, StendhalGroovyScript>();
    StendhalRPRuleProcessor.register("script",this);
    }
 
  public void init()
    {
    URL url = getClass().getClassLoader().getResource(scriptDir);
    if(url != null)
      {
      File dir = new File (url.getFile());
      String[] strs = dir.list(new FilenameFilter() {
        public boolean accept(File dir, String name) {
        return name.endsWith(".groovy");
        }});
      for (int i=0; i < strs.length; i++)
        {
        perform(strs[i]);
        }
      }
    
    }
 
  public synchronized boolean perform(String name)
    {
    boolean ret = false;
    StendhalGroovyScript gr;
    name = name.trim();
    if(getClass().getClassLoader().getResource(scriptDir + name) != null)
      {
      if((gr = scripts.remove(name))!=null)
        {
        gr.unload();
        }
      gr = new StendhalGroovyScript(scriptDir + name,rules,world);
      ret = gr.load();
      scripts.put(name,gr);
      }
    return(ret);
    }
  
  public String getMessage(String name)
    {
    StendhalGroovyScript gr = scripts.get(name);
    if(gr !=null)
      {
      return(gr.getMessage());
      }
    return(null);
    }
  
  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onScript");

    System.out.println("onScript " + action);
    
    if(action.has("target"))
      {
      String script=action.get("target");
      String text = "Script " + script + "not found!";
      if(perform(script))
        {
        text = "Script " + script + " was successfully executed.";
        }
      else
        {
        String msg = getMessage(script);
        if (msg != null)
          {
          text = msg;            
          }
        else
          {
          text = "Script " + script + " not found or unexpected error!";
          }
        }
      player.setPrivateText(text);
      rules.removePlayerText(player);
      }
    Log4J.finishMethod(logger,"onScript");
    }
  
  }
