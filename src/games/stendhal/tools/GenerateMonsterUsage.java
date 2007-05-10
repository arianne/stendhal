package games.stendhal.tools;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneGroupsXMLLoader;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.spawner.CreatureRespawnPoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.IRPZone;

import org.xml.sax.SAXException;


public class GenerateMonsterUsage {
	public static void main(String[] args) throws URISyntaxException, SAXException, IOException {
		ZoneGroupsXMLLoader loader = new ZoneGroupsXMLLoader(new URI("/data/conf/zones.xml"));
		loader.load();
		
		Map<String, Integer> count=new HashMap<String,Integer>();
				
		for(IRPZone zone: StendhalRPWorld.get()) {
			for(CreatureRespawnPoint p: ((StendhalRPZone)zone).getRespawnPointList()) {
				Creature c=p.getPrototypeCreature();
				int creatureCount=0;
				if(count.containsKey(c.getName())) {
					creatureCount=count.get(c.getName())+1;
				}
				count.put(c.getName(), creatureCount);
			}
		}
		
		System.out.println("World Creature usage");
		for(Map.Entry<String, Integer> e: count.entrySet()) {
			System.out.println(e.getKey()+";"+e.getValue());
		}
	
	}
}
