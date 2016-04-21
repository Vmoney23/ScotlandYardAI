package player;

import aigraph.*;
import graph.UndirectedGraph;
import scotlandyard.*;

import java.io.IOException;
import java.util.*;

/**
 *
 */
public class MiniMaxPlayer implements Player {

    private int location;
    private ScotlandYardView currentGameState;
    private UndirectedGraph<Integer, Transport> graph;
    private List<Move> moves;

    public MiniMaxPlayer(ScotlandYardView view, String graphFilename) {
        //TODO: A better AI makes use of `view` and `graphFilename`.

        this.currentGameState = view;
        ScotlandYardGraphReader graphReader = new ScotlandYardGraphReader();
        try {
            this.graph = graphReader.readGraph("lib/scotlandyard.jar/" + graphFilename);
        } catch (IOException e) {
            System.err.println("failed to read " + graphFilename);
        }
    }


    /**
     *
     * @param location
     * @param moves
     * @param token
     * @param receiver
     */
    @Override
    public void notify(int location, List<Move> moves, Integer token,
                       Receiver receiver) {
        // update current game state
        if (!(receiver instanceof ScotlandYardView))
            throw new IllegalArgumentException("Receiver must implement ScotlandYardView");
        this.currentGameState = (ScotlandYardView) receiver;
        this.moves = moves;
        this.location = location;

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
        //int treeDepth = 0;
        //ScotlandYardGameTree gameTree = new ScotlandYardGameTree(currentGameState);
        //generateTree(gameTree, treeDepth);//calls pruneTree(), score()
        HashMap<Move, Integer> moveScores = score();
        // return best moved based on MiniMax
        //List<AINode<ScotlandYardView>> finalStates = gameTree.getFinalStatesList();
        //return minimax(finalStates); // TODO HOW TO GET INITIAL MOVE?? map scores/states->initial move?
        return moves.get(0);
    }

    /**
     * Calculates scores for all given moves.
     *
     * @return map of each move to its score.
     */
    private HashMap<Move, Integer> score() {
        // create map of moves to scores
        HashMap<Move, Integer> moveScoreMap = new HashMap<>();

        // store locations of other players
        HashMap<Colour, Integer> playerLocationMap = new HashMap<>();
        for (Colour player : currentGameState.getPlayers()) {
            playerLocationMap.put(player, currentGameState.getPlayerLocation(player));
        }

        // iterate through possible moves, calculating score for each one and
        // adding to moveScoreMap
        for (Move move : moves) {
            int score = 0;

            // TODO score a double move
            // TODO score a single move
            if (move instanceof MoveTicket) {

            }
        }

        return moveScoreMap;
    }

    /**
     * Generates a game tree to specified depth, given game tree with just root
     * node.
     *
     */
    private void generateTree(ScotlandYardGameTree gameTree, int treeDepth) {

    }


    private static Move minimax(List<AINode<ScotlandYardView>> gameStates) {
        return null;
    }


}
