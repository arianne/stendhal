package games.stendhal.server.pathfinder;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class PathfinderProfile {

	private static final Logger logger = Log4J.getLogger(PathfinderProfile.class);
	private static final String pathfinderProfileData = "PathfinderProfile.data";  
	private static final String pathfinderZone = "int_pathfinding";  
	private static final int iterations = 1000;
	private static final int dataSize = 1000;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		startLogSystem();
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addArea(pathfinderZone);
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(pathfinderZone);

		// read profile data
		ArrayList<String> tests; 
		try {
			FileInputStream fis = new FileInputStream(pathfinderProfileData);
			ObjectInputStream ois = new ObjectInputStream(fis);
			String[] testsIn = (String[])ois.readObject();
			tests = new ArrayList<String>(Arrays.asList(testsIn));
			ois.close();
		} catch (Exception e) {
			tests = new ArrayList<String>(); 
		}

		// create profile data, if not read
		boolean testsChanged = false;
		while (tests.size() < dataSize) {
			int x0 = 0;
			int y0 = 0;
			int x1 = 0;
			int y1 = 0;
			do {
				x0 = Rand.rand(zone.getWidth());
				y0 = Rand.rand(zone.getHeight());
				x1 = Rand.rand(zone.getWidth());
				y1 = Rand.rand(zone.getHeight());
			} while (zone.collides(x0, y0) || zone.collides(x1, y1) || (Math.abs(x0 -x1) > 8) || (Math.abs(y0 -y1) > 8));
			String test = "rat;" + x0 + ";" + y0 + ";rat;" + x1 + ";" + y1;
			logger.debug(test);
			tests.add(test);
			testsChanged = true;
		}

		// store profile data, if created
		if (testsChanged) {
			FileOutputStream fos = new FileOutputStream(pathfinderProfileData);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			String[] testsOut = new String[tests.size()];
			testsOut = tests.toArray(testsOut);
			oos.writeObject(testsOut);
			oos.close();
		}

		DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld
				.get().getRuleManager().getEntityManager();

		long searchTime = 0;
		long searchTimeValidPath = 0;
		int validPath = 0;
		List<Path.Node> nodes = null;
		for (String test : tests) {
			String testData[] = test.split(";");
			Creature entity = manager.getCreature(testData[0]);
			int x0 = Integer.parseInt(testData[1]);
			int y0 = Integer.parseInt(testData[2]);
			zone.assignRPObjectID(entity);
			StendhalRPAction.placeat(zone, entity, x0, y0);
			zone.add(entity);

			Creature target = manager.getCreature(testData[3]);
			int x1 = Integer.parseInt(testData[4]);
			int y1 = Integer.parseInt(testData[5]);
			zone.assignRPObjectID(target);
			StendhalRPAction.placeat(zone, target, x1, y1);
			zone.add(target);

			// calculate the destArea
			Rectangle2D entityArea = entity.getArea(entity.getX(), entity
					.getY());
			Rectangle2D targetArea = target.getArea(target.getX(), target
					.getY());
			Rectangle destArea = new Rectangle(
					(int) (targetArea.getX() - entityArea.getWidth()),
					(int) (targetArea.getY() - entityArea.getHeight()),
					(int) (entityArea.getWidth() + targetArea.getWidth() + 1),
					(int) (entityArea.getHeight() + targetArea.getHeight() + 1));

			// for 1x2 size creatures the destArea, needs bo be one up
			destArea.translate(0, (int) (entity.getY() - entityArea.getY()));

			long startTime = System.currentTimeMillis();
			for (int i = 0; i < iterations; i++) {
				nodes = Path.searchPath(entity, entity.getX(), entity.getY(), destArea, 20.0);
			}
			long endTime = System.currentTimeMillis();
			searchTime += (endTime - startTime);
			if ((nodes != null) && (nodes.size() != 0)) {
				searchTimeValidPath += (endTime - startTime);
				validPath++;
			}
			zone.remove(entity);
			zone.remove(target);
		}
		logger.info("Total search time: " + searchTime + "ms");
		logger.info("average search time: " + (1000 * searchTime / (iterations * tests.size())) + "ns per searchPath()");
		logger.info("average search time for " + validPath + " valid Pathes: " + (1000 * searchTimeValidPath / (iterations * tests.size())) + "ns per searchPath()");
	}

	/**
	 * Starts the LogSystem
	 */
	private static void startLogSystem() {
		Log4J.init("data/conf/log4j.properties");

		logger.info("OS: " + System.getProperty("os.name") + " "
				+ System.getProperty("os.version"));
		logger.info("Java: " + System.getProperty("java.version"));
	}
}
