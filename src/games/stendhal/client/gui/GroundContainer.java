package games.stendhal.client.gui;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.StaticGameLayers;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.wt.EntityViewCommandList;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.Direction;
import games.stendhal.common.EquipActionConsts;

import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Mouse handler for the game screen floor.
 */
public class GroundContainer extends MouseHandler implements Inspector, 
	MouseWheelListener {
	/** 
	 * The amount to shift popup menus to have the first entry under 
	 * the mouse.
	 */
	private static final int MENU_OFFSET = 10;
	private static final Logger logger = Logger.getLogger(GroundContainer.class);
	
	/** The game screen this handler is providing mouse processing */
	private final IGameScreen screen;
	/** Client for sending actions */
	private final StendhalClient client;
	/** The UI. */
	private final j2DClient ui;
	/** Component to place popup menus */
	private final JComponent canvas;

	
	// mouse tweaks for MS windows
	private boolean windowWasActiveOnMousePressed = true;
	private int xOnMousePressed;
	private int yOnMousePressed;

	/** 
	 * <code>true</code>, when a context menu is visible, <em>or</em>
	 * it has been just closed with a click.
	 */
	private boolean contextMenuFlag;
	/**
	 * <code>true</code> if the next click should be ignored (because the click
	 * was used to hide a context menu).
	 */
	private boolean ignoreClick;
	
	/**
	 * Create a new GroundContainer.
	 * 
	 * @param client
	 * @param gameScreen
	 * @param canvas The component to place popup menus
	 */
	public GroundContainer(final StendhalClient client, final IGameScreen gameScreen, 
			final JComponent canvas) {
		this.client = client;
		this.screen = gameScreen;
		this.canvas = canvas;
		this.ui = j2DClient.get();
	}

	@Override
	protected void onDragStart(Point point) {
		ignoreClick = false;
		// Find the entity under the starting point
		final Point2D location = screen.convertScreenViewToWorld(point);
		final EntityView view = screen.getMovableEntityViewAt(location.getX(),
				location.getY());

		if (view != null) {
			// Let the DragLayer handle the drawing and dropping.
			DragLayer.get().startDrag(view.getEntity());
		}
	}

	@Override
	protected boolean onMouseClick(Point point) { 
		// Context menu detection
		if (ignoreClick) {
			ignoreClick = false;
			return false;
		}
		// on MS Windows releasing the mouse after a drag&drop action is
		// counted as mouse click: https://sourceforge.net/support/tracker.php?aid=2976895
		if ((Math.abs(point.getX() - xOnMousePressed) > 10) 
			|| (Math.abs(point.getY() - yOnMousePressed) > 10)) {
			return false;
		}
		
		// get clicked entity
		final Point2D location = screen.convertScreenViewToWorld(point);
		
		// for the text pop up....
		final Text text = screen.getTextAt(location.getX(), location.getY());
		if (text != null) {
			screen.removeText(text);
			return true;
		}

		// for the clicked entity....
		final EntityView view = screen.getEntityViewAt(location.getX(), location.getY());
		boolean doubleClick = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("ui.doubleclick", "false"));
		if ((view != null) && view.isInteractive()) {
			if (ui.isCtrlDown()) {
				view.onAction();
				return true;
			} else if (ui.isShiftDown()) {
				view.onAction(ActionType.LOOK);
				return true;
			} else if (!doubleClick) {
				return view.onHarmlessAction();
			}
		} else if (windowWasActiveOnMousePressed && !ui.isCtrlDown()) {
			if (!doubleClick) {
				createAndSendMoveToAction(location, false);
				// let it pass "unhandled", so that the possible double click
				// move can be sent to server as well
			}
		}

		return false;
	}

	@Override
	protected boolean onMouseDoubleClick(Point point) {
		final Point2D location = screen.convertScreenViewToWorld(point);

		final EntityView view = screen.getEntityViewAt(location.getX(), location.getY());

		if ((view != null) && view.isInteractive()) {
			// ... do the default action
			view.onAction();
			return true;
		} else {
			createAndSendMoveToAction(location, true);
			return true;
		}
	}

	@Override
	protected void onMouseRightClick(Point point) {
		ignoreClick = false;
		final Point2D location = screen.convertScreenViewToWorld(point);
		final EntityView view = screen.getEntityViewAt(location.getX(), location.getY());

		if (view != null) {
			// ... show context menu (aka command list)
			final String[] actions = view.getActions();

			if (actions.length > 0) {
				final IEntity entity = view.getEntity();

				JPopupMenu menu = new EntityViewCommandList(entity.getType(), actions, view);
				menu.show(canvas, point.x - MENU_OFFSET, point.y - MENU_OFFSET);
				contextMenuFlag = true;
				/*
				 * Tricky way to detect recent popup menues. We need the
				 * information to prevent walking when hiding the menu.
				 */
				menu.addPopupMenuListener(new PopupMenuListener() {
					public void popupMenuCanceled(PopupMenuEvent e) {
						//ignore
					}
					public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
						/*
						 *  Hidden. inform onMouseClick; unfortunately this gets
						 *  called before onMousePressed, so we need to push it
						 *  pack to the event queue
						 */
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								contextMenuFlag = false;	
							}
						});
					}
					public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
						// ignore
					}
				});
			}
		}
	}
	
	/**
	 * Remembers whether the client was active on last mouse down
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		ignoreClick = contextMenuFlag;
		windowWasActiveOnMousePressed = (KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() != null);
		xOnMousePressed = e.getX();
		yOnMousePressed = e.getY();
		super.mousePressed(e);
	}

	/**
	 * Send a move to command to the server.
	 * 
	 * @param point destination
	 * @param doubleClick <code>true</code> if the action was created with a
	 * 	double click, <code>false</code> otherwise
	 */
	private void createAndSendMoveToAction(final Point2D point, boolean doubleClick) {
		final RPAction action = new RPAction();
		action.put("type", "moveto");
		action.put("x", (int) point.getX());
		action.put("y", (int) point.getY());
		if (doubleClick) {
			action.put("double_click", "");
		}
		Direction dir = calculateZoneChangeDirection(point);
		if (dir != null) {
			action.put("extend", dir.ordinal());
		}
		client.send(action);
	}
	
	/**
	 * calculates whether the click was close enough to a zone border to trigger
	 * a zone change
	 *
	 * @param point click point in world coordinates
	 * @return Direction of the zone to change to, <code>null</code> if no zone change should happen
	 */
	private Direction calculateZoneChangeDirection(Point2D point) {
		StaticGameLayers layers = StendhalClient.get().getStaticGameLayers();
		double x = point.getX();
		double y = point.getY();
		double width = layers.getWidth();
		double height = layers.getHeight();
		if (x < 0.333) {
			return Direction.LEFT;
		}
		if (x > width - 0.333) {
			return Direction.RIGHT;
		}
		if (y < 0.333) {
			return Direction.UP;
		}
		if (y > height - 0.4) {
			return Direction.DOWN;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see games.stendhal.client.gui.DropTarget#dropEntity(games.stendhal.client.entity.IEntity, java.awt.Point)
	 */
	public void dropEntity(IEntity entity, Point point) {
		final RPAction action = new RPAction();
		
		RPObject item = entity.getRPObject();
		if (item == null) {
			return;
		}
		
		RPObject parent = item.getContainer();
		if (parent != null) {
			// looks like an drop
			action.put("type", "drop");
			
			action.put(EquipActionConsts.BASE_OBJECT, parent.getID().getObjectID());
			action.put(EquipActionConsts.BASE_SLOT, item.getContainerSlot().getName());
		} else {
			// it is a displace
			action.put("type", "displace");
		}
		action.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());

		// if ctrl is pressed, attempt to split stackables
		if (ui.isCtrlDown()) {
			action.put("quantity", 1);
		}

		// 'move to'
		final Point2D location = screen.convertScreenViewToWorld(point);
		action.put("x", (int) location.getX());
		action.put("y", (int) location.getY());

		client.send(action);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		/*
		 * Turning with mouse wheel. Ignore all but the first to avoid flooding
		 * the server with turn commands.
		 */
		logger.debug(e.getClickCount() + " click count and " + e.getScrollType() + " scroll type and wheel rotation " + e.getWheelRotation());
		if (e.getClickCount() <= 1) {
			Direction current = User.get().getDirection();
			if (e.getUnitsToScroll() > 0) {
				// Turn right
				client.addDirection(current.nextDirection(), true);
			} else {
				// Turn left
				client.addDirection(current.nextDirection().oppositeDirection(), true);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see games.stendhal.client.entity.Inspector#inspectMe(games.stendhal.client.entity.IEntity, marauroa.common.game.RPSlot, games.stendhal.client.gui.SlotWindow, int, int)
	 */
	public SlotWindow inspectMe(final IEntity suspect, final RPSlot content,
			final SlotWindow container, final int width, final int height) {
		if ((container != null) && container.isVisible()) {
			container.raise();
			return container;
		} else {
			SlotWindow window = new SlotWindow(suspect.getType(), width, height);
			window.setSlot(suspect, content.getName());

			j2DClient.get().addWindow(window);
			window.raise();
			window.setVisible(true);

			return window;
		}
	}
}
