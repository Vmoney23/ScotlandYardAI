package player;

import aigraph.AINode;
import aigraph.ScotlandYardGameTree;
import graph.Edge;
import graph.Graph;
import prijkstra.DijkstraCalculator;
import prijkstra.Weighter;
import scotlandyard.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
    private ScotlandYard currentGameState;
    private HashMap<Colour, Integer> playerLocationMap;
    private ScotlandYardGraph graph;
    private ScotlandYardGameTree gameTree;
    private DijkstraCalculator dijkstraGraph;
    private Integer token;
    protected List<Move> moves;

    public MiniMaxPlayer(ScotlandYardView view, String graphFilename) {
        // store current game
        if (!(view instanceof ScotlandYard))
            throw new IllegalArgumentException("view must be " +
                                               "a ScotlandYard object");
        this.currentGameState = (ScotlandYard) view;

        // store graph
        ScotlandYardGraphReader graphReader = new ScotlandYardGraphReader();
        try {
            // read the graph, convert it to a DijkstraCalculator and store it.
            this.graph = graphReader.readGraph(graphFilename);
        } catch (IOException e) {
            System.err.println("failed to read " + graphFilename);
            e.printStackTrace(System.err);
        }

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
        // update current game state
        if (!(receiver instanceof ScotlandYard))
            throw new IllegalArgumentException("Receiver must be " +
                    "a ScotlandYard object. Passed: " + receiver.getClass());

        this.currentGameState =
                (ScotlandYard) receiver; // FAIL to cast

        this.moves = moves;
        this.location = location;
        this.token = token;

        // store locations of other players
        playerLocationMap = new HashMap<>();
        for (Colour player : currentGameState.getPlayers()) {
            playerLocationMap.put(player, currentGameState.getPlayerLocation(player));
        }

        // store this players colour
        this.colour = currentGameState.getCurrentPlayer();

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
    protected Move getAIMove() {
        // create new game tree to specified depth, with root as current state of the game
        //int treeDepth = 0;
        //ScotlandYardGameTree gameTree = new ScotlandYardGameTree(currentGameState);
        //generateTree(gameTree, treeDepth);//calls pruneTree(), score()
        // return best moved based on MiniMax
        //List<AINode<ScotlandYardView>> finalStates = gameTree.getFinalStatesList();
        //return minimax(finalStates);

        // initialise tree
        gameTree = new ScotlandYardGameTree(currentGameState);

        // calculate a score for each move and put this info in a map
        int depth = 1;
        //HashMap<Move, Double> moveScores = score(gameTree.getHead(), depth,true);

        // return key associated with highest value
        //return Collections.max(moveScores.entrySet(), (entry1, entry2) ->(entry1.getValue() > entry2.getValue()) ? 1 : -1).getKey();

        return score(depth, true);
    }

    /**
     * Calculates scores for all given moves.
     *
     * @return a HashMap of each given move to its score.
     */
    protected Move score(int depth, boolean mrx) {

        // create map of moves to scores
        // HashMap<Move, Double> moveScoreMap = new HashMap<>();

        // move ai chooses
        Move aiMove = null;
/*
        // iterate through possible moves, calculating score for each one and
        // adding to moveScoreMap
        for (Move move : moves) {

            double score = 0.0;

            // TODO make score more complex
            // TODO score a detective move better

            // calculate score for move. A MovePass is left with score = 0;
            if (move instanceof MoveTicket)
                score = scoreMoveTicket((MoveTicket) move);
            else if (move instanceof MoveDouble)
                score = scoreMoveDouble((MoveDouble) move);

            // put entry (move, score) in map
            moveScoreMap.put(move, score);
        }

        return moveScoreMap;
*/
        // generate the game tree
        generateTree(gameTree, depth, mrx);

        // choose the move
        double bestMoveScore = gameTree.getHead().getScore();

        // check all first level edges to see which move gave the best score
        for (Edge<ScotlandYard, Move> possibleBest : gameTree.getListFirstLevelEdges()) {
            if (((AINode)possibleBest.getTarget()).getScore() == bestMoveScore) {
                aiMove = possibleBest.getData();
                break;
            }
        }

        return aiMove;
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
        double score;
        double total = 0;
        int routes = 0;

        // loop through all other players, find 'best' route to each other
        // player from move target, score this route, add route score to total.
        for (Colour player : currentGameState.getPlayers()) {

            // no need to calculate distance between player and himself...
            if (move.colour == player) continue;

            // calculate shortest route from MiniMax player to other player
            Graph<Integer, Transport> route = dijkstraGraph.getResult(move.target, playerLocationMap.get(player), TRANSPORT_WEIGHTER);

            // add weight of each edge in route to score. Use a different
            // Weighter if player is Detective or MrX.
            if (move.colour == Colour.Black) {
                // add more to score if edge requires greater value transport
                // to traverse.
                for (Edge<Integer, Transport> e : route.getEdges())
                    total += TRANSPORT_WEIGHTER.toWeight(e.getData());
            }
            else {
                // add more to score if edge requires lesser value transport
                // to traverse. TODO change for route to other detectives vs MrX
                for (Edge<Integer, Transport> e : route.getEdges())
                    total += TRANSPORT_INV_WEIGHTER.toWeight(e.getData());
            }

            // increment routes, for taking mean later
            routes++;
        }

        // calculate mean and return it
        score = total / routes;
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
     * An anonymous class that implements Weighter<Transport>, to be passed to
     * Dijkstra's. This Weighter assigns a higher weight to transports with
     * which players start with less tickets for.
     */
    protected static final Weighter<Transport> TRANSPORT_WEIGHTER = new Weighter<Transport>() {
        @Override
        public double toWeight(Transport t) {
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
                    val = 8;
                    break;
            }
            return val;
        }
    };

    /**
     * An anonymous class that implements Weighter<Transport>, to be passed to
     * Dijkstra's. This Weighter assigns a lower weight to transports with
     * which players start with less tickets for.
     */
    protected static final Weighter<Transport> TRANSPORT_INV_WEIGHTER = new Weighter<Transport>() {
        @Override
        public double toWeight(Transport t) {
            int val = 0;
            switch (t) {
                case Taxi:
                val = 8;
                break;
            case Bus:
                val = 4;
                break;
            case Underground:
                val = 2;
                break;
            case Boat:
                val = 0; // detective cannot use a boat.
                break;
            }
            return val;
        }
    };

    /**
     *
     * @param gameTree
     * @param depth
     * @param max
     */
    private void generateTree(ScotlandYardGameTree gameTree, int depth,
                                boolean max) {
        gameTree.getHead().setScore(MiniMax(gameTree.getHead(), depth, max));
    }


    /**
     *  TODO 1) duplicateGameState.
     *  TODO 2) don't switch max on each recurse, as could be more than 1 detective playing, who will also want to min
     *  TODO 3) alpha-beta pruning.
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
        // base case
        if (depth <= 0) {
            calculateScore(node);
            return node.getScore();
        }

        // store the score for the best move
        Double bestScore;

        // this player is maximising MrX score
        if (max) {
            // create children nodes
            for (Move move : node.getGameState().validMoves(colour)) {
                // make a copy of currentGameState for the next move
                ScotlandYard stateAfterMove = duplicateGameState(node.getGameState());
                stateAfterMove.playMove(move, token); // TODO will token be valid?

                // create node for this game state and link to tree. Give
                // unassigned score = 0
                AINode child = new AINode(stateAfterMove, 0.0);
                gameTree.add(child);
                Edge<ScotlandYard, Move> edgeToChild = new Edge<>(node, child, move);
                gameTree.add(edgeToChild);
            }

            // find child node with highest score
            bestScore = Double.NEGATIVE_INFINITY;

            for (AINode child : gameTree.getChildren(node)) {
                Double v = MiniMax(child, depth - 1, false);
                bestScore = bestScore > v ? bestScore : v;
            }
        }

        // or player is minimising MrX score
        else {
            // create children nodes
            for (Move move : node.getGameState().validMoves(colour)) {
                // make a copy of currentGameState for the next move
                ScotlandYard stateAfterMove = duplicateGameState(node.getGameState());
                stateAfterMove.playMove(move, token);

                // create node for this game state and link to tree. Give
                // unassigned score = 0
                AINode child = new AINode(stateAfterMove, 0.0);
                gameTree.add(child);
                Edge<ScotlandYard, Move> edgeToChild = new Edge<>(node, child, move);
                gameTree.add(edgeToChild);
            }

            // find child with lowest score
            bestScore = Double.POSITIVE_INFINITY;

            for (AINode child : gameTree.getChildren(node)) {
                Double v = MiniMax(child, depth - 1, true);
                bestScore = bestScore < v ? bestScore : v;
            }
        }

        // return the best score
        return bestScore;
    }


    //TODO implement duplicateGameState
    protected ScotlandYard duplicateGameState(ScotlandYard sy) {
        // create the queue
        ScotlandYardMapQueue<Integer, Token> queue = new ScotlandYardMapQueue<>();
        queue.put(token, new Token(token, colour, 0));//random timestamp given

        return new ScotlandYard(sy.getPlayers().size() - 1,
                                             sy.getRounds(),
                                             this.graph,
                                             queue,//sy.queue is private
                                             token);//sy.gameId is private
    }


    //TODO implement calculateScore (calls scoreMoveTicket/Double)
    protected void calculateScore(AINode node) {

    }

}