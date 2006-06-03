package games.stendhal.server.scripting;

import games.stendhal.server.*;

public class StendhalPythonConfig {
	private StendhalRPRuleProcessor rp;

	private StendhalRPWorld world;

	public StendhalRPWorld getWorld() {
		return world;
	}

	public StendhalRPRuleProcessor getRules() {
		return rp;
	}

	public void setContext(StendhalRPRuleProcessor rp, StendhalRPWorld world) {
		this.rp = rp;
		this.world = world;
	}

	public void init() {
		// do nothing
	}
}
