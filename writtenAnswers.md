# Question 1
## Question 1.1
the 8-puzzle problem can be seen as a seach problem because you can represent each state of the board as a node in a graph, you can then make each possible move an edge in the graph.
you can then start at any given node and search the graph for the desired state, in the case of this problem it would be the solution state.
you can do this using graph searching algorightms and you can record the steps that you have to take to go form the start state to the desired state

## Question 1.2
1. A* is an algorith that is given a start state and then using some heuristic function it will open the search space to include the best node that is within one step of the nodes that are currently in the search space.
it will keep doing this until it finds the desired node, when choosing a node to jump to A* will take into account the cost of reaching the node aswell as the cost of reaching the desired state from the new node

2. one addmissable heuristic function that you could use is the amount of tiles that are in the wrong place. This is addmissable because it will never have less moves to take than the amount of wrong tiles, this means that the cost of reaching the desired node will always be equal to or more than the calulated cost.\
another addmissable heuristic function that could be used is how many tiles away from its final position each tile is. this is addmissable because you will never have to move a tile less spaces than the amount of tiles between it and its final space to get it to the final space.\

3. (see EightPuzzleSolver.java).\ in the code i represented each node as an array of integers where 0 would represent the blank space, i would then swap one of the numbers in the array with the 0 to represent a move, i have a function to make sure that all the swaps that were made are legal in the puzzle.\
the PuzzleGraph class has two subclasses (one for each heuristic function) which will be instantiated based on which function the user chooses

4. the two heuristic functions did perform differently, the function that would take the total amount of tiles that were out of place performed worse than the function that would return the total distance of tiles from their destination. I think this is because the first function does not take into account how close each tile is, this means that a state in which all the tiles are only 1 space out of place out be evaluated the same as a state in which all the tiles are 2 out of place, dispite the fact that the second scenario is clearly worse than the first, this same thing would not happen with the second function

## Question 1.3
(see EightPuzzleSolver) the main method is in EightPuzzleSolver.java
this algorithm can solve any configuration of configurations, provided there is a solution.
this is because at a worst case it will check through all the possible paths to get to an answer

note: in the case provided by the coursework pdf the algorithm took just under 300 seconds to terminate on my personal machine.

# Question 2

## Question 2.1 p1
a) a solution representation is a 2d array that represents the board where all for all x values you have a x y value from the numbers 1-9
and for all y values you have a x y value for all numbers 1-9
and for each value x = 0-2, y = 0-2; x = 3-5, y = 3-5; and x = 6-8, y = 6-8 you have an x y value for each number 1-9
then you will have a solved solution
since you have a 9x9 board, just knowing that you have one of each number 1-9 means that you have no duplicates

b) a fitness function that you can use is how many rows, columns are squres contain one of each number 1-9 

c) a crossover function that you can use is to split the board horizontally and take one half from one parent and another half from another parent

d) a mutation function that you can use is to swap two of the numbers in one or more row or column.

e) you can initialize the population as having one of each number for every row randomly chosen, with the exception of the pre-determined places where it will be the number that was inputted

f) to select the parents you can take the top 10% of evaluated candidates and then populate a new generation with the crossovers and mutations that you get from them

g) you can terminate when you have found a board that fits the solution criteria

i will be representing the problem as a 2 dimensional array where each inner array will represent a row of the board, this means that you can index as a grid using [row][column]

## Question 2.1 p2

(see SudokuSolver) the main method is in SolverApp.java
