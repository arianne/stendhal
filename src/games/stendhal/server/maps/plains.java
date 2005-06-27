package games.stendhal.server.maps;

import marauroa.common.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;

public class plains 
  {
  public plains(StendhalRPZone zone)
    {
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(118);
    sign.sety(43);
    sign.setText("You are about to leave this area to move to the forest.|You may fatten up your sheep there on wild berries.|Be careful though, these forests crawl with wolves.");
    zone.add(sign);
    
    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(38);
    sign.sety(3);
    sign.setText("You are about to leave this area to move to the village.|You can buy a new sheep there.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(113);
    sign.sety(3);
    sign.setText("You are about to leave this area to move to the city.|You can sell your sheep there.");
    zone.add(sign);
    }  
  }
