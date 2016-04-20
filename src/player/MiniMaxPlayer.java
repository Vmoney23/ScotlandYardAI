package player;

import aigraph.ScotlandYardGameTree;
import scotlandyard.*;

import java.util.*;

/**
 *
 */
public class MiniMaxPlayer implements Player {

    ScotlandYardView currentGameState;

    public MiniMaxPlayer(ScotlandYardView view, String graphFilename) {
        //TODO: A better AI makes use of `view` and `graphFilename`.

        this.currentGameState = view;
    }

    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
        // update current game state
        if (!(receiver instanceof ScotlandYardView))
            throw new IllegalArgumentException("Receiver must implement ScotlandYardView");
        currentGameState = (ScotlandYardView) receiver;

        // get ai move
        System.out.println("Getting move");
        Move aiMove = getAIMove();

        // play ai move
        System.out.println("Playing move: " + aiMove);
        receiver.playMove(aiMove, token);
    }

    /**
     * Chooses a move
     *
     * @return the chosen move
     */
    Move getAIMove() {
        // create new game tree to specified depth, with root as current state of the game
        int treeDepth = 0;
        ScotlandYardGameTree<ScotlandYardView, Move> gameTree = new ScotlandYardGameTree<>(currentGameState);
        generateTree(gameTree, treeDepth);//calls pruneTree(), score()

        // return best moved based on MiniMax
        List<ScotlandYardView> finalStates = gameTree.getFinalStatesList();
        return minimax(finalStates);
    }

    /**
     * Generates a game tree to specified depth.
     *
     */
    private void generateTree(ScotlandYardGameTree gameTree, int treeDepth) {

    }


    private Move minimax(List<ScotlandYardView> gameStates) {
        return null;
    }


}
