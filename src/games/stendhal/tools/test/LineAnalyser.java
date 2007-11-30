package games.stendhal.tools.test;

import java.util.Arrays;
import java.util.List;

/**
 * Analyses a chat log line
 *
 * @author hendrik
 */
class LineAnalyser {
	private List<String> playerNames = Arrays.asList("hendrikus"); // todo: do not hard code this

	private String line;
	private String stripped;
	private String protagonist;
	
	protected LineAnalyser(String line) {
		this.line = line;
		stripTimeStamp();
		extractProtagonist();
	}

	private void stripTimeStamp() {
		int pos = line.indexOf(']');
		if (pos < 0) {
			stripped = line;
		}
		stripped = line.substring(pos);
	}

	private void extractProtagonist() {
		int posStart = stripped.indexOf('<');
		int posEnd = stripped.indexOf('>');
		if (posEnd < posStart || posStart < 0) {
			return;
		}
		protagonist = stripped.substring(posStart, posEnd);
	}

	protected String getLine() {
		return line;
	}

	protected String getProtagonist() {
		return protagonist;
	}

	protected String getStripped() {
		return stripped;
	}

	public boolean isPlayerSpeaking() {
		return (protagonist != null) && playerNames.contains(protagonist);
	}

	public boolean isNPCSpeaking() {
		return (protagonist != null) && !playerNames.contains(protagonist);
	}

	public boolean isEmpty() {
		return line.trim().equals("");
	}
	
	public boolean isStatus() {
		return protagonist == null;
	}
}
