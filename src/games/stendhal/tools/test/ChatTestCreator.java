package games.stendhal.tools.test;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * This program creates a simple NPC chat test based on a chat log
 * copy&pasted from the the client chat log window. 
 *
 * @author hendrik
 */
public class ChatTestCreator {
	private BufferedReader br;
	private PrintStream out;
	private String playerName = "hendrikus"; // todo: do not hard code this

	public ChatTestCreator(BufferedReader br, PrintStream out) {
		this.br = br;
		this.out = out;
	}

	private void convert() throws IOException {
		String line = br.readLine();
		while (line != null) {
			out.println("\t\t//" + line);
			line = br.readLine();
		}
	}

	/**
	 * Converts a chat log into a test case
	 *
	 * @param args chatlog.txt [test.java]
	 * @throws IOException in case of an input/output error 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 1 || args.length > 2) {
			System.err.println("java " + ChatTestCreator.class.getName() + " chatlog.txt [chatlogtest.java]");
		}
		
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		PrintStream out = System.out;
		if (args.length > 1) {
			out = new PrintStream(new FileOutputStream(args[1]));
		}

		ChatTestCreator ctt = new ChatTestCreator(br, out);
		ctt.convert();

		br.close();
		out.close();
	}

}
