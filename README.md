# COMP 424 Tablut AI Project

Final project for [COMP 424 Artificial Intelligence](https://www.mcgill.ca/study/2017-2018/courses/comp-424) at McGill.  
Original code repository can be found at https://github.com/kiankd/comp424.

## Introduction
The aim of this project was to develop an AI approach to the game Tablut. This game is a variant of Hnefatafl where on a 9 by 9 board, the Swedes, starting with 8 pieces and a king piece, attempt to move the king into a corner while the Muscovites, starting with 16 pieces, attempt to capture the king. For full details concerning this game and its rules, please see the [project specification document](project_specification.pdf) as well as the [code description document](code_description.pdf) for game implementation details.

## Technical Approach

### General Explanation of Algorithm:
The AI algorithm developed for the game uses a Minimax cut-off search with alpha-beta pruning and iterative deepening. The AI algorithm conducts an initial Minimax cut-off search with a cut-off depth level of 2. It records the move returned by this algorithm and then proceeds with iterative deepening by re-conducting a Minimax search with a higher cut-off depth level on each iteration. Each Minimax search builds a search tree by applying legal moves to a board state in order to generate children board states at a given node. The depth of the tree is limited to a maximum cut-off depth at which point a heuristic function is used to evaluate the board state at that leaf. Furthermore, the size of the tree required to be searched is reduced by applying alpha-beta pruning during the Minimax search. Because there is a time limit constraining execution, the Minimax search is provided with the turn start time and halts execution when the time limit (with a buffer) is reached. The AI then returns the move saved from the last completed Minimax search. 

The following sections provide further detail.

### Minimax cut-off search:
The Minimax search uses a search tree (with a maximum depth) as stated in the previous section. This allows for a strategy of picking a move based on simulations of what the opponent might do on subsequent turns. At the head of the tree, the best move is picked and returned based on the maximum or minimum value (depending on if the player is a Swede or Muscovite) of the child board states. Recursively, the value of a node will be determined in the same way by applying Minimax search and with each level of the tree alternating minimizing and maximizing.

### Heuristic Function:
The leaves of the Minimax tree (at the cut off depth) are given a value based on a heuristic function that
attempts to approximate how well the Swedes are “winning” against the Muscovites. It combines two
weighted values. The first value represents the ratio of remaining Swede pieces to the total number of
remaining pieces and therefore ranges between 0 and 1. The second value represents the distance of the
king from the corner where 0 is the furthest distance away and 1 is on a corner. These two values are each
then combined with weights of 0.8 and 0.2 respectively so that the overall heuristic function ranges from
0 to 1 and is maximized for the Swedes and minimized for the Muscovites during playing. Note that if
there is an actual winner for the board state, the heuristic function will return 0 or 1 (depending on who
won) directly.

### Alpha-Beta Pruning:
The Minimax cut-off search explained above in fact also uses alpha-beta pruning to avoid expanding the
entire search tree. At the head of the tree, alpha is initialized to 0 (smallest value) and beta is initialized to
1 (largest value). At each node in the recursive Minimax search, alpha is maximized or beta is minimized
as explained before, but the initial values are provided from the parent node. In this way, on a maximizing
node for example, if a new child board is found to have a value greater than the current alpha, it is set as
the new alpha and is then also checked against the beta provided from the parent node. If it is larger than
the beta, then the rest of the children nodes can be pruned because the beta represents the current
minimum value from the parent min node. It follows from how Minimax search works that the parent min
node will never chose the alpha value returned from the child max node since it has a beta smaller than it.
It therefore saves time to not continue maximizing the child nodes.

### Alpha-Beta Depth Bias:
My implementation of alpha-beta pruning also applies a variation to bias the Minimax search to winning
moves found at smaller depths. Both alpha and beta include a depth attained value so that during the
Minimax search, if two child nodes have the same value, the one with the smaller depth attained value can
be chosen. This was done so that if there is a winning move at depth 1 it can be chosen over a winning
move at depth 3. Normally it would be expected that this variation wouldn’t be required. After all, if there
is a wining move at depth 3, it would be expected that the intermediate node at depth 2 wouldn’t chose it.
This is usually the case, except when all of the child nodes (at depth 3) of the intermediate node at depth 2
are also winning moves for the player on depth 1 and 3. This comes into play near the end of the game
where a Swede player is 1 move away from the corner and no move by the Muscovites can block it.
Without this depth bias alteration, the Minimax search might not chose the winning move at depth 1 since
it also can maximize with any of the winning nodes at depth 3.

### Iterative Deepening and Time Constraint:
The iterative deepening begins with a max depth of 2 and iterates by 1 until time constraints are reached.
The time constraints are handled in two ways, the first is by only beginning a Minimax search if there is
still time left and the second is by halting execution if the time constraint is reached (with a small buffer).
The execution halting is done within a Minimax search by checking the time at the beginning of each
node minimaximization and throwing an exception if the time limit is reached. The exception is thrown
all the way up to the function call by the player where it is caught and returns the move from the last
completed Minimax search.
