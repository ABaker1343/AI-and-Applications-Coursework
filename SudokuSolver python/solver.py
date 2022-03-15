import random
import sys

class Node:
    def __init__(self, lockedPositions):
        self.lockedPositions = lockedPositions
        self.values = []
        for r in range(9):
            self.values.append([])
            ints = [1,2,3,4,5,6,7,8,9]
            for c in range(9):
                if (r,c) in lockedPositions:
                    ints.remove(self.values[r][c])
                else:
                    index = random.randint(0,len(ints) -1)
                    ints.pop(index)

if __name__ == "__main__":
    filepath = sys.argv[0]

    initialBoard = []

    with open(filepath) as file:
        lines = file.readlines()
        r = 0
        for l in lines:
            initialBoard.append([])
            for c in l:
                if c.isdigit:
                    initialBoard[r]
