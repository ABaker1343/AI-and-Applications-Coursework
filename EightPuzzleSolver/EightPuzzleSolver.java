package EightPuzzleSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class EightPuzzleSolver {
    public static void main(String args[]){

        Scanner sc = new Scanner(System.in);

        int[] startState = getUserStartState(sc);
        int[] goalState = getUserGoalState(sc);
        int algoChoice = getUserChoice(sc);

        sc.close();
        //int[] startState = {7,2,4,5,0,6,8,3,1};
        //int[] startState = {1,0,2,3,4,5,6,7,8};
        try {

            PuzzleGraph pg = new PuzzleGraph(startState, goalState);

            switch(algoChoice){
                case 1:
                    pg = new PuzzleGraphOutOfPlace(startState, goalState);
                    break;
                case 2:
                    pg = new PuzzleGraphDistance(startState, goalState);
                    break;
                default:
                    System.out.println("invalid choice of algorith, please enter 1/2");
                    System.exit(-1);
            }

            long startTime = System.currentTimeMillis();
            PuzzleGraph.Node finalNode = pg.run();
            long endTime = System.currentTimeMillis();

            long execTime = endTime - startTime;

            PuzzleGraph.printNodeTrace(finalNode);

            System.out.println("search finished in " + execTime + " miliseconds");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static int getUserChoice(Scanner sc){

        System.out.println("what evaluation function would you like to use?");
        System.out.println("1 - number of wrong tiles in the board");
        System.out.println("2 - total moves to get each tile to its final point");

        int choice = sc.nextInt();

        return choice;
    }

    public static int[] getUserStartState(Scanner sc){

        System.out.println("input the start state from top left " +
        "to bottom right reading along each row, "+
        "enter the numbers one at a time using 0 for the blank space");

        int[] startState = new int[9];

        for (int i = 0; i < 9; i++){
            startState[i] = sc.nextInt();
        }

        return startState;
    }

    public static int[] getUserGoalState(Scanner sc){
        System.out.println("would you like to use a custom goal state");

        if(!sc.hasNext()) sc.nextLine();
        String answer = sc.next();
        answer = answer.toLowerCase();

        int[] goalState = new int[9];;

        if (!(answer.equals("yes") || answer.equals("y"))){
            return new int[]{0,1,2,3,4,5,6,7,8};
        }

        System.out.println("input the start state from top left " +
        "to bottom right reading along each row, "+
        "enter the numbers one at a time using 0 for the blank space");

        for (int i = 0; i < 9; i++){
            goalState[i] = sc.nextInt();
        }

        return goalState;
    }


}