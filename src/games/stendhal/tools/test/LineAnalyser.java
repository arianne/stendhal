package games.stendhal.tools.test;

import java.util.Arrays;
import java.util.List;

/**
 * Analyses a chat log line
 *
 * @author hendrik
 */
class LineAnalyser {
	private List<String> playerNames = Arrays.asList("hendrikus", "player", 
		"rosie", "gambit", "superkym", "Heman", "jellybean", "Heman"); // todo: do not hard code this

	private String line;
	private String stripped;
	private String protagonist;
	private boolean comment = false;  
	
	protected LineAnalyser(String line) {
		this.line = line.trim();
		stripTimeStamp();
		stripComment();
		extractProtagonist();
	}

	private void stripTimeStamp() {
		stripped = line;
		int pos = stripped.indexOf(']');
		if (pos < 0) {
			return;
		}
		stripped = stripped.substring(pos + 2);
	}
	
	private void stripComment() {
		if (line.startsWith("//")) {
			comment = true;
			stripped = line.substring(3);
		}
	}

	private void extractProtagonist() {
		if (comment) {
			return;
		}
		int posStart = stripped.indexOf('<');
		int posEnd = stripped.indexOf('>');
		if (posEnd < posStart || posStart < 0) {
			return;
		}
		protagonist = stripped.substring(posStart + 1, posEnd);
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

	public String getText() {
		if (protagonist == null) {
			return stripped;
		}
		return line.substring(line.indexOf("> ") + 1).trim();
	}

	public boolean isEmpty() {
		return line.trim().equals("");
	}
	
	public boolean isComment() {
		return comment;
	}

	public boolean isNPCSpeaking() {
		return (protagonist != null) && !playerNames.contains(protagonist);
	}

	public boolean isPlayerSpeaking() {
		return (protagonist != null) && playerNames.contains(protagonist);
	}

	public boolean isStatus() {
		return !isEmpty() && (protagonist == null);
	}
}
