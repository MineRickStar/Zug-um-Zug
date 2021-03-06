package game;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

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

	public static final String MAPFILE = "Map.png";
	public static final String RULESFILE = "Rules.txt";
	public static final String LOCATIONSFILE = "Locations.txt";
	public static final String CONNECTIONFILE = "Connections.txt";
	public static final String MISSIONCARDSFILE = "Missioncards.txt";

	public static final File SavedGamesFolder = new File(System.getProperty("user.home"), "Saved Games");

	public static final File myGameFolder = new File(GameMap.SavedGamesFolder, Application.NAME);

	public static List<GameMap> getMaps() {
		List<GameMap> maps = new ArrayList<>();
		maps.add(new GameMap(null));
		maps.addAll(new ArrayList<>(List.of(GameMap.myGameFolder.listFiles(File::isDirectory)).stream().map(GameMap::new).toList()));
		return maps;
	}

	public final String mapName;

	private final File folder;

	private TreeMap<String, Location> locations;
	private TreeMap<UUID, Connection> connections;
	private Map<Distance, List<MissionCard>> missionCards;

	private BufferedImage mapImage;
	private Dimension mapDimensions;

	protected RuleSet ruleSet;

	private GameMap(File folder) {
		this.folder = folder;
		this.mapName = folder == null ? "Germany original" : folder.getName();
		this.locations = new TreeMap<>();
		this.connections = new TreeMap<>();
		this.missionCards = new EnumMap<>(Distance.class);
		for (Distance distance : Distance.values()) {
			this.missionCards.put(distance, new ArrayList<>());
		}
		this.load(folder != null);
		this.mapDimensions = new Dimension(this.mapImage.getWidth(), this.mapImage.getHeight());
	}

	private void load(boolean folderExists) {
		try {
			this.loadStuff(t -> {
				try {
					this.mapImage = ImageIO.read(t);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}, GameMap.MAPFILE, folderExists);
			this.loadStuff(this::loadRuleSet, GameMap.RULESFILE, folderExists);
			this.loadStuff(this::loadLocations, GameMap.LOCATIONSFILE, folderExists);
			this.loadStuff(this::loadConnections, GameMap.CONNECTIONFILE, folderExists);
			this.loadStuff(this::loadMissionCards, GameMap.MISSIONCARDSFILE, folderExists);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void loadStuff(Consumer<InputStream> consumer, String file, boolean folderExists) throws FileNotFoundException {
		InputStream stream;
		if (folderExists) {
			stream = new FileInputStream(new File(this.folder, file));
		} else {
			stream = ClassLoader.getSystemResourceAsStream(this.getResourceFile(file));
		}
		consumer.accept(stream);
	}

	private String getResourceFile(String location) {
		return "Germany original/" + location;
	}

	public final BufferedImage getMapImage() {
		return this.mapImage;
	}

	public final Dimension getDimensions() {
		return this.mapDimensions;
	}

	public void startGame() {
		for (Distance distance : Distance.values()) {
			Collections.shuffle(this.missionCards.get(distance), Game.getInstance().getRandomGenerator());
		}
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

	private void loadLocations(InputStream input) {
		String[] line = null;
		try {
			Decode decode = Decode.decode(input);
			while (decode.hasNext()) {
				line = decode.next();
				String name = line[0];
				int x = Integer.parseInt(line[1]);
				int y = Integer.parseInt(line[2]);
				Point p = new Point(x, y);
				String abbreviation = line[3];
				this.locations.put(name, new Location(name, p, abbreviation));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage() + " in Line: " + Stream.of(line).collect(Collectors.joining(", ")));
		}
	}

	private void loadConnections(InputStream input) {
		String[] line = null;
		try {
			Decode decode = Decode.decode(input);
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

	private void loadMissionCards(InputStream input) {
		try {
			Decode decode = Decode.decode(input);
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

	private void loadRuleSet(InputStream input) {
		try {
			ObjectInputStream ois = new ObjectInputStream(input);
			this.ruleSet = (RuleSet) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
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
