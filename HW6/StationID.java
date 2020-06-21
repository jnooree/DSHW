import java.util.*;


public class StationID {
	private static Map<String, StationID> allID = new HashMap<>();

	private final Station station;
	private final String id;

	private StationID(Station station, String id) {
		this.station = station;
		this.id = id;
		allID.put(id, this);
	}

	public Station getStation() {
		return station;
	}

	public static StationID newID(Station station, String id) {
		StationID stID = allID.get(id);
		if (stID == null) {
			stID = new StationID(station, id);
			allID.put(id, stID);
		}
		return stID;
	}

	public static StationID searchID(String id) {
		StationID stID = allID.get(id);
		if (stID == null) {
			throw new NoSuchElementException();
		}
		return stID;
	}

	public static Collection<StationID> getAll() {
		return allID.values();
	}

	public static void clear() {
		allID.clear();
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
		return station.hashCode() + id.hashCode();
	}
}