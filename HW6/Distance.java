public class Distance implements Comparable<Distance> {
	public static final long TRSF_COST = 5l;
	private final Long dist;
	private final boolean isTransfer;

	public Distance() {
		dist = TRSF_COST;
		isTransfer = true;
	}

	public Distance(long dist) {
		this.dist = dist;
		this.isTransfer = false;
	}

	// For addition
	private Distance(long dist, boolean isTransfer) {
		this.dist = dist;
		this.isTransfer = isTransfer;
	}

	public long getValue() {
		return dist;
	}

	public boolean isInf() {
		return dist < 0;
	}

	public Distance add(Distance other) {
		if (dist < 0 || other.dist < 0) return new Distance(-1l);
		else return new Distance(dist + other.dist, isTransfer || other.isTransfer);
	}

	@Override
	public int compareTo(Distance other) {
		if (other == null || other.isInf()) return -1;
		else if (isInf()) return 1;
		else if (dist != other.dist) return dist.compareTo(other.dist);
		else if (other.isTransfer) return -1;
		else if (isTransfer) return 1;
		else return 0;
	}
}
