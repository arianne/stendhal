package games.stendhal.server.maps;

import java.util.Map;
import java.util.HashMap;
import games.stendhal.server.entity.npc.*;


public class NPCList 
  {
  static private NPCList instance;
  
  static public NPCList get()
    {
    if(instance==null)
      {
      instance=new NPCList();     
      }
    
    return instance;
    }
  
  private Map<String, SpeakerNPC> contents;
  
  private NPCList()
    {
    contents=new HashMap<String, SpeakerNPC>();
    }
  
  public SpeakerNPC get(String name)
    {
    return contents.get(name);
    }
  
  public boolean has(String name)
    {
    return contents.containsKey(name);
    }
  
  public SpeakerNPC add(SpeakerNPC npc)
    {
    if(!contents.containsKey(npc.getName()))
      {
      contents.put(npc.getName(), npc);            
      }    
    return npc;
    }
  }
