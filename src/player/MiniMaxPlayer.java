package player;

import aigraph.*;
import scotlandyard.*;

import java.util.*;

/**
 *
 */
public class MiniMaxPlayer implements Player {

    private ScotlandYardView currentGameState;
    private List<Move> moves;

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
        this.moves = moves;

        // get ai move
        System.out.println("Getting move");
        Move aiMove = getAIMove();

        // play ai move
        System.out.println("Playing move: " + aiMove);
        receiver.playMove(aiMove, token);
    }

    /**
     * Chooses a move.
     *
     * @return the chosen move.
     */
    Move getAIMove() {
        // create new game tree to specified depth, with root as current state of the game
        int treeDepth = 0;
        ScotlandYardGameTree gameTree = new ScotlandYardGameTree(currentGameState);
        generateTree(gameTree, treeDepth);//calls pruneTree(), score()

        // return best moved based on MiniMax
        //List<AINode<ScotlandYardView>> finalStates = gameTree.getFinalStatesList();
        //return minimax(finalStates); // TODO HOW TO GET INITIAL MOVE?? map scores/states->initial move?
        return moves.get(0);
    }

    /**
     * Generates a game tree to specified depth, given game tree with just root
     * node.
     *
     */
    private void generateTree(ScotlandYardGameTree gameTree, int treeDepth) {

    }


    private Move minimax(List<AINode<ScotlandYardView>> gameStates) {
        return null;
    }


}
