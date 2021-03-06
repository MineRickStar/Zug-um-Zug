package gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import application.Application;
import connection.Connection;
import connection.SingleConnection;
import game.Player;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import language.MyResourceBundle.LanguageKey;

public class BuyingDialog extends JDialog {

	private static final long serialVersionUID = -2452919286579576889L;

	private Player player;

	private List<JPanel> allBuyingOptionPanels;

	private JPanel selectedPanel;

	private ColorPanel selectedBuyingOption;

	public BuyingDialog(JPanel parentPanel, SingleConnection singleConnection, Player player) {
		super(Application.frame, Application.resources.getString(LanguageKey.BUYCONNECTION), true);
		this.player = player;
		this.setLayout(new GridBagLayout());
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				BuyingDialog.this.selectedBuyingOption = null;
			}
		});
		this.allBuyingOptionPanels = new ArrayList<>();

		Connection connection = singleConnection.parentConnection;
		JLabel description = new JLabel(String.format(Application.resources.getString(LanguageKey.CONNECTIONDESCRIPTION), connection.fromLocation.name, connection.toLocation.name,
				singleConnection.length, singleConnection.getColorName(), singleConnection.getTransportModeName()));

		JScrollPane buyingOptionsScrollPane = this.getColorCardsScrollPane(player.getBuyingOptions(singleConnection.getColorCardRepresentation(), singleConnection.length),
				Application.frame.getWidth(), Application.frame.getHeight());

		JPanel buttonPanel = this.createButtonPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;

		this.add(description, gbc);

		gbc.weighty = .1;
		gbc.gridy = 1;

		this.add(buyingOptionsScrollPane, gbc);

		gbc.weighty = 0;
		gbc.gridy = 2;

		this.add(buttonPanel, gbc);

		this.setMaximumSize(new Dimension(500, 300));
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(parentPanel);
		this.setVisible(true);
	}

	private JScrollPane getColorCardsScrollPane(List<ColorCard[]> buyingOptions, int width, int height) {
		JPanel buyingOptionPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 10);

		Collections.sort(buyingOptions, (o1, o2) -> {
			long l = Long.compare(Stream.of(o1).filter(c -> c.color() == MyColor.RAINBOW).count(), Stream.of(o2).filter(c -> c.color() == MyColor.RAINBOW).count());
			if (l == 0) { return -Integer.compare(this.player.getColorCardCount(o1[o1.length - 1]), this.player.getColorCardCount(o2[o2.length - 1])); }
			return (int) l;
		});
		for (int i = 0, max = buyingOptions.size(); i < max; i++) {
			ColorCard[] option = buyingOptions.get(i);
			ColorPanel cardPanel = this.createColorPanel(option);
			if (i == 0) {
				this.selectedPanel = cardPanel;
				this.selectedPanel.setBackground(Color.DARK_GRAY);
				BuyingDialog.this.selectedBuyingOption = cardPanel;
			}
			buyingOptionPanel.add(cardPanel, gbc);
			this.allBuyingOptionPanels.add(cardPanel);
			gbc.gridy++;
		}

		MyJScrollPane buyingOptionsScrollPane = new MyJScrollPane(buyingOptionPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		buyingOptionsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return buyingOptionsScrollPane;
	}

	private ColorPanel createColorPanel(ColorCard[] colorCards) {
		ColorPanel cardPanel = new ColorPanel(colorCards);
		cardPanel.addMouseListener(new MouseHelper());
		cardPanel.setBackground(Color.LIGHT_GRAY);

		for (ColorCard colorCard : colorCards) {
			JLabel label;
			if (colorCard.color() == MyColor.RAINBOW) {
				label = new JGradientLabel(colorCard.color().getColorNamePlural());
				label.setForeground(Color.BLACK);
				label.setOpaque(false);
			} else {
				label = new JLabel(colorCard.color().getColorNamePlural());
				label.setForeground(MyColor.getComplementaryColor(colorCard.color()));
				label.setBackground(colorCard.color().realColor);
				label.setOpaque(true);
			}
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setPreferredSize(new Dimension(70, 100));
			cardPanel.add(label);
		}
		return cardPanel;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		JButton okButton = new JButton(Application.resources.getString(LanguageKey.OK));
		okButton.addActionListener(e -> this.testBuyOption());
		Application.addCTRLEnterShortcut(okButton, e -> BuyingDialog.this.testBuyOption());

		JButton cancelButton = new JButton(Application.resources.getString(LanguageKey.CANCEL));
		cancelButton.addActionListener(e -> {
			this.selectedBuyingOption = null;
			this.dispose();
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}

	private void testBuyOption() {
		if (this.selectedBuyingOption == null) {
			JOptionPane.showMessageDialog(this, Application.resources.getString(LanguageKey.SELECTORCANCEL));
			return;
		}
		this.dispose();
	}

	public ColorCard[] getSelectedBuyingOption() {
		return this.selectedBuyingOption == null ? null : this.selectedBuyingOption.colorOption;
	}

	private static final class ColorPanel extends JPanel {

		private static final long serialVersionUID = -2522687597311503332L;
		final ColorCard[] colorOption;

		ColorPanel(ColorCard[] colorOption) {
			this.colorOption = colorOption;
		}
	}

	private final class MyJScrollPane extends JScrollPane {
		private static final long serialVersionUID = 1774208335274203599L;

		public MyJScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
			super(view, vsbPolicy, hsbPolicy);
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d.height = Math.min(Application.frame.getHeight() / 2, d.height);
			return d;
		}
	}

	private final class JGradientLabel extends JLabel {
		private static final long serialVersionUID = 5469665614084730926L;

		public JGradientLabel(String text) {
			super(text);
			this.setDoubleBuffered(true);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();

			MyColor[] colors = { MyColor.BLACK, MyColor.PURPLE, MyColor.BLUE, MyColor.GREEN, MyColor.YELLOW, MyColor.ORANGE, MyColor.RED, MyColor.WHITE };
			final int stripHeigth = this.getHeight() / (colors.length - 1);

			float[] fractions = new float[colors.length];
			Float[] floats = IntStream.range(0, colors.length).mapToObj(value -> (float) (value * stripHeigth) / this.getHeight()).toArray(Float[]::new);
			for (int i = 0; i < floats.length; i++) {
				fractions[i] = floats[i];
			}

			LinearGradientPaint lgp = new LinearGradientPaint(new Point(0, 0), new Point(0, this.getHeight()), fractions, Stream.of(colors).map(MyColor::getRealColor).toArray(Color[]::new));
			g2.setPaint(lgp);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			g2.dispose();

			super.paintComponent(g);
		}
	}

	private final class MouseHelper extends MouseAdapter {
		@Override
		public void mouseExited(MouseEvent e) {
			ColorPanel source = (ColorPanel) e.getComponent();
			if (!source.equals(BuyingDialog.this.selectedBuyingOption)) {
				source.setBackground(Color.LIGHT_GRAY);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			ColorPanel source = (ColorPanel) e.getComponent();
			if (!source.equals(BuyingDialog.this.selectedBuyingOption)) {
				source.setBackground(Color.LIGHT_GRAY.darker());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			ColorPanel source = (ColorPanel) e.getComponent();
			source.setBackground(Color.DARK_GRAY);
			if (BuyingDialog.this.selectedBuyingOption != null) {
				BuyingDialog.this.selectedBuyingOption.setBackground(Color.LIGHT_GRAY);
			}
			BuyingDialog.this.selectedBuyingOption = source;
		}
	}

}
