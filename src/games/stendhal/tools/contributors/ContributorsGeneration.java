/***************************************************************************
 *                   (C) Copyright 2020 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools.contributors;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

/**
 * generates contributors.md
 *
 * @author hendrik
 */
public class ContributorsGeneration {
	private Iterable<Map<String, Object>> contributors;
	private Map<String, String> iconMap = new HashMap<>();
	private Iterable<Map<String, Object>> others;

	public ContributorsGeneration() {
		iconMap.put("a11y", "♿️");
		iconMap.put("audio", "🔊");
		iconMap.put("bug", "🐛");
		iconMap.put("blog", "📝");
		iconMap.put("business", "💼");
		iconMap.put("code", "💻");
		iconMap.put("content", "🖋");
		iconMap.put("data", "🔣");
		iconMap.put("doc", "📖");
		iconMap.put("example", "💡");
		iconMap.put("eventOrganizing", "📋");
		iconMap.put("graphics", "🎨");
		iconMap.put("ideas", "🤔");
		iconMap.put("infra", "🚇");
		iconMap.put("maintenance", "🚧");
		iconMap.put("maps", "🗺");
		iconMap.put("platform", "📦");
		iconMap.put("plugin", "🔌");
		iconMap.put("projectManagement", "📆");
		iconMap.put("question", "💬");
		iconMap.put("review", "👀");
		iconMap.put("security", "🛡️");
		iconMap.put("tool", "🔧");
		iconMap.put("translation", "🌍");
		iconMap.put("test", "⚠️");
		iconMap.put("tutorial", "✅");
		iconMap.put("talk", "📢");
		iconMap.put("userTesting", "📓");
		iconMap.put("video", "📹");
		iconMap.put("founder", "💼");
	}

	@SuppressWarnings("unchecked")
	private void parse(final String inputFilename) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream(inputFilename),
				StandardCharsets.UTF_8)) {
			Map<String, Object> map = (Map<String, Object>) (JSONValue.parse(reader));
			this.contributors = (Iterable<Map<String, Object>>) map.get("all");
			this.others = (Iterable<Map<String, Object>>) map.get("other");
		}
	}


	private void writeHeader(final PrintStream out) {
		out.println("# Contributors\n");
		out.println("<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->");
		out.println("<!-- prettier-ignore-start -->");
		out.println("<!-- markdownlint-disable -->");
	}

	private void writeContributor(final PrintStream out, final Map<String, Object> contributor,
			final int colsPerRow) {
		out.print(" <td align=\"center\" width=\"" + (colsPerRow / 100) + "%\">");
		out.print("<a href=\"" + contributor.get("link") + "\">");
		String image = (String) contributor.get("image");
		if (image == null || image.trim().equals("")) {
			image = "https://i2.wp.com/openhub.net/anon80.gif";
		}
		out.print("<img src=\"" + image + "\" width=\"64\" height=\"64\" alt=\"\">");
		out.print("<br />");
		out.print("<sub><b>" + contributor.get("fullname") + "</b></sub>");
		// out.print("<br> (" + contributor.get("name") + ")");
		out.print("</a>");
		out.print("<br />");
		this.writeContributions(out, contributor);
		out.print("</td>");
	}

	@SuppressWarnings("unchecked")
	private void writeContributions(final PrintStream out, final Map<String, Object> contributor) {
		Iterable<Map<String, Object>> contributions = (Iterable<Map<String, Object>>) contributor.get("contributions");
		for (Map<String, Object> contribution : contributions) {
			//out.print("<a href=\"" + contribution.get("link") + "\" title=\"" + contribution.get("type") + "\">");
			out.println("<span title=\"" + contribution.get("type") + "\">");
			out.print(iconMap.get(contribution.get("type")));
			//out.print("</a> ");
			out.print("</span> ");
		}
	}

	private void writeContributors(final PrintStream out) {
		out.println("<table>");
		out.println("<tr>");

		int i = 0;
		int colsPerRow = 6;
		for (Map<String, Object> contributor : contributors) {
			if ((i % colsPerRow == 0) && (i > 0)) {
				out.println("</tr>");
				out.println("<tr>");
			}
			if (Boolean.FALSE.equals(contributor.get("confirmed"))) {
				continue;
			}

			this.writeContributor(out, contributor, colsPerRow);
			out.println("");
			i++;
		}

		out.println("</tr>");
		out.println("</table>");
	}

	private void formatOther(final StringBuilder sb, final Map<String, Object> other) {
		final String name = (String) other.get("name");
		final String fullname = (String) other.get("fullname");
		final String link = (String) other.get("link");

		sb.append("\n* ");
		if (link != null) {
			sb.append("[");
		}
		if (fullname != null) {
			sb.append(fullname);
			if (name != null) {
				sb.append(" (");
			}
		}
		if (name != null) {
			sb.append(name);
			if (fullname != null) {
				sb.append(")");
			}
		}
		if (link != null) {
			sb.append("](" + link + ")");
		}
	}

	private void writeOthers(final PrintStream out) {
		boolean add_others = false;
		if (others instanceof Collection) {
			add_others = ((Collection<?>) others).size() > 0;
		}

		if (add_others) {
			final StringBuilder sb = new StringBuilder("\n# Special Thanks\n\nThe following people released their work to the public with a suitable license for us to use.\n");

			for (final Map<String, Object> other : others) {
				formatOther(sb, other);
			}

			out.println(sb.toString());
		}
	}

	private void writeFooter(final PrintStream out) {
		out.println("<!-- markdownlint-enable -->");
		out.println("<!-- prettier-ignore-end -->");
		out.println("<!-- ALL-CONTRIBUTORS-LIST:END -->");
	}

	public void process(final String filename) throws IOException {
		// encode to UTF-8 by default & force LF line endings
		final PrintStream out = new PrintStream(System.out, true, "UTF-8") {
			@Override
			public void println() {
				write("\n".getBytes(), 0, 1);
			}

			@Override
			public void println(final String st) {
				write((st + "\n").getBytes(), 0, st.length() + 1);
			}
		};

		this.parse(filename);
		this.writeHeader(out);
		this.writeContributors(out);
		this.writeOthers(out);
		this.writeFooter(out);
	}

	public static void main(String[] args) throws IOException {
		new ContributorsGeneration().process("/doc/contributors/contributors.json");
	}

}
