import java.io.*;
import java.util.*;

public class SortingTest {
	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			boolean isRandom = false;	// 입력받은 배열이 난수인가 아닌가?
			int[] value;	// 입력 받을 숫자들의 배열
			String nums = br.readLine();	// 첫 줄을 입력 받음
			if (nums.charAt(0) == 'r') {
				// 난수일 경우
				isRandom = true;	// 난수임을 표시

				String[] nums_arg = nums.split(" ");

				int numsize = Integer.parseInt(nums_arg[1]);	// 총 갯수
				int rminimum = Integer.parseInt(nums_arg[2]);	// 최소값
				int rmaximum = Integer.parseInt(nums_arg[3]);	// 최대값

				Random rand = new Random();	// 난수 인스턴스를 생성한다.

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 각각의 배열에 난수를 생성하여 대입
					value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			} else {
				// 난수가 아닐 경우
				int numsize = Integer.parseInt(nums);

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 한줄씩 입력받아 배열원소로 대입
					value[i] = Integer.parseInt(br.readLine());
			}

			// 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
			while (true) {
				int[] newvalue = value.clone();	// 원래 값의 보호를 위해 복사본을 생성한다.

				String command = br.readLine();

				long t = System.currentTimeMillis();
				switch (Character.toUpperCase(command.charAt(0))) {
					case 'B':	// Bubble Sort
						newvalue = DoBubbleSort(newvalue);
						break;
					case 'I':	// Insertion Sort
						newvalue = DoInsertionSort(newvalue);
						break;
					case 'H':	// Heap Sort
						newvalue = DoHeapSort(newvalue);
						break;
					case 'M':	// Merge Sort
						newvalue = DoMergeSort(newvalue);
						break;
					case 'Q':	// Quick Sort
						newvalue = DoQuickSort(newvalue);
						break;
					case 'R':	// Radix Sort
						newvalue = DoRadixSort(newvalue);
						break;
					case 'X':
						return;	// 프로그램을 종료한다.
					default:
						throw new IOException("잘못된 정렬 방법을 입력했습니다.");
				}

				if (isRandom) {
					// 난수일 경우 수행시간을 출력한다.
					System.out.println((System.currentTimeMillis() - t) + " ms");
				} else {
					// 난수가 아닐 경우 정렬된 결과값을 출력한다.
					for (int i = 0; i < newvalue.length; i++) {
						ConsoleWriter.writeln(newvalue[i]);
					}
					ConsoleWriter.flush();
				}
			}
		} catch (IOException e) {
			System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
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
		for (int i = 1; i < value.length; i++) {
			int temp = value[i];

			int j;
			for (j = i-1; (j >= 0) && (value[j] >= temp); j--) {
				// Shift to right until value[j] is larger than or equal to temp
				value[j+1] = value[j];
			}
			value[j+1] = temp; // Insert temp at the right position
		}
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoHeapSort(int[] value) {
		int n = value.length;

		// Build heap
		for (int i = n/2 - 1; i >= 0; i--) {
			percolateDown(value, i, n);
		}

		// Do heapsort
		for (int i = n-1; i > 0; i--) {
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
			for (int k=i; k<array1.length; k++) {
				mergedArray[k + j] = array1[k];
			}
		} else if (j != array2.length) {
			for (int k=j; k<array2.length; k++) {
				mergedArray[i + k] = array2[k];
			}
		}

		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int count = 0;

	private static int[] DoQuickSort(int[] value) {
		partition(value, 0, value.length);

		return value;
	}

	private static void partition(int[] array, int start, int end) {
		if (end - start <= 1) {
			return;
		}

		// Select pivot based on index
		int pivotItem = array[(end + start) / 2];

		// lp == the (last index) + 1 of smaller items than pivot
		// mp == the (last index) + 1 of equal items to pivot
		// rp is not needed (always rp == end)
		int lp = start;
		int mp = start;

		for (int i=start; i<end; i++) {
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

		// Recursive call
		partition(array, start, lp);
		partition(array, mp, end);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// For radix sort
	private static final int MAX_INT_LEN = 10;
	private static final int DIGITS_LEN = 19; // -9 ~ 0 ~ 9

	private static int[] DoRadixSort(int[] value) {
		int exp = 1;

		for (int i=0; i<MAX_INT_LEN; i++) {
			ArrayList<ArrayList<Integer>> sorted = new ArrayList<>();

			for (int j=0; j<DIGITS_LEN; j++) {
				sorted.add(new ArrayList<>());
			}

			exp *= 10;
			for (int num: value) {
				// Add based on digits; ArrayList.get() method is an O(1) operation.
				sorted.get((num % exp) / (exp / 10) + 9).add(num);
			}

			// Add back to the original array
			int j = 0;
			for (ArrayList<Integer> digitList: sorted) {
				for (int num: digitList) {
					value[j++] = num;
				}
			}
		}

		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Small utility for convenience
	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
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
