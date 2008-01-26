package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;

/**
 * Search...
 * <p>
 * /script EntitySearch.class cname &lt;creatureName &gt; Respawn points for archrat
 * [1] int_admin_playground [7] -3_orril_dungeon
 * <p>
 * /script EntitySearch.class nonrespawn Non-Respawn creatures (minus sheep)
 * balrog (350) 0_semos_plains_n 11 11 death (30) 0_semos_plains_n 27 24
 * black_death (300) 0_semos_plains_n 44 44
 * <p>
 * /script EntitySearch.class zname &lt; partialZoneName &gt; Respawn points for zone
 * names containing: 0_semos_plain Respawn points for 0_semos_plains_ne [1]
 * gnome(2) [17] rat(0) Respawn points for 0_semos_plains_n_e2 [2] snake(3) [1]
 * gnome(2) ...
 * 
 */

public class EntitySearch extends ScriptImpl {

	public void findByCreatureName(Player player, String targetName) {
		StringBuilder res = new StringBuilder();
		Map<String, Integer> zoneCount = new HashMap<String, Integer>();

		// check targetName

		EntityManager manager = SingletonRepository.getEntityManager();
		Creature tempc = manager.getCreature(targetName);
		if (tempc != null) {
			// get the proper case of the characters in the string
			targetName = tempc.getName();
		} else {
			sandbox.privateText(player, "Not Found");
			return;
		}

		// count for each zone
		for (IRPZone irpzone : SingletonRepository.getRPWorld()) {
			StendhalRPZone zone = (StendhalRPZone) irpzone;

			for (CreatureRespawnPoint p : zone.getRespawnPointList()) {
				Creature c = p.getPrototypeCreature();
				if (targetName.equals(c.getName())) {
					String zoneName = zone.getName();
					if (zoneCount.containsKey(zoneName)) {
						int tempi = zoneCount.get(zoneName) + 1;
						zoneCount.put(zoneName, tempi);
					} else {
						zoneCount.put(zoneName, 1);
					}
				}
			}
		}

		// make string
		res.append("\r\nRespawn points for " + targetName + " : ");
		for (Map.Entry<String, Integer> e : zoneCount.entrySet()) {
			res.append("\r\n[" + e.getValue() + "]\t" + e.getKey());
		}

		sandbox.privateText(player, res.toString());

	}

	public void findNonRespawn(Player player) {
		StringBuilder res = new StringBuilder();

		res.append("\r\nNon-Respawn creatures (minus domestic animals):");

		for (IRPZone irpzone : SingletonRepository.getRPWorld()) {
			StendhalRPZone zone = (StendhalRPZone) irpzone;

			for (RPObject rpObj : zone) {
				if (isACreatureButNoPet(rpObj)) { 
					Creature c = (Creature) rpObj;
					if (c.getRespawnPoint() == null) {
						String zoneName = zone.getName();
						res.append("\r\n" + c.getName() + " (" + c.getLevel()
								+ ")");
						res.append("\t" + zoneName + " " + c.getX() + " "
								+ c.getY());
					}
				}
			}

		}

		sandbox.privateText(player, res.toString());
	}

	private boolean isACreatureButNoPet(RPObject rpObj) {
		return (rpObj instanceof Creature) && !(rpObj instanceof DomesticAnimal);
	}

	public void findByZoneName(Player player, String targetName) {
		StringBuilder res = new StringBuilder();

		res.append("\r\nRespawn points for zone names containing: "
				+ targetName);
		for (IRPZone irpzone : SingletonRepository.getRPWorld()) {
			StendhalRPZone zone = (StendhalRPZone) irpzone;

			String zoneName = zone.getName();
			if (zoneName.contains(targetName)) {
				// Count one zone
				Map<String, Integer> creatureCount = new HashMap<String, Integer>();
				for (CreatureRespawnPoint p : zone.getRespawnPointList()) {
					Creature c = p.getPrototypeCreature();
					String cn = c.getName() + "(" + c.getLevel() + ")";

					if (creatureCount.containsKey(cn)) {
						int tempi = creatureCount.get(cn) + 1;
						creatureCount.put(cn, tempi);
					} else {
						creatureCount.put(cn, 1);
					}

				}
				// Output one zone
				if (!creatureCount.isEmpty()) {
					res.append("\r\nRespawn points for " + zoneName);
				}
				for (Map.Entry<String, Integer> e : creatureCount.entrySet()) {
					res.append("\r\n[" + e.getValue() + "]\t" + e.getKey());
				}
			}
		}

		sandbox.privateText(player, res.toString());
	}

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);

		if (args.size() == 2 && args.get(0).equals("cname")) {
			findByCreatureName(admin, args.get(1));
		} else if (args.size() == 1 && args.get(0).equals("nonrespawn")) {
			findNonRespawn(admin);
		} else if (args.size() == 2 && args.get(0).equals("zname")) {
			findByZoneName(admin, args.get(1));
		} else {
			admin.sendPrivateText(
					"/script EntitySearch.class cname <creatureName>\n"
					+"/script EntitySearch.class nonrespawn\n"
					+"/script EntitySearch.class zname <partialZoneName>");
		}

	}

}
