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
        int treeDepth = 0;
        GameTree<ScotlandYardView, Move> gameTree = new GameTree(currentGameState);
        generateTree(gameTree, treeDepth);//calls pruneTree() on recurses
        ArrayList<ScotlandYardView> finalStates = gameTree.getFinalStatesList();
        return minimax(finalStates); //calls score()
    }

    /**
     * Generates a game tree to specified depth and returns it
     *
     */
    public void generateTree(GameTree gameTree, int treeDepth) {

    }


}
