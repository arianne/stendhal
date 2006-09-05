// *********************************
//   unfinished experimental stuff
// *********************************

package games.stendhal.server.scripting;

import games.stendhal.server.entity.Player;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

public class ScriptInJava extends ScriptingSandbox {
	private static Logger logger = Logger.getLogger(ScriptInJava.class);
	private Script script = null;
	private String classname = null;

	public ScriptInJava(String filename) {
		super(filename);
		this.classname = filename;
	}


	private void instanceiate() throws MalformedURLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		   // Create new class loader 
		   // with current dir as CLASSPATH
		   File file = new File("./data/script");
		   ClassLoader loader = new URLClassLoader(new URL[] {file.toURL()});
		   // load class through new loader
		   Class aClass = loader.loadClass(classname);
		   script = (Script) aClass.newInstance();
	}


	@Override
	public boolean load(Player admin, String[] args) {
		Class[] signature = new Class[] {Player.class, String[].class, this.getClass()};
		Object[] params = new Object[] {admin, args, this};

		try {
			instanceiate();
			Method theMethod = script.getClass().getDeclaredMethod("load", signature);
			theMethod.invoke(script, params);
		} catch (Exception e) {
			logger.error(e, e);
			setMessage(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean execute(Player admin, String[] args) {
		Class[] signature = new Class[] {Player.class, String[].class};
		Object[] params = new Object[] {admin, args};

		try {
			Method theMethod = script.getClass().getDeclaredMethod("execute", signature);
			theMethod.invoke(script, params);
		} catch (Exception e) {
			logger.error(e, e);
			setMessage(e.getMessage());
			return false;
		}
		return true;
	}
	
	
	@Override
	public void unload() {
		Class[] signature = new Class[] {};
		Object[] params = new Object[] {};
		try {
			Method theMethod = script.getClass().getDeclaredMethod("unload", signature);
			theMethod.invoke(script, params);
		} catch (Exception e) {
			logger.error(e, e);
			setMessage(e.getMessage());
		}

		super.unload();
	}

	
}
