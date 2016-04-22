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
    private HashMap<Colour, Integer> playerLocationMap;
    private UndirectedGraph<Integer, Transport> graph;
    private List<Move> moves;

    public MiniMaxPlayer(ScotlandYardView view, String graphFilename) {
        // store current game
        this.currentGameState = view;

        // store graph
        ScotlandYardGraphReader graphReader = new ScotlandYardGraphReader();
        try {
            this.graph = graphReader.readGraph("lib/scotlandyard.jar/" + graphFilename);
        } catch (IOException e) {
            System.err.println("failed to read " + graphFilename);
        }

        // store locations of other players
        for (Colour player : currentGameState.getPlayers()) {
            playerLocationMap.put(player, currentGameState.getPlayerLocation(player));
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
        //return minimax(finalStates);

        // return key associated with  highest value
        return Collections.max(moveScores.entrySet(), (entry1, entry2) -> (entry1.getValue() > entry2.getValue()) ? 1 : -1).getKey();
    }

    /**
     * Calculates scores for all given moves.
     *
     * @return map of each move to its score.
     */
    private HashMap<Move, Integer> score() {
        // create map of moves to scores
        HashMap<Move, Integer> moveScoreMap = new HashMap<>();

        // iterate through possible moves, calculating score for each one and
        // adding to moveScoreMap
        for (Move move : moves) {
            int score = 0;

            // TODO score a mrX move
            // TODO score a detective move
            // TODO score a double move
            // TODO score a single move
            if (move.colour == Colour.Black) {
                if (move instanceof MoveTicket)
                    score = scoreMoveTicket((MoveTicket) move);
                else if (move instanceof MoveDouble)
                    score = scoreMoveDouble((MoveDouble) move);
            }

            // put (move, score) in map
            moveScoreMap.put(move, score);
        }

        return moveScoreMap;
    }

    /**
     * Assigns a score to a possible MoveTicket using currentGameState, and
     * returns that score.
     *
     * @param move the MoveTicket to calculate score for.
     * @return the score for move.
     */
    private int scoreMoveTicket(MoveTicket move) {
        int score = 0;

        // give a move a higher score if it results in MrX being further away
        // from detectives
        for (Colour player : currentGameState.getPlayers())
            score += Djikstra(move.target, playerLocationMap.get(player));

        return score;
    }

    /**
     * Assigns a score to a possible MoveDouble using currentGameState, and
     * returns that score.
     *
     * @param move the MoveTicket to calculate score for.
     * @return the score for move.
     */
    private int scoreMoveDouble(MoveDouble move) {
        return -1;
    }

    /**
     * Generates a game tree to specified depth, given game tree with just root
     * node.
     *
     */
    private void generateTree(ScotlandYardGameTree gameTree, int treeDepth) {

    }

}
