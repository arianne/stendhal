package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;
import marauroa.common.game.RPObject;

public class FlowerGrowerTransFormer implements Transformer {

	public RPObject transform(RPObject object) {
			String itemname = object.get("class");
			itemname = itemname.substring(itemname.lastIndexOf('/') + 1, itemname.length() - "_grower".length());
			return new FlowerGrower(object, itemname);
	}

}
