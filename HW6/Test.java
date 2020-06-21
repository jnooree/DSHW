import java.io.*;
import java.nio.file.*;

import java.util.*;
import java.util.stream.*;

public class Test {
	public static void main(String[] args) {
		HashMap<String, Long> hashmap = new HashMap<>();
		long a = hashmap.get("0");

		System.out.println(a);
	}
}

class Test2 implements Comparable<Test2> {
	private Integer t;

	Test2(int t) {
		this.t = t;
	}

	void update(int newT) {
		this.t = newT;
	}

	public int compareTo(Test2 obj) {
		return this.t.compareTo(obj.t);
	}

	public String toString() {
		return String.valueOf(t);
	}
}