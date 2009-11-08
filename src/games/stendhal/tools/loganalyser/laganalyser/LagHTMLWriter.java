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
		// TODO Auto-generated method stub
		
	}

	public void writeFooter() {
		// TODO Auto-generated method stub
		
	}

	public void writeTurnOverflows(int[] times) {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		ps.close();
	}

}
