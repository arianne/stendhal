package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.Door;

public class village 
  {
  public village(StendhalRPWorld world, StendhalRPZone zone) throws java.io.IOException
    {
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(23);
    sign.sety(61);
    sign.setText("You are about to leave this area and move to the plains.|You may fatten up your sheep there on the wild berries.|Be careful though, wolves roam these plains.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(60);
    sign.sety(47);
    sign.setText("You are about to leave this area to move to the city.|You can sell your sheep there.");    
    zone.add(sign);
    
    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(16);
    sign.sety(35);
    sign.setText("[CLOSED]|The tavern has moved to a much|better and central house in town.|Come buy your weapons, find your|quests and hang out there instead.");
    zone.add(sign);
    
    world.createHouse(0,zone,29,40);
    }
  
  }
