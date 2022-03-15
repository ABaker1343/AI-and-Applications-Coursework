package SudokuSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SudokuSolver implements Runnable {

    // class representing a Sudoku board
    static class Board {
        int[][] values;
        ArrayList<int[]> lockedPositions;

        Board(int[][] values, ArrayList<int[]> lockedPositions){
            this.values = values; 
            this.lockedPositions = lockedPositions;
        }

        Board(int size, ArrayList<int[]>lockedPositions){
            this.lockedPositions = lockedPositions;
            this.values = new int[size][size];

        }

        /**
         * initializes random values to the sudoku board where each row will have one of each number
         */
        public void  initRandomValues(Board initialBoard){

            Random rand = new Random();

            for (int r = 0; r < values[0].length; r++){
                Integer[] ints = new Integer[]{1,2,3,4,5,6,7,8,9};
                ArrayList<Integer> remaining = new ArrayList<Integer>(Arrays.asList(ints));
                for (int c = 0; c < values.length; c++){
                    //make sure that we dont repeat any numbers in each row
                    if (containsCoor(new int[] {r,c})){
                        for (int i = 0; i < remaining.size(); i++){
                            if (remaining.get(i).intValue() == initialBoard.values[r][c]){
                                remaining.remove(i);
                            }
                        }
                        values[r][c] = initialBoard.values[r][c];
                    }
                }
                for (int c = 0; c < values.length; c++){
                    if (!containsCoor(new int[]{r,c})){
                        int index = rand.nextInt(remaining.size());
                        values[r][c] = remaining.get(index);
                        remaining.remove(index);
                    }
                }

                if (!remaining.isEmpty()){
                    System.out.println("failed to initialize board correctly");
                }
            }
        }

        /**
         * initializes values for the sudoku board that dont have any hueristic
         */
        public void initTrueRandomValues(){
            Random rand = new Random();

            for (int r = 0; r < values[0].length; r++){
                for (int c = 0; c < values.length; c++){
                    //make sure that we dont repeat any numbers in each row
                    if (containsCoor(new int[] {r,c})){
                        continue;
                    }

                    values[r][c] = rand.nextInt(9);
                }
            }
        }

        @Override
        public String toString(){
            String outString = "";

            for (int r = 0; r < values.length; r++){
                for (int c = 0; c < values.length; c++){
                    outString += values[r][c] + "|";
                }
                outString += "\n------------------\n";
            }

            return outString;
        }

        @Override
        public Board clone(){
            Board clone = new Board(9, lockedPositions);
            for (int r = 0; r < this.values.length; r++){
                for (int c = 0; c < this.values.length; c++){
                    clone.values[r][c] = this.values[r][c];
                }
            }

            return clone;
        }

        public boolean containsCoor(int[] arr){

            for (int i = 0; i < lockedPositions.size(); i++){
                if (lockedPositions.get(i)[0] == arr[0]){
                    if (lockedPositions.get(i)[1] == arr[1]){
                        return true;
                    }
                }
            }
    
            return false;
        }

    }




    ArrayList<int[]> lockedPositions;
    Board initialBoard;

    Board[] boardSpace;
    int numBoards;
    static volatile int maxEval;
    int localMaxEval; //this will be a max for this instance
    float evolutionPercentage; //what amount will be taken to evolve

    /**
     * constructor for SudokuSolver class
     * @param initialBoard
     * @param noBoards
     */
    SudokuSolver(int[][] initialBoard, int noBoards, float evolutionPercentage){

        //set initial values that will be used in solving the puzzle

        //set the initial locked positions
        //these are the positions that are specified by the puzzle
        lockedPositions = new ArrayList<>();

        numBoards = noBoards;

        for (int r = 0; r < initialBoard[0].length; r++){
            for(int c = 0; c < initialBoard.length; c++){
                if (initialBoard[r][c] != 0){
                    lockedPositions.add(new int []{r,c});
                }
            }
        }

        //create the initial board, this is just the start state of the board
        this.initialBoard = new Board(initialBoard, lockedPositions);
        this.evolutionPercentage = evolutionPercentage;

        //create the initial batch of boards that will start the evolutions
        boardSpace = new Board[numBoards];
        for (int i = 0; i < boardSpace.length; i++){
            boardSpace[i] = new Board(9, lockedPositions);
        }
        
        for (Board b : boardSpace){
            //initialize
            b.initRandomValues(this.initialBoard);
        }

    }


    /**
     * runs the solver so that you come out with a result
     */
    public void run(){

        //create the array of boards that will contain the best of each generation
        Board[] boardsToEvolve = new Board[(int)(numBoards * evolutionPercentage)];
        for (int i = 0; i < boardsToEvolve.length; i++){
            boardsToEvolve[i] = boardSpace[i];
        }

        System.out.println("starting algorithm on thread: " + Thread.currentThread() + "\n");
        Board bestBoard = null;
        boolean improved = false;
        while (true){
            improved = false;
            for (Board b : boardSpace){
                int val = evaluate(b);
                if (val > localMaxEval){
                    //logic for printing the best board weve had
                    localMaxEval = val;
                    if (val > maxEval){
                        maxEval = val;
                        bestBoard = b.clone();
                        improved = true;
                    }
                }
                //replace the best boards with this board if its better
                for (int i = 0; i < boardsToEvolve.length; i++){
                    if (val >= evaluate(boardsToEvolve[i])){
                        int index = getLowestEvalIndex(boardsToEvolve);
                        boardsToEvolve[index] = b;
                        break;
                    }
                }
            }

            if (improved){
                System.out.println("Board rating: " + maxEval + "/27");
                System.out.println(bestBoard);
            }

            boardSpace = evolve(boardsToEvolve);

            //check if any of the new boards are solving the puzzle
            for (Board b : boardSpace){
                if (isBoardComplete(b)){
                    System.out.println("SOLUTION FOUND");
                    System.out.println(b);
                    return;
                }
            }

            if(Thread.interrupted()){
                break;
            }

        }
    }
    
    /**
     * function to evaluate a board and return a score
     * @param board
     * @return
     */
    private int evaluate(Board board){
        int evaluation = 0;
        //we can evaluate the board by returning how many
        //rows and columns are legal

        for (int r = 0; r < board.values[0].length; r++){
            Integer[] ints = {1,2,3,4,5,6,7,8,9};
            ArrayList<Integer> remaining = new ArrayList<>(Arrays.asList(ints));

            
            for (int c = 0; c < board.values.length; c++){
                Integer i = board.values[r][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
            }

            if (remaining.isEmpty()){
                evaluation ++;
            }
        }

        //check y direction

        for (int c = 0; c < board.values[0].length; c++){
            Integer[] ints = {1,2,3,4,5,6,7,8,9};
            ArrayList<Integer> remaining = new ArrayList<>(Arrays.asList(ints));

            
            for (int r = 0; r < board.values.length; r++){
                Integer i = board.values[r][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
            }

            if (remaining.isEmpty()){
                evaluation ++;
            }
        }

        //check squares

        for (int r = 0; r < board.values.length; r+=3){
            Integer[] ints = new Integer[]{1,2,3,4,5,6,7,8,9};
            ArrayList<Integer> remaining = new ArrayList<Integer>(Arrays.asList(ints));

            for (int c = 0; c < 3; c+=1){
                Integer i = board.values[r][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
                i = board.values[r+1][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
                i = board.values[r+2][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
            }

            if (remaining.isEmpty()){
                evaluation++;
            }

            remaining = new ArrayList<Integer>(Arrays.asList(ints));

            for (int c = 3; c < 6; c+=1){
                Integer i = board.values[r][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
                i = board.values[r+1][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
                i = board.values[r+2][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
            }

            if (remaining.isEmpty()){
                evaluation++;
            }

            remaining = new ArrayList<Integer>(Arrays.asList(ints));

            for (int c = 6; c < 9; c+=1){
                Integer i = board.values[r][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
                i = board.values[r+1][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
                i = board.values[r+2][c];
                if (remaining.contains(i)){
                    remaining.remove(i);
                }
            }

            if (remaining.isEmpty()){
                evaluation++;
            }
        }

        return evaluation;
    }


    /**
     * takes an array of boards and evolves it into a new array of boards
     * @param boardSpace
     * @return
     */
    private Board[] evolve(Board[] boardSpace){
        //as big as the boards given to it

        Random rand = new Random();

        Board[] returnBoards = new Board[(int)(boardSpace.length / this.evolutionPercentage)];

        // make the first section the same as the best boards
        for (int i = 0; i < boardSpace.length; i++){
            returnBoards[i] = boardSpace[i];
        }
        
        // corossover to make some new boards
        for (int i = boardSpace.length; i < returnBoards.length; i++){
            int evolNum = rand.nextInt(4);
            Board[] newBoards;
            Board newBoard;

            switch(evolNum){
                case 0:
                    newBoards = getVerticalSlices(boardSpace[rand.nextInt(boardSpace.length)], boardSpace[rand.nextInt(boardSpace.length)]);
                    returnBoards[i] = newBoards[0];
                    i++;
                    if (! (i < returnBoards.length )) break;
                    returnBoards[i] = newBoards[1];
                    break;
                case 1:
                    newBoards = getHorizontalSlices(boardSpace[rand.nextInt(boardSpace.length)], boardSpace[rand.nextInt(boardSpace.length)]);
                    returnBoards[i] = newBoards[0];
                    i++;
                    if (! (i < returnBoards.length )) break;
                    returnBoards[i] = newBoards[1];
                    break;
                case 2:
                    newBoard = ShuffleLinesX(boardSpace[rand.nextInt(boardSpace.length)], boardSpace[rand.nextInt(boardSpace.length)]);
                    returnBoards[i] = newBoard;
                    break;
                case 3:
                    newBoard = ShuffleLinesY(boardSpace[rand.nextInt(boardSpace.length)], boardSpace[rand.nextInt(boardSpace.length)]);
                    returnBoards[i] = newBoard;
                    break;
                default:
                    break;
            }
        }

        //mutate the new boards

        for (int i = boardSpace.length; i < returnBoards.length; i++){
            
            int evolNum;

            if (localMaxEval == 25) evolNum = rand.nextInt(3) + 3; //when it reaches 25 right only do swaps
            else evolNum = rand.nextInt(6); //if its not at 25 then shift aswell

            switch(evolNum){
                case 0:
                    //dont evolve
                    break;
                case 1:
                    returnBoards[i] = MutateBoardRows(returnBoards[rand.nextInt(returnBoards.length)]);
                    break;
                case 2:
                    returnBoards[i] = MutateBoardCols(returnBoards[rand.nextInt(returnBoards.length)]);
                    break;
                case 3: case 4:
                    //swaps on rows seem to be the best of the mutations so we will make
                    //them more common
                    returnBoards[i] = mutateSwapsInRow(returnBoards[rand.nextInt(returnBoards.length)]);
                    break;
                case 5:
                    returnBoards[i] = mutateSwapsInCol(returnBoards[rand.nextInt(returnBoards.length)]);
                    break;
                // case 6:
                //     returnBoards[i] = mutateSwap2Random(returnBoards[rand.nextInt(returnBoards.length)]);
                default:
                    //dont mutate
                    break;
            }
        }

        return returnBoards;
    }

    /**
     * slices a board vertically so that half comes from one parent board and the other half comes
     * from the other parent board
     * @param b1
     * @param b2
     * @return
     */
    private Board[] getVerticalSlices(Board b1, Board b2){
        
        Board[] returnBoards = new Board[2];
        for (int i = 0; i < returnBoards.length; i++){
            returnBoards[i] = new Board(9, lockedPositions);
        }

        for (int r = 0; r < b1.values.length; r++){
            for (int c = 0; c < b1.values.length; c++){
                if (r < b1.values.length / 2){
                    returnBoards[0].values[r][c] = b1.values[r][c];
                    returnBoards[1].values[r][c] = b2.values[r][c];
                }
                else{
                    returnBoards[1].values[r][c] = b1.values[r][c];
                    returnBoards[0].values[r][c] = b2.values[r][c];
                }
            }
        }

        return returnBoards;

    }


    /**
     * creates a new board based on two parents
     * splits the board in half horizontally so that half the board comes from one parent
     * and the other half comes from the other parent
     * @param b1
     * @param b2
     * @return
     */
    private Board[] getHorizontalSlices(Board b1, Board b2){
                Board[] returnBoards = new Board[2];
        for (int i = 0; i < returnBoards.length; i++){
            returnBoards[i] = new Board(9, lockedPositions);
        }

        for (int r = 0; r < b1.values.length; r++){
            for (int c = 0; c < b1.values.length; c++){
                if (c < b1.values.length / 2){
                    returnBoards[0].values[r][c] = b1.values[r][c];
                    returnBoards[1].values[r][c] = b2.values[r][c];
                }
                else{
                    returnBoards[1].values[r][c] = b1.values[r][c];
                    returnBoards[0].values[r][c] = b2.values[r][c];
                }
            }
        }

        return returnBoards;
    }

    /**
     * crossover function that randomly takes rows from either the first or second parent board
     */
    private Board ShuffleLinesX(Board b1, Board b2){
        Board returnBoard = new Board(9, lockedPositions);
        Random rand = new Random();

        for (int row = 0; row < returnBoard.values.length; row++){
            if (rand.nextInt(2) == 0){
                returnBoard.values[row] = b1.values[row];
            }
            else{
                returnBoard.values[row] = b2.values[row];
            }
        }

        return returnBoard;
    }

    /**
     * crossover function that takes columns randomly from either the first or second parent board
     * @param b1
     * @param b2
     * @return
     */
    private Board ShuffleLinesY(Board b1, Board b2){
        Board returnBoard = new Board(9, lockedPositions);
        Random rand = new Random();

        for (int c = 0; c < returnBoard.values[0].length; c++){
            Board pullFrom = b1;
            if (rand.nextInt(2) == 0) { pullFrom = b2;}
            for (int r = 0; r < returnBoard.values.length; r++){
                returnBoard.values[r][c] = pullFrom.values[r][c];
            }
        }

        return returnBoard;
    }

    /**
     * mutation funtion that shifts all the numbers except the starting ones along by random amounts
     * per row
     * @param b
     * @return
     */
    private Board MutateBoardRows(Board b){
        Board mutation = new Board(9, lockedPositions);
        Random rand = new Random();
        //shift rows
        for (int r = 0; r < b.values.length; r++){
            int shiftAmount = rand.nextInt(9);
            for (int c = 0; c < b.values.length; c++){
                if (c + shiftAmount > 8){
                    int overshoot = (c + shiftAmount) - 8;
                    mutation.values[r][overshoot] = b.values[r][c];
                }
                else {
                    mutation.values[r][c] = b.values[r][c];
                }
            }
            //put back the locked positions
            //find wheere they were replaced
            ArrayList<Integer[]> lockedTaken = new ArrayList<Integer[]>();
            for (int c = 0; c < b.values.length; c++){
                if (containsCoor(new int[]{r,c})){
                    if (mutation.values[r][c] == b.values[r][c]){
                        continue;
                    }
                    lockedTaken.add(new Integer[]{r,c});
                }
            }
            //put them back and store what replaced them
            ArrayList<Integer> removed = new ArrayList<Integer>();
            ArrayList<Integer> inserted = new ArrayList<Integer>();
            for (Integer[] taken : lockedTaken){
                removed.add(mutation.values[taken[0]][taken[1]]);
                mutation.values[taken[0]][taken[1]] = b.values[taken[0]][taken[1]];
                inserted.add(mutation.values[taken[0]][taken[1]]);
            }
            // fill back in the replaced numbers
            for (int c = 0; c < b.values.length; c++){
                if (inserted.contains(mutation.values[r][c])){
                    if(containsCoor(new int[]{r, c})){
                        continue;
                    }
                    int index = rand.nextInt(removed.size());
                    mutation.values[r][c] = removed.get(index);
                    removed.remove(index);
                    inserted.remove(index);
                }
            }
        }

        return mutation;
    }

     /**
     * mutation funtion that shifts all the numbers except the starting ones along by random amounts
     * per row
     * @param b
     * @return
     */
    private Board MutateBoardCols(Board b){
        Board mutation = new Board(9, lockedPositions);
        Random rand = new Random();
        //shift rows
        for (int c = 0; c < b.values.length; c++){
            int shiftAmount = rand.nextInt(9);
            for (int r = 0; r < b.values.length; r++){
                if (r + shiftAmount > 8){
                    int overshoot = (r + shiftAmount) - 8;
                    mutation.values[overshoot][c] = b.values[r][c];
                }
                else {
                    mutation.values[r][c] = b.values[r][c];
                }
            }
            //put back the locked positions
            //find wheere they were replaced
            ArrayList<Integer[]> lockedTaken = new ArrayList<Integer[]>();
            for (int r = 0; r < b.values.length; r++){
                if (containsCoor(new int[]{r,c})){
                    if (mutation.values[r][c] == b.values[r][c]){
                        continue;
                    }
                    lockedTaken.add(new Integer[]{r,c});
                }
            }
            //put them back and store what replaced them
            ArrayList<Integer> removed = new ArrayList<Integer>();
            ArrayList<Integer> inserted = new ArrayList<Integer>();
            for (Integer[] taken : lockedTaken){
                removed.add(mutation.values[taken[0]][taken[1]]);
                mutation.values[taken[0]][taken[1]] = b.values[taken[0]][taken[1]];
                inserted.add(mutation.values[taken[0]][taken[1]]);
            }
            // fill back in the replaced numbers
            for (int r = 0; r < b.values.length; r++){
                if (inserted.contains(mutation.values[r][c])){
                    if(containsCoor(new int[]{r, c})){
                        continue;
                    }
                    int index = rand.nextInt(removed.size());
                    mutation.values[r][c] = removed.get(index);
                    removed.remove(index);
                    inserted.remove(index);
                }
            }
        }

        return mutation;
    }
    
    /**
     * mutation function that swaps a random number from a random row with another number from that row
     */
    private Board mutateSwapsInRow(Board b){
        Board mutation = new Board(9, lockedPositions);
        Random rand = new Random();
        mutation = b;

        for (int i = 0; i < rand.nextInt(9); i++){
            //make a swap

            int r = rand.nextInt(9);

            boolean swapValid = false;
            int firstIndex = 0;
            int secondIndex = 0;
            while (!swapValid){
                firstIndex = rand.nextInt(9);
                secondIndex = rand.nextInt(9);
                if (! (containsCoor(new int[]{r, firstIndex}) 
                || containsCoor(new int[]{r,secondIndex}))){
                    swapValid = true;
                }

            }
            int toSwap = mutation.values[r][firstIndex];
            mutation.values[r][firstIndex] = mutation.values[r][secondIndex];
            mutation.values[r][secondIndex] = toSwap;
        }

        return mutation;
    }

        
    /**
     * mutation function that swaps a random number from a random column with another number from that
     * column
     */
    private Board mutateSwapsInCol(Board b){
        Board mutation = new Board(9, lockedPositions);
        Random rand = new Random();
        mutation = b;

        for (int i = 0; i < rand.nextInt(9); i++){

            int c = rand.nextInt(9);
            //make a swap
            boolean swapValid = false;
            int firstIndex = 0;
            int secondIndex = 0;
            while (!swapValid){
                firstIndex = rand.nextInt(9);
                secondIndex = rand.nextInt(9);
                if (! (containsCoor(new int[]{firstIndex, c}) 
                || containsCoor(new int[]{secondIndex, c}))){
                    swapValid = true;
                }

            }
            int toSwap = mutation.values[firstIndex][c];
            mutation.values[firstIndex][c] = mutation.values[secondIndex][c];
            mutation.values[secondIndex][c] = toSwap;
        }

        return mutation;
    }

    /**
     * mutation function that will swap 2 random points on the board 
     * @param b
     * @return
     */
    private Board mutateSwap2Random(Board b){
        Board mutation = new Board(9, lockedPositions);
        Random rand = new Random();
        mutation = b;

        for (int i = 0; i < rand.nextInt(9); i++){

            int c = rand.nextInt(9);
            //make a swap
            boolean swapValid = false;
            int firstIndexR = 0;
            int secondIndexR = 0;
            int firstIndexC = 0;
            int secondIndexC = 0;
            while (!swapValid){
                firstIndexR = rand.nextInt(9);
                secondIndexR = rand.nextInt(9);
                firstIndexC = rand.nextInt(9);
                secondIndexC = rand.nextInt(9);
                if (! (containsCoor(new int[]{firstIndexR, firstIndexC}) 
                || containsCoor(new int[]{secondIndexR, secondIndexC}))){
                    swapValid = true;
                }

            }
            int toSwap = mutation.values[firstIndexR][firstIndexC];
            mutation.values[firstIndexR][firstIndexC] = mutation.values[secondIndexR][secondIndexC];
            mutation.values[secondIndexR][secondIndexC] = toSwap;
        }

        return mutation;
    }

    /**
     * function that returns the index of the lowest evaluated board in an array of boards
     * @param boards
     * @return
     */
    private int getLowestEvalIndex(Board[] boards){
        int lowestEval = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < boards.length; i++){
            if (evaluate(boards[i]) < lowestEval){
                lowestEval = evaluate(boards[i]);
                index = i;
            }
        }
        return index;
    }

    /**
     * function that returns the index of the highest evaluated board in an array of boards
     * @param boards
     * @return
     */
    private int getHighestEvalIndex(Board[] boards){
        int highestEval = -1;
        int index = 0;
        for (int i = 0; i < boards.length; i++){
            if (evaluate(boards[i]) > highestEval){
                highestEval = evaluate(boards[i]);
                index = i;
            }
        }
        return index;
    }

    /**
     * function that checks if a board is complete this means solved
     * @param board
     * @return
     */
    private boolean isBoardComplete(Board board){
        if (evaluate(board) == 27){
            return true;
        }
        return false;
    }

    public boolean containsCoor(int[] arr){

        for (int i = 0; i < lockedPositions.size(); i++){
            if (lockedPositions.get(i)[0] == arr[0]){
                if (lockedPositions.get(i)[1] == arr[1]){
                    return true;
                }
            }
        }

        return false;
    }

}
