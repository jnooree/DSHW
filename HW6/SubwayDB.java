import java.util.*;
import java.util.Map.*;


public class SubwayDB {
	public static final long TRSF_COST = 5l;
	private RailDB railDB;
	private StationDB stationDB;

	public SubwayDB() {
		railDB = new RailDB();
		stationDB = new StationDB();
	}

	public void clear() {
		railDB.clear();
		stationDB.clear();
	}

	public void addStation(String[] stationInfo) {
		railDB.add(stationInfo[0]);
		stationDB.add(stationInfo[0], stationInfo[1]);
	}

	public void setTransfer() {
		for (Set<String> transferSet: stationDB.iterName()) {
			List<String> transferList = new ArrayList<>(transferSet); // Type conversion

			for (int i=0; i<transferList.size(); i++) {
				for (int j=i+1; j<transferList.size(); j++) {
					railDB.add(transferList.get(i), transferList.get(j), TRSF_COST);
					railDB.add(transferList.get(j), transferList.get(i), TRSF_COST);
				}
			}
		}
	}

	public void addEdge(String[] edgeInfo) {
		railDB.add(edgeInfo[0], edgeInfo[1], Long.parseLong(edgeInfo[2]));
	}

	public String getName(String id) {
		return stationDB.getName(id);
	}

	public Set<String> getIDFrom(String name) {
		return stationDB.getIDFrom(name);
	}

	public Set<String> getAllID() {
		return stationDB.getAllID();
	}

	public Map<String, Long> getNear(String stationID) {
		return railDB.getNear(stationID);
	}
}

class StationDB implements Iterable<Entry<String, Set<String>>> {
	private Map<String, Set<String>> nameDB;
	private Map<String, String> idDB;

	StationDB() {
		nameDB = new HashMap<>();
		idDB = new HashMap<>();
	}

	void clear() {
		nameDB.clear();
		idDB.clear();
	}

	void add(String newID, String newName) {
		idDB.put(newID, newName);

		Set<String> stSet = nameDB.get(newName);
		if (stSet == null)
			stSet = new HashSet<>();
		stSet.add(newID);
		nameDB.put(newName, stSet);
	}

	String getName(String id) {
		return idDB.get(id);
	}

	Set<String> getIDFrom(String name) throws NoSuchElementException {
		Set<String> stList = nameDB.get(name);
		
		if (stList == null)
			throw new NoSuchElementException();
		
		return stList;
	}

	Set<String> getAllID() {
		return idDB.keySet();
	}

	Collection<Set<String>> iterName() {
		return nameDB.values();
	}

	@Override
	public Iterator<Entry<String, Set<String>>> iterator() {
		return nameDB.entrySet().iterator();
	}
}

class RailDB implements Iterable<Entry<String, Map<String, Long>>> {
	private Map<String, Map<String, Long>> db;

	RailDB() {
		db = new HashMap<>();
	}

	void clear() {
		db.clear();
	}

	void add(String newID) {
		db.put(newID, new HashMap<>());
	}

	void add(String srcID, String destID, long weight) {
		db.get(srcID).put(destID, weight);
	}

	Map<String, Long> getNear(String id) {
		return db.get(id);
	}

	@Override
	public Iterator<Entry<String, Map<String, Long>>> iterator() {
		return db.entrySet().iterator();
	}
}
