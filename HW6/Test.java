import java.io.*;
import java.nio.file.*;

import java.util.*;
import java.util.stream.*;

public class Test {
	public static void main(String[] args) {
		PriorityQueue<Test2> test = new PriorityQueue<>();
		Test2 a = new Test2(1);
		Test2 b = new Test2(10);
		Test2 c = new Test2(1000);

		test.add(a);
		test.add(b);
		test.add(c);

		a.update(100);

		int size = test.size();

		for (int i=0; i<size; i++) {
			Test2 tmp = test.poll();
			System.out.println(tmp);
		}
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