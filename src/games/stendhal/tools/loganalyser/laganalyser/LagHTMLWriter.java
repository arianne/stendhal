/**
 * 
 */
package games.stendhal.tools.loganalyser.laganalyser;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * writes a colored html based lag report
 *
 * @author hendrik
 */
public class LagHTMLWriter {
	private PrintStream ps;

	public LagHTMLWriter(String outputFileName) throws FileNotFoundException {
		this.ps = new PrintStream(outputFileName);
	}

	public void writeHeader() {
		ps.println("<html>");
		ps.println("<head><title>Lag</title></head>");
		ps.println("<body>");
		ps.println("<table border=\"1\"");
	}

	public void writeTurnOverflows(int[] times) {
		ps.print("<tr>");
		for (int i = 0; i < times.length; i++) {
			ps.print("<td>" + times[i] + "</td>");
		}
		ps.println("</tr>");
	}

	public void writeFooter() {
		ps.println("</table>");
		ps.println("</body>");
		ps.println("</html>");
	}

	public void close() {
		ps.close();
	}

}
