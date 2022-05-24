package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import game.Game;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.ColorCard.TransportMode;

public class JColorCardPanel extends JPanel {

	private static final long serialVersionUID = 7038017071626911475L;

	public JColorCardPanel() {
		super(new GridBagLayout());
		this.setBackground(Color.LIGHT_GRAY);
	}

	public void updateColorCardPanel(int maxPanelHeight) {
		this.removeAll();

		Map<TransportMode, SortedMap<Integer, List<ColorCard>>> rankingMap = Game.getInstance().getInstancePlayer().getColorCards();

		int padding = 5;
		double ratio = 2 / 3.0;
		int maxHeight = 150;
		int maxWidth = (int) (maxHeight * ratio);
		int height = 0;
		int width = 0;

		while (width <= (padding * 4)) {
			int maxColumnCount = rankingMap.values().stream().flatMap(t -> Stream.of(t.values().stream().flatMap(List::stream).toList().size())).mapToInt(i -> i).max().getAsInt();
			int maxPossibleHeight = ((maxPanelHeight - 20) / maxColumnCount) - (2 * padding);
			int maxRowCount = rankingMap.values().stream().flatMap(t -> Stream.of(new ArrayList<>(t.keySet()).get(0))).reduce(0, (t, u) -> t + u);
			int maxPossibleWidth = ((this.getWidth() - 10) / maxRowCount) - (2 * padding);
			height = maxPossibleHeight > maxHeight ? maxHeight : maxPossibleHeight;
			width = maxPossibleWidth > maxWidth ? maxWidth : maxPossibleWidth;
			double proportion = width / (double) height;
			if (proportion < ratio) {
				height = (int) (width / ratio);
			} else {
				width = (int) (height * ratio);
			}
			padding--;
		}

		Dimension prefederredDimension = new Dimension(width, height);
		GridBagConstraints gbcTransport = new GridBagConstraints();
		gbcTransport.anchor = GridBagConstraints.NORTH;
		JPanel transportPanel;
		GridBagConstraints gbc = new GridBagConstraints();
		Iterator<Entry<TransportMode, SortedMap<Integer, List<ColorCard>>>> iteratorMap = rankingMap.entrySet().iterator();
		while (iteratorMap.hasNext()) {
			transportPanel = new JPanel(new GridBagLayout());
			transportPanel.setBackground(Color.LIGHT_GRAY);
			gbcTransport.gridx++;
			gbc.insets = new Insets(padding, padding, padding, padding);
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.BOTH;

			Entry<TransportMode, SortedMap<Integer, List<ColorCard>>> entryTransportMode = iteratorMap.next();
			Iterator<Entry<Integer, List<ColorCard>>> it = entryTransportMode.getValue().entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, List<ColorCard>> entry = it.next();
				JColorCardLabel label;
				List<ColorCard> cards = entry.getValue();
				for (int i = 0, m = cards.size(); i < m; i++) {
					ColorCard card = cards.get(i);
					gbc.gridx = 0;
					for (int j = 0, n = entry.getKey(); j < n; j++) {
						if (card.color() == MyColor.RAINBOW) {
							label = new JGradientLabel(card);
						} else {
							label = new JColorCardLabel(card);
						}
						label.setPreferredSize(prefederredDimension);
						transportPanel.add(label, gbc);
						gbc.gridx++;
					}
					gbc.gridy++;
				}
			}
			transportPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), entryTransportMode.getKey().displayName));
			this.add(transportPanel, gbcTransport);
		}
		gbcTransport.weightx = 1;
		gbcTransport.weighty = 1;
		gbcTransport.gridx++;
		gbcTransport.fill = GridBagConstraints.BOTH;
		this.add(new JPanel(), gbcTransport);
	}

	private static class JColorCardLabel extends JLabel {
		private static final long serialVersionUID = -5607808287807778978L;

		public final ColorCard colorCard;

		public JColorCardLabel(ColorCard colorCard) {
			super(colorCard.getColorCardString());
			this.colorCard = colorCard;
			this.setForeground(MyColor.getComplementaryColor(colorCard.color()));
			this.setPreferredSize(new Dimension(60, 20));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
			this.setBackground(colorCard.color().realColor);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setDoubleBuffered(true);
			this.setFocusable(false);
			this.setOpaque(true);
		}
	}

	public static class JGradientLabel extends JColorCardLabel {
		private static final long serialVersionUID = 5469665614084730926L;

		public JGradientLabel(ColorCard colorCard) {
			super(colorCard);
			this.setForeground(Color.BLACK);
			this.setBackground(null);
			this.setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();

			MyColor[] colors = MyColor.getNormalMyColors();
			int stripHeigth = this.getHeight() / (colors.length - 1);

			for (int i = 0; i < (colors.length - 1); i++) {
				g2.setPaint(new GradientPaint(new Point(0, i * stripHeigth), colors[i].realColor, new Point(0, (i + 1) * stripHeigth), colors[i + 1].realColor));
				g2.fillRect(0, i * stripHeigth, this.getWidth(), (i + 1) * stripHeigth);
			}
			g2.dispose();
			super.paintComponent(g);
		}
	}

}
