import java.util.*;
import java.util.Map.*;


public class SubwayDB {
	private Map<StationID, Map<StationID, Distance>> db;

	public SubwayDB() {
		db = new HashMap<>();
	}

	public void clear() {
		Station.clear();
		db.clear();
	}

	public void addStation(String[] stationInfo) {
		Station.add(stationInfo[0], stationInfo[1]);
		db.put(StationID.searchID(stationInfo[0]), new HashMap<>());
	}

	public void addEdge(String[] edgeInfo) {
		StationID srcID = StationID.searchID(edgeInfo[0]);
		
		Map<StationID, Distance> edges = db.get(srcID);
		edges.put(StationID.searchID(edgeInfo[1]), new Distance(Long.parseLong(edgeInfo[2])));
		db.put(srcID, edges);
	}

	public void setTransfer() {
		for (Station station: Station.getAll()) {
			List<StationID> transferList = new ArrayList<>(station.getID()); // Type conversion

			for (int i=0; i<transferList.size(); i++) {
				for (int j=i+1; j<transferList.size(); j++) {
					db.get(transferList.get(i)).put(transferList.get(j), new Distance());
					db.get(transferList.get(j)).put(transferList.get(i), new Distance());
					//System.out.println(transferList.get(i));
					//System.out.println(transferList.get(j));
				}
			}
		}
	}

	public Map<StationID, Distance> getNear(StationID id) {
		return db.get(id);
	}
}
