package games.stendhal.server.actions;


import org.apache.log4j.Logger;

import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.*;

import marauroa.common.Log4J;

public class Own extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

  public static void register()
    {
    StendhalRPRuleProcessor.register("own",new Own());
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"own");

    // BUG: This features is potentially abusable right now. Consider removing it...
    if(player.hasSheep() && action.has("target") && action.getInt("target")==-1) // Allow release of sheep
      {
      Sheep sheep=(Sheep)world.get(player.getSheep());
      player.removeSheep(sheep);

      sheep.setOwner(null);
      rules.addNPC(sheep);
      
      // HACK: Avoid a problem on database 
      if(sheep.has("#db_id"))
        {
        sheep.remove("#db_id");
        }

      world.modify(player);
      return;
      }

    if(player.hasSheep())
      {
      return;
      }

    if(action.has("target"))
      {
      int targetObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof Sheep)
          {
          Sheep sheep=(Sheep)object;
          if(sheep.getOwner()==null)
            {
            sheep.setOwner(player);
            rules.removeNPC(sheep);

            player.setSheep(sheep);
            world.modify(player);
            }
          }
        }
      }

    Log4J.finishMethod(logger,"own");
    }
  }
