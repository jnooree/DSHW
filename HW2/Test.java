public class Test {
	public static void main(String[] args) {
		MyLinkedList<String> a = new MyLinkedList<>("head", "a");
		a.insert("b", 2);
		a.insert("c", 2);

		for (String i: a) {
			System.out.println(i);
		}
	}
}