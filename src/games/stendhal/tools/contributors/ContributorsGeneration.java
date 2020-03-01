package games.stendhal.tools.contributors;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;

import org.json.simple.JSONValue;

/**
 * generates contributors.md
 *
 * @author hendrik
 */
public class ContributorsGeneration {
	private Iterable<Map<String, Object>> contributors;

	@SuppressWarnings("unchecked")
	private void parse(String inputFilename) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream(inputFilename))) {
			Map<String, Object> map = (Map<String, Object>) (JSONValue.parse(reader));
			this.contributors = (Iterable<Map<String, Object>>) map.get("all");
		}
	}
	

	private void writeHeader(PrintStream out) {
		out.println("<!-- prettier-ignore-start -->");
		out.println("<!-- markdownlint-disable -->");
		out.println("<table>");
		out.println("<tr>");
	}

	private void writeContributor(PrintStream out, Map<String, Object> contributor) {
		out.print(" <td align=\"center\">");
		out.print("<a href=\"" + contributor.get("link") + "\">");
		out.print("<img src=\"" + contributor.get("image") + "\" width=\"64\" alt=\"\">");
		out.print("<br />");
		out.print("<sub><b>" + contributor.get("fullname") + "</b></sub></a>");
		out.print("<br />");
		this.writeContributions(out, contributor);
		out.print("</td>");
	}
	
	@SuppressWarnings("unchecked")
	private void writeContributions(PrintStream out, Map<String, Object> contributor) {
		Iterable<Map<String, Object>> contributions = (Iterable<Map<String, Object>>) contributor.get("contributions");
		for (Map<String, Object> contribution : contributions) {
			out.print("<a href=\"" + contribution.get("link") + "\">");
			out.print(contribution.get("type"));
			out.print("</a> ");
		}
	}

	private void writeContributors(PrintStream out) {
		int i = 0;
		int colsPerRow = 7;
		for (Map<String, Object> contributor : contributors) {
			if ((i % colsPerRow == 0) && (i > 0)) {
				out.println("</tr>");
				out.println("<tr>");
			}
			if (Boolean.FALSE.equals(contributor.get("confirmed"))) {
				continue;
			}

			this.writeContributor(out, contributor);
			out.println("");
			i++;
		}
	}

	private void writeFooter(PrintStream out) {
		out.println("</tr>");
		out.println("</table>");
		out.println("<!-- markdownlint-enable -->");
		out.println("<!-- prettier-ignore-end -->");
		out.println("<!-- ALL-CONTRIBUTORS-LIST:END -->");
	}

	public void process(String filename) throws IOException {
		this.parse(filename);
		this.writeHeader(System.out);
		this.writeContributors(System.out);
		this.writeFooter(System.out);
	}

	public static void main(String[] args) throws IOException {
		new ContributorsGeneration().process("/doc/contributors.json");
	}

}
