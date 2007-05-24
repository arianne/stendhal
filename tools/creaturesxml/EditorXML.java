import games.stendhal.server.config.CreaturesXMLLoader;
import games.stendhal.server.config.ItemsXMLLoader;
import games.stendhal.server.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.rule.defaultruleset.DefaultItem;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.xml.sax.SAXException;
/*
 * EditorXML.java
 *
 * Created on 23 de mayo de 2007, 13:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author miguel
 */
public class EditorXML {
    private List<DefaultCreature> creatures;
    private List<DefaultItem> items;
    
    /** Creates a new instance of EditorXML */
    public EditorXML() throws SAXException {
        creatures=loadCreaturesList("creatures.xml");
        items=loadItemsList("items.xml");        
    }
    
    public static void main(String[] args) throws SAXException {
        EditorXML xml=new EditorXML();
        
        JCreature creature=new JCreature(xml);
        JItem item=new JItem(xml);
        
        creature.setVisible(true);
        item.setVisible(true);        
    }
    
    void sortCreatures(final List<DefaultCreature> creatures) {
        Collections.sort(creatures, new Comparator<DefaultCreature>() {
            
            public int compare(DefaultCreature o1, DefaultCreature o2) {
                return o1.getLevel() - o2.getLevel();
            }
            
            @Override
            public boolean equals(Object obj) {
                return true;
            }
        });
    }
    
    public void updateCreaturesFromFile(String ref) throws SAXException {
        creatures=loadCreaturesList(ref);
    }

    public void updateItemsFromFile(String ref) throws SAXException {
        items=loadItemsList(ref);
    }
    
    
    private List<DefaultCreature> loadCreaturesList(String ref) throws SAXException {
        CreaturesXMLLoader creatureLoader = CreaturesXMLLoader.get();
        List<DefaultCreature> creatures = creatureLoader.load(ref);
        sortCreatures(creatures);
        
        return creatures;
    }

    List<DefaultCreature> getCreatures() {
        return creatures;
    }

    private List<DefaultItem> loadItemsList(String ref) throws SAXException {
        ItemsXMLLoader itemsLoader = ItemsXMLLoader.get();
        List<DefaultItem> items = itemsLoader.load(ref);
        
        sortItems(items);
        
        return items;
    }
    
     void sortItems(final List<DefaultItem> items) {
        Collections.sort(items, new Comparator<DefaultItem>() {
            
            public int compare(DefaultItem o1, DefaultItem o2) {
                int cmp=o1.getItemClass().compareTo(o2.getItemClass());
                if(cmp==0) {
                    return o1.getValue()-o2.getValue();
                } 
                
                return cmp;                          
            }
            
            @Override
            public boolean equals(Object obj) {
                return true;
            }
        });
    }

     List<DefaultItem> getItems() {
        return items;
    }
    
}
