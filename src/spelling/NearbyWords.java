/**
 * 
 */
package spelling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


/**
 * @author UC San Diego Intermediate MOOC team
 *
 */
public class NearbyWords implements SpellingSuggest {
	// THRESHOLD to determine how many words to look through when looking
	// for spelling suggestions (stops prohibitively long searching)
	// For use in the Optional Optimization in Part 2.
	private static final int THRESHOLD = 1000 * 100;

	Dictionary dict;

	public NearbyWords (Dictionary dict) 
	{
		this.dict = dict;
	}

	/** Return the list of Strings that are one modification away
	 * from the input string.  
	 * @param s The original String
	 * @param wordsOnly controls whether to return only words or any String
	 * @return list of Strings which are nearby the original string
	 */
	public List<String> distanceOne(String s, boolean wordsOnly )  {
		   List<String> retList = new ArrayList<String>();
		   insertions(s, retList, wordsOnly);
		   substitution(s, retList, wordsOnly);
		   deletions(s, retList, wordsOnly);
		   return retList;
	}

	
	/** Add to the currentList Strings that are one character mutation away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void substitution(String s, List<String> currentList, boolean wordsOnly) {
		// for each letter in the s and for all possible replacement characters
		for(int index = 0; index < s.length(); index++){
			for(int charCode = (int)'a'; charCode <= (int)'z'; charCode++) {
				// use StringBuffer for an easy interface to permuting the
				// letters in the String
				StringBuffer sb = new StringBuffer(s);
				sb.setCharAt(index, (char)charCode);

				// if the item isn't in the list, isn't the original string, and
				// (if wordsOnly is true) is a real word, add to the list
				if(!currentList.contains(sb.toString()) &&
						(!wordsOnly||dict.isWord(sb.toString())) &&
						!s.equals(sb.toString())) {
					currentList.add(sb.toString());
				}
			}
		}
	}
	
	/** Add to the currentList Strings that are one character insertion away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void insertions(String s, List<String> currentList, boolean wordsOnly ) {
        if (s != null) {
            for (int index = 0; index < s.length() + 1; index++) {
                for (int charCode = (int) 'a'; charCode <= (int) 'z'; charCode++) {
                    StringBuffer sb = new StringBuffer(s);
                    sb.insert(index, (char) charCode);

                    if (!currentList.contains(sb.toString()) &&
                            (!wordsOnly || dict.isWord(sb.toString())) &&
                            !s.equals(sb.toString())) {
                        currentList.add(sb.toString());
                    }
                }
            }
        }
	}

	/** Add to the currentList Strings that are one character deletion away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void deletions(String s, List<String> currentList, boolean wordsOnly ) {
        if (s != null && s.length() > 1) {
            for (int index = 0; index < s.length(); index++) {
                StringBuffer sb = new StringBuffer(s);
                sb.deleteCharAt(index);

                if (!currentList.contains(sb.toString()) &&
                        (!wordsOnly || dict.isWord(sb.toString())) &&
                        !s.equals(sb.toString())) {
                    currentList.add(sb.toString());
                }
            }
        }
	}

	/** Add to the currentList Strings that are one character deletion away
	 * from the input string.  
	 * @param word The misspelled word
	 * @param numSuggestions is the maximum number of suggestions to return 
	 * @return the list of spelling suggestions
	 */
	@Override
	public List<String> suggestions(String word, int numSuggestions) {
		List<String> queue = new LinkedList<String>();
		HashSet<String> visited = new HashSet<String>();
		List<String> suggestionList = new LinkedList<String>();
        int wordCheckCounter = 0;

		queue.add(word);
		visited.add(word);
					
        while (!queue.isEmpty() && suggestionList.size() < numSuggestions && wordCheckCounter < THRESHOLD) {
            String curr = queue.remove(0);
            List<String> neighbors = new ArrayList<>();
            findNeighborWords(curr, neighbors);

            for (int i = 0; i < neighbors.size() && wordCheckCounter < THRESHOLD; i++) {
                String n = neighbors.get(i);

                if (!visited.contains(n)) {
                    visited.add(n);
                    queue.add(n);
                }

                if (dict.isWord(n) && !suggestionList.contains(n)) {
                    suggestionList.add(n);
                }

                wordCheckCounter++;
            }
        }
		
		return suggestionList;
	}

    private void findNeighborWords(String curr, List<String> neighbors) {
        substitution(curr, neighbors, true);
        insertions(curr, neighbors, true);
        deletions(curr, neighbors, true);
    }

    public static void main(String[] args) {
	    //basic testing code to get started
	   String word = "i";
	   // Pass NearbyWords any Dictionary implementation you prefer
	   Dictionary d = new DictionaryHashSet();
	   DictionaryLoader.loadDictionary(d, "data/dict.txt");
	   NearbyWords w = new NearbyWords(d);
	   List<String> l = w.distanceOne(word, true);
	   System.out.println("One away word Strings for for \""+word+"\" are:");
	   System.out.println(l+"\n");

	   word = "tailo";
	   List<String> suggest = w.suggestions(word, 10);
	   System.out.println("Spelling Suggestions for \""+word+"\" are:");
	   System.out.println(suggest);

       word = "kangaro";
       suggest = w.suggestions(word, 10);
       System.out.println("Spelling Suggestions for \""+word+"\" are:");
       System.out.println(suggest);
   }
}
