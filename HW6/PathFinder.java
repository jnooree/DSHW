import java.util.*;
import java.util.Map.*;


public class PathFinder {
	public static final long MAX_WEIGHT = ((long) Integer.MAX_VALUE) * 10;

	private SubwayDB db;
	private Map<String, Station> distance;


	public PathFinder(SubwayDB db) {
		this.db = db;
	}

	public List<Station> findMinPath(String srcName, String destName) {
		Set<String> src = db.getIDFrom(srcName);
		Set<String> dest = db.getIDFrom(destName);

		Station finalDest = null;
		Map<String, Station> minDist = null;

		for (String srcID: src) {
			Station newDest = localMinPath(srcID, dest); // Find min path for each ID

			if (newDest.getDist() != 0 && newDest.compareTo(finalDest) < 0) {
				finalDest = newDest;
				minDist = distance;
			}
		}

		List<Station> minPath = new ArrayList<>();
		minPath.add(finalDest);

		do {
			finalDest = minDist.get(finalDest.getSrc());
			minPath.add(finalDest);
		} while (finalDest.getSrc() != null);

		return minPath;
	}

	private Station localMinPath(String srcID, Set<String> dest) {
		distance = new HashMap<>();

		PriorityQueue<Station> candidate = new PriorityQueue<>();
		Set<String> visited = new HashSet<>();

		// Initialize
		Map<String, Long> near = db.getNear(srcID);
		for (String id: db.getAllID()) {
			Station newSt = id.equals(srcID) 
							? new Station(null, id, 0)
							: new Station(srcID, id, near.getOrDefault(id, -1l));

			distance.put(id, newSt);
			if (!newSt.isInf()) {
				candidate.add(newSt);
			}
		}

		// Find the shortest path by Dijkstra algorithm
		Station curr;
		String currID;

		do {
			curr = candidate.poll(); // curr == src for the first loop
			currID = curr.getID();
			if (visited.contains(currID)) continue; // Avoid double-visiting
			
			visited.add(currID);
			long currDist = distance.get(currID).getDist();

			for (Entry<String, Long> next: db.getNear(currID).entrySet()) {
				if (!visited.contains(next.getKey())) {
					Station old = distance.get(next.getKey());
					long newDist = currDist + next.getValue();
					
					if (old.isInf() || old.getDist() > newDist) {
						Station newNext = new Station(currID, next.getKey(), newDist);
						
						distance.replace(next.getKey(), newNext);
						candidate.add(newNext);
					}
				}
			}
		} while (!dest.contains(currID) && !candidate.isEmpty());

		return curr;
	}
}


class Station implements Comparable<Station> {
	private final String id;
	private final String srcID;
	private final Long distance;

	Station(String srcID, String id, long dist) {
		this.id = id;
		this.srcID = srcID;
		this.distance = dist;
	}

	String getID() {
		return id;
	}

	String getSrc() {
		return srcID;
	}

	long getDist() {
		return distance;
	}

	public final boolean isInf() {
		return distance < 0;
	}

	@Override
	public int compareTo(Station other) {
		if (other == null || other.distance < 0) return -1;
		else if (this.distance >= other.distance) return 1;
		else return -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;

		Station st = (Station) obj;
		return this.id.equals(st.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode() * 7 + 1;
	}

	@Override
	public String toString() {
		return srcID + " --(" + String.valueOf(distance) + ")-> " + id;
	}
}
