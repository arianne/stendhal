package games.stendhal.client.scripting;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class ScriptRecorder {
	private PrintStream ps = null;
	private String classname = null;
	private long lastTimestamp = 0;
	
	public ScriptRecorder(String classname) throws FileNotFoundException {
		String filename = classname; // TODO
		lastTimestamp = 0;
		ps = new PrintStream(filename);
	}
	
	public void start() {
		ps.println("package games.stendhal.client.script;");
		ps.println("import games.stendhal.client.scripting.*;");
		ps.println("/**");
		ps.println(" * TODO: write documentation");
		ps.println(" * ");
		// TODO: ps.println(" * @author recorded by" + player.getName());
		ps.println(" */");
		ps.println("public class " + classname + " extends ClientScriptImpl {");
		ps.println("");
		ps.println("\t@Override");
		ps.println("\tpublic void run(String args) {");
	}

	public void recordChatLine(String text) {

		// write sleep command (and add a paragraph if the wait time was large
		long thisTimestamp = System.currentTimeMillis();
		long diff = thisTimestamp - lastTimestamp;
		if (diff > 5000) {
			ps.println("");
			if (diff > 60000) {
				ps.println("\t\t// -----------------------------------");
			}
			ps.println("\t\tcsi.sleepSeconds(" + diff + ");");
		} else if (diff > 0) {
			ps.println("\t\tcsi.sleepMillis(" + diff + ");");
		}

		// write invoke command
		ps.println("\t\tcsi.invoke(\"" + text.replace("\"", "\\\"") + "\");");

		// reduce wait time by one turn because csi.invokes waits one turn
		lastTimestamp = thisTimestamp + 300;
	}
	
	public void end() {
		ps.println("\t}");
		ps.println("}");
	}
}
