package games.stendhal.tools.contributors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SourceForgeContributorsGenerator {
	private static final String[] DEFAULT_AVATARS = {"monsterid", "wavatar", "retro", "robohash"};

	public void process(String folder, PrintStream out) throws FileNotFoundException, IOException {
		Map<String, Map<String, Object>> contributors = parse(folder);
		fetchProfiles(contributors);
		dump(contributors, out);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Object>>  parse(String folder) throws FileNotFoundException, IOException {
		Map<String, String> trackerToType = new HashMap<>();
		trackerToType.put("bugs", "bug");
		trackerToType.put("developers", "ideas");
		trackerToType.put("feature-requests", "ideas");
		trackerToType.put("patches", "ideas");

		Map<String, Map<String, Object>> contributors = new TreeMap<>();
		for (Map.Entry<String, String> entry : trackerToType.entrySet()) {
			try (InputStreamReader reader = new FileReader(folder + "/" + entry.getKey() + ".json")) {
				Map<String, Object> map = (Map<String, Object>) (JSONValue.parse(reader));
				parseTrackerFile(map, contributors, entry.getValue());
			}
		}
		try (InputStreamReader reader = new FileReader(folder + "/discussion.json")) {
			Map<String, Object> map = (Map<String, Object>) (JSONValue.parse(reader));
			parseDiscussionFile(map, contributors, "ideas");
		}
		return contributors;
	}

	private void parseDiscussionFile(Map<String, Object> file, Map<String, Map<String, Object>> contributors, String contributionType) {
		JSONArray tickets = (JSONArray) file.get("forums");
		for (Object ticket : tickets) {
			JSONArray threads = (JSONArray) ((JSONObject) ticket).get("threads");
			for (Object thread : threads) {
				JSONArray posts = (JSONArray) ((JSONObject) thread).get("posts");
				parsePosts(contributors, contributionType, posts);
			}
		}
	}

	private void parseTrackerFile(Map<String, Object> file, Map<String, Map<String, Object>> contributors, String contributionType) {
		JSONArray tickets = (JSONArray) file.get("tickets");
		for (Object ticket : tickets) {
			JSONObject discussionThread = (JSONObject) ((JSONObject) ticket).get("discussion_thread");
			JSONArray posts = (JSONArray) discussionThread.get("posts");
			parsePosts(contributors, contributionType, posts);
		}
	}

	private void parsePosts(Map<String, Map<String, Object>> contributors, String contributionType, JSONArray posts) {
		for (Object postObject : posts) {
			JSONObject post = (JSONObject) postObject;
			String name = (String) post.get("author");
			if (contributors.get(name) != null) {
				updateContributionsObject((JSONArray) contributors.get(name).get("contributions"), contributionType);
				continue;
			}
			Map<String, Object> contributor = new HashMap<>();
			contributor.put("name",  name);
			contributor.put("fullname",  name);
			contributor.put("link", "https://sourceforge.net/u/" + name + "/profile");
			contributor.put("image",  generateImageUrl((String) post.get("author_icon_url")));
			contributor.put("contributions", generateContributionsObject(contributionType));

			contributors.put(name,  contributor);
		}
	}

	@SuppressWarnings("unchecked")
	private static void updateContributionsObject(JSONArray contributions, String contributionType) {
		for (Object contributionObject : contributions) {
			JSONObject contribution = (JSONObject) contributionObject;
			if (contribution.get("type").equals(contributionType)) {
				Integer count = (Integer) contribution.get("count");
				if (count == null) {
					count = Integer.valueOf(1);
				}
				contribution.put("count", count + 1);
				return;
			}
		}

		JSONObject type = new JSONObject();
		type.put("type", contributionType);
		contributions.add(type);
	}

	@SuppressWarnings("unchecked")
	private static Object generateContributionsObject(String contributionType) {
		JSONObject type = new JSONObject();
		type.put("type", contributionType);
		type.put("count", Integer.valueOf(1));

		JSONArray contributions = new JSONArray();
		contributions.add(type);
		return contributions;
	}

	private static String generateImageUrl(String url) {
		int i = (int) (Math.random() * DEFAULT_AVATARS.length);
		return url.replaceAll("\\?.*", "?s=64&rating=PG&d=" + DEFAULT_AVATARS[i]);
	}

	@SuppressWarnings("unchecked")
	private void fetchProfiles(Map<String, Map<String, Object>> contributors) throws IOException {
		for (Map.Entry<String, Map<String, Object>> entry : contributors.entrySet()) {
			try (Reader reader = new InputStreamReader(new URL("https://sourceforge.net/rest/u/" + entry.getKey() +"/profile").openStream(), "UTF-8")) {
				Map<String, Object> contributor = entry.getValue();
				Map<String, Object> profile = (Map<String, Object>) JSONValue.parse(reader);
				String fullname = (String) profile.get("name");
				contributor.put("fullname", fullname);
				Map<String, String> location = (Map<String, String>) profile.get("localization");
				String city = location.get("city");
				if (city != null && !city.trim().equals("")) {
					contributor.put("city", city);
				}
				String country = location.get("country");
				if (country != null && !country.trim().equals("")) {
					contributor.put("country", country);
				}
			} catch (FileNotFoundException e) {
				System.err.println("Profile " + entry.getKey() + " does not exist.");
			}
		}
	}

	private void dump(Map<String, Map<String, Object>> contributors, PrintStream out) {
		for (Map<String, Object> contributor : contributors.values()) {
			out.println("\t\t{");
			out.println("\t\t\t\"name\": \"" + contributor.get("name") + "\",");
			out.println("\t\t\t\"fullname\": \"" + contributor.get("fullname") + "\",");
			out.println("\t\t\t\"link\": \"" + contributor.get("link") + "\",");
			out.println("\t\t\t\"image\": \"" + contributor.get("image") + "\",");
			if (contributor.get("city") != null) {
				out.println("\t\t\t\"city\": \"" + contributor.get("city") + "\",");
			}
			if (contributor.get("country") != null) {
				out.println("\t\t\t\"country\": \"" + contributor.get("country") + "\",");
			}
			out.print("\t\t\t\"contributions\": [");
			boolean first = true;
			for (Object contribution : (JSONArray) contributor.get("contributions")) {
				if (first) {
					first = false;
				} else {
					out.print(",");
				}
				out.println();
				out.println("\t\t\t\t{");
				out.println("\t\t\t\t\t\"type\": \"" + ((JSONObject) contribution).get("type") + "\",");
				out.println("\t\t\t\t\t\"count\": \"" + ((JSONObject) contribution).get("count") + "\"");
				out.print("\t\t\t\t}");
			}
			out.println();
			out.println("\t\t\t]");
			out.println("\t\t},");
		}
	}


	public static void main(String[] args) throws FileNotFoundException, IOException {
		try (PrintStream out = new PrintStream(new FileOutputStream("/tmp/out.json"))) {
			new SourceForgeContributorsGenerator().process("/tmp/backup/arianne-backup-2020-06-06-210506/", out);
		}
	}

}
