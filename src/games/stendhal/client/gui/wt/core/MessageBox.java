/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * MessageBox.java
 *
 * Created on 23. Oktober 2005, 11:09
 */

package games.stendhal.client.gui.wt.core;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple MessageBox.
 *
 * @author matthias
 */
public class MessageBox extends Panel implements ClickListener, CloseListener
{
  /** the max height of the panel */
  private static final int MAX_HEIGHT = 100;
  /** space between the buttons */
  private static final int BUTTON_SPACING = 5;
  
  /** the text panel */
  private TextPanel textPanel;
  /** the button */
  private List<Button> buttons;
  /** name of the button clicked when the window is closed */
  private String closeButtonName;
  
  /** false when the messagebox still has to layout the buttons */
  private boolean layedout;
  
  /** Creates a new instance of MessageBox */
  public MessageBox(String name, int x, int y, int width, String message, ButtonCombination buttonCombination)
  {
    super(name, x, y, width, MAX_HEIGHT);
    
    textPanel = new TextPanel("messageboxtext", 5,0, width-20, MAX_HEIGHT, message);
    addChild(textPanel);
    
    buttons = new ArrayList<Button>();
    for (ButtonEnum buttonEnum : buttonCombination.getButtons())
    {
      Button button = buttonEnum.getButton();
      button.registerClickListener(this);
      buttons.add(button);
    }
    this.closeButtonName = buttonCombination.getCloseButton().getName();

    int fullWidth = (buttons.size()-1) * BUTTON_SPACING;
    for (Button button : buttons)
    {
      fullWidth += button.getWidth();
      addChild(button);
    }
    
    int xpos = (getWidth() - fullWidth) / 2;

    for (Button button : buttons)
    {
      button.moveTo(xpos, 0);
      xpos += button.getWidth()+BUTTON_SPACING;
    }

    setMinimizeable(false);
    setFrame(true);
    setTitleBar(true);
    // we're watching ourself
    registerCloseListener(this);
  }
  
  /** draws the MessageBox */
  public Graphics draw(Graphics g)
  {
    // draw frame/title bar
    Graphics clientArea = super.draw(g);
    
    // layout the buttons
    if (!layedout)
    {
      int lastHeight = textPanel.getLastHeight();
      for (Button button : buttons)
      {
        button.moveTo(button.getX(), lastHeight);
      }
      layedout = true;
    }

    return clientArea;
  }

  /** clicked a button */
  public void onClick(String name, boolean pressed)
  {
    // tell our listeners that a button has been clicked
    notifyClickListeners(name,  true);
    removeCloseListener(this);
    close();
  }

  /** closed the window */
  public void onClose(String name)
  {
    // pseudoclicked the close button
    onClick(closeButtonName, true);
  }
  
  /** some default buttons */
  public enum ButtonEnum
  {
    YES    ("Yes"   , 50, 30),
    NO     ("No"    , 50, 30),
    CANCEL ("Cancel", 50, 30),
    OK     ("Ok"    , 50, 30),
    QUIT   ("Quit"  , 50, 30);
    
    private String name;
    private int width;
    private int height;
    
    /** private constructon */
    private ButtonEnum(String name, int width, int height)
    {
      this.name = name;
      this.width = width;
      this.height = height;
    }
    
    /** returns the name of this button */
    public String getName()
    {
      return name;
    }

    /** returns a new wt-button */
    public Button getButton()
    {
      return new Button(name, width, height, name);
    }
  }
  
  /** some button combinations */
  public enum ButtonCombination
  {
    OK           (ButtonEnum.OK     , ButtonEnum.OK),
    YES_NO       (ButtonEnum.NO     , ButtonEnum.YES, ButtonEnum.NO),
    YES_NO_CANCEL(ButtonEnum.CANCEL , ButtonEnum.YES, ButtonEnum.NO, ButtonEnum.CANCEL),
    OK_CANCEL    (ButtonEnum.CANCEL , ButtonEnum.OK, ButtonEnum.CANCEL),
    QUIT_CANCEL  (ButtonEnum.CANCEL , ButtonEnum.QUIT, ButtonEnum.CANCEL);
    
    /** list of buttons for this combination*/
    private List<ButtonEnum> buttons;
    /** default button when the window is closed */
    private ButtonEnum closeButton;
    
    /** contructor */
    private ButtonCombination(ButtonEnum closeButton, ButtonEnum ... buttons)
    {
      List<ButtonEnum> buttonList = new ArrayList<ButtonEnum>();
      this.closeButton = closeButton;
      for (ButtonEnum button : buttons)
      {
        buttonList.add(button);
      }
      this.buttons = Collections.unmodifiableList(buttonList);
    }
    
    /** returns a list with the buttons */
    public List<ButtonEnum> getButtons()
    {
      return buttons;
    }
    
    /** returns a list with the buttons */
    public ButtonEnum getCloseButton()
    {
      return closeButton;
    }
    
  }
}
