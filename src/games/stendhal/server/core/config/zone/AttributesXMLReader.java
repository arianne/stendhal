package games.stendhal.server.core.config.zone;

import org.w3c.dom.Element;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.ZoneAttributes;

/**
 * Reader for zone attributes.
 */
public class AttributesXMLReader extends SetupXMLReader {
	@Override
	public SetupDescriptor read(Element element) {
		final AttributesDescriptor desc = new AttributesDescriptor();
		readParameters(desc, element);

		return desc;
	}

	/**
	 * Setup descriptor for preparing zone attributes.
	 */
	private static class AttributesDescriptor extends SetupDescriptor {
		@Override
		public void setup(StendhalRPZone zone) {
			ZoneAttributes attr = new ZoneAttributes(zone);
			attr.putAll(getParameters());
			zone.setAttributes(attr);
		}
	}
}
