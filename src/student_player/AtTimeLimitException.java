package student_player;

// Exception class for breaking out of recursion in MinimaxPruneTree algorithm
public class AtTimeLimitException extends Exception {
	public AtTimeLimitException(String message) {
        super(message);
    }
}