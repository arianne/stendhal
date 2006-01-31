package games.stendhal.client.events;

import games.stendhal.client.entity.*;

interface TalkEvent 
  {
  // Called when entity says text
  public void onTalk(String text);
  // Called when entity listen to text from talker
  public void onListen(RPEntity talker, String text);
  } 
