package games.stendhal.server.core.scripting;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class ScriptInGroovy extends ScriptingSandbox {

	private final String groovyScript;

	private final Binding groovyBinding;

	private static final Logger logger = Logger.getLogger(ScriptInGroovy.class);

	public ScriptInGroovy(final String filename) {
		super(filename);
		groovyScript = filename;
		groovyBinding = new Binding();
		groovyBinding.setVariable("game", this);
		groovyBinding.setVariable("logger", logger);
		groovyBinding.setVariable("storage", new HashMap<Object, Object>());

		// TODO: get rid of these variables, use the Singleton getters
		// in the scripts
		groovyBinding.setVariable("rules", SingletonRepository.getRuleProcessor());
		groovyBinding.setVariable("world", SingletonRepository.getRPWorld());
	}

	// ------------------------------------------------------------------------

	@Override
	public boolean load(final Player player, final List<String> args) {
		groovyBinding.setVariable("player", player);
		if (args != null) {
			groovyBinding.setVariable("args", args.toArray(new String[args.size()]));
		} else {
			groovyBinding.setVariable("args", new String[0]);
		}
		final GroovyShell interp = new GroovyShell(groovyBinding);
		boolean ret = true;

		try {
			final File f = new File(groovyScript);
			interp.evaluate(f);
		} catch (final Exception e) {
			logger.error("Exception while sourcing file " + groovyScript, e);
			setMessage(e.getMessage());
			ret = false;
		} catch (final Error e) {
			logger.error("Exception while sourcing file " + groovyScript, e);
			setMessage(e.getMessage());
			ret = false;
		}

		return (ret);
	}

	@Override
	public boolean execute(final Player player, final List<String> args) {
		return load(player, args);
	}
}
