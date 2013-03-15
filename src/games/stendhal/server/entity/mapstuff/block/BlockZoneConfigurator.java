/**
 * 
 */
package games.stendhal.server.entity.mapstuff.block;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.Map;

/**
 * Factory for pushable Blocks
 * 
 * Necessary parameters:
 * start-x = initial x coordinate, where the block also will be resetted to
 * start-y = initial y coordinate, where the block also will be resetted to
 * 
 * optional parameters:
 * multi = boolean flag to allow the block being pushed more than once from its initial position, defaults to false
 * class = defines the client side representation differing from the default boulder, defaults to block
 * 
 * @author madmetzger
 */
public class BlockZoneConfigurator implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		int x = Integer.parseInt(attributes.get("start-x"));
		int y = Integer.parseInt(attributes.get("start-y"));
		boolean multiPush = false;
		final String multiString = attributes.get("multi");
		
		if(multiString != null) {
			multiPush = Boolean.parseBoolean(multiString);
		}
		
		String style = attributes.get("class");
		Block b;
		
		if(style == null) {
			b = new Block(x, y, multiPush);
		} else {
			b = new Block(x, y, multiPush, style);
		}
		
		zone.add(b, false);
		zone.addMovementListener(b);
		zone.addZoneEnterExitListener(b);
	}

}
