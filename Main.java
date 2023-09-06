package enigma;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Lei Hao
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output.
     *  * B Beta I II III AAAA
     *  */
    private void process() {
        /** FIXME fixed */
        Machine m = readConfig();

        while (_input.hasNextLine()) {
            String convert = _input.nextLine();
            if (convert.contains("*")) {
                setUp(m, convert);
            } else {
                if (m.usedRotors().isEmpty()) {
                    System.exit(1);
                }
                printMessageLine(m.convert(convert.
                        replaceAll(" ", "")));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numRotors;
            int pawls;
            String charater = _config.next().trim();
            _alphabet = new Alphabet(charater);

            if (_config.hasNextInt()) {
                numRotors = _config.nextInt();
            } else {
                throw new EnigmaException("Config file error, couldn't "
                        + "set the numRotors.");
            }

            if (_config.hasNextInt()) {
                pawls = _config.nextInt();
                _config.nextLine();
            } else {
                throw new EnigmaException("Cofig file error, "
                        + "couldn't set the pawls.");
            }

            while (_config.hasNextLine()) {
                _configList.add(_config.nextLine().trim());
            }

            for (int i = 0; i < _configList.size(); i++) {
                if (_configList.get(i).startsWith("(")) {
                    _configList.set(i - 1, _configList.get(i - 1)
                            + _configList.get(i));
                    _configList.remove(i);
                }
            }

            for (int i = 0; i < _configList.size(); i++) {
                if (_configList.get(i).isBlank()) {
                    continue;
                }
                String[] s = _configList.get(i).split(" ", 3);

                // just for check which line is empty, for debugging.
                if (s.length < 3) {
                    throw new EnigmaException("\"" + _configList.get(i)
                            + "\"" + i + " input is not correct. ");
                }

                _rotorName = s[0];
                _rotorConfig = s[1];
                _cycles = s[2];
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            if (_rotorConfig.charAt(0) == 'M') {
                String notch = _rotorConfig.substring(1);
                return new MovingRotor(_rotorName,
                        new Permutation(_cycles, _alphabet), notch);
            } else if (_rotorConfig.charAt(0) == 'N') {
                return new FixedRotor(_rotorName,
                        new Permutation(_cycles, _alphabet));
            } else if (_rotorConfig.charAt(0) == 'R') {
                return new Reflector(_rotorName,
                        new Permutation(_cycles, _alphabet));
            } else {
                throw new EnigmaException("readRotor failed. debug me.");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings.charAt(0) != '*') {
            throw error("Should start with '*'. ");
        }

        String cycle = "";
        int usedRotor = M.numRotors();
        String setting = settings.replace("*", "").trim();
        String[] settingList = setting.split(" ");
        String[] rotorList = new String[usedRotor + 1];

        for (int i = 0; i < settingList.length; i++) {
            if (i <= usedRotor) {
                rotorList[i] = settingList[i];
            } else if (i > usedRotor) {
                cycle += settingList[i];
            }
        }

        for (int i = 0; i < rotorList.length; i++) {
            for (int j = i + 1; j < rotorList.length; j++) {
                if (rotorList[i].equals(rotorList[j])) {
                    throw new EnigmaException("Duplicate rotors!");
                }
            }
        }

        M.insertRotors(rotorList);
        if (!M.usedRotors().get(0).reflecting()) {
            throw new EnigmaException("First rotor should be the reflector.");
        }
        for (int i = 0; i < usedRotor - M.numPawls(); i++) {
            if (M.usedRotors().get(i).rotates()) {
                throw new EnigmaException("Wrong number of arguments!");
            }
        }

        M.setRotors(settingList[usedRotor]);
        try {
            if (!settingList[1 + usedRotor + 1].contains("(")) {
                String ringSetting = settingList[1 + usedRotor + 1];
                if (ringSetting.length() > 0) {
                    M.setRings(ringSetting);
                }
            }
            M.setPlugboard(new Permutation(cycle, _alphabet));
        } catch (ArrayIndexOutOfBoundsException ignored) {
            M.setPlugboard(new Permutation("", _alphabet));
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        ArrayList<String> main = new ArrayList<>();
        int mlen = msg.length();
        for (int i = 0; i < mlen; i += 5) {
            main.add(msg.substring(i, Math.min(mlen, i + 5)));
        }

        String s = main.toString();
        s = s.replaceAll("\\[", "");
        s = s.replaceAll(", ", " ");
        s = s.replaceAll("\\]", "");

        _output.println(s);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** _configList. */
    private ArrayList<String> _configList = new ArrayList<>();

    /** _rotorName. */
    private String _rotorName;

    /** _rotorConfig. */
    private String _rotorConfig;

    /** _cycles. */
    private String _cycles;

    /** _allRotors. */
    private Collection<Rotor> _allRotors = new ArrayList<>();
}
