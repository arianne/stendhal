package games.stendhal.client.events;

public interface TalkEvent {
	// Called when entity says text
	public void onTalk(String text);

	// Called when entity gets a private text
	public void onPrivateListen(String text);
}
