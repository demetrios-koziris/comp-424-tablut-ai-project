package student_player;

import boardgame.Move;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class MinimaxPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public MinimaxPlayer() {
        super("MinimaxPlayer");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
    	long startTime = System.currentTimeMillis();
    	
        MinimaxTree minimaxTree = new MinimaxTree(2, 0, boardState);
//        if (minimaxTree.treeNodeCount < 2000) {
//        	minimaxTree = new MinimaxTree(3, 0, boardState);
//        }
        Move chosenMove = minimaxTree.getBestMove(player_id);
        		
        long finishTime = System.currentTimeMillis() - startTime;
        
        System.out.println("time: " + finishTime);
        System.out.println("DtoC: "+Coordinates.distanceToClosestCorner(boardState.getKingPosition()));
        System.out.println(minimaxTree.treeNodeCount);


        // Return your move to be processed by the server.
        return chosenMove;
//        return myMove;
    }
}