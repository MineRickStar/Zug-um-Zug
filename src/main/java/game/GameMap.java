package game;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import application.Application;
import connection.Connection;
import connection.SingleConnection;
import csvCoder.Decode;
import game.board.Location;
import game.cards.ColorCard.MyColor;
import game.cards.ColorCard.TransportMode;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;
import game.cards.MissionCard.MissionCardConstraints;

public class GameMap {

	public static final String LOCATIONSFILE = "Locations.txt";
	public static final String CONNECTIONFILE = "Connections.txt";
	public static final String MISSIONCARDSFILE = "Missioncards.txt";
	public static final String RULESFILE = "Rules.txt";
	public static final String MAPFILE = "Map.png";

	public static final File SavedGamesFolder = new File(System.getProperty("user.home"), "Saved Games");

	public static final File myGameFolder = new File(GameMap.SavedGamesFolder, Application.NAME);

	public static List<GameMap> getMaps() {
		List<File> files = new ArrayList<>();
		try {
			files.add(Paths.get(ClassLoader.getSystemResource("Germany original").toURI()).toFile());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		files.addAll(List.of(GameMap.myGameFolder.listFiles(File::isDirectory)));
		return files.stream().map(GameMap::new).toList();
	}

	public final String mapName;

	private final File folder;

	private TreeMap<String, Location> locations;
	private TreeMap<UUID, Connection> connections;
	private Map<Distance, List<MissionCard>> missionCards;

	private BufferedImage mapImage;
	private Dimension mapDimensions;

	protected RuleSet ruleSet;

	public GameMap(File folder) {
		this.folder = folder;
		this.mapName = folder.getName();
		try {
			this.mapImage = ImageIO.read(new File(this.folder, GameMap.MAPFILE));
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
		this.loadMap();
	}

	public final BufferedImage getMapImage() {
		return this.mapImage;
	}

	public final Dimension getDimensions() {
		return this.mapDimensions;
	}

	private final void loadMap() {
		try {
			this.ruleSet = RuleSet.load(this.folder);
			if (this.ruleSet == null) {
				this.ruleSet = RuleSet.emptyRuleSet();
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Application.frame, "Rules of Map could not be loaded");
		}
		this.loadLocations();
		this.loadConnections();
		this.loadMissionCards();
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

	public int getCarrigeCount(TransportMode transportMode) {
		return this.connections.values().stream().mapToInt(c -> Stream.of(c.singleConnections).filter(s -> s.transportMode == transportMode).mapToInt(s -> s.length).sum()).sum();
	}

	public TransportMode[] getTransportModes() {
		List<TransportMode> transportModes = new ArrayList<>();
		for (Connection con : this.connections.values()) {
			for (SingleConnection single : con.singleConnections) {
				if (!transportModes.contains(single.transportMode)) {
					transportModes.add(single.transportMode);
				}
			}
		}
		return transportModes.toArray(TransportMode[]::new);
	}

	public int[] getLengths(TransportMode transportMode) {
		List<Integer> distances = new ArrayList<>();
		for (Connection con : this.connections.values()) {
			for (SingleConnection single : con.singleConnections) {
				if (((transportMode == null) || (single.transportMode == transportMode)) && !distances.contains((int) single.length)) {
					distances.add((int) single.length);
				}
			}
		}
		return distances.stream().mapToInt(i -> i).sorted().toArray();
	}

	private void loadLocations() {
		try {
			Decode decode = Decode.decode(new File(this.folder, GameMap.LOCATIONSFILE));
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
			Decode decode = Decode.decode(new File(this.folder, GameMap.CONNECTIONFILE));
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
					transportMode[i] = TransportMode.getTransportMode(line[i + 3 + (2 * multiplicity)]);
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
			Decode decode = Decode.decode(new File(this.folder, GameMap.MISSIONCARDSFILE));
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

	public final RuleSet getRuleSet() {
		return this.ruleSet;
	}

	public void store() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(this.folder, GameMap.RULESFILE)));
			oos.writeObject(this.ruleSet);
			oos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// TODO save other files
	}

	@Override
	public String toString() {
		return this.mapName;
	}

}
