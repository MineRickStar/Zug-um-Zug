package game;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.imageio.ImageIO;

import application.Application;
import connection.Connection;
import csvCoder.Decode;
import game.board.Location;
import game.cards.ColorCard.MyColor;
import game.cards.ColorCard.TransportMode;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;
import game.cards.MissionCard.MissionCardConstraints;

public class GameMap {

	private final String folder;

	private TreeMap<String, Location> locations;
	private TreeMap<UUID, Connection> connections;
	private Map<Distance, List<MissionCard>> missionCards;

	private BufferedImage mapImage;
	private Dimension mapDimensions;

	protected RuleSet ruleSet;

	public GameMap(String folder) {
		this.folder = folder;
		try {
			this.mapImage = ImageIO.read(ClassLoader.getSystemResource(this.folder + "\\map.png"));
			this.mapDimensions = new Dimension(this.mapImage.getWidth(), this.mapImage.getHeight());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.locations = new TreeMap<>();
		this.connections = new TreeMap<>();
		this.missionCards = new EnumMap<>(Distance.class);
		for (Distance distance : Distance.values()) {
			this.missionCards.put(distance, new ArrayList<>());
		}
	}

	public final BufferedImage getMapImage() {
		return this.mapImage;
	}

	public final Dimension getDimensions() {
		return this.mapDimensions;
	}

	public final void loadMap() {
		this.loadRuleSet();
		this.loadLocations();
		this.loadConnections();
		this.loadMissionCards();
		Application.frame.repaint();
	}

	public void startGame() {
		Collections.shuffle(this.missionCards.get(Distance.SHORT), Game.getInstance().getRandomGenerator());
		Collections.shuffle(this.missionCards.get(Distance.LONG), Game.getInstance().getRandomGenerator());
	}

	public final Location getLocation(String locationName) {
		return this.locations.getOrDefault(locationName, null);
	}

	public final List<Location> getLocations() {
		return new ArrayList<>(this.locations.values());
	}

	public final List<Connection> getConnections() {
		return new ArrayList<>(this.connections.values());
	}

	public final Connection getConnectionFromLocations(Location fromLocation, Location toLocation) {
		return this.connections.values().parallelStream().filter(c -> {
			if (c.fromLocation.equals(fromLocation) || c.fromLocation.equals(toLocation)) { return c.toLocation.equals(fromLocation) || c.toLocation.equals(toLocation); }
			return false;
		}).findAny().orElse(null);
	}

	public MissionCard drawMissionCard(Distance distance) {
		return this.missionCards.get(distance).remove(0);
	}

	public int getMissionCardCount(Distance distance) {
		return this.missionCards.get(distance).size();
	}

	private void loadLocations() {
		try {
			Decode decode = Decode.decode(this.folder + "\\Locations.txt");
			while (decode.hasNext()) {
				String[] line = decode.next();
				String name = line[0];
				int x = Integer.parseInt(line[1]);
				int y = Integer.parseInt(line[2]);
				Point p = new Point(x, y);
				this.locations.put(name, new Location(name, p));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadConnections() {
		String[] line = null;
		try {
			Decode decode = Decode.decode(this.folder + "\\Connections.txt");
			while (decode.hasNext()) {
				line = decode.next();

				Location fromLocation = this.getLocation(line[0]);
				if (fromLocation == null) {
					System.err.println("From Location not found " + line[0]);
					continue;
				}
				Location toLocation = this.getLocation(line[1]);
				if (toLocation == null) {
					System.err.println("To Location not found " + line[1]);
					continue;
				}
				byte multiplicity = Byte.parseByte(line[2]);
				byte[] length = new byte[multiplicity];
				MyColor[] colors = new MyColor[multiplicity];
				TransportMode[] transportMode = new TransportMode[multiplicity];
				for (byte i = 0; i < multiplicity; i++) {
					length[i] = Byte.parseByte(line[i + 3]);
					colors[i] = MyColor.getMyColor(line[i + 3 + multiplicity]);
					transportMode[i] = TransportMode.getTransportMode(line[i + 3 + 2 * multiplicity]);
				}
				List<Integer> removedModes = new ArrayList<>();
				List<TransportMode> allowedTransportModes = List.of(this.ruleSet.getTransportModes());
				for (int i = 0; i < transportMode.length; i++) {
					TransportMode mode = transportMode[i];
					if (!allowedTransportModes.contains(mode)) {
						removedModes.add(i);
					}
				}
				if (removedModes.size() == multiplicity) {
					System.out.println("Connection not in RuleSet [" + fromLocation.name + " - " + toLocation.name + "]");
					continue;
				}
				if (!removedModes.isEmpty()) {
					for (Integer i : removedModes) {
						length[i] = 0;
						colors[i] = null;
						transportMode[i] = null;
					}
					multiplicity -= removedModes.size();
					byte[] newL = new byte[multiplicity];
					for (int i = 0, newI = 0; i < length.length; i++) {
						if (length[i] != 0) {
							newL[newI++] = length[i];
						}
					}
					length = newL;
					List<MyColor> colorList = new ArrayList<>(List.of(colors));
					colorList.removeIf(m -> m == null);
					colors = colorList.toArray(MyColor[]::new);
					List<TransportMode> transportList = List.of(transportMode);
					transportList.removeIf(t -> t == null);
					transportMode = transportList.toArray(TransportMode[]::new);
				}
				Connection connection = new Connection(fromLocation, toLocation, multiplicity, length, colors, transportMode);
				fromLocation.addConnection(connection);
				toLocation.addConnection(connection);
				if (!this.connections.containsKey(connection.ID)) {
					this.connections.put(connection.ID, connection);
				} else {
					System.err.println("Connection already saved: " + connection);
				}
			}
		} catch (IOException | NumberFormatException e) {
			System.err.println(Arrays.toString(line));
			e.printStackTrace();
		}
	}

	private void loadMissionCards() {
		try {
			Decode decode = Decode.decode(this.folder + "\\Missioncards.txt");
			while (decode.hasNext()) {
				String[] line = decode.next();
				Distance distance = Distance.findByAbbreviation(line[0]);
				byte points = 0;
				try {
					points = Byte.parseByte(line[1]);
				} catch (NumberFormatException nfe) {
					System.err.println(Arrays.toString(line));
					continue;
				}
				List<Location> locations = new ArrayList<>();
				for (int i = 2; i < line.length; i++) {
					locations.add(this.getLocation(line[i]));
				}
				MissionCardConstraints constraints = new MissionCardConstraints();
				MissionCard missionCard = new MissionCard(distance, points, locations, constraints);

				List<MissionCard> cards = this.missionCards.get(missionCard.distance);
				if (!cards.contains(missionCard)) {
					cards.add(missionCard);
				} else {
					System.err.println("MissionCard already saved: " + missionCard);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadRuleSet() {
		this.ruleSet = new RuleSet();
		this.ruleSet.transportModes = new TransportMode[] { TransportMode.TRAIN, TransportMode.SHIP };
	}

	public final RuleSet getRuleSet() {
		return this.ruleSet;
	}

	public class RuleSet {
		private TransportMode[] transportModes;

		private RuleSet() {

		}

		public TransportMode[] getTransportModes() {
			return this.transportModes;
		}
	}

}
