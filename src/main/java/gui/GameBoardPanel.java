package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import connection.Connection;
import connection.SingleConnection;
import game.Game;
import game.Player;
import game.board.Location;
import game.cards.ColorCard;
import gui.dialog.BuyingDialog;

public class GameBoardPanel extends JPanel {

	private static final long serialVersionUID = 7437383669142439957L;

	private Point origin = new Point(0, 0);
	private Dimension dimensions;

	private double scaleFactorBase = 1.1;
	private int scrollDirection = -1;
	private int scaleCount = 0;
	private double scaleFactor = 1;

	private boolean pressed;
	private Point pressedPoint;

	private Point hoveredPoint;
	private SingleConnection hoveredConnection;

	public GameBoardPanel() {
		MouseHelper mouseHelper = new MouseHelper();
		this.addMouseWheelListener(mouseHelper);
		this.addMouseListener(mouseHelper);
		this.addMouseMotionListener(mouseHelper);
		this.setDoubleBuffered(true);
	}

	public void startGame() {
		this.dimensions = new Dimension(Game.getInstance().getMap().getDimensions());
	}

	@Override
	public void paint(Graphics g) {
		this.resetScreen(g);
		super.paint(g);
		if (Game.getInstance().isGameStarted()) {
			g.drawImage(Game.getInstance().getMap().getMapImage(), this.origin.x, this.origin.y, this.dimensions.width, this.dimensions.height, this);
			this.drawConnections(g);
			this.drawCities(g);
		}
	}

	private void resetScreen(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	private void drawCities(Graphics g) {
		g.setColor(Color.BLACK);
		List<Location> locations = Game.getInstance().getLocations();
		Iterator<Location> it = locations.iterator();
		Font font = g.getFont();
		g.setFont(new Font(font.getName(), font.getStyle(), Math.max((int) (font.getSize() / this.scaleFactor), 9)));
		while (it.hasNext()) {
			Location location = it.next();
			Point drawingPoint = new Point((int) ((location.point.x / this.scaleFactor) + this.origin.x), (int) ((location.point.y / this.scaleFactor) + this.origin.y));
			this.drawCross(g, drawingPoint, 5);

			g.drawString(location.name, drawingPoint.x + 10, drawingPoint.y - 10);
		}
	}

	private void drawConnections(Graphics g) {
		this.hoveredConnection = null;
		List<Connection> connections = Game.getInstance().getConnections();
		Iterator<Connection> it = connections.iterator();
		while (it.hasNext()) {
			Connection connection = it.next();
			Point from = connection.fromLocation.point;
			Point to = connection.toLocation.point;

			int dx = to.x - from.x;
			if (dx < 0) { // From ist immer der linke Punkt
				Point tmp = to;
				to = from;
				from = tmp;
				dx = to.x - from.x;
			}
			int dy = +(to.y - from.y);
			if (dx == 0) {
				dx = 1;
			}
			double slope = dy / (double) dx;
			double radiansSlope = Math.atan(slope);

			int distance = 20;

			int x1 = (int) (this.origin.x + ((from.x + (Math.cos(radiansSlope) * distance)) / this.scaleFactor));
			int y1 = (int) (this.origin.y + ((from.y + (Math.sin(radiansSlope) * distance)) / this.scaleFactor));
			int x2 = (int) (this.origin.x + ((to.x - (Math.cos(radiansSlope) * distance)) / this.scaleFactor));
			int y2 = (int) (this.origin.y + ((to.y - (Math.sin(radiansSlope) * distance)) / this.scaleFactor));

			double antiSlope = -1 / slope;
			double radiansAntiSlope = Math.atan(antiSlope);

			int multiLineDistance = (int) (15 / this.scaleFactor);
			int multiLineDistanceX = (int) (Math.cos(radiansAntiSlope) * multiLineDistance);
			int multiLineDistanceY = (int) (Math.sin(radiansAntiSlope) * multiLineDistance);

			List<Polygon> polygons = Collections.emptyList();
			switch (connection.multiplicity) {
			case 1:
				SingleConnection s01 = connection.getSingleConnectionAt(0);
				polygons = this.createPolygons(x1, y1, x2, y2, multiLineDistanceX, multiLineDistanceY, .5f, -.5f, radiansSlope, s01.length);
				this.setAndDrawConnection(g, polygons, s01);
				break;
			case 2:
				SingleConnection s02 = connection.getSingleConnectionAt(0);
				polygons = this.createPolygons(x1, y1, x2, y2, multiLineDistanceX, multiLineDistanceY, 1f, .25f, radiansSlope, s02.length);
				this.setAndDrawConnection(g, polygons, s02);
				SingleConnection s12 = connection.getSingleConnectionAt(1);
				polygons = this.createPolygons(x1, y1, x2, y2, multiLineDistanceX, multiLineDistanceY, -.25f, -1f, radiansSlope, s12.length);
				this.setAndDrawConnection(g, polygons, s12);
				break;
			case 3:
				SingleConnection s03 = connection.getSingleConnectionAt(0);
				polygons = this.createPolygons(x1, y1, x2, y2, multiLineDistanceX, multiLineDistanceY, 1f, .5f, radiansSlope, s03.length);
				this.setAndDrawConnection(g, polygons, s03);
				SingleConnection s13 = connection.getSingleConnectionAt(1);
				polygons = this.createPolygons(x1, y1, x2, y2, multiLineDistanceX, multiLineDistanceY, .25f, -.25f, radiansSlope, s13.length);
				this.setAndDrawConnection(g, polygons, s13);
				SingleConnection s23 = connection.getSingleConnectionAt(2);
				polygons = this.createPolygons(x1, y1, x2, y2, multiLineDistanceX, multiLineDistanceY, -.5f, -1f, radiansSlope, s23.length);
				this.setAndDrawConnection(g, polygons, s23);
				break;
			}
		}
	}

	private void setAndDrawConnection(Graphics g, List<Polygon> polygons, SingleConnection singleConnection) {
		Player owner = singleConnection.getOwner();
		boolean containsPoint = this.hoveredPoint == null ? false : polygons.stream().anyMatch(p -> p.contains(this.hoveredPoint));
		boolean contains = containsPoint && (this.hoveredConnection == null);
		Graphics2D g2d = (Graphics2D) g.create();
		if (contains && (owner == null)) {
			this.hoveredConnection = singleConnection;
		}
		if (contains) {
			g2d.setStroke(new BasicStroke(5));
		} else {
			g2d.setStroke(new BasicStroke(2));
		}
		if (owner != null) {
			g2d.setColor(owner.playerColor.realColor);
			polygons.forEach(g2d::fillPolygon);
		}
		g2d.setColor(singleConnection.color.realColor);
		polygons.forEach(g2d::drawPolygon);
	}

	private List<Polygon> createPolygons(int x1, int y1, int x2, int y2, int multiLineDistanceX, int multiLineDistanceY, float factor1, float factor2, double radianSlope, int segments) {
		ArrayList<Polygon> polygons = new ArrayList<>();
		final double distance = 10;
		int lengthX = x2 - x1;
		int lengthY = y2 - y1;
		for (int i = 0; i < segments; i++) {
			int[] xPoints = { (int) (x1 + (i * (lengthX / segments)) + ((Math.cos(radianSlope) * distance) / 2) + (multiLineDistanceX * factor1)),
					(int) (x1 + (i * (lengthX / segments)) + ((Math.cos(radianSlope) * distance) / 2) + (multiLineDistanceX * factor2)),
					(int) (((x1 + ((i + 1) * (lengthX / segments))) - ((Math.cos(radianSlope) * distance) / 2)) + (multiLineDistanceX * factor2)),
					(int) (((x1 + ((i + 1) * (lengthX / segments))) - ((Math.cos(radianSlope) * distance) / 2)) + (multiLineDistanceX * factor1)) };
			int[] yPoints = { (int) (y1 + (i * (lengthY / segments)) + ((Math.sin(radianSlope) * distance) / 2) + (multiLineDistanceY * factor1)),
					(int) (y1 + (i * (lengthY / segments)) + ((Math.sin(radianSlope) * distance) / 2) + (multiLineDistanceY * factor2)),
					(int) (((y1 + ((i + 1) * (lengthY / segments))) - ((Math.sin(radianSlope) * distance) / 2)) + (multiLineDistanceY * factor2)),
					(int) (((y1 + ((i + 1) * (lengthY / segments))) - ((Math.sin(radianSlope) * distance) / 2)) + (multiLineDistanceY * factor1)) };
			polygons.add(new Polygon(xPoints, yPoints, 4));
		}
		return polygons;
	}

	@SuppressWarnings("unused")
	private void drawGrid(Graphics g) {
		g.setColor(Color.BLACK);
		int gridSize = 100;
		for (int i = 0; i <= this.dimensions.width; i += gridSize) {
			g.drawLine(i + this.origin.x, this.origin.y, i + this.origin.x, this.dimensions.height + this.origin.y);
		}
		g.drawLine(this.dimensions.width + this.origin.x, this.origin.y, this.dimensions.width + this.origin.x, this.origin.y + this.dimensions.height);
		for (int j = 0; j <= this.dimensions.height; j += gridSize) {
			g.drawLine(this.origin.x, j + this.origin.y, this.dimensions.width + this.origin.x, j + this.origin.y);
		}
		g.drawLine(this.origin.x, this.dimensions.height + this.origin.y, this.origin.x + this.dimensions.width, this.dimensions.height + this.origin.y);
	}

	private void drawCross(Graphics g, Point center, int sideLength) {
		g.drawLine(center.x - sideLength, center.y, center.x + sideLength, center.y);
		g.drawLine(center.x, center.y + sideLength, center.x, center.y - sideLength);
	}

	private final class MouseHelper extends MouseAdapter {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (GameBoardPanel.this.pressed) { return; }
			if (((GameBoardPanel.this.scaleCount < -10) && (e.getWheelRotation() < 0)) || ((GameBoardPanel.this.scaleCount > 10) && (e.getWheelRotation() > 0))) { return; }

			GameBoardPanel.this.scaleCount += e.getWheelRotation();
			GameBoardPanel.this.scaleFactorBase = 1.1;
			GameBoardPanel.this.scrollDirection = -1;
			GameBoardPanel.this.scaleFactor = Math.pow(GameBoardPanel.this.scaleFactorBase, GameBoardPanel.this.scaleCount);

			Dimension dim = Game.getInstance().getMap().getDimensions();

			GameBoardPanel.this.dimensions.width = (int) (dim.getWidth() * Math.pow(GameBoardPanel.this.scaleFactor, GameBoardPanel.this.scrollDirection));
			GameBoardPanel.this.dimensions.height = (int) (dim.getHeight() * Math.pow(GameBoardPanel.this.scaleFactor, GameBoardPanel.this.scrollDirection));

			Point mousePoint = e.getPoint();

			double diffX = mousePoint.x - GameBoardPanel.this.origin.x;
			double diffY = mousePoint.y - GameBoardPanel.this.origin.y;

			GameBoardPanel.this.origin.x += diffX - (diffX * Math.pow(GameBoardPanel.this.scaleFactorBase, GameBoardPanel.this.scrollDirection * e.getWheelRotation()));
			GameBoardPanel.this.origin.y += diffY - (diffY * Math.pow(GameBoardPanel.this.scaleFactorBase, GameBoardPanel.this.scrollDirection * e.getWheelRotation()));

			GameBoardPanel.this.repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int modifiers = e.getModifiersEx();
			if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) != 0) { return; }
			GameBoardPanel.this.pressed = true;
			GameBoardPanel.this.pressedPoint = e.getPoint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			GameBoardPanel.this.pressed = false;
			GameBoardPanel.this.pressedPoint = null;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point newPoint = e.getPoint();
			GameBoardPanel.this.hoveredPoint = newPoint;
			if (GameBoardPanel.this.pressedPoint == null) { return; }
			GameBoardPanel.this.origin.translate(newPoint.x - GameBoardPanel.this.pressedPoint.x, newPoint.y - GameBoardPanel.this.pressedPoint.y);
			GameBoardPanel.this.pressedPoint = newPoint;
			GameBoardPanel.this.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			GameBoardPanel.this.hoveredPoint = e.getPoint();
			GameBoardPanel.this.repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (GameBoardPanel.this.hoveredConnection == null) { return; }
			if (e.getButton() == MouseEvent.BUTTON3) {
				if (!Game.getInstance().isGameStarted() || Game.getInstance().isCardAlreadyDrawn() || !Game.getInstance().isPlayersTurn()) { return; }
				GameBoardPanel.this.showPopUpMenu(e, GameBoardPanel.this.hoveredConnection);
			}
		}
	}

	private void showPopUpMenu(MouseEvent e, SingleConnection selectedConnection) {
		Player player = Game.getInstance().getInstancePlayer();
		boolean canBuyConnection = player.canPlayerBuySingleConnection(selectedConnection)
				&& !player.getSingleConnections().stream().anyMatch(c -> c.parentConnection.equals(this.hoveredConnection.parentConnection));
		JPopupMenu menu = new JPopupMenu();
		JMenuItem buyButton = new JMenuItem("Buy");
		buyButton.setEnabled(canBuyConnection);
		buyButton.addActionListener(e1 -> {
			if (canBuyConnection) {
				List<ColorCard> bought = this.getBuyingOptions(player, selectedConnection);
				if ((bought != null) && !bought.isEmpty()) {
					Game.getInstance().playerBuysConnection(player, selectedConnection, bought);
					this.repaint();
				}
			}
		});
		buyButton.setSelected(true);
		menu.add(buyButton);
		menu.show(e.getComponent(), e.getX() - 20, e.getY() - 10);
	}

	private List<ColorCard> getBuyingOptions(Player player, SingleConnection singleConnection) {
		BuyingDialog dialog = new BuyingDialog(this, singleConnection, player);
		ColorCard[] colors = dialog.getSelectedBuyingOption();
		if ((colors == null) || (colors.length == 0)) { return Collections.emptyList(); }
		return Arrays.asList(colors);
	}

}
