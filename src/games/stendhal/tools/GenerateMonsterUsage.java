package games.stendhal.tools;

import games.stendhal.server.core.config.CreaturesXMLLoader;
import games.stendhal.server.core.config.ZoneGroupsXMLLoader;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

import org.xml.sax.SAXException;

public class GenerateMonsterUsage {
	public static void main(String[] args) throws URISyntaxException,
			SAXException, IOException {
		ZoneGroupsXMLLoader loader = new ZoneGroupsXMLLoader(new URI(
				"/data/conf/zones.xml"));
		loader.load();

		Map<String, Integer> count = new HashMap<String, Integer>();

		CreaturesXMLLoader creatureLoader = SingletonRepository.getCreaturesXMLLoader();
		List<DefaultCreature> creatures = creatureLoader.load("data/conf/creatures.xml");

		for (DefaultCreature c : creatures) {
			count.put(c.getCreatureName(), 0);
		}

		for (IRPZone zone : SingletonRepository.getRPWorld()) {
			for (CreatureRespawnPoint p : ((StendhalRPZone) zone).getRespawnPointList()) {
				Creature c = p.getPrototypeCreature();
				int creatureCount = 1;
				if (count.containsKey(c.getName())) {
					creatureCount = count.get(c.getName()) + 1;
				}

				count.put(c.getName(), creatureCount);
			}
		}

		for (Map.Entry<String, Integer> e : count.entrySet()) {
			if (e.getValue() == 0) {
				System.out.println(e.getKey() + ";" + e.getValue());
			}
		}

	}
}
