import java.util.*;

public class ConvStack extends Stack<Terms> {
	private static final long serialVersionUID = 1L;

	public ConvStack() {
		super();
		this.push(Terms.toTerm("(", 1));
	}

	public List<Terms> popTo(Terms term) {
		List<Terms> result = new ArrayList<>();

		while (this.peek().compareTo(term) >= 0) {
			result.add(this.pop());
		}

		return result;
	}
}