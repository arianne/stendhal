/*
 * JCreature.java
 *
 * Created on 6 de mayo de 2007, 22:52
 */

package monsterxmlcreator;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.server.config.CreaturesXMLLoader;
import games.stendhal.server.config.ItemsXMLLoader;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;
import games.stendhal.server.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.rule.defaultruleset.DefaultItem;
import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import org.xml.sax.SAXException;


/**
 *
 * @author  miguel
 */
public class JCreature extends javax.swing.JFrame {
    List<DefaultCreature> creatures;
    List<DefaultItem> items;
    
    /** Creates new form JCreature */
    public JCreature() throws SAXException {
        initComponents();
        creatures=loadCreaturesList();
        items=loadItemsList();
        setLists();
        creatureList.setSelectedIndex(0);
        refresh();
    }
    
    private void setLists() {
        creatureClass.setModel(new javax.swing.DefaultComboBoxModel(
             new String[] {
            "animal",
            "beholder",
            "boss",
            "dark_elf",
            "demon",
            "dwarf",
            "elemental",
            "elf",
            "gargoyle",
            "giant",
            "giant_animal",
            "giant_human",
            "gnome",
            "goblin",
            "golem",
            "huge_animal",
            "human",
            "kobold",
            "minotaur",
            "mummy",
            "mutant",
            "mythical_animal",
            "orc",
            "ratfolk",
            "small_animal",
            "troll",
            "undead",
            "vampire"}));
        
        updateLists();
    }
    
    private void updateLists() {
        itemList.setModel(new javax.swing.AbstractListModel() {
            public Object getElementAt(int i) { return items.get(i).getItemName(); }
            public int getSize() { return items.size(); }
        });

        creatureList.setModel(new javax.swing.AbstractListModel() {
            public Object getElementAt(int i) {
                DefaultCreature creature=creatures.get(i);
                return "("+creature.getLevel()+") "+creature.getCreatureName();
            }
            public int getSize() { return creatures.size(); }
        });
    }
    
    private List<DefaultCreature> loadCreaturesList() throws SAXException {
        CreaturesXMLLoader creatureLoader = CreaturesXMLLoader.get();
        List<DefaultCreature> creatures = creatureLoader.load("creatures.xml");
        sortCreatures(creatures);
        return creatures;
    }
    
    private void sortCreatures(final List<DefaultCreature> creatures) {
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
    
    private List<DefaultItem> loadItemsList() throws SAXException {
        ItemsXMLLoader itemsLoader = ItemsXMLLoader.get();
        List<DefaultItem> items = itemsLoader.load("items.xml");
        
        return items;
    }

    private void clean(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(2, 2, creatureImage.getWidth() - 2, creatureImage.getHeight() - 2);
    }

    private void drawSinglePart(Sprite sprite, double w, double h, Graphics g)  {
        clean(g);
        if(w == 1.0D && h == 2D)
            w = 1.5D;
        int offset = (int)(h * 2D * 32D);
        if(sprite.getHeight() < offset)
            offset = 0;
        int x = creatureImage.getWidth() / 2 - (int)((w * 32D) / 2D);
        int y = creatureImage.getHeight() / 2 - (int)((h * 32D) / 2D);
        sprite.draw(g, x, y, 0, offset, (int)(w * 32D), (int)(h * 32D));
    }

    private void refresh() {
        int pos=creatureList.getSelectedIndex();

        if(pos<0) {
            return;
        }
        
        DefaultCreature actual = (DefaultCreature)creatures.get(pos);
        
        if(actual.getCreatureName()==null) {
            return;
        }
        
        creatureName.setText(actual.getCreatureName());
        creatureClass.setSelectedItem(actual.getCreatureClass());
        creatureSubclass.setText(actual.getCreatureSubClass());
        creatureSize.setText((int)actual.getWidth()+","+(int)actual.getHeight());
        creatureTileid.setText(actual.getTileId().replace("../../tileset/logic/creature/",""));
        String gfxLocation = "/"+actual.getCreatureClass()+"/"+actual.getCreatureSubClass()+".png";
        Sprite spr = SpriteStore.get().getSprite("data/sprites/monsters"+gfxLocation);
        
        drawSinglePart(spr, actual.getWidth(), actual.getHeight(), creatureImage.getGraphics());
        creatureGFXLocation.setText(gfxLocation);
        creatureDescription.setText(actual.getDescription());
        
        creatureATK.setText(Integer.toString(actual.getATK()));
        creatureDEF.setText(Integer.toString(actual.getDEF()));
        creatureHP.setText(Integer.toString(actual.getHP()));
        creatureSpeed.setText(Double.toString(actual.getSpeed()));
        
        creatureLevel.setText(Integer.toString(actual.getLevel()));
        creatureXP.setText(Integer.toString(actual.getXP()/20));
        creatureRespawn.setText(Integer.toString(actual.getRespawnTime()));
        
        StringBuffer os = new StringBuffer("");
        for(DropItem item: actual.getDropItems()) {
            os.append(item.name+"; ["+item.min+","+item.max+"]; "+item.probability+"\n");
        }
        
        if(actual.getCreatureName() != null)
            creatureDrops.setText(os.toString());
        
        os = new StringBuffer("");
        for(EquipItem item: actual.getEquipedItems()) {
            os.append(item.name+"; "+item.slot+"; "+item.quantity+"\n");
        }

        if(actual.getCreatureName() != null)
            creatureEquips.setText(os.toString());
        
        
        os = new StringBuffer("");
        for(String line: actual.getNoiseLines()) {
            os.append("says: "+line+"\n");
        }
            
    	Map<String,String> aiList=actual.getAIProfiles();
    	for(Map.Entry<String, String> profile: aiList.entrySet()) {
    		os.append(profile.getKey());
    		if(profile.getValue()!=null) {
    			os.append(" = "+profile.getValue());
    		}
    		os.append("\n");
    	}

        if(actual.getCreatureName() != null)
            creatureAI.setText(os.toString());
    }
    
    
    /** This method is called from withinthe constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        creatureList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        creatureSubclass = new javax.swing.JTextField();
        creatureSize = new javax.swing.JTextField();
        creatureGFXLocation = new javax.swing.JTextField();
        creatureImage = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        creatureTileid = new javax.swing.JTextField();
        creatureClass = new javax.swing.JComboBox();
        updateGFXButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        creatureName = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        creatureLevel = new javax.swing.JTextField();
        creatureXP = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        creatureRespawn = new javax.swing.JTextField();
        SuggestAttributeButton = new javax.swing.JButton();
        SuggestLevelButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        creatureATK = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        creatureDEF = new javax.swing.JTextField();
        creatureHP = new javax.swing.JTextField();
        creatureSpeed = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        data = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        creatureDescription = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        creatureDrops = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        creatureEquips = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        creatureAI = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        setButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jSave = new javax.swing.JMenu();
        jSaveToFile = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Stendhal Creature Editor 2.00");
        creatureList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        creatureList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        creatureList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                creatureListValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(creatureList);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel2.setFont(new java.awt.Font("Arial", 1, 11));
        jLabel2.setText("Class:");

        jLabel3.setText("Subclass:");

        jLabel4.setText("GFX Location:");

        jLabel5.setText("Size:");

        creatureSubclass.setText("jTextField3");

        creatureSize.setText("jTextField4");

        creatureGFXLocation.setEditable(false);
        creatureGFXLocation.setText("jTextField5");

        creatureImage.setBackground(new java.awt.Color(255, 255, 255));
        creatureImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        org.jdesktop.layout.GroupLayout creatureImageLayout = new org.jdesktop.layout.GroupLayout(creatureImage);
        creatureImage.setLayout(creatureImageLayout);
        creatureImageLayout.setHorizontalGroup(
            creatureImageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 178, Short.MAX_VALUE)
        );
        creatureImageLayout.setVerticalGroup(
            creatureImageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 175, Short.MAX_VALUE)
        );

        jLabel6.setText("Tiled ID");

        creatureTileid.setText("jTextField6");

        creatureClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        updateGFXButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        updateGFXButton.setText("Update GFX");
        updateGFXButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateGFXButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel5)
                            .add(jLabel4)
                            .add(jLabel6)
                            .add(jLabel3))
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(creatureTileid)
                            .add(creatureGFXLocation)
                            .add(creatureSize)
                            .add(creatureSubclass)
                            .add(creatureClass, 0, 256, Short.MAX_VALUE)))
                    .add(updateGFXButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 49, Short.MAX_VALUE)
                .add(creatureImage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(creatureClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(creatureSubclass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel5)
                            .add(creatureSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(creatureGFXLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(creatureTileid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                        .add(updateGFXButton))
                    .add(creatureImage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setFont(new java.awt.Font("Arial", 1, 11));
        jLabel1.setText("Name");

        creatureName.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(52, 52, 52)
                .add(creatureName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 198, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(280, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(creatureName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel7.setText("Attributes");

        jLabel8.setText("Level:");

        jLabel9.setText("XP:");

        creatureLevel.setText("jTextField2");

        creatureXP.setText("jTextField7");

        jLabel10.setText("Respawn:");

        creatureRespawn.setText("jTextField8");

        SuggestAttributeButton.setText("Suggest Attributes");
        SuggestAttributeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SuggestAttributeButtonActionPerformed(evt);
            }
        });

        SuggestLevelButton.setText("Suggest Level");

        jLabel12.setText("ATK:");

        creatureATK.setText("jTextField1");

        jLabel13.setText("DEF:");

        jLabel14.setText("HP:");

        jLabel15.setText("Speed:");

        creatureDEF.setText("jTextField2");

        creatureHP.setText("jTextField3");

        creatureSpeed.setText("jTextField4");

        jLabel16.setText("turns");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel12)
                            .add(jLabel14)
                            .add(jLabel13)
                            .add(jLabel15))
                        .add(22, 22, 22)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(creatureSpeed)
                            .add(creatureHP)
                            .add(creatureDEF)
                            .add(creatureATK, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                        .add(51, 51, 51)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(SuggestAttributeButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                                .add(SuggestLevelButton))
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel8)
                                    .add(jLabel9)
                                    .add(jLabel10))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(creatureXP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                                    .add(creatureLevel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                                        .add(creatureRespawn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel16)))))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(creatureLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel12)
                    .add(creatureATK, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(creatureXP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(creatureDEF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(jLabel14)
                    .add(creatureHP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel16)
                    .add(creatureRespawn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(SuggestAttributeButton)
                        .add(SuggestLevelButton))
                    .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(creatureSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel15)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        creatureDescription.setColumns(20);
        creatureDescription.setLineWrap(true);
        creatureDescription.setRows(5);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creatureDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creatureDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        data.addTab("Description", jPanel2);

        creatureDrops.setColumns(20);
        creatureDrops.setLineWrap(true);
        creatureDrops.setRows(5);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creatureDrops, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creatureDrops, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
        );
        data.addTab("Drops", jPanel5);

        creatureEquips.setColumns(20);
        creatureEquips.setLineWrap(true);
        creatureEquips.setRows(5);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creatureEquips, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creatureEquips, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        data.addTab("Equips", jPanel6);

        creatureAI.setColumns(20);
        creatureAI.setLineWrap(true);
        creatureAI.setRows(5);

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creatureAI, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(creatureAI, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        data.addTab("AI Profile", jPanel7);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel11.setText("Items:");

        itemList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(itemList);

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(jLabel11)
                        .addContainerGap(240, Short.MAX_VALUE))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
        );

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        setButton.setFont(new java.awt.Font("Arial", 1, 12));
        setButton.setText("Set");
        setButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setButtonActionPerformed(evt);
            }
        });

        jSave.setText("Save");
        jSaveToFile.setText("To File");
        jSaveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSaveToFileActionPerformed(evt);
            }
        });

        jSave.add(jSaveToFile);

        jMenuBar1.add(jSave);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(data, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 284, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 100, Short.MAX_VALUE)
                        .add(setButton)
                        .add(28, 28, 28))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(data, 0, 0, Short.MAX_VALUE)
                            .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jScrollPane1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(addButton)
                            .add(setButton))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateGFXButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateGFXButtonActionPerformed
        try {
            String clazz=(String)creatureClass.getSelectedItem();
            String subclass=creatureSubclass.getText();
            
            String gfxLocation = "/"+clazz+"/"+subclass+".png";
            Sprite spr = SpriteStore.get().getSprite("data/sprites/monsters"+gfxLocation);
            String[] sizes=creatureSize.getText().split(",");
            
            drawSinglePart(spr, Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]), creatureImage.getGraphics());
            creatureGFXLocation.setText(gfxLocation);
        } catch(NumberFormatException e) {
        }
    }//GEN-LAST:event_updateGFXButtonActionPerformed

    private void SuggestAttributeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuggestAttributeButtonActionPerformed
        int level=Integer.parseInt(creatureLevel.getText());
        
        int atk=(int)(level*1.6+40);
        int def=(int)(level*level*0.0007+level*0.44+14);
        int hp=(int)(level*level*0.08+level*2+50);
        int xp=(level*level*10+level*200+50)/20;
        
        int pos=creatureList.getSelectedIndex();
        DefaultCreature actual = (DefaultCreature)creatures.get(pos);

        creatureATK.setText(Integer.toString(atk));
        creatureDEF.setText(Integer.toString(def));
        creatureHP.setText(Integer.toString(hp));
        creatureSpeed.setText(Double.toString(actual.getSpeed()));
        
        creatureXP.setText(Integer.toString(xp));        
    }//GEN-LAST:event_SuggestAttributeButtonActionPerformed

    private void jSaveToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSaveToFileActionPerformed
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(this);
        if(returnVal == 0)
        {
            java.io.File file = fc.getSelectedFile();
            try
            {
                PrintWriter out = new PrintWriter(new FileOutputStream(file));
                out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
                out.println("<creatures>");
                
                for(DefaultCreature c: creatures) {
                    out.println(c.toXML());
                }

                out.println("</creatures>");
                out.close();
            }
            catch(FileNotFoundException e) { 
            }
        }
    }//GEN-LAST:event_jSaveToFileActionPerformed

    private void setButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setButtonActionPerformed
        try {
        addButton.setEnabled(true);
        setButton.setForeground(Color.BLACK);
        
        int pos=creatureList.getSelectedIndex();
        DefaultCreature actual = (DefaultCreature)creatures.get(pos);
        
        actual.setCreatureName(creatureName.getText());
        actual.setCreatureClass((String)creatureClass.getSelectedItem());
        actual.setCreatureSubClass(creatureSubclass.getText());
        String[] sizes=creatureSize.getText().split(",");
        actual.setSize(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]));
        actual.setTileId(creatureTileid.getText());
        
        actual.setLevel(Integer.parseInt(creatureLevel.getText()),Integer.parseInt(creatureXP.getText())*20);
        actual.setRespawnTime(Integer.parseInt(creatureRespawn.getText()));
        
        actual.setDescription(creatureDescription.getText());
        
        actual.setRPStats(Integer.parseInt(creatureHP.getText()), 
            Integer.parseInt(creatureATK.getText()),
            Integer.parseInt(creatureDEF.getText()),
            Double.parseDouble(creatureSpeed.getText()));
        
        /* Drops */
        List<DropItem> dropList=new LinkedList<DropItem>();        
        BufferedReader reader=new BufferedReader(new StringReader(creatureDrops.getText()));
        String line=reader.readLine();
        while(line!=null) {
            String[] tok=line.split(";");
            String[] minmax=tok[1].replace("[","").replace("]","").split(",");
            dropList.add(new DropItem(tok[0].trim(),Double.parseDouble(tok[2].trim()),Integer.parseInt(minmax[0].trim()),Integer.parseInt(minmax[1].trim()))); 
            line=reader.readLine();
        }
        actual.setDropItems(dropList);

        List<EquipItem> equipList=new LinkedList<EquipItem>();        
        reader=new BufferedReader(new StringReader(creatureEquips.getText()));
        line=reader.readLine();
        while(line!=null) {
            String[] tok=line.split(";");
            equipList.add(new EquipItem(tok[1].trim(),tok[0].trim(),Integer.parseInt(tok[2].trim()))); 
            line=reader.readLine();
        }
        actual.setEquipedItems(equipList);
        
        Map<String,String> profiles=new HashMap<String,String>();
        List<String> noises=new LinkedList<String>();
        
        reader=new BufferedReader(new StringReader(creatureAI.getText()));
        line=reader.readLine();
        while(line!=null) {
            if(line.startsWith("says")) {
                noises.add(line.replace("says","").replace(":","").trim());
            } else {
                int i=line.indexOf("=");
                String key=null;
                String val=null;
                if(i!=-1) {
                    key=line.substring(0,i-1).trim();
                    val=line.substring(i+1).trim();                    
                } else {
                    key=line;
                }
                
                profiles.put(key,val);                        
            }
            
            line=reader.readLine();
        }
        actual.setAIProfiles(profiles);
        actual.setNoiseLines(noises);
        
        sortCreatures(creatures);
        setLists();
        refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_setButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        creatures.add(new DefaultCreature(null,null,null, null));
        updateLists();
        creatureList.setSelectedIndex(creatures.size()-1);
        creatureList.ensureIndexIsVisible(creatures.size()-1);
        refresh();
        setButton.setForeground(Color.RED);
        addButton.setEnabled(false);
    }//GEN-LAST:event_addButtonActionPerformed

    private void creatureListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_creatureListValueChanged
        refresh();
    }//GEN-LAST:event_creatureListValueChanged
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                new JCreature().setVisible(true);
                } catch(Exception e) {                    
                }
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SuggestAttributeButton;
    private javax.swing.JButton SuggestLevelButton;
    private javax.swing.JButton addButton;
    private javax.swing.JTextArea creatureAI;
    private javax.swing.JTextField creatureATK;
    private javax.swing.JComboBox creatureClass;
    private javax.swing.JTextField creatureDEF;
    private javax.swing.JTextArea creatureDescription;
    private javax.swing.JTextArea creatureDrops;
    private javax.swing.JTextArea creatureEquips;
    private javax.swing.JTextField creatureGFXLocation;
    private javax.swing.JTextField creatureHP;
    private javax.swing.JPanel creatureImage;
    private javax.swing.JTextField creatureLevel;
    private javax.swing.JList creatureList;
    private javax.swing.JTextField creatureName;
    private javax.swing.JTextField creatureRespawn;
    private javax.swing.JTextField creatureSize;
    private javax.swing.JTextField creatureSpeed;
    private javax.swing.JTextField creatureSubclass;
    private javax.swing.JTextField creatureTileid;
    private javax.swing.JTextField creatureXP;
    private javax.swing.JTabbedPane data;
    private javax.swing.JList itemList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JMenu jSave;
    private javax.swing.JMenuItem jSaveToFile;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton setButton;
    private javax.swing.JButton updateGFXButton;
    // End of variables declaration//GEN-END:variables
    
}
