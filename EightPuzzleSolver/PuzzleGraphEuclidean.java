package EightPuzzleSolver;

public class PuzzleGraphEuclidean extends PuzzleGraph {


    PuzzleGraphEuclidean(int[] initialNode, int[] goalNode) throws Exception {
        super(initialNode, goalNode);
    }


    /**
     * takes a node and return how good it is considerd to be base on
     * how many items are in the WRONG place
     * @return number of nodes in the wrong place
     */
    // @Override
    // protected int evaluate(Node node){
    //     int wrongItems = 0; //assume all are wrong. this is worst case``
        
    //     //go over the node and if an item is in the right place decrement wrongNodes
    //     for (int i = 0; i < goalNode.value.length; i++){
    //         if (goalNode.value[i] == 0) {continue;}
    //         if ( ! (node.value[i] == goalNode.value[i]) ) wrongItems++;
    //     }

    //     return wrongItems;
    // }

    @Override
    protected double evaluate(Node node){
        double distance = 0;

        for (int i = 0; i < goalNode.value.length; i++){
            if (node.value[i] == 0){continue;}
            
            if (node.value[i] == goalNode.value[i]) {continue;}

            int goalIndex = 0;
            for (int j = 0; j < goalNode.value.length; j++){
                if (goalNode.value[j] == node.value[i]){
                    goalIndex = j;
                    break;
                } 
            }

            int[] xyGoal =  convertToXY(goalIndex);
            int[] xyNode = convertToXY(i);

            double toAdd = Math.sqrt(Math.pow((double)(xyGoal[0] - xyNode[0]), 2) + Math.pow((double)(xyGoal[1] - xyNode[1]), 2));
            distance += toAdd;

            //System.out.println(goalIndex + " " + i + " distance: " + toAdd);
        }

        return distance;
    }

    private int[] convertToXY(int index){

        int y = (int)index / 3;
        int x = index % 3;

        return new int[]{x,y};

    }

}

