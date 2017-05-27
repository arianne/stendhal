/**
 *
 */
package games.stendhal.server.entity.mapstuff.block;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * Factory for pushable Blocks
 *
 * required parameters:
 * start-x = initial x coordinate, where the block also will be resetted to
 * start-y = initial y coordinate, where the block also will be resetted to
 *
 * optional parameters:
 * multi = boolean flag to allow the block being pushed more than once from its initial position, defaults to false
 * class = defines the client side representation differing from the default boulder, defaults to block
 * shape = defines special shape to make block fit only on special shaped targets
 * description = override default description
 * sounds = comma separated list of sounds which should be played clients side on push
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
		final String description = attributes.get("description");
		final String sounds = attributes.get("sounds");
		final String shape = attributes.get("shape");

		List<String> soundList = null;
		if(sounds!= null) {
			soundList = Arrays.asList(sounds.split(","));
		}

		if(multiString != null) {
			multiPush = Boolean.parseBoolean(multiString);
		}

		String style = attributes.get("class");
		Block b;


		if(style == null) {
			b = new Block(multiPush);
		} else {
			b = new Block( multiPush, style, shape, soundList);
		}
		b.setPosition(x, y);

		if(description != null) {
			b.setDescription(description);
		}

		zone.add(b, false);
	}

}
