//* $Id$ */

/** StendhalSpouse Extension is copyright of Jo Seiler, 2006
 *  @author intensifly
 *  Adds wedding functionality to Stendhal
*/

package games.stendhal.server.extension;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalServerExtension;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

/**
 * @author intensifly
 * This extension adds marriage to the game world.
 * there are 2 commands:
 *  /marry <Player1> <Player2> which will create a bond between those players
 * This command is an admin command of the same access level as /jail ;) 
 *  /spouse which will teleport a married player to his spouse
 * To enable this extension, add it to the marauroa.int file:

  # load StendhalServerExtension(s)
  groovy=games.stendhal.server.scripting.StendhalGroovyRunner
  http=games.stendhal.server.StendhalHttpServer
  spouse=games.stendhal.server.extension.SpouseExtension
  server_extension=groovy,http,spouse

 */
public class SpouseExtension extends StendhalServerExtension {

    private final String SPOUSE = "spouse";
	private static final Logger logger = Log4J.getLogger(SpouseExtension.class);
    
    /**
     * @param rules - the reference to the rules processor
     * @param world - the reference to the game objects
     */
    public SpouseExtension(StendhalRPRuleProcessor rules, StendhalRPWorld world) {
        super(rules, world);
        logger.info("SpouseExtension starting...");
        StendhalRPRuleProcessor.register("marry", this);
        AdministrationAction.registerCommandLevel("marry", 400);
        StendhalRPRuleProcessor.register("spouse", this);
    }

    /* 
     * @see games.stendhal.server.StendhalServerExtension#init()
     */
    @Override
    public void init() {
        // this extension has no spespecific init code, everything is
        // implemented as /commands that are handled onAction
    }
    
    @Override
    public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
            Player player, RPAction action) {
       String type = action.get("type");
        
        if (type.equals("marry")) {
            onMarry(world, rules, player, action);
        } else if (type.equals("spouse")) {
            onSpouse(world, rules, player, action);
        }
     
    }
    
    private Player findPlayer (String name) {
        for (Player p : rules.getPlayers()) {
            if (p.getName().equals(name)) {
                return(p);
            }
        }
        return(null);  
    }
    
    private void onMarry(RPWorld world, StendhalRPRuleProcessor rules,
            Player player, RPAction action) {
        Log4J.startMethod(logger, "onMarry");

        String usage = "usage: /marry <player1> <player2>";
        String text = "";

        Player player1 = null;
        String name1   = null;
        Player player2 = null;
        String name2   = null;
        boolean canMarry = true;

        if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player,
                "marry", true)) {
            return;
        }
        
        if(action.has("target")) {
            name1 = action.get("target");
            player1 = findPlayer(name1);
            if(player1 == null) {
                canMarry = false;
                text += "Player " + name1 + " not found. ";
                canMarry = false;
            }
        }
        else {
            canMarry = false;
            text = usage;
        }
        
        if(action.has("args")) {
            name2 = action.get("args");
            player2 = findPlayer(name2);
            if(player2 == null) {
                canMarry = false;
                text += "Player " + name2 + " not found. ";
                canMarry = false;
            }
        }
        else {
            canMarry = false;
            text = usage;
        }

        if (canMarry) {
            if(name1.equals(name2)) {
                text += "Sorry, you can only marry 2 different players ;)";
                canMarry = false;
            }            
        }
        
        if (canMarry) {
            if(player1.hasQuest(SPOUSE)) {
                text += name1 + " is already married to "
                    + player1.getQuest(SPOUSE) + ". ";
                canMarry = false;
            }
            if(player2.hasQuest(SPOUSE)) {
                text += name2 + " is already married to "
                    + player2.getQuest(SPOUSE) + ". ";
                canMarry = false;
            }
        }
        
        if (canMarry) {
            player1.setQuest(SPOUSE,name2);
            player1.sendPrivateText("Congratulations! You are now married to " + name2 + ". You can use /spouse if you want to be together.");
            player2.setQuest(SPOUSE,name1);
            player2.sendPrivateText("Congratulations! You are now married to " + name1 + ". You can use /spouse if you want to be together.");
            text = "You have successfully married " + name1 + " " + name2 + ".";
            rules.addGameEvent(player.getName(), "marry", name1 + " + " + name2);
        }
        
        player.sendPrivateText(text);
       
        Log4J.finishMethod(logger, "onMarry");
    }
    

    
    
    private void onSpouse(RPWorld world, StendhalRPRuleProcessor rules,
            Player player, RPAction action) {
        Log4J.startMethod(logger, "onSpouse");

        if (player.hasQuest(SPOUSE)) {
            Player teleported = null;

            String name = player.getQuest(SPOUSE);
            teleported = findPlayer(name);

            if (teleported == null) {
                String text = "Your spouse " + name + " is not online.";
                player.sendPrivateText(text);
                logger.debug(text);
                return;
            }

            StendhalRPZone zone = (StendhalRPZone) world.getRPZone(teleported
                    .getID());
            int x = teleported.getx();
            int y = teleported.gety();

            if (StendhalRPAction.placeat(zone, player, x, y)) {
                rules.addGameEvent(player.getName(), "teleportto", teleported
                        .getName() + "(spouse)");

                StendhalRPAction.changeZone(player, zone.getID().getID());
                StendhalRPAction.transferContent(player);
            }

            world.modify(player);
        }

        Log4J.finishMethod(logger, "onSpouse");
    }
    
    
}
