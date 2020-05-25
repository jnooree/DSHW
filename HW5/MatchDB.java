import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;

public class MatchDB {
	public static final int SUBSTR_LEN = 6;
	private HashTable<MatchDBItem> substrings;

	public MatchDB() {
		substrings = new HashTable<>();
	}

	public void reset() {
		substrings.clear();
	}

	public void insert(List<String> inputList) throws UnsupportedEncodingException {
		for (int i = 0; i < inputList.size(); i++) {
			String input = inputList.get(i);
			for (int j = 0; j <= (input.length() - SUBSTR_LEN); j++) {
				substrings.insert(new MatchDBItem(input.substring(j, j+SUBSTR_LEN), 
								  				  new int[] {i+1, j+1}));
			}
		}
	}

	public List<MatchDBItem> getItems(int hash) {
		return substrings.getItems(hash);
	}

	public MyList<MatchDBItem> search(String target) throws UnsupportedEncodingException {
		int tgtLen = target.length();
		List<MyList<MatchDBItem>> matches = new ArrayList<>();

		// 앞에서부터 최대 SUBSTR_LEN개 만큼씩 잘라서 비교
		for (int i = SUBSTR_LEN; i < (tgtLen + SUBSTR_LEN); i += SUBSTR_LEN) {
			int endIdx = i < tgtLen ? i : tgtLen;
			int startIdx = endIdx - SUBSTR_LEN;
			
			String key = target.substring(startIdx, endIdx);
			matches.add(substrings.search(new MatchDBItem(key)));
		}

		return getFullMatch(matches, tgtLen);
	}

	private MyList<MatchDBItem> getFullMatch(List<MyList<MatchDBItem>> matches, int tgtLen) {
		MyList<MatchDBItem> result = matches.get(0);

		for (int i = 1; (i < matches.size()) && (!result.isEmpty()); i++) {
			Iterator<MatchDBItem> resultIter = result.iterator();
			
			int diff; //자른 과정에서 전체 key의 첫 index와 발생한 차이
			if ((i + 1) * SUBSTR_LEN <= tgtLen) {
				diff = i * SUBSTR_LEN;
			} else {
				diff = (i - 1) * SUBSTR_LEN + tgtLen % SUBSTR_LEN;
			}

			while (resultIter.hasNext()) {
				MatchDBItem ref = resultIter.next();
				boolean remove = true;

				for (MatchDBItem comp: matches.get(i)) {
					int[] j = ref.getIdx();
					int[] k = comp.getIdx();

					if (j[0] == k[0] && j[1] == (k[1] - diff))
						remove = false; //Index 비교해 모두 동일한 경우만 제거하지 않음
				}

				if (remove) resultIter.remove();
			}
		}

		return result;
	}
}