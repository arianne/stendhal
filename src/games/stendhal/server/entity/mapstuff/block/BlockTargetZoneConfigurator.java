package games.stendhal.server.entity.mapstuff.block;

import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
/**
 * Configurator for a block target
 *
 * required parameters
 * - x and y coordinate
 *
 * optional parameters
 * - condition the condition to evaluate when a block target can be triggered
 * - action the trigger action when the block target is triggered
 *
 * @author madmetzger
 */
public class BlockTargetZoneConfigurator implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		int x = Integer.parseInt(attributes.get("x"));
		int y = Integer.parseInt(attributes.get("y"));
		String condition = attributes.get("condition");
		String action = attributes.get("action");

		BlockTarget blockTarget = new BlockTarget();
		blockTarget.setPosition(x, y);

		try {
			if(condition != null) {
				ChatCondition created = createCondition(condition);
				if(created != null) {
					blockTarget.setCondition(created);
				}
			}
			if(action != null) {
				ChatAction created = createAction(action);
				if(created != null) {
					blockTarget.setAction(created);
				}
			}
		} catch (CompilationFailedException e) {
			throw new IllegalArgumentException(e);
		}

		zone.add(blockTarget);
	}

	/**
	 * Create a ChatAction
	 *
	 * @param action the configuration String
	 * @return the action or null
	 * @throws CompilationFailedException
	 */
	private ChatAction createAction(String action)
			throws CompilationFailedException {
		final GroovyShell interp = createGroovyShell();
		String code = "import games.stendhal.server.entity.npc.action.*;\r\n"
			+ action;
		ChatAction created = (ChatAction) interp.evaluate(code);
		return created;
	}

	/**
	 * Create a ChatCondtion
	 *
	 * @param condition the configuration String
	 * @return the condition or null
	 * @throws CompilationFailedException
	 */
	private ChatCondition createCondition(String condition)
			throws CompilationFailedException {
		final GroovyShell interp = createGroovyShell();
		String code = "import games.stendhal.server.entity.npc.condition.*;\r\n"
			+ condition;
		ChatCondition created = (ChatCondition) interp.evaluate(code);
		return created;
	}

	/**
	 * Create a GroovyShell
	 *
	 * @return a fresh GroovyShell
	 */
	private GroovyShell createGroovyShell() {
		Binding groovyBinding = new Binding();
		final GroovyShell interp = new GroovyShell(groovyBinding);
		return interp;
	}

}
