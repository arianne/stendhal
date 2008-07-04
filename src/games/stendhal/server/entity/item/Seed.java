package games.stendhal.server.entity.item;

import games.stendhal.server.actions.PlantAction;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;

import java.util.Map;

public class Seed extends Item implements UseListener {

	public Seed(Seed item) {
		super(item);
	}

	public Seed(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public boolean onUsed(RPEntity user) {
		
		PlantAction plantAction = new PlantAction();
		plantAction.setUser(user);
		plantAction.setSeed(this);
		return plantAction.execute();
	}
	
}
