/******************************************************************************
 * MatchDB의 인터페이스에서 공통으로 사용하는 클래스.
 */
public class MatchDBItem implements Comparable<MatchDBItem> {
	private final String substring;
	private final int[] indices;

	public MatchDBItem(String string) {
		this.substring = string;
		this.indices = null;
	}

	public MatchDBItem(String string, int[] indices) {
		this.substring = string;
		this.indices = indices;
	}

	public int[] getIdx() {
		return indices;
	}

	@Override
	public String toString() {
		return substring;
	}

	@Override
	public int compareTo(MatchDBItem other) {
		return substring.compareTo(other.substring);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		MatchDBItem other = (MatchDBItem) obj;
		return this.substring.equals(other.substring);
	}

	@Override
	public int hashCode() {
		return substring.hashCode();
	}
}