package EightPuzzleSolver;

import java.util.ArrayList;
import java.util.Arrays;

class PuzzleGraph {

    static class Node{
        int[] value;
        Node prev;
        int zeroIndex;

        @Override
        public String toString(){
            String out = "";
            for (int i = 0; i < value.length; i++){
                out += value[i] + " ";
                if (i%3 == 2) out += "\n";
            }
            return out;
        }

        @Override
        public boolean equals(Object o){
            if (o == this) return true;
            if (! (o instanceof Node)) return false;
            Node no = (Node)o;
            return Arrays.equals(this.value, no.value);
        }
    }


    //in the searchspace the empty space is represented by 0
    protected ArrayList<Node> searchSpace;

    //the goal node is always the same so we can set that here
    protected Node goalNode;

    protected Node initialNode;

    PuzzleGraph(int[] initialNode, int[] goalNode) throws Exception{

        if (!checkValidNode(initialNode) || !checkValidNode(goalNode)){
            throw new Exception("initial state is not a valid state");
        }

        this.goalNode = new Node();
        this.goalNode.value = goalNode;
        this.goalNode.prev = null;
        this.goalNode.zeroIndex = getZeroIndex(this.goalNode);

        searchSpace = new ArrayList<>();

        this.initialNode = new Node();
        this.initialNode.value = initialNode;
        this.initialNode.prev = null;
        this.initialNode.zeroIndex = getZeroIndex(this.initialNode);

        searchSpace.add(this.initialNode);
    }

    /**
     * starts the search
     * @param initialNode
     */
    public Node run() throws Exception{

        Node next = initialNode;
        while (!Arrays.equals(next.value, goalNode.value)){
            next = getNextMove();
            searchSpace.add(next);
        }
        //once we are he we should be at the goal state

        return searchSpace.get(searchSpace.size() - 1);

    }

    public static String nodeToString(Node node){
        String out = "";
        for (int i : node.value){
            out += i;
        }
        return out;
    }

    /**
     * gets the next best node and adds it to the serach space
     * @return
     */
    public Node getNextMove() throws Exception{
        //we want to swap a number with the 0 to get the best move
        
        int bestMoveIndex = -1;
        int bestNodeMove = -1;
        int bestMoveValue = Integer.MAX_VALUE; //as close to infinity as we can get
        int moveValue = 1;

        //for each possible swap with 0 choose the best one
        
        for (int outer = 0; outer < searchSpace.size(); outer++){
            Node n = searchSpace.get(outer);
            //swap check all swaps with 0
            for (int swapIndex : getValidSwaps(n.zeroIndex)) {
                //get the value of swapping that index with 0
                Node newNode;
                if (!nodeSearched((newNode = swapWithZero(n, swapIndex)))){
                    //make sure that node hasnt already been searched
                    //evaluation + trace length because we want to favour nodes with less depth
                    if ( ( moveValue = evaluate( newNode ) + getTraceLength(n)) < bestMoveValue){
                        bestMoveIndex = swapIndex;
                        bestNodeMove = outer;
                        bestMoveValue = moveValue;
                    }
                }
            }
        }

        if (bestNodeMove < 0 || bestMoveIndex < 0) {
            throw new Exception("no move found");
        }

        Node comingFrom = searchSpace.get(bestNodeMove);
        Node newNode = new Node();
        newNode = swapWithZero(searchSpace.get(bestNodeMove), bestMoveIndex);
        newNode.prev = comingFrom;
        newNode.zeroIndex = getZeroIndex(newNode);

        return newNode;

    }

    protected boolean nodeSearched(Node node){

        if (searchSpace.contains(node)){
            return true;
        }

        return false;

    }

    protected Node swapWithZero(Node node, int index) throws Exception{
        //create a copy that we can change as we want without chanign the search space
        Node tempNode = new Node();
        tempNode.value = node.value.clone();

        //find where the 0 is
        tempNode.zeroIndex = getZeroIndex(tempNode);

        //swap the 0 with the index and return node
        tempNode.value[tempNode.zeroIndex] = tempNode.value[index];
        tempNode.value[index] = 0;

        tempNode.zeroIndex = getZeroIndex(tempNode);

        return tempNode;

    }

    protected int evaluate(Node node){
        return 1;
    }


    /**
     * checks the node is a valid game state
     * @param node
     * @return true is node is valid
     */
    protected boolean checkValidNode(int[] nodeValue){
        //make sure there are the right amount of tiles
        if (nodeValue.length != 9){
            return false;
        }

        //these are all the tiles that should be present
        int[] remaining = {0,1,2,3,4,5,6,7,8};

        //chech each tile in the node and make sure all the required tiles are present
        for (int i : nodeValue){
            for (int j = 0; j < remaining.length; j++){
                //if tile is present remove it from the remaining list
                if (i == remaining[j]){
                    remaining[j] = -1;
                }
            }
        }

        //make sure the remaining list is completely null
        for (int r : remaining){
            if (r != -1){
                return false;
            }
        }

        //if all tiles are present return true
        return true;

    }

    protected int[] getValidSwaps(int zeroIndex) throws Exception{

        // all the valid moves for a given gap

        int[] validSwaps = null;

        switch (zeroIndex){
            case 0:
                validSwaps = new int[]{1,3};
                break;
            case 1:
                validSwaps = new int[]{0,2,4};
                break;
            case 2:
                validSwaps = new int[]{1,5};
                break;
            case 3:
                validSwaps = new int[]{0,4,6};
                break;
            case 4:
                validSwaps = new int[]{1,3,5,7};
                break;
            case 5:
                validSwaps = new int[]{2,4,8};
                break;
            case 6:
                validSwaps = new int[]{3,7};
                break;
            case 7:
                validSwaps = new int[]{4,6,8};
                break;
            case 8:
                validSwaps = new int[]{5,7};
                break;
            default:
                throw new Exception("no valid swaps for this index");
        }

        return validSwaps;
    }

    /**
     * function to get the index of the gap in the board
     * @param node
     * @return
     * @throws Exception
     */
    int getZeroIndex(Node node) throws Exception {
        int zeroIndex = -1;
        for (int i = 0; i < node.value.length; i++){
            if (node.value[i] == 0){
                zeroIndex = i;
                break;
            }
        }

        if (zeroIndex == -1) throw new Exception("no zero index");

        return zeroIndex;
    }

    /**
     * function to print the trace of a board to stdout
     * @param node
     */
    public static void printNodeTrace(Node node){
        int depth = -1;
        Node currNode = node;
        while (true){
            System.out.println(currNode);
            depth++;
            if (currNode.prev != null){
                currNode = currNode.prev;
            }
            else{
                break;
            }
        }
        System.out.println("depth " + depth);
    }

    /**
     * function to get the depth of a node
     * this is used because we want to favour nodes with lower depths
     * @param n
     * @return
     */
    private int getTraceLength(Node n){
        Node currentNode = n;
        int total = 0;
        while(true){
            total ++;
            if (currentNode.prev != null){
                currentNode = currentNode.prev;
            } else break;
        }
        return total;
    }

}