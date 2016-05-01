package player;

import aigraph.AINode;
import aigraph.ScotlandYardGameTree;
import graph.Edge;
import graph.Graph;
import prijkstra.DijkstraCalculator;
import prijkstra.Weighter;
import scotlandyard.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
        int depth = 6;
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

        // initialise move ai will choose
        Move aiMove = moves.get(0);

        // generate the game tree
        generateTree(gameTree, depth, mrx);

        // choose the move
        double bestMoveScore = gameTree.getHead().getScore();
        System.out.println("score at head: " + bestMoveScore);

        boolean gotAMove = false;
        // check all first level edges to see which move gave the best score
        for (Edge<ScotlandYardState, Move> possibleBest : gameTree.getListFirstLevelEdges()) {
            System.out.println("score at child of move : " + possibleBest);
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
        double total = 0;
		double addOn = 0;

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

//			boolean edgesLessThan3 = false;
			if (route.getEdges().size() < 3) {
//				edgesLessThan3 = true;
				addOn += -15;
			}
			else {
				addOn += route.getEdges().size();
			}

//			for (Edge<Integer, Transport> e : route.getEdges()) {
//				double temp = TRANSPORT_WEIGHTER.toWeight(e.getData());
////				double temp = e.size();
//				if (edgesLessThan3)
//					temp = -temp;
//				addOn += temp;
//			}
		}

		int round = view.getRound();
		if (round != 0 && view.getRounds().get(round-1) && move.ticket == Ticket.Secret){
			addOn += 10;
		}

		double ticketValue = 0;
		if (move.colour == Colour.Black)
			ticketValue = TICKET_WEIGHTER_X.toWeight(move.ticket);
		else
			ticketValue = TICKET_WEIGHTER.toWeight(move.ticket);
		System.out.println("Ticket Value = " + ticketValue);
		addOn += ticketValue;

        return (total + addOn);
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
		double addOn = 0;
		int round = view.getRound();
		System.out.println("Round: " + round);
		if (round != 0 && view.getRounds().get(round+1)){
			addOn += 100;
			if (move.move2.ticket == Ticket.Secret)
			addOn += 20;
		}
		else
			addOn += -50;
		return (scoreMoveTicket(move.move2) + addOn);
    }


    /**
     * A lambda function implements Weighter<Ticket>. This Weighter assigns a higher
	 * weight to transports with which players start with less tickets for.
     */
    protected static final Weighter<Ticket> TICKET_WEIGHTER_X = t -> {
        int val = 0;
        switch (t) {
            case Taxi:
                val = 5;
                break;
            case Bus:
                val = 10;
                break;
            case Underground:
                val = 15;
                break;
        }
        return val;
    };

	/**
     * A lambda function implements Weighter<Ticket>. This Weighter assigns a higher
	 * weight to transports with which players start with less tickets for.
     */
    protected static final Weighter<Ticket> TICKET_WEIGHTER = t -> {
        int val = 0;
        switch (t) {
            case Taxi:
                val = 15;
                break;
            case Bus:
                val = 10;
                break;
            case Underground:
                val = 5;
                break;
        }
        return val;
    };

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
				val = 10;
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
    private void generateTree(ScotlandYardGameTree gameTree, int depth, boolean max) {

        // generate the tree
        MiniMax(gameTree.getHead(), depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, max);

        // set the score for game tree head
        gameTree.getHead().setScore(Collections.max(gameTree.getFirstLevelScores()));
    }


    /**
     * Implements the MiniMax algorithm with alpha-beta pruning.
     *
     * @param node the AINode from which to create a tree from. node will be
     *             head of the (sub)tree.
     * @param depth the number of lays to generate for the tree.
     * @param alpha should be set to initial value Double.NEGATIVE_INFINITY.
     * @param beta should be set to initial value Double.POSITIVE_INFINITY.
     * @param max If true, the player who's turn it is in
     *            {@code node.getGameState()}
     *            should be a maximising player, else minimising player.
     * @return the best possible score that scoreMoveTicket or
     *         scoreMoveDouble assigns that
     *         {@code node.getGameState().getCurrentPlayer()}
     *         can get, based on a tree of {@code depth} depth.
     */
    private Double MiniMax(AINode node, int depth, double alpha, double beta, boolean max) {
        // base case 1 - depth is zero
        if (depth <= 0) {
            //calculateScore(node);
            return node.getScore();
        }

        // create children nodes and add to tree
        //System.out.println("number of valid moves: " + node.getGameState().validMoves(node.getGameState().getCurrentPlayer()).size());
        System.out.println(node.getGameState().getCurrentPlayer());

        for (Move move : node.getGameState().validMoves(node.getGameState().getCurrentPlayer())) {
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
        Double v;

        // this player is maximising MrX score
        if (max) {
            // find child node with highest score
            v = Double.NEGATIVE_INFINITY;

            //System.out.println("MiniMax: NUMBER OF CHILDREN NODES: " + gameTree.getChildren(node).size());
            for (AINode child : gameTree.getChildrenQueue(node, AINODE_COMPARATOR)) {
                // see if next player is maximising or minimising
                boolean isMax = child.getGameState().getCurrentPlayer().equals(Colour.Black);

                v = Math.max(v, MiniMax(child, depth - 1, alpha, beta, isMax));
                alpha = Math.max(alpha, v);
                if (beta <= alpha)
                    break; // beta cut off
            }
        }

        // or player is minimising MrX score
        else {
            // find child with lowest score
            v = Double.POSITIVE_INFINITY;

            //System.out.println("MiniMax: NUMBER OF CHILDREN NODES: " + gameTree.getChildren(node).size());
            for (AINode child : gameTree.getChildrenQueue(node, AINODE_INV_COMPARATOR)) {
                // see if next player is maximising or minimising
                boolean isMax = child.getGameState().getCurrentPlayer().equals(Colour.Black);

                v = Math.min(v, MiniMax(child, depth - 1, alpha, beta, isMax));
                beta = Math.min(beta, v);
                if (beta <= alpha)
                    break; // alpha cut off
            }
        }

        // return the best score
        return v;
    }


    /**
     * Calculates a score for a given node's game state, and sets said node's
     * score.
     *
     * @param node the node to calculate the score for.
     */
    protected void calculateScore(AINode node) {
        // debugging check
        if (gameTree.getEdgesTo(node).size() != 1 && node != gameTree.getHead()) {
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
            score = 0.0;

        // adjust score to be higher if degree of MrX's node is higher.
        // this also avoids outskirts of map.
        if (colour.equals(Colour.Black))
            score *= node.getDegree(); //MrX maximises score
        else
            score /= node.getDegree(); //Detectives minimise score


        // set node.score to score
        node.setScore(score);
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
}
