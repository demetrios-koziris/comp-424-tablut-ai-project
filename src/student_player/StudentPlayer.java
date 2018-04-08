package student_player;

import boardgame.Move;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

	int timeLimit = 1800;
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260584555");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
    	long startTime = System.currentTimeMillis();
    	long endTime = startTime + timeLimit;
    			
    	int iterDepth = 2;
    	Move chosenMove = null;
		try {
			chosenMove = new MinimaxPruneTree(iterDepth, boardState, endTime).getBestMove(player_id);
		} catch (AtTimeLimitException e) {
//			System.out.println("No time left while trying iterDepth " + iterDepth);
			iterDepth--;
		}
    	while ((System.currentTimeMillis() - startTime) < timeLimit/2) {
//		while (iterDepth < 3) {
			iterDepth++;
//    		System.out.println("Trying iterDepth " + iterDepth + " at time " + (System.currentTimeMillis() - startTime));
	    	try {
	    		Move newMove = new MinimaxPruneTree(iterDepth, boardState, endTime).getBestMove(player_id);
				chosenMove = newMove;
			} catch (AtTimeLimitException e) {
//				System.out.println("No time left while trying iterDepth " + iterDepth);
    			iterDepth--;
			}

    	}
	
        

        // Return your move to be processed by the server.
    	return finishChooseMove(startTime, chosenMove, iterDepth);
    }
    
    private Move finishChooseMove(long startTime, Move chosenMove, int iterDepth) {
    	long finishTime = System.currentTimeMillis() - startTime;
        System.out.println("time: " + finishTime + " with last succ iterDepth " + iterDepth);
        return chosenMove;
    }
}