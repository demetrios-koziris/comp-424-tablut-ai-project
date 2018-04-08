package student_player;

import java.util.List;
import java.util.Random;

import boardgame.Board;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class MinimaxPruneTree {

	public int maxDepth;
	public TablutBoardState headBoardState;
	public long endTime;

	public MinimaxPruneTree(int maxDepth, TablutBoardState boardState, long endTime) {
		this.headBoardState = boardState;
		this.maxDepth = maxDepth;
		this.endTime = endTime;

	}



	public TablutMove getBestMove(int boardPlayer) throws AtTimeLimitException {
//		System.out.println("getBestMove: player="+boardPlayer+" depth="+depth);
		//
		double alpha = 0;
		double beta = 1;
		int depth = 0;
		
			
		if (boardPlayer == TablutBoardState.SWEDE) {
			TablutMove maxMove = null;
			List<TablutMove> nextMoves = this.headBoardState.getAllLegalMoves();
			for (TablutMove move : nextMoves) {
				// clone the bs and process the move
				TablutBoardState childBS = (TablutBoardState) this.headBoardState.clone();
				childBS.processMove(move);
				double childVal = minimaxPrune(childBS, depth+1, alpha, beta, this.headBoardState.getOpponent());
				if (childVal > alpha) {
					alpha = childVal;
					maxMove = move;
				}
				if (alpha >= beta) {
					return maxMove;
				}
			}
			return maxMove;
		}
		else {
			TablutMove minMove = null;
			List<TablutMove> nextMoves = this.headBoardState.getAllLegalMoves();
			for (TablutMove move : nextMoves) {
				// clone the bs and process the move
				TablutBoardState childBS = (TablutBoardState) this.headBoardState.clone();
				childBS.processMove(move);
				double childVal = minimaxPrune(childBS, depth+1, alpha, beta, this.headBoardState.getOpponent());
				if (childVal < beta) {
					beta = childVal;
					minMove = move;
				}
				if (beta <= alpha) {
					return minMove;
				}
			}
			return minMove;
		}
	}

	private double minimaxPrune(TablutBoardState nodeBS, int depth, double alpha, double beta, int boardPlayer) throws AtTimeLimitException {
		if (System.currentTimeMillis() > endTime) {
			throw new AtTimeLimitException("");
		}
		
		if (depth == this.maxDepth) {
			return evaluate(nodeBS);
		}
		if (boardPlayer == TablutBoardState.SWEDE) {
			List<TablutMove> nextMoves = nodeBS.getAllLegalMoves();
			for (TablutMove move : nextMoves) {
				// clone the nodeBS and process the move
				TablutBoardState childBS = (TablutBoardState) nodeBS.clone();
				childBS.processMove(move);
				double childVal = minimaxPrune(childBS, depth+1, alpha, beta, nodeBS.getOpponent());
				alpha = Math.max(alpha, childVal);
				if (alpha >= beta) {
					return beta;
				}
			}
			return alpha;
		}
		else {
			List<TablutMove> nextMoves = nodeBS.getAllLegalMoves();
			for (TablutMove move : nextMoves) {
				// clone the nodeBS and process the move
				TablutBoardState childBS = (TablutBoardState) nodeBS.clone();
				childBS.processMove(move);
				double childVal = minimaxPrune(childBS, depth+1, alpha, beta, nodeBS.getOpponent());
				beta = Math.min(beta, childVal);
				if (beta <= alpha) {
					return alpha;
				}
			}
			return beta;
		}

	}

	private double evaluate(TablutBoardState nodeBS) throws AtTimeLimitException {
		if (System.currentTimeMillis() > endTime) {
			throw new AtTimeLimitException("");
		}
		
		if (nodeBS.getWinner() != Board.NOBODY) {
			return nodeBS.getWinner();
		}
		int swedePieceCount = nodeBS.getNumberPlayerPieces(TablutBoardState.SWEDE);
		int muscoPieceCount = nodeBS.getNumberPlayerPieces(TablutBoardState.MUSCOVITE);
		int totalPieceCount = swedePieceCount + muscoPieceCount;
		double pieceH = (swedePieceCount*1.0/totalPieceCount);
		
		int kingDistToCorner = Coordinates.distanceToClosestCorner(nodeBS.getKingPosition());
		int maxDistToCorner = 8;
		double distToCornerH = ((maxDistToCorner-kingDistToCorner)*1.0/maxDistToCorner);
		
		double h = pieceH*0.8 + distToCornerH*0.2;

		return h;
	}
}

