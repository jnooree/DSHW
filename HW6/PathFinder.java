import java.util.*;
import java.util.Map.*;


public class PathFinder {
	private final SubwayDB db;
	private Map<StationID, Distance> currBest; // For memoization

	public PathFinder(SubwayDB db) {
		this.db = db;
		this.currBest = new HashMap<>();
	}

	public Route findMinPath(String srcName, String destName) {
		currBest.clear();

		Station src = Station.searchName(srcName);
		Station dest = Station.searchName(destName);

		RouteNode finalDest = null;

		// Find min path for each ID
		for (StationID srcID: src) {
			RouteNode newDest = localMinPath(new RouteNode(srcID), dest);

			if (newDest.getStation().equals(dest)) {
				finalDest = newDest;
			}
		}

		return new Route(finalDest);
	}

	private RouteNode localMinPath(RouteNode src, Station dest) {
		PriorityQueue<RouteNode> candidate = new PriorityQueue<>();
		Set<StationID> visited = new HashSet<>();

		// Initialize
		Map<StationID, Distance> near = db.getNear(src.getID());
		for (StationID id: StationID.getAll()) {
			RouteNode newSt = id.equals(src.getID()) 
							? src
							: new RouteNode(src, id, near.getOrDefault(id, new Distance(-1l)));

			Distance oldDist = currBest.get(id);
			if (newSt.getDist().compareTo(oldDist) < 0) {
				currBest.put(id, newSt.getDist());
				candidate.add(newSt);
			}
		}

		// Find the shortest path by Dijkstra algorithm
		RouteNode curr;
		StationID currID;

		do {
			curr = candidate.poll(); // curr == src for the first loop
			currID = curr.getID();
			if (visited.contains(currID)) continue; // Avoid double-visiting
			
			visited.add(currID);
			Distance currDist = currBest.get(currID);

			for (Entry<StationID, Distance> next: db.getNear(currID).entrySet()) {
				if (!visited.contains(next.getKey())) {
					Distance oldDist = currBest.get(next.getKey()); // can be null
					Distance newDist = currDist.add(next.getValue());
					
					if (newDist.compareTo(oldDist) < 0) {
						RouteNode newNext = new RouteNode(curr, next.getKey(), newDist);
						
						currBest.put(next.getKey(), newDist);
						candidate.add(newNext);
					}
				}
			}
		} while (!currID.getStation().equals(dest) && !candidate.isEmpty());

		return curr;
	}
}

class Route {
	private final RouteNode finalDest;
	private final List<String> route;

	Route(RouteNode finalDest) {
		this.finalDest = finalDest;

		List<String> minPath = new ArrayList<>();
		while (finalDest != null) {
			minPath.add(finalDest.getStation().getName());
			finalDest = finalDest.getPrev();
		}
		this.route = minPath;
	}

	public long totalTime() {
		return finalDest.getDist().getValue();
	}

	@Override
	public String toString() {
		String routeString = "";

		for (int i=route.size()-1; i>0;) {
			String currName = route.get(i);
			
			if (currName.equals(route.get(--i))) {
				while (currName.equals(route.get(--i)));
				currName = "[" + currName + "]";
			}
			
			routeString += currName + " ";
		}

		routeString += route.get(0);
		return routeString;
	}
}

class RouteNode implements Comparable<RouteNode> {
	private final RouteNode prev;
	private final StationID id;
	private final Distance dist;

	RouteNode(StationID srcID) {
		prev = null;
		id = srcID;
		dist = new Distance(0l);
	}

	RouteNode(RouteNode prev, StationID id, Distance dist) {
		this.prev = prev;
		this.id = id;
		this.dist = dist;
	}

	StationID getID() {
		return id;
	}

	Station getStation() {
		return id.getStation();
	}

	RouteNode getPrev() {
		return prev;
	}

	Distance getDist() {
		return dist;
	}

	public final boolean isReachable() {
		return !dist.isInf();
	}

	@Override
	public int compareTo(RouteNode other) {
		if (other == null) return -1;
		else return this.dist.compareTo(other.dist);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;

		RouteNode st = (RouteNode) obj;
		return this.id.equals(st.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode() * 7 + 1;
	}

	@Override
	public String toString() {
		return prev.getStation().toString() 
			   + " --(" + String.valueOf(dist.getValue()) + ")-> " 
			   + this.getStation().toString();
	}
}
