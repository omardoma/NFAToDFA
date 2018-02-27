import autamatons.DFA;
import exceptions.InvalidInputException;
import states.State;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TestDFA {
    public static final int INPUT_DIVISIONS = 6;
    public static final String PRIMARY_INPUT_SPLIT = ",";
    public static final String SECONDARY_INPUT_SPLIT = "#";

    public static ArrayList<String> readInputFile(String filePath) throws IOException {
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        fr.close();
        br.close();
        return lines;
    }

    private static State findState(String name, Set<State> states) {
        for (State state : states) {
            if (state.getName().equals(name)) {
                return state;
            }
        }
        return null;
    }


    public static void testInput(String[] input) {
        Set<State> states = new LinkedHashSet<>();
        for (String s : input[0].split(PRIMARY_INPUT_SPLIT)) {
            states.add(new State(s));
        }

        Set<String> acceptStates = new HashSet<>(Arrays.asList(input[1].split(PRIMARY_INPUT_SPLIT)));

        Set<String> alphabet = new LinkedHashSet<>(Arrays.asList(input[2].split(PRIMARY_INPUT_SPLIT)));

        State startState = findState(input[3], states);
        startState = (startState != null ? startState : new State(""));

        String[] inputs = input[5].split(SECONDARY_INPUT_SPLIT);

        try {
            String[] currentTransition;
            String[] transition;
            String[] transitions = input[4].split(SECONDARY_INPUT_SPLIT);
            Set<String> addedStates = new HashSet<>();
            State currentState;
            for (int i = 0; i < transitions.length; i++) {
                currentTransition = transitions[i].split(PRIMARY_INPUT_SPLIT);
                currentState = findState(currentTransition[0], states);
                if (!addedStates.contains(currentTransition[0])) {
                    for (int j = 0; j < transitions.length; j++) {
                        transition = transitions[j].split(PRIMARY_INPUT_SPLIT);
                        if (transition.length < 3) {
                            throw new Exception("Incomplete Transition " + transitions[j]);
                        }
                        if (transition[0].equals(currentTransition[0])) {
                            currentState.addTransition(transition[2], findState(transition[1], states));
                        }
                    }
                    addedStates.add(currentTransition[0]);
                }
            }

            DFA dfa = new DFA(states, acceptStates, startState, alphabet);
            System.out.println("DFA constructed");
            for (String currentInput : inputs) {
                try {
                    System.out.println(dfa.testString(currentInput) ? "Accepted" : "Rejected");
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            for (String currentInput : inputs) {
                String ignored = "Ignored";
                System.out.println(ignored);
            }        } finally {
            System.out.println();
        }
    }

    public static void main(String[] args) {
        try {
            ArrayList<String> lines = readInputFile("src/data/in.in");
            String[] input = new String[INPUT_DIVISIONS];
            int count;
            for (int i = 0; i < lines.size(); i++) {
                count = (i % (INPUT_DIVISIONS + 1));
                if (count != INPUT_DIVISIONS) {
                    input[count] = lines.get(i);
                } else {
                    testInput(input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
