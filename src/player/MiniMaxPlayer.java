package player;

import aigraph.AINode;
import aigraph.ScotlandYardGameTree;
import graph.Edge;
import graph.Graph;
import pair.Pair;
import prijkstra.DijkstraCalculator;
import prijkstra.Weighter;
import scotlandyard.*;

import java.io.IOException;
import java.util.*;

/**
 * A class for a player in a Scotland Yard game, implementing Player. A player
 * of this class will choose a move automatically, by creating a game tree, with
 * alpha-beta pruning, and selecting a move based on the MiniMax algorithm. The
 * score for each possible state of the game is based on numerous factors.
 * @see Player the interface for a Player in this game.
 *
 *
 * TODO better order valid moves to improve alpha-beta pruning
 * TODO make score more complex
 */
public class MiniMaxPlayer implements Player {

    private int location;
    private Colour colour;
    private ScotlandYardView view;
    private ScotlandYardState currentGameState;
    private Map<Colour, Integer> playerLocationMap;
    private ScotlandYardGraph graph;
    private ScotlandYardGameTree gameTree;
    private DijkstraCalculator dijkstraGraph;
    protected List<Move> moves;


    public MiniMaxPlayer(ScotlandYardView view, String graphFilename, Colour colour) {
        // store graph
        ScotlandYardGraphReader graphReader = new ScotlandYardGraphReader();
        try {
            // read the graph, convert it to a DijkstraCalculator and store it.
            this.graph = graphReader.readGraph(graphFilename);
        } catch (IOException e) {
            System.err.println("failed to read " + graphFilename);
            e.printStackTrace();
        }

        // store player colour
        this.colour = colour;

        // store current game
        this.view = view;

        // store dijkstra graph
        this.dijkstraGraph = new DijkstraCalculator(this.graph);
    }


    /**
     * Gets the current state of the game from a Receiver (the Scotland Yard
     * model), chooses a move, and makes the receiver play it. This method
     * should be called by the receiver.
     *
     * @param location the location of the player to request a move from.
     * @param moves a list of valid moves the player can make.
     * @param token the token to verify correct player returns a move: it is
     *              given back to the receiver.
     * @param receiver the Receiver who makes this method call.
     */
    @Override
    public void notify(int location, List<Move> moves, Integer token,
                       Receiver receiver) {

        // get data for fields
        this.moves = moves;
        this.location = location;

        // create a ScotlandYardState based on view and location
        this.currentGameState = new ScotlandYardState(view, location, graph);

        // store locations of other players
        playerLocationMap = currentGameState.getPlayerLocations();

        // get ai move
        System.out.println("\n\n*********Getting move***********");
        Move aiMove = getAIMove();

        // play ai move
        System.out.println("*************Playing move: " + aiMove + "\n\n");
        receiver.playMove(aiMove, token);
    }


    /**
     * Chooses a move.
     *
     * @return the chosen move.
     */
    protected Move getAIMove() {
        // initialise tree with the current game state at root node
        gameTree = new ScotlandYardGameTree(currentGameState);

        // calculate a score for each move by using the MiniMax algorithm.
        // return the move with the best score.
        int depth = 3;
        boolean mrx = colour.equals(Colour.Black);
        return score(depth, mrx);
    }


    /**
     * Calculates scores for all given moves.
     *
     * @return the move which results in the game state with the highest score
     *         after looking at every possibility after {@code depth} moves are
     *         played.
     */
    protected Move score(int depth, boolean mrx) {

//        // initialise move ai will choose
//        Move aiMove = moves.get(0);
//
//        // generate the game tree
//        generateTree(gameTree, depth, mrx);
//
//        // choose the move
//        double bestMoveScore = gameTree.getHead().getScore();
//        System.out.println("score at head: " + bestMoveScore);
//
//        boolean gotAMove = false;
//        // check all first level edges to see which move gave the best score
//        for (Edge<ScotlandYardState, Move> possibleBest : gameTree.getListFirstLevelEdges()) {
//            System.out.println("score at child of move : " + possibleBest);
//            if (((AINode) possibleBest.getTarget()).getScore() == bestMoveScore) { // ERROR IF STATEMENT BROKEN
//                aiMove = possibleBest.getData();
//                gotAMove = true;
//                break;
//            }
//        }
//        if (!gotAMove) System.out.println("score(): Move was not assigned " +
//                "from gameTree");
//        else System.out.println("score(): Move was assigned from gameTree");

        return generateTree(gameTree, depth, mrx);
    }


    /**
     * A lambda function implements Weighter<Transport>, to be passed to
     * Dijkstra's. This Weighter assigns a higher weight to transports with
     * which players start with less tickets for.
     */
    protected static final Weighter<Transport> TRANSPORT_WEIGHTER = t -> {
        int val = 0;
        switch (t) {
            case Taxi:
                val = 1;
                break;
            case Bus:
                val = 2;
                break;
            case Underground:
                val = 4;
                break;
            case Boat:
                val = 15;
                break;
        }
        return val;
    };


    /**
     * Generates a gameTree given the head node, which should have have
     * {@code gameTree.head} as an AINode whose index field should be a
     * ScotlandYardState object holding the current state of the game.
     *
     * @param gameTree the graph containing just the head node of the MiniMax
     *                 tree
     * @param depth depth to which to generate the tree
     * @param max true if this player wants to maximise the score
     * @return the move which leads to the game state with highest score
     */
    private Move generateTree(ScotlandYardGameTree gameTree, int depth, boolean max) {
        // generate the tree
        //Pair<Double, Move> result = MiniMax(gameTree.getHead(), depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, max);
        Pair<Double, Move> result = MiniMax(gameTree.getHead(), depth, max);

        gameTree.getHead().setScore(result.getLeft());
        return result.getRight();
    }


    /**
     * Implements the MiniMax algorithm with alpha-beta pruning.
     *
     * @param node the AINode from which to create a tree from. node will be
     *             head of the (sub)tree.
     * @param depth the number of lays to generate for the tree.
     * param alpha should be set to initial value Double.NEGATIVE_INFINITY.
     * param beta should be set to initial value Double.POSITIVE_INFINITY.
     * @param max If true, the player who's turn it is in
     *            {@code node.getGameState()}
     *            should be a maximising player, else minimising player.
     * @return the best possible score that scoreMoveTicket or
     *         scoreMoveDouble assigns that
     *         {@code node.getGameState().getCurrentPlayer()}
     *         can get, based on a tree of {@code depth} depth.
     */
    private Pair<Double, Move> MiniMax(AINode node, int depth, boolean max) {
        // store the valid moves for current player at current game state
        List<Move> validMoves = node.getGameState().validMoves(node.getGameState().getCurrentPlayer());

        System.out.println("depth = " + depth);

        // base cases - 1) depth is zero. 2) node is leaf (no valid moves).
        //              3) game is over in current state
        if (depth <= 0 || validMoves.size() == 0 || node.getGameState().isGameOver()) {
            node.setScore(calculateScore(node));
            return new Pair<>(node.getScore(), null);
        }

        // store the current bestPair, initialising it's score also
        double bestScore = max ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Pair<Double, Move> bestPair = new Pair<>(bestScore, null);

        // store the pair to be returned from each child
        Pair<Double, Move> currentPair;

        for (Move currentMove : validMoves) {

            // make a copy of currentGameState for the next move
            ScotlandYardState stateAfterMove = node.getGameState().copy();
            stateAfterMove.playMove(currentMove);

            // create child node for this game state and link to tree.
            // Give unassigned score = -1.0
            AINode child = new AINode(stateAfterMove, -1.0);
            gameTree.add(child);
            Edge<ScotlandYardState, Move> edgeToChild = new Edge<>(node, child, currentMove);
            gameTree.add(edgeToChild);


            // get the MiniMax for current node
            boolean isMax = Objects.equals(child.getGameState().getCurrentPlayer(), Colour.Black);
            currentPair = MiniMax(child, depth-1, isMax);

            // if the current pair is better, assign the bestPair it's score,
            // and make the currentMove the best move.
            if (max) {
                if (currentPair.getLeft() > bestPair.getLeft()) {
                    bestPair = currentPair; // update bestPair score
                    bestPair.setRight(currentMove);
                }
            }
            else {//min
                if (currentPair.getLeft() < bestPair.getLeft()) {
                    bestPair = currentPair; // update bestPair score
                    bestPair.setRight(currentMove);
                }
            }
        }



        return bestPair;




//        // store the current bestPair and the pair the next recurse is going to
//        // return
//        Pair<Double, Move> bestPair = null;
//        Pair<Double, Move> currentPair;
//
//        // iterate through validMoves, add child to tree and play move,
//        // choose MiniMax child, update values and prune.
//        if (max) {
//
//            for (Move currentMove : node.getGameState().validMoves(node.getGameState().getCurrentPlayer())) {
//
//                // make a copy of currentGameState for the next move
//                ScotlandYardState stateAfterMove = node.getGameState().copy();
//                stateAfterMove.playMove(currentMove);
//
//                // create child node for this game state and link to tree.
//                // Give unassigned score = 0
//                AINode child = new AINode(stateAfterMove, 0.0);
//                gameTree.add(child);
//                Edge<ScotlandYardState, Move> edgeToChild = new Edge<>(node, child, currentMove);
//                gameTree.add(edgeToChild);
//
//
//                // see if next player is maximising or minimising
//                boolean isMax = child.getGameState().getCurrentPlayer().equals(Colour.Black);
//
//                // get (score, move) pair from child
//                currentPair = MiniMax(child, depth - 1, alpha, beta, isMax);
//
//                // if haven't yet assigned a bestPair (not reached leaf), OR
//                // if child returns a better score, set it's pair to bestPair
//                if ((bestPair == null || bestPair.getLeft() < currentPair.getLeft())) {
//                    bestPair = currentPair;
//                }
//
//                // update alpha (found a new leaf with a better option for the
//                // current maximising player)
//                if (currentPair.getLeft() > alpha) {
//                    alpha = currentPair.getLeft();
//                    bestPair = currentPair;
//                }
//
//                // prune
//                if (beta <= alpha) {
//                    bestPair.setLeft(beta);
//                    bestPair.setRight(null);
//                    break;
//                }
//
//            }
//
//        }
//
//        else { // min
//
//            for (Move currentMove : node.getGameState().validMoves(node.getGameState().getCurrentPlayer())) {
//
//                // make a copy of currentGameState for the next move
//                ScotlandYardState stateAfterMove = node.getGameState().copy();
//                stateAfterMove.playMove(currentMove);
//
//                // create child node for this game state and link to tree.
//                // Give unassigned score = 0
//                AINode child = new AINode(stateAfterMove, 0.0);
//                gameTree.add(child);
//                Edge<ScotlandYardState, Move> edgeToChild = new Edge<>(node, child, currentMove);
//                gameTree.add(edgeToChild);
//
//
//                // see if next player is maximising or minimising
//                boolean isMax = child.getGameState().getCurrentPlayer().equals(Colour.Black);
//
//                // get (score, move) pair from child
//                currentPair = MiniMax(child, depth - 1, alpha, beta, isMax);
//
//                // if haven't yet assigned a bestPair, OR
//                // if child returns a better score, set it's pair to bestPair
//                if ((bestPair == null || bestPair.getLeft() > currentPair.getLeft())) {
//                    bestPair = currentPair;
//                }
//
//                // update beta
//                if (currentPair.getLeft() < beta) {
//                    beta = currentPair.getLeft();
//                    bestPair = currentPair;
//                }
//
//                // prune
//                if (beta <= alpha) {
//                    bestPair.setLeft(alpha);
//                    bestPair.setRight(null);
//                    break;
//                }
//
//            }
//
//        }
//
//        // return the best score
//        return bestPair;
    }


    /**
     * Calculates a score for a given node's game state, and sets said node's
     * score.
     *
     * @param node the node to calculate the score for.
     * @return the node's score
     */
    protected Double calculateScore(AINode node) {
        Double score = 0.0;

        // if game over in this state, set to +/-infinity accordingly, else
        // calculate the score
        if (node.getGameState().getWinningPlayers().contains(Colour.Black)
                     && node.getGameState().isGameOver()) {
            score = Double.POSITIVE_INFINITY;
        }
        else if (!node.getGameState().getWinningPlayers().contains(Colour.Black)
                         && node.getGameState().isGameOver()) {
            score = Double.NEGATIVE_INFINITY;
        }
        else {
            // use Dijkstra's and Weighter to assign a score based on distance
            // MrX is from each detective
            score += scoreDistancesState(node.getGameState());
        }

        return score;
    }


    /**
     * Returns a score for a state, based on how far away MrX is from each
     * detective.
     *
     * @param state the state to score.
     * @return the calculated score for the state, based only on how far MrX is
     *         from the detectives.
     */
    private Double scoreDistancesState(ScotlandYardState state) {
        Double score = 0.0;

        for (Colour detective : state.getPlayers()) {
            // don't find distance between MrX and himself
            if (detective == Colour.Black) continue;

            // calculate shortest route between detective and MrX
            Graph<Integer, Transport> route =
                    dijkstraGraph.getResult(playerLocationMap.get(detective), playerLocationMap.get(Colour.Black), TRANSPORT_WEIGHTER);

            // add weight of each edge in route to score.
            // add more to score if edge requires greater value transport
            // to traverse.
            for (Edge<Integer, Transport> e : route.getEdges())
                score += TRANSPORT_WEIGHTER.toWeight(e.getData());
        }

        return score;
    }


    /**
     * A comparator for AINode that ranks AINodes with a greater score field as
     * higher.
     */
    private static final Comparator<AINode> AINODE_COMPARATOR =
           (node1, node2) -> node1.getScore().intValue() - node2.getScore().intValue();


    /**
     * A comparator for AINode that ranks AINodes with a smaller score field as
     * higher.
     */
    private static final Comparator<AINode> AINODE_INV_COMPARATOR =
           (node1, node2) -> node2.getScore().intValue() - node1.getScore().intValue();





    //-----------------old--------------------------
    /**
     * Assigns a score to a possible move using currentGameState, and returns
     * that score.
     *
     * @param move the Move to calculate score for.
     * @return the score for move.
     */
    protected double scoreMove(Move move) {
        if (move instanceof MoveTicket)
            return scoreMoveTicket((MoveTicket) move);
        else if (move instanceof MoveDouble)
            return scoreMoveDouble((MoveDouble) move);
        else //MovePass
            return 0;
    }


    /**
     * Assigns a score to a possible MoveTicket using currentGameState, and
     * returns that score.
     *
     * @param move the MoveTicket to calculate score for.
     * @return the score for move.
     */
    protected double scoreMoveTicket(MoveTicket move) {
        // upon return, score
        double score = 0;

        // loop through all other players, find 'best' route to each other
        // player from move target, score this route, add route score to total.
        for (Colour player : currentGameState.getPlayers()) {

            // no need to calculate distance between player and himself...
            if (move.colour == player) continue;

            // calculate shortest route from MiniMax player to other player
            Graph<Integer, Transport> route = dijkstraGraph.getResult(move.target, playerLocationMap.get(player), TRANSPORT_WEIGHTER);

            // add weight of each edge in route to score.
            // add more to score if edge requires greater value transport
            // to traverse.
            for (Edge<Integer, Transport> e : route.getEdges())
                score += TRANSPORT_WEIGHTER.toWeight(e.getData());
        }

        return score;
    }


    /**
     * Assigns a score to a possible MoveDouble using currentGameState, and
     * returns that score.
     *
     * @param move the MoveTicket to calculate score for.
     * @return the score for move.
     */
    protected double scoreMoveDouble(MoveDouble move) {
        // score the move as if single move, then divide by some factor to
        // account for using a valuable double move ticket
        return scoreMoveTicket(move.move2) / 2;
    }
}
