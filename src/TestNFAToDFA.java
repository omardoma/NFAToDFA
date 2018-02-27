import autamatons.DFA;
import autamatons.NFA;
import exceptions.InvalidInputException;
import states.State;
import transtitions.Transition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TestNFAToDFA {
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

    private static void printEquivalentDFA(DFA dfa, String inputs) {
        String result = "";

        // Print States
        for (State state : dfa.getStates()) {
            result += state.toString() + ",";
        }
        System.out.println(result.substring(0, result.length() - 1));

        // Print Accept States
        result = "";
        for (String state : dfa.getAcceptStates()) {
            result += state + ",";
        }
        System.out.print(result.equals("") ? result : result.substring(0, result.length() - 1));

        // Print Alphabet
        result = "";
        for (String alpha : dfa.getAlphabet()) {
            result += alpha + ",";
        }
        System.out.println(result.substring(0, result.length() - 1));

        // Print start state
        System.out.println(dfa.getStartState());

        // Print transitions
        result = "";
        for (State state : dfa.getStates()) {
            for (Transition transition : state.getTransitions()) {
                result += state + "," + transition + "#";
            }
        }
        System.out.println(result.substring(0, result.length() - 1));

        // Print Inputs
        System.out.println(inputs);
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
            State currentState, nextState;
            boolean error = false;
            for (int i = 0; i < transitions.length; i++) {
                currentTransition = transitions[i].split(PRIMARY_INPUT_SPLIT);
                if (!addedStates.contains(currentTransition[0])) {
                    currentState = findState(currentTransition[0], states);
                    if (currentState != null) {
                        for (int j = 0; j < transitions.length; j++) {
                            transition = transitions[j].split(PRIMARY_INPUT_SPLIT);
                            if (transition.length < 3) {
                                System.out.print("\nIncomplete Transition " + transitions[j]);
                                break;
                            }
                            if (transition[0].equals(currentTransition[0])) {
                                nextState = findState(transition[1], states);
                                if (nextState == null) {
                                    error = true;
                                    System.out.print("\nInvalid transition " + transitions[j] + " state " + currentTransition[1] + " does not exist");
                                } else {
                                    currentState.addTransition(transition[2], nextState);
                                }
                            }
                        }
                        addedStates.add(currentTransition[0]);
                    } else {
                        error = true;
                        System.out.print("\nInvalid transition " + transitions[i] + " state " + currentTransition[0] + " does not exist");
                    }
                }
            }

            if (error) {
                throw new Exception("");
            }

            NFA nfa = new NFA(states, acceptStates, startState, alphabet);
            System.out.println("NFA constructed");

            DFA dfa = nfa.getEquivalentDFA();
            System.out.println("Equivalent DFA:");
            printEquivalentDFA(dfa, input[5]);

            // run dfa on input
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
            System.out.println("DFA Construction skipped and inputs are ignored");
        } finally {
            System.out.println();
        }
    }

    public static void main(String[] args) {
        try {
            ArrayList<String> lines = readInputFile("src/data/in2.in");
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
            System.out.println(e.getMessage());
        }
    }
}
