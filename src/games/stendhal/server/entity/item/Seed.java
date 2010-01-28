package games.stendhal.server.entity.item;

import games.stendhal.server.actions.PlantAction;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;

import java.util.Map;

/**
 * A seed can be planted. 
 * The plant action defines the behaviour (e.g. only plantable on fertile ground).
 * The infostring stores what it will grow.
 */
public class Seed extends StackableItem implements UseListener {

	public Seed(final Seed item) {
		super(item);
	}

	/**
	 * Creates a new seed
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Seed(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public boolean onUsed(final RPEntity user) {
		
		final PlantAction plantAction = new PlantAction();
		plantAction.setUser(user);
		plantAction.setSeed(this);
		return plantAction.execute();
	}


	@Override
	public String describe() {
		final String flowerName = getInfoString();

		if (flowerName != null) {
			return "You see a " + flowerName + " seed. It can be planted anywhere, but it will only thrive on fertile ground.";
		} else {
			return "You see a seed. It can be planted anywhere, but it will only thrive on fertile ground.";
		}
	}
	
}
