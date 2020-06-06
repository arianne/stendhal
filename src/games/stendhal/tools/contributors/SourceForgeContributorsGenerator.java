package games.stendhal.tools.contributors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SourceForgeContributorsGenerator {
	private static final String[] DEFAULT_AVATARS = {"monsterid", "wavatar", "retro", "robohash"};
	
	@SuppressWarnings("unchecked")
	private void parse(String folder) throws FileNotFoundException, IOException {
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
		
		JSONArray array = new JSONArray();
		for (Map<String, Object> contributor : contributors.values()) {
			array.add(contributor);
		}
		System.out.println(JSONValue.toJSONString(array));
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

		JSONArray contributions = new JSONArray();
		contributions.add(type);
		return contributions;
	}

	private static String generateImageUrl(String url) {
		int i = (int) (Math.random() * DEFAULT_AVATARS.length);
		return url.replaceAll("\\?.*", "?s=64&rating=PG&d=" + DEFAULT_AVATARS[i]);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		new SourceForgeContributorsGenerator().parse("/tmp/backup/arianne-backup-2020-06-06-210506/");
	}
}
