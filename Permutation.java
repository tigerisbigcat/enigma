package enigma;

import java.util.HashMap;
import java.util.Map;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Lei Hao
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        cycles = cycles.replace(" ", "");
        cycles = cycles.replace("(", "");
        String[] cycleArray = cycles.split("\\)");

        for (String a : cycleArray) {
            if (!a.equals("") || !a.isEmpty()) {
                for (int i = 0; i < a.length(); i++) {
                    if (!alphabet().contains(a.charAt(i))) {
                        throw new EnigmaException(a.charAt(i)
                                + " is not in the alphabet.");
                    }
                }
                addCycle(a);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm.
     * @param cycle = "AELTPHQXRU". */
    private void addCycle(String cycle) {
        cycle = cycle.replace(" ", "");
        for (int i = 0; i < cycle.length() - 1; i++) {
            cycleMap.put(cycle.charAt(i), cycle.charAt(i + 1));
        }
        cycleMap.put(cycle.charAt(cycle.length() - 1), cycle.charAt(0));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int index = wrap(p);
        char a = permute(alphabet().toChar(index));
        if (!alphabet().contains(a)) {
            throw new EnigmaException(a + " is not in the alphabet. ");
        }
        return alphabet().toInt(a);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!alphabet().contains(p)) {
            throw new EnigmaException(p + " is not in the alphabet.");
        }
        return cycleMap.getOrDefault(p, p);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int index = wrap(c);
        char a = alphabet().toChar(index);
        char invertA = invert(a);
        return alphabet().toInt(invertA);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!alphabet().contains(c)) {
            throw new EnigmaException(c + " is not in the alphabet. ");
        }
        for (Map.Entry<Character, Character> element : cycleMap.entrySet()) {
            if (element.getValue() == c) {
                return element.getKey();
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (Character s : cycleMap.keySet()) {
            if (cycleMap.getOrDefault(s, s) == s) {
                return true;
            }
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** cycleMap of this permutation. */
    private HashMap<Character, Character> cycleMap = new HashMap<>();


    /** FIXME fixed : ADDITIONAL FIELDS HERE, AS NEEDED */


    /**
    public static void main(String[] args) {
        String cycles = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        String cycles = "";
        Alphabet alphabet = new Alphabet();
        Permutation a = new Permutation(cycles, alphabet);
    } */
}
