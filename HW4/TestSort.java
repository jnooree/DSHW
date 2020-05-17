import java.io.*;
import java.util.*;

public class TestSort {
	private static final int[] TEST_SIZES = new int[] {10000, 50000, 100000, 500000, 1000000, 5000000, 10000000, 50000000, 100000000};

	public static void main(String args[]) {
		int[] value;

		for (int size: TEST_SIZES) {
			System.out.println("-----Test for size == " + Integer.toString(size) + "-----");
			for (int i=0; i<10; i++) {
				System.out.println(Integer.toString(i) + "th experiment");
				Random rand = new Random();	// 난수 인스턴스를 생성한다.

				value = new int[size];	// 배열을 생성한다.
				for (int j = 0; j < value.length; j++)	// 각각의 배열에 난수를 생성하여 대입
					value[j] = rand.nextInt(Integer.MAX_VALUE) * (rand.nextBoolean() ? -1 : 1);

				long t = System.currentTimeMillis();

				int[] dummy = value.clone();
				dummy = DoQuickSort(value);
				
				System.out.println("Quick: " + (System.currentTimeMillis() - t) + " ms");
			}
			System.out.println();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoBubbleSort(int[] value) {
		// i == last index to compare
		// j == first item to compare (with (j+1)th item)
		for (int i = value.length-1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				if (value[j] > value[j+1]) {
					swap(value, j, j+1); // Swap if in wrong order
				}
			}
		}
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoInsertionSort(int[] value) {
		// i == start of the unsorted array
		// j == index of sorted array (from right to left)
		int next, j; // Saving time for memory allocation

		for (int i = 1; i < value.length; i++) {
			next = value[i];

			for (j = i-1; (j >= 0) && (value[j] >= next); j--) {
				// Shift right until value[j] is larger than or equal to next
				value[j+1] = value[j];
			}
			value[j+1] = next; // Insert next at the right position
		}
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoHeapSort(int[] value) {
		int n = value.length;
		int i; // Saving time for memory allocation

		// Build heap
		for (i = n/2 - 1; i >= 0; i--) {
			percolateDown(value, i, n);
		}

		// Do heapsort
		for (i = n-1; i > 0; i--) {
			swap(value, 0, i);
			percolateDown(value, 0, i);
		}

		return value;
	}

	private static void percolateDown(int[] array, int parent, int end) {
		int child = 2 * parent;
		int rightChild = child + 1;

		if (child < end) {
			if ((rightChild < end) && (array[child] < array[rightChild])) {
				child = rightChild;
			}
			if (array[parent] < array[child]) {
				swap(array, parent, child);
				percolateDown(array, child, end);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoMergeSort(int[] value) {
		if (value.length > 1) {
			// Do mergesort and merge the two arrays
			int[] leftValue = DoMergeSort(Arrays.copyOfRange(value, 0, value.length/2));
			int[] rightValue = DoMergeSort(Arrays.copyOfRange(value, value.length/2, value.length));
			return merge(leftValue, rightValue);
		} else {
			// Base case: do nothing when length < 1 (No need to sort)
			return value;
		}
	}

	private static int[] merge(int[] array1, int[] array2) {
		int[] mergedArray = new int[array1.length + array2.length];
		int i = 0;
		int j = 0;

		// Loop until one of the arrays is finished
		while (i < array1.length && j < array2.length) {
			if (array1[i] == array2[j]) {
				// If the two items are equal, add both
				mergedArray[i + j] = array1[i++];
				mergedArray[i + j] = array2[j++];
			} else if (array1[i] < array2[j]) {
				// Else, add the smaller item
				mergedArray[i + j] = array1[i++];
			} else {
				mergedArray[i + j] = array2[j++];
			}
		}

		// If one of the arrays is not finished, add the rest of items
		if (i != array1.length) {
			for (int k = i; k < array1.length; k++) {
				mergedArray[k + j] = array1[k];
			}
		} else if (j != array2.length) {
			for (int k = j; k < array2.length; k++) {
				mergedArray[i + k] = array2[k];
			}
		}

		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoQuickSort(int[] value) {
		partition(value, 0, value.length);

		return value;
	}

	private static void partition(int[] array, int start, int end) {
		// Select pivot based on index
		int pivotItem = array[(end + start) / 2];

		// lp == the (last index) + 1 of smaller items than pivot
		// mp == the (last index) + 1 of equal items to pivot
		// rp is not needed (always rp == end)
		int lp = start;
		int mp = start;

		for (int i = start; i < end; i++) {
			// Do nothing when array[i] > pivotItem
			if (array[i] == pivotItem) {
				// If same to pivot, move to the right place
				swap(array, i, mp++);
			} else if (array[i] < pivotItem) {
				// If smaller than pivot, move to the right place
				swap(array, i, lp++);
				// If there are some equal items to the pivot, need an additional swap
				if (lp != ++mp) swap(array, i, mp-1);
			}
		}

		// Recursive call (with base case)
		if (lp - start > 1) partition(array, start, lp);
		if (end - mp > 1) partition(array, mp, end);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// For radix sort
	private static final int MAX_INT_LEN = 10;
	private static final int DIGITS_TYPE = 19; // -9 ~ 0 ~ 9

	private static int[] DoRadixSort(int[] value) {
		int exp = 1;
		boolean isFinished = false;

		// Saving time for memory allocation
		int digit, j;
		int[] counts = new int[DIGITS_TYPE];

		for (int i = 0; (i < MAX_INT_LEN) && (! isFinished); i++) {
			isFinished = true;

			for (j = 0; j < DIGITS_TYPE; j++) {
				counts[j] = 0;
			}

			for (int num: value) {
				// Add based on digits
				digit = (num / exp) % 10 + 9;
				counts[digit]++;
				if (isFinished && digit != 9) isFinished = false;
			}

			// return if every digits are zero
			if (isFinished) return value;

			for (j = 1; j < DIGITS_TYPE; j++) {
				counts[j] += counts[j-1];
			}

			// Fill back to the original array
			int[] sorted = new int[value.length];
			for (j = value.length - 1; j >= 0; j--) {
				digit = (value[j] / exp) % 10 + 9;
				sorted[--counts[digit]] = value[j];
			}
			value = sorted;
			exp *= 10;
		}

		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int temp; // To save time for memory allocation

	// Small utility for convenience
	private static void swap(int[] array, int i, int j) {
		temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
}

// Utility class for faster output
class ConsoleWriter {
	private static BufferedWriter consoleWriter = new BufferedWriter(new OutputStreamWriter(System.out));

	public ConsoleWriter() {}

	public static void writef(String s, Object... arg) throws IOException {
		consoleWriter.write(String.format(s, arg));
	}

	public static void writeln(String s, Object... arg) throws IOException {
		ConsoleWriter.writef(s + "\n", arg);
	}

	public static void writeln(int num, Object... arg) throws IOException {
		String s = String.valueOf(num);
		ConsoleWriter.writef(s + "\n", arg);
	}

	public static void flush() throws IOException {
		consoleWriter.flush();
	}

	public static void println(String s, Object... arg) throws IOException {
		ConsoleWriter.writeln(s, arg);
		ConsoleWriter.flush();
	}
}
