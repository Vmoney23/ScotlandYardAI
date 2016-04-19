package player;

import scotlandyard.*;

import java.util.*;

/**
 *
 */
public class MiniMaxPlayer implements Player {

    public MiniMaxPlayer(ScotlandYardView view, String graphFilename) {
        //TODO: A better AI makes use of `view` and `graphFilename`.
    }

    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
        //TODO: Some clever AI here ...
        System.out.println("Getting random move");
        // Collections.shuffle(moves);
        // System.out.println("Moves: " + moves);
        System.out.println("Playing random move: " + moves.get(0));

        System.out.println("Getting move");
        Move aiMove = getAIMove();

        System.out.println("Playing move: " + moves.get(0));
        receiver.playMove(moves.get(0), token);
    }

    /**
     * Chooses a move
     *
     * @return the chosen move
     */
    Move getAIMove() {
        int treeDepth = 3;
        Graph gameTree = generateTree(treeDepth);
        pruneTree(gameTree);
        return minimax(gameTree.getLastMovesList());
    }

    /**
     * Generates a game tree, 
     * @return
     */
    private Graph generateTree(int treeDepth) {}

    /**
     * Prunes a game tree using alpha-beta pruning.
     *
     * @param gameTree the tree to prune
     */
    private void pruneTree(Graph gameTree) {}

}
