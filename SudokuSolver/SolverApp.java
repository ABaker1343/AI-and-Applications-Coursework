package SudokuSolver;

import java.io.FileReader;
import java.util.ArrayList;

public class SolverApp {
    
    public static void main(String args[]){

        String filepath = args[0];
        ArrayList<Character> charBuffer = new ArrayList<Character>();

        try (FileReader fr = new FileReader(filepath)){
            int newChar = -1;
            while((newChar = fr.read()) != -1){
                if ( Character.isDigit( (char)newChar ) || (char)newChar == '.'){
                    charBuffer.add((char)newChar);
                }
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        int[][] startBoard = new int[9][9];

        for (int r = 0; r < 9; r++){
            for (int c = 0; c < 9; c++){
                if (charBuffer.get(r+c) == '.'){
                    startBoard[r][c] = 0;
                }
                else {
                    startBoard[r][c] = charBuffer.get(r+c) - '0'; // - character 0 to get numerical value
                }
            }
        }

        // startBoard = {
        //     {5,3,0,0,7,0,0,0,0},
        //     {6,0,0,1,9,5,0,0,0},
        //     {0,9,8,0,0,0,0,6,0},
        //     {8,0,0,0,6,0,0,0,3},
        //     {4,0,0,8,0,3,0,0,1},
        //     {7,0,0,0,2,0,0,0,6},
        //     {0,6,0,0,0,0,2,8,0},
        //     {0,0,0,4,1,9,0,0,5},
        //     {0,0,0,0,8,0,0,7,9}
        // };

        // SudokuSolver solver = new SudokuSolver(startBoard, 1000);

        // solver.run();

        final int solverCount = 8;
        SudokuSolver[] solvers = new SudokuSolver[solverCount];
        Thread[] solverThreads = new Thread[solverCount];

        for (int i = 0; i < solverCount; i++){
            solvers[i] = new SudokuSolver(startBoard, 1000);
            solverThreads[i] = new Thread(solvers[i]);
            solverThreads[i].start();
        }

        for (int i = 0; i < solverCount; i++){
            if(!solverThreads[i].isAlive()){
                //thread finished and answer found
                for (int j = 0; j < solverCount; j++){
                    if(j!=i){
                        solverThreads[j].interrupt();
                    }
                }
                break;
            }
        }

    }

}
