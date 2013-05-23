package games.stendhal.server.core.rule.defaultruleset;

import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.PassiveNPC;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class DefaultNPC {
    
    /** the logger instance. */
    private static final Logger logger = Logger.getLogger(DefaultCreature.class);
    
    /** NPC class. */
    private String clazz;
    
    /** NPC subclass */
    private String subclass;
    
    /** NPC name */
    // FIXME: name is not getting loaded from xml
    private String name = "empty";
    
    /** Optional NPC description */
    private String description;
    
    private Class< ? > implementation = null;
    
    /** Map Tile Id in the way tileset.png:pos. */
    private String tileid;
    
    /** size of the creature. */
    private int width;
    private int height;
    
    /** List of possible sound events. */
    private List<String> sounds;
    
    /** Looped sound effect for moving creature */
    private String movementSound;
    
    /** Phrases that NPC will say */
    private LinkedHashMap<String, LinkedList<String>> npcSays;
    
    /** NPC behavior */
    private Map<String, String> aiProfiles;
    
    /** speed relative to player [0.0 ... 1.0] */
    private double speed;
    
    public DefaultNPC(final String clazz, final String subclass, final String tileid) {
        this.clazz = clazz;
        this.subclass = subclass;
        this.tileid = tileid;
        
        npcSays = new LinkedHashMap<String, LinkedList<String>>();
        
        aiProfiles = new LinkedHashMap<String, String>();
    }
    
    public void setDescription(final String text) {
        this.description = text;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setImplementation(final Class< ? > implementation) {
        this.implementation = implementation;
        // FIXME: creator = buildCreator(implementation);
    }
    
    public Class< ? > getImplementation() {
        return implementation;
    }
    
    public void setRPStats(final double speed) {
        this.speed = speed;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setNoiseLines(final LinkedHashMap<String, LinkedList<String>> npcSays) {
        this.npcSays = npcSays;
    }
    
    public HashMap<String, LinkedList<String>> getNoiseLines() {
        return npcSays;
    }
    
    public void setAIProfiles(final Map<String, String> aiProfiles) {
        this.aiProfiles = aiProfiles;
    }
    
    /**
     * FIXME: Should be used for multiple types of NPCs
     * 
     * @return a NPC-instance. 
     */
    public NPC getNPC() {
        final PassiveNPC npc = new PassiveNPC();
        
        npc.setEntityClass(clazz);
        npc.setEntitySubclass(subclass);
        
        // FIXME: these methods do not exist (move from Creature to NPC)
        //npc.setSounds(sounds);
        //npc.setMovementSound(movementSound);
        
        //npc.setBaseSpeed(speed); Done in NPCsXMLLoader (I think)
        //npc.setSize(width, height); Done in NPCsXMLLoader
        
        // FIXME: this method does not exist
        //npc.setAIProfiles(aiProfiles);
        
        // FIXME: random path should only be used if aiProfile is not specified
        npc.moveRandomly();
        
        npc.update();
        npc.updateModifiedAttributes();
        
        return npc;
    }
    
    /** @return the tileid. */
    public String getTileId() {
        return tileid;
    }

    public void setTileId(final String val) {
        tileid = val;
    }

    /** @return the class. */
    public String getNPCClass() {
        return clazz;
    }

    public String getNPCSubclass() {
        return subclass;
    }

    public String getNPCName() {
        return name;
    }

    public void setNPCClass(final String val) {
        clazz = val;
    }

    public void setNPCSubclass(final String val) {
        subclass = val;
    }

    public void setNPCName(final String val) {
        name = val;
    }
    
    /**
     * Set the possible sound names.
     * 
     * @param sounds list of sounds
     */
    public void setNPCSounds(List<String> sounds) {
        this.sounds = sounds;
    }
    
    /**
     * Set a looped sound effect for NPC when moving
     * 
     * @param movementSound
     *              desired sound effect
     */
    public void setNPCMovementSound(String movementSound) {
        this.movementSound = movementSound;
    }
    
    public String toXML() {
        final StringBuilder os = new StringBuilder();
        if (name != null) {
            os.append("  <npc name=\"" + name + "\">\n");
        } else {
            os.append("  <npc>\n");
        }
        os.append("    <type class=\"" + clazz + "\" subclass=\"" + subclass
                + "\" tileid=\""
                + tileid.replace("../../tileset/logic/npc/", "")
                + "\"/>\n");
        if (description != null) {
            os.append("    <description>" + description + "</description>\n");
        }
        os.append("    <attributes>\n");
        os.append("      <speed value=\"" + speed + "\"/>\n");
        os.append("      <size value=\"" + width + "," + height + "\"/>\n");
        os.append("    </attributes>\n");
        os.append("    <ai>\n");
        if (!npcSays.isEmpty()) {
            os.append("      <says>\n");
            
            while(npcSays.entrySet().iterator().hasNext()) {
                final Entry<String, LinkedList<String>> entry =
                    npcSays.entrySet().iterator().next();
                for (int i=0; i<entry.getValue().size(); i++) {
                    os.append("        <noise state=\""+entry.getKey()+
                            "\" value=\"" + entry.getValue().get(i) + "\"/>\n");                    
                }
            }

        os.append("      </says>\n");
        }
        for (final Map.Entry<String, String> entry : aiProfiles.entrySet()) {
            os.append("      <profile name=\"" + entry.getKey() + "\"");
            if (entry.getValue() != null) {
                os.append(" params=\"" + entry.getValue() + "\"");
            }
            os.append("/>\n");
        }
        os.append("    </ai>\n");
        os.append("  </npc>\n");
        return os.toString();
    }
    
    public Map<String, String> getAIProfiles() {
        return aiProfiles;
    }
}
