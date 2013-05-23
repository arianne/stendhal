package games.stendhal.server.core.config;

import games.stendhal.server.core.rule.defaultruleset.DefaultNPC;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Load and configure NPCs via an XML configuration file.
 */
public class NPCGroupsXMLLoader extends DefaultHandler {
    
    private static final Logger logger = Logger.getLogger(NPCGroupsXMLLoader.class);
    
    /** The main NPC configuration file. */
    protected URI uri;

    /**
     * Create an xml based loader of NPC groups.
     * 
     * @param uri
     *            The location of the configuration file.
     */
    public NPCGroupsXMLLoader(final URI uri) {
        this.uri = uri;
    }
    
    /**
     * Create an xml based loader of NPC groups.
     * 
     * @param uri
     *            The location of the configuration file.
     */
    public NPCGroupsXMLLoader(final String uri) {
        try {
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            logger.error(e, e);
        }
    }
    
    /**
     * Loads NPCs
     * 
     * @return list of all NPCs.
     */
    public List<DefaultNPC> load() {
        final GroupsXMLLoader groupsLoader = new GroupsXMLLoader(uri);
        final List<DefaultNPC> list = new LinkedList<DefaultNPC>();
        try {
            List<URI> groups = groupsLoader.load();

            // Load each group
            for (final URI tempUri : groups) {
                final NPCsXMLLoader loader = new NPCsXMLLoader();
    
                try {
                    list.addAll(loader.load(tempUri));
                } catch (final SAXException ex) {
                    logger.error("Error loading creature group: " + tempUri, ex);
                }
            }
        } catch (SAXException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        
        return list;
    }
}
