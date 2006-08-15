import marauroa.common.game.IRPZone;
import games.stendhal.common.Direction;
import games.stendhal.server.*
import games.stendhal.server.entity.*
import games.stendhal.server.entity.creature.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.scripting.*
import games.stendhal.server.pathfinder.Path


StendhalRPZone zone1 = (StendhalRPZone) world.getRPZone(new IRPZone.ID("-1_ados_outside_nw"));
StendhalRPZone zone2 = (StendhalRPZone) world.getRPZone(new IRPZone.ID("-2_ados_outside_nw"));

Portal portal = new Portal();
zone1.assignRPObjectID(portal);
portal.setx(49);
portal.sety(23);
portal.setNumber(60);
portal.setDestination("-2_ados_outside_nw", 61);
zone1.addPortal(portal);

portal = new Portal();
zone2.assignRPObjectID(portal);
portal.setx(49);
portal.sety(23);
portal.setNumber(61);
portal.setDestination("-1_ados_outside_nw", 60);
zone2.addPortal(portal);
