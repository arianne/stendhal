package games.stendhal.client;

import java.awt.Graphics;

public interface IEntity 
  {
  public void move(long delta);
  public void setHorizontalMovement(double dx);
  public void setVerticalMovement(double dy);
  public double getHorizontalMovement();
  public double getVerticalMovement();

  public void draw(Graphics g);
  public void doLogic();

  public void setX(double x);
  public void setY(double y);
  public int getX();
  public int getY();
  }
