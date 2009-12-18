package games.stendhal.server.maps.ados.magician_house;

import games.stendhal.tools.tiled.LayerDefinition;
import games.stendhal.tools.tiled.StendhalMapStructure;
import games.stendhal.tools.tiled.TileSetDefinition;

public class MazePainter {
	class Style {		
		protected static final int GROUND_INDEX = 42;
		public static final int PORTAL_INDEX = 59;
		protected final int[] wall = { 2, 4, 20, 3, 5, 15, 19, 16, 36, 32, 35, 31, 1 };
		
		public Style(StendhalMapStructure map) {
			TileSetDefinition set = new TileSetDefinition("filler", 1);
			set.setSource("../../tileset/ground/gravel.png");
			map.addTileset(set);
			
			set = new TileSetDefinition("outercorners", 2);
			set.setSource("../../tileset/building/wall/int_wall_dark_red_corners_2.png");
			map.addTileset(set);
			
			set = new TileSetDefinition("wall", 10);
			set.setSource("../../tileset/building/wall/int_wall_dark_red.png");
			map.addTileset(set);
			
			set = new TileSetDefinition("innercorners", 26);
			set.setSource("../../tileset/building/wall/int_wall_dark_red_corners.png");
			map.addTileset(set);
			
			set = new TileSetDefinition("paving", 42);
			set.setSource("../../tileset/ground/brown_paving.png");
			map.addTileset(set);
			
			set = new TileSetDefinition("portal", 44);
			set.setSource("../../tileset/building/decoration/floor_sparkle.png");
			map.addTileset(set);
		}
		
		public int selectTile(LayerDefinition collision, int x, int y) {
			if (collision.getTileAt(x, y) == 0) {
				return GROUND_INDEX;
			} else {
				return selectWall(collision, x, y);
			}
		}
		
		private int selectWall(LayerDefinition collision, int x, int y) {
			if (!collides(collision, x - 1, y)) {
				if (!collides(collision, x, y - 1)) {
					// nw corner
					return wall[0];
				} else if (!collides(collision, x, y + 1)) {
					// sw corner
					return wall[1];
				} else {
					// w wall
					return wall[2];
				}
			} else if (!collides(collision, x + 1, y)) {
				if (!collides(collision, x, y - 1)) {
					// ne corner
					return wall[3];
				} else if (!collides(collision, x, y + 1)) {
					// se corner
					return wall[4];
				} else {
					// e wall
					return wall[5];
				}
			} else if (!collides(collision, x, y - 1)) {
				// n wall
				return wall[6];
			} else if (!collides(collision, x, y + 1)) {
				// s wall
				return wall[7];
			} else if (!collides(collision, x - 1, y - 1)) {
				// inner nw corner
				return wall[8];
			} else if (!collides(collision, x - 1, y + 1)) {
				// inner sw corner
				return wall[9];
			} else if (!collides(collision, x + 1, y - 1)) {
				// inner nw corner
				return wall[10];
			} else if (!collides(collision, x + 1, y + 1)) {
				// inner nw corner
				return wall[11];
			}
			// Filler
			return wall[wall.length - 1];
		}
		
		private boolean collides(LayerDefinition collision, int x, int y) {
			if (x < 0 || y < 0 || x >= collision.getWidth() || y >= collision.getWidth()) {
				return true;
			} else {
				return (collision.getTileAt(x, y) != 0);
			}
		}
	}
	
	public void paint(StendhalMapStructure map) {
		LayerDefinition collision = map.getLayer("collision");
		LayerDefinition ground = map.getLayer("0_floor");
		
		// prepare the floor data arrays for for painting 
		ground.build();
		
		Style style = new Style(map);
		
		drawFloor(style, ground, collision);
	}
	
	public void paintPortal(StendhalMapStructure map, int x, int y) {
		LayerDefinition ground = map.getLayer("0_floor");
		ground.set(x, y, Style.PORTAL_INDEX);
	}
	
	private void drawFloor(Style style, LayerDefinition floor, LayerDefinition collision) {
		int width = floor.getWidth();
		int height = floor.getHeight();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				floor.set(x, y, style.selectTile(collision, x, y));
			}
		}
	}
}
