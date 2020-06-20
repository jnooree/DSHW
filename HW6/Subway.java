import java.io.*;
import java.nio.file.*;

import java.util.*;
import java.util.stream.*;

public class Subway {
	private static SubwayDB db = new SubwayDB();
	private static PathFinder navigation = new PathFinder(db);

	private static BufferedReader consoleReader
					= new BufferedReader(new InputStreamReader(System.in));
	private static BufferedWriter consoleWriter 
					= new BufferedWriter(new OutputStreamWriter(System.out));


	public static void main(String[] args) throws IOException {
		loadSubwayInfo(args[0]);

		String line;
		while (!(line = consoleReader.readLine()).equalsIgnoreCase("QUIT")) {
			String[] request = line.split(" ");
			printMinPath(navigation.findMinPath(request[0], request[1]));
		}
	}

	private static void loadSubwayInfo(String filePath) throws IOException {
		BufferedReader fileReader = Files.newBufferedReader(Paths.get(filePath));
		String line;

		while (!(line = fileReader.readLine()).isEmpty()) {
			db.addStation(line.split(" "));
		}

		db.setTransfer();

		while ((line = fileReader.readLine()) != null) {
			db.addEdge(line.split(" "));
		}
	}

	private static void printMinPath(List<Station> minPath) throws IOException {
		for (int i=minPath.size()-1; i>0; i--) {
			Station currStation = minPath.get(i);
			Station nextStation = minPath.get(i-1);
			
			String currName = db.getName(currStation.getID());
			String nextName = db.getName(nextStation.getID());

			if (currName.equals(nextName)) {
				do {
					nextStation = minPath.get(--i);
					nextName = getStationName(nextStation);
				} while (!currName.equals(nextName));

				currName = "[" + currName + "]";
			}

			consoleWriter.write(currName + " ");
		}

		Station lastStation = minPath.get(0);
		consoleWriter.write(getStationName(lastStation) + "\n");
		consoleWriter.write(String.valueOf(lastStation.getDist()) + "\n");
		consoleWriter.flush();
	}

	private static String getStationName(Station station) {
		return db.getName(station.getID());
	}
}