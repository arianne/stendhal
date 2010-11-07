package games.stendhal.server.entity.mapstuff.useable;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * Factory for ViewChangeEntities
 */
public class ViewChangeEntityFactory implements ConfigurableFactory {
	public Object create(ConfigurableFactoryContext ctx) {
		int x = ctx.getRequiredInt("x");
		int y = ctx.getRequiredInt("y");
		ViewChangeEntity entity = new ViewChangeEntity(x, y);
		String desc = ctx.getString("description", null);
		if (desc != null) {
			entity.setDescription(desc);
		}
		
		return entity;
	}
}
