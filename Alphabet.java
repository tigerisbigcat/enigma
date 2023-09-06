package enigma;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Lei Hao
 */
class Alphabet {
    /** An alphabet of encodable characters.  Provides a mapping from characters
     *  to and from indices into the alphabet. */

    private String charList;

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        charList = chars;
        char[] errArray = {' ', '*', ',', '/', '(', ')'};

        for (int i = 0; i < errArray.length; i++) {
            char a = errArray[i];
            if (contains(a)) {
                throw new EnigmaException("Invalid character: " + a
                        + " in the provided alphabet.");
            }
        }

        LinkedList<Integer> charIndex = new LinkedList<>();
        Map<Character, LinkedList> dict = new HashMap<Character, LinkedList>();
        for (int i = 0; i < size(); i++) {
            char key = charList.charAt(i);
            int index = charList.indexOf(key, i);
            if (!dict.containsKey(key)) {
                charIndex.add(index);
                dict.put(key, charIndex);
            } else {
                throw new EnigmaException("Duplicated character " + key
                        + " in the provided alphabet.");
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return charList.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < size(); i++) {
            if (charList.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (0 <= index && index < size()) {
            return charList.charAt(index);
        } else {
            throw new EnigmaException("Alphabet index not exist!");
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int index = charList.indexOf(ch);
        if (index < 0 || index > size()) {
            throw new EnigmaException("No this char in Alphabet!");
        }
        return index;
    }

    /**
    public static void main(String[] args) {
        String chars  = "ABCDEFGHI";
        Alphabet a = new Alphabet(chars);
        Alphabet b = new Alphabet();
        char ch = 'f';
        System.out.println(a.toInt(ch));
        System.out.println(b.toInt(ch));
        System.out.println();

        int i = 2;
        System.out.println(a.toChar(i));
        System.out.println(b.toChar(i));
        System.out.println();

        System.out.println(a.size());
        System.out.println(b.size());
        System.out.println();

        System.out.println(a.contains(ch));
        System.out.println(b.contains(ch));
    }*/
}




