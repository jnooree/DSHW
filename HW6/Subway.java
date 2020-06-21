import java.io.*;
import java.nio.file.*;
import java.util.*;


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

	private static void printMinPath(Route minPath) throws IOException {
		consoleWriter.write(minPath.toString() + "\n");
		consoleWriter.write(String.valueOf(minPath.totalTime()) + "\n");
		consoleWriter.flush();
	}
}