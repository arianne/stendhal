package games.stendhal.server.script;

import games.stendhal.server.core.reflectiondebugger.FieldLister;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;

import java.util.List;

/**
 * inspects the java fields of an object 
 * 
 * @author hendrik
 */
public class FieldInspect extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {

		// help text
		if (args.size() == 0) {
			admin.sendPrivateText("/script FieldInspect <entity name or number>");
			return;
		}

		Object entity = EntityHelper.entityFromTargetName(args.get(0), admin);
		if (entity == null) {
			admin.sendPrivateText("Entity not found");
			return;
		}

		FieldLister fl = new FieldLister(entity);
		fl.scan();
		admin.sendPrivateText("Entity is of class " + entity.getClass().getName() + "\r\n" + fl.getResult());
	}

}
