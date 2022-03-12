package EightPuzzleSolver;

public class PuzzleGraphDistance extends PuzzleGraph {


    PuzzleGraphDistance(int[] initialNode, int[] goalNode) throws Exception {
        super(initialNode, goalNode);
    }

    @Override
    protected int evaluate(Node node){
        int totalDistance = 0;

        for (int i = 0; i < goalNode.value.length; i++){
            if (node.value[i] != 0) {
                totalDistance += getDistanceFromFinalLocation(node.value[i], i);
            }
        }

        return totalDistance;



    }
    /**
     * return the amount of moves to slide a tile to its target location
     * @param value
     * @param index
     * @return
     */
    private int getDistanceFromFinalLocation(int value, int index){

        int targetIndex = -1;
        int distance = 0;

        for (int i = 0; i < goalNode.value.length; i++){
            if (value == goalNode.value[i]){
                targetIndex = i;
            }
        }

        // you can move up/down and left/right but
        // you have to make sure that is represented
        // by the moves made in this function

        while(index != targetIndex){
            //its its above / below
            if (index <= targetIndex - 3){
                index += 3;
            }
            else if (index >= targetIndex + 3){
                index -= 3;
            }
            //if its in the middle col
            else if(index%3 == 1){
                //you can move it both left and right
                if (index <= targetIndex - 2){
                    //move down
                    index += 3;
                }
                else if (index >= targetIndex + 2){
                    index -= 3;
                }
                if (index < targetIndex){
                    index++;
                }
                else {
                    index--;
                }
            }
            //if its on the far sides
            else if (index%3 == 0){
                //left side
                //you can just increment beacuse its not
                //above or below and its not in the right spot
                index++;
            }
            else if(index%3 == 2){
                //right side
                //you can just decrement because its not
                //above or below and its not in the right spot
                index--;
            }

            distance++;
        }

        return distance;

    }
}
