package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import application.Application;
import application.Property;
import game.Game;
import game.Player;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.MissionCard;

public class PlayerPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = -5734164742630869042L;

	private JPanel missionCardsSettingsPanel;
	private JLabel finishedMissionCardLabel;
	private JButton showFinishedMissionCardsButton;
	private JButton editMissionCards;

	private MyJScrollPane allJMissionCardsScrollPane;
	private AllJMissionCardsPanel missionCardsPanel;

	private JColorCardPanel colorCardPanel;

	public PlayerPanel() {
		super(new GridBagLayout());

		this.missionCardsSettingsPanel = this.setUpMissionCardPanel();
		this.missionCardsPanel = new AllJMissionCardsPanel("Active Mission Cards");
		this.allJMissionCardsScrollPane = new MyJScrollPane(this.missionCardsPanel);
		this.allJMissionCardsScrollPane.setMaximumSize(new Dimension(0, (int) (Application.frame.getHeight() * .25)));

		this.allJMissionCardsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.colorCardPanel = new JColorCardPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(this.missionCardsSettingsPanel, gbc);

		gbc.weightx = 1;
		gbc.gridy = 1;
		this.add(this.allJMissionCardsScrollPane, gbc);

		gbc.weighty = 1;
		gbc.gridy = 2;
		this.add(this.colorCardPanel, gbc);
		Game.getInstance().addPropertyChangeListener(Property.COLORCARDCHANGE, this);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDSADDED, this);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDHIDDEN, this);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDFINISHED, this);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDINDEXCHANGE, this);
	}

	private JPanel setUpMissionCardPanel() {
		JPanel missionCards = new JPanel(new GridBagLayout());
		missionCards.setBackground(Color.LIGHT_GRAY);
		this.finishedMissionCardLabel = new JLabel("Finished Missioncards: 0, Points: 0");
		this.showFinishedMissionCardsButton = new JButton("Show Finished Mission Cards");
		this.showFinishedMissionCardsButton.setEnabled(false);
		this.showFinishedMissionCardsButton.addActionListener(e -> Application.createNewFinishedMissionCardDialog());
		this.editMissionCards = new JButton("Edit Missioncards");
		Application.addCTRLShortcut(this.editMissionCards, KeyEvent.VK_E, e -> Application.createNewMssionCardEditor());
		this.editMissionCards.addActionListener(e -> Application.createNewMssionCardEditor());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		missionCards.add(this.finishedMissionCardLabel, gbc);
		gbc.gridx = 1;
		missionCards.add(this.showFinishedMissionCardsButton, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.weightx = 1;
		missionCards.add(new JLabel(), gbc);
		gbc.weightx = 0;
		gbc.gridx = 3;
		missionCards.add(this.editMissionCards, gbc);
		return missionCards;
	}

	private void updateFinishedMissionCardPanel(MissionCard finishedMissionCard) {
		List<MissionCard> finishedMissionCards = Game.getInstance().getInstancePlayer().getFinishedMissionCards();
		this.finishedMissionCardLabel.setText("Finished Missioncards: " + finishedMissionCards.size() + ", Points: " + finishedMissionCards.stream().mapToInt(m -> m.points).sum());
		this.showFinishedMissionCardsButton.setEnabled(true);
		this.missionCardsPanel.getMissionCardPanelList().stream().filter(j -> j.missionCard.equals(finishedMissionCard)).map(j -> (InfoPanelMissionCardPanel) j).forEach(i -> i.finished = true);
		this.missionCardsPanel.update();
		this.allJMissionCardsScrollPane.revalidate();
		this.allJMissionCardsScrollPane.repaint();
	}

	private class MyJScrollPane extends JScrollPane {
		private static final long serialVersionUID = 6489961897887902367L;

		private MyJScrollPane(Component view) {
			super(view, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension supPref = super.getPreferredSize();
			Dimension max = super.getMaximumSize();
			return new Dimension(supPref.width, Math.min(supPref.height, max.height));
		}

	}

	private class InfoPanelMissionCardPanel extends JMissionCardPanel {

		private static final long serialVersionUID = -3372640083631650600L;

		private boolean visible;
		private boolean finished;

		protected InfoPanelMissionCardPanel(MissionCard missionCard) {
			super(missionCard);
			this.finished = false;
			this.visible = true;
		}

		@Override
		public boolean isPanelDisplayable() {
			return !this.finished && this.visible;
		}

	}

	public static class JColorCardButton extends JButton {
		private static final long serialVersionUID = -4035891459387013101L;

		public final ColorCard colorCard;
		public final int index;

		public JColorCardButton(ColorCard colorCard, int index) {
			super(colorCard.getColorCardString());
			this.colorCard = colorCard;
			this.index = index;
			this.setForeground(MyColor.getComplementaryColor(colorCard.color()));
			this.setPreferredSize(new Dimension(60, 20));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
			this.setBackground(colorCard.color().realColor);
			this.setSelected(false);
			this.setDoubleBuffered(true);
			this.setFocusable(false);
		}
	}

	public static class JGradientButton extends JColorCardButton {
		private static final long serialVersionUID = 5469665614084730926L;

		public JGradientButton(ColorCard colorCard, int index) {
			super(colorCard, index);
			this.setForeground(Color.BLACK);
			this.setContentAreaFilled(false);
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Property.COLORCARDCHANGE.name().equals(evt.getPropertyName())) {
			if (Game.getInstance().getInstancePlayer().equals(evt.getSource())) {
				ColorCard[] card = evt.getNewValue() == null ? (ColorCard[]) evt.getOldValue() : (ColorCard[]) evt.getNewValue();
				SwingUtilities.invokeLater(() -> this.colorCardPanel.editCardAndUpdatePanel(card, evt.getNewValue() == null));
			}
		} else if (Property.MISSIONCARDSADDED.name().equals(evt.getPropertyName())) {
			try {
				SwingUtilities.invokeAndWait(() -> {
					Stream.of((MissionCard[]) evt.getNewValue()).map(InfoPanelMissionCardPanel::new).forEach(i -> this.missionCardsPanel.addMissionCard(i));
					this.colorCardPanel.updateColorCardPanel();
					this.revalidate();
					this.repaint();
					System.out.println(this.allJMissionCardsScrollPane.getMinimumSize());
					System.out.println(this.allJMissionCardsScrollPane.getMaximumSize());
					System.out.println(this.allJMissionCardsScrollPane.getPreferredSize());
					System.out.println(this.allJMissionCardsScrollPane.getSize());
				});
				this.allJMissionCardsScrollPane.revalidate();
				this.allJMissionCardsScrollPane.repaint();
				this.revalidate();
				this.repaint();
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
			SwingUtilities.invokeLater(() -> this.colorCardPanel.updateColorCardPanel());
		} else if (Property.MISSIONCARDHIDDEN.name().equals(evt.getPropertyName())) {
			this.missionCardsPanel.getMissionCardPanelList()
					.stream()
					.filter(j -> j.missionCard.equals(evt.getSource()))
					.map(j -> (InfoPanelMissionCardPanel) j)
					.forEach(i -> i.visible = !(boolean) evt.getNewValue());
			this.missionCardsPanel.update();
			this.colorCardPanel.updateColorCardPanel();
			this.revalidate();
			this.repaint();
		} else if (Property.MISSIONCARDFINISHED.name().equals(evt.getPropertyName())) {
			if (evt.getSource() instanceof Player player) {
				this.updateFinishedMissionCardPanel((MissionCard) evt.getNewValue());
				this.missionCardsPanel.getMissionCardPanelList().removeIf(j -> j.missionCard.equals(evt.getNewValue()));
				this.missionCardsPanel.update();
			}
		} else if (Property.MISSIONCARDINDEXCHANGE.name().equals(evt.getPropertyName())) {
			int[] oldLocations = (int[]) evt.getOldValue();
			int[] newLocations = (int[]) evt.getNewValue();
			this.missionCardsPanel.sortMissionCardPanels(newLocations, oldLocations);
		}
		this.revalidate();
		this.repaint();
	}

}
