package games.stendhal.server.script;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

/**
 * Searches for inconsistencies in zone collisions. For every square
 * that there is an open way out, should be also an open square in
 * the neighbouring zone.
 * <p>
 * No attention paid to performance. There should be no reason to run
 * this on anything but a test server.
 */
public class ZoneCollisionCheck extends ScriptImpl {
	private static enum Border {
		NORTH {
			@Override
			public Border opposite() {
				return SOUTH;
			}
		},
		EAST {
			@Override
			public Border opposite() {
				return WEST;
			}
		},
		SOUTH {
			@Override
			public Border opposite() {
				return NORTH;
			}
		},
		WEST {
			@Override
			public Border opposite() {
				return EAST;
			}
		};
		
		
		public abstract Border opposite();
	}
	
	private Entity entity;
	private Player admin; 
	
	@Override
	public void execute(final Player admin, final List<String> args) {
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		this.admin = admin;
		
		entity = new Entity() {
		};
		
		for (IRPZone izone : world) {
			StendhalRPZone zone = (StendhalRPZone) izone;
			checkZone(zone);
		}
	}
	
	private void checkZone(StendhalRPZone zone) {
		for (Border border : Border.values()) {
			report(checkBorder(zone, border));
		}
	}
	
	/**
	 * Check a border
	 * @param zone the to check
	 * @param border the border to check
	 * @return list of the found problems
	 */
	private List<String> checkBorder(StendhalRPZone zone, Border border) {
		LinkedList<String> problems = new LinkedList<String>();
		
		// the coordinates to check in zone
		int zoneX = 0;
		int zoneY = 0;
		
		// walking direction
		int dx = 0;
		int dy = 0;
		
		// for finding the neighbour
		int tmpx = zone.getX();
		int tmpy = zone.getY();
		switch (border) {
		case NORTH:
			tmpy--;
			dx = 1;
			break;
		case EAST:
			tmpx += zone.getWidth();
			zoneX = zone.getWidth() - 1;
			dy = 1;
			break;
		case SOUTH:
			tmpy += zone.getWidth();
			zoneY = zone.getHeight() - 1;
			dx = 1;
			break;
		case WEST:
			dy = 1;
			tmpx--;
		}
		
		final StendhalRPZone neighbour = SingletonRepository.getRPWorld().getZoneAt(
				zone.getLevel(), tmpx, tmpy, entity);
		if (neighbour != null) {
			// find the starting coordinates for neighbour
			int neighbourX = Math.abs(neighbour.getX() - zone.getX());
			int neighbourY = Math.abs(neighbour.getY() - zone.getY());
			switch (border.opposite()) {
			case EAST:
				neighbourX--;
				break;
			case SOUTH:
				neighbourY--;
				break;
			default:
			}
	
			// Walk through the border and check do the collisions match
			while ((zoneX < zone.getWidth()) && (zoneY < zone.getHeight())
					&& (neighbourX < neighbour.getWidth()) && (neighbourY < neighbour.getHeight())) {
				
				boolean zCollides = zone.collides(zoneX, zoneY);
				boolean nCollides = neighbour.collides(neighbourX, neighbourY);
				
				if (zCollides != nCollides) {
					problems.add(collidesMessage(zone.getName(), zoneX, zoneY, zCollides)
							+ " but " +
							collidesMessage(neighbour.getName(), neighbourX, neighbourY, nCollides));
				}
				
				zoneX += dx;
				zoneY += dy;
				neighbourX += dx;
				neighbourY += dy;
			}
		}
		
		return problems;
	}
	
	private String collidesMessage(String zone, int x, int y, boolean collides) {
		if (collides) {
			return zone + " has collision at [" + x + "," + y + "]";
		} else {
			return zone + " does not have collision at [" + x + "," + y + "]";
		}
	}
	
	/**
	 * Send a problem report to the admin.
	 * 
	 * @param problems the problems to include in this report
	 */
	private void report(List<String> problems) {
		if (!problems.isEmpty()) {
			StringBuilder msg = new StringBuilder();
			for (String problem : problems) {
				msg.append(problem);
				msg.append("\n");
			}
			
			admin.sendPrivateText(msg.toString());
		}
	}
}
