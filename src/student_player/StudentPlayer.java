package student_player;

import boardgame.Board;
import boardgame.Move;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

	int timeLimit = 1900;
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260584555");
    }

    // choose move based on iterative minimax with pruning
    public Move chooseMove(TablutBoardState boardState) {
    	// keep track of time started and time limit
    	long startTime = System.currentTimeMillis();
    	long endTime = startTime + timeLimit;
    			
    	// start minimaxPruning algorithm with depth of 2
    	int iterDepth = 2;
    	Move chosenMove = boardState.getRandomMove();
		try {
			// if move is chosen without time limit reached, set it to chosenMove
			chosenMove = new MinimaxPruneTree(iterDepth, (TablutBoardState) boardState.clone(), endTime).getBestMove(player_id);
		} catch (AtTimeLimitException e) {
//			System.out.println("Reached time limit while trying iterDepth " + iterDepth--);
		}
		
		// keep running minimax-pruning with higher depth as long as there is time left
    	while ((System.currentTimeMillis() - startTime) < timeLimit) {
			iterDepth++;
			try {
				// if move is chosen without time limit reached, set it to chosenMove
	    		Move newMove = new MinimaxPruneTree(iterDepth, (TablutBoardState) boardState.clone(), endTime).getBestMove(player_id);
				chosenMove = newMove;
			} catch (AtTimeLimitException e) {
//				 System.out.println("Reached time limit while trying iterDepth " + iterDepth--);
			}

    	}
	
        // Return the move to be processed by the server.
//        System.out.println("Time: " + (System.currentTimeMillis() - startTime) + " with last successful iterDepth of " + iterDepth);
        return chosenMove;
    }

}

