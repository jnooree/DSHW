import java.util.*;
import java.util.Map.*;


public class Station implements Iterable<StationID> {
	private static Map<String, Station> allStation = new HashMap<>();

	private final String name;
	private Set<StationID> idSet;

	private Station(String name) {
		this.name = name;
		this.idSet = new HashSet<>();
	}

	public static void add(String id, String name) {
		if (allStation.containsKey(name)) {
			allStation.get(name).addID(id);
		} else {
			Station station = new Station(name);
			station.addID(id);
			allStation.put(name, station);
		}
	}

	private void addID(String id) {
		idSet.add(StationID.newID(this, id));
	}


	public String getName() {
		return name;
	}

	public Collection<StationID> getID() {
		return idSet;
	}

	public static Station searchName(String name) {
		Station station = allStation.get(name);
		if (station == null)
			throw new NoSuchElementException();
		return station;
	}

	public static Collection<Station> getAll() {
		return allStation.values();
	}

	public static void clear() {
		allStation.clear();
		StationID.clear();
	}

	@Override
	public Iterator<StationID> iterator() {
		return idSet.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (this == obj) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode() * 11 + 1;
	}

	@Override
	public String toString() {
		return name;
	}
}
