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

	/**
	 * creates a new LagHTMLWriter
	 *
	 * @param outputFileName name of file to write
	 * @throws FileNotFoundException in case 
	 */
	public LagHTMLWriter(String outputFileName) throws FileNotFoundException {
		this.ps = new PrintStream(outputFileName);
	}

	/**
	 * writes the header of the html-file (including the content header).
	 */
	public void writeHeader() {
		ps.println("<html>");
		ps.println("<head><title>Lag</title></head>");
		ps.println("<body>");
		ps.println("<table border=\"1\">");
	}

	/**
	 * writes a line
	 *
	 * @param times times
	 */
	public void writeTurnOverflows(int[] times) {
		ps.print("<tr>");
		for (int i = 0; i < times.length; i++) {
			ps.print("<td>" + times[i] + "</td>");
		}
		ps.println("</tr>");
	}

	/**
	 * writes the html footer.
	 */
	public void writeFooter() {
		ps.println("</table>");
		ps.println("</body>");
		ps.println("</html>");
	}

	/**
	 * closes the output stream.
	 */
	public void close() {
		ps.close();
	}

}
