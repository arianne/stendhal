package games.stendhal.server.core.config;

import games.stendhal.server.core.rule.defaultruleset.DefaultNPC;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class NPCsXMLLoader extends DefaultHandler {
    
    /** the logger instance. */
    private static final Logger logger = Logger.getLogger(ItemsXMLLoader.class);
    
    private Class< ? > implementation;
    
    private String name;
    
    private String clazz;
    
    private String subclass;
    
    private String description;
    
    private String text;
    
    private String tileid;
    
    private double speed;
    
    private int sizeWidth;
    
    private int sizeHeight;
    
    /** List of possible sound events. */
    private List<String> sounds;
    
    /** Looped sound effect for moving creature */
    private String movementSound;
    
    private LinkedHashMap<String, LinkedList<String>> npcSays;
    
    private Map<String, String> aiProfiles;
    
    private List<DefaultNPC> list;
    
    private boolean ai;
    
    private boolean says;
    
    private boolean attributes;
    
    private String condition;
    
    NPCsXMLLoader() {
        // hide constructor, use NPCGroupsXMLLoader instead
    }
    
    public List<DefaultNPC> load(final URI uri) throws SAXException {
        list = new LinkedList<DefaultNPC>();
        // Use the default (non-validating) parser
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            // Parse the input
            final SAXParser saxParser = factory.newSAXParser();
            
            final InputStream is = NPCsXMLLoader.class.getResourceAsStream(uri.getPath());
            
            if (is == null) {
                throw new FileNotFoundException("cannot find resource '" + uri
                        + "' in classpath");
            }
            
            try {
                saxParser.parse(is, this);
            } finally {
                is.close();
            }
        } catch (final ParserConfigurationException t) {
            logger.error(t);
        } catch (final IOException e) {
            logger.error(e);
            throw new SAXException(e);
        }
        
        return list;
    }
    
    @Override
    public void startDocument() {
        // do nothing
    }
    
    @Override
    public void endDocument() {
        // do nothing
    }
    
    @Override
    public void startElement(final String namespaceURI, final String lName, final String qName,
            final Attributes attrs) {
        text = "";
        if (qName.equals("npc")) {
            name = attrs.getValue("name");
            condition = attrs.getValue("condition");
            ai = false;
            sounds = new LinkedList<String>();
            npcSays = new LinkedHashMap<String, LinkedList<String>>();
            aiProfiles = new LinkedHashMap<String, String>();
            description = null;
            implementation = null;
        } else if (qName.equals("type")) {
            clazz = attrs.getValue("class");
            subclass = attrs.getValue("subclass");

            tileid = "../../tileset/logic/npc/" + attrs.getValue("tileid");
        } else if (qName.equals("implementation")) {

            final String className = attrs.getValue("class-name");

            try {
                implementation = Class.forName(className);
            } catch (final ClassNotFoundException ex) {
                logger.error("Unable to load class: " + className);
            }
        } else if (qName.equals("attributes")) {
            attributes = true;
        } else if (attributes && qName.equals("speed")) {
            speed = Double.parseDouble(attrs.getValue("value"));
        } else if (attributes && qName.equals("size")) {
            final String[] size = attrs.getValue("value").split(",");

            sizeWidth = Integer.parseInt(size[0]);
            sizeHeight = Integer.parseInt(size[1]);
        } else if (qName.equals("ai")) {
            ai = true;
        } else if (ai && qName.equals("profile")) {
            aiProfiles.put(attrs.getValue("name"), attrs.getValue("params"));
        } else if (ai && qName.equals("says")) {
            says = true;
        } else if (says) {
            if (qName.equals("noise")) {
                final String states = attrs.getValue("state");
                final String value = attrs.getValue("value");
                final List<String> keys=Arrays.asList(states.split(" "));
                // no such state in noises, will add it
                for (int i=0; i<keys.size(); i++) {
                    final String key=keys.get(i);
                    if(npcSays.get(key)==null) {
                        final LinkedList<String> ll=new LinkedList<String>();
                        ll.add(value);
                        npcSays.put(key, ll);
                        // no such value in existing state, will add it
                    } else if (npcSays.get(key).indexOf(value)==-1) {
                        npcSays.get(key).add(value);
                        // both state and value already exists
                    } else {
                        logger.warn("NPCsXMLLoader: NPC (" + name +
                                "): double definition for noise \"" +
                                key + "\" (" + value + ")");
                    }
                }
            } else if (qName.equals("sound")) {
                sounds.add(attrs.getValue("value"));
            } else if (qName.equals("movement")) {
                movementSound = attrs.getValue("value");
            }
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String sName, final String qName) {
        if (qName.equals("npc")) {
            
            if (!XMLUtil.checkCondition(condition)) {
                return;
            }
            
            if (!tileid.contains(":")) {
                logger.error("Corrupt XML file: Bad tileid for NPC (" + name + ")");
                return;
            }
            
            final DefaultNPC npc = new DefaultNPC(clazz, subclass, tileid);
            
            npc.setRPStats(speed);
            npc.setSize(sizeWidth, sizeHeight);
            
            npc.setAIProfiles(aiProfiles);
            npc.setNoiseLines(npcSays);
            npc.setDescription(description);
            npc.setNPCSounds(sounds);
            npc.setNPCMovementSound(movementSound);
            list.add(npc);
        } else if (qName.equals("attributes")) {
            attributes = false;
        } else if (ai && qName.equals("says")) {
            says = false;
        } else if (qName.equals("ai")) {
            ai = false;
        } else if (qName.equals("description")) {
            if (text != null) {
                description = text.trim();
            }
            text = "";
        }
    }
    
    @Override
    public void characters(final char[] buf, final int offset, final int len) {
        text = text + (new String(buf, offset, len)).trim() + " ";
    }
}
