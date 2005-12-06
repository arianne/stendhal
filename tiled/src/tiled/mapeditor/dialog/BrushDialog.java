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

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tiled.core.Tile;
import tiled.io.MapHelper;
import tiled.io.MapReader;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.brush.*;
import tiled.mapeditor.plugin.PluginClassLoader;
import tiled.mapeditor.util.LayerTableModel;
import tiled.mapeditor.util.TiledFileFilter;
import tiled.mapeditor.widget.BrushBrowser;
import tiled.mapeditor.widget.IntegerSpinner;
import tiled.mapeditor.widget.MiniMapViewer;
import tiled.mapeditor.widget.VerticalStaticJPanel;
import tiled.util.TiledConfiguration;


public class BrushDialog extends JDialog implements ActionListener,
       ItemListener, ChangeListener, PropertyChangeListener,
       ListSelectionListener
{
  private static final long serialVersionUID = -2959077893082929438L;

    private Brush myBrush;
    private MapEditor editor;

    private JCheckBox cbRandomBrush;
    private IntegerSpinner affectLayers, brushSize;
    private JSlider sRandomAmount;
    private JButton bOk, bApply, bCancel;
    private BrushBrowser brushes;
    private MiniMapViewer mmv;
    private JTable layerTable;
    
    public BrushDialog(MapEditor editor, JFrame parent,
            Brush currentBrush)
    {
        super(parent, "Brush Options", false);
        myBrush = currentBrush;
        this.editor = editor;

        init();
        update();
        pack();

        setLocationRelativeTo(parent);
    }

    private JPanel createShapePanel() {
        // Brush presets
        brushes = new BrushBrowser();
        JScrollPane brushScrollPane = new JScrollPane(brushes);
        brushScrollPane.setPreferredSize(new Dimension(100, 100));
        brushes.addPropertyChangeListener(this);
        brushes.setSelectedBrush(myBrush);

        JPanel presets = new JPanel();
        presets.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Presets"),
                    BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        presets.setLayout(new GridLayout(1, 1));
        presets.add(brushScrollPane);

        // Brush size
        brushSize = new IntegerSpinner(1, 1);
        if (myBrush != null) {
            brushSize.setValue(myBrush.getBounds().width);
        }
        brushSize.addChangeListener(this);
        brushSize.setToolTipText("Sets the size of the brush in tiles");

        // Number of affected layers
        affectLayers = new IntegerSpinner(myBrush.getAffectedLayers(), 1);
        affectLayers.addChangeListener(this);

        // Randomize tile placement
        cbRandomBrush = new JCheckBox("Random");
        cbRandomBrush.setToolTipText(
                "Make brush paint randomly within the shape area");
        cbRandomBrush.addItemListener(this);
        sRandomAmount = new JSlider();
        sRandomAmount.setPreferredSize(new Dimension(
                    50, sRandomAmount.getPreferredSize().height));
        sRandomAmount.setToolTipText(
                "The amount of area to fill with randomness");
        sRandomAmount.addChangeListener(this);

        // Brush options section
        JPanel opts = new VerticalStaticJPanel();
        opts.setLayout(new GridBagLayout());
        opts.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Options"),
                    BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 5, 0);
        opts.add(new JLabel("Brush size: "), c);
        c.gridy = 1;
        opts.add(new JLabel("Affected layers: "), c);
        c.gridy = 2;
        opts.add(cbRandomBrush, c);
        c.gridy = 0; c.gridx = 1; c.weightx = 0.3;
        c.fill = GridBagConstraints.HORIZONTAL;
        opts.add(brushSize, c);
        c.gridy = 1;
        opts.add(affectLayers, c);
        c.gridy = 2;
        opts.add(sRandomAmount, c);

        JPanel shapePanel = new JPanel();
        shapePanel.setLayout(new BoxLayout(shapePanel, BoxLayout.Y_AXIS));
        shapePanel.add(presets);
        shapePanel.add(opts);

        return shapePanel;
    }

    private JPanel createCustomPanel() {
        JPanel customPanel = new JPanel();
        
        mmv = new MiniMapViewer();
        if (myBrush instanceof CustomBrush) {
            //mmv.setView(((CustomBrush)myBrush));
        }
        
        JScrollPane miniSp = new JScrollPane();
        miniSp.getViewport().setView(mmv);
        miniSp.setPreferredSize(new Dimension(100,100));
        JButton bCreate = new JButton("Create...");
        bCreate.addActionListener(this);
        //TODO: create functionality is not available yet
        bCreate.setEnabled(false);
        JButton bLoad = new JButton("Load...");
        bLoad.addActionListener(this);
        layerTable = new JTable(new LayerTableModel(myBrush));
        layerTable.getColumnModel().getColumn(0).setPreferredWidth(32);
        layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerTable.getSelectionModel().addListSelectionListener(this);
        
        customPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 2; c.weighty = 2;
        customPanel.add(miniSp, c);
        c.weightx = 1; c.weighty = 1;
        c.gridx=2;
        customPanel.add(bCreate, c);
        c.gridy=1;
        customPanel.add(bLoad, c);
        c.gridx=0; c.gridy=2;
        customPanel.add(layerTable, c);
        
        return customPanel;
    }

    private void init() {
        createCustomPanel();

        bOk = new JButton("OK");
        bApply = new JButton("Apply");
        bCancel = new JButton("Cancel");
        bOk.addActionListener(this);
        bApply.addActionListener(this);
        bCancel.addActionListener(this);
        bApply.setEnabled(false);

        /* BUTTONS PANEL */
        JPanel buttons = new VerticalStaticJPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(bOk);        
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(bApply);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(bCancel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(createShapePanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(bOk);
    }

    private void createFromOptions() {
        Tile t = null;

        if (myBrush instanceof ShapeBrush) {
            t = ((ShapeBrush)myBrush).getTile();
        }

        ShapeBrush sel = (ShapeBrush)brushes.getSelectedBrush();
        sel.setSize(((Integer)brushSize.getValue()).intValue());

        if (cbRandomBrush.isSelected()) {
            RandomBrush randomBrush = new RandomBrush(sel);
            randomBrush.setRatio(
                    sRandomAmount.getValue() /
                    (double)sRandomAmount.getMaximum());
            myBrush = randomBrush;
        } else {
            myBrush = new ShapeBrush(sel);
        }

        ((ShapeBrush)myBrush).setTile(t);
        myBrush.setAffectedLayers(
                ((Integer)affectLayers.getValue()).intValue());

        update();
    }

    private void update() {
        if (myBrush instanceof CustomBrush) {  // Custom brush
            affectLayers.setEnabled(false);
            cbRandomBrush.setEnabled(false);
        } else if (myBrush instanceof RandomBrush) {  // Random brush
            cbRandomBrush.setSelected(true);
            sRandomAmount.setValue(
                    (int)(((RandomBrush)myBrush).getRatio() * 100));
        }

        sRandomAmount.setEnabled(cbRandomBrush.isSelected());
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == bOk) {
            createFromOptions();
            editor.setBrush(myBrush);
            dispose();
        }
        else if (source == bApply) {
            createFromOptions();
            editor.setBrush(myBrush);
            bApply.setEnabled(false);
        }
        else if (source == bCancel) {
            dispose();
        }
        else if (e.getActionCommand().equals("Load...")) {
            try {
                openMap();
                //mmv.setView(((CustomBrush)myBrush));
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        e1.toString(), "Load Brush",
                        JOptionPane.WARNING_MESSAGE);
            }
            repaint();
        }
    }

    public void stateChanged(ChangeEvent e) {
        bApply.setEnabled(true);
    }

    public void itemStateChanged(ItemEvent event) {
        Object source = event.getItemSelectable();

        if (source == cbRandomBrush) {
            sRandomAmount.setEnabled(cbRandomBrush.isSelected());
        }

        bApply.setEnabled(true);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("selectedbrush")) {
            Brush b = brushes.getSelectedBrush();
            if (b != null) {
                brushSize.setValue(b.getBounds().width);
            }
        }

        bApply.setEnabled(true);
    }

    public void valueChanged(ListSelectionEvent e) {
        // TODO Auto-generated method stub
    }
    
    private void openMap() throws Exception {
        String startLocation = "";
        TiledConfiguration configuration = TiledConfiguration.getInstance();
        
        // Start at the location of the most recently loaded map file
        if (configuration.hasOption("tiled.recent.1")) {
            startLocation = configuration.getValue("tiled.recent.1");
        }

        JFileChooser ch = new JFileChooser(startLocation);

        try {
            MapReader readers[] =
                (MapReader[])PluginClassLoader.getInstance().getReaders();
            for(int i = 0; i < readers.length; i++) {
                ch.addChoosableFileFilter(new TiledFileFilter(
                            readers[i].getFilter(), readers[i].getName()));
            }
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(this,
                    "Error while loading plugins: " + e.getMessage(),
                    "Error while loading map",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        ch.addChoosableFileFilter(
                new TiledFileFilter(TiledFileFilter.FILTER_TMX));
        
        int ret = ch.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            myBrush = new CustomBrush(
                    MapHelper.loadMap(ch.getSelectedFile().getAbsolutePath()));
        }
    }
}
