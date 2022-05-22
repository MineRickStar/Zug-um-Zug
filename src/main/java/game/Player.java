package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application.Property;
import connection.SingleConnection;
import game.board.LocationOrganizer;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.ColorCard.TransportMode;
import game.cards.MissionCard;

public class Player {

	private final UUID ID;

	protected String name;
	public final MyColor playerColor;

	protected EnumMap<TransportMode, Integer> pieceCount;

	protected Map<TransportMode, SortedMap<Integer, List<ColorCard>>> playerCards;
	protected List<MissionCard> missionCards;
	protected List<MissionCard> finishedMissionCards;

	private List<SingleConnection> singleConnections;
	private LocationOrganizer locationOrganizer;

	public Player(String name, MyColor color) {
		this.ID = UUID.randomUUID();
		this.name = name;
		this.playerColor = color;
		this.pieceCount = Rules.getInstance().getTransportMap();
		this.playerCards = new EnumMap<>(TransportMode.class);
		this.missionCards = new ArrayList<>();
		this.finishedMissionCards = new ArrayList<>();
		this.singleConnections = new ArrayList<>();
		this.locationOrganizer = new LocationOrganizer();
//		if (name.equals("Patrick")) {
//			SingleConnection c = Game.getInstance().getConnectionFromLocations("Kassel", "Frankfurt", MyColor.WHITE);
//			c.setOwner(this);
//			this.buySingleConnection(c, new ArrayList<>());
//		}
	}

	///// Color Cards on Hand /////

	public void addColorCard(ColorCard card) {
		this.editColorCards(new ColorCard[] { card }, true);
	}

	public void addColorCards(ColorCard[] colorCards) {
		this.editColorCards(colorCards, true);
	}

	public void removeColorCards(ColorCard[] colorCards) {
		this.editColorCards(colorCards, false);
	}

	public Map<TransportMode, SortedMap<Integer, List<ColorCard>>> getColorCards() {
		return this.playerCards;
	}

	public List<MissionCard> getFinishedMissionCards() {
		return this.finishedMissionCards;
	}

	private void editColorCards(ColorCard[] colorCards, boolean add) {
		for (ColorCard colorCard : colorCards) {
			SortedMap<Integer, List<ColorCard>> map = this.playerCards.getOrDefault(colorCard.transportMode(), new TreeMap<Integer, List<ColorCard>>(Collections.reverseOrder()));
			int colorCardCount = this.getColorCardCount(colorCard);
			List<ColorCard> list = map.getOrDefault(colorCardCount, new ArrayList<>());
			list.remove(colorCard);
			if (list.isEmpty()) {
				map.remove(colorCardCount);
			}
			list = map.getOrDefault(colorCardCount + (add ? 1 : (-1)), new ArrayList<>());
			list.add(colorCard);
			map.put(colorCardCount + (add ? 1 : (-1)), list);
			this.playerCards.put(colorCard.transportMode(), map);
		}
		if (add) {
			Game.getInstance().fireAction(this, Property.COLORCARDCHANGE, null, colorCards);
		} else {
			Game.getInstance().fireAction(this, Property.COLORCARDCHANGE, colorCards, null);
		}
	}

	public int getColorCardCount(ColorCard colorCard) {
		if (colorCard.color() == MyColor.GRAY) { return -1; }
		return this.playerCards.getOrDefault(colorCard.transportMode(), new TreeMap<>())
				.entrySet()
				.stream()
				.filter(e -> e.getValue().contains(colorCard))
				.map(Entry::getKey)
				.findAny()
				.orElseGet(() -> Integer.valueOf(0));
	}

	///// Connections /////

	public boolean hasPlayerEnoughColorCards(ColorCard colorCard, int count) {
		SortedMap<Integer, List<ColorCard>> map = this.playerCards.getOrDefault(colorCard.transportMode(), new TreeMap<Integer, List<ColorCard>>());
		int rainbowCount = this.getColorCardCount(new ColorCard(MyColor.RAINBOW, colorCard.transportMode()));
		if (rainbowCount >= count) { return true; }
		if (colorCard.color() == MyColor.GRAY) { return (map.firstKey() + rainbowCount) >= count; }
		return (this.getColorCardCount(colorCard) + rainbowCount) >= count;
	}

	public List<ColorCard[]> getBuyingOptions(ColorCard colorCard, int count) {
		List<ColorCard[]> colorList = new ArrayList<>();

		if (colorCard.color() == MyColor.GRAY) {
			for (MyColor color : MyColor.getNormalMyColors()) {
				this.getBuyingOptions(new ColorCard(color, colorCard.transportMode()), count).forEach(c -> this.addIfNotContained(colorList, c));
			}
		} else {
			int colorCount = this.getColorCardCount(colorCard);
			int rainbowCount = this.getColorCardCount(new ColorCard(MyColor.RAINBOW, colorCard.transportMode()));

			int maxCount;
			if (count > rainbowCount) {
				maxCount = rainbowCount;
			} else if (count < rainbowCount) {
				maxCount = count;
			} else {
				maxCount = rainbowCount - 1;
			}

			for (int i = Math.max(count - colorCount, 0); i <= maxCount; i++) {
				ColorCard[] array = new ColorCard[count];
				for (int j = 0; j < count; j++) {
					array[j] = j < i ? new ColorCard(MyColor.RAINBOW, colorCard.transportMode()) : colorCard;
				}
				this.addIfNotContained(colorList, array);
			}
			if (count <= rainbowCount) {
				this.addIfNotContained(colorList, Stream.generate(() -> new ColorCard(MyColor.RAINBOW, colorCard.transportMode())).limit(count).toArray(ColorCard[]::new));
			}
		}
		return colorList;
	}

	private void addIfNotContained(List<ColorCard[]> colorList, ColorCard[] colors) {
		for (ColorCard[] colorArray : colorList) {
			if (Objects.deepEquals(colorArray, colors)) { return; }
		}
		colorList.add(colors);
	}

	public boolean canPlayerBuySingleConnection(SingleConnection singleConnection) {
		boolean enoughCarriges = this.getPieceCount(singleConnection.transportMode) >= singleConnection.length;
		boolean enoughColorCards = this.hasPlayerEnoughColorCards(singleConnection.getColorCardRepresentation(), singleConnection.length);
		return enoughCarriges && enoughColorCards;
	}

	public void buySingleConnection(SingleConnection singleConnection, List<ColorCard> buyingCards) {
		this.singleConnections.add(singleConnection);
		this.locationOrganizer.addSingleConnection(singleConnection);
		this.removeColorCards(buyingCards.toArray(ColorCard[]::new));
		Game.getInstance().addUsedCards(buyingCards);
		this.testForFinishedMissionCards();
	}

	public List<SingleConnection> getSingleConnections() {
		return this.singleConnections;
	}

	///// Missioncards /////

	public void addMissionCards(MissionCard[] missionCards) {
		this.missionCards.addAll(List.of(missionCards));
		this.testForFinishedMissionCards();
		List<MissionCard> cards = new ArrayList<>(List.of(missionCards));
		cards.removeAll(this.finishedMissionCards);
		// New Thread because InvokeAndWait cannot be called from EDT
		new Thread(() -> Game.getInstance().fireAction(this, Property.MISSIONCARDSADDED, null, cards.toArray(MissionCard[]::new))).start();
	}

	public List<MissionCard> getMissionCards() {
		return this.missionCards;
	}

	private void testForFinishedMissionCards() {
		this.missionCards.stream().filter(m -> m.isFinished(this.locationOrganizer)).forEach(m -> {
			this.finishedMissionCards.add(m);
			Game.getInstance().fireAction(this, Property.MISSIONCARDFINISHED, null, m);
		});
		this.missionCards.removeAll(this.finishedMissionCards);
	}

	///// Pieces (Trains, Ships, Airplanes) /////

	public EnumMap<TransportMode, Integer> getPieceCount() {
		return this.pieceCount;
	}

	public int getPieceCount(TransportMode transportMode) {
		Integer i = this.pieceCount.get(transportMode);
		return i == null ? 0 : i;
	}

	public void setPieceCount(EnumMap<TransportMode, Integer> pieceCount) {
		this.pieceCount = pieceCount;
	}

	///// Player Info /////

	public String getName() {
		return this.name;
	}

	public String getPlayerInfo() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("Name: " + this.name).append(System.lineSeparator());

		StringJoiner transportModeJoiner = new StringJoiner(System.lineSeparator());
		Iterator<Entry<TransportMode, SortedMap<Integer, List<ColorCard>>>> transportIterator = this.playerCards.entrySet().iterator();
		while (transportIterator.hasNext()) {
			Entry<TransportMode, SortedMap<Integer, List<ColorCard>>> entry = transportIterator.next();
			StringJoiner cardJoiner = new StringJoiner(System.lineSeparator());
			for (Entry<Integer, List<ColorCard>> colorCardEntry : entry.getValue().entrySet()) {
				cardJoiner.add(colorCardEntry.getKey() + " " + colorCardEntry.getValue().stream().map(c -> c.color().colorName).collect(Collectors.joining(", ")));
			}
			transportModeJoiner.add(entry.getKey() + ":");
			transportModeJoiner.add(cardJoiner.toString());
		}
		sb.append(transportModeJoiner.toString());

		sb.append(System.lineSeparator());
		sb.append(this.missionCards.stream().map(MissionCard::toString).collect(Collectors.joining(", ", "MissionCards: ", "")));
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.ID, this.name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (this.getClass() != obj.getClass()) { return false; }
		Player other = (Player) obj;
		return Objects.equals(this.ID, other.ID) && Objects.equals(this.name, other.name);
	}

	@Override
	public String toString() {
		return this.getPlayerInfo();
	}

}
