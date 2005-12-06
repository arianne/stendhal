/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.*;
import tiled.mapeditor.widget.*;

public class PropertiesDialog extends JDialog implements ActionListener,
       ListSelectionListener
{
  private static final long serialVersionUID = 6545364425824220311L;
  
    private JTable tProperties;
    private JButton bOk, bCancel, bDel;
    private Properties properties;
    private PropertiesTableModel tableModel;

    public PropertiesDialog(JFrame parent, Properties p) {
        super(parent, "Properties", true);
        properties = p;
        init();
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void init() {
        tableModel = new PropertiesTableModel();
        tProperties = new JTable(tableModel);
        tProperties.getSelectionModel().addListSelectionListener(this);
        JScrollPane propScrollPane = new JScrollPane(tProperties);
        propScrollPane.setPreferredSize(new Dimension(200, 150));

        bOk = new JButton("OK");
        bCancel = new JButton("Cancel");
        try {
            bDel = new JButton(new ImageIcon(MapEditor.loadImageResource("resources/gnome-delete.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        bOk.addActionListener(this);
        bCancel.addActionListener(this);
        bDel.addActionListener(this);
       
        JPanel user = new VerticalStaticJPanel();
        user.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        user.setLayout(new BoxLayout(user, BoxLayout.X_AXIS));
        user.add(Box.createGlue());
        user.add(Box.createRigidArea(new Dimension(5, 0)));
        user.add(bDel);
        
        JPanel buttons = new VerticalStaticJPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(bOk);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(bCancel);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(propScrollPane);
        mainPanel.add(user);
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(bOk);
    }

    private void updateInfo() {
        // Make a copy of the properties that will be changed by the
        // properties table model.
        Properties props = new Properties();
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement(); 
            props.put(key, properties.getProperty(key));
        }
        tableModel.update(props);
    }

    public void getProps() {
        updateInfo();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == bOk) {
            // Copy over the new set of properties from the properties table
            // model.
            properties.clear();

            Properties newProps = tableModel.getProperties();
            Enumeration keys = newProps.keys();
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement(); 
                properties.put(key, newProps.getProperty(key));
            }

            dispose();
        } else if (source == bCancel) {
            dispose();
        } else if (source == bDel) {
            int total = tProperties.getSelectedRowCount();
            Object[] keys = new Object[total];
            int[] selRows = tProperties.getSelectedRows();
            
            for(int i = 0; i < total; i++) {
                keys[i] = tProperties.getValueAt(selRows[i], 0);
            }
            
            for (int i = 0; i < total; i++) {
                if (keys[i] != null) {
                    tableModel.remove(keys[i]);
                }
            }            
        }
    }

    public void valueChanged(ListSelectionEvent e) {
    }
}
