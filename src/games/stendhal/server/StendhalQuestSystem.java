package games.stendhal.server;

import marauroa.common.Log4J;
import org.apache.log4j.Logger;
import java.io.File;
import java.net.URI;
import games.stendhal.server.quests.IQuest;

public class StendhalQuestSystem 
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(StendhalQuestSystem.class);

  StendhalRPWorld world; 
  StendhalRPRuleProcessor rules;
  
  public StendhalQuestSystem(StendhalRPWorld world, StendhalRPRuleProcessor rules)
    {
    this.world=world;
    this.rules=rules;
    
    URI url;
    try
      {
      url=this.getClass().getClassLoader().getResource("games/stendhal/server/quests").toURI(); 
      }
    catch(java.net.URISyntaxException e)
      {
      logger.error("Can't find file: games/stendhal/server/quests",e);
      return;
      }
  
    if(url==null) 
      {
      logger.error("Can't find file: games/stendhal/server/quests");
      return;
      }

    File questsFolder=new File(url);
    String[] files=questsFolder.list();
    
    if(files == null)
      {
      logger.error("quest folder not found. should be "+questsFolder.getAbsolutePath());
      return;
      }
    
    for(String file: files)
      {
      String className=file.substring(0,file.indexOf("."));
      if(!className.equals("IQuest"))
        {
        loadQuest(className);
        }
      }
    }
  
  public static void main(String[] args)
    {
    new StendhalQuestSystem(null,null);
    }
    
  private boolean loadQuest(String name)
    {
    try
      {
      Class entityClass=Class.forName("games.stendhal.server.quests."+name);
      
      boolean implementsIQuest=false;
      
      Class[] interfaces=entityClass.getInterfaces();
      for(Class interf: interfaces)
        {
        if(interf.equals(IQuest.class))
          {
          implementsIQuest=true;
          break;
          }        
        }
      
      if(implementsIQuest==false)
        {
        logger.debug("Class don't implement IQuest interface.");
        return false;
        }
      
      logger.info("Loading Quest: "+name);
      java.lang.reflect.Constructor constr=entityClass.getConstructor(StendhalRPWorld.class,StendhalRPRuleProcessor.class);

      // simply creatre a new instance. The constructor creates all additionally objects  
      constr.newInstance(world,rules);
      return true;
      }
    catch(Exception e)
      {
      logger.warn("Quest("+name+") loading failed.");
      return false;
      }
    }
  }
