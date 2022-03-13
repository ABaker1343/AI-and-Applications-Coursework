package EightPuzzleSolver;

public class PuzzleGraphOutOfPlace extends PuzzleGraph {


    PuzzleGraphOutOfPlace(int[] initialNode, int[] goalNode) throws Exception {
        super(initialNode, goalNode);
    }


    /**
     * takes a node and return how good it is considerd to be base on
     * how many items are in the WRONG place
     * @return number of nodes in the wrong place
     */
    @Override
    protected int evaluate(Node node){
        int wrongItems = 0; //assume all are wrong. this is worst case``
        
        //go over the node and if an item is in the right place decrement wrongNodes
        for (int i = 0; i < goalNode.value.length; i++){
            if (goalNode.value[i] == 0) {continue;}
            if ( ! (node.value[i] == goalNode.value[i]) ) wrongItems++;
        }

        return wrongItems;
    }
}
