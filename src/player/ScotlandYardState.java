package player;

import scotlandyard.*;

import java.util.*;

/**
 * A class to store the state of a Scotland Yard game at some fixed point duing
 * the game. An object of this type can be cloned.
 */
// TODO if playMove returns the cloned object, fields can be made final as the updated fields can be calculated before creating new instance of object.
public final class ScotlandYardState implements Cloneable {
    private final ScotlandYardGraph graph;
    private final List<Colour> players;

    // which of these are these needed for MiniMax?
    private Set<Colour> winningPlayers;
    private Map<Colour, Integer> playerLocations;
    private Map<Colour, Map<Ticket, Integer>> playerTickets;
    private boolean gameOver;
    private boolean ready;
    private Colour currentPlayer;
    int round;
    private List<Boolean> rounds;


    private ScotlandYardState(ScotlandYardGraph graph, List<Colour> players, Set<Colour> winningPlayers, Map<Colour, Integer> playerLocations, Map<Colour, Map<Ticket, Integer>> playerTickets, boolean gameOver, boolean ready, Colour currentPlayer, int round, List<Boolean> rounds) {
        this.graph = graph;
        this.players = players;
        this.winningPlayers = new HashSet<>(winningPlayers);
        this.playerLocations = new HashMap<>(playerLocations);
        this.playerTickets = new HashMap<>(playerTickets);
        this.gameOver = gameOver;
        this.ready = ready;
        this.currentPlayer = currentPlayer;
        this.round = round;
        this.rounds = new ArrayList<>(rounds);
    }


    public ScotlandYardState(ScotlandYardView view, ScotlandYardGraph graph) {
        this.graph = graph;
        this.players = view.getPlayers();

        this.winningPlayers = view.getWinningPlayers();

        this.playerLocations = new HashMap<>();
        for (Colour player : players)
            playerLocations.put(player, view.getPlayerLocation(player));

        this.playerTickets = new HashMap<>();
        for (Colour player : players) {
            HashMap<Ticket, Integer> thisPlayersTicketMap = new HashMap<>();
            for (Ticket ticket : Ticket.values()) {
                thisPlayersTicketMap.put(ticket, view.getPlayerTickets(player, ticket));
            }
            playerTickets.put(player, thisPlayersTicketMap);
        }

        this.gameOver = view.isGameOver();
        this.ready = view.isReady();
        this.currentPlayer = view.getCurrentPlayer();
        this.round = view.getRound();
        this.rounds = view.getRounds();
    }


    /**
     * Returns the list of valid moves for a given player.
     *
     * @param player the player whose moves we want to see.
     * @return the list of valid moves for a given player.
     */
    public List<Move> validMoves(Colour player) {
        int location = playerLocations.get(player);
        List<Move> moves = graph.generateMoves(player, location);
        List<Move> validMoves = new ArrayList<>();
		Map<Ticket, Integer> thisPlayersTicketMap = new HashMap<>();
		thisPlayersTicketMap = playerTickets.get(player);
        if (player != Colour.Black) {
            for (Move move : moves) {
                int dest = ((MoveTicket) move).target;
                boolean occupied = false;
                for (Colour internalPlayer : players.subList(1, players.size())) {
                    if (playerLocations.get(internalPlayer) == dest)
                        occupied = true;
                }
                if (!occupied && thisPlayersTicketMap.get(((MoveTicket) move).ticket) > 1)
                    validMoves.add(move);
            }
            if (validMoves.isEmpty())
                validMoves.add(MovePass.instance(player));
        } else {
            for (Move move : moves) {
                if (move instanceof MoveTicket) {
                    int dest = ((MoveTicket) move).target;
                    boolean occupied = false;
                    for (Colour internalPlayer : players) {
                        if (playerLocations.get(internalPlayer) == dest)
                            occupied = true;
                    }
                    if (!occupied && thisPlayersTicketMap.get(((MoveTicket) move).ticket) > 1)
                        validMoves.add(move);
                } else if ((move instanceof MoveDouble) && thisPlayersTicketMap.get(Ticket.Double) > 1) {
                    int dest1 = ((MoveDouble) move).move1.target;
                    int dest2 = ((MoveDouble) move).move2.target;
                    boolean occupied1 = false;
                    boolean occupied2 = false;
                    for (Colour internalPlayer : players) {
                        if (playerLocations.get(internalPlayer) == dest1)
                            occupied1 = true;
                        if (playerLocations.get(internalPlayer) == dest2)
                            occupied2 = true;
                    }
                    if ((thisPlayersTicketMap.get(((MoveDouble) move).move1.ticket) > 1) && (thisPlayersTicketMap.get(((MoveDouble) move).move2.ticket) > 1) && !occupied1 && !occupied2)
                        validMoves.add(move);
                }
            }
        }
        return validMoves;
    }


    // TODO: Implement playMove
    public void playMove(Move move) {

    }


    /**
     * Returns a deep copy of this ScotlandYardState.
     *
     * @return a deep copy of this ScotlandYardState.
     */
    public ScotlandYardState copy() {
        return new ScotlandYardState(graph,
                                     players,
                                     winningPlayers,
                                     playerLocations,
                                     playerTickets,
                                     gameOver,
                                     ready,
                                     currentPlayer,
                                     round,
                                     rounds);
    }



    // GETTERS
    //
    public Set<Colour> getWinningPlayers() {
        return winningPlayers;
    }

    public Map<Colour, Integer> getPlayerLocations() {
        return playerLocations;
    }

    public Map<Colour, Map<Ticket, Integer>> getPlayerTickets() {
        return playerTickets;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isReady() {
        return ready;
    }

    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRound() {
        return round;
    }

    public List<Boolean> getRounds() {
        return rounds;
    }

    public ScotlandYardGraph getGraph() {
        return graph;
    }

    public List<Colour> getPlayers() {
        return players;
    }

}
