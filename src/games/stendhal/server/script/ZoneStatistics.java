package games.stendhal.server.script;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ObjectCounter;

import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * prints out statistics about the danger of zones
 *
 * @author hendrik
 */
public class ZoneStatistics extends ScriptImpl {
	public final int FACTOR = 1000;

	@Override
	public void execute(Player admin, List<String> args) {
		System.out.println("zonename\tsum/area*" + FACTOR + "\t\tsum\tarea\t\twidth\theight\t");
		for (IRPZone iZone : StendhalRPWorld.get()) {
			StendhalRPZone zone = (StendhalRPZone) iZone;
			printZoneStat(zone);
		}
	}

	private void printZoneStat(StendhalRPZone zone) {
		ObjectCounter<Integer> counter = new ObjectCounter<Integer>();
		for (CreatureRespawnPoint point : zone.getRespawnPointList()) {
			counter.add(point.getPrototypeCreature().getLevel());
		}
		int sum = 0;
		for (Map.Entry<Integer, Integer> entry : counter.getMap().entrySet()) {
			sum = sum + entry.getValue() * entry.getKey();
		}
		int area = zone.getWidth() * zone.getHeight();
		
		System.out.print(zone.getName() + "\t");
		System.out.print((sum * FACTOR / area) + "\t\t");
		System.out.print(sum + "\t");
		System.out.print(area + "\t\t");
		System.out.print(zone.getWidth() + "\t" + zone.getHeight() + "\t");
		for (Map.Entry<Integer, Integer> entry : counter.getMap().entrySet()) {
			System.out.print("\t\t" + entry.getValue() + "\t" + entry.getKey());
		}
		System.out.println();
	}
}
