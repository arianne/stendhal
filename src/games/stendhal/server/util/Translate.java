package games.stendhal.server.util;

import games.stendhal.common.Grammar;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * A simple translation framework
 *
 * @author hendrik
 */
public class Translate {
	private static Logger logger = Logger.getLogger(Translate.class);
	private static Properties dictionary = new Properties();
	private static Grammar grammar = new Grammar();

	/**
	 * Loads the specified dictionary
	 *
	 * @param language the 2 letter language code
	 */
	public static void initLanguage(String language) {
		Grammar tempGrammar = null;
		try {
			dictionary.load(Translate.class.getResourceAsStream("../properties/" + language + ".txt"));
			tempGrammar = (Grammar) Class.forName(dictionary.getProperty("_grammar.class")).newInstance();
		} catch (Exception e) {
			logger.error(e, e);
		}
		if (tempGrammar != null) {
			grammar = tempGrammar;
		}
	}

	/**
	 * Translates a text into the specified language
	 *
	 * @param text text to translate
	 * @param args arguments to integrate
	 * @return translated text
	 */
	public static String _(String text, String... args) {

		// use translated text, if one was specified
		String translated = text;
		if (dictionary.get(text) != null) {
			translated = dictionary.getProperty(text);
		}

		// extract arguments
		VelocityContext context = new VelocityContext();
		context.put("grammar", grammar);
		int i = 0;
		for (String arg : args) {
			i++;
			context.put(Integer.toString(i), arg);
		}

		// parse arguments and invoke scripts
		Writer writer = new StringWriter();
		try {
			Velocity.evaluate(context, writer, "errors", translated);
		} catch (Exception e) {
			logger.error(e, e);
		}
		
		// return result
		return writer.toString();
	}
}
