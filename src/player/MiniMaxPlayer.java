package player;

import aigraph.AINode;
import aigraph.ScotlandYardGameTree;
import graph.Edge;
import graph.Graph;
import prijkstra.DijkstraCalculator;
import prijkstra.Weighter;
import scotlandyard.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A class for a player in a Scotland Yard game, implementing Player. A player
 * of this class will choose a move automatically, by creating a game tree, with
 * alpha-beta pruning, and selecting a move based on the MiniMax algorithm. The
 * score for each possible state of the game is based on numerous factors.
 * @see Player the interface for a Player in this game.
 *
 * TODO **dont score state as a ScotlandYard, create new ScotlandYardData so can be cloned
 *
 * TODO alpha-beta pruning
 * TODO make score more complex
 * TODO Dijkstra must not let Detective-Detective journeys have boats.
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
        System.out.println("Playing move: " + aiMove + "\n\n");
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
        int depth = 4;
        boolean mrx = colour == Colour.Black;
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

        // initialise move ai will choose
        Move aiMove = moves.get(0);

        // generate the game tree
        generateTree(gameTree, depth, mrx);

        // choose the move
        double bestMoveScore = gameTree.getHead().getScore();

        boolean gotAMove = false;
        // check all first level edges to see which move gave the best score
        for (Edge<ScotlandYardState, Move> possibleBest : gameTree.getListFirstLevelEdges()) {
            if (((AINode) possibleBest.getTarget()).getScore() == bestMoveScore) { // ERROR IF STATEMENT BROKEN
                aiMove = possibleBest.getData();
                gotAMove = true;
                break;
            }
        }
        if (!gotAMove) System.out.println("score(): Move was not assigned " +
                "from gameTree");
        else System.out.println("score(): Move was assigned from gameTree");

        return aiMove;
    }


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
        // upon return, score = total / routes
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
        return scoreMoveTicket(move.move2) / 2.5;
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
     */
    private void generateTree(ScotlandYardGameTree gameTree, int depth,
                                boolean max) {
        gameTree.getHead().setScore(MiniMax(gameTree.getHead(), depth, max));
    }


    /**
     *  TODO 1) don't switch max on each recurse, as could be more than 1 detective playing, who will also want to min
     *  TODO 2) alpha-beta pruning.
     *
    /**
     * <p>Implements the MiniMax algorithm with alpha-beta pruning.
     *
     * @param node the AINode from which to create a tree from. node will be
     *             head of the (sub)tree.
     * @param depth the number of lays to generate for the tree.
     * @param max If true, the player who's turn it is in
     *            {@code node.getGameState()}
     *            should be a maximising player, else minimising player.
     * @return the best possible score that scoreMoveTicket or
     *         scoreMoveDouble assigns that
     *         {@code node.getGameState().getCurrentPlayer()}
     *         can get, based on a tree of {@code depth} depth.
     */
    private Double MiniMax(AINode node, int depth, boolean max) {
        // base case 1 - depth is zero
        if (depth <= 0) {
            calculateScore(node);
            return node.getScore();
        }

        // create children nodes and add to tree
        for (Move move : node.getGameState().validMoves(colour)) {
            // make a copy of currentGameState for the next move
            ScotlandYardState stateAfterMove = node.getGameState().copy();
            stateAfterMove.playMove(move);

            // create node for this game state and link to tree.
            // Give unassigned score = 0
            AINode child = new AINode(stateAfterMove, 0.0);
            gameTree.add(child);
            Edge<ScotlandYardState, Move> edgeToChild = new Edge<>(node, child, move);
            gameTree.add(edgeToChild);
            calculateScore(child);
        }

        // base case 2 - node is leaf (no valid moves)
        if (gameTree.getChildren(node).size() == 0) {
            calculateScore(node);
            return node.getScore();
        }

        // store the score for the best move
        Double bestScore;

        // this player is maximising MrX score
        if (max) {
            // find child node with highest score
            bestScore = Double.NEGATIVE_INFINITY;

            System.out.println("MiniMax: NUMBER OF CHILDREN NODES: " + gameTree.getChildren(node).size());
            for (AINode child : gameTree.getChildren(node)) {
                Double v = MiniMax(child, depth - 1, false);
                // max(bestScore, v)
                bestScore = bestScore > v ? bestScore : v;
            }
        }

        // or player is minimising MrX score
        else {
            // find child with lowest score
            bestScore = Double.POSITIVE_INFINITY;

            System.out.println("MiniMax: NUMBER OF CHILDREN NODES: " + gameTree.getChildren(node).size());
            for (AINode child : gameTree.getChildren(node)) {
                Double v = MiniMax(child, depth - 1, true);
                // min(bestScore, v)
                bestScore = bestScore < v ? bestScore : v;
            }
        }

        // return the best score
        return bestScore;
    }


    /**
     * Calculates a score for a given node's game state, and sets said node's
     * score.
     *
     * @param node the node to calculate the score for.
     */
    protected void calculateScore(AINode node) {
        // debugging check
        if (gameTree.getEdgesTo(node).size() != 1) {
            throw new RuntimeException("Illegal state: Not one edge to " +
                    "AINode: " + node.getScore());
        }

        // get move, which should be on edge to node. There should only be one
        // edge to node.
        Move moveToNode = gameTree.getEdgesTo(node).get(0).getData();

        // get score based on Dijkstra
        Double score = scoreMove(moveToNode);

        // decrease score if MrX loses in this game state
        if (!node.getGameState().getWinningPlayers().contains(Colour.Black)
                && node.getGameState().isGameOver())
            score /= 5;

        // adjust score to be higher if degree of MrX's node is higher.
        // this also avoids outskirts of map.
        if (colour.equals(Colour.Black))
            score *= node.getDegree(); //MrX maximises score
        else
            score /= node.getDegree(); //Detectives minimise score


        // set node.score to score
        node.setScore(score);
    }

}
