package games.stendhal.server.script;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spawner.CreatureRespawnPoint;
import games.stendhal.server.rule.EntityManager;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;

/**
 * Search...
 * 
 * /script EntitySearch.class name <creatureName>
 * Respawn points for archrat
 * (1)	int_admin_playground
 * (7)	-3_orril_dungeon
 * 
 * /script EntitySearch.class nonrespawn
 * Non-Respawn creatures (minus sheep)
 * balrog	(350)	0_semos_plains_n	(11,11)
 * death	(30)	0_semos_plains_n	(27,24)
 * black_death	(300)	0_semos_plains_n	(44,44)
 * 
 */

public class EntitySearch extends ScriptImpl {
	
	public void searchCreature(Player player, String targetName) {
		StringBuilder res = new StringBuilder();
		Map<String, Integer> zoneCount = new HashMap<String,Integer>();
		
		// check targetName
		Creature tempc;
		EntityManager manager = StendhalRPWorld.get().getRuleManager().getEntityManager();
		if((tempc = manager.getCreature(targetName)) != null) {
			// get the proper case of the characters in the string
			targetName = tempc.getName();
		} else {
			sandbox.privateText(player, "Not Found");
			return;
		}
		
		// count for each zone
		for(IRPZone zone : StendhalRPWorld.get()) {
			for(CreatureRespawnPoint p : ((StendhalRPZone)zone).getRespawnPointList()) {
				Creature c = p.getPrototypeCreature();
				if(targetName.equals(c.getName())) {
					String zoneName = zone.getID().getID();
					if(zoneCount.containsKey(zoneName)) {
						int tempi = zoneCount.get(zoneName) + 1;
						zoneCount.put(zoneName, tempi);
					} else {
						zoneCount.put(zoneName, 1);
					}
				}
			}
		}
		
		// make string
		res.append("\r\nRespawn points for " + targetName);
		for(Map.Entry<String, Integer> e: zoneCount.entrySet()) {
			res.append("\r\n(" + e.getValue() + ")\t" + e.getKey());
		}
		
		sandbox.privateText(player, res.toString());
		
	}
	
	public void listNonRespawn(Player player) {
		StringBuilder res = new StringBuilder();
		
		res.append("\r\nNon-Respawn creatures (minus sheep)");
		
		for(IRPZone zone : StendhalRPWorld.get()) {
			for(RPObject n : ((StendhalRPZone)zone)) {
				if ((n instanceof Creature) && !(n instanceof Sheep)) {
					Creature c = (Creature)n;
					if(c.getRespawnPoint() == null) {
						String zoneName = zone.getID().getID();
						res.append("\r\n" + c.getName() + "\t(" + c.getLevel() + ")");
						res.append("\t" + zoneName + "\t(" + c.getX() + "," + c.getY() + ")");
					}
				}
			}
			
		}
		
		sandbox.privateText(player, res.toString());
		
	}
	
	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);

		if (args.size() == 0) {
			admin.sendPrivateText("/script EntitySearch.class name <creatureName>\n/script EntitySearch.class nonrespawn");
			return;
		}

		String temp = args.get(0);
		
		if (temp.equals("name") && args.size() == 2) {
			searchCreature(admin,args.get(1));
		} else if (temp.equals("nonrespawn")) {
			listNonRespawn(admin);
		}
		
	}

}
