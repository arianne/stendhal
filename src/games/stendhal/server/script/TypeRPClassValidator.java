package games.stendhal.server.script;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class TypeRPClassValidator extends ScriptImpl {
	private static Logger logger = Logger.getLogger(TypeRPClassValidator.class);


	@Override
	public void execute(final Player admin, final List<String> args) {
		for (IRPZone zone : StendhalRPWorld.get()) {
			for (RPObject object : zone) {
				if (!object.has("type")) {
					logger.warn("object " + object + " does not have type");
				} else {
					if (!object.getRPClass().getName().equals(object.get("type"))) {
						logger.warn("rpclass and type missmatch, rpclass: " + object.getRPClass().getName() + "  type: " + object.get("type"));
					}
				}
			}
		}
	}

}
