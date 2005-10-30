package games.stendhal.server.actions;


import org.apache.log4j.Logger;

import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;

import marauroa.common.Log4J;

public class Chat extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

  public static void register()
    {
    Chat chat=new Chat();
    StendhalRPRuleProcessor.register("chat",chat);
    StendhalRPRuleProcessor.register("tell",chat);    
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    if(action.get("type").equals("chat"))
      {
      onChat(world,rules,player,action);
      }
    else
      {
      onTell(world,rules,player,action);
      }    
    }
  
  private void onChat(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"chat");
    if(action.has("text"))
      {
      player.put("text",action.get("text"));
      world.modify(player);

      rules.removePlayerText(player);
      }
    Log4J.finishMethod(logger,"chat");
    }

  private void onTell(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"tell");

    if(action.has("target") && action.has("text"))
      {
      String message = player.getName() +  " tells you: " + action.get("text");
      for(Player p : rules.getPlayers())
        {
        if(p.getName().equals(action.get("target")))
          {
          p.setPrivateText(message);
          player.setPrivateText("You tell " + p.getName() + ": " + action.get("text"));
          world.modify(p);
          world.modify(player);

          rules.removePlayerText(player);
          rules.removePlayerText(p);
          return;
          }
        }
      player.setPrivateText(action.get("target") + " is not currently logged.");
      }

    Log4J.finishMethod(logger,"tell");
    }
  }
