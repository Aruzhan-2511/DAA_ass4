import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        for (int i = 1; i < m; i++){
            int j = pi[i-1];
            while(j > 0 && p.charAt(i) != p.charAt(j)){
                j = pi[j-1];
            }
            if (p.charAt(i) == p.charAt(j)){
                j++;
            }
            pi[i] = j;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        if (p.isEmpty()){
            return new int[0];
        }
        int[] pi = prefixFunction(p);
        ArrayList<Integer> positions = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < text.length(); i++){
            while(j > 0 && text.charAt(i) != p.charAt(j)){
                j = pi[j-1];
            }
            if (text.charAt(i) == p.charAt(j)){
                j++;
            }
            if(j == p.length()){
                positions.add(i-p.length()+1);
                j = pi[j-1];
            }
        }
        int[] result = new int[positions.size()];
        for (int i = 0; i < positions.size(); i++){
            result[i] = positions.get(i);
        }
        return result;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k <= 0 || a.length() < k || b.length() < k){
            return 0;
        }
        final long BASE = 257;
        final long MOD = 1_000_000_007L;
        long power = 1;
        for (int i = 1; i < k; i++){
            power = (power * BASE) % MOD;
        }
        HashMap<Long, ArrayList<Integer>> hashes = new HashMap<>();
        long hashB = 0;
        for (int i = 0; i < k; i++){
            hashB = (hashB * BASE + b.charAt(i)) % MOD;
        }
        for (int i = 0; i <= b.length() - k; i++){
            if (i > 0){
                hashB = (hashB - b.charAt(i-1) * power % MOD + MOD) % MOD;
                hashB = (hashB * BASE + b.charAt(i+k-1)) % MOD;
            }
            hashes.computeIfAbsent(hashB, x -> new ArrayList<>()).add(i);
        }
        long hashA = 0;
        for (int i = 0; i < k; i++){
            hashA = (hashA * BASE +a.charAt(i)) % MOD;
        }
        int count = 0;
        for (int i = 0; i <= a.length() -k; i++){
            if (i > 0){
                hashA = (hashA - a.charAt(i-1) * power % MOD + MOD) % MOD;
                hashA = (hashA * BASE +a.charAt(i + k -1)) % MOD;
            }
            ArrayList<Integer> positions = hashes.get(hashA);
            if(positions != null){
                for(int pos : positions){
                    boolean same = true;
                    for (int j = 0; i < k; j++){
                        if(a.charAt(i+j) != b.charAt(pos+j)){
                            same = false;
                            break;
                        }
                    }
                    if (same){
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }
}
