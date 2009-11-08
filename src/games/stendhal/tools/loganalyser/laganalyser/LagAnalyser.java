package games.stendhal.tools.loganalyser.laganalyser;

import java.io.IOException;

/**
 * analyses turn overflows
 *
 * @author hendrik
 */
public class LagAnalyser {

	/**
	 * analyses the logfile for turn overflows and generates a colored html file
	 *
	 * @param inputFileName
	 * @param outputFileName
	 * @throws IOException 
	 */
	private void generateHTMLReport(String inputFileName, String outputFileName) throws IOException {
		LagReader reader = new LagReader(inputFileName);
		LagHTMLWriter writer = new LagHTMLWriter(outputFileName);

		writer.writeHeader();
		int[] times = reader.readTurnOverflowRelative();
		while (times != null) {
			writer.writeTurnOverflows(times);
			times = reader.readTurnOverflowRelative();
		}
		writer.writeFooter();

		reader.close();
		writer.close();
	}

	/**
	 * main method
	 *
	 * @param args inputfile, outputfile
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new LagAnalyser().generateHTMLReport(args[0], args[1]);
	}

}
