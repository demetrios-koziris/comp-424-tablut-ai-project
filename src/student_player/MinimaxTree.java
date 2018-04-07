package student_player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import boardgame.Board;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class MinimaxTree {

	public int depth;
	public TablutBoardState boardState;
	public TablutMove prevMove;
	public ArrayList<MinimaxTree> children = new ArrayList<MinimaxTree>();
	public boolean leaf = false;
	public int treeNodeCount = 1;
	public float valuation;
	private Random rand = new Random(1848);
	
	public MinimaxTree(int maxDepth, int depth, TablutBoardState boardState) {
		this.boardState = boardState;
		this.depth = depth;
		if (depth != maxDepth) {
			List<TablutMove> nextMoves = this.boardState.getAllLegalMoves();
			if (nextMoves.size() == 0) {
				System.out.println("NO MOVES AT NONLEAF NODE");
			}
			for (TablutMove move : nextMoves) {
				// clone the bs and process the move
				TablutBoardState cloneBS = (TablutBoardState) this.boardState.clone();
	            cloneBS.processMove(move);
	            // create a new child tree
	            MinimaxTree child = new MinimaxTree(maxDepth, this.depth+1, cloneBS);
	            child.setPrevMove(move);
	            children.add(child);
	            treeNodeCount += child.treeNodeCount;
			}
		}
		else {
			this.leaf = true;
		}
	}

	private void setPrevMove(TablutMove prevMove) {
		this.prevMove = prevMove;
	}
	
	public TablutMove getBestMove(int boardPlayer) {
//		System.out.println("getBestMove: player="+boardPlayer+" depth="+depth);
		if (boardPlayer == TablutBoardState.SWEDE) {
			float maxVal = 0;
			MinimaxTree maxChild = children.get(rand.nextInt(children.size()));
			for (MinimaxTree child : children) {
				float childVal = child.minimax(boardState.getOpponent());
				if (childVal > maxVal) {
					maxVal = childVal;
					maxChild = child;
				}
			}
			return maxChild.prevMove;
		}
		else {
			float minVal = 1;
			MinimaxTree minChild = children.get(rand.nextInt(children.size()));
			for (MinimaxTree child : children) {
				float childVal = child.minimax(boardState.getOpponent());
				if (childVal < minVal) {
					minVal = childVal;
					minChild = child;
				}
			}
			return minChild.prevMove;
		}
	}

	private float minimax(int boardPlayer) {
//		System.out.print("minimax: player="+boardPlayer+" depth="+depth);
		if (leaf == true) {
			valuation = evaluate();
			return valuation;
		}
		if (boardPlayer == TablutBoardState.SWEDE) {
			float maxVal = 0;
			for (MinimaxTree child : children) {
				float childVal = child.minimax(boardState.getOpponent());
				maxVal = Math.max(maxVal, childVal);
			}
			return maxVal;
		}
		else {
			float minVal = 1;
			for (MinimaxTree child : children) {
				float childVal = child.minimax(boardState.getOpponent());
				minVal = Math.min(minVal, childVal);
			}
			return minVal;
		}
	}

	private float evaluate() {
//		System.out.println("Ran Evaluate");
		if (boardState.getWinner() != Board.NOBODY) {
			return boardState.getWinner();
		}
		int swedePieceCount = boardState.getNumberPlayerPieces(TablutBoardState.SWEDE);
		int muscoPieceCount = boardState.getNumberPlayerPieces(TablutBoardState.MUSCOVITE);
		int totalPieceCount = swedePieceCount + muscoPieceCount;
		return (swedePieceCount/totalPieceCount)*1;
	}
}

