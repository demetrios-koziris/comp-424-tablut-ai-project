package student_player;

import java.util.List;

import boardgame.Board;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;

//Implementation of minimax alg with alpha beta pruning
public class MinimaxPruneTree {

	public int maxDepth;
	public TablutBoardState headBoardState;
	public long endTime;

	public MinimaxPruneTree(int maxDepth, TablutBoardState headBoardState, long endTime) {
		this.headBoardState = headBoardState;
		this.maxDepth = maxDepth;
		this.endTime = endTime;
	}

	
	// This function differs only slightly from minimaxPrune as it needs to keep track of not just max and min
	// but also the corresponding moves that led to those values. It is also biased to prefer winning moves at shorter depths
	public TablutMove getBestMove(int boardPlayer) throws AtTimeLimitException {
		// initialize a, b, and starting depth
		int depth = 0;
		Valuation alpha = new Valuation(0.0, depth);
		Valuation beta = new Valuation(1.0, depth);
		
		// if the player with the turn is a SWEDE then maximize
		if (boardPlayer == TablutBoardState.SWEDE) {
			TablutMove maxMove = null;
			List<TablutMove> nextMoves = this.headBoardState.getAllLegalMoves();
			// iterate through all possible moves
			for (TablutMove move : nextMoves) {
				// clone the bs and process the move to generate a new board
				TablutBoardState childBS = (TablutBoardState) this.headBoardState.clone();
				childBS.processMove(move);
				// recursively run minimaxPrune on the child board state
				Valuation childValuation = minimaxPrune(childBS, depth+1, alpha.clone(), beta.clone(), (boardPlayer == 1 ? 0 : 1));
				if (childValuation != null && (childValuation.hVal > alpha.hVal || (childValuation.hVal == alpha.hVal && childValuation.depthAttained < alpha.depthAttained))) {
					alpha.hVal = childValuation.hVal;
					alpha.depthAttained = childValuation.depthAttained;
					maxMove = move;
				}
			}
			return maxMove;
		}
		else {
			// if the player with the turn is a MOSCO then minimize
			TablutMove minMove = null;
			List<TablutMove> nextMoves = this.headBoardState.getAllLegalMoves();
			// iterate through all possible moves
			for (TablutMove move : nextMoves) {
				// clone the bs and process the move to generate a new board
				TablutBoardState childBS = (TablutBoardState) this.headBoardState.clone();
				childBS.processMove(move);
				// recursively run minimaxPrune on the child board state
				Valuation childValuation = minimaxPrune(childBS, depth+1, alpha.clone(), beta.clone(), (boardPlayer == 1 ? 0 : 1));
				if (childValuation != null && (childValuation.hVal < beta.hVal || (childValuation.hVal == beta.hVal && childValuation.depthAttained < beta.depthAttained))) {
					beta.hVal = childValuation.hVal;
					beta.depthAttained = childValuation.depthAttained;
					minMove = move;
				}
			}
			return minMove;
		}
	}

	// Implementation of minmax alg with alpha beta pruning
	private Valuation minimaxPrune(TablutBoardState nodeBS, int depth, Valuation alpha, Valuation beta, int boardPlayer) throws AtTimeLimitException {
		boolean updated = false;
		
		if (System.currentTimeMillis() > endTime) {
			throw new AtTimeLimitException("");
		}
		// if node is a leaf, then evaluate with h function
		if (depth == this.maxDepth) {
			return new Valuation(evaluate(nodeBS), depth);
		}
		// if there is a winner return the winner (corresponds to the max and min h values of 0 or 1)
		if (nodeBS.getWinner() == 0 || nodeBS.getWinner() == 1) {
			return new Valuation(nodeBS.getWinner(), depth);
		}
		if (boardPlayer == TablutBoardState.SWEDE) {
			// if the player with the turn is a SWEDE then maximize
			List<TablutMove> nextMoves = nodeBS.getAllLegalMoves();
			// iterate through all possible moves
			for (TablutMove move : nextMoves) {
				// clone the nodeBS and process the move
				TablutBoardState childBS = (TablutBoardState) nodeBS.clone();
				childBS.processMove(move);
				// recursively run minimaxPrune on the child board state
				Valuation childValuation = minimaxPrune(childBS, depth+1, alpha.clone(), beta.clone(), (boardPlayer == 1 ? 0 : 1));
				if (childValuation != null && (childValuation.hVal > alpha.hVal || (childValuation.hVal == alpha.hVal && childValuation.depthAttained < alpha.depthAttained))) {
					alpha.hVal = childValuation.hVal;
					alpha.depthAttained = childValuation.depthAttained;
					updated = true;
				}
				if (alpha.hVal > beta.hVal) {
					// no need to search further, we can prune the remaining children
					return null;
				}
			}
			if (!updated) {
				return new Valuation(alpha.hVal, depth+1);
			}
			return alpha;
		}
		else {
			// if the player with the turn is a MOSCO then minimize
			List<TablutMove> nextMoves = nodeBS.getAllLegalMoves();
			for (TablutMove move : nextMoves) {
				// clone the nodeBS and process the move
				TablutBoardState childBS = (TablutBoardState) nodeBS.clone();
				childBS.processMove(move);
				// recursively run minimaxPrune on the child board state
				Valuation childValuation = minimaxPrune(childBS, depth+1, alpha.clone(), beta.clone(), (boardPlayer == 1 ? 0 : 1));
				if (childValuation != null && (childValuation.hVal < beta.hVal || (childValuation.hVal == beta.hVal && childValuation.depthAttained < beta.depthAttained))) {
					beta.hVal = childValuation.hVal;
					beta.depthAttained = childValuation.depthAttained;
					updated = true;
				}
				if (beta.hVal < alpha.hVal) {
					// no need to search further, we can prune the remaining children
					return null;
				}
			}
			if (!updated) {
				return new Valuation(beta.hVal, depth+1);
			}
			return beta;
		}

	}

	// heuristic function for evaluating state of leaf nodes
	private double evaluate(TablutBoardState nodeBS) throws AtTimeLimitException {
		if (System.currentTimeMillis() > endTime) {
			throw new AtTimeLimitException("");
		}
		// Heuristic values are generated to be between 0 and 1
		
		// if there is a winner return the winner (corresponds to the max and min h values of 0 or 1)
		if (nodeBS.getWinner() == 0 || nodeBS.getWinner() == 1) {
			return nodeBS.getWinner();
		}
		
		// calculate a ratio of swede to musco pieces remaining on the board
		int swedePieceCount = nodeBS.getNumberPlayerPieces(TablutBoardState.SWEDE);
		int muscoPieceCount = nodeBS.getNumberPlayerPieces(TablutBoardState.MUSCOVITE);
		int totalPieceCount = swedePieceCount + muscoPieceCount;
		double pieceH = (swedePieceCount*1.0/totalPieceCount);
		
		// calculate the distance of the king to a corner
		int kingDistToCorner = Coordinates.distanceToClosestCorner(nodeBS.getKingPosition());
		int maxDistToCorner = 8;
		double distToCornerH = ((maxDistToCorner-kingDistToCorner)*1.0/maxDistToCorner);
		
		// weigh the two h values calculated above
		double h = pieceH*0.8 + distToCornerH*0.2;

		return h;
	}
}

