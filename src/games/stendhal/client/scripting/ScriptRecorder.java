package games.stendhal.client.scripting;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.chatlog.StandardEventLine;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Record chat/commands.
 * 
 * @author hendrik
 */
public class ScriptRecorder {

	private final String classname;

	private final String filename;

	private final PrintStream ps;

	private long lastTimestamp;

	/**
	 * Creates a new ScriptRecorder.
	 * 
	 * @param classname
	 *            Name of Class to record
	 * @throws FileNotFoundException
	 *             if the file cannot be created
	 */
	public ScriptRecorder(final String classname) throws FileNotFoundException {
		this.classname = classname;
		filename = System.getProperty("java.io.tmpdir") + "/" + classname
				+ ".java";
		j2DClient.get().addEventLine(new StandardEventLine("Starting recoding to " + filename));
		lastTimestamp = 0;
		ps = new PrintStream(filename);
	}

	/**
	 * Starts the recording by writing the header.
	 */
	public void start() {
		ps.println("package games.stendhal.client.script;");
		ps.println("import games.stendhal.client.scripting.*;");
		ps.println("/**");
		// some compiler warning tools check for this keyword even outside of comments
		ps.println(" * TO" + "DO: write documentation");
		ps.println(" * ");
		ps.println(" * @author recorded by " + User.get().getName());
		ps.println(" */");
		ps.println("public class " + classname + " extends ClientScriptImpl {");
		ps.println("");
		ps.println("\t@Override");
		ps.println("\tpublic void run(String args) {");
		lastTimestamp = System.currentTimeMillis();
	}

	/**
	 * Records a chat/command.
	 * 
	 * @param text
	 *            command to record
	 */
	public void recordChatLine(final String text) {

		// ignore recording related commands
		if (text.startsWith("/record")) {
			return;
		}

		// write sleep command (and add a paragraph if the wait time was large
		final long thisTimestamp = System.currentTimeMillis();
		final long diff = thisTimestamp - lastTimestamp;
		if (diff > 5000) {
			ps.println("");
			if (diff > 60000) {
				ps.println("\t\t// -----------------------------------");
			}
			ps.println("\t\tcsi.sleepSeconds(" + (diff / 1000) + ");");
		} else if (diff > 0) {
			ps.println("\t\tcsi.sleepMillis(" + diff + ");");
		}

		// write invoke command
		ps.println("\t\tcsi.invoke(\"" + text.replace("\"", "\\\"") + "\");");

		// reduce wait time by one turn because csi.invokes waits one turn
		lastTimestamp = thisTimestamp + 300;
	}

	/**
	 * finishes the recording by writing the footer and closing the stream.
	 */
	public void end() {
		ps.println("\t}");
		ps.println("}");
		ps.close();
		j2DClient.get().addEventLine(new StandardEventLine("Stopping recoding to " + filename));
	}
}
