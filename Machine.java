package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Lei Hao
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;

        if (numRotors > 1) {
            _numRotors = numRotors;
        } else {
            throw new EnigmaException(numRotors + " should be > 1");
        }

        if (pawls >= 0 && pawls < numRotors) {
            _pawls = pawls;
        } else {
            throw new EnigmaException("Invalid pawl: " + pawls  + ", "
                    + "Must between 0 and " + _numRotors);
        }
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return the number of rotors we use. */
    ArrayList<Rotor> usedRotors() {
        return _usedRotors;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors == null) {
            throw new EnigmaException("Your don't have a rotor.");
        }

        if (rotors.length > _allRotors.size() + 1) {
            throw new EnigmaException("Too many rotors!");
        }

//        // HashSet way to add it to the _usedRotors.
//        // when we need to load thousands or millions data,
//        // use HashSet is much faster than nest loop.
//        HashSet rotorNames = new HashSet(Arrays.asList(rotors));
//        for (Rotor r : _allRotors) {
//            if (rotorNames.contains(r.name())) {
//                _usedRotors.add(r);
//            }
//        }

        // nest loop way to add it to the _usedRotors.
        _usedRotors = new ArrayList<>();
        for (String s : rotors) {
            for (Rotor r : _allRotors) {
                if (r.name().equals(s)) {
                    _usedRotors.add(r);
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).
     *  n index rotor's set()  {setting = "1346"} */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Not enough setting for rotors.");
        } else {
            for (int i = 0; i < setting.length(); i++) {
                if (!_alphabet.contains(setting.charAt(i))) {
                    throw new EnigmaException("No character in the "
                            + "alphabet for "
                            + "this setting, please check again.");
                } else {
                    _usedRotors.get(i + 1).set(setting.charAt(i));
                }
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }


    /** Set the ring to used rotors.
     *  @param ringSeting = 'c' */
    void setRings(String ringSeting) {
        for (int i = 1; i < _usedRotors.size(); i++) {
            _usedRotors.get(i).setRing(ringSeting.charAt(i - 1));
        }
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine.
     *  iterate all the rotor, return the result. */
    int convert(int c) {
        doubleStepping();
        int out = _plugboard.permute(c);
        for (int i = _numRotors - 1; i >= 0; i--) {
            out = _usedRotors.get(i).convertForward(out);
        }
        for (int i = 1; i < _usedRotors.size(); i++) {
            out = _usedRotors.get(i).convertBackward(out);
        }
        out = _plugboard.permute(out);
        return out;
    }

    /** double stepping. */
    void doubleStepping() {
        Map<Integer, Boolean> rotate = new HashMap<Integer, Boolean>();
        int rotateIndex = 0;
        for (int i = numRotors() - 1; i > numRotors() - 1 - _pawls; i--) {
            if (i == _usedRotors.size() - 1) {
                rotate.put(rotateIndex, true);
            } else if (_usedRotors.get(i).rotates()
                    && _usedRotors.get(i + 1).atNotch()) {
                rotate.put(rotateIndex, true);
                rotate.put(rotateIndex - 1, true);
            }
            rotateIndex += 1;
        }
        int rotorIndex = numRotors() - 1;
        for (int i = 0; i < rotate.size(); i++) {
            if (rotate.get(i)) {
                _usedRotors.get(rotorIndex).advance();
            }
            rotorIndex -= 1;
        }
    }


    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly.
     *  "abcd"  */
    String convert(String msg) {
        String convered = "";
        for (int i = 0; i < msg.length(); i++) {
            if (_alphabet.contains(msg.charAt(i))) {
                char c = msg.charAt(i);
                int cIndex = _alphabet.toInt(c);
                int convCIndex = convert(cIndex);
                char toChar = _alphabet.toChar(convCIndex);
                convered += toChar;
            } else {
                throw new EnigmaException("Machine class, convert method, "
                        + "char not in the alphabet.");
            }
        }
        return convered;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** _numRotors. */
    private final int _numRotors;

    /** _pawls. */
    private final int _pawls;

    /** _allRotors. */
    private final Collection<Rotor> _allRotors;

    /** _plugboard. */
    private Permutation _plugboard;

    /** _usedRotors. */
    private ArrayList<Rotor> _usedRotors = new ArrayList<>();

    /** FIXME fixed : ADDITIONAL FIELDS HERE, IF NEEDED.*/

    /**
    public static void main(String[] args) {
        Alphabet alpha = new Alphabet();
        int numRotors = 2;
        int pawls = 1;

        String cycles = "AELTPHQXRU";
        Permutation p1 = new Permutation(cycles, alpha);

        Rotor a = new Rotor("Rotor I", p1);
        Rotor b = new Rotor("Rotor II", p1);
        Rotor c = new Rotor("Rotor III", p1);

        Collection<Rotor> allRotors = new ArrayList<>();
        allRotors.add(a);
        allRotors.add(b);
        allRotors.add(c);

        Machine machine = new Machine(alpha, numRotors, pawls,allRotors);
        machine.insertRotors(new String[]{"Rotor I", "Rotor II", "Rotor III"});
        machine.doubleStepping();
    }
     */
}
