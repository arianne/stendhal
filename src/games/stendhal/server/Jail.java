package games.stendhal.server;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.Player;
import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;
import marauroa.server.game.RPWorld;

public class Jail {

	private static final Logger logger = Log4J.getLogger(Jail.class);
	
	private static Jail instance = null;
	
	private RPWorld world;
	
	private StendhalRPRuleProcessor rules;
	
	public static Jail get(RPWorld world, StendhalRPRuleProcessor rules) {
		if (instance == null) {
			instance = new Jail(world, rules);
		}
		return instance;
	}
	
	public Jail(RPWorld world, StendhalRPRuleProcessor rules) {
		this.world = world;
		this.rules = rules;
	}
	
	// TODO: auto-release after given number of minutes
	public void imprison(String criminalName, Player policeman, int minutes) {
		System.out.println(minutes);
		Player criminal = null;

		String name = criminalName;
		for (Player p : rules.getPlayers()) {
			if (p.getName().equals(name)) {
				criminal = p;
				break;
			}
		}

		if (criminal == null) {
			String text = "Player " + name + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
		}

		IRPZone.ID zoneid = new IRPZone.ID("-1_semos_jail");
		if (!world.hasRPZone(zoneid)) {
			String text = "Zone " + zoneid + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
		}

		StendhalRPZone jail = (StendhalRPZone) world.getRPZone(zoneid);
		
		criminal.teleport(jail, 8, 2, Direction.DOWN, policeman, rules);

	}

}
