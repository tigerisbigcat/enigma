package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Lei Hao
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        rotorNotch = notches.split("");
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        for (String s : rotorNotch) {
            if (permutation().alphabet().toChar(setting()) == s.charAt(0)) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(setting() + 1);
    }

    /** FIXME fixed : ADDITIONAL FIELDS HERE, AS NEEDED. */
    private final String[] rotorNotch;

}
